/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client.reset.files;

import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.shared.IndexFile;
import com.codenvy.ide.ext.git.shared.ResetRequest.ResetType;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for resetting files from index.
 * <p/>
 * When user tries to reset files from index:
 * 1. Find Git work directory by selected item in browser tree.
 * 2. Get status for found work directory.
 * 3. Display files ready for commit in grid. (Checked items will be reseted from index).
 *
 * @author Ann Zhuleva
 */
@Singleton
public class ResetFilesPresenter implements ResetFilesView.ActionDelegate {
    private final DtoFactory              dtoFactory;
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       DialogFactory           dialogFactory;
    private       ResetFilesView          view;
    private       GitServiceClient        service;
    private       AppContext              appContext;
    private       GitLocalizationConstant constant;
    private       NotificationManager     notificationManager;
    private       CurrentProject          project;
    private       Array<IndexFile>        indexedFiles;

    /** Create presenter. */
    @Inject
    public ResetFilesPresenter(ResetFilesView view, GitServiceClient service, AppContext appContext,
                               GitLocalizationConstant constant, NotificationManager notificationManager,
                               DtoFactory dtoFactory, DtoUnmarshallerFactory dtoUnmarshallerFactory, DialogFactory dialogFactory) {
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.appContext = appContext;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        project = appContext.getCurrentProject();

        service.status(project.getRootProject(),
                       new AsyncRequestCallback<Status>(dtoUnmarshallerFactory.newUnmarshaller(Status.class)) {
                           @Override
                           protected void onSuccess(Status result) {
                               if (result.isClean()) {
                                   dialogFactory.createMessageDialog(constant.messagesWarningTitle(), constant.indexIsEmpty(), null).show();
                                   return;
                               }

                               Array<IndexFile> values = Collections.createArray();
                               ArrayList<String> valuesTmp = new ArrayList<>();

                               valuesTmp.addAll(result.getAdded());
                               valuesTmp.addAll(result.getChanged());
                               valuesTmp.addAll(result.getRemoved());

                               for (String value : valuesTmp) {
                                   IndexFile indexFile = dtoFactory.createDto(IndexFile.class);
                                   indexFile.setPath(value);
                                   indexFile.setIndexed(true);
                                   values.add(indexFile);
                               }

                               if (values.isEmpty()) {
                                   dialogFactory.createMessageDialog(constant.messagesWarningTitle(), constant.indexIsEmpty(), null).show();
                                   return;
                               }

                               view.setIndexedFiles(values);
                               indexedFiles = values;
                               view.showDialog();
                           }

                           @Override
                           protected void onFailure(Throwable exception) {
                               String errorMassage = exception.getMessage() != null ? exception.getMessage() : constant.statusFailed();
                               Notification notification = new Notification(errorMassage, ERROR);
                               notificationManager.showNotification(notification);
                           }
                       });
    }

    /** {@inheritDoc} */
    @Override
    public void onResetClicked() {
        List<String> files = new ArrayList<>();
        for (IndexFile indexFile : indexedFiles.asIterable()) {
            if (!indexFile.isIndexed()) {
                files.add(indexFile.getPath());
            }
        }

        if (files.isEmpty()) {
            view.close();
            Notification notification = new Notification(constant.nothingToReset(), INFO);
            notificationManager.showNotification(notification);
            return;
        }
        view.close();

        service.reset(project.getRootProject(), "HEAD", ResetType.MIXED, files, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Notification notification = new Notification(constant.resetFilesSuccessfully(), INFO);
                notificationManager.showNotification(notification);
            }

            @Override
            protected void onFailure(Throwable exception) {
                String errorMassage = exception.getMessage() != null ? exception.getMessage() : constant.resetFilesFailed();
                Notification notification = new Notification(errorMassage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}