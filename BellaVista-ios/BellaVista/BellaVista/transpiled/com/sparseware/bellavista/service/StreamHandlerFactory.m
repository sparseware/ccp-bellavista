//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/StreamHandlerFactory.java
//
//  Created by decoteaud on 2/17/16.
//

#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/service/StreamHandlerFactory.h"
#include "java/net/URLStreamHandler.h"

@implementation CCPBVStreamHandlerFactory

- (id)init {
  return [super init];
}

- (JavaNetURLStreamHandler *)createURLStreamHandlerWithNSString:(NSString *)protocol {
  return [CCPBVUtils createURLStreamHandlerWithNSString:protocol];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "createURLStreamHandlerWithNSString:", NULL, "LJavaNetURLStreamHandler", 0x1, NULL },
  };
  static J2ObjcClassInfo _CCPBVStreamHandlerFactory = { "StreamHandlerFactory", "com.sparseware.bellavista.service", NULL, 0x1, 1, methods, 0, NULL, 0, NULL};
  return &_CCPBVStreamHandlerFactory;
}

@end
