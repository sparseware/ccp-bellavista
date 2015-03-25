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

import java.beans.PropertyChangeEvent;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iDataCollection;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.spot.GroupBox;
import com.appnativa.rare.spot.Tab;
import com.appnativa.rare.spot.TabPane;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.spot.Widget;
import com.appnativa.rare.ui.ActionBar;
import com.appnativa.rare.ui.BorderUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIImage;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.iCollapsible;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.iListHandler;
import com.appnativa.rare.ui.iPlatformBorder;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.border.UICompoundBorder;
import com.appnativa.rare.ui.border.UILineBorder;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.event.EventBase;
import com.appnativa.rare.ui.event.FlingEvent;
import com.appnativa.rare.viewer.ImagePaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.ToolBarViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.aListViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.ObjectHolder;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.Settings.Server;

public class MainEventHandler implements iEventHandler {
  UILineBorder         lineBorder;
  static CharSequence  actionBarTitle;
  static iPlatformIcon actionBarIcon;

  public MainEventHandler() {
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {
  }

  public void onLoginFormEnter(String eventName, iWidget widget, EventObject event) {
    if (widget.getName().equals("password")) {
      signIn(eventName, widget, event);
    } else if (widget.getName().equals("username")) {
      widget.getFormViewer().getWidget("password").requestFocus();
    }

  }

  public void onFlagsListAction(String eventName, iWidget widget, EventObject event) {
    final aListViewer lv = (aListViewer) widget;
    String id = lv.getSelectionDataAsString();
    aWidget tw = (aWidget) widget.getFormViewer().getWidget("flagText");
    if (id != null && !id.equals(tw.getLinkedData())) {
      try {
        ActionLink l = Platform.getWindowViewer().createActionLink("/hub/main/patient/flag/" + id + ".html");
        tw.setDataLink(l);
        tw.setLinkedData(id);
      } catch (MalformedURLException e) {
        Utils.handleError(e);
      }
    }
  }

  public void onCreatedFlagsPopup(String eventName, iWidget widget, EventObject event) {
    Viewer cfg = (Viewer) ((DataEvent) event).getData();
    if(UIScreen.isLargeScreen()) {
      Widget w=cfg.findWidget("bv.action.fullscreen");
      w.visible.setValue(false);
    }
    else {
      Widget w=cfg.findWidget("okButton");
      w.visible.setValue(false);
    }
  }

  public void onConfigureFlagsPopup(String eventName, iWidget widget, EventObject event) {
    iDataCollection dc = CollectionManager.getInstance().getCollection("flags");
    Collection<RenderableDataItem> rows = dc.getItemData(widget, false);
    iContainer fv = widget.getContainerViewer();
    final aListViewer lv = (aListViewer) fv.getWidget("flagsList");
    lv.clear();
    for (RenderableDataItem row : rows) {
      RenderableDataItem item = row.get(0);
      lv.addEx(item);
    }
    lv.refreshItems();
    if (!lv.isEmpty()) { //should never be at this point
      lv.setSelectedIndex(0);
      Platform.invokeLater(new Runnable() {

        @Override
        public void run() {
          lv.fireActionForSelected();
        }
      });
    }
  }

  public void goBackInStackPane(String eventName, iWidget widget, EventObject event) {
    final StackPaneViewer sp = Utils.getStackPaneViewer(widget);
    if (sp != null) {
      final int pos = sp.getEntryCount() - 1;
      if (pos > -1) {

        sp.switchTo(pos - 1, new iFunctionCallback() {

          @Override
          public void finished(boolean canceled, Object returnValue) {
            if (!sp.isDisposed() && sp.getEntryCount() == pos + 1) {
              iViewer v = sp.removeViewer(pos);
              if (v != null) {
                v.dispose();
              }
            }
          }
        });
      }
    }
  }

  public void moveListItemDown(String eventName, iWidget widget, EventObject event) {
    String list = ((ActionEvent) event).getQueryString();
    iFormViewer fv = widget.getFormViewer();
    aListViewer lb = (aListViewer) fv.getWidget(list);
    int n = lb.getSelectedIndex();
    int len = lb.size();
    if (n > -1 && n < len - 2) {
      lb.clearSelection();
      lb.swap(n, n + 1);
      lb.setSelectedIndex(n + 1);
      if (n == len - 1) {
        widget.setEnabled(false);
      }
      fv.getWidget("upButton").setEnabled(true);

    }
  }

  public void clearSelection(String eventName, iWidget widget, EventObject event) {
    if (widget instanceof iListHandler) {
      ((iListHandler) widget).clearSelection();
    }
  }

  public void moveListItemUp(String eventName, iWidget widget, EventObject event) {
    String list = ((ActionEvent) event).getQueryString();
    iFormViewer fv = widget.getFormViewer();
    aListViewer lb = (aListViewer) fv.getWidget(list);
    int n = lb.getSelectedIndex();
    if (n > 0) {
      lb.clearSelection();
      lb.swap(n, n - 1);
      lb.setSelectedIndex(n - 1);
      if (n == 1) {
        widget.setEnabled(false);
      }
      fv.getWidget("downButton").setEnabled(true);
    }
  }

  public void requestFocus(String eventName, iWidget widget, EventObject event) {
    String name = ((EventBase) event).getQueryString();
    if("username".equals(name) && Platform.isTouchDevice()) {
      return;
    }
    iFormViewer fv = widget.getFormViewer();
    fv.getWidget(name).requestFocus();
  }

  public void onConfigureMainView(String eventName, iWidget widget, EventObject event) {
    PatientSelect.updateActionBar();
  }

  public void handleComboBoxMenuBorder(String eventName, iWidget widget, EventObject event) {

    if (lineBorder == null) {
      lineBorder = new UILineBorder(UILineBorder.getDefaultLineColor());
    }
    iPlatformBorder b = (iPlatformBorder) widget.getAttribute("_border");
    if (b == null) {
      b = new UICompoundBorder(lineBorder, BorderUtils.TWO_POINT_EMPTY_BORDER);
    }
    widget.setAttribute("_border", widget.getBorder());
    widget.setBorder(b);
  }

  public void sortColumnZero(String eventName, iWidget widget, EventObject event) {
    ((TableViewer) widget).sort(0, false);
  }

  public void sortColumnOne(String eventName, iWidget widget, EventObject event) {
    ((TableViewer) widget).sort(1, false);
  }

  /**
   * This method modifies a page navigator configuration to show the navigation
   * buttons when not running on a touch device or deletes then if on a touch
   * device
   */
  public void onPageNavigatorCreated(String eventName, iWidget widget, EventObject event) {
    GroupBox cfg = (GroupBox) ((DataEvent) event).getData();
    boolean touch = Platform.isTouchDevice();
    for (String s : Arrays.asList("firstPage", "nextPage", "previousPage", "lastPage")) {
      Widget w = cfg.findWidget(s, false);
      if (touch) {
        cfg.removeWidget(w);
      } else {
        w.visible.setValue(true);
      }
    }
  }

  public void onRegionFling(String eventName, iWidget widget, EventObject event) {

  }

  public void onTabPaneCreated(String eventName, iWidget widget, EventObject event) {
    ActionPath p = Utils.getActionPath(false);
    String tab = p == null ? null : p.shift();
    if (tab != null) {
      int index = -1;
      TabPane cfg = (TabPane) ((DataEvent) event).getData();
      int len = cfg.tabs.size();
      for (int i = 0; i < len; i++) {
        if (((Tab) cfg.tabs.getEx(i)).name.equals(tab)) {
          index = i;
          break;
        }
      }
      if (index != -1) {
        cfg.selectedIndex.setValue(index);
      }
    }

  }

  /**
   * Makes the specified widget an expanding widget in a toolbar. The parent of
   * the widget is assumed to be a toolbar viewer
   * 
   */
  public void onMakeExpander(String eventName, iWidget widget, EventObject event) {
    ToolBarViewer tb = (ToolBarViewer) widget.getParent();
    tb.setAsExpander(widget, true);
  }

  /**
   * Makes the specified widget a dragger for its window.
   */
  public void onMakeWindowDragger(String eventName, iWidget widget, EventObject event) {
    ((WindowViewer) widget.getWindow().getViewer()).addWindowDragger(widget);
  }

  /**
   * Closes the window that the specified widget is attached to
   */
  public void onCloseWindow(String eventName, iWidget widget, EventObject event) {
    widget.getWindow().close();
  }

  public void onInfobarOrientationWillChange(String eventName, iWidget widget, EventObject event) {
    Object config = ((PropertyChangeEvent) event).getNewValue();
    iContainer fv = widget.getContainerViewer();
    adjustInfobar(fv, UIScreen.isWiderForConfiguration(config));
  }

  protected void adjustInfobar(iContainer fv, boolean visible) {
    fv.getWidget("name_label").setVisible(visible);
    fv.getWidget("age_sex_label").setVisible(visible);
    fv.getWidget("wt_ht_bmi_label").setVisible(visible);
    fv.getWidget("location_label").setVisible(visible);
    fv.getWidget("location").setVisible(visible);
    fv.getWidget("code_status").setVisible(visible);
    fv.getWidget("code_status_label").setVisible(visible);
    iWidget dr = fv.getWidget("provider_label");
    String s = Platform.getResourceAsString(visible ? "bv.text.provider" : "bv.text.hcp");
    dr.setValue(s + ":");
    fv.update();
  }

  public void onCollapsiblePaneToggle(String eventName, iWidget widget, EventObject event) {
    iCollapsible pane = (iCollapsible) Platform.getPlatformComponent(event.getSource());
    if (pane != null) {
      pane.setShowTitle(pane.isExpanded());
    }
  }

  public void onFormHeaderFlung(String eventName, iWidget widget, EventObject event) {
    FlingEvent fling = (FlingEvent) event;
    float vy = fling.getYVelocity();
    float vx = fling.getXVelocity();
    if (Math.abs(vy) > Math.abs(vx)) {
      ActionBar ab = Platform.getWindowViewer().getActionBar();
      if (vy < 0) {
        if (ab.isVisible()) {
          ab.setVisible(false);
        }
      } else {
        if (!ab.isVisible()) {
          ab.setVisible(true);
        }
      }
    }
  }

  public void onFlingInfoBar(String eventName, iWidget widget, EventObject event) {
    FlingEvent fe = (FlingEvent) event;
    float y = fe.getYVelocity();
    iCollapsible pane = widget.getViewer().getCollapsiblePane();
    if (pane != null) {
      if (y < 0) {
        pane.collapsePane();
        pane.setShowTitle(true);
      } else {
        pane.setShowTitle(false);
        pane.expandPane();
      }
    }
  }

  public void onConfigureInfoBar(String eventName, iWidget widget, EventObject event) {
    iFormViewer fv = widget.getFormViewer();
    final ImagePaneViewer ip = (ImagePaneViewer) fv.getWidget("photo");
    if (ip != null) {
      JSONObject patient = (JSONObject) Platform.getAppContext().getData("patient");
      String s;
      ip.setImage(Platform.getAppContext().getResourceAsImage("bv.icon.no_photo"));
      s = patient.optString("photo");
      if (s != null && s.length() > 0) {
        try {
          ActionLink link = Utils.createPhotosActionLink(s, false);
          if (link != null) {
            Platform.getWindowViewer().getContent(ip, link, ActionLink.ReturnDataType.IMAGE, new iFunctionCallback() {

              @Override
              public void finished(boolean canceled, Object returnValue) {
                if (returnValue instanceof ObjectHolder && !ip.isDisposed()) {
                  ObjectHolder oh = (ObjectHolder) returnValue;
                  if (oh.value instanceof UIImage) {
                    ip.setImage((UIImage) oh.value);
                  }
                }
              }
            });
          }
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
      if (!UIScreen.isWider()) {
        adjustInfobar(fv, false);
      }
    }
    CollectionManager.getInstance().updateUI();
    PushButtonWidget pb=(PushButtonWidget) fv.getWidget("bv.action.flags");
    if(pb!=null && pb.isEnabled()) {
      JSONObject info = (JSONObject) Platform.getAppContext().getData("patientSelectInfo");
      if(info.optBoolean("autoShowFlags", false)) {
        pb.click();
      }
    }
  }

  public void signIn(String eventName, iWidget widget, EventObject event) {
    iFormViewer fv = widget.getFormViewer();
    String password = fv.getWidget("password").getValueAsString();
    if (Utils.isApplicationLocked()) {
      Utils.resignIn(widget, password);
    } else {
      String username = fv.getWidget("username").getValueAsString();
      Server server = (Server) fv.getWidget("server").getSelectionData();
      Utils.signIn(widget, username, password, server);
    }
  }

}
