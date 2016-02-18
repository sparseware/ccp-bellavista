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

import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public class Imaging extends aFHIRemoteService {
  public Imaging() {
    super("ImagingStudy");
  }

  public void images(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noData(conn, path, false, headers);
  }

  public void thumbnails(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, false, headers);
  }

  public void study(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noDocument(conn, path, headers);
  }

  public void media(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noDocument(conn, path, headers);
  }

  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {}
}
