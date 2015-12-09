//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Settings.java
//
//  Created by decoteaud on 11/19/15.
//

#ifndef _CCPBVSettings_H_
#define _CCPBVSettings_H_

@class CCPBVSettings_AppPreferences;
@class CCPBVSettings_Server;
@class JavaLangStringBuilder;
@class JavaUtilEventObject;
@class JavaUtilHashMap;
@class RAREActionLink;
@class RAREWindowViewer;
@protocol JavaUtilList;
@protocol RAREUTiPreferences;
@protocol RAREiFormViewer;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/aWorkerTask.h"
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
- (void)onSubmitPinWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onPinFocusedWithNSString:(NSString *)eventName
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
- (CCPBVSettings_Server *)getDefaultServer;
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
- (void)onServersAddRemoteActionWithNSString:(NSString *)eventName
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

#define CCPBVSettings_Server_TYPE_CONTEXT 1
#define CCPBVSettings_Server_TYPE_DEMO 2
#define CCPBVSettings_Server_TYPE_FROZEN 4111

@interface CCPBVSettings_Server : NSObject {
 @public
  NSString *serverName_;
  NSString *serverURL_;
  int serverType_;
}

+ (int)TYPE_CONTEXT;
+ (int)TYPE_DEMO;
+ (int)TYPE_FROZEN;
- (id)initWithNSString:(NSString *)s;
- (void)populateWithJavaUtilList:(id<JavaUtilList>)values;
- (id)initWithNSString:(NSString *)serverName
          withNSString:(NSString *)serverURL
           withBoolean:(BOOL)isContextServer;
- (id)initWithNSString:(NSString *)serverName
          withNSString:(NSString *)serverURL
               withInt:(int)serverType;
- (BOOL)isContextServer;
- (BOOL)isDemoServer;
- (BOOL)isFrozen;
- (BOOL)isValid;
- (NSString *)description;
- (JavaLangStringBuilder *)toStringWithJavaLangStringBuilder:(JavaLangStringBuilder *)sb;
- (void)copyAllFieldsTo:(CCPBVSettings_Server *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings_Server, serverName_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVSettings_Server, serverURL_, NSString *)

@interface CCPBVSettings_$1 : RAREaWorkerTask {
 @public
  RAREWindowViewer *val$w_;
  id<RAREiWidget> val$l_;
  RAREActionLink *val$link_;
  JavaUtilHashMap *val$data_;
}

- (void)finishWithId:(id)result;
- (id)compute;
- (id)initWithRAREWindowViewer:(RAREWindowViewer *)capture$0
               withRAREiWidget:(id<RAREiWidget>)capture$1
            withRAREActionLink:(RAREActionLink *)capture$2
           withJavaUtilHashMap:(JavaUtilHashMap *)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings_$1, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVSettings_$1, val$l_, id<RAREiWidget>)
J2OBJC_FIELD_SETTER(CCPBVSettings_$1, val$link_, RAREActionLink *)
J2OBJC_FIELD_SETTER(CCPBVSettings_$1, val$data_, JavaUtilHashMap *)

@interface CCPBVSettings_$2 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiFormViewer> val$fv_;
}

- (void)run;
- (id)initWithRAREiFormViewer:(id<RAREiFormViewer>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings_$2, val$fv_, id<RAREiFormViewer>)

@interface CCPBVSettings_$3 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiFormViewer> val$fv_;
}

- (void)run;
- (id)initWithRAREiFormViewer:(id<RAREiFormViewer>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVSettings_$3, val$fv_, id<RAREiFormViewer>)

#endif // _CCPBVSettings_H_
