//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/ContentWriter.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVContentWriter_H_
#define _CCPBVContentWriter_H_

@class CCPBVFileBackedBufferedOutputStream;
@class IOSCharArray;
@class JavaIoWriter;
@class RAREUTCharArray;
@protocol JavaUtilList;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/service/FileBackedBufferedWriter.h"

@interface CCPBVContentWriter : CCPBVFileBackedBufferedWriter {
 @public
  CCPBVFileBackedBufferedOutputStream *contentStream_;
  JavaIoWriter *contentWriter_;
  RAREUTCharArray *caIn_;
  RAREUTCharArray *caOut_;
}

- (BOOL)escapeWithCharArray:(IOSCharArray *)chars
                    withInt:(int)pos
                    withInt:(int)len
        withRAREUTCharArray:(RAREUTCharArray *)outArg;
- (void)printWithNSString:(NSString *)s;
- (void)printlnWithJavaUtilList:(id<JavaUtilList>)list;
- (void)printlnWithNSString:(NSString *)s;
- (id)init;
- (void)copyAllFieldsTo:(CCPBVContentWriter *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVContentWriter, contentStream_, CCPBVFileBackedBufferedOutputStream *)
J2OBJC_FIELD_SETTER(CCPBVContentWriter, contentWriter_, JavaIoWriter *)
J2OBJC_FIELD_SETTER(CCPBVContentWriter, caIn_, RAREUTCharArray *)
J2OBJC_FIELD_SETTER(CCPBVContentWriter, caOut_, RAREUTCharArray *)

typedef CCPBVContentWriter ComSparsewareBellavistaServiceContentWriter;

#endif // _CCPBVContentWriter_H_
