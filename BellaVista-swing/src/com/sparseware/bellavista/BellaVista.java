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

import java.net.URL;
import javax.swing.RepaintManager;
import org.jdesktop.swinghelper.debug.CheckThreadViolationRepaintManager;
import com.appnativa.rare.Platform;
import com.appnativa.rare.platform.swing.Main;

public class BellaVista extends Main {
  public BellaVista() {
    super();
  }

  public BellaVista(String[] args) {
    super(args);
  }

  @Override
  public void onEscapeKeyPressed() {
    Utils.popViewerStack();
  }
  
  public static void main(String[] args) {
    String resourcePath = null;
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Hello World!");
    //check if running from file system and if so
    //use the live assets and resources
    try {
      URL u = BellaVista.class.getResource("BellaVista.class");

      if (u != null && u.getProtocol().equals("file")) {
        String s = u.getPath();
        int n = s.indexOf("BellaVista-swing");

        if (n != -1) {
          s = s.substring(0, n);
          resourcePath = "file://" + s + "BellaVista-android/res";
          if (args == null || args.length == 0) {
            args = new String[] { s + "BellaVista-android/assets/application.rml" };
          } else {
            String[] a = new String[args.length + 1];
            a[0] = s + "BellaVista-android/assets/application.rml";
            for (int i = 0; i < args.length; i++) {
              a[i + 1] = args[i];
            }
            args = a;
          }
        }
        debugEnabled = true;
        //set CheckThreadViolationRepaintManager to check for UI actions being performed on background threads 
        RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());

      }
    } catch (Exception ignore) {
    }

    if (resourcePath == null) {
      args = new String[] { "lib:assets/application.rml" };
      resourcePath = "resources";
    }

    try {
      BellaVista m = new BellaVista(args);

      if (resourcePath != null) {
        Platform.getUIDefaults().put("Rare.Resources.path", resourcePath);
      }

      m.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
