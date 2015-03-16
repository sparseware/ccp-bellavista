//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aCommunicationHandler.java
//
//  Created by decoteaud on 3/12/15.
//

#ifndef _ComSparsewareBellavistaExternalaCommunicationHandler_H_
#define _ComSparsewareBellavistaExternalaCommunicationHandler_H_

@class ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus;
@protocol ComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener;
@protocol JavaUtilList;
@protocol JavaUtilMap;

#import "JreEmulation.h"
#include "java/lang/Enum.h"

@interface ComSparsewareBellavistaExternalaCommunicationHandler : NSObject {
 @public
  id<JavaUtilMap> listenerMap_;
}

- (id)init;
- (void)addStatusListenerWithJavaUtilList:(id<JavaUtilList>)users
withComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener:(id<ComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener>)listener;
- (void)changeLocalUserStatusWithNSString:(NSString *)user
withComSparsewareBellavistaExternalaCommunicationHandler_UserStatus:(ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus *)status;
- (ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus *)getUserStatusWithNSString:(NSString *)user;
- (void)initiateAudioChatWithNSString:(NSString *)user OBJC_METHOD_FAMILY_NONE;
- (void)initiateTextChatWithNSString:(NSString *)user OBJC_METHOD_FAMILY_NONE;
- (void)initiateVideoChatWithNSString:(NSString *)user OBJC_METHOD_FAMILY_NONE;
- (BOOL)isAudioChatAvailable;
- (BOOL)isTextChatAvailable;
- (BOOL)isVideoChatAvailable;
- (void)removeStatusListenerWithComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener:(id<ComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener>)listener;
- (void)removeStatusListenerWithJavaUtilList:(id<JavaUtilList>)users
withComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener:(id<ComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener>)listener;
- (void)addStatusListenerWithNSString:(NSString *)user
withComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener:(id<ComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener>)listener;
- (void)notifyListenersWithJavaUtilMap:(id<JavaUtilMap>)statuses;
- (void)notifyOfServiceAvailabilityChange;
- (void)registerInterestWithNSString:(NSString *)user
                         withBoolean:(BOOL)previouslyRegistered;
- (void)removeStatusListenerWithNSString:(NSString *)user
withComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener:(id<ComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener>)listener;
- (void)unregisterInterestWithNSString:(NSString *)user;
- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalaCommunicationHandler *)other;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalaCommunicationHandler, listenerMap_, id<JavaUtilMap>)

@protocol ComSparsewareBellavistaExternalaCommunicationHandler_iStatusListener < NSObject, JavaObject >
- (void)serviceAvailbilityChangedWithComSparsewareBellavistaExternalaCommunicationHandler:(ComSparsewareBellavistaExternalaCommunicationHandler *)handler;
- (void)statusChangedWithComSparsewareBellavistaExternalaCommunicationHandler:(ComSparsewareBellavistaExternalaCommunicationHandler *)handler
                                                                 withNSString:(NSString *)user
          withComSparsewareBellavistaExternalaCommunicationHandler_UserStatus:(ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus *)status;
@end

typedef enum {
  ComSparsewareBellavistaExternalaCommunicationHandler_Status_ONLINE = 0,
  ComSparsewareBellavistaExternalaCommunicationHandler_Status_OFFLINE = 1,
  ComSparsewareBellavistaExternalaCommunicationHandler_Status_AWAY = 2,
  ComSparsewareBellavistaExternalaCommunicationHandler_Status_BUSY = 3,
} ComSparsewareBellavistaExternalaCommunicationHandler_Status;

@interface ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum : JavaLangEnum < NSCopying > {
}
+ (ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)ONLINE;
+ (ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)OFFLINE;
+ (ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)AWAY;
+ (ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)BUSY;
+ (IOSObjectArray *)values;
+ (ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)valueOfWithNSString:(NSString *)name;
- (id)copyWithZone:(NSZone *)zone;
- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal;
@end

@interface ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus : NSObject {
 @public
  ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *videoChat_;
  ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *audioChat_;
  ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *textChat_;
}

- (id)init;
- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus *)other;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus, videoChat_, ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus, audioChat_, ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalaCommunicationHandler_UserStatus, textChat_, ComSparsewareBellavistaExternalaCommunicationHandler_StatusEnum *)

#endif // _ComSparsewareBellavistaExternalaCommunicationHandler_H_
