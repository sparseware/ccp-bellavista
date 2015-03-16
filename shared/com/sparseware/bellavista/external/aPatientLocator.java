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

import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.iChangeListener;

/**
 * This class provides a base implementation for location based
 * patient selection
 * 
 * @author Don DeCoteau
 */
public abstract class aPatientLocator {
  protected iChangeListener changeListener;

  public aPatientLocator() {
  }

  public abstract void dispose();

  /**
   * Gets the nearby list of patients
   * 
   * @param cb the callback to call with the results
   */
  public abstract void getNearbyPatients(iFunctionCallback cb);

  /**
   * Gets the updated list nearby list of patients.
   * The is meant to be called after the caller receives
   * a state changed event.
   * 
   * @return the list of nearby patients
   */
  public abstract List<RenderableDataItem> getUpdatedNearbyPatients();

  /**
   * Gets the nearby locations of patients
   * 
   * @param cb the callback to call with the results
   */
  public abstract void getNearbyLocations(iFunctionCallback cb);

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
   * Returns whether or not the nearby patient locator service is supported
   * 
   * @return true if it is; false otherwise
   */
  public abstract boolean isNearbyPatientsSupported();

  /**
   * Returns whether or not the nearby location locator service is supported
   * 
   * @return true if it is; false otherwise
   */
  public abstract boolean isNearbyLocationsSupported();
}
