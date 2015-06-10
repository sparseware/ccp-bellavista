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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.appnativa.rare.Platform;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.Label;
import com.appnativa.rare.ui.ColorUtils;
import com.appnativa.rare.ui.FontUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.RenderableDataItem.HorizontalAlign;
import com.appnativa.rare.ui.ScreenUtils;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIFontHelper;
import com.appnativa.rare.ui.aPlatformIcon;
import com.appnativa.rare.ui.iPlatformGraphics;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.painter.PaintBucket;
import com.appnativa.rare.ui.painter.iPlatformComponentPainter;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.widget.LabelWidget;
import com.appnativa.util.Helper;
import com.appnativa.util.SNumber;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

/**
 * This class manages the trending
 * 
 * @author Don DeCoteau
 */
public class TrendPanel {
  static Map<String, Map<String, String>> trendMap    = new LinkedHashMap<String, Map<String, String>>(4);
  String                                  name;
  String                                  title;
  Map<String, String>                     keyMap;
  Map<String, Trend>                      trends;
  Map<String,String> peers;
  boolean                                 reverseChronologicalOrder;
  static RenderableDataItem               dateLabel   = new RenderableDataItem();
  static RenderableDataItem               valueLabel  = new RenderableDataItem();
  static RenderableDataItem               trendLabel  = new RenderableDataItem();
  static RenderableDataItem               headerLabel = new RenderableDataItem();
  static UIFont                           nameFont;
  static UIFont                           timePeriodFont;
  static int                              timePeriodFontHeight;

  /**
   * Creates a new panel to hold trends for labs values corresponding to the
   * specified list of result keys
   * 
   * @param name
   *          the name of the panel
   * @param title
   *          the title for the panel
   * @param keys
   *          the list of keys that are allowed in this panel
   * @param reverseChronologicalOrder
   *          true if trends will be added in reverse chronological order; false
   *          otherwise
   */
  public TrendPanel(String name, String title, JSONArray keys, boolean reverseChronologicalOrder) {
    this.title = title;
    this.name = name;
    this.reverseChronologicalOrder = reverseChronologicalOrder;
    if (keys != null) {
      keyMap = trendMap.get(title);
      if (keyMap == null) {
        int len = keys.size();
        Map<String, String> map = new LinkedHashMap<String, String>(len);
        for (int i = 0; i < len; i++) {
          JSONObject o = keys.getJSONObject(i);
          String key=o.getString("key");
          map.put(key, o.getString("name"));
          String peer=o.optString("peer",null);
          if(peer!=null) {
            if(peers==null) {
              peers=new HashMap<String,String>(2);
            }
            peers.put(key,peer);
          }
        }
        keyMap = map;
        trendMap.put(title, map);
      }
    }
  }

  /**
   * Attempts to add a trend to the panel. The keyMap is used to find the name
   * of the result value. The trend will only be added if the key has been
   * mapped
   * 
   * @param key
   *          the key that uniquely identifies the type of result
   * @param date
   *          the date of the trend value
   * @param valueItem
   *          the trend value
   * @return true if it was added; false otherwise
   */
  public boolean addTrend(String key, Date date, RenderableDataItem valueItem) {
    String name = keyMap.get(key);
    if (name == null) {
      return false;
    }
    if (trends == null) {
      trends = new LinkedHashMap<String, Trend>();
    }
    Trend t = trends.get(key);
    if (t == null) {
      t = new Trend(name, reverseChronologicalOrder);
      trends.put(key, t);
    }
    t.add(date, valueItem.toString(), valueItem.getForeground());

    return true;
  }

  /**
   * Adds a trend to the panel
   * 
   * @param key
   *          the key that uniquely identifies the type of result
   * @param date
   *          the date of the trend value
   * @param name
   *          the name of the trend value
   * @param valueItem
   *          the trend value
   * 
   * @return
   */
  public boolean addTrend(String key, Date date, String name, RenderableDataItem valueItem) {
    if (trends == null) {
      trends = new LinkedHashMap<String, Trend>();
    }
    Trend t = trends.get(key);
    if (t == null) {
      t = new Trend(name, reverseChronologicalOrder);
      trends.put(key, t);
    }
    t.add(date, valueItem.toString(), valueItem.getForeground());

    return true;
  }

  public void clear() {
    if (trends != null) {
      trends.clear();
    }
  }

  /**
   * Removes the peer of a trend
   * that exists
   */
  public void removePeers() {
    if (peers != null && trends!=null) {
      Iterator<Entry<String, String>> it = peers.entrySet().iterator();
      while(it.hasNext()) {
        Entry<String, String> e = it.next();
        String key=e.getKey();
        if(trends.containsKey(key)) {
          String peer=e.getValue();
          trends.remove(peer);
          trendMap.remove(peer);
        }
      }
    }
  }

  /**
   * Populates a table with trends
   * 
   * @param table
   *          the table to populate
   * @param layout
   *          a map of field names/forms layout values
   */
  public void popuplateTable(TableViewer table, Map<String, String> layout) {
    WindowViewer w = Platform.getWindowViewer();
    if(timePeriodFont==null) {
      TrendPanel.setupLabels(w);
    }
    int colCount = table.getColumnCount();
    LabelWidget label = LabelWidget.create(table.getFormViewer(),
        (Label) w.createConfigurationObject("Label", "bv.label.trendPanelHeader"));
    label.setValue(title);

    RenderableDataItem header = table.createRow(colCount, true);
    header.setHeight(label.getPreferredSize().intHeight());
    RenderableDataItem item = header.get(0);
    RenderableDataItem col;
    item.setRenderingComponent(label.getContainerComponent());
    item.setColumnSpan(colCount);
    table.addEx(header);
    boolean singleColumn = table.getColumnCount() == 1;
    if (trends == null || trends.isEmpty()) {
      header = table.createRow(colCount, true);
      col = header.get(0);
      col.setColumnSpan(colCount);
      item = new RenderableDataItem(w.getString("bv.text.no_lab_trends"));
      item.setEnabled(false);
      item.setHorizontalAlignment(HorizontalAlign.CENTER);
      col.setCustomProperty(layout.get("name"), item);
      table.addEx(header);
      return;
    }
    int count = (int) Math.floor((trends.size() + 1) / colCount);
    List<RenderableDataItem> list = new ArrayList<RenderableDataItem>(count);
    for (int i = 0; i < count; i++) {
      list.add(table.createRow(colCount, true));
    }

    Iterator<String> it;
    if (keyMap == null) {
      it = trends.keySet().iterator();
    } else {
      it = keyMap.keySet().iterator();
    }
    RenderableDataItem row = null;
    int n = 0;
    int i = 0;
    PaintBucket pb = Platform.getUIDefaults().getPaintBucket("TrendPanel.trendName");
    iPlatformComponentPainter cp = pb == null ? null : pb.getCachedComponentPainter();
    UIFont font = nameFont;
    ;
    if (pb != null && pb.getFont() != null) {
      font = pb.getFont();
    }
    Object ctx = table.getColumn(0).getValueContext();
    while (it.hasNext()) {
      Trend t = trends.get(it.next());
      if (t == null) {
        continue;
      }
      if (n == 0) {
        row = list.get(i++);
      }
      col = row.get(n);
      row.setLinkedData(t);
      item = new RenderableDataItem(t.name);

      item.setFont(font);
      item.setComponentPainter(cp);
      col.setCustomProperty(layout.get("name"), item);
      item = new RenderableDataItem(t.value);
      if (t.colors[3] != null) {
        item.setForeground(t.colors[3]);
      }
      col.setCustomProperty(layout.get("valueValue"), item);
      item = new RenderableDataItem(t.date, RenderableDataItem.TYPE_DATETIME, null);
      item.setValueContext(ctx);
      col.setCustomProperty(layout.get("dateValue"), item);

      item = new RenderableDataItem("", null, t);
      col.setCustomProperty(layout.get("trendIcon"), item);

      col.setCustomProperty(layout.get("date"), dateLabel);
      col.setCustomProperty(layout.get("value"), valueLabel);
      if (!singleColumn) {
        n = 1 - n;
      }

    }
    for (i = 0; i < count; i++) {
      row = list.get(i);
      if (row.getLinkedData() != null) {
        row.setLinkedData(null);
        table.addEx(row);
      }
    }

  }

  /**
   * Populates a form with trends
   * 
   * @param fv
   *          the form to populate
   * @return a string representing the date range of the values added to the
   *         form
   */
  public String popuplateForm(iContainer fv) {
    WindowViewer w = Platform.getWindowViewer();
    if(timePeriodFont==null) {
      TrendPanel.setupLabels(w);
    }
    int count = Math.min(trends == null ? 0 : trends.size(), fv.getWidgetCount());
    if (count == 0) {
      return null;
    }
    String format = w.getString("bv.format.trend");
    Date beg = null;
    Date end = null;
    boolean html = format.startsWith("<html>");
    Iterator<String> it;
    if (keyMap == null) {
      it = trends.keySet().iterator();
    } else {
      it = keyMap.keySet().iterator();
    }

    StringBuilder sb = html ? new StringBuilder() : null;
    String fg;
    String dfg = ColorUtils.getForeground().toHexString();
    String pfg = html ? ColorUtils.getColor("clinicalPrompt").toHexString() : null;
    SimpleDateFormat df = new SimpleDateFormat(w.getString("bv.format.time.small_trend_date"));
    int i=0;
    while(i<count) {
      Trend t = trends.get(it.next());
      if(t==null) {
        continue;
      }
      if (t.colors[3] != null) {
        fg = t.colors[3].toHexString();
      } else {
        fg = dfg;
      }
      if (beg == null) {
        beg = t.startDate;
        end = t.date;
      } else {
        //values are in reverse chronological order
        if (t.date.after(end)) {
          end = t.date;
        }
        if (t.startDate.before(beg)) {
          beg = t.startDate;
        }
      }

      String date = df.format(t.date);
      if (html) {
        sb.setLength(0);
        sb.append("<font color=\"").append(pfg).append("\">");
        sb.append(date).append("</font>");
        date = sb.toString();
      }
      if(html && t.valueNeedsHTMLEncoding()) {
        t.value=Functions.escapeHTML(t.value, true, false);
      }
      String text = Functions.format(format, t.name, fg, t.value, date);
      LabelWidget label = (LabelWidget) fv.getWidget(i++);
      label.setValue(text);
      label.setIcon(t);
    }
    String s = w.getString("bv.format.time.general_short");

    df = new SimpleDateFormat(s);

    if (beg.equals(end)) {
      s = df.format(beg);
    } else {
      s = df.format(beg) + " - " + df.format(end);
    }

    return s;
  }

  /**
   * Sets up reuse-able items that will be used to label values on the trend
   * panel
   * 
   * @param w
   */
  public static void setupLabels(WindowViewer w) {
    dateLabel.setValue(w.getString("bv.text.date") + ":");
    valueLabel.setValue(w.getString("bv.text.value") + ":");
    trendLabel.setValue(w.getString("bv.text.trend") + ":");
    dateLabel.setHorizontalAlignment(HorizontalAlign.RIGHT);
    valueLabel.setHorizontalAlignment(HorizontalAlign.RIGHT);
    trendLabel.setHorizontalAlignment(HorizontalAlign.RIGHT);
    nameFont = FontUtils.getDefaultFont().deriveBold();
    timePeriodFont = FontUtils.getDefaultFont().deriveFontSize(FontUtils.getDefaultFont().getSize() - 4);
    timePeriodFontHeight = (int) FontUtils.getFontHeight(timePeriodFont, false) - ScreenUtils.PLATFORM_PIXELS_4;
    UIColor color = ColorUtils.getColor("clinicalPrompt");
    dateLabel.setForeground(color);
    trendLabel.setForeground(color);
    valueLabel.setForeground(color);
    UIFont font = FontUtils.getDefaultFont();
    font = font.deriveFontSize(font.getSize() - 1);
    dateLabel.setFont(font);
    valueLabel.setFont(font);
    trendLabel.setFont(font);
  }

  /**
   * This class paints the trend graph
   * 
   * @author Don DeCoteau
   *
   */
  public static class Trend extends aPlatformIcon {
    float[]         numbers        = new float[4];
    UIColor[]       colors         = new UIColor[4];
    int             index          = 4;
    String          name;
    Date            date;
    Date            startDate;
    String          value;
    int             segmentLength  = ScreenUtils.PLATFORM_PIXELS_8;
    int             startingY      = -1;
    private boolean drawTimePeriod = true;
    String          timePeroid;
    private float   timePeriodWidth;
    boolean         reverseChronologicalOrder;
    String peer;

    /**
     * Creates a new trend
     * 
     * @param name
     *          the name of the trend
     */
    public Trend(String name, boolean reverseChronologicalOrder) {
      this.name = name;
      this.reverseChronologicalOrder = reverseChronologicalOrder;
    }
    void setPeer(String peer) {
      this.peer=peer;
    }
    /**
     * Adds a value to the trend
     * 
     * @param date
     *          the date for the value
     * @param value
     *          the value
     * @param color
     *          the color for the value
     */
    void add(Date date, String value, UIColor color) {
      if (index > 0) {
        if (this.date == null) {
          this.startDate = date;
          this.date = date;
          this.value = value;
        }
        if (reverseChronologicalOrder) {
          this.startDate = date;
        } else {
          this.date = date;
        }
        numbers[--index] = new SNumber(value, false).floatValue();
        colors[index] = color;
      }
    }

    /**
     * Calculates the y position to start painting the trend line at
     */
    void calculateStartingYPositionm() {
      int total = getIconHeight() - (segmentLength * 2);
      int max = total;
      int y = total;
      int min = total;
      float lv = Float.NaN;
      for (int i = 0; i < 4; i++) {
        float num = numbers[i];
        if (Float.isNaN(lv)) {
          if (!SNumber.isEqual(lv, num)) {
            if (num > lv) {
              y -= segmentLength;
            } else {
              y += segmentLength;
            }
          }
          min = Math.min(y, min);
          max = Math.max(y, max);
        }
        lv = num;
      }
      int height = max - min;
      startingY = max - height - ScreenUtils.PLATFORM_PIXELS_4;
    }

    @Override
    public iPlatformIcon getDisabledVersion() {
      return this;
    }

    @Override
    public int getIconHeight() {
      return (segmentLength * 6) + (drawTimePeriod ? timePeriodFontHeight : 0);
    }

    @Override
    public int getIconWidth() {
      if (drawTimePeriod) {
        if (timePeroid == null) {
          calculateTimeperiod();
        }
      }
      return (int) Math.max(segmentLength * 6, timePeriodWidth + ScreenUtils.PLATFORM_PIXELS_4);
    }

    @Override
    public void paint(iPlatformGraphics g, float x, float y, float width, float height) {
      if (startingY == -1) {
        calculateStartingYPositionm();
      }
      int iw = getIconWidth();
      int ih = getIconHeight();
      y += (height - ih) / 2;
      width = iw;
      height = ih;
      float d1 = ScreenUtils.PLATFORM_PIXELS_1;
      float d2 = ScreenUtils.PLATFORM_PIXELS_2;
      //  y += ScreenUtils.PLATFORM_PIXELS_4;
      height -= ScreenUtils.PLATFORM_PIXELS_4;
      float sy = startingY + y;
      float ny = sy;
      float sx = x + ScreenUtils.PLATFORM_PIXELS_4;
      float lv = Float.NaN;
      UIColor oc = g.getColor();
      float sw = g.getStrokeWidth();
      g.setStrokeWidth(d1);
      boolean cardStack = Utils.isCardStack();
      if (!cardStack) {
        g.setPaint(ColorUtils.getColor("trendBackground"));
        g.fillRect(x, y, width, height);
      }
      g.setColor(ColorUtils.getControlShadow());
      g.drawRect(x, y, width, height);
      if (drawTimePeriod) {
        if (timePeroid == null) {
          calculateTimeperiod();
        }
        g.setFont(timePeriodFont);
        g.drawString(timePeroid, x + ((width - timePeriodWidth) / 2), y + ScreenUtils.PLATFORM_PIXELS_2, timePeriodFontHeight);
      }
      g.setStrokeWidth(d2);
      sy += (drawTimePeriod ? timePeriodFontHeight : 0);
      UIColor c;
      for (int i = 0; i < 4; i++) {
        float num = numbers[i];
        if (!Float.isNaN(lv)) {
          if (!SNumber.isEqual(lv, num)) {
            if (num > lv) {
              ny = sy - segmentLength;
            } else {
              ny = sy + segmentLength;
            }
          }

        }

        c = colors[i];
        g.setColor(c == null ? UIColor.GRAY : c);

        lv = num;
        g.drawLine(sx, sy, sx + segmentLength, ny);
        g.setColor(cardStack ? UIColor.WHITE : UIColor.BLACK);
        g.drawRoundRect(sx - d1, sy - d1, d2, d2, d2, d2);

        sy = ny;
        sx += segmentLength;
      }
      g.setColor(cardStack ? UIColor.WHITE : UIColor.BLACK);
      g.drawRoundRect(sx, sy - d1, d2, d2, d2, d2);
      g.setColor(oc);
      g.setStrokeWidth(sw);
    }

    boolean valueNeedsHTMLEncoding() {
      return value.indexOf('>') != -1 || value.indexOf('<') != -1;
    }

    /**
     * Calculates the time period the the trend represents
     */
    void calculateTimeperiod() {
      int days = Math.abs(Helper.daysBetween(startDate, date));
      if (days < 1) {
        days = 1;
      }
      SNumber num = new SNumber();
      String suf;
      if (days > 365) {
        num.setValue((float) (days / 365));
        num.setScale(1);
        suf = "year";
      } else if (days > 29) {
        num.setValue((float) (days / 30));
        num.setScale(1);
        suf = "month";
      } else {
        num.setValue(days);
        num.setScale(1);
        suf = "day";
      }
      StringBuilder sb = new StringBuilder();
      sb.append(suf);
      if (num.gt(1)) {
        sb.append("s");
      }
      suf = Platform.getResourceAsString("bv.text.time_trend." + sb.toString());
      sb.setLength(0);

      sb.append(num).append(suf);
      timePeroid = sb.toString();
      timePeriodWidth = UIFontHelper.stringWidth(timePeriodFont, timePeroid);
    }

    boolean isDrawTimePeriod() {
      return drawTimePeriod;
    }

    void setDrawTimePeriod(boolean drawTimePeriod) {
      this.drawTimePeriod = drawTimePeriod;
    }
  }

}
