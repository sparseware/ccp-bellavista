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

import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.Browser;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIColorHelper;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.viewer.WebBrowser;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.Base64;
import com.appnativa.util.FileResolver;
import com.appnativa.util.Helper;
import com.appnativa.util.ObjectHolder;
import com.appnativa.util.OrderedProperties;
import com.appnativa.util.SNumber;
import com.appnativa.util.SimpleDateFormatEx;
import com.appnativa.util.Streams;
import com.appnativa.util.iURLResolver;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONException;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONTokener;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.MessageException;
import com.sparseware.bellavista.Settings.Server;
import com.sparseware.bellavista.SmartDateContext;
import com.sparseware.bellavista.Utils;
import com.sparseware.bellavista.external.ActionLinkEx;
import com.sparseware.bellavista.external.aProtocolHandler;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher.aCallback;
import com.sparseware.bellavista.external.fhir.util.Patients;
import com.sparseware.bellavista.external.fhir.util.Users;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.aRemoteService;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * The protocol handler for connecting to FHIR servers via the 'fhir' URL scheme
 *
 * @author Don DeCoteau
 */
public class FHIRServer extends aProtocolHandler {
  private static FHIRServer               _instance;
  private static JSONObject               localConfig;
  static SmartDateContext                 dateContext     = new SmartDateContext(true,
                                                              new SimpleDateFormatEx("MMM dd, yyyy"));
  static SmartDateContext                 dateTimeContext = new SmartDateContext(true,
                                                              new SimpleDateFormatEx("MMM dd, yyyy'@'HH:mm"));
  public float                            version;
  public String                           endPoint;
  public String                           endPointNoSlash;
  public String                           publisher = "";
  protected Server                        serverConfig;
  protected StreamHandler                 streamHandler;
  protected HashMap<String, FHIRResource> resources;
  protected boolean                       demoMode = true;
  protected HashMap<String, String>       labCategories;
  protected HashMap<String, String>       procedureCategories;
  protected HashMap<String, String>       strings;
  protected boolean                       commandLine;
  protected HashMap<String, String>       timingWhen;
  protected HashMap<String, String>       timingCode;
  protected HashMap<String, String>       timeUnits;
  protected FileResolver                  defaultResolver;
  protected String                        patientEncounterID;
  protected String                        patientID;
  public boolean                          debug;
  protected ActionLink                    lastLink;
  private AuthHandler                     authHandler;

  public FHIRServer() {
    _instance = this;
  }

  /**
   * The constructor is meant to be used when testing functionality without
   * running the full GUI client
   *
   * @param endPoint
   *          the servers end point
   */
  FHIRServer(String endPoint) {
    _instance = this;

    if (endPoint.endsWith("/")) {
      this.endPoint        = endPoint;
      this.endPointNoSlash = endPoint.substring(0, endPoint.length() - 1);
    } else {
      this.endPoint        = endPoint + "/";
      this.endPointNoSlash = endPoint;
    }
  }

  @Override
  public ActionLinkEx createLink(iWidget context, URL url) {
    ActionLinkEx l = super.createLink(context, url);

    decorateLink(l);
    l.setRequestHeader("Accept", "application/json+fhir");

    return l;
  }

  @Override
  public ActionLinkEx createLink(iWidget context, URL url, String type) {
    ActionLinkEx l = super.createLink(context, url, type);

    decorateLink(l);
    l.setRequestHeader("Accept", "application/json+fhir");

    return l;
  }

  public ActionLinkEx createLink(String url) {
    if (!url.startsWith("http")) {
      if (url.startsWith("/")) {
        url = url.substring(1);
      }

      url = endPoint + url;
    }

    ActionLinkEx l = new ActionLinkEx(url);

    decorateLink(l);
    l.setRequestHeader("Accept", "application/json+fhir");

    return l;
  }

  public ActionLinkEx createLink(String object, Map<String, Object> params) {
    ActionLinkEx l = new ActionLinkEx(endPoint + object);

    decorateLink(l);
    l.setRequestHeader("Accept", "application/json+fhir");

    if (params != null) {
      l.setAttributes(params);
    }

    return l;
  }

  public ActionLinkEx createLink(URL url) {
    ActionLinkEx l = super.createLink(null, url);

    decorateLink(l);
    l.setRequestHeader("Accept", "application/json+fhir");

    return l;
  }

  public ActionLinkEx createLink(URL url, String type) {
    ActionLinkEx l = super.createLink(null, url, type);

    decorateLink(l);
    l.setRequestHeader("Accept", "application/json+fhir");

    return l;
  }

  public ActionLinkEx createResourceLink(String resource, String... params) {
    ActionLinkEx l = new ActionLinkEx(fixLink(resource));

    decorateLink(l);
    l.setRequestHeader("Accept", "application/json+fhir");

    if ((params != null) && (params.length > 0)) {
      int                     len = params.length;
      HashMap<String, Object> map = new HashMap<String, Object>(len / 2);
      int                     i   = 0;

      while(i < len) {
        map.put(params[i++], params[i++]);
      }

      l.setAttributes(map);
    }

    return l;
  }

  @Override
  public URLStreamHandler createURLStreamHandler(String protocol) {
    if (streamHandler == null) {
      streamHandler = new StreamHandler();
    }

    return streamHandler;
  }

  @Override
  public void customLogin(final Server server, final iFunctionCallback cb) {
    customLoginEx(server, cb, false);
  }

  public StringWriter dataNotAvailable(boolean row, HttpHeaders headers, String[] columnNames, int column)
          throws IOException {
    headers.setDefaultResponseHeaders();

    String       ext = "";
    StringWriter w   = new StringWriter();

    if ("json".equals(ext)) {
      headers.mimeJson();

      StringWriter sw = new StringWriter();
      JSONWriter   jw = new JSONWriter(sw);

      jw.object().key(columnNames[column]);
      jw.object();
      jw.key("enabled").value(false);
      jw.key("value").value(getResourceAsString("bv.text.data_not_available"));
      jw.endObject();
      jw.endObject();
      w.write(sw.toString());
    } else if ("html".equals(ext)) {
      headers.mimeHtml();
      w.write(aRemoteService.textToHTML(getResourceAsString("bv.text.data_not_available")));
      w.write("\n");
    } else if ("txt".equals(ext)) {
      headers.mimeText();
      w.write(getResourceAsString("bv.text.data_not_available"));
      w.write("\n");
    } else {
      if (row) {
        headers.mimeRow();

        StringBuilder sb  = new StringBuilder("{enabled: false}~");
        int           len = columnNames.length;

        for (int i = 0; i < len; i++) {
          if (i == column) {
            sb.append("{enabled: false}");
            sb.append(getResourceAsString("bv.text.data_not_available"));
          }

          sb.append('^');
        }

        w.write(sb.toString());
        w.write("\n");
      } else {
        headers.mimeList();

        StringBuilder sb = new StringBuilder("{enabled: false}");

        sb.append(getResourceAsString("bv.text.data_not_available"));
        w.write(sb.toString());
        w.write("\n");
      }
    }

    return w;
  }

  public void debugLog(String msg) {
    System.out.println(msg);
  }

  public String fixLink(String href) {
    String ep = endPointNoSlash;
    int    n  = href.indexOf(':');

    if (n != -1) {
      int p = href.indexOf('/');

      if (p != -1) {
        if (n < p) {
          if (!href.startsWith(ep)) {
            throw new ApplicationException(getString("bv.text.bad_fhir_link", ep, href));
          } else {
            return href;
          }
        }
      }
    }

    if (href.charAt(0) == '/') {
      href = ep + href;
    } else {
      href = endPoint + href;
    }

    return href;
  }

  public UIColor getColor(String color) {
    if (commandLine) {
      return UIColor.BLACK;
    }

    return UIColorHelper.getColor(color);
  }

  public JSONObject getConfigurationData(String name) {
    if (commandLine) {
      return getLocalConfigurationData(name);
    }

    return (JSONObject) Platform.getAppContext().getData(name);
  }

  public JSONObject getData(String object, Map<String, Object> params) throws Exception {
    ActionLink l = null;

    try {
      return new JSONObject(new JSONTokener((l = createLink(object, params)).getReader()));
    } finally {
      if (l != null) {
        l.close();
      }
    }
  }

  public JSONObject getData(String object, String... params) throws Exception {
    ActionLink l = null;

    try {
      return new JSONObject(new JSONTokener((l = createResourceLink(object, params)).getReader()));
    } finally {
      if (l != null) {
        l.close();
      }
    }
  }

  public iURLResolver getDefaultResolver() {
    return (defaultResolver == null)
           ? Platform.getDefaultURLResolver()
           : defaultResolver;
  }

  public boolean getFHIRConfigBoolean(String key, boolean defaultValue) {
    JSONObject o = serverConfig.optJSONObject("fhir");

    return (o == null)
           ? defaultValue
           : o.optBoolean(key, defaultValue);
  }

  public String getFHIRConfigString(String key) {
    JSONObject o = serverConfig.optJSONObject("fhir");

    return (o == null)
           ? null
           : o.optString(key, null);
  }

  public String getID(String id, boolean includePath) {
    if (id.startsWith(endPoint) && includePath) {
      return id.substring(endPoint.length());
    }

    if (!includePath) {
      int n = id.lastIndexOf('/');

      if (n != -1) {
        id = id.substring(n + 1);
      }
    }

    return id;
  }

  public HashMap<String, String> getLabCategories() {
    return labCategories;
  }

  /**
   * Get the id of the currently selected patient encounter
   *
   * @return the id of the currently selected patient encounter
   */
  public String getPatientEncounterID() {
    return patientEncounterID;
  }

  /**
   * Get the id of the currently selected patient
   *
   * @return the id of the currently selected patient
   * @throws IOException
   *           if not patient has been selected
   */
  public String getPatientID() throws IOException {
    if (patientID == null) {
      throw new IOException("NO PATIENT SELECTED");
    }

    return patientID;
  }

  /**
   * Get the id of the currently selected patient without throwing an exception
   * if the id is null
   *
   * @return the id of the currently selected patient
   */
  public String getPatientIDEx() {
    return patientID;
  }

  public HashMap<String, String> getProcedureCategories() {
    return procedureCategories;
  }

  public FHIRResource getResource(String name) {
    return (resources == null)
           ? null
           : resources.get(name);
  }

  public String getResourceAsString(String name) {
    if (strings == null) {
      return Platform.getResourceAsString(name);
    }

    return strings.get(name);
  }

  /**
   * Gets the configuration object for the FHIR server
   *
   * @return the configuration object for the FHIR server
   */
  public Server getServerConfig() {
    return serverConfig;
  }

  public String getString(String name, Object... args) {
    if (strings == null) {
      return Platform.getWindowViewer().getString(name, args);
    }

    String s = strings.get(name);

    return String.format(s, args);
  }

  public void ignoreException(Exception e) {
    ignoreException(null, e);
  }

  public void ignoreException(String msg, Exception e) {
    if (commandLine) {
      if (msg != null) {
        System.out.println(msg);
      }

      if (e != null) {
        e.printStackTrace();
      }
    } else {
      Platform.ignoreException(msg, e);
    }
  }

  @Override
  public void initialize(Server server) throws Exception {
    super.initialize(server);
    this.demoMode     = server.isDemoServer();
    this.serverConfig = server;

    JSONObject fhir = server.optJSONObject("fhir");

    if (fhir != null) {
      debug = fhir.optBoolean("debug");

      boolean log = fhir.optBoolean("logg_connections");

      if (log) {}
    }

    String serverURL = server.getURL();
    String ep        = "http" + serverURL.substring(4);

    if ((endPoint == null) ||!endPoint.equals(ep)) {
      if (!ep.endsWith("/")) {
        endPointNoSlash = ep;
        ep              += "/";
      } else {
        endPointNoSlash = ep.substring(0, ep.length() - 1);
      }

      if (!ep.equals(endPoint)) {
        this.endPoint = ep;
        resources     = null;
        authHandler   = new AuthHandler(server, endPoint);
        loadMetadata();

        if (labCategories == null) {
          loadFHIRData(Platform.getDefaultURLResolver());
        }

        JSONObject o = getConfigurationData("patientSelectInfo");

        o.put("hasPatientAlerts", FHIRServer.getInstance().getResource("Appointment") != null);
        o.put("hasPatientFlags", getResource("Flag") != null);

        if (!hasPatientLocatorSupport()) {
          o.put("patientLocatorClass", "");
        }

        if (!hasBarCodeSupport()) {
          o.put("barcodeReaderClass", "");
        }

        o.put("alwaysShowSearchFirst", true);

        JSONArray a   = o.getJSONArray("listCategories");
        int       len = a.length();

        for (int i = len - 1; i >= 0; i--) {
          o = a.getJSONObject(i);

          if (o.opt("type").equals("category")) {
            String s = o.getString("listPath");

            if (s.equals("teams") || s.equals("specialities")) {
              a.remove(i);
            } else {
              o.put("enabled", false);
            }
          }
        }

        JSONObject li = getConfigurationData("labsInfo");

        li.put("bun_id", "bun");
        li.put("creatinine_id", "creat");
        li.put("showUnits", true);
        li = getConfigurationData("vitalsInfo");
        li.put("showUnits", true);
        li = getConfigurationData("notesInfo");
        li.put("documentURLColumn", 7);
        li = getConfigurationData("ordersInfo");
        li.put("hasClinicalCategories", false);
        li.put("hasOrderDiscontinueSupport", false);
        li.put("hasOrderEntrySupport", false);
        li.put("hasOrderDictationSupport", false);
        li.put("hasOrderRewriteSupport", false);
        li.put("hasOrderFlagSupport", false);
        li.put("hasOrderHoldSupport", false);
        li.put("hasOrderSignSupport", false);
        li.put("hasOrderSentenceSupport", false);
        li = getConfigurationData("tabsInfo");
        li.put("hasProcedures", getResource("Procedure") != null);
        li.put("hasNotes", getResource("DocumentReference") != null);
        a   = getConfigurationData("collectionsInfo").optJSONArray("collections");
        len = (a == null)
              ? 0
              : a.length();

        for (int i = 0; i < len; i++) {
          o = a.getJSONObject(i);
          o.put("autoUpdate", false);
        }
      }
    }
  }

  public boolean isDebugMode() {
    return debug || commandLine;
  }

  public boolean isDemoMode() {
    return demoMode;
  }

  public boolean isSessionValid() {
    if (authHandler != null) {
      authHandler.isTokenValid();
    }

    return true;
  }

  public OrderedProperties load(String file) throws IOException {
    return load(getDefaultResolver(), file);
  }

  public void logout() {
    if (authHandler != null) {
      authHandler.logout();
    }
  }

  /**
   * Gets the currently logged in user
   *
   * @return the currently logged in user
   */
  public JSONObject getUser() {
    if (isSessionValid()) {
      return authHandler.getLoggedInUser();
    }

    return null;
  }

  public Object putConfigurationData(String name, Object value) {
    if (commandLine) {
      return putLocalConfigurationData(name, value);
    }

    return Platform.getAppContext().putData(name, value);
  }

  @Override
  public void relogin(final iFunctionCallback cb) {
    if (authHandler != null) {
      Platform.clearSessionCookies();
      customLoginEx(this.serverConfig, cb, true);
    }
  }

  @Override
  public void serverConfigurationUpdated(Server s) {
    if (!s.isFrozen()) {
      String url = s.getURL();

      if (url != null) {
        JSONObject fhir = s.optJSONObject("fhir");

        if (fhir == null) {
          fhir = new JSONObject();
          s.put("fhir", fhir);
          fhir.put("get_encounter", true);

          JSONObject orders = new JSONObject();

          fhir.put("orders", orders);
          orders.put("generateNutritionReport", true);
          orders.put("generateDisgnosticReport", true);
          orders.put("generateMedicationReport", true);
          orders.put("generateMedicationDirections", true);
        }

        if (url.contains("fhir-open")) {
          s.put("restricted", false);
          s.put("custom_login", false);
          fhir.put("debug", true);
        } else {
          s.put("restricted", true);
          s.put("custom_login", false);
          fhir.put("debug", false);
        }
      }
    }
  }

  public void setPatientInfo(String id, String encounter) {
    this.patientID          = id;
    this.patientEncounterID = encounter;
  }

  @Override
  public String toString() {
    return publisher + " (DSTU" + version + ")";
  }

  protected void customLoginEx(final Server server, final iFunctionCallback cb, final boolean relogin) {
    final WindowViewer w    = Platform.getWindowViewer();
    aWorkerTask        task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          if (!relogin) {
            initialize(server);
            authHandler.setCallback(cb);
          } else {
            authHandler.setCallback(cb);

            if (authHandler.renewToken()) {
              return null;
            }
          }

          Browser b = new Browser();

          return b;
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        w.hideWaitCursor(true);

        if (result instanceof Throwable) {
          Utils.handleError((Throwable) result);

          return;
        }

        if (result == null) {    //relogin and and token renewal was successful
          cb.finished(false, null);

          return;
        }

        WebBrowser v = (WebBrowser) w.createViewer(w, (Viewer) result);

        v.setHandleWaitCursor(true);
        v.setEventHandler(iConstants.EVENT_CHANGE, authHandler, false);
        v.setDataURL(authHandler.createAuthorizeUrl());
        Utils.pushWorkspaceViewer(v);
      }
    };

    w.spawn(task);
    w.showWaitCursor();
  }

  protected void loadFHIRData(iURLResolver resolver) throws IOException {
    OrderedProperties p  = load(resolver, "/data/fhir/diagnostic_categories.properties");
    OrderedProperties lp = load(resolver, "/data/fhir/lab_categories.properties");

    for (Object o : lp.keySet()) {
      String s = (String) o;

      p.remove(s);
    }

    labCategories       = lp;
    procedureCategories = p;
    timingWhen          = load(resolver, "/data/fhir/timing_when.properties");
    timingCode          = load(resolver, "/data/fhir/timing_code.properties");
    timeUnits           = load(resolver, "/data/fhir/time_units.properties");

    if (!serverConfig.isRestricted() || ((authHandler == null) || (authHandler.authorizeUri == null))) {
      Reader r = resolver.getReader("/data/user.json");

      authHandler.loggedInUser = new JSONObject(Streams.readerToString(r));
      r.close();
    }

    if (resolver instanceof FileResolver) {
      strings = load(resolver, "/resource_strings.properties");
    }
  }

  private File createCacheFile(String name) {
    if (commandLine) {
      return new File("/Code/tmp/metadata_" + name + ".json");
    }

    return Platform.createCacheFile(name);
  }

  private void loadMetadata() throws Exception {
    String             data = null;
    final ObjectHolder oh   = new ObjectHolder(null);

    try {
      String name = Functions.sha1(endPoint);
      int    n    = name.indexOf('=');

      if (n != -1) {
        name = name.substring(0, n);
      }

      name = name.replace('+', '_');
      name = name.replace('/', '_');

      File f = createCacheFile(name);

      if ((f != null) && f.exists()) {
        long lm = f.lastModified();

        if ((lm + (1000 * 60 * 500)) > System.currentTimeMillis()) {
          FileReader r = new FileReader(f);

          data = Streams.readerToString(r);
          r.close();
        }
      }

      if (data == null) {
        ActionLink l = createResourceLink("metadata");

        data = l.getContentAsString();

        if (f != null) {
          FileWriter w = new FileWriter(f);

          w.write(data);
          w.close();
        }
      }
    } catch(Exception e) {
      ignoreException(null, e);
    }

    if (data == null) {
      ActionLink l = createResourceLink("metadata");

      data = l.getContentAsString();
    }

    resources = new HashMap<String, FHIRResource>();

    final JSONTokener t = new JSONTokener(new StringReader(data));

    t.setWatcher(new FHIRJSONWatcher(new aCallback() {
      @Override
      public Object entryEncountered(JSONObject entry) {
        String type = entry.optString("type", null);

        if (type != null) {
          resources.put(type, new FHIRResource(FHIRServer.this, type, entry));
        }

        return null;
      }
      @Override
      public Object otherArrayElementEncountered(String arrayName, Object value) {
        if ("rest/security/extension/extension".equals(arrayName)) {
          JSONObject o = (JSONObject) value;
          String     s = o.optString("url");

          if (s.equals("register")) {
            // registerUri = o.getString("valueUri");
          } else if (s.equals("authorize")) {
            authHandler.authorizeUri = o.optString("valueUri", null);
          } else if (s.equals("token")) {
            authHandler.tokenUri = o.optString("valueUri", null);
          } else if (s.equals("userinfo")) {
            authHandler.userinfoUri = o.optString("valueUri", null);
          }
        }

        return value;
      }
      @Override
      public boolean parseArray(String arrayName) {
        if ("rest/security/extension/extension".equals(arrayName)) {
          return true;
        }

        if (arrayName.equals("format")) {
          return true;
        }

        return false;
      }
      @Override
      public Object valueEncountered(JSONObject parent, String key, Object value) {
        String name = parent.getName();

        if (name == null) {
          String s;

          if (key.equals("fhirVersion")) {
            s = (String) value;

            float v = SNumber.floatValue(s);

            if ((v > 0.4) && (v < 2)) {
              version = v;
            } else {
              oh.value = new IOException("Unsupported FHIR version: " + s);
              t.setTerminateParsing(true);
            }
          } else if (key.equals("publisher")) {
            publisher = (String) value;
          } else if (key.equals("format")) {
            JSONArray a   = (JSONArray) value;
            int       len = a.length();
            int       i;
            boolean   json = false;

            for (i = 0; i < len; i++) {
              if (a.getString(i).contains("json")) {
                json = true;

                break;
              }
            }

            if (!json) {
              oh.value = new IOException("Server does not support JSON");
              t.setTerminateParsing(true);
            }
          }
        } else if (name.startsWith("rest/security/extension/extension")) {
          return value;
        }

        return null;
      }
    }, "rest/resource"));

    JSONObject o = new JSONObject(t);

    if (oh.value != null) {
      throw(Exception) oh.value;
    }

    t.dispose();

    JSONArray a = (JSONArray) serverConfig.opt("fhir", "resource_metadata");

    if (a != null) {
      int len = a.length();

      for (int i = 0; i < len; i++) {
        o = a.getJSONObject(i);

        String type = o.getString("type");

        if (resources.get(type) == null) {
          resources.put(type, new FHIRResource(this, type, o));
        }
      }
    }
  }

  /**
   * Decorates a link with the additional request headers needed to access the
   * server
   *
   * @param link
   *          the link
   */
  protected void decorateLink(ActionLink link) {
    if ((authHandler != null) && (authHandler.authHeader != null)) {
      link.setRequestHeader("Authorization", authHandler.authHeader);
    }
  }

  /**
   * Converts a date to its display format
   *
   * @param date
   *          the date
   * @return the date for display
   */
  public static String convertDate(Date date) {
    return dateContext.dateToString(date);
  }

  /**
   * Converts a date to its display format
   *
   * @param date
   *          the date
   * @return the date for display
   */
  public static String convertDate(String date) {
    try {
      if (_instance.commandLine) {
        return dateContext.parseAndFormat(date);
      }

      return Functions.convertDate(date);
    } catch(Exception e) {
      return date;
    }
  }

  /**
   * Converts a date and time to its display format
   *
   * @param date
   *          the date
   * @return the date and time for display
   */
  public static String convertDateTime(Date date) {
    return dateTimeContext.dateToString(date);
  }

  /**
   * Converts a date and time to its display format
   *
   * @param date
   *          the date
   * @return the date and time for display
   */
  public static String convertDateTime(String date) {
    try {
      if (date.indexOf('T') == -1) {
        return convertDate(date);
      }

      if (_instance.commandLine) {
        return dateTimeContext.parseAndFormat(date);
      }

      return Functions.convertDateTime(date);
    } catch(Exception e) {
      return date;
    }
  }

  /**
   * Converts a date string to a date object
   *
   * @param date
   *          the date string
   * @return the date object or null if the date was not able to be converted
   */
  public static Date toDate(String date) {
    try {
      return dateContext.dateFromString(date);
    } catch(Exception e) {
      return null;
    }
  }

  /**
   * Converts a date and time to the FHIR server format
   *
   * @param cal
   *          the date
   * @param time
   *          true to include the time; false otherwise
   * @return the date and time in the FHIR server format
   */
  public static String convertToServerDate(Calendar cal, boolean time) {
    return Helper.toString(cal, !time, false);
  }

  /**
   * Gets the singleton instance of the server
   *
   * @param return the singleton instance of the server
   */
  public static FHIRServer getInstance() {
    return _instance;
  }

  /**
   * Gets the style sheet to use when generating HTML
   *
   * @return the style sheet to use when generating HTML
   */
  public static String getStyleSheet() {
    if (_instance.commandLine) {
      try {
        FileReader r =
          new FileReader("/Code/Dev/appNativa/applications/BellaVista/BellaVista-android/assets/data/stylesheet.css");

        try {
          return Streams.readerToString(r);
        } finally {
          r.close();
        }
      } catch(Exception e) {
        return "";
      }
    } else {
      return Utils.getStyleSheet();
    }
  }

  /**
   * Gets the version of the FHIR server as a float
   *
   * @return the version of the FHIR server as a float
   */
  public static float getVersion() {
    return getInstance().version;
  }

  public static void main(String args[]) {
    try {
      createForCommandLineTesting();

      aFHIRemoteService p  = new Labs();
      StringWriter      sw = new StringWriter(2048);
      FileReader        r  = new FileReader("/Code/tmp/test.json");

      p.search(r, sw, new HttpHeaders(), new HttpHeaders());

      String s = sw.toString();

      System.out.println(s);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static void createForCommandLineTesting() throws Exception {
    FHIRServer server = new FHIRServer("https://fhir-open-api.smartplatforms.org/");
    //    FHIRServer server = new FHIRServer("https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/");
    //    FHIRServer server = new FHIRServer("http://fhirtest.uhn.ca/baseDstu2/");
    //    FHIRServer server =
    //      new FHIRServer("https://fhir-open.sandboxcernerpowerchart.com/dstu2/d075cf8b-3261-481d-97e5-ba6c48d3b41f/");

    server.commandLine  = true;
    server.serverConfig = new Server("Demo", "local", false);
    server.serverConfig.put("fhir", new JSONObject().put("non_compliant", true));

    FileResolver fr =
      new FileResolver(new File("/Code/Dev/appNativa/applications/BellaVista/BellaVista-android/assets")) {
      @Override
      public java.io.Reader getReader(String file) throws IOException {
        return new FileReader(baseFile.toString() + file);
      }
      ;
    };

    server.authHandler     = new AuthHandler(server.serverConfig, server.endPoint);
    server.defaultResolver = fr;
    server.loadFHIRData(fr);
    server.loadMetadata();
  }

  private static JSONObject getLocalConfigurationData(String name) {
    if (localConfig == null) {
      localConfig = new JSONObject();
    }

    JSONObject o = localConfig.optJSONObject(name);

    if (o == null) {
      o = new JSONObject();
      localConfig.put(name, o);
    }

    return o;
  }

  private static Object putLocalConfigurationData(String name, Object value) {
    if (localConfig == null) {
      localConfig = new JSONObject();
    }

    return localConfig.put(name, value);
  }

  static OrderedProperties load(iURLResolver resolver, String file) throws IOException {
    Reader            r = resolver.getReader(file);
    OrderedProperties p = new OrderedProperties();

    p.load(r);
    r.close();

    return p;
  }

  public static class FHIRResource {
    Boolean          search;
    Boolean          read;
    public JSONArray searchParams;
    public JSONArray interaction;
    public boolean   summarySupported;
    public boolean   readSupported;
    public boolean   countSupported;
    private String   name;

    FHIRResource(FHIRServer server, String name, JSONObject entry) {
      this.name    = name;
      searchParams = entry.optJSONArray("searchParam");

      int len = (searchParams == null)
                ? 0
                : searchParams.length();

      for (int i = 0; i < len; i++) {
        JSONObject o = searchParams.getJSONObject(i);

        o.remove("target");
        o.remove("definition");
        o.remove("documentation");
      }

      interaction   = entry.optJSONArray("interaction");
      readSupported = (interaction == null)
                      ? true
                      : interaction.findJSONObjectIndex("code", "read", 0) != -1;

      if (name.equals("Patient")) {
        Patients.initializeSupportedOptions(this, server.getConfigurationData("patientSelectInfo"));
      }

      summarySupported = hasSearchParam("_summary");
      countSupported   = hasSearchParam("_count");
    }

    public boolean canDo(String text) {
      if (interaction == null) {
        return false;
      }

      return interaction.findJSONObjectIndex("code", text, 0) != -1;
    }

    public boolean canRead() {
      if (read == null) {
        search = canDo("read-type");
      }

      return read.booleanValue();
    }

    public boolean canSearch() {
      if (search == null) {
        search = canDo("search-type");
      }

      return search.booleanValue();
    }

    public String getName() {
      return name;
    }

    public boolean hasSearchParam(String name) {
      return (searchParams == null)
             ? false
             : searchParams.findJSONObject("name", name) != null;
    }
  }


  static class AuthHandler implements iEventHandler {
    protected String  userinfoUri;
    JSONObject        loggedInUser;
    iFunctionCallback callback;
    Server            server;
    String            state;
    String            authHeader;
    long              tokenTimeout;
    String            tokenUri;
    String            authorizeUri;
    String            endPoint;
    String            refreshToken;
    boolean           redirected;
    boolean           basicAuth;
    JSONObject        fhir;

    public AuthHandler(Server server, String endPoint) {
      super();
      this.server = server;
      this.fhir   = server.optJSONObject("fhir");

      if (fhir == null) {
        fhir = new JSONObject();
      }

      if (endPoint.endsWith("/")) {
        endPoint = endPoint.substring(0, endPoint.length() - 1);
      }

      this.endPoint = endPoint;
    }

    /**
     * Called from {@link Account#login} to authenticate a user using basic
     * authentication
     *
     * @param username
     *          the username
     * @param password
     *          the password
     *
     * @return the authenticated user
     */
    public JSONObject login(final String username, final String password) throws IOException {
      final String url = fhir.optString("basic_auth_uri", authorizeUri);

      if (url == null) {
        return null;
      }

      ActionLinkEx l         = new ActionLinkEx(url);
      String       basicAuth = "Basic " + Functions.base64NOLF(username + ":" + password);

      l.setRequestHeader("Authorization", basicAuth);

      JSONObject o = new JSONObject(l.getContentAsString());

      if (o.optString("username", null) == null) {
        o.put("username", username);
      }

      authHeader   = basicAuth;
      loggedInUser = resolveUserInfo(o);

      return loggedInUser;
    }

    public JSONObject getLoggedInUser() {
      return loggedInUser;
    }

    public String createAuthorizeUrl() {
      HashMap<String, Object> data = new HashMap<String, Object>();
      long                    l1   = Functions.randomLong();
      long                    l2   = System.currentTimeMillis();

      if (l1 == l2) {
        l1 /= 3;
      }

      /**
       * String s1 = Long.toHexString(l1); String s2 = Long.toHexString(l2);
       *
       * state = Functions.sha1(s1 + s2); Using hex string as scope because some
       * servers
       */
      l1    ^= l2;
      state = Long.toHexString(l1);
      data.put("response_type", "code");
      data.put("client_id", fhir.getString("oauth_client_id"));
      data.put("redirect_uri", fhir.getString("oauth_redirect_uri"));
      data.put("scope", fhir.getString("oauth_scope"));
      data.put("state", state);
      data.put("aud", fhir.optString("oauth_aud", fhir.optString("aud")));

      ActionLink link = new ActionLink(authorizeUri);

      link.setAttributes(data);

      return link.toString();
    }

    public boolean isTokenValid() {
      if (!basicAuth) {
        long time     = System.currentTimeMillis();
        long ctimeout = fhir.optLong("client_timeout");

        if (ctimeout > 0) {
          ctimeout *= 1000;

          if (ctimeout + FHIRHttpURLConnection.lastConnectTime < time) {
            refreshToken = null;
            Platform.clearSessionCookies();
          }
        }

        tokenTimeout = 0;

        if ((time > tokenTimeout) &&!renewToken()) {
          return false;
        }
      }

      return authHeader != null;
    }

    public void logout() {
      authHeader   = null;
      callback     = null;
      refreshToken = null;
      loggedInUser = null;
      Platform.clearSessionCookies();
    }

    /**
     * Called by the browser widget during the Oauth process
     */
    @Override
    public void onEvent(String eventName, final iWidget widget, EventObject event) {
      if (redirected) {
        return;
      }

      DataEvent de  = (DataEvent) event;
      String    url = (String) de.getTarget();

      if ((url == null) ||!url.startsWith(fhir.getString("oauth_redirect_uri"))) {
        return;
      }

      redirected = true;
      ((WebBrowser) widget).stopLoading();

      int n = url.indexOf('?');

      if (n == -1) {
        error(Platform.getResourceAsString("bv.text.oauth_failure"), null);

        return;
      }

      Map<String, String> map = aRemoteService.getQueryParams(url.substring(n + 1));

      if (map.get("error") != null) {
        String s = map.get("error_Description");

        if (s == null) {
          s = map.get("error");
        }

        error(Platform.getResourceAsString("bv.text.oauth_failure") + "\n\n" + s, null);

        return;
      }

      if (!this.state.equals(map.get("state"))) {
        error(Platform.getResourceAsString("bv.text.oauth_state_failure"), null);

        return;
      }

      ((WebBrowser) widget).setAutoDispose(false);
      Utils.clearViewerStack();

      final WindowViewer w = Platform.getWindowViewer();

      w.hideWaitCursor(true);
      w.showProgressPopup(w.getString("bv.text.authenticating"));

      final String code = map.get("code");
      aWorkerTask  task = new aWorkerTask() {
        @Override
        public Object compute() {
          try {
            HashMap<String, String> data = new HashMap<String, String>();

            data.put("grant_type", "authorization_code");
            data.put("code", code);
            data.put("redirect_uri", fhir.getString("oauth_redirect_uri"));
            data.put("client_id", fhir.getString("oauth_client_id"));

            ActionLink link = new ActionLink(tokenUri);
            String     s    = link.sendFormData(null, data);
            JSONObject o    = new JSONObject(s);

            authHeader   = o.getString("token_type") + " " + o.getString("access_token");
            tokenTimeout = o.getInt("expires_in") * 1000 + System.currentTimeMillis();
            refreshToken = o.optString("refresh_token", null);

            String     id      = o.optString("id_token", null);
            String     profile = null;
            JSONObject user    = null;
            String     patient = o.optString("patient", null);

            if (patient != null) {
              int n = patient.lastIndexOf('/');

              if (n != -1) {
                patient = patient.substring(n + 1);
              }
            }

            if (id != null) {
              int n = id.indexOf('.');
              int p = id.indexOf('.', n + 1);

              id = id.substring(n + 1, p);
              id = Base64.decodeUTF8(id + "=");

              JSONObject oo = new JSONObject(id);

              profile = oo.optString("profile",null);
              id=fhir.optString("oauth_id_token_user_info_key",null);
              if(id!=null) {
                user=oo.optJSONObject(id);
                if(user!=null) {
                  resolveUserInfo(user);
                }
              }
            }

            if (user == null) {
              if (profile == null) {
                user = resolveUserInfo(null);
              } else {
                link = FHIRServer.getInstance().createResourceLink(profile);

                JSONObject oo = new JSONObject(link.getContentAsString());

                user = Users.populateUser(FHIRServer.getInstance().getID(profile, false), oo);
              }
            }

            user.put("patient", patient);

            return user;
          } catch(Exception e) {
            return e;
          }
        }
        @Override
        public void finish(Object result) {
          ((WebBrowser) widget).dispose();

          if (result instanceof Throwable) {
            error(null, (Throwable) result);
          } else {
            success((JSONObject) result);
          }
        }
      };

      w.spawn(task);
    }

    /**
     * Called to renew a bearer token
     *
     * @return
     */
    public boolean renewToken() {
      try {
        if (refreshToken != null) {
          WindowViewer w    = Platform.getWindowViewer();
          ActionLink   link = new ActionLink(authorizeUri);
          JSONObject   o    = new JSONObject(link.sendFormData(w, "grant_type", "refresh_token", "refresh_token",
                                refreshToken));

          if (o.containsKey("access_token")) {
            authHeader   = o.getString("token_type") + " " + o.getString("access_token");
            tokenTimeout = o.getInt("expires_in");
            refreshToken = o.optString("refresh_token", null);

            if (refreshToken != null) {}

            tokenTimeout = tokenTimeout * 1000 + System.currentTimeMillis();

            return true;
          }
        }
      } catch(Exception e) {
        Platform.ignoreException(e);
      }

      refreshToken = null;

      return false;
    }

    public void setCallback(iFunctionCallback callback) {
      this.callback   = callback;
      this.redirected = false;
    }

    /**
     * Gets the user information from a the data returned in an authorization
     * request
     *
     * @param o
     * @return
     * @throws IOException
     * @throws JSONException
     */
    protected JSONObject resolveUserInfo(JSONObject o) throws JSONException, IOException {
      if (o == null) {
        if (userinfoUri == null) {
          String s = tokenUri;
          int    n = s.lastIndexOf('/');

          s           = s.substring(0, n + 1) + "userinfo";
          userinfoUri = s;
        }

        ActionLink l = FHIRServer.getInstance().createLink(userinfoUri);

        o = new JSONObject(l.getContentAsString());
      }

      if (o.optString("username", null) == null) {
        o.put("username", o.optString("sub"));
      }

      String s = o.optString("name", null);

      if (s == null) {
        StringBuilder sb = new StringBuilder();

        s = o.optString("family_name", null);

        if (s != null) {
          sb.append(s).append(", ");
        }

        s = o.optString("given_name", null);

        if (s != null) {
          if (s != null) {
            sb.append(s);
          }
        }

        String m = o.optString("middle_name", null);

        if (m != null) {
          if (s != null) {
            sb.append(" ").append(m);
          } else {
            sb.append(m);
          }
        }

        if (sb.length() == 0) {
          sb.append("Anonymous User");
        }

        o.put("name", sb.toString());
      }

      return o;
    }

    void error(final String msg, final Throwable e) {
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          callback.finished(true, (e == null)
                                  ? new MessageException(msg)
                                  : new MessageException(e));
        }
      });
    }

    void success(final JSONObject result) {
      Object o  = result.remove("patient");
      String id = null;

      if (o instanceof JSONObject) {
        id = ((JSONObject) o).optString("id");
      } else if (o != null) {
        id = o.toString();
      }

      loggedInUser = result;

      if (id != null) {
        Utils.setActionPath(new ActionPath(id));
      }

      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          callback.finished(false, result);
        }
      });
    }
  }


  static class StreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
      return new FHIRHttpURLConnection(u);
    }
  }


  public Object basicAuthLogin(String username, String password) throws IOException {
    if (authHandler != null) {
      return authHandler.login(username, password);
    }

    return null;
  }
}
