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
package com.codenvy.ide.ext.git.client.action;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.push.PushToRemotePresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class PushAction extends GitAction {
    private final PushToRemotePresenter presenter;
    private final AnalyticsEventLogger  eventLogger;

    @Inject
    public PushAction(PushToRemotePresenter presenter,
                      AppContext appContext,
                      GitResources resources,
                      GitLocalizationConstant constant,
                      AnalyticsEventLogger eventLogger,
                      SelectionAgent selectionAgent) {
        super(constant.pushControlTitle(), constant.pushControlPrompt(), resources.push(), appContext, selectionAgent);
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        presenter.showDialog();
    }

    @Override
    protected void updateProjectAction(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(isGitRepository());
    }
}
