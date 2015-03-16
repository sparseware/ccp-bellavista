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
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iDataCollection;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIDimension;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.viewer.ListBoxViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.aListViewer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.external.aCommunicationHandler;
import com.sparseware.bellavista.external.aCommunicationHandler.Status;
import com.sparseware.bellavista.external.aCommunicationHandler.UserStatus;
import com.sparseware.bellavista.external.aCommunicationHandler.iStatusListener;

public class CareTeam implements iEventHandler, iStatusListener {
  protected aCommunicationHandler      commHandler;
  protected Map<String, UserStatus>    userStatuses;
  protected ListBoxViewer              listViewer;
  protected static UserStatus          OFFLINE    = new UserStatus();
  protected Map<Status, iPlatformIcon> statusIcons;
  protected int                        idPosition = 1;
  protected long lastUpdateTime;
  protected int dataTimeout;
  
  public CareTeam() {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("careTeamInfo");
    dataTimeout=info.optInt("dataTimeout", 0)*1000;
    commHandler = Utils.getaCommunicationHandler();
    if (commHandler != null) {
      userStatuses = new HashMap<String, UserStatus>();
      statusIcons = Utils.getStatusIcons();
      idPosition = info.optInt("commIdPosition", 1);
    }
  }

  public void onDispose(String eventName, iWidget widget, EventObject event) {
    if (commHandler != null) {
      commHandler.removeStatusListener(this);
    }
    commHandler = null;
    userStatuses = null;
    listViewer = null;
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {
  }

  protected String getUserID(RenderableDataItem row) {
    RenderableDataItem item = row.getItemEx(idPosition);
    return item == null ? null : (String) item.getValue();
  }

  /**
   * Called when the careteam view is created.
   * We adjust's is size for the screen size
   */
  public void onCreated(String eventName, final iWidget widget, EventObject event) {
    if(!UIScreen.isLargeScreen()) {
      UIDimension size=UIScreen.getUsableSize();
      int n=(int) UIScreen.fromPlatformPixels(Math.max(size.width, size.height));
      if(n<540) {
        Viewer cfg=(Viewer) ((DataEvent)event).getData();
        cfg.bounds.height.setValue("14ln");
      }
      else {
        Viewer cfg=(Viewer) ((DataEvent)event).getData();
        cfg.bounds.height.setValue("18ln");
      }
    }
  }
  
  /**
   * Called when the careteam view is shown.
   * We check the freshness of the data and reload as appropriate
   */
  public void onShown(String eventName, final iWidget widget, EventObject event) {
    final WindowViewer w = Platform.getWindowViewer();
    final iDataCollection dc = w.getDataCollection("careteam");
    if(lastUpdateTime+dataTimeout>System.currentTimeMillis() && dc.isLoaded()) {
      updateForm(widget.getFormViewer(), dc);
    } else {
      aWorkerTask task = new aWorkerTask() {

        @Override
        public void finish(Object result) {
          w.hideWaitCursor();
          if (result != null) {
            Utils.handleError((Throwable) result);
          } else {
            updateForm(widget.getFormViewer(), dc);
          }
        }

        @Override
        public Object compute() {
          try {
            dc.refresh(widget);
            lastUpdateTime=System.currentTimeMillis();
            return null;
          } catch (Exception e) {
            return e;
          }
        }
      };
      w.showWaitCursor();
      w.spawn(task);
    }
  }

  protected void updateForm(iFormViewer fv, iDataCollection dc) {
    Collection<RenderableDataItem> items = dc.getItemData(fv, false);
    int len = (items == null) ? 0 : items.size();

    if (len == 0) {
      return;
    }

    ArrayList<RenderableDataItem> othersList = new ArrayList<RenderableDataItem>();
    ArrayList<RenderableDataItem> physiciansList = new ArrayList<RenderableDataItem>();

    ArrayList<String> users = new ArrayList<String>(len);
    RenderableDataItem row, item;
    StringBuilder sb = new StringBuilder();
    String s;
    JSONObject user = (JSONObject) Platform.getAppContext().getData("user");
    String me = user.optString("xmppid","");
    boolean isme = false;
    Iterator<RenderableDataItem> it = items.iterator();
    while (it.hasNext()) {
      row = it.next();
      if (row.size() < 4) {
        continue;
      }

      final boolean small = UIScreen.isSmallScreen();
      item = row.get(2).copy();
      sb.setLength(0);
      sb.append("<html>").append((String) row.get(3).getValue());
      sb.append(small ? "<br/>- " : " - ");
      sb.append((String) item.getValue());
      if (!isme) {
        isme = me.equals(row.get(0).getValue());

        if (isme) {
          sb.append(" (Me) ");
        }
      }
      sb.append("</html>");
      s = sb.toString();
      item.setValue(s);
      item.setLinkedData(row);
      s = (String) row.get(4).getValue();

      if ("true".equalsIgnoreCase(s)) {
        physiciansList.add(item);
      } else {
        othersList.add(item);
      }

      s = commHandler == null ? null : getUserID(row);

      if (s == null) {
        continue;
      }
      item.setLinkedData(s);
      users.add(s);
    }

    aListViewer physicians = (aListViewer) fv.getWidget("physicians");
    aListViewer others = (aListViewer) fv.getWidget("others");

    if (physicians != null) {
      physicians.setAll(physiciansList);
    }

    if (others != null) {

      others.setAll(othersList);
    }
    if (!users.isEmpty()) {
      commHandler.addStatusListener(users, this);
    }

  }

  public void onListShown(String eventName, iWidget widget, EventObject event) {
    listViewer = (ListBoxViewer) widget;
  }

  public void onListChange(String eventName, iWidget widget, EventObject event) {
    if (commHandler != null) {
      iFormViewer fv = widget.getFormViewer();
      RenderableDataItem item = ((ListBoxViewer) widget).getSelectedItem();
      String id = item == null ? null : (String) item.getLinkedData();
      UserStatus status = id == null ? OFFLINE : userStatuses.get(id);
      if (status == null) {
        status = OFFLINE;
      }
      PushButtonWidget pb = (PushButtonWidget) fv.getWidget("bv.action.video_chat");
      if (pb != null) {
        pb.setEnabled(commHandler.isVideoChatAvailable() && status.videoChat != Status.ONLINE);
      }
      pb = (PushButtonWidget) fv.getWidget("bv.action.audio_chat");
      if (pb != null) {
        pb.setEnabled(commHandler.isVideoChatAvailable() && status.audioChat != Status.ONLINE);
      }
      pb = (PushButtonWidget) fv.getWidget("bv.action.text_chat");
      if (pb != null) {
        pb.setEnabled(commHandler.isVideoChatAvailable() && status.textChat != Status.ONLINE);
      }
    }
  }

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
      ListBoxViewer lv = listViewer;
      int len = lv.size();
      for (int i = 0; i < len; i++) {
        String id = (String) lv.get(i).getLinkedData();
        if (id != null && id.equals(user)) {
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

}
