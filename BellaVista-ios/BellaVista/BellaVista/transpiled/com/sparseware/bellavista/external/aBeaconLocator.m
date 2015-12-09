//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aBeaconLocator.java
//
//  Created by decoteaud on 11/18/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/exception/ApplicationException.h"
#include "com/appnativa/rare/iCancelableFuture.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/event/iChangeListener.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/util/SNumber.h"
#include "com/appnativa/util/StringCache.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/PatientSelect.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/external/aBeaconLocator.h"
#include "com/sparseware/bellavista/external/aBeaconLocatorSupport.h"
#include "com/sparseware/bellavista/external/aPatientLocator.h"
#include "java/lang/Exception.h"
#include "java/lang/StringBuilder.h"
#include "java/util/ArrayList.h"
#include "java/util/Collections.h"
#include "java/util/Comparator.h"
#include "java/util/EventObject.h"
#include "java/util/HashMap.h"
#include "java/util/Iterator.h"
#include "java/util/List.h"
#include "java/util/Set.h"
#include "java/util/regex/Matcher.h"
#include "java/util/regex/Pattern.h"

@implementation CCPBVaBeaconLocator

- (id)init {
  if (self = [super init]) {
    nearbyPatients_ = [[JavaUtilArrayList alloc] init];
    nearbyLocations_ = [[JavaUtilArrayList alloc] init];
    locatorSupport_ = [self createBeaconSupport];
    idPattern_ = [self createIDPattern];
    [((CCPBVaBeaconLocatorSupport *) nil_chk(locatorSupport_)) setChangeListenerWithRAREiChangeListener:self];
    if ([locatorSupport_ isAvailable]) {
      RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"patientSelectInfo"], [RAREUTJSONObject class]);
      info = [((RAREUTJSONObject *) nil_chk(info)) optJSONObjectWithNSString:@"beaconInfo"];
      if (info != nil) {
        locationBeacons_ = [self getBeaconsWithRAREUTJSONArray:[info optJSONArrayWithNSString:@"locationBeacons"]];
        patientBeacons_ = [self getBeaconsWithRAREUTJSONArray:[info optJSONArrayWithNSString:@"patientBeacons"]];
        locationsSignalColumn_ = [info optIntWithNSString:@"locationsSignalColumn" withInt:1];
      }
      if (locationBeacons_ != nil) {
        [locatorSupport_ setLocationBeaconsWithJavaUtilList:locationBeacons_];
      }
      if (patientBeacons_ != nil) {
        [locatorSupport_ setPatientBeaconsWithJavaUtilList:patientBeacons_];
      }
    }
  }
  return self;
}

- (void)dispose {
  [((CCPBVaBeaconLocatorSupport *) nil_chk(locatorSupport_)) dispose];
  locatorSupport_ = nil;
}

- (CCPBVaBeaconLocatorSupport *)createBeaconSupport {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (JavaUtilRegexPattern *)createIDPattern {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)getNearbyLocationsWithJavaUtilEventObject:(JavaUtilEventObject *)event
                        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  CCPBVaPatientLocator_LocatorChangeEvent *ce = (CCPBVaPatientLocator_LocatorChangeEvent *) check_class_cast(event, [CCPBVaPatientLocator_LocatorChangeEvent class]);
  JavaUtilHashMap *map = (JavaUtilHashMap *) check_class_cast([((CCPBVaPatientLocator_LocatorChangeEvent *) nil_chk(ce)) getData], [JavaUtilHashMap class]);
  if ((map != nil) && ![map isEmpty]) {
    [self resolveBeaconsWithNSString:@"/hub/main/util/lists/nearby_locations" withJavaUtilHashMap:map withJavaUtilList:nearbyLocations_ withInt:locationsSignalColumn_ withRAREiFunctionCallback:cb];
    return;
  }
  [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVaBeaconLocator_$1 alloc] initWithCCPBVaBeaconLocator:self withRAREiFunctionCallback:cb]];
}

- (void)getNearbyPatientsWithJavaUtilEventObject:(JavaUtilEventObject *)event
                       withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  CCPBVaPatientLocator_LocatorChangeEvent *ce = (CCPBVaPatientLocator_LocatorChangeEvent *) check_class_cast(event, [CCPBVaPatientLocator_LocatorChangeEvent class]);
  JavaUtilHashMap *map = (JavaUtilHashMap *) check_class_cast([((CCPBVaPatientLocator_LocatorChangeEvent *) nil_chk(ce)) getData], [JavaUtilHashMap class]);
  if ((map != nil) && ![map isEmpty]) {
    [self resolveBeaconsWithNSString:@"/hub/main/util/patients/nearby" withJavaUtilHashMap:map withJavaUtilList:nearbyPatients_ withInt:CCPBVPatientSelect_SIGNAL withRAREiFunctionCallback:cb];
    return;
  }
  id<JavaUtilList> patients = [self copyListWithJavaUtilList:nearbyPatients_];
  [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVaBeaconLocator_$2 alloc] initWithRAREiFunctionCallback:cb withJavaUtilList:patients]];
}

- (void)ignoreEventWithJavaUtilEventObject:(JavaUtilEventObject *)e {
}

- (BOOL)isNearbyLocationsSupported {
  return locationBeacons_ != nil;
}

- (BOOL)isNearbyPatientsSupported {
  return patientBeacons_ != nil;
}

- (void)startListeningForNearbyLocations {
  listeningForLocations_ = YES;
  [((CCPBVaBeaconLocatorSupport *) nil_chk(locatorSupport_)) startListeningForLocations];
}

- (void)startListeningForNearbyPatients {
  if ([((CCPBVaBeaconLocatorSupport *) nil_chk(locatorSupport_)) wasAccessDenied]) {
    [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVaBeaconLocator_$3 alloc] initWithCCPBVaBeaconLocator:self]];
  }
  else {
    listeningForPatients_ = YES;
    [locatorSupport_ startListeningForPatients];
  }
}

- (void)stateChangedWithJavaUtilEventObject:(JavaUtilEventObject *)e {
  CCPBVaPatientLocator_LocatorChangeEvent *ce = (CCPBVaPatientLocator_LocatorChangeEvent *) check_class_cast(e, [CCPBVaPatientLocator_LocatorChangeEvent class]);
  int col = 0;
  id<JavaUtilList> list = nil;
  switch ([[((CCPBVaPatientLocator_LocatorChangeEvent *) nil_chk(ce)) getChangeType] ordinal]) {
    case CCPBVaPatientLocator_LocatorChangeType_PATIENTS:
    if (listeningForPatients_ && (changeListener_ != nil)) {
      list = nearbyPatients_;
      col = CCPBVPatientSelect_SIGNAL;
    }
    break;
    case CCPBVaPatientLocator_LocatorChangeType_LOCATIONS:
    if (listeningForLocations_ && (changeListener_ != nil)) {
      list = nearbyLocations_;
      col = locationsSignalColumn_;
    }
    break;
    case CCPBVaPatientLocator_LocatorChangeType_ACCESS_DENIED:
    return;
    default:
    [self fireStactChangedWithCCPBVaPatientLocator_LocatorChangeEvent:ce];
    break;
  }
  if (list != nil) {
    id<JavaUtilList> beacons = (id<JavaUtilList>) check_protocol_cast([ce getData], @protocol(JavaUtilList));
    JavaUtilHashMap *map = [[JavaUtilHashMap alloc] initWithInt:[((id<JavaUtilList>) nil_chk(beacons)) size]];
    for (CCPBVaBeaconLocator_Beacon * __strong b in beacons) {
      if (((CCPBVaBeaconLocator_Beacon *) nil_chk(b))->proximity_ >= 0) {
        (void) [map putWithId:[b description] withId:b];
      }
    }
    [self synchronizeItemsWithBeaconsWithJavaUtilList:list withJavaUtilHashMap:map withInt:col];
    if ([map isEmpty]) {
      [self sortItemsAndUpdateWithJavaUtilList:list withInt:col];
    }
    CCPBVaPatientLocator_LocatorChangeEvent *lce = [[CCPBVaPatientLocator_LocatorChangeEvent alloc] initWithId:self withCCPBVaPatientLocator_LocatorChangeTypeEnum:(list == nearbyPatients_) ? [CCPBVaPatientLocator_LocatorChangeTypeEnum PATIENTS] : [CCPBVaPatientLocator_LocatorChangeTypeEnum LOCATIONS] withId:map];
    [self fireStactChangedWithCCPBVaPatientLocator_LocatorChangeEvent:lce];
  }
}

- (void)stopListeningForNearbyLocations {
  listeningForLocations_ = NO;
  [((CCPBVaBeaconLocatorSupport *) nil_chk(locatorSupport_)) stopListeningForLocations];
}

- (void)stopListeningForNearbyPatients {
  listeningForPatients_ = NO;
  [((CCPBVaBeaconLocatorSupport *) nil_chk(locatorSupport_)) stopListeningForPatients];
}

- (id<JavaUtilList>)copyListWithJavaUtilList:(id<JavaUtilList>)list {
  int len = [((id<JavaUtilList>) nil_chk(list)) size];
  JavaUtilArrayList *nlist = [[JavaUtilArrayList alloc] initWithInt:len];
  for (int i = 0; i < len; i++) {
    [nlist addWithId:[((RARERenderableDataItem *) nil_chk([list getWithInt:i])) copy__]];
  }
  return nlist;
}

- (void)fireStactChangedWithCCPBVaPatientLocator_LocatorChangeEvent:(CCPBVaPatientLocator_LocatorChangeEvent *)ce {
  if ([RAREPlatform isUIThread]) {
    if (changeListener_ != nil) {
      [changeListener_ stateChangedWithJavaUtilEventObject:ce];
    }
  }
  else {
    [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVaBeaconLocator_$4 alloc] initWithCCPBVaBeaconLocator:self withCCPBVaPatientLocator_LocatorChangeEvent:ce]];
  }
}

- (id<JavaUtilList>)getBeaconsWithRAREUTJSONArray:(RAREUTJSONArray *)array {
  int len = (array == nil) ? 0 : [array size];
  if (len == 0) {
    return nil;
  }
  JavaUtilArrayList *list = [[JavaUtilArrayList alloc] initWithInt:len];
  CCPBVaBeaconLocator_Beacon *b;
  for (int i = 0; i < len; i++) {
    NSString *s = [((RAREUTJSONArray *) nil_chk(array)) getStringWithInt:i];
    [list addWithId:b = [[CCPBVaBeaconLocator_Beacon alloc] initWithNSString:s]];
    if (![((JavaUtilRegexMatcher *) nil_chk([((JavaUtilRegexPattern *) nil_chk(idPattern_)) matcherWithJavaLangCharSequence:b->uuid_])) matches]) {
      @throw [[RAREApplicationException alloc] initWithNSString:[NSString stringWithFormat:@"Bad uuid:%@", b->uuid_]];
    }
  }
  return list;
}

- (void)removeBeaconsWithItemsWithJavaUtilList:(id<JavaUtilList>)items
                           withJavaUtilHashMap:(JavaUtilHashMap *)beacons
                                       withInt:(int)col {
  int len = [((id<JavaUtilList>) nil_chk(items)) size];
  for (int i = 0; i < len; i++) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk([items getWithInt:i])) getWithInt:col];
    NSString *id_ = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getLinkedData], [NSString class]);
    if (id_ != nil) {
      (void) [((JavaUtilHashMap *) nil_chk(beacons)) removeWithId:id_];
    }
  }
}

- (BOOL)removeFromListWithJavaUtilList:(id<JavaUtilList>)items
                   withJavaUtilHashMap:(JavaUtilHashMap *)beacons
                               withInt:(int)col {
  int len = [((id<JavaUtilList>) nil_chk(items)) size];
  BOOL removed = NO;
  for (int i = len - 1; i > -1; i--) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk([items getWithInt:i])) getWithInt:col];
    NSString *id_ = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getLinkedData], [NSString class]);
    if ((id_ != nil) && [((JavaUtilHashMap *) nil_chk(beacons)) containsKeyWithId:id_]) {
      (void) [items removeWithInt:i];
      removed = YES;
    }
  }
  return removed;
}

- (void)resolveBeaconsWithNSString:(NSString *)url
               withJavaUtilHashMap:(JavaUtilHashMap *)beacons
                  withJavaUtilList:(id<JavaUtilList>)list
                           withInt:(int)col
         withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RAREaWorkerTask *task = [[CCPBVaBeaconLocator_$5 alloc] initWithCCPBVaBeaconLocator:self withRAREWindowViewer:w withNSString:url withJavaUtilHashMap:beacons withJavaUtilList:list withInt:col withRAREiFunctionCallback:cb];
  (void) [((RAREWindowViewer *) nil_chk(w)) spawnWithNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ task } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
}

- (void)sortItemsAndUpdateWithJavaUtilList:(id<JavaUtilList>)items
                                   withInt:(int)col {
  id<JavaUtilComparator> c = [[CCPBVaBeaconLocator_$6 alloc] initWithInt:col];
  [JavaUtilCollections sortWithJavaUtilList:items withJavaUtilComparator:c];
  int len = [((id<JavaUtilList>) nil_chk(items)) size];
  for (int i = 0; i < len; i++) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk([items getWithInt:i])) getItemWithInt:col];
    [item setValueWithId:[RAREUTStringCache valueOfWithInt:[self getNumbersOfBarsWithInt:[((RARERenderableDataItem *) nil_chk(item)) getWidth]]]];
    [item setWidthWithInt:0];
  }
}

- (void)synchronizeItemsWithBeaconsWithJavaUtilList:(id<JavaUtilList>)items
                                withJavaUtilHashMap:(JavaUtilHashMap *)beacons
                                            withInt:(int)col {
  int len = [((id<JavaUtilList>) nil_chk(items)) size];
  for (int i = len - 1; i > -1; i--) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk([items getWithInt:i])) getWithInt:col];
    NSString *id_ = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getLinkedData], [NSString class]);
    CCPBVaBeaconLocator_Beacon *b = (id_ == nil) ? nil : [((JavaUtilHashMap *) nil_chk(beacons)) getWithId:id_];
    if (b == nil) {
      (void) [items removeWithInt:i];
    }
    else {
      [item setWidthWithInt:(int) (b->proximity_ * 1000)];
      (void) [((JavaUtilHashMap *) nil_chk(beacons)) removeWithId:id_];
    }
  }
}

- (void)updateProximityWithJavaUtilList:(id<JavaUtilList>)items
                    withJavaUtilHashMap:(JavaUtilHashMap *)beacons
                                withInt:(int)col {
  int len = [((id<JavaUtilList>) nil_chk(items)) size];
  for (int i = 0; i < len; i++) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk([items getWithInt:i])) getWithInt:col];
    NSString *id_ = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getLinkedData], [NSString class]);
    CCPBVaBeaconLocator_Beacon *b = (id_ == nil) ? nil : [((JavaUtilHashMap *) nil_chk(beacons)) getWithId:id_];
    if (b != nil) {
      [item setWidthWithInt:(int) (b->proximity_ * 1000)];
      (void) [((JavaUtilHashMap *) nil_chk(beacons)) removeWithId:id_];
    }
  }
}

- (int)getNumbersOfBarsWithInt:(int)proximity {
  if (proximity < 1000) {
    return 5;
  }
  if (proximity < 5000) {
    return 4;
  }
  if (proximity < 10000) {
    return 3;
  }
  if (proximity < 15000) {
    return 2;
  }
  return 1;
}

- (JavaUtilHashMap *)createBeaconMapWithJavaUtilList:(id<JavaUtilList>)beacons {
  JavaUtilHashMap *map = [[JavaUtilHashMap alloc] initWithInt:[((id<JavaUtilList>) nil_chk(beacons)) size]];
  for (CCPBVaBeaconLocator_Beacon * __strong b in beacons) {
    (void) [map putWithId:[((CCPBVaBeaconLocator_Beacon *) nil_chk(b)) description] withId:b];
  }
  return map;
}

- (void)updateBeaconIsForDemoWithJavaUtilHashMap:(JavaUtilHashMap *)beacons
                                withJavaUtilList:(id<JavaUtilList>)list
                                         withInt:(int)col {
  id<JavaUtilIterator> it = [((id<JavaUtilSet>) nil_chk([((JavaUtilHashMap *) nil_chk(beacons)) keySet])) iterator];
  int len = [((id<JavaUtilList>) nil_chk(list)) size];
  while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
    NSString *bid = [it next];
    int n = [((NSString *) nil_chk(bid)) lastIndexOf:';'];
    if (n != -1) {
      NSString *s = [bid substring:n + 1];
      for (int i = 0; i < len; i++) {
        RARERenderableDataItem *row = [list getWithInt:i];
        RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:0];
        if ([((RARERenderableDataItem *) nil_chk(item)) valueEqualsWithId:s]) {
          item = [row getWithInt:col];
          [((RARERenderableDataItem *) nil_chk(item)) setLinkedDataWithId:bid];
        }
      }
    }
  }
  for (int i = len - 1; i > -1; i--) {
    RARERenderableDataItem *row = [list getWithInt:i];
    if ([((RARERenderableDataItem *) nil_chk([((RARERenderableDataItem *) nil_chk(row)) getWithInt:col])) getLinkedData] == nil) {
      (void) [list removeWithInt:i];
    }
  }
}

- (void)copyAllFieldsTo:(CCPBVaBeaconLocator *)other {
  [super copyAllFieldsTo:other];
  other->idPattern_ = idPattern_;
  other->listeningForLocations_ = listeningForLocations_;
  other->listeningForPatients_ = listeningForPatients_;
  other->locationBeacons_ = locationBeacons_;
  other->locationsSignalColumn_ = locationsSignalColumn_;
  other->locatorSupport_ = locatorSupport_;
  other->nearbyLocations_ = nearbyLocations_;
  other->nearbyPatients_ = nearbyPatients_;
  other->patientBeacons_ = patientBeacons_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "createBeaconSupport", NULL, "LCCPBVaBeaconLocatorSupport", 0x404, NULL },
    { "createIDPattern", NULL, "LJavaUtilRegexPattern", 0x404, NULL },
    { "isNearbyLocationsSupported", NULL, "Z", 0x1, NULL },
    { "isNearbyPatientsSupported", NULL, "Z", 0x1, NULL },
    { "copyListWithJavaUtilList:", NULL, "LJavaUtilList", 0x4, NULL },
    { "fireStactChangedWithCCPBVaPatientLocator_LocatorChangeEvent:", NULL, "V", 0x4, NULL },
    { "getBeaconsWithRAREUTJSONArray:", NULL, "LJavaUtilList", 0x4, NULL },
    { "removeBeaconsWithItemsWithJavaUtilList:withJavaUtilHashMap:withInt:", NULL, "V", 0x4, NULL },
    { "removeFromListWithJavaUtilList:withJavaUtilHashMap:withInt:", NULL, "Z", 0x4, NULL },
    { "resolveBeaconsWithNSString:withJavaUtilHashMap:withJavaUtilList:withInt:withRAREiFunctionCallback:", NULL, "V", 0x4, NULL },
    { "sortItemsAndUpdateWithJavaUtilList:withInt:", NULL, "V", 0x4, NULL },
    { "synchronizeItemsWithBeaconsWithJavaUtilList:withJavaUtilHashMap:withInt:", NULL, "V", 0x4, NULL },
    { "updateProximityWithJavaUtilList:withJavaUtilHashMap:withInt:", NULL, "V", 0x4, NULL },
    { "getNumbersOfBarsWithInt:", NULL, "I", 0x4, NULL },
    { "createBeaconMapWithJavaUtilList:", NULL, "LJavaUtilHashMap", 0x4, NULL },
    { "updateBeaconIsForDemoWithJavaUtilHashMap:withJavaUtilList:withInt:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "locationBeacons_", NULL, 0x4, "LJavaUtilList" },
    { "patientBeacons_", NULL, 0x4, "LJavaUtilList" },
    { "locatorSupport_", NULL, 0x4, "LCCPBVaBeaconLocatorSupport" },
    { "listeningForPatients_", NULL, 0x4, "Z" },
    { "listeningForLocations_", NULL, 0x4, "Z" },
    { "nearbyPatients_", NULL, 0x4, "LJavaUtilArrayList" },
    { "nearbyLocations_", NULL, 0x4, "LJavaUtilArrayList" },
    { "locationsSignalColumn_", NULL, 0x4, "I" },
    { "idPattern_", NULL, 0x4, "LJavaUtilRegexPattern" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator = { "aBeaconLocator", "com.sparseware.bellavista.external", NULL, 0x401, 16, methods, 9, fields, 0, NULL};
  return &_CCPBVaBeaconLocator;
}

@end
@implementation CCPBVaBeaconLocator_Beacon

- (id)initWithNSString:(NSString *)bstring {
  if (self = [super init]) {
    int n = [((NSString *) nil_chk(bstring)) indexOf:';'];
    uuid_ = (n == -1) ? bstring : [bstring substring:0 endIndex:n];
    if (n != -1) {
      int p = [bstring indexOf:n + 1 fromIndex:';'];
      if (p != -1) {
        major_ = [RAREUTSNumber intValueWithNSString:[bstring substring:n + 1 endIndex:p]];
        minor_ = [RAREUTSNumber intValueWithNSString:[bstring substring:p + 1]];
      }
    }
  }
  return self;
}

- (id)initWithNSString:(NSString *)uuid
               withInt:(int)major
               withInt:(int)minor {
  if (self = [super init]) {
    self->uuid_ = uuid;
    self->major_ = major;
    self->minor_ = minor;
  }
  return self;
}

- (id)initWithNSString:(NSString *)uuid
               withInt:(int)major
               withInt:(int)minor
             withFloat:(float)proximity {
  if (self = [super init]) {
    self->uuid_ = uuid;
    self->major_ = major;
    self->minor_ = minor;
    self->proximity_ = proximity;
  }
  return self;
}

- (BOOL)isOneOfMineWithNSString:(NSString *)uuid
                        withInt:(int)major {
  if (self->major_ == 0) {
    major = 0;
  }
  return [((NSString *) nil_chk(self->uuid_)) isEqual:uuid] && (self->major_ == major);
}

- (NSString *)description {
  return [NSString stringWithFormat:@"%@;%d;%d", uuid_, major_, minor_];
}

- (void)copyAllFieldsTo:(CCPBVaBeaconLocator_Beacon *)other {
  [super copyAllFieldsTo:other];
  other->major_ = major_;
  other->minor_ = minor_;
  other->proximity_ = proximity_;
  other->uuid_ = uuid_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "isOneOfMineWithNSString:withInt:", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "uuid_", NULL, 0x1, "LNSString" },
    { "major_", NULL, 0x1, "I" },
    { "minor_", NULL, 0x1, "I" },
    { "proximity_", NULL, 0x1, "F" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator_Beacon = { "Beacon", "com.sparseware.bellavista.external", "aBeaconLocator", 0x9, 1, methods, 4, fields, 0, NULL};
  return &_CCPBVaBeaconLocator_Beacon;
}

@end
@implementation CCPBVaBeaconLocator_$1

- (void)run {
  [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:NO withId:[[JavaUtilArrayList alloc] initWithJavaUtilCollection:this$0_->nearbyLocations_]];
}

- (id)initWithCCPBVaBeaconLocator:(CCPBVaBeaconLocator *)outer$
        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0 {
  this$0_ = outer$;
  val$cb_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVaBeaconLocator" },
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator_$1 = { "$1", "com.sparseware.bellavista.external", "aBeaconLocator", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVaBeaconLocator_$1;
}

@end
@implementation CCPBVaBeaconLocator_$2

- (void)run {
  [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:NO withId:[[JavaUtilArrayList alloc] initWithJavaUtilCollection:val$patients_]];
}

- (id)initWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0
                   withJavaUtilList:(id<JavaUtilList>)capture$1 {
  val$cb_ = capture$0;
  val$patients_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
    { "val$patients_", NULL, 0x1012, "LJavaUtilList" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator_$2 = { "$2", "com.sparseware.bellavista.external", "aBeaconLocator", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVaBeaconLocator_$2;
}

@end
@implementation CCPBVaBeaconLocator_$3

- (void)run {
  [this$0_ fireStactChangedWithCCPBVaPatientLocator_LocatorChangeEvent:[[CCPBVaPatientLocator_LocatorChangeEvent alloc] initWithId:self withCCPBVaPatientLocator_LocatorChangeTypeEnum:[CCPBVaPatientLocator_LocatorChangeTypeEnum ACCESS_DENIED]]];
}

- (id)initWithCCPBVaBeaconLocator:(CCPBVaBeaconLocator *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVaBeaconLocator" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator_$3 = { "$3", "com.sparseware.bellavista.external", "aBeaconLocator", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVaBeaconLocator_$3;
}

@end
@implementation CCPBVaBeaconLocator_$4

- (void)run {
  if (this$0_->changeListener_ != nil) {
    [this$0_->changeListener_ stateChangedWithJavaUtilEventObject:val$ce_];
  }
}

- (id)initWithCCPBVaBeaconLocator:(CCPBVaBeaconLocator *)outer$
withCCPBVaPatientLocator_LocatorChangeEvent:(CCPBVaPatientLocator_LocatorChangeEvent *)capture$0 {
  this$0_ = outer$;
  val$ce_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVaBeaconLocator" },
    { "val$ce_", NULL, 0x1012, "LCCPBVaPatientLocator_LocatorChangeEvent" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator_$4 = { "$4", "com.sparseware.bellavista.external", "aBeaconLocator", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVaBeaconLocator_$4;
}

@end
@implementation CCPBVaBeaconLocator_$5

- (id)compute {
  @try {
    RAREActionLink *l = [CCPBVUtils createLinkWithRAREiWidget:val$w_ withNSString:val$url_ withBoolean:YES];
    if (![CCPBVUtils isDemo]) {
      JavaUtilHashMap *data = [[JavaUtilHashMap alloc] initWithInt:2];
      JavaLangStringBuilder *sb = [[JavaLangStringBuilder alloc] init];
      id<JavaUtilIterator> it = [((id<JavaUtilSet>) nil_chk([((JavaUtilHashMap *) nil_chk(val$beacons_)) keySet])) iterator];
      while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
        (void) [((JavaLangStringBuilder *) nil_chk([sb appendWithNSString:[it next]])) appendWithNSString:@"^"];
      }
      [sb setLengthWithInt:[sb sequenceLength] - 1];
      (void) [data putWithId:@"beacons" withId:[sb description]];
      (void) [((RAREActionLink *) nil_chk(l)) sendFormDataWithRAREiWidget:val$w_ withJavaUtilMap:data];
    }
    id<JavaUtilList> items = [((RAREWindowViewer *) nil_chk(val$w_)) parseDataLinkWithRAREActionLink:l withBoolean:YES];
    if (items != nil) {
      if ([CCPBVUtils isDemo] && (val$list_ == this$0_->nearbyPatients_)) {
        [this$0_ updateBeaconIsForDemoWithJavaUtilHashMap:val$beacons_ withJavaUtilList:items withInt:val$col_];
      }
      [this$0_ updateProximityWithJavaUtilList:items withJavaUtilHashMap:val$beacons_ withInt:val$col_];
      [((id<JavaUtilList>) nil_chk(val$list_)) addAllWithJavaUtilCollection:items];
      [this$0_ sortItemsAndUpdateWithJavaUtilList:val$list_ withInt:val$col_];
    }
    return [this$0_ copyListWithJavaUtilList:val$list_];
  }
  @catch (JavaLangException *e) {
    return e;
  }
}

- (void)finishWithId:(id)result {
  [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:[result isKindOfClass:[JavaLangException class]] withId:result];
}

- (id)initWithCCPBVaBeaconLocator:(CCPBVaBeaconLocator *)outer$
             withRAREWindowViewer:(RAREWindowViewer *)capture$0
                     withNSString:(NSString *)capture$1
              withJavaUtilHashMap:(JavaUtilHashMap *)capture$2
                 withJavaUtilList:(id<JavaUtilList>)capture$3
                          withInt:(int)capture$4
        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$5 {
  this$0_ = outer$;
  val$w_ = capture$0;
  val$url_ = capture$1;
  val$beacons_ = capture$2;
  val$list_ = capture$3;
  val$col_ = capture$4;
  val$cb_ = capture$5;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "compute", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVaBeaconLocator" },
    { "val$w_", NULL, 0x1012, "LRAREWindowViewer" },
    { "val$url_", NULL, 0x1012, "LNSString" },
    { "val$beacons_", NULL, 0x1012, "LJavaUtilHashMap" },
    { "val$list_", NULL, 0x1012, "LJavaUtilList" },
    { "val$col_", NULL, 0x1012, "I" },
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator_$5 = { "$5", "com.sparseware.bellavista.external", "aBeaconLocator", 0x8000, 1, methods, 7, fields, 0, NULL};
  return &_CCPBVaBeaconLocator_$5;
}

@end
@implementation CCPBVaBeaconLocator_$6

- (int)compareWithId:(RARERenderableDataItem *)o1
              withId:(RARERenderableDataItem *)o2 {
  o1 = [((RARERenderableDataItem *) nil_chk(o1)) getItemWithInt:val$col_];
  o2 = [((RARERenderableDataItem *) nil_chk(o2)) getItemWithInt:val$col_];
  return [((RARERenderableDataItem *) nil_chk(o1)) getWidth] - [((RARERenderableDataItem *) nil_chk(o2)) getWidth];
}

- (BOOL)isEqual:(id)param0 {
  return [super isEqual:param0];
}

- (id)initWithInt:(int)capture$0 {
  val$col_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$col_", NULL, 0x1012, "I" },
  };
  static J2ObjcClassInfo _CCPBVaBeaconLocator_$6 = { "$6", "com.sparseware.bellavista.external", "aBeaconLocator", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVaBeaconLocator_$6;
}

@end
