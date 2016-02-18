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

import com.appnativa.rare.net.ActionLink;
import com.appnativa.util.CharArray;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.external.fhir.FHIRServer.FHIRResource;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import java.util.HashMap;

public class Procedures extends aFHIRemoteService {
  protected static boolean  hasProcedures;
  protected static String[] COLUMN_NAMES;

  public Procedures() {
    super("Procedure");

    if (COLUMN_NAMES == null) {
      COLUMN_NAMES = new String[] {
        "date", "title", "status", "type", "has_attachments", "parent_id", "document_url"
      };

      FHIRResource r = server.getResource(resourceName);

      hasProcedures = r != null;
      searchParams  = (String) server.getServerConfig().opt("fhir", "vitals", "search_params");
    }

    columnNames = COLUMN_NAMES;
  }

  public void procedure(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    ActionLink l = createReadLink(path.shift());

    try {
      read(l.getReader(), conn.getContentWriter(), headers);
    } finally {
      l.close();
    }
  }

  public void report(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    ActionLink l = createReferenceReadLink(decodeLink(path.shift()));

    try {
      DiagnosticReport r = new DiagnosticReport();

      r.read(l.getReader(), conn.getContentWriter(), headers, headers);
    } finally {
      l.close();
    }
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    if (!hasProcedures) {
      dataNotAvailable(conn, path, true, headers, columnNames, 1);

      return;
    }

    ActionLink l = createSearchLink("patient", server.getPatientID());

    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers);
    } finally {
      l.close();
    }
  }

  @Override
  protected void read(Reader r, Object writer, HttpHeaders headers, Object... params) throws IOException {
    JSONObject entry = getReadEntry(r);

    readEntry(entry, null, (Writer) writer, headers);
  }

  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
    String     date   = entry.optString("performedDateTime", null);
    String     title  = FHIRUtils.getBestMedicalText(entry.optJSONObject("code"));
    String     status = entry.optString("status", null);
    JSONObject o;
    CharArray  ca = new CharArray();

    if (date == null) {
      date = (String) entry.opt("performedPeriod", "start");

      if (date != null) {
        ca.set(FHIRServer.convertDateTime(date));
        date = (String) entry.opt("performedPeriod", "end");

        if (date != null) {
          ca.append(" - ").append(FHIRServer.convertDateTime(date));
        }

        date = ca.toString();
      }
    } else {
      date = FHIRServer.convertDate(date);
    }

    HttpHeaders headers     = (HttpHeaders) params[0];
    JSONArray   attachments = entry.optJSONArray("report");
    int         acount      = (attachments == null)
                              ? 0
                              : attachments.length();

    headers.setDefaultResponseHeaders();

    if (acount > 0) {
      headers.mimeMultipart(Documents.BOUNDARY);
      FHIRUtils.writeMultipartDocumentIndex("document", "", title, null, w);

      for (int i = 0; i < acount; i++) {
        o = attachments.getJSONObject(i);
        ca.set("/hub/main/procedures/report/");
        ca.append(encodeLink(o.getString("reference")));
        FHIRUtils.writeMultipartDocumentIndex("document", null, o.optString("display"), ca.toString(), w);
      }

      FHIRUtils.writeMultipartSubDocumentStart(w, title);
    } else {
      headers.mimeHtml();
      FHIRUtils.writeHTMLDocumentStart(w, null);
    }

    FHIRUtils.writeDocumentTitle(w, title);
    FHIRUtils.writeTableStart(w);

    String s = FHIRUtils.getCodeableConceptOrReferenceText(entry, "reason", ca);

    if (s != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.reason"), s);
    }

    if (status != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.status"), status);
    }

    s = FHIRUtils.getBestMedicalCode(entry.optJSONObject("category"));

    if (s != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.category"), s);
    }

    JSONArray list = entry.optJSONArray("bodySite");

    if ((list != null) && (list.length() > 0)) {
      FHIRUtils.writeTableValueCodeableConcept(w, getResourceAsString("bv.text.body_site"), list);
    }

    FHIRUtils.writeTableFinish(w);

    if (entry.optBoolean("notPerformed", false)) {
      FHIRUtils.writeSectionStart(w, getResourceAsString("bv.text.not_performed"));
      FHIRUtils.writeTableStart(w);
      list = entry.optJSONArray("reasonNotPerformed");

      if ((list != null) && (list.length() > 0)) {
        FHIRUtils.writeTableValueCodeableConcept(w, getResourceAsString("bv.text.reason"), list);
      }

      FHIRUtils.writeTableFinish(w);
      FHIRUtils.writeSectionEnd(w);
    } else {
      FHIRUtils.writeSectionStart(w, getResourceAsString("bv.text.performed"));
      FHIRUtils.writeTableStart(w);

      if (date != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.date"), date);
      }

      list = entry.optJSONArray("performer");

      if ((list != null) && (list.length() > 0)) {
        FHIRUtils.writeTableValueActor(w, getResourceAsString("bv.text.performer"), list, ca);
      }

      o = entry.optJSONObject("location");

      if (o != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.location"), FHIRUtils.getReferenceText(o));
      }

      o = entry.optJSONObject("outcome");

      if (o != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.outcome"), FHIRUtils.getBestMedicalText(o));
      }

      list = entry.optJSONArray("complication");

      if ((list != null) && (list.length() > 0)) {
        FHIRUtils.writeTableValueActor(w, getResourceAsString("bv.text.complications"), list, ca);
      }

      list = entry.optJSONArray("followUp");

      if ((list != null) && (list.length() > 0)) {
        FHIRUtils.writeTableValueCodeableConcept(w, getResourceAsString("bv.text.follow_up"), list);
      }

      list = entry.optJSONArray("used");

      if ((list != null) && (list.length() > 0)) {
        FHIRUtils.writeTableValueCodeableConcept(w, getResourceAsString("bv.text.items_used"), list);
      }

      FHIRUtils.writeTableFinish(w);
      FHIRUtils.writeSectionEnd(w);
    }

    o = entry.optJSONObject("text");

    if (o != null) {
      FHIRUtils.writeText(w, o, true, false);
    }

    list = entry.optJSONArray("notes");

    if (list != null) {
      FHIRUtils.writeNotes(w, list);
    }

    if (acount > 0) {
      FHIRUtils.writeMultipartSubDocumentFinish(w);
      FHIRUtils.writeMultipartDocumentFinish(w);
    } else {
      FHIRUtils.writeHTMLDocumentFinish(w);
    }
  }

  @Override
  public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... params)
          throws IOException {
    if (!entry.optString("resourceType").equals(resourceName)) {
      return;
    }

    boolean parsed = false;

    do {
      String dateld          = getID(entry);
      String date            = entry.optString("performedDateTime", null);
      String title           = FHIRUtils.getBestMedicalText(entry.optJSONObject("code"));
      String status          = entry.optString("status");
      String type            = "P";
      String has_attachments = null;

//      String parent_id=null;
      if (date == null) {
        date = (String) entry.opt("performedPeriod", "start");
      }

      if ((title == null) || (title.length() == 0)) {
        if (server.isDebugMode()) {
          title = MISSING_INVALID;
        } else {
          throw missingRequiredData("code", dateld);
        }
      }

      if ((dateld == null) || (dateld.length() == 0)) {
        if (server.isDebugMode()) {
          dateld = BAD_ID;
        } else {
          throw missingRequiredData("id", dateld);
        }
      }

      if (date == null) {
        date = "";
      }

      JSONArray a = entry.optJSONArray("report");

      has_attachments = ((a != null) && (a.length() > 0))
                        ? "true"
                        : "false";

      if (jw != null) {
        jw.object();

        if (date != null) {
          if (dateld != null) {
            jw.key("date").object();
            jw.key("linkedData").value(dateld).key("value").value(date);
            jw.endObject();
          } else {
            jw.key("date").value(date);
          }
        }

        if (title != null) {
          jw.key("title").value(title);
        }

        if (status != null) {
          jw.key("status").value(status);
        }

        if (type != null) {
          jw.key("type").value(type);
        }

        if (has_attachments != null) {
          jw.key("has_attachments").value(has_attachments);
        }

//        if(parent_id!=null) {
//          jw.key("parent_id").value(parent_id);
//        }
        jw.endObject();
      } else {
        if (date != null) {
          if (dateld != null) {
            w.write(dateld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, date, ca);
        }

        w.write((char) '^');

        if (title != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, title, ca);
        }

        w.write((char) '^');

        if (status != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, status, ca);
        }

        w.write((char) '^');

        if (type != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, type, ca);
        }

        w.write((char) '^');

        if (has_attachments != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, has_attachments, ca);
        }

        w.write((char) '^');
//        if(parent_id!=null) {
//          FHIRUtils.writeQuotedStringIfNecessary(w, parent_id, ca);
//        }
        w.write((char) '^');
        w.write((char) '\n');
      }

      parsed = true;
    } while(false);

    if (!parsed) {
      debugLog("Could not parse entry:\n" + entry.toString(2));
    }
  }

  class DagnosticReportProcedures extends Labs {
    public DagnosticReportProcedures() {
      super();
      columnNames = new String[] {
        "date", "title", "status", "type", "has_attachments", "parent_id", "document_url"
      };
    }

    @Override
    protected HashMap<String, String> getCategoryCodes() {
      return server.getProcedureCategories();
    }
  }
}
