//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/DefaultImageViewer.java
//
//  Created by decoteaud on 3/24/15.
//

#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/iCancelableFuture.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/spot/Viewer.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/viewer/CarouselViewer.h"
#include "com/appnativa/rare/viewer/ImagePaneViewer.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iFormViewer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/spot/iSPOTElement.h"
#include "com/appnativa/util/ObjectHolder.h"
#include "com/sparseware/bellavista/DefaultImageViewer.h"
#include "com/sparseware/bellavista/Document.h"
#include "com/sparseware/bellavista/Utils.h"
#include "java/lang/Exception.h"
#include "java/lang/Throwable.h"
#include "java/util/EventObject.h"
#include "java/util/List.h"

@implementation CCPBVDefaultImageViewer

static int CCPBVDefaultImageViewer_THUMBNAIL_URL_POSITION_ = 2;
static int CCPBVDefaultImageViewer_IMAGE_URL_POSITION_ = 3;

+ (int)THUMBNAIL_URL_POSITION {
  return CCPBVDefaultImageViewer_THUMBNAIL_URL_POSITION_;
}

+ (int *)THUMBNAIL_URL_POSITIONRef {
  return &CCPBVDefaultImageViewer_THUMBNAIL_URL_POSITION_;
}

+ (int)IMAGE_URL_POSITION {
  return CCPBVDefaultImageViewer_IMAGE_URL_POSITION_;
}

+ (int *)IMAGE_URL_POSITIONRef {
  return &CCPBVDefaultImageViewer_IMAGE_URL_POSITION_;
}

- (id)init {
  return [super init];
}

- (void)createViewerWithRAREiContainer:(id<RAREiContainer>)parent
                     withCCPBVDocument:(CCPBVDocument *)document
        withCCPBVDocument_DocumentItem:(CCPBVDocument_DocumentItem *)attachment
             withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  RAREaWorkerTask *task = [[CCPBVDefaultImageViewer_$1 alloc] initWithCCPBVDefaultImageViewer:self withRAREiFunctionCallback:cb withRAREWindowViewer:w withRAREiContainer:parent withCCPBVDocument_DocumentItem:attachment withCCPBVDocument:document];
  (void) [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) executeWorkerTaskWithRAREiWorkerTask:task];
}

- (id<JavaUtilList>)getToolbarWidgetsWithRAREiViewer:(id<RAREiViewer>)viewer {
  return nil;
}

- (void)dispose {
}

- (void)stateChangedWithJavaUtilEventObject:(JavaUtilEventObject *)e {
  RARECarouselViewer *slides = (RARECarouselViewer *) check_class_cast([RAREPlatform getWidgetForComponentWithId:[((JavaUtilEventObject *) nil_chk(e)) getSource]], [RARECarouselViewer class]);
  RAREImagePaneViewer *iv = (RAREImagePaneViewer *) check_class_cast([((id<RAREiFormViewer>) nil_chk([((RARECarouselViewer *) nil_chk(slides)) getFormViewer])) getWidgetWithNSString:@"imageViewer"], [RAREImagePaneViewer class]);
  RARERenderableDataItem *item = (RARERenderableDataItem *) check_class_cast([slides getSelection], [RARERenderableDataItem class]);
  [((RAREImagePaneViewer *) nil_chk(iv)) clearContents];
  if (item != nil) {
    NSString *src = (NSString *) check_class_cast([((RARERenderableDataItem *) nil_chk([item getWithInt:CCPBVDefaultImageViewer_IMAGE_URL_POSITION_])) getValue], [NSString class]);
    if ((src == nil) || ([src sequenceLength] == 0) || (iv == nil) || [src isEqual:[iv getLinkedData]]) {
      return;
    }
    [iv setLinkedDataWithId:src];
    [iv handleActionLinkWithRAREActionLink:[[RAREActionLink alloc] initWithNSString:src] withBoolean:YES];
  }
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getToolbarWidgetsWithRAREiViewer:", NULL, "LJavaUtilList", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "THUMBNAIL_URL_POSITION_", NULL, 0x9, "I" },
    { "IMAGE_URL_POSITION_", NULL, 0x9, "I" },
  };
  static J2ObjcClassInfo _CCPBVDefaultImageViewer = { "DefaultImageViewer", "com.sparseware.bellavista", NULL, 0x1, 1, methods, 2, fields, 0, NULL};
  return &_CCPBVDefaultImageViewer;
}

@end
@implementation CCPBVDefaultImageViewer_$1

- (void)finishWithId:(id)result {
  if ([result isKindOfClass:[JavaLangThrowable class]]) {
    [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:YES withId:result];
    return;
  }
  RAREUTObjectHolder *oh = (RAREUTObjectHolder *) check_class_cast(result, [RAREUTObjectHolder class]);
  id<RAREiContainer> v = (id<RAREiContainer>) check_protocol_cast([((RAREWindowViewer *) nil_chk(val$w_)) createViewerWithRAREiWidget:[((id<RAREiContainer>) nil_chk(val$parent_)) getFormViewer] withRARESPOTWidget:(RARESPOTViewer *) check_class_cast(((RAREUTObjectHolder *) nil_chk(oh))->type_, [RARESPOTViewer class])], @protocol(RAREiContainer));
  RARECarouselViewer *slides = (RARECarouselViewer *) check_class_cast([((id<RAREiContainer>) nil_chk(v)) getWidgetWithNSString:@"thumbNails"], [RARECarouselViewer class]);
  [((RARECarouselViewer *) nil_chk(slides)) addAllWithJavaUtilCollection:(id<JavaUtilList>) check_protocol_cast(oh->value_, @protocol(JavaUtilList))];
  [slides addChangeListenerWithRAREiChangeListener:this$0_];
  [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:NO withId:[[RAREUTObjectHolder alloc] initWithId:this$0_ withId:val$attachment_ withId:v]];
}

- (id)compute {
  @try {
    NSString *id_ = [((CCPBVDocument *) nil_chk(val$document_)) getID];
    RAREActionLink *link = [CCPBVUtils createLinkWithRAREiWidget:val$w_ withNSString:[NSString stringWithFormat:@"/hub/main/imaging/thumbnails/%@", id_] withBoolean:NO];
    id<JavaUtilList> list = [((RAREWindowViewer *) nil_chk(val$w_)) parseDataLinkWithRAREActionLink:link withBoolean:YES];
    for (RARERenderableDataItem * __strong item in nil_chk(list)) {
      [item setValueWithId:[((RARERenderableDataItem *) nil_chk([((RARERenderableDataItem *) nil_chk(item)) getWithInt:[CCPBVDefaultImageViewer THUMBNAIL_URL_POSITION]])) getValue]];
    }
    RARESPOTViewer *cfg = (RARESPOTViewer *) check_class_cast([val$w_ createConfigurationObjectWithRAREActionLink:[val$w_ createActionLinkWithId:@"/image_viewer.rml"]], [RARESPOTViewer class]);
    return [[RAREUTObjectHolder alloc] initWithId:cfg withId:list];
  }
  @catch (JavaLangException *e) {
    return e;
  }
}

- (id)initWithCCPBVDefaultImageViewer:(CCPBVDefaultImageViewer *)outer$
            withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0
                 withRAREWindowViewer:(RAREWindowViewer *)capture$1
                   withRAREiContainer:(id<RAREiContainer>)capture$2
       withCCPBVDocument_DocumentItem:(CCPBVDocument_DocumentItem *)capture$3
                    withCCPBVDocument:(CCPBVDocument *)capture$4 {
  this$0_ = outer$;
  val$cb_ = capture$0;
  val$w_ = capture$1;
  val$parent_ = capture$2;
  val$attachment_ = capture$3;
  val$document_ = capture$4;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "compute", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVDefaultImageViewer" },
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
    { "val$w_", NULL, 0x1012, "LRAREWindowViewer" },
    { "val$parent_", NULL, 0x1012, "LRAREiContainer" },
    { "val$attachment_", NULL, 0x1012, "LCCPBVDocument_DocumentItem" },
    { "val$document_", NULL, 0x1012, "LCCPBVDocument" },
  };
  static J2ObjcClassInfo _CCPBVDefaultImageViewer_$1 = { "$1", "com.sparseware.bellavista", "DefaultImageViewer", 0x8000, 1, methods, 6, fields, 0, NULL};
  return &_CCPBVDefaultImageViewer_$1;
}

@end
