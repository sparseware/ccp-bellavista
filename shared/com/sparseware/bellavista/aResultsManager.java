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

/**
 * This provides a core set of functionality for managing results for display The data is assumed to be returned is
 * reverse chronological order (newest values first). The middle-ware should
 * enforce this constraint.
 * 
 * @author Don DeCoteau
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.converters.DateTimeConverter;
import com.appnativa.rare.converters.iDataConverter;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.ItemDescription;
import com.appnativa.rare.spot.Table;
import com.appnativa.rare.ui.ColorUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.chart.ChartDataItem;
import com.appnativa.rare.ui.effects.iTransitionAnimator;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.event.FlingEvent;
import com.appnativa.rare.ui.event.ScaleEvent;
import com.appnativa.rare.util.SubItemComparator;
import com.appnativa.rare.viewer.ChartViewer;
import com.appnativa.rare.viewer.GroupBoxViewer;
import com.appnativa.rare.viewer.SplitPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.aGroupableButton;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.spot.SPOTSet;
import com.appnativa.util.Helper;
import com.appnativa.util.MutableInteger;
import com.appnativa.util.StringCache;
import com.appnativa.util.iFilterableList;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.ActionPath.iActionPathSupporter;

public abstract class aResultsManager extends aEventHandler implements iActionPathSupporter {
  public static int                               DATE_POSITION           = 0;
  public static int                               NAME_POSITION           = 1;
  public static int                               VALUE_POSITION          = 2;
  public static int                               RANGE_POSITION          = -1;
  public static int                               MIN_ANGLED_LABEL_HEIGHT = 400;

  protected ResultsView                           currentView             = ResultsView.CHARTS;
  protected TableViewer                           dataTable;
  protected TableViewer                           spreadsheetTable;
  protected boolean                               hasNoData;
  protected boolean                               dataLoaded;
  protected int                                   spreadsheetPosition;
  protected int                                   spreadSheetPageSize     = 7;
  /** number of days of results at a time to show */
  protected int                                   dataPageSize            = 7;
  protected String                                namePrefix;
  protected aChartHandler                         chartHandler;
  private iTransitionAnimator                     transitionAnimation;
  protected List<RenderableDataItem>              originalRows;

  protected LinkedHashMap<String, MutableInteger> itemCounts;
  protected Date[]                                itemDates;
  protected LinkedHashSet<Date>                   itemDatesSet;
  /** specifies whether the chart panel has been loaded */
  protected boolean                               chartsLoaded;
  protected ActionPath                            keyPath;

  /** data context object used by the spreadsheet columns */
  protected Object                                dateContext;

  /**
   * the name of the calls that will be uses as an event handler (set by the
   * parent class)
   */
  protected String                                scriptClassName;
  /**
   * Identifies whether we have already checked for a selected item in the table
   * and taken the appropriate action. This in the scenario when we are
   * initially passed an item to show or switching views.
   */
  protected boolean                               selectionChecked;

  /** whether we are in multi chart mode */
  protected boolean                               multiChartMode;
  private HashMap<String, Boolean>                chartableItems;
  static int                                      MIN_POINTSLABEL_HEIGHT  = 400;

  public aResultsManager(String namePrefix, String scriptClassName) {
    this.namePrefix = namePrefix;
    this.scriptClassName = scriptClassName;
  }

  public void chooseStartingDate(String eventName, iWidget widget, EventObject event) {

  }

  /**
   * Called when a fling action occurs on a chart. We move to the next/previous
   * chartable item based on the direction of the fling
   */
  public void onChartFling(String eventName, iWidget widget, EventObject event) {
    if (multiChartMode) {
      return;
    }
    FlingEvent e = (FlingEvent) event;

    float x = e.getXVelocity();
    float y = e.getYVelocity();
    if (Math.abs(x) > Math.abs(y)) {
      slideToChartableItem(x < 0, true);
    } else {
      slideToChartableItem(y < 0, false);
    }

  }

  @Override
  public ActionPath getDisplayedActionPath() {
    ActionPath path = new ActionPath(Utils.getPatientID(), namePrefix, currentView.toString().toLowerCase(Locale.US));
    addCurrentPathID(path);
    return path;
  }

  public void handleActionPath(ActionPath path) {
    String view = path.shift();
    if (view != null) {
      try {
        keyPath = path;
        changeView(view, dataTable);
      } catch (Throwable ignore) {
      }
    }
  }

  protected void handleActionPathEx(ActionPath path) {
  }

  /**
   * Add unique identifying information the being displayed. If a piece of
   * information with a permanent context specific id is being displayed (like
   * the id of a clinical note) then that id should be added so that the path
   * will lead to that specific item.
   * 
   * @param path
   *          the path to add the identifying information to.
   */
  protected void addCurrentPathID(ActionPath path) {
  }

  /**
   * Called when a pinch/zoom action occurs on a chart
   */
  public void onChartScale(String eventName, iWidget widget, EventObject event) {
    ScaleEvent e = (ScaleEvent) event;
    switch (e.getEventType()) {
      case ScaleEvent.SCALE_END:
        if (chartHandler != null) {
          chartHandler.zoom(widget.getFormViewer(), e.getScaleFactor() >= 1);
        }

        break;
    }

  }

  /**
   * Called when a chart is resized. The angle of the label is changed based on
   * the height of the chart
   * 
   */
  public void onChartResize(String eventName, iWidget widget, EventObject event) {
    chartHandler.adjustForSize((ChartViewer) widget);
  }

  /**
   * Called when a spread sheet row is created. Subclasses can override to
   * modify the row
   * 
   * @param table
   *          the table
   * @param spreadsheetRow
   *          the row that was created
   * @param sourceRow
   *          the source of the newly created row
   */
  protected void spreadsheetRowCreated(TableViewer table, RenderableDataItem spreadsheetRow, RenderableDataItem sourceRow) {
    if (sourceRow != null && RANGE_POSITION > -1) {
      spreadsheetRow.setItem(spreadsheetRow.size() - 1, sourceRow.get(RANGE_POSITION));
    }
  }

  /**
   * Clears the the selection in the table(s)
   */
  protected void clearSelection() {
    dataTable.clearSelection();
    TableViewer table = (TableViewer) Platform.getWindowViewer().getViewer("spreadsheetTable");
    if (table != null) {
      table.clearSelection();
    }
  }

  /**
   * Updates the spreadsheet rows. This can be done in the background
   * 
   * @param table
   *          the table to update
   */
  protected void updateSpreadsheetRows(final TableViewer table) {
    if (table.isDisposed()) {
      return;
    }
    final WindowViewer w = Platform.getWindowViewer();
    try {
      boolean categorize = hasCategories();
      Date[] dates = itemDates;
      int start = spreadsheetPosition;
      int end = Math.min(start + spreadSheetPageSize, dates.length);
      int cols = table.getColumnCount();
      RenderableDataItem row, test, orow;
      int clen = end - start;
      List<RenderableDataItem> list = originalRows;
      LinkedHashMap<String, MutableInteger> counts = itemCounts;
      LinkedHashMap<String, List<RenderableDataItem>> categories = new LinkedHashMap<String, List<RenderableDataItem>>();
      HashMap<String, RenderableDataItem> map = new HashMap<String, RenderableDataItem>(counts.size());
      ArrayList<RenderableDataItem> rows = new ArrayList<RenderableDataItem>(counts.size());
      boolean found;
      long firstDate = dates[start].getTime();
      long lastDate = dates[end - 1].getTime();
      int len = list.size();
      for (int i = 0; i < len; i++) {
        orow = list.get(i);
        test = orow.get(NAME_POSITION);
        String s = (String) test.getLinkedData();
        if (!counts.containsKey(s)) { // only contains chartable items (numeric
                                      // values)
          continue;
        }
        Date date = (Date) orow.get(DATE_POSITION).getValue();
        long time = date.getTime();
        if (time < firstDate || time > lastDate) {
          continue;
        }
        s = (String) test.getValue();
        row = map.get(s);

        if (row == null) {
          row = table.createRow(cols, false);
          row.setItemCount(cols);
          spreadsheetRowCreated(table, row, orow);
          row.setItem(0, test);
          rows.add(row);
          map.put(s, row);
          if (categorize) {
            String category = getCategory(orow);
            row.setLinkedData(category);
            List<RenderableDataItem> clist = categories.get(category);
            if (clist == null) {
              clist = new ArrayList<RenderableDataItem>();
              categories.put(category, clist);
            }
            clist.add(row);
          }
        }
        found = false;
        for (int col = 0; col < clen; col++) {
          if (dates[col + start].getTime() == time) {
            found = true;
            row.setItem(col + 1, orow.get(VALUE_POSITION));
          }
        }
        if (!found) {// should only happen when normal numeric value is non-numeric - middleware should prevent this
          Platform.debugLog("SPREADSHEET ENTRY NOT FOUND"+ orow);
        }
      }
      if (categorize) {
        SubItemComparator c = new SubItemComparator();
        c.setOptions(0, false);
        table.clear();
        Iterator<Entry<String, List<RenderableDataItem>>> it = categories.entrySet().iterator();
        rows.clear();
        UIColor bg = ColorUtils.getColor("spreadsheetCategoryBackground");
        RenderableDataItem emptyCatRow = new RenderableDataItem();
        emptyCatRow.setBackground(bg);
        emptyCatRow.setColumnSpan(-1);
        UIFont bold = Utils.getListWidgetBoldFont(table);
        iPlatformIcon icon = Functions.createEmptyIcon(0, 0, null);
        while (it.hasNext()) {
          Entry<String, List<RenderableDataItem>> e = it.next();
          String category = e.getKey();
          List<RenderableDataItem> clist = e.getValue();
          Collections.sort(clist, c);
          row = table.createRow(cols, false);
          row.setSelectable(false);
          row.setColumnSpan(-1);
          for (int i = 0; i < cols; i++) {
            row.add(emptyCatRow);
          }
          test = new RenderableDataItem(category);
          test.setBackground(bg);
          test.setColumnSpan(-1);
          test.setFont(bold);
          test.setIcon(icon);
          row.set(0, test);
          row.setLinkedData(category);
          rows.add(row);
          rows.addAll(clist);
        }
      }
      
      table.setAll(rows);
      Runnable r = new Runnable() {

        @Override
        public void run() {
          table.pageEndHorizontal();
          iWidget label = table.getFormViewer().getWidget("tableLabel");
          if (label != null) {
            int page = (int) ((spreadsheetPosition + spreadSheetPageSize - 1) / spreadSheetPageSize) + 1;
            int pageCount = (int) ((itemDates.length + spreadSheetPageSize - 1) / spreadSheetPageSize);
            String s = w.getString("bv.text.page_of_page", page, pageCount);
            label.setValue(s);
          }
          String key=keyPath==null ? null : keyPath.shift();
          if(key==null) {
            if(UIScreen.isLargeScreen()) {
              selectFirstChartableItem(table, true);
            }
          }
          else {
            handlePathKey(table, key, 0, true);
          }
        }
      };
      Platform.runOnUIThread(r);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      w.hideWaitCursor();
    }
  }

  public void showChartForSelectedItem(TableViewer table) {
    if (chartHandler != null && isChartable(table.getSelectedItem())) {
      WindowViewer w = Platform.getWindowViewer();
      StackPaneViewer stack = (StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack");
      if (stack == null) {
        String url = namePrefix + "_charts.rml";
        try {
          if (!UIScreen.isLargeScreen()) {
            if (currentView == ResultsView.SPREADSHEET) {
              spreadsheetTable = table;
            }
            Utils.pushWorkspaceViewer(url);
          } else {
            SplitPaneViewer sp = (SplitPaneViewer) table.getFormViewer();
            w.activateViewer(url, sp.getRegion(1).getName());

          }
        } catch (IOException e) {
          w.handleException(e);
        }
      } else {
        showChartForSelectedItemEx(table, stack, null, true);
      }
    } else if (UIScreen.isLargeScreen()) {
      clearCharts(table);
    }
  }
  protected void clearCharts(TableViewer table) {
    StackPaneViewer stack = (StackPaneViewer) Platform.getWindowViewer().getViewer("chartPaneStack");
    if (stack != null) {
      stack.removeAllViewers(true);
      stack.update();
      chartHandler.updateZoomButtons(stack.getFormViewer());
    } else {
      SplitPaneViewer sp = Utils.getSplitPaneViewer(table);
      if (sp != null) {
        iViewer v = sp.getRegion(1).removeViewer();
        if (v != null) {
          v.dispose();
          sp.update();
        }
      }
    }
    
  }
  
  public void showMostRecent(String eventName, iWidget widget, EventObject event) {

  }

  public void showNextResultSet(String eventName, iWidget widget, EventObject event) {

  }

  public void changeView(String eventName, iWidget widget, EventObject event) {
    String name = ((aGroupableButton) widget).getSelectedButtonName();
    if (name == null) {
      name = widget.getName();
    }
    changeView(name, widget);
  }

  protected void changeView(String name, iWidget widget) {
    final ResultsView view = ResultsView.valueOf(name.toUpperCase(Locale.US));
    if (view == currentView) {
      return;
    }
    if (view == ResultsView.SPREADSHEET && chartHandler != null) {
      chartHandler.resetChartPoints();
    }
    iContainer parent = widget.getFormViewer();
    selectionChecked = false;
    switch (view) {
      case TRENDS:
        currentView = view;
        showRegularTable(parent, true);
        break;
      case CHARTS:
        currentView = view;
        showRegularTable(parent, false);
        break;
      case SPREADSHEET:
        currentView = view;
        showSpreesheet(parent);
        break;
      default:
        changeViewEx(view);
        break;
    }
  }

  protected void changeViewEx(ResultsView view) {

  }

  public void showPreviousResultSet(String eventName, iWidget widget, EventObject event) {

  }

  protected void showChartForSelectedItemEx(final TableViewer table, final StackPaneViewer sp, final Boolean forward,
      final boolean horizontal) {
    final WindowViewer w = Platform.getWindowViewer();
    final RenderableDataItem row = table.getSelectedItem();
    final String key;
    final boolean spreadsheet = currentView == ResultsView.SPREADSHEET;
    if (spreadsheet) {
      key = (String) row.get(0).getLinkedData();
    } else {
      key = (String) row.get(NAME_POSITION).getLinkedData();
    }
    if (key == null) {
      return;
    }

    aWorkerTask task = new aWorkerTask() {

      @Override
      public Object compute() {
        try {
          return chartHandler.createSeries(originalRows, key, RANGE_POSITION);
        } catch (Exception e) {
          return e;
        }
      }

      @Override
      public void finish(Object result) {
        w.hideWaitCursor();
        if (result == null || table.isDisposed() || sp.isDisposed()) {
          return;
        }
        if (result instanceof Throwable) {
          table.handleException((Throwable) result);
          return;
        }
        ChartDataItem series = (ChartDataItem) result;
        ChartViewer cv = chartHandler.createChart(sp.getFormViewer(), key, 1, series);
        iTransitionAnimator ta = forward == null ? null : transitionAnimation;

        Utils.setViewerInStackPaneViewer(sp, cv, ta, forward == null ? true : forward.booleanValue(), horizontal, true);
        chartHandler.updateZoomButtons(sp.getFormViewer());
      }
    };
    w.spawn(task);
    w.showWaitCursor();
  }

  protected boolean hasCategories() {
    return false;
  }

  protected String getCategory(RenderableDataItem row) {
    return null;
  }

  protected void selectTableIndex(final TableViewer table, final int index) {
    if (!table.isDisposed()) {
      table.pageEndHorizontal();
      table.setSelectedIndex(index);
      table.fireActionForSelected();
      Platform.invokeLater(new Runnable() {

        @Override
        public void run() {
          table.scrollRowToVisible(index);
        }
      });
    }
  }

  /**
   * Updates the spreadsheet columns. This must be done on the UI thread
   * 
   * @param table
   *          the table to update
   */
  protected void updateSpreadsheetColumns(TableViewer table) {
    WindowViewer w = Platform.getWindowViewer();
    iDataConverter cvt = Platform.getAppContext().getDataConverter(DateTimeConverter.class);
    Date[] dates = itemDates;
    Object ctx = dateContext;
    int start = spreadsheetPosition;
    int end = Math.min(start + spreadSheetPageSize, dates.length);
    int col = 1;
    for (int i = start; i < end; i++) {
      table.getColumn(col).setLinkedData(dates[i]);
      table.setColumnTitle(col, (String) cvt.objectToString(w, dates[i], ctx));
      table.setColumnVisible(col, true);
      col++;
    }
    int len = table.getColumnCount() - RANGE_POSITION > -1 ? 1 : 0;
    for (int i = col; i < len; i++) {
      table.setColumnVisible(i, false);
    }
  }

  protected void changePage(iWidget widget, boolean forward, boolean jump) {
    if (widget.getParent().getName().equals("tableToolbar")) {
      if (forward) {
        if (jump) {
          spreadsheetPosition = Math.max(0, itemDates.length - spreadSheetPageSize);
        } else {
          spreadsheetPosition += spreadSheetPageSize;
          if (spreadsheetPosition + spreadSheetPageSize >= itemDates.length) {
            spreadsheetPosition = Math.max(0, itemDates.length - spreadSheetPageSize);
          }
        }
      } else {
        if (jump) {
          spreadsheetPosition = 0;
        } else {
          spreadsheetPosition = Math.max(0, spreadsheetPosition - spreadSheetPageSize);
        }
      }
      final TableViewer table = (TableViewer) widget.getFormViewer().getWidget("spreadsheet");
      updateNavigationButtons(widget.getFormViewer());
      updateSpreadsheetColumns(table);
      Platform.getWindowViewer().showWaitCursor();
      Platform.getAppContext().executeBackgroundTask(new Runnable() {

        @Override
        public void run() {
          updateSpreadsheetRows(table);
        }
      });
    }
  }

  protected void showSpreesheet(iContainer fv) {
    WindowViewer w = Platform.getWindowViewer();
    /**
     * create a context to convert the data values the first | piece of the
     * value is the format for converting from strings to dates and the second
     * piece is the format for converting from dates to strings strings. Were
     * are only converting to strings so we don't need to specify the first
     * piece
     */
    dateContext = Platform.getAppContext().getDataConverter(DateTimeConverter.class)
        .createContext(w, "|" + w.getString("bv.format.time.column_header"));
    Table cfg = (Table) w.createConfigurationObject("Table", "bv.table.spreadsheet");
    SPOTSet set = cfg.columns;
    ItemDescription prototype = (ItemDescription) set.getEx(1);
    int len = Math.min(spreadSheetPageSize, itemDates.length);
    for (int i = 0; i < len; i++) {
      set.add((ItemDescription) prototype.clone());
    }
    ((ItemDescription) set.getEx(0)).title.setValue(getSpeeedSheetColumnTitle());
    cfg.setEventHandler("onAction", "class:" + scriptClassName + "#onTableAction");
    final TableViewer table = (TableViewer) w.createViewer(fv, cfg);
    if (hasCategories()) {
      table.getColumn(0).setIcon(Platform.getResourceAsIcon("Rare.icon.empty"));
    }
    GroupBoxViewer gb = (GroupBoxViewer) dataTable.getParent();
    Object constraints = gb.getConsraints(dataTable);
    gb.removeWidget(dataTable);
    gb.addWidget(table, constraints, -1);
    if (fv instanceof SplitPaneViewer) {
      SplitPaneViewer sp = (SplitPaneViewer) fv;
      sp.setTopToBottom(true);
      sp.setAutoOrient(false);
      sp.setSplitProportions(0.5f);
      try {
        iViewer v = sp.getViewer(1);
        if (v==null || !v.getName().equals(namePrefix + "Charts")) {
          iTarget t = sp.getRegion(1);
          t.removeViewer();
          if(v!=null) {
            v.dispose();
          }
          w.activateViewer(namePrefix + "_charts.rml", t.getName());
        }
      } catch (Exception ex) {
        w.handleException(ex);
      }
    }
    setNavigationButtonsVisible(fv, true);
    updateNavigationButtons(fv);
    updateSpreadsheetColumns(table);
    if (chartHandler != null) {
      chartHandler.setChartPoints(spreadSheetPageSize);
    }

    w.showWaitCursor();
    Platform.getAppContext().executeBackgroundTask(new Runnable() {

      @Override
      public void run() {
        updateSpreadsheetRows(table);
      }
    });
  }

  protected void showRegularTable(iContainer fv, boolean trends) {
    SplitPaneViewer sp = null;
    if (fv instanceof SplitPaneViewer) {
      sp = (SplitPaneViewer) fv;
    }
    WindowViewer w = Platform.getWindowViewer();
    try {
      TableViewer table = (TableViewer) w.getViewer("spreadsheetTable");
      if (table != null) {
        GroupBoxViewer gb = (GroupBoxViewer) table.getParent();
        Object constraints = gb.getConsraints(table);
        gb.removeWidget(table);
        table.dispose();
        gb.addWidget(dataTable, constraints, -1);
        if (sp != null) {
          sp.setTopToBottom(!UIScreen.isWider());
          sp.setAutoOrient(true);
          sp.setSplitProportions(0.4f);

        }
        setNavigationButtonsVisible(fv, false);
        iWidget label = fv.getWidget("tableLabel");
        if (label != null) {
          label.setValue(" ");
        }
      }
      if (trends) {
        String url = namePrefix + "_trends.rml";
        dataTable.clearSelection();
        if (chartHandler != null) {
          chartHandler.resetChartPoints();
        }
        if (sp == null) {
          Utils.pushWorkspaceViewer(url);
        } else {
          w.activateViewer(url, sp.getRegion(1).getName());
        }
      } else {
        if (sp != null) {
          iViewer v = sp.getViewer(1);
          if (v!=null && v.getName().equals(namePrefix + "Charts")) {
            v.getFormViewer().getWidget("chartHeader").setVisible(true);
            String key=keyPath==null ? null : keyPath.shift();
            if(key==null) {
              if(!dataTable.hasSelection()) {
                selectFirstChartableItem(dataTable, true);
              }
              else {
                dataTable.fireActionForSelected();
              }
            }
            else {
              handlePathKey(table, key, 1, true);
            }
          } else {
            if (chartHandler != null) {
              chartHandler.resetChartPoints();
            }
            w.activateViewer(namePrefix + "_charts.rml", sp.getRegion(1).getName());
          }
        }
      }

      fv.update();
    } catch (IOException e) {
      w.handleException(e);
    }
  }

  protected String getSpeeedSheetColumnTitle() {
    return "";
  }

  protected void setNavigationButtonsVisible(iContainer fv, boolean visible) {
    iWidget button = fv.getWidget("nextPage");
    if (button != null) {
      button.setVisible(visible);
    }
    button = fv.getWidget("lastPage");
    if (button != null) {
      button.setVisible(visible);
    }
    button = fv.getWidget("firstPage");
    if (button != null) {
      button.setVisible(visible);
    }
    button = fv.getWidget("previousPage");
    if (button != null) {
      button.setVisible(visible);
    }

  }

  protected void updateNavigationButtons(iContainer fv) {
    iWidget button = fv.getWidget("nextPage");
    if (button != null) {
      button.setEnabled(spreadsheetPosition + spreadSheetPageSize < itemDates.length);
    }
    button = fv.getWidget("lastPage");
    if (button != null) {
      button.setEnabled(spreadsheetPosition + spreadSheetPageSize < itemDates.length);
    }
    button = fv.getWidget("firstPage");
    if (button != null) {
      button.setEnabled(spreadsheetPosition > 0);
    }
    button = fv.getWidget("previousPage");
    if (button != null) {
      button.setEnabled(spreadsheetPosition > 0);
    }

  }

  public void onChartsPanelLoaded(String eventName, iWidget widget, EventObject event) {
    chartsLoaded = true;
    StackPaneViewer sp = (StackPaneViewer) widget.getFormViewer().getWidget("chartPaneStack");
    if (sp != null) {
      transitionAnimation = sp.getTransitionAnimator();
      sp.setTransitionAnimator((iTransitionAnimator) null);
    }
    TableViewer table;
    if (currentView == ResultsView.SPREADSHEET) {
      table = spreadsheetTable;
      if (table == null) {
        table = (TableViewer) Platform.getWindowViewer().getViewer("spreadsheetTable");
      }
    } else {
      table = dataTable;
    }
    if (table != null) {
      aGroupableButton b = (aGroupableButton) table.getFormViewer().getWidget("charts");
      if (b != null && !b.isSelected() && isOnNonChartingView()) {
        b.setSelected(true);
        currentView = ResultsView.CHARTS;
      }
    }
    if (table != null) {
      if (table.hasSelection()) {
        showChartForSelectedItem(table);
      } else {
        String key = keyPath == null ? null : keyPath.shift();
        if (key != null) {
          handlePathKey(table, key, table==dataTable ? 1 : 0, chartsLoaded || !UIScreen.isLargeScreen());
        }
        else {
          selectFirstChartableItem(table, true);
        }
      }
    }
  }

  protected String getFirstChartableKey() {
    boolean spreadsheet = currentView == ResultsView.SPREADSHEET;
    TableViewer table;
    if (spreadsheet) {
      table = (TableViewer) Platform.getWindowViewer().getWidget("spreadsheetTable");
    } else {
      table = dataTable;
    }
    int index = getFirstChartableItem(table);
    if (index == -1) {
      return null;
    }
    RenderableDataItem row = table.get(index);
    if (spreadsheet) {
      return (String) row.get(0).getLinkedData();
    } else {
      return (String) row.get(NAME_POSITION).getLinkedData();
    }
  }

  protected String getSelectedChartableKey() {

    boolean spreadsheet = currentView == ResultsView.SPREADSHEET;
    TableViewer table;
    if (spreadsheet) {
      table = (TableViewer) Platform.getWindowViewer().getWidget("spreadsheetTable");
    } else {
      table = dataTable;
    }
    int index = table.getSelectedIndex();
    if (index == -1) {
      return null;
    }
    RenderableDataItem row = table.get(index);
    if (spreadsheet) {
      return (String) row.get(0).getLinkedData();
    } else {
      return (String) row.get(NAME_POSITION).getLinkedData();
    }
  }

  protected int getFirstChartableItem(TableViewer table) {
    int pos = -1;
    int len = table.size();
    if (!table.hasSelection()) {
      for (int i = 0; i < len; i++) {
        if (isChartable(table.get(i))) {
          pos = i;
          break;
        }
      }
    }
    return pos;
  }

  protected void selectFirstChartableItem(TableViewer table, boolean fireAction) {
    int pos = getFirstChartableItem(table);
    if (pos != -1) {
      table.setSelectedIndex(pos);
      if (fireAction) {
        table.fireActionForSelected();
      }
    }
  }

  protected boolean isOnNonChartingView() {
    switch (currentView) {
      case TRENDS:
      case DOCUMENT:
        return true;
      default:
        return false;
    }
  }

  /**
   * Called when the managers main viewer is disposed. We null out values to
   * prevent leaks on not garbage collecting platforms.
   */
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    Utils.removeActionPathSupporter(this);
    reset();
  }

  public void onFirstPage(String eventName, iWidget widget, EventObject event) {
    changePage(widget, false, true);

  }

  public void onLastPage(String eventName, iWidget widget, EventObject event) {
    changePage(widget, true, true);
  }

  public void onNextPage(String eventName, iWidget widget, EventObject event) {
    changePage(widget, true, false);
  }

  public void onPreviousPage(String eventName, iWidget widget, EventObject event) {
    changePage(widget, false, false);

  }

  /**
   * Called when the user clicks on a zoom in button
   */
  public void onZoomInAction(String eventName, iWidget widget, EventObject event) {
    if (chartHandler != null) {
      chartHandler.zoom(widget.getFormViewer(), true);
    }
  }

  /**
   * Called when the user clicks on a zoom out button
   */
  public void onZoomOutAction(String eventName, iWidget widget, EventObject event) {
    if (chartHandler != null) {
      chartHandler.zoom(widget.getFormViewer(), false);
    }
  }

  /**
   * Called when the user clicks on a table item
   */
  public void onTableAction(String eventName, iWidget widget, EventObject event) {
    showChartForSelectedItem((TableViewer) widget);
  }

  /**
   * Called when the table is first created and before it has been configured.
   * The event is a data event that contains the table configuration object.
   * 
   * We remove the data URL from the configuration object and handle the parsing
   * ourselves so we can massage it after it is parsed.
   */
  public void onTableCreated(String eventName, iWidget widget, EventObject event) {
    reset();
    Utils.addActionPathSupporter(this);
    DataEvent de = (DataEvent) event;
    Table cfg = (Table) de.getData();
    ActionLink link = new ActionLink(widget, cfg.dataURL);
    cfg.dataURL.spot_clear(); // clear it out so that the widget does not try to
                              // load it
    dataTable = (TableViewer) widget;
    parseDataURL((aWidget) widget, link, true);
  }

  /**
   * Called to reselect the default view button when the standard table is shown
   * in a UI without split pane
   * 
   * @param eventName
   * @param widget
   * @param event
   */
  public void reselectDefaultView(String eventName, iWidget widget, EventObject event) {
    iWidget cw = widget.getFormViewer().getWidget(spreadsheetTable == null ? "charts" : "spreadsheet");
    if (cw != null && !cw.isSelected()) {
      cw.setSelected(true);
    }
    spreadsheetTable = null;
  }

  /**
   * Called via the action for the timeframe button. This is called when the
   * button is clicked prior top the menu being shown.
   */
  public void onTimeframePopupAction(String eventName, iWidget widget, EventObject event) {
    iFilterableList<RenderableDataItem> menu = ((PushButtonWidget) widget).getItems();
    RenderableDataItem mi;

    if (widget.getLinkedData() == null) {
      JSONObject info = (JSONObject) Platform.getAppContext().getData(namePrefix + "Info");
      String s;
      int len = menu.size();
      for (int i = 0; i < len; i++) {
        mi = menu.get(i);
        if ("previous".equals(mi.getLinkedData())) {
          s = info.optString("previousResultSetMenuText");
          if (s != null && s.length() > 0) {
            s = widget.expandString(s, false);
            mi.setValue(Helper.expandString(s, StringCache.valueOf(dataPageSize)));
          }
        } else if ("next".equals(mi.getLinkedData())) {
          s = info.optString("nextResultSetMenuText");
          if (s != null && s.length() > 0) {
            s = widget.expandString(s, false);
            mi.setValue(Helper.expandString(s, StringCache.valueOf(dataPageSize)));
          }
        }
        mi.setEnabled(false);
      }
      widget.setLinkedData(Boolean.TRUE);
      widget.update();
    }
  }

  protected void slideToChartableItem(boolean next, boolean horizontal) {
    WindowViewer w = Platform.getWindowViewer();
    TableViewer table = spreadsheetTable;
    if(table==null) {
      table=(TableViewer) w.getViewer("spreadsheetTable");
    }
    if (table == null) {
      table = dataTable;
    }

    int n = table.getSelectedIndex();
    int len = table.size();
    int pos = -1;
    if (next) {
      for (int i = n + 1; i < len; i++) {
        if (isChartable(table.get(i))) {
          pos = i;
          break;
        }
      }
    } else {
      for (int i = n - 1; i > -1; i--) {
        if (isChartable(table.get(i))) {
          pos = i;
          break;
        }
      }
    }
    StackPaneViewer sp = (StackPaneViewer) w.getViewer("chartPaneStack");
    if (pos == -1) {
      Utils.showPullBackAnimation(sp.getActiveViewer(), horizontal, next ? false : true);
    } else {
      table.setSelectedIndex(pos);
      table.scrollRowToVisible(pos);
      showChartForSelectedItemEx(table, sp, next, horizontal);
    }
  }

  protected boolean isChartable(RenderableDataItem row) {
    if (row == null) {
      return false;
    }
    if (currentView == ResultsView.SPREADSHEET) {
      String key = (String) row.get(0).getLinkedData();
      if (key == null) {
        return false;
      }
      return true;
    }
    if (chartableItems == null) {
      chartableItems = new HashMap<String, Boolean>();
    }
    String key = (String) row.get(NAME_POSITION).getLinkedData();
    if (key == null) {
      return false;
    }
    Boolean b = chartableItems.get(key);
    if (b != null) {
      return b.booleanValue();
    }
    String s = row.get(VALUE_POSITION).toString().trim();
    char c = s.length() == 0 ? 0 : s.charAt(0);
    if (c == '-' && s.length() > 1) {
      c = s.charAt(1);
    }
    boolean chartable = Character.isDigit(c);
    chartableItems.put(key, chartable);
    return chartable;
  }

  protected void handlePathKey(TableViewer table, String key, int column, boolean fireAction) {
    int len=table.size();
    int n=-1;
    for(int i=0;i<len;i++) {
      RenderableDataItem item = table.getItem(i, column);
      String val = item == null ? null : (String) item.getLinkedData();
      if(key.equals(val)) {
        n=i;
        break;
      }
    }
    if(n!=-1) {
      table.setSelectedIndex(n);
      table.scrollRowToVisible(n);
      if (fireAction) {
        table.fireActionForSelected();
      }
    }
  }

  /**
   * Resets the state of the handler
   */
  protected void reset() {
    if (itemCounts != null) {
      itemCounts.clear();
    }
    if (itemDatesSet != null) {
      itemDatesSet.clear();
    }
    if (originalRows != null) {
      originalRows.clear();
    }
    currentView = ResultsView.CHARTS;
    if (dataTable != null && dataTable.getParent() == null) {
      dataTable.dispose();
    }
    if (spreadsheetTable != null && spreadsheetTable.getParent() == null) {
      spreadsheetTable.dispose();
    }
    dataTable = null;
    hasNoData = false;
    dataLoaded = false;
    originalRows = null;
    itemDates = null;
    chartsLoaded = false;
  }

}
