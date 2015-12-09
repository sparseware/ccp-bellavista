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

import java.net.URL;

/**
 * This interface represents an interface to an HTTP based connection
 * that a service implementation can use to get information about a client
 * request and send back results to the client
 *
 * @author Don DeCoteau
 *
 */
public interface iHttpConnection {

  /**
   * Gets a request header for the connection
   * @param name the name of the header
   * @return the headers value
   */
  String getResquestHeader(String name);

  /**
   * Gets the request method for the connection
   * @return the request method for the connection
   */
  String getRequestMethod();

  /**
   * Gets the URL for the connection
   * @return  the URL for the connection
   */
  URL getURL();

  /**
   * Gets the content writer that can be used to
   * write the connections output
   * @return
   */
  ContentWriter getContentWriter();
}
