//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/DemoBarcodeReader.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVDemoBarcodeReader_H_
#define _CCPBVDemoBarcodeReader_H_

@protocol RAREiFunctionCallback;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/external/aBarcodeReader.h"
#include "java/lang/Runnable.h"

@interface CCPBVDemoBarcodeReader : CCPBVaBarcodeReader {
}

- (id)init;
- (void)readWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)resultCallback;
- (BOOL)isReaderAvailable;
- (void)dispose;
@end

typedef CCPBVDemoBarcodeReader ComSparsewareBellavistaExternalDemoBarcodeReader;

@interface CCPBVDemoBarcodeReader_$1 : NSObject < JavaLangRunnable > {
 @public
  id<RAREiFunctionCallback> val$resultCallback_;
}

- (void)run;
- (id)initWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0;
@end

J2OBJC_FIELD_SETTER(CCPBVDemoBarcodeReader_$1, val$resultCallback_, id<RAREiFunctionCallback>)

#endif // _CCPBVDemoBarcodeReader_H_
