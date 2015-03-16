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
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.BackingStoreException;

import com.appnativa.rare.Platform;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.aListViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.aListWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.CharScanner;
import com.appnativa.util.iPreferences;

/*
 * THis class manages application settings and the UI
 * associated with those settings
 * 
 */
public class Settings implements iEventHandler {
  AppPreferences preferences;
  List<Server>   servers;

  boolean        serversUpdated;

  public Settings() {
    // the runtime will crate this automatically to handle events
    // we will trigger an event call at startup when we configure the
    // login server combo box and then save the handle so that it is not garbage
    // collected;
    Utils.settingsHandler = this;
    preferences = new AppPreferences();
  }

  public AppPreferences getAppPreferences(String user) {
    preferences.setUser(user);
    return preferences;
  }

  public String getLastLoggedinUser() {
    return preferences.getLastLoggedinUser();
  }

  public void onBackButton(String eventName, iWidget widget, EventObject event) {
    StackPaneViewer sp = (StackPaneViewer) widget.getFormViewer();
    int n = sp.getActiveViewerIndex();
    if (n > 0) {
      sp.switchTo(n - 1);
    }
  }

  /**
   * Called what an action is taken on a check box
   */
  public void onCheckBoxAction(String eventName, iWidget widget, EventObject event) {
    preferences.putBoolean(widget.getName(), widget.isSelected());
  }

  /**
   * Called when the close button is pressed. We will update the backing store
   * and close the window
   */
  public void onClose(String eventName, iWidget widget, EventObject event) {
    StackPaneViewer sp=(StackPaneViewer) widget.getFormViewer().getWidget("settingsStack");
    iWidget gb=sp==null ? null : sp.getWidget("serversSettings");
    if(gb!=null) {
      onServersUnload(eventName, gb, event); 
    }
    preferences.setServers(servers);
    preferences.update();
    widget.getWindow().close();
  }

  public void onConfigureBasicSettings(String eventName, iWidget widget, EventObject event) {
    iContainer fv = (iContainer) widget;
    for (iWidget w : fv.getWidgetList()) {
      switch (w.getWidgetType()) {
        case CheckBox:
          w.setSelected(preferences.getBoolean(w.getName(), false));
          break;

        default:
          break;
      }
    }
    serversUpdated = false;
  }

  public void onConfigureLoginComboBox(String eventName, iWidget widget, EventObject event) {
    aListWidget lw = (aListWidget) widget;
    servers = preferences.getServers();
    for (Server s : servers) {
      lw.addEx(new RenderableDataItem(s.serverName, s, null));
    }
    Server s = new Server(Platform.getResourceAsString("bv.text.local_demo"), "local", true);
    lw.addEx(new RenderableDataItem(s.serverName, s, null));
    lw.refreshItems();
    lw.setSelectedIndex(0);
  }

  public void onConfigureServers(String eventName, iWidget widget, EventObject event) {
    servers = preferences.getServers();
    aListViewer lv = (aListViewer) widget;
    for (Server s : servers) {
      lv.addEx(new RenderableDataItem(s.serverName, s, null));
    }
    lv.refreshItems();
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {
  }

  /**
   * Called when the setting list is finished being loaded
   * 
   * The data in the list is specified a string resource names so after they are
   * loaded we resolve to names into actual values.
   */
  public void onFinishedLoadingList(String eventName, iWidget widget, EventObject event) {
    aListViewer lv = (aListViewer) widget;
    int len = lv.size();
    for (int i = 0; i < len; i++) {
      RenderableDataItem item = lv.get(i);
      Object o = item.getValue();
      if (o instanceof String) {
        item.setValue(Platform.getResourceAsString((String) o));
      }
    }
  }

  public void onOtherOptionsAction(String eventName, iWidget widget, EventObject event) {
    int n = ((aListViewer) widget).getSelectedIndex();
    if (n != -1) {
      ((StackPaneViewer) widget.getFormViewer()).switchTo(n + 1);
    }
  }

  public void onServersAddAction(String eventName, iWidget widget, EventObject event) {
    final iFormViewer fv = widget.getFormViewer();
    Server s = new Server("", "", false);
    aListViewer lv = (aListViewer) widget.getFormViewer().getWidget("servers");
    lv.add(new RenderableDataItem(Platform.getResourceAsString("bv.text.settings.new_server"), s, null));
    servers.add(s);
    lv.setSelectedIndex(lv.size() - 1);
    Platform.invokeLater(new Runnable() {

      @Override
      public void run() {
        if (!fv.isDisposed()) {
          iWidget w = fv.getWidget("name");
          if (w != null && !w.isDisposed()) {
            w.requestFocus();
          }
        }

      }
    });
  }

  public void onServersChange(String eventName, iWidget widget, EventObject event) {
    iFormViewer fv = widget.getFormViewer();
    aListViewer lv = (aListViewer) widget;
    Server s = (Server) lv.getSelectionData();
    fv.getWidget("name").setValue(s == null ? "" : s.serverName);
    fv.getWidget("url").setValue(s == null ? "" : s.serverURL);
    fv.getWidget("context").setSelected(s == null ? false : s.isContextServer);
    fv.getWidget("update").setEnabled(s != null);
  }

  public void onServersDeleteAction(String eventName, iWidget widget, EventObject event) {
    aListViewer lv = (aListViewer) widget.getFormViewer().getWidget("servers");
    Server s = (Server) lv.getSelectionData();
    if (s != null) {
      lv.remove(lv.getSelectedIndex());
    }
    servers.remove(s);
  }

  /**
   * Called when the servers form is unloaded
   */
  public void onServersUnload(String eventName, iWidget widget, EventObject event) {
    aListViewer lv = (aListViewer) ((iContainer)widget).getWidget("servers");
    if(lv!=null) {
      servers.clear();
      int len = lv.size();
      for (int i = 0; i < len; i++) {
        servers.add((Server) lv.get(i).getLinkedData());
      }
    }
  }

  public void onServersUpdateAction(String eventName, iWidget widget, EventObject event) {
    iFormViewer fv = widget.getFormViewer();
    aListViewer lv = (aListViewer) fv.getWidget("servers");
    int n = lv.getSelectedIndex();
    if (n != -1) {
      RenderableDataItem item = lv.getSelectedItem();
      Server s = (Server) item.getLinkedData();
      s.serverName = fv.getWidget("name").getValueAsString();
      s.serverURL = fv.getWidget("url").getValueAsString().trim();
      s.isContextServer = fv.getWidget("context").isSelected();
      if (s.serverName.length() == 0) {
        Platform.getWindowViewer().beep();
      } else {
        item.setValue(s.serverName);
        lv.rowChanged(n);
      }
    }
  }

  public void saveLastLoggedinUser(String username) {
    preferences.saveLastLoggedinUser(username);
  }

  /**
   * This class manages the storing and retrieval of preferences
   * 
   * @author Don DeCoteau
   */
  public static class AppPreferences {
    boolean      edited;
    iPreferences prefs;
    iPreferences globalPrefs;
    String       user;
    List<Server> servers;
    HashMap      settings = new HashMap();

    public AppPreferences() {
      String path = Settings.class.getPackage().getName();
      path = path.replace('.', '/');
      globalPrefs = Functions.getPreferences(path);
    }

    public void dispose() {
      if (edited) {
        update();
      }
      globalPrefs = null;
      prefs = null;
    }

    public boolean getBoolean(String key, boolean def) {
      return prefs.getBoolean(key, def);
    }

    public int getInt(String key, int def) {
      return prefs.getInt(key, def);
    }

    public String getLastLoggedinUser() {
      try {
        return globalPrefs.get("username", null);
      } catch (Throwable e) {
        Platform.ignoreException(null, e);
        return null;
      }
    }

    public String getPassword(String key, String def) {
      String value = prefs.get(key, null);

      if (value != null) {
        value = Functions.decodeBase64(value);
      }

      return (value == null) ? def : value;
    }

    public List<Server> getServers() {
      if (servers == null) {
        ArrayList<Server> list = new ArrayList<Settings.Server>(5);
        String s = globalPrefs.get("servers", null);
        if (s != null && s.length() > 0) {
          s = Functions.decodeBase64(s);
          CharScanner sc = new CharScanner(s);
          while ((s = sc.nextToken('\t')) != null) {
            list.add(new Server(s));
          }
          sc.close();
        }
        servers = list;
      }
      return servers;
    }

    public String getString(String key, String def) {
      return prefs.get(key, def);
    }

    public void putBoolean(String key, boolean value) {
      prefs.putBoolean(key, value);
      edited = true;
    }

    public void putInt(String key, int value) {
      edited = true;
      prefs.putInt(key, value);
    }

    public void putPassword(String key, String value) {
      value = Functions.base64(value);
      putString(key, value);
    }

    public void putString(String key, String value) {
      edited = true;
      prefs.put(key, value);
    }

    public void removeValue(String key) {
      edited = true;
      prefs.remove(key);
    }

    public void saveLastLoggedinUser(String username) {
      try {
        globalPrefs.put("username", username);
      } catch (Throwable e) {
        Platform.ignoreException(null, e);
      }
    }

    public void setServers(List<Server> list) {
      servers = list;
      if (list == null || list.isEmpty()) {
        globalPrefs.remove("servers");
      } else {
        StringBuilder sb = new StringBuilder();
        boolean hasInvalid = false;
        for (Server s : list) {
          if (s.isValid()) {
            s.toString(sb).append('\t');
          } else {
            hasInvalid = true;
          }
        }
        if (sb.length() == 0) {
          globalPrefs.remove("servers");
        } else {
          sb.setLength(sb.length() - 1);
          globalPrefs.put("servers", Functions.base64(sb.toString()));
        }
        if (hasInvalid) {
          int len = list.size();
          for (int i = len - 1; i > -1; i--) {
            if (!list.get(i).isValid()) {
              list.remove(i);
            }
          }
        }
      }
      edited = true;
    }

    public void setUser(String user) {
      this.user = user;
      prefs = globalPrefs.getNode(user);
    }

    public void update() {
      try {
        if (globalPrefs != null) {
          globalPrefs.flush();
          globalPrefs.sync();
        }
        if (prefs != null) {
          prefs.flush();
          prefs.sync();
        }
      } catch (BackingStoreException e) {
        Platform.ignoreException(null, e);
      }

    }
  }

  public static class Server {
    public String  serverName;
    public String  serverURL;
    public boolean isContextServer;

    public Server(String s) {
      isContextServer = s.charAt(0) == '1';
      int n = s.indexOf('^');
      serverName = s.substring(1, n);
      serverURL = s.substring(n + 1);
    }

    public Server(String serverName, String serverURL, boolean isContextServer) {
      super();
      this.serverName = serverName;
      this.serverURL = serverURL;
      this.isContextServer = isContextServer;
    }

    public boolean isValid() {
      if (serverName == null || serverName.length() == 0 || serverURL == null || !serverURL.startsWith("http")) {
        return false;
      }
      return true;
    }

    public String toString() {
      return toString(new StringBuilder()).toString();
    }

    public StringBuilder toString(StringBuilder sb) {
      sb.append(isContextServer ? "1" : "0");
      sb.append(serverName).append("^").append(serverURL);
      return sb;
    }
  }
}
