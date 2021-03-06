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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.dto.shared.DTO;

import java.util.List;

/**
 * Request to remove files.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RmRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
@DTO
public interface RmRequest extends GitRequest {
    /** @return files to remove */
    List<String> getItems();
    
    void setItems(List<String> items);
    
    RmRequest withItems(List<String> items);

    /** @return is RmRequest represents remove from index only */
    boolean isCached();
    
    void setCached(boolean isCached);
    
    RmRequest withCached(boolean cached);

    boolean isRecursively();

    void setRecursively(boolean isRecursively);

    RmRequest withRecursively(boolean isRecursively);
}