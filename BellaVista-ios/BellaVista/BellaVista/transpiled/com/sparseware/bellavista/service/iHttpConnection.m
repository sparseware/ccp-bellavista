//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/service/iHttpConnection.java
//
//  Created by decoteaud on 11/18/15.
//

#include "com/sparseware/bellavista/service/ContentWriter.h"
#include "com/sparseware/bellavista/service/iHttpConnection.h"
#include "java/net/URL.h"


@interface CCPBViHttpConnection : NSObject
@end

@implementation CCPBViHttpConnection

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getResquestHeaderWithNSString:", NULL, "LNSString", 0x401, NULL },
    { "getRequestMethod", NULL, "LNSString", 0x401, NULL },
    { "getURL", NULL, "LJavaNetURL", 0x401, NULL },
    { "getContentWriter", NULL, "LCCPBVContentWriter", 0x401, NULL },
  };
  static J2ObjcClassInfo _CCPBViHttpConnection = { "iHttpConnection", "com.sparseware.bellavista.service", NULL, 0x201, 4, methods, 0, NULL, 0, NULL};
  return &_CCPBViHttpConnection;
}

@end