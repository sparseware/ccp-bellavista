package com.sparseware.bellavista.oe;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIColorHelper;
import com.appnativa.rare.ui.UIImageIcon;
import com.appnativa.rare.ui.ViewerCreator;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.painter.iBackgroundPainter;
import com.appnativa.rare.ui.painter.iComponentPainter;
import com.appnativa.rare.viewer.ListBoxViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.ObjectHolder;

import com.sparseware.bellavista.OrderManager;
import com.sparseware.bellavista.Orders;
import com.sparseware.bellavista.Utils;

import java.io.FileNotFoundException;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Ordering extends Orders {
  RenderableDataItem noOrdersFound;
  iBackgroundPainter actionbarPainter;
  iBackgroundPainter orderingActionbarPainter;
  long lastShowOrderFormTime;
  public Ordering() {}

  public void onItemsListAction(final String eventName, final iWidget widget, final EventObject event) {
    long time=System.currentTimeMillis();
    if(lastShowOrderFormTime+500<time) { //prevent double tap from showing the order form twice on touch devices
      lastShowOrderFormTime=time;
      RenderableDataItem row  = (RenderableDataItem) widget.getSelectionData();
      String             type = row.get(1).toString();
  
      OrderManager.showOrderForm(type, null, row.get(0), row.get(2).toString());
    }
    
  }

  @Override
  public void onTableAction(final String eventName, final iWidget widget, final EventObject event) {
    final WindowViewer w      = widget.getWindow();
    final iTarget      target = w.getTarget("orderingContentViewer");

    if (target == null) {
      super.onTableAction(eventName, widget, event);

      return;
    }

    final iContainer fv = widget.getFormViewer();
    iViewer          v  = target.getViewer();

    if ((v == null) ||!v.getName().equals("documentViewer")) {
      iFunctionCallback cb = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          w.hideWaitCursor();

          if (!widget.isDisposed()) {
            if (returnValue instanceof Throwable) {
              Utils.handleError((Throwable) returnValue);
            } else {
              Utils.pushViewer((iViewer) returnValue, target, false, new Runnable() {
                @Override
                public void run() {
                  if (!widget.isDisposed()) {
                    ((TableViewer) widget).clearSelection();
                  }

                  Utils.popViewerStack(true);
                }
              });
              onTableActionEx(eventName, widget, event);
            }
          }
        }
      };

      try {
        ViewerCreator.createViewer(fv, new ActionLink("/document_viewer.rml"), cb);
        w.showWaitCursor();
      } catch(MalformedURLException e) {
        Utils.handleError(e);
      }
    } else {
      super.onTableAction(eventName, widget, event);
    }
  }

  public void onFilterAction(String eventName, iWidget widget, EventObject event) {
    OrderManager.showFilterDialog(widget, true);
  }

  public void onFilterTypeAction(String eventName, iWidget widget, EventObject event) {
    boolean       ok    = Orders.medicationOrderType.equals(widget.getSelectionDataAsString());
    ListBoxViewer route = (ListBoxViewer) widget.getFormViewer().getWidget("route");

    if (ok) {
      route.setAll((Collection<? extends RenderableDataItem>) route.getLinkedData());
      route.setSelectedIndex(route.indexOfLinkedDataEquals(Orders.defaultRoute));
    } else {
      route.clear();
    }
  }

  public void onShown(String eventName, iWidget widget, EventObject event) {
    OrderManager.showFilterDialog(widget.getFormViewer().getWidget("filter"), false);
  }

  public void onSearchFieldAction(String eventName, iWidget widget, EventObject event) {
    String              text = widget.getValueAsString().toLowerCase(Locale.getDefault());
    final ListBoxViewer lb   = (ListBoxViewer) widget.getFormViewer().getWidget("items");

    if (noOrdersFound == null) {
      noOrdersFound = new RenderableDataItem(Platform.getResourceAsString("bv.oe.text.no_orders_found"));
      noOrdersFound.setSelectable(false);
      noOrdersFound.setForeground(UIColorHelper.getForeground().getDisabledColor());
      noOrdersFound.setIcon(Platform.getResourceAsIcon("Rare.icon.empty"));
    }

    int len = text.length();

    if (len == 0) {
      return;
    }

    if (Utils.isDemo()) {
      String penicillin = "penicillin";
      int    i          = 0;

      if (len < penicillin.length()) {
        for (i = 0; i < len; i++) {
          if (text.charAt(i) != penicillin.charAt(i)) {
            break;
          }
        }
      }

      if (i != len) {
        lb.setAll(Arrays.asList(noOrdersFound));

        return;
      }

      text = penicillin;
    }

    OrderManager.searchCatalog(text, new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        WindowViewer w = Platform.getWindowViewer();

        w.hideWaitCursor();

        if (returnValue instanceof Throwable) {
          if (returnValue instanceof FileNotFoundException) {
            lb.setAll(Arrays.asList(noOrdersFound));
            lb.setLinkedData(null);
          } else {
            Utils.handleError((Throwable) returnValue);
          }
        } else {
          ObjectHolder                  oh    = (ObjectHolder) returnValue;
          List<RenderableDataItem>      list  = (oh == null)
                  ? null
                  : (List<RenderableDataItem>) oh.value;
          int                           len   = (list == null)
                  ? 0
                  : list.size();
          Map<String, UIImageIcon>      icons = w.getIcons();
          StringBuilder                 sb    = new StringBuilder("bv.icon.order_type_");
          int                           sblen = sb.length();
          ArrayList<RenderableDataItem> rows  = new ArrayList<RenderableDataItem>((len == 0)
                  ? 1
                  : len);
          boolean                       demo  = Utils.isDemo();
          String                        route = demo
                  ? OrderManager.getOrdersFilter().optString("route")
                  : null;

          for (int i = 0; i < len; i++) {
            RenderableDataItem row  = list.get(i);
            RenderableDataItem item = row.get(0);

            if (demo && (route.length() > 0)) {
              RenderableDataItem di = row.getItemEx(3);

              if ((di != null) &&!di.valueEquals(route)) {
                continue;
              }
            }

            item = item.copy();
            item.setLinkedData(row);
            sb.setLength(sblen);
            sb.append((String) row.get(1).getValue());

            iPlatformIcon icon = icons.get(sb.toString());

            if (icon != null) {
              item.setIcon(icon);
            }

            rows.add(item);
          }

          if (rows.isEmpty()) {
            rows.add(noOrdersFound);
          }

          lb.setAll(rows);
        }
      }
    });
    Platform.getWindowViewer().showWaitCursor();
  }

  @Override
  public void onConfigure(String eventName, iWidget widget, EventObject event) {
    super.onConfigure(eventName, widget, event);

    WindowViewer w = Platform.getWindowViewer();

    w.getAction("bv.action.new_orders").setEnabled(false);
    w.getAction("bv.action.change_patient").setEnabled(false);

    iComponentPainter cp = Platform.getWindowViewer().getActionBar().getComponentPainter();

    actionbarPainter = cp.getBackgroundPainter();
    cp.setBackgroundPainter(Platform.getUIDefaults().getBackgroundPainter("orderEntryColor"), false);
  }

  @Override
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    super.onDispose(eventName, widget, event);

    WindowViewer w = Platform.getWindowViewer();

    w.getAction("bv.action.new_orders").setEnabled(true);
    w.getAction("bv.action.change_patient").setEnabled(true);
    w.getActionBar().getComponentPainter().setBackgroundPainter(actionbarPainter, false);

    final iContainer fv = widget.getFormViewer();
    iViewer          v  = (iViewer) fv.getAttribute("orderEditor");

    if ((v != null) && (v.getParent() == null)) {
      v.dispose();
    }
  }

  private void onTableActionEx(String eventName, iWidget widget, EventObject event) {
    super.onTableAction(eventName, widget, event);
  }
}
