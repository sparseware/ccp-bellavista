//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/oe/Order.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVOrder_H_
#define _CCPBVOrder_H_

@class CCPBVOrderFields;
@class CCPBVOrder_ActionTypeEnum;
@class RARERenderableDataItem;
@class RAREUTCharArray;
@protocol JavaUtilMap;
@protocol RAREiPlatformIcon;

#import "JreEmulation.h"
#include "java/lang/Enum.h"

@interface CCPBVOrder : NSObject {
 @public
  NSString *orderType_;
  CCPBVOrder_ActionTypeEnum *actionType_;
  NSString *orderID_;
  NSString *linkedOrderID_;
  RARERenderableDataItem *orderedItem_;
  RARERenderableDataItem *directionsItem_;
  id<JavaUtilMap> orderValues_;
  CCPBVOrderFields *orderFields_;
  BOOL complete_;
  BOOL dirty_;
  id<RAREiPlatformIcon> documentIcon_;
  id<RAREiPlatformIcon> discontinueIcon_;
  id<RAREiPlatformIcon> documentIncompleteIcon_;
}

- (id)initWithNSString:(NSString *)orderType
withCCPBVOrder_ActionTypeEnum:(CCPBVOrder_ActionTypeEnum *)actionType
          withNSString:(NSString *)orderID
          withNSString:(NSString *)orderName;
- (id)initWithNSString:(NSString *)orderType
withCCPBVOrder_ActionTypeEnum:(CCPBVOrder_ActionTypeEnum *)actionType
withRARERenderableDataItem:(RARERenderableDataItem *)item;
- (NSString *)getOrderedItemID;
+ (CCPBVOrder *)createDiscontinuedOrderWithRARERenderableDataItem:(RARERenderableDataItem *)selectedItem
                                             withCCPBVOrderFields:(CCPBVOrderFields *)fields
                                                  withJavaUtilMap:(id<JavaUtilMap>)values;
- (id<RAREiPlatformIcon>)getActionTypeIcon;
- (void)updateDirectionsItem;
- (id<JavaUtilMap>)copyValues OBJC_METHOD_FAMILY_NONE;
- (void)updateValuesWithJavaUtilMap:(id<JavaUtilMap>)values
                withRAREUTCharArray:(RAREUTCharArray *)ca;
- (BOOL)isComplete;
- (void)setCompleteWithBoolean:(BOOL)complete;
- (BOOL)isDirty;
- (void)setDirtyWithBoolean:(BOOL)dirty;
- (void)copyAllFieldsTo:(CCPBVOrder *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVOrder, orderType_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVOrder, actionType_, CCPBVOrder_ActionTypeEnum *)
J2OBJC_FIELD_SETTER(CCPBVOrder, orderID_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVOrder, linkedOrderID_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVOrder, orderedItem_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVOrder, directionsItem_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVOrder, orderValues_, id<JavaUtilMap>)
J2OBJC_FIELD_SETTER(CCPBVOrder, orderFields_, CCPBVOrderFields *)
J2OBJC_FIELD_SETTER(CCPBVOrder, documentIcon_, id<RAREiPlatformIcon>)
J2OBJC_FIELD_SETTER(CCPBVOrder, discontinueIcon_, id<RAREiPlatformIcon>)
J2OBJC_FIELD_SETTER(CCPBVOrder, documentIncompleteIcon_, id<RAREiPlatformIcon>)

typedef CCPBVOrder ComSparsewareBellavistaOeOrder;

typedef enum {
  CCPBVOrder_ActionType_NEW = 0,
  CCPBVOrder_ActionType_DISCONTINUED = 1,
  CCPBVOrder_ActionType_RENEWED = 2,
} CCPBVOrder_ActionType;

@interface CCPBVOrder_ActionTypeEnum : JavaLangEnum < NSCopying > {
}
+ (CCPBVOrder_ActionTypeEnum *)NEW;
+ (CCPBVOrder_ActionTypeEnum *)DISCONTINUED;
+ (CCPBVOrder_ActionTypeEnum *)RENEWED;
+ (IOSObjectArray *)values;
+ (CCPBVOrder_ActionTypeEnum *)valueOfWithNSString:(NSString *)name;
- (id)copyWithZone:(NSZone *)zone;
- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal;
@end

#endif // _CCPBVOrder_H_
