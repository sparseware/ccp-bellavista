package com.sparseware.bellavista.oe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.util.ObjectCache;
import com.appnativa.util.RegularExpressionFilter;
import com.appnativa.util.SNumber;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.Orders;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.oe.Order.ActionType;
// URLS
// /hub/main/util/ordering/discontinue_fields>
// /hub/main/util/ordering/procedures/sites/<itemid>
// /hub/main/util/ordering/labs/specimens/<itemid>
// /hub/main/orders/discontinue_fields/<orderid>/<itemid>
// specimen and site fields
// id^display^primary

public class OrderFields {
  public static String ID                    = "id";
  public static String MIN_VALUE             = "min_value";
  public static String MAX_VALUE             = "max_value";
  public static String DEFAULT_VALUE         = "default_value";
  public static String DEFAULT_VALUE_DISPLAY = "default_value_display";
  public static String INCREMENT             = "increment";
  public static String EDITABLE              = "editable";
  public static String ENABLED               = "enabled";
  public static String VISIBLE               = "visible";
  public static String DECIMAL_PLACES        = "decimal_places";
  public static String INPUT_MASK            = "input_mask";
  public static String TYPE                  = "type";
  public static String REQUIRED              = "required";
  public static String INPREVIEW             = "in_preview";
  public static String PREVIEW_PREFIX        = "preview_prefix";
  public static String PREVIEW_SUFFIX        = "preview_suffix";
  public static String SEQUENCE_NUMBER       = "seq_number";
  public static String MAX_OCCURRENCES       = "max_occurrences";
  public static String PROMPT                = "prompt";
  public static String DESCRIPTION           = "description";
  public static String DATA_URL              = "data_url";
  public static String FIELDS                = "fields";
  public static String FIELD_TYPE            = "_ftype";
  public static String WIDGET_COUNT          = "_widgetCount";
  public static String INPUT_FILTER          = "_inputFilter";
  public static String SEARCHFIELD_NAME      = "search_field_name";
  public static String SEARCHFIELD_EMPTYTEX  = "search_field_emptytext";
  static ObjectCache   orderingFields;
  static ObjectCache   cachedLists;
  String               id;
  List<JSONObject>     fields;
  ActionType           orderType;
  String               dataHref;
  boolean              loaded;
  private int widgetCount=-1;

  public OrderFields(String id, String href) {
    this.id  = id;
    dataHref = href;
  }

  public OrderFields(String id, List<JSONObject> fields) {
    this.id     = id;
    this.fields = fields;
  }

  public OrderFields(String id, String href, boolean load) {
    this.id  = id;
    dataHref = href;

    if (load) {
      load(null);
    }
  }

  public List<RenderableDataItem> createTableValues(Order order, UIFont requiredFieldFont) {
    Map<String, FieldValue>  values = order.orderValues;
    int                      len    = fields.size();
    List<RenderableDataItem> rows   = new ArrayList<RenderableDataItem>(len);
    List<JSONObject>         list   = fields;
    String                   s;

    if (values == null) {
      values = order.orderValues = new HashMap<String, FieldValue>(len);
    }
    WindowViewer w=Platform.getWindowViewer();
    for (int i = 0; i < len; i++) {
      JSONObject field = list.get(i);
      String     id    = field.optString(ID, null);

      if (id == null) {
        continue;
      }

      FieldType type = (FieldType) field.opt(FIELD_TYPE);

      if (type == null) {
        s    = field.getString(TYPE).toUpperCase(Locale.US);
        type = FieldType.valueOf(s);
        field.put(FIELD_TYPE, type);
      }

      int        itype = RenderableDataItem.TYPE_STRUCT;
      FieldValue value = values.get(id);

      if (value == null) {
        value = new FieldValue(field);
        values.put(id, value);
      } else if ((value.field == null) && (value.value != null)) {    //supports reset to default functionality
        JSONObject ff = new JSONObject();

        ff.putAll(field);
        ff.put(DEFAULT_VALUE, value.value);
        ff.put(DEFAULT_VALUE_DISPLAY, value.displayValue);
        field = ff;
      }

      value.field = field;

      if (isHidden(field)) {                                          //do here so that we ensure that the FieldValue object is created before we bail
        continue;
      }

      RenderableDataItem valueItem = new RenderableDataItem(value, itype, null);
      RenderableDataItem nameItem  = new RenderableDataItem(w.expandString(field.getString(OrderFields.PROMPT)));
      RenderableDataItem row       = new RenderableDataItem();

      row.add(nameItem);
      row.add(valueItem);

      if (isRequired(field)) {
        row.setUserStateFlag(Orders.REQUIRED_FLAG);
        row.setFont(requiredFieldFont);
      }

      if (!isEditable(field)) {
        row.setSelectable(false);
      }

      row.setLinkedData(value);
      rows.add(row);
    }

    return rows;
  }

  public List<JSONObject> getFields() {
    return fields;
  }

  public String getID() {
    return id;
  }

  public boolean isLoaded() {
    return loaded;
  }

  public void load(final iFunctionCallback cb) {
    if ((cb == null) &&!Platform.isUIThread()) {
      try {
        loadEx();
      } catch(Exception e) {
        throw ApplicationException.runtimeException(e);
      }
    }

    aWorkerTask task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          loadEx();

          return fields;
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        if (result instanceof Exception) {
          if (cb != null) {
            cb.finished(true, result);
          }
        } else if (cb != null) {
          cb.finished(false, fields);
        }
      }
    };

    Platform.getWindowViewer().spawn(task);
  }

  protected void loadEx() throws Exception {
    try {
      ActionLink l = new ActionLink(dataHref);
      JSONObject o = new JSONObject(l.getContentAsString());
      JSONArray  a = o.getJSONArray("rows");

      if (a == null) {
        throw new ApplicationException(dataHref + " is not a valid fields definition");
      }

      fields = (List<JSONObject>) a.getObjectList();
      Collections.sort(fields, new Comparator<JSONObject>() {
        @Override
        public int compare(JSONObject o1, JSONObject o2) {
          double d1 = o1.optDouble(SEQUENCE_NUMBER, 999999999);
          double d2 = o2.optDouble(SEQUENCE_NUMBER, 999999999);

          return Double.compare(d1, d2);
        }
      });
    } finally {
      loaded = true;
    }
  }

  public static String getDescription(JSONObject field) {
    return field.getString(DESCRIPTION);
  }

  public int getWidgetCount() {
    int count = widgetCount;

    if (count < 0) {
      count = 0;

      for (JSONObject field : fields) {
        if (OrderFields.isEditable(field) ||!OrderFields.isHidden(field)) {
          count++;
        }
      }

      widgetCount=count;
    }

    return count;
  }
  public static int getWidgetCount(JSONObject info) {
    int count = info.optInt(WIDGET_COUNT, -1);

    if (count < 0) {
      JSONArray a = info.getJSONArray("fields");

      count = 0;

      for (JSONObject field : (List<JSONObject>) a.getObjectList()) {
        if (OrderFields.isEditable(field) ||!OrderFields.isHidden(field)) {
          count++;
        }
      }

      info.put(OrderFields.WIDGET_COUNT, count);
    }

    return count;
  }

  public static FieldType getFieldType(JSONObject field) {
    FieldType type = (FieldType) field.opt(FIELD_TYPE);

    if (type == null) {
      String s = field.optString(TYPE, "group").toUpperCase(Locale.US);

      type = FieldType.valueOf(s);
      field.put(FIELD_TYPE, type);
    }

    return type;
  }

  public static String getID(JSONObject field, boolean onlyIfInPreview) {
    if (onlyIfInPreview &&!field.optBoolean(INPREVIEW)) {
      return null;
    }

    return field.optString(ID, null);
  }

  public static void getItemDiscontinueFields(final String itemID, final iFunctionCallback cb) {
    aWorkerTask task = new aWorkerTask() {
      @Override
      public Object compute() {
        OrderFields fields = new OrderFields(itemID, "/hub/main/util/ordering/discontinue_fields/" + itemID);

        try {
          fields.load(null);
        } catch(Exception e) {
          return e;
        }

        List<JSONObject> list = fields.getFields();

        if ((list != null) &&!list.isEmpty()) {
          return fields;
        }

        return fields;
      }
      @Override
      public void finish(Object result) {
        cb.finished(false, result);
      }
    };

    Platform.getWindowViewer().spawn(task);
  }

  /**
   * Retrieve the fields for an item.
   * MUST be called from a background thread
   *
   * @param itemID the item id
   * @return the fields;
   */
  public static OrderFields getOrderFields(String fieldsID) {
    if (Platform.isUIThread()) {
      throw new ApplicationException("MUST be called from a background thread");
    }

    OrderFields fields = null;

    if (orderingFields != null) {
      fields = (OrderFields) orderingFields.get(fieldsID);
    }

    if (fields == null) {
      fields = new OrderFields(fieldsID, "/hub/main/util/ordering/fields/" + fieldsID + ".json");
      fields.load(null);

      if (orderingFields == null) {
        orderingFields = new ObjectCache();
        orderingFields.setBufferSize(10);
        orderingFields.setPurgeInline(true);
        orderingFields.setStrongReferences(true);
      }

      orderingFields.put(fields.getID(), fields);
    }

    return fields;
  }

  public static void getOrderSentences(final String itemID, final iFunctionCallback cb) {
    aWorkerTask task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          return Utils.getContentAsJSON("/hub/main/util/ordering/sentences/" + itemID, null, false);
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        cb.finished(result instanceof Throwable, result);
      }
    };

    Platform.getWindowViewer().spawn(task);
  }

  public static Map<String, FieldValue> getFieldValues(String url) throws Exception{
          JSONObject o = Utils.getContentAsJSON(url, null,
                           false);
          JSONArray                   a      = (o == null)
                  ? null
                  : o.getJSONArray("rows");
          HashMap<String, FieldValue> values = null;
          int                         len    = (a == null)
                  ? 0
                  : a.size();

          if (len > 0) {
            values = new HashMap<String, FieldValue>(len);

            for (int i = 0; i < len; i++) {
              JSONObject value = a.getJSONObject(i);
              String     id    = value.getString(ID);

              values.put(id,
                         new FieldValue(value.getString(DEFAULT_VALUE), value.optString(DEFAULT_VALUE_DISPLAY, null)));
            }
          }

          return values;
  }
  public static boolean isEditable(JSONObject field) {
    return field.optBoolean(EDITABLE, true);
  }

  public static boolean isEnabled(JSONObject field) {
    return field.optBoolean(ENABLED, true);
  }

  public static boolean isHidden(JSONObject field) {
    return !field.optBoolean(VISIBLE, true);
  }

  public static boolean isValid(JSONObject field, Object value) {
    switch(getFieldType(field)) {
      case INTEGER :
      case DECIMAL :
        return isInRangeValue(field, (SNumber) value);

      case TEXTFIELD :
        String s = (String) value;

        if (!isInRangeValue(field, s.length())) {
          return false;
        }

        return isValidPattern(field, s);

      default :
        break;
    }

    return true;
  }

  public static boolean isInRangeValue(JSONObject field, SNumber value) {
    SNumber min = getNumber(field, true);
    SNumber max = getNumber(field, true);

    if ((min != null) && value.lt(min)) {
      return false;
    }

    if ((min != null) && value.gt(max)) {
      return false;
    }

    return true;
  }

  public static boolean isValidPattern(JSONObject field, String value) {
    RegularExpressionFilter f = (RegularExpressionFilter) field.opt(INPUT_FILTER);

    if (f == null) {
      String mask = field.optString(INPUT_MASK, null);

      if (mask != null) {
        f = Functions.createRegExFilter(mask, false);
        field.put(INPUT_FILTER, f);
      }
    }

    return (f == null)
           ? true
           : f.passes(value, null);
  }

  public static boolean isInRangeValue(JSONObject field, int value) {
    SNumber min = getNumber(field, true);
    SNumber max = getNumber(field, true);

    if ((min != null) && (value < min.intValue())) {
      return false;
    }

    if ((min != null) && (value > max.intValue())) {
      return false;
    }

    return true;
  }

  public static SNumber getNumber(JSONObject field, boolean min) {
    Object o = field.opt(min
                         ? MIN_VALUE
                         : MAX_VALUE);

    if (o instanceof SNumber) {
      return (SNumber) o;
    } else if (o instanceof String) {
      SNumber num = new SNumber((String) o);

      field.put(min
                ? MIN_VALUE
                : MAX_VALUE, num);

      return num;
    } else if (o instanceof Number) {
      SNumber num = new SNumber(((Number) o).doubleValue());

      field.put(min
                ? MIN_VALUE
                : MAX_VALUE, num);

      return num;
    }

    return null;
  }

  public static boolean isInPreview(JSONObject field) {
    return field.optBoolean(INPREVIEW);
  }

  public static boolean isRequired(JSONObject field) {
    return field.optBoolean(REQUIRED);
  }

  public static enum FieldType {
    BOOLEAN, TEXTFIELD, TEXTAREA, RICHTEXT, PASSWORD, INTEGER, DECIMAL, LIST, DATE, DATE_TIME, TIME, LABEL, LINE, GROUP,SEARCHLIST
  }

  public static enum GroupType {
    INLINE, DROP_DOWN, OVERLAY, DIALOG
  }
}
