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
package com.sparseware.bellavista;

import java.util.ArrayList;
import java.util.List;

import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.CharScanner;
import com.appnativa.util.Helper;

/**
 * The class has a path thorough the application. It is meant to be use to
 * programmatically move through through application. When fully implemented all
 * primary screens in the application will have a unique path and will be able
 * to be navigated to programmatically via its action path
 * 
 * @author Don DeCoteau
 *
 */
public class ActionPath extends ArrayList<String> {
  protected Object linkedData;

  public ActionPath() {
  }

  public ActionPath(int pathCount) {
    super(pathCount);
  }

  public ActionPath(List<String> path) {
    if (path != null) {
      addAll(path);
    }
  }

  public ActionPath(String... path) {
    if (path != null) {
      addPath(path);
    }
  }

  public void addPath(String... path) {
    int len = path == null ? 0 : path.length;
    if (len == 1) {
      int n = Functions.length(path[0], "/");
      if (n > 1) {
        CharScanner.getTokens(path[0], '/', false, this);
      } else {
        add(path[0]);
      }
    } else {
      for (int i = 0; i < len; i++) {
        add(path[i]);
      }
    }
  }

  public ActionPath copy() {
    ActionPath p = new ActionPath(this);
    return p;
  }

  public String peek() {
    int len = size();
    return len == 0 ? null : get(len - 1);
  }

  public String shiftPeek() {
    int len = size();
    return len == 0 ? null : get(0);
  }

  public String pop() {
    int len = size();
    return len == 0 ? null : remove(len - 1);
  }

  public void push(String path) {
    add(path);
  }

  public String see() {
    int len = size();
    return len == 0 ? null : get(0);
  }

  public String shift() {
    return isEmpty() ? null : remove(0);
  }

  @Override
  public String toString() {
    return Helper.toString(this, "/");
  }

  public void unshift(String path) {
    add(0, path);
  }

  Object getLinkedData() {
    return linkedData;
  }

  void setLinkedData(Object linkedData) {
    this.linkedData = linkedData;
  }

  public static ActionPath fromString(String path) {
    int len = Functions.length(path, "/");
    ActionPath p = new ActionPath(len);
    CharScanner.getTokens(path, '/', false, p);
    return p;
  }

  /**
   * An interface for UI handlers that support action paths
   * 
   * @author Don DeCoteau
   *
   */
  interface iActionPathSupporter {
    /**
     * Gets the a path for the information currently displayed by the handler.
     * 
     * @return a path for the information currently displayed information
     */
    ActionPath getDisplayedActionPath();

    /**
     * Called on an active handler to handle the specified path.
     * 
     * @param path
     *          a path that is relative to the handler
     */
    void handleActionPath(ActionPath path);
  }
}
