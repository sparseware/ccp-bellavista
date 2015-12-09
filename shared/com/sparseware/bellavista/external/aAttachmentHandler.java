/*
 * Copyright (C) SparseWare Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparseware.bellavista.external;

import java.util.List;

import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.iWidget;
import com.sparseware.bellavista.Document;
import com.sparseware.bellavista.Document.DocumentItem;

/**
 * This class provides a base implementation for attachment handlers
 * that handle special document attachment types
 *
 * @author Don DeCoteau
 *
 */
public abstract class aAttachmentHandler {

  /**
   * Creates a new instance
   */
  public aAttachmentHandler() {}

  /**
   * Creates a viewer to render the specified attachment
   *
   * @param parent
   *          the parent for the viewer
   * @param document
   *          the main document
   * @param attachment
   *          the attachment item
   * @param cb
   *          the callback to be called when the viewer is created or on failure
   */
  public abstract void createViewer(iContainer parent, Document document, DocumentItem attachment,
                                    iFunctionCallback cb);

  /**
   * Gets a list of widgets to be placed on the document toolbar
   *
   * @param viewer
   *          the viewer for the attachment (created by this handler)
   * @return
   */
  public abstract List<iWidget> getToolbarWidgets(iViewer viewer);

  /**
   * Call to dispose of this object. All background services should be stopped
   * and all resources released.
   */
  public abstract void dispose();
}
