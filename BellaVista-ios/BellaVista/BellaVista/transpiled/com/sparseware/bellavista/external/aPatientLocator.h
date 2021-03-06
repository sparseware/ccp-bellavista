//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aPatientLocator.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVaPatientLocator_H_
#define _CCPBVaPatientLocator_H_

@class JavaUtilEventObject;
@protocol RAREiChangeListener;
@protocol RAREiFunctionCallback;

#import "JreEmulation.h"
#include "com/appnativa/rare/ui/event/DataEvent.h"
#include "java/lang/Enum.h"

@interface CCPBVaPatientLocator : NSObject {
 @public
  id<RAREiChangeListener> changeListener_;
}

- (id)init;
- (void)dispose;
- (void)getNearbyPatientsWithJavaUtilEventObject:(JavaUtilEventObject *)event
                       withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (void)getNearbyLocationsWithJavaUtilEventObject:(JavaUtilEventObject *)event
                        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (void)setChangeListenerWithRAREiChangeListener:(id<RAREiChangeListener>)changeListener;
- (BOOL)isNearbyPatientsSupported;
- (BOOL)isNearbyLocationsSupported;
- (void)stopListeningForNearbyPatients;
- (void)stopListeningForNearbyLocations;
- (void)startListeningForNearbyPatients;
- (void)startListeningForNearbyLocations;
- (void)ignoreEventWithJavaUtilEventObject:(JavaUtilEventObject *)e;
- (void)copyAllFieldsTo:(CCPBVaPatientLocator *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaPatientLocator, changeListener_, id<RAREiChangeListener>)

typedef CCPBVaPatientLocator ComSparsewareBellavistaExternalAPatientLocator;

typedef enum {
  CCPBVaPatientLocator_LocatorChangeType_PATIENTS = 0,
  CCPBVaPatientLocator_LocatorChangeType_LOCATIONS = 1,
  CCPBVaPatientLocator_LocatorChangeType_ACCESS_DENIED = 2,
} CCPBVaPatientLocator_LocatorChangeType;

@interface CCPBVaPatientLocator_LocatorChangeTypeEnum : JavaLangEnum < NSCopying > {
}
+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)PATIENTS;
+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)LOCATIONS;
+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)ACCESS_DENIED;
+ (IOSObjectArray *)values;
+ (CCPBVaPatientLocator_LocatorChangeTypeEnum *)valueOfWithNSString:(NSString *)name;
- (id)copyWithZone:(NSZone *)zone;
- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal;
@end

@interface CCPBVaPatientLocator_LocatorChangeEvent : RAREDataEvent {
 @public
  CCPBVaPatientLocator_LocatorChangeTypeEnum *changeType_;
}

- (id)initWithId:(id)source
withCCPBVaPatientLocator_LocatorChangeTypeEnum:(CCPBVaPatientLocator_LocatorChangeTypeEnum *)type;
- (id)initWithId:(id)source
withCCPBVaPatientLocator_LocatorChangeTypeEnum:(CCPBVaPatientLocator_LocatorChangeTypeEnum *)type
          withId:(id)data;
- (CCPBVaPatientLocator_LocatorChangeTypeEnum *)getChangeType;
- (void)copyAllFieldsTo:(CCPBVaPatientLocator_LocatorChangeEvent *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaPatientLocator_LocatorChangeEvent, changeType_, CCPBVaPatientLocator_LocatorChangeTypeEnum *)

#endif // _CCPBVaPatientLocator_H_
