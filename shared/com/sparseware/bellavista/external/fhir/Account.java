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

import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.util.Map;

public class Account extends aFHIRemoteService {
  public Account() {
    super();
  }

  public void login(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    Map map = getQueryParams(conn.getURL(), data);

    if (map != null) {
      String username = (String) map.get("username");
      String password = (String) map.get("password");

      if ((username != null) && (password != null)) {
        String b64 = (String) map.get("base64");

        if ((password != null) && "true".equals(b64)) {
          password = Functions.decodeBase64(password);
        }
      }
    }

    JSONObject user = server.getUser();

    if (user == null) {
      headers.setStatus("HTTP/1.1 401 Ok");
      headers.mimeText();
      conn.getContentWriter().write("Unauthorized");
    } else {
      headers.setDefaultResponseHeaders();
      headers.mimeJson();
      conn.getContentWriter().write(user.toString());
    }
  }

  public void logout(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    server.logout();
    noData(conn, path, false, headers);
  }

  public void status(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    if (server.getUser() == null) {
      login(conn, path, data, headers);
    } else {
      headers.setDefaultResponseHeaders();
      headers.mimeJson();
      conn.getContentWriter().write("{\"success\": true}");
    }
  }

  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {}
}
