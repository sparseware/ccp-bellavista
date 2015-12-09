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

import java.net.URL;
import java.net.URLStreamHandler;

import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.widget.iWidget;
import com.sparseware.bellavista.Settings.Server;

public abstract class aProtocolHandler {
  public aProtocolHandler() {}

  public ActionLink createLink(iWidget context, URL url) {
    return new ActionLink(context, url);
  }

  public ActionLink createLink(iWidget context, URL url, String type) {
    return new ActionLink(context, url, type);
  }

  public URLStreamHandler createURLStreamHandler(String protocol) {
    return null;
  }

  public boolean hasBarCodeSupport() {
    return false;
  }

  public boolean hasPatientLocatorSupport() {
    return false;
  }

  public boolean hasListSupport() {
    return false;
  }

  public boolean hasMyPatiensSupport() {
    return false;
  }

  public abstract void initialize(Server server) throws Exception;
}
