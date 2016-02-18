//
//  BVBeaconMonitor.h
//  BellaVista
//
//  Created by Don DeCoteau on 3/19/15.
//  Copyright (c) 2015 appNativa, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
@import CoreLocation;
@class CCPBVBluetoothBeaconLocatorSupport;

@interface CCPBVBeaconMonitor : NSObject <CLLocationManagerDelegate>

-(instancetype)initWithLocatorSupport: (CCPBVBluetoothBeaconLocatorSupport*) support;
-(void) startMonitoringBeacon: (NSString*) uuidString major: (int) bmajor minor: (int) bminor identifier: (NSString*) identifier;
-(void) stopAllMonitoring;
-(void) stopMonitoringIdentifier: (NSString*) identifier;
-(void) dispose;
-(BOOL) wasAccessGranted;
-(BOOL) isAccessPending;
-(void) requestAuthorization;
@end
