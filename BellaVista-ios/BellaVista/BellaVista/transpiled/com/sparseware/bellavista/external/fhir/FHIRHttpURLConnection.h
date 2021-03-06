//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/FHIRHttpURLConnection.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVFHIRFHIRHttpURLConnection_H_
#define _CCPBVFHIRFHIRHttpURLConnection_H_

@class CCPBVHttpHeaders;
@class JavaIoInputStream;
@class JavaLangThrowable;
@class JavaNetURL;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/service/aHttpURLConnection.h"

@interface CCPBVFHIRFHIRHttpURLConnection : CCPBVaHttpURLConnection {
}

+ (long long int)lastConnectTime;
+ (long long int *)lastConnectTimeRef;
- (id)initWithJavaNetURL:(JavaNetURL *)u;
- (JavaIoInputStream *)getExceptionInputStreamWithCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
                                             withJavaLangThrowable:(JavaLangThrowable *)e;
- (void)connectToService;
@end

typedef CCPBVFHIRFHIRHttpURLConnection ComSparsewareBellavistaExternalFhirFHIRHttpURLConnection;

#endif // _CCPBVFHIRFHIRHttpURLConnection_H_
