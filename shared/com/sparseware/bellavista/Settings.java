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
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.viewer.CheckBoxListViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.aListViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.TextFieldWidget;
import com.appnativa.rare.widget.aListWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.ObjectHolder;
import com.appnativa.util.SNumber;
import com.appnativa.util.StringCache;
import com.appnativa.util.iPreferences;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

import com.sparseware.bellavista.external.aProtocolHandler;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.prefs.BackingStoreException;

/*
 * This class manages application settings and the UI
 * associated with those settings
 *
 */
public class Settings implements iEventHandler {
  static List<Server> defaultServers;
  AppPreferences      preferences;
  List<Server>        servers;
  boolean             serversUpdated;

  public Settings() {
    // the runtime will crate this automatically to handle events
    // we will trigger an event call at startup when we configure the
    // login server combo box (or onEvent call )and then save the handle so that it is not garbage
    // collected;
    Utils.settingsHandler = this;
    preferences           = new AppPreferences();
  }

  public AppPreferences getAppPreferences(String user) {
    preferences.setUser(user);

    return preferences;
  }

  /**
   * Called to get the default server for the device when the user is not able
   * to choose a server.
   *
   * @return the server
   */
  public Server getDefaultServer() {
    List<Server> list = preferences.getServers();

    if ((list != null) &&!list.isEmpty()) {
      return list.get(0);
    }

    Server s = new Server(Platform.getResourceAsString("bv.text.local_demo"), "local", true);

    s.put("restricted", false);

    return s;
  }

  public String getLastLoggedinUser() {
    return preferences.getLastLoggedinUser();
  }

  public void onActionLoginComboBox(String eventName, iWidget widget, EventObject event) {
    updateLoginFormForSelectedServer((aListWidget) widget);
  }

  public void onBackButton(String eventName, iWidget widget, EventObject event) {
    StackPaneViewer sp = (StackPaneViewer) widget.getFormViewer();

    sp.switchTo(0);
  }

  /**
   * Called when an action is taken on a check box
   */
  public void onCheckBoxAction(String eventName, iWidget widget, EventObject event) {
    preferences.putBoolean(widget.getName(), widget.isSelected());
  }

  public void onConfigureBasicSettings(String eventName, iWidget widget, EventObject event) {
    iContainer fv = (iContainer) widget;

    for (iWidget w : fv.getWidgetList()) {
      switch(w.getWidgetType()) {
        case CheckBox :
          w.setSelected(preferences.getBoolean(w.getName(), false));

          break;

        default :
          break;
      }
    }

    serversUpdated = false;
  }

  public void onConfigureLoginComboBox(final String eventName, final iWidget widget, final EventObject event) {
    final aListWidget  lw = (aListWidget) widget;
    final WindowViewer w  = Platform.getWindowViewer();

    if (defaultServers == null) {
      if (Platform.getAppContext().getContextURL().getProtocol().equals("file")) {
        loadDefaultServers();
      } else {
        aWorkerTask task = new aWorkerTask() {
          @Override
          public Object compute() {
            loadDefaultServers();

            return null;
          }
          @Override
          public void finish(Object result) {
            w.hideWaitCursor();
            populateLoginServersWidget(lw);

            if (lw.getFormViewer().getWidget("password") != null) {
              updateLoginFormForSelectedServer(lw);
            }
          }
        };

        w.spawn(task);
        w.showWaitCursor();

        return;
      }
    }

    populateLoginServersWidget(lw);
  }

  public void onConfigureServers(String eventName, iWidget widget, EventObject event) {
    servers = preferences.getServers();
    populateServers((aListViewer) widget);
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {}

  /**
   * Called when the setting list is finished being loaded
   *
   * The data in the list is specified a string resource names so after they are
   * loaded we resolve to names into actual values.
   */
  public void onFinishedLoadingList(String eventName, iWidget widget, EventObject event) {
    aListViewer lv  = (aListViewer) widget;
    int         len = lv.size();

    for (int i = 0; i < len; i++) {
      RenderableDataItem item = lv.get(i);
      Object             o    = item.getValue();

      if (o instanceof String) {
        item.setValue(Platform.getResourceAsString((String) o));
      }
    }
  }

  public void onNetworkServersAddAction(String eventName, iWidget widget, EventObject event) {
    final WindowViewer w  = Platform.getWindowViewer();
    final aListViewer  lv = (aListViewer) widget.getFormViewer().getWidget("servers");

    try {
      w.createViewer("/network_servers.rml", new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          w.hideWaitCursor();

          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          } else {
            showNetworkServersViewer((iContainer) returnValue, lv);
          }
        }
      });
      w.showWaitCursor();
    } catch(MalformedURLException e) {
      Utils.handleError(e);
    }
  }

  public void onNetworkServersDownload(String eventName, iWidget widget, EventObject event) {
    iFormViewer           fv  = widget.getFormViewer();
    final aListViewer     lv  = (aListViewer) fv.getWidget("servers");
    final WindowViewer    w   = Platform.getWindowViewer();
    final TextFieldWidget tf  = (TextFieldWidget) fv.getWidget("url");
    String                url = tf.getValueAsString();

    if (url != null) {
      url = url.trim();
    }

    if ((url == null) || (url.length() == 0)) {
      url = (String) tf.getAttribute("default_network_servers_url");
    }

    if ((url == null) || (url.length() == 0)) {
      return;
    }

    try {
      w.getContentAsJSON(url, new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          w.hideWaitCursor();

          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          } else {
            try {
              ObjectHolder oh = (ObjectHolder) returnValue;
              JSONObject   o  = (JSONObject) oh.value;
              JSONArray    a  = o.getJSONArray("servers");

              lv.clear();

              for (Object s : a) {
                Server             server = new Server((JSONObject) s);
                RenderableDataItem item   = new RenderableDataItem(server.getName(), server, null);

                lv.addEx(item);
              }

              lv.refreshItems();
            } catch(Exception e) {
              w.alert(w.getString("bv.text.invalid_network_servers_data"));
              Platform.ignoreException(null, e);
            }
          }
        }
      });
      w.showWaitCursor();
    } catch(MalformedURLException e) {
      Utils.handleError(e);
    }
  }

  public void onOtherOptionsAction(String eventName, iWidget widget, EventObject event) {
    int n = ((aListViewer) widget).getSelectedIndex();

    if (n != -1) {
      ((StackPaneViewer) widget.getFormViewer()).switchTo(n + 1);
    }
  }

  /**
   * Called when the pin field is focused
   */
  public void onPinFocused(String eventName, iWidget widget, EventObject event) {
    widget.getFormViewer().getWidget("message").setValue("");
  }

  public void onServersAddAction(String eventName, iWidget widget, EventObject event) {
    final iFormViewer fv = widget.getFormViewer();
    Server            s  = new Server("", "", false);
    aListViewer       lv = (aListViewer) fv.getWidget("servers");

    lv.add(new RenderableDataItem(Platform.getResourceAsString("bv.text.settings.new_server"), s, null));
    servers.add(s);
    lv.setSelectedIndex(lv.size() - 1);
    Platform.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (!fv.isDisposed()) {
          iWidget w = fv.getWidget("name");

          if ((w != null) &&!w.isDisposed()) {
            w.requestFocus();
          }
        }
      }
    });
  }

  public void onServersChange(String eventName, iWidget widget, EventObject event) {
    iFormViewer     fv      = widget.getFormViewer();
    aListViewer     lv      = (aListViewer) widget;
    Server          s       = (Server) lv.getSelectionData();
    TextFieldWidget name    = (TextFieldWidget) fv.getWidget("name");
    TextFieldWidget url     = (TextFieldWidget) fv.getWidget("url");
    iWidget         context = fv.getWidget("context");

    name.setValue((s == null)
                  ? ""
                  : s.getName());
    url.setValue((s == null)
                 ? ""
                 : s.getURL());
    url.setCaretPosition(0);
    name.setCaretPosition(0);
    context.setSelected((s == null)
                        ? false
                        : s.isContextServer());
    fv.getWidget("update").setEnabled((s != null) &&!s.isFrozen());
    name.setEnabled((s != null) &&!s.isFrozen());
    url.setEnabled((s != null) &&!s.isFrozen());
    context.setEnabled((s != null) &&!s.isFrozen());
  }

  public void onServersDeleteAction(String eventName, iWidget widget, EventObject event) {
    aListViewer lv = (aListViewer) widget.getFormViewer().getWidget("servers");
    Server      s  = (Server) lv.getSelectionData();

    if (s != null) {
      int n = lv.getSelectedIndex();

      lv.remove(n);

      if (lv.size() >= n) {
        n--;
      }

      if (n > -1) {
        lv.setSelectedIndex(n);
      }
    }

    servers.remove(s);
  }

  /**
   * Called when the servers form is unloaded
   */
  public void onServersUnload(String eventName, iWidget widget, EventObject event) {
    aListViewer lv = (aListViewer) ((iContainer) widget).getWidget("servers");

    if (lv != null) {
      servers.clear();

      int len = lv.size();

      for (int i = 0; i < len; i++) {
        servers.add((Server) lv.get(i).getLinkedData());
      }
    }

    preferences.setServers(servers);
    preferences.update();
  }

  public void onServersUpdateAction(String eventName, iWidget widget, EventObject event) {
    iFormViewer  fv = widget.getFormViewer();
    aListViewer  lv = (aListViewer) fv.getWidget("servers");
    int          n  = lv.getSelectedIndex();
    WindowViewer w  = Platform.getWindowViewer();

    if (n != -1) {
      String url      = fv.getWidget("url").getValueAsString().trim();
      int    p        = url.indexOf(':');
      String protocol = (p == -1)
                        ? null
                        : url.substring(0, p);

      if ((protocol != null) &&!Utils.isServerProtocolSupported(protocol)) {
        w.alert(w.getString("bv.text.settings.protocol_not_supported"));

        return;
      }

      RenderableDataItem item = lv.getSelectedItem();
      Server             s    = (Server) item.getLinkedData();

      s.setName(fv.getWidget("name").getValueAsString());
      s.setUrl(url);
      s.serverType = fv.getWidget("context").isSelected()
                     ? Server.TYPE_CONTEXT
                     : 0;

      if (s.getName().length() == 0) {
        Platform.getWindowViewer().beep();
      } else {
        aProtocolHandler h = Utils.getProtocolHandler(protocol);

        item.setValue(s.getName());
        lv.rowChanged(n);

        if (h != null) {
          h.serverConfigurationUpdated(s);
        }
      }
    }
  }

  public void onShownLoginForm(String eventName, iWidget widget, EventObject event) {
    updateLoginFormForSelectedServer((aListWidget) widget.getFormViewer().getWidget("server"));
  }

  /**
   * Called to submit a pin for a wearable device
   */
  public void onSubmitPin(String eventName, iWidget widget, EventObject event) {
    try {
      String pin = widget.getFormViewer().getWidget("pin").getValueAsString();

      if (pin == null) {
        pin = "";
      }

      final WindowViewer w = Platform.getWindowViewer();
      final iWidget      l = widget.getFormViewer().getWidget("message");

      if ((pin.length() < CardStack.getPinDigitCount()) ||!SNumber.isNumeric(pin)) {
        String s = w.getString("bv.format.invalid_pin", StringCache.valueOf(CardStack.getPinDigitCount()));

        l.setValue(s);
        w.beep();
      } else {
        l.setValue("");

        if (Utils.isDemo()) {
          l.setValue(w.getString("bv.text.settings.pin_submitted"));
        } else {
          final ActionLink link = Utils.createLink(w, "/hub/account/allow_pin");
          final HashMap    data = new HashMap(2);

          data.put("pin", pin);

          aWorkerTask task = new aWorkerTask() {
            @Override
            public Object compute() {
              try {
                link.sendFormData(w, data);

                return link.getContentAsString();
              } catch(Exception e) {
                return e;
              }
            }
            @Override
            public void finish(Object result) {
              w.hideWaitCursor();

              if (result instanceof Throwable) {
                Utils.handleError((Throwable) result);
              } else {
                String s = (String) result;

                if (s != null) {
                  s = s.trim();
                }

                if ((s == null) || (s.length() == 0)) {
                  s = w.getString("bv.text.settings.pin_submitted");
                }

                l.setValue(s);
              }
            }
          };

          w.spawn(task);
          w.showWaitCursor();
        }
      }
    } catch(Exception e) {
      Utils.handleError(e);
    }
  }

  public void saveLastLoggedinUser(String username) {
    preferences.saveLastLoggedinUser(username);
  }

  protected void loadDefaultServers() {
    if (defaultServers == null) {
      try {
        ActionLink l = new ActionLink("/data/servers.json");
        JSONObject o = new JSONObject(l.getContentAsString());
        JSONArray  a = o.getJSONArray("servers");

        for (Object s : a) {
          Server server = new Server((JSONObject) s);

          if (defaultServers == null) {
            defaultServers = new ArrayList<Settings.Server>(3);
          }

          defaultServers.add(server);
        }
      } catch(Exception e) {
        Platform.ignoreException(e);
      }
    }

    if (defaultServers == null) {
      defaultServers = new ArrayList<Settings.Server>(1);
      defaultServers.add(getDefaultServer());
    }
  }

  protected HashSet<String> getServersSet() {
    servers = preferences.getServers();

    HashSet<String> set = new HashSet<String>();

    for (Server s : servers) {
      if (s.isValid()) {
        set.add(s.toHashKey());
      }
    }

    return set;
  }

  protected void populateLoginServersWidget(aListWidget lw) {
    servers = preferences.getServers();

    HashSet<String> set = new HashSet<String>();

    for (Server s : servers) {
      if (s.isValid()) {
        lw.addEx(new RenderableDataItem(s.getName(), s, null));
        set.add(s.toHashKey());
      }
    }

    if (set.isEmpty()) {
      for (Server s : defaultServers) {
        if (s.isValid() &&!set.contains(s.toHashKey())) {
          lw.addEx(new RenderableDataItem(s.getName(), s, null));
        }
      }
    } else {
      Server s = new Server(Platform.getResourceAsString("bv.text.local_demo"), "local", true);

      s.put("restricted", false);

      if (!set.contains(s.toHashKey())) {
        lw.addEx(new RenderableDataItem(s.getName(), s, null));
      }
    }

    lw.refreshItems();
    lw.setSelectedIndex(0);
  }

  protected void populateServers(aListViewer lv) {
    for (Server s : servers) {
      lv.addEx(new RenderableDataItem(s.getName(), s, null));
    }

    lv.refreshItems();
  }

  protected void showNetworkServersViewer(final iContainer v, final aListViewer lv) {
    String                   url = Utils.getPreferences().getString("network_servers_url", null);
    final TextFieldWidget    tf  = (TextFieldWidget) v.getWidget("url");
    final CheckBoxListViewer lb  = (CheckBoxListViewer) v.getWidget("servers");

    lb.getItemRenderer().setSelectionPainted(false);
    tf.setAttribute("default_network_servers_url", tf.getValueAsString());

    if ((url != null) && (url.length() > 0)) {
      tf.setValue(url);
    }

    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (!canceled && Boolean.TRUE.equals(returnValue)) {
          int sels[] = lb.getSelectedIndexes();

          if (sels != null) {
            HashSet<String> set = getServersSet();

            for (int i : sels) {
              Server s = (Server) lb.get(i).getLinkedData();

              if (s.isValid() &&!set.contains(s.toHashKey())) {
                servers.add(s);
              }
            }

            populateServers(lv);

            String url  = tf.getValueAsString();
            String ourl = (String) tf.getAttribute("network_servers_url");

            if (!url.equals(ourl)) {
              Utils.getPreferences().putString("network_servers_url", url);
            }
          }
        }

        v.dispose();
      }
    };

    Platform.getWindowViewer().okCancel(v, cb);
  }

  protected void updateLoginFormForSelectedServer(aListWidget widget) {
    Server s = (Server) widget.getSelectionData();

    if (s != null) {
      iFormViewer fv         = widget.getFormViewer();
      boolean     restricted = s.isRestricted();

      fv.getWidget("usernameLabel").setEnabled(restricted);
      fv.getWidget("username").setEnabled(restricted);
      fv.getWidget("passwordLabel").setEnabled(restricted);
      fv.getWidget("password").setEnabled(restricted);

      if (restricted) {
        if (!Platform.isTouchDevice()) {
          fv.getWidget("username").requestFocus();
        }
      } else {
        PushButtonWidget pb = (PushButtonWidget) fv.getWidget("signin");

        if (s.hasCustomLogin()) {
          String text = s.optString("button_text", null);

          if (text == null) {
            text = Platform.getResourceAsString("bv.text.continue");
          } else {
            text = pb.expandString(text);
          }

          pb.setText(text);
        } else {
          pb.setText(Platform.getResourceAsString("bv.text.sign_in"));
        }

        pb.requestFocus();
      }
    }
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

      path        = path.replace('.', '/');
      globalPrefs = Functions.getPreferences(path);
    }

    public void dispose() {
      if (edited) {
        update();
      }

      globalPrefs = null;
      prefs       = null;
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
      } catch(Throwable e) {
        Platform.ignoreException(null, e);

        return null;
      }
    }

    public String getPassword(String key, String def) {
      String value = prefs.get(key, null);

      if (value != null) {
        value = Functions.decodeBase64(value);
      }

      return (value == null)
             ? def
             : value;
    }

    public List<Server> getServers() {
      if (servers == null) {
        ArrayList<Server> list = new ArrayList<Settings.Server>(5);

        servers = list;

        try {
          iPreferences p   = globalPrefs.getNode("servers");
          int          len = p.getInt("length", 0);

          for (int i = 0; i < len; i++) {
            String s = p.get("s_" + i, null);

            if ((s != null) && (s.length() > 0)) {
              try {
                s = Functions.decodeBase64(s);

                Server server = new Server(new JSONObject(s));

                if (server.isValid()) {
                  list.add(server);
                }
              } catch(Exception e) {
                Platform.ignoreException(e);
              }
            }
          }
        } catch(Exception e) {
          Platform.ignoreException(e);
        }
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
      } catch(Throwable e) {
        Platform.ignoreException(null, e);
      }
    }

    public void setServers(List<Server> list) {
      servers = list;

      iPreferences p = globalPrefs.getNode("servers");

      try {
        p.clear();
      } catch(BackingStoreException e) {
        Platform.ignoreException(e);
      }

      int len = (list == null)
                ? 0
                : list.size();

      p.putInt("length", len);

      int count = 0;

      for (int i = 0; i < len; i++) {
        Server s = list.get(i);

        if (s.isValid()) {
          p.put("s_" + count, Functions.base64(s.toString()));
          count++;
        }
      }

      p.putInt("length", count);

      for (int i = len - 1; i > -1; i--) {
        if (!list.get(i).isValid()) {
          list.remove(i);
        }
      }

      edited = true;
    }

    public void setUser(String user) {
      this.user = user;
      prefs     = globalPrefs.getNode(user);
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
      } catch(Exception e) {
        Platform.ignoreException(null, e);
      }
    }
  }


  public static class Server extends JSONObject {
    public static final int TYPE_CONTEXT = 0x01;
    public static final int TYPE_DEMO    = 0x02;
    public static final int TYPE_OTHER   = 0x04;
    private int             serverType   = 0;

    public Server(JSONObject o) {
      super(o.getObjectMap());
      populate(this);
    }

    public Server(String serverName, String serverURL, boolean isContextServer) {
      this(serverName, serverURL, isContextServer
                                  ? TYPE_CONTEXT
                                  : 0);
    }

    public Server(String serverName, String serverURL, int serverType) {
      super();
      put("type", serverType);
      put("name", serverName);
      put("url", serverURL);

      boolean restricted = !"local".equals(serverURL);

      put("restricted", restricted);
    }

    public boolean isContextServer() {
      return (serverType & TYPE_CONTEXT) != 0;
    }

    public boolean isDemoServer() {
      return (serverType & TYPE_DEMO) != 0;
    }

    public boolean isFrozen() {
      return (serverType & TYPE_OTHER) != 0;
    }

    public boolean canChangePatient() {
      return optBoolean("can_change_patient", true);
    }

    public String toHashKey() {
      return getName() + ":" + getURL();
    }

    public void setName(String name) {
      put("name", name);
    }

    public void setUrl(String url) {
      put("url", url);
    }

    void setType(int type) {
      serverType = type;
      put("type", type);
    }

    public String getName() {
      return optString("name");
    }

    public String getURL() {
      return optString("url");
    }

    public boolean hasCustomLogin() {
      return optBoolean("custom_login", false);
    }

    public boolean isRestricted() {
      return optBoolean("restricted");
    }

    public boolean isValid() {
      if ((getName().length() == 0) || (getURL().length() == 0)) {
        return false;
      }

      return true;
    }

    protected void populate(JSONObject o) {
      int type = o.optInt("type", -1);

      if (type == -1) {
        String s = o.optString("type");

        if (s.equalsIgnoreCase("default")) {
          serverType = 0;
        } else if (s.equalsIgnoreCase("demo")) {
          serverType = TYPE_DEMO;
        } else if (s.equalsIgnoreCase("context")) {
          serverType = TYPE_CONTEXT;
        } else {
          serverType = TYPE_OTHER;
        }
      } else {
        serverType = type;
      }

      boolean restricted = optBoolean("restricted", true);

      put("restricted", restricted);
    }
  }
}
