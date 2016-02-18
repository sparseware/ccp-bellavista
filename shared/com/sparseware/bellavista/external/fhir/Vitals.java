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
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Vitals extends aFHIRemoteService {
  protected static boolean                 searchByDateSupported;
  protected static boolean                 searchByCategorySupported;
  protected static boolean                 hasVitals;
  protected static HashSet<String>         bpCodes                          = new HashSet<String>(2);
  protected static HashMap<String, String> bpSysCodes                       = new HashMap<String, String>(2);
  protected static HashMap<String, String> bpDysCodes                       = new HashMap<String, String>(2);
  protected static HashSet<String>         bpComponents                     = new HashSet<String>(4);
  protected static HashSet<String>         weightCodes                      = new HashSet<String>(2);
  protected static HashSet<String>         heightCodes                      = new HashSet<String>(2);
  protected static HashMap<String, String> vitalCodes                       = new HashMap<String, String>();
  protected static boolean                 ignoreObservWithMissingCode      = true;
  protected static boolean                 ignoreObservWithoutLoncOrHL7Code = true;
  protected static String[]                COLUMN_NAMES;

  static {
    bpCodes.add("75367002");
    bpCodes.add("55284-4");
    bpSysCodes.put("271649006", "271650006");
    bpSysCodes.put("8480-6", "8462-4");
    bpSysCodes.put("8459-0", "8453-3");
    bpDysCodes.put("271650006", "271649006");
    bpDysCodes.put("8462-4", "8480-6");
    bpDysCodes.put("8453-3", "8459-0");
    bpComponents.add("271649006");
    bpComponents.add("8480-6");
    bpComponents.add("271650006");
    bpComponents.add("8462-4");
    bpComponents.add("8459-0");
    bpComponents.add("8453-3");
    vitalCodes.put("75367002", "bp");
    vitalCodes.put("55284-4", "bp");
    vitalCodes.put("78564009", "pulse");
    vitalCodes.put("54718008", "pulse");
    vitalCodes.put("8867-4", "pulse");
    vitalCodes.put("415945006", "temp");
    vitalCodes.put("8310-5", "temp");
    vitalCodes.put("8331-1", "temp");
    vitalCodes.put("27113001", "wt");
    vitalCodes.put("3141-9", "wt");
    vitalCodes.put("50373000", "ht");
    vitalCodes.put("8302-2", "ht");
    vitalCodes.put("8890-6", "hr");
    vitalCodes.put("8867-4", "hr");
    vitalCodes.put("8478-0", "map");
    vitalCodes.put("59408-5", "spo2");
    vitalCodes.put("2710-2", "spo2");
    vitalCodes.put("9279-1", "resp");
  }

  private static String searchParamsEx;

  public Vitals() {
    super("Observation");

    if (COLUMN_NAMES == null) {
      COLUMN_NAMES = new String[] {
        "date", "vital", "result", "unit", "range", "sort_order", "result_id"
      };

      FHIRResource r = server.getResource(resourceName);

      hasVitals = r != null;

      JSONArray a = (r == null)
                    ? null
                    : r.searchParams;

      searchByDateSupported     = (a == null)
                                  ? false
                                  : a.findJSONObjectIndex("name", "date", 0) != -1;
      searchByCategorySupported = (a == null)
                                  ? false
                                  : a.findJSONObjectIndex("name", "category", 0) != -1;
      searchParamsEx            = (String) server.getServerConfig().opt("fhir", "vitals", "search_params");
    }

    searchParams = searchParamsEx;
    columnNames  = COLUMN_NAMES;
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    list(conn, path, data, headers, -1);
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers, int days)
          throws IOException {
    if (!hasVitals) {
      dataNotAvailable(conn, path, true, headers, columnNames, 1);

      return;
    }

    String from = null;
    String to   = null;

    if ((days > 0) && searchByDateSupported && prefixSearchSupported) {
      Calendar      c  = Calendar.getInstance();
      StringBuilder sb = new StringBuilder();

      sb.append("lt").append(FHIRServer.convertToServerDate(c, false));
      to = sb.toString();
      c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - days);
      c.set(Calendar.HOUR_OF_DAY, 0);
      sb.setLength(0);
      sb.append("gt").append(FHIRServer.convertToServerDate(c, false));
      from = sb.toString();
    }

    ActionLink l;

    if (from == null) {
      l = createSearchLink("patient", server.getPatientID(), "_count", "500");
    } else {
      l = createSearchLink("patient", server.getPatientID(), "date", from, "date", to);
    }

    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers);
    } finally {
      l.close();
    }
  }

  public void document(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    ActionLink l = createReadLink(path.shift());

    try {
      read(l.getReader(), conn.getContentWriter(), headers);
    } finally {
      l.close();
    }
  }

  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
    if (entry.optString("resourceType").equals("DiagnosticReport")) {
      FHIRUtils.writeHTMLDocumentStart(w, null);

      JSONObject text = entry.optJSONObject("text");

      if (text != null) {
        FHIRUtils.writeText(w, text, false, true);
      } else {
        w.append(getResourceAsString("bv.text.blank_report"));
      }

      FHIRUtils.writeHTMLDocumentFinish(w);
    }

    JSONObject o = entry.optJSONObject("valueAttachment");

    if (o != null) {
      FHIRUtils.writeAttachment(o, w, false);

      return;
    }

    o = entry.optJSONObject("valueSampledData");
    FHIRUtils.writeHTMLDocumentStart(w, null);

    if (o != null) {
      MedicalCode mc = FHIRUtils.getMedicalCode(o.optJSONArray("coding"));

      if (mc == null) {
        String text = o.optString("text", null);

        if (text != null) {
          mc = new MedicalCode("UNK", null, text, null);
        }
      }

      if (mc != null) {
        FHIRUtils.writeDocumentTitle(w, mc.getBestText());
      }

      FHIRUtils.writeTableStart(w);
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.origin"), o.optString("origin"));
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.period"), o.optString("period"));

      String s = o.optString("factor", null);

      if (s != null) {
        FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.factor"), s);
      }

      s = o.optString("lowerLimit", null);

      if (s != null) {
        FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.lower_limit"), s);
      }

      s = o.optString("upperLimit", null);

      if (s != null) {
        FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.upper_limit"), s);
      }

      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.dimensions"), o.optString("dimensions"));
      FHIRUtils.writeTableValue(w, server.getResourceAsString("bv.text.values"), o.optString("data"));
      FHIRUtils.writeTableFinish(w);
    } else {
      w.append(getResourceAsString("bv.text.blank_report"));
    }

    FHIRUtils.writeHTMLDocumentFinish(w);
  }

  public void search(Reader r, Object writer, HttpHeaders headers, Object... params) throws IOException {
    HashMap<String, JSONObject> bpcomponents = new HashMap<String, JSONObject>();
    ArrayList<BloodPressure>    bps          = new ArrayList<BloodPressure>();

    super.search(r, writer, headers, bps, bpcomponents);
  }

  protected void parsingComplete(JSONWriter jw, Writer w, CharArray ca, Object... params) throws IOException {
    Map<String, JSONObject> bpcomponents = (HashMap<String, JSONObject>) params[1];

    if (!bpcomponents.isEmpty()) {
      for (JSONObject o : bpcomponents.values()) {
        processEntryEx(o, jw, w, ca, null, null);
      }
    }
  }

  @Override
  public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... params)
          throws IOException {
    if (!entry.optString("resourceType").equals(resourceName)) {
      return;
    }

    List<BloodPressure>     bps          = (List<BloodPressure>) params[0];
    Map<String, JSONObject> bpcomponents = (HashMap<String, JSONObject>) params[1];

    processEntryEx(entry, jw, w, ca, bps, bpcomponents);

    int foundIndex = -1;
    int len        = bps.size();

    for (int i = 0; i < len; i++) {
      BloodPressure bp = bps.get(i);
      JSONObject    o  = bp.updateEntry(bpcomponents, false, ca);

      if (o != null) {
        processEntryEx(o, jw, w, ca, null, null);
        foundIndex = i;

        break;
      }
    }

    if (foundIndex != -1l) {
      bps.remove(foundIndex);
    }
  }

  protected void processEntryEx(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, List<BloodPressure> bps,
                                Map<String, JSONObject> bpcomponents)
          throws IOException {
    JSONObject o;
    String     status = entry.optString("status");

    if (status.equals("entered-in-error") || status.equals("cancelled")) {
      return;
    }

    boolean parsed = false;
    String  msg    = null;

    do {
      String dateld       = null;
      String date         = null;
      String vitalld      = null;
      String vital        = null;
      String resultld     = null;
      String resultldtext = null;
      String result       = null;
      String unit         = null;
      String range        = null;
      //      String sort_order=null;
      //      String result_id=null;

      dateld = getID(entry);
      o      = entry.optJSONObject("code");

      JSONArray   coding = (o == null)
                           ? null
                           : o.optJSONArray("coding");
      MedicalCode mc     = FHIRUtils.getMedicalCode(coding);

      if (mc == null) {
        if (ignoreObservWithMissingCode) {
          parsed = true;

          break;
        }

        String text = (o == null)
                      ? null
                      : o.optString("text", null);

        if (text != null) {
          mc = new MedicalCode("UNK", null, text, null);
        } else {
          if (mc == null) {
            if (server.isDebugMode()) {
              mc = missingInvalid;
            } else {
              throw missingRequiredData("code", dateld);
            }
          }
        }
      }

      if ((bpcomponents != null) && (bpComponents != null) && mc.isOneOf(bpComponents)) {
        bpcomponents.put(dateld, entry);
        parsed = true;

        break;
      }

      if ((bps != null) && mc.isOneOf(bpCodes)) {
        JSONArray a = entry.optJSONArray("component");

        if ((a != null) && (a.length() == 2)) {
          JSONObject o1 = a.getJSONObject(0);
          JSONObject o2 = a.getJSONObject(1);

          entry.put("valueQuantity", BloodPressure.createValue(o1, o2, ca));
          entry.remove("component");
          date = getDateTime(o1);
          entry.put("appliesDateTime", date);
        } else {
          a = entry.optJSONArray("related");

          if ((a != null) && (a.length() == 2)) {
            String ref1 = a.getJSONObject(0).getJSONObject("target").optString("reference");
            String ref2 = a.getJSONObject(1).getJSONObject("target").optString("reference");
            int    n    = ref1.lastIndexOf('/');

            if (n != -1) {
              ref1 = ref1.substring(n + 1);
            }

            n = ref2.lastIndexOf('/');

            if (n != -1) {
              ref2 = ref2.substring(n + 1);
            }

            JSONObject o1 = bpcomponents.get(ref1);
            JSONObject o2 = bpcomponents.get(ref2);

            if ((o1 != null) && (o2 != null)) {
              entry.put("valueQuantity", BloodPressure.createValue(o1, o2, ca));
              bpcomponents.remove(ref1);
              bpcomponents.remove(ref2);
            } else {
              bps.add(new BloodPressure(entry, ref1, ref2));
              parsed = true;

              break;
            }
          }
        }
      }

      JSONArray contained = entry.optJSONArray("contained");

      if ((contained != null) && (contained.length() > 0)) {
        int len = contained.length();

        for (int i = 0; i < len; i++) {
          processEntryEx(contained.getJSONObject(i), jw, w, ca, bps, bpcomponents);
        }

        parsed = true;

        break;
      }

      date  = getDateTime(entry);
      vital = o.optString("text", null);

      if (vital == null) {
        vital = mc.getBestText();
      }

      vitalld = (mc == null)
                ? ""
                : mc.getBestCode();
      o       = entry.optJSONObject("valueQuantity");

      if (o != null) {
        unit   = o.optString("units", null);
        result = o.optString("value");

        if ("cel".equalsIgnoreCase(o.optString("code"))) {
          float f = Functions.floatValue(result);

          if (f > 0) {
            f = f * 1.8f + 32f;

            SNumber num = new SNumber(f);

            num.setScale(2);
            result = num.toString();
          }

          unit = "DegF";
        }

        String comparator = o.optString("comparator", null);

        if (comparator != null) {
          result = ca.set(comparator).append(result).toString();
        }
      } else {
        result = FHIRUtils.getValue(entry, false, ca);
      }

      JSONArray a = entry.optJSONArray("component");

      if (a != null) {
        String s = getComponents(a, ca);

        if (result == null) {
          result = s;
        } else {
          result = ca.set(result).append("; ").append(s).toString();
        }
      }

      if (result == null) {
        if (server.isDebugMode()) {
          result = MISSING_INVALID;
        } else {
          throw missingRequiredData("result", dateld);
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

      a     = entry.optJSONArray("referenceRange");
      range = (a == null)
              ? null
              : FHIRUtils.getRange(a, true, ca);

      //String comment = entry.optString("comments",null);
      String s = vitalCodes.get(vitalld);

      if (s != null) {
        vitalld = s;
      }

      String icolor = (resultld == null)
                      ? null
                      : FHIRUtils.getInterpretationColor(resultld);

      if ((result != null) && ((icolor != null) || (resultld != null))) {
        ca.setLength(0);

        if (icolor != null) {
          ca.append("{fgColor:").append(icolor).append("}");
        }

        ca.append(result);

        if (resultld != null) {
          ca.append(" (").append(resultldtext).append(')');
        }

        result = ca.toString();
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

        if (vital != null) {
          if (vitalld != null) {
            jw.key("vital").object();
            jw.key("linkedData").value(vitalld).key("value").value(vital);
            jw.endObject();
          } else {
            jw.key("vital").value(vital);
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

        //          if(sort_order!=null) {
        //            jw.key("sort_order").value(sort_order);
        //          }
        //          if(result_id!=null) {
        //            jw.key("result_id").value(result_id);
        //          }
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

        if (vital != null) {
          if (vitalld != null) {
            w.write(vitalld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, vital, ca);
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
        //          if(sort_order!=null) {
        //            FHIRUtils.writeQuotedStringIfNecessary(w, sort_order, ca);
        //          }
        w.write((char) '^');
        //          if(result_id!=null) {
        //            FHIRUtils.writeQuotedStringIfNecessary(w, result_id, ca);
        //          }
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

  public void most_recent(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    list(conn, path, data, headers, 7);
  }

  public void realtime(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, true, headers);
  }

  public void summary(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    list(conn, path, data, headers, 7);
  }

  static class BloodPressure {
    public JSONObject entry;
    public String     reference1;
    public String     reference2;

    public BloodPressure(JSONObject entry, String reference1, String reference2) {
      super();
      this.entry      = entry;
      this.reference1 = reference1;
      this.reference2 = reference2;
    }

    public JSONObject updateEntry(Map<String, JSONObject> components, boolean finalPass, CharArray ca)
            throws IOException {
      JSONObject e1 = components.get(reference1);
      JSONObject e2 = components.get(reference2);

      if (!finalPass && ((e1 == null) || (e2 == null))) {
        return null;
      }

      if ((e1 == null) && (e2 == null)) {
        FHIRServer.getInstance().debugLog("Could not find references for BP:" + entry.optString("id"));

        return null;
      }

      if (e1 == null) {
        FHIRServer.getInstance().debugLog("Could not find reference: " + reference1 + "for BP:"
                                          + entry.optString("id"));

        return e2;
      }

      if (e2 == null) {
        FHIRServer.getInstance().debugLog("Could not find reference: " + reference2 + "for BP:"
                                          + entry.optString("id"));

        return e1;
      }

      components.remove(reference1);
      components.remove(reference2);
      entry.put("valueQuantity", createValue(e1, e2, ca));

      return entry;
    }

    public static JSONObject createValue(JSONObject e1, JSONObject e2, CharArray ca) throws IOException {
      JSONObject o      = new JSONObject();
      JSONObject sys    = null;
      JSONObject dys    = null;
      JSONArray  coding = e1.getJSONObject("code").optJSONArray("coding");
      String     code   = FHIRUtils.getBestMedicalCode(coding);

      if (bpSysCodes.containsKey(code)) {
        sys = e1;
      } else {
        dys = e1;
      }

      coding = e2.getJSONObject("code").optJSONArray("coding");
      code   = FHIRUtils.getBestMedicalCode(coding);

      if (bpDysCodes.containsKey(code)) {
        dys = e2;
      } else {
        sys = e2;
      }

      String s = FHIRUtils.getValue(sys, false, ca);
      String d = FHIRUtils.getValue(dys, false, ca);

      if (s == null) {
        s = FHIRUtils.getBestMedicalText(sys.optJSONObject("dataAbsentReason"));
      }

      if (d == null) {
        d = FHIRUtils.getBestMedicalText(dys.optJSONObject("dataAbsentReason"));
      }

      o.put("value", s + "/" + d);

      return o;
    }
  }


  String getComponents(JSONArray components, CharArray ca) {
    int           len = components.length();
    StringBuilder sb  = new StringBuilder();

    for (int i = 0; i < len; i++) {
      JSONObject o = components.getJSONObject(i);
      String     s = FHIRUtils.getValue(o, false, ca);

      if (s == null) {
        continue;
      }

      if (i > 0) {
        sb.append(";");
      }

      sb.append(FHIRUtils.getBestMedicalText(o.optJSONObject("code")));
      sb.append('=').append(s);
    }

    return sb.toString();
  }
}
