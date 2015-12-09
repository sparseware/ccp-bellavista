package com.sparseware.bellavista.oe;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.util.CharArray;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.Orders;

public class Order {
  public static enum ActionType { NEW, DISCONTINUED, RENEWED }

  public String                  orderType;
  public ActionType              actionType;
  public String                  orderID;
  public String                  linkedOrderID;
  public RenderableDataItem      orderedItem;
  public RenderableDataItem      directionsItem;
  public Map<String, FieldValue> orderValues;
  public OrderFields             orderFields;
  protected boolean              complete;
  private boolean dirty;
  private iPlatformIcon documentIcon;
  private iPlatformIcon discontinueIcon;
  private iPlatformIcon documentIncompleteIcon;
  public Order(String orderType, ActionType actionType, String orderID, String orderName) {
    this(orderType, actionType, new RenderableDataItem(orderName, orderID));
  }

  public Order(String orderType, ActionType actionType, RenderableDataItem item) {
    this.orderType  = orderType;
    this.actionType = actionType;
    orderedItem     = item;
    directionsItem  = new RenderableDataItem();
  }
  
  public String getOrderedItemID() {
    return (String) orderedItem.getLinkedData();
  }
  public static Order createDiscontinuedOrder(RenderableDataItem selectedItem, OrderFields fields, Map<String, FieldValue> values) {
    String type = selectedItem.get(0).itemsToString();
    Order  o    = new Order(type, ActionType.DISCONTINUED, selectedItem.get(1).copy());

    o.linkedOrderID = (String) selectedItem.get(0).getLinkedData();
    o.directionsItem.setValue(Orders.getOrderDirections(selectedItem));
    o.orderFields=fields;
    o.orderValues=values;
    o.complete=true;

    return o;
  }

  public iPlatformIcon getActionTypeIcon() {
    if(documentIcon==null) {
      discontinueIcon=Platform.getResourceAsIcon("bv.icon.order_discontinued");
      documentIcon=Platform.getResourceAsIcon("bv.icon.order_complete");
      documentIncompleteIcon=Platform.getResourceAsIcon("bv.icon.order_incomplete");
    }
    switch(actionType) {
      case DISCONTINUED :
        return discontinueIcon;
      default :
        return complete ? documentIcon : documentIncompleteIcon;
    }
  }

  public void updateDirectionsItem() {
    if(actionType!=ActionType.DISCONTINUED) {
      updateValues(orderValues, null);
    }
  }
  
  public Map<String, FieldValue> copyValues() {
    if(orderValues==null) {
      return null;
    }
    else {
      LinkedHashMap<String, FieldValue> values = new LinkedHashMap<String, FieldValue>(orderValues);
      Iterator<Entry<String, FieldValue>> it = values.entrySet().iterator();
      while(it.hasNext()) {
        Entry<String, FieldValue> e = it.next();
        e.setValue(e.getValue().copy());
      }
      return values;
    }
  }
  public void updateValues(Map<String, FieldValue> values, CharArray ca) {
    this.orderValues = values;

    if (ca == null) {
      ca = new CharArray();
    }

    List<JSONObject> list = orderFields.getFields();

    for (JSONObject o : list) {
      String id = OrderFields.getID(o, true);

      if (!o.optBoolean(OrderFields.INPREVIEW)) {
        continue;
      }

      if (id != null) {
        FieldValue item = values.get(id);

        if (item != null && item.value!=null) {
          String s = o.optString(OrderFields.PREVIEW_PREFIX, null);

          if (s != null) {
            ca.append(s);
          }

          ca.append(item.toString());
          s = o.optString(OrderFields.PREVIEW_SUFFIX, null);

          if (s != null) {
            ca.append(s);
          }

          ca.append(", ");
        }
      }
    }

    if (ca._length > 2) {
      ca._length -= 2;    //remove trailing ", "
    }

    directionsItem.setValue(ca.toString());
  }

  public boolean isComplete() {
    return complete;
  }

  public void setComplete(boolean complete) {
    this.complete = complete;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }
}
