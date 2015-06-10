/*
 * @(#)MainActivity.java
 */

package com.sparseware.bellavista;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIScreen;

public class MainActivity extends com.appnativa.rare.platform.android.MainActivity {
  GestureDetector gestureDetector;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
  }

  @Override
  public void setUseFullScreen(boolean use) {
    if (use) {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    } else {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
      getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
  }

  protected UIFont adjustSystemFont(UIFont f) {
    if(!Utils.googleGlass && UIScreen.isSmallScreen()) {
      float size = Math.max(f.getSize2D(), 20);
      return f.deriveFontSize(size);
      
    }
    else {
      return super.adjustSystemFont(f);
    }
  }
  @Override
  protected void setInitialOptions() {
    super.setInitialOptions();
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setActivityListener(new ActivityListener());
    Utils.reverseFling = Utils.googleGlass = isGoogleGlass();
    Utils.cardStack=UIScreen.isSmallScreen();

    /**
     * Uncomment for production as android makes screen shots for the task
     * switcher before the onPause method is called if
     * (android.os.Build.VERSION.SDK_INT >=
     * android.os.Build.VERSION_CODES.HONEYCOMB) {
     * getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE); }
     */

    /**
     * If you have your apps hosted via a web server, un-comment this code and
     * specify the correct URL in order use the live assets when running in the
     * emulator
     */
    if (isRunningOnEmulator()) {
      System.setProperty("Rare.applicationURL", "http://192.168.1.50/apps/BellaVista/BellaVista-android/assets/application.rml");
    }
  }
  @Override
  protected void showSplashScreen() {
    String name=isGoogleGlass() ? "splash_screen_glass" : "splash_screen";
    int id = getResources().getIdentifier(name, "layout", getPackageName());
    if (id > 0) {
      setContentView(id);
    }
    //Uncomment to simulate glass interface on non glass android device
    //System.setProperty("Rare.screenSize", "small");
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }

}
