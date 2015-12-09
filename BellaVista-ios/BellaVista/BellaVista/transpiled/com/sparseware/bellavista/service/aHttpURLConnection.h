//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/aHttpURLConnection.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _CCPBVaHttpURLConnection_H_
#define _CCPBVaHttpURLConnection_H_

@class CCPBVActionPath;
@class CCPBVContentWriter;
@class CCPBVFileBackedBufferedOutputStream;
@class CCPBVHttpHeaders;
@class IOSByteArray;
@class IOSObjectArray;
@class JavaIoOutputStream;
@class JavaIoReader;
@class JavaLangReflectMethod;
@class JavaLangThrowable;
@class JavaNetURL;
@class JavaUtilConcurrentConcurrentHashMap;
@protocol JavaIoCloseable;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/service/iHttpConnection.h"
#include "java/io/InputStream.h"
#include "java/net/HttpURLConnection.h"

@interface CCPBVaHttpURLConnection : JavaNetHttpURLConnection < CCPBViHttpConnection > {
 @public
  CCPBVFileBackedBufferedOutputStream *outStream_;
  JavaIoInputStream *inStream_;
  CCPBVHttpHeaders *headers_;
  NSString *clsPackage_;
  JavaLangReflectMethod *serviceMethod_;
  id serviceObject_;
  NSString *HUB_SUBSTRING_;
  CCPBVActionPath *servicePath_;
  CCPBVContentWriter *contentWriter_;
  JavaIoInputStream *errStream_;
}

+ (JavaUtilConcurrentConcurrentHashMap *)handlerMap;
+ (void)setHandlerMap:(JavaUtilConcurrentConcurrentHashMap *)handlerMap;
+ (IOSObjectArray *)methodParams;
+ (void)setMethodParams:(IOSObjectArray *)methodParams;
+ (JavaIoInputStream *)nullStream;
+ (void)setNullStream:(JavaIoInputStream *)nullStream;
- (id)initWithJavaNetURL:(JavaNetURL *)u
            withNSString:(NSString *)clsPackage;
- (void)connect;
- (CCPBVContentWriter *)getContentWriter;
- (void)disconnect;
- (NSString *)getHeaderFieldWithInt:(int)n;
- (NSString *)getHeaderFieldWithNSString:(NSString *)name;
- (NSString *)getHeaderFieldKeyWithInt:(int)n;
- (JavaIoInputStream *)getErrorStream;
- (JavaIoInputStream *)getInputStream;
- (JavaIoOutputStream *)getOutputStream;
- (NSString *)getResquestHeaderWithNSString:(NSString *)name;
- (BOOL)usingProxy;
- (void)connectToService;
- (void)closeStreamWithJavaIoCloseable:(id<JavaIoCloseable>)stream;
- (JavaIoInputStream *)getExceptionInputStreamWithCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
                                             withJavaLangThrowable:(JavaLangThrowable *)e;
- (JavaIoReader *)getContentReader;
- (void)copyAllFieldsTo:(CCPBVaHttpURLConnection *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, outStream_, CCPBVFileBackedBufferedOutputStream *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, inStream_, JavaIoInputStream *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, headers_, CCPBVHttpHeaders *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, clsPackage_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, serviceMethod_, JavaLangReflectMethod *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, serviceObject_, id)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, HUB_SUBSTRING_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, servicePath_, CCPBVActionPath *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, contentWriter_, CCPBVContentWriter *)
J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection, errStream_, JavaIoInputStream *)

typedef CCPBVaHttpURLConnection ComSparsewareBellavistaServiceAHttpURLConnection;

@interface CCPBVaHttpURLConnection_ContentReaderInputStream : JavaIoInputStream {
 @public
  __weak CCPBVContentWriter *contentWriter_;
  JavaIoInputStream *stream_;
}

- (id)initWithCCPBVContentWriter:(CCPBVContentWriter *)contentWriter;
- (int)read;
- (int)readWithByteArray:(IOSByteArray *)b
                 withInt:(int)off
                 withInt:(int)len;
- (void)copyAllFieldsTo:(CCPBVaHttpURLConnection_ContentReaderInputStream *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaHttpURLConnection_ContentReaderInputStream, stream_, JavaIoInputStream *)

@interface CCPBVaHttpURLConnection_$1 : JavaIoInputStream {
}

- (int)read;
- (long long int)skipWithLong:(long long int)n;
- (id)init;
@end

#endif // _CCPBVaHttpURLConnection_H_