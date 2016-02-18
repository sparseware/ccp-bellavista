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
import com.appnativa.rare.scripting.Functions;
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

import java.net.HttpURLConnection;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Documents extends aFHIRemoteService {
  public final static String  BOUNDARY       = "__FF00_SIRF_MULTIPART_BOUNDARY_PART_OOFF__";
  public final static String  BOUNDARY_START = "\r\n--__FF00_SIRF_MULTIPART_BOUNDARY_PART_OOFF__\r\n";
  public final static String  BOUNDARY_END   = "\r\n--__FF00_SIRF_MULTIPART_BOUNDARY_PART_OOFF__--\r\n";
  protected static boolean    hasDocuments;
  protected static String[]   COLUMN_NAMES;
  protected static JSONObject setAttachments;
  boolean                     docReferences;

  public Documents() {
    this("DocumentReference");
    docReferences = true;
  }

  public Documents(String resourceName) {
    super(resourceName);

    if (COLUMN_NAMES == null) {
      COLUMN_NAMES = new String[] {
        "date", "title", "author", "status", "type", "has_attachments", "parent_id", "data_url"
      };

      FHIRResource r = server.getResource(resourceName);

      hasDocuments = r != null;
      searchParams = (String) server.getServerConfig().opt("fhir", "vitals", "search_params");
    }

    columnNames = COLUMN_NAMES;
  }

  public void categories(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    noData(conn, path, false, headers);
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

  public void document_direct(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers)
          throws IOException {
    Map<String, String> map          = getQueryParams(conn.getURL(), data);
    String              url          = map.get("url");
    String              content_type = map.get("content_type");
    ActionLink          l            = server.createLink(url);

    if (content_type != null) {
      l.setRequestHeader("Accept", content_type);

      if (content_type.startsWith("text/plain")) {
        sendTextAsHTML(conn, headers, "", FHIRUtils.getDataAsString(url, content_type));

        return;
      }

      if (content_type.startsWith("image/")) {
        sendImageAsHTML(conn, headers, "", content_type, FHIRUtils.getDataAsBase64String(url, content_type));

        return;
      }

      if (content_type.startsWith("application/json+fhir")) {
        try {
          read(l.getReader(), conn.getContentWriter(), headers);
        } finally {
          l.close();
        }

        return;
      }
    }

    conn.setConnectionPipe((HttpURLConnection) l.getConnection().getConnectionObject());
  }

  public void list(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    if (!hasDocuments) {
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
  public void processEntry(JSONObject entry, JSONWriter jw, Writer w, CharArray ca, Object... params)
          throws IOException {
    Object     v;
    JSONObject o;
    boolean    parsed = false;

    if (!entry.optString("resourceType").equals(resourceName)) {
      return;
    }

    do {
      String dateld          = server.getID(entry.optString("id"), false);
      String date            = entry.optString(docReferences
              ? "created"
              : "date");
      String title           = entry.optString(docReferences
              ? "description"
              : "title");
      String authorld        = null;
      String author          = null;
      String status          = null;
      String type            = null;
      String data_url        = null;
      String has_attachments = null;

//      String parent_id=null;
      v = entry.opt("type");

      if ((title == null) || (title.length() == 0)) {
        title = MISSING_INVALID;
      }

      if (v instanceof String) {
        type = (String) v;
      } else if (v instanceof JSONObject) {
        type = FHIRUtils.getBestMedicalText((JSONObject) v);
      }

      JSONArray a = entry.optJSONArray("content");

      if (docReferences) {
        if ((a != null) &&!a.isEmpty()) {
          a = resolveContentArray(a, ca);

          if (a.length() > 1) {
            has_attachments = "true";
          } else {
            o = a.getJSONObject(0).getJSONObject("attachment");

            String s = o.optString("contentType");

            if (s.startsWith("text/") || s.startsWith("image/")) {
              data_url = createAttachmentHREF(dateld, o, ca);
            }
          }
        }
      }

      v = entry.opt("status");

      if (v instanceof String) {
        status = (String) v;
      } else if (v instanceof JSONObject) {
        status = FHIRUtils.getBestMedicalText((JSONArray) v);
      }

      o = entry.optJSONObject("author");

      if (o != null) {
        authorld = server.getID(o.optString("reference"), false);
        author   = o.optString("display");
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

        if (title != null) {
          jw.key("title").value(title);
        }

        if (author != null) {
          if (authorld != null) {
            jw.key("author").object();
            jw.key("linkedData").value(authorld).key("value").value(author);
            jw.endObject();
          } else {
            jw.key("author").value(author);
          }
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
        if (data_url != null) {
          jw.key("data_url").value(data_url);
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

        if (title != null) {
          FHIRUtils.writeQuotedStringIfNecessary(w, title, ca);
        }

        w.write((char) '^');

        if (author != null) {
          if (authorld != null) {
            w.write(authorld);
            w.write((char) '|');
          }

          FHIRUtils.writeQuotedStringIfNecessary(w, author, ca);
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

        if (data_url != null) {
          w.write(data_url);
        }

        w.write((char) '^');
        w.write((char) '\n');
      }

      parsed = true;
    } while(false);

    if (!parsed) {
      debugLog("Could not parse entry:\n" + entry.toString(2));
    }
  }

  @Override
  public void readEntry(JSONObject entry, JSONWriter jw, Writer w, Object... params) throws IOException {
    if (!entry.optString("resourceType").equals("Composition")) {
      docReferences = false;
    }

    HttpHeaders headers = (HttpHeaders) params[0];

    headers.setDefaultResponseHeaders();

    JSONArray a = entry.optJSONArray(docReferences
                                     ? "content"
                                     : "sanction");

    if (docReferences) {
      a = resolveContentArray(a, null);
    }

    if ((a != null) && (a.length() > 1)) {
      headers.mimeMultipart(BOUNDARY);

      if (docReferences) {
        JSONObject ao   = a.getJSONObject(0).getJSONObject("attachment");
        String     type = ao.optString("contentType", "text/plain");

        if (!type.startsWith("text/")) {
          a.add(0, getSeeAttachmentsObject());
        }
      }
    } else {
      headers.mimeHtml();
    }

    if (docReferences) {
      processContent(a, w);
    } else {
      processSection(entry, w, false);
    }
  }

  public static void writeAttachmentsAsDocument(JSONArray a, Writer w, HttpHeaders headers) throws IOException {
    JSONObject ao   = a.getJSONObject(0);
    String     type = ao.optString("contentType", "text/plain");

    if (a.length() > 1) {
      headers.mimeMultipart(BOUNDARY);

      if (!type.startsWith("text/")) {
        a.add(0, getSeeAttachmentsObject());
      }

      processAttachments(a, w);
    } else {
      headers.mimeHtml();
      FHIRUtils.writeAttachment(ao, w, false);
    }
  }

  public static void writeAttachmentDocumentIndex(String id, JSONObject attachment, Writer w, boolean main,
          CharArray ca)
          throws IOException {
    String title = attachment.optString("title", null);

    if (title == null) {
      title = FHIRServer.getInstance().getResourceAsString("bv.text.attachment");
    }

    String mime = attachment.optString("contentType", "text/plain");
    String type;

    if (mime.startsWith("text/")) {
      type = "document";
    } else if (mime.startsWith("image/")) {
      type = "image";
    } else {
      type = ca.set("custom:").append(mime).toString();
    }

    w.append(type).append('^').append(title);
    w.append("^").append(attachment.optString("creation")).append("^").append(main
            ? "true"
            : "false");
    w.append("^");

    if (!main) {
      String url = createAttachmentHREF(id, attachment, ca);

      if (url != null) {
        w.write(url);
      }
    }

    w.append("\n");
  }

  public static void processContent(JSONArray a, Writer w) throws IOException {
    int len = a.length();

    if (len > 0) {
      CharArray  ca      = new CharArray();
      JSONObject content = a.getJSONObject(0);

      if (len > 1) {
        writeAttachmentDocumentIndex(content.optString("id", null), content.getJSONObject("attachment"), w, true, ca);

        for (int i = 1; i < len; i++) {
          content = a.getJSONObject(i);
          writeAttachmentDocumentIndex(content.optString("id", null), content.getJSONObject("attachment"), w, false,
                                       ca);
        }
      }

      FHIRUtils.writeAttachment(content.getJSONObject("attachment"), w, len > 1);

      if (len > 1) {
        w.write(BOUNDARY_END);
      }
    }
  }

  public static void processAttachments(JSONArray a, Writer w) throws IOException {
    int       len = a.length();
    CharArray ca  = new CharArray();

    if (len > 1) {
      writeAttachmentDocumentIndex(null, a.getJSONObject(0), w, true, ca);

      for (int i = 1; i < len; i++) {
        JSONObject o = a.getJSONObject(i);

        writeAttachmentDocumentIndex(o.optString("id"), o, w, false, ca);
      }
    }

    FHIRUtils.writeAttachment(a.getJSONObject(0), w, len > 1);

    if (len > 1) {
      w.write(BOUNDARY_END);
    }
  }

  @Override
  protected void read(Reader r, Object writer, HttpHeaders headers, Object... params) throws IOException {
    JSONObject entry = getReadEntry(r);

    readEntry(entry, null, (Writer) writer, headers);
  }

  static JSONObject getSeeAttachmentsObject() {
    if (setAttachments == null) {
      String s = FHIRServer.getInstance().getResourceAsString("bv.text.see_report_attachments");

      s = Functions.base64NOLF(s);

      JSONObject content    = new JSONObject();
      JSONObject attachment = new JSONObject().put("contentType", "text/html").put("data", s);

      content.put("attachment", attachment);
      setAttachments = content;
    }

    return setAttachments;
  }

  @SuppressWarnings("resource")
  public static JSONArray resolveContentArray(JSONArray a, CharArray ca) {
    int                               len     = a.length();
    String                            type    = null;
    JSONObject                        best    = null;
    String                            bestKey = null;
    String                            s;
    LinkedHashMap<String, JSONObject> map = new LinkedHashMap<String, JSONObject>(len);

    if (ca == null) {
      ca = new CharArray();
    }

    for (int i = 0; i < len; i++) {
      JSONObject content = a.getJSONObject(i);
      JSONObject o       = content.optJSONObject("format");

      s = (o == null)
          ? null
          : FHIRUtils.getBestMedicalCode(o);
      ca.set((s == null)
             ? ""
             : s);
      o = content.getJSONObject("attachment");
      s = o.optString("url", String.valueOf(o.hashCode()));
      ca.append("^").append((s == null)
                            ? ""
                            : s);
      s    = ca.toString();
      type = o.optString("contentType", null);

      if (type != null) {
        content.put("_type", type);

        if (type.startsWith("text/html")) {
          best    = content;
          bestKey = s;
        } else if ((best == null) && type.startsWith("text/")) {
          best    = content;
          bestKey = s;
        }
      } else if (best == null) {
        best    = content;
        bestKey = s;
      }

      JSONObject econtent = map.get(s);

      if (econtent != null) {
        if ((type != null) && type.startsWith("image/")) {
          String etype = econtent.getJSONObject("attachment").optString("contentType", null);

          if (etype != null) {
            if (type.startsWith("image/svg")) {
              map.put(s, content);
            } else if (type.startsWith("image/jpeg") &&!etype.startsWith("image/svg")) {
              map.put(s, content);
            } else if (type.startsWith("image/png") &&!etype.startsWith("image/jpeg")
                       &&!etype.startsWith("image/svg")) {
              map.put(s, content);
            }
          } else {
            map.put(s, content);
          }
        }
      } else {
        map.put(s, content);
      }

      if ((s != null) && s.startsWith("text/")) {
        if (type == null) {
          type = s;
          best = content;
        } else if (s.startsWith("text/html")) {
          type = s;
          best = content;
        }
      }
    }

    a = new JSONArray();

    if (bestKey != null) {
      map.remove(bestKey);
      a.add(best);
    }

    Iterator<JSONObject> it = map.values().iterator();

    while(it.hasNext()) {
      a.add(it.next());
    }

    return a;
  }

  public static void writeSectionDocumentIndex(String type, JSONObject section, Writer w, boolean main)
          throws IOException {
    w.append(type).append('^').append(section.optString("title"));
    w.append("^").append(section.optString("date")).append("^").append(main
            ? "true"
            : "false");
    w.append("^\n");
  }

  public static String createAttachmentHREF(String contenid, JSONObject attachment, CharArray ca) {
    String url = attachment.optString("url", null);

    if ((url != null) && url.startsWith("data:")) {
      url = null;
    }

    if (url != null) {
      if (url.startsWith("/")) {
        url = url.substring(1);
      }

      String s = attachment.optString("contentType", null);

      ca.set("/hub/main/documents/document_direct?url=");
      ca.append(Functions.encodeUrl(url));

      if (s != null) {
        ca.append("&content_type=").append(Functions.encode(s));
      }

      url = ca.toString();
    }

    return url;
  }

  static void processSection(JSONObject section, Writer w, boolean child) throws IOException {
    String    date     = section.optString("date");
    JSONArray sections = section.optJSONArray("section");
    int       len      = (sections == null)
                         ? 0
                         : sections.length();

    if (len > 0) {
      writeSectionDocumentIndex("document", section, w, true);

      for (int i = 0; i < len; i++) {
        JSONObject o = sections.getJSONObject(i);

        writeSectionDocumentIndex("document", o, w, false);
      }

      w.write(BOUNDARY_START);
      w.write("Content-Type: text/html\r\n\r\n");
    } else if (child) {
      w.write(BOUNDARY_START);
      w.write("Content-Type: text/html\r\n\r\n");
    }

    writeSection(section, w);

    if (len > 0) {
      processSections(date, sections, w);
      w.write(BOUNDARY_END);
    }
  }

  static void processSections(String date, JSONArray sections, Writer w) throws IOException {
    int len = sections.length();

    for (int i = 0; i < len; i++) {
      JSONObject o = sections.getJSONObject(i);

      if (!o.containsKey("date")) {
        o.put("date", date);
      }

      processSection(o, w, true);
    }
  }

  static void writeSection(JSONObject section, Writer w) throws IOException {
    w.append("<html><head><title>").append(section.optString("title"));
    w.append("</title></head><body>\n");
    w.append("<h1 class='fhir_title'>").append("title").append("</h1>\n");
    w.append("<h3 class='fhir_section'>").append(section.optString("title")).append("</h3>\n");
    w.append("<div class='fhir_section_div'>\n");

    Object o = section.opt("text");
    String s = "";

    if (o instanceof String) {
      s = (String) o;
    } else if (o instanceof JSONObject) {
      s = ((JSONObject) o).optString("div");
    }

    w.append(s);
    w.append("\n</div>\n");
    w.append("\n</body></html>");
  }
}
