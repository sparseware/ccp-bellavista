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

import java.util.List;

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.event.iChangeListener;
import com.google.j2objc.annotations.Weak;
import com.sparseware.bellavista.external.aBeaconLocator.Beacon;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeEvent;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeType;

public class aBeaconLocatorSupport {
  @Weak
  protected iChangeListener changeListener;

  public aBeaconLocatorSupport() {}

  /**
   * Called to dispose of the object
   */
  public void dispose() {
    changeListener = null;
  }

  /**
   * The event was ignored release any resources associated with it
   *
   * @param event
   *          the ignored event
   */
  public void ignoreEvent(LocatorChangeEvent event) {}

  /**
   * Returns whether support is available
   *
   * @return returns true if service is available and access was granted or
   *         pending; false otherwise
   */
  public boolean isAvailable() {
    return false;
  }

  /**
   * Return whether access was explicitly denied
   *
   * @return true if access was denied; false otherwise
   */
  public boolean wasAccessDenied() {
    return false;
  }

  /**
   * Set the change listener to call when the nearby list of patient changes
   *
   * @param changeListener
   *          the change listener
   */
  public void setChangeListener(iChangeListener changeListener) {
    this.changeListener = changeListener;
  }

  /**
   * Sets the location beacons to monitor
   *
   * @param beacons
   *          the list of beacons
   */
  public void setLocationBeacons(List<Beacon> beacons) {}

  /**
   * Sets the patient beacons to monitor
   *
   * @param beacons
   *          the list of beacons
   */
  public void setPatientBeacons(List<Beacon> beacons) {}

  /**
   * Called to start listening for locations
   */
  public void startListeningForLocations() {}

  /**
   * Called to start listening for patients
   */
  public void startListeningForPatients() {}

  /**
   * Called to stop listening for locations
   */
  public void stopListeningForLocations() {}

  /**
   * Called to stop listening for patients
   */
  public void stopListeningForPatients() {}

  /**
   * Notifies the listener about found beacons
   *
   * @param beacons
   *          the beacons
   * @param patients
   *          true for patient beacons; false for locations
   */
  protected void notify(List<Beacon> beacons, boolean patients) {
    if (changeListener != null) {
      LocatorChangeType        type = patients
                                      ? LocatorChangeType.PATIENTS
                                      : LocatorChangeType.LOCATIONS;
      final LocatorChangeEvent e    = new LocatorChangeEvent(this, type, beacons);

      if (Platform.isUIThread()) {
        changeListener.stateChanged(e);
      } else {
        Platform.invokeLater(new Runnable() {
          @Override
          public void run() {
            if (changeListener != null) {
              changeListener.stateChanged(e);
            }
          }
        });
      }
    }
  }
}
