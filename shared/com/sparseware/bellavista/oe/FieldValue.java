package com.sparseware.bellavista.oe;

import com.appnativa.rare.Platform;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * This class represents the value of
 * a field
 * 
 * @author Don DeCoteau
 */
public class FieldValue implements Cloneable {

  /** the value to be submitted */
  public Object value;

  /** the display value */
  public String displayValue;

  /** the field linked to this value */
  public JSONObject field;

  /** the group field that is linked with this value's field */
  public JSONObject groupField;

  /**
   * Creates a new instance
   * 
   * @param field the field
   */
  public FieldValue(JSONObject field) {
    this.field        = field;
    this.value        = field.optString(OrderFields.DEFAULT_VALUE, null);
    this.displayValue = field.optString(OrderFields.DEFAULT_VALUE_DISPLAY, null);
  }

  /**
   * Creates a new instance
   * 
   * @param field the field
   * @param value the fields value
   */
  public FieldValue(JSONObject field, Object value) {
    this.field = field;
    this.value = value;
  }

  /**
   * Creates a new instance
   * 
   * @param value the fields value
   * @param displayValue the fields display value
   */
  public FieldValue(Object value, String displayValue) {
    super();
    this.value        = value;
    this.displayValue = displayValue;
  }

  /**
   * Clears the fields value
   */
  public void clear() {
    value        = null;
    displayValue = null;
  }

  /**
   * Resets the field's value to its default
   */
  public void reset() {
    this.value        = field.optString(OrderFields.DEFAULT_VALUE, null);
    this.displayValue = field.optString(OrderFields.DEFAULT_VALUE_DISPLAY, null);
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  /**
   * Returns the value as string to uses to submit
   * the value
   *
   * @return the string value
   */
  public String valueToString() {
    switch(OrderFields.getFieldType(field)) {
      case DATE_TIME :
        return Functions.convertDateTime( value, false);

      case DATE :
        return Functions.convertDate(value, false);

      case TIME :
        SimpleDateFormat df = (SimpleDateFormat) Platform.getAppContext().getDefaultTimeContext().getItemFormat();

        return Functions.convertDate( value, df.toPattern());

      default :
        return (value == null)
               ? null
               : value.toString();
    }
  }

  public String toString() {
    if (displayValue != null) {
      return displayValue;
    }

    return (value == null)
           ? null
           : value.toString();
  }

  public FieldValue copy() {
    try {
      return (FieldValue) clone();
    } catch (CloneNotSupportedException e) {
      throw new ApplicationException(e);
    }
  }
}
