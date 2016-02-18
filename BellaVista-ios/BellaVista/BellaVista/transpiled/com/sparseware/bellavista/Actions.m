//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Actions.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/ui/ActionBar.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIAction.h"
#include "com/appnativa/rare/ui/UIImageIcon.h"
#include "com/appnativa/rare/ui/UIScreen.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/ui/event/WindowEvent.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/viewer/SplitPaneViewer.h"
#include "com/appnativa/rare/viewer/TableViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iTarget.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/Actions.h"
#include "com/sparseware/bellavista/OrderManager.h"
#include "com/sparseware/bellavista/PatientSelect.h"
#include "com/sparseware/bellavista/Utils.h"
#include "java/lang/CharSequence.h"
#include "java/lang/Exception.h"
#include "java/util/EventObject.h"

@implementation CCPBVActions

- (id)init {
  return [super init];
}

- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (void)onGoBackWithNSString:(NSString *)eventName
             withRAREiWidget:(id<RAREiWidget>)widget
     withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVUtils popViewerStack];
}

- (void)onExitWithNSString:(NSString *)eventName
           withRAREiWidget:(id<RAREiWidget>)widget
   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([event isKindOfClass:[RAREWindowEvent class]]) {
    [((RAREWindowEvent *) check_class_cast(event, [RAREWindowEvent class])) consume];
    [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVActions_$1 alloc] init]];
  }
  else {
    [CCPBVUtils exit];
  }
}

- (void)onPreferencesWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVUtils showDialogWithNSString:@"/settings.rml" withBoolean:YES withBoolean:YES];
}

- (void)onLockWithNSString:(NSString *)eventName
           withRAREiWidget:(id<RAREiWidget>)widget
   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVUtils lockApplicationWithBoolean:NO];
}

- (void)onChangePatientWithNSString:(NSString *)eventName
                    withRAREiWidget:(id<RAREiWidget>)widget
            withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVPatientSelect changePatientWithRAREiWidget:widget withCCPBVActionPath:nil];
}

- (void)onFlagsWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
  @try {
    (void) [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) openDialogWithNSString:@"/flags.rml"];
  }
  @catch (JavaLangException *e) {
    [CCPBVUtils handleErrorWithJavaLangThrowable:e];
  }
}

- (void)onConfigureFullscreenButtonWithNSString:(NSString *)eventName
                                withRAREiWidget:(id<RAREiWidget>)widget
                        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (![RAREUIScreen isLargeScreen]) {
    [((id<RAREiWidget>) nil_chk(widget)) setVisibleWithBoolean:NO];
  }
  else {
    id<RAREiPlatformIcon> fullscreen = [RAREPlatform getResourceAsIconWithNSString:@"bv.icon.fullscreen"];
    RAREUIAction *action = [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getActionWithNSString:@"bv.action.fullscreen"];
    [((RAREUIAction *) nil_chk(action)) setIconWithRAREiPlatformIcon:fullscreen];
  }
}

- (void)onShownFullscreenButtonWithNSString:(NSString *)eventName
                            withRAREiWidget:(id<RAREiWidget>)widget
                    withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([RAREUIScreen isLargeScreen]) {
    RAREUIAction *action = [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getActionWithNSString:@"bv.action.fullscreen"];
    [((RAREUIAction *) nil_chk(action)) setContextWithRAREiWidget:widget];
  }
}

- (void)onShowCartWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVOrderManager showCart];
}

- (void)onOrderDiscontinueWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  [CCPBVOrderManager orderDiscontinueWithRARETableViewer:table withInt:index];
}

- (void)onOrderRenewAndDiscontinueWithNSString:(NSString *)eventName
                               withRAREiWidget:(id<RAREiWidget>)widget
                       withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  RARERenderableDataItem *row = [table getWithInt:index];
  RARERenderableDataItem *orderItem = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:1];
  RARERenderableDataItem *order = [row getWithInt:0];
  NSString *type = [((RARERenderableDataItem *) nil_chk(order)) description];
  [CCPBVOrderManager orderRenewAndDiscontinueWithNSString:type withRARERenderableDataItem:orderItem withNSString:(NSString *) check_class_cast([order getLinkedData], [NSString class])];
}

- (void)onOrderSignWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  [CCPBVOrderManager orderSignWithRARETableViewer:table withInt:index];
}

- (void)onOrderRewriteWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  RARERenderableDataItem *row = [table getWithInt:index];
  RARERenderableDataItem *orderItem = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:1];
  NSString *type = [((RARERenderableDataItem *) nil_chk([row getWithInt:0])) description];
  [CCPBVOrderManager orderRenewWithNSString:type withRARERenderableDataItem:orderItem];
}

- (void)onOrderNewWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (![CCPBVUtils clearViewerStack]) {
    return;
  }
  if ([(id) widget isKindOfClass:[RARETableViewer class]]) {
    RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
    NSString *type = nil;
    RARERenderableDataItem *reorderItem = nil;
    RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"ordersInfo"], [RAREUTJSONObject class]);
    if ([((NSString *) nil_chk([((RARETableViewer *) nil_chk(table)) getName])) isEqual:@"proceduresTable"]) {
      type = [((RAREUTJSONObject *) nil_chk(info)) optStringWithNSString:@"proceduresOrderType" withNSString:nil];
    }
    else if ([((NSString *) nil_chk([table getName])) isEqual:@"labsTable"]) {
      type = [((RAREUTJSONObject *) nil_chk(info)) optStringWithNSString:@"labsOrderType" withNSString:nil];
    }
    else if ([((NSString *) nil_chk([table getName])) isEqual:@"ordersTable"]) {
      int index = [table getContextMenuIndex];
      RARERenderableDataItem *row = [table getWithInt:index];
      reorderItem = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:1];
      type = [((RARERenderableDataItem *) nil_chk([row getWithInt:0])) description];
    }
    [CCPBVOrderManager orderNewWithNSString:type withRARERenderableDataItem:reorderItem];
  }
  else {
    [CCPBVOrderManager orderNewWithNSString:nil withRARERenderableDataItem:nil];
  }
}

- (void)onOrderHoldWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  [CCPBVOrderManager orderChangeHoldStatusWithRARETableViewer:table withInt:index withBoolean:YES];
}

- (void)onOrderUnHoldWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  [CCPBVOrderManager orderChangeHoldStatusWithRARETableViewer:table withInt:index withBoolean:NO];
}

- (void)onOrderFlagWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  [CCPBVOrderManager orderChangeFlagStatusWithRARETableViewer:table withInt:index withBoolean:YES];
}

- (void)onOrderUnFlagWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  int index = [((RARETableViewer *) nil_chk(table)) getContextMenuIndex];
  [CCPBVOrderManager orderChangeFlagStatusWithRARETableViewer:table withInt:index withBoolean:NO];
}

- (void)onFullscreenWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id l = [((id<RAREiWidget>) nil_chk(widget)) getLinkedData];
  if ([l conformsToProtocol: @protocol(RAREiActionListener)]) {
    [((id<RAREiActionListener>) check_protocol_cast(l, @protocol(RAREiActionListener))) actionPerformedWithRAREActionEvent:(RAREActionEvent *) check_class_cast(event, [RAREActionEvent class])];
    return;
  }
  if (![RAREUIScreen isLargeScreen]) {
    [CCPBVUtils popViewerStack];
  }
  else {
    RARESplitPaneViewer *sp = [CCPBVUtils getSplitPaneViewerWithRAREiWidget:widget];
    if (sp == nil) {
      return;
    }
    RAREWindowViewer *w = [RAREPlatform getWindowViewer];
    RAREActionBar *ab = [((RAREWindowViewer *) nil_chk(w)) getActionBar];
    RAREUIAction *action = [w getActionWithNSString:@"bv.action.fullscreen"];
    if (![((RARESplitPaneViewer *) nil_chk(sp)) isRegionVisibleWithInt:0]) {
      [((RAREUIAction *) nil_chk(action)) setIconWithRAREiPlatformIcon:[w getResourceIconWithNSString:@"bv.icon.fullscreen"]];
      [((id<RAREiTarget>) nil_chk([w getTargetWithNSString:@"patient_info"])) setVisibleWithBoolean:YES];
      [sp setRegionVisibleWithInt:0 withBoolean:YES];
      [((RAREActionBar *) nil_chk(ab)) setTitleWithJavaLangCharSequence:[w getTitle]];
    }
    else {
      [((RAREUIAction *) nil_chk(action)) setIconWithRAREiPlatformIcon:[w getResourceIconWithNSString:@"bv.icon.shrink"]];
      [sp setRegionVisibleWithInt:0 withBoolean:NO];
      [((id<RAREiTarget>) nil_chk([w getTargetWithNSString:@"patient_info"])) setVisibleWithBoolean:NO];
      [((RAREActionBar *) nil_chk(ab)) setTitleWithJavaLangCharSequence:(id<JavaLangCharSequence>) check_protocol_cast([((id<RAREiPlatformAppContext>) nil_chk([w getAppContext])) getDataWithId:@"pt_name"], @protocol(JavaLangCharSequence))];
    }
  }
}

+ (BOOL)handledPseudoFullScreenMode {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RAREUIAction *action = [((RAREWindowViewer *) nil_chk(w)) getActionWithNSString:@"bv.action.fullscreen"];
  if ((action != nil)) {
    RAREPushButtonWidget *pb = (RAREPushButtonWidget *) check_class_cast([action getContext], [RAREPushButtonWidget class]);
    if ((pb != nil) && ![pb isDisposed] && [pb isAttached] && [pb isEnabled]) {
      RARESplitPaneViewer *sp = [CCPBVUtils getSplitPaneViewerWithRAREiWidget:pb];
      if (sp == nil) {
        return NO;
      }
      if (![((RARESplitPaneViewer *) nil_chk(sp)) isRegionVisibleWithInt:0]) {
        [pb click];
        return YES;
      }
    }
  }
  return NO;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "handledPseudoFullScreenMode", NULL, "Z", 0x9, NULL },
  };
  static J2ObjcClassInfo _CCPBVActions = { "Actions", "com.sparseware.bellavista", NULL, 0x1, 1, methods, 0, NULL, 0, NULL};
  return &_CCPBVActions;
}

@end
@implementation CCPBVActions_$1

- (void)run {
  [CCPBVUtils exit];
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcClassInfo _CCPBVActions_$1 = { "$1", "com.sparseware.bellavista", "Actions", 0x8000, 0, NULL, 0, NULL, 0, NULL};
  return &_CCPBVActions_$1;
}

@end
