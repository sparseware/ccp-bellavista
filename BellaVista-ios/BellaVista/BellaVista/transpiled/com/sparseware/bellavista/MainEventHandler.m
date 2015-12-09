//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/MainEventHandler.java
//
//  Created by decoteaud on 11/30/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iDataCollection.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/spot/GroupBox.h"
#include "com/appnativa/rare/spot/Tab.h"
#include "com/appnativa/rare/spot/TabPane.h"
#include "com/appnativa/rare/spot/Viewer.h"
#include "com/appnativa/rare/spot/Widget.h"
#include "com/appnativa/rare/ui/ActionBar.h"
#include "com/appnativa/rare/ui/BorderUtils.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIColor.h"
#include "com/appnativa/rare/ui/UIFont.h"
#include "com/appnativa/rare/ui/UIImage.h"
#include "com/appnativa/rare/ui/UIProperties.h"
#include "com/appnativa/rare/ui/UIScreen.h"
#include "com/appnativa/rare/ui/border/UICompoundBorder.h"
#include "com/appnativa/rare/ui/border/UIEmptyBorder.h"
#include "com/appnativa/rare/ui/border/UILineBorder.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/ui/event/DataEvent.h"
#include "com/appnativa/rare/ui/event/EventBase.h"
#include "com/appnativa/rare/ui/event/FlingEvent.h"
#include "com/appnativa/rare/ui/iCollapsible.h"
#include "com/appnativa/rare/ui/iListHandler.h"
#include "com/appnativa/rare/ui/iPlatformBorder.h"
#include "com/appnativa/rare/ui/iPlatformComponent.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/viewer/ImagePaneViewer.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/TableViewer.h"
#include "com/appnativa/rare/viewer/ToolBarViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/aListViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/ComboBoxWidget.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/aWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/SPOTBoolean.h"
#include "com/appnativa/spot/SPOTInteger.h"
#include "com/appnativa/spot/SPOTPrintableString.h"
#include "com/appnativa/spot/SPOTSet.h"
#include "com/appnativa/spot/iSPOTElement.h"
#include "com/appnativa/util/ObjectHolder.h"
#include "com/appnativa/util/iCancelable.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/ActionPath.h"
#include "com/sparseware/bellavista/CollectionManager.h"
#include "com/sparseware/bellavista/MainEventHandler.h"
#include "com/sparseware/bellavista/Settings.h"
#include "com/sparseware/bellavista/Utils.h"
#include "java/beans/PropertyChangeEvent.h"
#include "java/lang/Boolean.h"
#include "java/lang/CharSequence.h"
#include "java/lang/Math.h"
#include "java/net/MalformedURLException.h"
#include "java/util/Arrays.h"
#include "java/util/Collection.h"
#include "java/util/EventObject.h"
#include "java/util/List.h"

@implementation CCPBVMainEventHandler

static id<JavaLangCharSequence> CCPBVMainEventHandler_actionBarTitle_;
static id<RAREiPlatformIcon> CCPBVMainEventHandler_actionBarIcon_;

+ (id<JavaLangCharSequence>)actionBarTitle {
  return CCPBVMainEventHandler_actionBarTitle_;
}

+ (void)setActionBarTitle:(id<JavaLangCharSequence>)actionBarTitle {
  CCPBVMainEventHandler_actionBarTitle_ = actionBarTitle;
}

+ (id<RAREiPlatformIcon>)actionBarIcon {
  return CCPBVMainEventHandler_actionBarIcon_;
}

+ (void)setActionBarIcon:(id<RAREiPlatformIcon>)actionBarIcon {
  CCPBVMainEventHandler_actionBarIcon_ = actionBarIcon;
}

- (id)init {
  return [super init];
}

- (void)onEventWithNSString:(NSString *)eventName
            withRAREiWidget:(id<RAREiWidget>)widget
    withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (void)onLoginFormEnterWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([((NSString *) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getName])) isEqual:@"password"]) {
    [self signInWithNSString:eventName withRAREiWidget:widget withJavaUtilEventObject:event];
  }
  else if ([((NSString *) nil_chk([widget getName])) isEqual:@"username"]) {
    [((id<RAREiWidget>) nil_chk([((id<RAREiFormViewer>) nil_chk([widget getFormViewer])) getWidgetWithNSString:@"password"])) requestFocus];
  }
}

- (void)onResolveComboStringsWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREComboBoxWidget *cb = (RAREComboBoxWidget *) check_class_cast(widget, [RAREComboBoxWidget class]);
  int len = [((RAREComboBoxWidget *) nil_chk(cb)) size];
  for (int i = 0; i < len; i++) {
    RARERenderableDataItem *row = [cb getWithInt:i];
    [row setValueWithId:[cb expandStringWithNSString:[((RARERenderableDataItem *) nil_chk(row)) description]]];
  }
}

- (void)onFlagsListActionWithNSString:(NSString *)eventName
                      withRAREiWidget:(id<RAREiWidget>)widget
              withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREaListViewer *lv = (RAREaListViewer *) check_class_cast(widget, [RAREaListViewer class]);
  NSString *id_ = [((RAREaListViewer *) nil_chk(lv)) getSelectionDataAsString];
  RAREaWidget *tw = (RAREaWidget *) check_class_cast([((id<RAREiFormViewer>) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getFormViewer])) getWidgetWithNSString:@"flagText"], [RAREaWidget class]);
  if ((id_ != nil) && ![id_ isEqual:[((RAREaWidget *) nil_chk(tw)) getLinkedData]]) {
    @try {
      RAREActionLink *l = [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) createActionLinkWithId:[NSString stringWithFormat:@"/hub/main/patient/flag/%@.html", id_]];
      [tw setDataLinkWithRAREActionLink:l];
      [tw setLinkedDataWithId:id_];
    }
    @catch (JavaNetMalformedURLException *e) {
      [CCPBVUtils handleErrorWithJavaLangThrowable:e];
    }
  }
}

- (void)lockInLandscapeModeWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) lockOrientationWithJavaLangBoolean:[JavaLangBoolean getTRUE]];
}

- (void)unlockLandscapeModeWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) unlockOrientation];
}

- (void)onCardStackLoginWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiContainer> fv = (id<RAREiContainer>) check_protocol_cast([((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getViewerWithNSString:@"logonPanel"], @protocol(RAREiContainer));
  NSString *pin = [((id<RAREiWidget>) nil_chk([((id<RAREiContainer>) nil_chk(fv)) getWidgetWithNSString:@"pinValue"])) getValueAsString];
  CCPBVSettings_Server *server = [CCPBVUtils getDefaultServer];
  [CCPBVUtils signInWithRAREiWidget:widget withNSString:pin withNSString:pin withCCPBVSettings_Server:server];
}

- (void)onCreatedFlagsPopupWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARESPOTViewer *cfg = (RARESPOTViewer *) check_class_cast([((RAREDataEvent *) check_class_cast(event, [RAREDataEvent class])) getData], [RARESPOTViewer class]);
  if ([RAREUIScreen isLargeScreen]) {
    RARESPOTWidget *w = [((RARESPOTViewer *) nil_chk(cfg)) findWidgetWithNSString:@"bv.action.fullscreen"];
    [((SPOTBoolean *) nil_chk(((RARESPOTWidget *) nil_chk(w))->visible_)) setValueWithBoolean:NO];
  }
  else {
    RARESPOTWidget *w = [((RARESPOTViewer *) nil_chk(cfg)) findWidgetWithNSString:@"okButton"];
    [((SPOTBoolean *) nil_chk(((RARESPOTWidget *) nil_chk(w))->visible_)) setValueWithBoolean:NO];
  }
}

- (void)onConfigureFlagsPopupWithNSString:(NSString *)eventName
                          withRAREiWidget:(id<RAREiWidget>)widget
                  withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiDataCollection> dc = [((CCPBVCollectionManager *) nil_chk([CCPBVCollectionManager getInstance])) getCollectionWithNSString:@"flags"];
  id<JavaUtilCollection> rows = [((id<RAREiDataCollection>) nil_chk(dc)) getItemDataWithRAREiWidget:widget withBoolean:NO];
  id<RAREiContainer> fv = [((id<RAREiWidget>) nil_chk(widget)) getContainerViewer];
  RAREaListViewer *lv = (RAREaListViewer *) check_class_cast([((id<RAREiContainer>) nil_chk(fv)) getWidgetWithNSString:@"flagsList"], [RAREaListViewer class]);
  [((RAREaListViewer *) nil_chk(lv)) clear];
  for (RARERenderableDataItem * __strong row in nil_chk(rows)) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:0];
    [lv addExWithRARERenderableDataItem:item];
  }
  [lv refreshItems];
  if (![lv isEmpty]) {
    [lv setSelectedIndexWithInt:0];
    [RAREPlatform invokeLaterWithJavaLangRunnable:[[CCPBVMainEventHandler_$1 alloc] initWithRAREaListViewer:lv]];
  }
}

- (void)popWorkspaceViewerWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVUtils popViewerStack];
}

- (void)goBackInStackPaneWithNSString:(NSString *)eventName
                      withRAREiWidget:(id<RAREiWidget>)widget
              withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREStackPaneViewer *sp = [CCPBVUtils getStackPaneViewerWithRAREiWidget:widget];
  if (sp != nil) {
    int pos = [sp getEntryCount] - 1;
    if (pos > -1) {
      [sp switchToWithInt:pos - 1 withRAREiFunctionCallback:[[CCPBVMainEventHandler_$2 alloc] initWithRAREStackPaneViewer:sp withInt:pos]];
    }
  }
}

- (void)moveListItemDownWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event {
  NSString *list = [((RAREActionEvent *) check_class_cast(event, [RAREActionEvent class])) getQueryString];
  id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
  RAREaListViewer *lb = (RAREaListViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:list], [RAREaListViewer class]);
  int n = [((RAREaListViewer *) nil_chk(lb)) getSelectedIndex];
  int len = [lb size];
  if ((n > -1) && (n < len - 2)) {
    [lb clearSelection];
    [lb swapWithInt:n withInt:n + 1];
    [lb setSelectedIndexWithInt:n + 1];
    if (n == len - 1) {
      [widget setEnabledWithBoolean:NO];
    }
    [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"upButton"])) setEnabledWithBoolean:YES];
  }
}

- (void)clearSelectionWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([(id) widget conformsToProtocol: @protocol(RAREiListHandler)]) {
    [((id<RAREiListHandler>) check_protocol_cast(widget, @protocol(RAREiListHandler))) clearSelection];
  }
}

- (void)moveListItemUpWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  NSString *list = [((RAREActionEvent *) check_class_cast(event, [RAREActionEvent class])) getQueryString];
  id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
  RAREaListViewer *lb = (RAREaListViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:list], [RAREaListViewer class]);
  int n = [((RAREaListViewer *) nil_chk(lb)) getSelectedIndex];
  if (n > 0) {
    [lb clearSelection];
    [lb swapWithInt:n withInt:n - 1];
    [lb setSelectedIndexWithInt:n - 1];
    if (n == 1) {
      [widget setEnabledWithBoolean:NO];
    }
    [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"downButton"])) setEnabledWithBoolean:YES];
  }
}

- (void)requestFocusWithNSString:(NSString *)eventName
                 withRAREiWidget:(id<RAREiWidget>)widget
         withJavaUtilEventObject:(JavaUtilEventObject *)event {
  NSString *name = [((RAREEventBase *) check_class_cast(event, [RAREEventBase class])) getQueryString];
  if (name == nil) {
    [((id<RAREiWidget>) nil_chk(widget)) requestFocus];
  }
  else {
    id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
    [((id<RAREiWidget>) nil_chk([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:name])) requestFocus];
  }
}

- (void)requestFocusIfKeyboardPresentWithNSString:(NSString *)eventName
                                  withRAREiWidget:(id<RAREiWidget>)widget
                          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([RAREPlatform hasPhysicalKeyboard]) {
    NSString *name = [((RAREEventBase *) check_class_cast(event, [RAREEventBase class])) getQueryString];
    if (name == nil) {
      [((id<RAREiWidget>) nil_chk(widget)) requestFocus];
    }
    else {
      id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
      [((id<RAREiWidget>) nil_chk([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:name])) requestFocus];
    }
  }
}

- (void)onConfigureMainViewWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [CCPBVUtils updateActionBar];
}

- (void)handleComboBoxMenuBorderWithNSString:(NSString *)eventName
                             withRAREiWidget:(id<RAREiWidget>)widget
                     withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (lineBorder_ == nil) {
    RAREUIColor *c = [((RAREUIProperties *) nil_chk([RAREPlatform getUIDefaults])) getColorWithNSString:@"Rare.ComboBox.borderColor"];
    if (c == nil) {
      c = [RAREaUILineBorder getDefaultLineColor];
    }
    lineBorder_ = [[RAREUILineBorder alloc] initWithRAREUIColor:c];
  }
  id<RAREiPlatformBorder> b = (id<RAREiPlatformBorder>) check_protocol_cast([((id<RAREiWidget>) nil_chk(widget)) getAttributeWithNSString:@"_border"], @protocol(RAREiPlatformBorder));
  if (b == nil) {
    b = [[RAREUICompoundBorder alloc] initWithRAREiPlatformBorder:lineBorder_ withRAREiPlatformBorder:[RAREBorderUtils TWO_POINT_EMPTY_BORDER]];
  }
  [widget setAttributeWithNSString:@"_border" withId:[widget getBorder]];
  [widget setBorderWithRAREiPlatformBorder:b];
}

- (void)sortColumnZeroWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [((RARETableViewer *) check_class_cast(widget, [RARETableViewer class])) sortWithInt:0 withBoolean:NO];
}

- (void)sortColumnOneWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [((RARETableViewer *) check_class_cast(widget, [RARETableViewer class])) sortWithInt:1 withBoolean:NO];
}

- (void)onPageNavigatorCreatedWithNSString:(NSString *)eventName
                           withRAREiWidget:(id<RAREiWidget>)widget
                   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARESPOTGroupBox *cfg = (RARESPOTGroupBox *) check_class_cast([((RAREDataEvent *) check_class_cast(event, [RAREDataEvent class])) getData], [RARESPOTGroupBox class]);
  BOOL touch = [RAREPlatform isTouchDevice];
  for (NSString * __strong s in nil_chk([JavaUtilArrays asListWithNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ @"firstPage", @"nextPage", @"previousPage", @"lastPage" } count:4 type:[IOSClass classWithClass:[NSObject class]]]])) {
    RARESPOTWidget *w = [((RARESPOTGroupBox *) nil_chk(cfg)) findWidgetWithNSString:s withBoolean:NO];
    if (touch) {
      [cfg removeWidgetWithRARESPOTWidget:w];
    }
    else {
      [((SPOTBoolean *) nil_chk(((RARESPOTWidget *) nil_chk(w))->visible_)) setValueWithBoolean:YES];
    }
  }
}

- (void)onRegionFlingWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (void)onTabPaneCreatedWithNSString:(NSString *)eventName
                     withRAREiWidget:(id<RAREiWidget>)widget
             withJavaUtilEventObject:(JavaUtilEventObject *)event {
  CCPBVActionPath *p = [CCPBVUtils getActionPathWithBoolean:NO];
  NSString *tab = (p == nil) ? nil : [p shift];
  int index = -1;
  RAREUTJSONObject *ti = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"tabsInfo"], [RAREUTJSONObject class]);
  RARESPOTTabPane *cfg = (RARESPOTTabPane *) check_class_cast([((RAREDataEvent *) check_class_cast(event, [RAREDataEvent class])) getData], [RARESPOTTabPane class]);
  int len = [((SPOTSet *) nil_chk(((RARESPOTTabPane *) nil_chk(cfg))->tabs_)) size];
  for (int i = 0; i < len; i++) {
    RARESPOTTab *t = (RARESPOTTab *) check_class_cast([cfg->tabs_ getExWithInt:i], [RARESPOTTab class]);
    if ((tab != nil) && [((SPOTPrintableString *) nil_chk(((RARESPOTTab *) nil_chk(t))->name_)) equalsWithNSString:tab]) {
      index = i;
    }
    else if ([((SPOTPrintableString *) nil_chk(((RARESPOTTab *) nil_chk(t))->name_)) equalsWithNSString:@"notes"]) {
      if (![((RAREUTJSONObject *) nil_chk(ti)) optBooleanWithNSString:@"hasNotes" withBoolean:YES]) {
        [((SPOTBoolean *) nil_chk(t->enabled_)) setValueWithBoolean:NO];
      }
    }
    else if ([t->name_ equalsWithNSString:@"procedures"]) {
      if (![((RAREUTJSONObject *) nil_chk(ti)) optBooleanWithNSString:@"hasProcedures" withBoolean:YES]) {
        [((SPOTBoolean *) nil_chk(t->enabled_)) setValueWithBoolean:NO];
      }
    }
  }
  if (index != -1) {
    [((SPOTInteger *) nil_chk(cfg->selectedIndex_)) setValueWithLong:index];
  }
}

- (void)onMakeExpanderWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREToolBarViewer *tb = (RAREToolBarViewer *) check_class_cast([((id<RAREiWidget>) nil_chk(widget)) getParent], [RAREToolBarViewer class]);
  [((RAREToolBarViewer *) nil_chk(tb)) setAsExpanderWithRAREiWidget:widget withBoolean:YES];
}

- (void)onMakeWindowDraggerWithNSString:(NSString *)eventName
                        withRAREiWidget:(id<RAREiWidget>)widget
                withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [((RAREWindowViewer *) check_class_cast([((RAREWindowViewer *) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getWindow])) getViewer], [RAREWindowViewer class])) addWindowDraggerWithRAREiWidget:widget];
}

- (void)onCloseWindowWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [((RAREWindowViewer *) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getWindow])) close];
}

- (void)onInfobarOrientationWillChangeWithNSString:(NSString *)eventName
                                   withRAREiWidget:(id<RAREiWidget>)widget
                           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id config = [((JavaBeansPropertyChangeEvent *) check_class_cast(event, [JavaBeansPropertyChangeEvent class])) getNewValue];
  id<RAREiContainer> fv = [((id<RAREiWidget>) nil_chk(widget)) getContainerViewer];
  [self adjustInfobarWithRAREiContainer:fv withBoolean:[RAREUIScreen isWiderForConfigurationWithId:config]];
}

- (void)adjustInfobarWithRAREiContainer:(id<RAREiContainer>)fv
                            withBoolean:(BOOL)visible {
  [((id<RAREiWidget>) nil_chk([((id<RAREiContainer>) nil_chk(fv)) getWidgetWithNSString:@"name_label"])) setVisibleWithBoolean:visible];
  [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"age_sex_label"])) setVisibleWithBoolean:visible];
  [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"wt_ht_bmi_label"])) setVisibleWithBoolean:visible];
  [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"location_label"])) setVisibleWithBoolean:visible];
  [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"location"])) setVisibleWithBoolean:visible];
  [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"code_status"])) setVisibleWithBoolean:visible];
  [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"code_status_label"])) setVisibleWithBoolean:visible];
  id<RAREiWidget> dr = [fv getWidgetWithNSString:@"provider_label"];
  NSString *s = [RAREPlatform getResourceAsStringWithNSString:visible ? @"bv.text.provider" : @"bv.text.hcp"];
  [((id<RAREiWidget>) nil_chk(dr)) setValueWithId:[NSString stringWithFormat:@"%@:", s]];
  [fv update];
}

- (void)onCollapsiblePaneToggleWithNSString:(NSString *)eventName
                            withRAREiWidget:(id<RAREiWidget>)widget
                    withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiCollapsible> pane = (id<RAREiCollapsible>) check_protocol_cast([RAREPlatform getPlatformComponentWithId:[((JavaUtilEventObject *) nil_chk(event)) getSource]], @protocol(RAREiCollapsible));
  if (pane != nil) {
    [pane setShowTitleWithBoolean:[pane isExpanded]];
  }
}

- (void)onFormHeaderFlungWithNSString:(NSString *)eventName
                      withRAREiWidget:(id<RAREiWidget>)widget
              withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREFlingEvent *fling = (RAREFlingEvent *) check_class_cast(event, [RAREFlingEvent class]);
  float vy = [((RAREFlingEvent *) nil_chk(fling)) getYVelocity];
  float vx = [fling getXVelocity];
  if ([JavaLangMath absWithFloat:vy] > [JavaLangMath absWithFloat:vx]) {
    RAREActionBar *ab = [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getActionBar];
    if (vy < 0) {
      if ([((RAREActionBar *) nil_chk(ab)) isVisible]) {
        [ab setVisibleWithBoolean:NO];
      }
    }
    else {
      if (![((RAREActionBar *) nil_chk(ab)) isVisible]) {
        [ab setVisibleWithBoolean:YES];
      }
    }
  }
}

- (void)onFinishedLoadingSummaryTableWithNSString:(NSString *)eventName
                                  withRAREiWidget:(id<RAREiWidget>)widget
                          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  if ([((RARETableViewer *) nil_chk(table)) isEmpty]) {
    NSString *resource = [((RAREEventBase *) check_class_cast(event, [RAREEventBase class])) getQueryString];
    if (resource == nil || [resource sequenceLength] == 0) {
      resource = @"bv.text.no_data_found";
    }
    RARERenderableDataItem *row = [table createRowWithInt:2 withBoolean:YES];
    [((RARERenderableDataItem *) nil_chk(row)) setEnabledWithBoolean:NO];
    RARERenderableDataItem *item = [row getWithInt:0];
    [((RARERenderableDataItem *) nil_chk(item)) setEnabledWithBoolean:NO];
    [item setColumnSpanWithInt:-1];
    [item setValueWithId:[RAREPlatform getResourceAsStringWithNSString:resource]];
    [item setFontWithRAREUIFont:[((RAREUIFont *) nil_chk([table getFont])) deriveItalic]];
    [table addWithId:row];
  }
  else {
    [table sortWithInt:0 withBoolean:NO];
  }
}

- (void)onFlingInfoBarWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREFlingEvent *fe = (RAREFlingEvent *) check_class_cast(event, [RAREFlingEvent class]);
  float y = [((RAREFlingEvent *) nil_chk(fe)) getYVelocity];
  id<RAREiCollapsible> pane = [((id<RAREiViewer>) nil_chk([((id<RAREiWidget>) nil_chk(widget)) getViewer])) getCollapsiblePane];
  if (pane != nil) {
    if (y < 0) {
      [pane collapsePane];
      [pane setShowTitleWithBoolean:YES];
    }
    else {
      [pane setShowTitleWithBoolean:NO];
      [pane expandPane];
    }
  }
}

- (void)onConfigureInfoBarWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
  RAREImagePaneViewer *ip = (RAREImagePaneViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:@"photo"], [RAREImagePaneViewer class]);
  if (ip != nil) {
    RAREUTJSONObject *patient = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"patient"], [RAREUTJSONObject class]);
    NSString *s;
    [ip setImageWithRAREUIImage:[((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getResourceAsImageWithNSString:@"bv.icon.no_photo"]];
    s = [((RAREUTJSONObject *) nil_chk(patient)) optStringWithNSString:@"photo"];
    if ((s != nil) && ([s sequenceLength] > 0)) {
      @try {
        RAREActionLink *link = [CCPBVUtils createPhotosActionLinkWithNSString:s withBoolean:NO];
        if (link != nil) {
          (void) [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getContentWithRAREiWidget:ip withId:link withId:[RAREActionLink_ReturnDataTypeEnum IMAGE] withRAREiFunctionCallback:[[CCPBVMainEventHandler_$3 alloc] initWithRAREImagePaneViewer:ip]];
        }
      }
      @catch (JavaNetMalformedURLException *e) {
        [((JavaNetMalformedURLException *) nil_chk(e)) printStackTrace];
      }
    }
    if (![RAREUIScreen isWider]) {
      [self adjustInfobarWithRAREiContainer:fv withBoolean:NO];
    }
  }
  [((CCPBVCollectionManager *) nil_chk([CCPBVCollectionManager getInstance])) updateUI];
  RAREPushButtonWidget *pb = (RAREPushButtonWidget *) check_class_cast([fv getWidgetWithNSString:@"bv.action.flags"], [RAREPushButtonWidget class]);
  if ((pb != nil) && [pb isEnabled]) {
    RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"patientSelectInfo"], [RAREUTJSONObject class]);
    if ([((RAREUTJSONObject *) nil_chk(info)) optBooleanWithNSString:@"autoShowFlags" withBoolean:NO]) {
      [pb click];
    }
  }
}

- (void)signInWithNSString:(NSString *)eventName
           withRAREiWidget:(id<RAREiWidget>)widget
   withJavaUtilEventObject:(JavaUtilEventObject *)event {
  id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
  NSString *password = [((id<RAREiWidget>) nil_chk([((id<RAREiFormViewer>) nil_chk(fv)) getWidgetWithNSString:@"password"])) getValueAsString];
  if ([CCPBVUtils isApplicationLocked]) {
    [CCPBVUtils resignInWithRAREiWidget:widget withNSString:password];
  }
  else {
    NSString *username = [((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"username"])) getValueAsString];
    CCPBVSettings_Server *server = (CCPBVSettings_Server *) check_class_cast([((id<RAREiWidget>) nil_chk([fv getWidgetWithNSString:@"server"])) getSelectionData], [CCPBVSettings_Server class]);
    [CCPBVUtils signInWithRAREiWidget:widget withNSString:username withNSString:password withCCPBVSettings_Server:server];
  }
}

- (void)copyAllFieldsTo:(CCPBVMainEventHandler *)other {
  [super copyAllFieldsTo:other];
  other->lineBorder_ = lineBorder_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "adjustInfobarWithRAREiContainer:withBoolean:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "lineBorder_", NULL, 0x0, "LRAREUILineBorder" },
    { "actionBarTitle_", NULL, 0x8, "LJavaLangCharSequence" },
    { "actionBarIcon_", NULL, 0x8, "LRAREiPlatformIcon" },
  };
  static J2ObjcClassInfo _CCPBVMainEventHandler = { "MainEventHandler", "com.sparseware.bellavista", NULL, 0x1, 1, methods, 3, fields, 0, NULL};
  return &_CCPBVMainEventHandler;
}

@end
@implementation CCPBVMainEventHandler_$1

- (void)run {
  [((RAREaListViewer *) nil_chk(val$lv_)) fireActionForSelected];
}

- (id)initWithRAREaListViewer:(RAREaListViewer *)capture$0 {
  val$lv_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$lv_", NULL, 0x1012, "LRAREaListViewer" },
  };
  static J2ObjcClassInfo _CCPBVMainEventHandler_$1 = { "$1", "com.sparseware.bellavista", "MainEventHandler", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVMainEventHandler_$1;
}

@end
@implementation CCPBVMainEventHandler_$2

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  if (![((RAREStackPaneViewer *) nil_chk(val$sp_)) isDisposed] && ([val$sp_ getEntryCount] == val$pos_ + 1)) {
    id<RAREiViewer> v = [val$sp_ removeViewerWithInt:val$pos_];
    if (v != nil) {
      [v dispose];
    }
  }
}

- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0
                          withInt:(int)capture$1 {
  val$sp_ = capture$0;
  val$pos_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
    { "val$pos_", NULL, 0x1012, "I" },
  };
  static J2ObjcClassInfo _CCPBVMainEventHandler_$2 = { "$2", "com.sparseware.bellavista", "MainEventHandler", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVMainEventHandler_$2;
}

@end
@implementation CCPBVMainEventHandler_$3

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  if (([returnValue isKindOfClass:[RAREUTObjectHolder class]]) && ![((RAREImagePaneViewer *) nil_chk(val$ip_)) isDisposed]) {
    RAREUTObjectHolder *oh = (RAREUTObjectHolder *) check_class_cast(returnValue, [RAREUTObjectHolder class]);
    if ([((RAREUTObjectHolder *) nil_chk(oh))->value_ isKindOfClass:[RAREUIImage class]]) {
      [val$ip_ setImageWithRAREUIImage:(RAREUIImage *) check_class_cast(oh->value_, [RAREUIImage class])];
    }
  }
}

- (id)initWithRAREImagePaneViewer:(RAREImagePaneViewer *)capture$0 {
  val$ip_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$ip_", NULL, 0x1012, "LRAREImagePaneViewer" },
  };
  static J2ObjcClassInfo _CCPBVMainEventHandler_$3 = { "$3", "com.sparseware.bellavista", "MainEventHandler", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVMainEventHandler_$3;
}

@end
