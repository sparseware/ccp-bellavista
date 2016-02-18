//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/util/Users.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVFHIRUsers_H_
#define _CCPBVFHIRUsers_H_

@class CCPBVActionPath;
@class CCPBVHttpHeaders;
@class IOSObjectArray;
@class JavaIoInputStream;
@class JavaIoWriter;
@class RAREUTJSONObject;
@class RAREUTJSONWriter;
@protocol CCPBViHttpConnection;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/external/fhir/aFHIRemoteService.h"

@interface CCPBVFHIRUsers : CCPBVFHIRaFHIRemoteService {
 @public
  BOOL usersSupported_;
}

+ (IOSObjectArray *)info_fields;
+ (void)setInfo_fields:(IOSObjectArray *)info_fields;
- (id)init;
- (void)infoWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                 withCCPBVActionPath:(CCPBVActionPath *)path
               withJavaIoInputStream:(JavaIoInputStream *)data
                withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
+ (RAREUTJSONObject *)populateUserWithNSString:(NSString *)id_
                          withRAREUTJSONObject:(RAREUTJSONObject *)practioner;
- (void)readEntryWithRAREUTJSONObject:(RAREUTJSONObject *)entry_
                 withRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                     withJavaIoWriter:(JavaIoWriter *)w
                    withNSObjectArray:(IOSObjectArray *)params;
- (void)copyAllFieldsTo:(CCPBVFHIRUsers *)other;
@end

typedef CCPBVFHIRUsers ComSparsewareBellavistaExternalFhirUtilUsers;

#endif // _CCPBVFHIRUsers_H_
