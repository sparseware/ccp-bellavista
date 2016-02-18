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
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIDimension;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.viewer.ListBoxViewer;
import com.appnativa.rare.viewer.TabPaneViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.aListViewer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.ObjectHolder;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

import com.sparseware.bellavista.external.aCommunicationHandler;
import com.sparseware.bellavista.external.aCommunicationHandler.Status;
import com.sparseware.bellavista.external.aCommunicationHandler.UserStatus;
import com.sparseware.bellavista.external.aCommunicationHandler.iStatusListener;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages the loading, presentation and communication with
 * the patients care team.
 *
 * @author Don DeCoteau
 *
 */
public class CareTeam implements iEventHandler, iStatusListener {
  protected static UserStatus          OFFLINE = new UserStatus();
  protected aCommunicationHandler      commHandler;
  protected Map<String, UserStatus>    userStatuses;
  protected ListBoxViewer              listViewer;
  protected Map<Status, iPlatformIcon> statusIcons;
  protected int                        idPosition = 1;
  protected long                       lastUpdateTime;
  protected int                        dataTimeout;

  public CareTeam() {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("careTeamInfo");

    dataTimeout = info.optInt("dataTimeout", 0) * 1000;
    commHandler = Utils.getaCommunicationHandler();

    if (commHandler != null) {
      userStatuses = new HashMap<String, UserStatus>();
      statusIcons  = Utils.getStatusIcons();
      idPosition   = info.optInt("commIdPosition", 1);
    }
  }

  /**
   * Called when the care team UI is created.
   * We adjust's is size for the screen size
   */
  public void onCreated(String eventName, final iWidget widget, EventObject event) {
    if (!UIScreen.isLargeScreen()) {
      UIDimension size = UIScreen.getUsableSize();
      int         n    = (int) UIScreen.fromPlatformPixels(Math.max(size.width, size.height));

      if (n < 540) {
        Viewer cfg = (Viewer) ((DataEvent) event).getData();

        cfg.bounds.height.setValue("14ln");
      } else {
        Viewer cfg = (Viewer) ((DataEvent) event).getData();

        cfg.bounds.height.setValue("18ln");
      }
    }
  }

  /**
   * Called when the care team UI is being disposed
   *
   */
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    if (commHandler != null) {
      commHandler.removeStatusListener(this);
    }

    commHandler  = null;
    userStatuses = null;
    listViewer   = null;
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {}

  /**
   * Called when the user selects a new entry in one of the lists.
   * We check the availability of the member for their availability to communicate
   */
  public void onListChange(String eventName, iWidget widget, EventObject event) {
    if (commHandler != null) {
      iFormViewer        fv     = widget.getFormViewer();
      RenderableDataItem item   = ((ListBoxViewer) widget).getSelectedItem();
      String             id     = (item == null)
                                  ? null
                                  : (String) item.getLinkedData();
      UserStatus         status = (id == null)
                                  ? OFFLINE
                                  : userStatuses.get(id);

      if (status == null) {
        status = OFFLINE;
      }

      PushButtonWidget pb = (PushButtonWidget) fv.getWidget("bv.action.video_chat");

      if (pb != null) {
        pb.setEnabled(commHandler.isVideoChatAvailable(null) && (status.videoChat != Status.ONLINE));
      }

      pb = (PushButtonWidget) fv.getWidget("bv.action.audio_chat");

      if (pb != null) {
        pb.setEnabled(commHandler.isVideoChatAvailable(null) && (status.audioChat != Status.ONLINE));
      }

      pb = (PushButtonWidget) fv.getWidget("bv.action.text_chat");

      if (pb != null) {
        pb.setEnabled(commHandler.isVideoChatAvailable(null) && (status.textChat != Status.ONLINE));
      }
    }
  }

  /**
   * Called when either the 'providers' or 'others' list is shown.
   * If this is the first time the list is being shown, then we populate
   * it if we have data.
   */
  public void onListShown(String eventName, iWidget widget, EventObject event) {
    listViewer = (ListBoxViewer) widget;

    iFormViewer              fv   = widget.getFormViewer();
    List<RenderableDataItem> list = null;

    if (listViewer.getName().equals("others")) {
      list = (List<RenderableDataItem>) fv.removeAttribute("_othersList");
    } else {
      list = (List<RenderableDataItem>) fv.removeAttribute("_physiciansList");
    }

    if (list != null) {
      listViewer.setAll(list);
    }
  }

  /**
   * Called when the care team UI is shown.
   * We check the freshness of the data and reload as appropriate
   */
  public void onShown(String eventName, final iWidget widget, EventObject event) {
    final WindowViewer w = Platform.getWindowViewer();
    JSONArray          a = (JSONArray) Platform.getAppContext().getData("pt_careteam");

    if ((a != null) || (lastUpdateTime + dataTimeout > System.currentTimeMillis())) {
      updateForm(widget.getFormViewer(), a);
    } else {
      try {
        w.getContentAsJSON("/hub/main/patient/careteam.json", new iFunctionCallback() {
          @Override
          public void finished(boolean canceled, Object returnValue) {
            w.hideWaitCursor();

            if (returnValue instanceof Throwable) {
              Utils.handleError((Throwable) returnValue);

              return;
            }

            ObjectHolder oh = (ObjectHolder) returnValue;
            JSONObject   o  = (JSONObject) oh.value;
            JSONArray    a  = (o == null)
                              ? null
                              : o.optJSONArray("_rows");

            if (a == null) {
              a = new JSONArray();
            } else {
              Platform.getAppContext().putData("pt_careteam", a);
              lastUpdateTime = System.currentTimeMillis();
            }

            updateForm(widget.getFormViewer(), a);
          }
        });
      } catch(MalformedURLException e) {
        Utils.handleError(e);
      }

      w.showWaitCursor();
    }
  }

  /**
   * Called when the availability of a communication service has change.
   * We re-check the availability of the currently selected care team member.
   *
   * @param handler the handler
   */
  @Override
  public void serviceAvailbilityChanged(aCommunicationHandler handler) {
    if (listViewer != null) {
      onListChange(iConstants.EVENT_CHANGE, listViewer, null);
    }
  }

  @Override
  public void statusChanged(aCommunicationHandler handler, String user, UserStatus status) {
    userStatuses.put(user, status);

    if (listViewer != null) {
      ListBoxViewer lv  = listViewer;
      int           len = lv.size();

      for (int i = 0; i < len; i++) {
        String id = (String) lv.get(i).getLinkedData();

        if ((id != null) && id.equals(user)) {
          RenderableDataItem item = lv.get(i);

          item.setIcon(statusIcons.get(status));
          lv.repaintRow(i);

          if (lv.isRowSelected(i)) {
            onListChange(iConstants.EVENT_CHANGE, listViewer, null);
          }

          break;
        }
      }
    }
  }

  /**
   * Gets the user id of the care team member in the specified row.
   * This is the id that can be used the the communication manager.
   *
   * @param row the row representing the care team member
   * @return the member's user id.
   */
  protected String getUserID(RenderableDataItem row) {
    RenderableDataItem item = row.getItemEx(idPosition);

    return (item == null)
           ? null
           : (String) item.getValue();
  }

  /**
   * Called to update the UI with data
   * @param fv the form viewer
   * @param items the data items
   */
  protected void updateForm(iFormViewer fv, JSONArray items) {
    int len = (items == null)
              ? 0
              : items.size();

    if (len == 0) {
      return;
    }

    ArrayList<RenderableDataItem> othersList     = new ArrayList<RenderableDataItem>();
    ArrayList<RenderableDataItem> physiciansList = new ArrayList<RenderableDataItem>();
    ArrayList<String>             users          = new ArrayList<String>(len);
    RenderableDataItem            item;
    StringBuilder                 sb = new StringBuilder();
    String                        s;
    JSONObject                    user = Utils.getUser();
    String                        me   = user.optString("xmpp_id", "");
    boolean                       isme = false;

    for (int i = 0; i < len; i++) {
      user = items.getJSONObject(i);

      final boolean small  = UIScreen.isSmallScreen();
      String        name   = user.optString("name");
      String        role   = user.optString("role");
      String        xmppid = user.optString("xmpp_id", null);

      item = new RenderableDataItem();
      sb.setLength(0);
      sb.append("<html>").append(role);
      sb.append(small
                ? "<br/>- "
                : " - ");
      sb.append(name);

      if (!isme) {
        isme = me.equals(xmppid);

        if (isme) {
          sb.append(" (Me) ");
        }
      }

      sb.append("</html>");
      s = sb.toString();
      item.setValue(s);
      item.setLinkedData(user);

      if (user.optBoolean("is_provider")) {
        physiciansList.add(item);
      } else {
        othersList.add(item);
      }

      if ((commHandler != null) && (xmppid != null)) {
        users.add(xmppid);
      }
    }

    aListViewer physicians = (aListViewer) fv.getWidget("physicians");
    aListViewer others     = (aListViewer) fv.getWidget("others");

    if (physicians != null) {
      physicians.setAll(physiciansList);
    } else {
      fv.setAttribute("_physiciansList", physiciansList);
    }

    if (others != null) {
      others.setAll(othersList);
    } else {
      fv.setAttribute("_othersList", othersList);
    }

    if (physiciansList.isEmpty() &&!othersList.isEmpty()) {
      TabPaneViewer tv = (TabPaneViewer) Platform.getWindowViewer().getViewer("careteam");

      if (tv != null) {
        tv.setSelectedTabName("others");
      }
    }

    if (!users.isEmpty()) {
      commHandler.addStatusListener(users, this);
    }
  }
}
