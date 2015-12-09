//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/FileBackedBufferedWriter.java
//
//  Created by decoteaud on 11/18/15.
//

#include "IOSByteArray.h"
#include "IOSCharArray.h"
#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/util/ByteArray.h"
#include "com/appnativa/util/CharArray.h"
#include "com/appnativa/util/ISO88591Helper.h"
#include "com/sparseware/bellavista/service/FileBackedBufferedOutputStream.h"
#include "com/sparseware/bellavista/service/FileBackedBufferedWriter.h"
#include "java/io/ByteArrayInputStream.h"
#include "java/io/File.h"
#include "java/io/FileWriter.h"
#include "java/io/IOException.h"
#include "java/io/InputStream.h"
#include "java/io/Reader.h"
#include "java/io/StringReader.h"
#include "java/lang/Exception.h"

@implementation CCPBVFileBackedBufferedWriter

- (id)init {
  return [self initCCPBVFileBackedBufferedWriterWithInt:1024];
}

- (id)initCCPBVFileBackedBufferedWriterWithInt:(int)bufferSize
                                       withInt:(int)maxBufferSize {
  if (self = [super init]) {
    self->bufferSize_ = bufferSize;
    self->maxBufferSize_ = maxBufferSize;
  }
  return self;
}

- (id)initWithInt:(int)bufferSize
          withInt:(int)maxBufferSize {
  return [self initCCPBVFileBackedBufferedWriterWithInt:bufferSize withInt:maxBufferSize];
}

- (id)initCCPBVFileBackedBufferedWriterWithInt:(int)bufferSize {
  return [self initCCPBVFileBackedBufferedWriterWithInt:bufferSize withInt:1024 * 1024 * 32];
}

- (id)initWithInt:(int)bufferSize {
  return [self initCCPBVFileBackedBufferedWriterWithInt:bufferSize];
}

- (long long int)getContentLength {
  if (fileWriter_ != nil) {
    @try {
      [fileWriter_ flush];
    }
    @catch (JavaIoIOException *e) {
    }
    return (int) [((JavaIoFile *) nil_chk(backingFile_)) length];
  }
  if (charWriter_ == nil) {
    return 0;
  }
  return ((RAREUTCharArray *) nil_chk(charWriter_))->_length_;
}

- (void)writeWithInt:(int)b {
  count_++;
  if ((count_ > maxBufferSize_) && (fileWriter_ == nil)) {
    [self createFileWriter];
  }
  if (fileWriter_ != nil) {
    [fileWriter_ writeWithInt:b];
  }
  else {
    if (charWriter_ == nil) {
      charWriter_ = [[RAREUTCharArray alloc] initWithInt:bufferSize_];
    }
    (void) [((RAREUTCharArray *) nil_chk(charWriter_)) appendWithChar:(unichar) b];
  }
}

- (void)writeWithCharArray:(IOSCharArray *)b
                   withInt:(int)off
                   withInt:(int)len {
  count_ += len;
  if ((count_ > maxBufferSize_) && (fileWriter_ == nil)) {
    [self createFileWriter];
  }
  if (fileWriter_ != nil) {
    [fileWriter_ writeWithCharArray:b withInt:off withInt:len];
  }
  else {
    if (charWriter_ == nil) {
      charWriter_ = [[RAREUTCharArray alloc] initWithInt:bufferSize_];
    }
    (void) [((RAREUTCharArray *) nil_chk(charWriter_)) appendWithCharArray:b withInt:off withInt:len];
  }
}

- (void)close {
  if (fileWriter_ != nil) {
    [fileWriter_ close];
  }
}

- (void)flush {
  if (fileWriter_ != nil) {
    [fileWriter_ flush];
  }
}

- (void)clear {
  if (fileWriter_ != nil) {
    @try {
      [fileWriter_ close];
    }
    @catch (JavaIoIOException *ignore) {
    }
    @try {
      [((JavaIoFile *) nil_chk(backingFile_)) delete__];
    }
    @catch (JavaLangException *ignore) {
    }
    backingFile_ = nil;
    fileWriter_ = nil;
  }
  if (charWriter_ != nil) {
    (void) [charWriter_ setLengthWithInt:0];
  }
}

- (JavaIoReader *)getReader {
  if (fileWriter_ != nil) {
    [fileWriter_ close];
    return [[CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader alloc] initWithJavaIoFile:backingFile_];
  }
  if (charWriter_ == nil) {
    return [[JavaIoStringReader alloc] initWithNSString:@""];
  }
  return charWriter_;
}

- (JavaIoInputStream *)getInputStream {
  if (fileWriter_ != nil) {
    [fileWriter_ close];
    return [[CCPBVFileBackedBufferedOutputStream_DeleteFileOnCloseInputStream alloc] initWithJavaIoFile:backingFile_];
  }
  if (charWriter_ == nil) {
    return [[JavaIoByteArrayInputStream alloc] initWithByteArray:[IOSByteArray arrayWithLength:0]];
  }
  RAREUTByteArray *ba = [[RAREUTByteArray alloc] initWithInt:[((RAREUTCharArray *) nil_chk(charWriter_)) sequenceLength]];
  ba->_length_ = [charWriter_ getBytesWithRAREUTByteArray:ba withRAREUTiCharsetHelper:[RAREUTISO88591Helper getInstance]];
  return ba;
}

- (void)createFileWriter {
  backingFile_ = [RAREPlatform createCacheFileWithNSString:@"BVOutput"];
  fileWriter_ = [[JavaIoFileWriter alloc] initWithJavaIoFile:backingFile_];
}

- (void)copyAllFieldsTo:(CCPBVFileBackedBufferedWriter *)other {
  [super copyAllFieldsTo:other];
  other->backingFile_ = backingFile_;
  other->bufferSize_ = bufferSize_;
  other->charWriter_ = charWriter_;
  other->count_ = count_;
  other->fileWriter_ = fileWriter_;
  other->maxBufferSize_ = maxBufferSize_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "writeWithInt:", NULL, "V", 0x1, "JavaIoIOException" },
    { "writeWithCharArray:withInt:withInt:", NULL, "V", 0x1, "JavaIoIOException" },
    { "close", NULL, "V", 0x1, "JavaIoIOException" },
    { "flush", NULL, "V", 0x1, "JavaIoIOException" },
    { "getReader", NULL, "LJavaIoReader", 0x1, "JavaIoIOException" },
    { "getInputStream", NULL, "LJavaIoInputStream", 0x1, "JavaIoIOException" },
    { "createFileWriter", NULL, "V", 0x4, "JavaIoIOException" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "backingFile_", NULL, 0x0, "LJavaIoFile" },
    { "bufferSize_", NULL, 0x0, "I" },
    { "maxBufferSize_", NULL, 0x0, "I" },
    { "count_", NULL, 0x0, "I" },
    { "charWriter_", NULL, 0x0, "LRAREUTCharArray" },
    { "fileWriter_", NULL, 0x0, "LJavaIoFileWriter" },
  };
  static J2ObjcClassInfo _CCPBVFileBackedBufferedWriter = { "FileBackedBufferedWriter", "com.sparseware.bellavista.service", NULL, 0x1, 7, methods, 6, fields, 0, NULL};
  return &_CCPBVFileBackedBufferedWriter;
}

@end
@implementation CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader

- (id)initWithJavaIoFile:(JavaIoFile *)file {
  if (self = [super initWithJavaIoFile:file]) {
    self->file_ = file;
  }
  return self;
}

- (void)close {
  [super close];
  @try {
    [((JavaIoFile *) nil_chk(file_)) delete__];
  }
  @catch (JavaLangException *ignore) {
  }
}

- (void)copyAllFieldsTo:(CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader *)other {
  [super copyAllFieldsTo:other];
  other->file_ = file_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithJavaIoFile:", NULL, NULL, 0x1, "JavaIoIOException" },
    { "close", NULL, "V", 0x1, "JavaIoIOException" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "file_", NULL, 0x0, "LJavaIoFile" },
  };
  static J2ObjcClassInfo _CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader = { "DeleteFileOnCloseReader", "com.sparseware.bellavista.service", "FileBackedBufferedWriter", 0x8, 2, methods, 1, fields, 0, NULL};
  return &_CCPBVFileBackedBufferedWriter_DeleteFileOnCloseReader;
}

@end