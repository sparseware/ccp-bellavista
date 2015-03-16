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

import java.util.Date;

import com.appnativa.rare.Platform;

public class ClinicalValue {
  protected String id;
  protected String name;
  protected Date   date;
  protected String value;
  private String attributName;

  public ClinicalValue(String id, String name) {
    super();
    this.id = id;
    this.name = name;
  }

  public ClinicalValue(String id, String name, Date date, String value) {
    super();
    this.id = id;
    this.name = name;
    this.date = date;
    this.value = value;
  }

  public Date getDate() {
    return date;
  }

  public String getID() {
    return id;

  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void update(Date date, String value) {
    if (this.date == null || date.after(this.date) || date.equals(this.date)) {
      this.value = value;
      this.date = date;
      if(attributName!=null) {
        Platform.getAppContext().putData(attributName, value);
      }
    }
  }

  public String getAttributName() {
    return attributName;
  }

  public void setAttributName(String attributName) {
    this.attributName = attributName;
  }
}
