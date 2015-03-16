//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/aEventHandler.java
//
//  Created by decoteaud on 3/12/15.
//

#ifndef _ComSparsewareBellavistaaEventHandler_H_
#define _ComSparsewareBellavistaaEventHandler_H_

@class JavaLangException;
@class JavaUtilEventObject;
@class RAREActionLink;
@class RAREWindowViewer;
@class RAREaWidget;
@protocol JavaUtilList;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/ui/iEventHandler.h"

@interface ComSparsewareBellavistaaEventHandler : NSObject < RAREiEventHandler > {
}

- (id)init;
- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)connecting;
- (void)widgetDisposed;
- (void)errorOccuredWithJavaLangException:(JavaLangException *)e;
- (void)parseDataURLWithRAREaWidget:(RAREaWidget *)widget
                       withNSString:(NSString *)href
                        withBoolean:(BOOL)rowInfo
                        withBoolean:(BOOL)tabular;
- (void)parseDataURLWithRAREaWidget:(RAREaWidget *)widget
                 withRAREActionLink:(RAREActionLink *)link
                        withBoolean:(BOOL)tabular;
- (void)dataParsedWithRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilList:(id<JavaUtilList>)rows
               withRAREActionLink:(RAREActionLink *)link;
@end

@interface ComSparsewareBellavistaaEventHandler_$1 : RAREaWorkerTask {
 @public
  ComSparsewareBellavistaaEventHandler *this$0_;
  RAREWindowViewer *val$w_;
  RAREaWidget *val$widget_;
  RAREActionLink *val$link_;
  BOOL val$tabular_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithComSparsewareBellavistaaEventHandler:(ComSparsewareBellavistaaEventHandler *)outer$
                              withRAREWindowViewer:(RAREWindowViewer *)capture$0
                                   withRAREaWidget:(RAREaWidget *)capture$1
                                withRAREActionLink:(RAREActionLink *)capture$2
                                       withBoolean:(BOOL)capture$3;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaaEventHandler_$1, this$0_, ComSparsewareBellavistaaEventHandler *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaaEventHandler_$1, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaaEventHandler_$1, val$widget_, RAREaWidget *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaaEventHandler_$1, val$link_, RAREActionLink *)

#endif // _ComSparsewareBellavistaaEventHandler_H_
