//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aCommunicationHandler.java
//
//  Created by decoteaud on 3/14/16.
//

#include "IOSClass.h"
#include "com/appnativa/util/IdentityArrayList.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/external/aCommunicationHandler.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/util/Collection.h"
#include "java/util/HashMap.h"
#include "java/util/HashSet.h"
#include "java/util/Iterator.h"
#include "java/util/List.h"
#include "java/util/Map.h"
#include "java/util/Set.h"

@implementation CCPBVaCommunicationHandler

- (id)init {
  return [super init];
}

- (void)addStatusListenerWithJavaUtilList:(id<JavaUtilList>)users
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener {
  int len = [((id<JavaUtilList>) nil_chk(users)) size];
  if ((listenerMap_ == nil) && (len > 0)) {
    listenerMap_ = [[JavaUtilHashMap alloc] initWithInt:(len > 10) ? len : 10];
  }
  for (int i = 0; i < len; i++) {
    NSString *id_ = [users getWithInt:i];
    [self addStatusListenerWithNSString:id_ withCCPBVaCommunicationHandler_iStatusListener:listener];
  }
}

- (void)changeLocalUserStatusWithNSString:(NSString *)id_
withCCPBVaCommunicationHandler_UserStatus:(CCPBVaCommunicationHandler_UserStatus *)status {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (CCPBVaCommunicationHandler_UserStatus *)getUserStatusWithNSString:(NSString *)id_ {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)initiateAudioChatWithRAREUTJSONObject:(RAREUTJSONObject *)user {
}

- (void)initiateTextChatWithRAREUTJSONObject:(RAREUTJSONObject *)user {
}

- (void)initiateVideoChatWithRAREUTJSONObject:(RAREUTJSONObject *)user {
}

- (BOOL)isAudioChatAvailableWithRAREUTJSONObject:(RAREUTJSONObject *)user {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (BOOL)isTextChatAvailableWithRAREUTJSONObject:(RAREUTJSONObject *)user {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (BOOL)isVideoChatAvailableWithRAREUTJSONObject:(RAREUTJSONObject *)user {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)removeStatusListenerWithCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener {
  if (listenerMap_ != nil) {
    id<JavaUtilIterator> it = [((id<JavaUtilSet>) nil_chk([listenerMap_ entrySet])) iterator];
    while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
      id<JavaUtilMap_Entry> e = [it next];
      id o = [((id<JavaUtilMap_Entry>) nil_chk(e)) getValue];
      if (o == listener) {
        [it remove];
        [self unregisterInterestWithNSString:[e getKey]];
      }
      else if ([o conformsToProtocol: @protocol(JavaUtilList)]) {
        id<JavaUtilList> list = (id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList));
        [((id<JavaUtilList>) nil_chk(list)) removeWithId:listener];
        if ([list isEmpty]) {
          [it remove];
          [self unregisterInterestWithNSString:[e getKey]];
        }
      }
    }
  }
}

- (void)removeStatusListenerWithJavaUtilList:(id<JavaUtilList>)users
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener {
  if (listenerMap_ != nil) {
    int len = [((id<JavaUtilList>) nil_chk(users)) size];
    for (int i = 0; i < len; i++) {
      NSString *id_ = [users getWithInt:i];
      [self removeStatusListenerWithNSString:id_ withCCPBVaCommunicationHandler_iStatusListener:listener];
    }
  }
}

- (void)addStatusListenerWithNSString:(NSString *)id_
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener {
  if (listenerMap_ == nil) {
    id o = [((id<JavaUtilMap>) nil_chk(listenerMap_)) getWithId:id_];
    if (o == listener) {
      return;
    }
    else if ([o conformsToProtocol: @protocol(JavaUtilList)]) {
      if ([((id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList))) indexOfWithId:listener] != -1) {
        return;
      }
    }
    if (o == nil) {
      (void) [listenerMap_ putWithId:id_ withId:listener];
    }
    else if ([o conformsToProtocol: @protocol(JavaUtilList)]) {
      [((id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList))) addWithId:listener];
    }
    else {
      RAREUTIdentityArrayList *list = [[RAREUTIdentityArrayList alloc] initWithInt:2];
      [list addWithId:o];
      [list addWithId:listener];
      (void) [listenerMap_ putWithId:id_ withId:list];
    }
    [self registerInterestWithNSString:id_ withBoolean:o != nil];
  }
}

- (void)notifyListenersWithJavaUtilMap:(id<JavaUtilMap>)statuses {
  if (listenerMap_ != nil) {
    id<JavaUtilMap> listeners = listenerMap_;
    id<JavaUtilIterator> it = [((id<JavaUtilSet>) nil_chk([((id<JavaUtilMap>) nil_chk(statuses)) entrySet])) iterator];
    while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
      id<JavaUtilMap_Entry> e = [it next];
      NSString *user = [((id<JavaUtilMap_Entry>) nil_chk(e)) getKey];
      CCPBVaCommunicationHandler_UserStatus *status = [e getValue];
      id o = [listeners getWithId:user];
      if ([o conformsToProtocol: @protocol(CCPBVaCommunicationHandler_iStatusListener)]) {
        [((id<CCPBVaCommunicationHandler_iStatusListener>) check_protocol_cast(o, @protocol(CCPBVaCommunicationHandler_iStatusListener))) statusChangedWithCCPBVaCommunicationHandler:self withNSString:user withCCPBVaCommunicationHandler_UserStatus:status];
      }
      else if ([o conformsToProtocol: @protocol(JavaUtilList)]) {
        id<JavaUtilList> list = (id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList));
        int len = [((id<JavaUtilList>) nil_chk(list)) size];
        for (int i = 0; i < len; i++) {
          [((id<CCPBVaCommunicationHandler_iStatusListener>) check_protocol_cast([list getWithInt:i], @protocol(CCPBVaCommunicationHandler_iStatusListener))) statusChangedWithCCPBVaCommunicationHandler:self withNSString:user withCCPBVaCommunicationHandler_UserStatus:status];
        }
      }
    }
  }
}

- (void)notifyOfServiceAvailabilityChange {
  if (listenerMap_ != nil) {
    id<JavaUtilIterator> it = [((id<JavaUtilCollection>) nil_chk([listenerMap_ values])) iterator];
    JavaUtilHashSet *notified = [[JavaUtilHashSet alloc] init];
    while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
      id o = [it next];
      if ([o conformsToProtocol: @protocol(CCPBVaCommunicationHandler_iStatusListener)]) {
        id<CCPBVaCommunicationHandler_iStatusListener> l = (id<CCPBVaCommunicationHandler_iStatusListener>) check_protocol_cast(o, @protocol(CCPBVaCommunicationHandler_iStatusListener));
        if ([notified addWithId:l]) {
          [((id<CCPBVaCommunicationHandler_iStatusListener>) nil_chk(l)) serviceAvailbilityChangedWithCCPBVaCommunicationHandler:self];
        }
      }
      else if ([o conformsToProtocol: @protocol(JavaUtilList)]) {
        id<JavaUtilList> list = (id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList));
        int len = [((id<JavaUtilList>) nil_chk(list)) size];
        for (int i = 0; i < len; i++) {
          id<CCPBVaCommunicationHandler_iStatusListener> l = (id<CCPBVaCommunicationHandler_iStatusListener>) check_protocol_cast([list getWithInt:i], @protocol(CCPBVaCommunicationHandler_iStatusListener));
          if ([notified addWithId:l]) {
            [((id<CCPBVaCommunicationHandler_iStatusListener>) nil_chk(l)) serviceAvailbilityChangedWithCCPBVaCommunicationHandler:self];
          }
        }
      }
    }
  }
}

- (void)registerInterestWithNSString:(NSString *)user
                         withBoolean:(BOOL)previouslyRegistered {
}

- (void)removeStatusListenerWithNSString:(NSString *)user
withCCPBVaCommunicationHandler_iStatusListener:(id<CCPBVaCommunicationHandler_iStatusListener>)listener {
  if (listenerMap_ == nil) {
    id o = [((id<JavaUtilMap>) nil_chk(listenerMap_)) getWithId:user];
    if (o == listener) {
      (void) [listenerMap_ removeWithId:user];
      [self unregisterInterestWithNSString:user];
    }
    else if ([o conformsToProtocol: @protocol(JavaUtilList)]) {
      id<JavaUtilList> list = (id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList));
      [((id<JavaUtilList>) nil_chk(list)) removeWithId:listener];
      if ([list isEmpty]) {
        (void) [listenerMap_ removeWithId:user];
        [self unregisterInterestWithNSString:user];
      }
    }
  }
}

- (void)unregisterInterestWithNSString:(NSString *)id_ {
}

- (void)copyAllFieldsTo:(CCPBVaCommunicationHandler *)other {
  [super copyAllFieldsTo:other];
  other->listenerMap_ = listenerMap_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "changeLocalUserStatusWithNSString:withCCPBVaCommunicationHandler_UserStatus:", NULL, "V", 0x401, NULL },
    { "getUserStatusWithNSString:", NULL, "LCCPBVaCommunicationHandler_UserStatus", 0x401, NULL },
    { "isAudioChatAvailableWithRAREUTJSONObject:", NULL, "Z", 0x401, NULL },
    { "isTextChatAvailableWithRAREUTJSONObject:", NULL, "Z", 0x401, NULL },
    { "isVideoChatAvailableWithRAREUTJSONObject:", NULL, "Z", 0x401, NULL },
    { "addStatusListenerWithNSString:withCCPBVaCommunicationHandler_iStatusListener:", NULL, "V", 0x4, NULL },
    { "notifyListenersWithJavaUtilMap:", NULL, "V", 0x4, NULL },
    { "notifyOfServiceAvailabilityChange", NULL, "V", 0x4, NULL },
    { "registerInterestWithNSString:withBoolean:", NULL, "V", 0x4, NULL },
    { "removeStatusListenerWithNSString:withCCPBVaCommunicationHandler_iStatusListener:", NULL, "V", 0x4, NULL },
    { "unregisterInterestWithNSString:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "listenerMap_", NULL, 0x4, "LJavaUtilMap" },
  };
  static J2ObjcClassInfo _CCPBVaCommunicationHandler = { "aCommunicationHandler", "com.sparseware.bellavista.external", NULL, 0x401, 11, methods, 1, fields, 0, NULL};
  return &_CCPBVaCommunicationHandler;
}

@end

@interface CCPBVaCommunicationHandler_iStatusListener : NSObject
@end

@implementation CCPBVaCommunicationHandler_iStatusListener

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "serviceAvailbilityChangedWithCCPBVaCommunicationHandler:", NULL, "V", 0x401, NULL },
    { "statusChangedWithCCPBVaCommunicationHandler:withNSString:withCCPBVaCommunicationHandler_UserStatus:", NULL, "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _CCPBVaCommunicationHandler_iStatusListener = { "iStatusListener", "com.sparseware.bellavista.external", "aCommunicationHandler", 0x201, 2, methods, 0, NULL, 0, NULL};
  return &_CCPBVaCommunicationHandler_iStatusListener;
}

@end

static CCPBVaCommunicationHandler_StatusEnum *CCPBVaCommunicationHandler_StatusEnum_ONLINE;
static CCPBVaCommunicationHandler_StatusEnum *CCPBVaCommunicationHandler_StatusEnum_OFFLINE;
static CCPBVaCommunicationHandler_StatusEnum *CCPBVaCommunicationHandler_StatusEnum_AWAY;
static CCPBVaCommunicationHandler_StatusEnum *CCPBVaCommunicationHandler_StatusEnum_BUSY;
IOSObjectArray *CCPBVaCommunicationHandler_StatusEnum_values;

@implementation CCPBVaCommunicationHandler_StatusEnum

+ (CCPBVaCommunicationHandler_StatusEnum *)ONLINE {
  return CCPBVaCommunicationHandler_StatusEnum_ONLINE;
}
+ (CCPBVaCommunicationHandler_StatusEnum *)OFFLINE {
  return CCPBVaCommunicationHandler_StatusEnum_OFFLINE;
}
+ (CCPBVaCommunicationHandler_StatusEnum *)AWAY {
  return CCPBVaCommunicationHandler_StatusEnum_AWAY;
}
+ (CCPBVaCommunicationHandler_StatusEnum *)BUSY {
  return CCPBVaCommunicationHandler_StatusEnum_BUSY;
}

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [CCPBVaCommunicationHandler_StatusEnum class]) {
    CCPBVaCommunicationHandler_StatusEnum_ONLINE = [[CCPBVaCommunicationHandler_StatusEnum alloc] initWithNSString:@"ONLINE" withInt:0];
    CCPBVaCommunicationHandler_StatusEnum_OFFLINE = [[CCPBVaCommunicationHandler_StatusEnum alloc] initWithNSString:@"OFFLINE" withInt:1];
    CCPBVaCommunicationHandler_StatusEnum_AWAY = [[CCPBVaCommunicationHandler_StatusEnum alloc] initWithNSString:@"AWAY" withInt:2];
    CCPBVaCommunicationHandler_StatusEnum_BUSY = [[CCPBVaCommunicationHandler_StatusEnum alloc] initWithNSString:@"BUSY" withInt:3];
    CCPBVaCommunicationHandler_StatusEnum_values = [[IOSObjectArray alloc] initWithObjects:(id[]){ CCPBVaCommunicationHandler_StatusEnum_ONLINE, CCPBVaCommunicationHandler_StatusEnum_OFFLINE, CCPBVaCommunicationHandler_StatusEnum_AWAY, CCPBVaCommunicationHandler_StatusEnum_BUSY, nil } count:4 type:[IOSClass classWithClass:[CCPBVaCommunicationHandler_StatusEnum class]]];
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithArray:CCPBVaCommunicationHandler_StatusEnum_values];
}

+ (CCPBVaCommunicationHandler_StatusEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < [CCPBVaCommunicationHandler_StatusEnum_values count]; i++) {
    CCPBVaCommunicationHandler_StatusEnum *e = CCPBVaCommunicationHandler_StatusEnum_values->buffer_[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static const char *superclass_type_args[] = {"LCCPBVaCommunicationHandler_StatusEnum"};
  static J2ObjcClassInfo _CCPBVaCommunicationHandler_StatusEnum = { "Status", "com.sparseware.bellavista.external", "aCommunicationHandler", 0x4019, 0, NULL, 0, NULL, 1, superclass_type_args};
  return &_CCPBVaCommunicationHandler_StatusEnum;
}

@end
@implementation CCPBVaCommunicationHandler_UserStatus

- (id)init {
  if (self = [super init]) {
    videoChat_ = [CCPBVaCommunicationHandler_StatusEnum OFFLINE];
    audioChat_ = [CCPBVaCommunicationHandler_StatusEnum OFFLINE];
    textChat_ = [CCPBVaCommunicationHandler_StatusEnum OFFLINE];
  }
  return self;
}

- (void)copyAllFieldsTo:(CCPBVaCommunicationHandler_UserStatus *)other {
  [super copyAllFieldsTo:other];
  other->audioChat_ = audioChat_;
  other->textChat_ = textChat_;
  other->videoChat_ = videoChat_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "videoChat_", NULL, 0x1, "LCCPBVaCommunicationHandler_StatusEnum" },
    { "audioChat_", NULL, 0x1, "LCCPBVaCommunicationHandler_StatusEnum" },
    { "textChat_", NULL, 0x1, "LCCPBVaCommunicationHandler_StatusEnum" },
  };
  static J2ObjcClassInfo _CCPBVaCommunicationHandler_UserStatus = { "UserStatus", "com.sparseware.bellavista.external", "aCommunicationHandler", 0x9, 0, NULL, 3, fields, 0, NULL};
  return &_CCPBVaCommunicationHandler_UserStatus;
}

@end
