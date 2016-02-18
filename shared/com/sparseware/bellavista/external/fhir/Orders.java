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
import com.sparseware.bellavista.external.fhir.FHIRUtils.MedicalCode;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public class Orders extends aFHIRemoteService {
  public static String      TYPE_MEDS                    = "meds";
  public static String      TYPE_NUTRITION               = "nutrition";
  public static String      TYPE_DIAGNOSTIC              = "diagnostic";
  boolean                   generateMedicationReport     = true;
  boolean                   generateDisgnosticRreport    = true;
  boolean                   generateNutritionReport      = true;
  boolean                   generateMedicationDirections = false;
  protected static String[] COLUMN_NAMES;

  public Orders() {
    this("MedicationOrder");
  }

  public Orders(String resourceName) {
    super(resourceName);
    columnNames = new String[] {
      "type", "ordered_item", "start_date", "stop_date", "refills", "quantity", "unit_per_dose", "status", "lastfill",
      "directions", "notes", "prn", "category", "clinical_category", "provider", "signed", "flagged"
    };

    if (server.serverConfig != null) {
      JSONObject o = (JSONObject) server.serverConfig.opt("fhir", "orders");

      if (o != null) {
        generateMedicationReport  = server.serverConfig.optBoolean("generateMedicationReport",
                generateMedicationReport);
        generateDisgnosticRreport = server.serverConfig.optBoolean("generateDisgnosticRreport",
                generateDisgnosticRreport);
        generateNutritionReport      = server.serverConfig.optBoolean("generateNutritionReport",
                generateNutritionReport);
        generateMedicationDirections = server.serverConfig.optBoolean("generateMedicationDirections",
                generateMedicationDirections);
        searchParams = o.optString("search_params", null);
      }
    }
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    ActionLink l = createSearchLink("patient", server.getPatientID(), "status", "active");

    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers);
    } finally {
      l.close();
    }
  }

  public void medications(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    ActionLink l       = createSearchLink("patient", server.getPatientID(), "status", "active");
    Boolean    summary = path.shift() != null;

    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers, summary);
    } finally {
      l.close();
    }
  }

  public void category(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    String cat = path.shift();

    if (cat.equals(TYPE_DIAGNOSTIC)) {
      DiagnosticOrders diagnosticOrders = new DiagnosticOrders();

      diagnosticOrders.list(conn, path, data, headers);

      return;
    }

    if (cat.equals(TYPE_NUTRITION)) {
      NutritionOrders nutritionOrders = new NutritionOrders();

      nutritionOrders.list(conn, path, data, headers);

      return;
    }

    ActionLink l = createSearchLink("patient", server.getPatientID());

    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers, path.pop());
    } finally {
      l.close();
    }
  }

  @Override
  protected void parsingComplete(JSONWriter jw, Writer w, CharArray ca, Object... params) throws IOException {
    if (((params == null) || (params.length == 0)) && resourceName.equals("MedicationOrder")) {
      String type = null;
      String desc = null;

      if (server.getResource("NutritionOrder") != null) {
        type = TYPE_NUTRITION;
        desc = getResourceAsString("bv.text.nutrition_orders");

        if (desc == null) {
          desc = "Nutrition Orders";
        }

        writeCategory(type, desc, jw, w);
      }

      if (server.getResource("DiagnosticOrder") != null) {
        type = TYPE_DIAGNOSTIC;
        desc = getResourceAsString("bv.text.diagnostic_orders");

        if (desc == null) {
          desc = "Diagnostic Orders";
        }

        writeCategory(type, desc, jw, w);
      }
    }

    super.parsingComplete(jw, w, ca, params);
  }

  private MedicalCode getMedication(JSONObject entry, boolean forRead) throws IOException {
    JSONObject o              = entry.optJSONObject("medicationCodeableConcept");
    String     ordered_item   = null;
    String     ordered_itemld = null;

    if (o == null) {
      o = entry.optJSONObject("medicationReference");

      if (o != null) {
        ordered_item = o.optString("display");

        if (ordered_item.length() == 0) {
          try {
            o = getReference(o.optString("reference"));
            o = o.optJSONObject("code");
          } catch(Exception e) {
            if (server.isDebugMode()) {
              o              = entry.optJSONObject("medicationReference");
              ordered_itemld = BAD_ID;

              if (forRead) {
                ordered_item = "BAD FHIR REF: " + o.optString("reference");
              } else {
                ordered_item = "{fgColor:badData}BAD FHIR REFERENCE: " + o.optString("reference");
              }

              o = null;
            } else {
              if (e instanceof IOException) {
                throw(IOException) e;
              }

              throw new IOException(e);
            }
          }
        } else {
          ordered_itemld = server.getID(o.optString("reference"), false);
          o              = null;    //so the logic below doesn't execute
        }
      }
    }

    if (o != null) {
      return FHIRUtils.getMedicalCode(o);
    }

    return new MedicalCode(ordered_itemld, null, ordered_item, null);
  }

  private static void writeCategory(String type, String desc, JSONWriter jw, Writer w) throws IOException {
    if (jw != null) {
      jw.object();
      jw.key("value").value(type);
      jw.key("status").value("active");
      jw.key("category").value(desc);
      jw.endObject();
    } else {
      w.write('|');
      w.write(type);
      w.write("^^^^^^Active^^^^^");
      w.write(desc);
      w.write("^\n");
    }
  }

  public void order(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    String id = path.shift();

    if (id.startsWith(TYPE_DIAGNOSTIC + "_")) {
      DiagnosticOrders o = new DiagnosticOrders();

      path.unshift(id.substring(TYPE_DIAGNOSTIC.length() + 1));
      o.order(conn, path, data, headers);
    } else if (id.startsWith(TYPE_NUTRITION + "_")) {
      NutritionOrders o = new NutritionOrders();

      path.unshift(id.substring(TYPE_NUTRITION.length() + 1));
      o.order(conn, path, data, headers);
    } else {
      ActionLink l = createReadLink(id);

      try {
        headers.setDefaultResponseHeaders();
        headers.mimeHtml();
        read(l.getReader(), conn.getContentWriter(), headers);
      } finally {
        l.close();
      }
    }
  }

  static class DosageInstructions {
    boolean   asNeeded;
    String    asNeededText;
    CharArray directions = new CharArray();
    String    additionalDirections;
    String    bodySite;
    String    dose;
    String    route;
    String    method;
    String    rate;
    String    maxDoses;
    String    startDate;
    String    stopDate;
    String    text;

    public void getText(CharArray ca, boolean generate) {
      if ((text == null) || generate) {
        generateText(ca);
      } else {
        ca.append(text);
      }
    }

    public void generateText(CharArray ca) {
      if (directions.length() > 0) {
        ca.append(directions);
      }

      int len = ca.trim().length();

      if (len > 1) {
        char c = ca.charAt(len - 1);

        if ((c == ',') || (c == ';')) {
          ca.setLength(len - 1);
        }
      }

      if (asNeeded) {
        ca.append(", PRN");

        if (asNeededText != null) {
          ca.append(": ").append(asNeededText);
        }
      }

      if (additionalDirections != null) {
        ca.append(", ");
        ca.append(additionalDirections);
      }
    }
  }


  protected DosageInstructions getDosageInstructions(JSONObject o, CharArray ca, boolean summary) throws IOException {
    DosageInstructions di = new DosageInstructions();
    String             s;
    CharArray          directions = di.directions;

    di.text = o.optString("text", null);

    JSONObject oo = o.optJSONObject("additionalInstructions");

    if (oo != null) {
      MedicalCode mc = FHIRUtils.getMedicalCode(oo);

      s = null;

      if (mc != null) {
        s = mc.getBestText();

        if ((s == null) || s.equals(mc.getBestCode())) {
          s = server.timingWhen.get(mc.getBestCode());
        }

        if (s == null) {
          s = mc.getBestText();
        }
      } else {
        s = oo.optString("text", null);
      }

      di.additionalDirections = s;
    }

    oo = o.optJSONObject("asNeededCodeableConcept");

    if (oo != null) {
      di.asNeededText = FHIRUtils.getBestMedicalText(oo);
      di.asNeeded     = Boolean.TRUE;
    } else {
      di.asNeeded = o.optBoolean("asNeededBoolean");
    }

    oo = o.optJSONObject("doseQuantity");

    if (oo != null) {
      di.dose = FHIRUtils.getQuantity(oo, ca);
    } else {
      oo = o.optJSONObject("doseRange");

      if (oo != null) {
        di.dose = FHIRUtils.getRange(oo, true, ca);
      }
    }

    oo = o.optJSONObject("route");

    if (oo != null) {
      di.route = FHIRUtils.getBestMedicalText(oo);
    }

    oo = o.optJSONObject("timing");

    if (oo != null) {
      if (directions.length() > 0) {
        directions.append("; ");
      }

      if (summary) {
        directions.append(FHIRUtils.getTiming(oo, di.dose, di.route, true, ca));
      } else {
        directions.append(FHIRUtils.getTiming(oo, null, null, false, ca));
      }

      di.startDate = oo.optString("_startDate", null);
      di.stopDate  = oo.optString("_stopDate", null);
    } else {
      s = o.optString("text", null);

      if (s != null) {
        if (directions.length() > 0) {
          directions.append("; ");
        }

        directions.append(s);
      }
    }

    if (!summary) {
      oo = o.optJSONObject("siteCodeableConcept");

      if (oo != null) {
        di.bodySite = FHIRUtils.getBestMedicalText(oo);
      } else {
        oo = o.optJSONObject("siteReference");

        if (oo != null) {
          s = oo.optString("description", null);

          if (s == null) {
            oo = oo.optJSONObject("code");

            if (oo != null) {
              s = FHIRUtils.getBestMedicalText(oo);
            }
          }

          if (s != null) {
            di.bodySite = s;
          }
        }
      }

      oo = o.optJSONObject("method");

      if (oo != null) {
        di.method = FHIRUtils.getBestMedicalText(oo);
      }

      oo = o.optJSONObject("rateRatio");

      if (oo != null) {
        di.rate = FHIRUtils.getRatio(oo, ca);
      } else {
        oo = o.optJSONObject("rateRange");

        if (oo != null) {
          di.rate = FHIRUtils.getRange(oo, true, ca);
        }
      }

      oo = o.optJSONObject("maxDosePerPeriod");

      if (oo != null) {
        di.maxDoses = FHIRUtils.getRatio(oo, ca);
      }
    }

    return di;
  }

  void writeDosageInstructions(Writer w, DosageInstructions di, CharArray sb) throws IOException {
    FHIRUtils.writeTableStart(w);

    String s;

    sb.setLength(0);

    if (di.directions.length() > 0) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.schedule"), di.directions.toString());
    }

    if (di.asNeeded) {
      if (di.asNeededText != null) {
        s = String.format(getResourceAsString("bv.text.prn_value"), di.asNeededText);
      } else {
        s = getResourceAsString("bv.text.prn_yes");
      }

      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.prn"), s);
    }

    if (di.additionalDirections != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.additional_instructions"), di.additionalDirections);
    }

    if (di.bodySite != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.body_site"), di.bodySite);
    }

    if (di.route != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.route"), di.route);
    }

    if (di.method != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.method"), di.method);
    }

    if (di.dose != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.dosage"), di.dose);
    }

    if (di.rate != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.rate"), di.rate);
    }

    if (di.maxDoses != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.max_dose_per_period"), di.maxDoses);
    }

    s = null;

    if (di.startDate != null) {
      if (di.stopDate != null) {
        s = String.format(server.getResourceAsString("bv.timing.format.bounds_start_stop"), di.startDate, di.stopDate);
      } else {
        s = String.format(server.getResourceAsString("bv.timing.format.bounds_start"), di.startDate);
      }
    } else if (di.stopDate != null) {
      s = String.format(server.getResourceAsString("bv.timing.format.bounds_stop"), di.stopDate);
    }

    if (s != null) {
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.validity_period"), s);
    }

    FHIRUtils.writeTableFinish(w);
  }

  @SuppressWarnings("resource")
  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
    FHIRUtils.writeHTMLDocumentStart(w, null);

    JSONObject text   = entry.optJSONObject("text");
    String     div    = (text == null)
                        ? null
                        : text.optString("div", null);
    String     status = (text == null)
                        ? ""
                        : text.optString("status");

    if (status.equals("empty")) {
      div = null;
    }

    boolean generated = (div == null)
                        ? false
                        : status.equals("generated");

    if ((div != null) &&!generated &&!status.equals("additional")) {
      FHIRUtils.writeText(w, text, false, true);
    } else {
      CharArray ca = new CharArray();

      if ((div == null) || generateMedicationReport) {
        try {
          JSONObject  o;
          JSONObject  oo;
          String      ordered_item = null;
          MedicalCode mc           = getMedication(entry, true);

          ordered_item = mc.getBestText();

          String    startDate  = null;
          String    stopDate   = null;
          String    refills    = null;
          String    quantity   = null;
          CharArray directions = new CharArray();
          String    provider   = null;

          o = entry.optJSONObject("prescriber");

          if (o != null) {
            provider = o.optString("display", null);
          }

          FHIRUtils.writeDocumentTitle(w, getResourceAsString("bv.text.medication_order"));
          FHIRUtils.writeTableStart(w);
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.medication"), ordered_item);

          JSONArray          a     = entry.optJSONArray("dosageInstruction");
          int                len   = (a == null)
                                     ? 0
                                     : a.length();
          DosageInstructions dia[] = new DosageInstructions[len];

          for (int i = 0; i < len; i++) {
            oo = a.getJSONObject(i);

            DosageInstructions di = getDosageInstructions(oo, ca, false);

            dia[i] = di;

            if (directions.length() > 0) {
              directions.append("; ");
            }

            directions.append(di.text);
          }

          if ((directions != null) && (directions.length() > 0)) {
            FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.directions"), directions.toString());
          }

          String s = entry.optString("status", null);

          if (s != null) {
            FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.status"), s);
          }

          if (provider != null) {
            FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.prescriber"), provider);
          }

          s = entry.optString("dateWritten", null);

          if (s != null) {
            s = FHIRServer.convertDateTime(s);
            FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.date_written"), s);
          }

          s  = null;
          oo = entry.optJSONObject("reasonCodeableConcept");

          if (oo != null) {
            s = FHIRUtils.getBestMedicalText(oo);
          } else {
            oo = entry.optJSONObject("reasonReference");

            if (oo != null) {
              s = oo.optString("display", null);
            }
          }

          if (s != null) {
            FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.reason"), s);
          }

          s = entry.optString("dateEnded", null);

          if (s != null) {
            FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.date_stopped"), s);
          }

          s = entry.optString("reasonEnded", null);

          if (s != null) {
            FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.date_stopped_reason"), s);
          }

          o = entry.optJSONObject("dispenseRequest");

          if (o != null) {
            mc = getMedication(o, true);

            String dordered_item = mc.getBestText();

            quantity = FHIRUtils.getQuantity(o.optJSONObject("quantity"), ca);
            refills  = o.optString("numberOfRepeatsAllowed", null);
            oo       = o.optJSONObject("validityPeriod");

            if (oo != null) {
              if (startDate == null) {
                startDate = oo.optString("start", null);
              }

              if (stopDate == null) {
                stopDate = oo.optString("end", null);
              }
            }

            if ((dordered_item != null) &&!dordered_item.equals(ordered_item)) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.medication"), dordered_item);
            }

            if (quantity != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.quantity"), quantity);
            }

            if (refills != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.refills"), refills);
            }

            s = null;

            if (startDate != null) {
              startDate = FHIRServer.convertDateTime(startDate);

              if (stopDate != null) {
                stopDate = FHIRServer.convertDateTime(stopDate);
                s        = String.format(server.getResourceAsString("bv.timing.format.bounds_start_stop"), startDate,
                                         stopDate);
              } else {
                s = String.format(server.getResourceAsString("bv.timing.format.bounds_start"), startDate);
              }
            } else if (stopDate != null) {
              stopDate = FHIRServer.convertDateTime(stopDate);
              s        = String.format(server.getResourceAsString("bv.timing.format.bounds_stop"), stopDate);
            }

            if (s != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.validity_period"), s);
            }
          }

          FHIRUtils.writeTableFinish(w);
          len = dia.length;

          if (len > 0) {
            FHIRUtils.writeSectionStart(w, getResourceAsString("bv.text.dosage_detail"));

            for (int i = 0; i < len; i++) {
              writeDosageInstructions(w, dia[i], ca);
            }

            if (len > 1) {
              FHIRUtils.writeTableSectionFinish(w);
            }

            FHIRUtils.writeSectionEnd(w);
          }

          if ((div != null) && status.equals("additional")) {
            FHIRUtils.writeText(w, text, true, false);
          }
        } catch(Exception e) {
          server.ignoreException(e);
        }
      } else {
        FHIRUtils.writeText(w, text, false, true);
      }
    }

    FHIRUtils.writeHTMLDocumentFinish(w);
  }

  public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... parms)
          throws IOException {
    if (!entry.optString("resourceType").equals(resourceName)) {
      return;
    }

    Object     v;
    JSONObject o;
    JSONObject oo;
    boolean    parsed  = false;
    boolean    summary = (parms != null) && (parms.length > 0) && parms[0].equals(Boolean.TRUE);

    do {
      String      typeld         = getID(entry);
      String      type           = TYPE_MEDS;
      String      ordered_item   = null;
      String      ordered_itemld = null;
      MedicalCode mc             = getMedication(entry, false);

      ordered_item   = mc.getBestText();
      ordered_itemld = mc.getBestCode();

      String start_date    = null;
      String stop_date     = null;
      String refills       = null;
      String quantity      = null;
      String unit_per_dose = null;
      String status        = entry.optString("status", null);
      //      String lastfill=null;
      String directions = null;
      String prn        = null;
      //      String category=null;
      //      String clinical_category=null;
      String providerld = null;
      String provider   = null;
      //      String signed=null;
      //      String flagged=null;
      JSONArray a   = entry.optJSONArray("dosageInstruction");
      int       len = (a == null)
                      ? 0
                      : a.length();

      for (int i = 0; i < len; i++) {
        oo = a.getJSONObject(i);

        DosageInstructions di = getDosageInstructions(oo, ca, true);

        prn = di.asNeeded
              ? "true"
              : null;

        if (summary) {
          break;
        }

        ca.setLength(0);

        if (directions != null) {
          ca.set(directions).append("; ");
        }

        di.getText(ca, generateMedicationDirections);
        directions = ca.toString();

        if (start_date != null) {
          start_date = di.startDate;
        }

        if (stop_date == null) {
          stop_date = di.stopDate;
        }

        if (unit_per_dose != null) {
          unit_per_dose = di.dose;
        }
      }

      if (summary) {
        if (prn != null) {
          ordered_item += " (PRN)";
        }
      } else {
        o = entry.optJSONObject("prescriber");

        if (o != null) {
          providerld = server.getID(o.optString("reference"), false);
          provider   = o.optString("display");
        }

        o = entry.optJSONObject("dispenseRequest");

        if (o != null) {
          v        = o.opt("quantity", "value");
          quantity = (v == null)
                     ? null
                     : v.toString();
          refills  = o.optString("numberOfRepeatsAllowed");
          oo       = o.optJSONObject("validityPeriod");

          if (oo != null) {
            if (start_date == null) {
              start_date = oo.optString("start", null);
            }

            if (stop_date == null) {
              stop_date = oo.optString("end", null);
            }
          }
        }
      }

      if ((ordered_item == null) || (ordered_item.length() == 0)) {
        if (server.isDebugMode()) {
          ordered_item = MISSING_INVALID;

          if (ordered_itemld == null) {
            ordered_itemld = BAD_ID;
          }
        } else {
          throw missingRequiredData("medication", typeld);
        }
      }

      if (jw != null) {
        jw.object();

        if (summary) {
          if (typeld != null) {
            jw.key("ordered_item").object();
            jw.key("linkedData").value(typeld).key("value").value(ordered_item);
            jw.endObject();
          } else {
            jw.key("ordered_item").value(ordered_item);
          }

          if (status != null) {
            jw.key("status").value(status);
          }
        } else {
          if ((type != null)) {
            if (typeld != null) {
              jw.key("type").object();
              jw.key("linkedData").value(typeld).key("value").value(type);
              jw.endObject();
            } else {
              jw.key("type").value(type);
            }
          }

          if (ordered_item != null) {
            if (ordered_itemld != null) {
              jw.key("ordered_item").object();
              jw.key("linkedData").value(ordered_itemld).key("value").value(ordered_item);
              jw.endObject();
            } else {
              jw.key("ordered_item").value(ordered_item);
            }
          }

          if (status != null) {
            jw.key("status").value(status);
          }

          if (start_date != null) {
            jw.key("start_date").value(start_date);
          }

          if (stop_date != null) {
            jw.key("stop_date").value(stop_date);
          }

          if (refills != null) {
            jw.key("refills").value(refills);
          }

          if (quantity != null) {
            jw.key("quantity").value(quantity);
          }

          if (unit_per_dose != null) {
            jw.key("unit_per_dose").value(unit_per_dose);
          }

          if (status != null) {
            jw.key("status").value(status);
          }

          //          if(lastfill!=null) {
          //            jw.key("lastfill").value(lastfill);
          //          }
          if (directions != null) {
            jw.key("directions").value(directions);
          }

          if (prn != null) {
            jw.key("prn").value(prn);
          }

          //          if(category!=null) {
          //            jw.key("category").value(category);
          //          }
          //          if(clinical_category!=null) {
          //            jw.key("clinical_category").value(clinical_category);
          //          }
          if (provider != null) {
            if (providerld != null) {
              jw.key("provider").object();
              jw.key("linkedData").value(providerld).key("value").value(provider);
              jw.endObject();
            } else {
              jw.key("provider").value(provider);
            }
          }
          //          if(signed!=null) {
          //            jw.key("signed").value(signed);
          //          }
          //          if(flagged!=null) {
          //            jw.key("flagged").value(flagged);
          //          }
        }

        jw.endObject();
      } else {
        if (summary) {
          if (typeld != null) {
            w.write(typeld);
            w.write((char) '|');
          }

          if (ordered_item != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, ordered_item, ca);
          }

          w.write((char) '^');

          if (status != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, status, ca);
          }
        } else {
          if (type != null) {
            if (typeld != null) {
              w.write(typeld);
              w.write((char) '|');
            }

            FHIRUtils.writeQuotedStringIfNecessary(w, type, ca);
          }

          w.write((char) '^');

          if (ordered_item != null) {
            if (ordered_itemld != null) {
              w.write(ordered_itemld);
              w.write((char) '|');
            }

            FHIRUtils.writeQuotedStringIfNecessary(w, ordered_item, ca);
          }

          w.write((char) '^');

          if (start_date != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, start_date, ca);
          }

          w.write((char) '^');

          if (stop_date != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, stop_date, ca);
          }

          w.write((char) '^');

          if (refills != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, refills, ca);
          }

          w.write((char) '^');

          if (quantity != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, quantity, ca);
          }

          w.write((char) '^');

          if (unit_per_dose != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, unit_per_dose, ca);
          }

          w.write((char) '^');

          if (status != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, status, ca);
          }

          w.write((char) '^');
          //          if(lastfill!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, lastfill, ca);
          //          }
          w.write((char) '^');

          if (directions != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, directions, ca);
          }

          w.write((char) '^');

          if (prn != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, prn, ca);
          }

          w.write((char) '^');
          //          if(category!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, category, ca);
          //          }
          w.write((char) '^');
          //          if(clinical_category!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, clinical_category, ca);
          //          }
          w.write((char) '^');

          if (provider != null) {
            if (providerld != null) {
              w.write(providerld);
              w.write((char) '|');
            }

            FHIRUtils.writeQuotedStringIfNecessary(w, provider, ca);
          }

          w.write((char) '^');
          //          if(signed!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, signed, ca);
          //          }
          w.write((char) '^');
          //          if(flagged!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, flagged, ca);
          //          }
          w.write((char) '^');
        }

        w.write((char) '\n');
      }

      parsed = true;
    } while(false);

    if (!parsed) {
      debugLog("Could not parse entry:\n" + entry.toString(2));
    }
  }

  public static class DiagnosticOrders extends Orders {
    static String category;

    public DiagnosticOrders() {
      super("DiagnosticOrder");
      summarySupported = false;

      if (category == null) {
        category = getResourceAsString("bv.text.diagnostic_orders");

        if (category == null) {
          category = "Diagnostic Orders";
        }
      }
    }

    public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
      ActionLink l = createSearchLink("patient", server.getPatientID());

      try {
        Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

        search(l.getReader(), w, headers);
      } finally {
        l.close();
      }
    }

    @Override
    public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
      FHIRUtils.writeHTMLDocumentStart(w, null);
      FHIRUtils.writeDocumentTitle(w, getResourceAsString("bv.text.diagnostic_order"));

      JSONObject text   = entry.optJSONObject("text");
      String     div    = (text == null)
                          ? null
                          : text.optString("div", null);
      String     status = (text == null)
                          ? ""
                          : text.optString("status");

      if (status.equals("empty")) {
        div = null;
      }

      boolean generated = (div == null)
                          ? false
                          : status.equals("generated");

      if ((div != null) &&!generated &&!status.equals("additional")) {
        FHIRUtils.writeText(w, text, false, false);
      } else {
        FHIRUtils.writeTableStart(w);

        JSONObject o = entry.optJSONObject("orderer");

        if (o != null) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.requestor"), o.optString("display"));
        }

        CharArray sb = new CharArray();
        JSONArray a  = entry.optJSONArray("reason");

        if (o != null) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.reason"),
                                    FHIRUtils.concatBestMedicalText(a, null, sb).toString());
        }

        String s = entry.optString("status", null);

        if (s != null) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.status"), s);
        }

        FHIRUtils.writeTableFinish(w);
        a = entry.optJSONArray("item");

        int len = (a == null)
                  ? 0
                  : a.length();

        if ((div == null) || (generateDisgnosticRreport && (len > 0))) {
          w.append("<h3>").append(getResourceAsString("bv.text.ordered_items")).append("</h3>\n");

          for (int i = 0; i < len; i++) {
            w.append("<div class='item_div'>\n");
            sb = writeItem(w, a.getJSONObject(i), sb);
            w.append("\n</div>\n");
          }
        } else {
          w.write(div);
        }

        JSONArray events = entry.optJSONArray("event");

        if (events != null) {
          FHIRUtils.writeEvents(w, events);
        }

        JSONArray notes = entry.optJSONArray("note");

        if (notes != null) {
          FHIRUtils.writeNotes(w, notes);
        }

        if ((div != null) && status.equals("additional")) {
          FHIRUtils.writeText(w, text, true, false);
        }
      }

      w.append("\n</body></html>");
    }

    protected CharArray writeItem(Writer w, JSONObject item, CharArray sb) throws IOException {
      FHIRUtils.writeTableStart(w);
      FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.item"),
                                FHIRUtils.getBestMedicalText(item.optJSONObject("code")));

      JSONArray a   = item.optJSONArray("specimen");
      int       len = (a == null)
                      ? 0
                      : a.length();

      if (len > 0) {
        if (sb == null) {
          sb = new CharArray();
        }

        sb.setLength(0);
        FHIRUtils.concatString(a, "display", sb);
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.specimen"), sb.toString());
      }

      String s = item.optString("status", null);

      if (s != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.status"), s);
      }

      JSONObject o = item.optJSONObject("bodySite");

      if (o != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.body_site"), FHIRUtils.getBestMedicalText(o));
      }

      FHIRUtils.writeTableFinish(w);

      return sb;
    }

    @Override
    public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... parms)
            throws IOException {
      JSONObject o;
      boolean    parsed = false;

      do {
        String    type         = TYPE_DIAGNOSTIC;
        String    typeld       = type + "_" + getID(entry);
        String    ordered_item = null;
        JSONArray a            = entry.optJSONArray("item");
        int       len          = (a == null)
                                 ? 0
                                 : a.length();

        if (len > 0) {
          if (len == 1) {
            ordered_item = FHIRUtils.getBestMedicalText(a.getJSONObject(0));
          } else {
            ordered_item = server.getString("bv.format.text.diagnostic_order", len);
          }
        } else {
          ordered_item = server.getResourceAsString("bv.text.diagnostic_order");
        }

        String status     = entry.optString("status", null);
        String providerld = null;
        String provider   = null;

        o = entry.optJSONObject("orderer");

        if (o != null) {
          providerld = server.getID(o.optString("reference"), false);
          provider   = o.optString("display");
        }

        if (jw != null) {
          jw.object();
          jw.key("type").object();
          jw.key("linkedData").value(typeld).key("value").value(type);
          jw.endObject();

          if (ordered_item != null) {
//        if (ordered_itemld != null) {
//          jw.key("ordered_item").object();
//          jw.key("linkedData").value(ordered_itemld).key("value").value(ordered_item);
//          jw.endObject();
//        } else {
            jw.key("ordered_item").value(ordered_item);
//        }
          }

          if (status != null) {
            jw.key("status").value(status);
          }

          if (provider != null) {
            if (providerld != null) {
              jw.key("provider").object();
              jw.key("linkedData").value(providerld).key("value").value(provider);
              jw.endObject();
            } else {
              jw.key("provider").value(provider);
            }
          }

          jw.key("category").value(category);
          jw.endObject();
        } else {
          if (type != null) {
            if (typeld != null) {
              w.write(typeld);
              w.write((char) '|');
            }

            FHIRUtils.writeQuotedStringIfNecessary(w, type, ca);
          }

          w.write((char) '^');

          if (ordered_item != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, ordered_item, ca);
          }

          w.write("^^^^^^^");

          if (status != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, status, ca);
          }

          w.write("^^^");
          w.write(category);
          w.write("^^^^");

          if (provider != null) {
            if (providerld != null) {
              w.write(providerld);
              w.write((char) '|');
            }

            FHIRUtils.writeQuotedStringIfNecessary(w, provider, ca);
          }

          w.write((char) '^');
          //          if(signed!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, signed, ca);
          //          }
          w.write((char) '^');
          //          if(flagged!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, flagged, ca);
          //          }
          w.write((char) '^');
          w.write((char) '\n');
        }

        parsed = true;
      } while(false);

      if (!parsed) {
        debugLog("Could not parse entry:\n" + entry.toString(2));
      }
    }
  }


  public static class NutritionOrders extends Orders {
    static String category;

    public NutritionOrders() {
      super("NutritionOrder");
      summarySupported = false;

      if (category == null) {
        category = getResourceAsString("bv.text.nutrition_orders");

        if (category == null) {
          category = "Nutrition Orders";
        }
      }
    }

    @Override
    public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
      FHIRUtils.writeHTMLDocumentStart(w, null);
      FHIRUtils.writeDocumentTitle(w, getResourceAsString("bv.text.nutrition_order"));

      JSONObject text   = entry.optJSONObject("text");
      String     div    = (text == null)
                          ? null
                          : text.optString("div", null);
      String     status = (text == null)
                          ? ""
                          : text.optString("status");

      if (status.equals("empty")) {
        div = null;
      }

      boolean generated = (div == null)
                          ? false
                          : status.equals("generated");

      if ((div != null) &&!generated &&!status.equals("additional")) {
        FHIRUtils.writeText(w, text, false, true);
      } else {
        FHIRUtils.writeTableStart(w);

        JSONArray  a;
        JSONObject oo, so;
        JSONObject o = entry.optJSONObject("orderer");

        if (o != null) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.requestor"), o.optString("display"));
        }

        String s = entry.optString("dateTime", null);

        if (s != null) {
          s = FHIRServer.convertDateTime(s);
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.date"), s);
        }

        s = entry.optString("status", null);

        if (s != null) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.status"), s);
        }

        CharArray sb = new CharArray();

        FHIRUtils.concatBestMedicalText(entry.optJSONArray("foodPreferenceModifier"), null, sb);

        if (sb.length() > 0) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.food_preference_modifier"), sb.toString());
        }

        sb.setLength(0);
        FHIRUtils.concatBestMedicalText(entry.optJSONArray("excludeFoodModifier"), null, sb);

        if (sb.length() > 0) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.exclude_food_modifier"), sb.toString());
        }

        FHIRUtils.writeTableFinish(w);

        if ((div == null) || generateNutritionReport) {
          o = entry.optJSONObject("oralDiet");

          if (o != null) {
            FHIRUtils.writeSectionStart(w, getResourceAsString("bv.text.diet"));
            FHIRUtils.writeTableStart(w);
            sb.setLength(0);
            FHIRUtils.concatBestMedicalText(o.optJSONArray("type"), null, sb);

            if (sb.length() > 0) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.type"), sb.toString());
            }

            writeSchedule(w, o.optJSONArray("schedule"), sb);
            a = o.optJSONArray("fluidConsistencyType");

            if ((a != null) && (a.length() > 0)) {
              sb.setLength(0);
              FHIRUtils.concatBestMedicalText(a, null, sb);
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.fluid_consistency"), sb.toString());
            }

            s = o.optString("instruction", null);

            if (s != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.instructions"), s);
            }

            FHIRUtils.writeTableFinish(w);

            for (int n = 0; n < 2; n++) {
              a = o.optJSONArray((n == 0)
                                 ? "nutrient"
                                 : "texture");

              int len = (a == null)
                        ? 0
                        : a.length();

              if (len > 1) {
                FHIRUtils.writeSubSectionStart(w, getResourceAsString((n == 0)
                        ? "bv.text.nutrients"
                        : "bv.text.textures"));

                for (int i = 0; i < len; i++) {
                  writeNutruentOrTexture(w, a.getJSONObject(i), sb, n == 0, len == 1);
                }

                FHIRUtils.writeSectionEnd(w);
              } else if (len == 1) {
                writeNutruentOrTexture(w, a.getJSONObject(0), sb, n == 0, len == 1);
              }
            }

            FHIRUtils.writeSectionEnd(w);
          }

          a = entry.optJSONArray("supplement");

          int len = (a == null)
                    ? 0
                    : a.length();

          if (len > 0) {
            FHIRUtils.writeSectionStart(w, getResourceAsString("bv.text.supplements"));

            for (int i = 0; i < len; i++) {
              writeSuppliment(w, a.getJSONObject(i), sb, len == 1);
            }

            FHIRUtils.writeSectionEnd(w);
          }

          o = entry.optJSONObject("enteralFormula");

          if (o != null) {
            FHIRUtils.writeSectionStart(w, getResourceAsString("bv.text.external_formula"));
            FHIRUtils.writeTableStart(w);
            oo = o.optJSONObject("baseFormulaType");

            if (o != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.formula_type"),
                                        FHIRUtils.getBestMedicalText(oo));
            }

            s = o.optString("baseFormulaProductName", null);

            if (s != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.formula"), s);
            }

            oo = o.optJSONObject("additiveType");

            if (o != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.additive_type"),
                                        FHIRUtils.getBestMedicalText(oo));
            }

            s = o.optString("additiveProductName", null);

            if (s != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.additive"), s);
            }

            so = o.optJSONObject("caloricDensity");

            if (so != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.caloric_density"),
                                        FHIRUtils.getQuantity(so, sb));
            }

            oo = o.optJSONObject("routeofAdministration");

            if (o != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.route_of_administration"),
                                        FHIRUtils.getBestMedicalText(oo));
            }

            a   = o.optJSONArray("administration");
            len = (a == null)
                  ? 0
                  : a.length();

            if (len > 0) {
              FHIRUtils.writeTableSectionStart(w, getResourceAsString("bv.text.administration"));

              for (int i = 0; i < len; i++) {
                writeAdministration(w, a.getJSONObject(i), sb);
              }

              FHIRUtils.writeTableSectionFinish(w);
            }

            oo = o.optJSONObject("maxVolumeToDeliver");

            if (oo != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.max_volume_to_deliver"),
                                        FHIRUtils.getQuantity(oo, sb));
            }

            s = o.optString("administrationInstruction", null);

            if (s != null) {
              FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.instructions"), s);
            }

            FHIRUtils.writeTableFinish(w);
            FHIRUtils.writeSectionEnd(w);
          }

          if ((div != null) && status.equals("additional")) {
            FHIRUtils.writeText(w, text, true, false);
          }
        } else {
          FHIRUtils.writeText(w, text, true, true);
        }
      }

      w.append("\n</body></html>");
    }

    void writeSchedule(Writer w, JSONArray a, CharArray sb) throws IOException {
      int len = (a == null)
                ? 0
                : a.length();

      if (len == 1) {
        sb.setLength(0);
        FHIRUtils.getTiming(a.getJSONObject(0), sb);
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.schedule"), sb.toString());
      } else if (len > 1) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.schedule"), "");
        FHIRUtils.writeListStart(w);

        for (int i = 0; i < len; i++) {
          sb.setLength(0);
          FHIRUtils.getTiming(a.getJSONObject(i), sb);
          FHIRUtils.writeListValue(w, sb.toString());
        }

        FHIRUtils.writeListFinish(w);
      }
    }

    void writeNutruentOrTexture(Writer w, JSONObject o, CharArray sb, boolean nutrient, boolean onlyOne)
            throws IOException {
      if (onlyOne) {
        FHIRUtils.writeSubSectionStart(w, getResourceAsString(nutrient
                ? "bv.text.nutrients"
                : "bv.text.textures"));
      } else {
        FHIRUtils.writeSubSubSectionStart(w, getResourceAsString(nutrient
                ? "bv.text.nutrient"
                : "bv.text.texture"));
      }

      FHIRUtils.writeTableStart(w);

      JSONObject oo = o.optJSONObject("modifier");

      if (oo != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.modifier"), FHIRUtils.getBestMedicalText(oo));
      }

      if (nutrient) {
        oo = o.optJSONObject("amount");

        if (oo != null) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.quantity"), FHIRUtils.getQuantity(oo, sb));
        }
      } else {
        oo = o.optJSONObject("foodType");

        if (oo != null) {
          FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.food_type"), FHIRUtils.getBestMedicalText(oo));
        }
      }

      FHIRUtils.writeTableFinish(w);
      FHIRUtils.writeSectionEnd(w);
    }

    void writeSuppliment(Writer w, JSONObject o, CharArray sb, boolean onlyOne) throws IOException {
      if (!onlyOne) {
        FHIRUtils.writeSubSubSectionStart(w, getResourceAsString("bv.text.supplement"));
      }

      FHIRUtils.writeTableStart(w);
      sb.setLength(0);

      JSONObject oo = o.optJSONObject("type");

      if (oo != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.type"), FHIRUtils.getBestMedicalText(oo));
      }

      String s = o.optString("productName", null);

      if (s != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.product_name"), s);
      }

      writeSchedule(w, o.optJSONArray("schedule"), sb);
      oo = o.optJSONObject("quantity");

      if (oo != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.quantity"), FHIRUtils.getQuantity(oo, sb));
      }

      s = o.optString("instruction", null);

      if (s != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.instructions"), s);
      }

      FHIRUtils.writeTableFinish(w);
      FHIRUtils.writeSectionEnd(w);
    }

    void writeAdministration(Writer w, JSONObject o, CharArray sb) throws IOException {
      FHIRUtils.writeTableStart(w);
      sb.setLength(0);

      JSONObject oo = o.optJSONObject("schedule");

      if (oo != null) {
        sb.setLength(0);
        FHIRUtils.getTiming(oo, sb);
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.schedule"), sb.toString());
      }

      oo = o.optJSONObject("quantity");

      if (oo != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.quantity"), FHIRUtils.getQuantity(oo, sb));
      }

      String s = null;

      oo = o.optJSONObject("rateQuantity");

      if (oo != null) {
        s = FHIRUtils.getQuantity(oo, sb);
      } else {
        oo = o.optJSONObject("rateRatio");

        if (oo != null) {
          s = FHIRUtils.getRatio(oo, sb);
        }
      }

      if (s != null) {
        FHIRUtils.writeTableValue(w, getResourceAsString("bv.text.rate"), s);
      }

      FHIRUtils.writeTableFinish(w);
    }

    public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
      ActionLink l = createSearchLink("patient", server.getPatientID());

      try {
        Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

        search(l.getReader(), w, headers);
      } finally {
        l.close();
      }
    }

    @Override
    public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... parms)
            throws IOException {
      JSONObject o;
      boolean    parsed = false;

      do {
        String type         = TYPE_NUTRITION;
        String typeld       = type + "_" + getID(entry);
        String ordered_item = getOrderedItem(entry, ca);

        if (ordered_item == null) {
          if (server.isDebugMode()) {
            ordered_item = MISSING_INVALID;
          } else {
            throw missingRequiredData("oralDiet|enteralFormula||supplement", getID(entry));
          }
        }

        String directions = "";

        o = entry.optJSONObject("oralDiet");

        if (o != null) {
          directions = o.optString("instruction");

          if (o != null) {
            String s = o.optString("instruction", null);

            if (s != null) {
              ca.setLength(0);
              ca.append(directions);

              if (ca.length() > 0) {
                ca.append("; ");
              }

              ca.append(s);
              directions = ca.toString();
            }
          }
        }

        String status     = entry.optString("status", null);
        String providerld = null;
        String provider   = null;

        o = entry.optJSONObject("orderer");

        if (o != null) {
          providerld = server.getID(o.optString("reference"), false);
          provider   = o.optString("display");
        }

        if (jw != null) {
          jw.object();
          jw.key("type").object();
          jw.key("linkedData").value(typeld).key("value").value(type);
          jw.endObject();
          jw.key("ordered_item").value(ordered_item);

          if (status != null) {
            jw.key("status").value(status);
          }

          if (directions != null) {
            jw.key("directions").value(directions);
          }

          if (provider != null) {
            if (providerld != null) {
              jw.key("provider").object();
              jw.key("linkedData").value(providerld).key("value").value(provider);
              jw.endObject();
            } else {
              jw.key("provider").value(provider);
            }
          }

          jw.key("category").value(category);
          jw.endObject();
        } else {
          if (type != null) {
            if (typeld != null) {
              w.write(typeld);
              w.write((char) '|');
            }

            FHIRUtils.writeQuotedStringIfNecessary(w, type, ca);
          }

          w.write((char) '^');

          if (ordered_item != null) {
//    if (ordered_itemld != null) {
//      w.write(ordered_itemld);
//      w.write((char) '|');
//    }
            FHIRUtils.writeQuotedStringIfNecessary(w, ordered_item, ca);
          }

          w.write("^^^^^^^");

          if (status != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, status, ca);
          }

          w.write("^^");

          if (directions != null) {
            FHIRUtils.writeQuotedStringIfNecessary(w, directions, ca);
          }

          w.write("^");
          w.write(category);
          w.write("^^^");

          if (provider != null) {
            if (providerld != null) {
              w.write(providerld);
              w.write((char) '|');
            }

            FHIRUtils.writeQuotedStringIfNecessary(w, provider, ca);
          }

          w.write((char) '^');
          //          if(signed!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, signed, ca);
          //          }
          w.write((char) '^');
          //          if(flagged!=null) {
          //            FHIRUtils.writeQuotedStringIfNecessary(w, flagged, ca);
          //          }
          w.write((char) '^');
          w.write((char) '\n');
        }

        parsed = true;
      } while(false);

      if (!parsed) {
        debugLog("Could not parse entry:\n" + entry.toString(2));
      }
    }

    String getOrderedItem(JSONObject entry, CharArray ca) {
      JSONObject o = entry.optJSONObject("oralDiet");

      if (o != null) {
        ca.setLength(0);
        FHIRUtils.concatBestMedicalText(o.optJSONArray("type"), null, ca);

        return ca.toString();
      }

      o = entry.optJSONObject("enteralFormula");

      if (o != null) {
        String s = o.optString("baseFormulaProductName", null);

        if (s != null) {
          return s;
        }

        JSONObject oo = o.optJSONObject("baseFormulaType");

        if (oo != null) {
          return FHIRUtils.getBestMedicalText(oo);
        }

        return server.getResourceAsString("bv.text.external_formula");
      }

      JSONArray a   = entry.optJSONArray("supplement");
      int       len = (a == null)
                      ? 0
                      : a.length();

      if (len > 0) {
        ca.setLength(0);

        for (int i = 0; i < len; i++) {
          o = a.getJSONObject(i);

          if (ca._length > 0) {
            ca.append("; ");
          }

          String s = o.optString("productName");

          if (s == null) {
            s = FHIRUtils.getBestMedicalText(o.optJSONObject("type"));
          }

          if (s != null) {
            ca.append(s);
          }
        }

        if (ca._length > 0) {
          return ca.toString();
        }

        return getResourceAsString("bv.text.nutritional_supplements");
      }

      return getResourceAsString("bv.text.nutrition_order");
    }
  }
}
