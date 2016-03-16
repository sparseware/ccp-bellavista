//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Notes.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVNotes_H_
#define _CCPBVNotes_H_

@class CCPBVDocument;
@class JavaUtilEventObject;
@class JavaUtilHashMap;
@class RAREActionEvent;
@class RAREActionLink;
@class RARERenderableDataItem;
@class RAREStackPaneViewer;
@class RARETableViewer;
@class RAREUTJSONObject;
@protocol JavaUtilList;
@protocol RAREiPlatformIcon;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "com/sparseware/bellavista/aResultsManager.h"
#include "com/sparseware/bellavista/iValueChecker.h"
#include "java/lang/Runnable.h"

@interface CCPBVNotes : CCPBVaResultsManager < CCPBViValueChecker > {
 @public
  int attachmentColumn_;
  int parentColumn_;
  int documentURLColumn_;
  NSString *infoName_;
  NSString *documentPath_;
  CCPBVDocument *loadedDocument_;
  id<RAREiPlatformIcon> attachmentIcon_;
  JavaUtilHashMap *parentMap_;
}

- (id)init;
- (id)initWithNSString:(NSString *)namePrefix
          withNSString:(NSString *)scriptClassName;
- (BOOL)checkRowWithRARERenderableDataItem:(RARERenderableDataItem *)row
                                   withInt:(int)index
                                   withInt:(int)expandableColumn
                                   withInt:(int)rowCount;
- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onFiltersConfigureWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event;
+ (void)goBackToDocumentViewWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp;
- (void)onTableActionWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onTableCreatedWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)reset;
- (void)dataParsedWithRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilList:(id<JavaUtilList>)rows
               withRAREActionLink:(RAREActionLink *)link;
- (void)addAttachmentWithRARETableViewer:(RARETableViewer *)table
                       withCCPBVDocument:(CCPBVDocument *)doc
              withRARERenderableDataItem:(RARERenderableDataItem *)row;
- (void)processDataWithRARETableViewer:(RARETableViewer *)table
                      withJavaUtilList:(id<JavaUtilList>)rows;
- (void)copyAllFieldsTo:(CCPBVNotes *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVNotes, infoName_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVNotes, documentPath_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVNotes, loadedDocument_, CCPBVDocument *)
J2OBJC_FIELD_SETTER(CCPBVNotes, attachmentIcon_, id<RAREiPlatformIcon>)
J2OBJC_FIELD_SETTER(CCPBVNotes, parentMap_, JavaUtilHashMap *)

typedef CCPBVNotes ComSparsewareBellavistaNotes;

@interface CCPBVNotes_FilterAction : NSObject < RAREiActionListener > {
 @public
  RAREUTJSONObject *filter_;
}

- (id)initWithRAREUTJSONObject:(RAREUTJSONObject *)filter;
- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (BOOL)isDefault;
- (void)copyAllFieldsTo:(CCPBVNotes_FilterAction *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVNotes_FilterAction, filter_, RAREUTJSONObject *)

@interface CCPBVNotes_Status : NSObject {
 @public
  NSString *key_;
  NSString *icon_;
}

- (id)init;
- (void)copyAllFieldsTo:(CCPBVNotes_Status *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVNotes_Status, key_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVNotes_Status, icon_, NSString *)

@interface CCPBVNotes_$1 : NSObject < JavaLangRunnable > {
}

- (void)run;
- (id)init;
@end

@interface CCPBVNotes_$2 : NSObject < RAREiFunctionCallback > {
 @public
  RAREStackPaneViewer *val$sp_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVNotes_$2, val$sp_, RAREStackPaneViewer *)

#endif // _CCPBVNotes_H_
