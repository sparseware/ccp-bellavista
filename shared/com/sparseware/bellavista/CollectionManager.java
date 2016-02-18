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
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iDataCollection;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIAction;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.UISound;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This class tracks and updates a set of collections the needs to be updated
 * periodically. This includes patient allergies, alerts , flags and user mail.
 *
 * @author Don DeCoteau
 *
 */
public class CollectionManager implements Runnable {
  int                              updateInterval;
  long                             lastUpdate;
  int                              quarterTime;
  private static CollectionManager instance;
  HashSet                          sounds      = new HashSet(3);
  HashMap<String, JSONObject>      collections = new HashMap<String, JSONObject>();
  volatile boolean                 paused;
  boolean                          started;

  public CollectionManager() {
    instance = this;

    iPlatformAppContext app = Platform.getAppContext();
    iDataCollection     dc;
    WindowViewer        w     = Platform.getWindowViewer();
    JSONObject          pinfo = (JSONObject) Platform.getAppContext().getData("patientSelectInfo");
    JSONObject          info  = (JSONObject) app.getData("collectionsInfo");
    JSONArray           list  = info.getJSONArray("collections");

    updateInterval = Math.max(info.optInt("updateInterval", 60) * 1000, 5000);

    JSONObject csp       = ((JSONObject) app.getData("user")).optJSONObject("site_parameters");
    int        keepalive = (csp == null)
                           ? 99999
                           : csp.optInt("keepalive_interval", 99999);

    keepalive *= 1000;

    if (keepalive < updateInterval) {
      updateInterval = keepalive;
    }

    quarterTime = updateInterval / 4;

    HashMap attributes = new HashMap<String, Object>(2);

    attributes.put("polling", true);    // this will tell the server that this is a

    // polling request and not an action taken
    // by the user
    int len = (list == null)
              ? 0
              : list.size();

    for (int i = 0; i < len; i++) {
      JSONObject o          = list.getJSONObject(i);
      String     name       = o.getString("name");
      boolean    autoUpdate = o.optBoolean("autoUpdate", true);

      if (name.equals("flags") &&!pinfo.optBoolean("hasPatientFlags", true)) {
        autoUpdate = false;
        o.put("noDataText", Platform.getResourceAsString("bv.text.no_support_for_flags"));
      } else if (name.equals("alerts") &&!pinfo.optBoolean("hasPatientFlags", true)) {
        autoUpdate = false;
        o.put("noDataText", Platform.getResourceAsString("bv.text.no_support_for_alerts"));
      }

      String     url  = o.getString("url");
      ActionLink link = Utils.createLink(w, url, true);

      if (autoUpdate &&!Utils.isDemo()) {
        link.setAttributes(attributes);
      }

      dc = w.createDataCollection(link, null);
      dc.setCollectionName(name);

      String s = o.optString("noDataText", null);

      if ((s != null) && (s.length() > 0)) {
        s = w.expandString(s);
        dc.setEmptyCollectionText(s);
      }

      app.registerDataCollection(dc);
      collections.put(name, o);
      o.put("_dc", dc);
    }

    // Create facilities collections
    list = (JSONArray) Platform.getAppContext().getData("facilities");
    len  = (list == null)
           ? 0
           : list.size();

    ArrayList<RenderableDataItem> rows = new ArrayList<RenderableDataItem>(len);

    for (int i = 0; i < len; i++) {
      String s = list.getString(i);

      s = w.expandString(s);
      rows.add(new RenderableDataItem(s));
    }

    dc = w.createDataCollection(w, rows);
    dc.setCollectionName("facilities");
    app.registerDataCollection(dc);
    // Create patient lists collections
    list = (JSONArray) pinfo.getJSONArray("listCategories");
    len  = (list == null)
           ? 0
           : list.size();
    rows = new ArrayList<RenderableDataItem>(len);

    iPlatformIcon listIcon = app.getResourceAsIcon("bv.icon.list");
    boolean       tabular  = UIScreen.isLargeScreen();

    for (int i = 0; i < len; i++) {
      PatientList pl = new PatientList(list.getJSONObject(i), w);

      rows.add(pl.createListItem(tabular, listIcon));
    }

    dc = w.createDataCollection(w, rows);
    dc.setCollectionName("patientListCategories");
    app.registerDataCollection(dc);
  }

  public void startPolling() {
    paused = false;

    if (!started &&!Utils.isDemo()) {
      started = true;
      Platform.getWindowViewer().setTimeout(this, updateInterval);
    }
  }

  public void stopPolling() {
    paused = true;
  }

  public void updateUI() {
    WindowViewer                        w  = Platform.getWindowViewer();
    Iterator<Entry<String, JSONObject>> it = collections.entrySet().iterator();

    while(it.hasNext()) {
      Entry<String, JSONObject> e    = it.next();
      String                    name = e.getKey();
      JSONObject                o    = e.getValue();

      updateUI(w, name, o, null);
    }
  }

  public void clear() {
    Iterator<JSONObject> it = collections.values().iterator();

    while(it.hasNext()) {
      JSONObject o = it.next();

      ((iDataCollection) o.get("_dc")).clearCollection();
    }
  }

  public iDataCollection getCollection(String name) {
    JSONObject o = collections.get(name);

    return (iDataCollection) ((o == null)
                              ? null
                              : o.get("_dc"));
  }

  public void refreshNow() {
    lastUpdate = 0;

    if (Platform.isUIThread()) {
      run();
    } else {
      update();
    }
  }

  @Override
  public void run() {
    long time = System.currentTimeMillis();

    if (lastUpdate + quarterTime < time) {
      Platform.getAppContext().executeBackgroundTask(new Runnable() {
        @Override
        public void run() {
          update();
        }
      });
    }
  }

  protected void update() {
    final WindowViewer w      = Platform.getWindowViewer();
    boolean            polled = false;

    if (!paused && Utils.continuePollingForUpdates()) {
      Iterator<Entry<String, JSONObject>> it = collections.entrySet().iterator();

      while(it.hasNext()) {
        Entry<String, JSONObject> e    = it.next();
        String                    name = e.getKey();
        JSONObject                o    = e.getValue();

        if (updateCollection(w, name, o)) {
          polled = true;
        }
      }

      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (!paused && Utils.continuePollingForUpdates()) {
            sounds.clear();

            Iterator<Entry<String, JSONObject>> it = collections.entrySet().iterator();

            while(it.hasNext()) {
              Entry<String, JSONObject> e    = it.next();
              String                    name = e.getKey();
              JSONObject                o    = e.getValue();

              updateUI(w, name, o, sounds);
            }
          }
        }
      });
    }

    if (!Platform.getAppContext().isShuttingDown()) {
      if (!polled && Utils.continuePollingForUpdates()) {
        try {
          final ActionLink l = w.createActionLink("/hub/main/account/status");
          JSONObject       o = new JSONObject(l.getContentAsString());

          Utils.handleStatusObject(o);
        } catch(Exception e) {}
      }

      w.setTimeout(this, updateInterval);
    }
  }

  protected boolean updateCollection(WindowViewer w, String name, JSONObject o) {
    boolean autoUpdate = o.optBoolean("autoUpdate", true);

    try {
      if (autoUpdate) {
        iDataCollection dc = (iDataCollection) o.get("_dc");

        dc.refresh(w);

        return true;
      }
    } catch(final Exception e) {
      Utils.handleError(e);
    }

    return false;
  }

  protected void updateUI(WindowViewer w, String name, JSONObject o, HashSet sounds) {
    iDataCollection dc  = (iDataCollection) o.get("_dc");
    int             len = dc.size();
    int             lc  = o.optInt("_lc", 0);

    o.put("_lc", len);

    if (len == 1) {
      Collection                   c    = dc.getCollection(w);
      Iterator<RenderableDataItem> it   = c.iterator();
      RenderableDataItem           item = it.next();

      if (!item.isEnabled()) {
        len = 0;
      }
    }

    String action = o.optString("action", null);

    if (action != null) {
      UIAction a = w.getAction(action);

      if (a != null) {
        if (a.getActionName().equals("bv.action.alerts")) {
          Alerts.updateAlertsIcon();
        } else {
          a.setEnabled(len > 0);
        }
      }

      if ((lc != len) && (sounds != null)) {
        String sound = o.optString("sound", null);

        if ((sound != null) && (sound.length() > 0) && sounds.add(sound)) {
          try {
            UISound sd = Platform.getAppContext().getSound(sound);

            if (sd != null) {
              sd.play();
            }
          } catch(Exception e) {
            Platform.ignoreException(null, e);
          }
        }
      }
    }
  }

  public static CollectionManager getInstance() {
    return instance;
  }

  static class PatientList {
    public iDataCollection collection;
    public String          title;
    public String          listPath;
    public String          patientsPath;
    public boolean         containPatients;
    public String          name;
    boolean                enabled;

    public boolean isContainsPatients() {
      return containPatients;
    }

    public RenderableDataItem createListItem(boolean tabular, iPlatformIcon listIcon) {
      RenderableDataItem item = new RenderableDataItem(title, this, null);

      if (containPatients) {
        item.setIcon(listIcon);
      }

      if (tabular) {
        RenderableDataItem row = new RenderableDataItem();

        row.add(item);
        row.setLinkedData(this);
        item = row;
      }

      if (!enabled) {
        item.setEnabled(false);
      }

      return item;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public String getCollectionHREF() {
      return iConstants.COLLECTION_PREFIX + name;
    }

    public String getListHREF(String id) {
      return "/hub/main/util/patients/" + patientsPath + "/" + id;
    }

    public ActionLink getListLink(iWidget context, String id) {
      return Utils.createLink(context, getListHREF(id), true);
    }

    public ActionLink getCollectionLink(iWidget context) {
      return Utils.createLink(context, getCollectionHREF(), true);
    }

    public PatientList(JSONObject list, WindowViewer w) {
      name  = list.optString("name", null);
      title = w.expandString(list.getString("title"));

      String s = list.optString("titleFormat");

      if ((s != null) && (s.length() > 0)) {
        s     = w.expandString(s);
        title = Functions.format(s, title);
      }

      s               = list.getString("type");
      containPatients = s.equals("patients");
      listPath        = list.optString("listPath");
      patientsPath    = list.optString("patientsPath");
      enabled         = list.optBoolean("enabled", true);

      ActionLink link;

      if (containPatients) {
        link       = Utils.createLink(w, "/hub/main/util/patients/" + patientsPath, false);
        collection = w.createDataCollection(name, link, true, null);
      } else {
        link       = Utils.createLink(w, "/hub/main/util/lists/" + listPath, false);
        collection = w.createDataCollection(name, link, false, null);
      }

      collection.setRefreshOnURLConnection(true);
    }
  }
}
