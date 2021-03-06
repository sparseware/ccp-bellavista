//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/SmartDateContext.java
//
//  Created by decoteaud on 3/14/16.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/converters/DateContext.h"
#include "com/appnativa/util/Helper.h"
#include "com/appnativa/util/SNumber.h"
#include "com/appnativa/util/SimpleDateFormatEx.h"
#include "com/sparseware/bellavista/SmartDateContext.h"
#include "java/lang/Character.h"
#include "java/lang/Exception.h"
#include "java/lang/System.h"
#include "java/text/DateFormat.h"
#include "java/util/Calendar.h"
#include "java/util/Date.h"

@implementation CCPBVSmartDateContext

- (id)initWithBoolean:(BOOL)dateTime
withJavaTextDateFormat:(JavaTextDateFormat *)displayFormat {
  if (self = [super init]) {
    dateTime_ = YES;
    supportRelativeDates_ = YES;
    yearMonthDisplay_ = [[RAREUTSimpleDateFormatEx alloc] initWithNSString:@"MMM yyyy"];
    self->dateTime_ = dateTime;
    self->displayFormat_ = displayFormat;
  }
  return self;
}

- (id)init {
  if (self = [super init]) {
    dateTime_ = YES;
    supportRelativeDates_ = YES;
    yearMonthDisplay_ = [[RAREUTSimpleDateFormatEx alloc] initWithNSString:@"MMM yyyy"];
  }
  return self;
}

- (BOOL)isCustomConverter {
  return YES;
}

- (NSString *)dateToStringWithJavaUtilDate:(JavaUtilDate *)date {
  if ([date isKindOfClass:[CCPBVSmartDateContext_ImperciseDate class]]) {
    return [((JavaUtilDate *) nil_chk(date)) description];
  }
  JavaTextDateFormat *df = displayFormat_;
  @synchronized (df) {
    return [((JavaTextDateFormat *) nil_chk(df)) formatWithJavaUtilDate:date];
  }
}

- (RAREDateContext *)createWithJavaTextDateFormat:(JavaTextDateFormat *)iformat
                           withJavaTextDateFormat:(JavaTextDateFormat *)dformat {
  if (iformat != nil) {
    return [super createWithJavaTextDateFormat:iformat withJavaTextDateFormat:(dformat == nil) ? displayFormat_ : dformat];
  }
  return [[CCPBVSmartDateContext alloc] initWithBoolean:dateTime_ withJavaTextDateFormat:(dformat == nil) ? displayFormat_ : dformat];
}

- (RAREDateContext *)createWithJavaTextDateFormatArray:(IOSObjectArray *)iformat
                                withJavaTextDateFormat:(JavaTextDateFormat *)dformat {
  if (iformat != nil) {
    return [super createWithJavaTextDateFormatArray:iformat withJavaTextDateFormat:(dformat == nil) ? displayFormat_ : dformat];
  }
  return [[CCPBVSmartDateContext alloc] initWithBoolean:dateTime_ withJavaTextDateFormat:(dformat == nil) ? displayFormat_ : dformat];
}

- (JavaUtilDate *)dateFromStringWithNSString:(NSString *)value {
  int len = (value == nil) ? 0 : [value sequenceLength];
  if (len == 0) {
    return nil;
  }
  if (![JavaLangCharacter isDigitWithChar:[((NSString *) nil_chk(value)) charAtWithInt:0]]) {
    JavaUtilDate *date = supportRelativeDates_ ? [RAREUTHelper createDateWithNSString:value] : nil;
    if (date == nil) {
      return [[CCPBVSmartDateContext_ImperciseDate alloc] initWithNSString:value withLong:[JavaLangSystem currentTimeMillis] withJavaTextDateFormat:nil];
    }
    return date;
  }
  if (len < 8) {
    return [[CCPBVSmartDateContext_ImperciseDate alloc] initWithNSString:value withLong:[CCPBVSmartDateContext getPartialDateWithNSString:value] withJavaTextDateFormat:yearMonthDisplay_];
  }
  JavaUtilCalendar *cal = [JavaUtilCalendar getInstance];
  if (![RAREUTHelper setDateTimeWithNSString:value withJavaUtilCalendar:cal withBoolean:dateTime_]) {
    return [[CCPBVSmartDateContext_ImperciseDate alloc] initWithNSString:value withLong:[CCPBVSmartDateContext getPartialDateWithNSString:value] withJavaTextDateFormat:yearMonthDisplay_];
  }
  return [((JavaUtilCalendar *) nil_chk(cal)) getTime];
}

+ (long long int)getPartialDateWithNSString:(NSString *)value {
  JavaUtilCalendar *c = [JavaUtilCalendar getInstance];
  @try {
    [((JavaUtilCalendar *) nil_chk(c)) setTimeInMillisWithLong:0];
    int len = [((NSString *) nil_chk(value)) sequenceLength];
    if (len > 3) {
      [c setWithInt:JavaUtilCalendar_YEAR withInt:[RAREUTSNumber intValueWithNSString:value]];
    }
    if (len > 6) {
      int n = [value indexOf:'-'];
      if (n != -1) {
        [c setWithInt:JavaUtilCalendar_MONTH withInt:[RAREUTSNumber intValueWithNSString:[value substring:n + 1]]];
      }
    }
  }
  @catch (JavaLangException *ignore) {
  }
  return [((JavaUtilCalendar *) nil_chk(c)) getTimeInMillis];
}

- (NSString *)parseAndFormatWithNSString:(NSString *)date {
  JavaUtilDate *d = [self dateFromStringWithNSString:date];
  return (d == nil) ? date : [self dateToStringWithJavaUtilDate:d];
}

- (BOOL)isSupportRelativeDates {
  return supportRelativeDates_;
}

- (void)setSupportRelativeDatesWithBoolean:(BOOL)supportRelativeDates {
  self->supportRelativeDates_ = supportRelativeDates;
}

- (void)copyAllFieldsTo:(CCPBVSmartDateContext *)other {
  [super copyAllFieldsTo:other];
  other->dateTime_ = dateTime_;
  other->supportRelativeDates_ = supportRelativeDates_;
  other->yearMonthDisplay_ = yearMonthDisplay_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "isCustomConverter", NULL, "Z", 0x1, NULL },
    { "dateToStringWithJavaUtilDate:", NULL, "LNSString", 0x1, NULL },
    { "createWithJavaTextDateFormat:withJavaTextDateFormat:", NULL, "LRAREDateContext", 0x1, NULL },
    { "createWithJavaTextDateFormatArray:withJavaTextDateFormat:", NULL, "LRAREDateContext", 0x1, NULL },
    { "dateFromStringWithNSString:", NULL, "LJavaUtilDate", 0x1, NULL },
    { "getPartialDateWithNSString:", NULL, "J", 0x8, NULL },
    { "parseAndFormatWithNSString:", NULL, "LNSString", 0x1, NULL },
    { "isSupportRelativeDates", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "dateTime_", NULL, 0x4, "Z" },
    { "yearMonthDisplay_", NULL, 0x4, "LJavaTextDateFormat" },
  };
  static J2ObjcClassInfo _CCPBVSmartDateContext = { "SmartDateContext", "com.sparseware.bellavista", NULL, 0x1, 8, methods, 2, fields, 0, NULL};
  return &_CCPBVSmartDateContext;
}

@end
@implementation CCPBVSmartDateContext_ImperciseDate

- (id)initWithNSString:(NSString *)value
              withLong:(long long int)time
withJavaTextDateFormat:(JavaTextDateFormat *)format {
  if (self = [super initWithLong:time]) {
    stringValue_ = value;
    toStringFormat_ = format;
  }
  return self;
}

- (NSString *)description {
  if (toStringFormat_ != nil) {
    @synchronized (toStringFormat_) {
      @try {
        stringValue_ = [toStringFormat_ formatWithJavaUtilDate:self];
      }
      @catch (JavaLangException *e) {
        [((JavaLangException *) nil_chk(e)) printStackTrace];
      }
      toStringFormat_ = nil;
    }
  }
  return stringValue_;
}

- (void)copyAllFieldsTo:(CCPBVSmartDateContext_ImperciseDate *)other {
  [super copyAllFieldsTo:other];
  other->stringValue_ = stringValue_;
  other->toStringFormat_ = toStringFormat_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcClassInfo _CCPBVSmartDateContext_ImperciseDate = { "ImperciseDate", "com.sparseware.bellavista", "SmartDateContext", 0x8, 0, NULL, 0, NULL, 0, NULL};
  return &_CCPBVSmartDateContext_ImperciseDate;
}

@end
