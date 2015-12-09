//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Alerts.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _CCPBVAlerts_H_
#define _CCPBVAlerts_H_

@class JavaUtilEventObject;
@class RAREActionLink;
@class RAREUICompoundIcon;
@class RAREUITextIcon;
@protocol JavaUtilList;
@protocol RAREiPlatformIcon;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/aEventHandler.h"

@interface CCPBVAlerts : CCPBVaEventHandler {
}

+ (int)lastAlertCount;
+ (int *)lastAlertCountRef;
+ (RAREUITextIcon *)alertCountIcon;
+ (void)setAlertCountIcon:(RAREUITextIcon *)alertCountIcon;
+ (RAREUICompoundIcon *)compoundIcon1;
+ (void)setCompoundIcon1:(RAREUICompoundIcon *)compoundIcon1;
+ (RAREUICompoundIcon *)compoundIcon2;
+ (void)setCompoundIcon2:(RAREUICompoundIcon *)compoundIcon2;
+ (id<RAREiPlatformIcon>)getAlertCountIcon;
+ (void)updateAlertsIcon;
- (void)onUpdateTableWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)dataParsedWithRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilList:(id<JavaUtilList>)rows
               withRAREActionLink:(RAREActionLink *)link;
- (id)init;
@end

typedef CCPBVAlerts ComSparsewareBellavistaAlerts;

#endif // _CCPBVAlerts_H_