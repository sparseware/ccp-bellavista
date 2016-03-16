//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/util/Lists.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVFHIRLists_H_
#define _CCPBVFHIRLists_H_

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

@interface CCPBVFHIRLists : CCPBVFHIRaFHIRemoteService {
}

- (id)init;
- (void)clinicsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                    withCCPBVActionPath:(CCPBVActionPath *)path
                  withJavaIoInputStream:(JavaIoInputStream *)data
                   withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)my_patient_listsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                             withCCPBVActionPath:(CCPBVActionPath *)path
                           withJavaIoInputStream:(JavaIoInputStream *)data
                            withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)my_teamsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                     withCCPBVActionPath:(CCPBVActionPath *)path
                   withJavaIoInputStream:(JavaIoInputStream *)data
                    withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)relationshipsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                          withCCPBVActionPath:(CCPBVActionPath *)path
                        withJavaIoInputStream:(JavaIoInputStream *)data
                         withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)specialitiesWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                         withCCPBVActionPath:(CCPBVActionPath *)path
                       withJavaIoInputStream:(JavaIoInputStream *)data
                        withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)teamsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                  withCCPBVActionPath:(CCPBVActionPath *)path
                withJavaIoInputStream:(JavaIoInputStream *)data
                 withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)unitsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                  withCCPBVActionPath:(CCPBVActionPath *)path
                withJavaIoInputStream:(JavaIoInputStream *)data
                 withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers;
- (void)readEntryWithRAREUTJSONObject:(RAREUTJSONObject *)entry_
                 withRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                     withJavaIoWriter:(JavaIoWriter *)w
                    withNSObjectArray:(IOSObjectArray *)params;
@end

typedef CCPBVFHIRLists ComSparsewareBellavistaExternalFhirUtilLists;

#endif // _CCPBVFHIRLists_H_
