//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/aChartHandler.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/spot/Chart.h"
#include "com/appnativa/rare/spot/DataItem.h"
#include "com/appnativa/rare/spot/Font.h"
#include "com/appnativa/rare/spot/GridCell.h"
#include "com/appnativa/rare/spot/ItemDescription.h"
#include "com/appnativa/rare/spot/Plot.h"
#include "com/appnativa/rare/spot/Widget.h"
#include "com/appnativa/rare/ui/Column.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/ScreenUtils.h"
#include "com/appnativa/rare/ui/UIColor.h"
#include "com/appnativa/rare/ui/UIDimension.h"
#include "com/appnativa/rare/ui/UIFont.h"
#include "com/appnativa/rare/ui/UIFontMetrics.h"
#include "com/appnativa/rare/ui/UIScreen.h"
#include "com/appnativa/rare/ui/chart/ChartAxis.h"
#include "com/appnativa/rare/ui/chart/ChartDataItem.h"
#include "com/appnativa/rare/ui/chart/ChartDefinition.h"
#include "com/appnativa/rare/viewer/ChartViewer.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/TableViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/SPOTBoolean.h"
#include "com/appnativa/spot/SPOTPrintableString.h"
#include "com/appnativa/spot/SPOTReal.h"
#include "com/appnativa/spot/SPOTSet.h"
#include "com/appnativa/util/CharScanner.h"
#include "com/appnativa/util/NumberRange.h"
#include "com/appnativa/util/SNumber.h"
#include "com/appnativa/util/StringCache.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/Settings.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/aChartHandler.h"
#include "java/lang/Integer.h"
#include "java/lang/Math.h"
#include "java/util/Collections.h"
#include "java/util/Date.h"
#include "java/util/LinkedHashMap.h"
#include "java/util/List.h"
#include "java/util/Map.h"

@implementation CCPBVaChartHandler

static float CCPBVaChartHandler_MIN_ANGLED_LABEL_HEIGHT_;
static NSString * CCPBVaChartHandler_TOTAL_POINT_LABELS_SIZE_ = @"maxValueCharCount";

+ (float)MIN_ANGLED_LABEL_HEIGHT {
  return CCPBVaChartHandler_MIN_ANGLED_LABEL_HEIGHT_;
}

+ (float *)MIN_ANGLED_LABEL_HEIGHTRef {
  return &CCPBVaChartHandler_MIN_ANGLED_LABEL_HEIGHT_;
}

+ (NSString *)TOTAL_POINT_LABELS_SIZE {
  return CCPBVaChartHandler_TOTAL_POINT_LABELS_SIZE_;
}

- (id)initWithNSString:(NSString *)eventHandlerClass
  withRAREUTJSONObject:(RAREUTJSONObject *)chartsInfo
               withInt:(int)nameColumn
               withInt:(int)dateColumn
               withInt:(int)valueColumn {
  if (self = [super init]) {
    chartTypeMap_ = [[JavaUtilLinkedHashMap alloc] initWithInt:4];
    chartPageSize_ = 7;
    chartPoints_ = 7;
    self->chartsInfo_ = chartsInfo;
    self->nameColumn_ = nameColumn;
    self->dateColumn_ = dateColumn;
    self->valueColumn_ = valueColumn;
    self->eventHandlerClass_ = eventHandlerClass;
  }
  return self;
}

- (BOOL)canZoomInWithRAREChartViewer:(RAREChartViewer *)cv {
  RAREChartDefinition *cd = [((RAREChartViewer *) nil_chk(cv)) getChartDefinition];
  int count = [((RAREChartDefinition *) nil_chk(cd)) getSeriesCount];
  for (int n = 0; n < count; n++) {
    if ([((RAREChartDataItem *) nil_chk([cd getSeriesWithInt:n])) size] > chartPageSize_) {
      return YES;
    }
  }
  return NO;
}

- (BOOL)canZoomOutWithRAREChartViewer:(RAREChartViewer *)cv {
  RAREChartDefinition *cd = [((RAREChartViewer *) nil_chk(cv)) getChartDefinition];
  int count = [((RAREChartDefinition *) nil_chk(cd)) getSeriesCount];
  for (int n = 0; n < count; n++) {
    if ([((RAREChartDataItem *) nil_chk([cd getSeriesWithInt:n])) isFiltered]) {
      return YES;
    }
  }
  return NO;
}

- (RAREChartViewer *)createChartWithRAREiFormViewer:(id<RAREiFormViewer>)fv
                                       withNSString:(NSString *)key
                                            withInt:(int)viewers
                              withRAREChartDataItem:(RAREChartDataItem *)series {
  RARESPOTChart *cfg = [[RARESPOTChart alloc] init];
  RAREChartViewer *viewer;
  [cfg setEventHandlerWithNSString:@"onFling" withNSString:[NSString stringWithFormat:@"class:%@#onChartFling", eventHandlerClass_]];
  [cfg setEventHandlerWithNSString:@"onScale" withNSString:[NSString stringWithFormat:@"class:%@#onChartScale", eventHandlerClass_]];
  [cfg setEventHandlerWithNSString:@"onResize" withNSString:[NSString stringWithFormat:@"class:%@#onChartResize", eventHandlerClass_]];
  int len = [((RAREChartDataItem *) nil_chk(series)) size];
  if (viewers == 1) {
    [((SPOTPrintableString *) nil_chk(((RARESPOTFont *) nil_chk(cfg->font_))->size_)) setValueWithNSString:@"-2"];
    [((RARESPOTPlot_CLabels *) nil_chk(((RARESPOTPlot *) nil_chk([cfg getPlotReference]))->labels_)) setValueWithInt:RARESPOTPlot_CLabels_linked_data];
  }
  else {
    [((SPOTPrintableString *) nil_chk(((RARESPOTFont *) nil_chk(cfg->font_))->size_)) setValueWithNSString:@"-4"];
    [((SPOTBoolean *) nil_chk(((RARESPOTDataItem *) nil_chk(cfg->domainAxis_))->visible_)) setValueWithBoolean:NO];
  }
  [((SPOTPrintableString *) nil_chk(((RARESPOTDataItem *) nil_chk(cfg->domainAxis_))->fgColor_)) setValueWithNSString:@"darkBorder"];
  [((RARESPOTDataItem *) nil_chk(cfg->rangeAxis_))->fgColor_ setValueWithNSString:@"darkBorder"];
  [cfg->domainAxis_ spot_setAttributeWithNSString:@"labelColor" withNSString:@"Rare.Chart.foreground"];
  [cfg->rangeAxis_ spot_setAttributeWithNSString:@"labelColor" withNSString:@"Rare.Chart.foreground"];
  [((SPOTPrintableString *) nil_chk(((RARESPOTPlot *) nil_chk([cfg getPlotReference]))->borderColor_)) setValueWithNSString:@"darkBorder"];
  [((RARESPOTWidget_CVerticalAlign *) nil_chk(cfg->verticalAlign_)) setValueWithInt:RARESPOTWidget_CVerticalAlign_full];
  [cfg setHorizontalAlignmentWithInt:RARESPOTWidget_CHorizontalAlign_full];
  viewer = [[RAREChartViewer alloc] initWithRAREiContainer:fv];
  [self configureChartWithRAREiWidget:viewer withRARESPOTChart:cfg withNSString:key withRAREChartDataItem:series];
  if (len > chartPoints_) {
    for (int i = len - chartPoints_; i < len; i++) {
      [series addIndexToFilteredListWithInt:i];
    }
  }
  [viewer configureWithRARESPOTViewer:cfg];
  (void) [viewer addSeriesWithRAREChartDataItem:series];
  NSString *range = (NSString *) check_class_cast([series getLinkedData], [NSString class]);
  if (range != nil) {
    (void) [viewer addRangeMarkerWithNSString:range withChar:'-'];
  }
  [viewer setLinkedDataWithId:key];
  [viewer rebuildChart];
  [viewer setPlotValuesVisibleWithBoolean:NO];
  return viewer;
}

- (JavaUtilLinkedHashMap *)createSeriesWithJavaUtilList:(id<JavaUtilList>)rows
                                        withJavaUtilMap:(id<JavaUtilMap>)keys
                                                withInt:(int)rangeColumn {
  int dateCol = dateColumn_;
  int nameCol = nameColumn_;
  int valueCol = valueColumn_;
  int len = [((id<JavaUtilList>) nil_chk(rows)) size];
  RARERenderableDataItem *row, *item;
  JavaUtilLinkedHashMap *seriesMap = [[JavaUtilLinkedHashMap alloc] initWithInt:[((id<JavaUtilMap>) nil_chk(keys)) size]];
  for (int i = 0; i < len; i++) {
    row = [rows getWithInt:i];
    item = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:nameCol];
    NSString *key = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getLinkedData], [NSString class]);
    NSString *type = [keys getWithId:key];
    if (type == nil) {
      continue;
    }
    RAREChartDataItem *series = [seriesMap getWithId:key];
    if (series == nil) {
      NSString *s = (NSString *) check_class_cast([item getValue], [NSString class]);
      series = [RAREaChartViewer createSeriesWithNSString:s];
      [((RAREChartDataItem *) nil_chk(series)) setTitleWithNSString:s];
      (void) [seriesMap putWithId:key withId:series];
      if (rangeColumn > -1) {
        s = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([row getWithInt:rangeColumn])) getValue], [NSString class]);
        if ((s != nil) && ([s sequenceLength] > 0)) {
          [series setLinkedDataWithId:s];
        }
      }
    }
    JavaUtilDate *date = (JavaUtilDate *) check_class_cast([((RARERenderableDataItem *) nil_chk([row getWithInt:dateCol])) getValue], [JavaUtilDate class]);
    item = [row getWithInt:valueCol];
    NSString *value = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getValue], [NSString class]);
    NSNumber *num = [CCPBVaChartHandler createNumberWithNSString:type withNSString:value];
    if (num != nil) {
      RAREChartDataItem *di;
      [((RAREChartDataItem *) nil_chk(series)) addWithId:di = [RAREaChartViewer createSeriesValueWithId:date withId:num]];
      [((RAREChartDataItem *) nil_chk(di)) setForegroundWithRAREUIColor:[item getForeground]];
      int n = [((NSString *) nil_chk(value)) indexOf:' '];
      if (n != -1) {
        value = [value substring:0 endIndex:n];
      }
      [di setLinkedDataWithId:value];
    }
  }
  return seriesMap;
}

- (void)adjustForSizeWithRAREChartViewer:(RAREChartViewer *)cv {
  BOOL cardstack = [CCPBVUtils isCardStack];
  int oangle = [((RAREChartAxis *) nil_chk([((RAREChartViewer *) nil_chk(cv)) getDomainAxis])) getAngle];
  BOOL oshow = [((RAREChartDefinition *) nil_chk([cv getChartDefinition])) isShowPlotLabels];
  int angle = cardstack ? 0 : [self getDomainAngleBasedOnPlotSizeWithRAREChartViewer:cv];
  BOOL show = [self shouldPlotLabelsBeVisibleWithRAREChartViewer:cv];
  if ((show != oshow) || (angle != oangle)) {
    RAREChartDefinition *cd = [cv getChartDefinition];
    [((RAREChartDefinition *) nil_chk(cd)) setShowPlotLabelsWithBoolean:show];
    [((RAREChartAxis *) nil_chk([cd getDomainAxis])) setAngleWithInt:angle];
    [cv rebuildChart];
  }
}

- (int)getDomainAngleBasedOnPlotSizeWithRAREChartViewer:(RAREChartViewer *)cv {
  RAREChartDefinition *cd = [((RAREChartViewer *) nil_chk(cv)) getChartDefinition];
  RAREUIDimension *size = [cv getPlotAreaSize];
  if ([cv getCustomPropertyWithId:@"bv_dont_mess_with_angle"] == nil) {
    if (((RAREUIDimension *) nil_chk(size))->height_ < CCPBVaChartHandler_MIN_ANGLED_LABEL_HEIGHT_) {
      return 0;
    }
    else {
      return -90;
    }
  }
  return [((RAREChartAxis *) nil_chk([((RAREChartDefinition *) nil_chk(cd)) getDomainAxis])) getAngle];
}

- (BOOL)shouldPlotLabelsBeVisibleWithRAREChartViewer:(RAREChartViewer *)cv {
  RAREChartDefinition *cd = [((RAREChartViewer *) nil_chk(cv)) getChartDefinition];
  RAREUIDimension *size = [cv getPlotAreaSize];
  if ([((RAREChartDefinition *) nil_chk(cd)) getSeriesCount] == 1) {
    RAREChartDataItem *series = [cd getSeriesWithInt:0];
    JavaLangInteger *pointWidth = (JavaLangInteger *) check_class_cast([((RAREChartDataItem *) nil_chk(series)) getCustomPropertyWithId:CCPBVaChartHandler_TOTAL_POINT_LABELS_SIZE_], [JavaLangInteger class]);
    if (pointWidth == nil) {
      RAREUIFontMetrics *fm = [RAREUIFontMetrics getMetricsWithRAREUIFont:[cv getFont]];
      int len = [series size];
      int count = 0;
      int w = 0;
      int mw = 0;
      for (int i = 0; i < len; i++) {
        NSString *s = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([series getWithInt:i])) getLinkedData], [NSString class]);
        if (s != nil) {
          w = [((RAREUIFontMetrics *) nil_chk(fm)) stringWidthWithNSString:s];
          count += w;
          if (w > mw) {
            mw = w;
          }
        }
      }
      count += mw * 6;
      pointWidth = [JavaLangInteger valueOfWithInt:count];
      (void) [series setCustomPropertyWithId:CCPBVaChartHandler_TOTAL_POINT_LABELS_SIZE_ withId:pointWidth];
    }
    return ((RAREUIDimension *) nil_chk(size))->width_ > [((JavaLangInteger *) nil_chk(pointWidth)) intValue];
  }
  else {
    return NO;
  }
}

- (RAREChartDataItem *)createSeriesWithJavaUtilList:(id<JavaUtilList>)rows
                                       withNSString:(NSString *)key
                                            withInt:(int)rangeColumn {
  NSString *type = [self getChartTypeWithNSString:key];
  id<JavaUtilMap> map = chartTypeMap_;
  [((id<JavaUtilMap>) nil_chk(map)) clear];
  (void) [map putWithId:key withId:type];
  return [((JavaUtilLinkedHashMap *) nil_chk([self createSeriesWithJavaUtilList:rows withJavaUtilMap:map withInt:rangeColumn])) getWithId:key];
}

- (RAREChartDataItem *)createSeriesFromSpreadSheetWithRARETableViewer:(RARETableViewer *)table
                                                         withNSString:(NSString *)key
                                           withRARERenderableDataItem:(RARERenderableDataItem *)row
                                                              withInt:(int)rangeColumn {
  int len = [((RARETableViewer *) nil_chk(table)) getColumnCount];
  NSString *s = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([((RARERenderableDataItem *) nil_chk(row)) getWithInt:0])) getValue], [NSString class]);
  RAREChartDataItem *series = [RAREaChartViewer createSeriesWithNSString:s];
  [((RAREChartDataItem *) nil_chk(series)) setTitleWithNSString:s];
  if (rangeColumn > -1) {
    s = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([row getWithInt:rangeColumn])) getValue], [NSString class]);
    if ((s != nil) && ([s sequenceLength] > 0)) {
      [series setLinkedDataWithId:s];
    }
  }
  NSString *type = [self getChartTypeWithNSString:key];
  for (int i = 0; i < len; i++) {
    RAREColumn *col = [table getColumnWithInt:i];
    if (![((RAREColumn *) nil_chk(col)) isVisible] || !([[col getLinkedData] isKindOfClass:[JavaUtilDate class]])) {
      continue;
    }
    JavaUtilDate *date = (JavaUtilDate *) check_class_cast([col getLinkedData], [JavaUtilDate class]);
    RARERenderableDataItem *item = [row getWithInt:i];
    s = (item == nil) ? nil : (NSString *) check_class_cast([item getValue], [NSString class]);
    NSNumber *num = (s == nil) ? nil : [CCPBVaChartHandler createNumberWithNSString:type withNSString:s];
    if (num != nil) {
      [series addWithId:[RAREaChartViewer createSeriesValueWithId:date withId:num]];
    }
  }
  return series;
}

- (int)getChartPageSize {
  return chartPageSize_;
}

- (int)getChartPoints {
  return chartPoints_;
}

- (void)resetChartPoints {
  chartPoints_ = chartPageSize_;
}

- (void)setChartPageSizeWithInt:(int)chartPageSize {
  self->chartPageSize_ = chartPageSize;
  self->chartPoints_ = chartPageSize;
}

- (void)setChartPointsWithInt:(int)chartPoints {
  self->chartPoints_ = chartPoints;
}

- (void)updateZoomButtonsWithRAREiContainer:(id<RAREiContainer>)fv {
  BOOL zoomin = NO;
  BOOL zoomout = NO;
  int min = 0;
  int max = 0;
  RAREChartViewer *cv;
  RAREStackPaneViewer *sp = (RAREStackPaneViewer *) check_class_cast([((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getViewerWithNSString:@"chartPaneStack"], [RAREStackPaneViewer class]);
  id<RAREiViewer> v = [((RAREStackPaneViewer *) nil_chk(sp)) getActiveViewer];
  if ([(id) v isKindOfClass:[RAREChartViewer class]]) {
    cv = (RAREChartViewer *) check_class_cast(v, [RAREChartViewer class]);
    if ([self canZoomInWithRAREChartViewer:cv]) {
      zoomin = YES;
    }
    if ([self canZoomOutWithRAREChartViewer:cv]) {
      zoomout = YES;
    }
    RAREUTNumberRange *r = [self getZoomRangeWithRAREChartViewer:cv];
    max = [((NSNumber *) nil_chk([((RAREUTNumberRange *) nil_chk(r)) getHighValue])) intValue];
    min = [((NSNumber *) nil_chk([r getLowValue])) intValue];
  }
  else if (v != nil) {
    id<JavaUtilList> widgets = [((id<RAREiContainer>) nil_chk([v getContainerViewer])) getWidgetList];
    for (id<RAREiWidget> __strong w in nil_chk(widgets)) {
      if ([(id) w isKindOfClass:[RAREChartViewer class]]) {
        cv = (RAREChartViewer *) check_class_cast(w, [RAREChartViewer class]);
        if ([self canZoomInWithRAREChartViewer:cv]) {
          zoomin = YES;
        }
        if ([self canZoomOutWithRAREChartViewer:cv]) {
          zoomout = YES;
        }
        RAREUTNumberRange *r = [self getZoomRangeWithRAREChartViewer:cv];
        if ([((NSNumber *) nil_chk([((RAREUTNumberRange *) nil_chk(r)) getHighValue])) intValue] > max) {
          max = [((NSNumber *) nil_chk([r getHighValue])) intValue];
          min = [((NSNumber *) nil_chk([r getLowValue])) intValue];
        }
      }
    }
  }
  id<RAREiWidget> w = [((id<RAREiContainer>) nil_chk(fv)) getWidgetWithNSString:@"zoomin"];
  if (w != nil) {
    [w setEnabledWithBoolean:zoomin];
  }
  w = [fv getWidgetWithNSString:@"zoomout"];
  if (w != nil) {
    [w setEnabledWithBoolean:zoomout];
  }
  w = [fv getWidgetWithNSString:@"chartLabel"];
  if (w != nil) {
    NSString *s;
    if (max == 0) {
      s = @"";
    }
    else {
      s = [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getStringWithNSString:@"bv.text.chart_point_range" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ [RAREUTStringCache valueOfWithInt:min], [RAREUTStringCache valueOfWithInt:max] } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
    }
    [w setValueWithId:s];
  }
}

- (RAREUTNumberRange *)getZoomRangeWithRAREChartViewer:(RAREChartViewer *)cv {
  RAREChartDefinition *cd = [((RAREChartViewer *) nil_chk(cv)) getChartDefinition];
  int count = [((RAREChartDefinition *) nil_chk(cd)) getSeriesCount];
  int max = 0;
  int min = 0;
  for (int n = 0; n < count; n++) {
    RAREChartDataItem *series = [cd getSeriesWithInt:n];
    int nmax = [((id<JavaUtilList>) nil_chk([((RAREChartDataItem *) nil_chk(series)) getUnfilteredList])) size];
    if (nmax > max) {
      max = nmax;
      min = [series size];
    }
  }
  return [[RAREUTNumberRange alloc] initWithNSNumber:[JavaLangInteger valueOfWithInt:min] withNSNumber:[JavaLangInteger valueOfWithInt:max]];
}

- (BOOL)zoomWithRAREChartViewer:(RAREChartViewer *)cv
                    withBoolean:(BOOL)inArg
                    withBoolean:(BOOL)update {
  RAREChartDefinition *cd = [((RAREChartViewer *) nil_chk(cv)) getChartDefinition];
  int count = [((RAREChartDefinition *) nil_chk(cd)) getSeriesCount];
  int cp = 0;
  BOOL zoomed = NO;
  for (int n = 0; n < count; n++) {
    RAREChartDataItem *series = [cd getSeriesWithInt:n];
    int len = [((RAREChartDataItem *) nil_chk(series)) size];
    BOOL filtered = [series isFiltered];
    if (inArg) {
      if (len > chartPageSize_) {
        int start = len - chartPageSize_;
        if (start < chartPageSize_) {
          start = chartPageSize_;
        }
        [series unfilter];
        len = [series size];
        start = len - start;
        for (int i = start; i < len; i++) {
          [series addIndexToFilteredListWithInt:i];
        }
        (void) [series setCustomPropertyWithId:CCPBVaChartHandler_TOTAL_POINT_LABELS_SIZE_ withId:nil];
        zoomed = YES;
      }
    }
    else if (filtered) {
      int clen = [((id<JavaUtilList>) nil_chk([series getUnfilteredList])) size];
      int end = clen - len;
      len += chartPageSize_;
      chartPoints_ += chartPageSize_;
      if (end <= 0) {
        [series unfilter];
      }
      else {
        int start = [JavaLangMath maxWithInt:0 withInt:end - chartPageSize_];
        if (start == 0) {
          [series unfilter];
        }
        else {
          for (int i = start; i < end; i++) {
            [series addIndexToFilteredListWithInt:i];
          }
        }
      }
      (void) [series setCustomPropertyWithId:CCPBVaChartHandler_TOTAL_POINT_LABELS_SIZE_ withId:nil];
      zoomed = YES;
    }
    cp = [JavaLangMath maxWithInt:cp withInt:[series size]];
  }
  if (zoomed) {
    cp = (cp + chartPageSize_ - 1) / chartPageSize_;
    chartPoints_ = [JavaLangMath maxWithInt:cp withInt:1] * chartPageSize_;
    [((RAREChartAxis *) nil_chk([cd getDomainAxis])) setAngleWithInt:[self getDomainAngleBasedOnPlotSizeWithRAREChartViewer:cv]];
    [cd setShowPlotLabelsWithBoolean:[self shouldPlotLabelsBeVisibleWithRAREChartViewer:cv]];
    [cv rebuildChart];
    if (update) {
      [self updateZoomButtonsWithRAREiContainer:[cv getFormViewer]];
    }
  }
  else if (update) {
    [CCPBVUtils showShakeAnimationWithRAREiViewer:cv];
  }
  return zoomed;
}

- (void)zoomWithRAREiContainer:(id<RAREiContainer>)fv
                   withBoolean:(BOOL)inArg {
  RAREStackPaneViewer *sp = (RAREStackPaneViewer *) check_class_cast([((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getViewerWithNSString:@"chartPaneStack"], [RAREStackPaneViewer class]);
  id<RAREiViewer> v = [((RAREStackPaneViewer *) nil_chk(sp)) getActiveViewer];
  if ([(id) v isKindOfClass:[RAREChartViewer class]]) {
    [self zoomWithRAREChartViewer:(RAREChartViewer *) check_class_cast(v, [RAREChartViewer class]) withBoolean:inArg withBoolean:YES];
  }
  else {
    BOOL zoomed = NO;
    id<JavaUtilList> widgets = [((id<RAREiContainer>) nil_chk([((id<RAREiViewer>) nil_chk(v)) getContainerViewer])) getWidgetList];
    for (id<RAREiWidget> __strong w in nil_chk(widgets)) {
      if ([(id) w isKindOfClass:[RAREChartViewer class]]) {
        if ([self zoomWithRAREChartViewer:(RAREChartViewer *) check_class_cast(w, [RAREChartViewer class]) withBoolean:inArg withBoolean:NO]) {
          zoomed = YES;
        }
      }
    }
    if (zoomed) {
      [self updateZoomButtonsWithRAREiContainer:fv];
    }
    else {
      [CCPBVUtils showShakeAnimationWithRAREiViewer:v];
    }
  }
}

- (void)configureChartWithRAREiWidget:(id<RAREiWidget>)context
                    withRARESPOTChart:(RARESPOTChart *)cfg
                         withNSString:(NSString *)key
                withRAREChartDataItem:(RAREChartDataItem *)series {
  RAREUTJSONObject *o = [((RAREUTJSONObject *) nil_chk(chartsInfo_)) optJSONObjectWithNSString:key];
  id<JavaUtilMap> attrs = (o == nil) ? [JavaUtilCollections EMPTY_MAP] : [o getObjectMap];
  BOOL gray = [CCPBVUtils isCardStack] || [((CCPBVSettings_AppPreferences *) nil_chk([CCPBVUtils getPreferences])) getBooleanWithNSString:@"gray_charts" withBoolean:NO];
  [((SPOTBoolean *) nil_chk(((RARESPOTChart *) nil_chk(cfg))->showLegends_)) setValueWithBoolean:NO];
  [((SPOTBoolean *) nil_chk(cfg->autoSort_)) setValueWithBoolean:YES];
  [((RARESPOTDataItem_CValueType *) nil_chk(((RARESPOTDataItem *) nil_chk(cfg->rangeAxis_))->valueType_)) setValueWithInt:RARESPOTDataItem_CValueType_integer_type];
  [cfg->rangeAxis_ spot_setAttributeWithNSString:@"lowerBound" withNSString:[attrs getWithId:@"lowerBound"]];
  [cfg->rangeAxis_ spot_setAttributeWithNSString:@"tickIncrement" withNSString:[attrs getWithId:@"tickIncrement"]];
  [cfg->rangeAxis_ spot_setAttributeWithNSString:@"upperBound" withNSString:[attrs getWithId:@"upperBound"]];
  [((SPOTPrintableString *) nil_chk(cfg->rangeAxis_->valueContext_)) setValueWithNSString:[attrs getWithId:@"rangeContext"]];
  [cfg->rangeAxis_ spot_setAttributeWithNSString:@"label" withNSString:[attrs getWithId:@"range"]];
  [((RARESPOTDataItem *) nil_chk(cfg->domainAxis_))->valueType_ setValueWithInt:RARESPOTDataItem_CValueType_date_time_type];
  [((SPOTBoolean *) nil_chk(cfg->zoomingAllowed_)) setValueWithBoolean:NO];
  NSString *s = [attrs getWithId:@"domainContext"];
  [cfg->domainAxis_->valueContext_ setValueWithNSString:(s == nil) ? @"|M/d/yy@HH:mm" : s];
  s = [attrs getWithId:@"domain"];
  if ([CCPBVUtils isCardStack]) {
    s = @"";
  }
  [cfg->domainAxis_ spot_setAttributeWithNSString:@"label" withNSString:(s == nil) ? @"Date" : s];
  s = [attrs getWithId:@"chartType"];
  if (s == nil) {
    [((RARESPOTChart_CChartType *) nil_chk(cfg->chartType_)) setValueWithInt:RARESPOTChart_CChartType_line];
  }
  else {
    [((RARESPOTChart_CChartType *) nil_chk(cfg->chartType_)) setValueWithNSString:s];
  }
  [cfg->domainAxis_ spot_setAttributeWithNSString:@"timeUnit" withNSString:@"none"];
  if ([((RARESPOTChart_CChartType *) nil_chk(cfg->chartType_)) intValue] == RARESPOTChart_CChartType_line) {
    [((SPOTReal *) nil_chk(((RARESPOTPlot *) nil_chk([cfg getPlotReference]))->lineThickness_)) setValueWithLong:2];
    [((RARESPOTPlot_CShapes *) nil_chk(((RARESPOTPlot *) nil_chk([cfg getPlotReference]))->shapes_)) setValueWithInt:RARESPOTPlot_CShapes_filled_and_outlined];
  }
  if (gray) {
    s = ([cfg->chartType_ intValue] == RARESPOTChart_CChartType_line) ? @"lineChartColor_g" : @"barChartColor_g";
  }
  else {
    s = ([cfg->chartType_ intValue] == RARESPOTChart_CChartType_line) ? @"lineChartColor" : @"barChartColor";
  }
  [cfg->rangeAxis_ setValueWithNSString:[self getRangeTitleWithNSString:[((RAREChartDataItem *) nil_chk(series)) getTitle]]];
  [((SPOTPrintableString *) nil_chk(((RARESPOTGridCell *) nil_chk([cfg->rangeAxis_ getGridCellReference]))->bgColor_)) setValueWithNSString:s];
  if ([((NSString *) nil_chk(s)) indexOf:','] != -1) {
    [((RARESPOTGridCell *) nil_chk([cfg->rangeAxis_ getGridCellReference]))->bgColor_ spot_setAttributeWithNSString:@"direction" withNSString:@"horizontal_left"];
  }
  s = [attrs getWithId:@"rangeMarker"];
  if (s != nil) {
    RARESPOTItemDescription *marker = [[RARESPOTItemDescription alloc] init];
    [((SPOTPrintableString *) nil_chk(marker->value_)) setValueWithNSString:s];
    [((SPOTSet *) nil_chk([cfg getRangeMarkersReference])) addWithISPOTElement:marker];
  }
  s = [attrs getWithId:@"border"];
  if (s != nil) {
    RARESPOTGridCell_CBorder *b = [((RARESPOTGridCell *) nil_chk([cfg->rangeAxis_ getGridCellReference])) addBorderWithNSString:s];
    s = [attrs getWithId:@"border_attributes"];
    if (s != nil) {
      [((RARESPOTGridCell_CBorder *) nil_chk(b)) spot_addAttributesWithJavaUtilMap:[RAREUTCharScanner parseOptionStringExWithNSString:s withChar:',']];
    }
  }
}

- (NSString *)getChartTypeWithNSString:(NSString *)key {
  RAREUTJSONObject *o = [((RAREUTJSONObject *) nil_chk(chartsInfo_)) optJSONObjectWithNSString:key];
  return (o == nil) ? @"line" : [o optStringWithNSString:@"chartType" withNSString:@"line"];
}

- (NSString *)getRangeTitleWithNSString:(NSString *)title {
  RAREUTJSONObject *o = [((RAREUTJSONObject *) nil_chk(chartsInfo_)) optJSONObjectWithNSString:@"shortNamesMap"];
  return (o == nil) ? title : [o optStringWithNSString:title withNSString:title];
}

+ (NSNumber *)createNumberWithNSString:(NSString *)type
                          withNSString:(NSString *)value {
  NSNumber *num;
  value = [((NSString *) nil_chk(value)) trim];
  if ([((NSString *) nil_chk(type)) hasPrefix:@"range"]) {
    int n = [((NSString *) nil_chk(value)) indexOf:'/'];
    if (n != -1) {
      num = [[RAREUTNumberRange alloc] initWithNSNumber:[[RAREUTSNumber alloc] initWithNSString:[((NSString *) nil_chk([value substring:n + 1])) trim]] withNSNumber:[[RAREUTSNumber alloc] initWithNSString:[value substring:0 endIndex:n]]];
    }
    else {
      n = [value indexOf:'-'];
      if (n == -1) {
        return nil;
      }
      num = [[RAREUTNumberRange alloc] initWithNSNumber:[[RAREUTSNumber alloc] initWithNSString:[value substring:0 endIndex:n]] withNSNumber:[[RAREUTSNumber alloc] initWithNSString:[((NSString *) nil_chk([value substring:n + 1])) trim]]];
    }
  }
  else {
    num = [[RAREUTSNumber alloc] initWithNSString:value];
  }
  return num;
}

+ (void)initialize {
  if (self == [CCPBVaChartHandler class]) {
    CCPBVaChartHandler_MIN_ANGLED_LABEL_HEIGHT_ = [RAREUIScreen toPlatformPixelsWithFloat:400 withRAREScreenUtils_UnitEnum:[RAREScreenUtils_UnitEnum POINT] withBoolean:NO];
  }
}

- (void)copyAllFieldsTo:(CCPBVaChartHandler *)other {
  [super copyAllFieldsTo:other];
  other->chartPageSize_ = chartPageSize_;
  other->chartPoints_ = chartPoints_;
  other->chartTypeMap_ = chartTypeMap_;
  other->chartsInfo_ = chartsInfo_;
  other->dateColumn_ = dateColumn_;
  other->eventHandlerClass_ = eventHandlerClass_;
  other->nameColumn_ = nameColumn_;
  other->valueColumn_ = valueColumn_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "canZoomInWithRAREChartViewer:", NULL, "Z", 0x1, NULL },
    { "canZoomOutWithRAREChartViewer:", NULL, "Z", 0x1, NULL },
    { "createChartWithRAREiFormViewer:withNSString:withInt:withRAREChartDataItem:", NULL, "LRAREChartViewer", 0x1, NULL },
    { "createSeriesWithJavaUtilList:withJavaUtilMap:withInt:", NULL, "LJavaUtilLinkedHashMap", 0x1, NULL },
    { "shouldPlotLabelsBeVisibleWithRAREChartViewer:", NULL, "Z", 0x4, NULL },
    { "createSeriesWithJavaUtilList:withNSString:withInt:", NULL, "LRAREChartDataItem", 0x1, NULL },
    { "createSeriesFromSpreadSheetWithRARETableViewer:withNSString:withRARERenderableDataItem:withInt:", NULL, "LRAREChartDataItem", 0x1, NULL },
    { "getZoomRangeWithRAREChartViewer:", NULL, "LRAREUTNumberRange", 0x4, NULL },
    { "zoomWithRAREChartViewer:withBoolean:withBoolean:", NULL, "Z", 0x1, NULL },
    { "configureChartWithRAREiWidget:withRARESPOTChart:withNSString:withRAREChartDataItem:", NULL, "V", 0x4, NULL },
    { "getChartTypeWithNSString:", NULL, "LNSString", 0x4, NULL },
    { "getRangeTitleWithNSString:", NULL, "LNSString", 0x4, NULL },
    { "createNumberWithNSString:withNSString:", NULL, "LNSNumber", 0xc, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "MIN_ANGLED_LABEL_HEIGHT_", NULL, 0x9, "F" },
    { "TOTAL_POINT_LABELS_SIZE_", NULL, 0x18, "LNSString" },
    { "chartsInfo_", NULL, 0x4, "LRAREUTJSONObject" },
    { "nameColumn_", NULL, 0x4, "I" },
    { "dateColumn_", NULL, 0x4, "I" },
    { "valueColumn_", NULL, 0x4, "I" },
    { "eventHandlerClass_", NULL, 0x4, "LNSString" },
    { "chartPageSize_", NULL, 0x4, "I" },
    { "chartPoints_", NULL, 0x4, "I" },
  };
  static J2ObjcClassInfo _CCPBVaChartHandler = { "aChartHandler", "com.sparseware.bellavista", NULL, 0x401, 13, methods, 9, fields, 0, NULL};
  return &_CCPBVaChartHandler;
}

@end
