//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/FHIRServer.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _ComSparsewareBellavistaExternalFhirFHIRServer_H_
#define _ComSparsewareBellavistaExternalFhirFHIRServer_H_

@class CCPBVSettings_Server;
@class ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource;
@class ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler;
@class IOSObjectArray;
@class JavaLangBoolean;
@class JavaNetURL;
@class JavaNetURLConnection;
@class JavaUtilArrayList;
@class JavaUtilHashMap;
@class RAREActionLink;
@class RAREUTJSONArray;
@class RAREUTJSONObject;
@class RAREUTJSONTokener;
@class RAREUTObjectHolder;
@protocol JavaUtilMap;
@protocol RAREiWidget;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/external/aProtocolHandler.h"
#include "com/sparseware/bellavista/external/fhir/FHIRJSONWatcher.h"
#include "com/sparseware/bellavista/service/aHttpURLConnection.h"
#include "java/net/URLStreamHandler.h"

@interface ComSparsewareBellavistaExternalFhirFHIRServer : CCPBVaProtocolHandler {
 @public
  int version__;
  NSString *endPoint_;
  NSString *publisher_;
  ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler *streamHandler_;
  NSString *patientID_;
  JavaUtilArrayList *careTeam_;
  JavaUtilHashMap *resources_;
  BOOL demoMode_;
}

+ (ComSparsewareBellavistaExternalFhirFHIRServer *)_instance;
+ (void)set_instance:(ComSparsewareBellavistaExternalFhirFHIRServer *)_instance;
- (id)init;
- (id)initWithNSString:(NSString *)endPoint;
- (RAREActionLink *)createLinkWithRAREiWidget:(id<RAREiWidget>)context
                               withJavaNetURL:(JavaNetURL *)url;
- (RAREActionLink *)createLinkWithRAREiWidget:(id<RAREiWidget>)context
                               withJavaNetURL:(JavaNetURL *)url
                                 withNSString:(NSString *)type;
- (RAREActionLink *)createLinkWithJavaNetURL:(JavaNetURL *)url;
- (RAREActionLink *)createLinkWithJavaNetURL:(JavaNetURL *)url
                                withNSString:(NSString *)type;
- (RAREActionLink *)createJSONLinkWithNSString:(NSString *)url;
- (RAREActionLink *)createLinkWithNSString:(NSString *)object
                           withJavaUtilMap:(id<JavaUtilMap>)params;
- (RAREActionLink *)createLinkWithNSString:(NSString *)object
                         withNSStringArray:(IOSObjectArray *)params;
- (JavaNetURLStreamHandler *)createURLStreamHandlerWithNSString:(NSString *)protocol;
- (RAREUTJSONObject *)getDataWithNSString:(NSString *)object
                          withJavaUtilMap:(id<JavaUtilMap>)params;
- (RAREUTJSONObject *)getDataWithNSString:(NSString *)object
                        withNSStringArray:(IOSObjectArray *)params;
- (void)initialize__WithCCPBVSettings_Server:(CCPBVSettings_Server *)server OBJC_METHOD_FAMILY_NONE;
- (void)loadMetadata;
- (NSString *)description;
- (NSString *)getIDWithNSString:(NSString *)id_
                    withBoolean:(BOOL)includePath;
+ (ComSparsewareBellavistaExternalFhirFHIRServer *)getInstance;
- (void)setPatientInfoWithNSString:(NSString *)id_
                      withNSString:(NSString *)encounter
             withJavaUtilArrayList:(JavaUtilArrayList *)team;
- (JavaUtilArrayList *)getCareTeam;
- (NSString *)getPatientID;
- (NSString *)getPatientIDEx;
- (ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource *)getResourceWithNSString:(NSString *)name;
- (BOOL)isDemoMode;
+ (RAREUTJSONObject *)getConfigurationDataWithNSString:(NSString *)name;
+ (void)mainWithNSStringArray:(IOSObjectArray *)args;
- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirFHIRServer *)other;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer, endPoint_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer, publisher_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer, streamHandler_, ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer, patientID_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer, careTeam_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer, resources_, JavaUtilHashMap *)

@interface ComSparsewareBellavistaExternalFhirFHIRServer_FHIRHttpUrlConnection : CCPBVaHttpURLConnection {
}

- (id)initWithJavaNetURL:(JavaNetURL *)u;
@end

@interface ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler : JavaNetURLStreamHandler {
}

- (JavaNetURLConnection *)openConnectionWithJavaNetURL:(JavaNetURL *)u;
- (id)init;
@end

@interface ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource : NSObject {
 @public
  JavaLangBoolean *search_;
  JavaLangBoolean *read_;
  RAREUTJSONArray *searchParams_;
  RAREUTJSONArray *interaction_;
  NSString *name_;
}

- (id)initWithNSString:(NSString *)name
  withRAREUTJSONObject:(RAREUTJSONObject *)entry_;
- (NSString *)getName;
- (BOOL)canSearch;
- (BOOL)canRead;
- (BOOL)hasSearchParamWithNSString:(NSString *)name;
- (BOOL)canDoWithNSString:(NSString *)text;
- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource *)other;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource, search_, JavaLangBoolean *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource, read_, JavaLangBoolean *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource, searchParams_, RAREUTJSONArray *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource, interaction_, RAREUTJSONArray *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource, name_, NSString *)

@interface ComSparsewareBellavistaExternalFhirFHIRServer_$1 : ComSparsewareBellavistaExternalFhirFHIRJSONWatcher_aCallback {
 @public
  ComSparsewareBellavistaExternalFhirFHIRServer *this$0_;
  RAREUTObjectHolder *val$oh_;
  RAREUTJSONTokener *val$t_;
}

- (id)entryEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *)entry_;
- (BOOL)parseArrayWithNSString:(NSString *)arrayName;
- (id)otherArrayElementEncounteredWithNSString:(NSString *)arrayName
                                        withId:(id)value;
- (id)valueEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *)parent
                              withNSString:(NSString *)key
                                    withId:(id)value;
- (id)initWithComSparsewareBellavistaExternalFhirFHIRServer:(ComSparsewareBellavistaExternalFhirFHIRServer *)outer$
                                     withRAREUTObjectHolder:(RAREUTObjectHolder *)capture$0
                                      withRAREUTJSONTokener:(RAREUTJSONTokener *)capture$1;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_$1, this$0_, ComSparsewareBellavistaExternalFhirFHIRServer *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_$1, val$oh_, RAREUTObjectHolder *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRServer_$1, val$t_, RAREUTJSONTokener *)

#endif // _ComSparsewareBellavistaExternalFhirFHIRServer_H_
