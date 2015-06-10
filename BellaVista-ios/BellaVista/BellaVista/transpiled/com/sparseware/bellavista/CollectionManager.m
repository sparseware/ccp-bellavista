//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/CollectionManager.java
//
//  Created by decoteaud on 5/11/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iCancelableFuture.h"
#include "com/appnativa/rare/iConstants.h"
#include "com/appnativa/rare/iDataCollection.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIAction.h"
#include "com/appnativa/rare/ui/UIImageIcon.h"
#include "com/appnativa/rare/ui/UIScreen.h"
#include "com/appnativa/rare/ui/UISound.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/iCancelable.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/CollectionManager.h"
#include "com/sparseware/bellavista/Utils.h"
#include "java/lang/Boolean.h"
#include "java/lang/Exception.h"
#include "java/lang/Math.h"
#include "java/lang/System.h"
#include "java/util/ArrayList.h"
#include "java/util/Collection.h"
#include "java/util/HashMap.h"
#include "java/util/HashSet.h"
#include "java/util/Iterator.h"
#include "java/util/Map.h"
#include "java/util/Set.h"

@implementation CCPBVCollectionManager

static CCPBVCollectionManager * CCPBVCollectionManager_instance_;

+ (CCPBVCollectionManager *)instance {
  return CCPBVCollectionManager_instance_;
}

+ (void)setInstance:(CCPBVCollectionManager *)instance {
  CCPBVCollectionManager_instance_ = instance;
}

- (id)init {
  if (self = [super init]) {
    sounds_ = [[JavaUtilHashSet alloc] initWithInt:3];
    collections_ = [[JavaUtilHashMap alloc] init];
    CCPBVCollectionManager_instance_ = self;
    id<RAREiPlatformAppContext> app = [RAREPlatform getAppContext];
    id<RAREiDataCollection> dc;
    RAREWindowViewer *w = [RAREPlatform getWindowViewer];
    RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk(app)) getDataWithId:@"collectionsInfo"], [RAREUTJSONObject class]);
    RAREUTJSONArray *list = [((RAREUTJSONObject *) nil_chk(info)) getJSONArrayWithNSString:@"collections"];
    updateInterval_ = [JavaLangMath maxWithInt:[info optIntWithNSString:@"updateInterval" withInt:60] * 1000 withInt:5000];
    RAREUTJSONObject *csp = [((RAREUTJSONObject *) check_class_cast([app getDataWithId:@"user"], [RAREUTJSONObject class])) optJSONObjectWithNSString:@"site_parameters"];
    int keepalive = csp == nil ? 99999 : [csp optIntWithNSString:@"keepalive_interval" withInt:99999];
    keepalive *= 1000;
    if (keepalive < updateInterval_) {
      updateInterval_ = keepalive;
    }
    quarterTime_ = updateInterval_ / 4;
    JavaUtilHashMap *attributes = [[JavaUtilHashMap alloc] initWithInt:2];
    (void) [attributes putWithId:@"polling" withId:[JavaLangBoolean valueOfWithBoolean:YES]];
    int len = list == nil ? 0 : [list size];
    for (int i = 0; i < len; i++) {
      RAREUTJSONObject *o = [((RAREUTJSONArray *) nil_chk(list)) getJSONObjectWithInt:i];
      NSString *name = [((RAREUTJSONObject *) nil_chk(o)) getStringWithNSString:@"name"];
      NSString *url = [o getStringWithNSString:@"url"];
      BOOL autoUpdate = [o optBooleanWithNSString:@"autoUpdate" withBoolean:YES];
      RAREActionLink *link = [CCPBVUtils createLinkWithRAREiWidget:w withNSString:url withBoolean:NO];
      if (autoUpdate && ![CCPBVUtils isDemo]) {
        [((RAREActionLink *) nil_chk(link)) setAttributesWithJavaUtilMap:attributes];
      }
      dc = [((RAREWindowViewer *) nil_chk(w)) createDataCollectionWithRAREActionLink:link withRAREiFunctionCallback:nil];
      [((id<RAREiDataCollection>) nil_chk(dc)) setCollectionNameWithNSString:name];
      NSString *s = [o optStringWithNSString:@"noDataText" withNSString:nil];
      if (s != nil && [s sequenceLength] > 0) {
        s = [w expandStringWithNSString:s];
        [dc setEmptyCollectionTextWithJavaLangCharSequence:s];
      }
      [app registerDataCollectionWithRAREiDataCollection:dc];
      (void) [collections_ putWithId:name withId:o];
      (void) [o putWithNSString:@"_dc" withId:dc];
    }
    list = (RAREUTJSONArray *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"facilities"], [RAREUTJSONArray class]);
    len = list == nil ? 0 : [list size];
    JavaUtilArrayList *rows = [[JavaUtilArrayList alloc] initWithInt:len];
    for (int i = 0; i < len; i++) {
      NSString *s = [((RAREUTJSONArray *) nil_chk(list)) getStringWithInt:i];
      s = [((RAREWindowViewer *) nil_chk(w)) expandStringWithNSString:s];
      [rows addWithId:[[RARERenderableDataItem alloc] initWithId:s]];
    }
    dc = [((RAREWindowViewer *) nil_chk(w)) createDataCollectionWithRAREiWidget:w withJavaUtilList:rows];
    [((id<RAREiDataCollection>) nil_chk(dc)) setCollectionNameWithNSString:@"facilities"];
    [app registerDataCollectionWithRAREiDataCollection:dc];
    info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"patientSelectInfo"], [RAREUTJSONObject class]);
    list = (RAREUTJSONArray *) check_class_cast([((RAREUTJSONObject *) nil_chk(info)) getJSONArrayWithNSString:@"listCategories"], [RAREUTJSONArray class]);
    len = list == nil ? 0 : [list size];
    rows = [[JavaUtilArrayList alloc] initWithInt:len];
    id<RAREiPlatformIcon> listIcon = [app getResourceAsIconWithNSString:@"bv.icon.list"];
    BOOL tabular = [RAREUIScreen isLargeScreen];
    for (int i = 0; i < len; i++) {
      CCPBVCollectionManager_PatientList *pl = [[CCPBVCollectionManager_PatientList alloc] initWithRAREUTJSONObject:[((RAREUTJSONArray *) nil_chk(list)) getJSONObjectWithInt:i] withRAREWindowViewer:w];
      [rows addWithId:[pl createListItemWithBoolean:tabular withRAREiPlatformIcon:listIcon]];
    }
    dc = [w createDataCollectionWithRAREiWidget:w withJavaUtilList:rows];
    [((id<RAREiDataCollection>) nil_chk(dc)) setCollectionNameWithNSString:@"patientListCategories"];
    [app registerDataCollectionWithRAREiDataCollection:dc];
  }
  return self;
}

- (void)startPolling {
  paused_ = NO;
  if (!started_ && ![CCPBVUtils isDemo]) {
    started_ = YES;
    (void) [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) setTimeoutWithId:self withLong:updateInterval_];
  }
}

- (void)stopPolling {
  paused_ = YES;
}

- (void)updateUI {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  id<JavaUtilIterator> it = [((id<JavaUtilSet>) nil_chk([((JavaUtilHashMap *) nil_chk(collections_)) entrySet])) iterator];
  while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
    id<JavaUtilMap_Entry> e = [it next];
    NSString *name = [((id<JavaUtilMap_Entry>) nil_chk(e)) getKey];
    RAREUTJSONObject *o = [e getValue];
    [self updateUIWithRAREWindowViewer:w withNSString:name withRAREUTJSONObject:o withJavaUtilHashSet:nil];
  }
}

- (void)clear {
  id<JavaUtilIterator> it = [((id<JavaUtilCollection>) nil_chk([((JavaUtilHashMap *) nil_chk(collections_)) values])) iterator];
  while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
    RAREUTJSONObject *o = [it next];
    [((id<RAREiDataCollection>) check_protocol_cast([((RAREUTJSONObject *) nil_chk(o)) getWithId:@"_dc"], @protocol(RAREiDataCollection))) clearCollection];
  }
}

- (id<RAREiDataCollection>)getCollectionWithNSString:(NSString *)name {
  RAREUTJSONObject *o = [((JavaUtilHashMap *) nil_chk(collections_)) getWithId:name];
  return (id<RAREiDataCollection>) check_protocol_cast((o == nil ? nil : [o getWithId:@"_dc"]), @protocol(RAREiDataCollection));
}

- (void)refreshNow {
  lastUpdate_ = 0;
  if ([RAREPlatform isUIThread]) {
    [self run];
  }
  else {
    [self update];
  }
}

- (void)run {
  long long int time = [JavaLangSystem currentTimeMillis];
  if (lastUpdate_ + quarterTime_ < time) {
    (void) [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) executeBackgroundTaskWithJavaLangRunnable:[[CCPBVCollectionManager_$1 alloc] initWithCCPBVCollectionManager:self]];
  }
}

- (void)update {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  BOOL polled = NO;
  if (!paused_ && [CCPBVUtils continuePollingForUpdates]) {
    id<JavaUtilIterator> it = [((id<JavaUtilSet>) nil_chk([((JavaUtilHashMap *) nil_chk(collections_)) entrySet])) iterator];
    while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
      id<JavaUtilMap_Entry> e = [it next];
      NSString *name = [((id<JavaUtilMap_Entry>) nil_chk(e)) getKey];
      RAREUTJSONObject *o = [e getValue];
      if ([self updateCollectionWithRAREWindowViewer:w withNSString:name withRAREUTJSONObject:o]) {
        polled = YES;
      }
    }
    [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVCollectionManager_$2 alloc] initWithCCPBVCollectionManager:self withRAREWindowViewer:w]];
  }
  if (![((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) isShuttingDown]) {
    if (!polled && [CCPBVUtils continuePollingForUpdates]) {
      @try {
        RAREActionLink *l = [((RAREWindowViewer *) nil_chk(w)) createActionLinkWithId:@"/hub/main/account/status"];
        RAREUTJSONObject *o = [[RAREUTJSONObject alloc] initWithNSString:[((RAREActionLink *) nil_chk(l)) getContentAsString]];
        [CCPBVUtils handleStatusObjectWithRAREUTJSONObject:o];
      }
      @catch (JavaLangException *e) {
      }
    }
    (void) [((RAREWindowViewer *) nil_chk(w)) setTimeoutWithId:self withLong:updateInterval_];
  }
}

- (BOOL)updateCollectionWithRAREWindowViewer:(RAREWindowViewer *)w
                                withNSString:(NSString *)name
                        withRAREUTJSONObject:(RAREUTJSONObject *)o {
  BOOL autoUpdate = [((RAREUTJSONObject *) nil_chk(o)) optBooleanWithNSString:@"autoUpdate" withBoolean:YES];
  @try {
    if (autoUpdate) {
      id<RAREiDataCollection> dc = (id<RAREiDataCollection>) check_protocol_cast([o getWithId:@"_dc"], @protocol(RAREiDataCollection));
      [((id<RAREiDataCollection>) nil_chk(dc)) refreshWithRAREiWidget:w];
      return YES;
    }
  }
  @catch (JavaLangException *e) {
    [CCPBVUtils handleErrorWithJavaLangThrowable:e];
  }
  return NO;
}

- (void)updateUIWithRAREWindowViewer:(RAREWindowViewer *)w
                        withNSString:(NSString *)name
                withRAREUTJSONObject:(RAREUTJSONObject *)o
                 withJavaUtilHashSet:(JavaUtilHashSet *)sounds {
  id<RAREiDataCollection> dc = (id<RAREiDataCollection>) check_protocol_cast([((RAREUTJSONObject *) nil_chk(o)) getWithId:@"_dc"], @protocol(RAREiDataCollection));
  int len = [((id<RAREiDataCollection>) nil_chk(dc)) size];
  int lc = [o optIntWithNSString:@"_lc" withInt:0];
  (void) [o putWithNSString:@"_lc" withInt:len];
  NSString *action = [o optStringWithNSString:@"action" withNSString:nil];
  if (action != nil) {
    RAREUIAction *a = [((RAREWindowViewer *) nil_chk(w)) getActionWithNSString:action];
    if (a != nil) {
      [a setEnabledWithBoolean:len > 0];
    }
    if (lc != len && sounds != nil) {
      NSString *sound = [o optStringWithNSString:@"sound" withNSString:nil];
      if (sound != nil && [sound sequenceLength] > 0 && [sounds addWithId:sound]) {
        @try {
          RAREUISound *sd = [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getSoundWithNSString:sound];
          if (sd != nil) {
            [sd play];
          }
        }
        @catch (JavaLangException *e) {
          [RAREPlatform ignoreExceptionWithNSString:nil withJavaLangThrowable:e];
        }
      }
    }
  }
}

+ (CCPBVCollectionManager *)getInstance {
  return CCPBVCollectionManager_instance_;
}

- (void)copyAllFieldsTo:(CCPBVCollectionManager *)other {
  [super copyAllFieldsTo:other];
  other->collections_ = collections_;
  other->lastUpdate_ = lastUpdate_;
  other->paused_ = paused_;
  other->quarterTime_ = quarterTime_;
  other->sounds_ = sounds_;
  other->started_ = started_;
  other->updateInterval_ = updateInterval_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getCollectionWithNSString:", NULL, "LRAREiDataCollection", 0x1, NULL },
    { "update", NULL, "V", 0x4, NULL },
    { "updateCollectionWithRAREWindowViewer:withNSString:withRAREUTJSONObject:", NULL, "Z", 0x4, NULL },
    { "updateUIWithRAREWindowViewer:withNSString:withRAREUTJSONObject:withJavaUtilHashSet:", NULL, "V", 0x4, NULL },
    { "getInstance", NULL, "LCCPBVCollectionManager", 0x9, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "updateInterval_", NULL, 0x0, "I" },
    { "lastUpdate_", NULL, 0x0, "J" },
    { "quarterTime_", NULL, 0x0, "I" },
    { "instance_", NULL, 0xa, "LCCPBVCollectionManager" },
    { "sounds_", NULL, 0x0, "LJavaUtilHashSet" },
    { "collections_", NULL, 0x0, "LJavaUtilHashMap" },
    { "paused_", NULL, 0x40, "Z" },
    { "started_", NULL, 0x0, "Z" },
  };
  static J2ObjcClassInfo _CCPBVCollectionManager = { "CollectionManager", "com.sparseware.bellavista", NULL, 0x1, 5, methods, 8, fields, 0, NULL};
  return &_CCPBVCollectionManager;
}

@end
@implementation CCPBVCollectionManager_PatientList

- (BOOL)isContainsPatients {
  return containPatients_;
}

- (RARERenderableDataItem *)createListItemWithBoolean:(BOOL)tabular
                                withRAREiPlatformIcon:(id<RAREiPlatformIcon>)listIcon {
  RARERenderableDataItem *item = [[RARERenderableDataItem alloc] initWithNSString:title_ withId:self withRAREiPlatformIcon:nil];
  if (containPatients_) {
    [item setIconWithRAREiPlatformIcon:listIcon];
  }
  if (tabular) {
    RARERenderableDataItem *row = [[RARERenderableDataItem alloc] init];
    [row addWithId:item];
    [row setLinkedDataWithId:self];
    item = row;
  }
  return item;
}

- (NSString *)getCollectionHREF {
  return [NSString stringWithFormat:@"%@%@", [RAREiConstants COLLECTION_PREFIX], name_];
}

- (NSString *)getListHREFWithNSString:(NSString *)id_ {
  return [NSString stringWithFormat:@"/hub/main/util/patients/%@/%@", patientsPath_, id_];
}

- (RAREActionLink *)getListLinkWithRAREiWidget:(id<RAREiWidget>)context
                                  withNSString:(NSString *)id_ {
  return [CCPBVUtils createLinkWithRAREiWidget:context withNSString:[self getListHREFWithNSString:id_] withBoolean:YES];
}

- (RAREActionLink *)getCollectionLinkWithRAREiWidget:(id<RAREiWidget>)context {
  return [CCPBVUtils createLinkWithRAREiWidget:context withNSString:[self getCollectionHREF] withBoolean:YES];
}

- (id)initWithRAREUTJSONObject:(RAREUTJSONObject *)list
          withRAREWindowViewer:(RAREWindowViewer *)w {
  if (self = [super init]) {
    name_ = [((RAREUTJSONObject *) nil_chk(list)) optStringWithNSString:@"name" withNSString:nil];
    title_ = [((RAREWindowViewer *) nil_chk(w)) expandStringWithNSString:[list getStringWithNSString:@"title"]];
    NSString *s = [list optStringWithNSString:@"titleFormat"];
    if (s != nil && [s sequenceLength] > 0) {
      s = [w expandStringWithNSString:s];
      title_ = [RAREFunctions formatWithNSString:s withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ title_ } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
    }
    s = [list getStringWithNSString:@"type"];
    containPatients_ = [((NSString *) nil_chk(s)) isEqual:@"patients"];
    listPath_ = [list optStringWithNSString:@"listPath"];
    patientsPath_ = [list optStringWithNSString:@"patientsPath"];
    RAREActionLink *link;
    if (containPatients_) {
      link = [CCPBVUtils createLinkWithRAREiWidget:w withNSString:[NSString stringWithFormat:@"/hub/main/util/patients/%@", patientsPath_] withBoolean:NO];
      collection_ = [w createDataCollectionWithNSString:name_ withRAREActionLink:link withBoolean:YES withRAREiFunctionCallback:nil];
    }
    else {
      link = [CCPBVUtils createLinkWithRAREiWidget:w withNSString:[NSString stringWithFormat:@"/hub/main/util/lists/%@", listPath_] withBoolean:NO];
      collection_ = [w createDataCollectionWithNSString:name_ withRAREActionLink:link withBoolean:NO withRAREiFunctionCallback:nil];
    }
    [((id<RAREiDataCollection>) nil_chk(collection_)) setRefreshOnURLConnectionWithBoolean:YES];
  }
  return self;
}

- (void)copyAllFieldsTo:(CCPBVCollectionManager_PatientList *)other {
  [super copyAllFieldsTo:other];
  other->collection_ = collection_;
  other->containPatients_ = containPatients_;
  other->listPath_ = listPath_;
  other->name_ = name_;
  other->patientsPath_ = patientsPath_;
  other->title_ = title_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "isContainsPatients", NULL, "Z", 0x1, NULL },
    { "createListItemWithBoolean:withRAREiPlatformIcon:", NULL, "LRARERenderableDataItem", 0x1, NULL },
    { "getCollectionHREF", NULL, "LNSString", 0x1, NULL },
    { "getListHREFWithNSString:", NULL, "LNSString", 0x1, NULL },
    { "getListLinkWithRAREiWidget:withNSString:", NULL, "LRAREActionLink", 0x1, NULL },
    { "getCollectionLinkWithRAREiWidget:", NULL, "LRAREActionLink", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "collection_", NULL, 0x1, "LRAREiDataCollection" },
    { "title_", NULL, 0x1, "LNSString" },
    { "listPath_", NULL, 0x1, "LNSString" },
    { "patientsPath_", NULL, 0x1, "LNSString" },
    { "containPatients_", NULL, 0x1, "Z" },
    { "name_", NULL, 0x1, "LNSString" },
  };
  static J2ObjcClassInfo _CCPBVCollectionManager_PatientList = { "PatientList", "com.sparseware.bellavista", "CollectionManager", 0x8, 6, methods, 6, fields, 0, NULL};
  return &_CCPBVCollectionManager_PatientList;
}

@end
@implementation CCPBVCollectionManager_$1

- (void)run {
  [this$0_ update];
}

- (id)initWithCCPBVCollectionManager:(CCPBVCollectionManager *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCollectionManager" },
  };
  static J2ObjcClassInfo _CCPBVCollectionManager_$1 = { "$1", "com.sparseware.bellavista", "CollectionManager", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVCollectionManager_$1;
}

@end
@implementation CCPBVCollectionManager_$2

- (void)run {
  if (!this$0_->paused_ && [CCPBVUtils continuePollingForUpdates]) {
    [((JavaUtilHashSet *) nil_chk(this$0_->sounds_)) clear];
    id<JavaUtilIterator> it = [((id<JavaUtilSet>) nil_chk([((JavaUtilHashMap *) nil_chk(this$0_->collections_)) entrySet])) iterator];
    while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
      id<JavaUtilMap_Entry> e = [it next];
      NSString *name = [((id<JavaUtilMap_Entry>) nil_chk(e)) getKey];
      RAREUTJSONObject *o = [e getValue];
      [this$0_ updateUIWithRAREWindowViewer:val$w_ withNSString:name withRAREUTJSONObject:o withJavaUtilHashSet:this$0_->sounds_];
    }
  }
}

- (id)initWithCCPBVCollectionManager:(CCPBVCollectionManager *)outer$
                withRAREWindowViewer:(RAREWindowViewer *)capture$0 {
  this$0_ = outer$;
  val$w_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVCollectionManager" },
    { "val$w_", NULL, 0x1012, "LRAREWindowViewer" },
  };
  static J2ObjcClassInfo _CCPBVCollectionManager_$2 = { "$2", "com.sparseware.bellavista", "CollectionManager", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVCollectionManager_$2;
}

@end
