//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Notes.java
//
//  Created by decoteaud on 3/24/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iCancelableFuture.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/net/JavaURLConnection.h"
#include "com/appnativa/rare/spot/GridPane.h"
#include "com/appnativa/rare/spot/StackPane.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/appnativa/rare/viewer/GridPaneViewer.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/TableViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/ComboBoxWidget.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/aWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/SPOTBoolean.h"
#include "com/appnativa/spot/SPOTPrintableString.h"
#include "com/appnativa/util/iFilterableList.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/ActionPath.h"
#include "com/sparseware/bellavista/Document.h"
#include "com/sparseware/bellavista/Notes.h"
#include "com/sparseware/bellavista/ResultsView.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/aResultsManager.h"
#include "java/lang/Exception.h"
#include "java/net/MalformedURLException.h"
#include "java/net/URL.h"
#include "java/util/ArrayList.h"
#include "java/util/Collection.h"
#include "java/util/Date.h"
#include "java/util/EventObject.h"
#include "java/util/HashMap.h"
#include "java/util/Iterator.h"
#include "java/util/List.h"

@implementation CCPBVNotes

static int CCPBVNotes_STATUS_COLUMN_ = 8;

+ (int)STATUS_COLUMN {
  return CCPBVNotes_STATUS_COLUMN_;
}

+ (int *)STATUS_COLUMNRef {
  return &CCPBVNotes_STATUS_COLUMN_;
}

- (id)init {
  return [self initCCPBVNotesWithNSString:@"notes" withNSString:@"Notes"];
}

- (id)initCCPBVNotesWithNSString:(NSString *)namePrefix
                    withNSString:(NSString *)scriptClassName {
  if (self = [super initWithNSString:namePrefix withNSString:scriptClassName]) {
    infoName_ = @"notesInfo";
    documentPath_ = @"/hub/main/documents/document/";
    parentMap_ = [[JavaUtilHashMap alloc] init];
    currentView_ = [CCPBVResultsViewEnum DOCUMENT];
    RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"notesInfo"], [RAREUTJSONObject class]);
    attachmentColumn_ = [((RAREUTJSONObject *) nil_chk(info)) optIntWithNSString:@"attachmentColumn" withInt:-1];
    parentColumn_ = [info optIntWithNSString:@"parentColumn" withInt:-1];
    attachmentIcon_ = [RAREPlatform getResourceAsIconWithNSString:@"bv.icon.document_with_attachment"];
    if ([CCPBVDocument documentViewerCfg] == nil) {
      (void) [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) spawnWithNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ [[CCPBVNotes_$1 alloc] init] } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
    }
  }
  return self;
}

- (id)initWithNSString:(NSString *)namePrefix
          withNSString:(NSString *)scriptClassName {
  return [self initCCPBVNotesWithNSString:namePrefix withNSString:scriptClassName];
}

- (BOOL)checkRowWithRARERenderableDataItem:(RARERenderableDataItem *)row
                                   withInt:(int)index
                                   withInt:(int)expandableColumn
                                   withInt:(int)rowCount {
  if (parentColumn_ > -1) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getItemExWithInt:parentColumn_];
    NSString *parent = item == nil ? nil : (NSString *) check_class_cast([item getValue], [NSString class]);
    if (parent != nil && [parent sequenceLength] > 0) {
      id<JavaUtilList> list = [((JavaUtilHashMap *) nil_chk(parentMap_)) getWithId:parent];
      if (list == nil) {
        list = [[JavaUtilArrayList alloc] initWithInt:3];
        (void) [parentMap_ putWithId:parent withId:list];
      }
      [((id<JavaUtilList>) nil_chk(list)) addWithId:row];
      return NO;
    }
  }
  if (attachmentColumn_ > -1) {
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getItemExWithInt:attachmentColumn_];
    if (item != nil) {
      NSString *s = [item description];
      if ([((NSString *) nil_chk(s)) isEqual:@"true"]) {
        item = [row getWithInt:0];
        if ([((RARERenderableDataItem *) nil_chk(item)) getIcon] == nil) {
          [item setIconWithRAREiPlatformIcon:attachmentIcon_];
        }
      }
    }
  }
  return YES;
}

- (void)onDisposeWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if (loadedDocument_ != nil) {
    [loadedDocument_ dispose];
  }
  loadedDocument_ = nil;
  [super onDisposeWithNSString:eventName withRAREiWidget:widget withJavaUtilEventObject:event];
}

- (void)onFiltersConfigureWithNSString:(NSString *)eventName
                       withRAREiWidget:(id<RAREiWidget>)widget
               withJavaUtilEventObject:(JavaUtilEventObject *)event {
  RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:infoName_], [RAREUTJSONObject class]);
  RAREUTJSONArray *filters = [((RAREUTJSONObject *) nil_chk(info)) getJSONArrayWithNSString:@"filters"];
  int len = filters == nil ? 0 : [filters size];
  if (len == 0) {
    [((id<RAREiWidget>) nil_chk(widget)) setEnabledWithBoolean:NO];
    return;
  }
  id<RAREiPlatformIcon> icon = [RAREPlatform getResourceAsIconWithNSString:@"bv.icon.notes"];
  int defaultFilter = 0;
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *filter = [((RAREUTJSONArray *) nil_chk(filters)) getJSONObjectWithInt:i];
    RARERenderableDataItem *item = [[RARERenderableDataItem alloc] initWithNSString:[((id<RAREiWidget>) nil_chk(widget)) expandStringWithNSString:[((RAREUTJSONObject *) nil_chk(filter)) getStringWithNSString:@"text"] withBoolean:NO] withId:nil withRAREiPlatformIcon:icon];
    CCPBVNotes_FilterAction *fa = [[CCPBVNotes_FilterAction alloc] initWithRAREUTJSONObject:filter];
    if ([fa isDefault]) {
      defaultFilter = i;
    }
    [item setActionListenerWithRAREiActionListener:fa];
    [((RAREaWidget *) check_class_cast(widget, [RAREaWidget class])) addWithId:item];
  }
  if ([(id) widget isKindOfClass:[RAREPushButtonWidget class]]) {
    [((RAREPushButtonWidget *) check_class_cast(widget, [RAREPushButtonWidget class])) setPopupScrollableWithBoolean:YES];
    [((RAREPushButtonWidget *) check_class_cast(widget, [RAREPushButtonWidget class])) setSelectedIndexWithInt:defaultFilter];
  }
  else if ([(id) widget isKindOfClass:[RAREComboBoxWidget class]]) {
    [((RAREComboBoxWidget *) check_class_cast(widget, [RAREComboBoxWidget class])) setSelectedIndexWithInt:defaultFilter];
  }
}

+ (void)goBackToDocumentViewWithRAREStackPaneViewer:(RAREStackPaneViewer *)sp {
  if (sp != nil && ![sp isEmpty] && [sp getActiveViewerIndex] != 0) {
    [sp switchToWithInt:0 withRAREiFunctionCallback:[[CCPBVNotes_$2 alloc] initWithRAREStackPaneViewer:sp]];
  }
}

- (void)onTableActionWithNSString:(NSString *)eventName
                  withRAREiWidget:(id<RAREiWidget>)widget
          withJavaUtilEventObject:(JavaUtilEventObject *)event {
  @try {
    RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
    RARERenderableDataItem *row = [((RARETableViewer *) nil_chk(table)) getSelectedItem];
    RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:[CCPBVaResultsManager DATE_POSITION]];
    JavaUtilDate *date = (JavaUtilDate *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getValue], [JavaUtilDate class]);
    NSString *id_ = (NSString *) check_class_cast([item getLinkedData], [NSString class]);
    id<RAREiFormViewer> fv = [((id<RAREiWidget>) nil_chk(widget)) getFormViewer];
    RAREStackPaneViewer *sp = (RAREStackPaneViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk([widget getFormViewer])) getWidgetWithNSString:@"documentStack"], [RAREStackPaneViewer class]);
    [CCPBVNotes goBackToDocumentViewWithRAREStackPaneViewer:sp];
    CCPBVDocument *doc;
    if ([[((id<RAREiFormViewer>) nil_chk(fv)) getLinkedData] isKindOfClass:[CCPBVDocument class]]) {
      doc = (CCPBVDocument *) check_class_cast([fv getLinkedData], [CCPBVDocument class]);
      if ([((NSString *) nil_chk([((CCPBVDocument *) nil_chk(doc)) getID])) isEqual:id_]) {
        return;
      }
    }
    if (loadedDocument_ != nil) {
      [loadedDocument_ dispose];
    }
    NSString *title = [((RARERenderableDataItem *) nil_chk([row getWithInt:[CCPBVaResultsManager NAME_POSITION]])) description];
    RAREActionLink *link = [[RAREActionLink alloc] initWithRAREiWidget:widget withJavaNetURL:[widget getURLWithNSString:[NSString stringWithFormat:@"%@%@.html", documentPath_, id_]]];
    doc = [[CCPBVDocument alloc] initWithRAREiWidget:widget withRAREActionLink:link withNSString:id_];
    loadedDocument_ = doc;
    [doc setMainDocumentInfoWithJavaUtilDate:date withNSString:title];
    if (![item isEmpty]) {
      for (RARERenderableDataItem * __strong di in nil_chk([item getItems])) {
        [self addAttachmentWithRARETableViewer:table withCCPBVDocument:doc withRARERenderableDataItem:di];
      }
    }
    if (sp != nil) {
      fv = sp;
    }
    RAREGridPaneViewer *gp = (RAREGridPaneViewer *) check_class_cast([fv getWidgetWithNSString:@"documentViewer"], [RAREGridPaneViewer class]);
    if (gp == nil) {
      gp = [CCPBVDocument createDocumentViewerWithRAREiContainer:fv withBoolean:[doc getAttachmentCount] > 0];
      if (sp != nil) {
        (void) [sp setViewerWithInt:0 withRAREiViewer:gp];
        [sp switchToWithInt:0];
      }
      else {
        RARESPOTStackPane *cfg = [[RARESPOTStackPane alloc] init];
        [((SPOTBoolean *) nil_chk(cfg->actAsFormViewer_)) setValueWithBoolean:YES];
        [((SPOTPrintableString *) nil_chk(cfg->transitionAnimator_)) setValueWithNSString:@"SlideAnimation"];
        [((SPOTPrintableString *) nil_chk(cfg->name_)) setValueWithNSString:@"documentStack"];
        RAREStackPaneViewer *spnew = (RAREStackPaneViewer *) check_class_cast([((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) createViewerWithRAREiWidget:fv withRARESPOTWidget:cfg], [RAREStackPaneViewer class]);
        [((RAREStackPaneViewer *) nil_chk(spnew)) addViewerWithNSString:nil withRAREiViewer:gp];
        [spnew switchToWithInt:0];
        [CCPBVUtils pushWorkspaceViewerWithRAREiViewer:spnew];
      }
    }
    [doc loadAndPopulateViewerWithRAREiContainer:[((RAREGridPaneViewer *) nil_chk(gp)) isActAsFormViewer] ? gp : ((id) fv)];
  }
  @catch (JavaLangException *e) {
    [CCPBVUtils handleErrorWithJavaLangThrowable:e];
  }
}

- (void)onTableCreatedWithNSString:(NSString *)eventName
                   withRAREiWidget:(id<RAREiWidget>)widget
           withJavaUtilEventObject:(JavaUtilEventObject *)event {
  [super onTableCreatedWithNSString:eventName withRAREiWidget:widget withJavaUtilEventObject:event];
  currentView_ = [CCPBVResultsViewEnum DOCUMENT];
}

- (void)reset {
  [super reset];
  [((JavaUtilHashMap *) nil_chk(parentMap_)) clear];
}

- (void)dataParsedWithRAREiWidget:(id<RAREiWidget>)widget
                 withJavaUtilList:(id<JavaUtilList>)rows
               withRAREActionLink:(RAREActionLink *)link {
  originalRows_ = rows;
  RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
  dataTable_ = table;
  [((RARETableViewer *) nil_chk(table)) setWidgetDataLinkWithRAREActionLink:link];
  int len = [((id<JavaUtilList>) nil_chk(rows)) size];
  if ((len == 1) && ![((RARERenderableDataItem *) nil_chk([rows getWithInt:0])) isEnabled]) {
    hasNoData_ = YES;
    [table addParsedRowWithRARERenderableDataItem:[rows getWithInt:0]];
    [table finishedParsing];
    [table finishedLoading];
    dataLoaded_ = YES;
    return;
  }
  else {
    [self processDataWithRARETableViewer:table withJavaUtilList:rows];
  }
}

- (void)addAttachmentWithRARETableViewer:(RARETableViewer *)table
                       withCCPBVDocument:(CCPBVDocument *)doc
              withRARERenderableDataItem:(RARERenderableDataItem *)row {
  RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:[CCPBVaResultsManager DATE_POSITION]];
  [((RARETableViewer *) nil_chk(table)) convertWithInt:[CCPBVaResultsManager DATE_POSITION] withRARERenderableDataItem:item];
  id o = [((RARERenderableDataItem *) nil_chk(item)) getValue];
  if ([o isKindOfClass:[NSString class]]) {
    o = [item getValue];
  }
  JavaUtilDate *date = (JavaUtilDate *) check_class_cast(o, [JavaUtilDate class]);
  NSString *id_ = (NSString *) check_class_cast([item getLinkedData], [NSString class]);
  NSString *title = [((RARERenderableDataItem *) nil_chk([row getWithInt:[CCPBVaResultsManager NAME_POSITION]])) description];
  JavaNetURL *url = [table getURLWithNSString:[NSString stringWithFormat:@"%@%@.html", documentPath_, id_]];
  [((CCPBVDocument *) nil_chk(doc)) addAttachmentWithCCPBVDocument_DocumentItemTypeEnum:[CCPBVDocument_DocumentItemTypeEnum DOCUMENT] withJavaUtilDate:date withNSString:title withNSString:[RAREJavaURLConnection toExternalFormWithJavaNetURL:url]];
}

- (void)processDataWithRARETableViewer:(RARETableViewer *)table
                      withJavaUtilList:(id<JavaUtilList>)rows {
  if (attachmentColumn_ > 1) {
    int len = [((id<JavaUtilList>) nil_chk(rows)) size];
    for (int i = len - 1; i > -1; i--) {
      RARERenderableDataItem *row = [rows getWithInt:i];
      if (![self checkRowWithRARERenderableDataItem:row withInt:i withInt:0 withInt:len]) {
        (void) [rows removeWithInt:i];
      }
    }
  }
  BOOL needsSorting = NO;
  if (![((JavaUtilHashMap *) nil_chk(parentMap_)) isEmpty]) {
    int len = [((id<JavaUtilList>) nil_chk(rows)) size];
    JavaUtilHashMap *map = parentMap_;
    for (int i = 0; i < len; i++) {
      RARERenderableDataItem *row = [rows getWithInt:i];
      RARERenderableDataItem *item = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:[CCPBVaResultsManager DATE_POSITION]];
      NSString *id_ = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk(item)) getLinkedData], [NSString class]);
      id<JavaUtilList> list = [map removeWithId:id_];
      if (list != nil) {
        [item addAllWithJavaUtilCollection:list];
        if ([item getIcon] == nil) {
          [item setIconWithRAREiPlatformIcon:attachmentIcon_];
        }
      }
    }
    if (![map isEmpty]) {
      id<JavaUtilIterator> it = [((id<JavaUtilCollection>) nil_chk([map values])) iterator];
      while ([((id<JavaUtilIterator>) nil_chk(it)) hasNext]) {
        [rows addAllWithJavaUtilCollection:[it next]];
      }
      needsSorting = YES;
    }
  }
  [((RARETableViewer *) nil_chk(table)) setAllWithJavaUtilCollection:rows];
  if (needsSorting) {
    [table sortWithInt:0 withBoolean:YES withBoolean:NO];
  }
  [table finishedLoading];
  CCPBVActionPath *path = [CCPBVUtils getActionPathWithBoolean:YES];
  NSString *key = path == nil ? nil : [path pop];
  if (key != nil) {
    [self handlePathKeyWithRARETableViewer:table withNSString:key withInt:0 withBoolean:YES];
  }
}

- (void)copyAllFieldsTo:(CCPBVNotes *)other {
  [super copyAllFieldsTo:other];
  other->attachmentColumn_ = attachmentColumn_;
  other->attachmentIcon_ = attachmentIcon_;
  other->documentPath_ = documentPath_;
  other->infoName_ = infoName_;
  other->loadedDocument_ = loadedDocument_;
  other->parentColumn_ = parentColumn_;
  other->parentMap_ = parentMap_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithNSString:withNSString:", NULL, NULL, 0x4, NULL },
    { "checkRowWithRARERenderableDataItem:withInt:withInt:withInt:", NULL, "Z", 0x1, NULL },
    { "goBackToDocumentViewWithRAREStackPaneViewer:", NULL, "V", 0x8, NULL },
    { "reset", NULL, "V", 0x4, NULL },
    { "dataParsedWithRAREiWidget:withJavaUtilList:withRAREActionLink:", NULL, "V", 0x4, NULL },
    { "addAttachmentWithRARETableViewer:withCCPBVDocument:withRARERenderableDataItem:", NULL, "V", 0x4, "JavaNetMalformedURLException" },
    { "processDataWithRARETableViewer:withJavaUtilList:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "attachmentColumn_", NULL, 0x4, "I" },
    { "parentColumn_", NULL, 0x4, "I" },
    { "STATUS_COLUMN_", NULL, 0x9, "I" },
    { "infoName_", NULL, 0x4, "LNSString" },
    { "documentPath_", NULL, 0x4, "LNSString" },
    { "loadedDocument_", NULL, 0x0, "LCCPBVDocument" },
    { "attachmentIcon_", NULL, 0x0, "LRAREiPlatformIcon" },
    { "parentMap_", NULL, 0x0, "LJavaUtilHashMap" },
  };
  static J2ObjcClassInfo _CCPBVNotes = { "Notes", "com.sparseware.bellavista", NULL, 0x1, 7, methods, 8, fields, 0, NULL};
  return &_CCPBVNotes;
}

@end
@implementation CCPBVNotes_FilterAction

- (id)initWithRAREUTJSONObject:(RAREUTJSONObject *)filter {
  if (self = [super init]) {
    self->filter_ = filter;
  }
  return self;
}

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
}

- (BOOL)isDefault {
  return [((RAREUTJSONObject *) nil_chk(filter_)) getBooleanWithNSString:@"serverSide"] && [((NSString *) nil_chk([filter_ getStringWithNSString:@"filter"])) sequenceLength] == 0;
}

- (void)copyAllFieldsTo:(CCPBVNotes_FilterAction *)other {
  [super copyAllFieldsTo:other];
  other->filter_ = filter_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "isDefault", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "filter_", NULL, 0x0, "LRAREUTJSONObject" },
  };
  static J2ObjcClassInfo _CCPBVNotes_FilterAction = { "FilterAction", "com.sparseware.bellavista", "Notes", 0x8, 1, methods, 1, fields, 0, NULL};
  return &_CCPBVNotes_FilterAction;
}

@end
@implementation CCPBVNotes_Status

- (id)init {
  return [super init];
}

- (void)copyAllFieldsTo:(CCPBVNotes_Status *)other {
  [super copyAllFieldsTo:other];
  other->icon_ = icon_;
  other->key_ = key_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "key_", NULL, 0x0, "LNSString" },
    { "icon_", NULL, 0x0, "LNSString" },
  };
  static J2ObjcClassInfo _CCPBVNotes_Status = { "Status", "com.sparseware.bellavista", "Notes", 0x8, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVNotes_Status;
}

@end
@implementation CCPBVNotes_$1

- (void)run {
  @try {
    [CCPBVDocument staticInitialize];
  }
  @catch (JavaLangException *e) {
    [CCPBVUtils handleErrorWithJavaLangThrowable:e];
  }
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcClassInfo _CCPBVNotes_$1 = { "$1", "com.sparseware.bellavista", "Notes", 0x8000, 0, NULL, 0, NULL, 0, NULL};
  return &_CCPBVNotes_$1;
}

@end
@implementation CCPBVNotes_$2

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  id<RAREiViewer> v = [((RAREStackPaneViewer *) nil_chk(val$sp_)) removeViewerWithInt:1];
  if (v != nil) {
    [v dispose];
  }
}

- (id)initWithRAREStackPaneViewer:(RAREStackPaneViewer *)capture$0 {
  val$sp_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$sp_", NULL, 0x1012, "LRAREStackPaneViewer" },
  };
  static J2ObjcClassInfo _CCPBVNotes_$2 = { "$2", "com.sparseware.bellavista", "Notes", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVNotes_$2;
}

@end
