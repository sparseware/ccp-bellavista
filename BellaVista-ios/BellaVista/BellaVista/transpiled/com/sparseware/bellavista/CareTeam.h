//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/CareTeam.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVCareTeam_H_
#define _CCPBVCareTeam_H_

@class CCPBVaCommunicationHandler;
@class CCPBVaCommunicationHandler_UserStatus;
@class JavaUtilEventObject;
@class RAREListBoxViewer;
@class RARERenderableDataItem;
@class RAREUTJSONArray;
@class RAREWindowViewer;
@protocol JavaUtilMap;
@protocol RAREiFormViewer;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/iEventHandler.h"
#include "com/sparseware/bellavista/external/aCommunicationHandler.h"

@interface CCPBVCareTeam : NSObject < RAREiEventHandler, CCPBVaCommunicationHandler_iStatusListener > {
 @public
  CCPBVaCommunicationHandler *commHandler_;
  id<JavaUtilMap> userStatuses_;
  RAREListBoxViewer *listViewer_;
  id<JavaUtilMap> statusIcons_;
  int idPosition_;
  long long int lastUpdateTime_;
  int dataTimeout_;
}

+ (CCPBVaCommunicationHandler_UserStatus *)OFFLINE;
+ (void)setOFFLINE:(CCPBVaCommunicationHandler_UserStatus *)OFFLINE;
- (id)init;
- (void)onCreatedWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onListChangeWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onListShownWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onShownWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)serviceAvailbilityChangedWithCCPBVaCommunicationHandler:(CCPBVaCommunicationHandler *)handler;
- (void)statusChangedWithCCPBVaCommunicationHandler:(CCPBVaCommunicationHandler *)handler
                                       withNSString:(NSString *)user
          withCCPBVaCommunicationHandler_UserStatus:(CCPBVaCommunicationHandler_UserStatus *)status;
- (NSString *)getUserIDWithRARERenderableDataItem:(RARERenderableDataItem *)row;
- (void)updateFormWithRAREiFormViewer:(id<RAREiFormViewer>)fv
                  withRAREUTJSONArray:(RAREUTJSONArray *)items;
- (void)copyAllFieldsTo:(CCPBVCareTeam *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVCareTeam, commHandler_, CCPBVaCommunicationHandler *)
J2OBJC_FIELD_SETTER(CCPBVCareTeam, userStatuses_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVCareTeam, listViewer_, RAREListBoxViewer *)
J2OBJC_FIELD_SETTER(CCPBVCareTeam, statusIcons_, id<JavaUtilMap>)

typedef CCPBVCareTeam ComSparsewareBellavistaCareTeam;

@interface CCPBVCareTeam_$1 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVCareTeam *this$0_;
  RAREWindowViewer *val$w_;
  id<RAREiWidget> val$widget_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVCareTeam:(CCPBVCareTeam *)outer$
       withRAREWindowViewer:(RAREWindowViewer *)capture$0
            withRAREiWidget:(id<RAREiWidget>)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVCareTeam_$1, this$0_, CCPBVCareTeam *)
J2OBJC_FIELD_SETTER(CCPBVCareTeam_$1, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVCareTeam_$1, val$widget_, id<RAREiWidget>)

#endif // _CCPBVCareTeam_H_
