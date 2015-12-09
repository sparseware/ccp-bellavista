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
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.spot.Region;
import com.appnativa.rare.spot.SplitPane;
import com.appnativa.rare.spot.Table;
import com.appnativa.rare.spot.Widget;
import com.appnativa.rare.ui.ColorUtils;
import com.appnativa.rare.ui.Column;
import com.appnativa.rare.ui.FontUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.RenderableDataItem.IconPosition;
import com.appnativa.rare.ui.ScreenUtils;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.ViewerCreator;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.ui.renderer.UIFormsLayoutRenderer;
import com.appnativa.rare.util.DataItemParserHandler;
import com.appnativa.rare.viewer.GridPaneViewer;
import com.appnativa.rare.viewer.SplitPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.ToolBarViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.ComboBoxWidget;
import com.appnativa.rare.widget.NavigatorWidget;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.aGroupableButton;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.Base64;
import com.appnativa.util.Helper;
import com.appnativa.util.MutableInteger;
import com.appnativa.util.SimpleDateFormatEx;
import com.appnativa.util.iFilter;
import com.appnativa.util.iStringConverter;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.Document.DocumentItem;

/**
 * This class handles lab values. The data is assumed to be returned is reverse
 * chronological order (newest values first). The middleware should enforce this
 * constraint.
 *
 * @author Don DeCoteau
 */
public class Labs extends aResultsManager implements iActionListener, iValueChecker {
  public static int                                 CATEGORY_NAME_POSITION = 6;
  public static int                                 IS_DOCUMENT_POSITION   = 5;
  public static int                                 SORT_ORDER_POSITION    = 8;
  public static int                                 UNIT_POSITION          = 3;
  private static int                                RESULT_ID_POSITION     = 9;
  private static int                                COMMENT_POSITION       = 10;
  static LinkedHashMap<String, Map<String, String>> trendMap;
  protected boolean                                 bunFound;
  protected boolean                                 creatFound;
  protected String                                  bunID;
  protected String                                  creatineID;
  protected ClinicalValue                           bunValue;
  protected ClinicalValue                           creatinineValue;
  protected boolean                                 isSummary;
  protected TrendPanel[]                            trendPanels;
  protected boolean                                 overViewLoaded;
  protected Map<String, String>                     trendsLayout;
  protected boolean                                 uniqueSummaryEntries = true;
  protected boolean                                 documentsInlined;
  protected boolean                                 sortCategoriesOnLinkedData;
  protected iPlatformIcon                           pageIcon;
  protected iPlatformIcon                           noteIcon;
  String                                            seeReport;
  private Document                                  loadedDocument;
  UIColor                                           unknowResultColor;
  String                                            collectionInfoKey;
  HashMap<Date, RenderableDataItem>                 collectionInfoMap;
  boolean                                           showUnits;
  StringBuilder                                     temp = new StringBuilder();
  public Labs() {
    super("labs", "Labs");
    trendMap = new LinkedHashMap<String, Map<String, String>>(4);

    iPlatformAppContext app     = Platform.getAppContext();
    JSONObject          patient = (JSONObject) app.getData("patient");

    bunValue        = (ClinicalValue) patient.opt("cv_bun");
    creatinineValue = (ClinicalValue) patient.opt("cv_creatinine");
    bunID           = (bunValue == null)
                      ? null
                      : bunValue.getID();
    creatineID      = (bunValue == null)
                      ? null
                      : creatinineValue.getID();

    JSONObject info = (JSONObject) app.getData("labsInfo");

    trendPanels                = createTrendPanels(info.optJSONArray("trends"), false);
    itemCounts                 = new LinkedHashMap<String, MutableInteger>();
    itemDatesSet               = new LinkedHashSet<Date>();
    dataPageSize               = info.optInt("dataPageSize", dataPageSize);
    documentsInlined           = info.optBoolean("documentsInlined", false);
    sortCategoriesOnLinkedData = info.optBoolean("documentsInlined", true);
    showUnits                  = info.optBoolean("showUnits", false);
    collectionInfoKey          = info.optString("collectionInfoKey", null);

    if ((collectionInfoKey != null) && (collectionInfoKey.length() > 0)) {
      collectionInfoMap = new HashMap<Date, RenderableDataItem>();
    } else {
      collectionInfoKey = null;
    }

    if (documentsInlined) {
      seeReport         = Platform.getResourceAsString("bv.text.see_report");
      unknowResultColor = ColorUtils.getColor("unknown");
    }

    spreadSheetPageSize = info.optInt("spreadSheetPageSize", spreadSheetPageSize);

    if (info.optBoolean("hasReferenceRange", false)) {
      RANGE_POSITION = 4;
    }

    if (trendPanels != null) {
      trendsLayout = info.getJSONObject("trendsLayout").getObjectMap();
    }

    currentView           = (UIScreen.isLargeScreen() || Utils.isCardStack())
                            ? ResultsView.TRENDS
                            : ResultsView.CHARTS;
    chartHandler          = new ChartHandler(info.getJSONObject("charts"));
    chartableItemsManager = new ChartableItemsManager();
    pageIcon              = Platform.getResourceAsIcon("bv.icon.document");
    noteIcon              = Platform.getResourceAsIcon("bv.icon.note");
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

    filterTable(item);
  }

  @Override
  public void changeView(String eventName, iWidget widget, EventObject event) {
    String name = ((aGroupableButton) widget).getSelectedButtonName();

    if (name == null) {
      name = widget.getName();
    }

    if (UIScreen.isLargeScreen() && "spreadsheet".equals(name)) {    //if going to the spreadsheet go to same result type
      String key = getSelectedChartableKey();

      if (key != null) {
        keyPath = new ActionPath(key);
      }
    }

    super.changeView(eventName, widget, event);
  }

  /**
   * Called by the summary screen to dispose of the lab object.
   */
  public void onSummaryDispose(String eventName, iWidget widget, EventObject event) {
    if (dataTable == widget) {    //this will be false if we create the labs page is created before summary is disposed
      super.onDispose(eventName, widget, event);
    }                             //else reset() already called so no need to cleanup
  }

  @Override
  public ActionPath getDisplayedActionPath() {
    if (isSummary) {
      return new ActionPath(Utils.getPatientID(), "summary");
    }

    return super.getDisplayedActionPath();
  }

  /**
   * Filters the table base on the specified category filter item
   *
   * @param filterItem
   *          the item to use to filter
   */
  protected void filterTable(RenderableDataItem filterItem) {
    if (filterItem != null) {
      WindowViewer w   = Platform.getWindowViewer();
      final String cat = filterItem.toString();
      boolean      all = filterItem.getLinkedData() == null;
      String       title;

      if (all) {
        title = w.getString("bv.text.lab_test_all");
      } else {
        title = w.getString("bv.format.lab_test", cat);
      }

      TableViewer table;

      if (currentView == ResultsView.SPREADSHEET) {
        table = (TableViewer) w.getViewer("spreadsheetTable");
        table.setColumnTitle(0, title);
      } else {
        table = dataTable;
        table.setColumnTitle(1, title);
      }

      table.clearSelection();

      if (UIScreen.isLargeScreen()) {
        clearCharts(table);
      }
      final List<String> fkeys=chartableItemsManager==null ? null : chartableItemsManager.filteredChartableKeys;
      if(fkeys!=null) {
        fkeys.clear();
      }
      if (all) {
        table.unfilter();

        if (!table.isEmpty()) {
          table.scrollRowToTop(0);
        }
      } else {
        table.filter(new iFilter() {
          @Override
          public boolean passes(Object value, iStringConverter converter) {
            RenderableDataItem row = (RenderableDataItem) value;
            boolean pass;
            String key=null;
            if (currentView == ResultsView.SPREADSHEET) {
              pass= cat.equals(row.getLinkedData());
              if(pass) {
                key = (String) row.get(0).getLinkedData();
              }
            } else {
              RenderableDataItem item = row.getItemEx(CATEGORY_NAME_POSITION);

              pass= (item == null)
                     ? false
                     : item.valueEquals(cat);
              if(pass) {
                key = (String) row.get(NAME_POSITION).getLinkedData();
              }
             }
            if(pass && fkeys!=null) {
              if(key!=null && chartableItemsManager.isChartable(key)) {
                fkeys.add(key);
              }
              
            }
            return pass;
          }
        });

        if (!table.isEmpty()) {
          table.scrollRowToTop(0);
        }
      }
    }
  }

  @Override
  public boolean checkRow(RenderableDataItem row, int index, int expandableColumn, int rowRount) {
    try {
      do {
        RenderableDataItem name = row.getItemEx(NAME_POSITION);
        Date               date = (Date) row.get(DATE_POSITION).getValue();

        if ((name == null) || (date == null)) {
          break;
        }

        String key = (String) name.getLinkedData();

        if (key == null) {
          break;
        }

        RenderableDataItem valueItem = row.getItemEx(IS_DOCUMENT_POSITION);

        if ((valueItem != null) && "true".equals(valueItem.getValue())) {
          valueItem = row.get(VALUE_POSITION);

          if ((collectionInfoKey != null) && collectionInfoKey.equals(key)) {
            collectionInfoMap.put(date, row);

            return false;
          }

          valueItem.setIcon(pageIcon);
          valueItem.setIconPosition(IconPosition.RIGHT_JUSTIFIED);

          if (documentsInlined) {
            valueItem = row.get(VALUE_POSITION);
            row.setLinkedData(valueItem.getValue());
            valueItem.setValue(seeReport);
            valueItem.setForeground(unknowResultColor);
          }
        }

        if ((creatFound && bunFound) && isSummary) {
          break;
        }

        valueItem = row.get(VALUE_POSITION);

        String value = (String) valueItem.getValue();

        if (!isSummary) {
          if (chartableItemsManager.check(key, value)) {
            itemDatesSet.add(date);

            MutableInteger count = itemCounts.get(key);

            if (count == null) {
              count = new MutableInteger(0);
              itemCounts.put(key, count);
            }

            count.incrementAndGet();

            if (trendPanels != null) {
              for (TrendPanel p : trendPanels) {
                if (p.addTrend(key, date, valueItem)) {
                  break;
                }
              }
            }
          }

          if (showUnits) {
            RenderableDataItem unitItem = row.getItemEx(UNIT_POSITION);
            String             unit     = (unitItem == null)
                                          ? ""
                                          : unitItem.toString();

            if (unit.length() > 0) {
              StringBuilder sb = temp;

              sb.setLength(0);
              sb.append(value);

              int n = value.indexOf('(');

              if (n == -1) {
                n = value.indexOf('*');
              }

              if (n == -1) {
                sb.append(' ').append(unit);
              } else {
                sb.insert(n, unit);
                sb.insert(n + unit.length(), ' ');
              }

              valueItem.setValue(sb.toString());
            }
          }
        }

        if (creatFound && bunFound) {
          break;
        }

        if (!bunFound && key.equals(bunID)) {
          bunFound = true;
          date     = (Date) row.get(DATE_POSITION).getValue();
          bunValue.update(date, value);

          if (creatFound) {
            break;
          }
        }

        if (!creatFound && key.equals(creatineID)) {
          creatFound = true;
          bunValue.update(date, value);
        }
      } while(false);
    } catch(Exception e) {
      Platform.ignoreException(null, e);
    }

    return true;
  }

  @Override
  protected void addCurrentPathID(ActionPath path) {
    switch(currentView) {
      case DOCUMENT :
        RenderableDataItem row = dataTable.getSelectedItem();

        if (row != null) {
          String id = (String) row.get(RESULT_ID_POSITION).getValue();

          path.add(id);
        }

        break;

      default :
        break;
    }
  }

  /**
   * Called when the labs data has been loaded into the table. We populate the
   * the categories list and sort the table based on the sort order for the
   * categories that was sent with the data
   */
  public void onFinishedLoading(String eventName, iWidget widget, EventObject event) {
    if (hasNoData) {
      ToolBarViewer tb = (ToolBarViewer) widget.getParent().getWidget("tableToolbar");
      iWidget       cw = (tb == null)
                         ? null
                         : tb.getWidget("spreadsheet");

      if (cw != null) {
        cw.setEnabled(false);
      }

      cw = (tb == null)
           ? null
           : tb.getWidget("categories");

      if (cw != null) {
        cw.setEnabled(false);
      }
    } else {
      WindowViewer w     = Platform.getWindowViewer();
      TableViewer  table = (TableViewer) widget;
      iWidget      cw    = table.getFormViewer().getWidget("categories");

      if (cw instanceof aWidget) {
        RenderableDataItem all   = new RenderableDataItem(w.getString("bv.text.all_labs"));
        RenderableDataItem other = new RenderableDataItem(w.getString("bv.text.other_labs_category"), "zzzzzzzzzzz",
                                     null);
        List<RenderableDataItem> list = Utils.categorize(table, CATEGORY_NAME_POSITION, other,
                                          sortCategoriesOnLinkedData);

        list.add(0, all);

        int           len  = list.size();
        iPlatformIcon icon = Platform.getResourceAsIcon("bv.icon.dash");

        for (int i = 0; i < len; i++) {
          RenderableDataItem item = list.get(i);

          item.setIcon(icon);
          item.setActionListener(this);
        }

        ((aWidget) cw).addAll(list);

        if (cw instanceof PushButtonWidget) {
          ((PushButtonWidget) cw).setPopupScrollable(true);
        }

        cw.update();
      }

      Utils.sortTable(table, SORT_ORDER_POSITION);
      chartableItemsManager.createList(table, NAME_POSITION);

      if (Utils.isCardStack()) {
        if ((trendPanels != null) && (trendPanels.length > 0)) {
          iContainer fv    = (iContainer) table.getFormViewer().getWidget("trends");
          TrendPanel panel = trendPanels[0];

          panel.removePeers();

          String text = panel.popuplateForm(fv);

          CardStackUtils.switchToViewer(table.getParent());
          updateCardStackTitle(panel.title, text);
        }
      }
    }
  }

  /**
   * Called when the lab report viewer has be loaded into a target
   *
   * @param eventName
   * @param widget
   * @param event
   */
  public void onLabReportLoad(String eventName, iWidget widget, EventObject event) {
    GridPaneViewer gp  = (GridPaneViewer) widget;
    Document       doc = (Document) gp.getLinkedData();    // calling
    // Document.proplateViewer(ActionLink)
    // will set this value

    if ((doc != null) && (doc.getLinkedData() != null)) {    //possible for load to get called multiple times
      doc.setLinkedData(null);

      WindowViewer    w      = Platform.getWindowViewer();
      StackPaneViewer sp     = (StackPaneViewer) gp.getWidget("reportStack");
      NavigatorWidget nw     = (NavigatorWidget) gp.getWidget("navigator");
      boolean         stains = reportHasStains(doc);
      boolean         sus    = reportHasSusceptibilities(doc);

      if (!stains &&!sus) {
        gp.setRegionVisible(1, false);
      } else {
        if (!sus) {
          nw.setActionVisible(1, false);
        } else {
          loadSusceptibilities(w, doc, (TableViewer) sp.getViewer(1));
        }

        if (!stains) {
          nw.setActionVisible(2, false);
        } else {
          loadStains(w, doc, sp.getViewer(2));
        }
      }
    }
  }

  public void onReportNavigatorChange(String eventName, iWidget widget, EventObject event) {
    NavigatorWidget w = (NavigatorWidget) widget;
    int             n = w.getSelectedIndex();

    ((StackPaneViewer) w.getFormViewer().getWidget("reportStack")).switchTo(n);
  }

  public void onSummaryTableAction(String eventName, iWidget widget, EventObject event) {
    String     key  = ((TableViewer) widget).getSelectionDataAsString(1);
    ActionPath path = new ActionPath("labs", key);

    Utils.handleActionPath(path);
  }

  public void onCreated(String eventName, iWidget widget, EventObject event) {
    if (Utils.isCardStack()) {
      CardStackUtils.setViewerAction((iViewer) widget, new ChartsActionListener(), true);
      currentView = ResultsView.CHARTS;
    } else {
      ActionPath path = Utils.getActionPath(false);

      if ((path != null) && (path.peek() != null)) {
        currentView = ResultsView.CHARTS;

        if (UIScreen.isLargeScreen()) {
          SplitPane cfg = (SplitPane) ((DataEvent) event).getData();
          Region    r   = (Region) cfg.regions.getEx(1);

          r.dataURL.setValue("labs_charts.rml");
        }
      } else {
        currentView = UIScreen.isLargeScreen()
                      ? ResultsView.TRENDS
                      : ResultsView.CHARTS;
      }
    }
  }

  @Override
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    super.onDispose(eventName, widget, event);
    disposeOfLoadedDocument();
  }

  /**
   * Called via the configure event on the trends table. We configure the form
   * renderers and populates the table if the labs data has already been loaded
   *
   */
  public void onTrendsTableConfigure(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;

    configureTrendFormsRenderer((UIFormsLayoutRenderer) table.getColumn(0).getCellRenderer());

    if (table.getColumnCount() > 1) {
      configureTrendFormsRenderer((UIFormsLayoutRenderer) table.getColumn(1).getCellRenderer());
    }

    if (dataLoaded && (trendPanels != null)) {
      for (TrendPanel p : trendPanels) {
        p.popuplateTable((TableViewer) widget, trendsLayout);
      }

      ((TableViewer) widget).refreshItems();
    }

    overViewLoaded = true;
  }

  /**
   * Called via the created event on the trends table. We remove the second
   * column on smaller screens.
   */
  public void onTrendsTableCreated(String eventName, iWidget widget, EventObject event) {
    int width = (int) UIScreen.fromPlatformPixels(Platform.getWindowViewer().getWidth());

    if (width < 480) {
      Table table = (Table) ((DataEvent) event).getData();

      table.columns.remove(1);
    }
  }

  public void selectLabs(String eventName, iWidget widget, EventObject event) {}

  @Override
  public void showChartForSelectedItem(TableViewer table) {
    RenderableDataItem row = table.getSelectedItem();

    if (!row.get(0).isEmpty() && ((row.getLinkedData() != null) || (row.getValue() != null))) {    //parent item so show collection info
      disposeOfLoadedDocument();
      showCollectionInfo(table, row);

      return;
    }

    RenderableDataItem item = (row == null)
                              ? null
                              : row.getItemEx(IS_DOCUMENT_POSITION);

    if ((item != null) && "true".equals(item.getValue())) {
      try {
        final WindowViewer w = Platform.getWindowViewer();

        item = row.get(RESULT_ID_POSITION);

        String          id = (String) item.getValue();
        SplitPaneViewer sv = Utils.getSplitPaneViewer(table);
        iTarget         t;

        if (sv != null) {
          t = sv.getRegion(1);
        } else {
          t = w.getTarget(iTarget.TARGET_WORKSPACE);
        }

        iViewer v = t.getViewer();

        if ((v != null) && (v.getLinkedData() instanceof Document)) {
          Document odoc = (Document) v.getLinkedData();

          if (id.equals(odoc.getID())) {
            return;
          }
        }

        disposeOfLoadedDocument();

        URL url;

        if (documentsInlined) {
          String s = (String) row.getValue();

          if (s == null) {
            s = (String) row.getLinkedData();
            s = Base64.decodeUTF8(s);
            row.setValue(s);
          }

          url = w.createInlineURL(s, "text.html");
        } else {
          url = table.getURL("document/" + id + ".html");
        }

        ActionLink link = w.createActionLink(null, "/lab_report.rml");

        link.setTargetName(t.getName());

        Document doc = new Document(table, new ActionLink(url), id);

        loadedDocument = doc;
        doc.setLinkedData(Boolean.FALSE);
        doc.setMainDocumentInfo((Date) row.get(DATE_POSITION).getValue(), row.get(NAME_POSITION).toString());
        doc.loadAndPopulateViewer(link);
      } catch(Exception e) {
        Utils.handleError(e);
      }
    } else {
      disposeOfLoadedDocument();
      super.showChartForSelectedItem(table);
    }
  }

  private void disposeOfLoadedDocument() {
    if (loadedDocument != null) {
      loadedDocument.dispose();
      loadedDocument = null;
    }
  }

  private void showCollectionInfo(final TableViewer table, final RenderableDataItem row) {
    try {
      JSONObject o = (JSONObject) row.getValue();

      if (o == null) {
        o = new JSONObject(Base64.decodeUTF8((String) row.getLinkedData()));
        row.setValue(o);
        row.setLinkedData(null);
      }

      final WindowViewer win  = Platform.getWindowViewer();
      final JSONObject   json = o;

      ViewerCreator.createConfiguration(table, new ActionLink("/lab_collection_info.rml"), new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          win.hideWaitCursor();

          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);

            return;
          }

          if (canceled || table.isDisposed()) {
            return;
          }

          iContainer v = (iContainer) win.createViewer(table.getFormViewer(), (Widget) returnValue);
          iWidget    w = v.getWidget("accessionNumber");

          if (w != null) {
            w.setValue(json.optString("accessionNumber"));
          }

          w = v.getWidget("comment");

          if (w != null) {
            w.setValue(json.optString("comment"));
          }

          w = v.getWidget("requestor");

          if (w != null) {
            w.setValue(json.optString("requestor"));
          }

          w = v.getWidget("collectionDate");

          if (w != null) {
            w.setValue(row.get(NAME_POSITION).getValue());
          }

          w = v.getWidget("specimen");

          if (w != null) {
            w.setValue(json.optString("specimen"));
          }

          if (UIScreen.isLargeScreen()) {
            SplitPaneViewer sp = (SplitPaneViewer) table.getFormViewer();

            win.activateViewer(v, sp.getRegion(1).getName());
          } else {
            Utils.pushWorkspaceViewer(v);
          }
        }
      });
    } catch(Exception e) {
      Utils.handleError(e);
    }
  }

  protected void configureTrendFormsRenderer(UIFormsLayoutRenderer renderer) {
    Map<String, String> map = trendsLayout;

    renderer.addLabelRenderer(map.get("name"));
    renderer.addLabelRenderer(map.get("value"));
    renderer.addLabelRenderer(map.get("valueValue"));
    renderer.addLabelRenderer(map.get("date"));
    renderer.addLabelRenderer(map.get("dateValue"));
    renderer.addLabelRenderer(map.get("trendIcon"));
  }

  @Override
  protected void dataParsed(iWidget widget, final List<RenderableDataItem> rows, ActionLink link) {
    originalRows = rows;
    bunFound     = bunValue == null;
    creatFound   = creatinineValue == null;

    final TableViewer table = (TableViewer) widget;

    table.setWidgetDataLink(link);
    isSummary = "summaryLabs".equals(widget.getName());

    if (!isSummary) {
      String title = Platform.getResourceAsString("bv.text.lab_test_all");

      if (title != null) {
        table.setColumnTitle(1, title);
      }
    }

    if (trendPanels != null) {
      for (TrendPanel p : trendPanels) {
        p.clear();
      }
    }

    if (checkAndHandleNoData(table, rows)) {
      return;
    }

    Platform.getWindowViewer().showWaitCursor();
    Platform.getAppContext().executeWorkerTask(new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          processData(table, rows);

          return null;
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        ActionPath path = Utils.getActionPath(true);

        Platform.getWindowViewer().hideWaitCursor();

        if (result instanceof Throwable) {
          Utils.handleError((Throwable) result);
        } else {
          if (overViewLoaded && (trendPanels != null)) {
            iContainer  c      = (iContainer) Platform.getWindowViewer().getViewer("labsOverview");
            TableViewer ttable = (TableViewer) c.getWidget("trendsTable");

            for (TrendPanel p : trendPanels) {
              p.popuplateTable(ttable, trendsLayout);
            }

            ttable.refreshItems();
          } else {
            final String key = (path == null)
                               ? null
                               : path.shift();

            if (key != null) {
              handlePathKey(table, key, 1, true);
            }
          }
        }
      }
    });
  }

  @Override
  protected String getCategory(RenderableDataItem row) {
    return (String) row.get(CATEGORY_NAME_POSITION).getValue();
  }

  @Override
  protected String getSpeeedSheetColumnTitle() {
    return Platform.getResourceAsString("bv.text.lab_test_all");
  }

  protected void handleSummaryLabs(TableViewer table, List<RenderableDataItem> rows) {
    HashSet<String> map = new HashSet<String>();
    Date            end = null;
    Date            beg = null;
    int             len = rows.size();

    if (len == 0) {
      return;
    }

    List<RenderableDataItem> list = rows;

    if (uniqueSummaryEntries) {
      list = new ArrayList<RenderableDataItem>(Math.min(len, 10));
    }

    for (int i = 0; i < len; i++) {
      RenderableDataItem row  = rows.get(i);
      RenderableDataItem item = row.get(NAME_POSITION);

      if (uniqueSummaryEntries) {
        String key = (String) item.getLinkedData();

        if (map.add(key)) {
          list.add(row);
          beg = (Date) row.get(DATE_POSITION).getValue();

          if (end == null) {
            end = beg;
          }
        }
      } else {
        beg = (Date) row.get(DATE_POSITION).getValue();

        if (end == null) {
          end = beg;
        }
      }
    }

    table.setAll(list);

    String       s;
    WindowViewer w = Platform.getWindowViewer();

    if (ScreenUtils.isSmallScreen()) {
      int d = Helper.daysBetween(new Date(), end);

      if (d > 30) {
        return;
      }

      s = w.getString("bv.text.summary_labs_small", d);
    } else {
      s = w.getString("bv.format.time.general_short");

      SimpleDateFormatEx df = new SimpleDateFormatEx(s);

      if (beg.equals(end)) {
        s = df.format(beg);
      } else {
        s = df.format(beg) + " - " + df.format(end);
      }

      s = w.getString("bv.text.summary_labs", s);
    }

    final iWidget label = table.getFormViewer().getWidget("labs_description");
    final String  value = s;

    Platform.runOnUIThread(new Runnable() {
      @Override
      public void run() {
        if (label != null) {
          if (!label.isDisposed()) {
            label.setValue(value);
          }
        }
      }
    });
  }

  @Override
  protected boolean hasCategories() {
    return true;
  }

  protected void loadStains(WindowViewer w, Document doc, iWidget browser) {
    int     len   = doc.getAttachmentCount();
    boolean error = false;

    for (int i = 0; i < len; i++) {
      DocumentItem di = doc.getAttachment(i);

      if (!"stains".equals(di.customItemType)) {
        continue;
      }

      try {
        browser.setValue(di.itemBody);
      } catch(Exception e) {
        e.printStackTrace();
        w.alert(w.getString("bv.text.labs_stains_error"));
      }

      break;
    }

    if (error) {
      Platform.getWindowViewer().alert(Platform.getWindowViewer().getString("bv.text.labs_susceptibilities_error"));
    }
  }

  protected void loadSusceptibilities(WindowViewer w, Document doc, TableViewer table) {
    boolean error = false;
    int     len   = doc.getAttachmentCount();

    for (int i = 0; i < len; i++) {
      DocumentItem di = doc.getAttachment(i);

      if (!"susceptibilities".equals(di.customItemType)) {
        continue;
      }

      try {
        ActionLink link = new ActionLink(di.itemBody, di.mimeType);

        link.setContext(table);

        List<RenderableDataItem> list = DataItemParserHandler.parse(table, link, -1);

        len = list.size();

        if (len < 2) {
          error = true;

          break;
        }

        RenderableDataItem cols  = list.remove(0);
        List<Column>       tcols = new ArrayList<Column>(table.getColumns());

        len = cols.size();

        Column c;
        UIFont f = FontUtils.getDefaultFont();

        f = f.deriveFontSize(f.getSize() - 2);

        for (int n = 2; n < len; n++) {
          tcols.add(c = table.createColumn(cols.getItem(n)));
          c.setHeaderFont(f);

          CharSequence s = c.getColumnTitle();

          c.setColumnWidth((s.length() + 1) + "ch");
        }

        f = FontUtils.getDefaultFont();
        table.setFont(Platform.getWindowViewer().getFont().deriveFontSize(f.getSize() - 1));
        table.resetTable(tcols, Utils.groupRows(table, list, 0, 0, false));

        for (int n = 2; n < len; n++) {
          table.sizeColumnToFit(n);
        }
      } catch(Exception e) {
        e.printStackTrace();
        error = true;
      }

      break;
    }

    if (error) {
      w.alert(w.getString("bv.text.labs_susceptibilities_error"));
    }
  }

  protected void processData(TableViewer table, List<RenderableDataItem> rows) {
    try {
      Utils.checkRowsInReverseAndOptimizeDates(rows, DATE_POSITION, RenderableDataItem.TYPE_DATETIME, this);
      itemDates = itemDatesSet.toArray(new Date[itemDatesSet.size()]);

      if (isSummary) {
        handleSummaryLabs(table, rows);
      } else {
        Arrays.sort(itemDates);

        int len = itemDates.length;

        spreadsheetPosition = Math.max(0, len - spreadSheetPageSize);
        rows                = Utils.groupByDate(table, rows);

        if ((collectionInfoMap != null) &&!collectionInfoMap.isEmpty()) {
          len = rows.size();

          for (int i = 0; i < len; i++) {
            RenderableDataItem row  = rows.get(i);
            Date               date = (Date) row.get(DATE_POSITION).getValue();
            RenderableDataItem ci   = collectionInfoMap.get(date);

            if (ci == null) {
              continue;
            }

            RenderableDataItem item = ci.getItemEx(COMMENT_POSITION);
            String             s    = (item == null)
                                      ? ""
                                      : item.toString();

            if ("true".equals(s)) {
              item = row.getItemEx(NAME_POSITION);
              item.setIcon(noteIcon);
              item.setIconPosition(IconPosition.LEADING);
            }

            row.setLinkedData(ci.getItemEx(VALUE_POSITION).getValue());
          }

          collectionInfoMap.clear();
        }

        table.handleGroupedCollection(rows, false);
        dataLoaded = true;
      }
    } finally {}
  }

  protected boolean reportHasStains(Document doc) {
    int len = doc.getAttachmentCount();

    for (int i = 0; i < len; i++) {
      DocumentItem di = doc.getAttachment(i);

      if ("stains".equalsIgnoreCase(di.customItemType)) {
        return true;
      }
    }

    return false;
  }

  protected boolean reportHasSusceptibilities(Document doc) {
    int len = doc.getAttachmentCount();

    for (int i = 0; i < len; i++) {
      DocumentItem di = doc.getAttachment(i);

      if ("susceptibilities".equalsIgnoreCase(di.customItemType)) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected void reset() {
    super.reset();

    if (UIScreen.isLargeScreen()) {
      currentView = ResultsView.TRENDS;
    }

    overViewLoaded = false;
  }

  class ChartHandler extends aChartHandler {
    public ChartHandler(JSONObject chartsInfo) {
      super("Labs", chartsInfo, NAME_POSITION, DATE_POSITION, VALUE_POSITION);
    }
  }
}
