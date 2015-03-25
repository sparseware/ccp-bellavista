//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Settings.java
//
//  Created by decoteaud on 3/24/15.
//

#ifndef _CCPBVSettings_H_
#define _CCPBVSettings_H_

@class CCPBVSettings_AppPreferences;
@class JavaLangStringBuilder;
@class JavaUtilEventObject;
@class JavaUtilHashMap;
@protocol JavaUtilList;
@protocol RAREUTiPreferences;
@protocol RAREiFormViewer;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/ui/iEventHandler.h"
#include "java/lang/Runnable.h"

@interface CCPBVSettings : NSObject < RAREiEventHandler > {
 @public
  CCPBVSettings_AppPreferences *preferences_;
  id<JavaUtilList> servers_;
  BOOL serversUpdated_;
}

- (id)init;
- (CCPBVSettings_AppPreferences *)getAppPreferencesWithNSString:(NSString *)user;
- (NSString *)getLastLoggedinUser;
- (void)onBackButtonWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onCheckBoxActionWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onCloseWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onConfigureBasicSettingsWithNSString:(NSString *)eventName
                             withRAREiWidget:(id<RAREiWidget>)widget
                     withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onConfigureLoginComboBoxWithNSString:(NSString *)eventName
                             withRAREiWidget:(id<RAREiWidget>)widget
                     withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onConfigureServersWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onFinishedLoadingListWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onOtherOptionsActionWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onServersAddActionWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onServersChangeWithNSString:(NSString *)eventName
                    withRAREiWidget:(id<RAREiWidget>)widget
            withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onServersDeleteActionWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onServersUnloadWithNSString:(NSString *)eventName
                    withRAREiWidget:(id<RAREiWidget>)widget
            withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onServersUpdateActionWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)saveLastLoggedinUserWithNSString:(NSString *)username;
- (void)copyAllFieldsTo:(CCPBVSettings *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings, preferences_, CCPBVSettings_AppPreferences *)
J2OBJC_FIELD_SETTER(CCPBVSettings, servers_, id<JavaUtilList>)

typedef CCPBVSettings ComSparsewareBellavistaSettings;

@interface CCPBVSettings_AppPreferences : NSObject {
 @public
  BOOL edited_;
  id<RAREUTiPreferences> prefs_;
  id<RAREUTiPreferences> globalPrefs_;
  NSString *user_;
  id<JavaUtilList> servers_;
  JavaUtilHashMap *settings_;
}

- (id)init;
- (void)dispose;
- (BOOL)getBooleanWithNSString:(NSString *)key
                   withBoolean:(BOOL)def;
- (int)getIntWithNSString:(NSString *)key
                  withInt:(int)def;
- (NSString *)getLastLoggedinUser;
- (NSString *)getPasswordWithNSString:(NSString *)key
                         withNSString:(NSString *)def;
- (id<JavaUtilList>)getServers;
- (NSString *)getStringWithNSString:(NSString *)key
                       withNSString:(NSString *)def;
- (void)putBooleanWithNSString:(NSString *)key
                   withBoolean:(BOOL)value;
- (void)putIntWithNSString:(NSString *)key
                   withInt:(int)value;
- (void)putPasswordWithNSString:(NSString *)key
                   withNSString:(NSString *)value;
- (void)putStringWithNSString:(NSString *)key
                 withNSString:(NSString *)value;
- (void)removeValueWithNSString:(NSString *)key;
- (void)saveLastLoggedinUserWithNSString:(NSString *)username;
- (void)setServersWithJavaUtilList:(id<JavaUtilList>)list;
- (void)setUserWithNSString:(NSString *)user;
- (void)update;
- (void)copyAllFieldsTo:(CCPBVSettings_AppPreferences *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings_AppPreferences, prefs_, id<RAREUTiPreferences>)
J2OBJC_FIELD_SETTER(CCPBVSettings_AppPreferences, globalPrefs_, id<RAREUTiPreferences>)
J2OBJC_FIELD_SETTER(CCPBVSettings_AppPreferences, user_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVSettings_AppPreferences, servers_, id<JavaUtilList>)
J2OBJC_FIELD_SETTER(CCPBVSettings_AppPreferences, settings_, JavaUtilHashMap *)

@interface CCPBVSettings_Server : NSObject {
 @public
  NSString *serverName_;
  NSString *serverURL_;
  BOOL isContextServer_;
}

- (id)initWithNSString:(NSString *)s;
- (id)initWithNSString:(NSString *)serverName
          withNSString:(NSString *)serverURL
           withBoolean:(BOOL)isContextServer;
- (BOOL)isValid;
- (NSString *)description;
- (JavaLangStringBuilder *)toStringWithJavaLangStringBuilder:(JavaLangStringBuilder *)sb;
- (void)copyAllFieldsTo:(CCPBVSettings_Server *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings_Server, serverName_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVSettings_Server, serverURL_, NSString *)

@interface CCPBVSettings_$1 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiFormViewer> val$fv_;
}

- (void)run;
- (id)initWithRAREiFormViewer:(id<RAREiFormViewer>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings_$1, val$fv_, id<RAREiFormViewer>)

#endif // _CCPBVSettings_H_
