//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aPatientLocator.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSClass.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/event/iChangeListener.h"
#include "com/sparseware/bellavista/external/aPatientLocator.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/util/EventObject.h"

@implementation CCPBVaPatientLocator

- (id)init {
  return [super init];
}

- (void)dispose {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)getNearbyPatientsWithJavaUtilEventObject:(JavaUtilEventObject *)event
                       withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)getNearbyLocationsWithJavaUtilEventObject:(JavaUtilEventObject *)event
                        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)setChangeListenerWithRAREiChangeListener:(id<RAREiChangeListener>)changeListener {
  self->changeListener_ = changeListener;
}

- (BOOL)isNearbyPatientsSupported {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (BOOL)isNearbyLocationsSupported {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)stopListeningForNearbyPatients {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)stopListeningForNearbyLocations {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)startListeningForNearbyPatients {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)startListeningForNearbyLocations {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)ignoreEventWithJavaUtilEventObject:(JavaUtilEventObject *)e {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)copyAllFieldsTo:(CCPBVaPatientLocator *)other {
  [super copyAllFieldsTo:other];
  other->changeListener_ = changeListener_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "dispose", NULL, "V", 0x401, NULL },
    { "getNearbyPatientsWithJavaUtilEventObject:withRAREiFunctionCallback:", NULL, "V", 0x401, NULL },
    { "getNearbyLocationsWithJavaUtilEventObject:withRAREiFunctionCallback:", NULL, "V", 0x401, NULL },
    { "isNearbyPatientsSupported", NULL, "Z", 0x401, NULL },
    { "isNearbyLocationsSupported", NULL, "Z", 0x401, NULL },
    { "stopListeningForNearbyPatients", NULL, "V", 0x401, NULL },
    { "stopListeningForNearbyLocations", NULL, "V", 0x401, NULL },
    { "startListeningForNearbyPatients", NULL, "V", 0x401, NULL },
    { "startListeningForNearbyLocations", NULL, "V", 0x401, NULL },
    { "ignoreEventWithJavaUtilEventObject:", NULL, "V", 0x401, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "changeListener_", NULL, 0x4, "LRAREiChangeListener" },
  };
  static J2ObjcClassInfo _CCPBVaPatientLocator = { "aPatientLocator", "com.sparseware.bellavista.external", NULL, 0x401, 10, methods, 1, fields, 0, NULL};
  return &_CCPBVaPatientLocator;
}

@end

static CCPBVaPatientLocator_LocatorChangeTypeEnum *CCPBVaPatientLocator_LocatorChangeTypeEnum_PATIENTS;
static CCPBVaPatientLocator_LocatorChangeTypeEnum *CCPBVaPatientLocator_LocatorChangeTypeEnum_LOCATIONS;
static CCPBVaPatientLocator_LocatorChangeTypeEnum *CCPBVaPatientLocator_LocatorChangeTypeEnum_ACCESS_DENIED;
IOSObjectArray *CCPBVaPatientLocator_LocatorChangeTypeEnum_values;

@implementation CCPBVaPatientLocator_LocatorChangeTypeEnum

+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)PATIENTS {
  return CCPBVaPatientLocator_LocatorChangeTypeEnum_PATIENTS;
}
+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)LOCATIONS {
  return CCPBVaPatientLocator_LocatorChangeTypeEnum_LOCATIONS;
}
+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)ACCESS_DENIED {
  return CCPBVaPatientLocator_LocatorChangeTypeEnum_ACCESS_DENIED;
}

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [CCPBVaPatientLocator_LocatorChangeTypeEnum class]) {
    CCPBVaPatientLocator_LocatorChangeTypeEnum_PATIENTS = [[CCPBVaPatientLocator_LocatorChangeTypeEnum alloc] initWithNSString:@"PATIENTS" withInt:0];
    CCPBVaPatientLocator_LocatorChangeTypeEnum_LOCATIONS = [[CCPBVaPatientLocator_LocatorChangeTypeEnum alloc] initWithNSString:@"LOCATIONS" withInt:1];
    CCPBVaPatientLocator_LocatorChangeTypeEnum_ACCESS_DENIED = [[CCPBVaPatientLocator_LocatorChangeTypeEnum alloc] initWithNSString:@"ACCESS_DENIED" withInt:2];
    CCPBVaPatientLocator_LocatorChangeTypeEnum_values = [[IOSObjectArray alloc] initWithObjects:(id[]){ CCPBVaPatientLocator_LocatorChangeTypeEnum_PATIENTS, CCPBVaPatientLocator_LocatorChangeTypeEnum_LOCATIONS, CCPBVaPatientLocator_LocatorChangeTypeEnum_ACCESS_DENIED, nil } count:3 type:[IOSClass classWithClass:[CCPBVaPatientLocator_LocatorChangeTypeEnum class]]];
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithArray:CCPBVaPatientLocator_LocatorChangeTypeEnum_values];
}

+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < [CCPBVaPatientLocator_LocatorChangeTypeEnum_values count]; i++) {
    CCPBVaPatientLocator_LocatorChangeTypeEnum *e = CCPBVaPatientLocator_LocatorChangeTypeEnum_values->buffer_[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static const char *superclass_type_args[] = {"LCCPBVaPatientLocator_LocatorChangeTypeEnum"};
  static J2ObjcClassInfo _CCPBVaPatientLocator_LocatorChangeTypeEnum = { "LocatorChangeType", "com.sparseware.bellavista.external", "aPatientLocator", 0x4019, 0, NULL, 0, NULL, 1, superclass_type_args};
  return &_CCPBVaPatientLocator_LocatorChangeTypeEnum;
}

@end
@implementation CCPBVaPatientLocator_LocatorChangeEvent

- (id)initWithId:(id)source
withCCPBVaPatientLocator_LocatorChangeTypeEnum:(CCPBVaPatientLocator_LocatorChangeTypeEnum *)type {
  if (self = [super initWithId:source withId:nil]) {
    changeType_ = type;
  }
  return self;
}

- (id)initWithId:(id)source
withCCPBVaPatientLocator_LocatorChangeTypeEnum:(CCPBVaPatientLocator_LocatorChangeTypeEnum *)type
          withId:(id)data {
  if (self = [super initWithId:source withId:data]) {
    changeType_ = type;
  }
  return self;
}

- (CCPBVaPatientLocator_LocatorChangeTypeEnum *)getChangeType {
  return changeType_;
}

- (void)copyAllFieldsTo:(CCPBVaPatientLocator_LocatorChangeEvent *)other {
  [super copyAllFieldsTo:other];
  other->changeType_ = changeType_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getChangeType", NULL, "LCCPBVaPatientLocator_LocatorChangeTypeEnum", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "changeType_", NULL, 0x4, "LCCPBVaPatientLocator_LocatorChangeTypeEnum" },
  };
  static J2ObjcClassInfo _CCPBVaPatientLocator_LocatorChangeEvent = { "LocatorChangeEvent", "com.sparseware.bellavista.external", "aPatientLocator", 0x9, 1, methods, 1, fields, 0, NULL};
  return &_CCPBVaPatientLocator_LocatorChangeEvent;
}

@end
