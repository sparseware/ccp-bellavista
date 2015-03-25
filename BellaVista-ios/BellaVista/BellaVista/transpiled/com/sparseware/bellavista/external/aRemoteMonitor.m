//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aRemoteMonitor.java
//
//  Created by decoteaud on 3/24/15.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iConstants.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/rare/spot/SplitPane.h"
#include "com/appnativa/rare/spot/StackPane.h"
#include "com/appnativa/rare/spot/Viewer.h"
#include "com/appnativa/rare/ui/UIDimension.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/ui/iEventHandler.h"
#include "com/appnativa/rare/viewer/SplitPaneViewer.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/iSPOTElement.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/external/RemoteMonitorEventHandler.h"
#include "com/sparseware/bellavista/external/aRemoteMonitor.h"

@implementation CCPBVaRemoteMonitor

- (id)init {
  if (self = [super init]) {
    toggleButtonName_ = @"toggleButton";
    eventHandlerClassName_ = [[IOSClass classWithClass:[CCPBVRemoteMonitorEventHandler class]] getName];
    patientPropertyName_ = @"_RM_PATIENT_";
  }
  return self;
}

- (BOOL)canMonitorPatientWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)createNumericsViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                              withRAREiContainer:(id<RAREiContainer>)parent
                       withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  [self createViewerWithRAREUTJSONObject:patient withRAREiContainer:parent withBoolean:YES withRAREiFunctionCallback:cb];
}

- (void)createViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                      withRAREiContainer:(id<RAREiContainer>)parent
                     withRAREUIDimension:(RAREUIDimension *)targetSize
               withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)createWaveformsViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                               withRAREiContainer:(id<RAREiContainer>)parent
                        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  [self createViewerWithRAREUTJSONObject:patient withRAREiContainer:parent withBoolean:NO withRAREiFunctionCallback:cb];
}

- (void)dispose {
  eventHandler_ = nil;
}

- (NSString *)getPatientPropertyName {
  return patientPropertyName_;
}

- (void)pauseMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)restartMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)startMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)stopMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (BOOL)supportsNumerics {
  return NO;
}

- (BOOL)supportsWaveforms {
  return NO;
}

- (void)createViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                      withRAREiContainer:(id<RAREiContainer>)parent
                             withBoolean:(BOOL)numerics
               withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
}

- (id<RAREiContainer>)finishCreatingViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                                            withRAREiContainer:(id<RAREiContainer>)parent
                                            withRARESPOTViewer:(RARESPOTViewer *)wcfg
                                            withRARESPOTViewer:(RARESPOTViewer *)ncfg
                                                   withBoolean:(BOOL)stacked {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  id<RAREiContainer> nv = (id<RAREiContainer>) check_protocol_cast((ncfg == nil ? nil : [((id<RAREiContainer>) nil_chk(parent)) createWidgetWithRARESPOTWidget:ncfg]), @protocol(RAREiContainer));
  id<RAREiContainer> wv = (id<RAREiContainer>) check_protocol_cast((wcfg == nil ? nil : [((id<RAREiContainer>) nil_chk(parent)) createWidgetWithRARESPOTWidget:wcfg]), @protocol(RAREiContainer));
  id<RAREiContainer> rv = nil;
  if (wv == nil || nv == nil) {
    rv = wv == nil ? nv : wv;
    [self viewerCratedWithRAREiContainer:rv withBoolean:rv == nv];
  }
  else {
    if (stacked) {
      RAREStackPaneViewer *sp = (RAREStackPaneViewer *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createViewerWithRAREiWidget:parent withRARESPOTWidget:[[RARESPOTStackPane alloc] init]], [RAREStackPaneViewer class]);
      [((RAREStackPaneViewer *) nil_chk(sp)) addViewerWithNSString:@"waveforms" withRAREiViewer:wv];
      [sp addViewerWithNSString:@"numerics" withRAREiViewer:nv];
      [sp setSelectedIndexWithInt:0];
      RAREPushButtonWidget *pb = (RAREPushButtonWidget *) check_class_cast([wv getWidgetWithNSString:@"toggleButton"], [RAREPushButtonWidget class]);
      if (pb != nil) {
        [pb addActionListenerWithRAREiActionListener:[[CCPBVaRemoteMonitor_$1 alloc] initWithRAREStackPaneViewer:sp]];
      }
      pb = (RAREPushButtonWidget *) check_class_cast([nv getWidgetWithNSString:@"toggleButton"], [RAREPushButtonWidget class]);
      if (pb != nil) {
        [pb addActionListenerWithRAREiActionListener:[[CCPBVaRemoteMonitor_$2 alloc] initWithRAREStackPaneViewer:sp]];
      }
      rv = sp;
    }
    else {
      RARESPOTSplitPane *pane = (RARESPOTSplitPane *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"SplitPane" withNSString:@"bv.monitor.splitpane"], [RARESPOTSplitPane class]);
      RARESplitPaneViewer *sp = (RARESplitPaneViewer *) check_class_cast([w createViewerWithRAREiWidget:parent withRARESPOTWidget:pane], [RARESplitPaneViewer class]);
      (void) [((RARESplitPaneViewer *) nil_chk(sp)) setViewerWithInt:0 withRAREiViewer:wv];
      (void) [sp setViewerWithInt:1 withRAREiViewer:nv];
      rv = sp;
    }
    [self viewerCratedWithRAREiContainer:nv withBoolean:YES];
    [self viewerCratedWithRAREiContainer:wv withBoolean:NO];
  }
  NSString *cls = eventHandlerClassName_;
  if (eventHandler_ == nil && cls != nil) {
    eventHandler_ = (id<CCPBVaRemoteMonitor_iRemoteMonitorEventHandler>) check_protocol_cast([RAREFunctions getEventHandlerWithNSString:cls], @protocol(CCPBVaRemoteMonitor_iRemoteMonitorEventHandler));
    [((id<CCPBVaRemoteMonitor_iRemoteMonitorEventHandler>) nil_chk(eventHandler_)) setMonitorWithCCPBVaRemoteMonitor:self];
  }
  if (eventHandler_ != nil) {
    [((id<RAREiContainer>) nil_chk(rv)) setEventHandlerWithNSString:[RAREiConstants EVENT_DISPOSE] withId:[NSString stringWithFormat:@"class:%@#onDispose", cls] withBoolean:YES];
    [rv setEventHandlerWithNSString:[RAREiConstants EVENT_SHOWN] withId:[NSString stringWithFormat:@"class:%@#onShown", cls] withBoolean:YES];
    [rv setEventHandlerWithNSString:[RAREiConstants EVENT_HIDDEN] withId:[NSString stringWithFormat:@"class:%@#onHidden", cls] withBoolean:YES];
  }
  [((id<RAREiContainer>) nil_chk(rv)) setAttributeWithNSString:patientPropertyName_ withId:patient];
  [self mainViewerCratedWithRAREiContainer:rv withRAREUTJSONObject:patient];
  return rv;
}

- (void)mainViewerCratedWithRAREiContainer:(id<RAREiContainer>)v
                      withRAREUTJSONObject:(RAREUTJSONObject *)patient {
}

- (void)viewerCratedWithRAREiContainer:(id<RAREiContainer>)v
                           withBoolean:(BOOL)numerics {
}

- (void)copyAllFieldsTo:(CCPBVaRemoteMonitor *)other {
  [super copyAllFieldsTo:other];
  other->eventHandler_ = eventHandler_;
  other->eventHandlerClassName_ = eventHandlerClassName_;
  other->patientPropertyName_ = patientPropertyName_;
  other->toggleButtonName_ = toggleButtonName_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x4, NULL },
    { "canMonitorPatientWithRAREUTJSONObject:", NULL, "Z", 0x401, NULL },
    { "createViewerWithRAREUTJSONObject:withRAREiContainer:withRAREUIDimension:withRAREiFunctionCallback:", NULL, "V", 0x401, NULL },
    { "getPatientPropertyName", NULL, "LNSString", 0x1, NULL },
    { "pauseMonitoringWithRAREUTJSONObject:", NULL, "V", 0x401, NULL },
    { "restartMonitoringWithRAREUTJSONObject:", NULL, "V", 0x401, NULL },
    { "startMonitoringWithRAREUTJSONObject:", NULL, "V", 0x401, NULL },
    { "stopMonitoringWithRAREUTJSONObject:", NULL, "V", 0x401, NULL },
    { "supportsNumerics", NULL, "Z", 0x1, NULL },
    { "supportsWaveforms", NULL, "Z", 0x1, NULL },
    { "createViewerWithRAREUTJSONObject:withRAREiContainer:withBoolean:withRAREiFunctionCallback:", NULL, "V", 0x4, NULL },
    { "finishCreatingViewerWithRAREUTJSONObject:withRAREiContainer:withRARESPOTViewer:withRARESPOTViewer:withBoolean:", NULL, "LRAREiContainer", 0x4, NULL },
    { "mainViewerCratedWithRAREiContainer:withRAREUTJSONObject:", NULL, "V", 0x4, NULL },
    { "viewerCratedWithRAREiContainer:withBoolean:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "toggleButtonName_", NULL, 0x4, "LNSString" },
    { "eventHandlerClassName_", NULL, 0x4, "LNSString" },
    { "patientPropertyName_", NULL, 0x4, "LNSString" },
    { "eventHandler_", NULL, 0x0, "LCCPBVaRemoteMonitor_iRemoteMonitorEventHandler" },
  };
  static J2ObjcClassInfo _CCPBVaRemoteMonitor = { "aRemoteMonitor", "com.sparseware.bellavista.external", NULL, 0x401, 14, methods, 4, fields, 0, NULL};
  return &_CCPBVaRemoteMonitor;
}

@end

@interface CCPBVaRemoteMonitor_iRemoteMonitorEventHandler : NSObject
@end

@implementation CCPBVaRemoteMonitor_iRemoteMonitorEventHandler

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "setMonitorWithCCPBVaRemoteMonitor:", NULL, "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _CCPBVaRemoteMonitor_iRemoteMonitorEventHandler = { "iRemoteMonitorEventHandler", "com.sparseware.bellavista.external", "aRemoteMonitor", 0x209, 1, methods, 0, NULL, 0, NULL};
  return &_CCPBVaRemoteMonitor_iRemoteMonitorEventHandler;
}

@end
@implementation CCPBVaRemoteMonitor_$1

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
  [((RAREStackPaneViewer *) nil_chk(val$sp_)) switchToWithNSString:@"numerics"];
}

- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0 {
  val$sp_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
  };
  static J2ObjcClassInfo _CCPBVaRemoteMonitor_$1 = { "$1", "com.sparseware.bellavista.external", "aRemoteMonitor", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVaRemoteMonitor_$1;
}

@end
@implementation CCPBVaRemoteMonitor_$2

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
  [((RAREStackPaneViewer *) nil_chk(val$sp_)) switchToWithNSString:@"waveforms"];
}

- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0 {
  val$sp_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
  };
  static J2ObjcClassInfo _CCPBVaRemoteMonitor_$2 = { "$2", "com.sparseware.bellavista.external", "aRemoteMonitor", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVaRemoteMonitor_$2;
}

@end
