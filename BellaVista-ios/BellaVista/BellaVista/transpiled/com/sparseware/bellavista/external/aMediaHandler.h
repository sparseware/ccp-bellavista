//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aMediaHandler.java
//
//  Created by decoteaud on 2/2/15.
//

#ifndef _ComSparsewareBellavistaExternalaMediaHandler_H_
#define _ComSparsewareBellavistaExternalaMediaHandler_H_

@class ComSparsewareBellavistaDocument;
@class ComSparsewareBellavistaDocument_DocumentItem;
@protocol JavaUtilList;
@protocol RAREiContainer;
@protocol RAREiFunctionCallback;
@protocol RAREiViewer;

#import "JreEmulation.h"

@interface ComSparsewareBellavistaExternalaMediaHandler : NSObject {
}

- (id)init;
- (void)createViewerWithRAREiContainer:(id<RAREiContainer>)parent
   withComSparsewareBellavistaDocument:(ComSparsewareBellavistaDocument *)document
withComSparsewareBellavistaDocument_DocumentItem:(ComSparsewareBellavistaDocument_DocumentItem *)attachment
             withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb;
- (id<JavaUtilList>)getToolbarWidgetsWithRAREiViewer:(id<RAREiViewer>)viewer;
- (void)dispose;
@end

#endif // _ComSparsewareBellavistaExternalaMediaHandler_H_
