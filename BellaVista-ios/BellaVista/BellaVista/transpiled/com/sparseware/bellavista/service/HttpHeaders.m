//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/HttpHeaders.java
//
//  Created by decoteaud on 2/17/16.
//

#include "com/appnativa/rare/iConstants.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/util/HTTPDateUtils.h"
#include "com/appnativa/util/StringCache.h"
#include "com/sparseware/bellavista/service/HttpHeaders.h"
#include "java/util/ArrayList.h"
#include "java/util/Date.h"
#include "java/util/LinkedHashMap.h"
#include "java/util/Locale.h"
#include "java/util/Set.h"

@implementation CCPBVHttpHeaders

- (NSString *)getHeaderWithInt:(int)index {
  if (index == 0) {
    return status_;
  }
  NSString *key = [self getKeyWithInt:index];
  if (key == nil) {
    return nil;
  }
  return [((JavaUtilLinkedHashMap *) nil_chk(headers_)) getWithId:key];
}

- (NSString *)getHeaderWithNSString:(NSString *)key {
  if ((headers_ == nil) || (key == nil)) {
    return nil;
  }
  return [((JavaUtilLinkedHashMap *) nil_chk(headers_)) getWithId:[((NSString *) nil_chk(key)) lowercaseStringWithJRELocale:[JavaUtilLocale US]]];
}

- (void)clear {
  if (headers_ != nil) {
    [headers_ clear];
  }
  keys_ = nil;
}

- (void)addWithNSString:(NSString *)key
           withNSString:(NSString *)value {
  if (headers_ == nil) {
    headers_ = [[JavaUtilLinkedHashMap alloc] initWithInt:5];
  }
  (void) [((JavaUtilLinkedHashMap *) nil_chk(headers_)) putWithId:[((NSString *) nil_chk(key)) lowercaseStringWithJRELocale:[JavaUtilLocale US]] withId:value];
}

- (void)setStatusWithNSString:(NSString *)status {
  self->status_ = status;
}

- (void)setDefaultResponseHeaders {
  self->status_ = @"HTTP/1.1 200 Ok";
  [self addWithNSString:@"server" withNSString:@"FHIR Proxy"];
  [self setDateWithJavaUtilDate:[[JavaUtilDate alloc] init]];
}

- (void)setDateWithJavaUtilDate:(JavaUtilDate *)date {
  [self setDateWithNSString:[RAREUTHTTPDateUtils formatDateWithJavaUtilDate:date]];
}

- (void)setContentTypeWithNSString:(NSString *)type {
  [self addWithNSString:@"content-type" withNSString:type];
}

- (void)setDateWithNSString:(NSString *)date {
  [self addWithNSString:@"date" withNSString:date];
}

- (void)mimeText {
  [self addWithNSString:@"content-type" withNSString:[RAREiConstants TEXT_MIME_TYPE]];
}

- (void)mimeJson {
  [self addWithNSString:@"content-type" withNSString:[RAREiConstants JSON_MIME_TYPE]];
}

- (void)mimeHtml {
  [self addWithNSString:@"content-type" withNSString:[RAREiConstants HTML_MIME_TYPE]];
}

- (void)mimeWithNSString:(NSString *)type {
  [self addWithNSString:@"content-type" withNSString:type];
}

- (void)mimeMultipartWithNSString:(NSString *)boundary {
  [self addWithNSString:@"content-type" withNSString:[NSString stringWithFormat:@"multipart/mixed; boundary=%@", boundary]];
}

- (void)mimeList {
  [self addWithNSString:@"content-type" withNSString:@"text/plain;ldseparator=|"];
}

- (void)mimeRow {
  [self addWithNSString:@"content-type" withNSString:@"text/plain;separator=^;ldseparator=|;riseparator=~"];
}

- (void)hasMoreWithNSString:(NSString *)next {
  [self addWithNSString:[RAREActionLink PAGING_HAS_MORE] withNSString:@"true"];
  [self addWithNSString:[RAREActionLink PAGING_NEXT] withNSString:next];
}

- (void)setLinkInfoWithNSString:(NSString *)info {
  [self addWithNSString:[RAREActionLink LINK_INFO] withNSString:info];
}

- (void)setContentLengthWithLong:(long long int)length {
  [self addWithNSString:@"content-length" withNSString:[RAREUTStringCache valueOfWithLong:length]];
}

- (NSString *)getKeyWithInt:(int)index {
  if (index == 0) {
    return @"HTTP_STATUS_CODE";
  }
  index--;
  if (headers_ == nil) {
    return nil;
  }
  if (keys_ == nil) {
    keys_ = [[JavaUtilArrayList alloc] initWithInt:[((JavaUtilLinkedHashMap *) nil_chk(headers_)) size]];
    [keys_ addAllWithJavaUtilCollection:[headers_ keySet]];
  }
  return [((JavaUtilArrayList *) nil_chk(keys_)) getWithInt:index];
}

- (id)init {
  return [super init];
}

- (void)copyAllFieldsTo:(CCPBVHttpHeaders *)other {
  [super copyAllFieldsTo:other];
  other->headers_ = headers_;
  other->keys_ = keys_;
  other->status_ = status_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getHeaderWithInt:", NULL, "LNSString", 0x1, NULL },
    { "getHeaderWithNSString:", NULL, "LNSString", 0x1, NULL },
    { "getKeyWithInt:", NULL, "LNSString", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "keys_", NULL, 0x4, "LJavaUtilArrayList" },
    { "headers_", NULL, 0x4, "LJavaUtilLinkedHashMap" },
    { "status_", NULL, 0x4, "LNSString" },
  };
  static J2ObjcClassInfo _CCPBVHttpHeaders = { "HttpHeaders", "com.sparseware.bellavista.service", NULL, 0x1, 3, methods, 3, fields, 0, NULL};
  return &_CCPBVHttpHeaders;
}

@end
