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

import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.ui.UIDimension;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.util.json.JSONObject;

/**
 * This class provides a base implementation for remote monitoring displays.
 * 
 * @author Don DeCoteau
 *
 */
public abstract class aRemoteMonitor {
  /**
   * Creates a new instance
   */
  protected aRemoteMonitor() {
  }

  /**
   * Checks whether the patient can be monitored (i.e. they have a monitoring
   * device).
   * 
   * @param patient
   *          the patient
   * @return true if they can be monitored; false otherwise
   */
  public abstract boolean canMonitorPatient(JSONObject patient);

  /**
   * Requests a viewer be created for displaying monitoring information for the
   * specified patient
   * 
   * @param patient
   *          the patient
   * @param parent
   *          the parent for the created viewer
   * @param targetSize
   *          the initial size for the viewer
   * @param cb
   *          the callback to be called when the viewer is created or on failure
   */
  public abstract void createViewer(JSONObject patient, iContainer parent, UIDimension targetSize, iFunctionCallback cb);

  /**
   * Call to dispose of this object. All background services should be stopped
   * and all resources released.
   */
  public abstract void dispose();

  /**
   * Notifies the monitor that it should stop sending updates temporarily
   * 
   * @param patient
   *          the patient
   */
  public abstract void pauseMonitoring(JSONObject patient);

  /**
   * Notifies the monitor that it should start sending updates.
   * 
   * @param patient
   *          the patient
   */
  public abstract void startMonitoring(JSONObject patient);

  /**
   * Notifies the monitor that it should stop sending updates.
   * 
   * @param patient
   *          the patient
   */
  public abstract void stopMonitoring(JSONObject patient);
}
