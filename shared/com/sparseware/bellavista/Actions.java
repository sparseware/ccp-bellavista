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

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.ActionBar;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIAction;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.WindowEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.viewer.SplitPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.json.JSONObject;

import java.util.EventObject;

/**
 * This class handles the actions on the action bar and some instances of the
 * fullscreen button
 *
 * @author Don DeCoteau
 *
 */
public class Actions implements iEventHandler {
  public Actions() {}

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {}

  public void onGoBack(String eventName, iWidget widget, EventObject event) {
    Utils.popViewerStack();
  }

  public void onExit(String eventName, iWidget widget, EventObject event) {
    if (event instanceof WindowEvent) {
      ((WindowEvent) event).consume();
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          Utils.exit();
        }
      });
    } else {
      Utils.exit();
    }
  }

  public void onPreferences(String eventName, iWidget widget, EventObject event) {
    Utils.showDialog("/settings.rml", true, true);
  }

  public void onLock(String eventName, iWidget widget, EventObject event) {
    Utils.lockApplication(false);
  }

  public void onChangePatient(String eventName, iWidget widget, EventObject event) {
    PatientSelect.changePatient(widget, null);
  }

  public void onFlags(String eventName, iWidget widget, EventObject event) {
    try {
      if (UIScreen.isLargeScreen()) {
        Platform.getWindowViewer().openDialog("/flags.rml");
      } else {
        Utils.pushWorkspaceViewer("/flags.rml");
      }
    } catch(Exception e) {
      Utils.handleError(e);
    }
  }

  public void onConfigureFullscreenButton(String eventName, iWidget widget, EventObject event) {
    if (!UIScreen.isLargeScreen()) {
      widget.setVisible(false);
    } else {
      iPlatformIcon fullscreen = Platform.getResourceAsIcon("bv.icon.fullscreen");
      UIAction      action     = Platform.getWindowViewer().getAction("bv.action.fullscreen");

      action.setIcon(fullscreen);
    }
  }

  public void onShownFullscreenButton(String eventName, iWidget widget, EventObject event) {
    if (UIScreen.isLargeScreen()) {
      UIAction action = Platform.getWindowViewer().getAction("bv.action.fullscreen");

      action.setContext(widget);
    }
  }

  public void onScanBarCode(String eventName, iWidget widget, EventObject event) {}

  public void onShowCart(String eventName, iWidget widget, EventObject event) {
    OrderManager.showCart();
  }

  public void onOrderDiscontinue(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderDiscontinue(table, index);
  }

  public void onOrderRenewAndDiscontinue(String eventName, iWidget widget, EventObject event) {
    TableViewer        table     = (TableViewer) widget;
    int                index     = table.getContextMenuIndex();
    RenderableDataItem row       = table.get(index);
    RenderableDataItem orderItem = row.get(1);
    RenderableDataItem order     = row.get(0);
    String             type      = order.toString();

    OrderManager.orderRenewAndDiscontinue(type, orderItem, (String) order.getLinkedData());
  }

  public void onOrderSign(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderSign(table, index);
  }

  public void onOrderRewrite(String eventName, iWidget widget, EventObject event) {
    TableViewer        table     = (TableViewer) widget;
    int                index     = table.getContextMenuIndex();
    RenderableDataItem row       = table.get(index);
    RenderableDataItem orderItem = row.get(1);
    String             type      = row.get(0).toString();

    OrderManager.orderRenew(type, orderItem);
  }

  public void onOrderNew(String eventName, iWidget widget, EventObject event) {
    if(!Utils.clearViewerStack()) {
      return;
    }
    if (widget instanceof TableViewer) {
      TableViewer        table       = (TableViewer) widget;
      String             type        = null;
      RenderableDataItem reorderItem = null;
      JSONObject         info        = (JSONObject) Platform.getAppContext().getData("ordersInfo");

      if (table.getName().equals("proceduresTable")) {
        type = info.optString("proceduresOrderType", null);
      } else if (table.getName().equals("labsTable")) {
        type = info.optString("labsOrderType", null);
      } else if (table.getName().equals("ordersTable")) {
        int                index = table.getContextMenuIndex();
        RenderableDataItem row   = table.get(index);

        reorderItem = row.get(1);
        type        = row.get(0).toString();
      }

      OrderManager.orderNew(type, reorderItem);
    } else {
      OrderManager.orderNew(null, null);
    }
  }

  public void onOrderHold(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeHoldStatus(table, index, true);
  }

  public void onOrderUnHold(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeHoldStatus(table, index, false);
  }

  public void onOrderFlag(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeFlagStatus(table, index, true);
  }

  public void onOrderUnFlag(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeFlagStatus(table, index, false);
  }

  public void onFullscreen(String eventName, iWidget widget, EventObject event) {
    Object l = widget.getLinkedData();

    if (l instanceof iActionListener) {
      ((iActionListener) l).actionPerformed((ActionEvent) event);

      return;
    }

    if (!UIScreen.isLargeScreen()) {
      Utils.popViewerStack();
    } else {
      SplitPaneViewer sp = Utils.getSplitPaneViewer(widget);

      if (sp == null) {
        return;
      }
      WindowViewer w      = Platform.getWindowViewer();
      ActionBar    ab     = w.getActionBar();
      UIAction     action = w.getAction("bv.action.fullscreen");    //use the action so that all fullscreen buttons are changed
      if(!sp.isRegionVisible(0)) {
        action.setIcon(w.getResourceIcon("bv.icon.fullscreen"));
        w.getTarget("patient_info").setVisible(true);
        sp.setRegionVisible(0, true);
        ab.setTitle(w.getTitle());
      } else {
        action.setIcon(w.getResourceIcon("bv.icon.shrink"));
        sp.setRegionVisible(0, false);
        w.getTarget("patient_info").setVisible(false);
        ab.setTitle((CharSequence) w.getAppContext().getData("pt_name"));
      }
    }
  }

  public static boolean handledPseudoFullScreenMode() {
    WindowViewer w      = Platform.getWindowViewer();
    UIAction     action = w.getAction("bv.action.fullscreen");    //use the action so that all fullscreen buttons are changed

    if ((action != null)) {
      PushButtonWidget pb = (PushButtonWidget) action.getContext();

      if ((pb != null) &&!pb.isDisposed() && pb.isAttached() && pb.isEnabled()) {
        SplitPaneViewer sp = Utils.getSplitPaneViewer(pb);

        if (sp == null) {
          return false;
        }
        if(!sp.isRegionVisible(0)) {
          pb.click();

          return true;
        }
      }
    }
    return false;
  }
}
