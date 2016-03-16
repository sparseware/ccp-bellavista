package com.sparseware.bellavista.external.fhir;

import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.CharArray;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONTokener;
import com.appnativa.util.json.JSONWriter;

import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.MessageException;
import com.sparseware.bellavista.external.ActionLinkEx;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher.aCallback;
import com.sparseware.bellavista.external.fhir.FHIRJSONWatcher.iCallback;
import com.sparseware.bellavista.external.fhir.FHIRServer.FHIRResource;
import com.sparseware.bellavista.external.fhir.FHIRUtils.MedicalCode;
import com.sparseware.bellavista.service.ContentWriter;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.NonFatalServiceException;
import com.sparseware.bellavista.service.aRemoteService;
import com.sparseware.bellavista.service.iHttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public abstract class aFHIRemoteService extends aRemoteService {
  protected FHIRServer      server;
  protected String          columnNames[];
  protected boolean         summarySupported;
  protected boolean         readSupported;
  protected boolean         prefixSearchSupported;
  protected boolean         countSupported;
  protected String          resourceName;
  protected ActionLinkEx    lastLink;
  protected String          searchParams;
  static HashSet<String>    validExtensions = new HashSet<String>();
  boolean                   nonConformant;
  protected String          pagingPath;
  public static String      MISSING_INVALID      = "{fgColor:badData}MISSING/INVALID FHIR DATA";
  public static String      MISSING_INVALID_HTML = "{fgColor:badData}MISSING/INVALID FHIR DATA";
  public static String      BAD_ID               = "_BAD_ID_";
  public static MedicalCode missingInvalid       = new MedicalCode(BAD_ID, null, MISSING_INVALID, null);

  static {
    validExtensions.add("json");
    validExtensions.add("txt");
    validExtensions.add("html");
  }

  public aFHIRemoteService() {
    server                = FHIRServer.getInstance();
    nonConformant         = server.getServerConfig().optBoolean("non_compliant");
    prefixSearchSupported = server.getServerConfig().optBoolean("supports_prefix_search", true);
  }

  public aFHIRemoteService(String resourceName) {
    server = FHIRServer.getInstance();

    JSONObject o = server.getServerConfig().optJSONObject("fhir");

    nonConformant     = (o == null)
                        ? false
                        : o.optBoolean("non_conformant");
    this.resourceName = resourceName;

    FHIRResource r = server.getResource(resourceName);

    summarySupported = (r == null)
                       ? false
                       : r.summarySupported;
    readSupported    = (r == null)
                       ? false
                       : r.readSupported;
    countSupported   = (r == null)
                       ? false
                       : r.countSupported;
  }

  protected String getPagingPath() {
    if (pagingPath == null) {
      String pkg = aFHIRemoteService.class.getPackage().getName();
      String s   = getClass().getName().substring(pkg.length() + 1);;

      s          = s.toLowerCase(Locale.US).replace('.', '/') + "/paging";
      pagingPath = "/hub/main/" + s;
    }

    return pagingPath;
  }

  protected String createPagingUrl(String href, boolean json) {
    @SuppressWarnings("resource") CharArray ca = new CharArray(getPagingPath());

    ca.append('/').append(encodeLink(href));

    if (json) {
      ca.append(".json");
    }

    return ca.toString();
  }

  public Map<String, JSONObject> createReferenceMap(JSONArray a) {
    int                               len = (a == null)
            ? 0
            : a.length();
    LinkedHashMap<String, JSONObject> map = new LinkedHashMap<String, JSONObject>(len);

    for (int i = 0; i < len; i++) {
      JSONObject o  = a.getJSONObject(i);
      String     id = o.optString("id", null);

      if (id != null) {
        map.put(id, o);
      }
    }

    return map;
  }

  public void sendTextAsHTML(iHttpConnection conn, HttpHeaders headers, String title, String data) throws IOException {
    headers.setDefaultResponseHeaders();
    headers.mimeHtml();

    ContentWriter w = conn.getContentWriter();

    FHIRUtils.writeHTMLDocumentStart(w, title);
    w.write("<pre>\n");
    w.write(data);
    w.write("</pre>\n");
    FHIRUtils.writeHTMLDocumentFinish(w);
  }

  public void sendImageAsHTML(iHttpConnection conn, HttpHeaders headers, String title, String type, String b64Data)
          throws IOException {
    headers.setDefaultResponseHeaders();
    headers.mimeHtml();

    ContentWriter w = conn.getContentWriter();

    FHIRUtils.writeHTMLDocumentStart(w, null);
    w.write("<div class=\"image_doc\">\n");
    w.write("<img alt=\"" + title + "\"");
    w.write(" src=\"data:" + type + ";base64,");
    w.write(b64Data);
    w.write("\"/>\n");
    w.write("</div>\n");
    FHIRUtils.writeHTMLDocumentFinish(w);
  }

  protected String getDateTime(JSONObject o) {
    String date = o.optString("effectiveDateTime", null);

    if (date == null) {
      date = o.optString("issued");

      if (date == null) {
        JSONObject oo = o.optJSONObject("effectivePeriod");

        if (oo != null) {
          date = oo.optString("start", null);

          if (date == null) {
            date = oo.optString("end", null);
          }
        }
      }
    }

    if ((date == null) || (date.length() == 0)) {
      date = getResourceAsString("bv.text.unspecified_date");
    }

    return date;
  }

  protected String encodeLink(String url) {
    return Functions.base64NOLF(url);
  }

  protected String decodeLink(String link) {
    int n = link.lastIndexOf('.');

    if (n != -1) {
      link = link.substring(0, n);
    }

    return Functions.decodeBase64(link);
  }

  protected ActionLinkEx createSearchLink(String... params) {
    StringBuilder sb = new StringBuilder(server.endPoint);

    sb.append(resourceName);
    sb.append('?');

    int i = 0;

    if ((params != null) && (params.length > 0)) {
      if ((params != null) && (params.length > 0)) {
        int len = params.length;

        while(i < len) {
          if (i > 0) {
            sb.append('&');
          }

          String s = params[i++];

          if (!countSupported && s.equals("_count")) {
            i++;
          } else {
            sb.append(s).append('=').append(params[i++]);
          }
        }
      }
    }

    if (summarySupported) {
      if (i > 0) {
        sb.append('&');
      }

      sb.append("_summary=true");
      i++;
    }

    if (searchParams != null && searchParams.length()>0) {
      if (i > 0) {
        sb.append('&');
      }

      sb.append(searchParams);
    }
    i=sb.length();
    if(sb.charAt(i-1)=='&') {
      sb.setLength(i-1);
    }
    ActionLinkEx l = server.createLink(sb.toString());

    lastLink = l;

    return l;
  }

  protected ActionLinkEx createTextLink(String id) {
    id = removeExtension(id);

    StringBuilder sb = new StringBuilder(server.endPoint);

    sb.append(resourceName).append('/').append(id);

    if (summarySupported) {
      sb.append("?_summary=text");
    }

    ActionLinkEx l = server.createLink(sb.toString());

    lastLink = l;

    if (!readSupported) {    //should never happen for a build out server but put this here so that the client can be used for testing
      throw new NonFatalServiceException(
          "This server does not support the READ interaction on the specified resource. Please notify the service administrator.");
    }

    return l;
  }

  protected ActionLinkEx createReadLink(String id) {
    return createReadLink(resourceName, id);
  }

  protected String removeExtension(String id) {
    if (id != null) {
      int n = id.lastIndexOf('.');

      if (n != -1) {
        String s = id.substring(n + 1);

        if (validExtensions.contains(s)) {
          id = id.substring(0, n);
        }
      }
    }

    if (nonConformant && BAD_ID.equals(id)) {
      throw new NonFatalServiceException(server.getResourceAsString("bv.text.bad_fhir_id"));
    }

    return id;
  }

  protected ActionLinkEx createReadLink(String resourceName, String id) {
    StringBuilder sb = new StringBuilder(server.endPoint);

    sb.append(resourceName).append('/').append(removeExtension(id));

    ActionLinkEx l = server.createLink(sb.toString());

    lastLink = l;

    if (!readSupported &&!nonConformant) {    //should never happen for a build out server but put this here so that the client can be used for testing
      throw new NonFatalServiceException(
          "This server does not support the READ interaction on the specified resource. Please notify the service administrator.");
    }

    return l;
  }

  protected ActionLinkEx createReferenceReadLink(String reference) {
    String       url = server.fixLink(reference);
    ActionLinkEx l   = server.createLink(url);

    lastLink = l;

    return l;
  }

  protected void resolveReferences(JSONArray refs, Map<String, JSONObject> map) throws IOException {
    int                     len   = refs.length();
    ArrayList<ActionLinkEx> links = null;

    for (int i = len - 1; i > -1; i--) {
      String ref = refs.getJSONObject(i).getString("reference");

      if (ref.startsWith("#")) {
        if (map != null) {
          JSONObject o = map.remove(ref.substring(1));

          if (o != null) {
            refs.set(i, o);
          } else {
            refs.remove(i);
          }
        }
      } else {
        ActionLinkEx l = createReferenceReadLink(ref);

        if (links == null) {
          links = new ArrayList<ActionLinkEx>(len);
        }

        l.setLinkedIndex(i);
        links.add(l);
      }
    }

    if (links != null) {
      len = links.size();

      LinkWaiter waiter = null;

      if (len > 1) {
        waiter = new LinkWaiter(len - 1);

        for (int i = 1; i < len; i++) {
          waiter.addLink(links.get(i));
        }
      }

      ActionLinkEx l = links.get(0);
      JSONTokener  t = null;

      try {
        t = new JSONTokener(l.getReader());

        JSONObject o = new JSONObject(t);

        refs.set(l.getLinkedIndex(), o);

        if (waiter != null) {
          waiter.startWaiting();

          if (!waiter.hadError()) {
            for (int i = 1; i < len; i++) {
              ActionLinkEx ln = links.get(i);

              refs.set(ln.getLinkedIndex(), waiter.getResult(i - 1));
            }
          }
        }
      } catch(Exception e) {
        if (waiter != null) {
          waiter.cancel(null, null);
        }
      } finally {
        l.close();

        if (t != null) {
          t.dispose();
        }

        if (waiter != null) {
          waiter.dispose();
        }
      }
    }
  }

  protected JSONObject getReference(String ref) throws IOException {
    ActionLinkEx l = createReferenceReadLink(ref);
    JSONTokener  t = null;

    try {
      t = new JSONTokener(l.getReader());

      JSONObject o = new JSONObject(t);

      return o;
    } finally {
      if (t != null) {
        t.dispose();
      }

      l.close();
    }
  }

  public ActionLinkEx getLastLink() {
    return lastLink;
  }

  protected void search(Reader r, Object writer, final HttpHeaders headers, final Object... params) throws IOException {
    final JSONWriter jw;
    final Writer     w;
    final CharArray  ca = new CharArray();

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

      for (String name : columnNames) {
        jw.value(name);
      }

      jw.endArray();
      jw.key("_rows").array();
    }

    JSONTokener t  = new JSONTokener(r);
    iCallback   cb = new aCallback() {
      @Override
      public Object entryEncountered(JSONObject entry) {
        try {
          processResource(entry, jw, w, ca, params);
        } catch(Exception e) {
          throw ApplicationException.runtimeException(e);
        }

        return null;
      }
      @Override
      public Object linkEncountered(String arrayName, String type, String url) {
        if ("link".equals(arrayName)) {    //top level link
          if (type.equals("next")) {
            headers.hasMore(createPagingUrl(cleanLink(url), jw != null));
          } else if (type.equals("self")) {
            headers.setLinkInfo(createPagingUrl(cleanLink(url), jw != null));
          }
        }

        return null;
      }
    };

    t.setWatcher(new FHIRJSONWatcher(cb));

    @SuppressWarnings("unused") JSONObject o = new JSONObject(t);

    t.dispose();
    parsingComplete(jw, w, ca, params);

    if (jw != null) {
      jw.endArray();
      jw.endObject();
    }
  }

  protected void read(Reader r, Object writer, final HttpHeaders headers, final Object... params) throws IOException {
    JSONObject o = getReadEntry(r);

    if (o != null) {
      final JSONWriter jw;
      final Writer     w;

      if (writer instanceof JSONWriter) {
        jw = (JSONWriter) writer;
        jw.object();
        w = null;
      } else {
        w  = (Writer) writer;
        jw = null;
      }

      readEntry(o, jw, w, params);

      if (jw != null) {
        jw.endObject();
      }
    }
  }

  public static JSONObject getReadEntry(Reader r) {
    JSONTokener t = new JSONTokener(r);

    try {
      JSONObject o = new JSONObject(t);

      t.dispose();

      JSONObject entry = o;

      if (o.optString("resourceType").equals("Bundle")) {
        JSONArray a   = o.optJSONArray("entry");
        int       len = (a == null)
                        ? 0
                        : a.length();

        if (len > 0) {
          entry = a.getJSONObject(0).optJSONObject("resource");
        }
      }

      if (entry.optString("resourceType").equals("OperationOutcome")) {
        JSONArray issue = entry.optJSONArray("issue");

        if (issue != null) {
          throw new ApplicationException(issue.toString());
        }
      }

      return entry;
    } finally {
      t.dispose();
    }
  }

  protected void writeLinkedData(JSONWriter jw, String key, String linkedData, String value) throws IOException {
    if (linkedData == null) {
      jw.key(key).value(value);
    } else {
      jw.key(key).object();

      if (linkedData != null) {
        jw.key("linkedData").value(linkedData);
      }

      jw.key("value").value(value);
      jw.endObject();
    }
  }

  protected String cleanLink(String href) {
    int n = href.indexOf(':');

    if (n != -1) {
      int p = href.indexOf('/');

      if (p != -1) {
        if (n < p) {
          String ep = server.endPointNoSlash;

          if (!href.startsWith(ep)) {
            if (server.commandLine) {
              return href;
            }

            throw new ApplicationException(server.getString("bv.text.bad_fhir_link", ep, href));
          } else {
            href = href.substring(ep.length());
          }
        }
      }
    }

    return href;
  }

  protected void parsingComplete(JSONWriter jw, Writer w, CharArray ca, Object... params) throws IOException {}

  protected void processResource(JSONObject resource, JSONWriter jw, Writer w, CharArray ca, Object... params)
          throws IOException {
    if (nonConformant) {
      JSONObject o = resource.getJSONObject("resource");

      o.put("_link", resource.optJSONArray("link"));
      processEntry(o, jw, w, ca, params);
    } else {
      processEntry(resource.getJSONObject("resource"), jw, w, ca, params);
    }
  }

  protected String getID(JSONObject entry) {
    String id = entry.optString("id", null);

    if ((id == null) && nonConformant) {
      JSONArray  link = entry.optJSONArray("_link");
      JSONObject o    = (link == null)
                        ? null
                        : link.findJSONObject("relation", "self");

      if (o != null) {
        id = o.optString("url", null);
      }
    }

    if (id != null) {
      int n = id.lastIndexOf('/');

      if (n != -1) {
        id = id.substring(n + 1);
      }
    }

    if (nonConformant) {
      if ((id == null) || (id.length() == 0) || id.equals("0")) {
        id = "__BV_BAD_ID__";
      }
    }

    return id;
  }

  protected String getID(String id) {
    if (id != null) {
      int n = id.lastIndexOf('/');

      if (n != -1) {
        id = id.substring(n + 1);
      }
    }

    return id;
  }

  public void paging(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    String       href = decodeLink(path.toString());
    ActionLinkEx l    = server.createResourceLink(href);

    try {
      Object w = FHIRUtils.createWriter(path, conn.getContentWriter(), headers, true);

      search(l.getReader(), w, headers);
    } finally {
      l.close();
    }
  }

  public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... params)
          throws IOException {}

  public abstract void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException;

  protected void debugLog(String msg) {
    if (server.debug) {
      super.debugLog(msg);
    } else {
      server.debugLog(msg);
    }
  }

  protected void ignoreException(Exception e) {
    if (server.debug) {
      super.ignoreException(e);
    } else {
      server.ignoreException(e);
    }
  }

  protected String getResourceAsString(String name) {
    return server.getResourceAsString(name);
  }

  protected RuntimeException missingRequiredData(String name, String id) {
    return missingRequiredData(name, id, resourceName);
  }

  protected RuntimeException missingRequiredData(String name, String id, String resourceName) {
    StringBuilder sb = new StringBuilder();

    sb.append("Required element '").append(name);
    sb.append("' is missing from FHIR '").append(resourceName).append("'");

    if (id != null) {
      sb.append(" with id:").append(id);
    }

    return new MessageException(sb.toString(), true);
  }

  protected RuntimeException invalidData(String name, String id) {
    StringBuilder sb = new StringBuilder("Invalid/missing data ");

    if (name != null) {
      ;
    }

    {
      sb.append("for element '").append(name).append("' ");
    }
    sb.append(" in FHIR '").append(resourceName).append("'");

    if (id != null) {
      sb.append(" with id:").append(id);
    }

    return new MessageException(sb.toString(), true);
  }
}
