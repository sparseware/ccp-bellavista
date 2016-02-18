package com.sparseware.bellavista;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.ActionLink.RequestMethod;
import com.appnativa.rare.spot.ListBox;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UISoundHelper;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.viewer.ListBoxViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.aGroupableButton;
import com.appnativa.rare.widget.iWidget;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

/**
 * A class that handle a list box/search field combination.
 * The name of the text field is used as the name of the query
 * parameter that is added to the search url.
 *
 * <p>
 * To control the number of items in a page, you simple set a 'pageSize'
 * attribute on the list box via the 'customProperties' attribute of
 * the list box configuration.
 * </p>
 *
 * @author Don DeCoteau
 *
 */
public class ListSearcher implements iEventHandler {
  public ListSearcher() {}

  /**
   * Changes the page that is being displayed in the list box
   *
   * @param next true to go to the next page; false to go to the previous page
   * @param nextPageWidget the next page widget
   * @param previousPageWidget the previous page widget
   */
  public void changePage(boolean next, iWidget nextPageWidget, iWidget previousPageWidget) {
    ListBoxViewer lb    = getListBox(nextPageWidget);
    ListPager     pager = (ListPager) lb.getAttribute("_pager");
    ActionLink    link  = null;

    if (next) {
      link = pager.next();
    } else {
      link = pager.previous();
    }

    if (link == null) {    //should not happen
      if (next) {
        nextPageWidget.setEnabled(false);
      } else {
        previousPageWidget.setEnabled(false);
      }

      UISoundHelper.beep();
    } else {
      load(lb, link);
    }
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {}

  /**
   * Called when the list is created.
   *  It sets the list pager and save the data url for the list
   *  for later use.
   *
   */
  public void onListCreated(String eventName, iWidget widget, EventObject event) {
    ListBox cfg = (ListBox) ((DataEvent) event).getData();

    widget.setAttribute("_pager", new ListPager());
    widget.setAttribute("_url", cfg.dataURL.getValue());
    cfg.dataURL.spot_clear();
  }

  /**
   * Called when the next or previous page button is pressed
   */
  public void onNextOrPreviousPage(String eventName, iWidget widget, EventObject event) {
    changePage(widget.getName().equals("_nextPage"), widget, widget.getParent().getWidget("_previousPage"));
  }

  /**
   * Called when enter is pressed in the search field.
   *
   */
  public void onSearchFieldAction(String eventName, iWidget widget, EventObject event) {
    ListBoxViewer lb       = getListBox(widget);
    String        value    = widget.getValueAsString().trim();
    ListPager     pager    = (ListPager) lb.getAttribute("_pager");
    String        url      = (String) lb.getAttribute("_url");
    Object        o        = lb.getAttribute("pageSize");
    Integer       pageSize = null;

    if (o instanceof String) {
      pageSize = Integer.valueOf((String) o);
      lb.setAttribute("pageSize", pageSize);
    } else if (o instanceof Integer) {
      pageSize = (Integer) o;
    }

    pager.clear();

    if (value.length() == 0) {
      lb.clear();
      updateButtons(lb, pager);

      return;
    }

    ActionLink              link       = Utils.createLink(lb, url, true);
    HashMap<String, Object> attributes = new HashMap<String, Object>(2);

    attributes.put(widget.getName(), value);

    if ((pageSize != null) && (pageSize > 0)) {
      attributes.put("max", pageSize.toString());
    }

    link.setAttributes(attributes);
    link.setRequestMethod(RequestMethod.POST);
    load(lb, link);
  }

  protected ListBoxViewer getListBox(iWidget widget) {
    return (ListBoxViewer) widget.getFormViewer().getWidget("list");
  }

  /**
   * Called to load new data in to the list box.
   *
   * @param lb the list box
   * @param link the link that will load the data
   */
  protected void load(final ListBoxViewer lb, final ActionLink link) {
    final WindowViewer w = Platform.getWindowViewer();

    lb.parseDataLink(link, false, new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        w.hideWaitCursor();

        if (returnValue instanceof Throwable) {
          Utils.handleError((Throwable) returnValue);
        }

        lb.setAll((List<RenderableDataItem>) returnValue);

        ListPager pager = (ListPager) lb.getAttribute("_pager");
        String    pn    = null;

        if (link.getAttributes() != null) {    //we searched/or set a max number
          pager.add(link);
          pn = link.getPagingNext();

          if (pn != null) {
            pager.setNext(ListPager.createPagingLink(link, pn));
          }
        } else {
          pager.clear();
        }

        updateButtons(lb, pager);
      }
    });
    w.showWaitCursor();
  }

  /**
   * Called to update the next and previous buttons.
   *
   * @param lb the list box
   * @param paget the list pager
   */
  protected void updateButtons(ListBoxViewer lb, ListPager pager) {
    iFormViewer      fv = lb.getFormViewer();
    aGroupableButton pb = (aGroupableButton) fv.getWidget("_nextPage");

    if (pb != null) {
      pb.setEnabled(pager.hasNext());
    }

    pb = (aGroupableButton) fv.getWidget("_previousPage");

    if (pb != null) {
      pb.setEnabled(pager.hasPrevious());
    }
  }
}
