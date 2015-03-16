//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/ResultsView.java
//
//  Created by decoteaud on 3/12/15.
//

#include "IOSClass.h"
#include "com/sparseware/bellavista/ResultsView.h"
#include "java/lang/IllegalArgumentException.h"


static ComSparsewareBellavistaResultsViewEnum *ComSparsewareBellavistaResultsViewEnum_CHARTS;
static ComSparsewareBellavistaResultsViewEnum *ComSparsewareBellavistaResultsViewEnum_DOCUMENT;
static ComSparsewareBellavistaResultsViewEnum *ComSparsewareBellavistaResultsViewEnum_SPREADSHEET;
static ComSparsewareBellavistaResultsViewEnum *ComSparsewareBellavistaResultsViewEnum_TRENDS;
IOSObjectArray *ComSparsewareBellavistaResultsViewEnum_values;

@implementation ComSparsewareBellavistaResultsViewEnum

+ (ComSparsewareBellavistaResultsViewEnum *)CHARTS {
  return ComSparsewareBellavistaResultsViewEnum_CHARTS;
}
+ (ComSparsewareBellavistaResultsViewEnum *)DOCUMENT {
  return ComSparsewareBellavistaResultsViewEnum_DOCUMENT;
}
+ (ComSparsewareBellavistaResultsViewEnum *)SPREADSHEET {
  return ComSparsewareBellavistaResultsViewEnum_SPREADSHEET;
}
+ (ComSparsewareBellavistaResultsViewEnum *)TRENDS {
  return ComSparsewareBellavistaResultsViewEnum_TRENDS;
}

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [ComSparsewareBellavistaResultsViewEnum class]) {
    ComSparsewareBellavistaResultsViewEnum_CHARTS = [[ComSparsewareBellavistaResultsViewEnum alloc] initWithNSString:@"CHARTS" withInt:0];
    ComSparsewareBellavistaResultsViewEnum_DOCUMENT = [[ComSparsewareBellavistaResultsViewEnum alloc] initWithNSString:@"DOCUMENT" withInt:1];
    ComSparsewareBellavistaResultsViewEnum_SPREADSHEET = [[ComSparsewareBellavistaResultsViewEnum alloc] initWithNSString:@"SPREADSHEET" withInt:2];
    ComSparsewareBellavistaResultsViewEnum_TRENDS = [[ComSparsewareBellavistaResultsViewEnum alloc] initWithNSString:@"TRENDS" withInt:3];
    ComSparsewareBellavistaResultsViewEnum_values = [[IOSObjectArray alloc] initWithObjects:(id[]){ ComSparsewareBellavistaResultsViewEnum_CHARTS, ComSparsewareBellavistaResultsViewEnum_DOCUMENT, ComSparsewareBellavistaResultsViewEnum_SPREADSHEET, ComSparsewareBellavistaResultsViewEnum_TRENDS, nil } count:4 type:[IOSClass classWithClass:[ComSparsewareBellavistaResultsViewEnum class]]];
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithArray:ComSparsewareBellavistaResultsViewEnum_values];
}

+ (ComSparsewareBellavistaResultsViewEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < [ComSparsewareBellavistaResultsViewEnum_values count]; i++) {
    ComSparsewareBellavistaResultsViewEnum *e = ComSparsewareBellavistaResultsViewEnum_values->buffer_[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static const char *superclass_type_args[] = {"LComSparsewareBellavistaResultsViewEnum"};
  static J2ObjcClassInfo _ComSparsewareBellavistaResultsViewEnum = { "ResultsView", "com.sparseware.bellavista", NULL, 0x4011, 0, NULL, 0, NULL, 1, superclass_type_args};
  return &_ComSparsewareBellavistaResultsViewEnum;
}

@end
