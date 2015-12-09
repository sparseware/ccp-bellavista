//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/oe/OrderEntry.java
//
//  Created by decoteaud on 11/29/15.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIColor.h"
#include "com/appnativa/rare/ui/UIColorHelper.h"
#include "com/appnativa/rare/ui/UIFont.h"
#include "com/appnativa/rare/ui/UINotifier.h"
#include "com/appnativa/rare/ui/UISoundHelper.h"
#include "com/appnativa/rare/ui/ViewerCreator.h"
#include "com/appnativa/rare/ui/event/FocusEvent.h"
#include "com/appnativa/rare/ui/iListHandler.h"
#include "com/appnativa/rare/viewer/TableViewer.h"
#include "com/appnativa/rare/viewer/WidgetPaneViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/widget/DateChooserWidget.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/TextFieldWidget.h"
#include "com/appnativa/rare/widget/aTextFieldWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/MutableInteger.h"
#include "com/appnativa/util/iStringConverter.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/OrderManager.h"
#include "com/sparseware/bellavista/Orders.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/oe/FieldValue.h"
#include "com/sparseware/bellavista/oe/FormsManager.h"
#include "com/sparseware/bellavista/oe/Order.h"
#include "com/sparseware/bellavista/oe/OrderEntry.h"
#include "com/sparseware/bellavista/oe/OrderFields.h"
#include "java/lang/Throwable.h"
#include "java/net/MalformedURLException.h"
#include "java/util/Date.h"
#include "java/util/EventObject.h"
#include "java/util/List.h"

@implementation CCPBVOrderEntry

static BOOL CCPBVOrderEntry_navigateToRequiredOnly_ = YES;
static BOOL CCPBVOrderEntry_navigateToEmptyOnly_ = YES;
static BOOL CCPBVOrderEntry_showRequiredOnly_;

+ (BOOL)navigateToRequiredOnly {
  return CCPBVOrderEntry_navigateToRequiredOnly_;
}

+ (BOOL *)navigateToRequiredOnlyRef {
  return &CCPBVOrderEntry_navigateToRequiredOnly_;
}

+ (BOOL)navigateToEmptyOnly {
  return CCPBVOrderEntry_navigateToEmptyOnly_;
}

+ (BOOL *)navigateToEmptyOnlyRef {
  return &CCPBVOrderEntry_navigateToEmptyOnly_;
}

+ (BOOL)showRequiredOnly {
  return CCPBVOrderEntry_showRequiredOnly_;
}

+ (BOOL *)showRequiredOnlyRef {
  return &CCPBVOrderEntry_showRequiredOnly_;
}

- (id)init {
  if (self = [super init]) {
    yPosition_ = [[RAREUTMutableInteger alloc] initWithInt:0];
    CCPBVOrderEntry_showRequiredOnly_ = [CCPBVOrders showRequiredFieldsOnlyDefault];
  }
  return self;
}

- (void)onClearValueActionWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [self resetValueWithBoolean:YES];
}

- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (void)onFieldsTableActionWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  RARERenderableDataItem *row = [((RARETableViewer *) nil_chk(table)) getSelectedItem];
  [self updateEntryFormWithRAREiContainer:entryForm_ withRARERenderableDataItem:row];
  id<RAREiWidget> nextField = [((id<RAREiContainer>) nil_chk(entryForm_)) getWidgetWithNSString:@"nextField"];
  id<RAREiWidget> previousField = [entryForm_ getWidgetWithNSString:@"previousField"];
  [((id<RAREiWidget>) nil_chk(nextField)) setEnabledWithBoolean:[self hasNextFieldWithRAREiContainer:entryForm_ withBoolean:CCPBVOrderEntry_navigateToRequiredOnly_ withBoolean:CCPBVOrderEntry_navigateToEmptyOnly_]];
  [((id<RAREiWidget>) nil_chk(previousField)) setEnabledWithBoolean:[self hasPreviousFieldWithRAREiContainer:entryForm_ withBoolean:CCPBVOrderEntry_navigateToRequiredOnly_ withBoolean:CCPBVOrderEntry_navigateToEmptyOnly_]];
  if (entryFormInWorkspace_) {
    [CCPBVOrderManager pushOrderEntryViewerWithRAREiViewer:entryForm_ withJavaLangRunnable:nil];
  }
}

- (void)onFieldValueActionWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [self onFieldValueChangedWithNSString:eventName withRAREiWidget:widget withJavaUtilEventObject:event];
  [self moveToNextFieldWithRAREiContainer:entryForm_ withBoolean:CCPBVOrderEntry_navigateToRequiredOnly_ withBoolean:CCPBVOrderEntry_navigateToEmptyOnly_];
}

- (void)onFieldValueChangedWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVFormsManager updateValueFromWidgetWithRAREiWidget:widget];
  CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getLinkedData], [CCPBVFieldValue class]);
  [self updateFieldsTableWithRAREiContainer:[widget getFormViewer] withCCPBVFieldValue:fv];
}

- (void)onFieldValueForDateChangedWithNSString:(NSString *)eventName
                               withRAREiWidget:(id<RAREiWidget>)widget
                       withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiContainer> c = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
  NSString *name = [widget getName];
  JavaUtilDate *date = nil;
  BOOL updateChooser = YES;
  RAREDateChooserWidget *dc = nil;
  RARETextFieldWidget *valueField = nil;
  if ([((NSString *) nil_chk(name)) isEqual:@"now"]) {
    date = [[JavaUtilDate alloc] init];
  }
  else if ([name isEqual:@"dateChooser"]) {
    updateChooser = NO;
    dc = (RAREDateChooserWidget *) check_class_cast(widget, [RAREDateChooserWidget class]);
    date = [dc getDate];
  }
  else if ([name isEqual:@"valueField"]) {
    valueField = (RARETextFieldWidget *) check_class_cast(widget, [RARETextFieldWidget class]);
    NSString *s = [widget getValueAsString];
    date = [CCPBVUtils parseDateWithNSString:s];
    if ((date == nil) && ((s != nil) && ([s sequenceLength] > 0))) {
      [RAREUISoundHelper errorSound];
    }
  }
  if (dc == nil) {
    dc = (RAREDateChooserWidget *) check_class_cast([((id<RAREiContainer>) nil_chk(c)) getWidgetWithNSString:@"dateChooser"], [RAREDateChooserWidget class]);
  }
  if (valueField == nil) {
    valueField = (RARETextFieldWidget *) check_class_cast([((id<RAREiContainer>) nil_chk(c)) getWidgetWithNSString:@"valueField"], [RARETextFieldWidget class]);
  }
  if (updateChooser) {
    [((RAREDateChooserWidget *) nil_chk(dc)) setDateWithJavaUtilDate:date];
  }
  CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((id<RAREiContainer>) nil_chk(c)) getLinkedData], [CCPBVFieldValue class]);
  ((CCPBVFieldValue *) nil_chk(fv))->value_ = date;
  if ([CCPBVOrderFields getFieldTypeWithRAREUTJSONObject:fv->field_] == [CCPBVOrderFields_FieldTypeEnum DATE]) {
    fv->displayValue_ = [RAREFunctions convertDateWithRAREiWidget:widget withId:date];
  }
  else {
    fv->displayValue_ = [RAREFunctions convertDateTimeWithRAREiWidget:widget withId:date];
  }
  if (valueField != nil) {
    [valueField setTextWithJavaLangCharSequence:fv->displayValue_];
    if (date == nil) {
      [valueField requestFocus];
    }
  }
  [c setValueWithId:date];
  [((RARERenderableDataItem *) check_class_cast(c, [RARERenderableDataItem class])) setToStringValueWithJavaLangCharSequence:fv->displayValue_];
  [self updateFieldsTableWithRAREiContainer:[((id<RAREiContainer>) nil_chk([c getParent])) getFormViewer] withCCPBVFieldValue:fv];
}

- (void)onFocusGainedWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (![((RAREFocusEvent *) check_class_cast(event, [RAREFocusEvent class])) isTemporary]) {
    id<RAREiWidget> mb = [self getMessageBoxWithRAREiWidget:widget];
    if (mb != nil) {
      if ([mb getLinkedData] != widget) {
        CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getLinkedData], [CCPBVFieldValue class]);
        RAREUTJSONObject *field = ((fv == nil) ? nil : fv->field_);
        [mb setValueWithId:[field optStringWithNSString:[CCPBVOrderFields DESCRIPTION]]];
      }
      [mb setLinkedDataWithId:nil];
    }
  }
}

- (void)onFocusLostWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (![((RAREFocusEvent *) check_class_cast(event, [RAREFocusEvent class])) isTemporary]) {
    id<RAREiWidget> mb = [self getMessageBoxWithRAREiWidget:widget];
    if (mb != nil) {
      [mb setValueWithId:@""];
    }
  }
}

- (void)onListFieldFinishedLoadingWithNSString:(NSString *)eventName
                               withRAREiWidget:(id<RAREiWidget>)widget
                       withJavaUtilEventObject:(JavaUtilEventObject *)event {
  CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getLinkedData], [CCPBVFieldValue class]);
  if ((fv != nil) && (fv->value_ != nil)) {
    id<RAREiListHandler> list = (id<RAREiListHandler>) check_protocol_cast(widget, @protocol(RAREiListHandler));
    int n = [RARERenderableDataItem findLinkedObjectIndexWithJavaUtilList:list withId:fv->value_];
    if (n != -1) {
      [list setSelectedIndexWithInt:n];
      [list scrollRowToVisibleWithInt:n];
    }
  }
}

- (void)onNavigateToXOnlyActionWithNSString:(NSString *)eventName
                            withRAREiWidget:(id<RAREiWidget>)widget
                    withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([((NSString *) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getName])) isEqual:@"required"]) {
    CCPBVOrderEntry_navigateToRequiredOnly_ = [widget isSelected];
  }
  else {
    CCPBVOrderEntry_navigateToEmptyOnly_ = [widget isSelected];
  }
  [self updateNavigationButtons];
}

- (void)onNextOrPreviousFieldWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([((NSString *) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getName])) isEqual:@"nextField"]) {
    [self moveToNextFieldWithRAREiContainer:[widget getFormViewer] withBoolean:CCPBVOrderEntry_navigateToRequiredOnly_ withBoolean:CCPBVOrderEntry_navigateToEmptyOnly_];
  }
  else {
    [self moveToPreviousFieldWithRAREiContainer:[widget getFormViewer] withBoolean:CCPBVOrderEntry_navigateToRequiredOnly_ withBoolean:CCPBVOrderEntry_navigateToEmptyOnly_];
  }
}

- (void)onOrderFieldsConfigureWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiContainer> c = (id<RAREiContainer>) check_protocol_cast(widget, @protocol(RAREiContainer));
  fieldsTable_ = (RARETableViewer *) check_class_cast([((id<RAREiContainer>) nil_chk(c)) getWidgetWithNSString:@"fieldsTable"], [RARETableViewer class]);
  RARETableViewer *table = fieldsTable_;
  [table addAllWithJavaUtilCollection:[((CCPBVOrderFields *) nil_chk(((CCPBVOrder *) nil_chk(order_))->orderFields_)) createTableValuesWithCCPBVOrder:order_ withRAREUIFont:[((RAREUIFont *) nil_chk([((RARETableViewer *) nil_chk(table)) getFont])) deriveBold]]];
  if (CCPBVOrderEntry_showRequiredOnly_) {
    char required = CCPBVOrders_REQUIRED_FLAG;
    [table filterWithRAREUTiFilter:[[CCPBVOrderEntry_$1 alloc] init]];
  }
  [((id<RAREiWidget>) nil_chk([c getWidgetWithNSString:@"orderedItem"])) setValueWithId:[((RARERenderableDataItem *) nil_chk(order_->orderedItem_)) description]];
  [((id<RAREiWidget>) nil_chk([c getWidgetWithNSString:@"showRequiredOnly"])) setSelectedWithBoolean:CCPBVOrderEntry_showRequiredOnly_];
  if (CCPBVOrderEntry_showRequiredOnly_) {
    [((id<RAREiWidget>) nil_chk([c getWidgetWithNSString:@"showRequiredOnly"])) setForegroundWithRAREUIColor:[RAREUIColorHelper getColorWithNSString:@"oeClinicalPrompt"]];
  }
}

- (void)onOrderFieldsShownWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [self updateOrderButtons];
}

- (void)onOrderFormConfigureWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiContainer> c = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
  entryForm_ = (id<RAREiContainer>) check_protocol_cast([((id<RAREiContainer>) nil_chk(c)) getWidgetWithNSString:@"orderValueEditor"], @protocol(RAREiContainer));
  if (entryForm_ == nil) {
    entryFormInWorkspace_ = YES;
    @try {
      (void) [RAREViewerCreator createViewerWithRAREiWidget:c withRAREActionLink:[[RAREActionLink alloc] initWithNSString:@"/oe/order_value_editor.rml"] withRAREiFunctionCallback:[[CCPBVOrderEntry_$2 alloc] initWithCCPBVOrderEntry:self]];
    }
    @catch (JavaNetMalformedURLException *e) {
      [CCPBVUtils handleErrorWithJavaLangThrowable:e];
    }
  }
  else {
    entryFormInWorkspace_ = NO;
    [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVOrderEntry_$3 alloc] initWithCCPBVOrderEntry:self]];
  }
}

- (void)onOrderFormCreatedWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  order_ = [CCPBVOrderManager getOrderBeingEdited];
  wasComplete_ = [((CCPBVOrder *) nil_chk(order_)) isComplete];
  entryFormInWorkspace_ = NO;
  [((RAREUTMutableInteger *) nil_chk(yPosition_)) setWithInt:0];
}

- (void)onOrderFormDisposeWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiContainer> ef = entryForm_;
  entryForm_ = nil;
  fieldsTable_ = nil;
  if ((ef != nil) && ![ef isAutoDispose]) {
    [ef dispose];
  }
}

- (void)onOrderValueEditorConfigureWithNSString:(NSString *)eventName
                                withRAREiWidget:(id<RAREiWidget>)widget
                        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiContainer> navigateTo = (id<RAREiContainer>) check_protocol_cast([((id<RAREiFormViewer>) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getFormViewer])) getWidgetWithNSString:@"navigateTo"], @protocol(RAREiContainer));
  [((id<RAREiWidget>) nil_chk([((id<RAREiContainer>) nil_chk(navigateTo)) getWidgetWithNSString:@"required"])) setSelectedWithBoolean:CCPBVOrderEntry_navigateToRequiredOnly_];
  [((id<RAREiWidget>) nil_chk([navigateTo getWidgetWithNSString:@"empty"])) setSelectedWithBoolean:CCPBVOrderEntry_navigateToEmptyOnly_];
}

- (void)onOrderValueEditorLoadWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (entryFormInWorkspace_) {
    RAREWidgetPaneViewer *pane = (RAREWidgetPaneViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getFormViewer])) getWidgetWithNSString:@"valuePane"], [RAREWidgetPaneViewer class]);
    id<RAREiWidget> w = [((RAREWidgetPaneViewer *) nil_chk(pane)) getWidget];
    if (w != nil) {
      [w requestFocus];
    }
  }
}

- (void)onResetToDefaultActionWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [self resetValueWithBoolean:NO];
}

- (void)onShowRequiredOnlyActionWithNSString:(NSString *)eventName
                             withRAREiWidget:(id<RAREiWidget>)widget
                     withJavaUtilEventObject:(JavaUtilEventObject *)event {
  char required = CCPBVOrders_REQUIRED_FLAG;
  RARETableViewer *table = fieldsTable_;
  CCPBVOrderEntry_showRequiredOnly_ = [((id<RAREiWidget>) nil_chk(widget)) isSelected];
  RAREUIColor *fg = CCPBVOrderEntry_showRequiredOnly_ ? [RAREUIColorHelper getColorWithNSString:@"oeClinicalPrompt"] : [RAREUIColorHelper getForeground];
  [widget setForegroundWithRAREUIColor:fg];
  if (CCPBVOrderEntry_showRequiredOnly_) {
    [((RARETableViewer *) nil_chk(table)) filterWithRAREUTiFilter:[[CCPBVOrderEntry_$4 alloc] init]];
  }
  else {
    [((RARETableViewer *) nil_chk(table)) unfilter];
  }
}

- (void)onTogglingItemSelectedWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVFormsManager handleTogglingItemSelectedWithRAREiWidget:widget];
}

- (BOOL)updateEntryFormWithRAREiContainer:(id<RAREiContainer>)entryForm
               withRARERenderableDataItem:(RARERenderableDataItem *)row {
  RAREWidgetPaneViewer *pane = (RAREWidgetPaneViewer *) check_class_cast([((id<RAREiContainer>) nil_chk(entryForm)) getWidgetWithNSString:@"valuePane"], [RAREWidgetPaneViewer class]);
  id<RAREiWidget> widget = [((RAREWidgetPaneViewer *) nil_chk(pane)) removeWidget];
  if (widget != nil) {
    if (([(id) widget isKindOfClass:[RAREaTextFieldWidget class]]) && ![widget isValidForSubmissionWithBoolean:YES]) {
      [self reselectTableRowWithRAREiContainer:entryForm withCCPBVFieldValue:(CCPBVFieldValue *) check_class_cast([widget getLinkedData], [CCPBVFieldValue class])];
      [widget requestFocus];
      return NO;
    }
    [CCPBVFormsManager updateValueFromWidgetWithRAREiWidget:widget];
    [self updateFieldsTableWithRAREiContainer:entryForm withCCPBVFieldValue:(CCPBVFieldValue *) check_class_cast([widget getLinkedData], [CCPBVFieldValue class])];
    [widget dispose];
  }
  CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((RARERenderableDataItem *) nil_chk(row)) getLinkedData], [CCPBVFieldValue class]);
  [((RAREUTMutableInteger *) nil_chk(yPosition_)) setWithInt:0];
  [pane setLinkedDataWithId:row];
  widget = [CCPBVFormsManager addWidgetWithRAREiContainer:pane withCCPBVFieldValue:fv withRAREWindowViewer:[RAREPlatform getWindowViewer] withBoolean:NO withBoolean:NO withBoolean:NO withRAREUTMutableInteger:yPosition_ withBoolean:NO withJavaUtilArrayList:nil];
  [((id<RAREiWidget>) nil_chk([entryForm getWidgetWithNSString:@"prompt"])) setValueWithId:[NSString stringWithFormat:@"%@:", [((RAREUTJSONObject *) nil_chk(((CCPBVFieldValue *) nil_chk(fv))->field_)) optStringWithNSString:[CCPBVOrderFields PROMPT]]]];
  if (!([(id) widget conformsToProtocol: @protocol(RAREiListHandler)])) {
    [((id<RAREiWidget>) nil_chk(widget)) selectAll];
  }
  [((id<RAREiWidget>) nil_chk(widget)) requestFocus];
  return YES;
}

- (void)updateFieldsTableWithRAREiContainer:(id<RAREiContainer>)entryForm
                        withCCPBVFieldValue:(CCPBVFieldValue *)fv {
  RARETableViewer *table = fieldsTable_;
  if (table != nil) {
    int i = [table indexOfLinkedDataWithId:fv];
    if (i != -1) {
      RARERenderableDataItem *row = [table getWithInt:i];
      [((RARERenderableDataItem *) nil_chk([((RARERenderableDataItem *) nil_chk(row)) getWithInt:1])) setValueWithId:fv];
      [table rowChangedWithInt:i];
    }
    [((CCPBVOrder *) nil_chk(order_)) setDirtyWithBoolean:YES];
    [self updateOrderButtons];
  }
}

- (BOOL)hasNextFieldWithRAREiContainer:(id<RAREiContainer>)entryForm
                           withBoolean:(BOOL)requiredValueOnly
                           withBoolean:(BOOL)emptyValueOnly {
  RARETableViewer *table = fieldsTable_;
  int n = [((RARETableViewer *) nil_chk(table)) getSelectedIndex];
  int len = [table size];
  char required = CCPBVOrders_REQUIRED_FLAG;
  for (int i = n + 1; i < len; i++) {
    RARERenderableDataItem *row = [table getWithInt:i];
    if (!requiredValueOnly || ([((RARERenderableDataItem *) nil_chk(row)) getUserStateFlags] & required) != 0) {
      if (emptyValueOnly) {
        CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((RARERenderableDataItem *) nil_chk(row)) getLinkedData], [CCPBVFieldValue class]);
        if (((CCPBVFieldValue *) nil_chk(fv))->value_ != nil) {
          continue;
        }
        return YES;
      }
      else {
        return YES;
      }
    }
  }
  return NO;
}

- (BOOL)hasPreviousFieldWithRAREiContainer:(id<RAREiContainer>)entryForm
                               withBoolean:(BOOL)requiredValueOnly
                               withBoolean:(BOOL)emptyValueOnly {
  RARETableViewer *table = fieldsTable_;
  int n = [((RARETableViewer *) nil_chk(table)) getSelectedIndex];
  char required = CCPBVOrders_REQUIRED_FLAG;
  for (int i = n - 1; i >= 0; i--) {
    RARERenderableDataItem *row = [table getWithInt:i];
    if (!requiredValueOnly || ([((RARERenderableDataItem *) nil_chk(row)) getUserStateFlags] & required) != 0) {
      if (emptyValueOnly) {
        CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((RARERenderableDataItem *) nil_chk(row)) getLinkedData], [CCPBVFieldValue class]);
        if (((CCPBVFieldValue *) nil_chk(fv))->value_ != nil) {
          continue;
        }
        return YES;
      }
      else {
        return YES;
      }
    }
  }
  return NO;
}

- (BOOL)isComplete {
  RARETableViewer *table = fieldsTable_;
  int len = [((RARETableViewer *) nil_chk(table)) size];
  char required = CCPBVOrders_REQUIRED_FLAG;
  for (int i = 0; i < len; i++) {
    RARERenderableDataItem *row = [table getWithInt:i];
    if (([((RARERenderableDataItem *) nil_chk(row)) getUserStateFlags] & required) != 0) {
      CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([row getLinkedData], [CCPBVFieldValue class]);
      if (((CCPBVFieldValue *) nil_chk(fv))->value_ == nil) {
        return NO;
      }
    }
  }
  return YES;
}

- (void)moveToNextFieldWithRAREiContainer:(id<RAREiContainer>)entryForm
                              withBoolean:(BOOL)requiredValueOnly
                              withBoolean:(BOOL)emptyValueOnly {
  RARETableViewer *table = fieldsTable_;
  int n = [((RARETableViewer *) nil_chk(table)) getSelectedIndex];
  int len = [table size];
  char required = CCPBVOrders_REQUIRED_FLAG;
  RARERenderableDataItem *nextRow = nil;
  int index = -1;
  for (int i = n + 1; i < len; i++) {
    RARERenderableDataItem *row = [table getWithInt:i];
    if (!requiredValueOnly || ([((RARERenderableDataItem *) nil_chk(row)) getUserStateFlags] & required) != 0) {
      if (emptyValueOnly) {
        CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((RARERenderableDataItem *) nil_chk(row)) getLinkedData], [CCPBVFieldValue class]);
        if (((CCPBVFieldValue *) nil_chk(fv))->value_ != nil) {
          continue;
        }
      }
      index = i;
      nextRow = row;
      break;
    }
  }
  if (nextRow != nil) {
    [table setSelectedIndexWithInt:index];
    [table scrollRowToVisibleWithInt:index];
    if ([((id<RAREiContainer>) nil_chk(entryForm)) isAttached]) {
      [self updateEntryFormWithRAREiContainer:entryForm withRARERenderableDataItem:nextRow];
    }
  }
  if ([((id<RAREiContainer>) nil_chk(entryForm)) isAttached]) {
    [self updateNavigationButtons];
  }
}

- (void)moveToPreviousFieldWithRAREiContainer:(id<RAREiContainer>)entryForm
                                  withBoolean:(BOOL)requiredValueOnly
                                  withBoolean:(BOOL)emptyValueOnly {
  RARETableViewer *table = fieldsTable_;
  int n = [((RARETableViewer *) nil_chk(table)) getSelectedIndex];
  char required = CCPBVOrders_REQUIRED_FLAG;
  RARERenderableDataItem *prevRow = nil;
  int index = -1;
  for (int i = n - 1; i >= 0; i--) {
    RARERenderableDataItem *row = [table getWithInt:i];
    if (!requiredValueOnly || ([((RARERenderableDataItem *) nil_chk(row)) getUserStateFlags] & required) != 0) {
      if (emptyValueOnly) {
        CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((RARERenderableDataItem *) nil_chk(row)) getLinkedData], [CCPBVFieldValue class]);
        if (((CCPBVFieldValue *) nil_chk(fv))->value_ != nil) {
          continue;
        }
      }
      prevRow = row;
      index = i;
      break;
    }
  }
  if (prevRow != nil) {
    [table setSelectedIndexWithInt:index];
    [table scrollRowToVisibleWithInt:index];
    [self updateEntryFormWithRAREiContainer:entryForm withRARERenderableDataItem:prevRow];
  }
  [self updateNavigationButtons];
}

- (void)reselectTableRowWithRAREiContainer:(id<RAREiContainer>)entryForm
                       withCCPBVFieldValue:(CCPBVFieldValue *)fv {
  RARETableViewer *table = fieldsTable_;
  if (table != nil) {
    int i = [table indexOfLinkedDataWithId:fv];
    if (i != -1) {
      [table setSelectedIndexWithInt:i];
    }
  }
}

- (void)resetValueWithBoolean:(BOOL)clear {
  RAREWidgetPaneViewer *pane = (RAREWidgetPaneViewer *) check_class_cast([((id<RAREiContainer>) nil_chk(entryForm_)) getWidgetWithNSString:@"valuePane"], [RAREWidgetPaneViewer class]);
  id<RAREiWidget> fw = [((RAREWidgetPaneViewer *) nil_chk(pane)) getWidget];
  CCPBVFieldValue *fv = (CCPBVFieldValue *) check_class_cast([((id<RAREiWidget>) nil_chk(fw)) getLinkedData], [CCPBVFieldValue class]);
  if (clear) {
    [((CCPBVFieldValue *) nil_chk(fv)) clear];
  }
  else {
    [((CCPBVFieldValue *) nil_chk(fv)) reset];
  }
  [CCPBVFormsManager updateWidgetFromValueWithRAREiWidget:fw withCCPBVFieldValue:fv];
  [self updateFieldsTableWithRAREiContainer:entryForm_ withCCPBVFieldValue:fv];
}

- (id<RAREiWidget>)getMessageBoxWithRAREiWidget:(id<RAREiWidget>)widget {
  id<RAREiContainer> c = [((id<RAREiWidget>) nil_chk(widget)) getParent];
  while ((c != nil) && ![((NSString *) nil_chk([c getName])) isEqual:@"formFields"]) {
    c = [((id<RAREiContainer>) nil_chk(c)) getParent];
  }
  if (c != nil) {
    c = [c getParent];
  }
  return (c == nil) ? nil : [c getWidgetWithNSString:@"messageBox"];
}

- (void)updateNavigationButtons {
  id<RAREiWidget> nextField = [((id<RAREiContainer>) nil_chk(entryForm_)) getWidgetWithNSString:@"nextField"];
  id<RAREiWidget> previousField = [entryForm_ getWidgetWithNSString:@"previousField"];
  [((id<RAREiWidget>) nil_chk(nextField)) setEnabledWithBoolean:[self hasNextFieldWithRAREiContainer:entryForm_ withBoolean:CCPBVOrderEntry_navigateToRequiredOnly_ withBoolean:CCPBVOrderEntry_navigateToEmptyOnly_]];
  [((id<RAREiWidget>) nil_chk(previousField)) setEnabledWithBoolean:[self hasPreviousFieldWithRAREiContainer:entryForm_ withBoolean:CCPBVOrderEntry_navigateToRequiredOnly_ withBoolean:CCPBVOrderEntry_navigateToEmptyOnly_]];
}

- (void)updateOrderButtons {
  id<RAREiContainer> c = (id<RAREiContainer>) check_protocol_cast([((id<RAREiFormViewer>) nil_chk([((RARETableViewer *) nil_chk(fieldsTable_)) getFormViewer])) getWidgetWithNSString:@"buttonPanel"], @protocol(RAREiContainer));
  RAREPushButtonWidget *pb = (RAREPushButtonWidget *) check_class_cast([((id<RAREiContainer>) nil_chk(c)) getWidgetWithNSString:@"yesButton"], [RAREPushButtonWidget class]);
  BOOL complete = [self isComplete];
  [((CCPBVOrder *) nil_chk(order_)) setCompleteWithBoolean:complete];
  [((RAREPushButtonWidget *) nil_chk(pb)) setForegroundWithRAREUIColor:[RAREUIColorHelper getColorWithNSString:complete ? @"orderCompleteColor" : @"orderIncompleteColor"]];
  [pb update];
  if (complete && !wasComplete_ && entryForm_ != nil && [entryForm_ isAttached]) {
    wasComplete_ = YES;
    NSString *text = [RAREPlatform getResourceAsStringWithNSString:@"bv.oe.text.order_complete"];
    [RAREUINotifier showMessageWithNSString:text withInt:500 withRAREUINotifier_LocationEnum:[RAREUINotifier_LocationEnum CENTER] withRAREiPlatformIcon:nil withJavaLangRunnable:[[CCPBVOrderEntry_$5 alloc] initWithCCPBVOrderEntry:self]];
  }
}

- (void)copyAllFieldsTo:(CCPBVOrderEntry *)other {
  [super copyAllFieldsTo:other];
  other->entryForm_ = entryForm_;
  other->entryFormInWorkspace_ = entryFormInWorkspace_;
  other->fieldsTable_ = fieldsTable_;
  other->order_ = order_;
  other->wasComplete_ = wasComplete_;
  other->yPosition_ = yPosition_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "updateEntryFormWithRAREiContainer:withRARERenderableDataItem:", NULL, "Z", 0x1, NULL },
    { "hasNextFieldWithRAREiContainer:withBoolean:withBoolean:", NULL, "Z", 0x4, NULL },
    { "hasPreviousFieldWithRAREiContainer:withBoolean:withBoolean:", NULL, "Z", 0x4, NULL },
    { "isComplete", NULL, "Z", 0x4, NULL },
    { "moveToNextFieldWithRAREiContainer:withBoolean:withBoolean:", NULL, "V", 0x4, NULL },
    { "moveToPreviousFieldWithRAREiContainer:withBoolean:withBoolean:", NULL, "V", 0x4, NULL },
    { "reselectTableRowWithRAREiContainer:withCCPBVFieldValue:", NULL, "V", 0x4, NULL },
    { "resetValueWithBoolean:", NULL, "V", 0x4, NULL },
    { "getMessageBoxWithRAREiWidget:", NULL, "LRAREiWidget", 0x2, NULL },
    { "updateNavigationButtons", NULL, "V", 0x2, NULL },
    { "updateOrderButtons", NULL, "V", 0x2, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "navigateToRequiredOnly_", NULL, 0x8, "Z" },
    { "navigateToEmptyOnly_", NULL, 0x8, "Z" },
    { "showRequiredOnly_", NULL, 0x8, "Z" },
    { "yPosition_", NULL, 0x0, "LRAREUTMutableInteger" },
    { "fieldsTable_", NULL, 0x0, "LRARETableViewer" },
    { "entryForm_", NULL, 0x0, "LRAREiContainer" },
    { "order_", NULL, 0x0, "LCCPBVOrder" },
    { "entryFormInWorkspace_", NULL, 0x0, "Z" },
    { "wasComplete_", NULL, 0x0, "Z" },
  };
  static J2ObjcClassInfo _CCPBVOrderEntry = { "OrderEntry", "com.sparseware.bellavista.oe", NULL, 0x1, 11, methods, 9, fields, 0, NULL};
  return &_CCPBVOrderEntry;
}

@end
@implementation CCPBVOrderEntry_$1

- (BOOL)passesWithId:(id)value
withRAREUTiStringConverter:(id<RAREUTiStringConverter>)converter {
  return ([((RARERenderableDataItem *) check_class_cast(value, [RARERenderableDataItem class])) getUserStateFlags] & 64) != 0;
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "passesWithId:withRAREUTiStringConverter:", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcClassInfo _CCPBVOrderEntry_$1 = { "$1", "com.sparseware.bellavista.oe", "OrderEntry", 0x8000, 1, methods, 0, NULL, 0, NULL};
  return &_CCPBVOrderEntry_$1;
}

@end
@implementation CCPBVOrderEntry_$2

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  if ([returnValue isKindOfClass:[JavaLangThrowable class]]) {
    [CCPBVUtils handleErrorWithJavaLangThrowable:(JavaLangThrowable *) check_class_cast(returnValue, [JavaLangThrowable class])];
    return;
  }
  this$0_->entryForm_ = (id<RAREiContainer>) check_protocol_cast(returnValue, @protocol(RAREiContainer));
  [((id<RAREiContainer>) nil_chk(this$0_->entryForm_)) setAutoDisposeWithBoolean:NO];
  [this$0_ moveToNextFieldWithRAREiContainer:this$0_->entryForm_ withBoolean:YES withBoolean:YES];
}

- (id)initWithCCPBVOrderEntry:(CCPBVOrderEntry *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVOrderEntry" },
  };
  static J2ObjcClassInfo _CCPBVOrderEntry_$2 = { "$2", "com.sparseware.bellavista.oe", "OrderEntry", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVOrderEntry_$2;
}

@end
@implementation CCPBVOrderEntry_$3

- (void)run {
  [this$0_ moveToNextFieldWithRAREiContainer:this$0_->entryForm_ withBoolean:YES withBoolean:YES];
}

- (id)initWithCCPBVOrderEntry:(CCPBVOrderEntry *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVOrderEntry" },
  };
  static J2ObjcClassInfo _CCPBVOrderEntry_$3 = { "$3", "com.sparseware.bellavista.oe", "OrderEntry", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVOrderEntry_$3;
}

@end
@implementation CCPBVOrderEntry_$4

- (BOOL)passesWithId:(id)value
withRAREUTiStringConverter:(id<RAREUTiStringConverter>)converter {
  return ([((RARERenderableDataItem *) check_class_cast(value, [RARERenderableDataItem class])) getUserStateFlags] & 64) != 0;
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "passesWithId:withRAREUTiStringConverter:", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcClassInfo _CCPBVOrderEntry_$4 = { "$4", "com.sparseware.bellavista.oe", "OrderEntry", 0x8000, 1, methods, 0, NULL, 0, NULL};
  return &_CCPBVOrderEntry_$4;
}

@end
@implementation CCPBVOrderEntry_$5

- (void)run {
  if (this$0_->entryFormInWorkspace_ && (this$0_->entryForm_ != nil) && [this$0_->entryForm_ isAttached]) {
    [CCPBVUtils popViewerStack];
  }
}

- (id)initWithCCPBVOrderEntry:(CCPBVOrderEntry *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVOrderEntry" },
  };
  static J2ObjcClassInfo _CCPBVOrderEntry_$5 = { "$5", "com.sparseware.bellavista.oe", "OrderEntry", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVOrderEntry_$5;
}

@end
