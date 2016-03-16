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

import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.widget.iWidget;

import com.sparseware.bellavista.Settings.Server;

import java.net.URL;
import java.net.URLStreamHandler;

/**
 * An abstract class for protocol handlers
 * that will handle creating URL stream handles to handle custom
 * URL schemes used by the client for proxy-ing request to remote services
 * 
 * @author Don DeCoteau
 */
public abstract class aProtocolHandler {
  private boolean listSupport;
  private boolean patientLocatorSupport;
  private boolean barCodeSupport;
  private boolean myPatientsSupport;

  public aProtocolHandler() {}

  /**
   * Convenience method for creating a link
   * @param context the context
   * @param url the url
   * @return the new link
   */
  public ActionLinkEx createLink(iWidget context, URL url) {
    return new ActionLinkEx(context, url);
  }

  /**
   * Convenience method for creating a link
   * @param context the context
   * @param url the url
   * @param type the mime type for the returned data
   * @return the new link
   */
  public ActionLinkEx createLink(iWidget context, URL url, String type) {
    return new ActionLinkEx(context, url, type);
  }

  /**
   * called to create the stream handler for handling url connections
   * @param protocol the protocol that the handler is to support
   * @return the stream handler
   */
  public URLStreamHandler createURLStreamHandler(String protocol) {
    return null;
  }

  public boolean hasBarCodeSupport() {
    return barCodeSupport;
  }

  public boolean hasPatientLocatorSupport() {
    return patientLocatorSupport;
  }

  public boolean hasListSupport() {
    return listSupport;
  }

  public boolean hasMyPatientsSupport() {
    return myPatientsSupport;
  }

  /**
   * Called to login to a server that supports custom logins
   * @param cb the callback to return the status of the login
   * attempt
   */
  public void customLogin(Server server, iFunctionCallback cb) {}

  /**
   * Called to re-login when the server supports custom logins
   * @param cb the callback to return the status of the re-login
   * attempt
   */
  public void relogin(iFunctionCallback cb) {}

  /**
   * This is called when the user is about to attempt to connect to the server
   * the first time
   * @param server the server the user wants to connect to
   *
   * @throws Exception
   */
  public void initialize(Server server) throws Exception {
    myPatientsSupport     = server.optBoolean("myPatientsSupport");
    patientLocatorSupport = server.optBoolean("patientLocatorSupport");
    barCodeSupport        = server.optBoolean("barCodeSupport");
    listSupport           = server.optBoolean("listSupport");
  }

  /**
   * Notifies the protocol handler that a servers configuration has been updated.
   * This gives the handle an opportunity to add information to the configuration
   * @param s the server
   */
  public void serverConfigurationUpdated(Server s) {}
}
