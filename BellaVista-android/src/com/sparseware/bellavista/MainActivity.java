/*
 * @(#)MainActivity.java
 */

package com.sparseware.bellavista;

import android.view.Window;

public class MainActivity extends com.appnativa.rare.platform.android.MainActivity {
  
  @Override
  protected void showSplashScreen() {
    
    final int id = getResources().getIdentifier("splash_screen", "layout", getPackageName());
    if(id>0) {
      setContentView(id);
    }
  }

  @Override
  protected void setInitialOptions() {
    super.setInitialOptions();
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setActivityListener(new ActivityListener());
    /**
     * Uncomment for production as android makes screen shots for the task switcher
     * before the onPause method is called
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
    */
    
    
    /**
     * If you have your apps hosted via a web server, un-comment
     * this code and specify the correct URL in order use the 
     * live assets when running in the emulator
     */
    if(isRunningOnEmulator()) {
      System.setProperty("Rare.applicationURL", "http://192.168.1.50/apps/BellaVista/BellaVista-android/assets/application.rml");
    }
  }
}
