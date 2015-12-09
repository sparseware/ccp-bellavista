//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/FHIRUtils.java
//
//  Created by decoteaud on 11/18/15.
//

#include "IOSCharArray.h"
#include "IOSClass.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/util/CharArray.h"
#include "com/appnativa/util/Helper.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/appnativa/util/json/JSONWriter.h"
#include "com/sparseware/bellavista/ActionPath.h"
#include "com/sparseware/bellavista/external/fhir/FHIRUtils.h"
#include "com/sparseware/bellavista/service/ContentWriter.h"
#include "com/sparseware/bellavista/service/HttpHeaders.h"
#include "com/sparseware/bellavista/service/aRemoteService.h"
#include "java/io/IOException.h"
#include "java/io/Writer.h"
#include "java/lang/Integer.h"
#include "java/lang/StringBuilder.h"
#include "java/util/HashMap.h"
#include "java/util/List.h"
#include "java/util/Set.h"

@implementation ComSparsewareBellavistaExternalFhirFHIRUtils

static JavaUtilHashMap * ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_;

+ (JavaUtilHashMap *)interpretationColors {
  return ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_;
}

- (id)init {
  return [super init];
}

+ (void)writeNameWithId:(id)writer
    withRAREUTJSONArray:(RAREUTJSONArray *)a {
  RAREUTJSONWriter *jw = nil;
  JavaIoWriter *w = nil;
  if ([writer isKindOfClass:[RAREUTJSONWriter class]]) {
    jw = (RAREUTJSONWriter *) check_class_cast(writer, [RAREUTJSONWriter class]);
  }
  else {
    w = (JavaIoWriter *) check_class_cast(writer, [JavaIoWriter class]);
  }
  RAREUTJSONObject *name = nil;
  int len = [((RAREUTJSONArray *) nil_chk(a)) length];
  if (len == 1) {
    name = [a getJSONObjectWithInt:0];
  }
  else {
    for (int i = 0; i < len; i++) {
      RAREUTJSONObject *o = [a getJSONObjectWithInt:i];
      if ([@"usual" isEqual:[((RAREUTJSONObject *) nil_chk(o)) optWithNSString:@"use"]]) {
        name = o;
        break;
      }
      if ([@"official" isEqual:[o optWithNSString:@"use"]]) {
        name = o;
      }
    }
  }
  if (name == nil) {
    name = [a getJSONObjectWithInt:0];
  }
  NSString *s;
  if (jw != nil) {
    (void) [jw keyWithNSString:@"name"];
  }
  s = [((RAREUTJSONObject *) nil_chk(name)) optStringWithNSString:@"text" withNSString:nil];
  if (s != nil) {
    if (jw != nil) {
      (void) [jw valueWithId:s];
    }
    else {
      [((JavaIoWriter *) nil_chk(w)) writeWithNSString:s];
    }
    return;
  }
  a = [name optJSONArrayWithNSString:@"family"];
  JavaLangStringBuilder *sb = [[JavaLangStringBuilder alloc] init];
  if (a != nil) {
    (void) [RAREUTHelper toStringWithJavaLangStringBuilder:sb withJavaUtilList:[a getObjectList] withNSString:@" "];
    (void) [sb appendWithNSString:@", "];
  }
  a = [name optJSONArrayWithNSString:@"given"];
  if (a != nil) {
    (void) [RAREUTHelper toStringWithJavaLangStringBuilder:sb withJavaUtilList:[a getObjectList] withNSString:@" "];
  }
  if (jw != nil) {
    (void) [jw valueWithId:[sb description]];
  }
  else {
    [((JavaIoWriter *) nil_chk(w)) writeWithNSString:[sb description]];
  }
}

+ (void)writeCodableConceptWithRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                            withRAREUTJSONArray:(RAREUTJSONArray *)a
                            withJavaUtilHashMap:(JavaUtilHashMap *)map {
  int len = [((RAREUTJSONArray *) nil_chk(a)) length];
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *idf = [a getJSONObjectWithInt:i];
    RAREUTJSONObject *type = [((RAREUTJSONObject *) nil_chk(idf)) optJSONObjectWithNSString:@"type"];
    RAREUTJSONArray *aa = (type == nil) ? nil : [type optJSONArrayWithNSString:@"coding"];
    int alen = (aa == nil) ? 0 : [aa length];
    for (int ai = 0; ai < alen; ai++) {
      RAREUTJSONObject *coding = [aa getJSONObjectWithInt:ai];
      NSString *code = [((RAREUTJSONObject *) nil_chk(coding)) getStringWithNSString:@"code"];
      code = [((JavaUtilHashMap *) nil_chk(map)) getWithId:code];
      if (code != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk(jw)) keyWithNSString:code])) valueWithId:[idf getStringWithNSString:@"value"]];
        break;
      }
    }
    NSString *code = [idf optStringWithNSString:@"system" withNSString:nil];
    if (code != nil) {
      code = [((JavaUtilHashMap *) nil_chk(map)) getWithId:code];
      if (code != nil) {
        (void) [((RAREUTJSONWriter *) nil_chk([((RAREUTJSONWriter *) nil_chk(jw)) keyWithNSString:code])) valueWithId:[idf getStringWithNSString:@"value"]];
      }
    }
  }
}

+ (id)createWriterWithCCPBVActionPath:(CCPBVActionPath *)path
               withCCPBVContentWriter:(CCPBVContentWriter *)w
                 withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
                          withBoolean:(BOOL)row {
  id writer;
  RAREUTJSONWriter *jw = nil;
  if ([ComSparsewareBellavistaExternalFhirFHIRUtils isJSONFormatRequestedWithCCPBVActionPath:path]) {
    jw = [[RAREUTJSONWriter alloc] initWithJavaIoWriter:w];
    writer = jw;
    (void) [jw object];
  }
  else {
    writer = w;
  }
  [((CCPBVHttpHeaders *) nil_chk(headers)) setDefaultResponseHeaders];
  if (jw != nil) {
    [headers mimeJson];
  }
  else {
    if (row) {
      [headers mimeRow];
    }
    else {
      [headers mimeList];
    }
  }
  return writer;
}

+ (BOOL)isJSONFormatRequestedWithCCPBVActionPath:(CCPBVActionPath *)path {
  return [@"json" isEqual:[CCPBVaRemoteService getExtensionWithCCPBVActionPath:path]];
}

+ (BOOL)isHTMLFormatRequestedWithCCPBVActionPath:(CCPBVActionPath *)path {
  return [@"html" isEqual:[CCPBVaRemoteService getExtensionWithCCPBVActionPath:path]];
}

+ (NSString *)getBestMedicalCodeWithRAREUTJSONObject:(RAREUTJSONObject *)o {
  RAREUTJSONArray *a = (o == nil) ? nil : [o optJSONArrayWithNSString:@"coding"];
  NSString *code = (a == nil) ? nil : [ComSparsewareBellavistaExternalFhirFHIRUtils getBestMedicalCodeWithRAREUTJSONArray:a];
  if ((o != nil) && (code == nil)) {
    code = [o optStringWithNSString:@"text" withNSString:nil];
  }
  return code;
}

+ (NSString *)getBestMedicalTextWithRAREUTJSONObject:(RAREUTJSONObject *)o {
  if (o != nil) {
    NSString *code = [o optStringWithNSString:@"text" withNSString:nil];
    if (code == nil) {
      RAREUTJSONArray *a = (o == nil) ? nil : [o optJSONArrayWithNSString:@"coding"];
      code = (a == nil) ? nil : [ComSparsewareBellavistaExternalFhirFHIRUtils getBestMedicalTextWithRAREUTJSONArray:a];
    }
    return code;
  }
  return nil;
}

+ (NSString *)getBestMedicalCodeWithRAREUTJSONArray:(RAREUTJSONArray *)a {
  int len = (a == nil) ? 0 : [a length];
  NSString *loinc = nil;
  NSString *other = nil;
  NSString *snomed = nil;
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *coding = [((RAREUTJSONArray *) nil_chk(a)) getJSONObjectWithInt:i];
    NSString *system = [((RAREUTJSONObject *) nil_chk(coding)) optStringWithNSString:@"system" withNSString:nil];
    if (system != nil) {
      if ([system hasPrefix:@"http://hl7.org/fhir/"]) {
        return [coding getStringWithNSString:@"code"];
      }
      else if ([system hasPrefix:@"http://loinc.org"]) {
        loinc = [coding getStringWithNSString:@"code"];
      }
      else if ([system hasPrefix:@"http://snomed.info"]) {
        snomed = [coding getStringWithNSString:@"code"];
      }
      else {
        other = [coding getStringWithNSString:@"code"];
      }
    }
    else {
      other = [coding getStringWithNSString:@"code"];
    }
  }
  return (loinc != nil) ? loinc : ((snomed != nil) ? snomed : other);
}

+ (NSString *)getBestMedicalTextWithRAREUTJSONArray:(RAREUTJSONArray *)a {
  int len = (a == nil) ? 0 : [a length];
  NSString *loinc = nil;
  NSString *other = nil;
  NSString *snomed = nil;
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *coding = [((RAREUTJSONArray *) nil_chk(a)) getJSONObjectWithInt:i];
    NSString *system = [((RAREUTJSONObject *) nil_chk(coding)) optStringWithNSString:@"system" withNSString:nil];
    if (system != nil) {
      if ([system hasPrefix:@"http://hl7.org/fhir/"]) {
        NSString *s = [coding optStringWithNSString:@"display" withNSString:nil];
        if (s != nil) {
          return s;
        }
        other = [coding optStringWithNSString:@"code" withNSString:nil];
      }
      else if ([system hasPrefix:@"http://loinc.org"]) {
        loinc = [coding optStringWithNSString:@"display" withNSString:nil];
      }
      else if ([system hasPrefix:@"http://snomed.info"]) {
        snomed = [coding optStringWithNSString:@"display" withNSString:nil];
      }
      else {
        other = [coding optStringWithNSString:@"display" withNSString:nil];
      }
    }
    else {
      other = [coding optStringWithNSString:@"display" withNSString:nil];
    }
  }
  return (loinc != nil) ? loinc : ((snomed != nil) ? snomed : other);
}

+ (NSString *)getHL7FHIRCodeWithRAREUTJSONArray:(RAREUTJSONArray *)a {
  int len = (a == nil) ? 0 : [a length];
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *coding = [((RAREUTJSONArray *) nil_chk(a)) getJSONObjectWithInt:i];
    NSString *system = [((RAREUTJSONObject *) nil_chk(coding)) getStringWithNSString:@"system"];
    if ([((NSString *) nil_chk(system)) hasPrefix:@"http://hl7.org/fhir/"]) {
      return [coding optStringWithNSString:@"code" withNSString:nil];
    }
  }
  return nil;
}

+ (NSString *)getInterpretationColorWithNSString:(NSString *)code {
  if (code == nil) {
    return nil;
  }
  NSString *color = [((JavaUtilHashMap *) nil_chk(ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_)) getWithId:code];
  if (color == nil) {
    return @"unknown";
  }
  return ([((NSString *) nil_chk(color)) sequenceLength] == 0) ? nil : color;
}

+ (NSString *)getRangeWithRAREUTJSONArray:(RAREUTJSONArray *)ranges
                              withBoolean:(BOOL)includeUnits
                      withRAREUTCharArray:(RAREUTCharArray *)ca {
  int len = (ranges == nil) ? 0 : [ranges length];
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *o = [((RAREUTJSONArray *) nil_chk(ranges)) getJSONObjectWithInt:i];
    NSString *range = [ComSparsewareBellavistaExternalFhirFHIRUtils getRangeWithRAREUTJSONObject:o withBoolean:includeUnits withRAREUTCharArray:ca];
    RAREUTJSONArray *a = [((RAREUTJSONObject *) nil_chk(o)) optJSONArrayWithNSString:@"meaning"];
    NSString *meaning = (a == nil) ? nil : [ComSparsewareBellavistaExternalFhirFHIRUtils getHL7FHIRCodeWithRAREUTJSONArray:a];
    if ((range != nil) && ((meaning == nil) || [meaning isEqual:@"N"])) {
      return range;
    }
  }
  return nil;
}

+ (NSString *)cleanAndEncodeStringWithNSString:(NSString *)value {
  if (value != nil) {
    value = [value trim];
    if ([((NSString *) nil_chk(value)) sequenceLength] == 0) {
      value = nil;
    }
    else {
      value = [RAREFunctions base64NOLFWithNSString:value];
    }
  }
  return value;
}

+ (void)escapeContolSequencesIfNecessaryWithJavaIoWriter:(JavaIoWriter *)w
                                            withNSString:(NSString *)value {
}

+ (void)writeQuotedStringIfNecessaryWithJavaIoWriter:(JavaIoWriter *)w
                                        withNSString:(NSString *)value
                                 withRAREUTCharArray:(RAREUTCharArray *)ca {
  IOSCharArray *a;
  int len;
  if (ca == nil) {
    a = [((NSString *) nil_chk(value)) toCharArray];
    len = (int) [((IOSCharArray *) nil_chk(a)) count];
  }
  else {
    (void) [ca setWithNSString:value];
    a = ca->A_;
    len = ca->_length_;
  }
  BOOL quoted = NO;
  int lp = 0;
  int i = 0;
  while (i < len) {
    unichar c = IOSCharArray_Get(nil_chk(a), i++);
    switch (c) {
      case '|':
      case '^':
      if (!quoted) {
        quoted = YES;
        [((JavaIoWriter *) nil_chk(w)) writeWithInt:'"'];
      }
      break;
      case 0x0009:
      if (!quoted) {
        quoted = YES;
        [((JavaIoWriter *) nil_chk(w)) writeWithInt:'"'];
      }
      [((JavaIoWriter *) nil_chk(w)) writeWithCharArray:a withInt:lp withInt:i - lp - 1];
      [w writeWithNSString:@"\\t"];
      i++;
      lp = i;
      break;
      case '"':
      if (!quoted) {
        quoted = YES;
        [((JavaIoWriter *) nil_chk(w)) writeWithInt:'"'];
      }
      [((JavaIoWriter *) nil_chk(w)) writeWithCharArray:a withInt:lp withInt:i - lp - 1];
      [w writeWithNSString:@"\\\""];
      lp = i;
      break;
      case 0x000a:
      if (!quoted) {
        quoted = YES;
        [((JavaIoWriter *) nil_chk(w)) writeWithInt:'"'];
      }
      [((JavaIoWriter *) nil_chk(w)) writeWithCharArray:a withInt:lp withInt:i - lp - 1];
      [w writeWithNSString:@"\\n"];
      lp = i;
      break;
      case 0x000d:
      if (!quoted) {
        quoted = YES;
        [((JavaIoWriter *) nil_chk(w)) writeWithInt:'"'];
      }
      [((JavaIoWriter *) nil_chk(w)) writeWithCharArray:a withInt:lp withInt:i - lp - 1];
      lp = i;
      break;
    }
  }
  if (lp < len) {
    [((JavaIoWriter *) nil_chk(w)) writeWithCharArray:a withInt:lp withInt:len - lp];
  }
  if (quoted) {
    [((JavaIoWriter *) nil_chk(w)) writeWithInt:'"'];
  }
}

+ (NSString *)getRateWithRAREUTJSONObject:(RAREUTJSONObject *)rate
                      withRAREUTCharArray:(RAREUTCharArray *)ca {
  if (rate == nil) {
    return nil;
  }
  NSString *s = [ComSparsewareBellavistaExternalFhirFHIRUtils getRateOrRatioWithRAREUTJSONObject:rate withNSString:@"numerator" withNSString:@"denominator" withNSString:@"/" withBoolean:YES withRAREUTCharArray:ca];
  return (s == nil) ? [((RAREUTJSONObject *) nil_chk(rate)) optStringWithNSString:@"text" withNSString:nil] : s;
}

+ (NSString *)getRateOrRatioWithRAREUTJSONObject:(RAREUTJSONObject *)rate
                                    withNSString:(NSString *)firstKey
                                    withNSString:(NSString *)secondKey
                                    withNSString:(NSString *)sep
                                     withBoolean:(BOOL)includeUnits
                             withRAREUTCharArray:(RAREUTCharArray *)ca {
  if (rate == nil) {
    return nil;
  }
  NSString *firstUnit = nil;
  NSString *secondUnit = nil;
  NSString *firstValue = nil;
  NSString *secondValue = nil;
  RAREUTJSONObject *first = [((RAREUTJSONObject *) nil_chk(rate)) optJSONObjectWithNSString:firstKey];
  RAREUTJSONObject *second = [rate optJSONObjectWithNSString:secondKey];
  if (first != nil) {
    firstValue = [first optStringWithNSString:@"value" withNSString:nil];
    firstUnit = includeUnits ? [first optStringWithNSString:@"units" withNSString:nil] : nil;
  }
  if (second != nil) {
    secondValue = [second optStringWithNSString:@"value" withNSString:nil];
    secondUnit = includeUnits ? [second optStringWithNSString:@"units" withNSString:nil] : nil;
  }
  if ((firstValue == nil) && (secondValue == nil)) {
    return nil;
  }
  ((RAREUTCharArray *) nil_chk(ca))->_length_ = 0;
  if (firstValue != nil) {
    (void) [ca appendWithNSString:firstValue];
    if (firstUnit != nil) {
      (void) [((RAREUTCharArray *) nil_chk([ca appendWithChar:' '])) appendWithNSString:firstUnit];
    }
  }
  if (secondValue != nil) {
    (void) [((RAREUTCharArray *) nil_chk([ca appendWithNSString:sep])) appendWithNSString:secondValue];
    if (secondUnit != nil) {
      (void) [((RAREUTCharArray *) nil_chk([ca appendWithChar:' '])) appendWithNSString:secondUnit];
    }
  }
  return [ca description];
}

+ (NSString *)getRangeWithRAREUTJSONObject:(RAREUTJSONObject *)range
                               withBoolean:(BOOL)includeUnits
                       withRAREUTCharArray:(RAREUTCharArray *)ca {
  if (range == nil) {
    return nil;
  }
  NSString *s = [ComSparsewareBellavistaExternalFhirFHIRUtils getRateOrRatioWithRAREUTJSONObject:range withNSString:@"low" withNSString:@"high" withNSString:@" - " withBoolean:YES withRAREUTCharArray:ca];
  return (s == nil) ? [((RAREUTJSONObject *) nil_chk(range)) optStringWithNSString:@"text" withNSString:nil] : s;
}

+ (ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *)getMedicalCodeWithRAREUTJSONObject:(RAREUTJSONObject *)o {
  if (o == nil) {
    return nil;
  }
  RAREUTJSONArray *a = [((RAREUTJSONObject *) nil_chk(o)) optJSONArrayWithNSString:@"coding"];
  ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *mc = a == nil ? nil : [ComSparsewareBellavistaExternalFhirFHIRUtils getMedicalCodeWithRAREUTJSONArray:a];
  if (mc == nil) {
    NSString *code = [o getStringWithNSString:@"code"];
    NSString *display = [o optStringWithNSString:@"display" withNSString:nil];
    if (code != nil) {
      mc = [[ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode alloc] initWithNSString:code withNSString:nil withNSString:display withNSString:nil];
    }
  }
  if (mc != nil) {
    mc->text_ = [o optStringWithNSString:@"text" withNSString:nil];
  }
  return mc;
}

+ (ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *)getMedicalCodeWithRAREUTJSONArray:(RAREUTJSONArray *)a {
  int len = (a == nil) ? 0 : [a length];
  NSString *hl7 = nil;
  NSString *loinc = nil;
  NSString *other = nil;
  NSString *snomed = nil;
  NSString *hl7Text = nil;
  NSString *loincText = nil;
  NSString *otherText = nil;
  NSString *snomedText = nil;
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *coding = [((RAREUTJSONArray *) nil_chk(a)) getJSONObjectWithInt:i];
    NSString *system = [((RAREUTJSONObject *) nil_chk(coding)) optStringWithNSString:@"system" withNSString:nil];
    if (system != nil) {
      if ([system hasPrefix:@"http://hl7.org/fhir/"]) {
        hl7 = [coding getStringWithNSString:@"code"];
        hl7Text = [coding optStringWithNSString:@"display" withNSString:nil];
      }
      else if ([system hasPrefix:@"http://loinc.org"]) {
        loinc = [coding getStringWithNSString:@"code"];
        loincText = [coding optStringWithNSString:@"display" withNSString:nil];
      }
      else if ([system hasPrefix:@"http://snomed.info"]) {
        snomed = [coding getStringWithNSString:@"code"];
        snomedText = [coding optStringWithNSString:@"display" withNSString:nil];
      }
      else {
        other = [coding getStringWithNSString:@"code"];
        otherText = [coding optStringWithNSString:@"display" withNSString:nil];
      }
    }
    else {
      other = [coding getStringWithNSString:@"code"];
      otherText = [coding optStringWithNSString:@"display" withNSString:nil];
    }
  }
  if (snomed == nil && other != nil) {
    snomed = other;
    snomedText = otherText;
  }
  if (hl7 == nil && loinc == nil && snomed == nil) {
    return nil;
  }
  return [[ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode alloc] initWithNSString:hl7 withNSString:snomed withNSString:loinc withNSString:hl7Text withNSString:loincText withNSString:snomedText];
}

+ (NSString *)getCodableConceptWithRAREUTJSONArray:(RAREUTJSONArray *)a
                                      withNSString:(NSString *)field
                               withJavaUtilHashMap:(JavaUtilHashMap *)map {
  int len = [((RAREUTJSONArray *) nil_chk(a)) length];
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *idf = [a getJSONObjectWithInt:i];
    RAREUTJSONObject *type = [((RAREUTJSONObject *) nil_chk(idf)) optJSONObjectWithNSString:@"type"];
    RAREUTJSONArray *aa = (type == nil) ? nil : [type optJSONArrayWithNSString:@"coding"];
    int alen = [(aa == nil) ? nil : [JavaLangInteger valueOfWithInt:[aa length]] intValue];
    for (int ai = 0; ai < alen; ai++) {
      RAREUTJSONObject *coding = [aa getJSONObjectWithInt:ai];
      NSString *code = [((RAREUTJSONObject *) nil_chk(coding)) getStringWithNSString:@"code"];
      code = [((JavaUtilHashMap *) nil_chk(map)) getWithId:code];
      if ((code != nil) && [code isEqual:field]) {
        return [idf getStringWithNSString:@"value"];
      }
    }
    NSString *code = [idf optStringWithNSString:@"system" withNSString:nil];
    if (code != nil) {
      code = [((JavaUtilHashMap *) nil_chk(map)) getWithId:code];
      if ((code != nil) && [code isEqual:field]) {
        return [idf getStringWithNSString:@"value"];
      }
    }
  }
  return nil;
}

+ (NSString *)getLocationParentNameWithNSString:(NSString *)id_
                                        withInt:(int)backCount {
  return nil;
}

+ (void)initialize {
  if (self == [ComSparsewareBellavistaExternalFhirFHIRUtils class]) {
    ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ = [[JavaUtilHashMap alloc] init];
    {
      (void) [ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ putWithId:@"HH" withId:@"abnormal"];
      (void) [ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ putWithId:@"H" withId:@"abnormal"];
      (void) [ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ putWithId:@"L" withId:@"abnormal"];
      (void) [ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ putWithId:@"LL" withId:@"abnormal"];
      (void) [ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ putWithId:@"A" withId:@"abnormal"];
      (void) [ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ putWithId:@"AA" withId:@"abnormal"];
      (void) [ComSparsewareBellavistaExternalFhirFHIRUtils_interpretationColors_ putWithId:@"N" withId:@""];
    }
  }
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x2, NULL },
    { "writeNameWithId:withRAREUTJSONArray:", NULL, "V", 0x9, "JavaIoIOException" },
    { "writeCodableConceptWithRAREUTJSONWriter:withRAREUTJSONArray:withJavaUtilHashMap:", NULL, "V", 0x9, "JavaIoIOException" },
    { "createWriterWithCCPBVActionPath:withCCPBVContentWriter:withCCPBVHttpHeaders:withBoolean:", NULL, "LNSObject", 0x9, NULL },
    { "isJSONFormatRequestedWithCCPBVActionPath:", NULL, "Z", 0x9, NULL },
    { "isHTMLFormatRequestedWithCCPBVActionPath:", NULL, "Z", 0x9, NULL },
    { "getBestMedicalCodeWithRAREUTJSONObject:", NULL, "LNSString", 0x9, "JavaIoIOException" },
    { "getBestMedicalTextWithRAREUTJSONObject:", NULL, "LNSString", 0x9, "JavaIoIOException" },
    { "getBestMedicalCodeWithRAREUTJSONArray:", NULL, "LNSString", 0x9, "JavaIoIOException" },
    { "getBestMedicalTextWithRAREUTJSONArray:", NULL, "LNSString", 0x9, "JavaIoIOException" },
    { "getHL7FHIRCodeWithRAREUTJSONArray:", NULL, "LNSString", 0x9, NULL },
    { "getInterpretationColorWithNSString:", NULL, "LNSString", 0x9, NULL },
    { "getRangeWithRAREUTJSONArray:withBoolean:withRAREUTCharArray:", NULL, "LNSString", 0x9, NULL },
    { "cleanAndEncodeStringWithNSString:", NULL, "LNSString", 0x9, NULL },
    { "escapeContolSequencesIfNecessaryWithJavaIoWriter:withNSString:", NULL, "V", 0x9, "JavaIoIOException" },
    { "writeQuotedStringIfNecessaryWithJavaIoWriter:withNSString:withRAREUTCharArray:", NULL, "V", 0x9, "JavaIoIOException" },
    { "getRateWithRAREUTJSONObject:withRAREUTCharArray:", NULL, "LNSString", 0x9, NULL },
    { "getRateOrRatioWithRAREUTJSONObject:withNSString:withNSString:withNSString:withBoolean:withRAREUTCharArray:", NULL, "LNSString", 0x9, NULL },
    { "getRangeWithRAREUTJSONObject:withBoolean:withRAREUTCharArray:", NULL, "LNSString", 0x9, NULL },
    { "getMedicalCodeWithRAREUTJSONObject:", NULL, "LComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode", 0x9, NULL },
    { "getMedicalCodeWithRAREUTJSONArray:", NULL, "LComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode", 0x9, NULL },
    { "getCodableConceptWithRAREUTJSONArray:withNSString:withJavaUtilHashMap:", NULL, "LNSString", 0x9, NULL },
    { "getLocationParentNameWithNSString:withInt:", NULL, "LNSString", 0x9, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "interpretationColors_", NULL, 0x19, "LJavaUtilHashMap" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirFHIRUtils = { "FHIRUtils", "com.sparseware.bellavista.external.fhir", NULL, 0x1, 23, methods, 1, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirFHIRUtils;
}

@end
@implementation ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode

- (id)initWithNSString:(NSString *)loincCode
          withNSString:(NSString *)snomedCode {
  if (self = [super init]) {
    self->loincCode_ = loincCode;
    self->snomedCode_ = snomedCode;
  }
  return self;
}

- (id)initWithNSString:(NSString *)loincCode
          withNSString:(NSString *)snomedCode
          withNSString:(NSString *)loincDisplay
          withNSString:(NSString *)snomedDisplay {
  if (self = [super init]) {
    self->loincCode_ = loincCode;
    self->snomedCode_ = snomedCode;
    self->loincDisplay_ = loincDisplay;
    self->snomedDisplay_ = snomedDisplay;
  }
  return self;
}

- (BOOL)isOneOfWithJavaUtilSet:(id<JavaUtilSet>)codes {
  if ((loincCode_ != nil) && [((id<JavaUtilSet>) nil_chk(codes)) containsWithId:loincCode_]) {
    return YES;
  }
  if ((snomedCode_ != nil) && [((id<JavaUtilSet>) nil_chk(codes)) containsWithId:snomedCode_]) {
    return YES;
  }
  return NO;
}

- (id)initWithNSString:(NSString *)hl7Code
          withNSString:(NSString *)snomedCode
          withNSString:(NSString *)loincCode
          withNSString:(NSString *)hl7Display
          withNSString:(NSString *)loincDisplay
          withNSString:(NSString *)snomedDisplay {
  if (self = [super init]) {
    self->hl7Code_ = hl7Code;
    self->snomedCode_ = snomedCode;
    self->loincCode_ = loincCode;
    self->hl7Display_ = hl7Display;
    self->loincDisplay_ = loincDisplay;
    self->snomedDisplay_ = snomedDisplay;
  }
  return self;
}

- (NSString *)getBestCode {
  if (hl7Code_ != nil) {
    return hl7Code_;
  }
  if (loincCode_ != nil) {
    return loincCode_;
  }
  return snomedCode_;
}

- (void)resolveHL7DisplayWithJavaUtilHashMap:(JavaUtilHashMap *)codeMap {
  if (hl7Display_ == nil && hl7Code_ != nil) {
    hl7Display_ = [((JavaUtilHashMap *) nil_chk(codeMap)) getWithId:hl7Code_];
  }
}

- (NSString *)getBestText {
  if (text_ != nil) {
    return text_;
  }
  if (hl7Display_ != nil) {
    return hl7Display_;
  }
  if (loincDisplay_ != nil) {
    return loincDisplay_;
  }
  return snomedDisplay_;
}

- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *)other {
  [super copyAllFieldsTo:other];
  other->hl7Code_ = hl7Code_;
  other->hl7Display_ = hl7Display_;
  other->loincCode_ = loincCode_;
  other->loincDisplay_ = loincDisplay_;
  other->snomedCode_ = snomedCode_;
  other->snomedDisplay_ = snomedDisplay_;
  other->text_ = text_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "isOneOfWithJavaUtilSet:", NULL, "Z", 0x1, NULL },
    { "getBestCode", NULL, "LNSString", 0x1, NULL },
    { "getBestText", NULL, "LNSString", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "hl7Code_", NULL, 0x1, "LNSString" },
    { "snomedCode_", NULL, 0x1, "LNSString" },
    { "loincCode_", NULL, 0x1, "LNSString" },
    { "hl7Display_", NULL, 0x1, "LNSString" },
    { "loincDisplay_", NULL, 0x1, "LNSString" },
    { "snomedDisplay_", NULL, 0x1, "LNSString" },
    { "text_", NULL, 0x1, "LNSString" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode = { "MedicalCode", "com.sparseware.bellavista.external.fhir", "FHIRUtils", 0x9, 3, methods, 7, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode;
}

@end
