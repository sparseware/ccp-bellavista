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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.appnativa.rare.Platform;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.Column;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.iListHandler;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.util.SubItemComparator;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.widget.ComboBoxWidget;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.CharArray;
import com.appnativa.util.json.JSONObject;

/**
 * This class handles lab values. The data is assumed to be returned is reverse
 * chronological order (newest values first). The middleware should enforce this
 * constraint.
 * 
 * @author Don DeCoteau
 */
public class Orders extends aResultsManager implements iActionListener {
  public static final int            CATEGORY_NAME_POSITION     = 13;
  public static final int            CLINICAL_CATEGORY_POSITION = 14;
  public static int                  SIG_COLUMN                 = 10;
  public static int                  STATUS_COLUMN              = 8;
  public static int                  SUMMARY_STATUS_COLUMN      = 1;
  protected int                      categorySortPosition       = -1;
  protected boolean                  hasClinicalCategories;
  protected JSONObject               statusColors;
  protected String                   endHtml;
  protected String                   startHtml;
  protected String                   endHtmlSC;
  protected String                   startHtmlSC;
  protected boolean                  appendDirections;
  protected boolean                  includeIVsInCategorizedMeds;
  protected String                   categorizedMedsTitle;
  protected String                   missingClinicalCategoryTitle;
  protected String                   missingCategoryTitle;
  protected String                   ivsCategoryID;
  protected String                   medsCategoryID;
  protected String                   cmedsCategoryID            = "_c_meds_";
  protected List<RenderableDataItem> categorizedMeds;
  protected boolean                  categorizedMedsLoaded;
  protected List<RenderableDataItem> allOrders;

  public Orders() {
    super("orders", "Orders");
    currentView = ResultsView.DOCUMENT;
    JSONObject info = (JSONObject) Platform.getAppContext().getData("ordersInfo");
    hasClinicalCategories = info.optBoolean("hasClinicalCategories", false);
    includeIVsInCategorizedMeds = info.optBoolean("includeIVsInCategorizedMeds", true);
    categorizedMedsTitle = info.optString("categorizedMedsTitle", "Medications (Categorized)");
    missingClinicalCategoryTitle = info.optString("missingClinicalCategoryTitle", "unclassified medications");
    missingCategoryTitle = info.optString("missingCategoryTitle", "Misc. Orders");
    medsCategoryID = info.optString("medsCategoryID", "meds");
    ivsCategoryID = info.optString("ivsCategoryID", "ivs");

    statusColors = info.optJSONObject("statusColors");
    endHtml = info.optString("directionsHtmlEnd", "");
    startHtml = info.optString("directionsHtmlStart", "");
    endHtmlSC = info.optString("directionsStatusColorHtmlEnd", endHtml);
    startHtmlSC = info.optString("directionsStatusColorHtmlStart", startHtml);
    if (startHtml == null) {
      startHtml = "";
    }

    if (endHtml == null) {
      endHtml = "";
    }

  }

  @Override
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    super.onDispose(eventName, widget, event);
    categorizedMeds = null;
    allOrders = null;
  }

  /**
   * The action handler for the the user click on a categories menu. This class
   * is set as the action listener for the menu items.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    iWidget cw = e.getWidget();
    RenderableDataItem item = null;
    if (cw instanceof PushButtonWidget) {
      item = ((PushButtonWidget) cw).getSelectedItem();
    } else if (cw instanceof ComboBoxWidget) {
      item = ((ComboBoxWidget) cw).getSelectedItem();
    }
    if (item != null) {
      final String cat = (String) item.getLinkedData();
      filterTable(dataTable, cat);
    }
  }

  public void onSummaryTableAction(String eventName, iWidget widget, EventObject event) {
    String key = ((TableViewer) widget).getSelectionDataAsString(0);
    ActionPath path = new ActionPath("orders", key);
    Utils.handleActionPath(path);
  }

  @Override
  public void onTableAction(String eventName, iWidget widget, EventObject event) {
    final TableViewer table = (TableViewer) widget;
    final RenderableDataItem row = table.getSelectedItem();
    final String id = (row == null) ? null : (String) row.get(0).getLinkedData();
    if (id == null) {
      iFormViewer fv = widget.getFormViewer();
      iContainer dv = (iContainer) fv.getWidget("documentViewer");
      clearForm(dv == null ? fv : dv);
      return;
    }
    Object o = row.get(3).getValue();
    Date date = (o instanceof Date) ? (Date) o : null;
    try {
      String order = row.get(1).getValue().toString();
      URL url = table.getURL("order/" + id + ".html");
      Document doc = new Document(table, new ActionLink(url), id);
      doc.setMainDocumentInfo(date, order);
      iFormViewer fv = widget.getFormViewer();
      iContainer dv = (iContainer) fv.getWidget("documentViewer");
      if (dv == null) {
        ActionLink link = new ActionLink(fv, "/document_viewer.rml");
        link.setTargetName(iTarget.TARGET_WORKSPACE);
        doc.loadAndPopulateViewer(link);
      } else {
        doc.loadAndPopulateViewer(dv);
      }
    } catch (Exception e) {
      Utils.handleError(e);
    }
  }

  @Override
  protected void dataParsed(iWidget widget, final List<RenderableDataItem> rows, ActionLink link) {
    originalRows = rows;
    final TableViewer table = (TableViewer) widget;
    appendDirections = (table.getColumnCount() < 3) || (table.getColumn(2).getRenderDetail() == Column.RenderDetail.ICON_ONLY);
    table.setWidgetDataLink(link);

    int len = rows.size();

    hasNoData = false;
    categorizedMeds = null;

    if ((len == 1) && !rows.get(0).isEnabled()) {
      Utils.getActionPath(true);
      hasNoData = true;
      table.addParsedRow(rows.get(0));
      table.finishedParsing();
      table.finishedLoading();
      dataLoaded = true;
      updateCategories(table.getFormViewer(), null, -1);
      return;
    }
    Platform.getWindowViewer().showWaitCursor();
    Platform.getAppContext().executeBackgroundTask(new Runnable() {

      @Override
      public void run() {
        processData(table, rows);
      }
    });
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

  @SuppressWarnings("resource")
  protected void processData(final TableViewer table, List<RenderableDataItem> rows) {
    final ActionPath path = Utils.getActionPath(true);

    final WindowViewer w = Platform.getWindowViewer();
    try {
      if (table.isDisposed()) {
        return;
      }
      int len = rows.size();
      RenderableDataItem item, row, category;
      CharArray ca = new CharArray();
      String name = null;
      String statusColor;
      UIColor statusColorC;
      String s;
      UIFont defaultFont = null;
      final List<RenderableDataItem> categories = new ArrayList<RenderableDataItem>();
      HashSet categorySet = new HashSet();
      iPlatformIcon icon = Platform.getResourceAsIcon("bv.icon.dash");
      Map orderStates = (Map) Platform.getAppContext().getData("pt_orderStates");
      boolean hasCC = hasClinicalCategories;
      boolean hasMeds=false;
      if (orderStates != null) {
        defaultFont = table.getFont();
      }
      for (int i = 0; i < len; i++) {
        row = rows.get(i);
        statusColor = null;
        statusColorC = null;

        if ((row.size() > Orders.STATUS_COLUMN)) {
          s = row.get(Orders.STATUS_COLUMN).toString();

          if ((s != null) && (statusColors != null)) {
            statusColor = statusColors.optString(s, null);
            if(statusColor!=null) {
              statusColorC=w.getColor(statusColor);
            }
          }
        }

        item = row.get(2);

        String sig = (String) item.getValue();

        if (((sig == null) || (sig.length() == 0)) && (row.size() > Orders.SIG_COLUMN)) {
          sig = (String) row.get(Orders.SIG_COLUMN).getValue();
          item.setValue(sig);
          if(statusColorC!=null) {
            item.setForeground(statusColorC);
          }
        }

        item = row.get(1);
        name = item.toString();

        if (appendDirections) {
          ca._length = 0;
          ca.append("<html>");

          if (statusColor != null) {
            ca.append("<font color='").append(statusColor).append("'>");
          }

          ca.append(name);
          ca.append((statusColor == null) ? startHtml : startHtmlSC);
          ca.append(sig);
          ca.append((statusColor == null) ? endHtml : endHtmlSC);

          if (statusColor != null) {
            ca.append("</font>");
          }

          ca.append("</html>");
          item.setValue(ca.toString());
        } else if (statusColorC != null) {
          item.setForeground(statusColorC);
        }
        String type=row.get(0).toString();

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
          s = missingCategoryTitle;
          row.get(CATEGORY_NAME_POSITION).setValue(s);
        }
        if (!categorySet.contains(s)) {
          categories.add(category = new RenderableDataItem(s, type, icon));
          category.setActionListener(this);
          categorySet.add(s);
        }
        // check for orders that were discontinued by this user but not yet submitted and strike them through
        if (orderStates != null) {
          RenderableDataItem item0 = row.get(0);


          Map map = (Map) orderStates.get(type);
          UIFont strikeThrough = null;

          if (map != null) {
            Integer state = (Integer) map.get(item0.getLinkedData());

            if ((state != null) && (state & 4) != 0) {
              if (defaultFont == null) {
                defaultFont = w.getFont();
              }

              if (strikeThrough == null) {
                strikeThrough = defaultFont.deriveStrikethrough();
              }

              item.setFont(strikeThrough);
            }
          }
        }
      }
      Collections.sort(categories);
      allOrders = rows = Utils.groupRows(table, rows, CATEGORY_NAME_POSITION, -1, false);
      if (hasClinicalCategories) {
        RenderableDataItem ivs = null;
        RenderableDataItem meds = null;
        len = rows.size();
        for (int i = 0; i < len; i++) {
          row = rows.get(i); // group item
          item = row.get(0); // first column contains the list is children
          item = item.get(0); // first row
          s = (String) item.get(0).getValue(); // value from first column
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
        if (categorizedMeds != null && categorizedMeds.isEmpty()) {
          categorizedMeds = null;

        }
        if(categorizedMeds!=null) {
          categories.add(0, category = new RenderableDataItem(categorizedMedsTitle, cmedsCategoryID, icon));
          category.setActionListener(this);
        }
      } else  {
        int pos=RenderableDataItem.findLinkedObjectIndex(categories, medsCategoryID);
        if(pos!=-1) {
          category=categories.remove(pos);
          categories.add(0, category);
          hasMeds=true;
        }
      }

      categories.add(0, category = new RenderableDataItem(w.getString("bv.text.all_orders"), null, icon));
      category.setActionListener(this);
      final int selectedIndex;
      if (categorizedMeds != null || hasMeds) {
        selectedIndex = 1;
      } else {
        selectedIndex = 0;
      }
      dataLoaded = true;
      Platform.invokeLater(new Runnable() {

        @Override
        public void run() {
          if (!table.isDisposed()) {
            filterTable(table, categorizedMeds != null ? cmedsCategoryID : medsCategoryID);
            updateCategories(table.getFormViewer(), categories, selectedIndex);
          }
          final String key = path == null ? null : path.shift();
          if (key != null) {
            handlePathKey(table, key, 0, true);
          }
        }
      });
    } catch (final Exception e) {
      Utils.handleError(e);
    } finally {
      w.hideWaitCursor();
    }
  }

  protected void reset() {
    super.reset();
    currentView = ResultsView.DOCUMENT;
    if (categorizedMeds != null) {
      categorizedMeds.clear();
    }
    if (categorizedMeds != null) {
      categorizedMeds.clear();
    }
    categorizedMeds = null;
    allOrders = null;
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
    if (categories == null || categories.isEmpty()) {
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

  void clearForm(iContainer fv) {
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
    } catch (Exception e) {
      Utils.handleError(e);
    }

  }

}
