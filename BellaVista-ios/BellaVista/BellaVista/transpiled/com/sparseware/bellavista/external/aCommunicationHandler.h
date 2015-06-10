//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aCommunicationHandler.java
//
//  Created by decoteaud on 5/11/15.
//

#ifndef _CCPBVaCommunicationHandler_H_
#define _CCPBVaCommunicationHandler_H_

@class CCPBVaCommunicationHandler_UserStatus;
@protocol CCPBVaCommunicationHandler_iStatusListener;
@protocol JavaUtilList;
@protocol JavaUtilMap;

#import "JreEmulation.h"
#include "java/lang/Enum.h"

@interface CCPBVaCommunicationHandler : NSObject {
 @public
  id<JavaUtilMap> listenerMap_;
}

- (id)init;
- (void)addStatusListenerWithJavaUtilList:(id<JavaUtilList>)users
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener;
- (void)changeLocalUserStatusWithNSString:(NSString *)user
withCCPBVaCommunicationHandler_UserStatus:(CCPBVaCommunicationHandler_UserStatus *)status;
- (CCPBVaCommunicationHandler_UserStatus *)getUserStatusWithNSString:(NSString *)user;
- (void)initiateAudioChatWithNSString:(NSString *)user OBJC_METHOD_FAMILY_NONE;
- (void)initiateTextChatWithNSString:(NSString *)user OBJC_METHOD_FAMILY_NONE;
- (void)initiateVideoChatWithNSString:(NSString *)user OBJC_METHOD_FAMILY_NONE;
- (BOOL)isAudioChatAvailable;
- (BOOL)isTextChatAvailable;
- (BOOL)isVideoChatAvailable;
- (void)removeStatusListenerWithCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener;
- (void)removeStatusListenerWithJavaUtilList:(id<JavaUtilList>)users
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener;
- (void)addStatusListenerWithNSString:(NSString *)user
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener;
- (void)notifyListenersWithJavaUtilMap:(id<JavaUtilMap>)statuses;
- (void)notifyOfServiceAvailabilityChange;
- (void)registerInterestWithNSString:(NSString *)user
                         withBoolean:(BOOL)previouslyRegistered;
- (void)removeStatusListenerWithNSString:(NSString *)user
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener;
- (void)unregisterInterestWithNSString:(NSString *)user;
- (void)copyAllFieldsTo:(CCPBVaCommunicationHandler *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaCommunicationHandler, listenerMap_, id<JavaUtilMap>)

typedef CCPBVaCommunicationHandler ComSparsewareBellavistaExternalACommunicationHandler;

@protocol CCPBVaCommunicationHandler_iStatusListener < NSObject, JavaObject >
- (void)serviceAvailbilityChangedWithCCPBVaCommunicationHandler:(CCPBVaCommunicationHandler *)handler;
- (void)statusChangedWithCCPBVaCommunicationHandler:(CCPBVaCommunicationHandler *)handler
                                       withNSString:(NSString *)user
          withCCPBVaCommunicationHandler_UserStatus:(CCPBVaCommunicationHandler_UserStatus *)status;
@end

typedef enum {
  CCPBVaCommunicationHandler_Status_ONLINE = 0,
  CCPBVaCommunicationHandler_Status_OFFLINE = 1,
  CCPBVaCommunicationHandler_Status_AWAY = 2,
  CCPBVaCommunicationHandler_Status_BUSY = 3,
} CCPBVaCommunicationHandler_Status;

@interface CCPBVaCommunicationHandler_StatusEnum : JavaLangEnum < NSCopying > {
}
+ (CCPBVaCommunicationHandler_StatusEnum *)ONLINE;
+ (CCPBVaCommunicationHandler_StatusEnum *)OFFLINE;
+ (CCPBVaCommunicationHandler_StatusEnum *)AWAY;
+ (CCPBVaCommunicationHandler_StatusEnum *)BUSY;
+ (IOSObjectArray *)values;
+ (CCPBVaCommunicationHandler_StatusEnum *)valueOfWithNSString:(NSString *)name;
- (id)copyWithZone:(NSZone *)zone;
- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal;
@end

@interface CCPBVaCommunicationHandler_UserStatus : NSObject {
 @public
  CCPBVaCommunicationHandler_StatusEnum *videoChat_;
  CCPBVaCommunicationHandler_StatusEnum *audioChat_;
  CCPBVaCommunicationHandler_StatusEnum *textChat_;
}

- (id)init;
- (void)copyAllFieldsTo:(CCPBVaCommunicationHandler_UserStatus *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaCommunicationHandler_UserStatus, videoChat_, CCPBVaCommunicationHandler_StatusEnum *)
J2OBJC_FIELD_SETTER(CCPBVaCommunicationHandler_UserStatus, audioChat_, CCPBVaCommunicationHandler_StatusEnum *)
J2OBJC_FIELD_SETTER(CCPBVaCommunicationHandler_UserStatus, textChat_, CCPBVaCommunicationHandler_StatusEnum *)

#endif // _CCPBVaCommunicationHandler_H_
