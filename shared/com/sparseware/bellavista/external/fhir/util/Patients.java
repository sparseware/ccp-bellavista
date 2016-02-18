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

import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.CharArray;
import com.appnativa.util.CharScanner;
import com.appnativa.util.Helper;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONTokener;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.MessageException;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher.aCallback;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher.iCallback;
import com.sparseware.bellavista.external.fhir.FHIRServer;
import com.sparseware.bellavista.external.fhir.FHIRServer.FHIRResource;
import com.sparseware.bellavista.external.fhir.FHIRUtils;
import com.sparseware.bellavista.external.fhir.LinkWaiter;
import com.sparseware.bellavista.external.fhir.Patient;
import com.sparseware.bellavista.external.fhir.Patient.Practitioner;
import com.sparseware.bellavista.external.fhir.aFHIRemoteService;
import com.sparseware.bellavista.service.ContentWriter;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class Patients extends aFHIRemoteService {
  protected static String                  MOST_RECENT_KEY;
  protected static boolean                 myPatientsSupported;
  protected static boolean                 nameComponentsSearchSupported;
  protected static boolean                 identifierSearchSupported;
  protected static boolean                 idSearchSupported;
  protected static boolean                 dobSearchSupported;
  protected static boolean                 genderSearchSupported;
  protected static boolean                 getEncounter = true;
  protected static ArrayList<String>       mostRecent;
  protected static String[]                COLUMN_NAMES;
  protected static HashMap<String, String> identifierMap         = new HashMap<String, String>(5);
  static String                            MOST_RECENT_SEPARATOR =
    "{enabled: false}~{columnSpan:-1;valueType:widget_type; value:\"Label{templateName:bv.line.data_separator}\"}^^^^^^^^^^^\n";

  static {
    identifierMap.put("mr", "mrn");
    identifierMap.put("mrn", "mrn");
    identifierMap.put("MR", "mrn");
    identifierMap.put("MRN", "mrn");
    identifierMap.put("ss", "ssn");
    identifierMap.put("ssn", "ssn");
    identifierMap.put("SS", "ssn");
    identifierMap.put("SSN", "ssn");
  }

  Pattern          ssnPattern = Pattern.compile("###-##-####");
  SimpleDateFormat birthDate  = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  public Patients() {
    super("Patient");

    if (COLUMN_NAMES == null) {
      COLUMN_NAMES = new String[] {
        "id", "name", "dob", "gender", "mrn", "provider", "encounter_date", "encounter_reason", "location", "rm_bed",
        "photo", "attending", "language", "relationship", "code_status", "code_status_short", "wt", "ht", "io_in",
        "io_out"
      };
      MOST_RECENT_KEY = "Patients.mostRecent:" + Functions.sha1(server.endPoint);
//    Utils.getPreferences().removeValue(MOST_RECENT_KEY);
      mostRecent = new ArrayList<String>(5);

      String s = Utils.getPreferences().getString(MOST_RECENT_KEY, null);

      if (s != null) {
        CharScanner.getTokens(s, '\t', false, mostRecent);
      } else {
        JSONArray a   = server.getServerConfig().optJSONArray("demo_patients");
        int       len = (a == null)
                        ? 0
                        : a.length();

        for (int i = 0; i < len; i++) {
          mostRecent.add(a.getString(i));
        }
      }

      JSONObject o = server.getServerConfig().optJSONObject("fhir");

      if (o != null) {
        getEncounter = o.optBoolean("get_encounter", true);
      }

      if (getEncounter) {
        getEncounter = server.getResource("Encounter") != null;
      }
    }

    columnNames = COLUMN_NAMES;
  }

  public static void initializeSupportedOptions(FHIRResource r, JSONObject config) {
    JSONArray a = r.searchParams;

    if (a != null) {
      int n = 0;

      for (Object o : a) {
        JSONObject jo   = (JSONObject) o;
        String     name = jo.optString("name");

        if (name.equals("careprovider")) {
          myPatientsSupported = true;
        } else if (name.equalsIgnoreCase("given")) {
          n++;
        } else if (name.equalsIgnoreCase("family")) {
          n++;
        } else if (name.equalsIgnoreCase("gender")) {
          genderSearchSupported = true;
        } else if (name.equalsIgnoreCase("identifier")) {
          identifierSearchSupported = true;
        } else if (name.equalsIgnoreCase("birthdate")) {
          dobSearchSupported = true;
        } else if (name.equalsIgnoreCase("_id")) {
          idSearchSupported = true;
        }
      }

      if (n > 1) {
        nameComponentsSearchSupported = true;
      }
    }

    config.put("hasPatientAlerts", false);
    config.put("genderSearchSupported", genderSearchSupported);
  }

  public void by_category(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    String s=path.shift();
    if("provider".equals(s) && server.getResource("Patient").hasSearchParam("careprovider")) {
      ActionLink l = server.createResourceLink("Patient", "careprovider", Utils.getUserID());

      try {
        Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

        search(l.getReader(),w, headers, true);
      } finally {
        l.close();
      }
      return;
    }
    noData(conn, path, true, headers);
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    Map map = getQueryParams(conn.getURL(), data);

    if (map == null) {
      most_recent(conn, path, data, headers);

      return;
    }

    ActionLink l;
    String     count  = (String) map.get("max");
    String     gender = (String) map.get("gender");
    String     id     = (String) map.get("identifier");
    String     s;

    if (gender != null) {
      if (gender.equalsIgnoreCase("F")) {
        gender = "female";
      } else {
        gender = "male";
      }
    }

    map.clear();

    int n;

    if (id.startsWith("`")) {
      if (idSearchSupported) {
        map.put("_id", id.substring(1));
      } else {
        s = getExtension(path);

        if (s == null) {
          s = id.substring(1) + ".json";
        } else {
          s = id.substring(1) + "." + s;
        }

        selectEx(conn, new ActionPath(s), data, headers, true);

        return;
      }
    } else if (identifierSearchSupported && id.startsWith("@")) {
      map.put("identifier", id.substring(1));
    } else if (nameComponentsSearchSupported && (n = id.indexOf(',')) != -1) {
      s = id.substring(0, n).trim();

      if (s.length() != 0) {
        map.put("family", s);
      }

      s = id.substring(n + 1).trim();

      if (s.length() != 0) {
        map.put("given", s);
      }
    } else if (nameComponentsSearchSupported && (n = id.lastIndexOf(' ')) != -1) {
      s = id.substring(0, n).trim();

      if (s.length() != 0) {
        map.put("given", s);
      }

      s = id.substring(n + 1).trim();

      if (s.length() != 0) {
        map.put("family", s);
      }
    } else if (dobSearchSupported && ((id.indexOf('/') != -1) || (id.indexOf('.') != -1))) {
      setBirthDate(map, id);
    } else if (identifierSearchSupported && (id.indexOf('-') != -1)) {
      if (ssnPattern.matcher(id).matches() ||!isPosibleDate(id)) {
        map.put("identifier", id);
      }
    } else if (dobSearchSupported && (id.indexOf('-') != -1)) {
      setBirthDate(map, id);
    } else {
      map.put("name", id);
    }

    if (gender != null) {
      map.put("gender", gender);
    }

    if (count != null) {
      map.put("_count", count);
    }

    l = server.createResourceLink("Patient");
    l.setAttributes(map);

    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers, true);
    } finally {
      l.close();
    }
  }

  public void most_recent(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    headers.setDefaultResponseHeaders();
    headers.mimeRow();

    ContentWriter w = conn.getContentWriter();

    for (String s : mostRecent) {
      w.write(s);
      w.write('\n');
    }

    if (!mostRecent.isEmpty()) {
      w.write(MOST_RECENT_SEPARATOR);
    }
  }


  public void nearby(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {}

  public void select(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    selectEx(conn, path, data, headers, false);
  }

  protected void selectEx(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers, boolean forList)
          throws IOException {
    Object w  = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);
    String id = removeExtension(path.shift());

    if (id == null) {
      throw new MessageException("missing id for select");
    }

    ActionLink l      = server.createResourceLink("Patient/" + id);
    LinkWaiter waiter = null;

    if (getEncounter) {
      waiter = new LinkWaiter(1);
      waiter.addLink(null, createBestEncounterCallable(id));
    }

    try {
      read(l.getReader(), w, headers, forList, waiter);
    } finally {
      l.close();
    }
  }

  public static JSONObject getBestEncounter(JSONArray a) {
    int                    len = a.length();
    final BestObjectHolder oh  = new BestObjectHolder();

    for (int i = 0; i < len; i++) {
      JSONObject o = a.getJSONObject(i);

      if (!o.optString("resourceType").equals("Encounter")) {
        continue;
      }

      if (checkEncounter(o, oh)) {
        break;
      }
    }

    return (JSONObject) oh.value;
  }

  public static JSONObject getBestEncounter(String patient, boolean always) throws IOException {
    if (getEncounter || always) {
      try {
        return (JSONObject) createBestEncounterCallable(patient).call();
      } catch(Exception e) {
        if (e instanceof IOException) {
          throw(IOException) e;
        } else {
          return null;
        }
      }
    }

    return null;
  }

  public static Callable createBestEncounterCallable(final String patient) {
    return new Callable() {
      @Override
      public Object call() throws Exception {
        ActionLink             l  = FHIRServer.getInstance().createLink("Encounter?patient=" + patient);
        final BestObjectHolder oh = new BestObjectHolder();

        try {
          final JSONTokener t  = new JSONTokener(l.getReader());
          iCallback         cb = new aCallback() {
            @Override
            public Object entryEncountered(JSONObject entry) {
              if (checkEncounter(entry, oh)) {
                t.setTerminateParsing(true);
              }

              return null;
            }
          };

          t.setWatcher(new FHIRJSONWatcher(cb));
          new JSONObject(t);
          t.dispose();
        } finally {
          l.close();
        }

        JSONObject o = (JSONObject) oh.value;

        return o;
      }
    };
  }

  public static JSONArray getEncounterLocation(JSONObject encounter, boolean resolveAlways) {
    JSONObject loc = null;
    JSONArray  a   = encounter.optJSONArray("location");

    if ((a != null) &&!a.isEmpty()) {
      a = getBestLocation(a);

      int len = (a == null)
                ? 0
                : a.length();

      for (int i = 0; i < len; i++) {
        loc = a.getJSONObject(i);
        loc = loc.optJSONObject("location");

        String ref = (loc == null)
                     ? null
                     : loc.optString("reference", null);

        if (ref == null) {
          a.set(i, null);

          continue;
        }

        if (!resolveAlways &&!ref.startsWith("#")) {
          String name = loc.optString("display");

          if (name.length() > 0) {
            loc = new JSONObject().put("id", FHIRServer.getInstance().getID(ref, false)).put("name", name);
            a.set(i, loc);

            continue;
          }
        }

        if (ref.startsWith("#")) {
          JSONArray aa = encounter.optJSONArray("contained");

          loc = aa.findJSONObject("id", ref.substring(1));
          a.set(i, loc);
        } else {
          ActionLink l = FHIRServer.getInstance().createLink(ref);

          try {
            loc = aFHIRemoteService.getReadEntry(l.getReader());
          } catch(Exception e) {
            FHIRServer.getInstance().ignoreException(e);
            a = null;
          } finally {
            l.close();
          }
        }
      }
    }

    return a;
  }

  public static JSONArray getBestLocation(JSONArray a) {
    int                    len = a.length();
    final BestObjectHolder oh  = new BestObjectHolder();

    for (int i = 0; i < len; i++) {
      if (checkLocation(a.getJSONObject(i), oh)) {
        break;
      }
    }

    JSONObject o = (JSONObject) oh.value;

    if (o == null) {
      return null;
    }

    JSONArray aa = new JSONArray();

    aa.add(o);

    if (!oh.status.equals("active") && (oh.status.length() > 0)) {
      len = 0;
    }

    for (int i = 0; i < len; i++) {
      JSONObject oo = a.getJSONObject(i);

      if ((oo != o) && oo.optString("status").equals(oh.status)) {
        aa.add(oo);
      }
    }

    return aa;
  }

  static boolean checkEncounter(JSONObject entry, BestObjectHolder oh) {
    if (!"in-progress".equals(oh.status)) {
      //entry = entry.getJSONObject("resource");
      String status = entry.optString("status");
      String clazz  = entry.optString("class");

      if (status.equals("in-progress")) {
        oh.value  = entry;
        oh.status = status;

        return true;
      } else if ((oh.value == null) || status.equals("finished")) {
        JSONObject period = entry.optJSONObject("period");

        if (period != null) {
          String date = period.optString("end", null);

          if (date == null) {
            date = period.optString("start", null);
          }

          if ((oh.date == null) || (date.compareTo(oh.date) > 0)) {
            oh.value  = entry;
            oh.status = status;
            oh.date   = date;
            oh.clazz  = clazz;
          }
        } else if ((oh.value == null) || (oh.date == null)) {
          oh.value  = entry;
          oh.status = status;
          oh.clazz  = clazz;
        }
      }
    }

    return false;
  }

  static boolean checkLocation(JSONObject entry, BestObjectHolder oh) {
    if (!"active".equals(oh.status)) {
      //entry = entry.getJSONObject("resource");
      String status = entry.optString("status");

      if (status.equals("active")) {
        oh.value  = entry;
        oh.status = status;

        return true;
      } else if ((oh.value == null) || status.equals("completed")) {
        JSONObject period = entry.optJSONObject("period");

        if (period != null) {
          String date = period.optString("end", null);

          if (date == null) {
            date = period.optString("start", null);
          }

          if ((oh.date == null) || (date.compareTo(oh.date) > 0)) {
            oh.value  = entry;
            oh.status = status;
            oh.date   = date;
          }
        } else if ((oh.value == null) || (oh.date == null)) {
          oh.value  = entry;
          oh.status = status;
        }
      }
    }

    return false;
  }

  private boolean isPosibleDate(String id) {
    int len = id.length();

    for (int i = 0; i < len; i++) {
      char c = id.charAt(i);

      if (!Character.isDigit(c) && ((c != '-') || (c != '/') || (c != '.'))) {
        return false;
      }
    }

    return true;
  }

  private void setBirthDate(Map<String, String> map, String id) {
     Calendar cal = Calendar.getInstance();
     if(Helper.setDateTime(id, cal, false)) {
      map.put("birthDate", birthDate.format(cal.getTime()));
    } else{
      map.put("birthDate", id);
    }
  }

  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
    processEntry(entry, jw, w, new CharArray(), params);
  }

  @Override
  public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... params)
          throws IOException {
    Boolean            forList = (params.length == 0)
                                 ? Boolean.TRUE
                                 : (Boolean) params[0];
    JSONObject         o;
    JSONObject         oo;
    boolean            parsed = false;
    String             s;
    List<Practitioner> team = null;
    Writer             ow   = w;
    StringWriter       sw   = forList
                              ? null
                              : new StringWriter();

    if (sw != null) {
      w = sw;
    }

    String id           = null;
    String encounter_id = null;

    do {
      JSONArray name             = null;
      String    dob              = null;
      String    gender           = null;
      String    mrn              = null;
      String    providerld       = null;
      String    provider         = null;
      String    encounter_date   = null;
      String    encounter_reason = null;
      String    locationld       = null;
      String    location         = null;
      String    rm_bed           = null;
      String    photo            = null;
      String    attendingld      = null;
      String    attending        = null;
      String    language         = null;
      String    relationshipld   = "PART";
      String    relationship     = "Participation";

//      String code_status = null;
//      String code_status_short = null;
//      String wt = null;
//      String ht = null;
//      String io_in = null;
//      String io_out = null;
      id   = server.getID(entry.optString("id"), false);
      name = entry.optJSONArray("name");
      dob  = entry.optString("birthDate", null);
      s    = entry.optString("gender");

      if (s.length() > 0) {
        ca.set(s);
        ca.toMixedCase();
        gender = ca.toString();
      }

      JSONArray a = entry.optJSONArray("identifier");

      if (a != null) {
        mrn = FHIRUtils.getCodableConcept(a, "mrn", identifierMap);
      }

      a = entry.optJSONArray("photo");

      if ((a != null) && (a.length() > 0)) {
        o = a.getJSONObject(0);
        s = o.optString("data", null);

        if (s != null) {
          String type = o.optString("contentType", "");

          if (type.startsWith("image/") && (s.length() > 0)) {
            ca.set("data:").append(type).append(";base64,").append(s);
            photo = ca.toString();
          }
        } else {
          s = o.optString("uri", "");

          if (s.startsWith("http")) {
            photo = s;
          }
        }
      }

      if (!forList) {
        a = entry.optJSONArray("contained");
        o = null;

        if (a != null) {
          o = getBestEncounter(a);
        }

        if ((o == null) && (params.length > 1) && (params[1] instanceof LinkWaiter)) {
          LinkWaiter waiter = (LinkWaiter) params[1];

          waiter.startWaiting();

          if (!waiter.hadError()) {
            o = (JSONObject) waiter.getResult(0);
          }

          waiter.dispose();
        }

        if (o != null) {
          encounter_id   = server.getID(o.optString("id"), false);
          encounter_date = (String) o.opt("period", "start");
          ca.setLength(0);
          encounter_reason = FHIRUtils.concatBestMedicalText(o.optJSONArray("reason"), null, ca).toString();
          a                = o.optJSONArray("participant");

          if ((a != null) && (a.length() > 0)) {
            team = Patient.parseParticipant(server, a);

            int    len = team.size();
            String me  = Utils.getUserID();

            for (int i = 0; i < len; i++) {
              Practitioner p = team.get(i);

              if (p.isAttending()) {
                attending   = p.name;
                attendingld = p.id;
              } else if (p.isProvider()) {
                provider   = p.name;
                providerld = p.id;
              }

              if ((me != null) && me.equals(p.id)) {
                relationship   = p.typeName;
                relationshipld = p.typeCode;

                if ((relationship == null) || (relationship.length() == 0)) {}
              }
            }
          }

          a = getEncounterLocation(o, false);

          if ((a != null) && (a.length() > 0)) {
            int    len   = a.size();
            String rm    = null;
            String rmld  = null;
            String bed   = null;
            String bedld = null;

            for (int i = 0; i < len; i++) {
              oo = a.optJSONObject(i);    //use opt because invalid locations entries are nullified

              if (oo == null) {
                continue;
              }

              String text = oo.optString("name", null);

              if (text == null) {
                text = oo.optString("description", null);
              }

              if (location == null) {
                location = text;
              }

              s = oo.optString("status", null);

              String ld = FHIRServer.getInstance().getID(oo.optString("id"), false);

              s = FHIRUtils.getBestMedicalCode(oo.optJSONObject("physicalType"));

              if (s != null) {
                if (s.equals("ro")) {
                  rm   = text;
                  rmld = ld;
                } else if (s.equals("bd")) {
                  bed   = text;
                  bedld = ld;
                } else if (s.equals("wi")) {
                  location   = text;
                  locationld = ld;
                }
              }
            }

            ca._length = 0;

            if (rm != null) {
              ca.append(rm);
            }

            if (bed != null) {
              ca.append('-');
              ca.append(bed);
            }

            if (ca._length != 0) {
              rm_bed = ca.toString();
            }

            if (locationld == null) {
              if (bed != null) {
                locationld = bedld;
              } else if (rm != null) {
                locationld = rmld;
              }
            }
          }

          if ((a != null) && (a.length() > 0)) {
            int    len    = a.size();
            String rm     = null;
            String rmld   = null;
            String bed    = null;
            String bedld  = null;
            String wingld = null;

            for (int i = 0; i < len; i++) {
              oo = a.getJSONObject(i).optJSONObject("location");

              if (oo == null) {
                continue;
              }

              String display = oo.optString("name", null);

              if (display == null) {
                display = oo.optString("description", null);
              }

              if (location == null) {
                location = display;
              }

              s = oo.optString("status", null);

              if ((s == null) ||!s.equals("active")) {
                continue;
              }

              String ld = server.getID(oo.optString("id"), false);

              s = FHIRUtils.getBestMedicalCode(oo.optJSONObject("physicalType"));

              if (s != null) {
                if (s.equals("rm")) {
                  rm   = display;
                  rmld = ld;
                } else if (s.equals("bed")) {
                  bed   = display;
                  bedld = ld;
                } else if (s.equals("wing")) {
                  location = display;
                  wingld   = ld;
                }
              }
            }

            ca._length = 0;

            if (rm != null) {
              ca.append(rm);
            }

            if (bed != null) {
              ca.append('-');
              ca.append(bed);
            }

            if (ca._length != 0) {
              rm_bed = ca.toString();
            }

            if (locationld == null) {
              if (bed != null) {
                locationld = bedld;
              } else if (rm != null) {
                locationld = rmld;
              } else {
                locationld = wingld;
              }
            }
          }
        }

        if (provider == null) {
          a = entry.optJSONArray("careProvider");

          if ((a != null) && (a.length() > 0)) {
            int     len       = a.length();
            boolean addToTeam = false;

            if (team == null) {
              team      = new ArrayList<Patient.Practitioner>(len);
              addToTeam = true;
            }

            for (int i = 0; i < len; i++) {
              o = a.getJSONObject(i);
              s = o.optString("reference");

              if (s.contains("Practitioner/")) {
                s = server.getID(s, false);

                String pn = o.optString("display", null);

                if (pn != null) {
                  if (provider == null) {
                    providerld = s;
                    provider   = pn;

                    if (!addToTeam) {
                      break;
                    }
                  }

                  if (addToTeam) {
                    team.add(new Practitioner(s, pn));
                  }
                }
              }
            }
          }
        }

        a = entry.optJSONArray("communication");

        if ((a != null) && (a.length() > 0)) {
          int len = a.length();

          for (int i = 0; i < len; i++) {
            o        = a.getJSONObject(i);
            language = FHIRUtils.getBestMedicalText(o.optJSONObject("language"));

            if (o.optBoolean("preferred", false)) {
              break;
            }
          }
        }
      }

      if (jw != null) {
        if (forList) {
          jw.object();
        }

        if (id != null) {
          jw.key("id").value(id);
        }

        if (name != null) {
          FHIRUtils.writeName(jw, name);
        }

        if (dob != null) {
          jw.key("dob").value(dob);
        }

        if (gender != null) {
          jw.key("gender").value(gender);
        }

        if (mrn != null) {
          jw.key("mrn").value(mrn);
        }

        if (provider != null) {
          writeLinkedData(jw, "provider", providerld, provider);
        }

        if (encounter_date != null) {
          jw.key("encounter_date").value(encounter_date);
        }

        if (encounter_reason != null) {
          jw.key("encounter_reason").value(encounter_reason);
        }

        if (location != null) {
          writeLinkedData(jw, "location", locationld, location);
        }

        if (rm_bed != null) {
          jw.key("rm_bed").value(rm_bed);
        }

        if (photo != null) {
          jw.key("photo").value(photo);
        }

        if (attending != null) {
          if (attendingld != null) {
            jw.key("attending").object();
            jw.key("linkedData").value(attendingld).key("value").value(attending);
            jw.endObject();
          } else {
            jw.key("attending").value(attending);
          }
        }

        if (language != null) {
          jw.key("language").value(language);
        }

        jw.key("relationship").object();
        jw.key("linkedData").value(relationshipld).key("value").value(relationship);
        jw.endObject();

        //        if(code_status!=null) {
        //          jw.key("code_status").value(code_status);
        //        }
        //        if(code_status_short!=null) {
        //          jw.key("code_status_short").value(code_status_short);
        //        }
        //        if(wt!=null) {
        //          jw.key("wt").value(wt);
        //        }
        //        if(ht!=null) {
        //          jw.key("ht").value(ht);
        //        }
        //        if(io_in!=null) {
        //          jw.key("io_in").value(io_in);
        //        }
        //        if(io_out!=null) {
        //          jw.key("io_out").value(io_out);
        //        }
        if (forList) {
          jw.endObject();
        }
      }

      if (w != null) {
        if (id != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, id, ca);
        }

        w.write('^');

        if (name != null) {
          FHIRUtils.writeName(w, name);
        }

        w.write('^');

        if (dob != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, dob, ca);
        }

        w.write('^');

        if (gender != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, gender, ca);
        }

        w.write('^');

        if (mrn != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, mrn, ca);
        }

        w.write('^');

        if (provider != null) {
          if (providerld != null) {
            w.write(providerld);
            w.write('|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, provider, ca);
        }

        w.write('^');

        if (encounter_date != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, encounter_date, ca);
        }

        w.write('^');

        if (encounter_reason != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, encounter_reason, ca);
        }

        w.write('^');

        if (location != null) {
          if (locationld != null) {
            w.write(locationld);
            w.write('|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, location, ca);
        }

        w.write('^');

        if (rm_bed != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, rm_bed, ca);
        }

        w.write('^');

        if ((photo != null) && forList) {
          FHIRUtils.writeQuotedStringIfNecessary(w, photo, ca);
        }

        w.write('^');

        if (attending != null) {
          if (attendingld != null) {
            w.write(attendingld);
            w.write('|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, attending, ca);
        }

        w.write('^');

        if (language != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, language, ca);
        }

        w.write('^');
        w.write(relationshipld);
        w.write('|');
        FHIRUtils.writeQuotedStringIfNecessary(w, relationship, ca);
        w.write('^');
        //        if (code_status != null) {
        //          FHIRUtils.writeQuotedStringIfNecessary(w, code_status, ca);
        //        }
        //        w.write((char) '^');
        //        if (code_status_short != null) {
        //          FHIRUtils.writeQuotedStringIfNecessary(w, code_status_short, ca);
        //        }
        //        w.write((char) '^');
        //        if (wt != null) {
        //          FHIRUtils.writeQuotedStringIfNecessary(w, wt, ca);
        //        }
        //        w.write((char) '^');
        //        if (ht != null) {
        //          FHIRUtils.writeQuotedStringIfNecessary(w, ht, ca);
        //        }
        //        w.write((char) '^');
        //        if (io_in != null) {
        //          FHIRUtils.writeQuotedStringIfNecessary(w, io_in, ca);
        //        }
        //        w.write((char) '^');
        //        if (io_out != null) {
        //          FHIRUtils.writeQuotedStringIfNecessary(w, io_out, ca);
        //        }
        w.write('^');
        w.write('\n');
      }

      parsed = true;
      server.setPatientInfo(id, encounter_id);
    } while(false);

    if (!parsed) {
      debugLog("Could not parse entry:\n" + entry.toString(2));
    } else if (sw != null) {
      s = sw.toString();

      if (ow != null) {
        ow.write(s);
      }

      int len = mostRecent.size();

      if (len > 0) {
        String sid = id + "^";

        for (int i = 0; i < len; i++) {
          if (mostRecent.get(i).startsWith(sid)) {
            mostRecent.remove(i);

            break;
          }
        }
      }

      s = s.trim();
      mostRecent.add(0, s);

      if (mostRecent.size() > 5) {
        mostRecent.remove(5);
      }

      Utils.getPreferences().putString(MOST_RECENT_KEY, Helper.toString(mostRecent, "\t"));
      Utils.getPreferences().update();

      if (getEncounter) {
        if (team == null) {
          team = Collections.EMPTY_LIST;
        }
      }

      Patient.setSelectedPatientCareInfo(id, team);
    }
  }

  static class BestObjectHolder {
    public JSONObject value;
    public String     date;
    public String     status;
    public String     clazz;
  }
}
