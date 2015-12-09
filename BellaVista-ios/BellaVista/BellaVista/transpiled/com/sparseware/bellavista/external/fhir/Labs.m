//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/Labs.java
//
//  Created by decoteaud on 11/18/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/util/CharArray.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/appnativa/util/json/JSONWriter.h"
#include "com/sparseware/bellavista/ActionPath.h"
#include "com/sparseware/bellavista/external/fhir/FHIRServer.h"
#include "com/sparseware/bellavista/external/fhir/FHIRUtils.h"
#include "com/sparseware/bellavista/external/fhir/Labs.h"
#include "com/sparseware/bellavista/external/fhir/aFHIRemoteService.h"
#include "com/sparseware/bellavista/service/ContentWriter.h"
#include "com/sparseware/bellavista/service/HttpHeaders.h"
#include "com/sparseware/bellavista/service/aRemoteService.h"
#include "com/sparseware/bellavista/service/iHttpConnection.h"
#include "java/io/IOException.h"
#include "java/io/InputStream.h"
#include "java/io/Reader.h"
#include "java/io/Writer.h"
#include "java/util/Calendar.h"
#include "java/util/HashMap.h"
#include "java/util/HashSet.h"

@implementation ComSparsewareBellavistaExternalFhirLabs

static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirLabs_bunCodes_;
static JavaUtilHashSet * ComSparsewareBellavistaExternalFhirLabs_creatinineCodes_;
static JavaUtilHashMap * ComSparsewareBellavistaExternalFhirLabs_categoryCodes_;

+ (JavaUtilHashSet *)bunCodes {
  return ComSparsewareBellavistaExternalFhirLabs_bunCodes_;
}

+ (void)setBunCodes:(JavaUtilHashSet *)bunCodes {
  ComSparsewareBellavistaExternalFhirLabs_bunCodes_ = bunCodes;
}

+ (JavaUtilHashSet *)creatinineCodes {
  return ComSparsewareBellavistaExternalFhirLabs_creatinineCodes_;
}

+ (void)setCreatinineCodes:(JavaUtilHashSet *)creatinineCodes {
  ComSparsewareBellavistaExternalFhirLabs_creatinineCodes_ = creatinineCodes;
}

+ (JavaUtilHashMap *)categoryCodes {
  return ComSparsewareBellavistaExternalFhirLabs_categoryCodes_;
}

+ (void)setCategoryCodes:(JavaUtilHashMap *)categoryCodes {
  ComSparsewareBellavistaExternalFhirLabs_categoryCodes_ = categoryCodes;
}

- (id)init {
  if (self = [super initWithNSString:@"DiagnosticReport"]) {
    columnNames_ = [IOSObjectArray arrayWithObjects:(id[]){ @"date", @"lab", @"result", @"unit", @"range", @"is_document", @"category", @"panel", @"sort_order", @"result_id", @"comment" } count:11 type:[IOSClass classWithClass:[NSString class]]];
    ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource *r = [((ComSparsewareBellavistaExternalFhirFHIRServer *) nil_chk([ComSparsewareBellavistaExternalFhirFHIRServer getInstance])) getResourceWithNSString:@"DiagnosticReport"];
    RAREUTJSONArray *a = (r == nil) ? nil : r->searchParams_;
    searchByDateSupported_ = (a == nil) ? NO : [a findJSONObjectIndexWithNSString:@"name" withNSString:@"issued" withInt:0] != -1;
  }
  return self;
}

- (void)documentWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                     withCCPBVActionPath:(CCPBVActionPath *)path
                   withJavaIoInputStream:(JavaIoInputStream *)data
                    withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self demoWithCCPBViHttpConnection:conn withCCPBVActionPath:path withJavaIoInputStream:data withCCPBVHttpHeaders:headers];
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

- (void)listWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                 withCCPBVActionPath:(CCPBVActionPath *)path
               withJavaIoInputStream:(JavaIoInputStream *)data
                withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self listWithCCPBViHttpConnection:conn withCCPBVActionPath:path withJavaIoInputStream:data withCCPBVHttpHeaders:headers withInt:-1];
}

- (void)testsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                  withCCPBVActionPath:(CCPBVActionPath *)path
                withJavaIoInputStream:(JavaIoInputStream *)data
                 withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self noDataWithCCPBViHttpConnection:conn withCCPBVActionPath:path withBoolean:NO withCCPBVHttpHeaders:headers];
}

- (void)listWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                 withCCPBVActionPath:(CCPBVActionPath *)path
               withJavaIoInputStream:(JavaIoInputStream *)data
                withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
                             withInt:(int)days {
  NSString *from = nil;
  if ([((ComSparsewareBellavistaExternalFhirFHIRServer *) nil_chk(server_)) isDemoMode]) {
    days = -1;
  }
  if ((days > 0) && (searchByDateSupported_ == YES)) {
    JavaUtilCalendar *c = [JavaUtilCalendar getInstance];
    [c setWithInt:JavaUtilCalendar_DAY_OF_MONTH withInt:[((JavaUtilCalendar *) nil_chk(c)) getWithInt:JavaUtilCalendar_DAY_OF_MONTH] - days];
    [c setWithInt:JavaUtilCalendar_HOUR_OF_DAY withInt:0];
    from = [RAREFunctions convertDateTimeWithRAREiWidget:nil withId:c withBoolean:NO];
  }
  RAREActionLink *l;
  if (from == nil) {
    l = [self createSearchLinkWithNSStringArray:[IOSObjectArray arrayWithObjects:(id[]){ @"patient", [server_ getPatientID] } count:2 type:[IOSClass classWithClass:[NSString class]]]];
  }
  else {
    l = [self createSearchLinkWithNSStringArray:[IOSObjectArray arrayWithObjects:(id[]){ @"patient", [server_ getPatientID], @"issued", from } count:4 type:[IOSClass classWithClass:[NSString class]]]];
  }
  @try {
    id w = [ComSparsewareBellavistaExternalFhirFHIRUtils createWriterWithCCPBVActionPath:path withCCPBVContentWriter:[((id<CCPBViHttpConnection>) nil_chk(conn)) getContentWriter] withCCPBVHttpHeaders:headers withBoolean:YES];
    [self searchWithJavaIoReader:[((RAREActionLink *) nil_chk(l)) getReader] withId:w withCCPBVHttpHeaders:headers withNSObjectArray:[IOSObjectArray arrayWithLength:0 type:[IOSClass classWithClass:[NSObject class]]]];
  }
  @finally {
    [((RAREActionLink *) nil_chk(l)) close];
  }
}

- (void)processEntryWithRAREUTJSONObject:(RAREUTJSONObject *)entry_
                    withRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                        withJavaIoWriter:(JavaIoWriter *)w
                     withRAREUTCharArray:(RAREUTCharArray *)ca
                       withNSObjectArray:(IOSObjectArray *)params {
  BOOL parsed = NO;
  do {
    if (![((NSString *) nil_chk([((RAREUTJSONObject *) nil_chk(entry_)) getStringWithNSString:@"resourceType"])) isEqual:resourceName_]) {
      return;
    }
    NSString *dateld = nil;
    NSString *date = nil;
    NSString *labld = nil;
    NSString *lab = nil;
    NSString *result = nil;
    NSString *is_document = @"true";
    NSString *categoryld = nil;
    NSString *category = nil;
    NSString *comment = nil;
    dateld = [self getIDWithRAREUTJSONObject:entry_];
    RAREUTJSONObject *name = [entry_ getJSONObjectWithNSString:@"name"];
    ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *mc = [ComSparsewareBellavistaExternalFhirFHIRUtils getMedicalCodeWithRAREUTJSONObject:name];
    if (mc == nil) {
      break;
    }
    result = [((NSString *) nil_chk([entry_ optStringWithNSString:@"conclusion"])) trim];
    if ([((NSString *) nil_chk(result)) sequenceLength] == 0) {
      result = [ComSparsewareBellavistaExternalFhirFHIRUtils getBestMedicalTextWithRAREUTJSONObject:[entry_ optJSONObjectWithNSString:@"codedDiagnosis"]];
    }
    date = [entry_ getStringWithNSString:@"issued"];
    ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *mc1 = [ComSparsewareBellavistaExternalFhirFHIRUtils getMedicalCodeWithRAREUTJSONObject:[entry_ optJSONObjectWithNSString:@"serviceCategory"]];
    if (mc1 != nil) {
      [mc1 resolveHL7DisplayWithJavaUtilHashMap:ComSparsewareBellavistaExternalFhirLabs_categoryCodes_];
      category = [mc1 getBestText];
      categoryld = [mc1 getBestCode];
    }
    lab = [((ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *) nil_chk(mc)) getBestText];
    labld = [mc getBestCode];
    RAREUTJSONArray *contained = [entry_ optJSONArrayWithNSString:@"contained"];
    if ((contained != nil) && ([contained length] > 0)) {
      int len = [contained length];
      for (int i = 0; i < len; i++) {
        [self processObservationEntryWithNSString:date withNSString:categoryld withNSString:category withNSString:labld withNSString:lab withRAREUTJSONObject:[contained getJSONObjectWithInt:i] withRAREUTJSONWriter:jw withJavaIoWriter:w withRAREUTCharArray:ca];
      }
      if (result == nil) {
        parsed = YES;
        break;
      }
    }
    if (result == nil) {
      break;
    }
    comment = [((NSString *) nil_chk([entry_ optStringWithNSString:@"comments"])) trim];
    comment = [ComSparsewareBellavistaExternalFhirFHIRUtils cleanAndEncodeStringWithNSString:comment];
    result = [((NSString *) nil_chk([RAREFunctions base64NOLFWithNSString:[CCPBVaRemoteService textToHTMLWithNSString:result]])) trim];
    if (jw != nil) {
      (void) [jw object];
      if (date != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"date"])) object];
        (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:dateld])) keyWithNSString:@"value"])) valueWithId:date];
        (void) [jw endObject];
      }
      if (lab != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"lab"])) object];
        (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:labld])) keyWithNSString:@"value"])) valueWithId:lab];
        (void) [jw endObject];
      }
      if (result != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"result"])) valueWithId:result];
      }
      if (is_document != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"is_document"])) valueWithId:is_document];
      }
      if (category != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"category"])) object];
        (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:categoryld])) keyWithNSString:@"value"])) valueWithId:category];
        (void) [jw endObject];
      }
      if (comment != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"comment"])) valueWithId:comment];
      }
      (void) [jw endObject];
    }
    else {
      if (date != nil) {
        [((JavaIoWriter *) nil_chk(w)) writeWithNSString:dateld];
        [w writeWithInt:(unichar) '|'];
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:date withRAREUTCharArray:ca];
      }
      [((JavaIoWriter *) nil_chk(w)) writeWithInt:(unichar) '^'];
      if (lab != nil) {
        [w writeWithNSString:labld];
        [w writeWithInt:(unichar) '|'];
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:lab withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      if (result != nil) {
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:result withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      if (is_document != nil) {
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:is_document withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      if (category != nil) {
        [w writeWithNSString:categoryld];
        [w writeWithInt:(unichar) '|'];
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:category withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      if (comment != nil) {
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:comment withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
    }
    parsed = YES;
  }
  while (NO);
  if (!parsed) {
    [RAREPlatform debugLogWithNSString:[NSString stringWithFormat:@"Could not parse entry:\n%@", [((RAREUTJSONObject *) nil_chk(entry_)) toStringWithInt:2]]];
  }
}

- (void)processObservationEntryWithNSString:(NSString *)date
                               withNSString:(NSString *)categoryld
                               withNSString:(NSString *)category
                               withNSString:(NSString *)panelld
                               withNSString:(NSString *)panel
                       withRAREUTJSONObject:(RAREUTJSONObject *)entry_
                       withRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                           withJavaIoWriter:(JavaIoWriter *)w
                        withRAREUTCharArray:(RAREUTCharArray *)ca {
  if (![((NSString *) nil_chk([((RAREUTJSONObject *) nil_chk(entry_)) getStringWithNSString:@"resourceType"])) isEqual:@"Observation"]) {
    return;
  }
  RAREUTJSONObject *o;
  BOOL parsed = NO;
  do {
    NSString *dateld = nil;
    NSString *labld = nil;
    NSString *lab = nil;
    NSString *resultld = nil;
    NSString *result = nil;
    NSString *unit = nil;
    NSString *range = nil;
    NSString *is_document = @"false";
    NSString *comment = nil;
    dateld = [self getIDWithRAREUTJSONObject:entry_];
    o = [entry_ getJSONObjectWithNSString:@"code"];
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
    lab = [o optStringWithNSString:@"text" withNSString:[((ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *) nil_chk(mc)) getBestText]];
    labld = (mc == nil) ? @"" : [mc getBestCode];
    o = [entry_ optJSONObjectWithNSString:@"valueQuantity"];
    if (o != nil) {
      unit = [o optStringWithNSString:@"units" withNSString:nil];
      result = [o getStringWithNSString:@"value"];
      if ([((JavaUtilHashSet *) nil_chk(ComSparsewareBellavistaExternalFhirLabs_bunCodes_)) containsWithId:labld]) {
        labld = @"bun";
      }
      else if ([((JavaUtilHashSet *) nil_chk(ComSparsewareBellavistaExternalFhirLabs_creatinineCodes_)) containsWithId:labld]) {
        labld = @"creat";
      }
    }
    else {
      unit = nil;
      result = [entry_ optStringWithNSString:@"valueString" withNSString:nil];
      if (result == nil) {
        result = [RAREPlatform getResourceAsStringWithNSString:@"bv.text.see_report"];
      }
    }
    o = [entry_ optJSONObjectWithNSString:@"interpretation"];
    if (o != nil) {
      resultld = [ComSparsewareBellavistaExternalFhirFHIRUtils getHL7FHIRCodeWithRAREUTJSONArray:[o optJSONArrayWithNSString:@"coding"]];
    }
    comment = [entry_ optStringWithNSString:@"comments" withNSString:nil];
    if (comment != nil) {
      comment = [ComSparsewareBellavistaExternalFhirFHIRUtils cleanAndEncodeStringWithNSString:comment];
    }
    RAREUTJSONArray *a = [entry_ optJSONArrayWithNSString:@"referenceRange"];
    range = (a == nil) ? nil : [ComSparsewareBellavistaExternalFhirFHIRUtils getRangeWithRAREUTJSONArray:a withBoolean:YES withRAREUTCharArray:ca];
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
      if (lab != nil) {
        if (labld != nil) {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"lab"])) object];
          (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:labld])) keyWithNSString:@"value"])) valueWithId:lab];
          (void) [jw endObject];
        }
        else {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"lab"])) valueWithId:lab];
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
      if (is_document != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"is_document"])) valueWithId:is_document];
      }
      if (category != nil) {
        if (categoryld != nil) {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"category"])) object];
          (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:categoryld])) keyWithNSString:@"value"])) valueWithId:category];
          (void) [jw endObject];
        }
        else {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"category"])) valueWithId:category];
        }
      }
      if (panel != nil) {
        if (panelld != nil) {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"panel"])) object];
          (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"linkedData"])) valueWithId:panelld])) keyWithNSString:@"value"])) valueWithId:panel];
          (void) [jw endObject];
        }
        else {
          (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"panel"])) valueWithId:panel];
        }
      }
      if (comment != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([jw keyWithNSString:@"comment"])) valueWithId:comment];
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
      if (lab != nil) {
        if (labld != nil) {
          [w writeWithNSString:labld];
          [w writeWithInt:(unichar) '|'];
        }
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:lab withRAREUTCharArray:ca];
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
      if (is_document != nil) {
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:is_document withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      if (category != nil) {
        if (categoryld != nil) {
          [w writeWithNSString:categoryld];
          [w writeWithInt:(unichar) '|'];
        }
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:category withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      if (panel != nil) {
        if (panelld != nil) {
          [w writeWithNSString:panelld];
          [w writeWithInt:(unichar) '|'];
        }
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:panel withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) '^'];
      if (comment != nil) {
        [ComSparsewareBellavistaExternalFhirFHIRUtils writeQuotedStringIfNecessaryWithJavaIoWriter:w withNSString:comment withRAREUTCharArray:ca];
      }
      [w writeWithInt:(unichar) '^'];
      [w writeWithInt:(unichar) 0x000a];
    }
    parsed = YES;
  }
  while (NO);
  if (!parsed) {
    [RAREPlatform debugLogWithNSString:[NSString stringWithFormat:@"Could not parse entry:\n%@", [entry_ toStringWithInt:2]]];
  }
}

+ (void)initialize {
  if (self == [ComSparsewareBellavistaExternalFhirLabs class]) {
    ComSparsewareBellavistaExternalFhirLabs_bunCodes_ = [[JavaUtilHashSet alloc] initWithInt:2];
    ComSparsewareBellavistaExternalFhirLabs_creatinineCodes_ = [[JavaUtilHashSet alloc] initWithInt:2];
    ComSparsewareBellavistaExternalFhirLabs_categoryCodes_ = [[JavaUtilHashMap alloc] init];
    {
      [ComSparsewareBellavistaExternalFhirLabs_bunCodes_ addWithId:@"75367002"];
      [ComSparsewareBellavistaExternalFhirLabs_bunCodes_ addWithId:@"55284-4"];
      [ComSparsewareBellavistaExternalFhirLabs_creatinineCodes_ addWithId:@"271649006"];
      [ComSparsewareBellavistaExternalFhirLabs_creatinineCodes_ addWithId:@"8480-6"];
    }
  }
}

- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirLabs *)other {
  [super copyAllFieldsTo:other];
  other->searchByDateSupported_ = searchByDateSupported_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "documentWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "most_recentWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "realtimeWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "summaryWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "listWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "testsWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "listWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:withInt:", NULL, "V", 0x1, "JavaIoIOException" },
    { "processEntryWithRAREUTJSONObject:withRAREUTJSONWriter:withJavaIoWriter:withRAREUTCharArray:withNSObjectArray:", NULL, "V", 0x84, "JavaIoIOException" },
    { "processObservationEntryWithNSString:withNSString:withNSString:withNSString:withNSString:withRAREUTJSONObject:withRAREUTJSONWriter:withJavaIoWriter:withRAREUTCharArray:", NULL, "V", 0x4, "JavaIoIOException" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "searchByDateSupported_", NULL, 0x10, "Z" },
    { "bunCodes_", NULL, 0x8, "LJavaUtilHashSet" },
    { "creatinineCodes_", NULL, 0x8, "LJavaUtilHashSet" },
    { "categoryCodes_", NULL, 0x8, "LJavaUtilHashMap" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirLabs = { "Labs", "com.sparseware.bellavista.external.fhir", NULL, 0x1, 9, methods, 4, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirLabs;
}

@end
