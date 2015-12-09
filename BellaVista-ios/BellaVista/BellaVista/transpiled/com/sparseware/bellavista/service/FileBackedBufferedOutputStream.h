//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/FileBackedBufferedOutputStream.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _CCPBVFileBackedBufferedOutputStream_H_
#define _CCPBVFileBackedBufferedOutputStream_H_

@class IOSByteArray;
@class JavaIoFile;
@class JavaIoFileOutputStream;
@class JavaIoInputStream;
@class RAREUTByteArrayOutputStreamEx;

#import "JreEmulation.h"
#include "java/io/FileInputStream.h"
#include "java/io/OutputStream.h"

@interface CCPBVFileBackedBufferedOutputStream : JavaIoOutputStream {
 @public
  JavaIoFile *backingFile_;
  int bufferSize_;
  int maxBufferSize_;
  int count_;
  RAREUTByteArrayOutputStreamEx *byteStream_;
  JavaIoFileOutputStream *fileStream_;
}

- (id)init;
- (id)initWithInt:(int)bufferSize
          withInt:(int)maxBufferSize;
- (id)initWithInt:(int)bufferSize;
- (void)writeWithInt:(int)b;
- (void)writeWithByteArray:(IOSByteArray *)b
                   withInt:(int)off
                   withInt:(int)len;
- (void)close;
- (void)flush;
- (JavaIoInputStream *)getInputStream;
- (void)createFileStream;
- (void)copyAllFieldsTo:(CCPBVFileBackedBufferedOutputStream *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedOutputStream, backingFile_, JavaIoFile *)
J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedOutputStream, byteStream_, RAREUTByteArrayOutputStreamEx *)
J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedOutputStream, fileStream_, JavaIoFileOutputStream *)

typedef CCPBVFileBackedBufferedOutputStream ComSparsewareBellavistaServiceFileBackedBufferedOutputStream;

@interface CCPBVFileBackedBufferedOutputStream_DeleteFileOnCloseInputStream : JavaIoFileInputStream {
 @public
  JavaIoFile *file_;
}

- (id)initWithJavaIoFile:(JavaIoFile *)file;
- (void)close;
- (void)copyAllFieldsTo:(CCPBVFileBackedBufferedOutputStream_DeleteFileOnCloseInputStream *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVFileBackedBufferedOutputStream_DeleteFileOnCloseInputStream, file_, JavaIoFile *)

#endif // _CCPBVFileBackedBufferedOutputStream_H_
