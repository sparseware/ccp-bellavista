//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aBarcodeReader.java
//
//  Created by decoteaud on 5/11/15.
//

#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/iPlatformIcon.h"
#include "com/sparseware/bellavista/external/aBarcodeReader.h"

@implementation CCPBVaBarcodeReader

- (id)init {
  return [super init];
}

- (NSString *)getButtonText {
  return nil;
}

- (id<RAREiPlatformIcon>)getButtonIcon {
  return nil;
}

- (BOOL)isReaderAvailable {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)readWithRAREiFunctionCallback:(id<RAREiFunctionCallback>)resultCallback {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)dispose {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x4, NULL },
    { "getButtonText", NULL, "LNSString", 0x1, NULL },
    { "getButtonIcon", NULL, "LRAREiPlatformIcon", 0x1, NULL },
    { "isReaderAvailable", NULL, "Z", 0x401, NULL },
    { "readWithRAREiFunctionCallback:", NULL, "V", 0x401, NULL },
    { "dispose", NULL, "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _CCPBVaBarcodeReader = { "aBarcodeReader", "com.sparseware.bellavista.external", NULL, 0x401, 6, methods, 0, NULL, 0, NULL};
  return &_CCPBVaBarcodeReader;
}

@end
