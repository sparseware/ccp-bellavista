//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aAttachmentHandler.java
//
//  Created by decoteaud on 3/12/15.
//

#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/rare/viewer/iViewer.h"
#include "com/sparseware/bellavista/Document.h"
#include "com/sparseware/bellavista/external/aAttachmentHandler.h"
#include "java/util/List.h"

@implementation ComSparsewareBellavistaExternalaAttachmentHandler

- (id)init {
  return [super init];
}

- (void)createViewerWithRAREiContainer:(id<RAREiContainer>)parent
   withComSparsewareBellavistaDocument:(ComSparsewareBellavistaDocument *)document
withComSparsewareBellavistaDocument_DocumentItem:(ComSparsewareBellavistaDocument_DocumentItem *)attachment
             withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (id<JavaUtilList>)getToolbarWidgetsWithRAREiViewer:(id<RAREiViewer>)viewer {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)dispose {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "createViewerWithRAREiContainer:withComSparsewareBellavistaDocument:withComSparsewareBellavistaDocument_DocumentItem:withRAREiFunctionCallback:", NULL, "V", 0x401, NULL },
    { "getToolbarWidgetsWithRAREiViewer:", NULL, "LJavaUtilList", 0x401, NULL },
    { "dispose", NULL, "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalaAttachmentHandler = { "aAttachmentHandler", "com.sparseware.bellavista.external", NULL, 0x401, 3, methods, 0, NULL, 0, NULL};
  return &_ComSparsewareBellavistaExternalaAttachmentHandler;
}

@end
