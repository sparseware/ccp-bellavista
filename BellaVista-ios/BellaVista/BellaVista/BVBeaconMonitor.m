/*
* Copyright (C) SparseWare Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

#import "BVBeaconMonitor.h"
#include "com/sparseware/bellavista/external/BluetoothBeaconLocatorSupport.h"

@implementation CCPBVBeaconMonitor {
  CLLocationManager* locationManager;
  __weak CCPBVBluetoothBeaconLocatorSupport* locatorSupport;
  BOOL deniedAccess;
  BOOL pendingAccess;
}
- (instancetype)initWithLocatorSupport: (CCPBVBluetoothBeaconLocatorSupport*) support
{
  self = [super init];
  if (self) {
    locatorSupport=support;
    locationManager=[[CLLocationManager alloc] init];
    locationManager.delegate = self;
    CLAuthorizationStatus status=[CLLocationManager authorizationStatus];
    pendingAccess=YES;
    deniedAccess=![CLLocationManager isRangingAvailable];
    switch(status) {
      case kCLAuthorizationStatusNotDetermined:
        if ([locationManager respondsToSelector:@selector(requestWhenInUseAuthorization)]) {
          [locationManager requestWhenInUseAuthorization];
        }
        else {
          [locationManager startUpdatingLocation];
        }
        break;
      case kCLAuthorizationStatusDenied:
      case kCLAuthorizationStatusRestricted:
        pendingAccess=NO;
        deniedAccess=YES;
        break;
      default:
        pendingAccess=NO;
        break;
        
    }
  }
  return self;
}
-(BOOL) wasAccessGranted {
  return !deniedAccess && !pendingAccess;
}
-(BOOL) isAccessPending {
  return pendingAccess;
}
-(void) startMonitoringBeacon: (NSString*) uuidString major: (int) bmajor minor: (int) bminor identifier: (NSString*) identifier {
  
  NSUUID *uuid = [[NSUUID alloc] initWithUUIDString:uuidString];
  CLBeaconRegion *br;
  if(bminor!=0) {
    br= [[CLBeaconRegion alloc] initWithProximityUUID:uuid
                                                                         major:bmajor
                                                                         minor:bminor
                                                                    identifier:identifier];
  }
  else if(bmajor!=0) {
    br= [[CLBeaconRegion alloc] initWithProximityUUID:uuid
                                                major:bmajor
                                           identifier:identifier];
  }
  else {
    br= [[CLBeaconRegion alloc] initWithProximityUUID:uuid
                                           identifier:uuidString];
  }
  br.notifyOnEntry=NO;
  br.notifyOnExit=NO;
  br.notifyEntryStateOnDisplay=NO;
  [locationManager startMonitoringForRegion:br];
  [locationManager startRangingBeaconsInRegion:br];
}
-(void) stopMonitoringIdentifier: (NSString*) identifier {
  NSArray* regions=locationManager.monitoredRegions.allObjects;
  for (CLBeaconRegion* br in regions) {
    if([identifier isEqualToString:br.identifier]) {
      [locationManager stopMonitoringForRegion:br];
      [locationManager stopRangingBeaconsInRegion:br];
    }
  }
}

-(void) stopAllMonitoring {
  NSArray* regions=locationManager.monitoredRegions.allObjects;
  for (CLBeaconRegion* br in regions) {
    [locationManager stopMonitoringForRegion:br];
    [locationManager stopRangingBeaconsInRegion:br];
  }
}
- (void)notifySupportAboutBeacons:(NSArray *)beacons inRegion:(CLBeaconRegion *)region {
  [locatorSupport beginRangingBeacons];
  NSString* identifier=region.identifier;
  for (CLBeacon *beacon in beacons) {
    NSString * uuid=beacon.proximityUUID.UUIDString;
    [locatorSupport rangedBeaconWithNSString:uuid withInt:beacon.major.intValue withInt:beacon.minor.intValue withFloat:beacon.accuracy withNSString:identifier];
  }
  [locatorSupport endRangingBeacons];
  
  
}

- (void)locationManager:(CLLocationManager *)manager
        didRangeBeacons:(NSArray *)beacons
               inRegion:(CLBeaconRegion *)region
{
  if(beacons.count>0) {
    [self notifySupportAboutBeacons:beacons inRegion:region];
  }
}
- (void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region {
  
}
- (void)locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region {
}

- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error {
  NSLog(@"Failed monitoring region: %@", error);
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
  NSLog(@"Location manager failed: %@", error);
}
         
- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
  BOOL den=deniedAccess;
  pendingAccess=NO;
  switch(status) {
    case kCLAuthorizationStatusNotDetermined:
      pendingAccess=YES;
      break;
    case kCLAuthorizationStatusDenied:
    case kCLAuthorizationStatusRestricted:
      deniedAccess=YES;
      break;
    default:
      break;
  }
  if(!pendingAccess) {
    if(den !=deniedAccess) {
      [locatorSupport accessChangedWithBoolean:!deniedAccess];
    }
    if(!deniedAccess) {
      [locationManager startUpdatingLocation];
    }
  }
}

-(void)dispose {
  [self stopAllMonitoring];
  locationManager=nil;
}

@end
