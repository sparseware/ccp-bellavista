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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.appnativa.rare.iConstants;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.util.HTTPDateUtils;
import com.appnativa.util.StringCache;

/**
 * A class that holds the HTTP headers that a service will return to the client.
 *
 * @author Don DeCoteau
 *
 */
public class HttpHeaders {
  protected ArrayList<String>             keys;
  protected LinkedHashMap<String, String> headers;
  protected String                        status;

  /**
   * Get the header at the specified position.
   * Position zero (0) represents the HTTP status
   * @param index the index
   * @return the header at the specified index or null
   */
  public String getHeader(int index) {
    if (index == 0) {
      return status;
    }

    String key = getKey(index);

    if (key == null) {
      return null;
    }

    return headers.get(key);
  }

  /**
   * Gets the header for the specified key
   * @param key the key
   * @return the header value or null
   */
  public String getHeader(String key) {
    if ((headers == null) || (key == null)) {
      return null;
    }

    return headers.get(key.toLowerCase(Locale.US));
  }

  /**
   * Clears all headers allow the object to be reused
   */
  public void clear() {
    if (headers != null) {
      headers.clear();
    }

    keys = null;
  }

  /**
   * Adds a header value
   * @param key the key (automatically converted to lower case when stored)
   * @param value the value
   */
  public void add(String key, String value) {
    if (headers == null) {
      headers = new LinkedHashMap<String, String>(5);
    }

    headers.put(key.toLowerCase(Locale.US), value);
  }

  /**
   * Sets the HTTP response status
   * @param status the status
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Sets the default response headers (status, server, and date)
   */
  public void setDefaultResponseHeaders() {
    this.status = "HTTP/1.1 200 Ok";
    add("server", "FHIR Proxy");
    setDate(new Date());
  }

  /**
   * Sets the date response header
   * @param date the date
   */
  public void setDate(Date date) {
    setDate(HTTPDateUtils.formatDate(date));
  }

  /**
   * Sets the content type response header
   * @param type the type
   */
  public void setContentType(String type) {
    add("content-type", type);
  }

  /**
   * Sets the date response header
   * @param date the date
   */
  public void setDate(String date) {
    add("date", date);
  }

  /**
   * Sets the content type to {@code text/plain}
   */
  public void mimeText() {
    add("content-type", iConstants.TEXT_MIME_TYPE);
  }

  /**
   * Sets the content type to {@code application/json}
   */
  public void mimeJson() {
    add("content-type", iConstants.JSON_MIME_TYPE);
  }

  /**
   * Sets the content type to {@code text/html}
   */
  public void mimeHtml() {
    add("content-type", iConstants.HTML_MIME_TYPE);
  }

  /**
   * Sets the content type 
   *@param type the type
   */
  public void mime(String type) {
    add("content-type", type);
  }

  /**
   * Sets the content type to {@code text/html}
   */
  public void mimeMultipart(String boundary) {
    add("content-type",  "multipart/mixed; boundary=" + boundary);
  }

  /**
   * Sets the content type to {@code text/plain;ldseparator=|}
   */
  public void mimeList() {
    add("content-type", "text/plain;ldseparator=|");
  }

  /**
   * Sets the content type to {@code text/plain;separator=^;ldseparator=|}
   */
  public void mimeRow() {
    add("content-type", "text/plain;separator=^;ldseparator=|;riseparator=~");
  }

  /**
   * Sets the has more header (RARE specific)  to the specified value
   * @param next the url to retrieve the next page of data
   */
  public void hasMore(String next) {
    add(ActionLink.PAGING_HAS_MORE, "true");
    add(ActionLink.PAGING_NEXT, next);
  }

  /**
   * Sets the link info header (RARE specific)  to the specified value
   * @param int the information about the link
   */
  public void setLinkInfo(String info) {
    add(ActionLink.LINK_INFO, info);
  }

  public void setContentLength(long length) {
    add("content-length", StringCache.valueOf(length));
  }

  /**
   * Get the key at the specified position.
   * @param index the index
   * @return the key at the specified index or null
   */
  public String getKey(int index) {
    if (index == 0) {
      return "HTTP_STATUS_CODE";
    }

    index--;

    if (headers == null) {
      return null;
    }

    if (keys == null) {
      keys = new ArrayList<String>(headers.size());
      keys.addAll(headers.keySet());
    }

    return keys.get(index);
  }
}
