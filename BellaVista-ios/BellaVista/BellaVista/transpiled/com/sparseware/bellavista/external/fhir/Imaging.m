//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/Imaging.java
//
//  Created by decoteaud on 2/17/16.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/appnativa/util/json/JSONWriter.h"
#include "com/sparseware/bellavista/ActionPath.h"
#include "com/sparseware/bellavista/external/fhir/Imaging.h"
#include "com/sparseware/bellavista/service/HttpHeaders.h"
#include "com/sparseware/bellavista/service/aRemoteService.h"
#include "com/sparseware/bellavista/service/iHttpConnection.h"
#include "java/io/IOException.h"
#include "java/io/InputStream.h"
#include "java/io/Writer.h"

@implementation CCPBVFHIRImaging

- (id)init {
  return [super initWithNSString:@"ImagingStudy"];
}

- (void)imagesWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                   withCCPBVActionPath:(CCPBVActionPath *)path
                 withJavaIoInputStream:(JavaIoInputStream *)data
                  withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self noDataWithCCPBViHttpConnection:conn withCCPBVActionPath:path withBoolean:NO withCCPBVHttpHeaders:headers];
}

- (void)thumbnailsWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                       withCCPBVActionPath:(CCPBVActionPath *)path
                     withJavaIoInputStream:(JavaIoInputStream *)data
                      withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self noDataWithCCPBViHttpConnection:conn withCCPBVActionPath:path withBoolean:NO withCCPBVHttpHeaders:headers];
}

- (void)studyWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                  withCCPBVActionPath:(CCPBVActionPath *)path
                withJavaIoInputStream:(JavaIoInputStream *)data
                 withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self noDocumentWithCCPBViHttpConnection:conn withCCPBVActionPath:path withCCPBVHttpHeaders:headers];
}

- (void)mediaWithCCPBViHttpConnection:(id<CCPBViHttpConnection>)conn
                  withCCPBVActionPath:(CCPBVActionPath *)path
                withJavaIoInputStream:(JavaIoInputStream *)data
                 withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers {
  [self noDocumentWithCCPBViHttpConnection:conn withCCPBVActionPath:path withCCPBVHttpHeaders:headers];
}

- (void)readEntryWithRAREUTJSONObject:(RAREUTJSONObject *)entry_
                 withRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                     withJavaIoWriter:(JavaIoWriter *)w
                    withNSObjectArray:(IOSObjectArray *)params {
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "imagesWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "thumbnailsWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "studyWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "mediaWithCCPBViHttpConnection:withCCPBVActionPath:withJavaIoInputStream:withCCPBVHttpHeaders:", NULL, "V", 0x1, "JavaIoIOException" },
    { "readEntryWithRAREUTJSONObject:withRAREUTJSONWriter:withJavaIoWriter:withNSObjectArray:", NULL, "V", 0x81, "JavaIoIOException" },
  };
  static J2ObjcClassInfo _CCPBVFHIRImaging = { "Imaging", "com.sparseware.bellavista.external.fhir", NULL, 0x1, 5, methods, 0, NULL, 0, NULL};
  return &_CCPBVFHIRImaging;
}

@end
