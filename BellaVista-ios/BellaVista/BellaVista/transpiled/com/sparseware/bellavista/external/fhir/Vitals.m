//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/Vitals.java
//
//  Created by decoteaud on 11/18/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/util/CharArray.h"
#include "com/appnativa/util/SNumber.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/appnativa/util/json/JSONWriter.h"
#include "com/sparseware/bellavista/ActionPath.h"
#include "com/sparseware/bellavista/external/fhir/FHIRServer.h"
#include "com/sparseware/bellavista/external/fhir/FHIRUtils.h"
#include "com/sparseware/bellavista/external/fhir/Vitals.h"
#include "com/sparseware/bellavista/external/fhir/aFHIRemoteService.h"
#include "com/sparseware/bellavista/service/ContentWriter.h"
#include "com/sparseware/bellavista/service/HttpHeaders.h"
#include "com/sparseware/bellavista/service/aRemoteService.h"
#include "com/sparseware/bellavista/service/iHttpConnection.h"
#include "java/io/IOException.h"
#include "java/io/InputStream.h"
#include "java/io/Reader.h"
#include "java/io/Writer.h"
#include "java/util/ArrayList.h"
#include "java/util/Calendar.h"
#include "java/util/Collection.h"
#include "java/util/HashMap.h"
#include "java/util/HashSet.h"
#include "java/util/List.h"
#include "java/util/Map.h"

@implementation ComSparsewareBellavistaExternalFhirVitals

static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirVitals_bpCodes_;
static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirVitals_bpSysCodes_;
static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirVitals_bpDysCodes_;
static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirVitals_bpComponents_;
static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirVitals_weightCodes_;
static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirVitals_heightCodes_;
static JavaUtilHashMap * ComSparsewareBellavistaExternalFhirVitals_vitalCodes_;

+ (JavaUtilHashSet *)bpCodes {
  return ComSparsewareBellavistaExternalFhirVitals_bpCodes_;
}

+ (void)setBpCodes:(JavaUtilHashSet *)bpCodes {
  ComSparsewareBellavistaExternalFhirVitals_bpCodes_ = bpCodes;
}

+ (JavaUtilHashSet *)bpSysCodes {
  return ComSparsewareBellavistaExternalFhirVitals_bpSysCodes_;
}

+ (void)setBpSysCodes:(JavaUtilHashSet *)bpSysCodes {
  ComSparsewareBellavistaExternalFhirVitals_bpSysCodes_ = bpSysCodes;
}

+ (JavaUtilHashSet *)bpDysCodes {
  return ComSparsewareBellavistaExternalFhirVitals_bpDysCodes_;
}

+ (void)setBpDysCodes:(JavaUtilHashSet *)bpDysCodes {
  ComSparsewareBellavistaExternalFhirVitals_bpDysCodes_ = bpDysCodes;
}

+ (JavaUtilHashSet *)bpComponents {
  return ComSparsewareBellavistaExternalFhirVitals_bpComponents_;
}

+ (void)setBpComponents:(JavaUtilHashSet *)bpComponents {
  ComSparsewareBellavistaExternalFhirVitals_bpComponents_ = bpComponents;
}

+ (JavaUtilHashSet *)weightCodes {
  return ComSparsewareBellavistaExternalFhirVitals_weightCodes_;
}

+ (void)setWeightCodes:(JavaUtilHashSet *)weightCodes {
  ComSparsewareBellavistaExternalFhirVitals_weightCodes_ = weightCodes;
}

+ (JavaUtilHashSet *)heightCodes {
  return ComSparsewareBellavistaExternalFhirVitals_heightCodes_;
}

+ (void)setHeightCodes:(JavaUtilHashSet *)heightCodes {
  ComSparsewareBellavistaExternalFhirVitals_heightCodes_ = heightCodes;
}

+ (JavaUtilHashMap *)vitalCodes {
  return ComSparsewareBellavistaExternalFhirVitals_vitalCodes_;
}

+ (void)setVitalCodes:(JavaUtilHashMap *)vitalCodes {
  ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ = vitalCodes;
}

- (id)init {
  if (self = [super initWithNSString:@"Observation"]) {
    columnNames_ = [IOSObjectArray arrayWithObjects:(id[]){ @"date", @"vital", @"result", @"unit", @"range", @"sort_order", @"result_id" } count:7 type:[IOSClass classWithClass:[NSString class]]];
    ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource *r = [((ComSparsewareBellavistaExternalFhirFHIRServer *) nil_chk([ComSparsewareBellavistaExternalFhirFHIRServer getInstance])) getResourceWithNSString:@"Observation"];
    RAREUTJSONArray *a = (r == nil) ? nil : r->searchParams_;
    searchByDateSupported_ = (a == nil) ? NO : [a findJSONObjectIndexWithNSString:@"name" withNSString:@"appliesDateTime" withInt:0] != -1;
  }
  return self;
}

- (void)listWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                 withCCPBVActionPath:(CCPBVActionPath *)path
               withJavaIoInputStream:(JavaIoInputStream *)data
                withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self listWithCCPBViHttpConnection:conn withCCPBVActionPath:path withJavaIoInputStream:data withCCPBVHttpHeaders:headers withInt:-1];
}

- (void)listWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                 withCCPBVActionPath:(CCPBVActionPath *)path
               withJavaIoInputStream:(JavaIoInputStream *)data
                withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
                             withInt:(int)days {
  NSString *from = nil;
  if ((days > 0) && (searchByDateSupported_ == YES)) {
    JavaUtilCalendar *c = [JavaUtilCalendar getInstance];
    [c setWithInt:JavaUtilCalendar_DAY_OF_MONTH withInt:[((JavaUtilCalendar *) nil_chk(c)) getWithInt:JavaUtilCalendar_DAY_OF_MONTH] - days];
    [c setWithInt:JavaUtilCalendar_HOUR_OF_DAY withInt:0];
    from = [RAREFunctions convertDateTimeWithRAREiWidget:nil withId:c withBoolean:NO];
  }
  RAREActionLink *l;
  if (from == nil) {
    l = [self createSearchLinkWithNSStringArray:[IOSObjectArray arrayWithObjects:(id[]){ @"patient", [((ComSparsewareBellavistaExternalFhirFHIRServer *) nil_chk(server_)) getPatientID] } count:2 type:[IOSClass classWithClass:[NSString class]]]];
  }
  else {
    l = [self createSearchLinkWithNSStringArray:[IOSObjectArray arrayWithObjects:(id[]){ @"patient", [((ComSparsewareBellavistaExternalFhirFHIRServer *) nil_chk(server_)) getPatientID], @"appliesDateTime", from } count:4 type:[IOSClass classWithClass:[NSString class]]]];
  }
  @try {
    id w = [ComSparsewareBellavistaExternalFhirFHIRUtils createWriterWithCCPBVActionPath:path withCCPBVContentWriter:[((id<CCPBViHttpConnection>) nil_chk(conn)) getContentWriter] withCCPBVHttpHeaders:headers withBoolean:YES];
    [self searchWithJavaIoReader:[((RAREActionLink *) nil_chk(l)) getReader] withId:w withCCPBVHttpHeaders:headers withNSObjectArray:[IOSObjectArray arrayWithLength:0 type:[IOSClass classWithClass:[NSObject class]]]];
  }
  @finally {
    [((RAREActionLink *) nil_chk(l)) close];
  }
}

- (void)searchWithJavaIoReader:(JavaIoReader *)r
                        withId:(id)writer
          withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
             withNSObjectArray:(IOSObjectArray *)params {
  JavaUtilHashMap *bpcomponents = [[JavaUtilHashMap alloc] init];
  JavaUtilArrayList *bps = [[JavaUtilArrayList alloc] init];
  [super searchWithJavaIoReader:r withId:writer withCCPBVHttpHeaders:headers withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ bps, bpcomponents } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
}

- (void)parsingCompleteWithRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                           withJavaIoWriter:(JavaIoWriter *)w
                        withRAREUTCharArray:(RAREUTCharArray *)ca
                          withNSObjectArray:(IOSObjectArray *)params {
  id<JavaUtilMap> bpcomponents = (JavaUtilHashMap *) check_class_cast(IOSObjectArray_Get(nil_chk(params), 1), [JavaUtilHashMap class]);
  for (RAREUTJSONObject * __strong o in nil_chk([((id<JavaUtilMap>) nil_chk(bpcomponents)) values])) {
    [self processEntryExWithRAREUTJSONObject:o withRAREUTJSONWriter:jw withJavaIoWriter:w withRAREUTCharArray:ca withJavaUtilList:nil withJavaUtilMap:nil];
  }
}

- (void)processEntryWithRAREUTJSONObject:(RAREUTJSONObject *)entry_
                    withRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                        withJavaIoWriter:(JavaIoWriter *)w
                     withRAREUTCharArray:(RAREUTCharArray *)ca
                       withNSObjectArray:(IOSObjectArray *)params {
  id<JavaUtilList> bps = (id<JavaUtilList>) check_protocol_cast(IOSObjectArray_Get(nil_chk(params), 0), @protocol(JavaUtilList));
  id<JavaUtilMap> bpcomponents = (JavaUtilHashMap *) check_class_cast(IOSObjectArray_Get(params, 1), [JavaUtilHashMap class]);
  [self processEntryExWithRAREUTJSONObject:entry_ withRAREUTJSONWriter:jw withJavaIoWriter:w withRAREUTCharArray:ca withJavaUtilList:bps withJavaUtilMap:bpcomponents];
  int foundIndex = -1;
  int len = [((id<JavaUtilList>) nil_chk(bps)) size];
  for (int i = 0; i < len; i++) {
    ComSparsewareBellavistaExternalFhirVitals_BloodPressure *bp = [bps getWithInt:i];
    RAREUTJSONObject *o = [((ComSparsewareBellavistaExternalFhirVitals_BloodPressure *) nil_chk(bp)) updateEntryWithJavaUtilMap:bpcomponents withBoolean:NO];
    if (o != nil) {
      [self processEntryExWithRAREUTJSONObject:o withRAREUTJSONWriter:jw withJavaIoWriter:w withRAREUTCharArray:ca withJavaUtilList:nil withJavaUtilMap:nil];
      foundIndex = i;
      break;
    }
  }
  if (foundIndex != -1l) {
    (void) [bps removeWithInt:foundIndex];
  }
}

- (void)processEntryExWithRAREUTJSONObject:(RAREUTJSONObject *)entry_
                      withRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                          withJavaIoWriter:(JavaIoWriter *)w
                       withRAREUTCharArray:(RAREUTCharArray *)ca
                          withJavaUtilList:(id<JavaUtilList>)bps
                           withJavaUtilMap:(id<JavaUtilMap>)bpcomponents {
  RAREUTJSONObject *o;
  BOOL parsed = NO;
  do {
    NSString *dateld = nil;
    NSString *date = nil;
    NSString *vitalld = nil;
    NSString *vital = nil;
    NSString *resultld = nil;
    NSString *result = nil;
    NSString *unit = nil;
    NSString *range = nil;
    dateld = [self getIDWithRAREUTJSONObject:entry_];
    o = [((RAREUTJSONObject *) nil_chk(entry_)) getJSONObjectWithNSString:@"code"];
    RAREUTJSONArray *coding = [((RAREUTJSONObject *) nil_chk(o)) optJSONArrayWithNSString:@"coding"];
    ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *mc = [ComSparsewareBellavistaExternalFhirFHIRUtils getMedicalCodeWithRAREUTJSONArray:coding];
    if (mc == nil) {
      NSString *text = [o optStringWithNSString:@"text" withNSString:nil];
      if (text != nil) {
        mc = [[ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode alloc] initWithNSString:@"UNK" withNSString:nil withNSString:text withNSString:nil];
      }
      else {
        break;
      }
    }
    if (bpcomponents != nil && (ComSparsewareBellavistaExternalFhirVitals_bpComponents_ != nil) && [((ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *) nil_chk(mc)) isOneOfWithJavaUtilSet:ComSparsewareBellavistaExternalFhirVitals_bpComponents_]) {
      (void) [bpcomponents putWithId:dateld withId:entry_];
      parsed = YES;
      break;
    }
    if ((bps != nil) && [((ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *) nil_chk(mc)) isOneOfWithJavaUtilSet:ComSparsewareBellavistaExternalFhirVitals_bpCodes_]) {
      RAREUTJSONArray *a = [entry_ optJSONArrayWithNSString:@"related"];
      if ((a != nil) && ([a length] == 2)) {
        NSString *ref1 = [((RAREUTJSONObject *) nil_chk([((RAREUTJSONObject *) nil_chk([a getJSONObjectWithInt:0])) getJSONObjectWithNSString:@"target"])) getStringWithNSString:@"reference"];
        NSString *ref2 = [((RAREUTJSONObject *) nil_chk([((RAREUTJSONObject *) nil_chk([a getJSONObjectWithInt:1])) getJSONObjectWithNSString:@"target"])) getStringWithNSString:@"reference"];
        int n = [((NSString *) nil_chk(ref1)) lastIndexOf:'/'];
        if (n != -1) {
          ref1 = [ref1 substring:n + 1];
        }
        n = [((NSString *) nil_chk(ref2)) lastIndexOf:'/'];
        if (n != -1) {
          ref2 = [ref2 substring:n + 1];
        }
        RAREUTJSONObject *o1 = [((id<JavaUtilMap>) nil_chk(bpcomponents)) getWithId:ref1];
        RAREUTJSONObject *o2 = [bpcomponents getWithId:ref2];
        if ((o1 != nil) && (o2 != nil)) {
          (void) [entry_ putWithNSString:@"valueQuantity" withJavaUtilMap:[ComSparsewareBellavistaExternalFhirVitals_BloodPressure createValueWithRAREUTJSONObject:o1 withRAREUTJSONObject:o2]];
          (void) [bpcomponents removeWithId:ref1];
          (void) [bpcomponents removeWithId:ref2];
        }
        else {
          [bps addWithId:[[ComSparsewareBellavistaExternalFhirVitals_BloodPressure alloc] initWithRAREUTJSONObject:entry_ withNSString:ref1 withNSString:ref2]];
          break;
        }
      }
      else {
        break;
      }
    }
    date = [entry_ getStringWithNSString:@"appliesDateTime"];
    vital = [o getStringWithNSString:@"text"];
    vitalld = (mc == nil) ? @"" : [mc getBestCode];
    o = [entry_ optJSONObjectWithNSString:@"valueQuantity"];
    if (o != nil) {
      unit = [o optStringWithNSString:@"units" withNSString:nil];
      result = [o getStringWithNSString:@"value"];
      if ([@"cel" equalsIgnoreCase:[o optStringWithNSString:@"code"]]) {
        float f = [RAREFunctions floatValueWithNSString:result];
        if (f > 0) {
          f = f * 1.8f + 32.0f;
          RAREUTSNumber *num = [[RAREUTSNumber alloc] initWithDouble:f];
          (void) [num setScaleWithInt:2];
          result = [num description];
        }
        unit = @"DegF";
      }
    }
    else {
      result = [entry_ optStringWithNSString:@"valueString" withNSString:nil];
    }
    if (result == nil) {
      break;
    }
    o = [entry_ optJSONObjectWithNSString:@"interpretation"];
    if (o != nil) {
      resultld = [ComSparsewareBellavistaExternalFhirFHIRUtils getHL7FHIRCodeWithRAREUTJSONArray:[o optJSONArrayWithNSString:@"coding"]];
    }
    RAREUTJSONArray *a = [entry_ optJSONArrayWithNSString:@"referenceRange"];
    range = (a == nil) ? nil : [ComSparsewareBellavistaExternalFhirFHIRUtils getRangeWithRAREUTJSONArray:a withBoolean:YES withRAREUTCharArray:ca];
    NSString *s = [((JavaUtilHashMap *) nil_chk(ComSparsewareBellavistaExternalFhirVitals_vitalCodes_)) getWithId:vitalld];
    if (s != nil) {
      vitalld = s;
    }
    NSString *icolor = (resultld == nil) ? nil : [ComSparsewareBellavistaExternalFhirFHIRUtils getInterpretationColorWithNSString:resultld];
    if ((result != nil) && ((icolor != nil) || (resultld != nil))) {
      if (icolor != nil) {
        (void) [((RAREUTCharArray *) nil_chk([((RAREUTCharArray *) nil_chk([((RAREUTCharArray *) nil_chk(ca)) setWithNSString:@"{fgColor:"])) appendWithNSString:icolor])) appendWithNSString:@"}"];
      }
      (void) [((RAREUTCharArray *) nil_chk(ca)) appendWithNSString:result];
      if (resultld != nil) {
        (void) [((RAREUTCharArray *) nil_chk([((RAREUTCharArray *) nil_chk([ca appendWithNSString:@" ("])) appendWithNSString:resultld])) appendWithChar:')'];
      }
      result = [ca description];
    }
    if (jw != nil) {
      (void) [jw object];
      if (date != nil) {
        if (dateld != nil) {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"date"])) object];
          (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:dateld])) keyWithNSString:@"value"])) valueWithId:date];
          (void) [jw endObject];
        }
        else {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"date"])) valueWithId:date];
        }
      }
      if (vital != nil) {
        if (vitalld != nil) {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"vital"])) object];
          (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:vitalld])) keyWithNSString:@"value"])) valueWithId:vital];
          (void) [jw endObject];
        }
        else {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"vital"])) valueWithId:vital];
        }
      }
      if (result != nil) {
        if (resultld != nil) {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"result"])) object];
          (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:resultld])) keyWithNSString:@"value"])) valueWithId:result];
          (void) [jw endObject];
        }
        else {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"result"])) valueWithId:result];
        }
      }
      if (unit != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"unit"])) valueWithId:unit];
      }
      if (range != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"range"])) valueWithId:range];
      }
      (void) [jw endObject];
    }
    else {
      if (date != nil) {
        if (dateld != nil) {
          [((JavaIoWriter *) nil_chk(w)) writeWithNSString:dateld];
          [w writeWithInt:(unichar) '|'];
        }
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:date withRAREUTCharArray:ca];
      }
      [((JavaIoWriter *) nil_chk(w)) writeWithInt:(unichar) '^'];
      if (vital != nil) {
        if (vitalld != nil) {
          [w writeWithNSString:vitalld];
          [w writeWithInt:(unichar) '|'];
        }
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:vital withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      if (result != nil) {
        if (resultld != nil) {
          [w writeWithNSString:resultld];
          [w writeWithInt:(unichar) '|'];
        }
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:result withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      if (unit != nil) {
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:unit withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      if (range != nil) {
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:range withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) 0x000a];
    }
    parsed = YES;
  }
  while (NO);
  if (!parsed) {
    [RAREPlatform debugLogWithNSString:[NSString stringWithFormat:@"Could not parse entry:\n%@", [((RAREUTJSONObject *) nil_chk(entry_)) toStringWithInt:2]]];
  }
}

- (void)most_recentWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                        withCCPBVActionPath:(CCPBVActionPath *)path
                      withJavaIoInputStream:(JavaIoInputStream *)data
                       withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self listWithCCPBViHttpConnection:conn withCCPBVActionPath:path withJavaIoInputStream:data withCCPBVHttpHeaders:headers withInt:7];
}

- (void)realtimeWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                     withCCPBVActionPath:(CCPBVActionPath *)path
                   withJavaIoInputStream:(JavaIoInputStream *)data
                    withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self noDataWithCCPBViHttpConnection:conn withCCPBVActionPath:path withBoolean:YES withCCPBVHttpHeaders:headers];
}

- (void)summaryWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                    withCCPBVActionPath:(CCPBVActionPath *)path
                  withJavaIoInputStream:(JavaIoInputStream *)data
                   withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self listWithCCPBViHttpConnection:conn withCCPBVActionPath:path withJavaIoInputStream:data withCCPBVHttpHeaders:headers withInt:7];
}

+ (void)initialize {
  if (self == [ComSparsewareBellavistaExternalFhirVitals class]) {
    ComSparsewareBellavistaExternalFhirVitals_bpCodes_ = [[JavaUtilHashSet alloc] initWithInt:2];
    ComSparsewareBellavistaExternalFhirVitals_bpSysCodes_ = [[JavaUtilHashSet alloc] initWithInt:2];
    ComSparsewareBellavistaExternalFhirVitals_bpDysCodes_ = [[JavaUtilHashSet alloc] initWithInt:2];
    ComSparsewareBellavistaExternalFhirVitals_bpComponents_ = [[JavaUtilHashSet alloc] initWithInt:4];
    ComSparsewareBellavistaExternalFhirVitals_weightCodes_ = [[JavaUtilHashSet alloc] initWithInt:2];
    ComSparsewareBellavistaExternalFhirVitals_heightCodes_ = [[JavaUtilHashSet alloc] initWithInt:2];
    ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ = [[JavaUtilHashMap alloc] init];
    {
      [ComSparsewareBellavistaExternalFhirVitals_bpCodes_ addWithId:@"75367002"];
      [ComSparsewareBellavistaExternalFhirVitals_bpCodes_ addWithId:@"55284-4"];
      [ComSparsewareBellavistaExternalFhirVitals_bpSysCodes_ addWithId:@"271649006"];
      [ComSparsewareBellavistaExternalFhirVitals_bpSysCodes_ addWithId:@"8480-6"];
      [ComSparsewareBellavistaExternalFhirVitals_bpDysCodes_ addWithId:@"271650006"];
      [ComSparsewareBellavistaExternalFhirVitals_bpDysCodes_ addWithId:@"8462-4"];
      [ComSparsewareBellavistaExternalFhirVitals_bpComponents_ addWithId:@"271649006"];
      [ComSparsewareBellavistaExternalFhirVitals_bpComponents_ addWithId:@"8480-6"];
      [ComSparsewareBellavistaExternalFhirVitals_bpComponents_ addWithId:@"271650006"];
      [ComSparsewareBellavistaExternalFhirVitals_bpComponents_ addWithId:@"8462-4"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"75367002" withId:@"bp"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"55284-4" withId:@"bp"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"78564009" withId:@"pulse"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"8867-4" withId:@"pulse"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"415945006" withId:@"temp"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"8310-5" withId:@"temp"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"27113001" withId:@"wt"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"3141-9" withId:@"wt"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"50373000" withId:@"ht"];
      (void) [ComSparsewareBellavistaExternalFhirVitals_vitalCodes_ putWithId:@"8302-2" withId:@"ht"];
    }
  }
}

- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirVitals *)other {
  [super copyAllFieldsTo:other];
  other->searchByDateSupported_ = searchByDateSupported_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "listWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "listWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:withInt:", NULL, "V", 0x1, "JavaIoIOException" },
    { "searchWithJavaIoReader:withId:withCCPBVHttpHeaders:withNSObjectArray:", NULL, "V", 0x81, "JavaIoIOException" },
    { "parsingCompleteWithRAREUTJSONWriter:withJavaIoWriter:withRAREUTCharArray:withNSObjectArray:", NULL, "V", 0x84, "JavaIoIOException" },
    { "processEntryWithRAREUTJSONObject:withRAREUTJSONWriter:withJavaIoWriter:withRAREUTCharArray:withNSObjectArray:", NULL, "V", 0x84, "JavaIoIOException" },
    { "processEntryExWithRAREUTJSONObject:withRAREUTJSONWriter:withJavaIoWriter:withRAREUTCharArray:withJavaUtilList:withJavaUtilMap:", NULL, "V", 0x4, "JavaIoIOException" },
    { "most_recentWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "realtimeWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "summaryWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "searchByDateSupported_", NULL, 0x10, "Z" },
    { "bpCodes_", NULL, 0x8, "LJavaUtilHashSet" },
    { "bpSysCodes_", NULL, 0x8, "LJavaUtilHashSet" },
    { "bpDysCodes_", NULL, 0x8, "LJavaUtilHashSet" },
    { "bpComponents_", NULL, 0x8, "LJavaUtilHashSet" },
    { "weightCodes_", NULL, 0x8, "LJavaUtilHashSet" },
    { "heightCodes_", NULL, 0x8, "LJavaUtilHashSet" },
    { "vitalCodes_", NULL, 0x8, "LJavaUtilHashMap" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirVitals = { "Vitals", "com.sparseware.bellavista.external.fhir", NULL, 0x1, 9, methods, 8, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirVitals;
}

@end
@implementation ComSparsewareBellavistaExternalFhirVitals_BloodPressure

- (id)initWithRAREUTJSONObject:(RAREUTJSONObject *)entry_
                  withNSString:(NSString *)reference1
                  withNSString:(NSString *)reference2 {
  if (self = [super init]) {
    self->entry__ = entry_;
    self->reference1_ = reference1;
    self->reference2_ = reference2;
  }
  return self;
}

- (RAREUTJSONObject *)updateEntryWithJavaUtilMap:(id<JavaUtilMap>)components
                                     withBoolean:(BOOL)finalPass {
  RAREUTJSONObject *e1 = [((id<JavaUtilMap>) nil_chk(components)) getWithId:reference1_];
  RAREUTJSONObject *e2 = [components getWithId:reference2_];
  if (!finalPass && (e1 == nil || e2 == nil)) {
    return nil;
  }
  if ((e1 == nil) && (e2 == nil)) {
    [RAREPlatform debugLogWithNSString:[NSString stringWithFormat:@"Could not find references for BP:%@", [((RAREUTJSONObject *) nil_chk(entry__)) optStringWithNSString:@"id"]]];
    return nil;
  }
  if (e1 == nil) {
    [RAREPlatform debugLogWithNSString:[NSString stringWithFormat:@"Could not find reference: %@for BP:%@", reference1_, [((RAREUTJSONObject *) nil_chk(entry__)) optStringWithNSString:@"id"]]];
    return e2;
  }
  if (e2 == nil) {
    [RAREPlatform debugLogWithNSString:[NSString stringWithFormat:@"Could not find reference: %@for BP:%@", reference2_, [((RAREUTJSONObject *) nil_chk(entry__)) optStringWithNSString:@"id"]]];
    return e1;
  }
  (void) [components removeWithId:reference1_];
  (void) [components removeWithId:reference2_];
  (void) [((RAREUTJSONObject *) nil_chk(entry__)) putWithNSString:@"valueQuantity" withJavaUtilMap:[ComSparsewareBellavistaExternalFhirVitals_BloodPressure createValueWithRAREUTJSONObject:e1 withRAREUTJSONObject:e2]];
  return entry__;
}

+ (RAREUTJSONObject *)createValueWithRAREUTJSONObject:(RAREUTJSONObject *)e1
                                 withRAREUTJSONObject:(RAREUTJSONObject *)e2 {
  RAREUTJSONObject *o = [[RAREUTJSONObject alloc] init];
  RAREUTJSONObject *sys = nil;
  RAREUTJSONObject *dys = nil;
  RAREUTJSONArray *coding = [((RAREUTJSONObject *) nil_chk([((RAREUTJSONObject *) nil_chk(e1)) getJSONObjectWithNSString:@"code"])) optJSONArrayWithNSString:@"coding"];
  NSString *code = [ComSparsewareBellavistaExternalFhirFHIRUtils getBestMedicalCodeWithRAREUTJSONArray:coding];
  if ([((JavaUtilHashSet *) nil_chk([ComSparsewareBellavistaExternalFhirVitals bpSysCodes])) containsWithId:code]) {
    sys = e1;
  }
  else {
    dys = e1;
  }
  coding = [((RAREUTJSONObject *) nil_chk([((RAREUTJSONObject *) nil_chk(e2)) getJSONObjectWithNSString:@"code"])) optJSONArrayWithNSString:@"coding"];
  code = [ComSparsewareBellavistaExternalFhirFHIRUtils getBestMedicalCodeWithRAREUTJSONArray:coding];
  if ([((JavaUtilHashSet *) nil_chk([ComSparsewareBellavistaExternalFhirVitals bpDysCodes])) containsWithId:code]) {
    dys = e2;
  }
  else {
    sys = e2;
  }
  sys = [((RAREUTJSONObject *) nil_chk(sys)) getJSONObjectWithNSString:@"valueQuantity"];
  dys = [((RAREUTJSONObject *) nil_chk(dys)) getJSONObjectWithNSString:@"valueQuantity"];
  (void) [o putWithNSString:@"value" withId:[NSString stringWithFormat:@"%@/%@", [((RAREUTJSONObject *) nil_chk(sys)) optStringWithNSString:@"value"], [((RAREUTJSONObject *) nil_chk(dys)) optStringWithNSString:@"value"]]];
  return o;
}

- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirVitals_BloodPressure *)other {
  [super copyAllFieldsTo:other];
  other->entry__ = entry__;
  other->reference1_ = reference1_;
  other->reference2_ = reference2_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "updateEntryWithJavaUtilMap:withBoolean:", NULL, "LRAREUTJSONObject", 0x1, "JavaIoIOException" },
    { "createValueWithRAREUTJSONObject:withRAREUTJSONObject:", NULL, "LRAREUTJSONObject", 0x9, "JavaIoIOException" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "entry__", "entry", 0x1, "LRAREUTJSONObject" },
    { "reference1_", NULL, 0x1, "LNSString" },
    { "reference2_", NULL, 0x1, "LNSString" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirVitals_BloodPressure = { "BloodPressure", "com.sparseware.bellavista.external.fhir", "Vitals", 0x8, 2, methods, 3, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirVitals_BloodPressure;
}

@end
