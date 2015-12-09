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

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iWeakReference;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.net.HTTPException;
import com.appnativa.util.CharArray;
import com.appnativa.util.HTTPDateUtils;
import com.appnativa.util.URLEncoder;
import com.google.j2objc.annotations.Weak;
import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.MessageException;

/**
 * This is the base class for services available via a standard HTTP Connection.
 * It provides a connection that can be passed back to the client for the client
 * to retrieve data in the expected format.
 *
 * @author Don DeCoteau
 *
 */
public abstract class aHttpURLConnection extends HttpURLConnection implements iHttpConnection {
  protected FileBackedBufferedOutputStream         outStream;
  protected InputStream                            inStream;
  protected HttpHeaders                            headers        = new HttpHeaders();
  static ConcurrentHashMap<String, iWeakReference> handlerMap     = new ConcurrentHashMap<String, iWeakReference>();
  static Class                                     methodParams[] = new Class[] { iHttpConnection.class,
          ActionPath.class, InputStream.class, HttpHeaders.class };
  protected String             clsPackage;
  protected Method             serviceMethod;
  protected Object             serviceObject;
  protected String             HUB_SUBSTRING = "/hub/main/";
  protected ActionPath         servicePath;
  protected ContentWriter      contentWriter;
  protected InputStream        errStream;
  protected static InputStream nullStream = new InputStream() {
    @Override
    public int read() throws IOException {
      return -1;
    }
    public long skip(long n) throws IOException {
      return 0;
    }
  };

  /**
   * Creates a new connection
   *
   * @param u the url for the connection
   * @param clsPackage package for classes the represent the service the connection represents
   */
  public aHttpURLConnection(URL u, String clsPackage) {
    super(u);
    this.clsPackage = clsPackage;
  }

  @Override
  public void connect() throws IOException {
    if (serviceMethod == null) {
      try {
        connectToService();
      } catch(Exception e) {
        throw new IOException(e);
      }
    }

    connected = serviceMethod != null;
  }

  @Override
  public ContentWriter getContentWriter() {
    if (contentWriter == null) {
      contentWriter = new ContentWriter();
    }

    return contentWriter;
  }

  @Override
  public void disconnect() {
    closeStream(inStream);
    closeStream(outStream);
    closeStream(errStream);
    inStream  = null;
    outStream = null;
    headers.clear();
    connected = false;
  }

  @Override
  public String getHeaderField(int n) {
    try {
      getInputStream();
    } catch(IOException ignore) {}

    return headers.getHeader(n);
  }

  @Override
  public String getHeaderField(String name) {
    try {
      getInputStream();
    } catch(IOException ignore) {}

    return headers.getHeader(name);
  }

  @Override
  public String getHeaderFieldKey(int n) {
    try {
      getInputStream();
    } catch(IOException ignore) {}

    return headers.getKey(n);
  }

  @Override
  public InputStream getErrorStream() {
    return errStream;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    if (!doInput) {
      return super.getInputStream();
    }

    if (inStream == null) {
      try {
        if (serviceMethod == null) {
          connectToService();
        }

        serviceMethod.invoke(serviceObject, new Object[] { this, servicePath, (outStream == null)
                ? null
                : outStream.getInputStream(), headers });
        headers.setContentLength(contentWriter.getContentLength());
        inStream = new ContentReaderInputStream(contentWriter);
      } catch(Exception e) {
        inStream = getExceptionInputStream(headers, e);
      }
    }

    return inStream;
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    if (!doOutput) {
      return super.getOutputStream();
    }

    if (outStream == null) {
      outStream = new FileBackedBufferedOutputStream();
    }

    return outStream;
  }

  @Override
  public String getResquestHeader(String name) {
    return getRequestProperty(name);
  }

  @Override
  public boolean usingProxy() {
    return false;
  }

  /**
   * Does the work of connecting to the remote service.
   * <p>
   * The default implementation  will create and instance of a class corresponding to the name of the resource to be accessed.
   * The name of the class name and method to be invoked is derived from the path segment following '/hub/main/' in the URL string.
   * </p>
   * <p>
   * For example a path of '/hub/main/patient/allergies' will cause a <b>Patient</b> class to be instantiated and
   * the <i>allergies</i> method on the class to be invoked.
   * <br/>
   * If a class is not found for the first segment, the next segment is checked and the first segment becomes part
   * of the class's package name.
   * For example a path of '/hub/main/util/patients/list' will cause a <b>util.Patients</b> class to be instantiated
   * and the <i>list</i> method on the class to be invoked.
   * <br/>
   * </p>
   * <p>
   * The default package name for classes is passed in when the connection is created and class instances are cached
   * so that there is ever only one instance of a class in memory.
   * </p>
   * The signature of the method invoked corresponds to the signature of {@link aRemoteService#demo(iHttpConnection, ActionPath, InputStream, HttpHeaders)}
   *
   * @throws Exception
   */
  @SuppressWarnings("resource")
  protected void connectToService() throws Exception {
    String s = URLEncoder.decode(getURL().getPath());
    int    n = s.indexOf(HUB_SUBSTRING);

    s = s.substring(n + HUB_SUBSTRING.length());

    ActionPath     p  = ActionPath.fromString(s);
    CharArray      ca = new CharArray(clsPackage);
    int            pos;
    Object         o = null;
    iWeakReference r;

    while((o == null) &&!p.isEmpty()) {
      ca.append('.');
      pos = ca.length();
      ca.append(p.shift());
      ca.A[pos] = Character.toUpperCase(ca.A[pos]);

      String cls = ca.toString();

      r = handlerMap.get(cls);
      o = (r == null)
          ? null
          : r.get();

      if (o == null) {
        try {
          Class clz = Platform.loadClass(cls);

          o = clz.newInstance();
          handlerMap.put(cls, Platform.createWeakReference(o));

          break;
        } catch(Exception ignore) {
          ca.A[pos] = Character.toLowerCase(ca.A[pos]);
        }
      }
    }

    String method = p.shift();

    if ((o == null) || (method == null)) {
      throw new FileNotFoundException(s);
    }

    serviceMethod = o.getClass().getMethod(method, methodParams);
    servicePath   = p;
    serviceObject = o;
  }

  /**
   * Silently closes a stream
   * @param stream the stream to close
   */
  protected void closeStream(Closeable stream) {
    try {
      if (stream != null) {
        stream.close();
      }
    } catch(Exception ignore) {}
  }

  /**
   * Gets the input stream to be returned when an errors accessing the remote service occurs.
   * The default behavior mimics the standard java HTTP connection in that it returns
   * an empty data stream an makes the error response available via the {@link #getErrorStream()}
   * method
   *
   * @param headers the response headers that will be sent back to the client
   * @param e the exception
   * @return the stream
   */
  protected InputStream getExceptionInputStream(HttpHeaders headers, Throwable e) {
    String msg    = null;
    long   date   = 0;
    String server = null;
    String status = null;
    String ct     = null;

    e = ApplicationException.pealException(e);

    if (e instanceof HTTPException) {
      HttpURLConnection conn = ((HTTPException) e).geConnection();

      if (conn != null) {
        try {
          status = conn.getHeaderField(0);
          server = conn.getHeaderField("server");
          date   = conn.getDate();
          ct     = conn.getContentType();
          msg    = ((HTTPException) e).getMessageBody();

          if (msg.length() == 0) {
            msg = null;
          }
        } catch(Exception e1) {
          Platform.ignoreException(null, e1);
        }
      }
    } else if (e instanceof MessageException) {
      status = "HTTP/1.1 303 See Other";
      msg    = ((MessageException) e).getMessage();
    } else {
      Platform.ignoreException(null, e);
    }

    if (status == null) {
      status = "HTTP/1.1 400 Bad Request";
    }

    if (ct != null) {
      ct = "Content-Type: text/plain";
    }

    if (server == null) {
      server = "BellaVista";
    }

    if (date == 0) {
      date = System.currentTimeMillis();
    }

    if (msg == null) {
      msg = ApplicationException.getMessageEx(e);
    }

    byte   b[] = msg.getBytes();
    String d   = HTTPDateUtils.formatDate(new Date(date));

    headers.setStatus(status);
    headers.setDate(d);
    headers.add("last-modified", d);
    headers.add("server", server);
    headers.setContentType(ct);
    headers.setContentLength(0);
    errStream = new ByteArrayInputStream(b);

    return nullStream;
  }

  /**
   * Gets the content reader for the content of the request from the client
   * @return the reader
   *
   * @throws IOException
   */
  public Reader getContentReader() throws IOException {
    if (contentWriter != null) {
      return contentWriter.getReader();
    }

    return new StringReader("");
  }

  /**
   * An input stream for reading content that was part of the client's request
   *
   * @author Don DeCoteau
   */
  static class ContentReaderInputStream extends InputStream {
    @Weak
    ContentWriter contentWriter;
    InputStream   stream;

    public ContentReaderInputStream(ContentWriter contentWriter) {
      super();
      this.contentWriter = contentWriter;
    }

    @Override
    public int read() throws IOException {
      if (contentWriter == null) {
        return -1;
      }

      if (stream == null) {
        stream = contentWriter.getInputStream();
      }

      return stream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      if (contentWriter == null) {
        return -1;
      }

      if (stream == null) {
        stream = contentWriter.getInputStream();
      }

      return stream.read(b, off, len);
    }
  }
}
