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

import com.appnativa.util.CharScanner;
import com.appnativa.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * The class manages a path thorough the application. It is meant to be used to
 * programmatically move through the application. When fully implemented all
 * primary screens in the application will have a unique path and will be able
 * to be navigated to, programmatically via, its path.
 *
 * @author Don DeCoteau
 *
 */
public class ActionPath extends ArrayList<String> {
  protected Object linkedData;

  /**
   * Creates a new empty path.
   */
  public ActionPath() {
    super(5);
  }

  /**
   * Creates a new path with the specified segments.
   *
   * @param segments the segment to add
   */
  public ActionPath(List<String> segments) {
    super(5);

    if (segments != null) {
      addAll(segments);
    }
  }

  /**
   * Creates a new path with the specified segments.
   *
   * If a single segment is added then that segment
   * is parsed looking for segments separated by a forward slash, otherwise
   * the segments are added as is.
   *
   * @param segments the segment to add
   */
  public ActionPath(String... segments) {
    super(5);

    if (segments != null) {
      addSegments(segments);
    }
  }

  /**
   * Add segments to a path. If a single segment is added then that segment
   * is parsed looking for segments separated by a forward slash, otherwise
   * the segments are added as is.
   *
   * @param segments the segment to add
   */
  public void addSegments(String... segments) {
    int len = (segments == null)
              ? 0
              : segments.length;

    if (len == 1) {
      if (segments[0].indexOf("/") != -1) {
        CharScanner.getTokens(segments[0], '/', false, this);
      } else {
        add(segments[0]);
      }
    } else {
      for (int i = 0; i < len; i++) {
        add(segments[i]);
      }
    }
  }

  @Override
  public Object clone() {
    return copy();
  }

  /**
   * Returns a path that is a copy of this path
   * @return  a path that is a copy of this path
   */
  public ActionPath copy() {
    ActionPath p = new ActionPath(this);

    return p;
  }

  /**
   * Gets any data associated with the path
   * @return any data associated with the path
   */
  public Object getLinkedData() {
    return linkedData;
  }

  /**
   * Sees the last segment in the path
   * @return  the last segment in the path or null
   */
  public String peek() {
    int len = size();

    return (len == 0)
           ? null
           : get(len - 1);
  }

  /**
   * Gets the last segment in the path
   * @return  the last segment in the path or null
   */
  public String pop() {
    int len = size();

    return (len == 0)
           ? null
           : remove(len - 1);
  }

  /**
   * Add a segment to the end of the path
   * @param segment the segment to add
   */
  public void push(String segment) {
    add(segment);
  }

  /**
   * Sets data associated with the path
   * @param linkedData the data
   */
  public void setLinkedData(Object linkedData) {
    this.linkedData = linkedData;
  }

  /**
   * Gets the first segment in the path
   * @return  the first segment in the path or null
   */
  public String shift() {
    return isEmpty()
           ? null
           : remove(0);
  }

  /**
   * Sees the first segment in the path
   * @return  the first segment in the path or null
   */
  public String shiftPeek() {
    int len = size();

    return (len == 0)
           ? null
           : get(0);
  }

  @Override
  public String toString() {
    return Helper.toString(this, "/");
  }

  /**
   * Add a segment to the beginning of the path
   * @param segment the segment to add
   */
  public void unshift(String segment) {
    add(0, segment);
  }

  /**
   * Parses the specified string an creates and action path
   * @param path the string representing the path
   * @return the new path
   */
  public static ActionPath fromString(String path) {
    ActionPath p = new ActionPath();

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
     * Gets the a path for the information currently displayed by the supporter.
     *
     * @return a path for the information currently displayed information
     */
    ActionPath getDisplayedActionPath();

    /**
     * Called on an active supporter to handle the specified path.
     *
     * @param path
     *          a path that is relative to the supporter
     */
    void handleActionPath(ActionPath path);
  }
}
