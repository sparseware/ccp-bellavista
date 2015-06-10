package com.sparseware.bellavista;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.Form;
import com.appnativa.rare.spot.GridPane;
import com.appnativa.rare.spot.Label;
import com.appnativa.rare.spot.StackPane;
import com.appnativa.rare.spot.WidgetPane;
import com.appnativa.rare.ui.RenderType;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.RenderableDataItem.HorizontalAlign;
import com.appnativa.rare.ui.RenderableDataItem.VerticalAlign;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.ViewerCreator;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.ui.painter.iPlatformComponentPainter;
import com.appnativa.rare.viewer.FormViewer;
import com.appnativa.rare.viewer.GridPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TabPaneViewer;
import com.appnativa.rare.viewer.WidgetPaneViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.LabelWidget;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONObject;

/**
 * Utilities for working with card stack interfaces
 * 
 * @author Don DeCoteau
 *
 */
public class CardStackUtils {
  private static String  VIEWER_ACTION_PROPERTY     = "_BV_VIEWER_ACTION";
  private static String  VIEWER_BUNDLE_URL_PROPERTY = "_BV_VIEWER_BUNDLE_URL";
  private static String  VIEWER_IS_BUNDLE_PROPERTY  = "_BV_VIEWER_IS_BUNDLE";
  private static String  VIEWER_SUBTITLE_PROPERTY   = "_BV_VIEWER_SUBTITLE";
  private static String  ITEM_TEXT_PROPERTY         = "_BV_ITEM_TEXT_PROPERTY";
  private static String  ITEM_LIST_TEXT_PROPERTY    = "_BV_ITEM_LIST_TEXT_PROPERTY";
  static boolean         wearable;
  static boolean         voiceActionsSupported      = true;
  static Form            listItemPageConfig;
  static iActionListener defaultActionListener      = new iActionListener() {

                                                      @Override
                                                      public void actionPerformed(ActionEvent e) {
                                                        Object o = e.getSource();
                                                        iWidget w = null;
                                                        if (o instanceof iWidget) {
                                                          w = (iWidget) o;
                                                          o = w == null ? null : w.getLinkedData();
                                                        }
                                                        if (o instanceof RenderableDataItem) { //
                                                          iActionListener al = ((RenderableDataItem) o).getActionListener();
                                                          if (al != null) {
                                                            executeAction(w == null ? null : Platform.getWindowViewer(), o, al);
                                                          }
                                                        }
                                                      }
                                                    };

  private CardStackUtils() {
  }

  /**
   * Clears the application's title
   */
  public static void clearTitle() {
    iContainer titleWidget = Utils.titleWidget;
    LabelWidget l = (LabelWidget) titleWidget.getWidget("bundleIcon");
    l.setIcon(null);

    l = (LabelWidget) titleWidget.getWidget("title");
    l.setText("");

    l = (LabelWidget) titleWidget.getWidget("subtitleLeft");
    l.setText("");

    l = (LabelWidget) titleWidget.getWidget("subtitleRight");
    l.setText("");
    titleWidget.update();
  }

  /**
   * Create a widget pane that will cause the specified action to be invoked
   * when the card is tapped. The content widget is a label widget
   * 
   * @param parent
   *          the card's parent
   * @param action
   *          the action
   * @return the new pane
   */
  public static WidgetPaneViewer createActionCard(iContainer parent, Object action) {
    return createCard(parent, action, false);
  }

  /**
   * Switches to a viewer that is a child of a tab or stack pane
   * 
   * @param viewer
   *          the viewer to switch to
   */
  public static void switchToViewer(iViewer viewer) {
    StackPaneViewer sp = Utils.getStackPaneViewer(viewer);
    if(sp!=null) {
      int n=sp.indexOf(viewer);
      if(n!=-1) {
        sp.switchTo(n);
        return;
      }
    }
    TabPaneViewer tp=(TabPaneViewer) Platform.getAppContext().getWindowManager().getWorkspaceViewer();
    int n = tp.indexOf(viewer);
    if (n != -1) {
      if (tp.getSelectedTab() != n) {
        tp.setSelectedTab(n);
      }
    }
  }

  /**
   * Create a widget pane that will be flagged as a bundle and will use the
   * specified url to create the content for the bundle
   * 
   * @param parent
   *          the card's parent
   * @param bundleURL
   *          the url to use to create the content
   * @return the new pane
   */
  public static WidgetPaneViewer createBundleCard(iContainer parent, String bundleURL) {
    return createCard(parent, bundleURL, true);
  }

  /**
   * Creates a viewer for displaying the specified collection. Use this method
   * to create a card (or stack of cards) for displaying the collection as
   * non-actionable items
   * 
   * @param title
   *          the title for the card
   * @param list
   *          the collection
   * @param column
   *          the column to use to create the displayed information (use -1 for
   *          one-dimensional lists)
   * @return the card
   */
  public static iViewer createItemsViewer(String title, Collection<RenderableDataItem> list, int column) {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("cardStackInfo");
    int itemsPerPage = info == null ? 5 : info.optInt("listPagingThreshold", 5);
    WindowViewer w = Platform.getWindowViewer();
    int len = list.size();
    FormViewer fv = (FormViewer) w.createWidget(listItemPageConfig);
    int n = 0;
    int i = 0;
    if (len > itemsPerPage) {
      itemsPerPage--;
    }
    Iterator<RenderableDataItem> it = list.iterator();
    while (it.hasNext()) {
      RenderableDataItem item = it.next();
      if (column != -1) {
        item = item.get(column);
      }
      n++;//skip a label
      LabelWidget l = (LabelWidget) fv.getWidget(n++);
      l.setIcon(item.getIcon());
      l.setValue(getItemText(item, true));
      i++;
      if (i == itemsPerPage) {
        break;
      }
    }
    fv.setTitle(title);
    if (len <= itemsPerPage) {
      return fv;
    } else {
      GridPane cfg = (GridPane) w.createConfigurationObject("GridPane", "bv.gridpane.items");
      GridPaneViewer gp = (GridPaneViewer) w.createViewer(w, cfg);
      setViewerAction(gp, new CardStackCollectionActionListener(title, list, column), true);
      gp.setTitle(title);
      String s = Platform.getWindowViewer()
          .getString("bv.format.tap_to_see_more", StringCache.valueOf(i), StringCache.valueOf(len));
      iWidget tapLabel = gp.getViewer(1);
      tapLabel.setValue(s); //WidgetPaneViewer's passes the setValue call onto the widget
      gp.setViewer(0, fv);
      return gp;
    }
  }

  /**
   * Creates a viewer for a list of items. Whether a page is created for each
   * item of or with multiple items is determined by the configured
   * listPagingThreshold.
   * 
   * @param title
   *          the title of the stack pane
   * @param parent
   *          the parent component
   * @param list
   *          the list of items
   * @param itemsPerPage
   *          the number of items per page
   * @param itemsPerPage
   *          the number of items per page (use -1 for the configured default)
   * @param column
   *          the column to use to create the displayed information (use -1 for
   *          one-dimensional lists)
   * @param action
   *          the action to invoke when an item is selected.
   * @param bundle
   *          true if items are themselves bundles; false otherwise the action
   *          toe invoke to invoke when an item is selected
   * @param stretched
   *          TODO
   * @return the stack pane
   */
  public static StackPaneViewer createListItemsOrPageViewer(String title, final iContainer parent, List<RenderableDataItem> list,
      int itemsPerPage, int column, final iActionListener action, final boolean bundle, final boolean stretched) {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("cardStackInfo");
    int listPagingThreshold = info == null ? 5 : info.optInt("listPagingThreshold", 5);
    if (itemsPerPage == -1) {
      itemsPerPage = info == null ? 5 : info.optInt("itemsPerPage", 5);
    }
    if (list.size() > listPagingThreshold) {
      iActionListener l = new iActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          iWidget w = e.getWidget();
          Object o = w.getLinkedData();
          if (o instanceof RenderableDataItem) { //
            iActionListener al = ((RenderableDataItem) o).getActionListener();
            if (al == null) {
              al = action;
            }
            executeAction(w, o, al);
          } else {
            List<RenderableDataItem> items = (List<RenderableDataItem>) o;
            if (items.size() == 1) {
              o = items.get(0);
              w.setLinkedData(o); //the action is expecting the item to be the widget's linked data
              executeAction(w, o, action);
            } else {
              Utils.pushWorkspaceViewer(createListItemsViewer(null, null, parent, items, action, bundle, stretched), false);
            }
          }
        }
      };
      return createListPagesViewer(title, parent, list, itemsPerPage, column, l);
    } else {
      StackPaneViewer sp=createListItemsViewer(null, title, parent, list, action, bundle, stretched);
      sp.switchTo(0);
      return sp;
    }
  }

  /**
   * Creates a stack pane for displaying a list as multiple pages where each
   * item represents is represented as a single page. When a page is selected
   * the specified action is invoked.
   * 
   * <p>
   * The linked data for each page viewer is the item that the page represents
   * </p>
   * 
   * @param sp
   *          the stack pane viewer to populate or null to have one created
   * @param title
   *          the title of the stack pane
   * @param parent
   *          the parent component
   * @param list
   *          the list of items
   * @param action
   *          the action toe invoke to invoke when a page is selected. If null
   *          the specified items are expected to have their own action listener
   * @param bundle
   *          true if items are themselves bundles; false otherwise the action
   * @param stretched
   *          true if individual item viewers should be centered or stretched
   * @return the stack pane
   */
  public static StackPaneViewer createListItemsViewer(StackPaneViewer sp, String title, iContainer parent,
      List<RenderableDataItem> list, iActionListener action, boolean bundle, boolean stretched) {
    WindowViewer w = Platform.getWindowViewer();
    if (sp == null) {
      StackPane cfg = (StackPane) w.createConfigurationObject("StackPane", "bv.stackpane.card");
      if (title != null) {
        cfg.title.setValue(title);
      }
      sp = (StackPaneViewer) w.createViewer(parent, cfg);
    }
    if (action == null) {
      action = defaultActionListener;
    }
    int card = 1;
    int len = list.size();
    WidgetPaneViewer wp;
    for (int i = 0; i < len; i++) {
      RenderableDataItem item = list.get(i);
      sp.addViewer(null, wp = createCard(sp, action, false));
      wp.setLinkedData(item);
      wp.setAttribute(VIEWER_SUBTITLE_PROPERTY, w.getString("bv.format.card_of", card++, len));
      wp.setAttribute(VIEWER_IS_BUNDLE_PROPERTY, bundle);
      LabelWidget l = (LabelWidget) wp.getWidget();
      if (stretched) {
        wp.setWidgetRenderType(RenderType.STRETCHED);
        l.setHorizontalAlignment(HorizontalAlign.LEFT);
        l.setVerticalAlignment(VerticalAlign.TOP);
      }
      String s = getItemText(item, false);
      l.setValue(s);
      l.setIcon(item.getIcon());
      UIColor fg = item.getForeground();
      iPlatformComponentPainter cp = item.getComponentPainter();
      if (fg != null) {
        l.setForeground(fg);
      }
      if (cp != null) {
        l.setComponentPainter(cp);
      }
    }
    return sp;
  }

  /**
   * /** Creates a stack pane for displaying a list as multiple pages where each
   * item represents a line of text on the page.
   * 
   * @param title
   *          the title of the stack pane
   * @param parent
   *          the parent component
   * @param list
   *          the list of items
   * @param itemsPerPage
   *          the number of items per page
   * @param itemsPerPage
   *          the number of items per page (use -1 for the configured default)
   * @param column
   *          the column to use to create the displayed information (use -1 for
   *          one-dimensional lists)
   * @return the stack pane
   */
  public static StackPaneViewer createListPagesViewer(String title, final iContainer parent, List<RenderableDataItem> list,
      int itemsPerPage, int column) {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("cardStackInfo");
    if (itemsPerPage == -1) {
      itemsPerPage = info == null ? 5 : info.optInt("itemsPerPage", 5);
    }
    return createListPagesViewer(title, parent, list, itemsPerPage, column, null);
  }

  /**
   * Creates a new stack pane viewer configured for working a a card viewer
   * 
   * @return the stack pane viewer
   */
  public static StackPaneViewer createStackPaneViewer() {
    WindowViewer w = Platform.getWindowViewer();
    StackPane cfg = (StackPane) w.createConfigurationObject("StackPane", "bv.stackpane.card");
    return (StackPaneViewer) w.createViewer(w, cfg);
  }

  /**
   * Creates a card for displaying text
   * 
   * @param parent
   *          the parent viewer
   * @param text
   *          the text
   * @param action
   *          action to invoke (can be null)
   * @return the card
   */
  public static WidgetPaneViewer createTextCard(iContainer parent, String text, Object action) {
    WidgetPaneViewer wp = createActionCard(parent, action);
    LabelWidget l = (LabelWidget) wp.getWidget();
    l.setValue(text);
    return wp;
  }

  public static void executeAction(iWidget widget, Object source, Object action) {
    if (action instanceof iActionListener) {
      ((iActionListener) action).actionPerformed(new ActionEvent(source));
    } else if (action instanceof Runnable) {
      ((Runnable) action).run();
    } else if (action instanceof String) {
      ((aWidget) widget).evaluateCode(action);
    }
  }

  public static String generateRandomNumberString(int digits) {
    long max = 10;
    long min = 1;
    while (digits > 1) {
      max *= 10;
      min *= 10;
      digits--;
    }
    long rand = Functions.randomLong(max);
    while (rand < min) {
      long nrand = Functions.randomLong(max);
      if (nrand > min) {
        rand = nrand;
      } else {
        rand += nrand;
      }
    }
    rand = rand % max;
    while (rand < min) { //should not happen
      rand *= 10;
    }
    return Long.toString(rand);
  }

  /**
   * Get the text for an item
   * 
   * @param item
   *          the item
   * @param forList
   *          true if the text is to be displayed in a list; false otherwise
   * @return
   */
  public static String getItemText(RenderableDataItem item, boolean forList) {
    String s = (String) item.getCustomProperty(forList ? ITEM_LIST_TEXT_PROPERTY : ITEM_TEXT_PROPERTY);
    return s == null ? item.toString() : s;
  }

  /**
   * Gets the action associated with the viewer
   * 
   * @param viewer
   *          the viewer
   * @return the action or null
   */
  public static Object getViewerAction(iViewer viewer) {
    return viewer.getAttribute(VIEWER_ACTION_PROPERTY);
  }

  /**
   * Gets the bundle url associated with the viewer
   * 
   * @param viewer
   *          the viewer
   * @return the url or null
   */
  public static String getViewerBundleURL(iViewer viewer) {
    return (String) viewer.getAttribute(VIEWER_BUNDLE_URL_PROPERTY);
  }

  /**
   * Returns whether the specified viewer represents a bundle
   * 
   * @param viewer
   *          the viewer
   * @return true if it does; false otherwise
   */
  public static boolean isBundle(iViewer viewer) {
    return viewer.getAttribute(VIEWER_IS_BUNDLE_PROPERTY) == Boolean.TRUE;
  }

  /**
   * Selects an item on a page of numbered items
   * 
   * @param page
   *          the page containing the item
   * @param itemNumber
   *          the number of the item on the page
   * @return true if there was an item corresponding the the number; false
   *         otherwise
   */
  public static boolean selectItemOnPage(iViewer page, int itemNumber) {
    Object action = page.getAttribute(VIEWER_ACTION_PROPERTY);
    Object o = page.getLinkedData();
    if (o instanceof List) {
      List<RenderableDataItem> items = (List<RenderableDataItem>) o;
      if (items.size() < itemNumber) {
        o = items.get(itemNumber);
      }
    }
    if (o instanceof RenderableDataItem) {
      executeAction(page, o, action);
      return true;
    }
    return false;
  }

  /**
   * Sets the text to display on a card for the specified item. This is meant to
   * be used in conjunction with methods that create cards from a list of items
   * 
   * @param item
   *          the item
   * @param text
   *          the text to use instead of the item's toString value.
   */
  public static void setItemText(RenderableDataItem item, String text) {
    item.setCustomProperty(ITEM_TEXT_PROPERTY, text);
  }

  /**
   * Sets the line of text to display on a card for the specified item. This is
   * meant to be used in conjunction with methods that displays items as a list
   * of values on a card. If the list is actionable then the items will be
   * numbered
   * 
   * @param item
   *          the item
   * @param text
   *          the text to use instead of the item's toString value.
   */
  public static void setListItemText(RenderableDataItem item, String text) {
    item.setCustomProperty(ITEM_LIST_TEXT_PROPERTY, text);
  }

  /**
   * Called to setup the environment for working with a card stack
   * 
   * @param w
   *          the window
   */
  public static void setupEnvironment(WindowViewer w) {
    try {
      ViewerCreator.createConfiguration(w, new ActionLink("page_of_items.rml"), new iFunctionCallback() {

        @Override
        public void finished(boolean calceled, Object returnValue) {
          listItemPageConfig = (Form) returnValue;
        }
      });
    } catch (Exception e) {
      Utils.handleError(e);
    }
  }

  /**
   * Sets the action for the viewer
   * 
   * @param viewer
   *          the viewer
   * @param action
   *          the action
   * @param bundle
   *          true to if this card will represent a bundle; false otherwise
   */
  public static void setViewerAction(iViewer viewer, Object action, boolean bundle) {
    viewer.setAttribute(VIEWER_ACTION_PROPERTY, action);
    viewer.setAttribute(VIEWER_IS_BUNDLE_PROPERTY, bundle ? Boolean.TRUE : Boolean.FALSE);
  }

  /**
   * Set the bundle url for a viewer
   * 
   * @param viewer
   *          the viewer
   * @param url
   *          the url
   */
  public static void setViewerBundleURL(iViewer viewer, String url) {
    viewer.setAttribute(VIEWER_BUNDLE_URL_PROPERTY, url);
    viewer.setAttribute(VIEWER_IS_BUNDLE_PROPERTY, Boolean.TRUE);
  }

  /**
   * Sets the sub-title for a viewer that will act as a card
   * 
   * @param viewer
   *          the viewer
   * @param subtitle
   *          the sub-title
   */
  public static void setViewerSubTitle(iViewer viewer, String subtitle) {
    viewer.setAttribute(VIEWER_SUBTITLE_PROPERTY, subtitle);
  }

  /**
   * Sets the title and sub-title for a viewer that will act as a card
   * 
   * @param viewer
   *          the viewer
   * @param title
   *          the title
   * @param subtitle
   *          the sub-title
   */
  public static void setViewerTitle(iViewer viewer, String title, String subtitle) {
    viewer.setTitle(title);
    viewer.setAttribute(VIEWER_SUBTITLE_PROPERTY, subtitle);
  }

  /**
   * Update the application's sub-title using the specified title information
   * 
   * @param bundle
   *          true to display the bundle icon; false otherwise
   * @param subTitle
   *          the sub-title
   */
  public static void updateSubTitle(String subTitle, boolean bundle) {
    iContainer titleWidget = Utils.titleWidget;
    LabelWidget bundleIconLabel = (LabelWidget) titleWidget.getWidget("bundleIcon");
    LabelWidget bundleTextLabel = (LabelWidget) titleWidget.getWidget("subtitleRight");
    if (subTitle != null && subTitle.length() > 0) {
      bundleTextLabel.setText(subTitle);
    }
    bundleIconLabel.setIcon(bundle ? Platform.getResourceAsIcon("bv.icon.bundle") : null);
  }

  /**
   * Update the application's title using the specified viewer as the source of
   * the title information
   * 
   * @param viewer
   *          the viewer
   * @param force
   *          true update the title even if the viewer does not have a title (in
   *          that case the viewer parent chain will be searched for a title);
   *          false otherwise
   */
  public static void updateTitle(iViewer viewer, boolean force) {
    if (viewer instanceof TabPaneViewer) {
      viewer = ((TabPaneViewer) viewer).getSelectedTabViewer();
    } else if (viewer instanceof StackPaneViewer) {
      iViewer v = ((StackPaneViewer) viewer).getActiveViewer();
      if (v != null) {
        viewer = v;
      }
    }
    iContainer titleWidget = Utils.titleWidget;
    LabelWidget iconLabel = (LabelWidget) titleWidget.getWidget("bundleIcon");
    LabelWidget textLabel = (LabelWidget) titleWidget.getWidget("subtitleLeft");
    String title = viewer.getTitle();
    iViewer v = viewer;
    while (force && (title == null || title.length() == 0)) {
      v = v.getParent();
      if (v == null) {
        return;
      }
      title = v.getTitle();
    }
    if (title != null && title.length() > 0) {
      textLabel.setText(title);
    }
    iconLabel.setIcon(CardStackUtils.isBundle(viewer) ? Platform.getResourceAsIcon("bv.icon.bundle") : null);
    textLabel = (LabelWidget) titleWidget.getWidget("subtitleRight");
    title = (String) viewer.getAttribute(VIEWER_SUBTITLE_PROPERTY);
    textLabel.setText(title == null ? "" : title);
  }

  /**
   * Update the application's title using the specified title information
   * 
   * @param title
   *          the title
   * @param bundle
   *          true to display the bundle icon; false otherwise
   * @param subTitle
   *          the sub-title
   */
  public static void updateTitle(String title, boolean bundle, String subTitle) {
    iContainer titleWidget = Utils.titleWidget;
    LabelWidget iconLabel = (LabelWidget) titleWidget.getWidget("bundleIcon");
    LabelWidget textLabel = (LabelWidget) titleWidget.getWidget("subtitleLeft");
    if (title != null && title.length() > 0) {
      textLabel.setText(title);
    }
    iconLabel.setIcon(bundle ? Platform.getResourceAsIcon("bv.icon.bundle") : null);
    textLabel = (LabelWidget) titleWidget.getWidget("subtitleRight");
    if (subTitle == null) {
      subTitle = "";
    }
    textLabel.setText(subTitle);
  }

  /**
   * Create a widget pane that will cause the specified action to be invoked
   * when the card is tapped. The content of the pane is a label widget.
   * 
   * @param parent
   *          the card's parent
   * @param action
   *          the action (can be null)
   * @param bundle
   *          true to if this card will represent a bundle; false otherwise
   * 
   * @return the new pane
   */
  protected static WidgetPaneViewer createCard(iContainer parent, Object action, boolean bundle) {
    WindowViewer w = Platform.getWindowViewer();
    WidgetPane cfg = (WidgetPane) w.createConfigurationObject("WidgetPane", "bv.widgetpane.card");
    Label label = (Label) w.createConfigurationObject("Label", bundle ? "bv.label.card.bundle" : "bv.label.card.item");
    cfg.widget.setValue(label);
    iViewer v = w.createViewer(parent, cfg);
    if (bundle) {
      v.setAttribute(VIEWER_BUNDLE_URL_PROPERTY, action);
      v.setAttribute(VIEWER_IS_BUNDLE_PROPERTY, Boolean.TRUE);

    } else {
      v.setAttribute(VIEWER_ACTION_PROPERTY, action);
    }
    return (WidgetPaneViewer) v;
  }

  /**
   * Create a widget pane that will cause the specified action to be invoked
   * when the card is tapped
   * 
   * @param parent
   *          the card's parent
   * @param action
   *          the action (can be null)
   * @param bundle
   *          true to if this card will represent a bundle; false otherwise
   * @param content
   *          the content for the pane
   * 
   * @return the new pane
   */
  protected static WidgetPaneViewer createCard(iContainer parent, Object action, boolean bundle, iWidget content) {
    WindowViewer w = Platform.getWindowViewer();
    WidgetPane cfg = (WidgetPane) w.createConfigurationObject("WidgetPane", "bv.widgetpane.card");
    iViewer v = w.createViewer(parent, cfg);
    if (bundle) {
      v.setAttribute(VIEWER_BUNDLE_URL_PROPERTY, action);
      v.setAttribute(VIEWER_IS_BUNDLE_PROPERTY, Boolean.TRUE);
    } else {
      v.setAttribute(VIEWER_ACTION_PROPERTY, action);
    }
    WidgetPaneViewer wp = (WidgetPaneViewer) v;
    wp.setWidget(content);
    return wp;
  }

  /**
   * Creates a stack pane for displaying a list as multiple pages where each
   * item represents a line of text on the page. When a page is selected the
   * specified action is invoked.
   * 
   * <p>
   * The linked data for each page viewer is a sub-list of the specified list
   * representing the range of items displayed on the page
   * </p>
   * 
   * @param title
   *          the title of the stack pane
   * 
   * @param parent
   *          the parent component
   * @param list
   *          the list of items
   * @param itemsPerPage
   *          the number of items per page
   * @param column
   *          the column to use to create the displayed information (use -1 for
   *          one-dimensional lists)
   * @param action
   *          the action toe invoke to invoke when a page is selected
   * @return the stack pane
   */
  protected static StackPaneViewer createListPagesViewer(String title, iContainer parent, List<RenderableDataItem> list,
      int itemsPerPage, int column, iActionListener action) {
    WindowViewer w = Platform.getWindowViewer();
    StackPane cfg = (StackPane) w.createConfigurationObject("StackPane", "bv.stackpane.card");
    if (title != null) {
      cfg.title.setValue(title);
    }
    StackPaneViewer sp = (StackPaneViewer) w.createViewer(parent, cfg);
    int len = list.size();
    int lastPos = 0;
    StringBuilder sb = new StringBuilder();
    iViewer v;
    int card = 1;
    while (lastPos < len) {
      int start = lastPos;
      FormViewer fv = (FormViewer) w.createWidget(listItemPageConfig);
      int n = 0;
      for (int i = 0; i < itemsPerPage && lastPos < len; i++) {
        RenderableDataItem item = list.get(lastPos++);
        if (column != -1) {
          item = item.get(column);
        }
        LabelWidget l = (LabelWidget) fv.getWidget(n++);
        if (action != null) {
          sb.setLength(0);
          sb.append(i + 1).append('.');
          l.setValue(sb.toString());
        }
        l = (LabelWidget) fv.getWidget(n++);
        l.setIcon(item.getIcon());
        l.setValue(getItemText(item, true));
      }
      sp.addViewer(null, v = createCard(sp, action, false, fv));
      v.setLinkedData(list.subList(start, lastPos));
      v.setAttribute(VIEWER_IS_BUNDLE_PROPERTY, Boolean.TRUE);
      v.setAttribute(VIEWER_SUBTITLE_PROPERTY, w.getString("bv.format.card_of", card++, sp.size()));
    }
    sp.switchTo(0);
    return sp;
  }
}
