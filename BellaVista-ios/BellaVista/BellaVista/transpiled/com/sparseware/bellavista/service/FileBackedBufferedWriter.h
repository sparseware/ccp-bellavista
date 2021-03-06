//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/FileBackedBufferedWriter.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVFileBackedBufferedWriter_H_
#define _CCPBVFileBackedBufferedWriter_H_

@class IOSCharArray;
@class JavaIoFile;
@class JavaIoFileWriter;
@class JavaIoInputStream;
@class JavaIoReader;
@class RAREUTCharArray;

#import "JreEmulation.h"
#include "java/io/FileReader.h"
#include "java/io/Writer.h"

@interface CCPBVFileBackedBufferedWriter : JavaIoWriter {
 @public
  JavaIoFile *backingFile_;
  int bufferSize_;
  int maxBufferSize_;
  int count_;
  RAREUTCharArray *charWriter_;
  JavaIoFileWriter *fileWriter_;
}

- (id)init;
- (id)initWithInt:(int)bufferSize
          withInt:(int)maxBufferSize;
- (id)initWithInt:(int)bufferSize;
- (long long int)getContentLength;
- (void)writeWithInt:(int)b;
- (void)writeWithCharArray:(IOSCharArray *)b
                   withInt:(int)off
                   withInt:(int)len;
- (void)close;
- (void)flush;
- (void)clear;
- (JavaIoReader *)getReader;
- (JavaIoInputStream *)getInputStream;
- (void)createFileWriter;
- (void)copyAllFieldsTo:(CCPBVFileBackedBufferedWriter *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedWriter, backingFile_, JavaIoFile *)
J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedWriter, charWriter_, RAREUTCharArray *)
J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedWriter, fileWriter_, JavaIoFileWriter *)

typedef CCPBVFileBackedBufferedWriter ComSparsewareBellavistaServiceFileBackedBufferedWriter;

@interface CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader : JavaIoFileReader {
 @public
  JavaIoFile *file_;
}

- (id)initWithJavaIoFile:(JavaIoFile *)file;
- (void)close;
- (void)copyAllFieldsTo:(CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader, file_, JavaIoFile *)

#endif // _CCPBVFileBackedBufferedWriter_H_
