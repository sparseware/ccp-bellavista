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

import com.appnativa.rare.Platform;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.iURLConnection;
import com.appnativa.rare.util.MIMEMap;
import com.appnativa.util.ByteArray;
import com.appnativa.util.CharArray;
import com.appnativa.util.HTTPDateUtils;
import com.appnativa.util.ISO88591Helper;
import com.appnativa.util.Streams;
import com.appnativa.util.UTF8Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * A HTTP Proxy server that can be used to capture external browser request
 *
 * @author Don DeCoteau
 */
public class ProxyServer implements Runnable {
  int                                     port;
  iFunctionCallback                       unmappedURLHandler;
  int                                     bufferLength  = 8192;
  int                                     clientLinger  = 500;
  int                                     clientTimeout = 60 * 1000;         // one minute
  double                                  purgeRatio    = 0.25;              // purge 1/4 of the entries
  int                                     queueMax      = 100;
  int                                     urlTimeout    = 60 * 1000 * 10;    // 10 minutes
  ReentrantLock                           purgeLock;
  ConcurrentHashMap<String, RewrittenURL> rewrittenURLs;
  int                                     serverPort;
  volatile boolean                        serverRunning;
  ServerSocket                            serverSocket;
  String                                  serverURL;

  /**
   * Creates a new instance
   */
  public ProxyServer() {}

  /**
   * Clears the uRL rewrite cache
   */
  public void clearCache() {
    if (rewrittenURLs != null) {
      rewrittenURLs.clear();
    }
  }

  /**
   * Disposes to the server
   */
  public void dispose() {
    stopServer();

    if (rewrittenURLs != null) {
      rewrittenURLs.clear();
    }

    rewrittenURLs = null;
  }

  /**
   * Rewrites a URL an return an HREF that can be passed to an external browser
   * @param url the URL to be rewritten
   * @param cb the callback to invoke when the external browser requests the URL
   *
   * @return  the HREF to  pass to the external browser
   */
  public String rewriteURL(URL url, iFunctionCallback cb) {
    RewrittenURL rurl = rewriteURLEx(url);

    rurl.callback = cb;

    return serverURL + rurl.theDocument;
  }

  @Override
  public void run() {
    ExecutorService tp = Executors.newFixedThreadPool(2);

    try {
      ServerSocket server = null;

      try {
        server = new ServerSocket(port);
      } catch(IOException e) {
        if (port != 0) {
          server = new ServerSocket(0);
        }
      }
      if(server==null) {
        String s="Could not start server on port:"+port;
        Platform.debugLog(s);
        throw new ApplicationException(s);
      }

      serverPort   = server.getLocalPort();
      serverURL    = "http://localhost:" + serverPort;
      serverSocket = server;

      synchronized(this) {
        serverRunning = true;
        this.notifyAll();
      }

      while(true) {
        Socket client = server.accept();

        if (!client.getInetAddress().isLoopbackAddress() &&!client.getInetAddress().isAnyLocalAddress()) {
          try {
            client.close();
          } catch(Throwable ignored) {}
        } else {
          tp.submit(new Runner(client));
        }
      }
    } catch(IOException ignore) {}
    finally {
      serverRunning = false;
      tp.shutdownNow();
    }
  }

  /**
   * Writes a complete HTTP response the the specified stream
   * using the data from the specified connection
   * @param stream the stream to write the response to
   * @param ic the connection to use as the content
   *
   * @throws IOException
   */
  public static void serve(OutputStream stream, iURLConnection ic) throws IOException {
    String s = ic.getContentType();

    if ((s == null) || (s.length() == 0)) {
      s = MIMEMap.typeFromFile(ic.getURL().getFile());
    }

    writeHTTPResponse(stream, ic.getInputStream(), s);
  }

  /**
   * Writes a complete HTTP response the the specified stream
   * using the data from the specified url
   * @param stream the stream to write the response to
   * @param url the url to use as the content
   * @param type the content type for the HTTP response
   *
   * @throws IOException
   */
  public static void serve(OutputStream stream, URL url, String type) throws IOException {
    serve(stream, Platform.openConnection(null, url, type));
  }

  /**
   * Starts the server.
   *
   *  The server will be started when this method returns
   */
  public void startServer() {
    synchronized(this) {
      if (!serverRunning) {
        if (rewrittenURLs == null) {
          rewrittenURLs = new ConcurrentHashMap<String, RewrittenURL>();
          purgeLock     = new ReentrantLock();
        }

        Thread t = new Thread(this, "ProxyServer");

        t.setDaemon(true);
        t.start();

        try {
          wait();
        } catch(InterruptedException ex) {
          throw new ApplicationException(ex);
        }
      }
    }
  }

  /**
   * Stops the server
   */
  public void stopServer() {
    ServerSocket server = serverSocket;

    if (server != null) {
      try {
        server.close();
      } catch(Throwable ignored) {}

      serverSocket = null;
    }

    serverRunning = false;

    synchronized(this) {
      notifyAll();
    }
  }

  /**
   * Writes a complete HTTP response the the specified stream
   * @param stream the stream to write to
   * @param content the content for the response
   * @param contentType the content type of the response (null for text/plain)
   *
   * @throws IOException
   */
  public static void writeHTTPResponse(OutputStream stream, InputStream content, String contentType) throws IOException {
    StringWriter w = new StringWriter();

    if (contentType == null) {
      contentType = iConstants.TEXT_MIME_TYPE;
    }

    ByteArray ba = null;

    if (content instanceof ByteArray) {
      ba = (ByteArray) content;
    } else if (content != null) {
      ba = new ByteArray(8192);
      Streams.streamToBytes(content, ba);
    }

    int len = (ba == null)
              ? 0
              : ba._length;

    w.write("HTTP/1.0 200 OK");
    w.write('\n');
    w.write(HTTPDateUtils.formatDate(new Date()));
    w.write('\n');
    w.write("Server: RARE Proxy Server");
    w.write('\n');
    w.write("Content-Type: " + contentType);
    w.write('\n');
    w.write("Content-Length: " + len);
    w.write('\n');
    w.write('\n');

    if (ba != null) {
      stream.write(ba.A, 0, ba._length);
    }
  }
 /**
  * Writes a header portion of an HTTP response the the specified stream
  * 
  * @param stream
  * @param code the HTTP status code
  * @param status the HTTP status message
  * @param contentType the content type (null for text/plain)
  * @param contentLength the content length (zero for not content)
  * @throws IOException 
  */
  public static void startHTTPResponse(OutputStream stream, String code,String status,String contentType, long contentLength) throws IOException {
    StringWriter w = new StringWriter();

    if (contentType == null) {
      contentType = iConstants.TEXT_MIME_TYPE;
    }

    w.write("HTTP/1.0 ");
    w.write(code);
    w.write(" ");
    w.write(status);
    w.write('\n');
    w.write(HTTPDateUtils.formatDate(new Date()));
    w.write('\n');
    w.write("Server: RARE Proxy Server");
    w.write('\n');
    if(contentLength>0) {
      w.write("Content-Type: " + contentType);
      w.write('\n');
      w.write("Content-Length: " + contentLength);
    }
    w.write('\n');
    w.write('\n');
  }

  /**
   * Writes a complete HTTP response the the specified writer
   * @param w the writer to write to
   * @param content the content for the response
   * @param contentType the content type of the response (null for text/plain)
   *
   * @throws IOException
   */
  public static void writeHTTPResponse(Writer w, String content, String contentType) throws IOException {
    if (contentType == null) {
      contentType = iConstants.TEXT_MIME_TYPE;
    }

    w.write("HTTP/1.0 200 OK");
    w.write('\n');
    w.write(HTTPDateUtils.formatDate(new Date()));
    w.write('\n');
    w.write("Server: RARE Proxy Server");
    w.write('\n');
    w.write("Content-Type: " + contentType);
    w.write('\n');
    w.write("Content-Length: " + content.length());
    w.write('\n');
    w.write('\n');
    w.write(content);
  }

  /**
   * Sets the port for the server to bind to
   * @param port the port for the server to bind to
   */
  public void setBindPort(int port) {
    if ((port < 1000) && (port != 0)) {
      throw new IllegalArgumentException("port < 1000");
    }

    this.port = port;

    if (port != 0) {
      serverPort = port;
    }
  }

  /**
   * Sets a request header for a specific connection
   * @param conn the connection
   * @param key the header key
   * 
   * @param list the values for the header
   */
  public static void setRequestHeader(HttpURLConnection conn, String key, List<String> list) {
    int len = list.size();

    if (len > 0) {
      conn.setRequestProperty(key, list.get(0));
    }

    for (int i = 1; i < len; i++) {
      conn.addRequestProperty(key, list.get(i));
    }
  }
/**
 * Sets a set of request headers
 * @param conn the connection
 * @param headers the seaders to set
 */
  public static void setRequestHeaders(HttpURLConnection conn, Map<String, List<String>> headers) {
    Entry<String, List<String>>           e;
    Iterator<Entry<String, List<String>>> it = headers.entrySet().iterator();

    while(it.hasNext()) {
      e = it.next();
      setRequestHeader(conn, e.getKey(), e.getValue());
    }
  }

  /**
   *
   * @param cb the callback for urls requests that are note specifically mapped
   */
  public void setUnmappedURLHandler(iFunctionCallback cb) {
    unmappedURLHandler = cb;
  }

  public int getServerPort() {
    return serverPort;
  }

  /**
   *  {@inheritDoc}
   *
   *  @return {@inheritDoc}
   */
  public boolean isServerRunning() {
    return serverRunning;
  }

  int readLine(Reader r, CharArray ca) throws IOException {
    ca._length = 0;

    int c = -1;

    while((c = r.read()) != -1) {
      ca.append((char) c);

      if (c == 10) {
        break;
      }
    }

    return c;
  }

  protected void handle(Object handle, Socket client, String method, String url, Reader request,
                        Map<String, List<String>> reqheaders)
          throws IOException {
    do {
      String doc = url;
      int    n   = doc.indexOf('#');

      if (n == -1) {
        n = doc.indexOf('?');
      }

      if (n != -1) {
        doc = doc.substring(0, n);
      }

      String s        = doc;
      int    childPos = -1;

      n = s.indexOf('/');

      if (n != -1) {
        n = s.indexOf('/', n + 1);
      }

      if (n != -1) {
        s        = s.substring(0, n + 1);
        childPos = n + 1;
      }

      RewrittenURL rurl = (rewrittenURLs == null)
                          ? null
                          : rewrittenURLs.get(s);

      if ((rurl == null) || (rurl.theTime + urlTimeout) < System.currentTimeMillis()) {
        if (rurl != null) {
          rewrittenURLs.remove(doc);

          break;
        } else if (unmappedURLHandler != null) {
          rurl          = new RewrittenURL(null);
          rurl.callback = unmappedURLHandler;
        } else {
          break;
        }
      }

      Request r = new Request();

      r.requestHeaders = reqheaders;
      r.requestMethod  = method;
      r.originalUrl    = rurl.theURL;
      r.requestUrl     = url;
      r.content        = request;
      r.client         = client;
      rurl.callback.finished(false, r);

      break;
    } while(false);
  }

  protected void purge() {
    RewrittenURL a[] = new RewrittenURL[rewrittenURLs.size()];

    a = rewrittenURLs.values().toArray(a);

    Comparator comp = new Comparator() {
      @Override
      public int compare(Object o1, Object o2) {
        return (int) (((RewrittenURL) o2).theTime - ((RewrittenURL) o1).theTime);    // oldest at the bottom
      }
    };

    Arrays.sort(a, comp);

    int len = a.length;

    len -= (purgeRatio * len);
    rewrittenURLs.clear();

    for (int i = 0; i < len; i++) {
      rewrittenURLs.put(a[i].theDocument, a[i]);
    }
  }

  protected RewrittenURL rewriteURLEx(URL url) {
    if (!isServerRunning()) {
      startServer();
    }

    RewrittenURL rurl = new RewrittenURL(url);

    try {
      if (rewrittenURLs.size() > queueMax) {    // dont let queue overflow
        purgeLock.lockInterruptibly();
        purge();
        purgeLock.unlock();
      }

      rewrittenURLs.put(rurl.theDocument, rurl);
    } catch(InterruptedException ex) {
      return null;
    }

    return rurl;
  }

  @SuppressWarnings("resource")
  protected void sendResponseHeaders(Object handle, int code, int responseLength) throws IOException {
    Runner    runner = (Runner) handle;
    CharArray sb     = new CharArray("HTTP/1.1 ");

    sb.append(code).append(' ');

    if (code == 200) {
      sb.append("OK\r\n");
    } else {
      sb.append("ERROR\r\n");
    }

    Entry<String, List<String>> e;

    if (runner.responseHeaders != null) {
      List<String>                          list;
      Iterator<Entry<String, List<String>>> it = runner.responseHeaders.entrySet().iterator();
      int                                   len;

      while(it.hasNext()) {
        e    = it.next();
        list = e.getValue();
        len  = list.size();

        if (len > 0) {
          sb.append(e.getKey()).append(": ").append(list.get(0));
        }

        for (int i = 1; i < len; i++) {
          sb.append(";").append(list.get(i));
        }

        sb.append("\r\n");
      }
    }

    sb.append("\r\n");

    ISO88591Helper csh = ISO88591Helper.getInstance();

    runner.client.getOutputStream().write(csh.getBytes(sb.A, 0, sb._length));
  }

  protected Map<String, List<String>> getResponseHeaders(Object handle) {
    Runner runner = (Runner) handle;

    if (runner.responseHeaders == null) {
      runner.responseHeaders = new HashMap<String, List<String>>();
    }

    return runner.responseHeaders;
  }

  /**
   * Object that is passed to calback that is responsible for handling
   * a URL request
   */
  public static class Request {

    /** the request headers */
    public Map<String, List<String>> requestHeaders;

    /** the request method */
    public String requestMethod;

    /** the original URL that was re-written */
    public URL originalUrl;

    /** the URL that was requested by the browser */
    public String requestUrl;

    /** the reader to use the read the content of the request */
    public Reader content;

    /** the socket for the request */
    public Socket client;

    /**
     * Acknowledges the request by sending an empty OK response to the browser
     */
    public void acknowledge() {
      try {
        writeHTTPResponse(client.getOutputStream(), null, null);
      } catch(Exception e) {
        Platform.ignoreException(null, e);
      }
    }

    /**
     * Returns a not-found status to the client
     */
    public void notFound() {
      try {
        startHTTPResponse(client.getOutputStream(), "404", "Not Found", null, 0);
      } catch(Exception e) {
        Platform.ignoreException(null, e);
      }
    }

    /**
     * Returns a forbidden status to the client
     */
    public void forbidden() {
      try {
        startHTTPResponse(client.getOutputStream(), "403", "Forbidden", null, 0);
      } catch(Exception e) {
        Platform.ignoreException(null, e);
      }
    }
    
    /**
     * Acknowledges the request by sending an empty OK response
     * with the specified content to the browser
     * 
     * @param content the content to send
     * @param contentType the content type of the response (null for text/plain)
     */
    public void acknowledge(String content, String contentType) {
      try {
        ByteArray ba = new ByteArray();

        ba.setCharsetHelper(UTF8Helper.getInstance());
        ba.add(content);
        writeHTTPResponse(client.getOutputStream(), ba, null);
      } catch(Exception e) {
        Platform.ignoreException(null, e);
      }
    }

    /**
     * Acknowledges the request by sending an empty OK response
     * with the specified content to the browser
     * 
     * @param content the content to send
     * @param contentType the content type of the response (null for text/plain)
     */
    public void acknowledge(InputStream content, String contentType) {
      try {
        writeHTTPResponse(client.getOutputStream(), content, contentType);
      } catch(Exception e) {
        Platform.ignoreException(null, e);
      }
    }
  }


  protected static class RewrittenURL {
    iFunctionCallback callback;
    String            theDocument;
    long              theTime;
    URL               theURL;

    RewrittenURL(URL url) {
      theURL  = url;
      theTime = System.currentTimeMillis();

      StringBuilder sb = new StringBuilder("/");

      sb.append(Long.toHexString(this.hashCode()));
      sb.append("RARE").append(Long.toHexString(theTime)).append("/");
      theDocument = sb.toString();
    }
  }


  class Runner implements Runnable {
    Socket                    client;
    String                    document;
    String                    method;
    Map<String, List<String>> requestHeaders;
    Map<String, List<String>> responseHeaders;

    Runner(Socket s) {
      client = s;
    }

    @Override
    public void run() {
      try {
        client.setSoTimeout(clientTimeout);
        client.setSoLinger(true, clientLinger);

        Reader         r  = new InputStreamReader(client.getInputStream());
        BufferedReader br = new BufferedReader(r);

        parse(br);

        if (document == null) {
          sendResponseHeaders(this, HttpURLConnection.HTTP_BAD_REQUEST, -1);
        } else {
          handle(this, client, method, document, br, requestHeaders);
        }
      } catch(IOException e) {
        e.printStackTrace();
      } finally {
        try {
          client.shutdownOutput();
          client.shutdownInput();
          client.close();
        } catch(Exception ignored) {}
      }
    }

    void parse(BufferedReader r) throws IOException {
      do {
        CharArray line = new CharArray(32);

        readLine(r, line);

        if (line._length == 0) {
          break;
        }

        line = line.trim();

        int n = line.indexOf(' ');

        if (n == -1) {
          break;
        }

        method = line.substring(0, n);

        int p = line.indexOf(' ', n + 1);

        if (p == -1) {
          document = line.substring(n + 1);
        } else {
          document = line.substring(n + 1, p);
        }

        while(readLine(r, line) == 10) {
          line = line.trim();

          if (line._length == 0) {
            break;
          }

          n = line.indexOf(':');

          if (n != -1) {
            if (requestHeaders == null) {
              requestHeaders = new HashMap<String, List<String>>();
            }

            requestHeaders.put(line.substring(0, n).trim(), Arrays.asList(line.substring(n + 1).trim()));
          }
        }
      } while(false);

      document = document.trim();
      System.out.println(document);
    }
  }
}
