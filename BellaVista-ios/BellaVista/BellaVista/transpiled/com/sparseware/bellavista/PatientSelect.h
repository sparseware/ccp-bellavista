//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/PatientSelect.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVPatientSelect_H_
#define _CCPBVPatientSelect_H_

@class CCPBVActionPath;
@class CCPBVListPager;
@class CCPBVPatientSelect_CardPatientSelectionActionListener;
@class CCPBVPatientSelect_PatientIcon;
@class CCPBVaBarcodeReader;
@class CCPBVaPatientLocator;
@class IOSClass;
@class IOSObjectArray;
@class JavaUtilEventObject;
@class RAREActionEvent;
@class RAREActionLink;
@class RARERenderableDataItem;
@class RARETableViewer;
@class RAREUIImage;
@class RAREUILineBorder;
@class RAREUIPopupMenu;
@class RAREUTJSONObject;
@class RAREWindowViewer;
@protocol JavaUtilList;
@protocol RAREUTiFilter;
@protocol RAREiPlatformAppContext;
@protocol RAREiPlatformGraphics;
@protocol RAREiPlatformIcon;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/aPlatformIcon.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "com/appnativa/rare/ui/event/iChangeListener.h"
#include "com/appnativa/rare/ui/iEventHandler.h"
#include "java/lang/Runnable.h"
#include "java/util/Comparator.h"

#define CCPBVPatientSelect_ADMIT_DATE 6
#define CCPBVPatientSelect_ADMIT_DX 7
#define CCPBVPatientSelect_DOB 2
#define CCPBVPatientSelect_DOCTOR 5
#define CCPBVPatientSelect_GENDER 3
#define CCPBVPatientSelect_ID 0
#define CCPBVPatientSelect_LOCATION 8
#define CCPBVPatientSelect_MRN 4
#define CCPBVPatientSelect_NAME 1
#define CCPBVPatientSelect_PHOTO 10
#define CCPBVPatientSelect_RM_BED 9
#define CCPBVPatientSelect_SIGNAL 11

@interface CCPBVPatientSelect : NSObject < RAREiEventHandler, RAREiChangeListener > {
 @public
  RARETableViewer *patientsTable_;
  id<RAREiWidget> locatorWidget_;
  CCPBVaPatientLocator *patientLocator_;
  CCPBVaBarcodeReader *barcodeReader_;
  IOSClass *barcodeReaderClass_;
  IOSClass *patientLocatorClass_;
  RARERenderableDataItem *patientListLoaded_;
  RARERenderableDataItem *noPatientsFound_;
  RARERenderableDataItem *noPatientListLoaded_;
  RARERenderableDataItem *searchingForPatients_;
  BOOL autoShowDefaultList_;
  CCPBVListPager *pager_;
  int searchPageSize_;
  CCPBVPatientSelect_CardPatientSelectionActionListener *psActionListener_;
  RAREUIPopupMenu *selectionMenu_;
  BOOL alwaysShowSearchFirst_;
  BOOL genderSearchSupported_;
}

+ (int)ADMIT_DATE;
+ (int)ADMIT_DX;
+ (int)DOB;
+ (int)DOCTOR;
+ (int)GENDER;
+ (int)ID;
+ (int)LOCATION;
+ (int)MRN;
+ (int)NAME;
+ (int)PHOTO;
+ (int)SIGNAL;
+ (int)RM_BED;
+ (NSString *)lastPatientID;
+ (void)setLastPatientID:(NSString *)lastPatientID;
+ (NSString *)lastTabName;
+ (void)setLastTabName:(NSString *)lastTabName;
+ (IOSObjectArray *)signalIcons;
+ (void)setSignalIcons:(IOSObjectArray *)signalIcons;
+ (RAREUILineBorder *)photoBorder;
+ (void)setPhotoBorder:(RAREUILineBorder *)photoBorder;
+ (CCPBVPatientSelect_PatientIcon *)noPhotoIcon;
+ (void)setNoPhotoIcon:(CCPBVPatientSelect_PatientIcon *)noPhotoIcon;
+ (NSString *)PATIENT_SELECTION_TYPE;
+ (NSString *)PATIENT_SELECT_PAGE;
+ (NSString *)PATIENT_SELECT;
- (id)init;
- (void)onBarcodeButtonActionWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onBookmarkButtonActionWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onCreatedWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onIdentifierLabelShownWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onListCategoriesActionWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onListsBoxFinishLoadingWithNSString:(NSString *)eventName
                            withRAREiWidget:(id<RAREiWidget>)widget
                    withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onLoadWithNSString:(NSString *)eventName
           withRAREiWidget:(id<RAREiWidget>)widget
   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onMostRecentActionWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onNearbyPatientsActionWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onNextOrPreviousPageWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onPatientListSelectedWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onPatientSearchWithNSString:(NSString *)eventName
                    withRAREiWidget:(id<RAREiWidget>)widget
            withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onPatientsTableChangeWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onPatientsTableCreatedWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSearchFormConfigureWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSearchFormUnloadedWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSelectionFormConfigureWithNSString:(NSString *)eventName
                             withRAREiWidget:(id<RAREiWidget>)widget
                     withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSelectPatientWithNSString:(NSString *)eventName
                    withRAREiWidget:(id<RAREiWidget>)widget
            withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSignalButtonActionWithNSString:(NSString *)eventName
                         withRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSortByNameWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onSortByRoomNumberWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onTabPaneCreatedWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)onVoiceSearchActionWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)showFormForButtonWithNSString:(NSString *)eventName
                      withRAREiWidget:(id<RAREiWidget>)widget
              withJavaUtilEventObject:(JavaUtilEventObject *)event;
- (void)stateChangedWithJavaUtilEventObject:(JavaUtilEventObject *)e;
- (void)clearPreview;
- (void)loadPatientListWithRARETableViewer:(RARETableViewer *)table
                        withRAREActionLink:(RAREActionLink *)link
                              withNSString:(NSString *)filter
                              withNSString:(NSString *)gender;
- (id<JavaUtilList>)loadPatientListExWithRARETableViewer:(RARETableViewer *)widget
                                      withRAREActionLink:(RAREActionLink *)link
                                            withNSString:(NSString *)filter
                                            withNSString:(NSString *)gender
                                             withBoolean:(BOOL)sort;
- (void)populatePreviewWithRARERenderableDataItem:(RARERenderableDataItem *)row;
- (id<JavaUtilList>)processPatientsListWithRARETableViewer:(RARETableViewer *)widget
                                          withJavaUtilList:(id<JavaUtilList>)rows
                                               withBoolean:(BOOL)checkForSignal;
- (void)showNearbyPatientsWithRAREiWidget:(id<RAREiWidget>)widget;
- (void)showPreviewWithRARETableViewer:(RARETableViewer *)widget;
- (id<RAREUTiFilter>)createPatientNameFilterWithNSString:(NSString *)filter;
- (void)updateSignalIconWithRARERenderableDataItem:(RARERenderableDataItem *)row;
- (BOOL)areSamePatientsInSameOrderWithJavaUtilList:(id<JavaUtilList>)list
                               withRARETableViewer:(RARETableViewer *)table;
- (void)patientsTableUpdatedWithRARETableViewer:(RARETableViewer *)table
                             withRAREActionLink:(RAREActionLink *)link;
- (void)stopListeningForNearbyPatients;
+ (void)changePatientWithRAREiWidget:(id<RAREiWidget>)context
                 withCCPBVActionPath:(CCPBVActionPath *)path;
+ (void)clearoutPatientCentricInfo;
+ (BOOL)isShowing;
+ (void)showPatientSelectViewWithRAREiWidget:(id<RAREiWidget>)widget;
+ (void)establishRelationshipWithRAREiWidget:(id<RAREiWidget>)context
                                withNSString:(NSString *)patientID
                                withNSString:(NSString *)relationship
                   withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (id)getLinkedValueWithNSString:(NSString *)name
            withRAREUTJSONObject:(RAREUTJSONObject *)o;
+ (id<RAREiPlatformIcon>)getThimbnailWithRAREWindowViewer:(RAREWindowViewer *)w
                                             withNSString:(NSString *)s;
+ (void)loadPatientExWithRAREiWidget:(id<RAREiWidget>)widget
                        withNSString:(NSString *)id_
           withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (void)processPatientDataWithRAREUTJSONObject:(RAREUTJSONObject *)patient;
+ (void)showRelationshipPopupWithRAREWindowViewer:(RAREWindowViewer *)w
                                     withNSString:(NSString *)patientID
                                     withNSString:(NSString *)patientName
                        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
+ (void)updateWeightHeightBMIWithRAREiPlatformAppContext:(id<RAREiPlatformAppContext>)app
                                    withRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (void)copyAllFieldsTo:(CCPBVPatientSelect *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect, patientsTable_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, locatorWidget_, id<RAREiWidget>)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, patientLocator_, CCPBVaPatientLocator *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, barcodeReader_, CCPBVaBarcodeReader *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, barcodeReaderClass_, IOSClass *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, patientLocatorClass_, IOSClass *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, patientListLoaded_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, noPatientsFound_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, noPatientListLoaded_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, searchingForPatients_, RARERenderableDataItem *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, pager_, CCPBVListPager *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, psActionListener_, CCPBVPatientSelect_CardPatientSelectionActionListener *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect, selectionMenu_, RAREUIPopupMenu *)

typedef CCPBVPatientSelect ComSparsewareBellavistaPatientSelect;

@interface CCPBVPatientSelect_CardPatientSelectionActionListener : NSObject < RAREiActionListener > {
 @public
  CCPBVPatientSelect *this$0_;
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)initWithCCPBVPatientSelect:(CCPBVPatientSelect *)outer$;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_CardPatientSelectionActionListener, this$0_, CCPBVPatientSelect *)

@interface CCPBVPatientSelect_CardPatientSelectionActionListener_$1 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiPlatformIcon> val$icon_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiPlatformIcon:(id<RAREiPlatformIcon>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_CardPatientSelectionActionListener_$1, val$icon_, id<RAREiPlatformIcon>)

@interface CCPBVPatientSelect_MenuActionListener : NSObject < RAREiActionListener > {
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)init;
@end

@interface CCPBVPatientSelect_PatientIcon : RAREaPlatformIcon {
 @public
  id<RAREiPlatformIcon> icon_;
  BOOL imageIcon_;
}

+ (int)iconSize;
- (id)initWithRAREiPlatformIcon:(id<RAREiPlatformIcon>)icon;
- (id<RAREiPlatformIcon>)getDisabledVersion;
- (int)getIconHeight;
- (int)getIconWidth;
- (RAREUIImage *)getImage;
- (void)paintWithRAREiPlatformGraphics:(id<RAREiPlatformGraphics>)g
                             withFloat:(float)x
                             withFloat:(float)y
                             withFloat:(float)width
                             withFloat:(float)height;
- (void)copyAllFieldsTo:(CCPBVPatientSelect_PatientIcon *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_PatientIcon, icon_, id<RAREiPlatformIcon>)

@interface CCPBVPatientSelect_$1 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVPatientSelect *this$0_;
  RAREWindowViewer *val$w_;
  id<RAREiWidget> val$widget_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVPatientSelect:(CCPBVPatientSelect *)outer$
            withRAREWindowViewer:(RAREWindowViewer *)capture$0
                 withRAREiWidget:(id<RAREiWidget>)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$1, this$0_, CCPBVPatientSelect *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$1, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$1, val$widget_, id<RAREiWidget>)

@interface CCPBVPatientSelect_$2 : NSObject < JavaLangRunnable > {
 @public
  CCPBVPatientSelect *this$0_;
  id<RAREiWidget> val$widget_;
}

- (void)run;
- (id)initWithCCPBVPatientSelect:(CCPBVPatientSelect *)outer$
                 withRAREiWidget:(id<RAREiWidget>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$2, this$0_, CCPBVPatientSelect *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$2, val$widget_, id<RAREiWidget>)

@interface CCPBVPatientSelect_$3 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiPlatformIcon> val$icon_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiPlatformIcon:(id<RAREiPlatformIcon>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$3, val$icon_, id<RAREiPlatformIcon>)

@interface CCPBVPatientSelect_$4 : NSObject < JavaUtilComparator > {
}

- (int)compareWithId:(RARERenderableDataItem *)o1
              withId:(RARERenderableDataItem *)o2;
- (BOOL)isEqual:(id)param0;
- (id)init;
@end

@interface CCPBVPatientSelect_$5 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVPatientSelect *this$0_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVPatientSelect:(CCPBVPatientSelect *)outer$;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$5, this$0_, CCPBVPatientSelect *)

@interface CCPBVPatientSelect_$6 : RAREaWorkerTask {
 @public
  CCPBVPatientSelect *this$0_;
  RAREActionLink *val$link_;
  RARETableViewer *val$table_;
  NSString *val$filter_;
  NSString *val$gender_;
  RAREWindowViewer *val$w_;
}

- (void)cancelWithBoolean:(BOOL)canInterrupt;
- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithCCPBVPatientSelect:(CCPBVPatientSelect *)outer$
              withRAREActionLink:(RAREActionLink *)capture$0
             withRARETableViewer:(RARETableViewer *)capture$1
                    withNSString:(NSString *)capture$2
                    withNSString:(NSString *)capture$3
            withRAREWindowViewer:(RAREWindowViewer *)capture$4;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$6, this$0_, CCPBVPatientSelect *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$6, val$link_, RAREActionLink *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$6, val$table_, RARETableViewer *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$6, val$filter_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$6, val$gender_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$6, val$w_, RAREWindowViewer *)

@interface CCPBVPatientSelect_$7 : NSObject < JavaUtilComparator > {
}

- (int)compareWithId:(RARERenderableDataItem *)o1
              withId:(RARERenderableDataItem *)o2;
- (BOOL)isEqual:(id)param0;
- (id)init;
@end

@interface CCPBVPatientSelect_$8 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiWidget> val$context_;
  id<RAREiPlatformAppContext> val$app_;
  RAREWindowViewer *val$w_;
  CCPBVActionPath *val$path_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiWidget:(id<RAREiWidget>)capture$0
withRAREiPlatformAppContext:(id<RAREiPlatformAppContext>)capture$1
     withRAREWindowViewer:(RAREWindowViewer *)capture$2
      withCCPBVActionPath:(CCPBVActionPath *)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$8, val$context_, id<RAREiWidget>)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$8, val$app_, id<RAREiPlatformAppContext>)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$8, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$8, val$path_, CCPBVActionPath *)

@interface CCPBVPatientSelect_$9 : NSObject < RAREiFunctionCallback > {
 @public
  RAREWindowViewer *val$w_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREWindowViewer:(RAREWindowViewer *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$9, val$w_, RAREWindowViewer *)

@interface CCPBVPatientSelect_$10 : RAREaWorkerTask {
 @public
  RAREWindowViewer *val$w_;
  NSString *val$patientID_;
  NSString *val$relationship_;
  id<RAREiFunctionCallback> val$cb_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithRAREWindowViewer:(RAREWindowViewer *)capture$0
                  withNSString:(NSString *)capture$1
                  withNSString:(NSString *)capture$2
     withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$3;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$10, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$10, val$patientID_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$10, val$relationship_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$10, val$cb_, id<RAREiFunctionCallback>)

@interface CCPBVPatientSelect_$11 : RAREaWorkerTask {
 @public
  RAREWindowViewer *val$w_;
  NSString *val$id_;
  id<RAREiFunctionCallback> val$cb_;
}

- (id)compute;
- (void)finishWithId:(id)result;
- (id)initWithRAREWindowViewer:(RAREWindowViewer *)capture$0
                  withNSString:(NSString *)capture$1
     withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$11, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$11, val$id_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$11, val$cb_, id<RAREiFunctionCallback>)

@interface CCPBVPatientSelect_$11_$1 : NSObject < RAREiFunctionCallback > {
 @public
  CCPBVPatientSelect_$11 *this$0_;
  RAREUTJSONObject *val$patient_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithCCPBVPatientSelect_$11:(CCPBVPatientSelect_$11 *)outer$
                withRAREUTJSONObject:(RAREUTJSONObject *)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$11_$1, this$0_, CCPBVPatientSelect_$11 *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$11_$1, val$patient_, RAREUTJSONObject *)

@interface CCPBVPatientSelect_$12 : NSObject < RAREiFunctionCallback > {
 @public
  id<RAREiFunctionCallback> val$cb_;
  RAREWindowViewer *val$w_;
  NSString *val$patientID_;
}

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue;
- (id)initWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0
               withRAREWindowViewer:(RAREWindowViewer *)capture$1
                       withNSString:(NSString *)capture$2;
@end

J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$12, val$cb_, id<RAREiFunctionCallback>)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$12, val$w_, RAREWindowViewer *)
J2OBJC_FIELD_SETTER(CCPBVPatientSelect_$12, val$patientID_, NSString *)

#endif // _CCPBVPatientSelect_H_
