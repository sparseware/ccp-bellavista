//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/BluetoothBeaconLocator.java
//
//  Created by decoteaud on 5/11/15.
//

#include "com/sparseware/bellavista/external/BluetoothBeaconLocator.h"
#include "com/sparseware/bellavista/external/BluetoothBeaconLocatorSupport.h"
#include "com/sparseware/bellavista/external/aBeaconLocatorSupport.h"
#include "java/util/regex/Pattern.h"

@implementation CCPBVBluetoothBeaconLocator

- (id)init {
  return [super init];
}

- (CCPBVaBeaconLocatorSupport *)createBeaconSupport {
  return [[CCPBVBluetoothBeaconLocatorSupport alloc] init];
}

- (JavaUtilRegexPattern *)createIDPattern {
  return [JavaUtilRegexPattern compileWithNSString:@"^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$" withInt:JavaUtilRegexPattern_CASE_INSENSITIVE];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "createBeaconSupport", NULL, "LCCPBVaBeaconLocatorSupport", 0x4, NULL },
    { "createIDPattern", NULL, "LJavaUtilRegexPattern", 0x4, NULL },
  };
  static J2ObjcClassInfo _CCPBVBluetoothBeaconLocator = { "BluetoothBeaconLocator", "com.sparseware.bellavista.external", NULL, 0x1, 2, methods, 0, NULL, 0, NULL};
  return &_CCPBVBluetoothBeaconLocator;
}

@end
