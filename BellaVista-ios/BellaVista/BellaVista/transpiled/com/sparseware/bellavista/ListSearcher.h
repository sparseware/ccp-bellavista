//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/ListSearcher.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVListSearcher_H_
#define _CCPBVListSearcher_H_

@class CCPBVListPager;
@class JavaUtilEventObject;
@class RAREActionLink;
@class RAREListBoxViewer;
@class RAREWindowViewer;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/iEventHandler.h"

@interface CCPBVListSearcher : NSObject < RAREiEventHandler > {
}

- (id)init;
- (void)changePageWithBoolean:(BOOL)next
              withRAREiWidget:(id<RAREiWidget>)nextPageWidget
              withRAREiWidget:(id<RAREiWidget>)previousPageWidget;
- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onListCreatedWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onNextOrPreviousPageWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSearchFieldActionWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (RAREListBoxViewer *)getListBoxWithRAREiWidget:(id<RAREiWidget>)widget;
- (void)load__WithRAREListBoxViewer:(RAREListBoxViewer *)lb
                 withRAREActionLink:(RAREActionLink *)link;
- (void)updateButtonsWithRAREListBoxViewer:(RAREListBoxViewer *)lb
                        withCCPBVListPager:(CCPBVListPager *)pager;
@end

typedef CCPBVListSearcher ComSparsewareBellavistaListSearcher;

@interface CCPBVListSearcher_$1 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVListSearcher *this$0_;
  RAREWindowViewer *val$w_;
  RAREListBoxViewer *val$lb_;
  RAREActionLink *val$link_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVListSearcher:(CCPBVListSearcher *)outer$
           withRAREWindowViewer:(RAREWindowViewer *)capture$0
          withRAREListBoxViewer:(RAREListBoxViewer *)capture$1
             withRAREActionLink:(RAREActionLink *)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVListSearcher_$1, this$0_, CCPBVListSearcher *)
J2OBJC_FIELD_SETTER(CCPBVListSearcher_$1, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVListSearcher_$1, val$lb_, RAREListBoxViewer *)
J2OBJC_FIELD_SETTER(CCPBVListSearcher_$1, val$link_, RAREActionLink *)

#endif // _CCPBVListSearcher_H_