//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/ResultsView.java
//
//  Created by decoteaud on 3/12/15.
//

#ifndef _ComSparsewareBellavistaResultsView_H_
#define _ComSparsewareBellavistaResultsView_H_

#import "JreEmulation.h"
#include "java/lang/Enum.h"

typedef enum {
  ComSparsewareBellavistaResultsView_CHARTS = 0,
  ComSparsewareBellavistaResultsView_DOCUMENT = 1,
  ComSparsewareBellavistaResultsView_SPREADSHEET = 2,
  ComSparsewareBellavistaResultsView_TRENDS = 3,
} ComSparsewareBellavistaResultsView;

@interface ComSparsewareBellavistaResultsViewEnum : JavaLangEnum < NSCopying > {
}
+ (ComSparsewareBellavistaResultsViewEnum *)CHARTS;
+ (ComSparsewareBellavistaResultsViewEnum *)DOCUMENT;
+ (ComSparsewareBellavistaResultsViewEnum *)SPREADSHEET;
+ (ComSparsewareBellavistaResultsViewEnum *)TRENDS;
+ (IOSObjectArray *)values;
+ (ComSparsewareBellavistaResultsViewEnum *)valueOfWithNSString:(NSString *)name;
- (id)copyWithZone:(NSZone *)zone;
- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal;
@end

#endif // _ComSparsewareBellavistaResultsView_H_
