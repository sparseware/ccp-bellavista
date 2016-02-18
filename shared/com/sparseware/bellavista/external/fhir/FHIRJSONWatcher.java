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

package com.sparseware.bellavista.external.fhir;

import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONTokener.aWatcher;

import java.util.HashSet;

/**
 * The class provides a basic JSON watcher implementation
 * for walking entries in a bundles list.
 *
 * @author Don DeCoteau
 */
public class FHIRJSONWatcher extends aWatcher {
  protected boolean         parsingEntries;
  protected boolean         parsingLink;
  protected iCallback       callback;
  protected String          entryArray;
  protected String          parentArray;
  protected HashSet<String> ignoredArrays;

  public FHIRJSONWatcher(iCallback callback) {
    this(callback, "entry");
  }

  public FHIRJSONWatcher(iCallback callback, String entryArray) {
    this.callback   = callback;
    this.entryArray = entryArray;

    int n = entryArray.indexOf('/');

    if (n != -1) {
      parentArray = entryArray.substring(0, n);
    }
  }

  @Override
  public void didParseArray(JSONArray array) {
    String name = array.getName();

    if (entryArray.equals(name)) {
      parsingEntries = false;
    } else if ("link".equals(name)) {
      parsingLink = false;
    }

    if (ignoredArrays != null) {
      ignoredArrays.remove(name);
    }
  }

  @Override
  public Object valueEncountered(JSONArray parent, String arrayName, Object value) {
    if (arrayName.equals(entryArray)) {
      return callback.entryEncountered((JSONObject) value);
    } else if (arrayName.equals("link")) {
      JSONObject o = (JSONObject) value;

      return callback.linkEncountered(arrayName, o.getString("relation"), o.getString("url"));
    }

    if (parsingEntries || parsingLink) {
      return value;
    }

    if ((ignoredArrays != null) && ignoredArrays.contains(arrayName)) {
      return null;
    }

    return callback.otherArrayElementEncountered(arrayName, value);
  }

  @Override
  public Object valueEncountered(JSONObject parent, String valueName, Object value) {
    if (parsingEntries || parsingLink) {
      return value;
    }

    return callback.valueEncountered(parent, valueName, value);
  }

  @Override
  public void willParseArray(JSONArray array) {
    String name = array.getName();

    if ((parentArray != null) && name.equals(parentArray)) {
      return;
    }

    if (!parsingEntries) {
      parsingEntries = entryArray.equals(name);
    }

    if (!parsingLink) {
      parsingLink = "link".equals(name);
    }

    if (!parsingEntries &&!parsingLink &&!callback.parseArray(name)) {
      if (ignoredArrays == null) {
        ignoredArrays = new HashSet<String>();
      }

      ignoredArrays.add(name);
    }
  }

  public static interface iCallback {
    public Object entryEncountered(JSONObject entry);

    public Object valueEncountered(JSONObject parent, String key, Object value);

    public Object linkEncountered(String parentName, String type, String url);

    public Object otherArrayElementEncountered(String arrayName, Object value);

    public boolean parseArray(String arrayName);
  }


  public static abstract class aCallback implements iCallback {
    @Override
    public Object linkEncountered(String arrayName, String type, String url) {
      return null;
    }

    @Override
    public Object valueEncountered(JSONObject parent, String key, Object value) {
      return null;
    }

    @Override
    public Object otherArrayElementEncountered(String arrayName, Object value) {
      return null;
    }

    @Override
    public boolean parseArray(String arrayName) {
      return false;
    }
  }
}
