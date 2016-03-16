//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/TrendPanel.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVTrendPanel_H_
#define _CCPBVTrendPanel_H_

@class IOSFloatArray;
@class IOSObjectArray;
@class JavaUtilDate;
@class RARERenderableDataItem;
@class RARETableViewer;
@class RAREUIColor;
@class RAREUIFont;
@class RAREUTJSONArray;
@class RAREWindowViewer;
@protocol JavaUtilMap;
@protocol RAREiContainer;
@protocol RAREiPlatformGraphics;
@protocol RAREiPlatformIcon;

#import "JreEmulation.h"
#include "com/appnativa/rare/ui/aPlatformIcon.h"

@interface CCPBVTrendPanel : NSObject {
 @public
  NSString *name_;
  NSString *title_;
  id<JavaUtilMap> keyMap_;
  id<JavaUtilMap> trends_;
  id<JavaUtilMap> peers_;
  BOOL reverseChronologicalOrder_;
}

+ (id<JavaUtilMap>)trendMap;
+ (void)setTrendMap:(id<JavaUtilMap>)trendMap;
+ (RARERenderableDataItem *)dateLabel;
+ (void)setDateLabel:(RARERenderableDataItem *)dateLabel;
+ (RARERenderableDataItem *)valueLabel;
+ (void)setValueLabel:(RARERenderableDataItem *)valueLabel;
+ (RARERenderableDataItem *)trendLabel;
+ (void)setTrendLabel:(RARERenderableDataItem *)trendLabel;
+ (RAREUIFont *)nameFont;
+ (void)setNameFont:(RAREUIFont *)nameFont;
+ (RAREUIFont *)timePeriodFont;
+ (void)setTimePeriodFont:(RAREUIFont *)timePeriodFont;
+ (int)timePeriodFontHeight;
+ (int *)timePeriodFontHeightRef;
- (id)initWithNSString:(NSString *)name
          withNSString:(NSString *)title
   withRAREUTJSONArray:(RAREUTJSONArray *)keys
           withBoolean:(BOOL)reverseChronologicalOrder;
- (BOOL)addTrendWithNSString:(NSString *)key
            withJavaUtilDate:(JavaUtilDate *)date
  withRARERenderableDataItem:(RARERenderableDataItem *)valueItem;
- (BOOL)addTrendWithNSString:(NSString *)key
            withJavaUtilDate:(JavaUtilDate *)date
                withNSString:(NSString *)name
  withRARERenderableDataItem:(RARERenderableDataItem *)valueItem;
- (void)clear;
- (void)removePeers;
- (void)popuplateTableWithRARETableViewer:(RARETableViewer *)table
                          withJavaUtilMap:(id<JavaUtilMap>)layout;
- (NSString *)popuplateFormWithRAREiContainer:(id<RAREiContainer>)fv;
+ (void)setupLabelsWithRAREWindowViewer:(RAREWindowViewer *)w;
- (void)copyAllFieldsTo:(CCPBVTrendPanel *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVTrendPanel, name_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel, title_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel, keyMap_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel, trends_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel, peers_, id<JavaUtilMap>)

typedef CCPBVTrendPanel ComSparsewareBellavistaTrendPanel;

@interface CCPBVTrendPanel_Trend : RAREaPlatformIcon {
 @public
  IOSFloatArray *numbers_;
  IOSObjectArray *colors_;
  int index_;
  NSString *name_;
  JavaUtilDate *date_;
  JavaUtilDate *startDate_;
  NSString *value_;
  int segmentLength_;
  int startingY_;
  BOOL drawTimePeriod_;
  NSString *timePeroid_;
  float timePeriodWidth_;
  BOOL reverseChronologicalOrder_;
  NSString *peer_;
}

- (id)initWithNSString:(NSString *)name
           withBoolean:(BOOL)reverseChronologicalOrder;
- (void)setPeerWithNSString:(NSString *)peer;
- (void)addWithJavaUtilDate:(JavaUtilDate *)date
               withNSString:(NSString *)value
            withRAREUIColor:(RAREUIColor *)color;
- (void)calculateStartingYPositionm;
- (id<RAREiPlatformIcon>)getDisabledVersion;
- (int)getIconHeight;
- (int)getIconWidth;
- (void)paintWithRAREiPlatformGraphics:(id<RAREiPlatformGraphics>)g
                             withFloat:(float)x
                             withFloat:(float)y
                             withFloat:(float)width
                             withFloat:(float)height;
- (BOOL)valueNeedsHTMLEncoding;
- (void)calculateTimeperiod;
- (BOOL)isDrawTimePeriod;
- (void)setDrawTimePeriodWithBoolean:(BOOL)drawTimePeriod;
- (void)copyAllFieldsTo:(CCPBVTrendPanel_Trend *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, numbers_, IOSFloatArray *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, colors_, IOSObjectArray *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, name_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, date_, JavaUtilDate *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, startDate_, JavaUtilDate *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, value_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, timePeroid_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVTrendPanel_Trend, peer_, NSString *)

#endif // _CCPBVTrendPanel_H_
