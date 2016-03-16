//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/ApplicationListener.java
//
//  Created by decoteaud on 3/14/16.
//

#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/converters/DateContext.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/ui/UIScreen.h"
#include "com/sparseware/bellavista/ApplicationListener.h"
#include "com/sparseware/bellavista/SmartDateContext.h"
#include "com/sparseware/bellavista/Utils.h"
#include "java/text/DateFormat.h"

@implementation CCPBVApplicationListener

- (id)init {
  return [super init];
}

- (BOOL)allowClosingWithRAREiPlatformAppContext:(id<RAREiPlatformAppContext>)app {
  return YES;
}

- (void)applicationClosingWithRAREiPlatformAppContext:(id<RAREiPlatformAppContext>)app {
  if (![RAREPlatform isDebugEnabled]) {
    [RAREPlatform clearSessionCookies];
  }
}

- (void)applicationInitializedWithRAREiPlatformAppContext:(id<RAREiPlatformAppContext>)app {
  [RAREPlatform setTrackOpenConnectionsWithBoolean:YES];
  CCPBVSmartDateContext *dc = [[CCPBVSmartDateContext alloc] initWithBoolean:YES withJavaTextDateFormat:[((RAREDateContext *) nil_chk([((id<RAREiPlatformAppContext>) nil_chk(app)) getDefaultDateTimeContext])) getDisplayFormat]];
  [app setDefaultDateTimeContextWithRAREDateContext:dc];
  dc = [[CCPBVSmartDateContext alloc] initWithBoolean:NO withJavaTextDateFormat:[((RAREDateContext *) nil_chk([app getDefaultDateContext])) getDisplayFormat]];
  [app setDefaultDateContextWithRAREDateContext:dc];
  [CCPBVUtils applicationInitialized];
  [app addConfigurationListenerWithRAREiConfigurationListener:self];
  if (![RAREPlatform isDebugEnabled]) {
    [RAREPlatform clearSessionCookies];
  }
}

- (void)applicationPausedWithRAREiPlatformAppContext:(id<RAREiPlatformAppContext>)app {
  if (![CCPBVUtils isCardStack] && ![CCPBVUtils isShuttingDown]) {
    [CCPBVUtils applicationPaused];
  }
}

- (void)applicationResumedWithRAREiPlatformAppContext:(id<RAREiPlatformAppContext>)app {
  if (![CCPBVUtils isCardStack] && ![CCPBVUtils isShuttingDown]) {
    [CCPBVUtils applicationResumed];
  }
}

- (void)onConfigurationChangedWithId:(id)config {
}

- (void)onConfigurationWillChangeWithId:(id)config {
  if (![CCPBVUtils isCardStack] && ![CCPBVUtils isShuttingDown] && ![RAREUIScreen isLargeScreen]) {
    [CCPBVUtils toggleFullScreenWithBoolean:[RAREUIScreen isWiderForConfigurationWithId:config]];
  }
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "allowClosingWithRAREiPlatformAppContext:", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcClassInfo _CCPBVApplicationListener = { "ApplicationListener", "com.sparseware.bellavista", NULL, 0x1, 1, methods, 0, NULL, 0, NULL};
  return &_CCPBVApplicationListener;
}

@end
