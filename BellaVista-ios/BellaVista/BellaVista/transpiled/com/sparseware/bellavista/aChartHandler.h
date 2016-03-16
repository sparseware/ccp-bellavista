//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/aChartHandler.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVaChartHandler_H_
#define _CCPBVaChartHandler_H_

@class JavaUtilLinkedHashMap;
@class RAREChartDataItem;
@class RAREChartViewer;
@class RARERenderableDataItem;
@class RARESPOTChart;
@class RARETableViewer;
@class RAREUTJSONObject;
@class RAREUTNumberRange;
@protocol JavaUtilList;
@protocol JavaUtilMap;
@protocol RAREiContainer;
@protocol RAREiFormViewer;
@protocol RAREiWidget;

#import "JreEmulation.h"

@interface CCPBVaChartHandler : NSObject {
 @public
  RAREUTJSONObject *chartsInfo_;
  id<JavaUtilMap> chartTypeMap_;
  int nameColumn_;
  int dateColumn_;
  int valueColumn_;
  NSString *eventHandlerClass_;
  int chartPageSize_;
  int chartPoints_;
}

+ (float)MIN_ANGLED_LABEL_HEIGHT;
+ (float *)MIN_ANGLED_LABEL_HEIGHTRef;
+ (NSString *)TOTAL_POINT_LABELS_SIZE;
- (id)initWithNSString:(NSString *)eventHandlerClass
  withRAREUTJSONObject:(RAREUTJSONObject *)chartsInfo
               withInt:(int)nameColumn
               withInt:(int)dateColumn
               withInt:(int)valueColumn;
- (BOOL)canZoomInWithRAREChartViewer:(RAREChartViewer *)cv;
- (BOOL)canZoomOutWithRAREChartViewer:(RAREChartViewer *)cv;
- (RAREChartViewer *)createChartWithRAREiFormViewer:(id<RAREiFormViewer>)fv
                                       withNSString:(NSString *)key
                                            withInt:(int)viewers
                              withRAREChartDataItem:(RAREChartDataItem *)series;
- (JavaUtilLinkedHashMap *)createSeriesWithJavaUtilList:(id<JavaUtilList>)rows
                                        withJavaUtilMap:(id<JavaUtilMap>)keys
                                                withInt:(int)rangeColumn;
- (void)adjustForSizeWithRAREChartViewer:(RAREChartViewer *)cv;
- (int)getDomainAngleBasedOnPlotSizeWithRAREChartViewer:(RAREChartViewer *)cv;
- (BOOL)shouldPlotLabelsBeVisibleWithRAREChartViewer:(RAREChartViewer *)cv;
- (RAREChartDataItem *)createSeriesWithJavaUtilList:(id<JavaUtilList>)rows
                                       withNSString:(NSString *)key
                                            withInt:(int)rangeColumn;
- (RAREChartDataItem *)createSeriesFromSpreadSheetWithRARETableViewer:(RARETableViewer *)table
                                                         withNSString:(NSString *)key
                                           withRARERenderableDataItem:(RARERenderableDataItem *)row
                                                              withInt:(int)rangeColumn;
- (int)getChartPageSize;
- (int)getChartPoints;
- (void)resetChartPoints;
- (void)setChartPageSizeWithInt:(int)chartPageSize;
- (void)setChartPointsWithInt:(int)chartPoints;
- (void)updateZoomButtonsWithRAREiContainer:(id<RAREiContainer>)fv;
- (RAREUTNumberRange *)getZoomRangeWithRAREChartViewer:(RAREChartViewer *)cv;
- (BOOL)zoomWithRAREChartViewer:(RAREChartViewer *)cv
                    withBoolean:(BOOL)inArg
                    withBoolean:(BOOL)update;
- (void)zoomWithRAREiContainer:(id<RAREiContainer>)fv
                   withBoolean:(BOOL)inArg;
- (void)configureChartWithRAREiWidget:(id<RAREiWidget>)context
                    withRARESPOTChart:(RARESPOTChart *)cfg
                         withNSString:(NSString *)key
                withRAREChartDataItem:(RAREChartDataItem *)series;
- (NSString *)getChartTypeWithNSString:(NSString *)key;
- (NSString *)getRangeTitleWithNSString:(NSString *)title;
+ (NSNumber *)createNumberWithNSString:(NSString *)type
                          withNSString:(NSString *)value;
- (void)copyAllFieldsTo:(CCPBVaChartHandler *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaChartHandler, chartsInfo_, RAREUTJSONObject *)
J2OBJC_FIELD_SETTER(CCPBVaChartHandler, chartTypeMap_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVaChartHandler, eventHandlerClass_, NSString *)

typedef CCPBVaChartHandler ComSparsewareBellavistaAChartHandler;

#endif // _CCPBVaChartHandler_H_
