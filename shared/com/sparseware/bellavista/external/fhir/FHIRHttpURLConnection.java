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

package com.sparseware.bellavista.external.fhir;

import com.appnativa.rare.ErrorInformation;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.HTTPException;
import com.appnativa.util.json.JSONObject;

import com.sparseware.bellavista.MessageException;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.NonFatalServiceException;
import com.sparseware.bellavista.service.aHttpURLConnection;
import com.sparseware.bellavista.service.aRemoteService;

import java.io.InputStream;

import java.net.URL;

public class FHIRHttpURLConnection extends aHttpURLConnection {
  public static long lastConnectTime;

  public FHIRHttpURLConnection(URL u) {
    super(u, FHIRHttpURLConnection.class.getPackage().getName());
  }

  @Override
  protected InputStream getExceptionInputStream(HttpHeaders headers, Throwable e) {
    if ((serviceObject != null) && FHIRServer.getInstance().debug) {
      e = ApplicationException.pealException(e);

      ActionLink l   = ((aFHIRemoteService) serviceObject).getLastLink();
      String     url = (l == null)
                       ? null
                       : l.toString();
      String     msg;

      if (e instanceof HTTPException) {
        HTTPException he = (HTTPException) e;

        msg = ((HTTPException) e).getMessageBody();

        if (msg.startsWith("{")) {
          try {
            JSONObject o = new JSONObject(msg);

            msg = o.toString(1);
          } catch(Exception ignore) {}
        }

        if (msg.indexOf("</html>") != -1) {
          msg = msg.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        }

        msg = he.getStatus() + "\n" + msg;

        if (url == null) {
          url = he.getHREF();
        }
      } else if (e instanceof MessageException) {
        msg = ((MessageException) e).getMessage();
      } else if ((e instanceof ApplicationException) && ((ApplicationException) e).getCauseEx() == null) {
        msg = e.getMessage();
      } else {
        ErrorInformation ei = new ErrorInformation(e);

        msg = ei.toAlertPanelString();
      }

      StringBuilder sb = new StringBuilder();

      if (url != null) {
        sb.append("An error occured when attempting to perform the following url:\n");
        sb.append(url).append("\n\n");
      }

      sb.append(msg);

      String data = ((aRemoteService) serviceObject).getDebugOutput();

      if (data != null) {
        sb.append("\nAdditional debug output:\n");
        sb.append(data);
      }

      e = new NonFatalServiceException(sb.toString());
    }

    return super.getExceptionInputStream(headers, e);
  }

  @Override
  protected void connectToService() throws Exception {
    lastConnectTime = System.currentTimeMillis();

    try {
      super.connectToService();
    } catch(Exception e) {
      if (e instanceof HTTPException) {
        HTTPException he = (HTTPException) e;

        //check if we failed due to a time out and the token was able to be renewed
        if ((he.getStatusCode() == 401) && FHIRServer.getInstance().isSessionValid()) {
          super.connectToService();

          return;
        }
      }

      throw e;
    }
  }
}
