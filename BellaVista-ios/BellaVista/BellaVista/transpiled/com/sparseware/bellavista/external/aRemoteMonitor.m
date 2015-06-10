//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aRemoteMonitor.java
//
//  Created by decoteaud on 5/12/15.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iConstants.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/rare/spot/RadioButton.h"
#include "com/appnativa/rare/spot/SplitPane.h"
#include "com/appnativa/rare/spot/StackPane.h"
#include "com/appnativa/rare/spot/Viewer.h"
#include "com/appnativa/rare/ui/ActionBar.h"
#include "com/appnativa/rare/ui/UIDimension.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/ui/iEventHandler.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/viewer/GridPaneViewer.h"
#include "com/appnativa/rare/viewer/SplitPaneViewer.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/ToolBarViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/LabelWidget.h"
#include "com/appnativa/rare/widget/RadioButtonWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/SPOTPrintableString.h"
#include "com/appnativa/spot/iSPOTElement.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/external/RemoteMonitorEventHandler.h"
#include "com/sparseware/bellavista/external/aRemoteMonitor.h"
#include "java/lang/Boolean.h"
#include "java/util/List.h"

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
      rv = [self createGridPaneViewerWithRAREStackPaneViewer:sp];
      [self stackPaneViewerCratedWithRAREStackPaneViewer:sp withRAREUTJSONObject:patient];
      [((RAREActionBar *) nil_chk([((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getActionBar])) setVisibleWithBoolean:NO];
      [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) lockOrientationWithJavaLangBoolean:[JavaLangBoolean valueOfWithBoolean:YES]];
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

- (id<JavaUtilList>)getToolbarButtonsWithRAREiViewer:(id<RAREiViewer>)viewer {
  return nil;
}

- (void)mainViewerCratedWithRAREiContainer:(id<RAREiContainer>)v
                      withRAREUTJSONObject:(RAREUTJSONObject *)patient {
}

- (void)stackPaneViewerCratedWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp
                                withRAREUTJSONObject:(RAREUTJSONObject *)patient {
}

- (void)viewerCratedWithRAREiContainer:(id<RAREiContainer>)v
                           withBoolean:(BOOL)numerics {
}

- (id<RAREiContainer>)createGridPaneViewerWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RAREGridPaneViewer *gp = [CCPBVUtils createGenericContainerViewerWithRAREiContainer:w];
  RAREToolBarViewer *tb = (RAREToolBarViewer *) check_class_cast([((RAREGridPaneViewer *) nil_chk(gp)) getWidgetWithNSString:@"genericToolbar"], [RAREToolBarViewer class]);
  id<RAREiWidget> b = [((RAREToolBarViewer *) nil_chk(tb)) getWidgetWithNSString:@"bv.action.fullscreen"];
  if (b != nil) {
    [b setVisibleWithBoolean:NO];
  }
  b = [tb getWidgetWithNSString:@"backButton"];
  if (b != nil) {
    [b setEventHandlerWithNSString:[RAREiConstants EVENT_ACTION] withId:@"class:MainEventHandler#popWorkspaceViewer" withBoolean:NO];
  }
  RARELabelWidget *l = (RARELabelWidget *) check_class_cast([tb getWidgetWithNSString:@"genericLabel"], [RARELabelWidget class]);
  RARESPOTRadioButton *cfg = (RARESPOTRadioButton *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"RadioButton" withNSString:@"bv.radiobutton.toolbar"], [RARESPOTRadioButton class]);
  [((SPOTPrintableString *) nil_chk(((RARESPOTRadioButton *) nil_chk(cfg))->groupName_)) setValueWithNSString:@"sotera"];
  RARERadioButtonWidget *rb = (RARERadioButtonWidget *) check_class_cast([tb createWidgetWithRARESPOTWidget:cfg], [RARERadioButtonWidget class]);
  [((RARERadioButtonWidget *) nil_chk(rb)) setIconWithRAREiPlatformIcon:[RAREPlatform getResourceAsIconWithNSString:@"bv.icon.ecg"]];
  [rb addActionListenerWithRAREiActionListener:[[CCPBVaRemoteMonitor_$1 alloc] initWithRAREStackPaneViewer:sp withRARELabelWidget:l]];
  [tb addWidgetWithRAREiWidget:rb];
  [rb setSelectedWithBoolean:YES];
  rb = (RARERadioButtonWidget *) check_class_cast([tb createWidgetWithRARESPOTWidget:cfg], [RARERadioButtonWidget class]);
  [((RARERadioButtonWidget *) nil_chk(rb)) setIconWithRAREiPlatformIcon:[RAREPlatform getResourceAsIconWithNSString:@"bv.icon.vitals_numerics"]];
  [rb addActionListenerWithRAREiActionListener:[[CCPBVaRemoteMonitor_$2 alloc] initWithRAREStackPaneViewer:sp withRARELabelWidget:l]];
  [tb addWidgetWithRAREiWidget:rb];
  if (l != nil && [((RAREStackPaneViewer *) nil_chk(sp)) getActiveViewer] != nil) {
    [l setTextWithJavaLangCharSequence:[((id<RAREiViewer>) nil_chk([sp getActiveViewer])) getTitle]];
  }
  (void) [gp setViewerWithInt:1 withRAREiViewer:sp];
  return gp;
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
    { "getToolbarButtonsWithRAREiViewer:", NULL, "LJavaUtilList", 0x1, NULL },
    { "mainViewerCratedWithRAREiContainer:withRAREUTJSONObject:", NULL, "V", 0x4, NULL },
    { "stackPaneViewerCratedWithRAREStackPaneViewer:withRAREUTJSONObject:", NULL, "V", 0x4, NULL },
    { "viewerCratedWithRAREiContainer:withBoolean:", NULL, "V", 0x4, NULL },
    { "createGridPaneViewerWithRAREStackPaneViewer:", NULL, "LRAREiContainer", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "toggleButtonName_", NULL, 0x4, "LNSString" },
    { "eventHandlerClassName_", NULL, 0x4, "LNSString" },
    { "patientPropertyName_", NULL, 0x4, "LNSString" },
    { "eventHandler_", NULL, 0x0, "LCCPBVaRemoteMonitor_iRemoteMonitorEventHandler" },
  };
  static J2ObjcClassInfo _CCPBVaRemoteMonitor = { "aRemoteMonitor", "com.sparseware.bellavista.external", NULL, 0x401, 17, methods, 4, fields, 0, NULL};
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
  [((RAREStackPaneViewer *) nil_chk(val$sp_)) switchToWithNSString:@"waveforms"];
  if (val$l_ != nil) {
    [val$l_ setTextWithJavaLangCharSequence:[((id<RAREiViewer>) nil_chk([val$sp_ getActiveViewer])) getTitle]];
  }
}

- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0
              withRARELabelWidget:(RARELabelWidget *)capture$1 {
  val$sp_ = capture$0;
  val$l_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
    { "val$l_", NULL, 0x1012, "LRARELabelWidget" },
  };
  static J2ObjcClassInfo _CCPBVaRemoteMonitor_$1 = { "$1", "com.sparseware.bellavista.external", "aRemoteMonitor", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVaRemoteMonitor_$1;
}

@end
@implementation CCPBVaRemoteMonitor_$2

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
  [((RAREStackPaneViewer *) nil_chk(val$sp_)) switchToWithNSString:@"numerics"];
  if (val$l_ != nil) {
    [val$l_ setTextWithJavaLangCharSequence:[((id<RAREiViewer>) nil_chk([val$sp_ getActiveViewer])) getTitle]];
  }
}

- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0
              withRARELabelWidget:(RARELabelWidget *)capture$1 {
  val$sp_ = capture$0;
  val$l_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
    { "val$l_", NULL, 0x1012, "LRARELabelWidget" },
  };
  static J2ObjcClassInfo _CCPBVaRemoteMonitor_$2 = { "$2", "com.sparseware.bellavista.external", "aRemoteMonitor", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVaRemoteMonitor_$2;
}

@end
