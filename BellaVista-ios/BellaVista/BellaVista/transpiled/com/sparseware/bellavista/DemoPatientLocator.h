//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/DemoPatientLocator.java
//
//  Created by decoteaud on 3/12/15.
//

#ifndef _ComSparsewareBellavistaDemoPatientLocator_H_
#define _ComSparsewareBellavistaDemoPatientLocator_H_

@protocol JavaUtilList;

#import "JreEmulation.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/sparseware/bellavista/external/aPatientLocator.h"
#include "java/lang/Runnable.h"

@interface ComSparsewareBellavistaDemoPatientLocator : ComSparsewareBellavistaExternalaPatientLocator {
}

- (id)init;
- (void)dispose;
- (void)getNearbyPatientsWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (void)delayCallbackWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb
                                        withId:(id)result;
- (void)getNearbyLocationsWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (BOOL)isNearbyPatientsSupported;
- (BOOL)isNearbyLocationsSupported;
- (id<JavaUtilList>)getUpdatedNearbyPatients;
@end

@interface ComSparsewareBellavistaDemoPatientLocator_$1 : NSObject < RAREiFunctionCallback > {
 @public
  ComSparsewareBellavistaDemoPatientLocator *this$0_;
  id<RAREiFunctionCallback> val$cb_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithComSparsewareBellavistaDemoPatientLocator:(ComSparsewareBellavistaDemoPatientLocator *)outer$
                              withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaDemoPatientLocator_$1, this$0_, ComSparsewareBellavistaDemoPatientLocator *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaDemoPatientLocator_$1, val$cb_, id<RAREiFunctionCallback>)

@interface ComSparsewareBellavistaDemoPatientLocator_$2 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiFunctionCallback> val$cb_;
  id val$result_;
}

- (void)run;
- (id)initWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0
                             withId:(id)capture$1;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaDemoPatientLocator_$2, val$cb_, id<RAREiFunctionCallback>)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaDemoPatientLocator_$2, val$result_, id)

#endif // _ComSparsewareBellavistaDemoPatientLocator_H_
