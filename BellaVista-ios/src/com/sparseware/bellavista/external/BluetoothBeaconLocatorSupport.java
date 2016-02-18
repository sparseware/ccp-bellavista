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
package com.sparseware.bellavista.external;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sparseware.bellavista.external.aBeaconLocator.Beacon;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeEvent;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeType;

/*-[
 #import "BVBeaconMonitor.h"
 ]-*/

/**
 * The class provides bluetooth iBeacon support for iOS
 * 
 * @author Don DeCoteau
 *
 */
public class BluetoothBeaconLocatorSupport extends aBeaconLocatorSupport {
  Object             monitorProxy;
  boolean listeningForPatients;
  boolean listeningForLocations;
  ArrayList<Monitor> monitors = new ArrayList<BluetoothBeaconLocatorSupport.Monitor>(2);

  public BluetoothBeaconLocatorSupport() {
    monitorProxy = createProxy(this);
  }

  /**
   * Called to dispose of the object
   */
  public void dispose() {
    super.dispose();
    disposeNative();
    monitorProxy = null;
    if (monitors != null) {
      for (Monitor m : monitors) {
        m.dispose();
      }
      monitors.clear();
    }
    monitors = null;
  }

  public boolean isAvailable() {
    return wasAccessGranted() || isAccessPending();
  }

  @Override
  public boolean wasAccessDenied() {
    return !wasAccessGranted() && !isAccessPending();
  }

  /**
   * Sets the location beacons to monitor
   * 
   * @param beacons
   *          the list of beacons
   */
  public void setLocationBeacons(List<Beacon> beacons) {
    setBeacons(LocatorChangeType.LOCATIONS, beacons);
  }

  /**
   * Sets the patient beacons to monitor
   * 
   * @param beacons
   *          the list of beacons
   */
  public void setPatientBeacons(List<Beacon> beacons) {
    setBeacons(LocatorChangeType.PATIENTS, beacons);
  }

  /**
   * Called to start listening for locations
   */
  public void startListeningForLocations() {
    listeningForLocations=true;
    if(isAccessPending()) {
      requestAuthorization();
      return;
    }
    for (Monitor m : monitors) {
      if (m.type == LocatorChangeType.LOCATIONS) {
        m.start();
      }
    }
  }

  /**
   * Called to start listening for patients
   */
  public void startListeningForPatients() {
    listeningForPatients=true;
    if(isAccessPending()) {
      requestAuthorization();
      return;
    }
    for (Monitor m : monitors) {
      if (m.type == LocatorChangeType.PATIENTS) {
        m.start();
      }
    }
  }

  /**
   * Called to stop listening for locations
   */
  public void stopListeningForLocations() {
    listeningForLocations=false;
    for (Monitor m : monitors) {
      if (m.type == LocatorChangeType.LOCATIONS) {
        m.stop();
      }
    }
  }

  /**
   * Called to stop listening for patients
   */
  public void stopListeningForPatients() {
    listeningForPatients=false;
    for (Monitor m : monitors) {
      if (m.type == LocatorChangeType.PATIENTS) {
        m.stop();
      }
    }
  }

  /**
   * Called by CCPBVBeaconMonitor to notify us that our location services access
   * has changed
   */
  protected void accessChanged(boolean granted) {
    if (granted) {
      for (Monitor m : monitors) {
        if (m.monitoring) {
          if(m.type==LocatorChangeType.PATIENTS &&  listeningForPatients) {
            startMonitoring(m.beacons);
          }
          else if(m.type==LocatorChangeType.LOCATIONS &&  listeningForLocations) {
            startMonitoring(m.beacons);
          }
        }
      }
    } else if (changeListener != null) {
      changeListener.stateChanged(new LocatorChangeEvent(this, LocatorChangeType.ACCESS_DENIED));
    }
  }

  /**
   * Called by CCPBVBeaconMonitor to notify us that it will start ranging
   * beacons
   */
  protected void beginRangingBeacons() {
    for (Monitor m : monitors) {
      m.clearRangedBeacons();
    }
  }

  protected void noBeaconInrange() {
    for (Monitor m : monitors) {
      if (m.rangedBeacons != null) {
        notify(Collections.EMPTY_LIST, m.type == LocatorChangeType.PATIENTS);
      }
      m.clearRangedBeacons();
    }
  }

  /**
   * Called by CCPBVBeaconMonitor to notify us that it has ended ranging beacons
   */
  protected void endRangingBeacons() {
    for (Monitor m : monitors) {
      if (m.rangedBeacons != null) {
        notify(m.rangedBeacons, m.type == LocatorChangeType.PATIENTS);
      }
    }
  }

  /**
   * Checks if the specified uuid and major correspond to any of the beacons we
   * are looking for
   */
  protected boolean isMember(List<Beacon> list, String uuid, int major) {
    for (Beacon b : list) {
      if (b.isOneOfMine(uuid, major)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Called by CCPBVBeaconMonitor to notify us of a ranged beacon
   */
  protected void rangedBeacon(String uuid, int major, int minor, float proximity, String identifier) {
    for (Monitor m : monitors) {
      if (m.rangedBeacon(uuid, major, minor, proximity, identifier)) {
        break;
      }
    }
  }

  void startMonitoring(List<Beacon> beacons) {
    for (Beacon b : beacons) {
      startMonitoring(b.uuid, b.major, b.minor, b.uuid);
    }
  }

  void stopMonitoring(List<Beacon> beacons) {
    for (Beacon b : beacons) {
      stopMonitoring(b.uuid);
    }
  }

  void setBeacons(LocatorChangeType type, List<Beacon> beacons) {
    Monitor bm = null;
    for (Monitor m : monitors) {
      if (m.type == type) {
        bm = m;
        break;
      }
    }
    if (bm == null) {
      bm = new Monitor(type, beacons);
      monitors.add(bm);
    } else {
      if (bm.monitoring) {
        bm.stop();
        bm.beacons = beacons;
        bm.start();
      } else {
        bm.beacons = beacons;
      }
    }
  }

  native void disposeNative()
  /*-[
    [(CCPBVBeaconMonitor*)monitorProxy_ dispose];
  ]-*/;

  native boolean isAccessPending()
  /*-[
    return [(CCPBVBeaconMonitor*)monitorProxy_ isAccessPending];
  ]-*/;

  native void startMonitoring(String uuid, int bmajor, int bminor, String identifier)
  /*-[
    CCPBVBeaconMonitor* monitor=(CCPBVBeaconMonitor*)monitorProxy_;
    [monitor startMonitoringBeacon:uuid major:bmajor minor:bmajor identifier:identifier];
  ]-*/;

  native void stopMonitoring(String identifier)
  /*-[
    CCPBVBeaconMonitor* monitor=(CCPBVBeaconMonitor*)monitorProxy_;
    [monitor stopMonitoringIdentifier:identifier];
  ]-*/;

  native boolean wasAccessGranted()
  /*-[
    return [(CCPBVBeaconMonitor*)monitorProxy_ wasAccessGranted];
  ]-*/;


  native void requestAuthorization()
  /*-[
    [(CCPBVBeaconMonitor*)monitorProxy_ requestAuthorization];
  ]-*/;

  native static Object createProxy(BluetoothBeaconLocatorSupport bbs)
  /*-[
    return [[CCPBVBeaconMonitor alloc] initWithLocatorSupport: bbs];
  ]-*/;

  class Monitor {
    LocatorChangeType type;
    boolean           monitoring;
    List<Beacon>      beacons;
    List<Beacon>      rangedBeacons;

    public Monitor(LocatorChangeType type, List<Beacon> beacons) {
      super();
      this.type = type;
      this.beacons = beacons;
    }

    void addRangedBeacon(Beacon beacon) {
      if (rangedBeacons == null) {
        rangedBeacons = new ArrayList<BluetoothBeaconLocator.Beacon>(5);
      }
      rangedBeacons.add(beacon);
    }

    void clearRangedBeacons() {
      rangedBeacons = null;
    }

    void dispose() {
      beacons = null;
      rangedBeacons = null;
    }

    List<Beacon> getBeacons(List<Beacon> out) {
      if (beacons != null && !beacons.isEmpty()) {
        if (out != null) {
          out.addAll(beacons);
        } else {
          out = beacons;
        }
      }
      return out;
    }

    boolean rangedBeacon(String uuid, int major, int minor, float proximity, String identifier) {
      for (Beacon b : beacons) {
        if (b.isOneOfMine(uuid, major)) {
          addRangedBeacon(new Beacon(uuid, major, minor, proximity));
          return true;
        }
      }
      return false;
    }

    void start() {
      if (!monitoring) {
        monitoring = true;
        startMonitoring(beacons);
      }
    }

    void stop() {
      if (monitoring) {
        monitoring = false;
        stopMonitoring(beacons);
      }
    }
  }
}
