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

package com.sparseware.bellavista.external.fhir.util;

import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.external.fhir.aFHIRemoteService;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public class Lists extends aFHIRemoteService {
  public Lists() {
    super();
  }

  public void clinics(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noData(conn, path, false, headers);
  }

  public void my_patient_lists(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, false, headers);
  }

  public void my_teams(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, false, headers);
  }

  public void relationships(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, false, headers);
  }

  public void specialities(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, false, headers);
  }

  public void teams(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noData(conn, path, false, headers);
  }

  public void units(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noData(conn, path, false, headers);
  }

  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {}
}
