package com.sparseware.bellavista;

import android.app.Dialog;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.platform.android.aActivityListener;
import com.appnativa.rare.platform.android.iActivity;

public class ActivityListener extends aActivityListener {
  public ActivityListener() {}

  @Override
  public boolean onBackPressed(iPlatformAppContext app, iActivity a, Dialog dialog) {
    do {
      if (!Utils.isApplicationLocked()) {
        
        //check if we have a dialog or popup window showing
        if (Platform.getAppContext().isPopupWindowShowing() || Platform.getAppContext().isDialogWindowShowing()) {
          return true;
        }

        // check if we are in pseudo full screen
        if(Actions.handledPseudoFullScreenMode()) {
          return true;
        }
        
        //check if we have a viewer on the sack and pop it
        if (Utils.popViewerStack(true)) {
          return true;
        }
        
        //if change patient is not showing, show it
        if (!PatientSelect.isShowing() && (Utils.getPatient() != null)) {
          PatientSelect.changePatient(Platform.getWindowViewer(), null);

          return true;
        }
      }

      Utils.exit();

      return true;
    } while(false);
  }
}
