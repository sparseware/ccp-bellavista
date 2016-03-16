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

import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.MessageException;
import com.sparseware.bellavista.external.ActionLinkEx;
import com.sparseware.bellavista.external.fhir.FHIRServer;
import com.sparseware.bellavista.external.fhir.FHIRUtils;
import com.sparseware.bellavista.external.fhir.Patient;
import com.sparseware.bellavista.external.fhir.Patient.Practitioner;
import com.sparseware.bellavista.external.fhir.aFHIRemoteService;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.util.List;

public class Users extends aFHIRemoteService {
  protected static String[] info_fields;
  protected boolean         usersSupported;

  public Users() {
    super("Practitioner");
    usersSupported = server.getResource(resourceName) != null;
  }

  public void info(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    boolean json;

    headers.setDefaultResponseHeaders();

    if (FHIRUtils.isJSONFormatRequested(path)) {
      headers.mimeJson();
      json = true;
    } else {
      headers.mimeRow();
      json = false;
    }

    Writer w  = conn.getContentWriter();
    String id = removeExtension(path.shift());

    if (id == null) {
      throw new MessageException("missing id for user info");
    }

    List<Practitioner> team = Patient.getSelectedPatientCareteam();
    JSONObject         user = null;

    if (team != null) {
      for (Practitioner p : team) {
        if (p.getID().equals(id)) {
          user = p.getUserObject(true, usersSupported);

          break;
        }
      }
    }

    if (user == null) {
      ActionLinkEx l = server.createResourceLink("Practitioner/" + id);

      try {
        if (usersSupported) {
          user = new JSONObject(l.getContentAsString());
        } else {
          user = new JSONObject();
          user.put("id", server.getResourceAsString("bv.text.user_info_not_supported"));
        }
      } finally {
        l.close();
      }
    }

    if (json) {
      w.write(user.toString());
    } else {
      if (info_fields == null) {
        info_fields =
          "id^xmpp_id^name^dob^gender^speciality^is_provider^email^office_number^mobile_number^fax_number^street^city^state_or_province^zip_code^country^photo"
            .split("\\^");
      }

      for (String s : info_fields) {
        w.write(user.optString(s));
        w.write('^');
      }

      w.write('\n');
    }
  }

  public static JSONObject populateUser(String id, JSONObject fhirPerson) {
    JSONObject o    = new JSONObject();
    JSONObject name = fhirPerson.getJSONObject("name");
    String     s    = name.optString("text", null);

    if (s == null) {
      StringBuilder sb  = new StringBuilder();
      JSONArray     a   = name.optJSONArray("family");
      int           len = (a == null)
                          ? 0
                          : a.length();

      for (int i = 0; i < len; i++) {
        if (i > 0) {
          sb.append(" ");
        }

        sb.append(a.getString(i));
      }

      if (sb.length() > 0) {
        sb.append(",");
      }

      a   = name.optJSONArray("given");
      len = (a == null)
            ? 0
            : a.length();

      for (int i = 0; i < len; i++) {
        if (i > 0) {
          sb.append(" ");
        }

        sb.append(a.getString(i));
      }

      s = sb.toString();
    }
    
    o.put("name", s);
    s = fhirPerson.optString("sub");

    if (s != null) {
      o.put("username", s);
    }

    o.put("gender", fhirPerson.optString("gender"));
    o.put("dob", fhirPerson.optString("birthDate"));
    o.put("id", id);
    FHIRUtils.populateTelcom(fhirPerson.optJSONArray("telecom"), o);

    JSONArray a = fhirPerson.optJSONArray("address");

    if (a != null) {
      FHIRUtils.populateAddress(a.findJSONObject("use", "work"), o);
    }

    a = fhirPerson.optJSONArray("practitionerRole");

    int len = (a == null)
              ? 0
              : a.length();

    for (int i = 0; i < len; i++) {
      String role;

      try {
        role = FHIRUtils.getBestMedicalCode(a.getJSONObject(i).optJSONObject("code"));

        if ("doctor".equalsIgnoreCase(role)) {
          o.put("is_provider", true);

          break;
        }
      } catch(Exception e) {
        FHIRServer.getInstance().ignoreException(e);
      }
    }

    return o;
  }

  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {}
}
