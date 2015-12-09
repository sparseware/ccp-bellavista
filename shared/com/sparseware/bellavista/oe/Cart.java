package com.sparseware.bellavista.oe;

import com.appnativa.rare.Platform;
import com.appnativa.rare.exception.ExpandVetoException;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.ExpansionEvent;
import com.appnativa.rare.ui.event.iExpansionListener;
import com.appnativa.rare.viewer.ListBoxViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.IdentityArrayList;

import com.sparseware.bellavista.OrderManager;
import com.sparseware.bellavista.Orders;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.aEventHandler;
import com.sparseware.bellavista.oe.Order.ActionType;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class Cart extends aEventHandler {
  iContainer actionbar;
  @Override
  protected void dataParsed(iWidget widget, List<RenderableDataItem> rows, ActionLink link) {}

  public void onDispose(String eventName, iWidget widget, EventObject event) {
    if(actionbar!=null) {
      actionbar.dispose();
      actionbar=null;
    }
  }
  
  public void onConfigure(String eventName, iWidget widget, EventObject event) {
    iFormViewer      fv        = widget.getFormViewer();
    actionbar = (iContainer) fv.getWidget("rowActionBar");

    fv.removeWidget(actionbar);

    final ListBoxViewer lb = (ListBoxViewer) fv.getWidget("cart");

    lb.setRowEditingWidget(actionbar, true);
    lb.setLinkedData(OrderManager.getOrderCartLastModifiedTime());
    lb.setRowEditModeListener(new iExpansionListener() {
      @Override
      public void itemWillExpand(ExpansionEvent event) throws ExpandVetoException {
        int   row   = lb.getEditingRow();
        Order order = (Order) lb.get(row).getLinkedData();

        actionbar.getWidget("signOrder").setEnabled(order.isComplete());
      }
      @Override
      public void itemWillCollapse(ExpansionEvent event) throws ExpandVetoException {}
    });
    List<RenderableDataItem> list = createCartList();

    lb.setAll(list);
    lb.setEnabled(OrderManager.getOrderBeingEdited() == null);

    if (lb.isEnabled()) {
      fv.getWidget("emptyButton").setEnabled(true);
      checkForIncompleteOrders(lb);
    } else {
      fv.getWidget("signButton").setEnabled(false);
      fv.getWidget("emptyButton").setEnabled(false);
    }
  }

  public void onCartWillExpand(String eventName, iWidget widget, EventObject event) {
    iContainer    c  = (iContainer) ((PushButtonWidget) widget).getPopupWidget();
    ListBoxViewer lb = (ListBoxViewer) c.getWidget("cart");

    lb.clearSelection();

    List<RenderableDataItem> list = createCartList();

    lb.setAll(list);
    lb.setEnabled(OrderManager.getOrderBeingEdited() == null);

    if (lb.isEnabled()) {
      c.getWidget("emptyButton").setEnabled(true);
      checkForIncompleteOrders(lb);
    } else {
      c.getWidget("signButton").setEnabled(false);
      c.getWidget("emptyButton").setEnabled(false);
    }
  }

  public static List<RenderableDataItem> createCartList() {
    IdentityArrayList<Order>      cart = OrderManager.getOrderCart(false);
    int                           len  = (cart == null)
            ? 0
            : cart.size();
    ArrayList<RenderableDataItem> rows = new ArrayList<RenderableDataItem>(len);
    StringBuilder                 sb   = new StringBuilder();

    for (int i = 0; i < len; i++) {
      RenderableDataItem row = new RenderableDataItem();
      Order              o   = cart.get(i);

      sb.setLength(0);
      sb.append("<html>");

      if (o.actionType == ActionType.DISCONTINUED) {
        sb.append("<s>");
      }

      sb.append("<b>").append(o.orderedItem.getValue()).append("</b><br/><i>");
      sb.append(o.directionsItem.getValue()).append("</i>");

      if (o.actionType == ActionType.DISCONTINUED) {
        sb.append("</s>");
      }

      sb.append("</html>");
      row.setValue(sb.toString());
      row.setIcon(o.getActionTypeIcon());
      row.setLinkedData(o);
      rows.add(row);
    }

    return rows;
  }

  public void onEmptyCart(String eventName, final iWidget widget, EventObject event) {
    final WindowViewer w  = Platform.getWindowViewer();
    iFunctionCallback  cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (!canceled && Boolean.TRUE.equals(returnValue)) {
          OrderManager.emptyOrderCart();

          ListBoxViewer lb = (ListBoxViewer) widget.getFormViewer().getWidget("cart");

          if (!lb.isDisposed()) {
            lb.clear();
            closePopupOrWindow(lb);
          }
        }
      }
    };
    String yes = w.getString("bv.oe.text.empty_cart");

    w.yesNo(null, w.getString("bv.oe.text.empty_cart_message"), yes, null, cb);
  }

  public void onDeleteOrder(String eventName, final iWidget widget, EventObject event) {
    final WindowViewer  w     = Platform.getWindowViewer();
    final ListBoxViewer lb    = (ListBoxViewer) widget.getFormViewer().getWidget("cart");
    final int           row   = lb.getEditingRow();
    final Order         order = (Order) lb.get(row).getLinkedData();

    if (Orders.verifyOrderEntryDelete) {
      iFunctionCallback cb = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          if (!canceled && Boolean.TRUE.equals(returnValue)) {
            OrderManager.removeOrderFromCart(order);
            lb.remove(row);

            if (lb.isEmpty()) {
              closePopupOrWindow(lb);
            }
          }
        }
      };
      String yes = w.getString("bv.action.delete");

      w.yesNo(null, w.getString("bv.oe.text.delete_order_message"), yes, null, cb);
    } else {
      OrderManager.removeOrderFromCart(order);
      lb.remove(row);

      if (lb.isEmpty()) {
        closePopupOrWindow(lb);
      } else {
        checkForIncompleteOrders(lb);
      }
    }
  }

  private void checkForIncompleteOrders(ListBoxViewer lb) {
    boolean hasComplete = false;
    int     len         = lb.size();

    for (int i = 0; i < len; i++) {
      if (((Order) lb.get(i).getLinkedData()).isComplete()) {
        hasComplete = true;

        break;
      }
    }

    lb.getFormViewer().getWidget("signButton").setEnabled(hasComplete);
  }

  public void onCartAction(String eventName, iWidget widget, EventObject event) {
    final Order o = (Order) widget.getSelectionData();

    if (o.orderFields == null) {
      ((ListBoxViewer) widget).flashSelectedRow();

      return;
    }

    Platform.invokeLater(new Runnable() {
      @Override
      public void run() {
        OrderManager.showOrderForm(o);
      }
    });
    closePopupOrWindow(widget);
  }

  public void onSubmit(String eventName, iWidget widget, EventObject event) {
    final ListBoxViewer lb = (ListBoxViewer) widget.getFormViewer().getWidget("cart");
    iFunctionCallback   cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (canceled) {
          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          }
        } else {
          lb.clear();
          closePopupOrWindow(lb);
        }
      }
    };

    OrderManager.sumbitOrderCart(cb);
  }

  public void onSubmitOrder(String eventName, iWidget widget, EventObject event) {
    final ListBoxViewer lb    = (ListBoxViewer) widget.getFormViewer().getWidget("cart");
    final int           row   = lb.getEditingRow();
    final Order         order = (Order) lb.get(row).getLinkedData();
    iFunctionCallback   cb    = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (canceled) {
          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          }
        } else {
          lb.remove(row);

          if (lb.isEmpty()) {
            closePopupOrWindow(lb);
          } else {
            checkForIncompleteOrders(lb);
          }
        }
      }
    };

    OrderManager.sumbitOrder(order, cb);
  }

  public void onClose(String eventName, iWidget widget, EventObject event) {
    closePopupOrWindow(widget);
  }

  void closePopupOrWindow(iWidget widget) {
    if (widget.isInPopup()) {
      widget.hidePopupContainer();
    } else if (widget.getWindow() != Platform.getWindowViewer()) {
      widget.getWindow().close();
    }
  }
}
