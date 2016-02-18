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
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.Column;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.RenderableDataItem.VerticalAlign;
import com.appnativa.rare.ui.UIColorHelper;
import com.appnativa.rare.ui.UICompoundIcon;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIMenuItem;
import com.appnativa.rare.ui.UIPopupMenu;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.ui.iListHandler;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.util.SubItemComparator;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.widget.ComboBoxWidget;
import com.appnativa.rare.widget.LabelWidget;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.CharArray;
import com.appnativa.util.IntList;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class handles lab values. The data is assumed to be returned is reverse
 * chronological order (newest values first). The middleware should enforce this
 * constraint.
 *
 * @author Don DeCoteau
 */
public class Orders extends aResultsManager implements iActionListener {
  public static final int            CATEGORY_NAME_POSITION     = 11;
  public static final int            CLINICAL_CATEGORY_POSITION = 12;
  public static int                  SIG_COLUMN                 = 9;
  public static int                  STATUS_COLUMN              = 8;
  public static int                  SUMMARY_STATUS_COLUMN      = 1;
  public static int                  SIGNED_COLUMN              = 14;
  public static int                  FLAGGED_COLUMN             = 15;
  public static final byte           FLAGGED_FLAG               = 0x01;
  public static final byte           UNSIGNED_FLAG              = 0x02;
  public static final byte           HOLDABLE_FLAG              = 0x04;
  public static final byte           ONHOLD_FLAG                = 0x08;
  public static final byte           STOPABLE_FLAG              = 0x10;
  public static final byte           STOPPED_FLAG               = 0x20;
  public static final byte           REQUIRED_FLAG              = 0x40;
  public static boolean              hasOrderDiscontinueSupport;
  public static boolean              hasOrderEntrySupport;
  public static boolean              hasOrderDictationSupport;
  public static boolean              hasOrderFlagSupport;
  public static boolean              hasOrderRewriteSupport;
  public static boolean              hasOrderHoldSupport;
  public static boolean              hasOrderSignSupport;
  public static boolean              hasOrderSentenceSupport;
  public static boolean              showActionWaitMessage;
  public static boolean              verifyOrderEntryCancel;
  public static boolean              verifyOrderEntryDelete;
  public static boolean              signatureRequiredForSumbission;
  public static boolean              showRequiredFieldsOnlyDefault;
  public static List<String>         holdableStatus;
  public static List<String>         stopableStatus;
  public static String               holdStatusValue;
  public static String               unholdStatusValue;
  public static String               defaultOrderType;
  public static String               medicationOrderType;
  public static String               defaultRoute;
  public static String               booleanFieldTrueValue;
  public static String               booleanFieldFalseValue;
  public static String               booleanFieldTrueDisplayValue;
  public static String               booleanFieldFalseDisplayValue;
  public static JSONObject           statusColors;
  public static String               endHtml;
  public static String               startHtml;
  public static String               endHtmlSC;
  public static String               startHtmlSC;
  static iPlatformIcon               unsignedIcon;
  static iPlatformIcon               flaggedIcon;
  static iPlatformIcon               holdIcon;
  static iPlatformIcon               flaggedAndUnsignedIcon;
  static iPlatformIcon               flaggedAndHoldIcon;
  static UIMenuItem                  holdItem;
  static UIMenuItem                  flagItem;
  static UIMenuItem                  unholdItem;
  static UIMenuItem                  unflagItem;
  protected int                      categorySortPosition = -1;
  protected boolean                  hasClinicalCategories;
  protected boolean                  appendDirections;
  protected boolean                  includeIVsInCategorizedMeds;
  protected String                   categorizedMedsTitle;
  protected String                   missingClinicalCategoryTitle;
  protected String                   missingCategoryTitle;
  protected String                   medicationsTitle;
  protected String                   ivsTitle;
  protected String                   ivsCategoryID;
  protected String                   medsCategoryID;
  protected String                   cmedsCategoryID = "_c_meds_";
  protected List<RenderableDataItem> categorizedMeds;
  protected boolean                  categorizedMedsLoaded;
  protected List<RenderableDataItem> allOrders;
  protected long                     lastOrderActionTime;
  protected Document                 loadedDocument;

  public Orders() {
    super("orders", "Orders");

    boolean cardstack = Utils.isCardStack();

    currentView = ResultsView.DOCUMENT;

    JSONObject info = (JSONObject) Platform.getAppContext().getData("ordersInfo");

    hasClinicalCategories        = cardstack
                                   ? false
                                   : info.optBoolean("hasClinicalCategories", false);
    includeIVsInCategorizedMeds  = info.optBoolean("includeIVsInCategorizedMeds", true);
    categorizedMedsTitle         = info.optString("categorizedMedsTitle", "Medications (Categorized)");
    missingClinicalCategoryTitle = info.optString("missingClinicalCategoryTitle", "unclassified medications");
    missingCategoryTitle         = info.optString("missingCategoryTitle", "Misc. Orders");
    medsCategoryID               = info.optString("medsCategoryID", "meds");
    ivsCategoryID                = info.optString("ivsCategoryID", "ivs");
    medicationsTitle             = info.optString("medicationsTitle", null);

    if (medicationsTitle == null) {
      medicationsTitle = Platform.getResourceAsString("bv.text.medications");
    }

    ivsTitle = info.optString("ivsTitle", null);

    if (ivsTitle == null) {
      ivsTitle = Platform.getResourceAsString("bv.text.iv_solutions");
    }
  }

  public static void setupEnvironment(WindowViewer w) {
    JSONObject o = (JSONObject) Platform.getAppContext().getData("ordersInfo");

    hasOrderDiscontinueSupport     = o.optBoolean("hasOrderDiscontinueSupport");
    hasOrderEntrySupport           = o.optBoolean("hasOrderEntrySupport");
    hasOrderDictationSupport       = o.optBoolean("hasOrderDictationSupport");
    hasOrderRewriteSupport         = o.optBoolean("hasOrderRewriteSupport");
    hasOrderFlagSupport            = o.optBoolean("hasOrderFlagSupport");
    hasOrderHoldSupport            = o.optBoolean("hasOrderHoldSupport");
    hasOrderSignSupport            = o.optBoolean("hasOrderSignSupport");
    hasOrderSentenceSupport        = o.optBoolean("hasOrderSentenceSupport");
    showActionWaitMessage          = o.optBoolean("showActionWaitMessage");
    verifyOrderEntryCancel         = o.optBoolean("verifyOrderEntryCancel");
    verifyOrderEntryDelete         = o.optBoolean("verifyOrderEntryDelete");
    signatureRequiredForSumbission = o.optBoolean("signatureRequiredForSumbission");
    showRequiredFieldsOnlyDefault  = o.optBoolean("showRequiredFieldsOnlyDefault");
    holdStatusValue                = o.optString("holdStatusValue", "Hold");
    unholdStatusValue              = o.optString("unholdStatusValue", "Unhold");
    defaultOrderType               = o.optString("defaultOrderType", "");
    medicationOrderType            = o.optString("medicationOrderType", "");
    defaultRoute                   = o.optString("defaultRoute", "");

    JSONArray a = o.optJSONArray("holdableStatus");

    holdableStatus = (a != null)
                     ? a.getObjectList()
                     : Collections.EMPTY_LIST;
    a              = o.optJSONArray("stopableStatus");
    stopableStatus = (a != null)
                     ? a.getObjectList()
                     : Collections.EMPTY_LIST;

    JSONObject bo = o.getJSONObject("booleanFieldTypeInfo");

    booleanFieldTrueValue         = bo.optString("trueValue", "true");
    booleanFieldFalseValue        = bo.optString("falseValue", "false");
    booleanFieldTrueDisplayValue  = bo.optString("trueDisplayValue", booleanFieldTrueValue);
    booleanFieldFalseDisplayValue = bo.optString("falseDisplayValue", booleanFieldFalseValue);
    booleanFieldTrueDisplayValue  = Platform.getWindowViewer().expandString(booleanFieldTrueDisplayValue);
    booleanFieldFalseDisplayValue = Platform.getWindowViewer().expandString(booleanFieldFalseDisplayValue);
    statusColors                  = o.optJSONObject("statusColors");

    if (Utils.isCardStack()) {
      startHtml = o.optString("csDirectionsHtmlStart", null);
      endHtml   = o.optString("csDirectionsHtmlEnd", null);
    }

    if (startHtml == null) {
      startHtml = o.optString("directionsHtmlStart", "");
    }

    if (endHtml == null) {
      endHtml = o.optString("directionsHtmlEnd", "");
    }

    startHtmlSC = o.optString("csDirectionsStatusColorHtmlStart", null);

    if (startHtmlSC == null) {
      startHtmlSC = o.optString("directionsStatusColorHtmlStart", startHtml);
    }

    endHtmlSC = o.optString("csDirectionsStatusColorHtmlEnd", null);

    if (endHtmlSC == null) {
      endHtmlSC = o.optString("directionsStatusColorHtmlEnd", endHtml);
    }

    if (startHtml == null) {
      startHtml = "";
    }

    if (endHtml == null) {
      endHtml = "";
    }

    iPlatformAppContext app = Platform.getAppContext();
    String              s;

    if (hasOrderHoldSupport) {
      holdItem = new UIMenuItem(app.getAction(s = "bv.action.order_hold"));
      holdItem.setName(s);
      unholdItem = new UIMenuItem(app.getAction(s = "bv.action.order_unhold"));
      unholdItem.setName(s);
      holdItem.setDisposable(false);
      unholdItem.setDisposable(false);
    }

    flaggedIcon  = app.getResourceAsIcon("bv.icon.order_flagged");
    unsignedIcon = app.getResourceAsIcon("bv.icon.order_unsigned");

    UICompoundIcon ic = new UICompoundIcon(flaggedIcon, unsignedIcon);

    ic.alignSideBySide(false, UIScreen.PLATFORM_PIXELS_2, false);
    flaggedAndUnsignedIcon = ic;
    holdIcon               = app.getResourceAsIcon("bv.icon.order_hold");
    ic                     = new UICompoundIcon(flaggedIcon, holdIcon);
    ic.alignSideBySide(false, UIScreen.PLATFORM_PIXELS_2, false);
    flaggedAndHoldIcon = ic;

    if (hasOrderFlagSupport) {
      flagItem = new UIMenuItem(app.getAction(s = "bv.action.order_flag"));
      flagItem.setName(s);
      unflagItem = new UIMenuItem(app.getAction(s = "bv.action.order_unflag"));
      unflagItem.setName(s);
      flagItem.setDisposable(false);
      unflagItem.setDisposable(false);
    }
  }

  /**
   * The action handler for the the user click on a categories menu. This class
   * is set as the action listener for the menu items.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    iWidget            cw   = e.getWidget();
    RenderableDataItem item = null;

    if (cw instanceof PushButtonWidget) {
      item = ((PushButtonWidget) cw).getSelectedItem();
    } else if (cw instanceof ComboBoxWidget) {
      item = ((ComboBoxWidget) cw).getSelectedItem();
    }

    if (item != null) {
      if (item.isEditable()) {
        loadOrderCategory(cw, item);
      } else {
        filterTable(dataTable, (String) item.getLinkedData());
      }
    }
  }

  protected void loadOrderCategory(final iWidget cw, final RenderableDataItem item) {
    final TableViewer  table = dataTable;
    final WindowViewer w     = Platform.getWindowViewer();
    final String       cat   = (String) item.getLinkedData();

    item.setEditable(false);
    item.setIcon(Platform.getResourceAsIcon("bv.icon.dash"));
    if(cw instanceof ComboBoxWidget) {
      ((ComboBoxWidget)cw).rowChanged(item);
    }
    final ActionLink link = Utils.createLink(table, "/hub/main/orders/category/" + cat, false);

    try {
      aWorkerTask task = new aWorkerTask() {
        @Override
        public void finish(Object result) {
          try {
            if (result instanceof Throwable) {
              Utils.handleError((Throwable) result);

              return;
            }

            List<RenderableDataItem> rows = (List<RenderableDataItem>) result;

            if (table.isDisposed()) {
              return;
            }

            if (rows != null) {
              processDataEx(table, rows);
            }

            if ((rows == null) || rows.isEmpty()) {
              if (rows == null) {
                rows = new ArrayList<RenderableDataItem>(1);
              }

              String msg = Platform.getWindowViewer().getString("bv.format.no_orders_found_for_category",
                             item.toString());
              RenderableDataItem row;

              rows.add(row = createNoDataRow(table, msg));
              row.get(0).setValue(cat);
              row.addMissingColumns(CATEGORY_NAME_POSITION + 1);
              row.get(CATEGORY_NAME_POSITION).setValue(item.getValue());
            }

            rows = Utils.groupRows(table, rows, CATEGORY_NAME_POSITION, -1, false);
            allOrders.addAll(rows);
            table.unfilter();
            table.setAll(allOrders);
            filterTable(table, cat);
            cw.update();
          } finally {
            w.hideWaitCursor();
          }
        }
        @Override
        public Object compute() {
          try {
            List<RenderableDataItem> rows = table.isDisposed()
                                            ? null
                                            : table.parseDataLink(link, true);

            if (!table.isDisposed()) {}

            return rows;
          } catch(Exception e) {
            return e;
          }
        }
      };

      w.showProgressPopup(w.getString("bv.text.loading"));
      w.spawn(task);
    } catch(Exception e) {
      Utils.handleError(e);
    }
  }

  public boolean isHoldable(RenderableDataItem row) {
    if (holdableStatus != null) {
      return holdableStatus.indexOf(row.getCustomProperty(STATUS_COLUMN).toString()) != -1;
    }

    return true;
  }

  public boolean needsSigning(RenderableDataItem row) {
    if (holdableStatus != null) {
      return holdableStatus.indexOf(row.getCustomProperty(STATUS_COLUMN).toString()) != -1;
    }

    return true;
  }

  public void onCreated(String eventName, iWidget widget, EventObject event) {
    lastOrderActionTime = OrderManager.getLastOrderActionTtime();

    if (Utils.isCardStack()) {
      iContainer fv        = widget.getFormViewer();
      iContainer itemsForm = (iContainer) fv.getWidget("itemsForm");

      CardStackUtils.setViewerTitle(fv, fv.getTitle(), itemsForm.getTitle());
    }
  }

  public void onConfigure(String eventName, iWidget widget, EventObject event) {}

  @Override
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    super.onDispose(eventName, widget, event);
    categorizedMeds = null;
    allOrders       = null;

    if (loadedDocument != null) {
      loadedDocument.dispose();
      loadedDocument = null;
    }
  }

  public void onTableShown(String eventName, iWidget widget, EventObject event) {
    long time = OrderManager.getLastOrderActionTtime();

    if (time > lastOrderActionTime) {
      reloadTableData((TableViewer) widget);
    }

    lastOrderActionTime = time;
  }

  public void onTableHidden(String eventName, iWidget widget, EventObject event) {
    lastOrderActionTime = OrderManager.getLastOrderActionTtime();
  }

  /**
   * Called when the orders data has been loaded into the table. We populate the
   * the card when using a cardstack UI.
   */
  public void onFinishedLoading(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    if (Utils.isCardStack()) {
      iFormViewer fv    = table.getFormViewer();

      populateCardStack(fv, table);
      CardStackUtils.setViewerAction(fv, new OrdersStackActionListener(), true);
      CardStackUtils.switchToViewer(table.getParent());
      CardStackUtils.updateTitle(fv, false);
    }
    else {
      table.pageHome();
    }
  }

  public void onPopupMenu(String eventName, iWidget widget, EventObject event) {
    TableViewer         table  = (TableViewer) widget;
    int                 index  = table.getContextMenuIndex();
    RenderableDataItem  row    = table.get(index);
    int                 status = row.getUserStateFlags();
    UIPopupMenu         menu   = (UIPopupMenu) event.getSource();
    UIMenuItem          mi;
    int                 n;
    iPlatformAppContext app   = Platform.getAppContext();
    boolean             valid = row.get(0).getLinkedData() != null;

    app.getAction("bv.action.order_sign").setEnabled(valid && hasOrderSignSupport && (status & UNSIGNED_FLAG) != 0);
    app.getAction("bv.action.order_discontinue").setEnabled(valid && hasOrderDiscontinueSupport
                  && (status & STOPABLE_FLAG) != 0 && (status & STOPPED_FLAG) == 0);
    app.getAction("bv.action.order_new").setEnabled(valid && hasOrderEntrySupport);
    app.getAction("bv.action.order_rewrite").setEnabled(valid && hasOrderRewriteSupport);

    if (hasOrderFlagSupport) {
      mi = menu.getMenuItem("bv.action.order_flag");

      if (mi == null) {
        mi = menu.getMenuItem("bv.action.order_unflag");
      }

      if (mi != null) {
        n = menu.indexOf(mi);

        if ((status & FLAGGED_FLAG) != 0) {
          mi = unflagItem;
        } else {
          mi = flagItem;
        }

        mi.getAction().setEnabled(valid && (status & STOPPED_FLAG) == 0);
        menu.set(n, mi);
      }
    } else {
      app.getAction("bv.action.order_flag").setEnabled(false);
    }

    if (hasOrderHoldSupport) {
      mi = menu.getMenuItem("bv.action.order_hold");

      if (mi == null) {
        mi = menu.getMenuItem("bv.action.order_unhold");
      }

      if (mi != null) {
        n = menu.indexOf(mi);

        if ((status & ONHOLD_FLAG) != 0) {
          mi = unholdItem;
        } else {
          mi = holdItem;
        }

        if ((status & UNSIGNED_FLAG) != 0) {
          mi.getAction().setEnabled(false);
        } else {
          mi.getAction().setEnabled(valid && (status & STOPPED_FLAG) == 0);
        }

        menu.set(n, mi);
      }
    } else {
      app.getAction("bv.action.order_hold").setEnabled(false);
    }
  }

  public void onSummaryTableAction(String eventName, iWidget widget, EventObject event) {
    String     key  = ((TableViewer) widget).getSelectionDataAsString(0);
    ActionPath path = new ActionPath("orders", key);

    Utils.handleActionPath(path);
  }

  @Override
  public void onTableAction(String eventName, iWidget widget, EventObject event) {
    final TableViewer        table = (TableViewer) widget;
    final RenderableDataItem row   = table.getSelectedItem();
    final String             id    = (row == null)
                                     ? null
                                     : (String) row.get(0).getLinkedData();
    iFormViewer              fv    = widget.getFormViewer();

    if ((id == null) || (id.length() == 0)) {
      iContainer dv = (iContainer) fv.getWidget("documentViewer");

      clearForm((dv == null)
                ? fv
                : dv);

      return;
    }

    if (hasDocumentLoaded(id, fv)) {
      return;
    }

    if (loadedDocument != null) {
      loadedDocument.dispose();
    }

    Object o    = row.get(3).getValue();
    Date   date = (o instanceof Date)
                  ? (Date) o
                  : null;

    try {
      String   order = row.get(1).getValue().toString();
      URL      url   = table.getURL("order/" + id + ".html");
      Document doc   = new Document(table, new ActionLink(url), id);

      loadedDocument = doc;
      doc.setMainDocumentInfo(date, order);

      iContainer dv = (iContainer) fv.getWidget("documentViewer");

      if (dv == null) {
        ActionLink link = new ActionLink(fv, "/document_viewer.rml");

        link.setTargetName(iTarget.TARGET_WORKSPACE);
        doc.loadAndPopulateViewer(link);
      } else {
        doc.loadAndPopulateViewer(dv);
      }
    } catch(Exception e) {
      Utils.handleError(e);
    }
  }

  protected void clearForm(iContainer fv) {
    iWidget field;

    try {
      field = fv.getWidget("documentDate");

      if (field != null) {
        field.clearContents();
      }

      field = fv.getWidget("documentTitle");

      if (field != null) {
        field.clearContents();
      }

      field = fv.getWidget("documentDetail");

      if (field != null) {
        field.clearContents();
      }
    } catch(Exception e) {
      Utils.handleError(e);
    }

    if (loadedDocument != null) {
      loadedDocument.dispose();
      loadedDocument = null;
    }
  }

  @Override
  protected void dataParsed(iWidget widget, final List<RenderableDataItem> rows, final ActionLink link) {
    originalRows = rows;
    tableDataLoaded(link);

    final TableViewer table = (TableViewer) widget;

    appendDirections = (table.getColumnCount() < 3)
                       || (table.getColumn(2).getRenderDetail() == Column.RenderDetail.ICON_ONLY);
    hasNoData       = false;
    categorizedMeds = null;

    if (checkAndHandleNoData(table, rows)) {
      if (Utils.isCardStack()) {
        LabelWidget tapLabel = (LabelWidget) table.getFormViewer().getWidget("tapLabel");

        tapLabel.setValue(Platform.getResourceAsString("bv.text.no_" + namePrefix));
        tapLabel.setVerticalAlignment(VerticalAlign.CENTER);
      } else {
        updateCategories(table.getFormViewer(), null, -1);
      }
      return;
    }

    final WindowViewer w = Platform.getWindowViewer();

    w.spawn(new aWorkerTask() {
      @Override
      public void finish(Object result) {
        w.hideWaitCursor();

        if (result != null) {
          Utils.handleError((Throwable) result);
        }
      }
      @Override
      public Object compute() {
        try {
          processData(table, rows);
          return null;
        } catch(Exception e) {
          return e;
        }
      }
    });
    w.showWaitCursor();
  }

  /**
   * Filters the table based on the passed in filter category filter
   *
   * @param table
   *          the table
   * @param filter
   *          the filter or null to show all orders
   */
  protected void filterTable(TableViewer table, final String filter) {
    if (filter == null) {
      if (categorizedMedsLoaded) {
        categorizedMedsLoaded = false;
        updateTable(table, allOrders);
      } else {
        if (table.isEmpty()) {
          updateTable(table, allOrders);
        } else {
          table.unfilter();
        }
      }
    } else if (filter.equals(cmedsCategoryID)) {
      if (!categorizedMedsLoaded) {
        categorizedMedsLoaded = true;
        updateTable(table, categorizedMeds);
      }
    } else {
      if (categorizedMedsLoaded || table.isEmpty()) {
        categorizedMedsLoaded = false;
        updateTable(table, allOrders);
      } else {
        table.unfilter();
      }

      table.filter(0, filter, false);
    }
  }

  /**
   * Populates the card stack form with active orders
   *
   * @param fv
   *          the form container
   * @param rows
   *          the rows containing the orders
   */
  protected void populateCardStack(iContainer fv, List<RenderableDataItem> rows) {
    int        len       = rows.size();
    iContainer itemsForm = (iContainer) fv.getWidget("itemsForm");
    int        count     = Math.min(itemsForm.getWidgetCount() / 2, len);
    int        n         = 0;

    for (int i = 0; i < count; i++) {
      RenderableDataItem row    = rows.get(i);
      RenderableDataItem name   = row.get(NAME_POSITION);
      RenderableDataItem item   = row.getItemEx(STATUS_COLUMN);
      String             status = (item == null)
                                  ? ""
                                  : item.toString();
      LabelWidget        nl     = (LabelWidget) itemsForm.getWidget(n++);
      LabelWidget        sl     = (LabelWidget) itemsForm.getWidget(n++);

      if (name.getForeground() != null) {
        nl.setForeground(name.getForeground());
        sl.setForeground(name.getForeground());
      }

      nl.setValue(name);
      sl.setValue(status);
    }

    if (count < len) {
      String s = Platform.getWindowViewer().getString("bv.format.tap_to_see_more", StringCache.valueOf(count),
                   StringCache.valueOf(len));
      iWidget tapLabel = fv.getWidget("tapLabel");

      tapLabel.setValue(s);
    }
  }

  @SuppressWarnings("resource")
  protected void processData(final TableViewer table, List<RenderableDataItem> rows) {
    final ActionPath   path     = Utils.getActionPath(true);
    final WindowViewer w        = Platform.getWindowViewer();
    IntList            toDelete = new IntList();

    if (categorizedMeds != null) {
      categorizedMeds.clear();
    }

    if (categorizedMeds != null) {
      categorizedMeds.clear();
    }

    categorizedMeds       = null;
    allOrders             = null;
    categorizedMedsLoaded = false;

    if (table.isDisposed()) {
      return;
    }

    int                            len = rows.size();
    RenderableDataItem             row, category;
    CharArray                      ca   = new CharArray();
    String                         name = null;
    String                         statusColor;
    String                         s;
    final List<RenderableDataItem> categories    = new ArrayList<RenderableDataItem>();
    HashSet                        categorySet   = new HashSet();
    iPlatformIcon                  icon          = Platform.getResourceAsIcon("bv.icon.dash");
    Map                            orderStatuses = OrderManager.getOrderStatuses(false);
    boolean                        hasCC         = hasClinicalCategories;
    boolean                        hasMeds       = false;
    UIFont                         strikeThrough = null;
    boolean                        cardstack     = Utils.isCardStack();
    int                            rowSize;
    int                            istatus;
    JSONObject                     order;
    JSONObject                     demo = OrderManager.getDemoOrderObject(false);

    for (int i = 0; i < len; i++) {
      row         = rows.get(i);
      rowSize     = row.size();
      statusColor = null;
      istatus     = -1;
      RenderableDataItem item0 = row.get(0);    //date
      RenderableDataItem item1 = row.get(1);    //name
      RenderableDataItem item2 = row.get(2);    //start date; we will replace with the SIG
      String             sig   = "";

      if (rowSize > SIG_COLUMN) {
        sig = (String) row.get(SIG_COLUMN).getValue();
      }

      item2.setValue(sig);

      String orderID = (String) item0.getLinkedData();
      String status  = null;

      order = (demo == null)
              ? null
              : demo.getJSONObject(orderID);

      if ((row.size() > Orders.STATUS_COLUMN)) {
        s = row.get(Orders.STATUS_COLUMN).toString();

        if ((s != null) && (statusColors != null)) {
          statusColor = statusColors.optString(s, null);
        }

        status = s;
      }

      if (!cardstack) {
        if (order != null) {
          istatus = OrderManager.getDemoOrderStatus(order);
        }

        if (istatus == -1) {
          istatus = 0;

          if ((rowSize > SIGNED_COLUMN) && "false".equals(row.get(SIGNED_COLUMN).toString())) {
            istatus |= UNSIGNED_FLAG;

            if ((statusColor == null) && (statusColors != null)) {
              statusColor = statusColors.optString("Unsigned", null);
            }
          }

          if ((rowSize > FLAGGED_COLUMN) && "true".equals(row.get(FLAGGED_COLUMN).toString())) {
            istatus |= FLAGGED_FLAG;

            if ((statusColor == null) && (statusColors != null)) {
              statusColor = statusColors.optString("Flagged", null);
            }
          }

          if (status != null) {
            if (holdableStatus.indexOf(status) != -1) {
              if ((istatus & UNSIGNED_FLAG) == 0 || (holdableStatus.indexOf("Unsigned") != -1)) {
                istatus |= HOLDABLE_FLAG;
              }
            }

            if (stopableStatus.indexOf(status) != -1) {
              istatus |= STOPABLE_FLAG;
            }

            if (status.equals(holdableStatus)) {
              istatus |= ONHOLD_FLAG;
            }
          }
        }

        updateStatusIcon(row, item0, istatus);
      }

      name = item1.toString();

      if (appendDirections) {
        ca._length = 0;
        ca.append("<html>");

        if (statusColor != null) {
          ca.append("<font color='").append(statusColor).append("'>");
        }

        ca.append(name);
        ca.append((statusColor == null)
                  ? startHtml
                  : startHtmlSC);
        ca.append(sig);
        ca.append((statusColor == null)
                  ? endHtml
                  : endHtmlSC);

        if (statusColor != null) {
          ca.append("</font>");
        }

        ca.append("</html>");

        if (cardstack) {
          CardStackUtils.setItemText(row, ca.toString());
        } else {
          item1.setValue(ca.toString());
        }
      }

      String type = item0.toString();

      if (!cardstack) {
        if (hasCC) {
          RenderableDataItem cc = row.get(CLINICAL_CATEGORY_POSITION);

          s = cc.toString();

          if (s.length() == 0) {
            if (medsCategoryID.equals(type)) {
              cc.setValue(missingClinicalCategoryTitle);
            }
          }
        }

        s = row.get(CATEGORY_NAME_POSITION).toString();

        if (s.length() == 0) {
          if (medsCategoryID.equals(type)) {
            s = medicationsTitle;
          } else if (ivsCategoryID.equals(type)) {
            s = ivsTitle;
          } else {
            s = missingCategoryTitle;
          }

          row.get(CATEGORY_NAME_POSITION).setValue(s);
        }

        if (!categorySet.contains(s)) {
          categories.add(category = new RenderableDataItem(s, type, icon));
          category.setActionListener(this);
          categorySet.add(s);

          if (orderID.length() == 0) {
            category.setIcon(Platform.getResourceAsIcon("bv.icon.download"));
            category.setEditable(true);
            toDelete.add(i);
          }
        }

        // check for orders that were discontinued by this user but not yet submitted and strike them through
        if (orderStatuses != null) {
          if (OrderManager.isDiscontinued(orderID, orderStatuses)) {
            if (strikeThrough == null) {
              strikeThrough = table.getFont().deriveItalic().deriveStrikethrough();
            }

            istatus |= STOPPED_FLAG;
            row.setFont(strikeThrough);

            String c = statusColors.optString("Discontinued", null);

            if (c == null) {
              c = statusColors.optString("Stopped", null);
            }

            if (c != null) {
              statusColor = c;
            }
          }
        }
      }

      row.setUserStateFlag((byte) istatus);

      if (statusColor != null) {
        row.setForeground(w.getColor(statusColor));
      }
    }

    boolean hasUnloadedCategories = toDelete._length > 0;

    if (hasUnloadedCategories) {
      for (int i = toDelete._length - 1; i > -1; i--) {
        rows.remove(toDelete.get(i));
      }
    }

    if (cardstack) {
      dataLoaded = true;
      allOrders  = rows;
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          updateTable(table, allOrders);
        }
      });
    } else {
      Collections.sort(categories);
      allOrders = rows = Utils.groupRows(table, rows, CATEGORY_NAME_POSITION, -1, false);

      if (hasClinicalCategories) {
        RenderableDataItem ivs  = null;
        RenderableDataItem meds = null;

        len = rows.size();

        for (int i = 0; i < len; i++) {
          row = rows.get(i);                         // group item

          RenderableDataItem item = row.get(0);      // first column contains the list is children

          item = item.get(0);                        // first row
          s    = (String) item.get(0).getValue();    // value from first column

          if (s.equals(ivsCategoryID)) {
            ivs = row;
          } else if (s.equals(medsCategoryID)) {
            meds = row.get(0);
          }
        }

        if (meds != null) {
          categorizedMeds = Utils.groupRows(table, meds.getItems(), CLINICAL_CATEGORY_POSITION, -1, false);

          SubItemComparator c = new SubItemComparator();

          c.setOptions(0, false);
          Collections.sort(categorizedMeds, c);
        }

        if (ivs != null) {
          if (categorizedMeds != null) {
            categorizedMeds.add(0, ivs);
          } else {
            categorizedMeds = new ArrayList<RenderableDataItem>(1);
            categorizedMeds.add(ivs);
          }
        }

        if ((categorizedMeds != null) && categorizedMeds.isEmpty()) {
          categorizedMeds = null;
        }

        if (categorizedMeds != null) {
          categories.add(0, category = new RenderableDataItem(categorizedMedsTitle, cmedsCategoryID, icon));
          category.setActionListener(this);
        }
      } else {
        int pos = RenderableDataItem.findLinkedObjectIndex(categories, medsCategoryID);

        if (pos != -1) {
          category = categories.remove(pos);
          categories.add(0, category);
          hasMeds = true;
        }
      }

      if (!hasUnloadedCategories) {
        categories.add(0, category = new RenderableDataItem(w.getString("bv.text.all_orders"), null, icon));
        category.setActionListener(this);
      }

      if (!hasMeds) {
        int pos = RenderableDataItem.findLinkedObjectIndex(categories, medsCategoryID);

        if (pos == -1) {
          categories.add(0, category = new RenderableDataItem(medicationsTitle, medsCategoryID, icon));
          category.setActionListener(this);
        } else {
          category = categories.get(pos);
        }
        allOrders.addAll(createNotDataForCategory(table, category));
      }

      final int selectedIndex;

      if ((categorizedMeds != null) || hasMeds) {
        selectedIndex = hasUnloadedCategories
                        ? 0
                        : 1;
      } else {
        selectedIndex = 0;
      }

      dataLoaded = true;
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (!table.isDisposed()) {
            updateCategories(table.getFormViewer(), categories, selectedIndex);
          }

          final String key = (path == null)
                             ? null
                             : path.shift();

          if (key != null) {
            handlePathKey(table, key, 0, true);
          }
        }
      });
    }
  }

  @SuppressWarnings("resource")
  protected void processDataEx(iWidget context, List<RenderableDataItem> rows) {
    int                len = rows.size();
    RenderableDataItem row;
    CharArray          ca   = new CharArray();
    String             name = null;
    String             statusColor;
    String             s;
    Map                orderStatuses = OrderManager.getOrderStatuses(false);
    UIFont             strikeThrough = null;
    int                rowSize;
    int                istatus;
    JSONObject         order;
    JSONObject         demo = OrderManager.getDemoOrderObject(false);

    for (int i = 0; i < len; i++) {
      row         = rows.get(i);
      rowSize     = row.size();
      statusColor = null;
      istatus     = -1;

      RenderableDataItem item0   = row.get(0);
      RenderableDataItem item1   = row.get(1);
      RenderableDataItem item2   = row.get(2);
      String             orderID = (String) item0.getLinkedData();
      String             status  = null;

      order = (demo == null)
              ? null
              : demo.getJSONObject(orderID);

      if ((row.size() > Orders.STATUS_COLUMN)) {
        s = row.get(Orders.STATUS_COLUMN).toString();

        if ((s != null) && (statusColors != null)) {
          statusColor = statusColors.optString(s, null);
        }

        status = s;
      }
      s = row.get(CATEGORY_NAME_POSITION).toString();
      String type = item0.toString();

      if (s.length() == 0) {
        if (medsCategoryID.equals(type)) {
          s = medicationsTitle;
        } else if (ivsCategoryID.equals(type)) {
          s = ivsTitle;
        } else {
          s = missingCategoryTitle;
        }

        row.get(CATEGORY_NAME_POSITION).setValue(s);
      }

      if (order != null) {
        istatus = OrderManager.getDemoOrderStatus(order);
      }

      if (istatus == -1) {
        istatus = 0;

        if ((rowSize > SIGNED_COLUMN) && "false".equals(row.get(SIGNED_COLUMN).toString())) {
          istatus |= UNSIGNED_FLAG;

          if ((statusColor == null) && (statusColors != null)) {
            statusColor = statusColors.optString("Unsigned", null);
          }
        }

        if ((rowSize > FLAGGED_COLUMN) && "true".equals(row.get(FLAGGED_COLUMN).toString())) {
          istatus |= FLAGGED_FLAG;

          if ((statusColor == null) && (statusColors != null)) {
            statusColor = statusColors.optString("Flagged", null);
          }
        }

        if (status != null) {
          if (holdableStatus.indexOf(status) != -1) {
            if ((istatus & UNSIGNED_FLAG) == 0 || (holdableStatus.indexOf("Unsigned") != -1)) {
              istatus |= HOLDABLE_FLAG;
            }
          }

          if (stopableStatus.indexOf(status) != -1) {
            istatus |= STOPABLE_FLAG;
          }

          if (status.equals(holdableStatus)) {
            istatus |= ONHOLD_FLAG;
          }
        }
      }

      updateStatusIcon(row, item0, istatus);

      String sig = (String) item2.getValue();

      if (((sig == null) || (sig.length() == 0)) && (rowSize > SIG_COLUMN)) {
        sig = (String) row.get(SIG_COLUMN).getValue();
        item2.setValue(sig);
      }

      name = item1.toString();

      if (appendDirections) {
        ca._length = 0;
        ca.append("<html>");

        if (statusColor != null) {
          ca.append("<font color='").append(statusColor).append("'>");
        }

        ca.append(name);
        ca.append((statusColor == null)
                  ? startHtml
                  : startHtmlSC);
        ca.append(sig);
        ca.append((statusColor == null)
                  ? endHtml
                  : endHtmlSC);

        if (statusColor != null) {
          ca.append("</font>");
        }

        ca.append("</html>");
        item1.setValue(ca.toString());
      }

      // check for orders that were discontinued by this user but not yet submitted and strike them through
      if (orderStatuses != null) {
        if (OrderManager.isDiscontinued(orderID, orderStatuses)) {
          if (strikeThrough == null) {
            strikeThrough = context.getFont().deriveItalic().deriveStrikethrough();
          }

          istatus |= STOPPED_FLAG;
          row.setFont(strikeThrough);

          String c = statusColors.optString("Discontinued", null);

          if (c == null) {
            c = statusColors.optString("Stopped", null);
          }

          if (c != null) {
            statusColor = c;
          }
        }
      }

      row.setUserStateFlag((byte) istatus);

      if (statusColor != null) {
        row.setForeground(UIColorHelper.getColor(statusColor));
      }
    }
  }

  @Override
  protected void reset() {
    super.reset();
    currentView = ResultsView.DOCUMENT;

    if (categorizedMeds != null) {
      categorizedMeds.clear();
    }

    if (categorizedMeds != null) {
      categorizedMeds.clear();
    }

    categorizedMeds       = null;
    allOrders             = null;
    categorizedMedsLoaded = false;
  }

  /**
   * Updates the categories widget with a list of order categories
   *
   * @param fv
   *          the form that contains the categories widget
   * @param categories
   *          the list of categories
   * @param selectedIndex
   */
  protected void updateCategories(iFormViewer fv, List<RenderableDataItem> categories, int selectedIndex) {
    aWidget cw = (aWidget) fv.getWidget("categories");

    if (cw != null) {
      cw.clear();

      if ((categories == null) || categories.isEmpty()) {
        cw.setEnabled(false);
        cw.setValue(null);
      } else if (cw instanceof aWidget) {
        cw.setEnabled(true);

        if (selectedIndex == -1) {
          selectedIndex = 0;
        }

        cw.addAll(categories);

        if (cw instanceof PushButtonWidget) {
          PushButtonWidget pb = (PushButtonWidget) cw;

          pb.setPopupScrollable(true);
          pb.setSelectedIndex(selectedIndex);
        } else if (cw instanceof iListHandler) {
          ((iListHandler) cw).setSelectedIndex(selectedIndex);
        }

        RenderableDataItem item = categories.get(selectedIndex);

        filterTable(dataTable, (String) item.getLinkedData());
      }

      cw.update();
    }
  }

  /**
   * Updates the table with the specified rows
   *
   * @param table
   *          the table
   * @param rows
   *          the rows
   */
  protected void updateTable(TableViewer table, List<RenderableDataItem> rows) {
    table.clear();
    table.handleGroupedCollection(rows, true);
    table.finishedLoading();
  }

  protected List<RenderableDataItem> createNotDataForCategory(TableViewer table, RenderableDataItem item) {
    List<RenderableDataItem> rows = new ArrayList<RenderableDataItem>(1);
    String                   cat  = (String) item.getLinkedData();
    String                   name = item.toString();
    String                   msg  = Platform.getWindowViewer().getString("bv.format.no_orders_found_for_category",
                                      name);
    RenderableDataItem       row;

    rows.add(row = createNoDataRow(table, msg));
    row.get(0).setValue(cat);
    row.addMissingColumns(CATEGORY_NAME_POSITION + 1);
    row.get(CATEGORY_NAME_POSITION).setValue(item.getValue());
    rows = Utils.groupRows(table, rows, CATEGORY_NAME_POSITION, -1, false);

    return rows;
  }

  public static String getOrderDirections(RenderableDataItem orderItem) {
    String sig = (String) orderItem.get(2).getValue();

    if (((sig == null) || (sig.length() == 0)) && (orderItem.size() > SIG_COLUMN)) {
      sig = (String) orderItem.get(SIG_COLUMN).getValue();
    }

    return sig;
  }

  public static RenderableDataItem getOrderedItem(RenderableDataItem row) {
    return row.get(1);
  }

  public static int indexOfOrder(TableViewer table, String id) {
    int len = table.size();

    for (int i = 0; i < len; i++) {
      if (id.equals(table.get(i).get(0).getLinkedData())) {
        return i;
      }
    }

    return -1;
  }

  public static void updateStatusIcon(RenderableDataItem row, RenderableDataItem item, int istatus) {
    iPlatformIcon icon  = null;
    String        color = null;

    if ((istatus & UNSIGNED_FLAG) != 0) {
      color = statusColors.optString("Unsigned", null);

      if ((istatus & FLAGGED_FLAG) != 0) {
        if (color != null) {
          color = statusColors.optString("Flagged", null);
        }

        icon = flaggedAndUnsignedIcon;
      } else {
        icon = unsignedIcon;
      }
    } else if ((istatus & FLAGGED_FLAG) != 0) {
      color = statusColors.optString("Flagged", null);

      if ((istatus & ONHOLD_FLAG) != 0) {
        icon = flaggedAndHoldIcon;

        if (color != null) {
          color = statusColors.optString(holdStatusValue, null);
        }
      } else {
        icon = flaggedIcon;
      }
    } else if ((istatus & ONHOLD_FLAG) != 0) {
      icon  = holdIcon;
      color = statusColors.optString(holdStatusValue, null);
    }

    item.setIcon(icon);

    if (color != null) {
      row.setForeground(UIColorHelper.getColor(color));
    } else {
      row.setForeground(null);
    }
  }

  /**
   * Handles a card stack drill down action
   */
  protected class OrdersStackActionListener implements iActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      StackPaneViewer sp = CardStackUtils.createListItemsOrPageViewer(null, dataTable.getFormViewer(), allOrders, -1,
                             1, null, false, true);

      Utils.pushWorkspaceViewer(sp, false);
    }
  }
}
