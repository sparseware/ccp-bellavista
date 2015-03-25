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
import java.util.List;
import java.util.Random;

import com.appnativa.rare.Platform;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.iCancelable;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.external.aBeaconLocator.Beacon;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeEvent;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeType;

/**
 * Beacon location support is not available on the desktop. This support class
 * is for testing and demonstration purposes. It only registers an available
 * when in demo mode.
 * 
 * @author Don DeCoteau
 */
public class BluetoothBeaconLocatorSupport extends aBeaconLocatorSupport implements Runnable {

  iCancelable     timer;
  List<Beacon>    patientBeacons;
  boolean         stopped      = false;
  long            timeout      = 2000;
  int             patientIds[] = new int[5];

  public BluetoothBeaconLocatorSupport() {
    for (int i = 0; i < 5; i++) {
      patientIds[i] = i + 1;
    }
  }

  public boolean isAvailable() {
    return Utils.isDemo();
  }

  /**
   * Sets the patient beacons to monitor
   * 
   * @param beacons
   *          the list of beacons
   */
  public void setPatientBeacons(List<Beacon> beacons) {
    patientBeacons = beacons;
  }

  /**
  /**
   * Called to start listening for patients
   */
  public void startListeningForPatients() {
    stopped=false;
    Platform.getWindowViewer().setTimeout(this, timeout);
  }


  /**
   * Called to stop listening for patients
   */
  public void stopListeningForPatients() {
    if (timer != null) {
      timer.cancel(true);
      timer = null;
    }
    stopped = true;
  }

  void shuffleIds() {
    int[] list = patientIds;
    int len = list.length;
    int elen;

    Random r = new Random(System.currentTimeMillis());

    for (int i = 0; i < len; i++) {
      elen = len - i;

      int n = Math.abs(r.nextInt() % elen) + i;

      if (n < 0) {
        n = -n;
      }

      if (i != n) {
        int o = list[n];

        list[n] = list[i];
        list[i] = o;
      }
    }

  }

  @Override
  public void run() {
    if (changeListener != null && !stopped) {
      shuffleIds();
      int[] patients = patientIds;
      int cnt = (int) (Functions.randomLong(patientIds.length - 1)) + 1;
      ArrayList<Beacon> list = new ArrayList<BluetoothBeaconLocator.Beacon>(cnt);
      Beacon p = patientBeacons.get(0);
      for (int i = 0; i < cnt; i++) {
        Beacon b = new Beacon(p.uuid, p.major, patients[i]);
        b.proximity = (float) Functions.randomLong(20000) / 1000f;
        list.add(b);
      }
      LocatorChangeEvent ce = new LocatorChangeEvent(this, LocatorChangeType.PATIENTS, list);
      changeListener.stateChanged(ce);
      if (!stopped) {
        timer = Platform.getWindowViewer().setTimeout(this, timeout);
      }
    }
  }
}
