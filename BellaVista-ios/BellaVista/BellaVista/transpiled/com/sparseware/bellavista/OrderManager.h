//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/OrderManager.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVOrderManager_H_
#define _CCPBVOrderManager_H_

@class CCPBVActionPath;
@class CCPBVOrder;
@class CCPBVOrderFields;
@class CCPBVOrderManager_ShowOrderFormCallback;
@class JavaUtilArrayList;
@class RAREActionEvent;
@class RAREActionLink;
@class RAREAlertPanel;
@class RARECheckBoxWidget;
@class RAREComboBoxWidget;
@class RARELabelWidget;
@class RAREListBoxViewer;
@class RAREPushButtonWidget;
@class RARERenderableDataItem;
@class RARESPOTViewer;
@class RARETableViewer;
@class RARETextFieldWidget;
@class RAREUICompoundIcon;
@class RAREUITextIcon;
@class RAREUTIdentityArrayList;
@class RAREUTJSONObject;
@class RAREUTObjectCache;
@class RAREWindowViewer;
@protocol JavaUtilList;
@protocol JavaUtilMap;
@protocol RAREiContainer;
@protocol RAREiPlatformIcon;
@protocol RAREiViewer;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "java/lang/Runnable.h"

@interface CCPBVOrderManager : NSObject {
}

+ (NSString *)ORDER_CART_KEY;
+ (NSString *)ORDER_STATUSES_KEY;
+ (RAREUTObjectCache *)cachedLists;
+ (void)setCachedLists:(RAREUTObjectCache *)cachedLists;
+ (int)pendingOrderCount;
+ (int *)pendingOrderCountRef;
+ (int)lastPendingOrderCount;
+ (int *)lastPendingOrderCountRef;
+ (RAREUITextIcon *)pendingOrderCountIcon;
+ (void)setPendingOrderCountIcon:(RAREUITextIcon *)pendingOrderCountIcon;
+ (RAREUICompoundIcon *)compoundIcon1;
+ (void)setCompoundIcon1:(RAREUICompoundIcon *)compoundIcon1;
+ (RAREUICompoundIcon *)compoundIcon2;
+ (void)setCompoundIcon2:(RAREUICompoundIcon *)compoundIcon2;
+ (NSString *)DEMO_PATIENT_ORDER_KEY;
+ (void)setDEMO_PATIENT_ORDER_KEY:(NSString *)DEMO_PATIENT_ORDER_KEY;
+ (NSString *)DEMO_ORDER_STATUS_FLAGS_KEY;
+ (void)setDEMO_ORDER_STATUS_FLAGS_KEY:(NSString *)DEMO_ORDER_STATUS_FLAGS_KEY;
+ (RAREUTJSONObject *)ordersFilter;
+ (void)setOrdersFilter:(RAREUTJSONObject *)ordersFilter;
+ (RARESPOTViewer *)filterConfiguration;
+ (void)setFilterConfiguration:(RARESPOTViewer *)filterConfiguration;
+ (RARESPOTViewer *)cartConfiguration;
+ (void)setCartConfiguration:(RARESPOTViewer *)cartConfiguration;
+ (id<JavaUtilList>)orderRoutes;
+ (void)setOrderRoutes:(id<JavaUtilList>)orderRoutes;
+ (id<JavaUtilList>)orderTypes;
+ (void)setOrderTypes:(id<JavaUtilList>)orderTypes;
+ (CCPBVOrder *)editorOrder;
+ (void)setEditorOrder:(CCPBVOrder *)editorOrder;
+ (long long int)lastActionTime;
+ (long long int *)lastActionTimeRef;
+ (id)orderCart;
+ (void)setOrderCart:(id)orderCart;
+ (id)demoOrderInfo;
+ (void)setDemoOrderInfo:(id)demoOrderInfo;
+ (id)orderStatuses;
+ (void)setOrderStatuses:(id)orderStatuses;
+ (NSString *)orderingUser;
+ (void)setOrderingUser:(NSString *)orderingUser;
+ (NSString *)patientForOrders;
+ (void)setPatientForOrders:(NSString *)patientForOrders;
+ (CCPBVOrderManager_ShowOrderFormCallback *)showOrderFormCallback;
+ (void)setShowOrderFormCallback:(CCPBVOrderManager_ShowOrderFormCallback *)showOrderFormCallback;
- (id)init;
- (void)cleanupAfterOrdering;
+ (void)addOrderToCartWithCCPBVOrder:(CCPBVOrder *)order;
+ (BOOL)canChangePatientOrExitWithBoolean:(BOOL)exit
                      withCCPBVActionPath:(CCPBVActionPath *)path;
+ (void)emptyOrderCart;
+ (void)getCachedListWithNSString:(NSString *)name
                     withNSString:(NSString *)href
        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (RAREUTJSONObject *)getDemoOrderObjectWithBoolean:(BOOL)create;
+ (int)getDemoOrderStatusWithRAREUTJSONObject:(RAREUTJSONObject *)order;
+ (long long int)getLastOrderActionTtime;
+ (CCPBVOrder *)getOrderBeingEdited;
+ (RAREUTIdentityArrayList *)getOrderCartWithBoolean:(BOOL)create;
+ (long long int)getOrderCartLastModifiedTime;
+ (RAREUTJSONObject *)getOrdersFilter;
+ (RAREUTJSONObject *)getOrdersInfoWithNSString:(NSString *)key;
+ (id<JavaUtilMap>)getOrderStatusesWithBoolean:(BOOL)create;
+ (int)getPendingOrderCount;
+ (id<RAREiPlatformIcon>)getPendingOrderCountIcon;
+ (void)getSignatureWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (BOOL)isDiscontinuedWithNSString:(NSString *)orderID
                   withJavaUtilMap:(id<JavaUtilMap>)orderStatuses;
+ (void)lockingApplication;
+ (void)lockPatientRecord;
+ (void)orderChangeFlagStatusWithRARETableViewer:(RARETableViewer *)table
                                         withInt:(int)index
                                     withBoolean:(BOOL)flag;
+ (void)orderChangeFlagStatusWithRARETableViewer:(RARETableViewer *)table
                                         withInt:(int)index
                                     withBoolean:(BOOL)flag
                                 withJavaUtilMap:(id<JavaUtilMap>)data;
+ (void)orderChangeHoldStatusWithRARETableViewer:(RARETableViewer *)table
                                         withInt:(int)index
                                     withBoolean:(BOOL)hold;
+ (void)orderChangeHoldStatusWithRARETableViewer:(RARETableViewer *)table
                                         withInt:(int)index
                                     withBoolean:(BOOL)hold
                                 withJavaUtilMap:(id<JavaUtilMap>)data;
+ (void)orderDiscontinueWithRARETableViewer:(RARETableViewer *)table
                                    withInt:(int)index;
+ (void)orderDiscontinueWithRARETableViewer:(RARETableViewer *)table
                                    withInt:(int)index
                       withCCPBVOrderFields:(CCPBVOrderFields *)fields
                            withJavaUtilMap:(id<JavaUtilMap>)values;
+ (void)orderNewWithNSString:(NSString *)type
  withRARERenderableDataItem:(RARERenderableDataItem *)orderItem;
+ (void)orderRenewWithNSString:(NSString *)type
    withRARERenderableDataItem:(RARERenderableDataItem *)orderItem;
+ (void)orderRenewAndDiscontinueWithNSString:(NSString *)type
                  withRARERenderableDataItem:(RARERenderableDataItem *)orderItem
                                withNSString:(NSString *)linkedID;
+ (void)orderSignWithRARETableViewer:(RARETableViewer *)table
                             withInt:(int)index;
+ (void)orderSignExWithRARETableViewer:(RARETableViewer *)table
                               withInt:(int)index
                       withJavaUtilMap:(id<JavaUtilMap>)values;
+ (void)patientChanged;
+ (void)pushOrderEntryViewerWithRAREiViewer:(id<RAREiViewer>)v
                       withJavaLangRunnable:(id<JavaLangRunnable>)r;
+ (int)removeOrderFromCartWithCCPBVOrder:(CCPBVOrder *)order;
+ (void)searchCatalogWithNSString:(NSString *)text
        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (void)showCart;
+ (void)showFilterDialogWithRAREiWidget:(id<RAREiWidget>)filterValueWidget
                            withBoolean:(BOOL)always;
+ (void)showOrderFormWithCCPBVOrder:(CCPBVOrder *)order;
+ (void)showOrderFormWithNSString:(NSString *)type
                     withNSString:(NSString *)renewID
       withRARERenderableDataItem:(RARERenderableDataItem *)orderedItem
                     withNSString:(NSString *)fieldsID;
+ (void)submitOrdersWithJavaUtilList:(id<JavaUtilList>)orders
           withRAREiFunctionCallback:(id<RAREiFunctionCallback>)notifier;
+ (void)submitOrdersWithNSString:(NSString *)signatureToken
                withJavaUtilList:(id<JavaUtilList>)orders
       withRAREiFunctionCallback:(id<RAREiFunctionCallback>)notifier;
+ (void)sumbitOrderWithCCPBVOrder:(CCPBVOrder *)order
        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)notifier;
+ (void)sumbitOrderCartWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)notifier;
+ (void)testSignature;
+ (void)unlockPatientRecord;
+ (void)updateCartIcon;
+ (void)updateDemoOrderObjectWithNSString:(NSString *)id_
                             withNSString:(NSString *)key
                                   withId:(id)value;
+ (void)updateFilterWidgetWithRAREiWidget:(id<RAREiWidget>)filterValueWidget;
+ (void)updateOrderCartTimestamp;
+ (void)yesNoWithRAREWindowViewer:(RAREWindowViewer *)w
             withRAREUTJSONObject:(RAREUTJSONObject *)info
               withRAREiContainer:(id<RAREiContainer>)content
        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (void)orderDiscontinueExWithCCPBVOrder:(CCPBVOrder *)order;
+ (void)orderDiscontinueExWithRARETableViewer:(RARETableViewer *)table
                                      withInt:(int)index
                   withRARERenderableDataItem:(RARERenderableDataItem *)row
                         withRAREUTJSONObject:(RAREUTJSONObject *)info;
+ (CCPBVOrderFields *)getOrderFieldsWithNSString:(NSString *)id_
                            withRAREUTJSONObject:(RAREUTJSONObject *)info;
+ (void)handleLinksWithJavaUtilArrayList:(JavaUtilArrayList *)links
                    withRAREWindowViewer:(RAREWindowViewer *)w
                    withRAREUTJSONObject:(RAREUTJSONObject *)info
                      withRAREiContainer:(id<RAREiContainer>)content
               withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (void)removeOrdersWithJavaUtilList:(id<JavaUtilList>)orders;
+ (void)showOrderingScreenWithNSString:(NSString *)type
            withRARERenderableDataItem:(RARERenderableDataItem *)orderItem
                           withBoolean:(BOOL)renew;
+ (void)emptyOrderCartEx;
+ (BOOL)removeOrderFromCartExWithJavaUtilList:(id<JavaUtilList>)cart
                               withCCPBVOrder:(CCPBVOrder *)order;
@end

typedef CCPBVOrderManager ComSparsewareBellavistaOrderManager;

@interface CCPBVOrderManager_WidgetDataLink : NSObject {
 @public
  id<RAREiWidget> list_;
  RAREActionLink *link_;
}

- (id)initWithRAREiWidget:(id<RAREiWidget>)list
       withRAREActionLink:(RAREActionLink *)link;
- (void)handle;
- (void)copyAllFieldsTo:(CCPBVOrderManager_WidgetDataLink *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_WidgetDataLink, list_, id<RAREiWidget>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_WidgetDataLink, link_, RAREActionLink *)

@interface CCPBVOrderManager_GetOrderSentencesTask : RAREaWorkerTask < RAREiFunctionCallback > {
 @public
  CCPBVOrder *order_;
  id<JavaUtilList> sentences_;
}

- (id)initWithCCPBVOrder:(CCPBVOrder *)order;
- (id)compute;
- (void)finishWithId:(id)result;
- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (void)copyAllFieldsTo:(CCPBVOrderManager_GetOrderSentencesTask *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_GetOrderSentencesTask, order_, CCPBVOrder *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_GetOrderSentencesTask, sentences_, id<JavaUtilList>)

@interface CCPBVOrderManager_GetOrderSentencesTask_$1 : RAREaWorkerTask {
 @public
  CCPBVOrderManager_GetOrderSentencesTask *this$0_;
  id val$returnValue_;
  RAREWindowViewer *val$w_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithCCPBVOrderManager_GetOrderSentencesTask:(CCPBVOrderManager_GetOrderSentencesTask *)outer$
                                               withId:(id)capture$0
                                 withRAREWindowViewer:(RAREWindowViewer *)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_GetOrderSentencesTask_$1, this$0_, CCPBVOrderManager_GetOrderSentencesTask *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_GetOrderSentencesTask_$1, val$returnValue_, id)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_GetOrderSentencesTask_$1, val$w_, RAREWindowViewer *)

@interface CCPBVOrderManager_ShowOrderFormCallback : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiContainer> viewer_;
  CCPBVOrder *order_;
  BOOL update_;
  id<JavaUtilMap> values_;
}

- (id)initWithRAREiContainer:(id<RAREiContainer>)viewer
              withCCPBVOrder:(CCPBVOrder *)order
                 withBoolean:(BOOL)update;
- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (void)copyAllFieldsTo:(CCPBVOrderManager_ShowOrderFormCallback *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_ShowOrderFormCallback, viewer_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_ShowOrderFormCallback, order_, CCPBVOrder *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_ShowOrderFormCallback, values_, id<JavaUtilMap>)

@interface CCPBVOrderManager_ShowOrderFormCallback_$1 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVOrderManager_ShowOrderFormCallback *this$0_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVOrderManager_ShowOrderFormCallback:(CCPBVOrderManager_ShowOrderFormCallback *)outer$;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_ShowOrderFormCallback_$1, this$0_, CCPBVOrderManager_ShowOrderFormCallback *)

@interface CCPBVOrderManager_SignatureHandler : RAREaWorkerTask < RAREiActionListener, RAREiFunctionCallback > {
 @public
  RARETextFieldWidget *field_;
  RARELabelWidget *statusLabel_;
  RAREAlertPanel *panel_;
  id<RAREiFunctionCallback> callback_SignatureHandler_;
  NSString *signature_;
}

- (id)initWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)compute;
- (void)finishWithId:(id)result;
- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (void)handle;
- (void)copyAllFieldsTo:(CCPBVOrderManager_SignatureHandler *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_SignatureHandler, field_, RARETextFieldWidget *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_SignatureHandler, statusLabel_, RARELabelWidget *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_SignatureHandler, panel_, RAREAlertPanel *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_SignatureHandler, callback_SignatureHandler_, id<RAREiFunctionCallback>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_SignatureHandler, signature_, NSString *)

@interface CCPBVOrderManager_SignatureHandler_$1 : NSObject < RAREiActionListener > {
 @public
  RAREPushButtonWidget *val$ok_;
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)initWithRAREPushButtonWidget:(RAREPushButtonWidget *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_SignatureHandler_$1, val$ok_, RAREPushButtonWidget *)

@interface CCPBVOrderManager_$1 : NSObject < RAREiFunctionCallback > {
 @public
  RAREWindowViewer *val$w_;
  BOOL val$exit_;
  CCPBVActionPath *val$path_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREWindowViewer:(RAREWindowViewer *)capture$0
                   withBoolean:(BOOL)capture$1
           withCCPBVActionPath:(CCPBVActionPath *)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$1, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$1, val$path_, CCPBVActionPath *)

@interface CCPBVOrderManager_$2 : RAREaWorkerTask {
 @public
  NSString *val$href_;
  id<RAREiFunctionCallback> val$cb_;
  NSString *val$name_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithNSString:(NSString *)capture$0
withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$1
          withNSString:(NSString *)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$2, val$href_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$2, val$cb_, id<RAREiFunctionCallback>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$2, val$name_, NSString *)

@interface CCPBVOrderManager_$3 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiContainer> val$c_;
  RARETableViewer *val$table_;
  int val$index_;
  BOOL val$flag_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiContainer:(id<RAREiContainer>)capture$0
         withRARETableViewer:(RARETableViewer *)capture$1
                     withInt:(int)capture$2
                 withBoolean:(BOOL)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$3, val$c_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$3, val$table_, RARETableViewer *)

@interface CCPBVOrderManager_$4 : RAREaWorkerTask {
 @public
  RARERenderableDataItem *val$row_;
  BOOL val$flag_;
  id<JavaUtilMap> val$data_;
  RARETableViewer *val$table_;
  RAREWindowViewer *val$w_;
  int val$index_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithRARERenderableDataItem:(RARERenderableDataItem *)capture$0
                         withBoolean:(BOOL)capture$1
                     withJavaUtilMap:(id<JavaUtilMap>)capture$2
                 withRARETableViewer:(RARETableViewer *)capture$3
                withRAREWindowViewer:(RAREWindowViewer *)capture$4
                             withInt:(int)capture$5;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$4, val$row_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$4, val$data_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$4, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$4, val$w_, RAREWindowViewer *)

@interface CCPBVOrderManager_$5 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiContainer> val$c_;
  RARETableViewer *val$table_;
  int val$index_;
  BOOL val$hold_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiContainer:(id<RAREiContainer>)capture$0
         withRARETableViewer:(RARETableViewer *)capture$1
                     withInt:(int)capture$2
                 withBoolean:(BOOL)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$5, val$c_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$5, val$table_, RARETableViewer *)

@interface CCPBVOrderManager_$6 : RAREaWorkerTask {
 @public
  RARERenderableDataItem *val$row_;
  BOOL val$hold_;
  id<JavaUtilMap> val$data_;
  RARETableViewer *val$table_;
  RAREWindowViewer *val$w_;
  int val$index_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithRARERenderableDataItem:(RARERenderableDataItem *)capture$0
                         withBoolean:(BOOL)capture$1
                     withJavaUtilMap:(id<JavaUtilMap>)capture$2
                 withRARETableViewer:(RARETableViewer *)capture$3
                withRAREWindowViewer:(RAREWindowViewer *)capture$4
                             withInt:(int)capture$5;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$6, val$row_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$6, val$data_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$6, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$6, val$w_, RAREWindowViewer *)

@interface CCPBVOrderManager_$7 : NSObject < RAREiFunctionCallback > {
 @public
  RAREUTJSONObject *val$info_;
  RARETableViewer *val$table_;
  int val$index_;
  RARERenderableDataItem *val$row_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREUTJSONObject:(RAREUTJSONObject *)capture$0
           withRARETableViewer:(RARETableViewer *)capture$1
                       withInt:(int)capture$2
    withRARERenderableDataItem:(RARERenderableDataItem *)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$7, val$info_, RAREUTJSONObject *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$7, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$7, val$row_, RARERenderableDataItem *)

@interface CCPBVOrderManager_$8 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiContainer> val$c_;
  RARETableViewer *val$table_;
  int val$index_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiContainer:(id<RAREiContainer>)capture$0
         withRARETableViewer:(RARETableViewer *)capture$1
                     withInt:(int)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$8, val$c_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$8, val$table_, RARETableViewer *)

@interface CCPBVOrderManager_$9 : RAREaWorkerTask {
 @public
  RARERenderableDataItem *val$row_;
  RARETableViewer *val$table_;
  id<JavaUtilMap> val$values_;
  RAREWindowViewer *val$w_;
  int val$index_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithRARERenderableDataItem:(RARERenderableDataItem *)capture$0
                 withRARETableViewer:(RARETableViewer *)capture$1
                     withJavaUtilMap:(id<JavaUtilMap>)capture$2
                withRAREWindowViewer:(RAREWindowViewer *)capture$3
                             withInt:(int)capture$4;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$9, val$row_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$9, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$9, val$values_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$9, val$w_, RAREWindowViewer *)

@interface CCPBVOrderManager_$10 : NSObject < RAREiFunctionCallback > {
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)init;
@end

@interface CCPBVOrderManager_$11 : NSObject < RAREiFunctionCallback > {
 @public
  RARECheckBoxWidget *val$startsWith_;
  RAREComboBoxWidget *val$type_;
  RAREListBoxViewer *val$route_;
  id<RAREiContainer> val$viewer_;
  id<RAREiWidget> val$filterValueWidget_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRARECheckBoxWidget:(RARECheckBoxWidget *)capture$0
          withRAREComboBoxWidget:(RAREComboBoxWidget *)capture$1
           withRAREListBoxViewer:(RAREListBoxViewer *)capture$2
              withRAREiContainer:(id<RAREiContainer>)capture$3
                 withRAREiWidget:(id<RAREiWidget>)capture$4;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$11, val$startsWith_, RARECheckBoxWidget *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$11, val$type_, RAREComboBoxWidget *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$11, val$route_, RAREListBoxViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$11, val$viewer_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$11, val$filterValueWidget_, id<RAREiWidget>)

@interface CCPBVOrderManager_$12 : NSObject < JavaLangRunnable > {
 @public
  RAREPushButtonWidget *val$ok_;
}

- (void)run;
- (id)initWithRAREPushButtonWidget:(RAREPushButtonWidget *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$12, val$ok_, RAREPushButtonWidget *)

@interface CCPBVOrderManager_$13 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVOrder *val$order_;
  RAREWindowViewer *val$w_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVOrder:(CCPBVOrder *)capture$0
    withRAREWindowViewer:(RAREWindowViewer *)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$13, val$order_, CCPBVOrder *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$13, val$w_, RAREWindowViewer *)

@interface CCPBVOrderManager_$13_$1 : NSObject < JavaLangRunnable > {
 @public
  CCPBVOrderManager_ShowOrderFormCallback *val$cb_;
}

- (void)run;
- (id)initWithCCPBVOrderManager_ShowOrderFormCallback:(CCPBVOrderManager_ShowOrderFormCallback *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$13_$1, val$cb_, CCPBVOrderManager_ShowOrderFormCallback *)

@interface CCPBVOrderManager_$13_$2 : NSObject < JavaLangRunnable > {
 @public
  CCPBVOrderManager_ShowOrderFormCallback *val$cb_;
}

- (void)run;
- (id)initWithCCPBVOrderManager_ShowOrderFormCallback:(CCPBVOrderManager_ShowOrderFormCallback *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$13_$2, val$cb_, CCPBVOrderManager_ShowOrderFormCallback *)

@interface CCPBVOrderManager_$13_$3 : NSObject < JavaLangRunnable > {
 @public
  CCPBVOrderManager_ShowOrderFormCallback *val$cb_;
}

- (void)run;
- (id)initWithCCPBVOrderManager_ShowOrderFormCallback:(CCPBVOrderManager_ShowOrderFormCallback *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$13_$3, val$cb_, CCPBVOrderManager_ShowOrderFormCallback *)

@interface CCPBVOrderManager_$14 : RAREaWorkerTask {
 @public
  NSString *val$fieldsID_;
  NSString *val$renewID_;
  CCPBVOrder *val$order_;
  RAREWindowViewer *val$w_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithNSString:(NSString *)capture$0
          withNSString:(NSString *)capture$1
        withCCPBVOrder:(CCPBVOrder *)capture$2
  withRAREWindowViewer:(RAREWindowViewer *)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$14, val$fieldsID_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$14, val$renewID_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$14, val$order_, CCPBVOrder *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$14, val$w_, RAREWindowViewer *)

@interface CCPBVOrderManager_$15 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiFunctionCallback> val$notifier_;
  id<JavaUtilList> val$orders_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0
                   withJavaUtilList:(id<JavaUtilList>)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$15, val$notifier_, id<RAREiFunctionCallback>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$15, val$orders_, id<JavaUtilList>)

@interface CCPBVOrderManager_$16 : NSObject < JavaLangRunnable > {
 @public
  id<JavaUtilList> val$orders_;
  RAREWindowViewer *val$w_;
  id<RAREiFunctionCallback> val$notifier_;
}

- (void)run;
- (id)initWithJavaUtilList:(id<JavaUtilList>)capture$0
      withRAREWindowViewer:(RAREWindowViewer *)capture$1
 withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$16, val$orders_, id<JavaUtilList>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$16, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$16, val$notifier_, id<RAREiFunctionCallback>)

@interface CCPBVOrderManager_$17 : NSObject < RAREiFunctionCallback > {
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)init;
@end

@interface CCPBVOrderManager_$18 : NSObject < RAREiActionListener > {
 @public
  id<RAREiContainer> val$content_;
  RAREAlertPanel *val$d_;
  RAREWindowViewer *val$w_;
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)initWithRAREiContainer:(id<RAREiContainer>)capture$0
          withRAREAlertPanel:(RAREAlertPanel *)capture$1
        withRAREWindowViewer:(RAREWindowViewer *)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$18, val$content_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$18, val$d_, RAREAlertPanel *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$18, val$w_, RAREWindowViewer *)

@interface CCPBVOrderManager_$18_$1 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiWidget> val$mb_;
  id<RAREiWidget> val$widget_;
}

- (void)run;
- (id)initWithRAREiWidget:(id<RAREiWidget>)capture$0
          withRAREiWidget:(id<RAREiWidget>)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$18_$1, val$mb_, id<RAREiWidget>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$18_$1, val$widget_, id<RAREiWidget>)

@interface CCPBVOrderManager_$19 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVOrder *val$order_;
  id<JavaUtilMap> val$values_;
  id<RAREiContainer> val$c_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVOrder:(CCPBVOrder *)capture$0
         withJavaUtilMap:(id<JavaUtilMap>)capture$1
      withRAREiContainer:(id<RAREiContainer>)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$19, val$order_, CCPBVOrder *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$19, val$values_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$19, val$c_, id<RAREiContainer>)

@interface CCPBVOrderManager_$20 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiContainer> val$c_;
  RARETableViewer *val$table_;
  int val$index_;
  RAREUTJSONObject *val$info_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiContainer:(id<RAREiContainer>)capture$0
         withRARETableViewer:(RARETableViewer *)capture$1
                     withInt:(int)capture$2
        withRAREUTJSONObject:(RAREUTJSONObject *)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$20, val$c_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$20, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$20, val$info_, RAREUTJSONObject *)

@interface CCPBVOrderManager_$21 : NSObject < JavaLangRunnable > {
 @public
  JavaUtilArrayList *val$links_;
  RAREWindowViewer *val$w_;
  RAREUTJSONObject *val$info_;
  id<RAREiContainer> val$content_;
  id<RAREiFunctionCallback> val$cb_;
}

- (void)run;
- (id)initWithJavaUtilArrayList:(JavaUtilArrayList *)capture$0
           withRAREWindowViewer:(RAREWindowViewer *)capture$1
           withRAREUTJSONObject:(RAREUTJSONObject *)capture$2
             withRAREiContainer:(id<RAREiContainer>)capture$3
      withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$4;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$21, val$links_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$21, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$21, val$info_, RAREUTJSONObject *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$21, val$content_, id<RAREiContainer>)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$21, val$cb_, id<RAREiFunctionCallback>)

@interface CCPBVOrderManager_$21_$1 : NSObject < JavaLangRunnable > {
 @public
  CCPBVOrderManager_$21 *this$0_;
}

- (void)run;
- (id)initWithCCPBVOrderManager_$21:(CCPBVOrderManager_$21 *)outer$;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$21_$1, this$0_, CCPBVOrderManager_$21 *)

@interface CCPBVOrderManager_$22 : RAREaWorkerTask {
 @public
  RAREWindowViewer *val$w_;
  RARERenderableDataItem *val$orderItem_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithRAREWindowViewer:(RAREWindowViewer *)capture$0
    withRARERenderableDataItem:(RARERenderableDataItem *)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVOrderManager_$22, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVOrderManager_$22, val$orderItem_, RARERenderableDataItem *)

@interface CCPBVOrderManager_$22_$1 : NSObject < JavaLangRunnable > {
}

- (void)run;
- (id)init;
@end

#endif // _CCPBVOrderManager_H_
