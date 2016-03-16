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

import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.CharArray;
import com.appnativa.util.SNumber;
import com.appnativa.util.Streams;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONWriter;
import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.external.fhir.FHIRServer.FHIRResource;
import com.sparseware.bellavista.external.fhir.FHIRUtils.MedicalCode;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Labs extends aFHIRemoteService {
  protected static boolean                 searchByDateSupported;
  protected static boolean                 searchByCategorySupported;
  protected static HashMap<String, String> categoryCodes;
  protected static HashSet<String>         bunCodes         = new HashSet<String>(2);
  protected static HashSet<String>         reportTypes      = new HashSet<String>(2);
  protected static HashSet<String>         nonClinicalTypes = new HashSet<String>(2);
  protected static HashSet<String>         creatinineCodes  = new HashSet<String>(2);
  protected static String                  categorySearch;
  protected static boolean                 hasLabs;
  protected static boolean                 hasObservationCategorySupport;
  protected static boolean                 useObservations;
  protected static boolean                 generateDisgnosticReport = true;
  protected static String[]                COLUMN_NAMES;
  protected String                         labValueFormat =
    "<tr><td class=\"lab_name\">%s</td><td class=\"lab_value\">%s</td></tr>";
  private HashSet<String> uniqueDates=new HashSet<String>();

  static {
    bunCodes.add("75367002");
    bunCodes.add("55284-4");
    creatinineCodes.add("271649006");
    creatinineCodes.add("8480-6");
    reportTypes.add("MB");
    reportTypes.add("VR");
    reportTypes.add("URN");
    reportTypes.add("TX");
    reportTypes.add("PAT");
    reportTypes.add("SP");
    reportTypes.add("MYC");
    reportTypes.add("MCB");
    reportTypes.add("PF");
    reportTypes.add("MMF");
    nonClinicalTypes.add("DiagnosticOrder");
    nonClinicalTypes.add("Patient");
    nonClinicalTypes.add("Parctitioner");
    nonClinicalTypes.add("FamilyMemberHistory");
  }

  public Labs() {
    this("DiagnosticReport");
  }

  public Labs(String resourceName) {
    super(resourceName);

    if (COLUMN_NAMES == null) {
      COLUMN_NAMES = new String[] {
        "date", "lab", "result", "unit", "range", "is_document", "category", "panel", "sort_order", "result_id",
        "comment"
      };

      FHIRResource r = server.getResource(resourceName);

      hasLabs = (r != null);

      JSONArray a = (r == null)
                    ? null
                    : r.searchParams;

      searchByDateSupported     = (a == null)
                                  ? false
                                  : a.findJSONObjectIndex("name", "date", 0) != -1;
      searchByCategorySupported = (a == null)
                                  ? false
                                  : a.findJSONObjectIndex("name", "category", 0) != -1;
      categoryCodes             = getCategoryCodes();

      JSONObject o = (JSONObject) server.getServerConfig().opt("fhir", "labs");

      if (o != null) {
        generateDisgnosticReport = o.optBoolean("generateDisgnosticReport", true);
        searchParams             = o.optString("search_params", null);

        if (searchByCategorySupported &&!o.optBoolean("searchByCategorySupported", true)) {
          searchByCategorySupported = false;
        }
      }

      if (searchByCategorySupported) {
        HashMap<String, String> map = categoryCodes;

        if (map.isEmpty()) {
          categorySearch = null;
        } else {
          StringBuilder sb = new StringBuilder();

          for (String s : map.keySet()) {
            sb.append(s).append(',');
          }

          sb.setLength(sb.length() - 1);
          categorySearch = sb.toString();
        }
      } else {
        categorySearch = null;
      }
    }

    columnNames = COLUMN_NAMES;
  }

  protected HashMap<String, String> getCategoryCodes() {
    return server.getLabCategories();
  }

  public void document(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    ActionLink l = createReadLink(path.shift());

    try {
      read(l.getReader(), conn.getContentWriter(), headers, headers);
    } finally {
      l.close();
    }
  }

  public void realtime(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, true, headers);
  }

  public void summary(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    list(conn, path, data, headers, categorySearch, 7);
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    list(conn, path, data, headers, categorySearch, -1);
  }

  public void list_by_category(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    Map<String, String> map      = getQueryParams(conn.getURL(), data);
    String              category = map.get("category");
    int                 days     = -1;
    String              s        = map.get("days");

    if (s != null) {
      days = SNumber.intValue(s);
    }

    list(conn, path, data, headers, category, days);
  }

  public void tests(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noData(conn, path, false, headers);
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers, String category,
                   int days)
          throws IOException {
    if (!hasLabs) {
      dataNotAvailable(conn, path, true, headers, columnNames, 1);

      return;
    }

    String from = null;

    if (server.isDemoMode()) {
      days = -1;
    }

    if ((days > 0) && searchByDateSupported && prefixSearchSupported) {
      Calendar c = Calendar.getInstance();

      c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - days);
      c.set(Calendar.HOUR_OF_DAY, 0);
      from = FHIRServer.convertToServerDate(c, false);
    }

    ActionLink l;

    if (from == null) {
      if (category == null) {
        l = createSearchLink("patient", server.getPatientID(), "_count", "500");
      } else {
        l = createSearchLink("patient", server.getPatientID(), "category", category, "_count", "500");
      }
    } else {
      if (category == null) {
        l = createSearchLink("patient", server.getPatientID(), "date", from);
      } else {
        l = createSearchLink("patient", server.getPatientID(), "date", from, "category", category);
      }
    }
    
//    l=server.createLink(resourceName+"?_count=100");
    uniqueDates.clear();
    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers);
    } finally {
      l.close();
    }
  }

  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
    HttpHeaders headers = (HttpHeaders) params[0];
    JSONArray   a       = entry.getJSONArray("presentedForm");

    headers.setDefaultResponseHeaders();

    if (a != null) {
      Documents.writeAttachmentsAsDocument(a, w, headers);

      return;
    }

    JSONObject text   = entry.optJSONObject("text");
    JSONArray  result = ((text == null) || generateDisgnosticReport)
                        ? entry.optJSONArray("result")
                        : null;

    if ((result != null) &&!result.isEmpty()) {
      JSONArray               contained = entry.optJSONArray("contained");
      Map<String, JSONObject> map       = null;

      if (contained != null) {
        map = createReferenceMap(contained);
      }

      resolveReferences(result, map);
      createLabReport(entry, result, w, headers);
    } else {
      headers.mimeHtml();
      FHIRUtils.writeHTMLDocumentStart(w, null);
      FHIRUtils.writeText(w, text, false, true);
      FHIRUtils.writeHTMLDocumentFinish(w);
    }
  }

  protected void writeDocumentIndex(JSONObject content, Writer w, boolean main, boolean study, CharArray ca)
          throws IOException {
    String type;
    String title = "";
    String url   = null;

    if (!main) {
      if (study) {
        type  = "custom:study";
        title = content.optString("display", null);
        url   = "hub/main/imaging/study/" + Functions.encode(getID(content.optString("reference")));

        if (title == null) {
          server.getResourceAsString("bv.text.imaging_study");
        }
      } else {
        type    = "custom:media";
        title   = content.optString("comment", null);
        content = content.optJSONObject("link");

        if (content != null) {
          url = "hub/main/imaging/media/" + Functions.encode(getID(content.optString("reference")));

          if (title == null) {
            title = content.optString("display", null);
          }
        }
      }

      if (title == null) {
        title = server.getResourceAsString("bv.text.attachment");
      }

      if (study) {
        title += "{icon:resource:bv.icon.xray}";
      } else if (study) {
        title += "{icon:resource:bv.icon.xray}";
      }
    } else {
      type  = "document";
      title = "";
    }

    w.append(type).append('^').append(title);
    w.append("^^").append(main
                          ? "true"
                          : "false");
    w.append("^");

    if (!main) {
      if (url != null) {
        w.write(url);
      }
    }

    w.append("\n");
  }

  protected void createLabReport(JSONObject entry, JSONArray result, Writer w, HttpHeaders headers) throws IOException {
    headers.setDefaultResponseHeaders();

    CharArray ca        = new CharArray();
    CharArray wca       = new CharArray();
    JSONArray images    = entry.optJSONArray("image");
    JSONArray studies   = entry.optJSONArray("imagingStudy");
    boolean   hasImages = false;

    if (((images != null) &&!images.isEmpty()) || ((studies != null) &&!studies.isEmpty())) {
      hasImages = true;
      headers.mimeMultipart(Documents.BOUNDARY);
      writeDocumentIndex(entry, w, true, false, ca);

      int len = (images == null)
                ? 0
                : images.length();

      for (int i = 0; i < len; i++) {
        writeDocumentIndex(images.getJSONObject(i), w, false, false, ca);
      }

      len = (studies == null)
            ? 0
            : studies.length();

      for (int i = 0; i < len; i++) {
        writeDocumentIndex(studies.getJSONObject(i), w, false, true, ca);
      }

      w.write(Documents.BOUNDARY_START);
      w.write("Content-Type: text/html\r\n\r\n");
    } else {
      headers.mimeHtml();
    }

    FHIRUtils.writeHTMLDocumentStart(w, null);

    MedicalCode mc = FHIRUtils.getMedicalCode(entry.optJSONObject("code"));
    String      name;

    if (mc == null) {
      name = entry.optString("text");
    } else {
      name = mc.getBestText();
    }

    FHIRUtils.writeDocumentTitle(w, name);
    FHIRUtils.writeTableStart(w);

    String s = entry.optString("status", null);

    if (s != null) {
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.status"), s);
    }

    s = entry.optString("issued", null);

    if (s != null) {
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.issued"), FHIRServer.convertDateTime(s));
    }

    s = FHIRUtils.getReferenceText(entry.optJSONObject("specimen"));

    if (s != null) {
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.specimen"), s);
    }

    s = FHIRUtils.getBestMedicalText(entry.optJSONObject("method"));

    if (s != null) {
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.method"), s);
    }

    s = entry.optString("comments").trim();

    if (s.length() > 0) {
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.comments"), s);
    }

    s = entry.optString("conclusion", null);

    if (s != null) {
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.conclusion"), s);
    }

    s = FHIRUtils.getBestMedicalText(entry.optJSONObject("codedDiagnosis"));

    if (s != null) {
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.diagnosis"), s);
    }

    FHIRUtils.writeTableFinish(w);
    w.write("<hr class=\"bold\">\n");

    if ((result != null) &&!result.isEmpty()) {
      JSONArray  other = new JSONArray();
      JSONObject title = new JSONObject().put("text", server.getResourceAsString("bv.text.observations"));

      createObservationPanel(server.getResourceAsString("bv.text.observations"), result, other, w, wca, ca);

      if (!other.isEmpty()) {
        title.put("text", server.getResourceAsString("bv.text.other_results"));
      }
    }

    FHIRUtils.writeHTMLDocumentFinish(w);

    if (hasImages) {
      w.write(Documents.BOUNDARY_END);
    }
  }

  protected void createObservationPanel(String title, JSONArray contained, JSONArray nonObservations, Writer w,
          CharArray wca, CharArray ca)
          throws IOException {
    w.append("<table class='lab_report'>\n");
    w.append("<caption>").append(title).append("</caption>\n");
    w.append("<thead>\n");
    w.append("<tr><th></th><th>");
    w.append(server.getResourceAsString("bv.text.value"));
    w.append("</th></tr>\n");
    w.append("</thead>\n");
    w.append("<tbody>\n");

    Writer sw  = Streams.charArrayWriter(wca);
    int    len = contained.size();

    for (int i = 0; i < len; i++) {
      JSONObject o = contained.getJSONObject(i);

      wca.clear();

      if (o.optString("resourceType").equals("Observation")) {
        processObservationEntry(null, null, null, null, null, o, null, sw, ca, true);

        if (wca._length > 0) {
          w.write(wca.A, 0, wca._length);
        }
      } else {
        nonObservations.add(o);
      }
    }

    FHIRUtils.writeTableFinish(w);
  }

  protected void createComponentTable(JSONArray component, Writer w, CharArray wca, CharArray ca) throws IOException {
    w.append("<tr><td colspan=\"2\">");
    w.append("<table class=\"component\">\n");
    w.append("<tbody>\n");

    if (wca == null) {
      wca = new CharArray();
    }

    Writer sw  = Streams.charArrayWriter(wca);
    int    len = component.size();

    for (int i = 0; i < len; i++) {
      JSONObject o = component.getJSONObject(i);

      o.put("resourceType", "Observation");
      wca.clear();
      processObservationEntry(null, null, null, null, null, o, null, sw, ca, true);

      if (wca._length > 0) {
        w.write(wca.A, 0, wca._length);
      }
    }

    FHIRUtils.writeTableFinish(w);
    w.append("</td></tr>");
  }

  public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... params)
          throws IOException {
    if (!entry.optString("resourceType").equals(resourceName)) {
      return;
    }

    String status = entry.optString("status");

    if (status.equals("entered-in-error") || status.equals("cancelled")) {
      return;
    }

    boolean parsed = false;
    String  msg    = null;

    do {
      String dateld = null;
      String date   = null;
      String labld  = null;
      String lab    = null;
      String result = null;
//      String unit=null;
//      String range=null;
      String is_document = "true";
      String categoryld  = null;
      String category    = null;
//      String panelld=null;
//      String panel=null;
//      String sort_order=null;
      String result_id = null;
      String comment   = null;

      if (ca == null) {
        ca = new CharArray();
      }

      result_id = dateld = getID(entry);

      JSONObject  name = entry.optJSONObject("code");
      MedicalCode mc   = FHIRUtils.getMedicalCode(name);

      if (mc == null) {
        if (server.isDebugMode()) {
          mc = missingInvalid;
        } else {
          throw missingRequiredData("code", dateld);
        }
      }

      result = entry.optString("conclusion").trim();

      if (result.length() == 0) {
        result = FHIRUtils.getBestMedicalText(entry.optJSONObject("codedDiagnosis"));
      }

      date = getDateTime(entry);

      MedicalCode mc1 = FHIRUtils.getMedicalCode(entry.optJSONObject("category"));

      if (mc1 != null) {
        mc1.resolveHL7Display(categoryCodes);
        category   = mc1.getBestText();
        categoryld = mc1.getBestCode();

        if (!searchByCategorySupported && (categoryld != null) &&!categoryCodes.containsKey(categoryld)) {
          parsed = true;

          break;
        }
      }

      lab   = mc.getBestText();
      labld = mc.getBestCode();

      JSONArray contained = entry.optJSONArray("contained");

      if ((categoryld != null) && reportTypes.contains(categoryld)) {
        contained = null;
      }

      if ((contained != null) && hasAllObservations(contained, false)) {
        int len       = contained.length();
        int processed = 0;

        for (int i = 0; i < len; i++) {
          if (processObservationEntry(date, categoryld, category, labld, lab, contained.getJSONObject(i), jw, w, ca,
                                      false)) {
            processed++;
          }
        }

        if ((result == null) && (processed == len)) {
          parsed = true;

          break;
        }
      }

      if (parsed) {
        break;
      }

      is_document = "true";
      comment     = entry.optString("comments").trim();
      comment     = FHIRUtils.cleanAndEncodeString(comment);

      if (result == null) {
        result = getResourceAsString("bv.text.see_report");
      }

      if (jw != null) {
        jw.object();

        if (date != null) {
          jw.key("date").object();
          jw.key("linkedData").value(dateld).key("value").value(date);
          jw.endObject();
        }

        if (lab != null) {
          jw.key("lab").object();
          jw.key("linkedData").value(labld).key("value").value(lab);
          jw.endObject();
        }

        if (result != null) {
          jw.key("result").value(result);
        }

//        if(unit!=null) {
//          jw.key("unit").value(unit);
//        }
//        if(range!=null) {
//          jw.key("range").value(range);
//        }
        if (is_document != null) {
          jw.key("is_document").value(is_document);
        }

        if (category != null) {
          jw.key("category").object();
          jw.key("linkedData").value(categoryld).key("value").value(category);
          jw.endObject();
        }

//        if(panel!=null) {
//          jw.key("panel").object();
//          jw.key("linkedData").value(panelld).key("value").value(panel);
//          jw.endObject();
//        }
//        if(sort_order!=null) {
//          jw.key("sort_order").value(sort_order);
//        }
        if (result_id != null) {
          jw.key("result_id").value(result_id);
        }

        if (comment != null) {
          jw.key("comment").value(comment);
        }

        jw.endObject();
      } else {
        if (date != null) {
          w.write(dateld);
          w.write((char) '|');
          FHIRUtils.writeQuotedStringIfNecessary(w, date, ca);
        }

        w.write((char) '^');

        if (lab != null) {
          if (labld != null) {
            w.write(labld);
          }

          w.write((char) '|');
          FHIRUtils.writeQuotedStringIfNecessary(w, lab, ca);
        }

        w.write((char) '^');

        if (result != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, result, ca);
        }

        w.write((char) '^');
//        if(unit!=null) {
//          FHIRUtils.writeQuotedStringIfNecessary(w, unit, ca);
//        }
        w.write((char) '^');
//        if(range!=null) {
//          FHIRUtils.writeQuotedStringIfNecessary(w, range, ca);
//        }
        w.write((char) '^');

        if (is_document != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, is_document, ca);
        }

        w.write((char) '^');

        if (category != null) {
          if (categoryld != null) {
            w.write(categoryld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, category, ca);
        }

        w.write((char) '^');
//        if(panel!=null) {
//          w.write(panelld);
//          w.write((char)'|');
//          FHIRUtils.writeQuotedStringIfNecessary(w, panel, ca);
//        }
        w.write((char) '^');
//        if(sort_order!=null) {
//          FHIRUtils.writeQuotedStringIfNecessary(w, sort_order, ca);
//        }
        w.write((char) '^');

        if (result_id != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, result_id, ca);
        }

        w.write((char) '^');

        if (comment != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, comment, ca);
        }

        w.write((char) '^');
        w.write((char) '\n');
      }

      parsed = true;
    } while(false);

    if (!parsed) {
      if (msg == null) {
        msg = "Could not parse entry:";
      }

      if (server.isDebugMode()) {
        server.debugLog(msg);
        System.out.println(entry.toString(2));
      } else {
        throw new ApplicationException(msg + "\n" + entry.toString());
      }
    }
  }

  private boolean hasAllObservations(JSONArray a, boolean references) {
    int len = a.length();

    for (int i = 0; i < len; i++) {
      JSONObject o = a.getJSONObject(i);

      if (references) {
        String s = o.optString("reference");

        if (!s.startsWith("#") &&!s.contains("Observation/")) {
          return false;
        }
      } else {
        String s = o.optString("resourceType");

        if (!s.equals("Observation")) {
          if (!nonClinicalTypes.contains(s)) {
            return false;
          }
        }
      }
    }

    return true;
  }

  protected boolean processObservationEntry(String date, String categoryld, String category, String panelld,
          String panel, JSONObject entry, JSONWriter jw, Writer w, CharArray ca, boolean forReport)
          throws IOException {
    String status = entry.optString("status");

    if (status.equals("entered-in-error") || status.equals("cancelled")) {
      return false;
    }

    if (!entry.optString("resourceType").equals("Observation")) {
      return false;
    }

    JSONObject o;
    boolean    parsed = false;

    do {
      String dateld       = null;
      String labld        = null;
      String lab          = null;
      String resultld     = null;
      String resultldtext = null;
      String result       = null;
      String unit         = null;
      String range        = null;
      String is_document  = "false";
//      String sort_order  = null;
//      String result_id   = null;
      String    comment   = null;
      JSONArray component = entry.optJSONArray("component");

      if (component != null) {
        is_document = "true";
      }

      dateld = getID(entry);
      o      = entry.optJSONObject("code");

      JSONArray   coding = (o == null)
                           ? null
                           : o.optJSONArray("coding");
      MedicalCode mc     = FHIRUtils.getMedicalCode(coding);

      if (mc == null) {
        String text = (o == null)
                      ? null
                      : o.optString("text", null);

        if (text != null) {
          mc = new MedicalCode("UNK", null, text, null);
        } else {
          if (server.isDebugMode()) {
            mc = missingInvalid;
          } else {
            throw missingRequiredData("code", dateld);
          }
        }
      }

      lab   = mc.getBestText();
      labld = mc.getBestCode();
      o     = entry.optJSONObject("valueQuantity");

      if (o != null) {
        unit   = o.optString("units", null);
        result = o.optString("value");

        if (bunCodes.contains(labld)) {
          labld = "bun";
        } else if (creatinineCodes.contains(labld)) {
          labld = "creat";
        }

        String comparator = o.optString("comparator", null);

        if (comparator != null) {
          result = ca.set(comparator).append(result).toString();
        }
      } else {
        unit   = null;
        result = FHIRUtils.getValue(entry, true, ca);

        if (result == null) {
          result = "";    //getResourceAsString("bv.text.see_report");
        }
      }

      o = entry.optJSONObject("interpretation");

      if (o != null) {
        resultld     = FHIRUtils.getHL7FHIRCode(o.optJSONArray("coding"));
        resultldtext = o.optString("text", null);

        if ((resultldtext == null) || (resultldtext.length() == 0)) {
          resultldtext = resultld;
        }
      }

      comment = entry.optString("comments", null);

      if (comment != null) {
        comment = FHIRUtils.cleanAndEncodeString(comment);
      }

      JSONArray a = entry.optJSONArray("referenceRange");

      range = (a == null)
              ? null
              : FHIRUtils.getRange(a, true, ca);

      String icolor = (resultld == null)
                      ? null
                      : FHIRUtils.getInterpretationColor(resultld);

      if ((result != null) && ((icolor != null) || (resultld != null))) {
        ca.clear();

        if ((icolor != null) &&!forReport) {
          ca.append("{fgColor:").append(icolor).append("}");
        }

        ca.append(result);

        if (resultld != null) {
          ca.append(" (").append(resultldtext).append(')');
        }

        if (forReport) {
          result = Functions.escapeHTML(ca.toString(), false, false);

          if (icolor != null) {
            ca.set("<font color=\"").append(server.getColor(icolor).toHexString());
            ca.append("\">").append(result).append("</font>");
            result = ca.toString();
          }
        } else {
          result = ca.toString();
        }
      }

      if (forReport) {
        if (range == null) {
          range = "";
        }

        if (result == null) {
          result = "";
        }

        w.write(Functions.format(labValueFormat, lab, result, range));
        w.write('\n');

        if (component != null) {
          createComponentTable(component, w, null, ca);
        }

        return true;
      }

      if (((result == null) || (result.length() == 0)) && is_document.equals("true")) {
        result = server.getResourceAsString("bv.text.see_report");
      }
      if(server.debug && labld!=null && lab!=null && !uniqueDates.add(date+"-"+labld)) {
        lab="{fgColor:badData}DUPLICATE: "+lab;
      }

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

        if (lab != null) {
          if (labld != null) {
            jw.key("lab").object();
            jw.key("linkedData").value(labld).key("value").value(lab);
            jw.endObject();
          } else {
            jw.key("lab").value(lab);
          }
        }

        if (result != null) {
          if (resultld != null) {
            jw.key("result").object();
            jw.key("linkedData").value(resultld).key("value").value(result);
            jw.endObject();
          } else {
            jw.key("result").value(result);
          }
        }

        if (unit != null) {
          jw.key("unit").value(unit);
        }

        if (range != null) {
          jw.key("range").value(range);
        }

        if (is_document != null) {
          jw.key("is_document").value(is_document);
        }

        if (category != null) {
          if (categoryld != null) {
            jw.key("category").object();
            jw.key("linkedData").value(categoryld).key("value").value(category);
            jw.endObject();
          } else {
            jw.key("category").value(category);
          }
        }

        if (panel != null) {
          if (panelld != null) {
            jw.key("panel").object();
            jw.key("linkedData").value(panelld).key("value").value(panel);
            jw.endObject();
          } else {
            jw.key("panel").value(panel);
          }
        }

//        if(sort_order!=null) {
//          jw.key("sort_order").value(sort_order);
//        }
//        if(result_id!=null) {
//          jw.key("result_id").value(result_id);
//        }
        if (comment != null) {
          jw.key("comment").value(comment);
        }

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

        if (lab != null) {
          if (labld != null) {
            w.write(labld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, lab, ca);
        }

        w.write((char) '^');

        if (result != null) {
          if (resultld != null) {
            w.write(resultld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, result, ca);
        }

        w.write((char) '^');

        if (unit != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, unit, ca);
        }

        w.write((char) '^');

        if (range != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, range, ca);
        }

        w.write((char) '^');

        if (is_document != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, is_document, ca);
        }

        w.write((char) '^');

        if (category != null) {
          if (categoryld != null) {
            w.write(categoryld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, category, ca);
        }

        w.write((char) '^');

        if (panel != null) {
          if (panelld != null) {
            w.write(panelld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, panel, ca);
        }

        w.write((char) '^');
//        if(sort_order!=null) {
//          FHIRUtils.writeQuotedStringIfNecessary(w, sort_order, ca);
//        }
        w.write((char) '^');
//        if(result_id!=null) {
//          FHIRUtils.writeQuotedStringIfNecessary(w, result_id, ca);
//        }
        w.write((char) '^');

        if (comment != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, comment, ca);
        }

        w.write((char) '^');
        w.write((char) '\n');
      }

      parsed = true;
    } while(false);

    if (!parsed) {
      debugLog("Could not parse entry:\n" + entry.toString(2));
    }

    return parsed;
  }
}
