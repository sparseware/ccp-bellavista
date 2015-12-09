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

package com.sparseware.bellavista.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appnativa.rare.Platform;
import com.appnativa.util.CharArray;
import com.appnativa.util.CharScanner;
import com.appnativa.util.Streams;
import com.appnativa.util.URLEncoder;
import com.sparseware.bellavista.ActionPath;

/**
 * This class represents a object that will implement connectivity to a remote service.
 * Sub-classes will implements methods that correspond to the objects in the REST api
 * that the client uses (e.g. the {@code allergies} method on the {@code Patient} sub-class class will represent
 * {@code '/hub/main/patient/allergies'}
 * 
 * @author Don DeCoteau
 *
 */
public abstract class aRemoteService {

  /** Starting HTML to use for wrapping plain text in an HTML document */
  public static final String HTML_START =
    "<html>\n<body>\n<pre style=\"font-size:0.8em; font-family: Lucida Console, Monaco, Menlo, Courier New,monospace\">";

  /** Ending HTML to use for wrapping plain text in an HTML document */
  public static final String HTML_END = "</pre>\n</body>\n</html>";

  /**
   * Creates a new instance
   */
  public aRemoteService() {}

  /**
   * Converts plain text into an HTML document
   * @param text the plain text
   * @return the HTML document
   */
  public static String textToHTML(String text) {
    if (text.startsWith("</html>")) {
      return text;
    } else if (text.startsWith("<div>")) {
      return "<html>" + text + "</html>";
    } else {
      return HTML_START + text + HTML_END;
    }
  }

  /**
   * Get the file extension in the specified path
   * @param path the path
   * @return the extension or null
   */
  public static String getExtension(ActionPath path) {
    String s = path.peek();
    int    n = (s == null)
               ? -1
               : s.indexOf('.');

    return (n == -1)
           ? null
           : s.substring(n + 1);
  }

  /**
   * A service method that serves up the demo data for the specified path.
   * This allows you to run the client and test out new service functionality
   * and stub out methods with a call to this method for functionality thats not yet implemented
   *
   * @param conn the connection initiated by the client that caused this end point to be invoked
   * @param path the path for the request
   * @param data the input for the request or null if there was none
   * @param headers the object that holds the headers that will be returned to the client
   * @throws IOException
   */
  public void demo(iHttpConnection conn, ActionPath path, InputStream data, HttpHeaders headers) throws IOException {
    String s = conn.getURL().getFile();
    int    n = s.indexOf("/hub/");

    if (n != -1) {
      s = s.substring(n);
    }

    URL               u  = new URL("http://192.168.1.51/apps/BellaVista/BellaVista-android/assets" + s);
    HttpURLConnection uc = (HttpURLConnection) u.openConnection();

    uc.setRequestMethod(conn.getRequestMethod());

    if (data != null) {
      uc.setDoOutput(true);
      Streams.streamToStream(data, uc.getOutputStream(), null);
    }

    InputStream stream = uc.getInputStream();

    headers.setStatus(uc.getHeaderField(0));

    int i = 1;

    while(i < 1000) {
      String key = uc.getHeaderFieldKey(i++);

      if (key == null) {
        break;
      }

      headers.add(key, uc.getHeaderField(key));
    }

    Streams.streamToWriter(stream, conn.getContentWriter(), null);
    stream.close();
  }

  /**
   * A service method that returns no data for the specified path.
   * The extension of the file on the path and the {@code row} parameter will determine
   * the returned mime type.
   *
   * @param conn the connection initiated by the client that caused this end point to be invoked
   * @param path the path for the request
   * @param row true for row oriented data; false otherwise
   * @param headers the object that holds the headers that will be returned to the client
   * @throws IOException
   */
  public void noData(iHttpConnection conn, ActionPath path, boolean row, HttpHeaders headers) throws IOException {
    headers.setDefaultResponseHeaders();

    String ext = getExtension(path);

    if ("json".equals(ext)) {
      headers.mimeJson();
      conn.getContentWriter().write("{}");
    } else if ("html".equals(ext)) {
      headers.mimeHtml();
    } else if ("txt".equals(ext)) {
      headers.mimeText();
    } else {
      if (row) {
        headers.mimeRow();
      } else {
        headers.mimeList();
      }
    }

    conn.getContentWriter();    //bv.text.cant_load_document
  }

  /**
   * A service method that returns no data for the specified path.
   * The extension of the file on the path will determine
   * the returned mime type (text/plain or text/html)
   *
   * @param conn the connection initiated by the client that caused this end point to be invoked
   * @param path the path for the request
   * @param row true for row oriented data; false otherwise
   * @param headers the object that holds the headers that will be returned to the client
   * @throws IOException
   */
  public void noDocument(iHttpConnection conn, ActionPath path, HttpHeaders headers) throws IOException {
    headers.setDefaultResponseHeaders();

    ContentWriter w    = conn.getContentWriter();
    String        ext  = getExtension(path);
    String        text = Platform.getResourceAsString("bv.text.cant_load_document");

    if ("html".equals(ext)) {
      headers.mimeHtml();
      w.write(textToHTML(text));
    } else if ("txt".equals(ext)) {
      headers.mimeText();
      w.write(text);
    }
  }

  /**
   * Get the query parameters represented by the specified input stream
   * @param stream the stream containing the parameters
   * @return a man containing the query parameters parsed and decoded
   */
  public Map<String, String> getQueryParams(InputStream stream) throws IOException {
    Map<String, String> params = new HashMap<String, String>();
    Reader              r      = new InputStreamReader(stream);
    CharArray           s      = new CharArray();
    int                 c;

    while((c = r.read()) != -1) {
      if (c == '&') {
        parseParam(params, s);
        s._length = 0;
      } else {
        s.append((char) c);
      }
    }

    parseParam(params, s);
    stream.close();

    return params;
  }

  /**
   * Get the query parameters for the specified URL
   * @param url the URL
   * @return a man containing the query parameters parsed and decoded
   */
  @SuppressWarnings("resource")
  public Map<String, String> getQueryParams(URL url) {
    Map<String, String> params = new HashMap<String, String>();
    String              s      = url.getQuery();

    if ((s == null) || (s.length() == 0)) {
      return null;
    }

    CharScanner sc = new CharScanner(s);

    while((s = sc.nextToken('&')) != null) {
      int n = s.indexOf('=');

      if (n != -1) {
        if ((s.indexOf('+') == -1) && (s.indexOf('%') == -1)) {
          params.put(s.substring(0, n), s.substring(n + 1));
        } else {
          params.put(s.substring(0, n), URLEncoder.decode(s.substring(n + 1)));
        }
      }
    }

    return params;
  }

  /**
   * Get the query parameters for the specified URL for the URL itself
   * or the specified input stream if one is specified.
   * @param url the URL
   * @param stream the stream containing the parameters (can be null)
   * @return a man containing the query parameters parsed and decoded
   */
  public Map<String, String> getQueryParams(URL url, InputStream stream) throws IOException {
    if (stream == null) {
      return getQueryParams(url);
    }

    return getQueryParams(stream);
  }

  /**
   * Parses and decodes a parameter stored in the specified character array
   * as a name=value pair
   *
   * @param params the map to use to store the parameter
   * @param param the parameter to parse
   */
  protected void parseParam(Map params, CharArray param) {
    int n = param.indexOf('=');

    if (n != -1) {
      if ((param.indexOf('+') == -1) && (param.indexOf('%') == -1)) {
        params.put(param.substring(0, n), param.substring(n + 1));
      } else {
        params.put(param.substring(0, n), URLEncoder.decode(param.substring(n + 1)));
      }
    }

    param._length = 0;
  }

  /**
   * Writes the specified list the the specified writer using the specified separator
   * @param w the writer
   * @param list the list
   * @param sep the separator
   * @throws IOException
   */
  public static void toString(Writer w, List list, String sep) throws IOException {
    int len = list.size() - 1;

    for (int i = 0; i < len; i++) {
      w.write((String) list.get(i));
      w.write(sep);
    }

    w.write((String) list.get(len));
  }
}
