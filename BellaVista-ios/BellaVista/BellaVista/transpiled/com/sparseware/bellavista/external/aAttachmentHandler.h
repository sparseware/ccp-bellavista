//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aAttachmentHandler.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _CCPBVaAttachmentHandler_H_
#define _CCPBVaAttachmentHandler_H_

@class CCPBVDocument;
@class CCPBVDocument_DocumentItem;
@protocol JavaUtilList;
@protocol RAREiContainer;
@protocol RAREiFunctionCallback;
@protocol RAREiViewer;

#import "JreEmulation.h"

@interface CCPBVaAttachmentHandler : NSObject {
}

- (id)init;
- (void)createViewerWithRAREiContainer:(id<RAREiContainer>)parent
                     withCCPBVDocument:(CCPBVDocument *)document
        withCCPBVDocument_DocumentItem:(CCPBVDocument_DocumentItem *)attachment
             withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (id<JavaUtilList>)getToolbarWidgetsWithRAREiViewer:(id<RAREiViewer>)viewer;
- (void)dispose;
@end

typedef CCPBVaAttachmentHandler ComSparsewareBellavistaExternalAAttachmentHandler;

#endif // _CCPBVaAttachmentHandler_H_
