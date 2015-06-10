//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/CardStackUtils.java
//
//  Created by decoteaud on 6/10/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/rare/spot/Form.h"
#include "com/appnativa/rare/spot/GridPane.h"
#include "com/appnativa/rare/spot/Label.h"
#include "com/appnativa/rare/spot/StackPane.h"
#include "com/appnativa/rare/spot/WidgetPane.h"
#include "com/appnativa/rare/ui/RenderType.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIColor.h"
#include "com/appnativa/rare/ui/ViewerCreator.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/ui/iPlatformWindowManager.h"
#include "com/appnativa/rare/ui/painter/iPlatformComponentPainter.h"
#include "com/appnativa/rare/viewer/FormViewer.h"
#include "com/appnativa/rare/viewer/GridPaneViewer.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/TabPaneViewer.h"
#include "com/appnativa/rare/viewer/WidgetPaneViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/LabelWidget.h"
#include "com/appnativa/rare/widget/aWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/SPOTAny.h"
#include "com/appnativa/spot/SPOTPrintableString.h"
#include "com/appnativa/spot/iSPOTElement.h"
#include "com/appnativa/util/StringCache.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/CardStackCollectionActionListener.h"
#include "com/sparseware/bellavista/CardStackUtils.h"
#include "com/sparseware/bellavista/Utils.h"
#include "java/lang/Boolean.h"
#include "java/lang/Exception.h"
#include "java/lang/Integer.h"
#include "java/lang/Long.h"
#include "java/lang/Runnable.h"
#include "java/lang/StringBuilder.h"
#include "java/util/Collection.h"
#include "java/util/Iterator.h"
#include "java/util/List.h"

@implementation CCPBVCardStackUtils

static NSString * CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_ = @"_BV_VIEWER_ACTION";
static NSString * CCPBVCardStackUtils_VIEWER_BUNDLE_URL_PROPERTY_ = @"_BV_VIEWER_BUNDLE_URL";
static NSString * CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ = @"_BV_VIEWER_IS_BUNDLE";
static NSString * CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_ = @"_BV_VIEWER_SUBTITLE";
static NSString * CCPBVCardStackUtils_ITEM_TEXT_PROPERTY_ = @"_BV_ITEM_TEXT_PROPERTY";
static NSString * CCPBVCardStackUtils_ITEM_LIST_TEXT_PROPERTY_ = @"_BV_ITEM_LIST_TEXT_PROPERTY";
static BOOL CCPBVCardStackUtils_wearable_;
static BOOL CCPBVCardStackUtils_voiceActionsSupported_ = YES;
static RARESPOTForm * CCPBVCardStackUtils_listItemPageConfig_;
static id<RAREiActionListener> CCPBVCardStackUtils_defaultActionListener_;

+ (NSString *)VIEWER_ACTION_PROPERTY {
  return CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_;
}

+ (void)setVIEWER_ACTION_PROPERTY:(NSString *)VIEWER_ACTION_PROPERTY {
  CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_ = VIEWER_ACTION_PROPERTY;
}

+ (NSString *)VIEWER_BUNDLE_URL_PROPERTY {
  return CCPBVCardStackUtils_VIEWER_BUNDLE_URL_PROPERTY_;
}

+ (void)setVIEWER_BUNDLE_URL_PROPERTY:(NSString *)VIEWER_BUNDLE_URL_PROPERTY {
  CCPBVCardStackUtils_VIEWER_BUNDLE_URL_PROPERTY_ = VIEWER_BUNDLE_URL_PROPERTY;
}

+ (NSString *)VIEWER_IS_BUNDLE_PROPERTY {
  return CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_;
}

+ (void)setVIEWER_IS_BUNDLE_PROPERTY:(NSString *)VIEWER_IS_BUNDLE_PROPERTY {
  CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ = VIEWER_IS_BUNDLE_PROPERTY;
}

+ (NSString *)VIEWER_SUBTITLE_PROPERTY {
  return CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_;
}

+ (void)setVIEWER_SUBTITLE_PROPERTY:(NSString *)VIEWER_SUBTITLE_PROPERTY {
  CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_ = VIEWER_SUBTITLE_PROPERTY;
}

+ (NSString *)ITEM_TEXT_PROPERTY {
  return CCPBVCardStackUtils_ITEM_TEXT_PROPERTY_;
}

+ (void)setITEM_TEXT_PROPERTY:(NSString *)ITEM_TEXT_PROPERTY {
  CCPBVCardStackUtils_ITEM_TEXT_PROPERTY_ = ITEM_TEXT_PROPERTY;
}

+ (NSString *)ITEM_LIST_TEXT_PROPERTY {
  return CCPBVCardStackUtils_ITEM_LIST_TEXT_PROPERTY_;
}

+ (void)setITEM_LIST_TEXT_PROPERTY:(NSString *)ITEM_LIST_TEXT_PROPERTY {
  CCPBVCardStackUtils_ITEM_LIST_TEXT_PROPERTY_ = ITEM_LIST_TEXT_PROPERTY;
}

+ (BOOL)wearable {
  return CCPBVCardStackUtils_wearable_;
}

+ (BOOL *)wearableRef {
  return &CCPBVCardStackUtils_wearable_;
}

+ (BOOL)voiceActionsSupported {
  return CCPBVCardStackUtils_voiceActionsSupported_;
}

+ (BOOL *)voiceActionsSupportedRef {
  return &CCPBVCardStackUtils_voiceActionsSupported_;
}

+ (RARESPOTForm *)listItemPageConfig {
  return CCPBVCardStackUtils_listItemPageConfig_;
}

+ (void)setListItemPageConfig:(RARESPOTForm *)listItemPageConfig {
  CCPBVCardStackUtils_listItemPageConfig_ = listItemPageConfig;
}

+ (id<RAREiActionListener>)defaultActionListener {
  return CCPBVCardStackUtils_defaultActionListener_;
}

+ (void)setDefaultActionListener:(id<RAREiActionListener>)defaultActionListener {
  CCPBVCardStackUtils_defaultActionListener_ = defaultActionListener;
}

- (id)init {
  return [super init];
}

+ (void)clearTitle {
  id<RAREiContainer> titleWidget = [CCPBVUtils titleWidget];
  RARELabelWidget *l = (RARELabelWidget *) check_class_cast([((id<RAREiContainer>) nil_chk(titleWidget)) getWidgetWithNSString:@"bundleIcon"], [RARELabelWidget class]);
  [((RARELabelWidget *) nil_chk(l)) setIconWithRAREiPlatformIcon:nil];
  l = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"title"], [RARELabelWidget class]);
  [((RARELabelWidget *) nil_chk(l)) setTextWithJavaLangCharSequence:@""];
  l = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"subtitleLeft"], [RARELabelWidget class]);
  [((RARELabelWidget *) nil_chk(l)) setTextWithJavaLangCharSequence:@""];
  l = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"subtitleRight"], [RARELabelWidget class]);
  [((RARELabelWidget *) nil_chk(l)) setTextWithJavaLangCharSequence:@""];
  [titleWidget update];
}

+ (RAREWidgetPaneViewer *)createActionCardWithRAREiContainer:(id<RAREiContainer>)parent
                                                      withId:(id)action {
  return [CCPBVCardStackUtils createCardWithRAREiContainer:parent withId:action withBoolean:NO];
}

+ (void)switchToViewerWithRAREiViewer:(id<RAREiViewer>)viewer {
  RAREStackPaneViewer *sp = [CCPBVUtils getStackPaneViewerWithRAREiWidget:viewer];
  if (sp != nil) {
    int n = [sp indexOfWithId:viewer];
    if (n != -1) {
      [sp switchToWithInt:n];
      return;
    }
  }
  RARETabPaneViewer *tp = (RARETabPaneViewer *) check_class_cast([((id<RAREiPlatformWindowManager>) nil_chk([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getWindowManager])) getWorkspaceViewer], [RARETabPaneViewer class]);
  int n = [((RARETabPaneViewer *) nil_chk(tp)) indexOfWithId:viewer];
  if (n != -1) {
    if ([tp getSelectedTab] != n) {
      [tp setSelectedTabWithInt:n];
    }
  }
}

+ (RAREWidgetPaneViewer *)createBundleCardWithRAREiContainer:(id<RAREiContainer>)parent
                                                withNSString:(NSString *)bundleURL {
  return [CCPBVCardStackUtils createCardWithRAREiContainer:parent withId:bundleURL withBoolean:YES];
}

+ (id<RAREiViewer>)createItemsViewerWithNSString:(NSString *)title
                          withJavaUtilCollection:(id<JavaUtilCollection>)list
                                         withInt:(int)column {
  RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"cardStackInfo"], [RAREUTJSONObject class]);
  int itemsPerPage = info == nil ? 5 : [info optIntWithNSString:@"listPagingThreshold" withInt:5];
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  int len = [((id<JavaUtilCollection>) nil_chk(list)) size];
  RAREFormViewer *fv = (RAREFormViewer *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createWidgetWithRARESPOTWidget:CCPBVCardStackUtils_listItemPageConfig_], [RAREFormViewer class]);
  int n = 0;
  int i = 0;
  if (len > itemsPerPage) {
    itemsPerPage--;
  }
  id<JavaUtilIterator> it = [list iterator];
  while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
    RARERenderableDataItem *item = [it next];
    if (column != -1) {
      item = [((RARERenderableDataItem *) nil_chk(item)) getWithInt:column];
    }
    n++;
    RARELabelWidget *l = (RARELabelWidget *) check_class_cast([((RAREFormViewer *) nil_chk(fv)) getWidgetWithInt:n++], [RARELabelWidget class]);
    [((RARELabelWidget *) nil_chk(l)) setIconWithRAREiPlatformIcon:[((RARERenderableDataItem *) nil_chk(item)) getIcon]];
    [l setValueWithId:[CCPBVCardStackUtils getItemTextWithRARERenderableDataItem:item withBoolean:YES]];
    i++;
    if (i == itemsPerPage) {
      break;
    }
  }
  [((RAREFormViewer *) nil_chk(fv)) setTitleWithNSString:title];
  if (len <= itemsPerPage) {
    return fv;
  }
  else {
    RARESPOTGridPane *cfg = (RARESPOTGridPane *) check_class_cast([w createConfigurationObjectWithNSString:@"GridPane" withNSString:@"bv.gridpane.items"], [RARESPOTGridPane class]);
    RAREGridPaneViewer *gp = (RAREGridPaneViewer *) check_class_cast([w createViewerWithRAREiWidget:w withRARESPOTWidget:cfg], [RAREGridPaneViewer class]);
    [CCPBVCardStackUtils setViewerActionWithRAREiViewer:gp withId:[[CCPBVCardStackCollectionActionListener alloc] initWithNSString:title withJavaUtilCollection:list withInt:column] withBoolean:YES];
    [((RAREGridPaneViewer *) nil_chk(gp)) setTitleWithNSString:title];
    NSString *s = [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getStringWithNSString:@"bv.format.tap_to_see_more" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ [RAREUTStringCache valueOfWithInt:i], [RAREUTStringCache valueOfWithInt:len] } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
    id<RAREiWidget> tapLabel = [gp getViewerWithInt:1];
    [((id<RAREiWidget>) nil_chk(tapLabel)) setValueWithId:s];
    (void) [gp setViewerWithInt:0 withRAREiViewer:fv];
    return gp;
  }
}

+ (RAREStackPaneViewer *)createListItemsOrPageViewerWithNSString:(NSString *)title
                                              withRAREiContainer:(id<RAREiContainer>)parent
                                                withJavaUtilList:(id<JavaUtilList>)list
                                                         withInt:(int)itemsPerPage
                                                         withInt:(int)column
                                         withRAREiActionListener:(id<RAREiActionListener>)action
                                                     withBoolean:(BOOL)bundle
                                                     withBoolean:(BOOL)stretched {
  RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"cardStackInfo"], [RAREUTJSONObject class]);
  int listPagingThreshold = info == nil ? 5 : [info optIntWithNSString:@"listPagingThreshold" withInt:5];
  if (itemsPerPage == -1) {
    itemsPerPage = info == nil ? 5 : [info optIntWithNSString:@"itemsPerPage" withInt:5];
  }
  if ([((id<JavaUtilList>) nil_chk(list)) size] > listPagingThreshold) {
    id<RAREiActionListener> l = [[CCPBVCardStackUtils_$2 alloc] initWithRAREiActionListener:action withRAREiContainer:parent withBoolean:bundle withBoolean:stretched];
    return [CCPBVCardStackUtils createListPagesViewerWithNSString:title withRAREiContainer:parent withJavaUtilList:list withInt:itemsPerPage withInt:column withRAREiActionListener:l];
  }
  else {
    RAREStackPaneViewer *sp = [CCPBVCardStackUtils createListItemsViewerWithRAREStackPaneViewer:nil withNSString:title withRAREiContainer:parent withJavaUtilList:list withRAREiActionListener:action withBoolean:bundle withBoolean:stretched];
    [((RAREStackPaneViewer *) nil_chk(sp)) switchToWithInt:0];
    return sp;
  }
}

+ (RAREStackPaneViewer *)createListItemsViewerWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp
                                                         withNSString:(NSString *)title
                                                   withRAREiContainer:(id<RAREiContainer>)parent
                                                     withJavaUtilList:(id<JavaUtilList>)list
                                              withRAREiActionListener:(id<RAREiActionListener>)action
                                                          withBoolean:(BOOL)bundle
                                                          withBoolean:(BOOL)stretched {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  if (sp == nil) {
    RARESPOTStackPane *cfg = (RARESPOTStackPane *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"StackPane" withNSString:@"bv.stackpane.card"], [RARESPOTStackPane class]);
    if (title != nil) {
      [((SPOTPrintableString *) nil_chk(((RARESPOTStackPane *) nil_chk(cfg))->title_)) setValueWithNSString:title];
    }
    sp = (RAREStackPaneViewer *) check_class_cast([w createViewerWithRAREiWidget:parent withRARESPOTWidget:cfg], [RAREStackPaneViewer class]);
  }
  if (action == nil) {
    action = CCPBVCardStackUtils_defaultActionListener_;
  }
  int card = 1;
  int len = [((id<JavaUtilList>) nil_chk(list)) size];
  RAREWidgetPaneViewer *wp;
  for (int i = 0; i < len; i++) {
    RARERenderableDataItem *item = [list getWithInt:i];
    [((RAREStackPaneViewer *) nil_chk(sp)) addViewerWithNSString:nil withRAREiViewer:wp = [CCPBVCardStackUtils createCardWithRAREiContainer:sp withId:action withBoolean:NO]];
    [((RAREWidgetPaneViewer *) nil_chk(wp)) setLinkedDataWithId:item];
    [wp setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_ withId:[((RAREWindowViewer *) nil_chk(w)) getStringWithNSString:@"bv.format.card_of" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ [JavaLangInteger valueOfWithInt:card++], [JavaLangInteger valueOfWithInt:len] } count:2 type:[IOSClass classWithClass:[NSObject class]]]]];
    [wp setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ withId:[JavaLangBoolean valueOfWithBoolean:bundle]];
    RARELabelWidget *l = (RARELabelWidget *) check_class_cast([wp getWidget], [RARELabelWidget class]);
    if (stretched) {
      [wp setWidgetRenderTypeWithRARERenderTypeEnum:[RARERenderTypeEnum STRETCHED]];
      [((RARELabelWidget *) nil_chk(l)) setHorizontalAlignmentWithRARERenderableDataItem_HorizontalAlignEnum:[RARERenderableDataItem_HorizontalAlignEnum LEFT]];
      [l setVerticalAlignmentWithRARERenderableDataItem_VerticalAlignEnum:[RARERenderableDataItem_VerticalAlignEnum TOP]];
    }
    NSString *s = [CCPBVCardStackUtils getItemTextWithRARERenderableDataItem:item withBoolean:NO];
    [((RARELabelWidget *) nil_chk(l)) setValueWithId:s];
    [l setIconWithRAREiPlatformIcon:[((RARERenderableDataItem *) nil_chk(item)) getIcon]];
    RAREUIColor *fg = [item getForeground];
    id<RAREiPlatformComponentPainter> cp = [item getComponentPainter];
    if (fg != nil) {
      [l setForegroundWithRAREUIColor:fg];
    }
    if (cp != nil) {
      [l setComponentPainterWithRAREiPlatformComponentPainter:cp];
    }
  }
  return sp;
}

+ (RAREStackPaneViewer *)createListPagesViewerWithNSString:(NSString *)title
                                        withRAREiContainer:(id<RAREiContainer>)parent
                                          withJavaUtilList:(id<JavaUtilList>)list
                                                   withInt:(int)itemsPerPage
                                                   withInt:(int)column {
  RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"cardStackInfo"], [RAREUTJSONObject class]);
  if (itemsPerPage == -1) {
    itemsPerPage = info == nil ? 5 : [info optIntWithNSString:@"itemsPerPage" withInt:5];
  }
  return [CCPBVCardStackUtils createListPagesViewerWithNSString:title withRAREiContainer:parent withJavaUtilList:list withInt:itemsPerPage withInt:column withRAREiActionListener:nil];
}

+ (RAREStackPaneViewer *)createStackPaneViewer {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RARESPOTStackPane *cfg = (RARESPOTStackPane *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"StackPane" withNSString:@"bv.stackpane.card"], [RARESPOTStackPane class]);
  return (RAREStackPaneViewer *) check_class_cast([w createViewerWithRAREiWidget:w withRARESPOTWidget:cfg], [RAREStackPaneViewer class]);
}

+ (RAREWidgetPaneViewer *)createTextCardWithRAREiContainer:(id<RAREiContainer>)parent
                                              withNSString:(NSString *)text
                                                    withId:(id)action {
  RAREWidgetPaneViewer *wp = [CCPBVCardStackUtils createActionCardWithRAREiContainer:parent withId:action];
  RARELabelWidget *l = (RARELabelWidget *) check_class_cast([((RAREWidgetPaneViewer *) nil_chk(wp)) getWidget], [RARELabelWidget class]);
  [((RARELabelWidget *) nil_chk(l)) setValueWithId:text];
  return wp;
}

+ (void)executeActionWithRAREiWidget:(id<RAREiWidget>)widget
                              withId:(id)source
                              withId:(id)action {
  if ([action conformsToProtocol: @protocol(RAREiActionListener)]) {
    [((id<RAREiActionListener>) check_protocol_cast(action, @protocol(RAREiActionListener))) actionPerformedWithRAREActionEvent:[[RAREActionEvent alloc] initWithId:source]];
  }
  else if ([action conformsToProtocol: @protocol(JavaLangRunnable)]) {
    [((id<JavaLangRunnable>) check_protocol_cast(action, @protocol(JavaLangRunnable))) run];
  }
  else if ([action isKindOfClass:[NSString class]]) {
    (void) [((RAREaWidget *) check_class_cast(widget, [RAREaWidget class])) evaluateCodeWithId:action];
  }
}

+ (NSString *)generateRandomNumberStringWithInt:(int)digits {
  long long int max = 10;
  long long int min = 1;
  while (digits > 1) {
    max *= 10;
    min *= 10;
    digits--;
  }
  long long int rand = [RAREFunctions randomLongWithLong:max];
  while (rand < min) {
    long long int nrand = [RAREFunctions randomLongWithLong:max];
    if (nrand > min) {
      rand = nrand;
    }
    else {
      rand += nrand;
    }
  }
  rand = rand % max;
  while (rand < min) {
    rand *= 10;
  }
  return [JavaLangLong toStringWithLong:rand];
}

+ (NSString *)getItemTextWithRARERenderableDataItem:(RARERenderableDataItem *)item
                                        withBoolean:(BOOL)forList {
  NSString *s = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getCustomPropertyWithId:forList ? CCPBVCardStackUtils_ITEM_LIST_TEXT_PROPERTY_ : CCPBVCardStackUtils_ITEM_TEXT_PROPERTY_], [NSString class]);
  return s == nil ? [item description] : s;
}

+ (id)getViewerActionWithRAREiViewer:(id<RAREiViewer>)viewer {
  return [((id<RAREiViewer>) nil_chk(viewer)) getAttributeWithNSString:CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_];
}

+ (NSString *)getViewerBundleURLWithRAREiViewer:(id<RAREiViewer>)viewer {
  return (NSString *) check_class_cast([((id<RAREiViewer>) nil_chk(viewer)) getAttributeWithNSString:CCPBVCardStackUtils_VIEWER_BUNDLE_URL_PROPERTY_], [NSString class]);
}

+ (BOOL)isBundleWithRAREiViewer:(id<RAREiViewer>)viewer {
  return [((id<RAREiViewer>) nil_chk(viewer)) getAttributeWithNSString:CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_] == [JavaLangBoolean getTRUE];
}

+ (BOOL)selectItemOnPageWithRAREiViewer:(id<RAREiViewer>)page
                                withInt:(int)itemNumber {
  id action = [((id<RAREiViewer>) nil_chk(page)) getAttributeWithNSString:CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_];
  id o = [page getLinkedData];
  if ([o conformsToProtocol: @protocol(JavaUtilList)]) {
    id<JavaUtilList> items = (id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList));
    if ([((id<JavaUtilList>) nil_chk(items)) size] < itemNumber) {
      o = [items getWithInt:itemNumber];
    }
  }
  if ([o isKindOfClass:[RARERenderableDataItem class]]) {
    [CCPBVCardStackUtils executeActionWithRAREiWidget:page withId:o withId:action];
    return YES;
  }
  return NO;
}

+ (void)setItemTextWithRARERenderableDataItem:(RARERenderableDataItem *)item
                                 withNSString:(NSString *)text {
  (void) [((RARERenderableDataItem *) nil_chk(item)) setCustomPropertyWithId:CCPBVCardStackUtils_ITEM_TEXT_PROPERTY_ withId:text];
}

+ (void)setListItemTextWithRARERenderableDataItem:(RARERenderableDataItem *)item
                                     withNSString:(NSString *)text {
  (void) [((RARERenderableDataItem *) nil_chk(item)) setCustomPropertyWithId:CCPBVCardStackUtils_ITEM_LIST_TEXT_PROPERTY_ withId:text];
}

+ (void)setupEnvironmentWithRAREWindowViewer:(RAREWindowViewer *)w {
  @try {
    (void) [RAREViewerCreator createConfigurationWithRAREiWidget:w withRAREActionLink:[[RAREActionLink alloc] initWithNSString:@"page_of_items.rml"] withRAREiFunctionCallback:[[CCPBVCardStackUtils_$3 alloc] init]];
  }
  @catch (JavaLangException *e) {
    [CCPBVUtils handleErrorWithJavaLangThrowable:e];
  }
}

+ (void)setViewerActionWithRAREiViewer:(id<RAREiViewer>)viewer
                                withId:(id)action
                           withBoolean:(BOOL)bundle {
  [((id<RAREiViewer>) nil_chk(viewer)) setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_ withId:action];
  [viewer setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ withId:bundle ? [JavaLangBoolean getTRUE] : [JavaLangBoolean getFALSE]];
}

+ (void)setViewerBundleURLWithRAREiViewer:(id<RAREiViewer>)viewer
                             withNSString:(NSString *)url {
  [((id<RAREiViewer>) nil_chk(viewer)) setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_BUNDLE_URL_PROPERTY_ withId:url];
  [viewer setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ withId:[JavaLangBoolean getTRUE]];
}

+ (void)setViewerSubTitleWithRAREiViewer:(id<RAREiViewer>)viewer
                            withNSString:(NSString *)subtitle {
  [((id<RAREiViewer>) nil_chk(viewer)) setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_ withId:subtitle];
}

+ (void)setViewerTitleWithRAREiViewer:(id<RAREiViewer>)viewer
                         withNSString:(NSString *)title
                         withNSString:(NSString *)subtitle {
  [((id<RAREiViewer>) nil_chk(viewer)) setTitleWithNSString:title];
  [viewer setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_ withId:subtitle];
}

+ (void)updateSubTitleWithNSString:(NSString *)subTitle
                       withBoolean:(BOOL)bundle {
  id<RAREiContainer> titleWidget = [CCPBVUtils titleWidget];
  RARELabelWidget *bundleIconLabel = (RARELabelWidget *) check_class_cast([((id<RAREiContainer>) nil_chk(titleWidget)) getWidgetWithNSString:@"bundleIcon"], [RARELabelWidget class]);
  RARELabelWidget *bundleTextLabel = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"subtitleRight"], [RARELabelWidget class]);
  if (subTitle != nil && [subTitle sequenceLength] > 0) {
    [((RARELabelWidget *) nil_chk(bundleTextLabel)) setTextWithJavaLangCharSequence:subTitle];
  }
  [((RARELabelWidget *) nil_chk(bundleIconLabel)) setIconWithRAREiPlatformIcon:bundle ? [RAREPlatform getResourceAsIconWithNSString:@"bv.icon.bundle"] : nil];
}

+ (void)updateTitleWithRAREiViewer:(id<RAREiViewer>)viewer
                       withBoolean:(BOOL)force {
  if ([(id) viewer isKindOfClass:[RARETabPaneViewer class]]) {
    viewer = [((RARETabPaneViewer *) check_class_cast(viewer, [RARETabPaneViewer class])) getSelectedTabViewer];
  }
  else if ([(id) viewer isKindOfClass:[RAREStackPaneViewer class]]) {
    id<RAREiViewer> v = [((RAREStackPaneViewer *) check_class_cast(viewer, [RAREStackPaneViewer class])) getActiveViewer];
    if (v != nil) {
      viewer = v;
    }
  }
  id<RAREiContainer> titleWidget = [CCPBVUtils titleWidget];
  RARELabelWidget *iconLabel = (RARELabelWidget *) check_class_cast([((id<RAREiContainer>) nil_chk(titleWidget)) getWidgetWithNSString:@"bundleIcon"], [RARELabelWidget class]);
  RARELabelWidget *textLabel = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"subtitleLeft"], [RARELabelWidget class]);
  NSString *title = [((id<RAREiViewer>) nil_chk(viewer)) getTitle];
  id<RAREiViewer> v = viewer;
  while (force && (title == nil || [title sequenceLength] == 0)) {
    v = [v getParent];
    if (v == nil) {
      return;
    }
    title = [((id<RAREiViewer>) nil_chk(v)) getTitle];
  }
  if (title != nil && [title sequenceLength] > 0) {
    [((RARELabelWidget *) nil_chk(textLabel)) setTextWithJavaLangCharSequence:title];
  }
  [((RARELabelWidget *) nil_chk(iconLabel)) setIconWithRAREiPlatformIcon:[CCPBVCardStackUtils isBundleWithRAREiViewer:viewer] ? [RAREPlatform getResourceAsIconWithNSString:@"bv.icon.bundle"] : nil];
  textLabel = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"subtitleRight"], [RARELabelWidget class]);
  title = (NSString *) check_class_cast([viewer getAttributeWithNSString:CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_], [NSString class]);
  [((RARELabelWidget *) nil_chk(textLabel)) setTextWithJavaLangCharSequence:title == nil ? @"" : title];
}

+ (void)updateTitleWithNSString:(NSString *)title
                    withBoolean:(BOOL)bundle
                   withNSString:(NSString *)subTitle {
  id<RAREiContainer> titleWidget = [CCPBVUtils titleWidget];
  RARELabelWidget *iconLabel = (RARELabelWidget *) check_class_cast([((id<RAREiContainer>) nil_chk(titleWidget)) getWidgetWithNSString:@"bundleIcon"], [RARELabelWidget class]);
  RARELabelWidget *textLabel = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"subtitleLeft"], [RARELabelWidget class]);
  if (title != nil && [title sequenceLength] > 0) {
    [((RARELabelWidget *) nil_chk(textLabel)) setTextWithJavaLangCharSequence:title];
  }
  [((RARELabelWidget *) nil_chk(iconLabel)) setIconWithRAREiPlatformIcon:bundle ? [RAREPlatform getResourceAsIconWithNSString:@"bv.icon.bundle"] : nil];
  textLabel = (RARELabelWidget *) check_class_cast([titleWidget getWidgetWithNSString:@"subtitleRight"], [RARELabelWidget class]);
  if (subTitle == nil) {
    subTitle = @"";
  }
  [((RARELabelWidget *) nil_chk(textLabel)) setTextWithJavaLangCharSequence:subTitle];
}

+ (RAREWidgetPaneViewer *)createCardWithRAREiContainer:(id<RAREiContainer>)parent
                                                withId:(id)action
                                           withBoolean:(BOOL)bundle {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RARESPOTWidgetPane *cfg = (RARESPOTWidgetPane *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"WidgetPane" withNSString:@"bv.widgetpane.card"], [RARESPOTWidgetPane class]);
  RARESPOTLabel *label = (RARESPOTLabel *) check_class_cast([w createConfigurationObjectWithNSString:@"Label" withNSString:bundle ? @"bv.label.card.bundle" : @"bv.label.card.item"], [RARESPOTLabel class]);
  [((SPOTAny *) nil_chk(((RARESPOTWidgetPane *) nil_chk(cfg))->widget_)) setValueWithISPOTElement:label];
  id<RAREiViewer> v = [w createViewerWithRAREiWidget:parent withRARESPOTWidget:cfg];
  if (bundle) {
    [((id<RAREiViewer>) nil_chk(v)) setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_BUNDLE_URL_PROPERTY_ withId:action];
    [v setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ withId:[JavaLangBoolean getTRUE]];
  }
  else {
    [((id<RAREiViewer>) nil_chk(v)) setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_ withId:action];
  }
  return (RAREWidgetPaneViewer *) check_class_cast(v, [RAREWidgetPaneViewer class]);
}

+ (RAREWidgetPaneViewer *)createCardWithRAREiContainer:(id<RAREiContainer>)parent
                                                withId:(id)action
                                           withBoolean:(BOOL)bundle
                                       withRAREiWidget:(id<RAREiWidget>)content {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RARESPOTWidgetPane *cfg = (RARESPOTWidgetPane *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"WidgetPane" withNSString:@"bv.widgetpane.card"], [RARESPOTWidgetPane class]);
  id<RAREiViewer> v = [w createViewerWithRAREiWidget:parent withRARESPOTWidget:cfg];
  if (bundle) {
    [((id<RAREiViewer>) nil_chk(v)) setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_BUNDLE_URL_PROPERTY_ withId:action];
    [v setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ withId:[JavaLangBoolean getTRUE]];
  }
  else {
    [((id<RAREiViewer>) nil_chk(v)) setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_ACTION_PROPERTY_ withId:action];
  }
  RAREWidgetPaneViewer *wp = (RAREWidgetPaneViewer *) check_class_cast(v, [RAREWidgetPaneViewer class]);
  [((RAREWidgetPaneViewer *) nil_chk(wp)) setWidgetWithRAREiWidget:content];
  return wp;
}

+ (RAREStackPaneViewer *)createListPagesViewerWithNSString:(NSString *)title
                                        withRAREiContainer:(id<RAREiContainer>)parent
                                          withJavaUtilList:(id<JavaUtilList>)list
                                                   withInt:(int)itemsPerPage
                                                   withInt:(int)column
                                   withRAREiActionListener:(id<RAREiActionListener>)action {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RARESPOTStackPane *cfg = (RARESPOTStackPane *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"StackPane" withNSString:@"bv.stackpane.card"], [RARESPOTStackPane class]);
  if (title != nil) {
    [((SPOTPrintableString *) nil_chk(((RARESPOTStackPane *) nil_chk(cfg))->title_)) setValueWithNSString:title];
  }
  RAREStackPaneViewer *sp = (RAREStackPaneViewer *) check_class_cast([w createViewerWithRAREiWidget:parent withRARESPOTWidget:cfg], [RAREStackPaneViewer class]);
  int len = [((id<JavaUtilList>) nil_chk(list)) size];
  int lastPos = 0;
  JavaLangStringBuilder *sb = [[JavaLangStringBuilder alloc] init];
  id<RAREiViewer> v;
  int card = 1;
  while (lastPos < len) {
    int start = lastPos;
    RAREFormViewer *fv = (RAREFormViewer *) check_class_cast([w createWidgetWithRARESPOTWidget:CCPBVCardStackUtils_listItemPageConfig_], [RAREFormViewer class]);
    int n = 0;
    for (int i = 0; i < itemsPerPage && lastPos < len; i++) {
      RARERenderableDataItem *item = [list getWithInt:lastPos++];
      if (column != -1) {
        item = [((RARERenderableDataItem *) nil_chk(item)) getWithInt:column];
      }
      RARELabelWidget *l = (RARELabelWidget *) check_class_cast([((RAREFormViewer *) nil_chk(fv)) getWidgetWithInt:n++], [RARELabelWidget class]);
      if (action != nil) {
        [sb setLengthWithInt:0];
        (void) [((JavaLangStringBuilder *) nil_chk([sb appendWithInt:i + 1])) appendWithChar:'.'];
        [((RARELabelWidget *) nil_chk(l)) setValueWithId:[sb description]];
      }
      l = (RARELabelWidget *) check_class_cast([fv getWidgetWithInt:n++], [RARELabelWidget class]);
      [((RARELabelWidget *) nil_chk(l)) setIconWithRAREiPlatformIcon:[((RARERenderableDataItem *) nil_chk(item)) getIcon]];
      [l setValueWithId:[CCPBVCardStackUtils getItemTextWithRARERenderableDataItem:item withBoolean:YES]];
    }
    [((RAREStackPaneViewer *) nil_chk(sp)) addViewerWithNSString:nil withRAREiViewer:v = [CCPBVCardStackUtils createCardWithRAREiContainer:sp withId:action withBoolean:NO withRAREiWidget:fv]];
    [((id<RAREiViewer>) nil_chk(v)) setLinkedDataWithId:[list subListWithInt:start withInt:lastPos]];
    [v setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_IS_BUNDLE_PROPERTY_ withId:[JavaLangBoolean getTRUE]];
    [v setAttributeWithNSString:CCPBVCardStackUtils_VIEWER_SUBTITLE_PROPERTY_ withId:[w getStringWithNSString:@"bv.format.card_of" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ [JavaLangInteger valueOfWithInt:card++], [JavaLangInteger valueOfWithInt:[sp size]] } count:2 type:[IOSClass classWithClass:[NSObject class]]]]];
  }
  [((RAREStackPaneViewer *) nil_chk(sp)) switchToWithInt:0];
  return sp;
}

+ (void)initialize {
  if (self == [CCPBVCardStackUtils class]) {
    CCPBVCardStackUtils_defaultActionListener_ = [[CCPBVCardStackUtils_$1 alloc] init];
  }
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x2, NULL },
    { "createActionCardWithRAREiContainer:withId:", NULL, "LRAREWidgetPaneViewer", 0x9, NULL },
    { "createBundleCardWithRAREiContainer:withNSString:", NULL, "LRAREWidgetPaneViewer", 0x9, NULL },
    { "createItemsViewerWithNSString:withJavaUtilCollection:withInt:", NULL, "LRAREiViewer", 0x9, NULL },
    { "createListItemsOrPageViewerWithNSString:withRAREiContainer:withJavaUtilList:withInt:withInt:withRAREiActionListener:withBoolean:withBoolean:", NULL, "LRAREStackPaneViewer", 0x9, NULL },
    { "createListItemsViewerWithRAREStackPaneViewer:withNSString:withRAREiContainer:withJavaUtilList:withRAREiActionListener:withBoolean:withBoolean:", NULL, "LRAREStackPaneViewer", 0x9, NULL },
    { "createListPagesViewerWithNSString:withRAREiContainer:withJavaUtilList:withInt:withInt:", NULL, "LRAREStackPaneViewer", 0x9, NULL },
    { "createStackPaneViewer", NULL, "LRAREStackPaneViewer", 0x9, NULL },
    { "createTextCardWithRAREiContainer:withNSString:withId:", NULL, "LRAREWidgetPaneViewer", 0x9, NULL },
    { "generateRandomNumberStringWithInt:", NULL, "LNSString", 0x9, NULL },
    { "getItemTextWithRARERenderableDataItem:withBoolean:", NULL, "LNSString", 0x9, NULL },
    { "getViewerActionWithRAREiViewer:", NULL, "LNSObject", 0x9, NULL },
    { "getViewerBundleURLWithRAREiViewer:", NULL, "LNSString", 0x9, NULL },
    { "isBundleWithRAREiViewer:", NULL, "Z", 0x9, NULL },
    { "selectItemOnPageWithRAREiViewer:withInt:", NULL, "Z", 0x9, NULL },
    { "createCardWithRAREiContainer:withId:withBoolean:", NULL, "LRAREWidgetPaneViewer", 0xc, NULL },
    { "createCardWithRAREiContainer:withId:withBoolean:withRAREiWidget:", NULL, "LRAREWidgetPaneViewer", 0xc, NULL },
    { "createListPagesViewerWithNSString:withRAREiContainer:withJavaUtilList:withInt:withInt:withRAREiActionListener:", NULL, "LRAREStackPaneViewer", 0xc, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "VIEWER_ACTION_PROPERTY_", NULL, 0xa, "LNSString" },
    { "VIEWER_BUNDLE_URL_PROPERTY_", NULL, 0xa, "LNSString" },
    { "VIEWER_IS_BUNDLE_PROPERTY_", NULL, 0xa, "LNSString" },
    { "VIEWER_SUBTITLE_PROPERTY_", NULL, 0xa, "LNSString" },
    { "ITEM_TEXT_PROPERTY_", NULL, 0xa, "LNSString" },
    { "ITEM_LIST_TEXT_PROPERTY_", NULL, 0xa, "LNSString" },
    { "wearable_", NULL, 0x8, "Z" },
    { "voiceActionsSupported_", NULL, 0x8, "Z" },
    { "listItemPageConfig_", NULL, 0x8, "LRARESPOTForm" },
    { "defaultActionListener_", NULL, 0x8, "LRAREiActionListener" },
  };
  static J2ObjcClassInfo _CCPBVCardStackUtils = { "CardStackUtils", "com.sparseware.bellavista", NULL, 0x1, 18, methods, 10, fields, 0, NULL};
  return &_CCPBVCardStackUtils;
}

@end
@implementation CCPBVCardStackUtils_$1

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
  id o = [((RAREActionEvent *) nil_chk(e)) getSource];
  id<RAREiWidget> w = nil;
  if ([o conformsToProtocol: @protocol(RAREiWidget)]) {
    w = (id<RAREiWidget>) check_protocol_cast(o, @protocol(RAREiWidget));
    o = w == nil ? nil : [w getLinkedData];
  }
  if ([o isKindOfClass:[RARERenderableDataItem class]]) {
    id<RAREiActionListener> al = [((RARERenderableDataItem *) check_class_cast(o, [RARERenderableDataItem class])) getActionListener];
    if (al != nil) {
      [CCPBVCardStackUtils executeActionWithRAREiWidget:w == nil ? nil : [RAREPlatform getWindowViewer] withId:o withId:al];
    }
  }
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcClassInfo _CCPBVCardStackUtils_$1 = { "$1", "com.sparseware.bellavista", "CardStackUtils", 0x8000, 0, NULL, 0, NULL, 0, NULL};
  return &_CCPBVCardStackUtils_$1;
}

@end
@implementation CCPBVCardStackUtils_$2

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
  id<RAREiWidget> w = [((RAREActionEvent *) nil_chk(e)) getWidget];
  id o = [((id<RAREiWidget>) nil_chk(w)) getLinkedData];
  if ([o isKindOfClass:[RARERenderableDataItem class]]) {
    id<RAREiActionListener> al = [((RARERenderableDataItem *) check_class_cast(o, [RARERenderableDataItem class])) getActionListener];
    if (al == nil) {
      al = val$action_;
    }
    [CCPBVCardStackUtils executeActionWithRAREiWidget:w withId:o withId:al];
  }
  else {
    id<JavaUtilList> items = (id<JavaUtilList>) check_protocol_cast(o, @protocol(JavaUtilList));
    if ([((id<JavaUtilList>) nil_chk(items)) size] == 1) {
      o = [items getWithInt:0];
      [w setLinkedDataWithId:o];
      [CCPBVCardStackUtils executeActionWithRAREiWidget:w withId:o withId:val$action_];
    }
    else {
      [CCPBVUtils pushWorkspaceViewerWithRAREiViewer:[CCPBVCardStackUtils createListItemsViewerWithRAREStackPaneViewer:nil withNSString:nil withRAREiContainer:val$parent_ withJavaUtilList:items withRAREiActionListener:val$action_ withBoolean:val$bundle_ withBoolean:val$stretched_] withBoolean:NO];
    }
  }
}

- (id)initWithRAREiActionListener:(id<RAREiActionListener>)capture$0
               withRAREiContainer:(id<RAREiContainer>)capture$1
                      withBoolean:(BOOL)capture$2
                      withBoolean:(BOOL)capture$3 {
  val$action_ = capture$0;
  val$parent_ = capture$1;
  val$bundle_ = capture$2;
  val$stretched_ = capture$3;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$action_", NULL, 0x1012, "LRAREiActionListener" },
    { "val$parent_", NULL, 0x1012, "LRAREiContainer" },
    { "val$bundle_", NULL, 0x1012, "Z" },
    { "val$stretched_", NULL, 0x1012, "Z" },
  };
  static J2ObjcClassInfo _CCPBVCardStackUtils_$2 = { "$2", "com.sparseware.bellavista", "CardStackUtils", 0x8000, 0, NULL, 4, fields, 0, NULL};
  return &_CCPBVCardStackUtils_$2;
}

@end
@implementation CCPBVCardStackUtils_$3

- (void)finishedWithBoolean:(BOOL)calceled
                     withId:(id)returnValue {
  (void) [CCPBVCardStackUtils setListItemPageConfig:(RARESPOTForm *) check_class_cast(returnValue, [RARESPOTForm class])];
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcClassInfo _CCPBVCardStackUtils_$3 = { "$3", "com.sparseware.bellavista", "CardStackUtils", 0x8000, 0, NULL, 0, NULL, 0, NULL};
  return &_CCPBVCardStackUtils_$3;
}

@end
