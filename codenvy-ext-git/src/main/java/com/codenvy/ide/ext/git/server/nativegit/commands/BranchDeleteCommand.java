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
package com.codenvy.ide.ext.git.server.nativegit.commands;

import com.codenvy.ide.ext.git.server.GitException;

import java.io.File;

/**
 * Delete branch
 *
 * @author Eugene Voevodin
 */
public class BranchDeleteCommand extends GitCommand<Void> {

    private boolean deleteFullyMerged;
    private String  branchName;
    private String  remote;

    public BranchDeleteCommand(File repository) {
        super(repository);
    }

    /**
     * @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute()
     */
    @Override
    public Void execute() throws GitException {
        if (branchName == null) {
            throw new GitException("Branch name was not set.");
        }
        reset();

        if (remote != null) {
            commandLine.add("push");
            commandLine.add(remote);
            commandLine.add("--delete");
        } else {
            commandLine.add("branch");
            commandLine.add(deleteFullyMerged ? "-D" : "-d");
        }
        commandLine.add(branchName);

        start();
        return null;
    }

    /**
     * @param deleteFullyMerged
     *         if <code>true</code> fully merged branch will be deleted
     * @return BranchDeleteCommand with established delete fully merged branch parameter
     */
    public BranchDeleteCommand setDeleteFullyMerged(boolean deleteFullyMerged) {
        this.deleteFullyMerged = deleteFullyMerged;
        return this;
    }

    /**
     * @param branchName
     *         branch to delete
     * @return BranchDeleteCommand with established branch to delete parameter
     */
    public BranchDeleteCommand setBranchName(String branchName) {
        this.branchName = branchName;
        return this;
    }

    /**
     * @param remoteName
     *         remote name
     * @return BranchDeleteCommand with established remote name
     */
    public BranchDeleteCommand setRemote(String remoteName) {
        this.remote = remoteName;
        return this;
    }
}
