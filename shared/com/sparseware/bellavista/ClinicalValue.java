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

import com.appnativa.rare.Platform;

import java.util.Date;

/**
 * This class represents a clinical value
 * and is used when searching for and comparing certain significant values (e.g. BUN Creatine)
 * that may be used/referenced through out the application.
 * <p>
 * During the loading of lab value the most recent BUN and Creatine values
 * are preserved for use later on other screens.
 * </p>
 *
 * @author Don DeCoteau
 *
 */
public class ClinicalValue {
  protected String id;
  protected String name;
  protected Date   date;
  protected String value;
  private String   attributName;

  /**
   * Creates a new value
   *
   * @param id the id of the value
   * @param name the name of the value
   */
  public ClinicalValue(String id, String name) {
    super();
    this.id   = id;
    this.name = name;
  }

  /**
   * Creates a new value
   *
   * @param id the id of the value
   * @param name the name of the value
   * @param date the date of the value
   * @param value the actual value
   */
  public ClinicalValue(String id, String name, Date date, String value) {
    super();
    this.id    = id;
    this.name  = name;
    this.date  = date;
    this.value = value;
  }

  /**
   * Get the date associated with the value
   * @return the date associated with the value
   */
  public Date getDate() {
    return date;
  }

  /**
   * Get the id represented by the value
   * @return the id represented by the value
   */
  public String getID() {
    return id;
  }

  /**
   * Get the name associated with the value
   * @return the vale associated with the value
   */
  public String getName() {
    return name;
  }

  public String getValue() {

    /**
     * Get the actual clinical value
     * @return Get the actual clinical value
     */
    return value;
  }

  /**
   * Updates the value
   * @param date the date
   * @param value the value
   */
  public void update(Date date, String value) {
    if ((this.date == null) || date.after(this.date) || date.equals(this.date)) {
      this.value = value;
      this.date  = date;

      if (attributName != null) {
        Platform.getAppContext().putData(attributName, value);
      }
    }
  }

  /**
   * Gets the application level attribute associated with the value
   * @return the attribute associated with the value
   * @see #setAttributName(String)
   */
  public String getAttributName() {
    return attributName;
  }

  /**
   * Sets the application attribute associated with the value.
   * Application attributes can be referenced in UI markup by
   * prefixing the name with and '@' and surrounding it with curly braces.
   *
   * @param attributName the attribute name
   * @see #setAttributName(String)
   */
  public void setAttributName(String attributName) {
    this.attributName = attributName;
  }
}
