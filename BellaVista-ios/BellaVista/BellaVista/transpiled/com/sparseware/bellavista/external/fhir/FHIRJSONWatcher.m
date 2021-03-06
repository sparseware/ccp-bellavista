//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/FHIRJSONWatcher.java
//
//  Created by decoteaud on 3/14/16.
//

#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/external/fhir/FHIRJSONWatcher.h"
#include "java/util/HashSet.h"

@implementation CCPBVFHIRFHIRJSONWatcher

- (id)initWithCCPBVFHIRFHIRJSONWatcher_iCallback:(id<CCPBVFHIRFHIRJSONWatcher_iCallback>)callback {
  return [self initCCPBVFHIRFHIRJSONWatcherWithCCPBVFHIRFHIRJSONWatcher_iCallback:callback withNSString:@"entry"];
}

- (id)initCCPBVFHIRFHIRJSONWatcherWithCCPBVFHIRFHIRJSONWatcher_iCallback:(id<CCPBVFHIRFHIRJSONWatcher_iCallback>)callback
                                                            withNSString:(NSString *)entryArray {
  if (self = [super init]) {
    self->callback_ = callback;
    self->entryArray_ = entryArray;
    int n = [((NSString *) nil_chk(entryArray)) indexOf:'/'];
    if (n != -1) {
      parentArray_ = [entryArray substring:0 endIndex:n];
    }
  }
  return self;
}

- (id)initWithCCPBVFHIRFHIRJSONWatcher_iCallback:(id<CCPBVFHIRFHIRJSONWatcher_iCallback>)callback
                                    withNSString:(NSString *)entryArray {
  return [self initCCPBVFHIRFHIRJSONWatcherWithCCPBVFHIRFHIRJSONWatcher_iCallback:callback withNSString:entryArray];
}

- (void)didParseArrayWithRAREUTJSONArray:(RAREUTJSONArray *)array {
  NSString *name = [((RAREUTJSONArray *) nil_chk(array)) getName];
  if ([((NSString *) nil_chk(entryArray_)) isEqual:name]) {
    parsingEntries_ = NO;
  }
  else if ([@"link" isEqual:name]) {
    parsingLink_ = NO;
  }
  if (ignoredArrays_ != nil) {
    [ignoredArrays_ removeWithId:name];
  }
}

- (id)valueEncounteredWithRAREUTJSONArray:(RAREUTJSONArray *)parent
                             withNSString:(NSString *)arrayName
                                   withId:(id)value {
  if ([((NSString *) nil_chk(arrayName)) isEqual:entryArray_]) {
    return [((id<CCPBVFHIRFHIRJSONWatcher_iCallback>) nil_chk(callback_)) entryEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *) check_class_cast(value, [RAREUTJSONObject class])];
  }
  else if ([arrayName isEqual:@"link"]) {
    RAREUTJSONObject *o = (RAREUTJSONObject *) check_class_cast(value, [RAREUTJSONObject class]);
    return [((id<CCPBVFHIRFHIRJSONWatcher_iCallback>) nil_chk(callback_)) linkEncounteredWithNSString:arrayName withNSString:[((RAREUTJSONObject *) nil_chk(o)) getStringWithNSString:@"relation"] withNSString:[o getStringWithNSString:@"url"]];
  }
  if (parsingEntries_ || parsingLink_) {
    return value;
  }
  if ((ignoredArrays_ != nil) && [ignoredArrays_ containsWithId:arrayName]) {
    return nil;
  }
  return [((id<CCPBVFHIRFHIRJSONWatcher_iCallback>) nil_chk(callback_)) otherArrayElementEncounteredWithNSString:arrayName withId:value];
}

- (id)valueEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *)parent
                              withNSString:(NSString *)valueName
                                    withId:(id)value {
  if (parsingEntries_ || parsingLink_) {
    return value;
  }
  return [((id<CCPBVFHIRFHIRJSONWatcher_iCallback>) nil_chk(callback_)) valueEncounteredWithRAREUTJSONObject:parent withNSString:valueName withId:value];
}

- (void)willParseArrayWithRAREUTJSONArray:(RAREUTJSONArray *)array {
  NSString *name = [((RAREUTJSONArray *) nil_chk(array)) getName];
  if ((parentArray_ != nil) && [((NSString *) nil_chk(name)) isEqual:parentArray_]) {
    return;
  }
  if (!parsingEntries_) {
    parsingEntries_ = [((NSString *) nil_chk(entryArray_)) isEqual:name];
  }
  if (!parsingLink_) {
    parsingLink_ = [@"link" isEqual:name];
  }
  if (!parsingEntries_ && !parsingLink_ && ![((id<CCPBVFHIRFHIRJSONWatcher_iCallback>) nil_chk(callback_)) parseArrayWithNSString:name]) {
    if (ignoredArrays_ == nil) {
      ignoredArrays_ = [[JavaUtilHashSet alloc] init];
    }
    [((JavaUtilHashSet *) nil_chk(ignoredArrays_)) addWithId:name];
  }
}

- (void)copyAllFieldsTo:(CCPBVFHIRFHIRJSONWatcher *)other {
  [super copyAllFieldsTo:other];
  other->callback_ = callback_;
  other->entryArray_ = entryArray_;
  other->ignoredArrays_ = ignoredArrays_;
  other->parentArray_ = parentArray_;
  other->parsingEntries_ = parsingEntries_;
  other->parsingLink_ = parsingLink_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "valueEncounteredWithRAREUTJSONArray:withNSString:withId:", NULL, "LNSObject", 0x1, NULL },
    { "valueEncounteredWithRAREUTJSONObject:withNSString:withId:", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "parsingEntries_", NULL, 0x4, "Z" },
    { "parsingLink_", NULL, 0x4, "Z" },
    { "callback_", NULL, 0x4, "LCCPBVFHIRFHIRJSONWatcher_iCallback" },
    { "entryArray_", NULL, 0x4, "LNSString" },
    { "parentArray_", NULL, 0x4, "LNSString" },
    { "ignoredArrays_", NULL, 0x4, "LJavaUtilHashSet" },
  };
  static J2ObjcClassInfo _CCPBVFHIRFHIRJSONWatcher = { "FHIRJSONWatcher", "com.sparseware.bellavista.external.fhir", NULL, 0x1, 2, methods, 6, fields, 0, NULL};
  return &_CCPBVFHIRFHIRJSONWatcher;
}

@end

@interface CCPBVFHIRFHIRJSONWatcher_iCallback : NSObject
@end

@implementation CCPBVFHIRFHIRJSONWatcher_iCallback

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "entryEncounteredWithRAREUTJSONObject:", NULL, "LNSObject", 0x401, NULL },
    { "valueEncounteredWithRAREUTJSONObject:withNSString:withId:", NULL, "LNSObject", 0x401, NULL },
    { "linkEncounteredWithNSString:withNSString:withNSString:", NULL, "LNSObject", 0x401, NULL },
    { "otherArrayElementEncounteredWithNSString:withId:", NULL, "LNSObject", 0x401, NULL },
    { "parseArrayWithNSString:", NULL, "Z", 0x401, NULL },
  };
  static J2ObjcClassInfo _CCPBVFHIRFHIRJSONWatcher_iCallback = { "iCallback", "com.sparseware.bellavista.external.fhir", "FHIRJSONWatcher", 0x209, 5, methods, 0, NULL, 0, NULL};
  return &_CCPBVFHIRFHIRJSONWatcher_iCallback;
}

@end
@implementation CCPBVFHIRFHIRJSONWatcher_aCallback

- (id)linkEncounteredWithNSString:(NSString *)arrayName
                     withNSString:(NSString *)type
                     withNSString:(NSString *)url {
  return nil;
}

- (id)valueEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *)parent
                              withNSString:(NSString *)key
                                    withId:(id)value {
  return nil;
}

- (id)otherArrayElementEncounteredWithNSString:(NSString *)arrayName
                                        withId:(id)value {
  return nil;
}

- (BOOL)parseArrayWithNSString:(NSString *)arrayName {
  return NO;
}

- (id)entryEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *)param0 {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "linkEncounteredWithNSString:withNSString:withNSString:", NULL, "LNSObject", 0x1, NULL },
    { "valueEncounteredWithRAREUTJSONObject:withNSString:withId:", NULL, "LNSObject", 0x1, NULL },
    { "otherArrayElementEncounteredWithNSString:withId:", NULL, "LNSObject", 0x1, NULL },
    { "parseArrayWithNSString:", NULL, "Z", 0x1, NULL },
    { "entryEncounteredWithRAREUTJSONObject:", NULL, "LNSObject", 0x401, NULL },
  };
  static J2ObjcClassInfo _CCPBVFHIRFHIRJSONWatcher_aCallback = { "aCallback", "com.sparseware.bellavista.external.fhir", "FHIRJSONWatcher", 0x409, 5, methods, 0, NULL, 0, NULL};
  return &_CCPBVFHIRFHIRJSONWatcher_aCallback;
}

@end
