//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aRemoteMonitor.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVaRemoteMonitor_H_
#define _CCPBVaRemoteMonitor_H_

@class RAREActionEvent;
@class RARELabelWidget;
@class RARESPOTViewer;
@class RAREStackPaneViewer;
@class RAREUIDimension;
@class RAREUTJSONObject;
@protocol CCPBVaRemoteMonitor_iRemoteMonitorEventHandler;
@protocol JavaUtilList;
@protocol RAREiContainer;
@protocol RAREiFunctionCallback;
@protocol RAREiViewer;

#import "JreEmulation.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"

@interface CCPBVaRemoteMonitor : NSObject {
 @public
  NSString *toggleButtonName_;
  NSString *eventHandlerClassName_;
  NSString *patientPropertyName_;
  id<CCPBVaRemoteMonitor_iRemoteMonitorEventHandler> eventHandler_;
}

- (id)init;
- (BOOL)canMonitorPatientWithRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (void)createNumericsViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                              withRAREiContainer:(id<RAREiContainer>)parent
                       withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (void)createViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                      withRAREiContainer:(id<RAREiContainer>)parent
                     withRAREUIDimension:(RAREUIDimension *)targetSize
               withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (void)createWaveformsViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                               withRAREiContainer:(id<RAREiContainer>)parent
                        withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (void)dispose;
- (NSString *)getPatientPropertyName;
- (void)pauseMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (void)restartMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (void)startMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (void)stopMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (BOOL)supportsNumerics;
- (BOOL)supportsWaveforms;
- (void)createViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                      withRAREiContainer:(id<RAREiContainer>)parent
                             withBoolean:(BOOL)numerics
               withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (id<RAREiContainer>)finishCreatingViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                                            withRAREiContainer:(id<RAREiContainer>)parent
                                            withRARESPOTViewer:(RARESPOTViewer *)wcfg
                                            withRARESPOTViewer:(RARESPOTViewer *)ncfg
                                                   withBoolean:(BOOL)stacked;
- (id<JavaUtilList>)getToolbarButtonsWithRAREiViewer:(id<RAREiViewer>)viewer;
- (void)mainViewerCratedWithRAREiContainer:(id<RAREiContainer>)v
                      withRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (void)stackPaneViewerCratedWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp
                                withRAREUTJSONObject:(RAREUTJSONObject *)patient;
- (void)viewerCratedWithRAREiContainer:(id<RAREiContainer>)v
                           withBoolean:(BOOL)numerics;
- (id<RAREiContainer>)createGridPaneViewerWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp;
- (void)copyAllFieldsTo:(CCPBVaRemoteMonitor *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor, toggleButtonName_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor, eventHandlerClassName_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor, patientPropertyName_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor, eventHandler_, id<CCPBVaRemoteMonitor_iRemoteMonitorEventHandler>)

typedef CCPBVaRemoteMonitor ComSparsewareBellavistaExternalARemoteMonitor;

@protocol CCPBVaRemoteMonitor_iRemoteMonitorEventHandler < NSObject, JavaObject >
- (void)setMonitorWithCCPBVaRemoteMonitor:(CCPBVaRemoteMonitor *)monitor;
@end

@interface CCPBVaRemoteMonitor_$1 : NSObject < RAREiActionListener > {
 @public
  RAREStackPaneViewer *val$sp_;
  RARELabelWidget *val$l_;
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0
              withRARELabelWidget:(RARELabelWidget *)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor_$1, val$sp_, RAREStackPaneViewer *)
J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor_$1, val$l_, RARELabelWidget *)

@interface CCPBVaRemoteMonitor_$2 : NSObject < RAREiActionListener > {
 @public
  RAREStackPaneViewer *val$sp_;
  RARELabelWidget *val$l_;
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0
              withRARELabelWidget:(RARELabelWidget *)capture$1;
@end

J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor_$2, val$sp_, RAREStackPaneViewer *)
J2OBJC_FIELD_SETTER(CCPBVaRemoteMonitor_$2, val$l_, RARELabelWidget *)

#endif // _CCPBVaRemoteMonitor_H_
