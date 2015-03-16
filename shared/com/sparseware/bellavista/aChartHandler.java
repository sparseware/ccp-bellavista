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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.appnativa.rare.Platform;
import com.appnativa.rare.spot.Chart;
import com.appnativa.rare.spot.DataItem.CValueType;
import com.appnativa.rare.spot.GridCell;
import com.appnativa.rare.spot.ItemDescription;
import com.appnativa.rare.spot.Plot;
import com.appnativa.rare.spot.Plot.CShapes;
import com.appnativa.rare.ui.Column;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.ScreenUtils.Unit;
import com.appnativa.rare.ui.UIDimension;
import com.appnativa.rare.ui.UIFontMetrics;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.chart.ChartDataItem;
import com.appnativa.rare.ui.chart.ChartDefinition;
import com.appnativa.rare.viewer.ChartViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.CharScanner;
import com.appnativa.util.NumberRange;
import com.appnativa.util.SNumber;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONObject;

public abstract class aChartHandler {
  public static float         MIN_ANGLED_LABEL_HEIGHT = UIScreen.toPlatformPixels(400, Unit.POINT, false);

  static final String         TOTAL_POINT_LABELS_SIZE = "maxValueCharCount";
  /** the info to use to configure the handler */
  protected JSONObject        chartsInfo;
  /** holds a mapping of chart types for chart item item keys */
  private Map<String, String> chartTypeMap            = new LinkedHashMap<String, String>(4);
  /** the column to use for chart name */
  protected int               nameColumn;
  /** the column to use for chart dates */
  protected int               dateColumn;
  /** the column to use for chart values */
  protected int               valueColumn;
  /** name of the class that will handle chart events */
  protected String            eventHandlerClass;
  /** the number of points to page by when zooming */
  protected int               chartPageSize           = 7;
  /** the number of points to initially display */
  protected int               chartPoints             = 7;

  public aChartHandler(String eventHandlerClass, JSONObject chartsInfo, int nameColumn, int dateColumn, int valueColumn) {
    this.chartsInfo = chartsInfo;
    this.nameColumn = nameColumn;
    this.dateColumn = dateColumn;
    this.valueColumn = valueColumn;
    this.eventHandlerClass = eventHandlerClass;
  }

  public boolean canZoomIn(ChartViewer cv) {
    ChartDefinition cd = cv.getChartDefinition();
    int count = cd.getSeriesCount();
    for (int n = 0; n < count; n++) {
      if (cd.getSeries(n).size() > chartPageSize) {
        return true;
      }
    }
    return false;
  }

  public boolean canZoomOut(ChartViewer cv) {
    ChartDefinition cd = cv.getChartDefinition();
    int count = cd.getSeriesCount();
    for (int n = 0; n < count; n++) {
      if (cd.getSeries(n).isFiltered()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates a new chart viewer
   *
   * @param fv
   *          the charts parent form
   * @param rows
   *          the values for the chart
   * @param key
   *          then key for the chart
   * @param viewers
   *          the number of viewers being creates (use to decide on font size)
   * @param series
   *          the series of data points to display
   * @return the new chart viewer
   */
  public ChartViewer createChart(iFormViewer fv, String key, int viewers, ChartDataItem series) {
    Chart cfg = new Chart();
    ChartViewer viewer;
    cfg.setEventHandler("onFling", "class:" + eventHandlerClass + "#onChartFling");
    cfg.setEventHandler("onScale", "class:" + eventHandlerClass + "#onChartScale");
    cfg.setEventHandler("onResize", "class:" + eventHandlerClass + "#onChartResize");

    int len = series.size();
    if (viewers == 1) {
      cfg.font.size.setValue("-2");
      cfg.getPlotReference().labels.setValue(Plot.CLabels.linked_data);
    } else {
      cfg.font.size.setValue("-4");
      cfg.domainAxis.visible.setValue(false);
    }
    cfg.domainAxis.fgColor.setValue("darkBorder");
    cfg.rangeAxis.fgColor.setValue("darkBorder");
    cfg.domainAxis.spot_setAttribute("labelColor", "Rare.Chart.foreground");
    cfg.rangeAxis.spot_setAttribute("labelColor", "Rare.Chart.foreground");
    cfg.getPlotReference().borderColor.setValue("darkBorder");
    cfg.verticalAlign.setValue(Chart.CVerticalAlign.full);
    cfg.setHorizontalAlignment(Chart.CHorizontalAlign.full);
    viewer = new ChartViewer(fv);
    configureChart(viewer, cfg, key, series);
    if (len > chartPoints) {
      for (int i = len - chartPoints; i < len; i++) {
        series.addIndexToFilteredList(i);
      }
    }
    viewer.configure(cfg);
    viewer.addSeries(series);
    String range = (String) series.getLinkedData();
    if (range != null) {
      viewer.addRangeMarker(range, '-');
    }

    viewer.setLinkedData(key);
    viewer.rebuildChart();
    viewer.setPlotValuesVisible(false);
    return viewer;
  }

  public LinkedHashMap<String, ChartDataItem> createSeries(List<RenderableDataItem> rows, Map<String, String> keys, int rangeColumn) {
    int dateCol = dateColumn;
    int nameCol = nameColumn;
    int valueCol = valueColumn;
    int len = rows.size();
    RenderableDataItem row, item;
    LinkedHashMap<String, ChartDataItem> seriesMap = new LinkedHashMap<String, ChartDataItem>(keys.size());
    for (int i = 0; i < len; i++) {
      row = rows.get(i);
      item = row.get(nameCol);
      String key = (String) item.getLinkedData();
      String type = keys.get(key);
      if (type == null) {
        continue;
      }
      ChartDataItem series = seriesMap.get(key);
      if (series == null) {
        String s = (String) item.getValue();
        series = ChartViewer.createSeries(s);
        series.setTitle(s);
        seriesMap.put(key, series);
        if (rangeColumn > -1) {
          s = (String) row.get(rangeColumn).getValue();
          if (s != null && s.length() > 0) {
            series.setLinkedData(s);
          }
        }

      }
      Date date = (Date) row.get(dateCol).getValue();
      item = row.get(valueCol);
      String value = (String) item.getValue();
      Number num = createNumber(type, value);
      if (num != null) {
        ChartDataItem di;
        series.add(di = ChartViewer.createSeriesValue(date, num));
        di.setForeground(item.getForeground());
        int n = value.indexOf(' ');
        if (n != -1) {
          value = value.substring(0, n);
        }
        di.setLinkedData(value);
      }
    }
    return seriesMap;
  }

  public void adjustForSize(ChartViewer cv) {
    int oangle=cv.getDomainAxis().getAngle();
    boolean oshow=cv.getChartDefinition().isShowPlotLabels();
    int angle=getDomainAngleBasedOnPlotSize(cv);
    boolean show=shouldPlotLabelsBeVisible(cv);
    if(show!=oshow || angle!=oangle) {
      ChartDefinition cd=cv.getChartDefinition();
      cd.setShowPlotLabels(show);
      cd.getDomainAxis().setAngle(angle);
      cv.rebuildChart();
    }
  }

  public int getDomainAngleBasedOnPlotSize(ChartViewer cv) {
    ChartDefinition cd = cv.getChartDefinition();
    UIDimension size = cv.getPlotAreaSize();
    if (cv.getCustomProperty("bv_dont_mess_with_angle") == null) {
      if (size.height < MIN_ANGLED_LABEL_HEIGHT) {
        return 0;
      } else {
        return -90;
      }
    }
    return cd.getDomainAxis().getAngle();
  }

  protected boolean shouldPlotLabelsBeVisible(ChartViewer cv) {
    ChartDefinition cd = cv.getChartDefinition();
    UIDimension size = cv.getPlotAreaSize();
    if (cd.getSeriesCount() == 1) {
      ChartDataItem series = cd.getSeries(0);
      Integer pointWidth = (Integer) series.getCustomProperty(aChartHandler.TOTAL_POINT_LABELS_SIZE);
      if (pointWidth == null) {
        UIFontMetrics fm = UIFontMetrics.getMetrics(cv.getFont());
        int len = series.size();
        int count = 0;
        int w=0;
        int mw=0;
        for (int i = 0; i < len; i++) {
          String s = (String) series.get(i).getLinkedData();
          if (s != null) {
            w=fm.stringWidth(s);
            count += w;
            if(w>mw) {
              mw=w;
            }
          }
        }
        count += mw*6;
        pointWidth = count;
        series.setCustomProperty(aChartHandler.TOTAL_POINT_LABELS_SIZE, pointWidth);
      }
      return size.width > pointWidth;
    } else {
      return false;
    }
  }

  public ChartDataItem createSeries(List<RenderableDataItem> rows, String key, int rangeColumn) {
    String type = getChartType(key);
    Map<String, String> map = chartTypeMap;
    map.clear();
    map.put(key, type);
    return createSeries(rows, map, rangeColumn).get(key);
  }

  public ChartDataItem createSeriesFromSpreadSheet(TableViewer table, String key, RenderableDataItem row, int rangeColumn) {
    int len = table.getColumnCount();
    String s = (String) row.get(0).getValue();
    ChartDataItem series = ChartViewer.createSeries(s);
    series.setTitle(s);
    if (rangeColumn > -1) {
      s = (String) row.get(rangeColumn).getValue();
      if (s != null && s.length() > 0) {
        series.setLinkedData(s);
      }
    }
    String type = getChartType(key);
    for (int i = 0; i < len; i++) {
      Column col = table.getColumn(i);
      if (!col.isVisible() || !(col.getLinkedData() instanceof Date)) {
        continue;
      }
      Date date = (Date) col.getLinkedData();
      RenderableDataItem item = row.get(i);
      s = item == null ? null : (String) item.getValue();
      Number num = s == null ? null : createNumber(type, s);
      if (num != null) {
        series.add(ChartViewer.createSeriesValue(date, num));
      }
    }
    return series;
  }

  public int getChartPageSize() {
    return chartPageSize;
  }

  public int getChartPoints() {
    return chartPoints;
  }

  public void resetChartPoints() {
    chartPoints = chartPageSize;
  }

  public void setChartPageSize(int chartPageSize) {
    this.chartPageSize = chartPageSize;
    this.chartPoints = chartPageSize;
  }

  public void setChartPoints(int chartPoints) {
    this.chartPoints = chartPoints;
  }

  public void updateZoomButtons(iContainer fv) {
    boolean zoomin = false;
    boolean zoomout = false;
    int min = 0;
    int max = 0;
    ChartViewer cv;
    StackPaneViewer sp = (StackPaneViewer) fv.getWidget("chartPaneStack");
    iViewer v = sp.getActiveViewer();
    if (v instanceof ChartViewer) {
      cv = (ChartViewer) v;
      if (canZoomIn(cv)) {
        zoomin = true;
      }
      if (canZoomOut(cv)) {
        zoomout = true;
      }
      NumberRange r = getZoomRange(cv);
      max = r.getHighValue().intValue();
      min = r.getLowValue().intValue();
    } else if (v != null) {
      List<iWidget> widgets = v.getContainerViewer().getWidgetList();
      for (iWidget w : widgets) {
        if (w instanceof ChartViewer) {
          cv = (ChartViewer) w;
          if (canZoomIn(cv)) {
            zoomin = true;
          }
          if (canZoomOut(cv)) {
            zoomout = true;
          }
          NumberRange r = getZoomRange(cv);
          if (r.getHighValue().intValue() > max) {
            max = r.getHighValue().intValue();
            min = r.getLowValue().intValue();
          }
        }
      }
    }
    iWidget w = fv.getWidget("zoomin");
    if (w != null) {
      w.setEnabled(zoomin);
    }
    w = fv.getWidget("zoomout");
    if (w != null) {
      w.setEnabled(zoomout);
    }
    w = fv.getWidget("chartLabel");
    if (w != null) {
      String s;
      if (max == 0) {
        s = "";
      } else {
        s = Platform.getWindowViewer().getString("bv.text.chart_point_range", StringCache.valueOf(min), StringCache.valueOf(max));
      }
      w.setValue(s);
    }
  }

  protected NumberRange getZoomRange(ChartViewer cv) {
    ChartDefinition cd = cv.getChartDefinition();
    int count = cd.getSeriesCount();
    int max = 0;
    int min = 0;
    for (int n = 0; n < count; n++) {
      ChartDataItem series = cd.getSeries(n);
      int nmax = series.getUnfilteredList().size();
      if (nmax > max) {
        max = nmax;
        min = series.size();
      }
    }
    return new NumberRange(min, max);
  }

  /**
   * Handles zooming in/out of charts. This is accomplished by simply filtering
   * the list of points to only contain the points we want to show
   * 
   * @param cv
   *          the chart viewer
   * @param in
   *          true to zoom in (show less points); false to soom out (show more
   *          points)
   * @param update
   *          true to update zoom in/out buttons, show shake animation; false
   *          otherwise
   * @return true if the chart was zoomed; false otherwise
   */
  public boolean zoom(ChartViewer cv, boolean in, boolean update) {
    ChartDefinition cd = cv.getChartDefinition();
    int count = cd.getSeriesCount();
    int cp = 0;
    boolean zoomed = false;
    for (int n = 0; n < count; n++) {
      ChartDataItem series = cd.getSeries(n);
      int len = series.size();
      boolean filtered = series.isFiltered();
      if (in) {
        if (len > chartPageSize) {
          int start = len - chartPageSize;
          if (start < chartPageSize) {
            start = chartPageSize;
          }
          series.unfilter();
          len = series.size();
          start = len - start;
          for (int i = start; i < len; i++) {
            series.addIndexToFilteredList(i);
          }
          series.setCustomProperty(aChartHandler.TOTAL_POINT_LABELS_SIZE, null);
          zoomed = true;
        }
      } else if (filtered) {
        int clen = series.getUnfilteredList().size();
        int end = clen - len;
        len += chartPageSize;
        chartPoints += chartPageSize;
        if (end <= 0) {
          series.unfilter();
        } else {
          int start = Math.max(0, end - chartPageSize);
          if (start == 0) {
            series.unfilter();
          } else {
            for (int i = start; i < end; i++) {
              series.addIndexToFilteredList(i);
            }
          }
        }
        series.setCustomProperty(aChartHandler.TOTAL_POINT_LABELS_SIZE, null);
        zoomed = true;
      }
      cp = Math.max(cp, series.size());
    }
    if (zoomed) {
      cp = (cp + chartPageSize - 1) / chartPageSize;
      chartPoints = Math.max(cp, 1) * chartPageSize;
      cd.getDomainAxis().setAngle(getDomainAngleBasedOnPlotSize(cv));
      cd.setShowPlotLabels(shouldPlotLabelsBeVisible(cv));
      cv.rebuildChart();
      if (update) {
        updateZoomButtons(cv.getFormViewer());
      }
    } else if (update) {
      Utils.showShakeAnimation(cv);
    }
    return zoomed;
  }

  /**
   * Handles zooming in/out of charts. This is accomplished by simply filtering
   * the list of points to only contain the points we want to show. The method
   * will look for all chart viewers and zoom them
   * 
   * @param fv
   *          the form viewer containing chart viewers the chart viewer
   * @param in
   *          true to zoom in (show less points); false to soom out (show more
   *          points)
   */
  public void zoom(iContainer fv, boolean in) {
    StackPaneViewer sp = (StackPaneViewer) fv.getWidget("chartPaneStack");
    iViewer v = sp.getActiveViewer();
    if (v instanceof ChartViewer) {
      zoom((ChartViewer) v, in, true);
    } else {
      boolean zoomed = false;
      List<iWidget> widgets = v.getContainerViewer().getWidgetList();
      for (iWidget w : widgets) {
        if (w instanceof ChartViewer) {
          if (zoom((ChartViewer) w, in, false)) {
            zoomed = true;
          }
        }
      }
      if (zoomed) {
        updateZoomButtons(fv);
      } else {
        Utils.showShakeAnimation(v);
      }
    }
  }

  /**
   * Configures a chart configuration object for the specified type of chart.
   * The chart specific configuration is defined as part of the application and
   * can be accessed as global attributes/data.
   *
   * @param context
   *          the context
   * @param cfg
   *          the chart configuration object
   * @param key
   *          the key
   */
  protected void configureChart(iWidget context, Chart cfg, String key, ChartDataItem series) {

    JSONObject o = chartsInfo.optJSONObject(key);
    Map<String, String> attrs = o == null ? Collections.EMPTY_MAP : o.getObjectMap();

    boolean gray = Utils.getPreferences().getBoolean("gray_charts", false);

    cfg.showLegends.setValue(false);
    cfg.autoSort.setValue(true);
    cfg.rangeAxis.valueType.setValue(CValueType.integer_type);
    cfg.rangeAxis.spot_setAttribute("lowerBound", attrs.get("lowerBound"));
    cfg.rangeAxis.spot_setAttribute("tickIncrement", attrs.get("tickIncrement"));
    cfg.rangeAxis.spot_setAttribute("upperBound", attrs.get("upperBound"));
    cfg.rangeAxis.valueContext.setValue(attrs.get("rangeContext"));
    cfg.rangeAxis.spot_setAttribute("label", attrs.get("range"));
    cfg.domainAxis.valueType.setValue(CValueType.date_time_type);
    cfg.zoomingAllowed.setValue(false);

    String s = attrs.get("domainContext");

    cfg.domainAxis.valueContext.setValue((s == null) ? "|M/d@HH:mm' '" : s);
    s = attrs.get("domain");
    cfg.domainAxis.spot_setAttribute("label", (s == null) ? "Date" : s);
    s = attrs.get("chartType");

    if (s == null) {
      cfg.chartType.setValue(Chart.CChartType.line);
    } else {
      cfg.chartType.setValue(s);
    }

    cfg.domainAxis.spot_setAttribute("timeUnit", "none");

    if (cfg.chartType.intValue() == Chart.CChartType.line) {
      cfg.getPlotReference().lineThickness.setValue(2);
      cfg.getPlotReference().shapes.setValue(CShapes.filled_and_outlined);
    }

    if (gray) {
      s = (cfg.chartType.intValue() == Chart.CChartType.line) ? "lineChartColor_g" : "barChartColor_g";
    } else {
      s = (cfg.chartType.intValue() == Chart.CChartType.line) ? "lineChartColor" : "barChartColor";
    }
    cfg.rangeAxis.setValue(series.getTitle());
    cfg.rangeAxis.getGridCellReference().bgColor.setValue(s);

    if (s.indexOf(',') != -1) {
      cfg.rangeAxis.getGridCellReference().bgColor.spot_setAttribute("direction", "horizontal_left");
    }

    s = attrs.get("rangeMarker");

    if (s != null) {
      ItemDescription marker = new ItemDescription();
      marker.value.setValue(s);
      cfg.getRangeMarkersReference().add(marker);
    }

    s = attrs.get("border");

    if (s != null) {
      GridCell.CBorder b = cfg.rangeAxis.getGridCellReference().addBorder(s);

      s = attrs.get("border_attributes");

      if (s != null) {
        b.spot_addAttributes(CharScanner.parseOptionStringEx(s, ','));
      }
    }
  }

  protected String getChartType(String key) {

    JSONObject o = chartsInfo.optJSONObject(key);
    return o == null ? "line" : o.optString("chartType", "line");
  }

  protected static Number createNumber(String type, String value) {
    Number num;
    value = value.trim();
    if (type.startsWith("range")) {
      int n = value.indexOf('/');
      if (n != -1) {
        num = new NumberRange(new SNumber(value.substring(n + 1).trim()), new SNumber(value.substring(0, n)));
      } else {
        n = value.indexOf('-');
        if (n == -1) {
          return null;
        }
        num = new NumberRange(new SNumber(value.substring(0, n)), new SNumber(value.substring(n + 1).trim()));
      }
    } else {
      num = new SNumber(value);
    }
    return num;
  }

}
