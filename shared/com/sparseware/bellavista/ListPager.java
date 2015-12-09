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

import com.appnativa.rare.net.ActionLink;
import com.appnativa.util.CharScanner;

/**
 * This provides a simple manager for managing paging
 * via links.
 *
 * @author Don DeCoteau
 *
 */
public class ListPager {
  protected ArrayList<ActionLink> stack;
  protected ActionLink            next;

  /**
   * Creates a new instance
   */
  public ListPager() {}

  /**
   * Gets the link to use to get the next page of data
   * @return the link to use to get the next page of data
   */
  public ActionLink next() {
    ActionLink link = next;

    next = null;

    return link;
  }

  /**
   * Gets the link to use to get the previous page of data
   * @return the link to use to get the previous page of data
   */
  public ActionLink previous() {
    ActionLink link = null;

    if (stack != null) {
      int pos = stack.size() - 2;

      link = (pos > -1)
             ? stack.get(pos)
             : null;

      if (link != null) {
        stack.remove(pos);
        stack.remove(pos);
      }
    }

    next = null;

    return link;
  }

  /**
   * Creates a paging link
   * @param source the source link that generated the paging reference
   * @param href the paging reference (can be absolute or relative)
   * @return
   */
  public static ActionLink createPagingLink(ActionLink source, String href) {
    ActionLink l = (ActionLink) source.clone();

    if (href.indexOf(':') == -1) {
      l.getAttributes().putAll(CharScanner.parseOptionString(href, (href.indexOf('^') != -1)
              ? '^'
              : '&'));
    } else {
      l.getAttributes().clear();

      int n = href.indexOf('?');

      if (n != -1) {
        l.getAttributes().putAll(CharScanner.parseOptionString(href.substring(n + 1), '&'));
        href = href.substring(0, n);
      }

      l.getAttributes().put("href", href);
    }

    return l;
  }

  /**
   * Sets the link to use for the next page
   * @param next the link to use for the next page
   */
  public void setNext(ActionLink next) {
    this.next = next;
  }

  /**
   * Clears the pager
   */
  public void clear() {
    next = null;

    if (stack != null) {
      stack.clear();
    }
  }

  /**
   * Adds a link to the stack. This link will be treated as the current link.
   * Call to previous will return the link before this one on the stack.
   *
   * @param link a link to add to the stack
   */
  public void add(ActionLink link) {
    getStack().add(link);
  }

  /**
   * Gets whether or not there is a next page of data
   * @return true if there is; false otherwise
   */
  public boolean hasNext() {
    return next != null;
  }

  /**
   * Gets whether or not there is a previous page of data
   * @return true if there is; false otherwise
   */
  public boolean hasPrevious() {
    return (stack == null)
           ? false
           : stack.size() > 1;
  }

  /**
   * Gets the stack
   * @return the stack
   */
  protected ArrayList<ActionLink> getStack() {
    if (stack == null) {
      stack = new ArrayList<ActionLink>(5);
    }

    return stack;
  }
}
