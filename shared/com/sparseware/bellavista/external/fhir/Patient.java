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
import com.appnativa.util.CharArray;
import com.appnativa.util.OrderedProperties;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONTokener;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher.aCallback;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher.iCallback;
import com.sparseware.bellavista.external.fhir.FHIRUtils.MedicalCode;
import com.sparseware.bellavista.external.fhir.util.Patients;
import com.sparseware.bellavista.external.fhir.util.Users;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Patient extends aFHIRemoteService {
  protected static boolean            flagsSupported;
  protected static boolean            problemsSupported;
  protected static boolean            appointmentsSupported;
  protected static String[]           flagsColumnNames   = { "date", "location", "status" };
  protected static String[]           problemColumnNames = { "problem", "status" };
  protected static String[]           alertsColumnNames  = { "description", "due_date", "last_occurance", "priority",
          "type" };
  protected static String             selectedPatientID;
  protected static List<Practitioner> selectedPatientCareteam;
  protected static String             problemsSearchParams;
  protected static String             flagsSearchParams;
  protected static String             allergiesSearchParams;
  protected static String             alertsSearchParams;
  protected static boolean            initialized;

  public Patient() {
    super("Patient");

    if (!initialized) {
      flagsSupported        = FHIRServer.getInstance().getResource("Flag") != null;
      problemsSupported     = FHIRServer.getInstance().getResource("Condition") != null;
      appointmentsSupported = FHIRServer.getInstance().getResource("Appointment") != null;

      JSONObject o = server.getServerConfig().optJSONObject("fhir");

      if (o != null) {
        problemsSearchParams  = (String) o.opt("patient_problems", "search_params");
        flagsSearchParams     = (String) o.opt("patient_flags", "search_params");
        allergiesSearchParams = (String) o.opt("patient_allergies", "search_params");
        alertsSearchParams    = (String) o.opt("patient_alerts", "search_params");
      }
    }
  }

  public void alerts(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    if (!appointmentsSupported) {
      dataNotAvailable(conn, path, true, headers, alertsColumnNames, 0);

      return;
    }

    String        id     = server.getPatientID();
    Object        writer = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);
    StringBuilder sb     = new StringBuilder("Appointment?patient=");

    sb.append(id);

    if (alertsSearchParams != null) {
      sb.append('&').append(alertsSearchParams);
    }

    ActionLink l = server.createResourceLink(sb.toString());

    try {
      final JSONWriter jw;
      final Writer     w;

      if (writer instanceof JSONWriter) {
        jw = (JSONWriter) writer;
        w  = null;
      } else {
        w  = (Writer) writer;
        jw = null;
      }

      if (jw != null) {
        jw.object();
        jw.key("_columns").array();

        for (String name : alertsColumnNames) {
          jw.value(name);
        }

        jw.endArray();
        jw.key("_rows").array();
      }

      JSONTokener     t  = new JSONTokener(l.getReader());
      final CharArray ca = new CharArray();
      iCallback       cb = new aCallback() {
        @Override
        public Object entryEncountered(JSONObject entry) {
          try {
            do {
              entry = entry.getJSONObject("resource");

              if (entry.optString("resourceType").equals("Appointment")) {
                String status = entry.optString("status");

                if ("canceled".equals(status)) {
                  break;
                }

                String id          = server.getID(entry.optString("id"), false);
                String date        = entry.optString("start", null);
                String priority    = entry.optString("priority", null);
                String type        = FHIRUtils.getBestMedicalText(entry.optJSONObject("type"));
                String description = entry.optString("description", null);

                if (description == null) {
                  description = entry.optString("reason", null);
                }

                if (description == null) {
                  debugLog("Could not parse entry:\n" + entry.toString(2));

                  break;
                }

                if (jw != null) {
                  jw.object();
                  jw.key("description").object();
                  jw.key("linkedData").value(id).key("value").value(description);

                  if (date != null) {
                    jw.key("due_date").value(date);
                  }

                  if (type != null) {
                    jw.key("type").value(type);
                  }

                  if (priority != null) {
                    jw.key("priority").value(priority);
                  }

                  jw.endObject();
                  jw.endObject();
                } else {
                  w.write(id);
                  w.write((char) '|');
                  FHIRUtils.writeQuotedStringIfNecessary(w, description, ca);
                  w.write((char) '^');

                  if (date != null) {
                    w.write(date);
                  }

                  w.write("^^");

                  if (priority != null) {
                    w.write(priority);
                  }

                  w.write((char) '^');

                  if (type != null) {
                    w.write(type);
                  }

                  w.write('\n');
                }
              }
            } while(false);
          } catch(Exception e) {}

          return null;
        }
      };

      t.setWatcher(new FHIRJSONWatcher(cb));
      new JSONObject(t);
      t.dispose();

      if (jw != null) {
        jw.endArray();
        jw.endObject();
      }
    } finally {
      l.closeEx();
    }

    noData(conn, path, true, headers);
  }

  public void allergies(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    String id = server.getPatientID();
    Object w;

    writeAllergies(server, w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true), id);

    if (w instanceof JSONWriter) {
      ((JSONWriter) w).endObject();
    }
  }

  public void allergy(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noDocument(conn, path, headers);
  }

  public void careteam(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    String             id = Utils.getPatientID();
    List<Practitioner> team;

    if ((id != null) && id.equals(selectedPatientID) && (selectedPatientCareteam != null)) {
      team = selectedPatientCareteam;
    } else {
      JSONObject o = Patients.getBestEncounter(id, true);
      JSONArray  a = (o == null)
                     ? null
                     : o.optJSONArray("participant");

      if ((a != null) && (a.length() > 0)) {
        team = Patient.parseParticipant(server, a);
      } else {
        team = null;
      }

      if (team == null) {
        team = Collections.EMPTY_LIST;
      }

      selectedPatientCareteam = team;
      selectedPatientID       = id;
    }

    if ((team == null) || (team.size() == 0)) {
      noData(conn, path, true, headers);
    } else {
      Object           writer = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);
      final JSONWriter jw;
      final Writer     w;

      if (writer instanceof JSONWriter) {
        jw = (JSONWriter) writer;
        w  = null;
      } else {
        w  = (Writer) writer;
        jw = null;
      }

      if (jw != null) {
        jw.object();
        jw.key("_columns").array().value("id").value("xmpp_alias").value("name").value("role").value(
            "is_physician").endArray();
        jw.key("_rows").array();

        for (Practitioner member : team) {
          jw.object();
          jw.key("id").value(member.getID());
          jw.key("name").value(member.getName());
          jw.key("role").value(member.getRole());
          jw.key("is_physician").value(member.isProvider());
          jw.endObject();
        }

        jw.endArray();
        jw.endObject();
      } else {
        for (Practitioner member : team) {
          w.write(member.getID());
          w.write("^^");
          w.write(member.getName());
          w.write("^");
          w.write(member.getRole());
          w.write("^");
          w.write(member.isProvider()
                  ? "true"
                  : "false");
          w.write('\n');
        }
      }
    }
  }

  public void flag(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    String     id = removeExtension(path.shift());
    ActionLink l  = server.createResourceLink("Flag/" + id);

    try {
      headers.setDefaultResponseHeaders();
      headers.mimeHtml();

      Writer     w     = conn.getContentWriter();
      JSONObject entry = getReadEntry(l.getReader());

      FHIRUtils.writeHTMLDocumentStart(w, null);

      JSONObject text = entry.optJSONObject("text");

      if (text != null) {
        FHIRUtils.writeText(w, text, false, true);
      } else {
        w.append(getResourceAsString("bv.text.blank_report"));
      }

      FHIRUtils.writeHTMLDocumentFinish(w);
    } finally {
      l.close();
    }
  }

  public void flags(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    if (!flagsSupported) {
      dataNotAvailable(conn, path, true, headers, flagsColumnNames, 0);

      return;
    }

    String        id     = server.getPatientID();
    Object        writer = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);
    StringBuilder sb     = new StringBuilder("Flag?patient=");

    sb.append(id);

    if (flagsSearchParams != null) {
      sb.append('&').append(flagsSearchParams);
    }

    ActionLink l = server.createResourceLink(sb.toString());

    try {
      final JSONWriter jw;
      final Writer     w;

      if (writer instanceof JSONWriter) {
        jw = (JSONWriter) writer;
        w  = null;
      } else {
        w  = (Writer) writer;
        jw = null;
      }

      if (jw != null) {
        jw.object();
        jw.key("_columns").array();

        for (String name : flagsColumnNames) {
          jw.value(name);
        }

        jw.endArray();
        jw.key("_rows").array();
      }

      JSONTokener     t  = new JSONTokener(l.getReader());
      final CharArray ca = new CharArray();
      iCallback       cb = new aCallback() {
        @Override
        public Object entryEncountered(JSONObject entry) {
          try {
            do {
              entry = entry.getJSONObject("resource");

              if (entry.optString("resourceType").equals("Flag")) {
                String status = entry.optString("status");

                if (!"active".equals(status)) {
                  break;
                }

                String     id   = server.getID(entry.optString("id"), false);
                JSONObject code = entry.getJSONObject("code");
                String     flag = code.optString("text", null);

                if (flag == null) {
                  MedicalCode mc = FHIRUtils.getMedicalCode(code.optJSONArray("coding"));

                  flag = (mc == null)
                         ? null
                         : mc.getBestText();
                }

                if (flag == null) {
                  debugLog("Could not parse entry:\n" + entry.toString(2));

                  break;
                }

                if (jw != null) {
                  jw.object();
                  jw.key("description").object();
                  jw.key("linkedData").value(id).key("value").value(flag);
                  jw.endObject();
                  jw.endObject();
                } else {
                  w.write(id);
                  w.write((char) '|');
                  FHIRUtils.writeQuotedStringIfNecessary(w, flag, ca);
                  w.write('\n');
                }
              }
            } while(false);
          } catch(Exception e) {
            throw ApplicationException.runtimeException(e);
          }

          return null;
        }
      };

      t.setWatcher(new FHIRJSONWatcher(cb));
      new JSONObject(t);
      t.dispose();

      if (jw != null) {
        jw.endArray();
        jw.endObject();
      }
    } finally {
      l.closeEx();
    }
  }

  public void problem(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    noDocument(conn, path, headers);
  }

  public void problems(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    if (!problemsSupported) {
      dataNotAvailable(conn, path, true, headers, problemColumnNames, 0);

      return;
    }

    String        id     = server.getPatientID();
    Object        writer = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);
    StringBuilder sb     = new StringBuilder("Condition?patient=");

    sb.append(id);

    if (problemsSearchParams != null) {
      sb.append('&').append(problemsSearchParams);
    }

    ActionLink l = server.createResourceLink(sb.toString());

    try {
      final JSONWriter jw;
      final Writer     w;

      if (writer instanceof JSONWriter) {
        jw = (JSONWriter) writer;
        w  = null;
      } else {
        w  = (Writer) writer;
        jw = null;
      }

      if (jw != null) {
        jw.object();
        jw.key("_columns").array().value("problem").value("status").endArray();
        jw.key("_rows").array();
      }

      JSONTokener     t  = new JSONTokener(l.getReader());
      final CharArray ca = new CharArray();
      iCallback       cb = new aCallback() {
        @Override
        public Object entryEncountered(JSONObject entry) {
          try {
            entry = entry.getJSONObject("resource");

            if (entry.optString("resourceType").equals("Condition")) {
              String      id = server.getID(entry.optString("id"), false);
              MedicalCode mc = FHIRUtils.getMedicalCode(entry.getJSONObject("code"));

              if (mc == null) {
                if (server.isDebugMode()) {
                  mc = missingInvalid;
                } else {
                  throw missingRequiredData("code", id, "Condition");
                }
              }

              String problem = mc.getBestText();
              String status  = entry.optString("clinicalStatus", null);

              if (jw != null) {
                jw.object();
                jw.key("problem").object();
                jw.key("linkedData").value(id).key("value").value(problem);
                jw.endObject();

                if (status != null) {
                  jw.key("status").value(status);
                }

                jw.endObject();
              } else {
                w.write(id);
                w.write((char) '|');
                FHIRUtils.writeQuotedStringIfNecessary(w, problem, ca);
                w.write((char) '^');

                if (status != null) {
                  FHIRUtils.writeQuotedStringIfNecessary(w, status, ca);
                }

                w.write('\n');
              }
            }
          } catch(Exception e) {
            throw ApplicationException.runtimeException(e);
          }

          return null;
        }
      };

      t.setWatcher(new FHIRJSONWatcher(cb));
      new JSONObject(t);
      t.dispose();

      if (jw != null) {
        jw.endArray();
        jw.endObject();
      }
    } finally {
      l.closeEx();
    }
  }

  @SuppressWarnings({ "resource", "unused" })
  public void writeAllergies(final FHIRServer server, Object writer, String id) throws IOException {
    final JSONWriter jw;
    final Writer     w;

    if (writer instanceof JSONWriter) {
      jw = (JSONWriter) writer;
      w  = null;
    } else {
      w  = (Writer) writer;
      jw = null;
    }

    StringBuilder sb = new StringBuilder("AllergyIntolerance?patient=");

    sb.append(id);

    if (allergiesSearchParams != null) {
      sb.append('&').append(allergiesSearchParams);
    }

    ActionLink l = server.createResourceLink(sb.toString());

    try {
      if (jw != null) {
        jw.object();
        jw.key("_columns").array().value("allergen").value("reactions").endArray();
        jw.key("_rows").array();
      }

      JSONTokener     t  = new JSONTokener(l.getReader());
      final CharArray ca = new CharArray();
      iCallback       cb = new aCallback() {
        @Override
        public Object entryEncountered(JSONObject entry) {
          try {
            entry = entry.getJSONObject("resource");

            if (entry.optString("resourceType").equals("AllergyIntolerance")) {
              String id        = server.getID(entry.optString("id"), false);
              String substance = FHIRUtils.getBestMedicalText(entry.getJSONObject("substance"));

              if ((substance == null) || (substance.length() == 0)) {
                if (server.isDebugMode()) {
                  substance = MISSING_INVALID;
                } else {
                  throw missingRequiredData("substance", id, "AllergyIntolerance");
                }
              }

              ca.setLength(0);

              JSONArray a   = entry.optJSONArray("reaction");
              int       len = (a == null)
                              ? 0
                              : a.length();

              for (int i = 0; i < len; i++) {
                JSONArray ma = a.getJSONObject(i).optJSONArray("manifestation");

                for (Object o : ma) {
                  if (ca._length > 0) {
                    ca.append("; ");
                  }

                  ca.append(FHIRUtils.getBestMedicalText((JSONObject) o));
                }
              }

              if (jw != null) {
                jw.object();
                jw.key("allergen").object();
                jw.key("linkedData").value(id).key("value").value(substance);
                jw.endObject();
                jw.key("reactions").value(ca.toString());
                jw.endObject();
              } else {
                w.write(id);
                w.write((char) '|');
                w.write(substance);
                w.write((char) '^');
                w.write(ca.A, 0, ca._length);
                w.write('\n');
              }
            }
          } catch(Exception e) {
            throw ApplicationException.runtimeException(e);
          }

          return null;
        }
      };

      t.setWatcher(new FHIRJSONWatcher(cb));

      JSONObject o = new JSONObject(t);

      t.dispose();

      if (jw != null) {
        jw.endArray();
        jw.endObject();
      }
    } catch(Exception e) {
      server.debugLog(ApplicationException.getMessageEx(e));
    } finally {
      l.closeEx();
    }
  }

  public static List<Practitioner> parseParticipant(FHIRServer server, JSONArray participants) {
    int                     len  = participants.size();
    ArrayList<Practitioner> list = new ArrayList<Patient.Practitioner>(len);

    for (int i = 0; i < len; i++) {
      JSONObject   o     = participants.getJSONObject(i);
      String       tname = null;
      String       tcode = null;
      JSONObject   oo    = o.optJSONObject("individual");
      Practitioner p;

      if (oo != null) {
        String name = oo.optString("display");
        String id   = server.getID(oo.optString("reference"), false);

        list.add(p = new Practitioner(id, name, tcode, tname));

        JSONArray a    = o.optJSONArray("type");
        int       alen = (a == null)
                         ? 0
                         : a.length();

        for (int n = 0; n < alen; n++) {
          MedicalCode mc = FHIRUtils.getMedicalCode(a.getJSONObject(i));

          if (mc != null) {
            p.addType(mc.getBestCode(), mc.getBestText());
          }
        }
      }
    }

    return list;
  }

  public static class Practitioner {
    public String            id;
    public String            name;
    public String            typeName;
    public String            typeCode;
    List<String>             typeNames;
    List<String>             typeCodes;
    JSONObject               userObject;
    static OrderedProperties participantRoles;

    public Practitioner(String id, String name) {
      super();
      this.id   = id;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String getID() {
      return id;
    }

    public String getRole() {
      if ((typeName == null) || (typeName.length() == 0)) {
        if (typeCode == null) {
          typeCode = "PART";
        }

        if (participantRoles == null) {
          try {
            participantRoles = FHIRServer.getInstance().load("/data/participant.properties");
          } catch(IOException e) {
            participantRoles = new OrderedProperties();
          }
        }

        typeName = (String) participantRoles.get(typeCode);

        if (typeName == null) {
          typeName = "Participant";
        }
      }

      return typeName;
    }

    public Practitioner(String id, String name, String typeCode, String typeName) {
      super();
      this.id       = id;
      this.name     = name;
      this.typeName = typeName;
      this.typeCode = typeCode;
    }

    public boolean isAttending() {
      if ("ATND".equals(typeCode)) {
        return true;
      }

      return (typeCodes == null)
             ? false
             : typeCodes.indexOf("ATND") != -1;
    }

    public boolean isAdmitting() {
      if ("ADM".equals(typeCode)) {
        return true;
      }

      return (typeCodes == null)
             ? false
             : typeCodes.indexOf("ADM") != -1;
    }

    public boolean isType(String type) {
      if (type.equals(typeCode)) {
        return true;
      }

      return (typeCodes == null)
             ? false
             : typeCodes.indexOf(type) != -1;
    }

    public JSONObject getUserObject(boolean resolve, boolean usersSupported) {
      if ((userObject == null) && resolve) {
        if (usersSupported) {
          try {
            ActionLink l = FHIRServer.getInstance().createResourceLink("Practitioner/" + id);

            userObject = Users.populateUser(id, new JSONObject(l.getContentAsString()));
            userObject.put("role", getRole());
          } catch(Exception e) {
            FHIRServer.getInstance().ignoreException(e);
            userObject = new JSONObject();
            userObject.put("is_provider", isProvider());
          }
        } else {
          userObject = new JSONObject();
          userObject.put("is_provider", isProvider());
        }

        userObject.put("id", id);
        userObject.put("role", getRole());
        userObject.put("name", name);
      }

      return userObject;
    }

    public void setUserObject(JSONObject o) {
      userObject = o;
    }

    public void addType(String code, String name) {
      if (typeCode == null) {
        typeCode = code;
        typeName = name;
      } else {
        if (typeCodes == null) {
          typeCodes = new ArrayList<String>(3);
          typeNames = new ArrayList<String>(3);
        }

        typeCodes.add(code);
        typeNames.add(name);
      }
    }

    public boolean isProvider() {
      return "REFB".equals(typeCode) || isAttending() || isAdmitting();
    }
  }


  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {}

  public static void setSelectedPatientCareInfo(String id, List<Practitioner> team) {
    selectedPatientID       = id;
    selectedPatientCareteam = team;
  }

  public static List<Practitioner> getSelectedPatientCareteam() {
    return selectedPatientCareteam;
  }
}
