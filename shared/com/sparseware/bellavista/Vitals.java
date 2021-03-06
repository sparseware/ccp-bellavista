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
import com.appnativa.rare.viewer.ToolBarViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.aGroupableButton;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.MutableInteger;
import com.appnativa.util.json.JSONObject;

import com.sparseware.bellavista.external.aRemoteMonitor;

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

/**
 * This provides a core set of functionality for managing results for display
 * The data is assumed to be returned is reverse chronological order (newest
 * values first). The middle-ware should enforce this constraint.
 *
 * @author Don DeCoteau
 */
public class Vitals extends aResultsManager implements iValueChecker {
  protected static boolean        checkedForMonitoringSupport;
  protected static Class          monitoringClass;
  protected static aRemoteMonitor monitorInstance;
  protected boolean               showUnits;
  protected boolean               updateWeightHeight;
  protected TrendPanel[]          trendPanels;
  private StringBuilder           reuseableSB = new StringBuilder();

  /**
   * Creates a new instance
   */
  public Vitals() {
    super("vitals", "Vitals");

    iPlatformAppContext app  = Platform.getAppContext();
    JSONObject          info = (JSONObject) app.getData("vitalsInfo");

    itemCounts            = new LinkedHashMap<String, MutableInteger>();
    itemDatesSet          = new LinkedHashSet<Date>();
    dataPageSize          = info.optInt("dataPageSize", dataPageSize);
    spreadSheetPageSize   = info.optInt("spreadSheetPageSize", spreadSheetPageSize);
    showUnits             = info.optBoolean("showUnits", false);
    chartHandler          = new ChartHandler(info.getJSONObject("charts"));
    chartableItemsManager = new ChartableItemsManager();

    if (info.optBoolean("hasReferenceRange", false)) {
      RANGE_POSITION = 4;
    }

    if (Utils.isCardStack()) {
      trendPanels = createTrendPanels(info.optJSONArray("trends"), true);
    }

    if (!checkedForMonitoringSupport) {
      checkedForMonitoringSupport = true;

      String cls = info.optString("monitoringClass", null);

      if ((cls != null) && (cls.length() > 0)) {
        try {
          monitoringClass = Platform.loadClass(cls);
        } catch(Exception ignore) {}
      }
    }
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

  @Override
  public boolean checkRow(RenderableDataItem row, int index, int expandableColumn, int rowCount) {
    try {
      do {
        RenderableDataItem name = row.getItemEx(NAME_POSITION);
        Date               date = (Date) row.get(DATE_POSITION).getValue();

        if ((name == null) || (date == null)) {
          continue;
        }

        String key = (String) name.getLinkedData();

        if (key == null) {
          continue;
        }

        RenderableDataItem valueItem = row.get(VALUE_POSITION);
        String             value     = (String) valueItem.getValue();

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

        if (key.equals("wt") || key.equals("ht")) {
          JSONObject o = (JSONObject) Platform.getAppContext().getData("patient");
          String     s = o.optString(key);

          if (s.length() == 0) {
            RenderableDataItem unitItem = row.getItemEx(UNIT_POSITION);
            String             unit     = (unitItem == null)
                                          ? ""
                                          : unitItem.toString();

            if (unit.length() > 0) {
              o.put(key, valueItem.toString() + unit);
              updateWeightHeight = true;
            }
          }
        }

        if (showUnits) {
          RenderableDataItem unitItem = row.getItemEx(UNIT_POSITION);
          String             unit     = (unitItem == null)
                                        ? ""
                                        : unitItem.toString();

          if (unit.length() > 0) {
            StringBuilder sb = reuseableSB;

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
      } while(false);
    } catch(Exception e) {
      Platform.ignoreException(null, e);
    }

    return true;
  }

  /**
   * Called via the configure event on the charts panel
   */
  @Override
  public void onChartsPanelLoaded(String eventName, iWidget widget, EventObject event) {
    super.onChartsPanelLoaded(eventName, widget, event);

    if (dataLoaded &&!dataTable.hasSelection()) {
      showComboChart((StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack"));
    }
  }

  /**
   * Called to configure the real-time vitals button.
   * The button is enabled/disabled based on the available support
   */
  public void onConfigureRealtimeVitalsButton(String eventName, iWidget widget, EventObject event) {
    if ((monitoringClass != null) && Utils.getPatient().optBoolean("has_monitor", false)) {
      widget.setEnabled(true);
    }
  }

  /**
   * Called when the vitals UI is created. If the UI is a card stack
   * then we set an action on the card that will display the charts
   * when the card is tapped.
   */
  public void onCreated(String eventName, iWidget widget, EventObject event) {
    if (Utils.isCardStack()) {
      CardStackUtils.setViewerAction((iViewer) widget, new ChartsActionListener(), true);
    }
  }

  /**
   * Called when the vitals data has been loaded into the table. We populate the
   * the categories list and sort the table based on the sort order for the
   * categories that was sent with the data
   */
  public void onFinishedLoading(String eventName, iWidget widget, EventObject event) {
    ToolBarViewer tb = (ToolBarViewer) widget.getParent().getWidget("tableToolbar");
    iWidget       cw = (tb == null)
                       ? null
                       : tb.getWidget("spreadsheet");

    if (cw != null) {
      cw.setEnabled(!hasNoData);
    }

    cw = (tb == null)
         ? null
         : tb.getWidget("comboChart");

    if (cw != null) {
      cw.setEnabled(!hasNoData);
    }

    if (hasNoData) {
      return;
    }

    TableViewer table = (TableViewer) widget;

    table.pageHome();
    chartableItemsManager.createList(table, NAME_POSITION);

    if (Utils.isCardStack() && (trendPanels != null) && (trendPanels.length > 0)) {
      iContainer fv    = (iContainer) widget.getFormViewer().getWidget("trends");
      TrendPanel panel = trendPanels[0];

      panel.removePeers();

      String text = panel.popuplateForm(fv);

      CardStackUtils.switchToViewer(table.getParent());
      updateCardStackTitle(panel.title, text);
    }
  }

  /**
   * Event handler for showing the combo chart
   */
  public void onShowComboChart(String eventName, iWidget widget, EventObject event) {
    showComboChart((StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack"));
  }

  /**
   * Event handler for when the vitals section of the summary screen is clicked
   */
  public void onSummaryClick(String eventName, iWidget widget, EventObject event) {
    ActionPath path = new ActionPath("vitals", "combo");

    Utils.handleActionPath(path);
  }

  @Deprecated
  public void selectVitals(String eventName, iWidget widget, EventObject event) {}

  @Override
  protected void changeViewEx(final iWidget widget, final ResultsView view) {
    if (view != ResultsView.CUSTOM_1) {
      return;
    }

    final WindowViewer w = Platform.getWindowViewer();

    if (monitorInstance == null) {
      try {
        monitorInstance = (aRemoteMonitor) monitoringClass.newInstance();
      } catch(Exception e) {
        monitoringClass = null;
        widget.setEnabled(false);
        w.alert(w.getString("bv.text.failed_to_create_monitor"));

        return;
      }
    }

    final iContainer      fv = widget.getFormViewer();
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

    monitorInstance.createViewer(Utils.getPatient(), (sp == null)
            ? w
            : sp, targetSize, cb);
  }

  /**
   * Creates the rows for the spread sheet table
   * @param context a context for creating the rows
   * @return the list of rows setup for a spreadsheet view
   */
  protected List<RenderableDataItem> createSpreadsheetRows(aWidget context) {
    Date[]                                          dates      = itemDates;
    List<RenderableDataItem>                        list       = originalRows;
    LinkedHashMap<String, MutableInteger>           counts     = itemCounts;
    LinkedHashMap<String, List<RenderableDataItem>> categories = new LinkedHashMap<String, List<RenderableDataItem>>();
    HashMap<String, RenderableDataItem>             map        = new HashMap<String, RenderableDataItem>(counts.size());
    ArrayList<RenderableDataItem>                   rows       = new ArrayList<RenderableDataItem>(counts.size());
    int                                             len        = list.size();
    int                                             clen       = dates.length;
    boolean                                         found;
    RenderableDataItem                              row, test, orow;

    for (int i = 0; i < len; i++) {
      orow = list.get(i);
      test = orow.get(NAME_POSITION);

      String s = (String) test.getLinkedData();

      if (!counts.containsKey(s)) {    // only contains chartable items (numeric
        // values)
        continue;
      }

      Date date = (Date) orow.get(DATE_POSITION).getValue();

      s   = (String) test.getValue();
      row = map.get(s);

      if (row == null) {
        row = context.createRow(clen + 1, false);
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

    UIColor            bg          = ColorUtils.getColor("vitalsCategoryBackground");
    RenderableDataItem emptyCatRow = new RenderableDataItem();

    emptyCatRow.setBackground(bg);

    while(it.hasNext()) {
      Entry<String, List<RenderableDataItem>> e        = it.next();
      String                                  category = e.getKey();
      List<RenderableDataItem>                clist    = e.getValue();

      Collections.sort(clist, c);
      row = context.createRow(clen + 3, false);
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

  @Override
  protected void dataParsed(iWidget widget, final List<RenderableDataItem> rows, ActionLink link) {
    originalRows = rows;
    tableDataLoaded(link);

    final TableViewer table = (TableViewer) widget;

    if (itemCounts != null) {
      itemCounts.clear();
    }

    if (itemDatesSet != null) {
      itemDatesSet.clear();
    }

    chartableItemsManager.reset();

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

  @Override
  protected String getSpeeedSheetColumnTitle() {
    return Platform.getResourceAsString("bv.text.vitals");
  }

  @Override
  protected void handlePathKey(TableViewer table, String key, int column, boolean fireAction) {
    if ("combo".equals(key)) {
      showComboChart((StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack"));
    } else {
      super.handlePathKey(table, key, column, fireAction);
    }
  }

  /**
   * Called to process data loaded from a server before the data gets
   * presented in the UI
   *
   * @param table the table for the data
   * @param rows the received rows
   */
  protected void processData(TableViewer table, List<RenderableDataItem> rows) {
    updateWeightHeight = false;
    Utils.checkRowsAndOptimizeDates(rows, DATE_POSITION, RenderableDataItem.TYPE_DATETIME, this);

    if (updateWeightHeight) {
      PatientSelect.updateWeightHeightBMI(Platform.getAppContext(),
              (JSONObject) Platform.getAppContext().getData("patient"));

      iContainer c = (iContainer) Platform.getWindowViewer().getViewer("infobar");
      iWidget    w = (c == null)
                     ? null
                     : c.getWidget("wt_ht_bmi");

      if (w != null) {
        w.reset();
      }
    }

    itemDates = itemDatesSet.toArray(new Date[itemDatesSet.size()]);
    Arrays.sort(itemDates);

    int len = itemDates.length;

    spreadsheetPosition = Math.max(0, len - spreadSheetPageSize);
    rows                = Utils.groupByDate(table, rows);
    table.handleGroupedCollection(rows, false);
    dataLoaded = true;
    table.pageHome();

    ActionPath path = Utils.getActionPath(true);
    String     key  = (path == null)
                      ? null
                      : path.pop();

    if ((key == null) && UIScreen.isLargeScreen()) {
      key = "combo";
    }

    if (key != null) {
      if (UIScreen.isLargeScreen() &&!chartsLoaded) {
        keyPath = new ActionPath(key);
      } else {
        handlePathKey(table, key, 0, true);
      }
    }
  }

  /**
   * Shows the combo vitals chart
   *
   * @param sp the stack pane viewer that will host the chart
   */
  protected void showComboChart(StackPaneViewer sp) {
    clearSelection();

    try {
      if (sp == null) {
        keyPath = new ActionPath("combo");
        showChartsView((spreadsheetTable == null)
                       ? dataTable
                       : spreadsheetTable);
      } else {
        ChartDataItem series = ChartViewer.createSeries("Combo");
        ChartViewer   cv     = chartHandler.createChart(sp.getFormViewer(), "combo", 1, series);

        cv.getChartDefinition().setShowLegends(true);

        Summary parser = new Summary();

        cv.remove(series);
        parser.calculateRangesAndUpdateUI(cv.getFormViewer(), cv, originalRows, false);
        Utils.setViewerInStackPaneViewer(sp, cv, null, true, true, true);
        chartHandler.updateZoomButtons(sp.getFormViewer());
      }
    } catch(Exception e) {
      Utils.handleError(e);
    }
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
