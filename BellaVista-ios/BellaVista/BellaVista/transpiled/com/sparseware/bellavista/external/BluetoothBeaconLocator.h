//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/BluetoothBeaconLocator.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _CCPBVBluetoothBeaconLocator_H_
#define _CCPBVBluetoothBeaconLocator_H_

@class CCPBVaBeaconLocatorSupport;
@class JavaUtilRegexPattern;

#import "JreEmulation.h"
#include "com/sparseware/bellavista/external/aBeaconLocator.h"

@interface CCPBVBluetoothBeaconLocator : CCPBVaBeaconLocator {
}

- (id)init;
- (CCPBVaBeaconLocatorSupport *)createBeaconSupport;
- (JavaUtilRegexPattern *)createIDPattern;
@end

typedef CCPBVBluetoothBeaconLocator ComSparsewareBellavistaExternalBluetoothBeaconLocator;

#endif // _CCPBVBluetoothBeaconLocator_H_
