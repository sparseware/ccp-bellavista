//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/PickList.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iCancelableFuture.h"
#include "com/appnativa/rare/iConstants.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/spot/ListBox.h"
#include "com/appnativa/rare/ui/AlertPanel.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/event/ActionEvent.h"
#include "com/appnativa/rare/viewer/ListBoxViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/widget/PushButtonWidget.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/SPOTBoolean.h"
#include "com/appnativa/spot/iSPOTElement.h"
#include "com/sparseware/bellavista/PickList.h"
#include "java/lang/Boolean.h"
#include "java/util/List.h"

@implementation CCPBVPickList

- (id)init {
  if (self = [super init]) {
    supportListDblClick_ = YES;
    rightAlignButtons_ = YES;
  }
  return self;
}

- (id)initWithNSString:(NSString *)windowTitle
      withJavaUtilList:(id<JavaUtilList>)dataRows {
  if (self = [super init]) {
    supportListDblClick_ = YES;
    rightAlignButtons_ = YES;
    self->windowTitle_ = windowTitle;
    self->dataRows_ = dataRows;
  }
  return self;
}

- (id)initWithNSString:(NSString *)windowTitle
    withRAREActionLink:(RAREActionLink *)dataLink {
  if (self = [super init]) {
    supportListDblClick_ = YES;
    rightAlignButtons_ = YES;
    self->windowTitle_ = windowTitle;
    self->dataLink_ = dataLink;
  }
  return self;
}

- (void)setCancelButtonTextWithNSString:(NSString *)text {
  self->cancelButtonText_ = text;
}

- (void)setDataLinkWithRAREActionLink:(RAREActionLink *)dataLink {
  self->dataLink_ = dataLink;
  self->dataRows_ = nil;
}

- (void)setDataRowsWithJavaUtilList:(id<JavaUtilList>)dataRows {
  self->dataRows_ = dataRows;
  self->dataLink_ = nil;
}

- (void)setOkButtonTextWithNSString:(NSString *)text {
  self->okButtonText_ = text;
}

- (void)setShowNoneButtonWithBoolean:(BOOL)show
                        withNSString:(NSString *)text {
  showNone_ = show;
  noneButtonText_ = text;
}

- (void)setSupportListDblClickWithBoolean:(BOOL)supportListDblClick {
  self->supportListDblClick_ = supportListDblClick;
}

- (void)setWindowTitleWithNSString:(NSString *)windowTitle {
  self->windowTitle_ = windowTitle;
}

- (void)showWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RARESPOTListBox *cfg = (RARESPOTListBox *) check_class_cast([((RAREWindowViewer *) nil_chk(w)) createConfigurationObjectWithNSString:@"ListBox" withNSString:@"bv.listbox.pick_list"], [RARESPOTListBox class]);
  [((SPOTBoolean *) nil_chk(((RARESPOTListBox *) nil_chk(cfg))->singleClickActionEnabled_)) setValueWithBoolean:YES];
  RAREListBoxViewer *lb = (RAREListBoxViewer *) check_class_cast([w createWidgetWithRARESPOTWidget:cfg], [RAREListBoxViewer class]);
  if (dataRows_ != nil) {
    [((RAREListBoxViewer *) nil_chk(lb)) setAllWithJavaUtilCollection:dataRows_];
  }
  else {
    (void) [((RAREListBoxViewer *) nil_chk(lb)) setDataLinkWithRAREActionLink:dataLink_ withBoolean:YES];
  }
  dataRows_ = nil;
  dataLink_ = nil;
  RAREAlertPanel *p;
  if (showNone_) {
    p = [RAREAlertPanel yesNoCancelWithRAREiWidget:w withNSString:windowTitle_ withId:lb withRAREiPlatformIcon:nil withNSString:okButtonText_ withNSString:noneButtonText_ withNSString:cancelButtonText_ withNSString:@"bv.button.alert"];
  }
  else {
    p = [RAREAlertPanel yesNoWithRAREiWidget:w withNSString:windowTitle_ withId:lb withRAREiPlatformIcon:nil withNSString:okButtonText_ withNSString:noneButtonText_ withBoolean:YES];
  }
  [((RAREAlertPanel *) nil_chk(p)) setRightAlignButtonsWithBoolean:rightAlignButtons_];
  RAREPushButtonWidget *okButton = [p getYesOrOkButton];
  [((RAREPushButtonWidget *) nil_chk(okButton)) setEnabledWithBoolean:NO];
  if (supportListDblClick_) {
    [((RAREListBoxViewer *) nil_chk(lb)) setEventHandlerWithNSString:[RAREiConstants ATTRIBUTE_ON_DOUBLECLICK] withId:[[CCPBVPickList_$1 alloc] initWithRAREPushButtonWidget:okButton] withBoolean:YES];
  }
  [((RAREListBoxViewer *) nil_chk(lb)) addActionListenerWithRAREiActionListener:[[CCPBVPickList_$2 alloc] initWithRAREPushButtonWidget:okButton]];
  id<RAREiFunctionCallback> pcb = [[CCPBVPickList_$3 alloc] initWithRAREListBoxViewer:lb withRAREiFunctionCallback:cb];
  [p showDialogWithRAREiFunctionCallback:pcb];
}

- (void)setRightAlignButtonsWithBoolean:(BOOL)rightAlignButtons {
  self->rightAlignButtons_ = rightAlignButtons;
}

- (void)copyAllFieldsTo:(CCPBVPickList *)other {
  [super copyAllFieldsTo:other];
  other->cancelButtonText_ = cancelButtonText_;
  other->dataLink_ = dataLink_;
  other->dataRows_ = dataRows_;
  other->noneButtonText_ = noneButtonText_;
  other->okButtonText_ = okButtonText_;
  other->rightAlignButtons_ = rightAlignButtons_;
  other->showNone_ = showNone_;
  other->supportListDblClick_ = supportListDblClick_;
  other->windowTitle_ = windowTitle_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "showNone_", NULL, 0x4, "Z" },
    { "noneButtonText_", NULL, 0x4, "LNSString" },
    { "supportListDblClick_", NULL, 0x4, "Z" },
    { "windowTitle_", NULL, 0x4, "LNSString" },
    { "dataRows_", NULL, 0x4, "LJavaUtilList" },
    { "dataLink_", NULL, 0x4, "LRAREActionLink" },
    { "okButtonText_", NULL, 0x4, "LNSString" },
    { "cancelButtonText_", NULL, 0x4, "LNSString" },
    { "rightAlignButtons_", NULL, 0x4, "Z" },
  };
  static J2ObjcClassInfo _CCPBVPickList = { "PickList", "com.sparseware.bellavista", NULL, 0x1, 0, NULL, 9, fields, 0, NULL};
  return &_CCPBVPickList;
}

@end
@implementation CCPBVPickList_$1

- (void)run {
  [((RAREPushButtonWidget *) nil_chk(val$okButton_)) click];
}

- (id)initWithRAREPushButtonWidget:(RAREPushButtonWidget *)capture$0 {
  val$okButton_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$okButton_", NULL, 0x1012, "LRAREPushButtonWidget" },
  };
  static J2ObjcClassInfo _CCPBVPickList_$1 = { "$1", "com.sparseware.bellavista", "PickList", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVPickList_$1;
}

@end
@implementation CCPBVPickList_$2

- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e {
  [((RAREPushButtonWidget *) nil_chk(val$okButton_)) setEnabledWithBoolean:YES];
}

- (id)initWithRAREPushButtonWidget:(RAREPushButtonWidget *)capture$0 {
  val$okButton_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$okButton_", NULL, 0x1012, "LRAREPushButtonWidget" },
  };
  static J2ObjcClassInfo _CCPBVPickList_$2 = { "$2", "com.sparseware.bellavista", "PickList", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_CCPBVPickList_$2;
}

@end
@implementation CCPBVPickList_$3

- (void)finishedWithBoolean:(BOOL)canceled
                     withId:(id)returnValue {
  RARERenderableDataItem *item = nil;
  if ([((JavaLangBoolean *) nil_chk([JavaLangBoolean getTRUE])) isEqual:returnValue]) {
    item = [((RAREListBoxViewer *) nil_chk(val$lb_)) getSelectedItem];
  }
  [((RAREListBoxViewer *) nil_chk(val$lb_)) dispose];
  [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:canceled withId:item];
}

- (id)initWithRAREListBoxViewer:(RAREListBoxViewer *)capture$0
      withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$1 {
  val$lb_ = capture$0;
  val$cb_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$lb_", NULL, 0x1012, "LRAREListBoxViewer" },
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
  };
  static J2ObjcClassInfo _CCPBVPickList_$3 = { "$3", "com.sparseware.bellavista", "PickList", 0x8000, 0, NULL, 2, fields, 0, NULL};
  return &_CCPBVPickList_$3;
}

@end
