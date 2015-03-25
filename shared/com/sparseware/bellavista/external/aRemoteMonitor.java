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
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.SplitPane;
import com.appnativa.rare.spot.StackPane;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.ui.UIDimension;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.viewer.SplitPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.util.json.JSONObject;

/**
 * This class provides a base implementation for remote monitoring displays.
 * 
 * @author Don DeCoteau
 *
 */
public abstract class aRemoteMonitor {
  /**
   * the name of the button that will toggle between the numerics and waveforms
   * views"
   */
  protected String           toggleButtonName      = "toggleButton";

  /**
   * the name of the button that will toggle between the numerics and waveforms
   * views"
   */
  protected String           eventHandlerClassName = RemoteMonitorEventHandler.class.getName();

  /**
   * the name of the property to use to associate the patient object with a
   * viewer"
   */
  protected String           patientPropertyName   = "_RM_PATIENT_";

  /**
   * A handle to the event handler; we keep it so that were are always using the
   * same one and it does not get GCed
   */
  iRemoteMonitorEventHandler eventHandler;

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
   * Requests a viewer be created for displaying numeric vitals information for
   * the specified patient
   * 
   * @param patient
   *          the patient
   * @param parent
   *          the parent for the created viewer
   * @param cb
   *          the callback to be called when the viewer is created or on failure
   */
  public void createNumericsViewer(final JSONObject patient, final iContainer parent, iFunctionCallback cb) {
    createViewer(patient, parent, true, cb);

  }

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
   * Requests a viewer be created for displaying waveforms information for the
   * specified patient
   * 
   * @param patient
   *          the patient
   * @param parent
   *          the parent for the created viewer
   * @param cb
   *          the callback to be called when the viewer is created or on failure
   */
  public void createWaveformsViewer(final JSONObject patient, final iContainer parent, iFunctionCallback cb) {
    createViewer(patient, parent, false, cb);

  }

  /**
   * Call to dispose of this object. All background services should be stopped
   * and all resources released.
   */
  public void dispose() {
    eventHandler = null;
  }

  /**
   * Gets the name of the property to use to associate the patient object with a
   * viewer
   * 
   * @return the name
   */
  public String getPatientPropertyName() {
    return patientPropertyName;
  }

  /**
   * Notifies the monitor that it should stop sending updates temporarily
   * 
   * @param patient
   *          the patient or null for all patients
   */
  public abstract void pauseMonitoring(JSONObject patient);

  /**
   * Notifies the monitor that it should restart sending updates.
   * 
   * @param patient
   *          the patient the patient or null for all patients that were paused
   */
  public abstract void restartMonitoring(JSONObject patient);

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
   *          the patient or null for all patients
   */
  public abstract void stopMonitoring(JSONObject patient);

  /**
   * Returns whether or not the monitor supports numerics
   * 
   * @return true numerics are supported; false otherwise
   */
  public boolean supportsNumerics() {
    return false;
  }

  /**
   * Returns whether or not the monitor supports waveforms
   * 
   * @return true waveforms are supported; false otherwise
   */
  public boolean supportsWaveforms() {
    return false;
  }

  /**
   * Called to create either a numerics or waveforms viewer
   * 
   * @param patient
   *          the patient the viewer is for
   * @param parent
   *          the parent for the viewer
   * @param numerics
   *          true for a numerics viewer; false for a waveforms viewer
   * @param cb
   *          the call to be notified when the viewer is created
   */
  protected void createViewer(final JSONObject patient, final iContainer parent, final boolean numerics, final iFunctionCallback cb) {

  }

  /**
   * Called on the UI thread to finish the creation of a viewer
   * 
   * @param patient
   * @param parent
   *          the parent for the
   * @param wcfg
   *          the configuration for the waveform viewer (can be null)
   * @param ncfg
   *          the configuration for the waveform viewer (can be null)
   * @param stacked
   *          true if the viewers should be stacked; false to split
   * @return the created viewer
   */
  protected iContainer finishCreatingViewer(JSONObject patient, iContainer parent, Viewer wcfg, Viewer ncfg, boolean stacked) {
    WindowViewer w = Platform.getWindowViewer();
    iContainer nv = (iContainer) (ncfg == null ? null : parent.createWidget(ncfg));
    iContainer wv = (iContainer) (wcfg == null ? null : parent.createWidget(wcfg));
    iContainer rv = null;
    if (wv == null || nv == null) {
      rv = wv == null ? nv : wv;
      viewerCrated(rv, rv == nv);
    } else {
      if (stacked) {
        final StackPaneViewer sp = (StackPaneViewer) w.createViewer(parent, new StackPane());
        sp.addViewer("waveforms", wv);
        sp.addViewer("numerics", nv);
        sp.setSelectedIndex(0);
        PushButtonWidget pb = (PushButtonWidget) wv.getWidget("toggleButton");
        if (pb != null) {
          pb.addActionListener(new iActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              sp.switchTo("numerics");
            }
          });
        }
        pb = (PushButtonWidget) nv.getWidget("toggleButton");
        if (pb != null) {
          pb.addActionListener(new iActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              sp.switchTo("waveforms");
            }
          });
        }
        rv = sp;
      } else {
        SplitPane pane = (SplitPane) w.createConfigurationObject("SplitPane", "bv.monitor.splitpane");
        final SplitPaneViewer sp = (SplitPaneViewer) w.createViewer(parent, pane);
        sp.setViewer(0, wv);
        sp.setViewer(1, nv);
        rv = sp;
      }
      viewerCrated(nv, true);
      viewerCrated(wv, false);
    }
    String cls = eventHandlerClassName;
    if (eventHandler == null && cls != null) {
      eventHandler = (iRemoteMonitorEventHandler) Functions.getEventHandler(cls);
      eventHandler.setMonitor(this);
    }
    if (eventHandler != null) {
      rv.setEventHandler(iConstants.EVENT_DISPOSE, "class:" + cls + "#onDispose", true);
      rv.setEventHandler(iConstants.EVENT_SHOWN, "class:" + cls + "#onShown", true);
      rv.setEventHandler(iConstants.EVENT_HIDDEN, "class:" + cls + "#onHidden", true);
    }
    rv.setAttribute(patientPropertyName, patient);
    mainViewerCrated(rv, patient);
    return rv;
  }

  /**
   * Called when the main viewer has finished being created
   * 
   * @param v
   *          the viewer
   * @param patient
   *          the patient
   */
  protected void mainViewerCrated(iContainer v, JSONObject patient) {
  }

  /**
   * Called when a viewer has finished being created
   * 
   * @param v
   *          the viewer
   * @param numerics
   *          true if the viewer is a numerics viewer; false otherwise
   */
  protected void viewerCrated(iContainer v, boolean numerics) {

  }

  public static interface iRemoteMonitorEventHandler {
    void setMonitor(aRemoteMonitor monitor);
  }

}
