//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Labs.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVLabs_H_
#define _CCPBVLabs_H_

@class CCPBVActionPath;
@class CCPBVClinicalValue;
@class CCPBVDocument_DocumentItem;
@class CCPBVDocument_DocumentItemTypeEnum;
@class IOSObjectArray;
@class JavaLangStringBuilder;
@class JavaUtilDate;
@class JavaUtilEventObject;
@class JavaUtilHashMap;
@class JavaUtilLinkedHashMap;
@class RAREActionEvent;
@class RAREActionLink;
@class RARERenderableDataItem;
@class RARETableViewer;
@class RAREUIColor;
@class RAREUIFormsLayoutRenderer;
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
#include "com/sparseware/bellavista/Document.h"
#include "com/sparseware/bellavista/aChartHandler.h"
#include "com/sparseware/bellavista/aResultsManager.h"
#include "com/sparseware/bellavista/iValueChecker.h"
#include "java/lang/Runnable.h"

@interface CCPBVLabs : CCPBVaResultsManager < RAREiActionListener, CCPBViValueChecker > {
 @public
  BOOL bunFound_;
  BOOL creatFound_;
  NSString *bunID_;
  NSString *creatineID_;
  CCPBVClinicalValue *bunValue_;
  CCPBVClinicalValue *creatinineValue_;
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
  CCPBVDocument *loadedDocument_;
  RAREUIColor *unknowResultColor_;
  NSString *collectionInfoKey_;
  JavaUtilHashMap *collectionInfoMap_;
  BOOL showUnits_;
  JavaLangStringBuilder *temp_;
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
- (void)onSummaryDisposeWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (CCPBVActionPath *)getDisplayedActionPath;
- (void)filterTableWithRARERenderableDataItem:(RARERenderableDataItem *)filterItem;
- (BOOL)checkRowWithRARERenderableDataItem:(RARERenderableDataItem *)row
                                   withInt:(int)index
                                   withInt:(int)expandableColumn
                                   withInt:(int)rowRount;
- (void)addCurrentPathIDWithCCPBVActionPath:(CCPBVActionPath *)path;
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
- (void)onDisposeWithNSString:(NSString *)eventName
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
- (void)disposeOfLoadedDocument;
- (void)showCollectionInfoWithRARETableViewer:(RARETableViewer *)table
                   withRARERenderableDataItem:(RARERenderableDataItem *)row;
- (void)configureTrendFormsRendererWithRAREUIFormsLayoutRenderer:(RAREUIFormsLayoutRenderer *)renderer;
- (void)dataParsedWithRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilList:(id<JavaUtilList>)rows
               withRAREActionLink:(RAREActionLink *)link;
- (NSString *)getCategoryWithRARERenderableDataItem:(RARERenderableDataItem *)row;
- (NSString *)getSpeeedSheetColumnTitle;
- (void)handleSummaryLabsWithRARETableViewer:(RARETableViewer *)table
                            withJavaUtilList:(id<JavaUtilList>)rows;
- (BOOL)hasCategories;
- (void)loadStainsWithRAREWindowViewer:(RAREWindowViewer *)w
                     withCCPBVDocument:(CCPBVDocument *)doc
                       withRAREiWidget:(id<RAREiWidget>)browser;
- (void)loadSusceptibilitiesWithRAREWindowViewer:(RAREWindowViewer *)w
                               withCCPBVDocument:(CCPBVDocument *)doc
                             withRARETableViewer:(RARETableViewer *)table;
- (void)processDataWithRARETableViewer:(RARETableViewer *)table
                      withJavaUtilList:(id<JavaUtilList>)rows;
- (BOOL)reportHasStainsWithCCPBVDocument:(CCPBVDocument *)doc;
- (BOOL)reportHasSusceptibilitiesWithCCPBVDocument:(CCPBVDocument *)doc;
- (void)reset;
- (void)copyAllFieldsTo:(CCPBVLabs *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVLabs, bunID_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVLabs, creatineID_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVLabs, bunValue_, CCPBVClinicalValue *)
J2OBJC_FIELD_SETTER(CCPBVLabs, creatinineValue_, CCPBVClinicalValue *)
J2OBJC_FIELD_SETTER(CCPBVLabs, trendPanels_, IOSObjectArray *)
J2OBJC_FIELD_SETTER(CCPBVLabs, trendsLayout_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVLabs, pageIcon_, id<RAREiPlatformIcon>)
J2OBJC_FIELD_SETTER(CCPBVLabs, noteIcon_, id<RAREiPlatformIcon>)
J2OBJC_FIELD_SETTER(CCPBVLabs, seeReport_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVLabs, loadedDocument_, CCPBVDocument *)
J2OBJC_FIELD_SETTER(CCPBVLabs, unknowResultColor_, RAREUIColor *)
J2OBJC_FIELD_SETTER(CCPBVLabs, collectionInfoKey_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVLabs, collectionInfoMap_, JavaUtilHashMap *)
J2OBJC_FIELD_SETTER(CCPBVLabs, temp_, JavaLangStringBuilder *)

typedef CCPBVLabs ComSparsewareBellavistaLabs;

@interface CCPBVLabs_LabDocument : CCPBVDocument {
 @public
  CCPBVDocument_DocumentItem *susceptibilities_;
  CCPBVDocument_DocumentItem *stains_;
}

- (id)initWithRAREiWidget:(id<RAREiWidget>)widget
       withRAREActionLink:(RAREActionLink *)link
             withNSString:(NSString *)id_;
- (CCPBVDocument_DocumentItem *)addAttachmentWithCCPBVDocument_DocumentItemTypeEnum:(CCPBVDocument_DocumentItemTypeEnum *)type
                                                                   withJavaUtilDate:(JavaUtilDate *)date
                                                                       withNSString:(NSString *)title
                                                                       withNSString:(NSString *)mimeType
                                                                       withNSString:(NSString *)body
                                                                       withNSString:(NSString *)href;
- (CCPBVDocument_DocumentItem *)processDocumentRowWithCCPBVDocument_DocumentItem:(CCPBVDocument_DocumentItem *)doc
                                                      withRARERenderableDataItem:(RARERenderableDataItem *)row;
- (void)copyAllFieldsTo:(CCPBVLabs_LabDocument *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVLabs_LabDocument, susceptibilities_, CCPBVDocument_DocumentItem *)
J2OBJC_FIELD_SETTER(CCPBVLabs_LabDocument, stains_, CCPBVDocument_DocumentItem *)

@interface CCPBVLabs_ChartHandler : CCPBVaChartHandler {
}

- (id)initWithCCPBVLabs:(CCPBVLabs *)outer$
   withRAREUTJSONObject:(RAREUTJSONObject *)chartsInfo;
@end

@interface CCPBVLabs_$1 : NSObject < RAREUTiFilter > {
 @public
  CCPBVLabs *this$0_;
  NSString *val$cat_;
  id<JavaUtilList> val$fkeys_;
}

- (BOOL)passesWithId:(id)value
withRAREUTiStringConverter:(id<RAREUTiStringConverter>)converter;
- (id)initWithCCPBVLabs:(CCPBVLabs *)outer$
           withNSString:(NSString *)capture$0
       withJavaUtilList:(id<JavaUtilList>)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVLabs_$1, this$0_, CCPBVLabs *)
J2OBJC_FIELD_SETTER(CCPBVLabs_$1, val$cat_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVLabs_$1, val$fkeys_, id<JavaUtilList>)

@interface CCPBVLabs_$2 : NSObject < RAREiFunctionCallback > {
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

J2OBJC_FIELD_SETTER(CCPBVLabs_$2, val$win_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVLabs_$2, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVLabs_$2, val$json_, RAREUTJSONObject *)
J2OBJC_FIELD_SETTER(CCPBVLabs_$2, val$row_, RARERenderableDataItem *)

@interface CCPBVLabs_$3 : RAREaWorkerTask {
 @public
  CCPBVLabs *this$0_;
  RARETableViewer *val$table_;
  id<JavaUtilList> val$rows_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithCCPBVLabs:(CCPBVLabs *)outer$
    withRARETableViewer:(RARETableViewer *)capture$0
       withJavaUtilList:(id<JavaUtilList>)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVLabs_$3, this$0_, CCPBVLabs *)
J2OBJC_FIELD_SETTER(CCPBVLabs_$3, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVLabs_$3, val$rows_, id<JavaUtilList>)

@interface CCPBVLabs_$4 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiWidget> val$label_;
  NSString *val$value_;
}

- (void)run;
- (id)initWithRAREiWidget:(id<RAREiWidget>)capture$0
             withNSString:(NSString *)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVLabs_$4, val$label_, id<RAREiWidget>)
J2OBJC_FIELD_SETTER(CCPBVLabs_$4, val$value_, NSString *)

#endif // _CCPBVLabs_H_
