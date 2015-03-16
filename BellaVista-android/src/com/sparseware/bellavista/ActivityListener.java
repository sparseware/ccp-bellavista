package com.sparseware.bellavista;

import android.app.Dialog;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.platform.android.aActivityListener;
import com.appnativa.rare.platform.android.iActivity;

public class ActivityListener extends aActivityListener {

  public ActivityListener() {
  }

  @Override
  public boolean onBackPressed(iPlatformAppContext app, iActivity a, Dialog dialog) {
    do {
      if (!Utils.isApplicationLocked()) {
        if (Utils.popWorkspaceViewer()) {
          break;
        }
        if(Platform.getAppContext().isPopupWindowShowing() || Platform.getAppContext().isDialogWindowShowing()) {
          break;
        }
        if (!PatientSelect.isShowing() && Utils.getPatient()!=null) {
          PatientSelect.changePatient(Platform.getWindowViewer(), null);
          break;
        }
      }
      Utils.exit();
    } while (false);
    return true;
  }
}
