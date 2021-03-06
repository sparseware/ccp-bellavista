//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/oe/FormsManager.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVFormsManager_H_
#define _CCPBVFormsManager_H_

@class CCPBVFieldValue;
@class CCPBVOrder;
@class JavaUtilArrayList;
@class RARERenderableDataItem;
@class RARESPOTLabel;
@class RAREUIFont;
@class RAREUTJSONObject;
@class RAREUTMutableInteger;
@class RAREWindowViewer;
@protocol JavaUtilList;
@protocol JavaUtilMap;
@protocol RAREiContainer;
@protocol RAREiPlatformBorder;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "java/lang/Runnable.h"

@interface CCPBVFormsManager : NSObject {
}

+ (NSString *)decimalFormat;
+ (void)setDecimalFormat:(NSString *)decimalFormat;
+ (int)decimalFormatPosition;
+ (int *)decimalFormatPositionRef;
+ (RARESPOTLabel *)promptLabel;
+ (void)setPromptLabel:(RARESPOTLabel *)promptLabel;
+ (NSString *)FOCUS_GAINED_EVENT;
+ (NSString *)FOCUS_LOST_EVENT;
+ (NSString *)VISIBILITY_TOGGLE_ACTION;
+ (NSString *)VALUE_CHANGED;
+ (NSString *)VALUE_ACTION;
+ (id<RAREiPlatformBorder>)popupPaddingBorder;
+ (void)setPopupPaddingBorder:(id<RAREiPlatformBorder>)popupPaddingBorder;
+ (BOOL)addColonToPrompt;
+ (BOOL *)addColonToPromptRef;
+ (id<RAREiWidget>)updatingWidget;
+ (void)setUpdatingWidget:(id<RAREiWidget>)updatingWidget;
+ (void)handleTogglingItemSelectedWithRAREiWidget:(id<RAREiWidget>)widget;
+ (void)handleGroupWithRAREWindowViewer:(RAREWindowViewer *)w
                   withRAREUTJSONObject:(RAREUTJSONObject *)gfield
                     withRAREiContainer:(id<RAREiContainer>)group;
+ (id<RAREiWidget>)addWidgetWithRAREiContainer:(id<RAREiContainer>)parent
                           withCCPBVFieldValue:(CCPBVFieldValue *)fv
                          withRAREWindowViewer:(RAREWindowViewer *)w
                                   withBoolean:(BOOL)combosForSpinners
                                   withBoolean:(BOOL)combosForBoolean
                                   withBoolean:(BOOL)combosForList
                      withRAREUTMutableInteger:(RAREUTMutableInteger *)yPosition
                                   withBoolean:(BOOL)standalone
                         withJavaUtilArrayList:(JavaUtilArrayList *)links;
+ (id<RAREiContainer>)createFormContainerWithRAREWindowViewer:(RAREWindowViewer *)w
                                   withRARERenderableDataItem:(RARERenderableDataItem *)row
                                         withRAREUTJSONObject:(RAREUTJSONObject *)info
                                              withJavaUtilMap:(id<JavaUtilMap>)values
                                        withJavaUtilArrayList:(JavaUtilArrayList *)links;
+ (id<RAREiContainer>)createFormContainerWithRAREWindowViewer:(RAREWindowViewer *)w
                                               withCCPBVOrder:(CCPBVOrder *)order
                                        withJavaUtilArrayList:(JavaUtilArrayList *)links;
+ (id<RAREiContainer>)createGroupWithRAREiContainer:(id<RAREiContainer>)parent
                               withRAREUTJSONObject:(RAREUTJSONObject *)field
                               withRAREWindowViewer:(RAREWindowViewer *)w
                           withRAREUTMutableInteger:(RAREUTMutableInteger *)yPosition
                                    withJavaUtilMap:(id<JavaUtilMap>)values
                              withJavaUtilArrayList:(JavaUtilArrayList *)links;
+ (void)populateContainerWithRAREiContainer:(id<RAREiContainer>)c
                           withJavaUtilList:(id<JavaUtilList>)fields
                                    withInt:(int)widgetCount
                       withRAREWindowViewer:(RAREWindowViewer *)w
                            withJavaUtilMap:(id<JavaUtilMap>)values
                      withJavaUtilArrayList:(JavaUtilArrayList *)links;
+ (void)populateListWithJavaUtilList:(id<JavaUtilList>)list
                    withJavaUtilList:(id<JavaUtilList>)fields
                      withRAREUIFont:(RAREUIFont *)bold
                withRAREWindowViewer:(RAREWindowViewer *)w
                         withBoolean:(BOOL)requiredOnly
                         withBoolean:(BOOL)optionalOnly;
+ (void)updateValueFromWidgetWithRAREiWidget:(id<RAREiWidget>)widget;
+ (void)updateWidgetFromValueWithRAREiWidget:(id<RAREiWidget>)widget
                         withCCPBVFieldValue:(CCPBVFieldValue *)fv;
+ (id<JavaUtilMap>)getValuesMapWithRAREiWidget:(id<RAREiWidget>)widget;
+ (id<RAREiWidget>)getInvalidWidgetWithRAREiContainer:(id<RAREiContainer>)c;
+ (id<JavaUtilMap>)resolveValuesMapWithRAREiWidget:(id<RAREiWidget>)widget
                               withCCPBVFieldValue:(CCPBVFieldValue *)fv;
- (id)init;
@end

typedef CCPBVFormsManager ComSparsewareBellavistaOeFormsManager;

@interface CCPBVFormsManager_$1 : NSObject < JavaLangRunnable > {
 @public
  JavaUtilArrayList *val$links_;
  RAREWindowViewer *val$w_;
  RAREUTJSONObject *val$ggfield_;
  id<RAREiContainer> val$group_;
}

- (void)run;
- (id)initWithJavaUtilArrayList:(JavaUtilArrayList *)capture$0
           withRAREWindowViewer:(RAREWindowViewer *)capture$1
           withRAREUTJSONObject:(RAREUTJSONObject *)capture$2
             withRAREiContainer:(id<RAREiContainer>)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVFormsManager_$1, val$links_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(CCPBVFormsManager_$1, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVFormsManager_$1, val$ggfield_, RAREUTJSONObject *)
J2OBJC_FIELD_SETTER(CCPBVFormsManager_$1, val$group_, id<RAREiContainer>)

@interface CCPBVFormsManager_$1_$1 : NSObject < JavaLangRunnable > {
 @public
  CCPBVFormsManager_$1 *this$0_;
}

- (void)run;
- (id)initWithCCPBVFormsManager_$1:(CCPBVFormsManager_$1 *)outer$;
@end

J2OBJC_FIELD_SETTER(CCPBVFormsManager_$1_$1, this$0_, CCPBVFormsManager_$1 *)

@interface CCPBVFormsManager_$2 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiContainer> val$group_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiContainer:(id<RAREiContainer>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVFormsManager_$2, val$group_, id<RAREiContainer>)

#endif // _CCPBVFormsManager_H_
