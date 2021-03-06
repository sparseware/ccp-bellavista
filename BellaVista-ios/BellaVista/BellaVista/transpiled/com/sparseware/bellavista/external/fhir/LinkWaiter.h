//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/LinkWaiter.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVFHIRLinkWaiter_H_
#define _CCPBVFHIRLinkWaiter_H_

@class JavaLangException;
@class JavaUtilArrayList;
@class RAREActionLink;
@class RAREUTIdentityArrayList;
@protocol JavaUtilConcurrentCallable;
@protocol JavaUtilMap;

#import "JreEmulation.h"
#include "java/lang/Runnable.h"

@interface CCPBVFHIRLinkWaiter : NSObject {
 @public
  RAREUTIdentityArrayList *links_;
  JavaUtilArrayList *cancelables_;
  JavaUtilArrayList *results_;
  int totalCount_;
  int completedCount_;
  BOOL canceled_;
  BOOL asJSON_;
  BOOL cancelOnException_;
  JavaLangException *error_;
  RAREActionLink *errorLink_;
  id<JavaUtilMap> idMap_;
}

- (id)initWithInt:(int)count;
- (id)initWithInt:(int)count
      withBoolean:(BOOL)json;
- (void)addLinkWithRAREActionLink:(RAREActionLink *)link;
- (void)addLinkWithRAREActionLink:(RAREActionLink *)link
                      withBoolean:(BOOL)asBytes;
- (void)addLinkWithRAREActionLink:(RAREActionLink *)link
   withJavaUtilConcurrentCallable:(id<JavaUtilConcurrentCallable>)task;
- (void)startWaiting;
- (BOOL)hadError;
- (JavaLangException *)getError;
- (RAREActionLink *)getErrorLink;
- (RAREActionLink *)getLinkWithInt:(int)index;
- (id)getResultWithInt:(int)index;
- (id)getResultWithRAREActionLink:(RAREActionLink *)link;
- (void)dispose;
- (void)cancelWithJavaLangException:(JavaLangException *)e
                 withRAREActionLink:(RAREActionLink *)link;
- (void)setCancelOnExceptionWithBoolean:(BOOL)cancelOnException;
- (void)copyAllFieldsTo:(CCPBVFHIRLinkWaiter *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter, links_, RAREUTIdentityArrayList *)
J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter, cancelables_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter, results_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter, error_, JavaLangException *)
J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter, errorLink_, RAREActionLink *)
J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter, idMap_, id<JavaUtilMap>)

typedef CCPBVFHIRLinkWaiter ComSparsewareBellavistaExternalFhirLinkWaiter;

@interface CCPBVFHIRLinkWaiter_DataRetriever : NSObject < JavaLangRunnable > {
 @public
  CCPBVFHIRLinkWaiter *this$0_;
  RAREActionLink *link_;
  int index_;
  BOOL asBytes_;
  id<JavaUtilConcurrentCallable> task_;
}

- (id)initWithCCPBVFHIRLinkWaiter:(CCPBVFHIRLinkWaiter *)outer$
               withRAREActionLink:(RAREActionLink *)link
                          withInt:(int)index
                      withBoolean:(BOOL)asBytes;
- (id)initWithCCPBVFHIRLinkWaiter:(CCPBVFHIRLinkWaiter *)outer$
               withRAREActionLink:(RAREActionLink *)link
                          withInt:(int)index
   withJavaUtilConcurrentCallable:(id<JavaUtilConcurrentCallable>)task;
- (void)run;
- (void)copyAllFieldsTo:(CCPBVFHIRLinkWaiter_DataRetriever *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter_DataRetriever, this$0_, CCPBVFHIRLinkWaiter *)
J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter_DataRetriever, link_, RAREActionLink *)
J2OBJC_FIELD_SETTER(CCPBVFHIRLinkWaiter_DataRetriever, task_, id<JavaUtilConcurrentCallable>)

#endif // _CCPBVFHIRLinkWaiter_H_
