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

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.event.iChangeListener;

import java.util.List;

public class BluetoothBeaconLocatorSupport extends aBeaconLocatorSupport {
  aBeaconLocatorSupport support;
  public BluetoothBeaconLocatorSupport() {
    try {
      Class cls=Class.forName("com.sparseware.bellavista.external.GimbalBeaconLocatorSupport");
      if(cls!=null) {
        support= (aBeaconLocatorSupport) cls.newInstance();
      }
    }
    catch(Exception e) {
      Platform.ignoreException(e);
    }
  }
  @Override
  public void dispose() {
    if(support!=null) {
      support.dispose();
    }
    super.dispose();
  }

  @Override
  public void ignoreEvent(aPatientLocator.LocatorChangeEvent event) {
    if(support!=null) {
      support.ignoreEvent(event);
    }
  }

  @Override
  public boolean isAvailable() {
    if(support!=null) {
      return support.isAvailable();
    }
    return false;
  }

  @Override
  public boolean wasAccessDenied() {
    if(support!=null) {
      return support.wasAccessDenied();
    }
    return super.wasAccessDenied();
  }

  @Override
  public void setChangeListener(iChangeListener changeListener) {
    if(support!=null) {
      support.setChangeListener(changeListener);
    }
  }

  @Override
  public void setLocationBeacons(List<aBeaconLocator.Beacon> beacons) {
    if(support!=null) {
      support.setLocationBeacons(beacons);
    }
  }

  @Override
  public void setPatientBeacons(List<aBeaconLocator.Beacon> beacons) {
    if(support!=null) {
      support.setPatientBeacons(beacons);
    }
  }

  @Override
  public void startListeningForLocations() {
    if(support!=null) {
      support.startListeningForLocations();
    }
  }

  @Override
  public void startListeningForPatients() {
    if(support!=null) {
      support.startListeningForPatients();
    }
  }

  @Override
  public void stopListeningForLocations() {
    if(support!=null) {
      support.stopListeningForLocations();
    }
  }

  @Override
  public void stopListeningForPatients() {
    if(support!=null) {
      support.stopListeningForPatients();
    }
  }

}
