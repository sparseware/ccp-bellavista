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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.spot.Chart;
import com.appnativa.rare.ui.ColorUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.RenderableDataItem.HorizontalAlign;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIDimension;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.chart.ChartDataItem;
import com.appnativa.rare.util.SubItemComparator;
import com.appnativa.rare.viewer.ChartViewer;
import com.appnativa.rare.viewer.SplitPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.aGroupableButton;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.MutableInteger;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.external.aRemoteMonitor;

/**
 * This provides a core set of functionality for managing results for display
 * The data is assumed to be returned is reverse chronological order (newest
 * values first). The middle-ware should enforce this constraint.
 * 
 * @author Don DeCoteau
 */
public class Vitals extends aResultsManager implements iValueChecker {
  static boolean        checkedForMonitoringSupport;
  static Class          monitoringClass;
  static aRemoteMonitor monitorInstance;

  public Vitals() {
    super("vitals", "Vitals");
    iPlatformAppContext app = Platform.getAppContext();
    JSONObject info = (JSONObject) app.getData("vitalsInfo");
    itemCounts = new LinkedHashMap<String, MutableInteger>();
    itemDatesSet = new LinkedHashSet<Date>();
    dataPageSize = info.optInt("dataPageSize", dataPageSize);
    spreadSheetPageSize = info.optInt("spreadSheetPageSize", spreadSheetPageSize);
    chartHandler = new ChartHandler(info.getJSONObject("charts"));
    chartableItemsManager=new ChartableItemsManager();
    if (info.optBoolean("hasReferenceRange", false)) {
      RANGE_POSITION = 4;
    }
    if(Utils.isCardStack()) {
      trendPanels = createTrendPanels(info.optJSONArray("trends"),true);
    }

    if (!checkedForMonitoringSupport) {
      checkedForMonitoringSupport=true;
      String cls = info.optString("monitoringClass", null);
      if (cls != null && cls.length() > 0) {
        try {
          monitoringClass = Platform.loadClass(cls);
        } catch (Exception ignore) {
        }
      }
    }
  }

  @Override
  protected String getSpeeedSheetColumnTitle() {
    return Platform.getResourceAsString("bv.text.vitals");
  }

  public void onSummaryClick(String eventName, iWidget widget, EventObject event) {
    ActionPath path = new ActionPath("vitals", "combo");
    Utils.handleActionPath(path);
  }

  public void onConfigureRealtimeVitalsButton(String eventName, iWidget widget, EventObject event) {
    if (monitoringClass != null && Utils.getPatient().optBoolean("has_monitor", false)) {
      widget.setEnabled(true);
    }
  }

  protected void changeViewEx(final iWidget widget, final ResultsView view) {
    if (view != ResultsView.CUSTOM_1) {
      return;
    }
    final WindowViewer w = Platform.getWindowViewer();
    if (monitorInstance == null) {
      try {
        monitorInstance = (aRemoteMonitor) monitoringClass.newInstance();
      } catch (Exception e) {
        monitoringClass = null;
        widget.setEnabled(false);
        w.alert(w.getString("bv.text.failed_to_create_monitor"));
        return;
      }
    }
    final iContainer fv = widget.getFormViewer();
    final SplitPaneViewer sp;
    if (fv instanceof SplitPaneViewer) {
      sp = (SplitPaneViewer) fv;
    } else {
      sp = null;
    }

    iFunctionCallback cb = new iFunctionCallback() {

      @Override
      public void finished(boolean canceled, Object returnValue) {
        w.hideWaitCursor();
        if (widget.isDisposed()) {
          return;
        }
        if (returnValue instanceof Throwable) {
          Utils.handleError((Throwable) returnValue);
        } else if (returnValue == null) {
          widget.setEnabled(false);
          w.alert(w.getString("bv.text.failed_to_create_monitor"));
        } else {
          currentView = view;
          iViewer v = (iViewer) returnValue;
          dataTable.clearSelection();
          if (chartHandler != null) {
            chartHandler.resetChartPoints();
          }
          if (sp == null) {
            v.setEventHandler(iConstants.EVENT_UNLOAD, "class:Vitals#reselectDefaultView", true);
            Utils.pushWorkspaceViewer(v);
          } else {
            w.activateViewer(v, sp.getRegion(1).getName());
          }
        }
      }
    };
    showRegularTableEx(fv);
    w.showWaitCursor();
    UIDimension targetSize;
    if (sp != null) {
      targetSize = sp.getSize();
    } else {
      targetSize = w.getTarget(iTarget.TARGET_WORKSPACE).getTargetSize();
    }
    monitorInstance.createViewer(Utils.getPatient(), sp == null ? w : sp, targetSize, cb);
  }

  @Override
  public void changeView(String eventName, iWidget widget, EventObject event) {
    String name = ((aGroupableButton) widget).getSelectedButtonName();
    if (name == null) {
      name = widget.getName();
    }
    if (UIScreen.isLargeScreen() && "spreadsheet".equals(name)) { //if going to the spreadsheet go to same result type
      String key = getSelectedChartableKey();
      if (key != null) {
        keyPath = new ActionPath(key);
      }
    }
    super.changeView(eventName, widget, event);
  }

  public void onShowComboChart(String eventName, iWidget widget, EventObject event) {
    showComboChart((StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack"));
  }

  public boolean checkRow(RenderableDataItem row, int index, int expandableColumn, int rowCount) {
    try {
      do {
        RenderableDataItem name = row.getItemEx(NAME_POSITION);
        Date date = (Date) row.get(DATE_POSITION).getValue();
        if (name == null || date == null) {
          continue;
        }
        String key = (String) name.getLinkedData();
        if (key == null) {
          continue;
        }
        RenderableDataItem valueItem = row.get(VALUE_POSITION);
        String value = (String) valueItem.getValue();
        if (chartableItemsManager.check(key,value)) {
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
      } while (false);
    } catch (Exception e) {
      Platform.ignoreException(null, e);
    }
    return true;
  }

  /**
   * Called when the vitals data has been loaded into the table. We populate the
   * the categories list and sort the table based on the sort order for the
   * categories that was sent with the data
   */
  public void onFinishedLoading(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    chartableItemsManager.createList(table, NAME_POSITION);
    if(Utils.isCardStack() && trendPanels!=null && trendPanels.length>0) {
      iContainer fv = (iContainer) widget.getFormViewer().getWidget("trends");
      TrendPanel panel = trendPanels[0];
      panel.removePeers();
      String text = panel.popuplateForm(fv);
      CardStackUtils.switchToViewer(table.getParent());
      updateCardStackTitle(panel.title,text);
    }
  }
  
  public void onCreated(String eventName, iWidget widget, EventObject event) {
    if(Utils.isCardStack()) {
      CardStackUtils.setViewerAction((iViewer) widget, new ChartsActionListener(),true);
    }
  }
  

  /**
   * Called via the configure event on the charts panel
   */
  @Override
  public void onChartsPanelLoaded(String eventName, iWidget widget, EventObject event) {
    super.onChartsPanelLoaded(eventName, widget, event);
    if (dataLoaded && !dataTable.hasSelection()) {
      showComboChart((StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack"));
    }
  }

  public void selectVitals(String eventName, iWidget widget, EventObject event) {

  }

  protected List<RenderableDataItem> createSpreadsheetRows(TableViewer table) {
    Date[] dates = itemDates;
    List<RenderableDataItem> list = originalRows;
    LinkedHashMap<String, MutableInteger> counts = itemCounts;
    LinkedHashMap<String, List<RenderableDataItem>> categories = new LinkedHashMap<String, List<RenderableDataItem>>();
    HashMap<String, RenderableDataItem> map = new HashMap<String, RenderableDataItem>(counts.size());
    ArrayList<RenderableDataItem> rows = new ArrayList<RenderableDataItem>(counts.size());
    int len = list.size();
    int clen = dates.length;
    boolean found;
    RenderableDataItem row, test, orow;
    for (int i = 0; i < len; i++) {
      orow = list.get(i);
      test = orow.get(NAME_POSITION);
      String s = (String) test.getLinkedData();
      if (!counts.containsKey(s)) { // only contains chartable items (numeric
                                    // values)
        continue;
      }
      Date date = (Date) orow.get(DATE_POSITION).getValue();
      s = (String) test.getValue();
      row = map.get(s);

      if (row == null) {
        row = table.createRow(clen + 1, false);
        row.setItemCount(clen + 1);
        map.put(s, row);
        row.setItem(0, test);
        rows.add(row);
      }
      long time = date.getTime();
      found = false;
      for (int col = 0; col < clen; col++) {
        if (dates[col].getTime() == time) {
          found = true;
          row.setItem(col + 1, orow.get(VALUE_POSITION));
        }
      }
      if (!found) {
        System.out.println(orow);
      }
    }
    SubItemComparator c = new SubItemComparator();
    c.setOptions(0, false);
    Iterator<Entry<String, List<RenderableDataItem>>> it = categories.entrySet().iterator();
    rows.clear();
    UIColor bg = ColorUtils.getColor("vitalsCategoryBackground");
    RenderableDataItem emptyCatRow = new RenderableDataItem();
    emptyCatRow.setBackground(bg);
    while (it.hasNext()) {
      Entry<String, List<RenderableDataItem>> e = it.next();
      String category = e.getKey();
      List<RenderableDataItem> clist = e.getValue();
      Collections.sort(clist, c);
      row = table.createRow(clen + 3, false);
      row.setSelectable(false);
      for (int i = 0; i < clen + 3; i++) {
        row.add(emptyCatRow);
      }
      test = new RenderableDataItem(category);
      test.setBackground(bg);
      test.setColumnSpan(-1);
      test.setHorizontalAlignment(HorizontalAlign.CENTER);
      row.set(1, test);
      row.setLinkedData(category);
      rows.add(row);
      rows.addAll(clist);
    }
    return rows;
  }

  protected void processData(TableViewer table, List<RenderableDataItem> rows) {
    Utils.checkRowsAndOptimizeDates(rows, DATE_POSITION, RenderableDataItem.TYPE_DATETIME, this);
    itemDates = itemDatesSet.toArray(new Date[itemDatesSet.size()]);
    Arrays.sort(itemDates);
    int len = itemDates.length;
    spreadsheetPosition = Math.max(0, len - spreadSheetPageSize);
    rows = Utils.groupByDate(table, rows);

    table.handleGroupedCollection(rows, false);
    dataLoaded = true;
    ActionPath path = Utils.getActionPath(true);
    String key = path == null ? null : path.pop();
    if (key == null && UIScreen.isLargeScreen()) {
      key = "combo";
    }
    if (key != null) {
      if (UIScreen.isLargeScreen() && !chartsLoaded) {
        keyPath = new ActionPath(key);
      } else {
        handlePathKey(table, key, 0, true);
      }
    }
  }

  protected void showComboChart(StackPaneViewer sp) {
    clearSelection();
    try {
      if (sp == null) {
        keyPath = new ActionPath("combo");
        
        showChartsView(spreadsheetTable==null ? dataTable : spreadsheetTable);
      } else {
        ChartDataItem series = ChartViewer.createSeries("Combo");
        ChartViewer cv = chartHandler.createChart(sp.getFormViewer(), "combo", 1, series);
        cv.getChartDefinition().setShowLegends(true);
        Summary parser = new Summary();
        cv.remove(series);
        parser.calculateRangesAndUpdateUI(cv.getFormViewer(), cv, originalRows, false);
        Utils.setViewerInStackPaneViewer(sp, cv, null, true, true, true);
      }
    } catch (Exception e) {
      Utils.handleError(e);
    }
  }

  @Override
  protected void handlePathKey(TableViewer table, String key, int column, boolean fireAction) {
    if ("combo".equals(key)) {
      showComboChart((StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack"));
    } else {
      super.handlePathKey(table, key, column, fireAction);
    }
  }
  protected TrendPanel[]                            trendPanels;

  @Override
  protected void dataParsed(iWidget widget, final List<RenderableDataItem> rows, ActionLink link) {
    originalRows = rows;
    final TableViewer table = (TableViewer) widget;
    table.setWidgetDataLink(link);
    itemCounts.clear();
    itemDatesSet.clear();
    if (trendPanels != null) {
      for (TrendPanel p : trendPanels) {
        p.clear();
      }
    }

    if (checkAndHandleNoData(table, rows)) {
      itemDates = null;
      return;
    }
    processData(table, rows);
  }

  class ChartHandler extends aChartHandler {

    public ChartHandler(JSONObject chartsInfo) {
      super("Vitals", chartsInfo, NAME_POSITION, DATE_POSITION, VALUE_POSITION);
    }

    @Override
    protected void configureChart(iWidget context, Chart cfg, String key, ChartDataItem series) {
      super.configureChart(context, cfg, key, series);
      if (key.equals("combo")) {
        cfg.getPlotReference().bgColor.setValue("defaultBackground,defaultBackground-15");
      }
    }
  }
}
