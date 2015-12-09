//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aProtocolHandler.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _CCPBVaProtocolHandler_H_
#define _CCPBVaProtocolHandler_H_

@class CCPBVSettings_Server;
@class JavaNetURL;
@class JavaNetURLStreamHandler;
@class RAREActionLink;
@protocol RAREiWidget;

#import "JreEmulation.h"

@interface CCPBVaProtocolHandler : NSObject {
}

- (id)init;
- (RAREActionLink *)createLinkWithRAREiWidget:(id<RAREiWidget>)context
                               withJavaNetURL:(JavaNetURL *)url;
- (RAREActionLink *)createLinkWithRAREiWidget:(id<RAREiWidget>)context
                               withJavaNetURL:(JavaNetURL *)url
                                 withNSString:(NSString *)type;
- (JavaNetURLStreamHandler *)createURLStreamHandlerWithNSString:(NSString *)protocol;
- (BOOL)hasBarCodeSupport;
- (BOOL)hasPatientLocatorSupport;
- (BOOL)hasListSupport;
- (BOOL)hasMyPatiensSupport;
- (void)initialize__WithCCPBVSettings_Server:(CCPBVSettings_Server *)server OBJC_METHOD_FAMILY_NONE;
@end

typedef CCPBVaProtocolHandler ComSparsewareBellavistaExternalAProtocolHandler;

#endif // _CCPBVaProtocolHandler_H_