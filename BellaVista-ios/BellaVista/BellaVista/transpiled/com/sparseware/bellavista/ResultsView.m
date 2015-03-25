//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/ResultsView.java
//
//  Created by decoteaud on 3/24/15.
//

#include "IOSClass.h"
#include "com/sparseware/bellavista/ResultsView.h"
#include "java/lang/IllegalArgumentException.h"


static CCPBVResultsViewEnum *CCPBVResultsViewEnum_CHARTS;
static CCPBVResultsViewEnum *CCPBVResultsViewEnum_DOCUMENT;
static CCPBVResultsViewEnum *CCPBVResultsViewEnum_SPREADSHEET;
static CCPBVResultsViewEnum *CCPBVResultsViewEnum_TRENDS;
static CCPBVResultsViewEnum *CCPBVResultsViewEnum_CUSTOM_1;
static CCPBVResultsViewEnum *CCPBVResultsViewEnum_CUSTOM_2;
static CCPBVResultsViewEnum *CCPBVResultsViewEnum_CUSTOM_3;
IOSObjectArray *CCPBVResultsViewEnum_values;

@implementation CCPBVResultsViewEnum

+ (CCPBVResultsViewEnum *)CHARTS {
  return CCPBVResultsViewEnum_CHARTS;
}
+ (CCPBVResultsViewEnum *)DOCUMENT {
  return CCPBVResultsViewEnum_DOCUMENT;
}
+ (CCPBVResultsViewEnum *)SPREADSHEET {
  return CCPBVResultsViewEnum_SPREADSHEET;
}
+ (CCPBVResultsViewEnum *)TRENDS {
  return CCPBVResultsViewEnum_TRENDS;
}
+ (CCPBVResultsViewEnum *)CUSTOM_1 {
  return CCPBVResultsViewEnum_CUSTOM_1;
}
+ (CCPBVResultsViewEnum *)CUSTOM_2 {
  return CCPBVResultsViewEnum_CUSTOM_2;
}
+ (CCPBVResultsViewEnum *)CUSTOM_3 {
  return CCPBVResultsViewEnum_CUSTOM_3;
}

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [CCPBVResultsViewEnum class]) {
    CCPBVResultsViewEnum_CHARTS = [[CCPBVResultsViewEnum alloc] initWithNSString:@"CHARTS" withInt:0];
    CCPBVResultsViewEnum_DOCUMENT = [[CCPBVResultsViewEnum alloc] initWithNSString:@"DOCUMENT" withInt:1];
    CCPBVResultsViewEnum_SPREADSHEET = [[CCPBVResultsViewEnum alloc] initWithNSString:@"SPREADSHEET" withInt:2];
    CCPBVResultsViewEnum_TRENDS = [[CCPBVResultsViewEnum alloc] initWithNSString:@"TRENDS" withInt:3];
    CCPBVResultsViewEnum_CUSTOM_1 = [[CCPBVResultsViewEnum alloc] initWithNSString:@"CUSTOM_1" withInt:4];
    CCPBVResultsViewEnum_CUSTOM_2 = [[CCPBVResultsViewEnum alloc] initWithNSString:@"CUSTOM_2" withInt:5];
    CCPBVResultsViewEnum_CUSTOM_3 = [[CCPBVResultsViewEnum alloc] initWithNSString:@"CUSTOM_3" withInt:6];
    CCPBVResultsViewEnum_values = [[IOSObjectArray alloc] initWithObjects:(id[]){ CCPBVResultsViewEnum_CHARTS, CCPBVResultsViewEnum_DOCUMENT, CCPBVResultsViewEnum_SPREADSHEET, CCPBVResultsViewEnum_TRENDS, CCPBVResultsViewEnum_CUSTOM_1, CCPBVResultsViewEnum_CUSTOM_2, CCPBVResultsViewEnum_CUSTOM_3, nil } count:7 type:[IOSClass classWithClass:[CCPBVResultsViewEnum class]]];
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithArray:CCPBVResultsViewEnum_values];
}

+ (CCPBVResultsViewEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < [CCPBVResultsViewEnum_values count]; i++) {
    CCPBVResultsViewEnum *e = CCPBVResultsViewEnum_values->buffer_[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static const char *superclass_type_args[] = {"LCCPBVResultsViewEnum"};
  static J2ObjcClassInfo _CCPBVResultsViewEnum = { "ResultsView", "com.sparseware.bellavista", NULL, 0x4011, 0, NULL, 0, NULL, 1, superclass_type_args};
  return &_CCPBVResultsViewEnum;
}

@end
