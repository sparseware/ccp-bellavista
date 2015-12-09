/**
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import com.appnativa.rare.net.JavaURLConnection;
import com.appnativa.rare.net.iConnectionHandler;
import com.appnativa.rare.net.iURLConnection;
import com.appnativa.util.io.BufferedReaderEx;

/**
 * This class in a RARE connection handler that handles creating URL connections
 * for custom URL protocols
 * 
 * @author Don DeCoteau
 */
public class ConnectionHandler implements iConnectionHandler {
  String[] protocols;

  /**
   * Creates a new instance
   *
   * @param protocols the protocols that are to be supported
   */
  public ConnectionHandler(String[] protocols) {
    this.protocols = protocols;
  }

  @Override
  public iURLConnection openConnection(URL url, String mimeType) throws IOException {
    String protocol = url.getProtocol();

    for (String s : protocols) {
      if (s.equals(protocol)) {
        return new JavaURLConnectionEx(url.openConnection(), null, mimeType);
      }
    }

    return null;
  }

  /**
   * Extension that handles retrieving the reader for connections that are instances of {@link aHttpURLConnection}
   *
   * @author Don DeCoteau
   */
  static class JavaURLConnectionEx extends JavaURLConnection {
    public JavaURLConnectionEx(URLConnection conn) {
      super(conn);
    }

    public JavaURLConnectionEx(URLConnection conn, String userInfo) {
      super(conn, userInfo);
    }

    public JavaURLConnectionEx(URLConnection conn, String userInfo, String mimeType) {
      super(conn, userInfo, mimeType);
    }

    @Override
    public Reader getReader() throws IOException {
      connectAndCheckForError();

      if (aConnection instanceof aHttpURLConnection) {
        return ((aHttpURLConnection) aConnection).getContentReader();
      }

      final int n = getContentLength();

      if ((n > 0) && (n < 8192)) {
        return new BufferedReaderEx(new InputStreamReader(aConnection.getInputStream(), getCharset()), n);
      }

      return new BufferedReaderEx(new InputStreamReader(aConnection.getInputStream(), getCharset()), (n > 0)
              ? 8192
              : 4096);
    }
  }
}
