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

import java.util.EventObject;

import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.event.iChangeListener;

/**
 * This class provides a base implementation for location based patient
 * selection
 * 
 * @author Don DeCoteau
 */
public abstract class aPatientLocator {
  protected iChangeListener changeListener;

  public aPatientLocator() {
  }

  /**
   * Disposes of the locator
   */
  public abstract void dispose();

  /**
   * Gets the updated list nearby list of patients.
   * 
   * @param event
   *          the change event that this call is a response to
   * @param cb
   *          the callback to call with the results
   *
   */
  public abstract void getNearbyPatients(EventObject event, iFunctionCallback cb);

  /**
   * Gets the nearby locations of patients
   * 
   * @param cb
   *          the callback to call with the results
   * @param event
   *          the change event that this call is a response to
   */
  public abstract void getNearbyLocations(EventObject event, iFunctionCallback cb);

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

  /**
   * Called to stop listening for nearby patients
   */
  public abstract void stopListeningForNearbyPatients();

  /**
   * Called to stop listening for nearby locations
   */
  public abstract void stopListeningForNearbyLocations();

  /**
   * Called to start listening for nearby patients
   */
  public abstract void startListeningForNearbyPatients();

  /**
   * Called to start listening for nearby locations
   */
  public abstract void startListeningForNearbyLocations();

  /**
   * Enum representing the type of locator change we are being notified about
   * 
   * @author Don DeCoteau
   *
   */
  public enum LocatorChangeType {
    PATIENTS, LOCATIONS,ACCESS_DENIED
  }

  /**
   * Called to notify the locator that we are ignoring the event. Calling this
   * method allows to notified to discard any resources it may be holding on to
   * in regards to this event.
   * 
   * @param e
   *          the event
   */
  public abstract void ignoreEvent(EventObject e);

  /**
   * Class representing a locator change event
   * 
   * @author Don DeCoteau
   *
   */
  public static class LocatorChangeEvent extends DataEvent {

    /** the change type */
    protected LocatorChangeType changeType;

    public LocatorChangeEvent(Object source, LocatorChangeType type) {
      super(source, null);
      changeType = type;
    }

    public LocatorChangeEvent(Object source, LocatorChangeType type, Object data) {
      super(source, data);
      changeType = type;
    }

    /**
     * Gets the change type
     * 
     * @return the change type
     */
    public LocatorChangeType getChangeType() {
      return changeType;
    }
  }
}
