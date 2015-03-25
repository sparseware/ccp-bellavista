//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aBeaconLocatorSupport.java
//
//  Created by decoteaud on 3/24/15.
//

#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/ui/event/iChangeListener.h"
#include "com/sparseware/bellavista/external/aBeaconLocatorSupport.h"
#include "com/sparseware/bellavista/external/aPatientLocator.h"
#include "java/util/List.h"

@implementation CCPBVaBeaconLocatorSupport

- (id)init {
  return [super init];
}

- (void)dispose {
  changeListener_ = nil;
}

- (id<JavaUtilList>)getNearbyLocationsWithCCPBVaPatientLocator_LocatorChangeEvent:(CCPBVaPatientLocator_LocatorChangeEvent *)event {
  return nil;
}

- (id<JavaUtilList>)getNearbyPatientsWithCCPBVaPatientLocator_LocatorChangeEvent:(CCPBVaPatientLocator_LocatorChangeEvent *)event {
  return nil;
}

- (void)ignoreEventWithCCPBVaPatientLocator_LocatorChangeEvent:(CCPBVaPatientLocator_LocatorChangeEvent *)event {
}

- (BOOL)isAvailable {
  return NO;
}

- (BOOL)wasAccessDenied {
  return NO;
}

- (void)setChangeListenerWithRAREiChangeListener:(id<RAREiChangeListener>)changeListener {
  self->changeListener_ = changeListener;
}

- (void)setLocationBeaconsWithJavaUtilList:(id<JavaUtilList>)beacons {
}

- (void)setPatientBeaconsWithJavaUtilList:(id<JavaUtilList>)beacons {
}

- (void)startListeningForLocations {
}

- (void)startListeningForPatients {
}

- (void)stopListeningForLocations {
}

- (void)stopListeningForPatients {
}

- (void)notifyWithJavaUtilList:(id<JavaUtilList>)beacons
                   withBoolean:(BOOL)patients {
  if (changeListener_ != nil) {
    CCPBVaPatientLocator_LocatorChangeTypeEnum *type = patients ? [CCPBVaPatientLocator_LocatorChangeTypeEnum PATIENTS] : [CCPBVaPatientLocator_LocatorChangeTypeEnum LOCATIONS];
    CCPBVaPatientLocator_LocatorChangeEvent *e = [[CCPBVaPatientLocator_LocatorChangeEvent alloc] initWithId:self withCCPBVaPatientLocator_LocatorChangeTypeEnum:type withId:beacons];
    if ([RAREPlatform isUIThread]) {
      [changeListener_ stateChangedWithJavaUtilEventObject:e];
    }
    else {
      [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVaBeaconLocatorSupport_$1 alloc] initWithCCPBVaBeaconLocatorSupport:self withCCPBVaPatientLocator_LocatorChangeEvent:e]];
    }
  }
}

- (void)copyAllFieldsTo:(CCPBVaBeaconLocatorSupport *)other {
  [super copyAllFieldsTo:other];
  other->changeListener_ = changeListener_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getNearbyLocationsWithCCPBVaPatientLocator_LocatorChangeEvent:", NULL, "LJavaUtilList", 0x1, NULL },
    { "getNearbyPatientsWithCCPBVaPatientLocator_LocatorChangeEvent:", NULL, "LJavaUtilList", 0x1, NULL },
    { "isAvailable", NULL, "Z", 0x1, NULL },
    { "wasAccessDenied", NULL, "Z", 0x1, NULL },
    { "notifyWithJavaUtilList:withBoolean:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "changeListener_", NULL, 0x4, "LRAREiChangeListener" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocatorSupport = { "aBeaconLocatorSupport", "com.sparseware.bellavista.external", NULL, 0x1, 5, methods, 1, fields, 0, NULL};
  return &_CCPBVaBeaconLocatorSupport;
}

@end
@implementation CCPBVaBeaconLocatorSupport_$1

- (void)run {
  if (this$0_->changeListener_ != nil) {
    [this$0_->changeListener_ stateChangedWithJavaUtilEventObject:val$e_];
  }
}

- (id)initWithCCPBVaBeaconLocatorSupport:(CCPBVaBeaconLocatorSupport *)outer$
withCCPBVaPatientLocator_LocatorChangeEvent:(CCPBVaPatientLocator_LocatorChangeEvent *)capture$0 {
  this$0_ = outer$;
  val$e_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVaBeaconLocatorSupport" },
    { "val$e_", NULL, 0x1012, "LCCPBVaPatientLocator_LocatorChangeEvent" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocatorSupport_$1 = { "$1", "com.sparseware.bellavista.external", "aBeaconLocatorSupport", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVaBeaconLocatorSupport_$1;
}

@end