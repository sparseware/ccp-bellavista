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
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.appnativa.rare.Platform;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.BorderUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.ScreenUtils;
import com.appnativa.rare.ui.UIColorHelper;
import com.appnativa.rare.ui.chart.ChartDataItem;
import com.appnativa.rare.util.SubItemComparator;
import com.appnativa.rare.viewer.ChartViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.Helper;
import com.appnativa.util.MutableInteger;
import com.appnativa.util.NumberRange;
import com.appnativa.util.SNumber;
import com.appnativa.util.SimpleDateFormatEx;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

/**
 * This class manages the the creation of the Summary Vitals information. This
 * includes creating the vital ranges and creating the combination chart
 * 
 * @author Don DeCoteau
 *
 */
public class SummaryVitalsHandler extends aEventHandler {
  private static int DATE_COLUMN_POSITION  = 0;
  private static int NAME_COLUMN_POSITION  = 1;
  private static int VALUE_COLUMN_POSITION = 2;

  public SummaryVitalsHandler() {
  }

  /**
   * Calculates the vital ranges from the specified rows of data
   * and updates the UI as appropriate.
   * 
   * @param fv
   *          the form that will be updated
   * @param chart
   *          the chart to populate (can be null)
   * @param rows
   *          the rows of data
   * @param summary
   *          true if this if for a summary screen; false for the main vitals
   *          screen
   */
  public void calculateRangesAndUpdateUI(iFormViewer fv, ChartViewer chart, List<RenderableDataItem> rows, boolean summary) {
    int len = rows.size();
    if (len == 0) {
      return;
    }
    if (chart == null) {
      chart = (ChartViewer) fv.getWidget("summary_vitals_chart");
    }

    JSONObject chartInfo = ((JSONObject) Platform.getAppContext().getData("vitalsInfo")).getJSONObject("charts");
    JSONObject combo = chartInfo.getJSONObject(summary ? "summaryCombo" : "combo");
    if (summary && chart != null) {
      Double increment = null;
      String s = combo.optString("increment", null);
      if (s != null && s.length() > 0) {
        increment = SNumber.doubleValue(s);
      }
      chart.setRangeBounds(combo.optString("lowerBound", null), combo.optString("upperBound", null), increment);
    }
    JSONArray svitals = combo.getJSONArray("vitals");
    HashSet chartVitals = new HashSet(svitals);
    HashMap<String, VitalRange> vitals = new LinkedHashMap<String, VitalRange>();
    vitals.put("bp", new BPVitalRange(chart, chartInfo));
    vitals.put("map", new VitalRange("map", chart, chartInfo));
    vitals.put("temp", new VitalRange("temp", chart, chartInfo));
    vitals.put("resp", new VitalRange("resp", chart, chartInfo));
    vitals.put("hr", new VitalRange("hr", chart, chartInfo));
    vitals.put("pulse", new VitalRange("pulse", chart, chartInfo));
    SubItemComparator c = new SubItemComparator();
    c.setOptions(0, false);
    Collections.sort(rows, c);
    RenderableDataItem dateItem;
    dateItem = rows.get(0).get(DATE_COLUMN_POSITION);
    dateItem.setType(RenderableDataItem.TYPE_DATETIME);
    Date beg = (Date) dateItem.getValue();
    dateItem = rows.get(len - 1).get(DATE_COLUMN_POSITION);
    dateItem.setType(RenderableDataItem.TYPE_DATETIME);
    Date end = (Date) dateItem.getValue();
    len = Math.min(len, 5000);
    HashMap<Date, MutableInteger> dateMap = new HashMap<Date, MutableInteger>();
    for (int i = 0; i < len; i++) {
      RenderableDataItem row = rows.get(i);
      String key = (String) row.get(NAME_COLUMN_POSITION).getLinkedData();
      VitalRange vr = vitals.get(key);
      if (vr == null) {
        continue;
      }
      dateItem = row.get(DATE_COLUMN_POSITION);
      dateItem.setType(RenderableDataItem.TYPE_DATETIME);
      Date date = (Date) dateItem.getValue();
      MutableInteger mn = dateMap.get(date);
      if (mn == null) {
        mn = new MutableInteger(1);
        dateMap.put(date, mn);
      } else {
        mn.add(1);
      }
      vr.addValue(date, row.get(VALUE_COLUMN_POSITION).toString());
    }

    updateForm(fv, vitals, beg, end, chartVitals);
    if (chart != null) {
      chart.refreshItems();
    }
  }

  /**
   * This is meant to be called by a viewer on the form hosting
   * the vitals summary
   */
  public void onCreated(String eventName, iWidget widget, EventObject event) {
    ActionLink link = Utils.createLink(widget, "/hub/main/vitals/summary", true);
    parseDataURL((aWidget) widget, link, true);
  }

  /**
   * Updates the specified form with the vital ranges
   * 
   * @param fv
   *          the form to be updated
   * @param vitals
   *          the calculated vital ranges
   * @param beg
   *          the start date for our data range
   * @param end
   *          the end date for our data range
   * @param chartVitals
   *          the set of vitals that are to be displayed on the combo chart
   */
  public void updateForm(iContainer fv, Map<String, VitalRange> vitals, Date beg, Date end, HashSet chartVitals) {
    String s;
    WindowViewer w = Platform.getWindowViewer();
    if (ScreenUtils.isSmallScreen()) {
      int d = Helper.daysBetween(new Date(), end);

      if (d > 30) {
        return;
      }

      if (d < 2) {
        s = w.getString("bv.text.summary_vitals_24h");
      } else {
        s = w.getString("bv.text.summary_vitals_small", d);
      }
    } else {
      s = w.getString("bv.format.time.general_short");

      SimpleDateFormatEx df = new SimpleDateFormatEx(s);

      if (beg.equals(end)) {
        s = df.format(beg);
      } else {
        s = df.format(beg) + " - " + df.format(end);
      }

      s = w.getString("bv.text.summary_vitals", s);
    }

    TableViewer table = (TableViewer) fv.getWidget("vitals_table");
    iWidget label = fv.getWidget("vitals_description");
    if (label != null) {
      label.setValue(s);
    }
    Iterator<String> it = vitals.keySet().iterator();
    StringBuilder sb = new StringBuilder();
    SNumber num = new SNumber();
    while (it.hasNext()) {
      String name = it.next();
      VitalRange vr = vitals.get(name);
      if (chartVitals.contains(name)) {
        if (name.equals("bp")) {
          VitalRange map = vitals.get("map");
          if (map.hasValues()) { // if the server sent a map value use that
                                 // instead of the calculated one
            ((BPVitalRange) vr).removeMapSeries();
            vr.addToChart();
            continue;
          }
        }
        vr.addToChart();
      }
      name = vr.name;
      if (table != null) {
        s = w.getString("bv.text.vitals_" + name);
        RenderableDataItem row = table.createRow(2, true);
        row.get(0).setValue(s);
        ;
        s = vr.getRange(sb, num);
        if (s == null) {
          s = "---";
        }
        row.get(1).setValue(s);
        table.addEx(row);
      } else {
        label = fv.getWidget(name);
        if (label != null) {
          s = vr.getRange(sb, num);
          if (s != null) {
            label.setValue(s);
          }
        }
      }
    }
    if (table != null) {
      table.refreshItems();
    }
  }

  @Override
  protected void dataParsed(iWidget widget, List<RenderableDataItem> rows, ActionLink link) {
    calculateRangesAndUpdateUI(widget.getFormViewer(), null, rows, true);
  }

  /**
   * This class holds blood pressure specific ranges
   * 
   * @author Don DeCoteau
   *
   */
  static class BPVitalRange extends VitalRange {
    ChartDataItem bpSeries;
    int           bpLow;
    int           bpHigh;

    /**
     * Create a new range holder
     * 
     * @param chart
     *          the chart to associate with the range (can be null)
     * @param chartInfo
     *          object containing information about how to render vitals charts.
     *          Can be null only if chart is null.
     */
    public BPVitalRange(ChartViewer chart, JSONObject chartInfo) {
      super("map", chart, chartInfo);
      if (chart != null) {
        String legend = Platform.getResourceAsString("bv.text.vitals_bp");
        if (legend == null) {
          legend = name.toUpperCase(Locale.US);
        }
        bpSeries = ChartViewer.createSeries(legend);
        bpSeries.setValueContext("range_bar");
        boolean gray = Boolean.TRUE.equals(Utils.getPreferences().getBoolean("gray_charts", false));
        JSONObject attrs = chartInfo.getJSONObject("bp");
        String color = null;
        String border = null;
        if (attrs != null) {
          if (gray) {
            color = attrs.optString("color_g");

            if (color == null) {
              color = attrs.optString("color");
            }
          } else {
            color = attrs.optString("color");
          }
          border = attrs.optString("border");
        }

        if (color != null) {
          bpSeries.setBackgroundPainter(UIColorHelper.getBackgroundPainter(color));
        }
        if (border != null) {
          bpSeries.setBorder(BorderUtils.createBorder(chart, border, null));
        }
      }
    }

    @Override
    public void addToChart() {
      if (bpSeries != null) {
        chart.addSeries(bpSeries);
      }
      super.addToChart();
    }

    @Override
    public void addValue(Date date, String value) {
      int n = value.indexOf('/');
      NumberRange num = new NumberRange(new SNumber(value.substring(n + 1)).makeImmutable(),
          new SNumber(value.substring(0, n)).makeImmutable());

      int hi = num.getHighValue().intValue();
      int lo = num.getLowValue().intValue();
      float val = hi - lo;

      val *= .33;
      val += lo;
      val = (float) Math.floor(val);
      addValue(date, val);
      if (bpSeries != null) {
        ChartDataItem point;
        bpSeries.add(point = ChartViewer.createSeriesValue(date, num));
        point.setLinkedData(value);
        if (lo < bpLow) {
          bpLow = lo;
        }

        if (hi > bpHigh) {
          bpHigh = hi;
        }
      }
    }

    /**
     * Removes the MAP series for the BP range
     */
    public void removeMapSeries() {
      series = null;
    }
  }

  /**
   * Class that hold a range of vitals and information necessary for charting
   * that range
   * 
   * @author Don DeCoteau
   *
   */
  static class VitalRange {
    String        name;
    float         high;
    float         low;
    ChartDataItem series;
    ChartViewer   chart;
    boolean       first = true;

    /**
     * Create a new range holder
     * 
     * @param key
     *          the key for the range (unique id identifying the vital type)
     * @param chart
     *          the chart to associate with the range (can be null)
     * @param chartInfo
     *          object containing information about how to render vitals charts.
     *          Can be null only if chart is null.
     */
    public VitalRange(String key, ChartViewer chart, JSONObject chartInfo) {
      this.name = key;
      this.chart = chart;
      if (chart != null) {
        String legend = Platform.getResourceAsString("bv.text.vitals_" + key);
        if (legend == null) {
          legend = name.toUpperCase(Locale.US);
        }
        series = ChartViewer.createSeries(legend);
        JSONObject attrs = chartInfo.getJSONObject(key);

        series.setValueContext("line");
        series.setCustomProperty("plot.lineThickness", 3);
        boolean gray = Boolean.TRUE.equals(Utils.getPreferences().getBoolean("gray_charts", false));
        String color = null;
        if (attrs != null) {
          if (gray) {
            color = attrs.optString("color_g");

            if (color == null) {
              color = attrs.optString("color");
            }
          } else {
            color = attrs.optString("color");
          }
        }
        if (color != null) {
          series.setBackgroundPainter(UIColorHelper.getBackgroundPainter(color));
        }

      }
    }

    /**
     * Adds the range to the chart it is associated with
     */
    public void addToChart() {
      if (series != null && !series.isEmpty()) { //we only have a series if we were associated with a chart
        if (series.size() == 1) {
          series.setCustomProperty("plot.shapes", "filled");
        } else {
          series.setCustomProperty("plot.shapes", "none");
        }
        chart.addSeries(series);
      }
    }

    /**
     * Adds a value to the range
     * 
     * @param date
     *          the date of the value
     * @param value
     *          the value to add
     */
    public void addValue(Date date, String value) {
      addValue(date, SNumber.floatValue(value));
    }

    /**
     * Gets the rang as a display-able string
     * 
     * @param sb
     *          the string builder to use to store the range
     * @param num
     *          the number object to use for converting the number values to
     *          strings
     * @return
     */
    public String getRange(StringBuilder sb, SNumber num) {
      if (first) {
        return null;
      }
      sb.setLength(0);
      num.setValue(low);
      num.setScale(1);
      sb.append(num.toString());
      sb.append("-");
      num.setValue(high);
      num.setScale(1);
      sb.append(num.toString());
      return sb.toString();
    }

    /**
     * Gets whether the range has a chartable series
     * 
     * @return true if it does; false otherwise
     */
    public boolean hasChartSeries() {
      return series != null && !series.isEmpty();

    }

    public boolean hasValues() {
      return !first;
    }

    /**
     * Adds a value to the range
     * 
     * @param date
     *          the date of the value
     * @param value
     *          the value to add
     */
    protected void addValue(Date date, float value) {
      if (first) {
        first = false;
        low = value;
        high = value;
      } else {
        low = Math.min(low, value);
        high = Math.max(high, value);
      }

      if (series != null) {
        series.add(ChartViewer.createSeriesValue(date, new SNumber(value)));
      }
    }

  }
}
