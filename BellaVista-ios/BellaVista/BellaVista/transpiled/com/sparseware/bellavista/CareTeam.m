//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/CareTeam.java
//
//  Created by decoteaud on 5/11/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/iCancelableFuture.h"
#include "com/appnativa/rare/iConstants.h"
#include "com/appnativa/rare/iDataCollection.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/spot/Rectangle.h"
#include "com/appnativa/rare/spot/Viewer.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIDimension.h"
#include "com/appnativa/rare/ui/UIScreen.h"
#include "com/appnativa/rare/ui/event/DataEvent.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/viewer/ListBoxViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/aListViewer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/SPOTPrintableString.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/CareTeam.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/external/aCommunicationHandler.h"
#include "java/lang/Exception.h"
#include "java/lang/Math.h"
#include "java/lang/StringBuilder.h"
#include "java/lang/System.h"
#include "java/lang/Throwable.h"
#include "java/util/ArrayList.h"
#include "java/util/Collection.h"
#include "java/util/EventObject.h"
#include "java/util/HashMap.h"
#include "java/util/Iterator.h"
#include "java/util/Map.h"

@implementation CCPBVCareTeam

static CCPBVaCommunicationHandler_UserStatus * CCPBVCareTeam_OFFLINE_;

+ (CCPBVaCommunicationHandler_UserStatus *)OFFLINE {
  return CCPBVCareTeam_OFFLINE_;
}

+ (void)setOFFLINE:(CCPBVaCommunicationHandler_UserStatus *)OFFLINE {
  CCPBVCareTeam_OFFLINE_ = OFFLINE;
}

- (id)init {
  if (self = [super init]) {
    idPosition_ = 1;
    RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"careTeamInfo"], [RAREUTJSONObject class]);
    dataTimeout_ = [((RAREUTJSONObject *) nil_chk(info)) optIntWithNSString:@"dataTimeout" withInt:0] * 1000;
    commHandler_ = [CCPBVUtils getaCommunicationHandler];
    if (commHandler_ != nil) {
      userStatuses_ = [[JavaUtilHashMap alloc] init];
      statusIcons_ = [CCPBVUtils getStatusIcons];
      idPosition_ = [info optIntWithNSString:@"commIdPosition" withInt:1];
    }
  }
  return self;
}

- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (commHandler_ != nil) {
    [commHandler_ removeStatusListenerWithCCPBVaCommunicationHandler_iStatusListener:self];
  }
  commHandler_ = nil;
  userStatuses_ = nil;
  listViewer_ = nil;
}

- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (NSString *)getUserIDWithRARERenderableDataItem:(RARERenderableDataItem *)row {
  RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getItemExWithInt:idPosition_];
  return item == nil ? nil : (NSString *) check_class_cast([item getValue], [NSString class]);
}

- (void)onCreatedWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (![RAREUIScreen isLargeScreen]) {
    RAREUIDimension *size = [RAREUIScreen getUsableSize];
    int n = (int) [RAREUIScreen fromPlatformPixelsWithFloat:[JavaLangMath maxWithFloat:((RAREUIDimension *) nil_chk(size))->width_ withFloat:size->height_]];
    if (n < 540) {
      RARESPOTViewer *cfg = (RARESPOTViewer *) check_class_cast([((RAREDataEvent *) check_class_cast(event, [RAREDataEvent class])) getData], [RARESPOTViewer class]);
      [((SPOTPrintableString *) nil_chk(((RARESPOTRectangle *) nil_chk(((RARESPOTViewer *) nil_chk(cfg))->bounds_))->height_)) setValueWithNSString:@"14ln"];
    }
    else {
      RARESPOTViewer *cfg = (RARESPOTViewer *) check_class_cast([((RAREDataEvent *) check_class_cast(event, [RAREDataEvent class])) getData], [RARESPOTViewer class]);
      [((SPOTPrintableString *) nil_chk(((RARESPOTRectangle *) nil_chk(((RARESPOTViewer *) nil_chk(cfg))->bounds_))->height_)) setValueWithNSString:@"18ln"];
    }
  }
}

- (void)onShownWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  id<RAREiDataCollection> dc = [((RAREWindowViewer *) nil_chk(w)) getDataCollectionWithNSString:@"careteam"];
  if (lastUpdateTime_ + dataTimeout_ > [JavaLangSystem currentTimeMillis] && [((id<RAREiDataCollection>) nil_chk(dc)) isLoaded]) {
    [self updateFormWithRAREiFormViewer:[((id<RAREiWidget>) nil_chk(widget)) getFormViewer] withRAREiDataCollection:dc];
  }
  else {
    RAREaWorkerTask *task = [[CCPBVCareTeam_$1 alloc] initWithCCPBVCareTeam:self withRAREWindowViewer:w withRAREiWidget:widget withRAREiDataCollection:dc];
    [w showWaitCursor];
    (void) [w spawnWithNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ task } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
  }
}

- (void)updateFormWithRAREiFormViewer:(id<RAREiFormViewer>)fv
              withRAREiDataCollection:(id<RAREiDataCollection>)dc {
  id<JavaUtilCollection> items = [((id<RAREiDataCollection>) nil_chk(dc)) getItemDataWithRAREiWidget:fv withBoolean:NO];
  int len = (items == nil) ? 0 : [items size];
  if (len == 0) {
    return;
  }
  JavaUtilArrayList *othersList = [[JavaUtilArrayList alloc] init];
  JavaUtilArrayList *physiciansList = [[JavaUtilArrayList alloc] init];
  JavaUtilArrayList *users = [[JavaUtilArrayList alloc] initWithInt:len];
  RARERenderableDataItem *row, *item;
  JavaLangStringBuilder *sb = [[JavaLangStringBuilder alloc] init];
  NSString *s;
  RAREUTJSONObject *user = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"user"], [RAREUTJSONObject class]);
  NSString *me = [((RAREUTJSONObject *) nil_chk(user)) optStringWithNSString:@"xmppid" withNSString:@""];
  BOOL isme = NO;
  id<JavaUtilIterator> it = [((id<JavaUtilCollection>) nil_chk(items)) iterator];
  while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
    row = [it next];
    if ([((RARERenderableDataItem *) nil_chk(row)) size] < 4) {
      continue;
    }
    BOOL small = [RAREUIScreen isSmallScreen];
    item = [((RARERenderableDataItem *) nil_chk([row getWithInt:2])) copy__];
    [sb setLengthWithInt:0];
    (void) [((JavaLangStringBuilder *) nil_chk([sb appendWithNSString:@"<html>"])) appendWithNSString:(NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([row getWithInt:3])) getValue], [NSString class])];
    (void) [sb appendWithNSString:small ? @"<br/>- " : @" - "];
    (void) [sb appendWithNSString:(NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getValue], [NSString class])];
    if (!isme) {
      isme = [((NSString *) nil_chk(me)) isEqual:[((RARERenderableDataItem *) nil_chk([row getWithInt:0])) getValue]];
      if (isme) {
        (void) [sb appendWithNSString:@" (Me) "];
      }
    }
    (void) [sb appendWithNSString:@"</html>"];
    s = [sb description];
    [item setValueWithId:s];
    [item setLinkedDataWithId:row];
    s = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([row getWithInt:4])) getValue], [NSString class]);
    if ([@"true" equalsIgnoreCase:s]) {
      [physiciansList addWithId:item];
    }
    else {
      [othersList addWithId:item];
    }
    s = commHandler_ == nil ? nil : [self getUserIDWithRARERenderableDataItem:row];
    if (s == nil) {
      continue;
    }
    [item setLinkedDataWithId:s];
    [users addWithId:s];
  }
  RAREaListViewer *physicians = (RAREaListViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:@"physicians"], [RAREaListViewer class]);
  RAREaListViewer *others = (RAREaListViewer *) check_class_cast([fv getWidgetWithNSString:@"others"], [RAREaListViewer class]);
  if (physicians != nil) {
    [physicians setAllWithJavaUtilCollection:physiciansList];
  }
  if (others != nil) {
    [others setAllWithJavaUtilCollection:othersList];
  }
  if (![users isEmpty]) {
    [((CCPBVaCommunicationHandler *) nil_chk(commHandler_)) addStatusListenerWithJavaUtilList:users withCCPBVaCommunicationHandler_iStatusListener:self];
  }
}

- (void)onListShownWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event {
  listViewer_ = (RAREListBoxViewer *) check_class_cast(widget, [RAREListBoxViewer class]);
}

- (void)onListChangeWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (commHandler_ != nil) {
    id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
    RARERenderableDataItem *item = [((RAREListBoxViewer *) check_class_cast(widget, [RAREListBoxViewer class])) getSelectedItem];
    NSString *id_ = item == nil ? nil : (NSString *) check_class_cast([item getLinkedData], [NSString class]);
    CCPBVaCommunicationHandler_UserStatus *status = id_ == nil ? CCPBVCareTeam_OFFLINE_ : [((id<JavaUtilMap>) nil_chk(userStatuses_)) getWithId:id_];
    if (status == nil) {
      status = CCPBVCareTeam_OFFLINE_;
    }
    RAREPushButtonWidget *pb = (RAREPushButtonWidget *) check_class_cast([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:@"bv.action.video_chat"], [RAREPushButtonWidget class]);
    if (pb != nil) {
      [pb setEnabledWithBoolean:[commHandler_ isVideoChatAvailable] && ((CCPBVaCommunicationHandler_UserStatus *) nil_chk(status))->videoChat_ != [CCPBVaCommunicationHandler_StatusEnum ONLINE]];
    }
    pb = (RAREPushButtonWidget *) check_class_cast([fv getWidgetWithNSString:@"bv.action.audio_chat"], [RAREPushButtonWidget class]);
    if (pb != nil) {
      [pb setEnabledWithBoolean:[commHandler_ isVideoChatAvailable] && ((CCPBVaCommunicationHandler_UserStatus *) nil_chk(status))->audioChat_ != [CCPBVaCommunicationHandler_StatusEnum ONLINE]];
    }
    pb = (RAREPushButtonWidget *) check_class_cast([fv getWidgetWithNSString:@"bv.action.text_chat"], [RAREPushButtonWidget class]);
    if (pb != nil) {
      [pb setEnabledWithBoolean:[commHandler_ isVideoChatAvailable] && ((CCPBVaCommunicationHandler_UserStatus *) nil_chk(status))->textChat_ != [CCPBVaCommunicationHandler_StatusEnum ONLINE]];
    }
  }
}

- (void)serviceAvailbilityChangedWithCCPBVaCommunicationHandler:(CCPBVaCommunicationHandler *)handler {
  if (listViewer_ != nil) {
    [self onListChangeWithNSString:[RAREiConstants EVENT_CHANGE] withRAREiWidget:listViewer_ withJavaUtilEventObject:nil];
  }
}

- (void)statusChangedWithCCPBVaCommunicationHandler:(CCPBVaCommunicationHandler *)handler
                                       withNSString:(NSString *)user
          withCCPBVaCommunicationHandler_UserStatus:(CCPBVaCommunicationHandler_UserStatus *)status {
  (void) [((id<JavaUtilMap>) nil_chk(userStatuses_)) putWithId:user withId:status];
  if (listViewer_ != nil) {
    RAREListBoxViewer *lv = listViewer_;
    int len = [lv size];
    for (int i = 0; i < len; i++) {
      NSString *id_ = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([lv getWithInt:i])) getLinkedData], [NSString class]);
      if (id_ != nil && [id_ isEqual:user]) {
        RARERenderableDataItem *item = [lv getWithInt:i];
        [((RARERenderableDataItem *) nil_chk(item)) setIconWithRAREiPlatformIcon:[((id<JavaUtilMap>) nil_chk(statusIcons_)) getWithId:status]];
        [lv repaintRowWithInt:i];
        if ([lv isRowSelectedWithInt:i]) {
          [self onListChangeWithNSString:[RAREiConstants EVENT_CHANGE] withRAREiWidget:listViewer_ withJavaUtilEventObject:nil];
        }
        break;
      }
    }
  }
}

+ (void)initialize {
  if (self == [CCPBVCareTeam class]) {
    CCPBVCareTeam_OFFLINE_ = [[CCPBVaCommunicationHandler_UserStatus alloc] init];
  }
}

- (void)copyAllFieldsTo:(CCPBVCareTeam *)other {
  [super copyAllFieldsTo:other];
  other->commHandler_ = commHandler_;
  other->dataTimeout_ = dataTimeout_;
  other->idPosition_ = idPosition_;
  other->lastUpdateTime_ = lastUpdateTime_;
  other->listViewer_ = listViewer_;
  other->statusIcons_ = statusIcons_;
  other->userStatuses_ = userStatuses_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getUserIDWithRARERenderableDataItem:", NULL, "LNSString", 0x4, NULL },
    { "updateFormWithRAREiFormViewer:withRAREiDataCollection:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "commHandler_", NULL, 0x4, "LCCPBVaCommunicationHandler" },
    { "userStatuses_", NULL, 0x4, "LJavaUtilMap" },
    { "listViewer_", NULL, 0x4, "LRAREListBoxViewer" },
    { "OFFLINE_", NULL, 0xc, "LCCPBVaCommunicationHandler_UserStatus" },
    { "statusIcons_", NULL, 0x4, "LJavaUtilMap" },
    { "idPosition_", NULL, 0x4, "I" },
    { "lastUpdateTime_", NULL, 0x4, "J" },
    { "dataTimeout_", NULL, 0x4, "I" },
  };
  static J2ObjcClassInfo _CCPBVCareTeam = { "CareTeam", "com.sparseware.bellavista", NULL, 0x1, 2, methods, 8, fields, 0, NULL};
  return &_CCPBVCareTeam;
}

@end
@implementation CCPBVCareTeam_$1

- (void)finishWithId:(id)result {
  [((RAREWindowViewer *) nil_chk(val$w_)) hideWaitCursor];
  if (result != nil) {
    [CCPBVUtils handleErrorWithJavaLangThrowable:(JavaLangThrowable *) check_class_cast(result, [JavaLangThrowable class])];
  }
  else {
    [this$0_ updateFormWithRAREiFormViewer:[((id<RAREiWidget>) nil_chk(val$widget_)) getFormViewer] withRAREiDataCollection:val$dc_];
  }
}

- (id)compute {
  @try {
    [((id<RAREiDataCollection>) nil_chk(val$dc_)) refreshWithRAREiWidget:val$widget_];
    this$0_->lastUpdateTime_ = [JavaLangSystem currentTimeMillis];
    return nil;
  }
  @catch (JavaLangException *e) {
    return e;
  }
}

- (id)initWithCCPBVCareTeam:(CCPBVCareTeam *)outer$
       withRAREWindowViewer:(RAREWindowViewer *)capture$0
            withRAREiWidget:(id<RAREiWidget>)capture$1
    withRAREiDataCollection:(id<RAREiDataCollection>)capture$2 {
  this$0_ = outer$;
  val$w_ = capture$0;
  val$widget_ = capture$1;
  val$dc_ = capture$2;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "compute", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCareTeam" },
    { "val$w_", NULL, 0x1012, "LRAREWindowViewer" },
    { "val$widget_", NULL, 0x1012, "LRAREiWidget" },
    { "val$dc_", NULL, 0x1012, "LRAREiDataCollection" },
  };
  static J2ObjcClassInfo _CCPBVCareTeam_$1 = { "$1", "com.sparseware.bellavista", "CareTeam", 0x8000, 1, methods, 4, fields, 0, NULL};
  return &_CCPBVCareTeam_$1;
}

@end
