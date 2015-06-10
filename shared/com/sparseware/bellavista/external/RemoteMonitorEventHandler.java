package com.sparseware.bellavista.external;

import java.util.EventObject;

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.json.JSONObject;
import com.google.j2objc.annotations.Weak;
import com.sparseware.bellavista.external.aRemoteMonitor.iRemoteMonitorEventHandler;

public class RemoteMonitorEventHandler implements iEventHandler,iRemoteMonitorEventHandler {
  @Weak
  protected aRemoteMonitor remoteMonitor;

  public RemoteMonitorEventHandler() {
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {
  }

  public void onDispose(String eventName, iWidget widget, EventObject event) {
    if (remoteMonitor != null) {
      JSONObject patient = (JSONObject) widget.getAttribute(remoteMonitor.getPatientPropertyName());
      remoteMonitor.stopMonitoring(patient);
      if(UIScreen.isMediumScreen()) {
        Platform.getAppContext().unlockOrientation();
        Platform.getWindowViewer().getActionBar().setVisible(true);
      }
    }
  }

  public void onShown(String eventName, iWidget widget, EventObject event) {
    if (remoteMonitor != null) {
      JSONObject patient = (JSONObject) widget.getAttribute(remoteMonitor.getPatientPropertyName());
      remoteMonitor.restartMonitoring(patient);
    }
  }

  public void onHidden(String eventName, iWidget widget, EventObject event) {
    if (remoteMonitor != null) {
      JSONObject patient = (JSONObject) widget.getAttribute(remoteMonitor.getPatientPropertyName());
      remoteMonitor.pauseMonitoring(patient);
    }
  }

  public void setMonitor(aRemoteMonitor monitor) {
    this.remoteMonitor = monitor;
  }

}
