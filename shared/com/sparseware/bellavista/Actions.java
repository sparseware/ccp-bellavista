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

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.ActionBar;
import com.appnativa.rare.ui.UIAction;
import com.appnativa.rare.ui.UIImageIcon;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.viewer.SplitPaneViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.widget.iWidget;

/**
 * This class handles the actions on the action bar
 * and some instances of the fullscreen button
 * 
 * @author Don DeCoteau
 *
 */
public class Actions implements iEventHandler {

  public Actions() {
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {
  }

  public void onExit(String eventName, iWidget widget, EventObject event) {
    Utils.exit();
  }

  public void onPreferences(String eventName, iWidget widget, EventObject event) {
    Utils.showDialog("/settings.rml", false, false);
  }

  public void onLock(String eventName, iWidget widget, EventObject event) {
    Utils.lockApplication(false);
  }

  public void onChangePatient(String eventName, iWidget widget, EventObject event) {
    PatientSelect.changePatient(widget, null);
  }

  public void onFlags(String eventName, iWidget widget, EventObject event) {
    try {
      if(UIScreen.isLargeScreen()) {
       Platform.getWindowViewer().openDialog("/flags.rml");
      }
      else {
        Utils.pushWorkspaceViewer("/flags.rml");
      }
    } catch (Exception e) {
      Utils.handleError(e);
    }
  }

  public void onConfigureFullscreen(String eventName, iWidget widget, EventObject event) {
    if (!UIScreen.isLargeScreen()) {
      widget.setIcon(Platform.getResourceAsIcon("bv.icon.shrink"));
    }
  }

  public void onFullscreen(String eventName, iWidget widget, EventObject event) {
    Object l = widget.getLinkedData();
    if (l instanceof iActionListener) {
      ((iActionListener) l).actionPerformed((ActionEvent) event);
      return;
    }
    if (!UIScreen.isLargeScreen()) {
      Utils.popWorkspaceViewer();
    } else {
      SplitPaneViewer sp=Utils.getSplitPaneViewer(widget);
      if (sp==null) {
        return;
      }
      WindowViewer w = Platform.getWindowViewer();
      ActionBar ab = w.getActionBar();
      UIImageIcon fullscreen = w.getResourceIcon("bv.icon.fullscreen");
      UIImageIcon shrink = w.getResourceIcon("bv.icon.shrink");
      UIAction action=w.getAction("bv.action.fullscreen"); //use the action so that all fullscreen buttons are changed
      if (action.getIcon() == fullscreen) {
        action.setIcon(shrink);
        sp.setRegionVisible(0, false);
        w.getTarget("patient_info").setVisible(false);
        ab.setTitle((CharSequence) w.getAppContext().getData("pt_name"));

      } else {
        if (!Platform.getPlatform().isDesktop()) {
          Platform.setUseFullScreen(false);
        }
        action.setIcon(fullscreen);
        w.getTarget("patient_info").setVisible(true);
        sp.setRegionVisible(0, true);
        ab.setTitle(w.getTitle());
      }
    }
  }
}
