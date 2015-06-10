//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/CardStack.java
//
//  Created by decoteaud on 5/11/15.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/ui/ActionBar.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/ui/event/FlingEvent.h"
#include "com/appnativa/rare/ui/event/KeyEvent.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "com/appnativa/rare/ui/iPlatformComponent.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/ui/iTabPaneComponent.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/TabPaneViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/aWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/CardStack.h"
#include "com/sparseware/bellavista/CardStackUtils.h"
#include "com/sparseware/bellavista/PatientSelect.h"
#include "com/sparseware/bellavista/Utils.h"
#include "java/lang/Math.h"
#include "java/lang/Runnable.h"
#include "java/net/MalformedURLException.h"
#include "java/util/EventObject.h"

@implementation CCPBVCardStack

static int CCPBVCardStack_pinDigitCount_ = 4;

+ (int)GO_DOWN {
  return CCPBVCardStack_GO_DOWN;
}

+ (int)GO_LEFT {
  return CCPBVCardStack_GO_LEFT;
}

+ (int)GO_RIGHT {
  return CCPBVCardStack_GO_RIGHT;
}

+ (int)GO_UP {
  return CCPBVCardStack_GO_UP;
}

+ (int)pinDigitCount {
  return CCPBVCardStack_pinDigitCount_;
}

+ (int *)pinDigitCountRef {
  return &CCPBVCardStack_pinDigitCount_;
}

- (id)init {
  if (self = [super init]) {
    flingVelocityThreshold_ = 500;
    keyTimeThreshold_ = 500;
    RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"cardStackInfo"], [RAREUTJSONObject class]);
    if (info != nil) {
      flingVelocityThreshold_ = [info optIntWithNSString:@"flingVelocityThreshold" withInt:500];
      keyTimeThreshold_ = [info optIntWithNSString:@"keyTimeThreshold" withInt:500];
      CCPBVCardStack_pinDigitCount_ = [info optIntWithNSString:@"pinDigitCount" withInt:4];
    }
  }
  return self;
}

+ (int)getPinDigitCount {
  return CCPBVCardStack_pinDigitCount_;
}

- (void)drillDownWithRAREiViewer:(id<RAREiViewer>)v {
  NSString *url = [CCPBVCardStackUtils getViewerBundleURLWithRAREiViewer:v];
  if (url == nil) {
    id action = [CCPBVCardStackUtils getViewerActionWithRAREiViewer:v];
    if ([action conformsToProtocol: @protocol(RAREiActionListener)]) {
      [((id<RAREiActionListener>) check_protocol_cast(action, @protocol(RAREiActionListener))) actionPerformedWithRAREActionEvent:[[RAREActionEvent alloc] initWithId:v]];
    }
    else if ([action conformsToProtocol: @protocol(JavaLangRunnable)]) {
      [((id<JavaLangRunnable>) check_protocol_cast(action, @protocol(JavaLangRunnable))) run];
    }
    else if ([action isKindOfClass:[NSString class]]) {
      (void) [((RAREaWidget *) check_class_cast(v, [RAREaWidget class])) evaluateCodeWithId:action];
    }
    return;
  }
  [CCPBVUtils pushWorkspaceViewerWithNSString:url withBoolean:YES];
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  switching_ = NO;
}

- (void)goUpWithRAREiWidget:(id<RAREiWidget>)widget {
  if (![CCPBVUtils popWorkspaceViewer]) {
    [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) toBack];
  }
}

- (void)onChangeEventWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  @try {
    [CCPBVCardStackUtils updateTitleWithRAREiViewer:(id<RAREiViewer>) check_protocol_cast(widget, @protocol(RAREiViewer)) withBoolean:NO];
  }
  @finally {
    switching_ = NO;
  }
}

- (void)onConfigurePinValueWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [((RAREActionBar *) nil_chk([((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getActionBar])) setVisibleWithBoolean:NO];
  [((id<RAREiWidget>) nil_chk(widget)) setValueWithId:[CCPBVCardStackUtils generateRandomNumberStringWithInt:CCPBVCardStack_pinDigitCount_]];
}

- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (void)onFlingEventWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event {
  float n = 0;
  RAREFlingEvent *e = (RAREFlingEvent *) check_class_cast(event, [RAREFlingEvent class]);
  [((RAREFlingEvent *) nil_chk(e)) consume];
  if (switching_) {
    return;
  }
  if ([JavaLangMath absWithFloat:[e getGestureX]] >= [JavaLangMath absWithFloat:[e getGestureY]]) {
    n = [e getGestureX];
  }
  else {
    n = [e getGestureY];
    if (([JavaLangMath absWithFloat:n] > flingVelocityThreshold_) && (n > 0)) {
      [self goWithRAREiWidget:widget withInt:CCPBVCardStack_GO_UP];
    }
    return;
  }
  if ([JavaLangMath absWithFloat:n] > flingVelocityThreshold_) {
    if ([CCPBVUtils isReverseFling]) {
      n *= -1;
    }
    if (n > 0) {
      [self goWithRAREiWidget:widget withInt:CCPBVCardStack_GO_LEFT];
    }
    else {
      [self goWithRAREiWidget:widget withInt:CCPBVCardStack_GO_RIGHT];
    }
  }
}

- (void)onKeyUpEventWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREKeyEvent *ke = (RAREKeyEvent *) check_class_cast(event, [RAREKeyEvent class]);
  long long int time = [((RAREKeyEvent *) nil_chk(ke)) getEventTime];
  if ((time - lastKeyTime_ < keyTimeThreshold_) || switching_) {
    return;
  }
  lastKeyTime_ = time;
  if ([ke isEscapeKeyPressed]) {
    [self goWithRAREiWidget:widget withInt:CCPBVCardStack_GO_UP];
  }
}

- (void)onTabPaneConfiguredWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETabPaneViewer *tp = (RARETabPaneViewer *) check_class_cast(widget, [RARETabPaneViewer class]);
  [((RARETabPaneViewer *) nil_chk(tp)) setReloadTimeoutWithLong:60000];
  [((id<RAREiPlatformComponent>) nil_chk([((id<RAREiTabPaneComponent>) nil_chk([tp getTabPaneComponent])) getTabStrip])) setVisibleWithBoolean:NO];
  [CCPBVUtils updateActionBar];
}

- (void)onTapEventWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (switching_) {
    return;
  }
  [self goWithRAREiWidget:widget withInt:CCPBVCardStack_GO_DOWN];
}

- (void)goWithRAREiWidget:(id<RAREiWidget>)widget
                  withInt:(int)direction {
  RAREStackPaneViewer *sp = [CCPBVUtils getStackPaneViewerWithRAREiWidget:widget];
  RARETabPaneViewer *tp = [(id) [((id<RAREiWidget>) nil_chk(widget)) getParent] isKindOfClass:[RARETabPaneViewer class]] ? (RARETabPaneViewer *) check_class_cast([widget getParent], [RARETabPaneViewer class]) : nil;
  if (switching_ || (sp == nil && tp == nil)) {
    return;
  }
  id<JavaLangRunnable> r = [[CCPBVCardStack_$1 alloc] initWithCCPBVCardStack:self withInt:direction withRARETabPaneViewer:tp withRAREiWidget:widget withRAREStackPaneViewer:sp];
  [RAREPlatform invokeLaterWithJavaLangRunnable:r];
}

- (void)switchToWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp
                                withInt:(int)index {
  switching_ = YES;
  id<RAREiFunctionCallback> cb = [[CCPBVCardStack_$2 alloc] initWithRAREStackPaneViewer:sp withInt:index];
  (void) [((RAREStackPaneViewer *) nil_chk(sp)) getViewerWithInt:index withRAREiFunctionCallback:cb];
}

- (void)switchToWithRARETabPaneViewer:(RARETabPaneViewer *)tp
                              withInt:(int)index {
  switching_ = YES;
  id<RAREiFunctionCallback> cb = [[CCPBVCardStack_$3 alloc] initWithRARETabPaneViewer:tp withInt:index];
  (void) [((RARETabPaneViewer *) nil_chk(tp)) getTabViewerWithInt:index withRAREiFunctionCallback:cb];
}

- (void)copyAllFieldsTo:(CCPBVCardStack *)other {
  [super copyAllFieldsTo:other];
  other->bundleIcon_ = bundleIcon_;
  other->flingVelocityThreshold_ = flingVelocityThreshold_;
  other->keyTimeThreshold_ = keyTimeThreshold_;
  other->lastKeyTime_ = lastKeyTime_;
  other->switching_ = switching_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "drillDownWithRAREiViewer:", NULL, "V", 0x1, "JavaNetMalformedURLException" },
    { "goWithRAREiWidget:withInt:", NULL, "V", 0x4, NULL },
    { "switchToWithRAREStackPaneViewer:withInt:", NULL, "V", 0x2, NULL },
    { "switchToWithRARETabPaneViewer:withInt:", NULL, "V", 0x2, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "GO_DOWN_", NULL, 0x19, "I" },
    { "GO_LEFT_", NULL, 0x19, "I" },
    { "GO_RIGHT_", NULL, 0x19, "I" },
    { "GO_UP_", NULL, 0x19, "I" },
    { "bundleIcon_", NULL, 0x4, "LRAREiPlatformIcon" },
    { "lastKeyTime_", NULL, 0x4, "J" },
    { "switching_", NULL, 0x4, "Z" },
    { "flingVelocityThreshold_", NULL, 0x4, "I" },
    { "keyTimeThreshold_", NULL, 0x4, "I" },
    { "pinDigitCount_", NULL, 0x8, "I" },
  };
  static J2ObjcClassInfo _CCPBVCardStack = { "CardStack", "com.sparseware.bellavista", NULL, 0x1, 4, methods, 10, fields, 0, NULL};
  return &_CCPBVCardStack;
}

@end
@implementation CCPBVCardStack_$1

- (void)run {
  switch (val$direction_) {
    case CCPBVCardStack_GO_UP:
    if (val$tp_ != nil) {
      [CCPBVPatientSelect changePatientWithRAREiWidget:val$widget_ withCCPBVActionPath:nil];
    }
    else {
      [this$0_ goUpWithRAREiWidget:val$sp_];
    }
    break;
    case CCPBVCardStack_GO_LEFT:
    if (val$tp_ != nil) {
      if ([val$tp_ getSelectedTab] != 0) {
        [this$0_ switchToWithRARETabPaneViewer:val$tp_ withInt:[val$tp_ getSelectedTab] - 1];
      }
      else {
        [((id<RAREiContainer>) nil_chk([val$tp_ getContainerViewer])) animateWithNSString:@"Rare.anim.pullBackLeft" withRAREiFunctionCallback:nil];
      }
      break;
    }
    if ([((RAREStackPaneViewer *) nil_chk(val$sp_)) getActiveViewerIndex] == 0) {
      [((id<RAREiContainer>) nil_chk([val$sp_ getContainerViewer])) animateWithNSString:@"Rare.anim.pullBackLeft" withRAREiFunctionCallback:nil];
      break;
    }
    [this$0_ switchToWithRAREStackPaneViewer:val$sp_ withInt:[val$sp_ getActiveViewerIndex] - 1];
    break;
    case CCPBVCardStack_GO_RIGHT:
    if (val$tp_ != nil) {
      if ([val$tp_ getSelectedTab] + 1 != [val$tp_ getTabCount]) {
        [this$0_ switchToWithRARETabPaneViewer:val$tp_ withInt:[val$tp_ getSelectedTab] + 1];
      }
      else {
        [((id<RAREiContainer>) nil_chk([val$tp_ getContainerViewer])) animateWithNSString:@"Rare.anim.pullBackRight" withRAREiFunctionCallback:nil];
      }
      break;
    }
    if (([((RAREStackPaneViewer *) nil_chk(val$sp_)) getActiveViewerIndex] == [val$sp_ size] - 1) || ([val$sp_ size] == 1)) {
      [((id<RAREiContainer>) nil_chk([val$sp_ getContainerViewer])) animateWithNSString:@"Rare.anim.pullBackRight" withRAREiFunctionCallback:nil];
      return;
    }
    [this$0_ switchToWithRAREStackPaneViewer:val$sp_ withInt:[val$sp_ getActiveViewerIndex] + 1];
    break;
    case CCPBVCardStack_GO_DOWN:
    default:
    @try {
      [this$0_ drillDownWithRAREiViewer:val$tp_ == nil ? [((RAREStackPaneViewer *) nil_chk(val$sp_)) getActiveViewer] : [val$tp_ getSelectedTabViewer]];
    }
    @catch (JavaNetMalformedURLException *e) {
      [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) handleExceptionWithJavaLangThrowable:e];
    }
    break;
  }
}

- (id)initWithCCPBVCardStack:(CCPBVCardStack *)outer$
                     withInt:(int)capture$0
       withRARETabPaneViewer:(RARETabPaneViewer *)capture$1
             withRAREiWidget:(id<RAREiWidget>)capture$2
     withRAREStackPaneViewer:(RAREStackPaneViewer *)capture$3 {
  this$0_ = outer$;
  val$direction_ = capture$0;
  val$tp_ = capture$1;
  val$widget_ = capture$2;
  val$sp_ = capture$3;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCardStack" },
    { "val$direction_", NULL, 0x1012, "I" },
    { "val$tp_", NULL, 0x1012, "LRARETabPaneViewer" },
    { "val$widget_", NULL, 0x1012, "LRAREiWidget" },
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
  };
  static J2ObjcClassInfo _CCPBVCardStack_$1 = { "$1", "com.sparseware.bellavista", "CardStack", 0x8000, 0, NULL, 5, fields, 0, NULL};
  return &_CCPBVCardStack_$1;
}

@end
@implementation CCPBVCardStack_$2

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  [((RAREStackPaneViewer *) nil_chk(val$sp_)) switchToWithInt:val$index_];
}

- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0
                          withInt:(int)capture$1 {
  val$sp_ = capture$0;
  val$index_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
    { "val$index_", NULL, 0x1012, "I" },
  };
  static J2ObjcClassInfo _CCPBVCardStack_$2 = { "$2", "com.sparseware.bellavista", "CardStack", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVCardStack_$2;
}

@end
@implementation CCPBVCardStack_$3

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  [((RARETabPaneViewer *) nil_chk(val$tp_)) setSelectedTabWithInt:val$index_];
}

- (id)initWithRARETabPaneViewer:(RARETabPaneViewer *)capture$0
                        withInt:(int)capture$1 {
  val$tp_ = capture$0;
  val$index_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$tp_", NULL, 0x1012, "LRARETabPaneViewer" },
    { "val$index_", NULL, 0x1012, "I" },
  };
  static J2ObjcClassInfo _CCPBVCardStack_$3 = { "$3", "com.sparseware.bellavista", "CardStack", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVCardStack_$3;
}

@end
