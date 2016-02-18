//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/ContentWriter.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSCharArray.h"
#include "com/appnativa/rare/exception/ApplicationException.h"
#include "com/appnativa/util/CharArray.h"
#include "com/appnativa/util/CharScanner.h"
#include "com/sparseware/bellavista/service/ContentWriter.h"
#include "com/sparseware/bellavista/service/FileBackedBufferedOutputStream.h"
#include "com/sparseware/bellavista/service/FileBackedBufferedWriter.h"
#include "java/io/IOException.h"
#include "java/io/Writer.h"
#include "java/util/List.h"

@implementation CCPBVContentWriter

- (BOOL)escapeWithCharArray:(IOSCharArray *)chars
                    withInt:(int)pos
                    withInt:(int)len
        withRAREUTCharArray:(RAREUTCharArray *)outArg {
  unichar c;
  BOOL quote = NO;
  for (int i = 0; i < len; i++) {
    c = IOSCharArray_Get(nil_chk(chars), pos + i);
    switch (c) {
      case '\\':
      (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:'\\'];
      break;
      case 0x000d:
      (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:'\\'];
      c = 'r';
      break;
      case 0x000a:
      (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:'\\'];
      c = 'n';
      break;
      case 0x0009:
      (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:'\\'];
      c = 't';
      break;
      case 0x0008:
      (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:'\\'];
      c = 'b';
      break;
      case 0x000c:
      (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:'\\'];
      c = 'f';
      break;
      case '"':
      (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:'\\'];
      c = '"';
      break;
      case '^':
      case '\'':
      quote = YES;
      break;
      default:
      if ((c < 32) || (c > 126)) {
        (void) [RAREUTCharScanner charToUnicodeStringWithChar:c withRAREUTCharArray:outArg withBoolean:YES];
        continue;
      }
      break;
    }
    (void) [((RAREUTCharArray *) nil_chk(outArg)) appendWithChar:c];
  }
  return quote;
}

- (void)printWithNSString:(NSString *)s {
  @try {
    (void) [self appendWithJavaLangCharSequence:s];
  }
  @catch (JavaIoIOException *e) {
    @throw [[RAREApplicationException alloc] initWithJavaLangThrowable:e];
  }
}

- (void)printlnWithJavaUtilList:(id<JavaUtilList>)list {
  @try {
    int len = [((id<JavaUtilList>) nil_chk(list)) size];
    if (caIn_ == nil) {
      caIn_ = [[RAREUTCharArray alloc] init];
    }
    if (caOut_ == nil) {
      caOut_ = [[RAREUTCharArray alloc] init];
    }
    RAREUTCharArray *ca = caIn_;
    RAREUTCharArray *out = caOut_;
    ((RAREUTCharArray *) nil_chk(out))->_length_ = 0;
    for (int i = 0; i < len; i++) {
      if (i != 0) {
        [self writeWithInt:'^'];
      }
      NSString *s = [list getWithInt:i];
      (void) [((RAREUTCharArray *) nil_chk(ca)) setWithNSString:s];
      if ([self escapeWithCharArray:ca->A_ withInt:0 withInt:ca->_length_ withRAREUTCharArray:out]) {
        [self writeWithInt:'"'];
        [self writeWithCharArray:out->A_ withInt:0 withInt:out->_length_];
        [self writeWithInt:'"'];
      }
      else {
        [self writeWithCharArray:out->A_ withInt:0 withInt:out->_length_];
      }
    }
    [self writeWithInt:0x000a];
  }
  @catch (JavaIoIOException *e) {
    @throw [[RAREApplicationException alloc] initWithJavaLangThrowable:e];
  }
}

- (void)printlnWithNSString:(NSString *)s {
  @try {
    (void) [((JavaIoWriter *) nil_chk([self appendWithJavaLangCharSequence:s])) appendWithJavaLangCharSequence:@"\n"];
  }
  @catch (JavaIoIOException *e) {
    @throw [[RAREApplicationException alloc] initWithJavaLangThrowable:e];
  }
}

- (id)init {
  return [super init];
}

- (void)copyAllFieldsTo:(CCPBVContentWriter *)other {
  [super copyAllFieldsTo:other];
  other->caIn_ = caIn_;
  other->caOut_ = caOut_;
  other->contentStream_ = contentStream_;
  other->contentWriter_ = contentWriter_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "escapeWithCharArray:withInt:withInt:withRAREUTCharArray:", NULL, "Z", 0x1, NULL },
    { "printlnWithNSString:", NULL, "V", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "contentStream_", NULL, 0x4, "LCCPBVFileBackedBufferedOutputStream" },
    { "contentWriter_", NULL, 0x4, "LJavaIoWriter" },
    { "caIn_", NULL, 0x4, "LRAREUTCharArray" },
    { "caOut_", NULL, 0x4, "LRAREUTCharArray" },
  };
  static J2ObjcClassInfo _CCPBVContentWriter = { "ContentWriter", "com.sparseware.bellavista.service", NULL, 0x1, 2, methods, 4, fields, 0, NULL};
  return &_CCPBVContentWriter;
}

@end
