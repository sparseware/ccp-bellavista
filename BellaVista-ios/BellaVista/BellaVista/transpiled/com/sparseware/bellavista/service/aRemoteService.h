//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/aRemoteService.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVaRemoteService_H_
#define _CCPBVaRemoteService_H_

@class CCPBVActionPath;
@class CCPBVHttpHeaders;
@class IOSObjectArray;
@class JavaIoInputStream;
@class JavaIoPrintWriter;
@class JavaIoStringWriter;
@class JavaIoWriter;
@class JavaLangException;
@class JavaNetURL;
@class RAREUTCharArray;
@protocol CCPBViHttpConnection;
@protocol JavaUtilList;
@protocol JavaUtilMap;

#import "JreEmulation.h"

@interface CCPBVaRemoteService : NSObject {
 @public
  JavaIoStringWriter *debugLogger_;
  JavaIoPrintWriter *debugPrintWriter_;
}

+ (NSString *)HTML_START;
+ (NSString *)HTML_END;
- (id)init;
- (void)dataNotAvailableWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                             withCCPBVActionPath:(CCPBVActionPath *)path
                                     withBoolean:(BOOL)row
                            withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
                               withNSStringArray:(IOSObjectArray *)columnNames
                                         withInt:(int)column;
- (id<JavaUtilMap>)getQueryParamsWithJavaIoInputStream:(JavaIoInputStream *)stream;
- (id<JavaUtilMap>)getQueryParamsWithJavaNetURL:(JavaNetURL *)url
                          withJavaIoInputStream:(JavaIoInputStream *)stream;
- (void)noDataWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                   withCCPBVActionPath:(CCPBVActionPath *)path
                           withBoolean:(BOOL)row
                  withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)noDocumentWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                       withCCPBVActionPath:(CCPBVActionPath *)path
                      withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (NSString *)getDebugOutput;
- (void)debugLogWithNSString:(NSString *)msg;
- (JavaIoPrintWriter *)getDebugWriter;
- (void)ignoreExceptionWithJavaLangException:(JavaLangException *)e;
- (void)parseParamWithJavaUtilMap:(id<JavaUtilMap>)params
              withRAREUTCharArray:(RAREUTCharArray *)param;
+ (NSString *)getExtensionWithCCPBVActionPath:(CCPBVActionPath *)path;
+ (id<JavaUtilMap>)getQueryParamsWithNSString:(NSString *)queryString;
+ (id<JavaUtilMap>)getQueryParamsWithJavaNetURL:(JavaNetURL *)url;
+ (NSString *)textToHTMLWithNSString:(NSString *)text;
+ (void)toStringWithJavaIoWriter:(JavaIoWriter *)w
                withJavaUtilList:(id<JavaUtilList>)list
                    withNSString:(NSString *)sep;
- (void)copyAllFieldsTo:(CCPBVaRemoteService *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVaRemoteService, debugLogger_, JavaIoStringWriter *)
J2OBJC_FIELD_SETTER(CCPBVaRemoteService, debugPrintWriter_, JavaIoPrintWriter *)

typedef CCPBVaRemoteService ComSparsewareBellavistaServiceARemoteService;

#endif // _CCPBVaRemoteService_H_
