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

package com.sparseware.bellavista;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.platform.iConfigurationListener;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.listener.iApplicationListener;

/**
 * This class listens for and handles application
 * level events.
 *
 * @author Don DeCoteau
 *
 */
public class ApplicationListener implements iApplicationListener, iConfigurationListener {
  public ApplicationListener() {}

  @Override
  public boolean allowClosing(iPlatformAppContext app) {
    return true;
  }

  @Override
  public void applicationClosing(iPlatformAppContext app) {}

  @Override
  public void applicationInitialized(iPlatformAppContext app) {
    Platform.setTrackOpenConnections(true);
    Utils.applicationInitialized();
    //add ourselves as a listener for configuration change events
    app.addConfigurationListener(this);
  }

  @Override
  public void applicationPaused(iPlatformAppContext app) {
    if (!Utils.isCardStack() &&!Utils.isShuttingDown()) {
      Utils.applicationPaused();
    }
  }

  @Override
  public void applicationResumed(iPlatformAppContext app) {
    if (!Utils.isCardStack() &&!Utils.isShuttingDown()) {
      Utils.applicationResumed();
    }
  }

  @Override
  public void onConfigurationChanged(Object config) {}

  @Override
  public void onConfigurationWillChange(Object config) {
    if (!Utils.isCardStack() &&!Utils.isShuttingDown() &&!UIScreen.isLargeScreen()) {
      
      Utils.toggleFullScreen(UIScreen.isWiderForConfiguration(config));
    }
  }
}
