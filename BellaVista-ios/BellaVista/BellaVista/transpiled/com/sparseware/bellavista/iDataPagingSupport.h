//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/iDataPagingSupport.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBViDataPagingSupport_H_
#define _CCPBViDataPagingSupport_H_

@protocol RAREiWidget;

#import "JreEmulation.h"

@protocol CCPBViDataPagingSupport < NSObject, JavaObject >
- (void)changePageWithBoolean:(BOOL)next
              withRAREiWidget:(id<RAREiWidget>)nextPageWidget
              withRAREiWidget:(id<RAREiWidget>)previousPageWidget;
@end

#define ComSparsewareBellavistaIDataPagingSupport CCPBViDataPagingSupport

#endif // _CCPBViDataPagingSupport_H_
