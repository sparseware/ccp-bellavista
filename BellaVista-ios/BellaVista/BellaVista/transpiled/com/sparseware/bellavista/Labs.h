//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Labs.java
//
//  Created by decoteaud on 3/12/15.
//

#ifndef _ComSparsewareBellavistaLabs_H_
#define _ComSparsewareBellavistaLabs_H_

@class ComSparsewareBellavistaActionPath;
@class ComSparsewareBellavistaClinicalValue;
@class ComSparsewareBellavistaDocument;
@class ComSparsewareBellavistaTrendPanel;
@class IOSObjectArray;
@class JavaUtilEventObject;
@class JavaUtilHashMap;
@class JavaUtilLinkedHashMap;
@class RAREActionEvent;
@class RAREActionLink;
@class RARERenderableDataItem;
@class RARETableViewer;
@class RAREUIColor;
@class RAREUIFormsLayoutRenderer;
@class RAREUTJSONArray;
@class RAREUTJSONObject;
@class RAREWindowViewer;
@protocol JavaUtilList;
@protocol JavaUtilMap;
@protocol RAREUTiStringConverter;
@protocol RAREiPlatformIcon;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "com/appnativa/util/iFilter.h"
#include "com/sparseware/bellavista/aChartHandler.h"
#include "com/sparseware/bellavista/aResultsManager.h"
#include "com/sparseware/bellavista/iValueChecker.h"
#include "java/lang/Runnable.h"

@interface ComSparsewareBellavistaLabs : ComSparsewareBellavistaaResultsManager < RAREiActionListener, ComSparsewareBellavistaiValueChecker > {
 @public
  BOOL bunFound_;
  BOOL creatFound_;
  NSString *bunID_;
  NSString *creatineID_;
  ComSparsewareBellavistaClinicalValue *bunValue_;
  ComSparsewareBellavistaClinicalValue *creatinineValue_;
  BOOL isSummary_;
  IOSObjectArray *trendPanels_;
  BOOL overViewLoaded_;
  id<JavaUtilMap> trendsLayout_;
  BOOL uniqueSummaryEntries_;
  BOOL documentsInlined_;
  BOOL sortCategoriesOnLinkedData_;
  id<RAREiPlatformIcon> pageIcon_;
  id<RAREiPlatformIcon> noteIcon_;
  NSString *seeReport_;
  RAREUIColor *unknowResultColor_;
  NSString *collectionInfoKey_;
  JavaUtilHashMap *collectionInfoMap_;
}

+ (int)CATEGORY_NAME_POSITION;
+ (int *)CATEGORY_NAME_POSITIONRef;
+ (int)IS_DOCUMENT_POSITION;
+ (int *)IS_DOCUMENT_POSITIONRef;
+ (int)SORT_ORDER_POSITION;
+ (int *)SORT_ORDER_POSITIONRef;
+ (int)UNIT_POSITION;
+ (int *)UNIT_POSITIONRef;
+ (int)RESULT_ID_POSITION;
+ (int *)RESULT_ID_POSITIONRef;
+ (int)COMMENT_POSITION;
+ (int *)COMMENT_POSITIONRef;
+ (JavaUtilLinkedHashMap *)trendMap;
+ (void)setTrendMap:(JavaUtilLinkedHashMap *)trendMap;
- (id)init;
- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (void)changeViewWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (ComSparsewareBellavistaActionPath *)getDisplayedActionPath;
- (void)filterTableWithRARERenderableDataItem:(RARERenderableDataItem *)filterItem;
- (BOOL)checkRowWithRARERenderableDataItem:(RARERenderableDataItem *)row
                                   withInt:(int)index
                                   withInt:(int)expandableColumn
                                   withInt:(int)rowRount;
- (ComSparsewareBellavistaActionPath *)getActionPath;
- (void)onFinishedLoadingWithNSString:(NSString *)eventName
                      withRAREiWidget:(id<RAREiWidget>)widget
              withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onLabReportLoadWithNSString:(NSString *)eventName
                    withRAREiWidget:(id<RAREiWidget>)widget
            withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onReportNavigatorChangeWithNSString:(NSString *)eventName
                            withRAREiWidget:(id<RAREiWidget>)widget
                    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSummaryTableActionWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onCreatedWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onTrendsTableConfigureWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onTrendsTableCreatedWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)selectLabsWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)showChartForSelectedItemWithRARETableViewer:(RARETableViewer *)table;
- (void)showCollectionInfoWithRARETableViewer:(RARETableViewer *)table
                   withRARERenderableDataItem:(RARERenderableDataItem *)row;
- (void)configureTrendFormsRendererWithRAREUIFormsLayoutRenderer:(RAREUIFormsLayoutRenderer *)renderer;
- (ComSparsewareBellavistaTrendPanel *)createTrendPanelFromTableWithRARETableViewer:(RARETableViewer *)table
                                                                       withNSString:(NSString *)title;
- (IOSObjectArray *)createTrendPanelsWithRAREUTJSONArray:(RAREUTJSONArray *)trends;
- (void)dataParsedWithRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilList:(id<JavaUtilList>)rows
               withRAREActionLink:(RAREActionLink *)link;
- (NSString *)getCategoryWithRARERenderableDataItem:(RARERenderableDataItem *)row;
- (NSString *)getSpeeedSheetColumnTitle;
- (void)handleSummaryLabsWithRARETableViewer:(RARETableViewer *)table
                            withJavaUtilList:(id<JavaUtilList>)rows;
- (BOOL)hasCategories;
- (void)loadStainsWithRAREWindowViewer:(RAREWindowViewer *)w
   withComSparsewareBellavistaDocument:(ComSparsewareBellavistaDocument *)doc
                       withRAREiWidget:(id<RAREiWidget>)browser;
- (void)loadSusceptibilitiesWithRAREWindowViewer:(RAREWindowViewer *)w
             withComSparsewareBellavistaDocument:(ComSparsewareBellavistaDocument *)doc
                             withRARETableViewer:(RARETableViewer *)table;
- (void)processDataWithRARETableViewer:(RARETableViewer *)table
                      withJavaUtilList:(id<JavaUtilList>)rows;
- (BOOL)reportHasStainsWithComSparsewareBellavistaDocument:(ComSparsewareBellavistaDocument *)doc;
- (BOOL)reportHasSusceptibilitiesWithComSparsewareBellavistaDocument:(ComSparsewareBellavistaDocument *)doc;
- (void)reset;
- (void)copyAllFieldsTo:(ComSparsewareBellavistaLabs *)other;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, bunID_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, creatineID_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, bunValue_, ComSparsewareBellavistaClinicalValue *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, creatinineValue_, ComSparsewareBellavistaClinicalValue *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, trendPanels_, IOSObjectArray *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, trendsLayout_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, pageIcon_, id<RAREiPlatformIcon>)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, noteIcon_, id<RAREiPlatformIcon>)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, seeReport_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, unknowResultColor_, RAREUIColor *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, collectionInfoKey_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs, collectionInfoMap_, JavaUtilHashMap *)

@interface ComSparsewareBellavistaLabs_ChartHandler : ComSparsewareBellavistaaChartHandler {
}

- (id)initWithComSparsewareBellavistaLabs:(ComSparsewareBellavistaLabs *)outer$
                     withRAREUTJSONObject:(RAREUTJSONObject *)chartsInfo;
@end

@interface ComSparsewareBellavistaLabs_$1 : NSObject < RAREUTiFilter > {
 @public
  ComSparsewareBellavistaLabs *this$0_;
  NSString *val$cat_;
}

- (BOOL)passesWithId:(id)value
withRAREUTiStringConverter:(id<RAREUTiStringConverter>)converter;
- (id)initWithComSparsewareBellavistaLabs:(ComSparsewareBellavistaLabs *)outer$
                             withNSString:(NSString *)capture$0;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$1, this$0_, ComSparsewareBellavistaLabs *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$1, val$cat_, NSString *)

@interface ComSparsewareBellavistaLabs_$2 : NSObject < RAREiFunctionCallback > {
 @public
  RAREWindowViewer *val$win_;
  RARETableViewer *val$table_;
  RAREUTJSONObject *val$json_;
  RARERenderableDataItem *val$row_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREWindowViewer:(RAREWindowViewer *)capture$0
           withRARETableViewer:(RARETableViewer *)capture$1
          withRAREUTJSONObject:(RAREUTJSONObject *)capture$2
    withRARERenderableDataItem:(RARERenderableDataItem *)capture$3;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$2, val$win_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$2, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$2, val$json_, RAREUTJSONObject *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$2, val$row_, RARERenderableDataItem *)

@interface ComSparsewareBellavistaLabs_$3 : RAREaWorkerTask {
 @public
  ComSparsewareBellavistaLabs *this$0_;
  RARETableViewer *val$table_;
  id<JavaUtilList> val$rows_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithComSparsewareBellavistaLabs:(ComSparsewareBellavistaLabs *)outer$
                      withRARETableViewer:(RARETableViewer *)capture$0
                         withJavaUtilList:(id<JavaUtilList>)capture$1;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$3, this$0_, ComSparsewareBellavistaLabs *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$3, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$3, val$rows_, id<JavaUtilList>)

@interface ComSparsewareBellavistaLabs_$4 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiWidget> val$label_;
  NSString *val$value_;
}

- (void)run;
- (id)initWithRAREiWidget:(id<RAREiWidget>)capture$0
             withNSString:(NSString *)capture$1;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$4, val$label_, id<RAREiWidget>)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaLabs_$4, val$value_, NSString *)

#endif // _ComSparsewareBellavistaLabs_H_
