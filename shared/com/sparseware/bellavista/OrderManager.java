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
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.ActionLink.RequestEncoding;
import com.appnativa.rare.spot.PasswordField;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.spot.Widget;
import com.appnativa.rare.ui.AlertPanel;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIAction;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIColorHelper;
import com.appnativa.rare.ui.UICompoundIcon;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UINotifier;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.UITextIcon;
import com.appnativa.rare.ui.ViewerCreator;
import com.appnativa.rare.ui.border.UILineBorder;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.viewer.FormViewer;
import com.appnativa.rare.viewer.ListBoxViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.CheckBoxWidget;
import com.appnativa.rare.widget.ComboBoxWidget;
import com.appnativa.rare.widget.LabelWidget;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.TextFieldWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.IdentityArrayList;
import com.appnativa.util.ObjectCache;
import com.appnativa.util.ObjectHolder;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

import com.sparseware.bellavista.Utils.BadgeCompoundIcon;
import com.sparseware.bellavista.oe.FieldValue;
import com.sparseware.bellavista.oe.FormsManager;
import com.sparseware.bellavista.oe.Order;
import com.sparseware.bellavista.oe.Order.ActionType;
import com.sparseware.bellavista.oe.OrderFields;

import java.io.FileNotFoundException;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This manages ordering
 *
 * @author Don DeCoteau
 */
public class OrderManager {
  public static final String              ORDER_CART_KEY     = "_orderCart";
  public static final String              ORDER_STATUSES_KEY = "_orderStatuses";
  protected static ObjectCache            cachedLists;
  private static int                      pendingOrderCount;
  private static int                      lastPendingOrderCount;
  private static UITextIcon               pendingOrderCountIcon;
  private static UICompoundIcon           compoundIcon1;
  private static UICompoundIcon           compoundIcon2;
  public static String                    DEMO_PATIENT_ORDER_KEY      = "_demoOrderInfo";
  public static String                    DEMO_ORDER_STATUS_FLAGS_KEY = "statusFlags";
  private static JSONObject               ordersFilter;
  private static Viewer                   filterConfiguration;
  private static Viewer                   cartConfiguration;
  private static List<RenderableDataItem> orderRoutes;
  private static List<RenderableDataItem> orderTypes;
  private static Order                    editorOrder;
  private static long                     lastActionTime;
  private static Object                   orderCart;
  private static Object                   demoOrderInfo;
  private static Object                   orderStatuses;
  private static String                   orderingUser;
  private static String                   patientForOrders;
  private static ShowOrderFormCallback    showOrderFormCallback;

  private OrderManager() {}

  public void cleanupAfterOrdering() {
    filterConfiguration = null;
  }

  public static void addOrderToCart(Order order) {
    lastActionTime = System.currentTimeMillis();

    IdentityArrayList<Order> list = getOrderCart(true);

    list.addIfNotPresent(order);

    String id = order.linkedOrderID;

    if (id != null) {
      Map map = getOrderStatuses(true);

      map.put(id, order.actionType);
    }

    pendingOrderCount = list.size();
    updateCartIcon();
  }

  /**
   * Called to see if we can exit when the user initiates and exit or change
   * patient action.
   *
   * If there are orders pending this method will return false and handle the
   * prompting of the user informing about un-submitted orders.
   *
   * @param exit
   *          true if the user wants to exit; false if the user wants to change
   *          patient
   * @param path
   *          and action path to pass to the change patient logic.
   *
   * @return true if we can exit ; false otherwise
   */
  public static boolean canChangePatientOrExit(final boolean exit, final ActionPath path) {
    final WindowViewer w  = Platform.getWindowViewer();
    int                oc = getPendingOrderCount();

    if (oc == 0) {
      while(Utils.popViewerStack()) {}
      ;

      if (Utils.getViewerStackSize() > 0) {
        return false;
      }

      return true;
    }

    if ((path != null) &&!path.isEmpty()) {
      return true;
    }

    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (canceled || (returnValue == null)) {
          return;
        }

        if (returnValue instanceof Exception) {
          w.handleException((Exception) returnValue);
        }

        if (Boolean.TRUE.equals(returnValue)) {
          showCart();
        } else {
          emptyOrderCartEx();
          updateCartIcon();

          if (!exit) {
            PatientSelect.changePatient(w, path);
          } else {
            Utils.exitEx();
          }
        }
      }
    };
    String s  = w.getString("bv.oe.text.has_pending_orders", oc);
    String no = w.getString(exit
                            ? "bv.oe.text.exit_app"
                            : "bv.oe.text.delete_cart");

    w.yesNoCancel(null, s, w.getString("bv.oe.text.goto_cart"), no, cb);

    return false;
  }

  public static void emptyOrderCart() {
    emptyOrderCartEx();
    updateCartIcon();

    TableViewer table = (TableViewer) Platform.getWindowViewer().getViewer("ordersTable");

    if (table != null) {
      int len = table.getRowCount();

      for (int i = 0; i < len; i++) {
        RenderableDataItem row = table.get(i);

        row.unsetUserStateFlag(Orders.STOPPED_FLAG);
        row.setFont(null);
        row.setForeground(null);
      }

      table.refreshItems();
    }
  }

  public static void getCachedList(final String name, final String href, final iFunctionCallback cb) {
    if (cachedLists != null) {
      Object o = cachedLists.get(name);

      if (o != null) {
        cb.finished(false, o);

        return;
      }
    }

    aWorkerTask task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          ActionLink l = new ActionLink(href);

          return Platform.getWindowViewer().parseDataLink(l, false);
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        if (result instanceof Exception) {
          cb.finished(true, result);
        } else {
          List<RenderableDataItem> list = (List<RenderableDataItem>) result;

          if (cachedLists == null) {
            cachedLists = new ObjectCache();
            cachedLists.setPurgeInline(true);
            cachedLists.setStrongReferences(true);
            cachedLists.setBufferSize(10);
          }

          cachedLists.put(name, list);
          cb.finished(false, result);
        }
      }
    };

    Platform.getWindowViewer().spawn(task);
  }

  public static JSONObject getDemoOrderObject(boolean create) {
    if (Utils.isDemo()) {
      JSONObject patient = Utils.getPatient();
      JSONObject o       = patient.optJSONObject(DEMO_PATIENT_ORDER_KEY);

      if ((o == null) && create) {
        o = new JSONObject();
        patient.put(DEMO_PATIENT_ORDER_KEY, o);
      }

      return o;
    }

    return null;
  }

  public static int getDemoOrderStatus(JSONObject order) {
    return order.optInt(OrderManager.DEMO_ORDER_STATUS_FLAGS_KEY, -1);
  }

  public static long getLastOrderActionTtime() {
    return lastActionTime;
  }

  public static Order getOrderBeingEdited() {
    return editorOrder;
  }

  public static IdentityArrayList<Order> getOrderCart(boolean create) {
    JSONObject               o    = Utils.getPatient();
    ObjectHolder             oh   = (ObjectHolder) o.opt(ORDER_CART_KEY);
    IdentityArrayList<Order> list = (oh == null)
                                    ? null
                                    : (IdentityArrayList<Order>) oh.value;

    if ((list == null) && create) {
      list      = new IdentityArrayList<Order>();
      oh        = new ObjectHolder(list);
      oh.source = System.currentTimeMillis();
      o.put(ORDER_CART_KEY, oh);
    }

    return list;
  }

  public static long getOrderCartLastModifiedTime() {
    JSONObject   o  = Utils.getPatient();
    ObjectHolder oh = (ObjectHolder) o.opt(ORDER_CART_KEY);

    return (oh == null)
           ? 0
           : ((Long) oh.source);
  }

  public static JSONObject getOrdersFilter() {
    return ordersFilter;
  }

  /**
   * Gets the order information object for the corresponding key
   * @param key the key for the info
   * @return the order information object for the corresponding key
   */
  public static JSONObject getOrdersInfo(String key) {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("ordersInfo");

    info = info.getJSONObject(key);

    if (info != null) {
      if (!info.optBoolean("_resolved")) {
        info.put("_resolved", true);

        JSONArray  a     = info.optJSONArray("fields");
        String     link  = info.optString("linkedFields", null);
        JSONObject linfo = (link == null)
                           ? null
                           : getOrdersInfo(link);    //call so the the linked info is resolved
        JSONArray  la    = (linfo == null)
                           ? null
                           : linfo.optJSONArray("fields");

        if (la != null) {
          if (a == null) {
            a = la;
          } else {
            a.addAll(la);
          }
        }

        if ((a != null) &&!a.isEmpty()) {
          info.put("fields", a);
        } else {
          info.remove("fields");
        }
      }
    }

    return info;
  }

  public static Map getOrderStatuses(boolean create) {
    JSONObject              o   = Utils.getPatient();
    Map<String, ActionType> map = (Map<String, ActionType>) o.opt(ORDER_STATUSES_KEY);

    if ((map == null) && create) {
      map = new HashMap<String, ActionType>();
      o.put(ORDER_STATUSES_KEY, map);
    }

    return map;
  }

  public static int getPendingOrderCount() {
    return pendingOrderCount;
  }

  /**
   * Get the icon that represents the number of alerts.
   */
  public static iPlatformIcon getPendingOrderCountIcon() {
    int taskCount = getPendingOrderCount();

    if (taskCount == 0) {
      lastPendingOrderCount = 0;

      return null;
    }

    if ((taskCount == lastPendingOrderCount) && (pendingOrderCountIcon != null)) {
      return pendingOrderCountIcon;
    }

    String value;

    if (taskCount > 9) {
      value = "9+";
    } else {
      value = StringCache.valueOf(taskCount);
    }

    UILineBorder b;

    if (pendingOrderCountIcon == null) {
      WindowViewer w = Platform.getWindowViewer();

      b = new UILineBorder(UIColor.WHITE, UIScreen.PLATFORM_PIXELS_2, 8, 8);
      b.setInsetsPadding(UIScreen.PLATFORM_PIXELS_2, UIScreen.PLATFORM_PIXELS_2, UIScreen.PLATFORM_PIXELS_2,
                         UIScreen.PLATFORM_PIXELS_2);

      UIFont font = w.getFont();

      font = font.deriveFont(UIFont.BOLD, font.getSize() - 4);

      UITextIcon icon = new UITextIcon(value, UIColor.WHITE, font, b);

      icon.setForegroundColor(UIColor.WHITE);    //needed because of bug in runtime constructor
      icon.setSquare(true);
      icon.setBackgroundColor(UIColor.GREEN.alpha(128));
      pendingOrderCountIcon = icon;
    } else {
      b = (UILineBorder) pendingOrderCountIcon.getBorder();
      pendingOrderCountIcon.setText(value);
    }

    lastPendingOrderCount = taskCount;
    pendingOrderCountIcon.setBorder(null);
    pendingOrderCountIcon.setPadding(0, 0);    //forces icon size to be updated

    int w = pendingOrderCountIcon.getIconWidth();

    b.setCornerArc(w);                         //makes the border rounded
    pendingOrderCountIcon.setBorder(b);
    pendingOrderCountIcon.setPadding(0, 0);    //forces icon size to be updated

    return pendingOrderCountIcon;
  }

  public static void getSignature(iFunctionCallback cb) {
    SignatureHandler handler = new SignatureHandler(cb);

    handler.handle();
  }

  public static boolean isDiscontinued(String orderID, Map orderStatuses) {
    ActionType type = (orderStatuses == null)
                      ? null
                      : (ActionType) orderStatuses.get(orderID);

    return (type == null)
           ? false
           : type == ActionType.DISCONTINUED;
  }

  public static void lockingApplication() {
    if ((editorOrder != null) && (showOrderFormCallback != null)) {
      showOrderFormCallback.finished(false, Boolean.TRUE);
    }

    JSONObject patient = Utils.getPatient();

    demoOrderInfo     = (patient == null)
                        ? null
                        : patient.opt(DEMO_PATIENT_ORDER_KEY);
    orderCart         = (patient == null)
                        ? null
                        : patient.opt(ORDER_CART_KEY);
    orderStatuses     = (patient == null)
                        ? null
                        : patient.opt(ORDER_STATUSES_KEY);
    patientForOrders  = Utils.getPatientID();
    orderingUser      = Utils.getUserID();
    editorOrder       = null;
    pendingOrderCount = 0;
  }

  public static void lockPatientRecord() {}

  public static void orderChangeFlagStatus(final TableViewer table, final int index, final boolean flag) {
    JSONObject info = getOrdersInfo(flag
                                    ? "flagInfo"
                                    : "unflagInfo");

    if (info.optJSONArray("fields") == null) {
      orderChangeFlagStatus(table, index, flag, null);
    } else {
      final WindowViewer              w     = Platform.getWindowViewer();
      final RenderableDataItem        row   = table.get(index);
      final ArrayList<WidgetDataLink> links = new ArrayList<OrderManager.WidgetDataLink>(2);
      final iContainer                c     = FormsManager.createFormContainer(w, row, info, null, links);
      iFunctionCallback               cb    = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          try {
            if (!canceled && Boolean.TRUE.equals(returnValue)) {
              FormViewer fv = (FormViewer) c.getWidget("formFields");

              orderChangeFlagStatus(table, index, flag, fv.getHTTPValuesHash());
            }
          } finally {
            c.dispose();
          }
        }
      };

      if (links.isEmpty()) {
        yesNo(w, info, c, cb);
      } else {
        handleLinks(links, w, info, c, cb);
      }
    }
  }

  public static void orderChangeFlagStatus(final TableViewer table, final int index, final boolean flag,
          final Map data) {
    final RenderableDataItem row  = table.get(index);
    final WindowViewer       w    = Platform.getWindowViewer();
    aWorkerTask              task = new aWorkerTask() {
      @Override
      public Object compute() {
        String id     = (String) row.get(0).getLinkedData();
        String status = null;

        if (!Utils.isDemo()) {
          try {
            JSONObject o = Utils.getContentAsJSON("/hub/main/orders/" + (flag
                    ? "flag/"
                    : "unflag/") + id, data, true);

            if (o.optBoolean("success")) {
              status = o.optString("statusMessage", status);
            } else {
              String s = o.optString("errorMessage", null);

              if (s == null) {
                s = Platform.getResourceAsString(flag
                                                 ? "bv.oe.text.flag_failed"
                                                 : "bv.oe.text.unflag_failed");
              } else {
                s = table.expandString(s);
              }

              return new MessageException(s);
            }
          } catch(Exception e) {
            return e;
          }
        }

        if (flag) {
          row.setUserStateFlag(Orders.FLAGGED_FLAG);
        } else {
          row.unsetUserStateFlag(Orders.FLAGGED_FLAG);
        }

        int istatus = row.getUserStateFlags();

        Orders.updateStatusIcon(row, row.get(0), istatus);

        if (Utils.isDemo()) {
          updateDemoOrderObject(id, DEMO_ORDER_STATUS_FLAGS_KEY, istatus);
        }

        lastActionTime = System.currentTimeMillis();

        return (status == null)
               ? null
               : w.expandString(status);
      }
      @Override
      public void finish(Object result) {
        w.hideWaitCursor();

        if (result instanceof Throwable) {
          Utils.handleError((Throwable) result);
        } else if (!table.isDisposed()) {
          if (result != null) {
            UINotifier.showMessage((String) result);
          }

          table.rowChanged(index);
        }
      }
    };

    if (Orders.showActionWaitMessage) {
      w.showProgressPopup(w.getString(flag
                                      ? "bv.oe.text.flagging_order"
                                      : "bv.oe.text.unflagging_order"));
    } else {
      w.showWaitCursor();
    }

    w.spawn(task);
  }

  public static void orderChangeHoldStatus(final TableViewer table, final int index, final boolean hold) {
    JSONObject info = getOrdersInfo(hold
                                    ? "holdInfo"
                                    : "unholdInfo");

    if (info.optJSONArray("fields") == null) {
      orderChangeHoldStatus(table, index, hold, null);
    } else {
      final WindowViewer              w     = Platform.getWindowViewer();
      final RenderableDataItem        row   = table.get(index);
      final ArrayList<WidgetDataLink> links = new ArrayList<OrderManager.WidgetDataLink>(2);
      final iContainer                c     = FormsManager.createFormContainer(w, row, info, null, links);
      iFunctionCallback               cb    = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          try {
            if (!canceled && Boolean.TRUE.equals(returnValue)) {
              FormViewer fv = (FormViewer) c.getWidget("formFields");

              orderChangeHoldStatus(table, index, hold, fv.getHTTPValuesHash());
            }
          } finally {
            c.dispose();
          }
        }
      };

      if (links.isEmpty()) {
        yesNo(w, info, c, cb);
      } else {
        handleLinks(links, w, info, c, cb);
      }
    }
  }

  public static void orderChangeHoldStatus(final TableViewer table, final int index, final boolean hold,
          final Map data) {
    final RenderableDataItem row  = table.get(index);
    final WindowViewer       w    = Platform.getWindowViewer();
    aWorkerTask              task = new aWorkerTask() {
      @Override
      public Object compute() {
        String id     = (String) row.get(0).getLinkedData();
        String status = null;

        if (!Utils.isDemo()) {
          try {
            JSONObject o = Utils.getContentAsJSON("/hub/main/orders/" + (hold
                    ? "hold/"
                    : "unhold/") + id, data, true);

            if (o.optBoolean("success")) {
              status = o.optString("statusMessage", status);
            } else {
              String s = o.optString("errorMessage", null);

              if (s == null) {
                s = Platform.getResourceAsString(hold
                                                 ? "bv.oe.text.hold_failed"
                                                 : "bv.oe.text.unhold_failed");
              } else {
                s = table.expandString(s);
              }

              return new MessageException(s);
            }
          } catch(Exception e) {
            return e;
          }
        }

        if (hold) {
          row.setUserStateFlag(Orders.ONHOLD_FLAG);
        } else {
          row.unsetUserStateFlag(Orders.ONHOLD_FLAG);
        }

        int istatus = row.getUserStateFlags();

        Orders.updateStatusIcon(row, row.get(0), istatus);

        if (Utils.isDemo()) {
          updateDemoOrderObject(id, DEMO_ORDER_STATUS_FLAGS_KEY, istatus);
        }

        lastActionTime = System.currentTimeMillis();

        return (status == null)
               ? null
               : w.expandString(status);
      }
      @Override
      public void finish(Object result) {
        w.hideWaitCursor();

        if (result instanceof Throwable) {
          Utils.handleError((Throwable) result);
        } else if (!table.isDisposed()) {
          if (result != null) {
            UINotifier.showMessage((String) result);
          }

          table.rowChanged(index);
        }
      }
    };

    if (Orders.showActionWaitMessage) {
      w.showProgressPopup(w.getString(hold
                                      ? "bv.oe.text.holding_order"
                                      : "bv.oe.text.resuming_order"));
    } else {
      w.showWaitCursor();
    }

    w.spawn(task);
  }

  public static void orderDiscontinue(final TableViewer table, final int index) {
    final JSONObject         info       = getOrdersInfo("discontinueInfo");
    final RenderableDataItem row        = table.get(index);
    boolean                  itemfields = (info == null)
            ? null
            : info.optBoolean("hasItemSpecificFields");

    if ((info == null) || ((info.optJSONArray("fields") == null) &&!itemfields)) {
      orderDiscontinue(table, index, null, null);
    } else if (itemfields) {
      String itemID = (String) Orders.getOrderedItem(row).getLinkedData();

      OrderFields.getItemDiscontinueFields(itemID, new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          } else {
            List<JSONObject> fields = (List<JSONObject>) returnValue;

            if (fields == null) {
              fields = info.optJSONArray("fields").getObjectList();
            }

            orderDiscontinueEx(table, index, row, info);
          }
        }
      });
    } else {
      orderDiscontinueEx(table, index, row, info);
    }
  }

  public static void orderDiscontinue(TableViewer table, final int index, OrderFields fields,
          final Map<String, FieldValue> values) {
    final RenderableDataItem row   = table.get(index);
    final Order              order = Order.createDiscontinuedOrder(row, fields, values);

    addOrderToCart(order);
    row.setFont(table.getFont().deriveItalic().deriveStrikethrough());

    String color = Orders.statusColors.optString("Discontinued", null);

    if (color == null) {
      color = Orders.statusColors.optString("Stopped", null);
    }

    if (color != null) {
      row.setForeground(UIColorHelper.getColor(color));
    } else {
      row.setForeground(null);
    }

    row.setUserStateFlag(Orders.STOPPED_FLAG);
    table.rowChanged(index);
    lastActionTime = System.currentTimeMillis();
  }

  public static void orderNew(String type, RenderableDataItem orderItem) {
    showOrderingScreen(type, orderItem, false);
  }

  public static void orderRenew(String type, RenderableDataItem orderItem) {
    showOrderingScreen(type, orderItem, true);
  }

  public static void orderRenewAndDiscontinue(String type, RenderableDataItem orderItem, String linkedID) {}

  public static void orderSign(final TableViewer table, final int index) {
    JSONObject info = getOrdersInfo("signingInfo");

    if (info.optJSONArray("fields") == null) {
      orderSignEx(table, index, null);
    } else {
      final WindowViewer              w     = Platform.getWindowViewer();
      final RenderableDataItem        row   = table.get(index);
      final ArrayList<WidgetDataLink> links = new ArrayList<OrderManager.WidgetDataLink>(2);
      final iContainer                c     = FormsManager.createFormContainer(w, row, info, null, links);
      iFunctionCallback               cb    = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          try {
            if (!canceled && Boolean.TRUE.equals(returnValue)) {
              FormViewer fv = (FormViewer) c.getWidget("formFields");

              orderSignEx(table, index, fv.getHTTPValuesHash());
            }
          } finally {
            c.dispose();
          }
        }
      };

      if (links.isEmpty()) {
        yesNo(w, info, c, cb);
      } else {
        handleLinks(links, w, info, c, cb);
      }
    }
  }

  public static void orderSignEx(final TableViewer table, final int index, final Map values) {
    final RenderableDataItem row  = table.get(index);
    final WindowViewer       w    = Platform.getWindowViewer();
    aWorkerTask              task = new aWorkerTask() {
      @Override
      public Object compute() {
        String id     = (String) row.get(0).getLinkedData();
        String status = null;

        if (!Utils.isDemo()) {
          try {
            ActionLink l = Utils.createLink(table, "/hub/main/orders/sign/" + id);
            String     s;

            if (values != null) {
              l.setRequestEncoding(RequestEncoding.JSON);
              s = l.sendFormData(table, values);
            } else {
              s = l.getContentAsString();
            }

            JSONObject o = new JSONObject(s);

            if (o.optBoolean("success")) {
              status = o.optString("statusMessage", status);
            } else {
              s = o.getString("errorMessage");
              s = table.expandString(s);

              return new MessageException(s);
            }
          } catch(Exception e) {
            return e;
          }
        }

        row.unsetUserStateFlag(Orders.UNSIGNED_FLAG);

        int istatus = row.getUserStateFlags();

        Orders.updateStatusIcon(row, row.get(0), istatus);

        if (Utils.isDemo()) {
          updateDemoOrderObject(id, DEMO_ORDER_STATUS_FLAGS_KEY, istatus);
        }

        return (status == null)
               ? null
               : w.expandString(status);
      }
      @Override
      public void finish(Object result) {
        w.hideWaitCursor();
        lastActionTime = System.currentTimeMillis();

        if (result instanceof Throwable) {
          Utils.handleError((Throwable) result);
        } else if (!table.isDisposed()) {
          if (result != null) {
            UINotifier.showMessage((String) result);
          }

          table.rowChanged(index);
        }
      }
    };

    if (Orders.showActionWaitMessage) {
      w.showProgressPopup(w.getString("bv.oe.text.signing_order"));
    } else {
      w.showWaitCursor();
    }

    w.spawn(task);
  }

  public static void patientChanged() {
    boolean ok = false;

    do {
      String s = Utils.getPatientID();

      if ((s == null) ||!s.equals(patientForOrders)) {
        break;
      }

      s = Utils.getUserID();

      if ((s == null) ||!s.equals(orderingUser)) {
        break;
      }

      ok = true;
    } while(false);

    if (ok) {
      JSONObject patient = Utils.getPatient();

      patient.put(ORDER_CART_KEY, orderCart);
      patient.put(ORDER_STATUSES_KEY, orderStatuses);
      patient.put(DEMO_PATIENT_ORDER_KEY, demoOrderInfo);
    }

    orderCart        = null;
    orderStatuses    = null;
    demoOrderInfo    = null;
    patientForOrders = null;
    orderingUser     = null;

    IdentityArrayList<Order> list = getOrderCart(false);

    pendingOrderCount = (list == null)
                        ? 0
                        : list.size();
    updateCartIcon();
  }

  public static void pushOrderEntryViewer(iViewer v, Runnable r) {
    Utils.pushViewer(v, Platform.getWindowViewer().getTarget("tabsTarget"), false, r);
  }

  public static int removeOrderFromCart(Order order) {
    List<Order> cart = getOrderCart(true);

    if (removeOrderFromCartEx(cart, order)) {
      updateCartIcon();
    }

    return pendingOrderCount;
  }

  public static void searchCatalog(String text, iFunctionCallback cb) {
    WindowViewer  w    = Platform.getWindowViewer();
    StringBuilder sb   = new StringBuilder("/hub/main/util/ordering/catalog");
    JSONObject    o    = ordersFilter;
    String        type = o.optString("type", "all");

    sb.append("/").append(type);

    if (!Utils.isDemo()) {
      if (Orders.medicationOrderType.equals(type)) {
        sb.append("/").append(o.optString("route", "all"));
      }

      if (o.optBoolean("startsWith")) {
        sb.append("?startsWith=true");
      }
    }

    sb.append("/").append(text);
    w.parseDataLink(Utils.createLink(w, sb.toString(), false), true, cb);
  }

  public static void showCart() {
    final WindowViewer w = Platform.getWindowViewer();

    if (cartConfiguration == null) {
      try {
        w.createConfigurationObject(new ActionLink("/oe/cart.rml"), new iFunctionCallback() {
          @Override
          public void finished(boolean canceled, Object returnValue) {
            cartConfiguration = (Viewer) returnValue;
            showCart();
          }
        });
      } catch(Exception e) {
        Utils.handleError(e);
      }

      return;
    }

    iViewer v = w.createViewer(w, cartConfiguration);

    v.showAsDialog(Collections.singletonMap("title", w.getString("bv.text.order_cart")));
  }

  public static void showFilterDialog(final iWidget filterValueWidget, final boolean always) {
    final WindowViewer w    = Platform.getWindowViewer();
    boolean            show = always || (ordersFilter.optString("type", null) == null);

    if (!show) {
      updateFilterWidget(filterValueWidget);

      return;
    }

    final iContainer     viewer = (iContainer) w.createViewer(w, filterConfiguration);
    final ComboBoxWidget type   = (ComboBoxWidget) viewer.getWidget("type");

    type.setAll(orderTypes);

    int n = type.indexOfLinkedDataEquals(ordersFilter.optString(type.getName(), Orders.defaultOrderType));

    type.setSelectedIndex((n == -1)
                          ? 0
                          : n);

    final CheckBoxWidget startsWith = (CheckBoxWidget) viewer.getWidget("startsWith");

    startsWith.setChecked(ordersFilter.optBoolean(startsWith.getName()));

    final ListBoxViewer route = (ListBoxViewer) viewer.getWidget("route");

    route.setLinkedData(orderRoutes);;

    if (Orders.medicationOrderType.equals(type.getSelectionDataAsString())) {
      route.setAll(orderRoutes);
      n = route.indexOfLinkedDataEquals(ordersFilter.optString(route.getName(), Orders.defaultRoute));
      route.setSelectedIndex((n == -1)
                             ? 0
                             : n);
    }

    iFunctionCallback callback = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        ordersFilter.put("startsWith", startsWith.isChecked());
        ordersFilter.put("type", type.getSelectionDataAsString());
        ordersFilter.put("route", route.getSelectionDataAsString());
        ordersFilter.put("type_string", type.getValueAsString());
        ordersFilter.put("route_string", route.getSelectionAsString());
        viewer.dispose();
        updateFilterWidget(filterValueWidget);
      }
    };
    AlertPanel             p  = AlertPanel.ok(w, viewer.getTitle(), viewer, null, "bv.button.alert");
    final PushButtonWidget ok = p.getYesOrOkButton();

    route.setEventHandler(iConstants.ATTRIBUTE_ON_DOUBLECLICK, new Runnable() {
      @Override
      public void run() {
        ok.click();
      }
    }, true);
    p.showDialog(callback);
  }

  public static void showOrderForm(final Order order) {
    if (order.actionType == ActionType.DISCONTINUED) {
      orderDiscontinueEx(order);

      return;
    }

    editorOrder = order;

    final WindowViewer w = Platform.getWindowViewer();

    try {
      ViewerCreator.createViewer(w, new ActionLink("oe/order_form.rml"), new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          } else {
            List<Order>                 cart   = getOrderCart(false);
            boolean                     update = (cart == null)
                    ? false
                    : cart.indexOf(order) != -1;
            iContainer                  fv     = (iContainer) returnValue;
            final ShowOrderFormCallback cb     = new ShowOrderFormCallback(fv, order, update);
            Runnable                    rno    = new Runnable() {
              @Override
              public void run() {
                cb.finished(false, Boolean.FALSE);;
              }
            };
            Runnable ryes = new Runnable() {
              @Override
              public void run() {
                cb.finished(false, Boolean.TRUE);;
              }
            };
            String           yes = w.getString(update
                                               ? "bv.oe.text.update_order"
                                               : "bv.oe.text.add_to_cart");
            String           no  = w.getString(update
                                               ? "Rare.text.cancel"
                                               : "bv.oe.text.cancel_order");
            iContainer       bp  = (iContainer) fv.getWidget("buttonPanel");
            PushButtonWidget pb  = (PushButtonWidget) bp.getWidget("noButton");

            pb.setText(no);
            pb.setEventHandler(iConstants.ATTRIBUTE_ON_ACTION, rno, false);
            pb = (PushButtonWidget) bp.getWidget("yesButton");
            pb.setText(yes);
            pb.setEventHandler(iConstants.ATTRIBUTE_ON_ACTION, ryes, false);
            Utils.pushViewer(fv, w.getTarget("tabsTarget"), false, new Runnable() {
              @Override
              public void run() {
                cb.finished(true, Boolean.FALSE);
              }
            });
          }
        }
      });
    } catch(MalformedURLException e) {
      Utils.handleError(e);
    }
  }

  public static void showOrderForm(final String type, final String renewID, final RenderableDataItem orderedItem,
                                   final String fieldsID) {
    final Order        order = new Order(type, (renewID != null)
            ? ActionType.RENEWED
            : ActionType.NEW, orderedItem);
    final WindowViewer w     = Platform.getWindowViewer();
    aWorkerTask        task  = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          OrderFields fields = OrderFields.getOrderFields(fieldsID);

          if (renewID != null) {
            order.orderValues = OrderFields.getFieldValues("/hub/main/orders/order_values/" + renewID + ".json");
          }

          return fields;
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        w.hideWaitCursor();

        if (result instanceof Throwable) {
          Utils.handleError((Throwable) result);
        } else {
          order.orderFields = (OrderFields) result;

          if ((renewID == null) && Orders.hasOrderSentenceSupport) {
            w.spawn(new GetOrderSentencesTask(order));
            w.showWaitCursor();
          } else {
            showOrderForm(order);
          }
        }
      }
    };

    w.spawn(task);
    w.showWaitCursor();
  }

  public static void submitOrders(final List orders, final iFunctionCallback notifier) {
    if (!Orders.signatureRequiredForSumbission) {
      submitOrders(null, orders, notifier);
    } else {
      iFunctionCallback cb = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          if (canceled || (returnValue instanceof Throwable)) {
            notifier.finished(canceled, returnValue);
          } else if (!canceled) {
            submitOrders((String) returnValue, orders, notifier);
          }
        }
      };

      getSignature(cb);
    }
  }

  public static void submitOrders(String signatureToken, final List orders, final iFunctionCallback notifier) {
    final WindowViewer w   = Platform.getWindowViewer();
    int                len = orders.size();
    String             s;

    if (len == 1) {
      s = w.getString("bv.oe.text.submitting_order");
    } else {
      s = w.getString("bv.oe.format.submitting_orders", StringCache.valueOf(len));
    }

    w.showProgressPopup(s);
    Platform.invokeLater(new Runnable() {
      @Override
      public void run() {
        removeOrders(orders);
        w.hideProgressPopup();

        if (notifier != null) {
          notifier.finished(false, orders.size());
        }
      }
    }, 2000);
  }

  public static void sumbitOrder(Order order, iFunctionCallback notifier) {
    submitOrders(Arrays.asList(order), notifier);
  }

  public static void sumbitOrderCart(iFunctionCallback notifier) {
    List<Order> cart = getOrderCart(false);

    if ((cart != null) &&!cart.isEmpty()) {
      int              len = cart.size();
      ArrayList<Order> a   = new ArrayList<Order>(len);

      for (int i = 0; i < len; i++) {
        Order o = cart.get(i);

        if (o.isComplete()) {
          a.add(o);
        }
      }

      if (a.isEmpty()) {
        if (notifier != null) {
          notifier.finished(false, a.size());
        }
      } else {
        submitOrders(a, notifier);
      }
    }
  }

  public static void testSignature() {
    getSignature(new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {}
    });
  }

  public static void unlockPatientRecord() {}

  public static void updateCartIcon() {
    updateOrderCartTimestamp();

    UIAction a = Platform.getAppContext().getAction("bv.action.orders_cart");

    if (a == null) {
      return;
    }

    iPlatformIcon ai   = Platform.getResourceAsIcon("bv.icon.cart");
    iPlatformIcon icon = getPendingOrderCountIcon();

    if (icon == null) {
      icon = ai;
    } else {
      //we use 2 icons because the action wont notify listeners
      //if we pass in the same object
      if (compoundIcon1 == null) {
        compoundIcon1 = new BadgeCompoundIcon(ai, icon);
        compoundIcon2 = new BadgeCompoundIcon(ai, icon);
      } else {
        compoundIcon1.setSecondIcon(icon);
        compoundIcon2.setSecondIcon(icon);
      }

      if (a.getIcon() == compoundIcon1) {
        icon = compoundIcon2;
      } else {
        icon = compoundIcon2;
      }
    }

    a.setEnabled(icon != ai);
    a.setIcon(null);    //remove the icon and then set it to force a property change trigger
    a.setIcon(icon);
  }

  public static void updateDemoOrderObject(String id, String key, Object value) {
    if (Utils.isDemo()) {
      JSONObject o  = getDemoOrderObject(true);
      JSONObject oo = o.optJSONObject(id);

      if (oo == null) {
        oo = new JSONObject();
        o.put(id, oo);
      }

      oo.put(key, value);
    }
  }

  public static void updateFilterWidget(iWidget filterValueWidget) {
    WindowViewer w    = Platform.getWindowViewer();
    String       type = ordersFilter.optString("type");
    String       s    = w.getString(ordersFilter.optBoolean("startsWith")
                                    ? "bv.oe.text.that_starts_with"
                                    : "bv.oe.text.that_contains");

    if (Orders.medicationOrderType.equals(type)) {
      filterValueWidget.setValue(w.getString("bv.oe.format.search_meds_description",
              ordersFilter.optString("route_string"), s));
    } else {
      filterValueWidget.setValue(w.getString("bv.oe.format.search_filter_description",
              ordersFilter.optString("type_string"), s));
    }

    if (Platform.hasPhysicalKeyboard()) {
      filterValueWidget.getFormViewer().getWidget("searchField").requestFocus();
    }
  }

  public static void updateOrderCartTimestamp() {
    JSONObject   o  = Utils.getPatient();
    ObjectHolder oh = (ObjectHolder) o.opt(ORDER_CART_KEY);

    if (oh != null) {
      oh.source = System.currentTimeMillis();
    }
  }

  public static void yesNo(final WindowViewer w, JSONObject info, final iContainer content,
                           final iFunctionCallback cb) {
    String ok = info.optString("okText", null);

    if (ok != null) {
      ok = w.expandString(ok);
    } else {
      ok = w.getString("Rare.text.ok");
    }

    String cancel = info.optString("cancelText", null);

    if (cancel != null) {
      cancel = w.expandString(cancel);
    } else {
      cancel = w.getString("Rare.text.cancel");
    }

    String title = info.optString("titleText", null);

    if (title != null) {
      title = w.expandString(title);
    } else {
      title = w.getTitle();
    }

    content.setEventHandler(iConstants.ATTRIBUTE_ON_SHOWN, "class:MainEventHandler#requestFocusIfKeyboardPresent",
                            true);

    final AlertPanel d = AlertPanel.yesNo(w, title, content, null, ok, cancel, false, "bv.button.alert");

    d.getYesOrOkButton().removeActionListener(d);
    d.getYesOrOkButton().addActionListener(new iActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        FormViewer    fv     = (FormViewer) content.getWidget("formFields");
        final iWidget widget = (fv == null)
                               ? null
                               : FormsManager.getInvalidWidget(fv);

        if (widget == null) {
          d.actionPerformed(e);
        } else {
          widget.requestFocus();

          JSONObject field = ((FieldValue) widget.getLinkedData()).field;

          w.buzz();

          final iWidget mb = content.getWidget("messageBox");

          if (mb != null) {
            mb.setValue(w.getString("bv.oe.format.required", w.expandString(field.getString(OrderFields.PROMPT))));
            mb.setForeground(UIColorHelper.getColor("error"));
            mb.setLinkedData(widget);    //request focus on a widget clears the message box unless this is set, see FormsManager#onFocusGained
            Platform.invokeLater(new Runnable() {
              @Override
              public void run() {
                if (!mb.isDisposed()) {
                  mb.setForeground(UIColorHelper.getForeground().getDisabledColor());

                  FieldValue fv    = (FieldValue) widget.getLinkedData();
                  JSONObject field = (fv == null)
                                     ? null
                                     : fv.field;

                  if (field != null) {
                    mb.setValue(field.optString(OrderFields.DESCRIPTION));
                  }
                }
              }
            }, Platform.getUIDefaults().getInt("bv.message_delay", 5000));
          }
        }
      }
    });
    d.showDialog(cb);
  }

  protected static void orderDiscontinueEx(final Order order) {
    final WindowViewer              w      = Platform.getWindowViewer();
    final ArrayList<WidgetDataLink> links  = new ArrayList<OrderManager.WidgetDataLink>(2);
    final iContainer                c      = FormsManager.createFormContainer(w, order, links);
    final Map<String, FieldValue>   values = order.copyValues();
    iFunctionCallback               cb     = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (canceled || Boolean.FALSE.equals(returnValue)) {
          order.orderValues = values;
        } else {
          FormViewer fv = (FormViewer) c.getWidget("formFields");

          FormsManager.updateValueFromWidget(fv);
        }

        c.dispose();
      }
    };

    if (links.isEmpty()) {
      yesNo(w, getOrdersInfo("discontinueInfo"), c, cb);
    } else {
      handleLinks(links, w, getOrdersInfo("discontinueInfo"), c, cb);
    }
  }

  protected static void orderDiscontinueEx(final TableViewer table, final int index, final RenderableDataItem row,
          final JSONObject info) {
    final WindowViewer              w     = Platform.getWindowViewer();
    final ArrayList<WidgetDataLink> links = new ArrayList<OrderManager.WidgetDataLink>(2);
    final iContainer                c     = FormsManager.createFormContainer(w, row, info, null, links);
    iFunctionCallback               cb    = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        try {
          if (!canceled && Boolean.TRUE.equals(returnValue)) {
            FormViewer fv = (FormViewer) c.getWidget("formFields");

            FormsManager.updateValueFromWidget(fv);

            Map values = FormsManager.getValuesMap(fv);

            orderDiscontinue(table, index, getOrderFields("dc", info), values);
          }
        } finally {
          c.dispose();
        }
      }
    };

    if (links.isEmpty()) {
      yesNo(w, info, c, cb);
    } else {
      handleLinks(links, w, info, c, cb);
    }
  }

  private static OrderFields getOrderFields(String id, JSONObject info) {
    OrderFields fields = (OrderFields) info.opt("_ORDER_FIELDS");

    if (fields == null) {
      fields = new OrderFields(id, info.getJSONArray("fields").getObjectList());
    }

    return fields;
  }

  private static void handleLinks(final ArrayList<WidgetDataLink> links, final WindowViewer w, final JSONObject info,
                                  final iContainer content, final iFunctionCallback cb) {
    Runnable r = new Runnable() {
      @Override
      public void run() {
        for (WidgetDataLink link : links) {
          link.handle();
        }

        Platform.invokeLater(new Runnable() {
          @Override
          public void run() {
            w.hideWaitCursor();
            yesNo(w, info, content, cb);
          }
        });
      }
    };

    w.hideWaitCursor();
    w.spawn(r);
  }

  private static void removeOrders(List orders) {
    List cart = getOrderCart(false);

    for (Object o : orders) {
      removeOrderFromCartEx(cart, (Order) o);
    }

    updateCartIcon();
  }

  private static void showOrderingScreen(final String type, final RenderableDataItem orderItem, boolean renew) {
    final WindowViewer w    = Platform.getWindowViewer();
    aWorkerTask        task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          if (filterConfiguration == null) {
            filterConfiguration = (Viewer) w.createConfigurationObject(new ActionLink("/oe/new_order_filter.rml"));
          }

          if (orderRoutes == null) {
            orderRoutes = w.parseDataLink(Utils.createLink(w, "/hub/main/util/ordering/order_routes", false), false);;
          }

          if (orderTypes == null) {
            orderTypes = w.parseDataLink(Utils.createLink(w, "/hub/main/util/ordering/order_types", false), false);
          }

          if (ordersFilter == null) {
            ordersFilter = new JSONObject();
          }

          return w.createConfigurationObject(new ActionLink("oe/ordering.rml"));
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        w.hideWaitCursor();

        if (result instanceof Throwable) {
          Utils.handleError((Throwable) result);
        } else {
          iViewer v = (iViewer) w.createWidget((Widget) result);

          v.setAutoDispose(true);
          Utils.pushViewer(v, w.getTarget("tabsTarget"), false, null);
          w.getAction("bv.action.new_orders").setEnabled(false);

          if (orderItem != null) {
            Platform.invokeLater(new Runnable() {
              @Override
              public void run() {}
            });
          }
        }
      }
    };

    w.spawn(task);
    w.showWaitCursor();
  }

  static void emptyOrderCartEx() {
    List<Order> cart = getOrderCart(false);

    if (cart != null) {
      cart.clear();
    }

    Map map = getOrderStatuses(false);

    if (map != null) {
      map.clear();
    }

    pendingOrderCount = 0;
    lastActionTime    = System.currentTimeMillis();
  }

  static boolean removeOrderFromCartEx(List<Order> cart, Order order) {
    if (cart.remove(order)) {
      String id = order.linkedOrderID;

      if (id != null) {
        Map map = getOrderStatuses(false);

        if (map != null) {
          map.remove(id);
        }

        TableViewer table = (TableViewer) Platform.getWindowViewer().getViewer("ordersTable");

        if (table != null) {
          int len = table.getRowCount();

          for (int i = 0; i < len; i++) {
            RenderableDataItem row = table.get(i);

            if (id.equals(row.get(0).getLinkedData())) {
              row.unsetUserStateFlag(Orders.STOPPED_FLAG);
              row.setFont(null);
              row.setForeground(null);
              table.rowChanged(i);

              break;
            }
          }

          table.refreshItems();
        }
      }

      pendingOrderCount--;

      if (pendingOrderCount < 0) {
        pendingOrderCount = 0;
      }

      return true;
    }

    return false;
  }

  public static class WidgetDataLink {
    iWidget    list;
    ActionLink link;

    public WidgetDataLink(iWidget list, ActionLink link) {
      super();
      this.list = list;
      this.link = link;
    }

    public void handle() {
      list.setDataLink(link, false);
    }
  }


  static class GetOrderSentencesTask extends aWorkerTask implements iFunctionCallback {
    Order                    order;
    List<RenderableDataItem> sentences;

    public GetOrderSentencesTask(Order order) {
      this.order = order;
    }

    @Override
    public Object compute() {
      try {
        WindowViewer w = Platform.getWindowViewer();

        sentences = w.parseDataLink(Utils.createLink(w,
                "/hub/main/util/ordering/sentences/" + order.getOrderedItemID(), false), false);

        if ((sentences != null) && sentences.isEmpty()) {
          sentences = null;
        }

        return null;
      } catch(FileNotFoundException e) {
        return null;
      } catch(Exception e) {
        return e;
      }
    }

    @Override
    public void finish(Object result) {
      WindowViewer w = Platform.getWindowViewer();

      w.hideWaitCursor();

      if (result instanceof Throwable) {
        Utils.handleError((Throwable) result);
      } else if (sentences != null) {    // has sentences so let user pick one
        PickList pl = new PickList(Platform.getResourceAsString("bv.oe.text.select_order_sentence"), sentences);

        pl.setShowNoneButton(true, Platform.getResourceAsString("bv.oe.text.no_sentence"));
        pl.setOkButtonText(w.getString("Rare.text.ok"));
        pl.setSupportListDblClick(true);
        pl.setRightAlignButtons(false);
        pl.show(this);
      } else {                           //no sentences so just show the form
        showOrderForm(order);
      }
    }

    /**
     * Callback for the pick list
     * @param canceled true if the pick list dialog was canceled; false otherwise
     * @param returnValue the selected item
     */
    @Override
    public void finished(boolean canceled, final Object returnValue) {
      if (!canceled) {
        if (returnValue == null) {    //user was to create a custom order
          showOrderForm(order);
        } else {                      //user chose a sentence
          final WindowViewer w    = Platform.getWindowViewer();
          aWorkerTask        task = new aWorkerTask() {
            @Override
            public Object compute() {
              try {
                String id = (String) ((RenderableDataItem) returnValue).getLinkedData();

                return OrderFields.getFieldValues("/hub/main/util/ordering/sentence/" + id + ".json");
              } catch(Exception e) {
                return e;
              }
            }
            @Override
            public void finish(Object result) {
              w.hideWaitCursor();

              if (result instanceof Throwable) {
                Utils.handleError((Throwable) result);
              } else {
                order.orderValues = (Map<String, FieldValue>) result;
                showOrderForm(order);
              }
            }
          };

          w.spawn(task);
          w.showWaitCursor();
        }
      }
    }
  }


  static class ShowOrderFormCallback implements iFunctionCallback {
    iContainer              viewer;
    Order                   order;
    boolean                 update;
    Map<String, FieldValue> values;

    public ShowOrderFormCallback(iContainer viewer, Order order, boolean update) {
      this.viewer = viewer;
      this.order  = order;
      this.update = update;

      if (update) {
        values = order.copyValues();
      }

      order.setDirty(false);
      showOrderFormCallback = this;
    }

    @Override
    public void finished(boolean canceled, Object returnValue) {
      showOrderFormCallback = null;

      final WindowViewer w     = Platform.getWindowViewer().getTop();
      boolean            check = !update || order.isDirty();

      if (check && (canceled || Boolean.FALSE.equals(returnValue))) {
        if (Orders.verifyOrderEntryCancel || canceled) {
          iFunctionCallback cb = new iFunctionCallback() {
            @Override
            public void finished(boolean canceled, Object returnValue) {
              if (Boolean.TRUE.equals(returnValue)) {
                if (update) {
                  order.orderValues = values;
                }

                order.setDirty(false);
                Utils.popViewerStack(true);
                viewer.dispose();
                viewer      = null;
                editorOrder = null;
              }
            }
          };

          w.yesNo(w.getString(update
                              ? "bv.oe.text.verify_cancel_edits"
                              : "bv.oe.text.verify_cancel_order"), cb);
        } else {
          order.setDirty(false);
          Utils.popViewerStack(true);
          viewer.dispose();
          viewer      = null;
          editorOrder = null;

          if (update) {
            order.orderValues = values;
          }
        }
      } else {
        order.setDirty(false);
        Utils.popViewerStack(true);
        viewer.dispose();
        viewer      = null;
        editorOrder = null;
        order.updateDirectionsItem();

        if (!update) {
          addOrderToCart(order);
        }
      }
    }
  }


  static class SignatureHandler extends aWorkerTask implements iActionListener, iFunctionCallback {
    TextFieldWidget   field;
    LabelWidget       statusLabel;
    AlertPanel        panel;
    iFunctionCallback callback;
    String            signature;

    public SignatureHandler(iFunctionCallback cb) {
      super();
      this.callback = cb;
    }

    /**
     * Called but the alert panels okButton
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      signature = field.getValueAsString();

      WindowViewer w = Platform.getWindowViewer();

      if (signature.length() == 0) {
        w.buzz();

        return;
      }

      w.showProgressPopup(w.getString("bv.text.verifying_action"));
      w.spawn(this);
    }

    @Override
    public Object compute() {
      try {
        Map<String, String> data = Collections.singletonMap("signature", signature);

        if (Utils.isDemo()) {
          data = null;
        }

        return Utils.getContentAsJSON("/hub/main/account/validate_signature", data, false);
      } catch(Exception e) {
        return e;
      }
    }

    @Override
    public void finish(Object result) {
      WindowViewer w = Platform.getWindowViewer();

      w.hideProgressPopup();

      if (!(result instanceof Throwable)) {
        JSONObject o = (JSONObject) result;

        if (!o.getBoolean("success")) {
          String s = o.optString("errorMessage", w.getString("bv.oe.text.signature_code"));

          statusLabel.setText(s);
          w.buzz();

          return;
        }

        result = o.optString("token");
      }

      iFunctionCallback cb = callback;

      panel.cancel();
      panel       = null;
      field       = null;
      statusLabel = null;
      signature   = null;
      callback    = null;
      cb.finished(false, result);
    }

    /**
     * Called when the alert panel goes away
     */
    @Override
    public void finished(boolean canceled, Object returnValue) {
      if (callback != null) {    //user manually canceled
        iFunctionCallback cb = callback;

        field       = null;
        statusLabel = null;
        signature   = null;
        callback    = null;
        cb.finished(true, null);
      }
    }

    public void handle() {
      final WindowViewer w   = Platform.getWindowViewer();
      PasswordField      cfg = (PasswordField) w.createConfigurationObject("PasswordField",
                                 "bv.passwordfield.signature");

      field = (TextFieldWidget) w.createWidget(cfg);

      String title  = w.getString("bv.oe.text.signature_title");
      String prompt = w.getString("bv.oe.text.signature_prompt");

      panel       = AlertPanel.prompt(w, title, prompt, field, null, "bv.button.alert");
      statusLabel = panel.addStatusLabel();

      final PushButtonWidget ok = panel.getYesOrOkButton();

      ok.removeActionListener(panel);
      ok.addActionListener(this);
      field.addActionListener(new iActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ok.click();
        }
      });
      panel.showDialog(this);
    }
  }
}
