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
package com.sparseware.bellavista;

import java.util.EventObject;

import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.widget.iWidget;

/**
 * This class is the event handler for the generic pick list UI item
 * 
 * @author Don DeCoteau
 *
 */
public class PickList implements iEventHandler {

  public PickList() {
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {
  }

  public void onOkAction(String eventName, iWidget widget, EventObject event) {
    iContainer fv = widget.getFormViewer();
    Object selection = fv.getWidget("list").getSelection();
    iFunctionCallback cb = (iFunctionCallback) fv.getAttribute("dlgCallback");
    fv.getWindow().close();
    if (cb != null) {
      cb.finished(false, selection);
    }

  }

  public void onCancelAction(String eventName, iWidget widget, EventObject event) {
    iContainer fv = widget.getFormViewer();
    iFunctionCallback cb = (iFunctionCallback) fv.getAttribute("dlgCallback");
    fv.getWindow().close();
    if (cb != null) {
      cb.finished(true, null);
    }
  }

  public void onListAction(String eventName, iWidget widget, EventObject event) {
    widget.getFormViewer().getWidget("okButton").setEnabled(true);
  }

}
