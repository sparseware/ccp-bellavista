//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/DemoBarcodeReader.java
//
//  Created by decoteaud on 3/12/15.
//

#include "IOSClass.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/sparseware/bellavista/DemoBarcodeReader.h"

@implementation ComSparsewareBellavistaDemoBarcodeReader

- (id)init {
  return [super init];
}

- (void)readWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)resultCallback {
  [RAREPlatform invokeLaterWithJavaLangRunnable:[[ComSparsewareBellavistaDemoBarcodeReader_$1 alloc] initWithRAREiFunctionCallback:resultCallback]];
}

- (BOOL)isReaderAvailable {
  return NO;
}

- (void)dispose {
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "isReaderAvailable", NULL, "Z", 0x1, NULL },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaDemoBarcodeReader = { "DemoBarcodeReader", "com.sparseware.bellavista", NULL, 0x1, 1, methods, 0, NULL, 0, NULL};
  return &_ComSparsewareBellavistaDemoBarcodeReader;
}

@end
@implementation ComSparsewareBellavistaDemoBarcodeReader_$1

- (void)run {
  [((id<RAREiFunctionCallback>) nil_chk(val$resultCallback_)) finishedWithBoolean:NO withId:nil];
}

- (id)initWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0 {
  val$resultCallback_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcFieldInfo fields[] = {
    { "val$resultCallback_", NULL, 0x1012, "LRAREiFunctionCallback" },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaDemoBarcodeReader_$1 = { "$1", "com.sparseware.bellavista", "DemoBarcodeReader", 0x8000, 0, NULL, 1, fields, 0, NULL};
  return &_ComSparsewareBellavistaDemoBarcodeReader_$1;
}

@end
