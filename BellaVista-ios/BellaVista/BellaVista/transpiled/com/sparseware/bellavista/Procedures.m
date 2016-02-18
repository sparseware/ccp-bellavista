//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/Procedures.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIColor.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/viewer/StackPaneViewer.h"
#include "com/appnativa/rare/viewer/TableViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/widget/LabelWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/StringCache.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/CardStackUtils.h"
#include "com/sparseware/bellavista/Procedures.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/aResultsManager.h"
#include "java/lang/Math.h"
#include "java/util/EventObject.h"
#include "java/util/List.h"

@implementation CCPBVProcedures

static int CCPBVProcedures_STATUS_COLUMN_ = 3;

+ (int)STATUS_COLUMN {
  return CCPBVProcedures_STATUS_COLUMN_;
}

+ (int *)STATUS_COLUMNRef {
  return &CCPBVProcedures_STATUS_COLUMN_;
}

- (id)init {
  if (self = [super initWithNSString:@"procedures" withNSString:@"Procedures"]) {
    RAREUTJSONObject *info = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"proceduresInfo"], [RAREUTJSONObject class]);
    attachmentColumn_ = [((RAREUTJSONObject *) nil_chk(info)) optIntWithNSString:@"attachmentColumn" withInt:-1];
    parentColumn_ = [info optIntWithNSString:@"parentColumn" withInt:-1];
    infoName_ = @"proceduresInfo";
    documentPath_ = @"/hub/main/procedures/procedure/";
  }
  return self;
}

- (void)onCreatedWithNSString:(NSString *)eventName
              withRAREiWidget:(id<RAREiWidget>)widget
      withJavaUtilEventObject:(JavaUtilEventObject *)event {
}

- (void)onFinishedLoadingWithNSString:(NSString *)eventName
                      withRAREiWidget:(id<RAREiWidget>)widget
              withJavaUtilEventObject:(JavaUtilEventObject *)event {
  if ([CCPBVUtils isCardStack]) {
    RARETableViewer *table = (RARETableViewer *) check_class_cast(widget, [RARETableViewer class]);
    id<RAREiFormViewer> fv = [((RARETableViewer *) nil_chk(table)) getFormViewer];
    [self populateCardStackWithRAREiContainer:fv withJavaUtilList:table];
    [self updateCardStackTitleWithNSString:[((id<RAREiFormViewer>) nil_chk(fv)) getTitle] withNSString:nil];
  }
}

- (void)populateCardStackWithRAREiContainer:(id<RAREiContainer>)fv
                           withJavaUtilList:(id<JavaUtilList>)rows {
  int len = [((id<JavaUtilList>) nil_chk(rows)) size];
  id<RAREiContainer> itemsForm = (id<RAREiContainer>) check_protocol_cast([((id<RAREiContainer>) nil_chk(fv)) getWidgetWithNSString:@"itemsForm"], @protocol(RAREiContainer));
  int count = [JavaLangMath minWithInt:[((id<RAREiContainer>) nil_chk(itemsForm)) getWidgetCount] / 2 withInt:len];
  int n = 0;
  for (int i = 0; i < count; i++) {
    RARERenderableDataItem *row = [rows getWithInt:i];
    RARERenderableDataItem *name = [((RARERenderableDataItem *) nil_chk(row)) getWithInt:[CCPBVaResultsManager NAME_POSITION]];
    RARERenderableDataItem *item = [row getItemExWithInt:CCPBVProcedures_STATUS_COLUMN_];
    NSString *status = (item == nil) ? @"" : [item description];
    RARELabelWidget *nl = (RARELabelWidget *) check_class_cast([itemsForm getWidgetWithInt:n++], [RARELabelWidget class]);
    RARELabelWidget *sl = (RARELabelWidget *) check_class_cast([itemsForm getWidgetWithInt:n++], [RARELabelWidget class]);
    if ([((RARERenderableDataItem *) nil_chk(name)) getForeground] != nil) {
      [((RARELabelWidget *) nil_chk(nl)) setForegroundWithRAREUIColor:[name getForeground]];
      [((RARELabelWidget *) nil_chk(sl)) setForegroundWithRAREUIColor:[name getForeground]];
    }
    [((RARELabelWidget *) nil_chk(nl)) setValueWithId:name];
    [((RARELabelWidget *) nil_chk(sl)) setValueWithId:status];
  }
  if (count < len) {
    NSString *s = [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) getStringWithNSString:@"bv.format.tap_to_see_more" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ [RAREUTStringCache valueOfWithInt:count], [RAREUTStringCache valueOfWithInt:len] } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
    id<RAREiWidget> tapLabel = [fv getWidgetWithNSString:@"tapLabel"];
    [((id<RAREiWidget>) nil_chk(tapLabel)) setValueWithId:s];
  }
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "populateCardStackWithRAREiContainer:withJavaUtilList:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "STATUS_COLUMN_", NULL, 0xc, "I" },
  };
  static J2ObjcClassInfo _CCPBVProcedures = { "Procedures", "com.sparseware.bellavista", NULL, 0x1, 1, methods, 1, fields, 0, NULL};
  return &_CCPBVProcedures;
}

@end
@implementation CCPBVProcedures_OrdersStackActionListener

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
  RAREStackPaneViewer *sp = [CCPBVCardStackUtils createListItemsOrPageViewerWithNSString:nil withRAREiContainer:[((RARETableViewer *) nil_chk(this$0_->dataTable_)) getFormViewer] withJavaUtilList:[this$0_->dataTable_ getRawRows] withInt:-1 withInt:1 withRAREiActionListener:nil withBoolean:NO withBoolean:YES];
  [CCPBVUtils pushWorkspaceViewerWithRAREiViewer:sp withBoolean:NO];
}

- (id)initWithCCPBVProcedures:(CCPBVProcedures *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVProcedures" },
  };
  static J2ObjcClassInfo _CCPBVProcedures_OrdersStackActionListener = { "OrdersStackActionListener", "com.sparseware.bellavista", "Procedures", 0x4, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVProcedures_OrdersStackActionListener;
}

@end
