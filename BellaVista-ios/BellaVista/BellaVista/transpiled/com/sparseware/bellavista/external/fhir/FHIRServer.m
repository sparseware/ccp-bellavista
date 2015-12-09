//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/FHIRServer.java
//
//  Created by decoteaud on 11/18/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/rare/widget/iWidget.h"
#include "com/appnativa/util/ObjectHolder.h"
#include "com/appnativa/util/Streams.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/appnativa/util/json/JSONTokener.h"
#include "com/appnativa/util/json/JSONWriter.h"
#include "com/sparseware/bellavista/Settings.h"
#include "com/sparseware/bellavista/external/aProtocolHandler.h"
#include "com/sparseware/bellavista/external/fhir/FHIRJSONWatcher.h"
#include "com/sparseware/bellavista/external/fhir/FHIRServer.h"
#include "com/sparseware/bellavista/external/fhir/Orders.h"
#include "com/sparseware/bellavista/external/fhir/aFHIRemoteService.h"
#include "com/sparseware/bellavista/external/fhir/util/Patients.h"
#include "com/sparseware/bellavista/service/HttpHeaders.h"
#include "java/io/File.h"
#include "java/io/FileReader.h"
#include "java/io/FileWriter.h"
#include "java/io/IOException.h"
#include "java/io/PrintStream.h"
#include "java/io/Reader.h"
#include "java/io/StringReader.h"
#include "java/io/StringWriter.h"
#include "java/lang/Boolean.h"
#include "java/lang/Exception.h"
#include "java/lang/Package.h"
#include "java/lang/System.h"
#include "java/net/URL.h"
#include "java/net/URLConnection.h"
#include "java/net/URLStreamHandler.h"
#include "java/util/ArrayList.h"
#include "java/util/HashMap.h"
#include "java/util/Map.h"

@implementation ComSparsewareBellavistaExternalFhirFHIRServer

static ComSparsewareBellavistaExternalFhirFHIRServer * ComSparsewareBellavistaExternalFhirFHIRServer__instance_;

+ (ComSparsewareBellavistaExternalFhirFHIRServer *)_instance {
  return ComSparsewareBellavistaExternalFhirFHIRServer__instance_;
}

+ (void)set_instance:(ComSparsewareBellavistaExternalFhirFHIRServer *)_instance {
  ComSparsewareBellavistaExternalFhirFHIRServer__instance_ = _instance;
}

- (id)init {
  if (self = [super init]) {
    demoMode_ = YES;
    ComSparsewareBellavistaExternalFhirFHIRServer__instance_ = self;
  }
  return self;
}

- (id)initWithNSString:(NSString *)endPoint {
  if (self = [super init]) {
    demoMode_ = YES;
    ComSparsewareBellavistaExternalFhirFHIRServer__instance_ = self;
    self->endPoint_ = endPoint;
  }
  return self;
}

- (RAREActionLink *)createLinkWithRAREiWidget:(id<RAREiWidget>)context
                               withJavaNetURL:(JavaNetURL *)url {
  RAREActionLink *l = [super createLinkWithRAREiWidget:context withJavaNetURL:url];
  [((RAREActionLink *) nil_chk(l)) setRequestHeaderWithNSString:@"Accept" withNSString:@"application/json+fhir"];
  return l;
}

- (RAREActionLink *)createLinkWithRAREiWidget:(id<RAREiWidget>)context
                               withJavaNetURL:(JavaNetURL *)url
                                 withNSString:(NSString *)type {
  RAREActionLink *l = [super createLinkWithRAREiWidget:context withJavaNetURL:url withNSString:type];
  [((RAREActionLink *) nil_chk(l)) setRequestHeaderWithNSString:@"Accept" withNSString:@"application/json+fhir"];
  return l;
}

- (RAREActionLink *)createLinkWithJavaNetURL:(JavaNetURL *)url {
  RAREActionLink *l = [super createLinkWithRAREiWidget:nil withJavaNetURL:url];
  [((RAREActionLink *) nil_chk(l)) setRequestHeaderWithNSString:@"Accept" withNSString:@"application/json+fhir"];
  return l;
}

- (RAREActionLink *)createLinkWithJavaNetURL:(JavaNetURL *)url
                                withNSString:(NSString *)type {
  RAREActionLink *l = [super createLinkWithRAREiWidget:nil withJavaNetURL:url withNSString:type];
  [((RAREActionLink *) nil_chk(l)) setRequestHeaderWithNSString:@"Accept" withNSString:@"application/json+fhir"];
  return l;
}

- (RAREActionLink *)createJSONLinkWithNSString:(NSString *)url {
  RAREActionLink *l = [[RAREActionLink alloc] initWithNSString:url];
  [l setRequestHeaderWithNSString:@"Accept" withNSString:@"application/json+fhir"];
  return l;
}

- (RAREActionLink *)createLinkWithNSString:(NSString *)object
                           withJavaUtilMap:(id<JavaUtilMap>)params {
  RAREActionLink *l = [[RAREActionLink alloc] initWithNSString:[NSString stringWithFormat:@"%@/%@", endPoint_, object]];
  [l setRequestHeaderWithNSString:@"Accept" withNSString:@"application/json+fhir"];
  if (params != nil) {
    [l setAttributesWithJavaUtilMap:params];
  }
  return l;
}

- (RAREActionLink *)createLinkWithNSString:(NSString *)object
                         withNSStringArray:(IOSObjectArray *)params {
  RAREActionLink *l = [[RAREActionLink alloc] initWithNSString:[NSString stringWithFormat:@"%@/%@", endPoint_, object]];
  [l setRequestHeaderWithNSString:@"Accept" withNSString:@"application/json+fhir"];
  if ((params != nil) && ((int) [params count] > 0)) {
    int len = (int) [params count];
    JavaUtilHashMap *map = [[JavaUtilHashMap alloc] initWithInt:len / 2];
    int i = 0;
    while (i < len) {
      (void) [map putWithId:IOSObjectArray_Get(params, i++) withId:IOSObjectArray_Get(params, i++)];
    }
    [l setAttributesWithJavaUtilMap:map];
  }
  return l;
}

- (JavaNetURLStreamHandler *)createURLStreamHandlerWithNSString:(NSString *)protocol {
  if (streamHandler_ == nil) {
    streamHandler_ = [[ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler alloc] init];
  }
  return streamHandler_;
}

- (RAREUTJSONObject *)getDataWithNSString:(NSString *)object
                          withJavaUtilMap:(id<JavaUtilMap>)params {
  RAREActionLink *l = nil;
  @try {
    return [[RAREUTJSONObject alloc] initWithRAREUTJSONTokener:[[RAREUTJSONTokener alloc] initWithJavaIoReader:[(l = [self createLinkWithNSString:object withJavaUtilMap:params]) getReader]]];
  }
  @finally {
    if (l != nil) {
      [l close];
    }
  }
}

- (RAREUTJSONObject *)getDataWithNSString:(NSString *)object
                        withNSStringArray:(IOSObjectArray *)params {
  RAREActionLink *l = nil;
  @try {
    return [[RAREUTJSONObject alloc] initWithRAREUTJSONTokener:[[RAREUTJSONTokener alloc] initWithJavaIoReader:[(l = [self createLinkWithNSString:object withNSStringArray:params]) getReader]]];
  }
  @finally {
    if (l != nil) {
      [l close];
    }
  }
}

- (void)initialize__WithCCPBVSettings_Server:(CCPBVSettings_Server *)server {
  self->demoMode_ = [((CCPBVSettings_Server *) nil_chk(server)) isDemoServer];
  NSString *serverURL = server->serverURL_;
  serverURL = @"https://fhir.sandboxcernerpowerchart.com/may2015/open/d075cf8b-3261-481d-97e5-ba6c48d3b41f";
  NSString *ep = [NSString stringWithFormat:@"http%@", [serverURL substring:4]];
  if ((endPoint_ == nil) || ![endPoint_ isEqual:ep]) {
    self->endPoint_ = ep;
    [self loadMetadata];
    RAREUTJSONObject *o = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"patientSelectInfo"], [RAREUTJSONObject class]);
    (void) [((RAREUTJSONObject *) nil_chk(o)) putWithNSString:@"hasPatientAlerts" withBoolean:[((ComSparsewareBellavistaExternalFhirFHIRServer *) nil_chk([ComSparsewareBellavistaExternalFhirFHIRServer getInstance])) getResourceWithNSString:@"Appointment"] != nil];
    (void) [o putWithNSString:@"hasPatientFlags" withBoolean:[self getResourceWithNSString:@"Flag"] != nil];
    if (![self hasPatientLocatorSupport]) {
      (void) [o putWithNSString:@"patientLocatorClass" withId:@""];
    }
    if (![self hasBarCodeSupport]) {
      (void) [o putWithNSString:@"barcodeReaderClass" withId:@""];
    }
    (void) [o putWithNSString:@"alwaysShowSearchFirst" withBoolean:YES];
    RAREUTJSONArray *a = [o getJSONArrayWithNSString:@"listCategories"];
    int len = [((RAREUTJSONArray *) nil_chk(a)) length];
    for (int i = len - 1; i >= 0; i--) {
      o = [a getJSONObjectWithInt:i];
      if ([nil_chk([((RAREUTJSONObject *) nil_chk(o)) optWithNSString:@"type"]) isEqual:@"category"]) {
        NSString *s = [o getStringWithNSString:@"listPath"];
        if ([((NSString *) nil_chk(s)) isEqual:@"teams"] || [s isEqual:@"specialities"]) {
          (void) [a removeWithInt:i];
        }
        else {
          (void) [o putWithNSString:@"enabled" withBoolean:NO];
        }
      }
    }
    RAREUTJSONObject *li = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"labsInfo"], [RAREUTJSONObject class]);
    (void) [((RAREUTJSONObject *) nil_chk(li)) putWithNSString:@"bun_id" withId:@"bun"];
    (void) [li putWithNSString:@"creatinine_id" withId:@"creat"];
    (void) [li putWithNSString:@"showUnits" withBoolean:YES];
    (void) [li putWithNSString:@"documentsInlined" withBoolean:YES];
    li = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"vitalsInfo"], [RAREUTJSONObject class]);
    (void) [((RAREUTJSONObject *) nil_chk(li)) putWithNSString:@"showUnits" withBoolean:YES];
    (void) [li putWithNSString:@"documentsInlined" withBoolean:YES];
    li = (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"tabsInfo"], [RAREUTJSONObject class]);
    (void) [((RAREUTJSONObject *) nil_chk(li)) putWithNSString:@"hasConsults" withBoolean:[self getResourceWithNSString:@"Procedure"] != nil];
    (void) [li putWithNSString:@"hasNotes" withBoolean:[self getResourceWithNSString:@"Composition"] != nil];
    a = [((RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:@"collectionsInfo"], [RAREUTJSONObject class])) optJSONArrayWithNSString:@"collections"];
    len = a == nil ? 0 : [a length];
    for (int i = 0; i < len; i++) {
      o = [((RAREUTJSONArray *) nil_chk(a)) getJSONObjectWithInt:i];
      (void) [((RAREUTJSONObject *) nil_chk(o)) putWithNSString:@"autoUpdate" withBoolean:NO];
    }
  }
}

- (void)loadMetadata {
  NSString *data = nil;
  RAREUTObjectHolder *oh = [[RAREUTObjectHolder alloc] initWithRAREUTObjectHolder:nil];
  @try {
    NSString *name = [RAREFunctions sha1WithNSString:endPoint_];
    int n = [((NSString *) nil_chk(name)) indexOf:'='];
    if (n != -1) {
      name = [name substring:0 endIndex:n];
    }
    name = [((NSString *) nil_chk(name)) replace:'+' withChar:'-'];
    name = [((NSString *) nil_chk(name)) replace:'/' withChar:'_'];
    JavaIoFile *f = [RAREPlatform createCacheFileWithNSString:name];
    if ([((JavaIoFile *) nil_chk(f)) exists]) {
      long long int lm = [f lastModified];
      if ((lm + (1000 * 60 * 500)) > [JavaLangSystem currentTimeMillis]) {
        JavaIoFileReader *r = [[JavaIoFileReader alloc] initWithJavaIoFile:f];
        data = [RAREUTStreams readerToStringWithJavaIoReader:r];
        [r close];
      }
    }
    if (data == nil) {
      RAREActionLink *l = [self createLinkWithNSString:@"metadata" withNSStringArray:[IOSObjectArray arrayWithLength:0 type:[IOSClass classWithClass:[NSString class]]]];
      data = [((RAREActionLink *) nil_chk(l)) getContentAsString];
      JavaIoFileWriter *w = [[JavaIoFileWriter alloc] initWithJavaIoFile:f];
      [w writeWithNSString:data];
      [w close];
    }
  }
  @catch (JavaLangException *e) {
    [RAREPlatform ignoreExceptionWithNSString:nil withJavaLangThrowable:e];
  }
  if (data == nil) {
    RAREActionLink *l = [self createLinkWithNSString:@"metadata" withNSStringArray:[IOSObjectArray arrayWithLength:0 type:[IOSClass classWithClass:[NSString class]]]];
    data = [((RAREActionLink *) nil_chk(l)) getContentAsString];
  }
  resources_ = [[JavaUtilHashMap alloc] init];
  RAREUTJSONTokener *t = [[RAREUTJSONTokener alloc] initWithJavaIoReader:[[JavaIoStringReader alloc] initWithNSString:data]];
  [t setWatcherWithRAREUTJSONTokener_iWatcher:[[ComSparsewareBellavistaExternalFhirFHIRJSONWatcher alloc] initWithComSparsewareBellavistaExternalFhirFHIRJSONWatcher_iCallback:[[ComSparsewareBellavistaExternalFhirFHIRServer_$1 alloc] initWithComSparsewareBellavistaExternalFhirFHIRServer:self withRAREUTObjectHolder:oh withRAREUTJSONTokener:t] withNSString:@"rest/resource"]];
  RAREUTJSONObject *o = [[RAREUTJSONObject alloc] initWithRAREUTJSONTokener:t];
  if (oh->value_ != nil) {
    @throw (JavaLangException *) check_class_cast(oh->value_, [JavaLangException class]);
  }
  [t dispose];
}

- (NSString *)description {
  return [NSString stringWithFormat:@"%@ (DSTU%d)", publisher_, version__];
}

- (NSString *)getIDWithNSString:(NSString *)id_
                    withBoolean:(BOOL)includePath {
  if ([((NSString *) nil_chk(id_)) hasPrefix:endPoint_] && includePath) {
    return [id_ substring:[((NSString *) nil_chk(endPoint_)) sequenceLength] + 1];
  }
  if (!includePath) {
    int n = [id_ lastIndexOf:'/'];
    if (n != -1) {
      id_ = [id_ substring:n];
    }
  }
  return id_;
}

+ (ComSparsewareBellavistaExternalFhirFHIRServer *)getInstance {
  return ComSparsewareBellavistaExternalFhirFHIRServer__instance_;
}

- (void)setPatientInfoWithNSString:(NSString *)id_
                      withNSString:(NSString *)encounter
             withJavaUtilArrayList:(JavaUtilArrayList *)team {
  self->patientID_ = id_;
  self->careTeam_ = team;
}

- (JavaUtilArrayList *)getCareTeam {
  return careTeam_;
}

- (NSString *)getPatientID {
  if (patientID_ == nil) {
    @throw [[JavaIoIOException alloc] initWithNSString:@"NO PATIENT SELECTED"];
  }
  return patientID_;
}

- (NSString *)getPatientIDEx {
  return patientID_;
}

- (ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource *)getResourceWithNSString:(NSString *)name {
  return (resources_ == nil) ? nil : [resources_ getWithId:name];
}

- (BOOL)isDemoMode {
  return demoMode_;
}

+ (RAREUTJSONObject *)getConfigurationDataWithNSString:(NSString *)name {
  return (RAREUTJSONObject *) check_class_cast([((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) getDataWithId:name], [RAREUTJSONObject class]);
}

+ (void)mainWithNSStringArray:(IOSObjectArray *)args {
  ComSparsewareBellavistaExternalFhirFHIRServer *server = [[ComSparsewareBellavistaExternalFhirFHIRServer alloc] initWithNSString:@"https://fhir.sandboxcernerpowerchart.com/may2015/open/d075cf8b-3261-481d-97e5-ba6c48d3b41f"];
  ComSparsewareBellavistaExternalFhiraFHIRemoteService *p = [[ComSparsewareBellavistaExternalFhirOrders alloc] init];
  JavaIoStringWriter *sw = [[JavaIoStringWriter alloc] init];
  @try {
    JavaIoFileReader *r = [[JavaIoFileReader alloc] initWithNSString:@"/Code/tmp/meds.json"];
    RAREUTJSONWriter *jw = [[RAREUTJSONWriter alloc] initWithJavaIoWriter:sw];
    [p searchWithJavaIoReader:r withId:jw withCCPBVHttpHeaders:[[CCPBVHttpHeaders alloc] init] withNSObjectArray:[IOSObjectArray arrayWithLength:0 type:[IOSClass classWithClass:[NSObject class]]]];
    NSString *s = [sw description];
    [((JavaIoPrintStream *) nil_chk([JavaLangSystem out])) printlnWithNSString:[((RAREUTJSONObject *) [[RAREUTJSONObject alloc] initWithNSString:s]) toStringWithInt:2]];
  }
  @catch (JavaLangException *e) {
    [((JavaLangException *) nil_chk(e)) printStackTrace];
  }
}

- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirFHIRServer *)other {
  [super copyAllFieldsTo:other];
  other->careTeam_ = careTeam_;
  other->demoMode_ = demoMode_;
  other->endPoint_ = endPoint_;
  other->patientID_ = patientID_;
  other->publisher_ = publisher_;
  other->resources_ = resources_;
  other->streamHandler_ = streamHandler_;
  other->version__ = version__;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithNSString:", NULL, NULL, 0x0, NULL },
    { "createLinkWithRAREiWidget:withJavaNetURL:", NULL, "LRAREActionLink", 0x1, NULL },
    { "createLinkWithRAREiWidget:withJavaNetURL:withNSString:", NULL, "LRAREActionLink", 0x1, NULL },
    { "createLinkWithJavaNetURL:", NULL, "LRAREActionLink", 0x1, NULL },
    { "createLinkWithJavaNetURL:withNSString:", NULL, "LRAREActionLink", 0x1, NULL },
    { "createJSONLinkWithNSString:", NULL, "LRAREActionLink", 0x1, NULL },
    { "createLinkWithNSString:withJavaUtilMap:", NULL, "LRAREActionLink", 0x1, NULL },
    { "createLinkWithNSString:withNSStringArray:", NULL, "LRAREActionLink", 0x81, NULL },
    { "createURLStreamHandlerWithNSString:", NULL, "LJavaNetURLStreamHandler", 0x1, NULL },
    { "getDataWithNSString:withJavaUtilMap:", NULL, "LRAREUTJSONObject", 0x1, "JavaLangException" },
    { "getDataWithNSString:withNSStringArray:", NULL, "LRAREUTJSONObject", 0x81, "JavaLangException" },
    { "initialize__WithCCPBVSettings_Server:", NULL, "V", 0x1, "JavaLangException" },
    { "loadMetadata", NULL, "V", 0x2, "JavaLangException" },
    { "getIDWithNSString:withBoolean:", NULL, "LNSString", 0x1, NULL },
    { "getInstance", NULL, "LComSparsewareBellavistaExternalFhirFHIRServer", 0x9, NULL },
    { "getCareTeam", NULL, "LJavaUtilArrayList", 0x1, NULL },
    { "getPatientID", NULL, "LNSString", 0x1, "JavaIoIOException" },
    { "getPatientIDEx", NULL, "LNSString", 0x1, NULL },
    { "getResourceWithNSString:", NULL, "LComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource", 0x1, NULL },
    { "isDemoMode", NULL, "Z", 0x1, NULL },
    { "getConfigurationDataWithNSString:", NULL, "LRAREUTJSONObject", 0x9, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "version__", "version", 0x1, "I" },
    { "endPoint_", NULL, 0x1, "LNSString" },
    { "publisher_", NULL, 0x1, "LNSString" },
    { "_instance_", NULL, 0xa, "LComSparsewareBellavistaExternalFhirFHIRServer" },
    { "streamHandler_", NULL, 0x0, "LComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirFHIRServer = { "FHIRServer", "com.sparseware.bellavista.external.fhir", NULL, 0x1, 21, methods, 5, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirFHIRServer;
}

@end
@implementation ComSparsewareBellavistaExternalFhirFHIRServer_FHIRHttpUrlConnection

- (id)initWithJavaNetURL:(JavaNetURL *)u {
  return [super initWithJavaNetURL:u withNSString:[((JavaLangPackage *) nil_chk([[IOSClass classWithClass:[ComSparsewareBellavistaExternalFhirFHIRServer class]] getPackage])) getName]];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirFHIRServer_FHIRHttpUrlConnection = { "FHIRHttpUrlConnection", "com.sparseware.bellavista.external.fhir", "FHIRServer", 0x8, 0, NULL, 0, NULL, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirFHIRServer_FHIRHttpUrlConnection;
}

@end
@implementation ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler

- (JavaNetURLConnection *)openConnectionWithJavaNetURL:(JavaNetURL *)u {
  return [[ComSparsewareBellavistaExternalFhirFHIRServer_FHIRHttpUrlConnection alloc] initWithJavaNetURL:u];
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "openConnectionWithJavaNetURL:", NULL, "LJavaNetURLConnection", 0x4, "JavaIoIOException" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler = { "StreamHandler", "com.sparseware.bellavista.external.fhir", "FHIRServer", 0x8, 1, methods, 0, NULL, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirFHIRServer_StreamHandler;
}

@end
@implementation ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource

- (id)initWithNSString:(NSString *)name
  withRAREUTJSONObject:(RAREUTJSONObject *)entry_ {
  if (self = [super init]) {
    self->name_ = name;
    searchParams_ = [((RAREUTJSONObject *) nil_chk(entry_)) optJSONArrayWithNSString:@"searchParam"];
    int len = (searchParams_ == nil) ? 0 : [searchParams_ length];
    for (int i = 0; i < len; i++) {
      RAREUTJSONObject *o = [((RAREUTJSONArray *) nil_chk(searchParams_)) getJSONObjectWithInt:i];
      (void) [((RAREUTJSONObject *) nil_chk(o)) removeWithNSString:@"target"];
      (void) [o removeWithNSString:@"definition"];
      (void) [o removeWithNSString:@"documentation"];
    }
    interaction_ = [entry_ optJSONArrayWithNSString:@"interaction"];
    if ([((NSString *) nil_chk(name)) isEqual:@"Patient"]) {
      [ComSparsewareBellavistaExternalFhirUtilPatients initializeSupportedOptionsWithComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource:self withRAREUTJSONObject:[ComSparsewareBellavistaExternalFhirFHIRServer getConfigurationDataWithNSString:@"patientSelectInfo"]];
    }
  }
  return self;
}

- (NSString *)getName {
  return name_;
}

- (BOOL)canSearch {
  if (search_ == nil) {
    search_ = [JavaLangBoolean valueOfWithBoolean:[self canDoWithNSString:@"search-type"]];
  }
  return [((JavaLangBoolean *) nil_chk(search_)) booleanValue];
}

- (BOOL)canRead {
  if (read_ == nil) {
    search_ = [JavaLangBoolean valueOfWithBoolean:[self canDoWithNSString:@"read-type"]];
  }
  return [((JavaLangBoolean *) nil_chk(read_)) booleanValue];
}

- (BOOL)hasSearchParamWithNSString:(NSString *)name {
  return searchParams_ == nil ? NO : [searchParams_ findJSONObjectWithNSString:@"name" withNSString:name] != nil;
}

- (BOOL)canDoWithNSString:(NSString *)text {
  if (interaction_ == nil) {
    return NO;
  }
  return [((RAREUTJSONArray *) nil_chk(interaction_)) findJSONObjectIndexWithNSString:@"code" withNSString:text withInt:0] != -1;
}

- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource *)other {
  [super copyAllFieldsTo:other];
  other->interaction_ = interaction_;
  other->name_ = name_;
  other->read_ = read_;
  other->search_ = search_;
  other->searchParams_ = searchParams_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithNSString:withRAREUTJSONObject:", NULL, NULL, 0x0, NULL },
    { "getName", NULL, "LNSString", 0x1, NULL },
    { "canSearch", NULL, "Z", 0x1, NULL },
    { "canRead", NULL, "Z", 0x1, NULL },
    { "hasSearchParamWithNSString:", NULL, "Z", 0x1, NULL },
    { "canDoWithNSString:", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "search_", NULL, 0x0, "LJavaLangBoolean" },
    { "read_", NULL, 0x0, "LJavaLangBoolean" },
    { "searchParams_", NULL, 0x1, "LRAREUTJSONArray" },
    { "interaction_", NULL, 0x1, "LRAREUTJSONArray" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource = { "FHIRResource", "com.sparseware.bellavista.external.fhir", "FHIRServer", 0x9, 6, methods, 4, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource;
}

@end
@implementation ComSparsewareBellavistaExternalFhirFHIRServer_$1

- (id)entryEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *)entry_ {
  NSString *type = [((RAREUTJSONObject *) nil_chk(entry_)) optStringWithNSString:@"type" withNSString:nil];
  if (type != nil) {
    (void) [((JavaUtilHashMap *) nil_chk(this$0_->resources_)) putWithId:type withId:[[ComSparsewareBellavistaExternalFhirFHIRServer_FHIRResource alloc] initWithNSString:type withRAREUTJSONObject:entry_]];
  }
  return nil;
}

- (BOOL)parseArrayWithNSString:(NSString *)arrayName {
  if ([((NSString *) nil_chk(arrayName)) isEqual:@"format"]) {
    return YES;
  }
  return YES;
}

- (id)otherArrayElementEncounteredWithNSString:(NSString *)arrayName
                                        withId:(id)value {
  return value;
}

- (id)valueEncounteredWithRAREUTJSONObject:(RAREUTJSONObject *)parent
                              withNSString:(NSString *)key
                                    withId:(id)value {
  if ([((RAREUTJSONObject *) nil_chk(parent)) getName] == nil) {
    NSString *s;
    if ([((NSString *) nil_chk(key)) isEqual:@"fhirVersion"] || [key isEqual:@"version"]) {
      s = (NSString *) check_class_cast(value, [NSString class]);
      if ([((NSString *) nil_chk(s)) hasPrefix:@"0.5"]) {
        this$0_->version__ = 2;
      }
      else {
        ((RAREUTObjectHolder *) nil_chk(val$oh_))->value_ = [[JavaIoIOException alloc] initWithNSString:[NSString stringWithFormat:@"Unsupported FHIR version: %@", s]];
        [((RAREUTJSONTokener *) nil_chk(val$t_)) setTerminateParsingWithBoolean:YES];
      }
    }
    else if ([key isEqual:@"publisher"]) {
      this$0_->publisher_ = (NSString *) check_class_cast(value, [NSString class]);
    }
    else if ([key isEqual:@"format"]) {
      RAREUTJSONArray *a = (RAREUTJSONArray *) check_class_cast(value, [RAREUTJSONArray class]);
      int len = [((RAREUTJSONArray *) nil_chk(a)) length];
      int i;
      BOOL json = NO;
      for (i = 0; i < len; i++) {
        if ([((NSString *) nil_chk([a getStringWithInt:i])) contains:@"json"]) {
          json = YES;
          break;
        }
      }
      if (!json) {
        ((RAREUTObjectHolder *) nil_chk(val$oh_))->value_ = [[JavaIoIOException alloc] initWithNSString:@"Server does not support JSON"];
        [((RAREUTJSONTokener *) nil_chk(val$t_)) setTerminateParsingWithBoolean:YES];
      }
    }
  }
  return nil;
}

- (id)initWithComSparsewareBellavistaExternalFhirFHIRServer:(ComSparsewareBellavistaExternalFhirFHIRServer *)outer$
                                     withRAREUTObjectHolder:(RAREUTObjectHolder *)capture$0
                                      withRAREUTJSONTokener:(RAREUTJSONTokener *)capture$1 {
  this$0_ = outer$;
  val$oh_ = capture$0;
  val$t_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "entryEncounteredWithRAREUTJSONObject:", NULL, "LNSObject", 0x1, NULL },
    { "parseArrayWithNSString:", NULL, "Z", 0x1, NULL },
    { "otherArrayElementEncounteredWithNSString:withId:", NULL, "LNSObject", 0x1, NULL },
    { "valueEncounteredWithRAREUTJSONObject:withNSString:withId:", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LComSparsewareBellavistaExternalFhirFHIRServer" },
    { "val$oh_", NULL, 0x1012, "LRAREUTObjectHolder" },
    { "val$t_", NULL, 0x1012, "LRAREUTJSONTokener" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalFhirFHIRServer_$1 = { "$1", "com.sparseware.bellavista.external.fhir", "FHIRServer", 0x8000, 4, methods, 3, fields, 0, NULL};
  return &_ComSparsewareBellavistaExternalFhirFHIRServer_$1;
}

@end
