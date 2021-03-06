//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/aResultsManager.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVaResultsManager_H_
#define _CCPBVaResultsManager_H_

@class CCPBVActionPath;
@class CCPBVListPager;
@class CCPBVResultsViewEnum;
@class CCPBVaChartHandler;
@class CCPBVaResultsManager_ChartableItemsManager;
@class IOSObjectArray;
@class JavaLangBoolean;
@class JavaUtilEventObject;
@class JavaUtilHashSet;
@class JavaUtilLinkedHashMap;
@class JavaUtilLinkedHashSet;
@class RAREActionEvent;
@class RAREActionLink;
@class RARERenderableDataItem;
@class RAREStackPaneViewer;
@class RARETableViewer;
@class RAREUTJSONArray;
@class RAREWindowViewer;
@protocol JavaUtilList;
@protocol RAREiContainer;
@protocol RAREiTransitionAnimator;
@protocol RAREiViewer;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "com/sparseware/bellavista/ActionPath.h"
#include "com/sparseware/bellavista/aEventHandler.h"
#include "com/sparseware/bellavista/iDataPagingSupport.h"
#include "java/lang/Runnable.h"

@interface CCPBVaResultsManager : CCPBVaEventHandler < CCPBVActionPath_iActionPathSupporter, CCPBViDataPagingSupport > {
 @public
  CCPBVResultsViewEnum *currentView_;
  RARETableViewer *dataTable_;
  RARETableViewer *spreadsheetTable_;
  BOOL hasNoData_;
  BOOL dataLoaded_;
  int spreadsheetPosition_;
  int spreadSheetPageSize_;
  int dataPageSize_;
  NSString *namePrefix_;
  CCPBVaChartHandler *chartHandler_;
  id<RAREiTransitionAnimator> transitionAnimation_;
  id<JavaUtilList> originalRows_;
  JavaUtilLinkedHashMap *itemCounts_;
  IOSObjectArray *itemDates_;
  JavaUtilLinkedHashSet *itemDatesSet_;
  BOOL chartsLoaded_;
  CCPBVActionPath *keyPath_;
  id dateContext_;
  NSString *scriptClassName_;
  BOOL selectionChecked_;
  BOOL multiChartMode_;
  CCPBVaResultsManager_ChartableItemsManager *chartableItemsManager_;
  RAREActionLink *dataLink_;
  CCPBVListPager *pager_;
}

+ (int)DATE_POSITION;
+ (int *)DATE_POSITIONRef;
+ (int)NAME_POSITION;
+ (int *)NAME_POSITIONRef;
+ (int)VALUE_POSITION;
+ (int *)VALUE_POSITIONRef;
+ (int)UNIT_POSITION;
+ (int *)UNIT_POSITIONRef;
+ (int)RANGE_POSITION;
+ (int *)RANGE_POSITIONRef;
+ (int)MIN_ANGLED_LABEL_HEIGHT;
+ (int *)MIN_ANGLED_LABEL_HEIGHTRef;
+ (int)MIN_POINTSLABEL_HEIGHT;
+ (int *)MIN_POINTSLABEL_HEIGHTRef;
- (id)initWithNSString:(NSString *)namePrefix
          withNSString:(NSString *)scriptClassName;
- (void)changePageWithBoolean:(BOOL)next
              withRAREiWidget:(id<RAREiWidget>)nextPageWidget
              withRAREiWidget:(id<RAREiWidget>)previousPageWidget;
- (void)changeViewWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (BOOL)checkAndHandleNoDataWithRARETableViewer:(RARETableViewer *)table
                               withJavaUtilList:(id<JavaUtilList>)rows;
- (void)chooseStartingDateWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (CCPBVActionPath *)getDisplayedActionPath;
- (void)handleActionPathWithCCPBVActionPath:(CCPBVActionPath *)path;
- (void)onChartFlingWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onChartResizeWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onChartScaleWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onChartsPanelLoadedWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onFirstPageWithNSString:(NSString *)eventName
                withRAREiWidget:(id<RAREiWidget>)widget
        withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onLastPageWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onNextPageWithNSString:(NSString *)eventName
               withRAREiWidget:(id<RAREiWidget>)widget
       withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onPreviousPageWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onTableActionWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onTableCreatedWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onTimeframePopupActionWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onZoomInActionWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onZoomOutActionWithNSString:(NSString *)eventName
                    withRAREiWidget:(id<RAREiWidget>)widget
            withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)reloadTableDataWithRARETableViewer:(RARETableViewer *)table;
- (void)reselectDefaultViewWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)showChartForSelectedItemWithRARETableViewer:(RARETableViewer *)table;
- (void)showMostRecentWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)showNextResultSetWithNSString:(NSString *)eventName
                      withRAREiWidget:(id<RAREiWidget>)widget
              withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)showPreviousResultSetWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)addCurrentPathIDWithCCPBVActionPath:(CCPBVActionPath *)path;
- (void)changeSpreadsheetPageWithRAREiWidget:(id<RAREiWidget>)widget
                                 withBoolean:(BOOL)forward
                                 withBoolean:(BOOL)jump;
- (void)changeViewWithNSString:(NSString *)name
               withRAREiWidget:(id<RAREiWidget>)widget;
- (void)changeViewExWithRAREiWidget:(id<RAREiWidget>)widget
           withCCPBVResultsViewEnum:(CCPBVResultsViewEnum *)view;
- (void)clearChartsWithRARETableViewer:(RARETableViewer *)table;
- (void)clearSelection;
- (RARERenderableDataItem *)createNoDataRowWithRARETableViewer:(RARETableViewer *)table
                                                  withNSString:(NSString *)message;
- (IOSObjectArray *)createTrendPanelsWithRAREUTJSONArray:(RAREUTJSONArray *)trends
                                             withBoolean:(BOOL)reverseChronologicalOrder;
- (NSString *)getCategoryWithRARERenderableDataItem:(RARERenderableDataItem *)row;
- (int)getFirstChartableItemWithRARETableViewer:(RARETableViewer *)table;
- (NSString *)getFirstChartableKey;
- (int)getNextOrPreviousChartableItemWithRARETableViewer:(RARETableViewer *)table
                                        withJavaUtilList:(id<JavaUtilList>)keys
                                             withBoolean:(BOOL)next;
- (NSString *)getSelectedChartableKey;
- (NSString *)getSpeeedSheetColumnTitle;
- (void)handleActionPathExWithCCPBVActionPath:(CCPBVActionPath *)path;
- (void)handlePathKeyWithRARETableViewer:(RARETableViewer *)table
                            withNSString:(NSString *)key
                                 withInt:(int)column
                             withBoolean:(BOOL)fireAction;
- (BOOL)hasCategories;
- (BOOL)hasDocumentLoadedWithNSString:(NSString *)id_
                   withRAREiContainer:(id<RAREiContainer>)fv;
- (BOOL)isChartableWithRARERenderableDataItem:(RARERenderableDataItem *)row;
- (BOOL)isOnNonChartingView;
- (void)reselectDefaultViewEx;
- (void)reset;
- (void)selectFirstChartableItemWithRARETableViewer:(RARETableViewer *)table
                                        withBoolean:(BOOL)fireAction;
- (void)setNavigationButtonsVisibleWithRAREiContainer:(id<RAREiContainer>)fv
                                          withBoolean:(BOOL)visible;
- (void)showChartForSelectedItemExWithRARETableViewer:(RARETableViewer *)table
                              withRAREStackPaneViewer:(RAREStackPaneViewer *)sp
                                  withJavaLangBoolean:(JavaLangBoolean *)forward
                                          withBoolean:(BOOL)horizontal;
- (void)showChartsViewWithRARETableViewer:(RARETableViewer *)table;
- (void)showRegularTableWithRAREiContainer:(id<RAREiContainer>)fv
                               withBoolean:(BOOL)trends;
- (void)showRegularTableExWithRAREiContainer:(id<RAREiContainer>)fv;
- (void)showSpreesheetWithRAREiContainer:(id<RAREiContainer>)fv;
- (void)slideToChartableItemWithBoolean:(BOOL)next
                            withBoolean:(BOOL)horizontal;
- (void)spreadsheetRowCreatedWithRARETableViewer:(RARETableViewer *)table
                      withRARERenderableDataItem:(RARERenderableDataItem *)spreadsheetRow
                      withRARERenderableDataItem:(RARERenderableDataItem *)sourceRow;
- (void)tableDataLoadedWithRAREActionLink:(RAREActionLink *)link;
- (void)updateCardStackTitleWithNSString:(NSString *)title
                            withNSString:(NSString *)subtitle;
- (void)updateNavigationButtonsWithRAREiContainer:(id<RAREiContainer>)fv;
- (void)updateSpreadsheetColumnsWithRARETableViewer:(RARETableViewer *)table;
- (void)updateSpreadsheetRowsWithRARETableViewer:(RARETableViewer *)table;
- (void)viewerPopulatedWithRAREiViewer:(id<RAREiViewer>)v;
- (void)copyAllFieldsTo:(CCPBVaResultsManager *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager, currentView_, CCPBVResultsViewEnum *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, dataTable_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, spreadsheetTable_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, namePrefix_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, chartHandler_, CCPBVaChartHandler *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, transitionAnimation_, id<RAREiTransitionAnimator>)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, originalRows_, id<JavaUtilList>)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, itemCounts_, JavaUtilLinkedHashMap *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, itemDates_, IOSObjectArray *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, itemDatesSet_, JavaUtilLinkedHashSet *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, keyPath_, CCPBVActionPath *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, dateContext_, id)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, scriptClassName_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, chartableItemsManager_, CCPBVaResultsManager_ChartableItemsManager *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, dataLink_, RAREActionLink *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager, pager_, CCPBVListPager *)

typedef CCPBVaResultsManager ComSparsewareBellavistaAResultsManager;

@interface CCPBVaResultsManager_ChartsActionListener : NSObject < RAREiActionListener > {
 @public
  CCPBVaResultsManager *this$0_;
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)initWithCCPBVaResultsManager:(CCPBVaResultsManager *)outer$;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_ChartsActionListener, this$0_, CCPBVaResultsManager *)

@interface CCPBVaResultsManager_ChartableItemsManager : NSObject {
 @public
  JavaUtilHashSet *chartableSet_;
  id<JavaUtilList> chartableKeys_;
  id<JavaUtilList> filteredChartableKeys_;
}

- (id)init;
- (BOOL)checkWithNSString:(NSString *)key
             withNSString:(NSString *)value;
- (NSString *)createCardStackTitleWithNSString:(NSString *)key;
- (void)createListWithRARETableViewer:(RARETableViewer *)table
                              withInt:(int)keyColumn;
- (int)getNextOrPreviousItemWithRARETableViewer:(RARETableViewer *)table
                                    withBoolean:(BOOL)next
                                    withBoolean:(BOOL)unique
                                        withInt:(int)keyColumn;
- (BOOL)isChartableWithNSString:(NSString *)key;
- (void)reset;
- (int)getNextOrPreviousItemExWithRARETableViewer:(RARETableViewer *)table
                                      withBoolean:(BOOL)next
                                          withInt:(int)keyColumn;
- (void)copyAllFieldsTo:(CCPBVaResultsManager_ChartableItemsManager *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_ChartableItemsManager, chartableSet_, JavaUtilHashSet *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_ChartableItemsManager, chartableKeys_, id<JavaUtilList>)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_ChartableItemsManager, filteredChartableKeys_, id<JavaUtilList>)

@interface CCPBVaResultsManager_$1 : NSObject < JavaLangRunnable > {
 @public
  CCPBVaResultsManager *this$0_;
  RARETableViewer *val$table_;
}

- (void)run;
- (id)initWithCCPBVaResultsManager:(CCPBVaResultsManager *)outer$
               withRARETableViewer:(RARETableViewer *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$1, this$0_, CCPBVaResultsManager *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$1, val$table_, RARETableViewer *)

@interface CCPBVaResultsManager_$2 : NSObject < JavaLangRunnable > {
 @public
  RARETableViewer *val$t_;
  int val$index_;
}

- (void)run;
- (id)initWithRARETableViewer:(RARETableViewer *)capture$0
                      withInt:(int)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$2, val$t_, RARETableViewer *)

@interface CCPBVaResultsManager_$3 : RAREaWorkerTask {
 @public
  CCPBVaResultsManager *this$0_;
  NSString *val$key_;
  RAREWindowViewer *val$w_;
  RARETableViewer *val$table_;
  RAREStackPaneViewer *val$sp_;
  JavaLangBoolean *val$forward_;
  BOOL val$horizontal_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithCCPBVaResultsManager:(CCPBVaResultsManager *)outer$
                      withNSString:(NSString *)capture$0
              withRAREWindowViewer:(RAREWindowViewer *)capture$1
               withRARETableViewer:(RARETableViewer *)capture$2
           withRAREStackPaneViewer:(RAREStackPaneViewer *)capture$3
               withJavaLangBoolean:(JavaLangBoolean *)capture$4
                       withBoolean:(BOOL)capture$5;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$3, this$0_, CCPBVaResultsManager *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$3, val$key_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$3, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$3, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$3, val$sp_, RAREStackPaneViewer *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$3, val$forward_, JavaLangBoolean *)

@interface CCPBVaResultsManager_$4 : NSObject < JavaLangRunnable > {
 @public
  CCPBVaResultsManager *this$0_;
}

- (void)run;
- (id)initWithCCPBVaResultsManager:(CCPBVaResultsManager *)outer$;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$4, this$0_, CCPBVaResultsManager *)

@interface CCPBVaResultsManager_$5 : NSObject < JavaLangRunnable > {
 @public
  CCPBVaResultsManager *this$0_;
  RARETableViewer *val$table_;
}

- (void)run;
- (id)initWithCCPBVaResultsManager:(CCPBVaResultsManager *)outer$
               withRARETableViewer:(RARETableViewer *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$5, this$0_, CCPBVaResultsManager *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$5, val$table_, RARETableViewer *)

@interface CCPBVaResultsManager_$6 : NSObject < JavaLangRunnable > {
 @public
  CCPBVaResultsManager *this$0_;
  RARETableViewer *val$table_;
  RAREWindowViewer *val$w_;
}

- (void)run;
- (id)initWithCCPBVaResultsManager:(CCPBVaResultsManager *)outer$
               withRARETableViewer:(RARETableViewer *)capture$0
              withRAREWindowViewer:(RAREWindowViewer *)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$6, this$0_, CCPBVaResultsManager *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$6, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVaResultsManager_$6, val$w_, RAREWindowViewer *)

#endif // _CCPBVaResultsManager_H_
