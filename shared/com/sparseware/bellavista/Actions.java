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
 * This class handles the actions on the action bar, some instances of the
 * fullscreen button, and other buttons that use a global action.
 * <p>
 * Globals actions are used when  multiple buttons can invoke the same action.
 * In those instances the program will operate on the action (e.g. set enabled/disabled states) instead of the button.
 * </p>
 *
 * @author Don DeCoteau
 *
 */
public class Actions implements iEventHandler {
  public Actions() {}

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {}

  /**
   * Called by a back button to go back to the previous
   * screen.
   */
  public void onGoBack(String eventName, iWidget widget, EventObject event) {
    Utils.popViewerStack();
  }
  
/**
 * Called by the exit button to exit the application
 */
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
/**
 * Called by the preferences/settings button to show the preferences dialog s dialog
 */
  public void onPreferences(String eventName, iWidget widget, EventObject event) {
    Utils.showDialog("/settings.rml", true, true);
  }
  /**
   * Called by the lock button to lock the application
   */
  public void onLock(String eventName, iWidget widget, EventObject event) {
    Utils.lockApplication(false);
  }

  /**
   * Called by the change patient button to select a new patient
   */
  public void onChangePatient(String eventName, iWidget widget, EventObject event) {
    PatientSelect.changePatient(widget, null);
  }

  /**
   * Called by the lock flags button to display the patient flags
   */
  public void onFlags(String eventName, iWidget widget, EventObject event) {
    try {
      Platform.getWindowViewer().openDialog("/flags.rml");
    } catch(Exception e) {
      Utils.handleError(e);
    }
  }

  /**
   * Called to configure a full screen button
   */
  public void onConfigureFullscreenButton(String eventName, iWidget widget, EventObject event) {
    if (!UIScreen.isLargeScreen()) {
      widget.setVisible(false);
    } else {
      iPlatformIcon fullscreen = Platform.getResourceAsIcon("bv.icon.fullscreen");
      UIAction      action     = Platform.getWindowViewer().getAction("bv.action.fullscreen");

      action.setIcon(fullscreen);
    }
  }

  /**
   * Called when the full screen button is shown.
   * This sets the context of the action to be the button widget being shown.
   * When the the event handler for the full screen action is invoke it will be whit this widget.
   * See the {@link #onFullscreen} method to see how this is used
   * 
   */
  public void onShownFullscreenButton(String eventName, iWidget widget, EventObject event) {
    if (UIScreen.isLargeScreen()) {
      UIAction action = Platform.getWindowViewer().getAction("bv.action.fullscreen");

      action.setContext(widget);
    }
  }

  /**
   * Called by the order cart button to show the cart
   */
  public void onShowCart(String eventName, iWidget widget, EventObject event) {
    OrderManager.showCart();
  }

  /**
   * Called by order discontinue menu action to discontinue an order
   */
  public void onOrderDiscontinue(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderDiscontinue(table, index);
  }

  /**
   * Called by order renew and discontinue menu action to renew and discontinue an order
   */
  public void onOrderRenewAndDiscontinue(String eventName, iWidget widget, EventObject event) {
    TableViewer        table     = (TableViewer) widget;
    int                index     = table.getContextMenuIndex();
    RenderableDataItem row       = table.get(index);
    RenderableDataItem orderItem = row.get(1);
    RenderableDataItem order     = row.get(0);
    String             type      = order.toString();

    OrderManager.orderRenewAndDiscontinue(type, orderItem, (String) order.getLinkedData());
  }

  /**
   * Called by order sign button to sign an order
   */
  public void onOrderSign(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderSign(table, index);
  }

  /**
   * Called by order rewrite menu action to rewrite an order
   */
  public void onOrderRewrite(String eventName, iWidget widget, EventObject event) {
    TableViewer        table     = (TableViewer) widget;
    int                index     = table.getContextMenuIndex();
    RenderableDataItem row       = table.get(index);
    RenderableDataItem orderItem = row.get(1);
    String             type      = row.get(0).toString();

    OrderManager.orderRenew(type, orderItem);
  }

  /**
   * Called by new order button/menu action to write a new order
   */
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

  /**
   * Called by order hold menu action to hold an order
   */
  public void onOrderHold(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeHoldStatus(table, index, true);
  }

  /**
   * Called by un-hold rewrite menu action to un-hold an order
   */
  public void onOrderUnHold(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeHoldStatus(table, index, false);
  }

  /**
   * Called by order flag menu action to flag an order
   */
  public void onOrderFlag(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeFlagStatus(table, index, true);
  }

  /**
   * Called by order un-flag menu action to un-flag an order
   */
  public void onOrderUnFlag(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    int         index = table.getContextMenuIndex();

    OrderManager.orderChangeFlagStatus(table, index, false);
  }

  /**
   * Called when a full screen button is pressed.
   * If the the button has an action listener set as its linked data
   * then that listener is invoked.
   * <p>If were are running on a large screen, then the visibility of the first 
   * region of the {@link SplitPaneViewer} that contains the widget is toggled</p>
   * <p>
   * When not in running on a large screen then the full screen button just acts as a back button.
   * Normally, this should never be the case as the button is hidden by the {@link #onConfigureFullscreenButton} method
   * </p>
   * @param eventName
   * @param widget
   * @param event
   */
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

  /**
   * Called in the case here we are is pseudo full screen mode.
   * This is use in cases where the platform has a back button.
   * If is those then the pressing the back button will exit the mode.
   * 
   * @return true if we were in pseudo full screen mode and were able to exit it; false otherwise
   */
  public static boolean handledPseudoFullScreenMode() {
    WindowViewer w      = Platform.getWindowViewer();
    UIAction     action = w.getAction("bv.action.fullscreen");

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
