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

import java.net.MalformedURLException;
import java.util.List;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.util.ObjectHolder;
import com.sparseware.bellavista.external.aPatientLocator;

public class DemoPatientLocator extends aPatientLocator{

  public DemoPatientLocator() {
  }

  @Override
  public void dispose() {
  }

 
  @Override
  public void getNearbyPatients(final iFunctionCallback cb) {
    WindowViewer w=Platform.getWindowViewer();
    iFunctionCallback mcb=new iFunctionCallback() {
      
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if(returnValue instanceof Throwable) {
          cb.finished(true, returnValue);
        }
        else {
          delayCallback(cb, ((ObjectHolder)returnValue).value);
        }
      }
    };
    try {
      ActionLink link=Utils.createLink(w, "/hub/main/util/patients/nearby", false);
      w.getContentAsList(w,link , true, mcb);
    } catch (MalformedURLException e) {
      Utils.handleError(e);
    }
  }

  protected void delayCallback(final iFunctionCallback cb,final Object result) {
    Platform.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        cb.finished(false, result);

      }
    }, 200);
  }
  @Override
  public void getNearbyLocations(iFunctionCallback cb) {
  }

  @Override
  public boolean isNearbyPatientsSupported() {
   
    return true;
  }

  @Override
  public boolean isNearbyLocationsSupported() {
    return false;
  }

  @Override
  public List<RenderableDataItem> getUpdatedNearbyPatients() {
    return null;
  }

}
