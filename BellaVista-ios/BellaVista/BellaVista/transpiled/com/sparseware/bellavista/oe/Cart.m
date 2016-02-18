//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/oe/Cart.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/exception/ExpandVetoException.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/event/ExpansionEvent.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/viewer/ListBoxViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/IdentityArrayList.h"
#include "com/sparseware/bellavista/OrderManager.h"
#include "com/sparseware/bellavista/Orders.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/oe/Cart.h"
#include "com/sparseware/bellavista/oe/Order.h"
#include "com/sparseware/bellavista/oe/OrderFields.h"
#include "java/lang/Boolean.h"
#include "java/lang/Long.h"
#include "java/lang/StringBuilder.h"
#include "java/lang/Throwable.h"
#include "java/util/ArrayList.h"
#include "java/util/EventObject.h"
#include "java/util/List.h"

@implementation CCPBVCart

- (void)dataParsedWithRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilList:(id<JavaUtilList>)rows
               withRAREActionLink:(RAREActionLink *)link {
}

- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (actionbar_ != nil) {
    [actionbar_ dispose];
    actionbar_ = nil;
  }
}

- (void)onConfigureWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
  actionbar_ = (id<RAREiContainer>) check_protocol_cast([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:@"rowActionBar"], @protocol(RAREiContainer));
  [fv removeWidgetWithRAREiWidget:actionbar_];
  RAREListBoxViewer *lb = (RAREListBoxViewer *) check_class_cast([fv getWidgetWithNSString:@"cart"], [RAREListBoxViewer class]);
  [((RAREListBoxViewer *) nil_chk(lb)) setRowEditingWidgetWithRAREiWidget:actionbar_ withBoolean:YES];
  [lb setLinkedDataWithId:[JavaLangLong valueOfWithLong:[CCPBVOrderManager getOrderCartLastModifiedTime]]];
  [lb setRowEditModeListenerWithRAREiExpansionListener:[[CCPBVCart_$1 alloc] initWithCCPBVCart:self withRAREListBoxViewer:lb]];
  id<JavaUtilList> list = [CCPBVCart createCartList];
  [lb setAllWithJavaUtilCollection:list];
  [lb setEnabledWithBoolean:[CCPBVOrderManager getOrderBeingEdited] == nil];
  if ([lb isEnabled]) {
    [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"emptyButton"])) setEnabledWithBoolean:YES];
    [self checkForIncompleteOrdersWithRAREListBoxViewer:lb];
  }
  else {
    [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"signButton"])) setEnabledWithBoolean:NO];
    [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"emptyButton"])) setEnabledWithBoolean:NO];
  }
}

- (void)onCartWillExpandWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiContainer> c = (id<RAREiContainer>) check_protocol_cast([((RAREPushButtonWidget *) check_class_cast(widget, [RAREPushButtonWidget class])) getPopupWidget], @protocol(RAREiContainer));
  RAREListBoxViewer *lb = (RAREListBoxViewer *) check_class_cast([((id<RAREiContainer>) nil_chk(c)) getWidgetWithNSString:@"cart"], [RAREListBoxViewer class]);
  [((RAREListBoxViewer *) nil_chk(lb)) clearSelection];
  id<JavaUtilList> list = [CCPBVCart createCartList];
  [lb setAllWithJavaUtilCollection:list];
  [lb setEnabledWithBoolean:[CCPBVOrderManager getOrderBeingEdited] == nil];
  if ([lb isEnabled]) {
    [((id<RAREiWidget>) nil_chk([c getWidgetWithNSString:@"emptyButton"])) setEnabledWithBoolean:YES];
    [self checkForIncompleteOrdersWithRAREListBoxViewer:lb];
  }
  else {
    [((id<RAREiWidget>) nil_chk([c getWidgetWithNSString:@"signButton"])) setEnabledWithBoolean:NO];
    [((id<RAREiWidget>) nil_chk([c getWidgetWithNSString:@"emptyButton"])) setEnabledWithBoolean:NO];
  }
}

+ (id<JavaUtilList>)createCartList {
  RAREUTIdentityArrayList *cart = [CCPBVOrderManager getOrderCartWithBoolean:NO];
  int len = (cart == nil) ? 0 : [cart size];
  JavaUtilArrayList *rows = [[JavaUtilArrayList alloc] initWithInt:len];
  JavaLangStringBuilder *sb = [[JavaLangStringBuilder alloc] init];
  for (int i = 0; i < len; i++) {
    RARERenderableDataItem *row = [[RARERenderableDataItem alloc] init];
    CCPBVOrder *o = [((RAREUTIdentityArrayList *) nil_chk(cart)) getWithInt:i];
    [sb setLengthWithInt:0];
    (void) [sb appendWithNSString:@"<html>"];
    if (((CCPBVOrder *) nil_chk(o))->actionType_ == [CCPBVOrder_ActionTypeEnum DISCONTINUED]) {
      (void) [sb appendWithNSString:@"<s>"];
    }
    (void) [((JavaLangStringBuilder *) nil_chk([((JavaLangStringBuilder *) nil_chk([sb appendWithNSString:@"<b>"])) appendWithId:[((RARERenderableDataItem *) nil_chk(o->orderedItem_)) getValue]])) appendWithNSString:@"</b><br/><i>"];
    (void) [((JavaLangStringBuilder *) nil_chk([sb appendWithId:[((RARERenderableDataItem *) nil_chk(o->directionsItem_)) getValue]])) appendWithNSString:@"</i>"];
    if (o->actionType_ == [CCPBVOrder_ActionTypeEnum DISCONTINUED]) {
      (void) [sb appendWithNSString:@"</s>"];
    }
    (void) [sb appendWithNSString:@"</html>"];
    [row setValueWithId:[sb description]];
    [row setIconWithRAREiPlatformIcon:[o getActionTypeIcon]];
    [row setLinkedDataWithId:o];
    [rows addWithId:row];
  }
  return rows;
}

- (void)onEmptyCartWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  id<RAREiFunctionCallback> cb = [[CCPBVCart_$2 alloc] initWithCCPBVCart:self withRAREiWidget:widget];
  NSString *yes = [((RAREWindowViewer *) nil_chk(w)) getStringWithNSString:@"bv.oe.text.empty_cart"];
  [w yesNoWithNSString:nil withId:[w getStringWithNSString:@"bv.oe.text.empty_cart_message"] withNSString:yes withNSString:nil withRAREiFunctionCallback:cb];
}

- (void)onDeleteOrderWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RAREListBoxViewer *lb = (RAREListBoxViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getFormViewer])) getWidgetWithNSString:@"cart"], [RAREListBoxViewer class]);
  int row = [((RAREListBoxViewer *) nil_chk(lb)) getEditingRow];
  CCPBVOrder *order = (CCPBVOrder *) check_class_cast([((RARERenderableDataItem *) nil_chk([lb getWithInt:row])) getLinkedData], [CCPBVOrder class]);
  if ([CCPBVOrders verifyOrderEntryDelete]) {
    id<RAREiFunctionCallback> cb = [[CCPBVCart_$3 alloc] initWithCCPBVCart:self withCCPBVOrder:order withRAREListBoxViewer:lb withInt:row];
    NSString *yes = [((RAREWindowViewer *) nil_chk(w)) getStringWithNSString:@"bv.action.delete"];
    [w yesNoWithNSString:nil withId:[w getStringWithNSString:@"bv.oe.text.delete_order_message"] withNSString:yes withNSString:nil withRAREiFunctionCallback:cb];
  }
  else {
    [CCPBVOrderManager removeOrderFromCartWithCCPBVOrder:order];
    (void) [lb removeWithInt:row];
    if ([lb isEmpty]) {
      [self closePopupOrWindowWithRAREiWidget:lb];
    }
    else {
      [self checkForIncompleteOrdersWithRAREListBoxViewer:lb];
    }
  }
}

- (void)checkForIncompleteOrdersWithRAREListBoxViewer:(RAREListBoxViewer *)lb {
  BOOL hasComplete = NO;
  int len = [((RAREListBoxViewer *) nil_chk(lb)) size];
  for (int i = 0; i < len; i++) {
    if ([((CCPBVOrder *) check_class_cast([((RARERenderableDataItem *) nil_chk([lb getWithInt:i])) getLinkedData], [CCPBVOrder class])) isComplete]) {
      hasComplete = YES;
      break;
    }
  }
  [((id<RAREiWidget>) nil_chk([((id<RAREiFormViewer>) nil_chk([lb getFormViewer])) getWidgetWithNSString:@"signButton"])) setEnabledWithBoolean:hasComplete];
}

- (void)onCartActionWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event {
  CCPBVOrder *o = (CCPBVOrder *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getSelectionData], [CCPBVOrder class]);
  if (((CCPBVOrder *) nil_chk(o))->orderFields_ == nil) {
    [((RAREListBoxViewer *) check_class_cast(widget, [RAREListBoxViewer class])) flashSelectedRow];
    return;
  }
  [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVCart_$4 alloc] initWithCCPBVOrder:o]];
  [self closePopupOrWindowWithRAREiWidget:widget];
}

- (void)onSubmitWithNSString:(NSString *)eventName
             withRAREiWidget:(id<RAREiWidget>)widget
     withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREListBoxViewer *lb = (RAREListBoxViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getFormViewer])) getWidgetWithNSString:@"cart"], [RAREListBoxViewer class]);
  id<RAREiFunctionCallback> cb = [[CCPBVCart_$5 alloc] initWithCCPBVCart:self withRAREListBoxViewer:lb];
  [CCPBVOrderManager sumbitOrderCartWithRAREiFunctionCallback:cb];
}

- (void)onSubmitOrderWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREListBoxViewer *lb = (RAREListBoxViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getFormViewer])) getWidgetWithNSString:@"cart"], [RAREListBoxViewer class]);
  int row = [((RAREListBoxViewer *) nil_chk(lb)) getEditingRow];
  CCPBVOrder *order = (CCPBVOrder *) check_class_cast([((RARERenderableDataItem *) nil_chk([lb getWithInt:row])) getLinkedData], [CCPBVOrder class]);
  id<RAREiFunctionCallback> cb = [[CCPBVCart_$6 alloc] initWithCCPBVCart:self withRAREListBoxViewer:lb withInt:row];
  [CCPBVOrderManager sumbitOrderWithCCPBVOrder:order withRAREiFunctionCallback:cb];
}

- (void)onCloseWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [self closePopupOrWindowWithRAREiWidget:widget];
}

- (void)closePopupOrWindowWithRAREiWidget:(id<RAREiWidget>)widget {
  if ([((id<RAREiWidget>) nil_chk(widget)) isInPopup]) {
    [widget hidePopupContainer];
  }
  else if ([widget getWindow] != [RAREPlatform getWindowViewer]) {
    [((RAREWindowViewer *) nil_chk([widget getWindow])) close];
  }
}

- (id)init {
  return [super init];
}

- (void)copyAllFieldsTo:(CCPBVCart *)other {
  [super copyAllFieldsTo:other];
  other->actionbar_ = actionbar_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "dataParsedWithRAREiWidget:withJavaUtilList:withRAREActionLink:", NULL, "V", 0x4, NULL },
    { "createCartList", NULL, "LJavaUtilList", 0x9, NULL },
    { "checkForIncompleteOrdersWithRAREListBoxViewer:", NULL, "V", 0x2, NULL },
    { "closePopupOrWindowWithRAREiWidget:", NULL, "V", 0x0, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "actionbar_", NULL, 0x0, "LRAREiContainer" },
  };
  static J2ObjcClassInfo _CCPBVCart = { "Cart", "com.sparseware.bellavista.oe", NULL, 0x1, 4, methods, 1, fields, 0, NULL};
  return &_CCPBVCart;
}

@end
@implementation CCPBVCart_$1

- (void)itemWillExpandWithRAREExpansionEvent:(RAREExpansionEvent *)event {
  int row = [((RAREListBoxViewer *) nil_chk(val$lb_)) getEditingRow];
  CCPBVOrder *order = (CCPBVOrder *) check_class_cast([((RARERenderableDataItem *) nil_chk([val$lb_ getWithInt:row])) getLinkedData], [CCPBVOrder class]);
  [((id<RAREiWidget>) nil_chk([((id<RAREiContainer>) nil_chk(this$0_->actionbar_)) getWidgetWithNSString:@"signOrder"])) setEnabledWithBoolean:[((CCPBVOrder *) nil_chk(order)) isComplete]];
}

- (void)itemWillCollapseWithRAREExpansionEvent:(RAREExpansionEvent *)event {
}

- (id)initWithCCPBVCart:(CCPBVCart *)outer$
  withRAREListBoxViewer:(RAREListBoxViewer *)capture$0 {
  this$0_ = outer$;
  val$lb_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "itemWillExpandWithRAREExpansionEvent:", NULL, "V", 0x1, "RAREExpandVetoException" },
    { "itemWillCollapseWithRAREExpansionEvent:", NULL, "V", 0x1, "RAREExpandVetoException" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCart" },
    { "val$lb_", NULL, 0x1012, "LRAREListBoxViewer" },
  };
  static J2ObjcClassInfo _CCPBVCart_$1 = { "$1", "com.sparseware.bellavista.oe", "Cart", 0x8000, 2, methods, 2, fields, 0, NULL};
  return &_CCPBVCart_$1;
}

@end
@implementation CCPBVCart_$2

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  if (!canceled && [((JavaLangBoolean *) nil_chk([JavaLangBoolean getTRUE])) isEqual:returnValue]) {
    [CCPBVOrderManager emptyOrderCart];
    RAREListBoxViewer *lb = (RAREListBoxViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk([((id<RAREiWidget>) nil_chk(val$widget_)) getFormViewer])) getWidgetWithNSString:@"cart"], [RAREListBoxViewer class]);
    if (![((RAREListBoxViewer *) nil_chk(lb)) isDisposed]) {
      [lb clear];
      [this$0_ closePopupOrWindowWithRAREiWidget:lb];
    }
  }
}

- (id)initWithCCPBVCart:(CCPBVCart *)outer$
        withRAREiWidget:(id<RAREiWidget>)capture$0 {
  this$0_ = outer$;
  val$widget_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCart" },
    { "val$widget_", NULL, 0x1012, "LRAREiWidget" },
  };
  static J2ObjcClassInfo _CCPBVCart_$2 = { "$2", "com.sparseware.bellavista.oe", "Cart", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVCart_$2;
}

@end
@implementation CCPBVCart_$3

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  if (!canceled && [((JavaLangBoolean *) nil_chk([JavaLangBoolean getTRUE])) isEqual:returnValue]) {
    [CCPBVOrderManager removeOrderFromCartWithCCPBVOrder:val$order_];
    (void) [((RAREListBoxViewer *) nil_chk(val$lb_)) removeWithInt:val$row_];
    if ([val$lb_ isEmpty]) {
      [this$0_ closePopupOrWindowWithRAREiWidget:val$lb_];
    }
  }
}

- (id)initWithCCPBVCart:(CCPBVCart *)outer$
         withCCPBVOrder:(CCPBVOrder *)capture$0
  withRAREListBoxViewer:(RAREListBoxViewer *)capture$1
                withInt:(int)capture$2 {
  this$0_ = outer$;
  val$order_ = capture$0;
  val$lb_ = capture$1;
  val$row_ = capture$2;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCart" },
    { "val$order_", NULL, 0x1012, "LCCPBVOrder" },
    { "val$lb_", NULL, 0x1012, "LRAREListBoxViewer" },
    { "val$row_", NULL, 0x1012, "I" },
  };
  static J2ObjcClassInfo _CCPBVCart_$3 = { "$3", "com.sparseware.bellavista.oe", "Cart", 0x8000, 0, NULL, 4, fields, 0, NULL};
  return &_CCPBVCart_$3;
}

@end
@implementation CCPBVCart_$4

- (void)run {
  [CCPBVOrderManager showOrderFormWithCCPBVOrder:val$o_];
}

- (id)initWithCCPBVOrder:(CCPBVOrder *)capture$0 {
  val$o_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$o_", NULL, 0x1012, "LCCPBVOrder" },
  };
  static J2ObjcClassInfo _CCPBVCart_$4 = { "$4", "com.sparseware.bellavista.oe", "Cart", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVCart_$4;
}

@end
@implementation CCPBVCart_$5

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  if (canceled) {
    if ([returnValue isKindOfClass:[JavaLangThrowable class]]) {
      [CCPBVUtils handleErrorWithJavaLangThrowable:(JavaLangThrowable *) check_class_cast(returnValue, [JavaLangThrowable class])];
    }
  }
  else {
    [((RAREListBoxViewer *) nil_chk(val$lb_)) clear];
    [this$0_ closePopupOrWindowWithRAREiWidget:val$lb_];
  }
}

- (id)initWithCCPBVCart:(CCPBVCart *)outer$
  withRAREListBoxViewer:(RAREListBoxViewer *)capture$0 {
  this$0_ = outer$;
  val$lb_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCart" },
    { "val$lb_", NULL, 0x1012, "LRAREListBoxViewer" },
  };
  static J2ObjcClassInfo _CCPBVCart_$5 = { "$5", "com.sparseware.bellavista.oe", "Cart", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVCart_$5;
}

@end
@implementation CCPBVCart_$6

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  if (canceled) {
    if ([returnValue isKindOfClass:[JavaLangThrowable class]]) {
      [CCPBVUtils handleErrorWithJavaLangThrowable:(JavaLangThrowable *) check_class_cast(returnValue, [JavaLangThrowable class])];
    }
  }
  else {
    (void) [((RAREListBoxViewer *) nil_chk(val$lb_)) removeWithInt:val$row_];
    if ([val$lb_ isEmpty]) {
      [this$0_ closePopupOrWindowWithRAREiWidget:val$lb_];
    }
    else {
      [this$0_ checkForIncompleteOrdersWithRAREListBoxViewer:val$lb_];
    }
  }
}

- (id)initWithCCPBVCart:(CCPBVCart *)outer$
  withRAREListBoxViewer:(RAREListBoxViewer *)capture$0
                withInt:(int)capture$1 {
  this$0_ = outer$;
  val$lb_ = capture$0;
  val$row_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCart" },
    { "val$lb_", NULL, 0x1012, "LRAREListBoxViewer" },
    { "val$row_", NULL, 0x1012, "I" },
  };
  static J2ObjcClassInfo _CCPBVCart_$6 = { "$6", "com.sparseware.bellavista.oe", "Cart", 0x8000, 0, NULL, 3, fields, 0, NULL};
  return &_CCPBVCart_$6;
}

@end
