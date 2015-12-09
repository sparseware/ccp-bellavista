package com.sparseware.bellavista.oe;

import com.appnativa.rare.Platform;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.spot.CheckBox;
import com.appnativa.rare.spot.ComboBox;
import com.appnativa.rare.spot.DateChooser;
import com.appnativa.rare.spot.Form;
import com.appnativa.rare.spot.Label;
import com.appnativa.rare.spot.Line;
import com.appnativa.rare.spot.ListBox;
import com.appnativa.rare.spot.PasswordField;
import com.appnativa.rare.spot.TextArea;
import com.appnativa.rare.spot.TextField;
import com.appnativa.rare.spot.TimeSpinner;
import com.appnativa.rare.spot.Widget;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.border.UIEmptyBorder;
import com.appnativa.rare.ui.iListHandler;
import com.appnativa.rare.ui.iPlatformBorder;
import com.appnativa.rare.viewer.FormViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.widget.CheckBoxWidget;
import com.appnativa.rare.widget.ComboBoxWidget;
import com.appnativa.rare.widget.DateChooserWidget;
import com.appnativa.rare.widget.aSpinnerWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.MutableInteger;
import com.appnativa.util.SNumber;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

import com.sparseware.bellavista.OrderManager;
import com.sparseware.bellavista.OrderManager.WidgetDataLink;
import com.sparseware.bellavista.Orders;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.oe.OrderFields.FieldType;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FormsManager {
  private static String       decimalFormat = "#.################";
  private static int          decimalFormatPosition;
  private static Label        promptLabel;
  private static final String FOCUS_GAINED_EVENT       = "class:" + OrderEntry.class.getName() + "#onFocusGained";
  private static final String FOCUS_LOST_EVENT         = "class:" + OrderEntry.class.getName() + "#onFocusLost";
  private static final String VISIBILITY_TOGGLE_ACTION = "class:" + OrderEntry.class.getName()
                                                         + "#onTogglingItemSelected";
  private static final String    VALUE_CHANGED = "class:" + OrderEntry.class.getName() + "#onFieldValueChanged";
  private static final String    VALUE_ACTION  = "class:" + OrderEntry.class.getName() + "#onFieldValueAction";
  private static iPlatformBorder popupPaddingBorder;
  private static boolean         addColonToPrompt;
  private static iWidget         updatingWidget;

  public static void handleTogglingItemSelected(iWidget widget) {
    String ld;

    if (widget instanceof iListHandler) {
      ld = (String) ((iListHandler) widget).getSelectedItem().getLinkedData();
    } else {
      ld = ((CheckBoxWidget) widget).isChecked()
           ? "true"
           : "false";
    }

    FieldValue fv    = (FieldValue) widget.getLinkedData();
    JSONObject field = fv.field;
    JSONObject o     = field.getJSONObject("toggle");
    String     id    = o.getString("fieldID");
    iContainer c     = widget.getFormViewer();
    String     tt    = o.getString("type");

    if (tt.equals("enabled")) {
      iWidget tw = widget.getFormViewer().getWidget(id);

      if (tw != null) {
        tw.setEnabled(ld.equals(o.getString("itemLinkedData")));
      }
    } else if (tt.equals("visible")) {
      iWidget tw = widget.getFormViewer().getWidget(id);

      if (tw != null) {
        tw.setVisible(ld.equals(o.getString("itemLinkedData")));
        widget.getWindow().pack();
      }
    } else if (tt.equals("popup") && ld.equals(o.getString("itemLinkedData"))) {
      FieldValue       cfv    = (FieldValue) c.getLinkedData();
      JSONObject       info   = cfv.field;
      List<JSONObject> fields = (List<JSONObject>) info.getJSONArray("fields");
      JSONObject       gfield = fv.groupField;

      if ((gfield == null) && (fields != null)) {
        for (JSONObject f : fields) {
          if (id.equals(f.opt(OrderFields.ID))) {
            gfield        = f;
            fv.groupField = f;

            break;
          }
        }
      }

      if (gfield != null) {
        final WindowViewer w       = Platform.getWindowViewer();
        Map                pvalues = resolveValuesMap(c, cfv);

        id = gfield.getString(OrderFields.ID);

        Map values = (Map) pvalues.get(id);

        if (values == null) {
          values = new HashMap();
          pvalues.put(id, values);
        }

        final ArrayList<WidgetDataLink> links = new ArrayList<OrderManager.WidgetDataLink>(2);
        final iContainer                group = createGroup(null, gfield, w, new MutableInteger(0), values, links);

        group.setVisible(true);

        if (popupPaddingBorder == null) {
          popupPaddingBorder = new UIEmptyBorder(UIScreen.PLATFORM_PIXELS_4);
        }

        if (links.isEmpty()) {
          handleGroup(w, gfield, group);
        } else {
          final JSONObject ggfield = gfield;
          Runnable         r       = new Runnable() {
            @Override
            public void run() {
              for (WidgetDataLink link : links) {
                link.handle();
              }

              Platform.invokeLater(new Runnable() {
                @Override
                public void run() {
                  w.hideWaitCursor();
                  handleGroup(w, ggfield, group);
                }
              });
            }
          };
          w.hideWaitCursor();
          w.spawn(r);
        }
      }
    }
  }

  private static void handleGroup(WindowViewer w, final JSONObject gfield, final iContainer group) {
    group.setBorder(popupPaddingBorder);
    OrderManager.yesNo(w, gfield, group, new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (!canceled && Boolean.TRUE.equals(returnValue)) {
          try {
            updateValueFromWidget(group);
          } finally {
            group.dispose();
          }
        }
      }
    });
  }

  public static iWidget addWidget(iContainer parent, FieldValue fv, WindowViewer w, boolean combosForSpinners,
                                  boolean combosForBoolean, boolean combosForList, MutableInteger yPosition,
                                  boolean standalone, ArrayList<WidgetDataLink> links) {
    JSONObject field = fv.field;

    if (decimalFormat == null) {
      JSONObject o = (JSONObject) Platform.getAppContext().getData("ordersInfo");

      decimalFormat         = o.optString("fieldDecimalFormatString", "#.################");
      decimalFormatPosition = decimalFormat.indexOf('.');

      if (decimalFormatPosition == -1) {
        throw new ApplicationException("The 'orderInfo.fieldDecimalFormatString' is missing the decimal");
      }
    }

    FieldType  type   = OrderFields.getFieldType(field);
    Widget     cfg    = null;
    iWidget    widget = null;
    ComboBox   combo;
    String     s;
    ActionLink link = null;

    if (!OrderFields.isEditable(field)) {
      Label l = (Label) w.createConfigurationObject("Label", "bv.oe.field_uneditable");

      l.value.setValue(field.optString(OrderFields.DEFAULT_VALUE));
      cfg = l;
    } else {
      Object min = field.opt(OrderFields.MIN_VALUE);
      Object max = field.opt(OrderFields.MAX_VALUE);

      switch(type) {
        case INTEGER :
        case DECIMAL :
          s = (type == FieldType.INTEGER)
              ? "bv.oe.integer_field"
              : "bv.oe.decimal_field";

          TextField nf = (TextField) w.createConfigurationObject("TextField", s);

          s = field.optString(OrderFields.INPUT_MASK, null);

          if (s != null) {
            nf.inputValidator.setValue(s);
          } else {
            if (type == FieldType.DECIMAL) {
              int dp = field.optInt(OrderFields.DECIMAL_PLACES, 2) + decimalFormatPosition + 1;

              if (dp >= decimalFormat.length()) {
                nf.inputValidator.setValue(decimalFormat);
              } else {
                nf.inputValidator.setValue(decimalFormat.substring(0, dp));
              }
            }
          }

          cfg = nf;

          if (min != null) {
            nf.inputValidator.spot_setAttribute("minimum", min.toString());
          }

          if (max != null) {
            nf.inputValidator.spot_setAttribute("maximum", max.toString());
          }

          cfg = nf;

          if (!standalone) {
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VALUE_ACTION);
          }

          break;

        case TEXTFIELD :
          TextField tf = (TextField) w.createConfigurationObject("TextField", "bv.oe.text_field");

          tf.errorMessage.spot_setAttribute("displayWidget", "messageBox");
          cfg = tf;

          if (min != null) {
            tf.minCharacters.setValue(((Number) min).intValue());
          }

          if (max != null) {
            tf.maxCharacters.setValue(((Number) max).intValue());
          }

          s = field.optString(OrderFields.INPUT_MASK, null);

          if (s != null) {
            tf.inputMask.setValue(s);
          }

          if (!standalone) {
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VALUE_ACTION);
          }

          break;

        case PASSWORD :
          PasswordField pf = (PasswordField) w.createConfigurationObject("PasswordField", "bv.oe.password_field");

          cfg = pf;

          if (min != null) {
            pf.minCharacters.setValue(((Number) min).intValue());
          }

          if (max != null) {
            pf.maxCharacters.setValue(((Number) max).intValue());
          }

          s = field.optString(OrderFields.INPUT_MASK, null);

          if (s != null) {
            pf.inputMask.setValue(s);
          }

          if (!standalone) {
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VALUE_ACTION);
          }

          break;

        case TEXTAREA :
          TextArea ta = (TextArea) w.createConfigurationObject("TextArea", "bv.oe.textarea_field");

          cfg = ta;

          if (min != null) {
            ta.minCharacters.setValue(((Number) min).intValue());
          }

          if (max != null) {
            ta.maxCharacters.setValue(((Number) max).intValue());
          }

          s = field.optString(OrderFields.INPUT_MASK, null);

          if (s != null) {
            ta.inputMask.setValue(s);
          }

          break;

        case BOOLEAN :
          if (combosForBoolean) {
            ComboBox cb = (ComboBox) w.createConfigurationObject("ComboBox", "bv.oe.boolean_field.combobox");

            cfg = cb;
          } else {
            CheckBox cb = (CheckBox) w.createConfigurationObject("CheckBox", "bv.oe.boolean_field.checkbox");

            cb.value.setValue(field.getString(OrderFields.PROMPT));
            standalone = false;
            cfg        = cb;
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VALUE_CHANGED);
          }

          if (standalone) {
            if (field.optJSONObject("toggle") != null) {
              cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VISIBILITY_TOGGLE_ACTION);
            }
          } else {
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VALUE_CHANGED);
          }

          break;

        case LIST :
          link = Utils.createLink(parent, field.getString(OrderFields.DATA_URL), false);

          if (combosForList) {
            ComboBox cb = (ComboBox) w.createConfigurationObject("ComboBox", "bv.oe.list_field.combobox");

            cfg = cb;
          } else {
            ListBox lb = (ListBox) w.createConfigurationObject("ListBox", "bv.oe.list_field.listbox");

            if (standalone && (lb.getBorders() == null)) {
              lb.addBorder("line");
            }

            cfg = lb;
          }

          if (standalone) {
            if (field.optJSONObject("toggle") != null) {
              cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VISIBILITY_TOGGLE_ACTION);
            }
          } else {
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_ACTION, VALUE_CHANGED);
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_DOUBLECLICK, VALUE_ACTION);
          }

          break;

        case DATE :
        case DATE_TIME :
          s = (type == FieldType.DATE)
              ? "bv.oe.date_field"
              : "bv.oe.date_time_field";

          Form        form = (Form) w.createConfigurationObject("Form", s);
          DateChooser dc   = (DateChooser) form.findWidget("dateChooser");

          if (type == FieldType.DATE) {
            dc.showTime.setValue(false);
          }

          if (min != null) {
            dc.minValue.setValue(min.toString());
          }

          if (max != null) {
            dc.maxValue.setValue(max.toString());
          }

          s = field.optString(OrderFields.INPUT_MASK, null);

          if (s != null) {
            dc.format.setValue(s);
          }

          if (type == FieldType.TIME) {
            dc.showTime.setValue(true);
          }

          cfg = form;

          break;

        case TIME :
          TimeSpinner ts = (TimeSpinner) w.createConfigurationObject("TimeSpinner", "bv.oe.time_field");

          if (min != null) {
            ts.minValue.setValue(min.toString());
          }

          if (max != null) {
            ts.maxValue.setValue(max.toString());
          }

          s = field.optString(OrderFields.INPUT_MASK, null);

          if (s != null) {
            ts.format.setValue(s);
          }

          if (combosForSpinners) {
            combo = (ComboBox) w.createConfigurationObject("ComboBox", "bv.oe.combo.widget");
            combo.componentType.setValue(ComboBox.CComponentType.widget);
            combo.popupWidget.setValue(ts);
            cfg = combo;
          } else {
            cfg = ts;
          }

          if (!standalone) {
            cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_CHANGE, VALUE_CHANGED);
          }

          break;

        case LABEL :
          Label l = (Label) w.createConfigurationObject("Label", "bv.oe.label_field");

          l.value.setValue(field.optString(OrderFields.DEFAULT_VALUE));
          cfg = l;
        case LINE :
          Line ln = (Line) w.createConfigurationObject("Line", "bv.oe.line_field");

          if (fv.value != null) {
            ln.leftLabel.setValue(fv.value.toString());
          }

          s = field.optString(OrderFields.DESCRIPTION, null);

          if (s != null) {
            ln.rightLabel.setValue(s);
          }

          cfg = ln;

          break;

        case GROUP :
          Map values = null;

          if (fv.value instanceof Map) {
            values = (Map) fv.value;
          }

          s = field.optString(OrderFields.ID, null);

          if (s == null) {
            field.put(OrderFields.ID, Integer.toHexString(System.identityHashCode((int) field.hashCode())));
          }

          widget = createGroup(parent, field, w, yPosition, values, links);
          cfg    = null;

          break;

        default :
          break;
      }
    }

    if (cfg != null) {
      int y = yPosition.get();

      if (standalone &&!(cfg instanceof CheckBox)) {
        Label prompt = promptLabel;

        if (prompt == null) {
          promptLabel = prompt = (Label) w.createConfigurationObject("Label", "bv.oe.field_label_prompt");
          prompt.bounds.x.setValue("0");
          addColonToPrompt =
            ((JSONObject) Platform.getAppContext().getData("ordersInfo")).optBoolean("addColonToPrompt", true);
        }

        s = field.optString(OrderFields.PROMPT, null);

        if (s != null) {
          if (addColonToPrompt) {
            s = s.trim();

            if (!s.endsWith(":")) {
              s += ":";
            }
          }

          prompt.value.setValue(s);
          prompt.bounds.y.setValue(StringCache.valueOf(y++));
          parent.addWidget(prompt);
        }
      }

      String id = field.optString(OrderFields.ID, null);

      cfg.required.setValue(OrderFields.isRequired(field));
      cfg.visible.setValue(!OrderFields.isHidden(field));
      cfg.enabled.setValue(OrderFields.isEnabled(field));
      cfg.name.setValue(id);
      cfg.bounds.x.setValue("0");
      cfg.bounds.y.setValue(StringCache.valueOf(y++));
      cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_FOCUS, FOCUS_GAINED_EVENT);
      cfg.spot_setAttribute(iConstants.ATTRIBUTE_ON_BLUR, FOCUS_LOST_EVENT);
      widget = parent.addWidget(cfg);

      if ((type == FieldType.BOOLEAN) && (widget instanceof ComboBoxWidget)) {
        ComboBoxWidget cb = (ComboBoxWidget) widget;

        cb.add(new RenderableDataItem(Orders.booleanFieldTrueDisplayValue, RenderableDataItem.TYPE_STRING,
                                      Orders.booleanFieldTrueValue));
        cb.add(new RenderableDataItem(Orders.booleanFieldFalseDisplayValue, RenderableDataItem.TYPE_STRING,
                                      Orders.booleanFieldFalseValue));
      }

      if (fv.value != null) {
        updateWidgetFromValue(widget, fv);
      }

      if (id != null) {
        widget.setLinkedData(fv);
      }

      yPosition.set(y);

      if (link != null) {
        if(links!=null) {
          links.add(new WidgetDataLink(widget, link));
        }
        else {
          widget.setDataLink(link);
        }
      }
    }

    return widget;
  }

  public static iContainer createFormContainer(WindowViewer w, RenderableDataItem row, JSONObject info,
          Map<String, FieldValue> values, ArrayList<WidgetDataLink> links) {
    Form             f           = (Form) w.createConfigurationObject("Form", "bv.oe.form");
    List<JSONObject> fields      = info.getJSONArray("fields").getObjectList();
    int              widgetCount = OrderFields.getWidgetCount(info);

    if (row != null) {
      Label l = (Label) f.findWidget("order");

      l.value.setValue(row.get(1).toString());
      l = (Label) f.findWidget("directions");
      l.value.setValue(Orders.getOrderDirections(row));
    }

    Form ff = (Form) f.findWidget("formFields");

    if (widgetCount > 1) {
      ff.getScrollPaneReference();
    }

    StringBuilder sb  = new StringBuilder();
    int           len = fields.size() * 2;

    for (int i = 0; i < len; i++) {
      sb.append("d,");
    }

    sb.setLength(sb.length() - 1);
    ff.rows.setValue(sb.toString());

    final iContainer c  = (iContainer) w.createWidget(f);
    iContainer       fc = (iContainer) c.getWidget("formFields");

    if (values == null) {
      values = new HashMap<String, FieldValue>(len);
    }

    FormsManager.populateContainer(fc, fields, widgetCount, w, values, links);
    fc.setLinkedData(new FieldValue(info, values));

    return c;
  }

  public static iContainer createFormContainer(WindowViewer w, Order order, ArrayList<WidgetDataLink> links) {
    Form                    f           = (Form) w.createConfigurationObject("Form", "bv.oe.form");
    Map<String, FieldValue> values      = order.orderValues;
    List<JSONObject>        fields      = order.orderFields.getFields();
    int                     widgetCount = order.orderFields.getWidgetCount();
    Label                   l           = (Label) f.findWidget("order");

    l.value.setValue(order.orderedItem.toString());
    l = (Label) f.findWidget("directions");
    l.value.setValue(order.directionsItem.toString());

    Form          ff  = (Form) f.findWidget("formFields");
    StringBuilder sb  = new StringBuilder();
    int           len = fields.size() * 2;

    for (int i = 0; i < len; i++) {
      sb.append("d,");
    }

    sb.setLength(sb.length() - 1);
    ff.rows.setValue(sb.toString());

    final iContainer c     = (iContainer) w.createWidget(f);
    iContainer       fc    = (iContainer) c.getWidget("formFields");
    JSONObject       field = new JSONObject();

    field.put("fields", new JSONArray(fields));
    FormsManager.populateContainer(fc, fields, widgetCount, w, values, links);
    fc.setLinkedData(new FieldValue(field, values));

    return c;
  }

  public static iContainer createGroup(iContainer parent, JSONObject field, WindowViewer w, MutableInteger yPosition,
          Map<String, FieldValue> values, ArrayList<WidgetDataLink> links) {
    List<JSONObject> fields = (List<JSONObject>) field.getJSONArray(OrderFields.FIELDS).getObjectList();
    Form             cfg    = (Form) w.createConfigurationObject("Form", "bv.oe.group_field");
    StringBuilder    sb     = new StringBuilder();
    int              len    = fields.size() * 2;

    for (int i = 0; i < len; i++) {
      sb.append("d,");
    }

    sb.setLength(sb.length() - 1);
    cfg.rows.setValue(sb.toString());
    cfg.required.setValue(OrderFields.isRequired(field));
    cfg.name.setValue(field.getString(OrderFields.ID));
    cfg.visible.setValue(!OrderFields.isHidden(field));
    cfg.enabled.setValue(OrderFields.isEnabled(field));
    cfg.bounds.x.setValue("0");
    cfg.bounds.y.setValue(StringCache.valueOf(yPosition.getAndIncrement()));

    iContainer c           = (iContainer) ((parent == null)
            ? w.createWidget(cfg)
            : parent.addWidget(cfg));
    int        widgetCount = OrderFields.getWidgetCount(field);

    if (values == null) {
      values = new HashMap<String, FieldValue>(len);
    }

    populateContainer(c, fields, widgetCount, w, values, links);
    c.setLinkedData(new FieldValue(field, values));

    return c;
  }

  public static void populateContainer(iContainer c, List<JSONObject> fields, int widgetCount, WindowViewer w,
          Map<String, FieldValue> values, ArrayList<WidgetDataLink> links) {
    MutableInteger pos       = new MutableInteger(0);
    boolean        useCombos = widgetCount > (UIScreen.isLargeScreen() ? 2 : 1);

    for (JSONObject field : fields) {
      if (OrderFields.isHidden(field) &&!OrderFields.isEditable(field)) {
        continue;
      }

      FieldValue value = null;
      String     id    = field.optString(OrderFields.ID, null);

      if (id != null) {
        value = values.get(id);
      }

      if (value == null) {
        value = new FieldValue(field);

        if (id != null) {
          values.put(id, value);
        }
      }

      addWidget(c, value, w, true, false, useCombos, pos, true, links);
    }
  }

  public static void populateList(List<RenderableDataItem> list, List<JSONObject> fields, UIFont bold, WindowViewer w,
                                  boolean requiredOnly, boolean optionalOnly) {
    for (JSONObject field : fields) {
      if (OrderFields.isHidden(field)) {
        continue;
      }

      boolean required = OrderFields.isRequired(field);

      if (requiredOnly &&!required) {
        continue;
      }

      if (optionalOnly && required) {
        continue;
      }

      String             prompt = field.getString(OrderFields.PROMPT);
      FieldValue         value  = new FieldValue(field);
      RenderableDataItem item   = new RenderableDataItem(prompt, RenderableDataItem.TYPE_STRING, value);

      if (required) {
        item.setFont(bold);
        item.setUserStateFlag(Orders.REQUIRED_FLAG);
      }

      if (!OrderFields.isEditable(field)) {
        item.setSelectable(false);
      }
    }
  }

  public static void updateValueFromWidget(iWidget widget) {
    FieldValue fv = (FieldValue) widget.getLinkedData();

    if (fv == null) {
      return;
    }

    JSONObject field = fv.field;
    FieldType  type  = OrderFields.getFieldType(field);

    if (widget instanceof aSpinnerWidget) {
      ((aSpinnerWidget) widget).commitEdit();
    }

    switch(type) {
      case INTEGER :
      case DECIMAL :
        fv.value = widget.getValue();

        if (fv.value instanceof String) {
          fv.value = new SNumber((String) fv.value, false);
        }

        if (type == FieldType.INTEGER) {
          fv.displayValue = NumberFormat.getIntegerInstance(Locale.getDefault()).format(fv.value);
        } else {
          fv.displayValue = NumberFormat.getNumberInstance(Locale.getDefault()).format(fv.value);
        }

        break;

      case TEXTFIELD :
      case PASSWORD :
      case TEXTAREA :
      case RICHTEXT :
        fv.value        = widget.getValue();
        fv.displayValue = null;

        break;

      case DATE :
      case DATE_TIME :
      case TIME :
        if (widget instanceof aSpinnerWidget) {
          ((aSpinnerWidget) widget).commitEdit();
        }

        fv.value        = widget.getValue();
        fv.displayValue = widget.getValueAsString();

        break;

      case BOOLEAN :
        if (widget instanceof ComboBoxWidget) {
          fv.value        = widget.getSelectionData();
          fv.displayValue = widget.getSelectionAsString();
        } else {
          if (((CheckBoxWidget) widget).isChecked()) {
            fv.value        = Orders.booleanFieldTrueValue;
            fv.displayValue = Orders.booleanFieldTrueDisplayValue;
          } else {
            fv.value        = Orders.booleanFieldFalseValue;
            fv.displayValue = Orders.booleanFieldFalseDisplayValue;
          }
        }

        break;

      case LIST :
        fv.value        = widget.getSelectionData();
        fv.displayValue = widget.getSelectionAsString();

        break;

      case GROUP :
        iContainer              g      = (iContainer) widget;
        Map<String, FieldValue> values = resolveValuesMap(widget, fv);

        for (iWidget gw : g.getWidgetList()) {
          updateValueFromWidget(gw);
          fv = (FieldValue) gw.getLinkedData();

          if (fv != null) {
            field = fv.field;
            values.put(field.getString(OrderFields.ID), fv);
          }
        }

        break;

      default :
        break;
    }
  }

  public static void updateWidgetFromValue(iWidget widget, FieldValue fv) {
    if ((fv == null) || (updatingWidget == widget)) {
      return;
    }

    updatingWidget = widget;

    try {
      JSONObject field = fv.field;
      FieldType  type  = OrderFields.getFieldType(field);

      switch(type) {
        case INTEGER :
        case DECIMAL :
        case TEXTFIELD :
        case PASSWORD :
        case TEXTAREA :
        case RICHTEXT :
        case TIME :
          widget.setValue(fv.value);

          break;

        case DATE :
        case DATE_TIME :
          Date              date = (Date) fv.value;
          DateChooserWidget dc   = (DateChooserWidget) widget.getFormViewer().getWidget("dateChooser");

          dc.setValue(date);

          if (date != null) {
            widget.getFormViewer().getWidget("valueField").setValue(fv.displayValue);
          }

          break;

        case BOOLEAN :
          if (widget instanceof ComboBoxWidget) {
            widget.setValue((fv.value == null)
                            ? null
                            : fv.value.toString());
          } else {
            ((CheckBoxWidget) widget).setChecked((fv.value != null) && fv.value.equals(Orders.booleanFieldTrueValue));
          }

          break;

        case LIST :
          widget.setValue(fv.value);
          int row=((iListHandler)widget).getSelectedIndex();
          if(row!=-1) {
            ((iListHandler)widget).scrollRowToVisible(row);
          }

          break;

        case GROUP :
          iContainer g      = (iContainer) widget;
          Map        values = resolveValuesMap(widget, fv);

          for (iWidget gw : g.getWidgetList()) {
            updateValueFromWidget(gw);
            fv = (FieldValue) gw.getLinkedData();

            if (fv != null) {
              field = fv.field;
              values.put(field.getString(OrderFields.ID), fv.value);
            }
          }

          break;

        default :
          break;
      }
    } finally {
      updatingWidget = null;
    }
  }

  public static Map<String, FieldValue> getValuesMap(iWidget widget) {
    FieldValue fv = (FieldValue) widget.getLinkedData();

    return (Map<String, FieldValue>) fv.value;
  }

  public static iWidget getInvalidWidget(iContainer c) {
    List<iWidget> list = c.getWidgetList();

    for (iWidget w : list) {
      if (!w.isEnabled() ||!w.isShowing() ||!w.isRequired()) {
        continue;
      }

      if (w instanceof FormViewer) {
        iWidget ww = getInvalidWidget((iContainer) w);

        if (ww != null) {
          return ww;
        }
      }

      if (!w.hasValue()) {
        return w;
      }
    }

    return null;
  }

  private static Map<String, FieldValue> resolveValuesMap(iWidget widget, FieldValue fv) {
    if (fv == null) {
      fv = (FieldValue) widget.getLinkedData();
    }

    Map<String, FieldValue> values = (fv == null)
                                     ? null
                                     : (Map<String, FieldValue>) fv.value;

    if ((values == null) && (fv != null)) {
      values   = new LinkedHashMap<String, FieldValue>();
      fv.value = values;

      JSONObject field = fv.field;
      String     id    = field.optString(OrderFields.ID, null);

      if (id != null) {
        Map pvalues = resolveValuesMap(widget.getFormViewer(), null);

        if (pvalues != null) {
          pvalues.put(id, values);
        }
      }
    }

    return values;
  }
}
