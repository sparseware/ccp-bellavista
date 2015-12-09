package com.sparseware.bellavista.oe;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.EventObject;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIColorHelper;
import com.appnativa.rare.ui.UINotifier;
import com.appnativa.rare.ui.UISoundHelper;
import com.appnativa.rare.ui.ViewerCreator;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.iListHandler;
import com.appnativa.rare.ui.event.FocusEvent;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WidgetPaneViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.widget.DateChooserWidget;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.TextFieldWidget;
import com.appnativa.rare.widget.aTextFieldWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.MutableInteger;
import com.appnativa.util.iFilter;
import com.appnativa.util.iStringConverter;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.OrderManager;
import com.sparseware.bellavista.Orders;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.oe.OrderFields.FieldType;

public class OrderEntry implements iEventHandler {
  static boolean navigateToRequiredOnly = true;
  static boolean navigateToEmptyOnly    = true;
  static boolean showRequiredOnly;
  MutableInteger yPosition = new MutableInteger(0);
  TableViewer    fieldsTable;
  iContainer     entryForm;
  Order          order;
  boolean        entryFormInWorkspace;
  boolean wasComplete;

  public OrderEntry() {
    showRequiredOnly=Orders.showRequiredFieldsOnlyDefault;
  }

  public void onClearValueAction(final String eventName, final iWidget widget, final EventObject event) {
    resetValue(true);
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {}

  public void onFieldsTableAction(final String eventName, final iWidget widget, final EventObject event) {
    TableViewer        table = (TableViewer) widget;
    RenderableDataItem row   = table.getSelectedItem();

    updateEntryForm(entryForm, row);

    iWidget nextField     = entryForm.getWidget("nextField");
    iWidget previousField = entryForm.getWidget("previousField");

    nextField.setEnabled(hasNextField(entryForm, navigateToRequiredOnly, navigateToEmptyOnly));
    previousField.setEnabled(hasPreviousField(entryForm, navigateToRequiredOnly, navigateToEmptyOnly));

    if (entryFormInWorkspace) {
      OrderManager.pushOrderEntryViewer(entryForm, null);
    }
  }

  public void onFieldValueAction(String eventName, iWidget widget, EventObject event) {
    onFieldValueChanged(eventName, widget, event);
    moveToNextField(entryForm, navigateToRequiredOnly, navigateToEmptyOnly);
  }

  public void onFieldValueChanged(String eventName, iWidget widget, EventObject event) {
    FormsManager.updateValueFromWidget(widget);

    FieldValue fv = (FieldValue) widget.getLinkedData();

    updateFieldsTable(widget.getFormViewer(), fv);
  }

  public void onFieldValueForDateChanged(final String eventName, final iWidget widget, final EventObject event) {
    iContainer        c             = widget.getFormViewer();
    String            name          = widget.getName();
    Date              date          = null;
    boolean           updateChooser = true;
    DateChooserWidget dc            = null;
    TextFieldWidget   valueField    = null;

    if (name.equals("now")) {
      date = new Date();
    } else if (name.equals("dateChooser")) {
      updateChooser = false;
      dc            = (DateChooserWidget) widget;
      date          = dc.getDate();
    } else if (name.equals("valueField")) {
      valueField = (TextFieldWidget) widget;

      String s = widget.getValueAsString();

      date = Utils.parseDate(s);

      if ((date == null) && ((s != null) && (s.length() > 0))) {
        UISoundHelper.errorSound();
      }
    }

    if (dc == null) {
      dc = (DateChooserWidget) c.getWidget("dateChooser");
    }

    if (valueField == null) {
      valueField = (TextFieldWidget) c.getWidget("valueField");
    }

    if (updateChooser) {
      dc.setDate(date);
    }

    FieldValue fv = (FieldValue) c.getLinkedData();

    fv.value = date;

    if (OrderFields.getFieldType(fv.field) == FieldType.DATE) {
      fv.displayValue = Functions.convertDate(widget, date);
    } else {
      fv.displayValue = Functions.convertDateTime(widget, date);
    }

    if (valueField != null) {
      valueField.setText(fv.displayValue);

      if (date == null) {
        valueField.requestFocus();
      }
    }

    c.setValue(date);
    ((RenderableDataItem) c).setToStringValue(fv.displayValue);
    updateFieldsTable(c.getParent().getFormViewer(), fv);
  }

  public void onFocusGained(String eventName, iWidget widget, EventObject event) {
    if (!((FocusEvent) event).isTemporary()) {
      iWidget mb = getMessageBox(widget);

      if (mb != null) {
        if (mb.getLinkedData() != widget) {
          FieldValue fv    = (FieldValue) widget.getLinkedData();
          JSONObject field = ((fv == null)
                              ? null
                              : fv.field);

          mb.setValue(field.optString(OrderFields.DESCRIPTION));
        }

        mb.setLinkedData(null);
      }
    }
  }

  public void onFocusLost(String eventName, iWidget widget, EventObject event) {
    if (!((FocusEvent) event).isTemporary()) {
      iWidget mb = getMessageBox(widget);

      if (mb != null) {
        mb.setValue("");
      }
    }
  }

  public void onListFieldFinishedLoading(final String eventName, final iWidget widget, final EventObject event) {
    FieldValue fv = (FieldValue) widget.getLinkedData();

    if ((fv != null) && (fv.value != null)) {
      iListHandler list = (iListHandler) widget;
      int          n    = RenderableDataItem.findLinkedObjectIndex(list, fv.value);

      if (n != -1) {
        list.setSelectedIndex(n);
        list.scrollRowToVisible(n);
      }
    }
  }

  public void onNavigateToXOnlyAction(String eventName, iWidget widget, EventObject event) {
    if (widget.getName().equals("required")) {
      navigateToRequiredOnly = widget.isSelected();
    } else {
      navigateToEmptyOnly = widget.isSelected();
    }

    updateNavigationButtons();
  }

  public void onNextOrPreviousField(final String eventName, final iWidget widget, final EventObject event) {
    if (widget.getName().equals("nextField")) {
      moveToNextField(widget.getFormViewer(), navigateToRequiredOnly, navigateToEmptyOnly);
    } else {
      moveToPreviousField(widget.getFormViewer(), navigateToRequiredOnly, navigateToEmptyOnly);
    }
  }

  public void onOrderFieldsConfigure(String eventName, iWidget widget, EventObject event) {
    iContainer  c     = (iContainer) widget;
    fieldsTable=(TableViewer) c.getWidget("fieldsTable");
    TableViewer table = fieldsTable;
    table.addAll(order.orderFields.createTableValues(order, table.getFont().deriveBold()));

    if (showRequiredOnly) {
      final byte required = Orders.REQUIRED_FLAG;

      table.filter(new iFilter() {
        @Override
        public boolean passes(Object value, iStringConverter converter) {
          return (((RenderableDataItem) value).getUserStateFlags() & required) != 0;
        }
      });
    }

    c.getWidget("orderedItem").setValue(order.orderedItem.toString());
    c.getWidget("showRequiredOnly").setSelected(showRequiredOnly);
    if(showRequiredOnly) {
      c.getWidget("showRequiredOnly").setForeground(UIColorHelper.getColor("oeClinicalPrompt"));
    }
  }

  public void onOrderFieldsShown(String eventName, iWidget widget, EventObject event) {
    updateOrderButtons();
  }

  public void onOrderFormConfigure(String eventName, iWidget widget, EventObject event) {
    final iContainer c = widget.getFormViewer();

    entryForm = (iContainer) c.getWidget("orderValueEditor");
    if (entryForm == null) {
      entryFormInWorkspace = true;

      try {
        ViewerCreator.createViewer(c, new ActionLink("/oe/order_value_editor.rml"), new iFunctionCallback() {
          @Override
          public void finished(boolean canceled, Object returnValue) {
            if (returnValue instanceof Throwable) {
              Utils.handleError((Throwable) returnValue);

              return;
            }

            entryForm = (iContainer) returnValue;
            entryForm.setAutoDispose(false);
            moveToNextField(entryForm, true, true);
          }
        });
      } catch(MalformedURLException e) {
        Utils.handleError(e);
      }
    } else {
      entryFormInWorkspace = false;
      Platform.invokeLater(new Runnable() {
        
        @Override
        public void run() {
          moveToNextField(entryForm, true, true);
        }
      });
    }
  }

  public void onOrderFormCreated(String eventName, iWidget widget, EventObject event) {
    order = OrderManager.getOrderBeingEdited();
    //this class gets reused  by the event handler so we need to reset our variables
    wasComplete=order.isComplete();
    entryFormInWorkspace=false;
    yPosition.set(0);
  }

  public void onOrderFormDispose(String eventName, iWidget widget, EventObject event) {
    iContainer ef = entryForm;

    entryForm   = null;
    fieldsTable = null;
    if ((ef != null) &&!ef.isAutoDispose()) {
      ef.dispose();
    }
  }

  public void onOrderValueEditorConfigure(String eventName, iWidget widget, EventObject event) {
    iContainer navigateTo = (iContainer) widget.getFormViewer().getWidget("navigateTo");

    navigateTo.getWidget("required").setSelected(navigateToRequiredOnly);
    navigateTo.getWidget("empty").setSelected(navigateToEmptyOnly);
  }

  public void onOrderValueEditorLoad(String eventName, iWidget widget, EventObject event) {
    if (entryFormInWorkspace) {
      WidgetPaneViewer pane = (WidgetPaneViewer) widget.getFormViewer().getWidget("valuePane");
      iWidget          w    = pane.getWidget();

      if (w != null) {
        w.requestFocus();
      }
    }
  }

  public void onResetToDefaultAction(final String eventName, final iWidget widget, final EventObject event) {
    resetValue(false);
  }

  public void onShowRequiredOnlyAction(final String eventName, final iWidget widget, final EventObject event) {
    final byte  required = Orders.REQUIRED_FLAG;
    TableViewer table    = fieldsTable;

    showRequiredOnly = widget.isSelected();
    UIColor fg=showRequiredOnly ? UIColorHelper.getColor("oeClinicalPrompt") : UIColorHelper.getForeground();
    widget.setForeground(fg);

    if (showRequiredOnly) {
      table.filter(new iFilter() {
        @Override
        public boolean passes(Object value, iStringConverter converter) {
          return (((RenderableDataItem) value).getUserStateFlags() & required) != 0;
        }
      });
    } else {
      table.unfilter();
    }
  }

  public void onTogglingItemSelected(String eventName, iWidget widget, EventObject event) {
    FormsManager.handleTogglingItemSelected(widget);
  }

  public boolean updateEntryForm(iContainer entryForm, RenderableDataItem row) {
    WidgetPaneViewer pane   = (WidgetPaneViewer) entryForm.getWidget("valuePane");
    iWidget          widget = pane.removeWidget();

    if (widget != null) {
      if ((widget instanceof aTextFieldWidget) &&!widget.isValidForSubmission(true)) {
        reselectTableRow(entryForm, (FieldValue) widget.getLinkedData());
        widget.requestFocus();

        return false;
      }

      FormsManager.updateValueFromWidget(widget);
      updateFieldsTable(entryForm, (FieldValue) widget.getLinkedData());
      widget.dispose();
    }

    FieldValue fv = (FieldValue) row.getLinkedData();

    yPosition.set(0);
    pane.setLinkedData(row);
    widget = FormsManager.addWidget(pane, fv, Platform.getWindowViewer(), false, false, false, yPosition, false,null);
    entryForm.getWidget("prompt").setValue(fv.field.optString(OrderFields.PROMPT) + ":");

    if (!(widget instanceof iListHandler)) {
      widget.selectAll();
    }

    widget.requestFocus();

    return true;
  }

  public void updateFieldsTable(iContainer entryForm, FieldValue fv) {
    final TableViewer table = fieldsTable;

    if (table != null) {
      int i = table.indexOfLinkedData(fv);

      if (i != -1) {
        RenderableDataItem row = table.get(i);

        row.get(1).setValue(fv);
        table.rowChanged(i);
      }

      order.setDirty(true);
      updateOrderButtons();
    }
  }

  protected boolean hasNextField(iContainer entryForm, boolean requiredValueOnly, boolean emptyValueOnly) {
    final TableViewer table    = fieldsTable;
    int               n        = table.getSelectedIndex();
    int               len      = table.size();
    final byte        required = Orders.REQUIRED_FLAG;

    for (int i = n + 1; i < len; i++) {
      RenderableDataItem row = table.get(i);

      if (!requiredValueOnly || (row.getUserStateFlags() & required) != 0) {
        if (emptyValueOnly) {
          FieldValue fv = (FieldValue) row.getLinkedData();

          if (fv.value != null) {
            continue;
          }

          return true;
        } else {
          return true;
        }
      }
    }

    return false;
  }

  protected boolean hasPreviousField(iContainer entryForm, boolean requiredValueOnly, boolean emptyValueOnly) {
    final TableViewer table    = fieldsTable;
    int               n        = table.getSelectedIndex();
    final byte        required = Orders.REQUIRED_FLAG;

    for (int i = n - 1; i >= 0; i--) {
      RenderableDataItem row = table.get(i);

      if (!requiredValueOnly || (row.getUserStateFlags() & required) != 0) {
        if (emptyValueOnly) {
          FieldValue fv = (FieldValue) row.getLinkedData();

          if (fv.value != null) {
            continue;
          }

          return true;
        } else {
          return true;
        }
      }
    }

    return false;
  }

  protected boolean isComplete() {
    final TableViewer table    = fieldsTable;
    int               len      = table.size();
    final byte        required = Orders.REQUIRED_FLAG;

    for (int i = 0; i < len; i++) {
      RenderableDataItem row = table.get(i);

      if ((row.getUserStateFlags() & required) != 0) {
        FieldValue fv = (FieldValue) row.getLinkedData();

        if (fv.value == null) {
          return false;
        }
      }
    }

    return true;
  }

  protected void moveToNextField(iContainer entryForm, boolean requiredValueOnly, boolean emptyValueOnly) {
    final TableViewer  table    = fieldsTable;
    int                n        = table.getSelectedIndex();
    int                len      = table.size();
    final byte         required = Orders.REQUIRED_FLAG;
    RenderableDataItem nextRow  = null;
    int                index    = -1;

    for (int i = n + 1; i < len; i++) {
      RenderableDataItem row = table.get(i);

      if (!requiredValueOnly || (row.getUserStateFlags() & required) != 0) {
        if (emptyValueOnly) {
          FieldValue fv = (FieldValue) row.getLinkedData();

          if (fv.value != null) {
            continue;
          }
        }

        index   = i;
        nextRow = row;

        break;
      }
    }

    if (nextRow != null) {
      table.setSelectedIndex(index);
      table.scrollRowToVisible(index);

      if (entryForm.isAttached()) {
        updateEntryForm(entryForm, nextRow);
      }
    }

    if (entryForm.isAttached()) {
      updateNavigationButtons();
    }
  }

  protected void moveToPreviousField(iContainer entryForm, boolean requiredValueOnly, boolean emptyValueOnly) {
    final TableViewer  table    = fieldsTable;
    int                n        = table.getSelectedIndex();
    final byte         required = Orders.REQUIRED_FLAG;
    RenderableDataItem prevRow  = null;
    int                index    = -1;

    for (int i = n - 1; i >= 0; i--) {
      RenderableDataItem row = table.get(i);

      if (!requiredValueOnly || (row.getUserStateFlags() & required) != 0) {
        if (emptyValueOnly) {
          FieldValue fv = (FieldValue) row.getLinkedData();

          if (fv.value != null) {
            continue;
          }
        }

        prevRow = row;
        index   = i;

        break;
      }
    }

    if (prevRow != null) {
      table.setSelectedIndex(index);
      table.scrollRowToVisible(index);
      updateEntryForm(entryForm, prevRow);
    }

    updateNavigationButtons();
  }

  protected void reselectTableRow(iContainer entryForm, FieldValue fv) {
    TableViewer table = fieldsTable;

    if (table != null) {
      int i = table.indexOfLinkedData(fv);

      if (i != -1) {
        table.setSelectedIndex(i);
      }
    }
  }

  protected void resetValue(boolean clear) {
    WidgetPaneViewer pane = (WidgetPaneViewer) entryForm.getWidget("valuePane");
    iWidget          fw   = pane.getWidget();
    FieldValue       fv   = (FieldValue) fw.getLinkedData();

    if (clear) {
      fv.clear();
    } else {
      fv.reset();
    }

    FormsManager.updateWidgetFromValue(fw, fv);
    updateFieldsTable(entryForm, fv);
  }

  private iWidget getMessageBox(iWidget widget) {
    iContainer c = widget.getParent();

    while((c != null) &&!c.getName().equals("formFields")) {
      c = c.getParent();
    }

    if (c != null) {
      c = c.getParent();
    }

    return (c == null)
           ? null
           : c.getWidget("messageBox");
  }

  private void updateNavigationButtons() {
    iWidget nextField     = entryForm.getWidget("nextField");
    iWidget previousField = entryForm.getWidget("previousField");

    nextField.setEnabled(hasNextField(entryForm, navigateToRequiredOnly, navigateToEmptyOnly));
    previousField.setEnabled(hasPreviousField(entryForm, navigateToRequiredOnly, navigateToEmptyOnly));
  }

  private void updateOrderButtons() {
    iContainer       c        = (iContainer) fieldsTable.getFormViewer().getWidget("buttonPanel");
    PushButtonWidget pb       = (PushButtonWidget) c.getWidget("yesButton");
    boolean          complete = isComplete();

    order.setComplete(complete);
    pb.setForeground(UIColorHelper.getColor(complete
            ? "orderCompleteColor"
            : "orderIncompleteColor"));
    pb.update();

    if (complete && !wasComplete && entryForm!=null && entryForm.isAttached()) {
      wasComplete=true;
      String text = Platform.getResourceAsString("bv.oe.text.order_complete");

      UINotifier.showMessage(text, 500, UINotifier.Location.CENTER, null, new Runnable() {
        @Override
        public void run() {
          if (entryFormInWorkspace && (entryForm != null) && entryForm.isAttached()) {
            Utils.popViewerStack();
          }
        }
      });
    }
  }
}
