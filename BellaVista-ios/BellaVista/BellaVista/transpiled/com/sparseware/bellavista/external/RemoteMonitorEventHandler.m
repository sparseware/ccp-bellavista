//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/RemoteMonitorEventHandler.java
//
//  Created by decoteaud on 3/14/16.
//

#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/ui/ActionBar.h"
#include "com/appnativa/rare/ui/UIScreen.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/external/RemoteMonitorEventHandler.h"
#include "com/sparseware/bellavista/external/aRemoteMonitor.h"
#include "java/util/EventObject.h"

@implementation CCPBVRemoteMonitorEventHandler

- (id)init {
  return [super init];
}

- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (remoteMonitor_ != nil) {
    RAREUTJSONObject *patient = (RAREUTJSONObject *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getAttributeWithNSString:[remoteMonitor_ getPatientPropertyName]], [RAREUTJSONObject class]);
    [remoteMonitor_ stopMonitoringWithRAREUTJSONObject:patient];
    if ([RAREUIScreen isMediumScreen]) {
      [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) unlockOrientation];
      [((RAREActionBar *) nil_chk([((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getActionBar])) setVisibleWithBoolean:YES];
    }
  }
}

- (void)onShownWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (remoteMonitor_ != nil) {
    RAREUTJSONObject *patient = (RAREUTJSONObject *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getAttributeWithNSString:[remoteMonitor_ getPatientPropertyName]], [RAREUTJSONObject class]);
    [remoteMonitor_ restartMonitoringWithRAREUTJSONObject:patient];
  }
}

- (void)onHiddenWithNSString:(NSString *)eventName
             withRAREiWidget:(id<RAREiWidget>)widget
     withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (remoteMonitor_ != nil) {
    RAREUTJSONObject *patient = (RAREUTJSONObject *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getAttributeWithNSString:[remoteMonitor_ getPatientPropertyName]], [RAREUTJSONObject class]);
    [remoteMonitor_ pauseMonitoringWithRAREUTJSONObject:patient];
  }
}

- (void)setMonitorWithCCPBVaRemoteMonitor:(CCPBVaRemoteMonitor *)monitor {
  self->remoteMonitor_ = monitor;
}

- (void)copyAllFieldsTo:(CCPBVRemoteMonitorEventHandler *)other {
  [super copyAllFieldsTo:other];
  other->remoteMonitor_ = remoteMonitor_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "remoteMonitor_", NULL, 0x4, "LCCPBVaRemoteMonitor" },
  };
  static J2ObjcClassInfo _CCPBVRemoteMonitorEventHandler = { "RemoteMonitorEventHandler", "com.sparseware.bellavista.external", NULL, 0x1, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVRemoteMonitorEventHandler;
}

@end
