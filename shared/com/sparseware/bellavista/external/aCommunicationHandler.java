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

package com.sparseware.bellavista.external;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.appnativa.util.IdentityArrayList;
import com.appnativa.util.json.JSONObject;

/**
 * This class provides a base implementation for a communication handler that
 * handle communication between registered users.
 *
 * @author Don DeCoteau
 *
 */
public abstract class aCommunicationHandler {
  protected Map<String, Object> listenerMap;

  public aCommunicationHandler() {}

  /**
   * Adds the listener as interested in receiving status updates for the
   * specified list of users
   *
   * @param users
   *          the users
   * @param listener
   *          the listener
   */
  public void addStatusListener(List<String> users, iStatusListener listener) {
    int len = users.size();

    if ((listenerMap == null) && (len > 0)) {
      listenerMap = new HashMap<String, Object>((len > 10)
              ? len
              : 10);
    }

    for (int i = 0; i < len; i++) {
      String id = users.get(i);

      addStatusListener(id, listener);
    }
  }

  /**
   * Changes the status of the local user
   *
   * @param id the id of the user
   * @param status
   *          the new status
   */
  public abstract void changeLocalUserStatus(String id, UserStatus status);

  /**
   * Gets the status of a user
   *
   * @param id the id of the user
   * @return the user's status
   */
  public abstract UserStatus getUserStatus(String id);

  /**
   * Initiates an audio chat with the specified user
   *
   * @param user the user's profile information
   */
  public void initiateAudioChat(JSONObject user) {}

  /**
   * Initiates a text chat with the specified user
   *
   * @param user the user's profile information
   */
  public void initiateTextChat(JSONObject user) {}

  /**
   * Initiates a video chat with the specified user
   *
   * @param user the user's profile information
   */
  public void initiateVideoChat(JSONObject user) {}

  /**
   * Returns whether of not audio chatting is available
   *
   * @param user the user's profile information
   * @return true if it is; false otherwise
   */
  public abstract boolean isAudioChatAvailable(JSONObject user);

  /**
   * Returns whether of not text chatting is available
   *
   * @param user the user's profile information
   * @return true if it is; false otherwise
   */
  public abstract boolean isTextChatAvailable(JSONObject user);

  /**
   * Returns whether of not video chatting is available
   *
   * @param user the user's profile information
   * @return true if it is; false otherwise
   */
  public abstract boolean isVideoChatAvailable(JSONObject user);

  /**
   * Removes the specified for receiving notifications from any users
   *
   * @param careTeam
   */
  public void removeStatusListener(iStatusListener listener) {
    if (listenerMap != null) {
      Iterator<Entry<String, Object>> it = listenerMap.entrySet().iterator();

      while(it.hasNext()) {
        Entry<String, Object> e = it.next();
        Object                o = e.getValue();

        if (o == listener) {
          it.remove();
          unregisterInterest(e.getKey());
        } else if (o instanceof List) {
          List list = (List) o;

          list.remove(listener);

          if (list.isEmpty()) {
            it.remove();
            unregisterInterest(e.getKey());
          }
        }
      }
    }
  }

  /**
   * Removes the listener from receiving status updates for the specified list
   * of users
   *
   * @param users
   *          the users
   * @param listener
   *          the listener
   */
  public void removeStatusListener(List<String> users, iStatusListener listener) {
    if (listenerMap != null) {
      int len = users.size();

      for (int i = 0; i < len; i++) {
        String id = users.get(i);

        removeStatusListener(id, listener);
      }
    }
  }

  /**
   * Adds a status listener for a user
   *
   * @param id the id of the user
   * @param listener
   *          the listener
   */
  protected void addStatusListener(String id, iStatusListener listener) {
    if (listenerMap == null) {
      Object o = listenerMap.get(id);

      if (o == listener) {
        return;
      } else if (o instanceof List) {
        if (((List) o).indexOf(listener) != -1) {
          return;
        }
      }

      if (o == null) {
        listenerMap.put(id, listener);
      } else if (o instanceof List) {
        ((List) o).add(listener);
      } else {
        IdentityArrayList list = new IdentityArrayList(2);

        list.add(o);
        list.add(listener);
        listenerMap.put(id, list);
      }

      registerInterest(id, o != null);
    }
  }

  /**
   * Notifies listeners of a user's status change
   *
   * @param statuses
   *          the map of status changes
   */
  protected void notifyListeners(Map<String, UserStatus> statuses) {
    if (listenerMap != null) {
      Map<String, Object>                 listeners = listenerMap;
      Iterator<Entry<String, UserStatus>> it        = statuses.entrySet().iterator();

      while(it.hasNext()) {
        Entry<String, UserStatus> e      = it.next();
        String                    user   = e.getKey();
        UserStatus                status = e.getValue();
        Object                    o      = listeners.get(user);

        if (o instanceof iStatusListener) {
          ((iStatusListener) o).statusChanged(this, user, status);
        } else if (o instanceof List) {
          List list = (List) o;
          int  len  = list.size();

          for (int i = 0; i < len; i++) {
            ((iStatusListener) list.get(i)).statusChanged(this, user, status);
          }
        }
      }
    }
  }

  /**
   * Notifies listeners of service availability changes
   *
   */
  protected void notifyOfServiceAvailabilityChange() {
    if (listenerMap != null) {
      Iterator<Object> it       = listenerMap.values().iterator();
      HashSet          notified = new HashSet();

      while(it.hasNext()) {
        Object o = it.next();

        if (o instanceof iStatusListener) {
          iStatusListener l = (iStatusListener) o;

          if (notified.add(l)) {
            l.serviceAvailbilityChanged(this);
          }
        } else if (o instanceof List) {
          List list = (List) o;
          int  len  = list.size();

          for (int i = 0; i < len; i++) {
            iStatusListener l = (iStatusListener) list.get(i);

            if (notified.add(l)) {
              l.serviceAvailbilityChanged(this);
            }
          }
        }
      }
    }
  }

  /**
   * Called when there is a new listener interested in updates about this user
   *
   * @param user
   *          the user
   * @param previouslyRegistered
   *          true if the user had interest previously registered; false if this
   *          is the first interest
   */
  protected void registerInterest(String user, boolean previouslyRegistered) {}

  /**
   * Removes a status listener for a user
   *
   * @param user
   *          the user
   * @param listener
   *          the listener
   */
  protected void removeStatusListener(String user, iStatusListener listener) {
    if (listenerMap == null) {
      Object o = listenerMap.get(user);

      if (o == listener) {
        listenerMap.remove(user);
        unregisterInterest(user);
      } else if (o instanceof List) {
        List list = (List) o;

        list.remove(listener);

        if (list.isEmpty()) {
          listenerMap.remove(user);
          unregisterInterest(user);
        }
      }
    }
  }

  /**
   * Called when there are no listeners interested in updates about this user
   *
   * @param id the id of the user
   */
  protected void unregisterInterest(String id) {}

  /**
   * Status listener interface
   */
  public interface iStatusListener {

    /**
     * Called when the availability of one of the handler's services changes
     *
     * @param handler
     *          the handler that is notifying
     */
    void serviceAvailbilityChanged(aCommunicationHandler handler);

    /**
     * Called to notify a listener that status has changed for a user
     *
     * @param handler
     *          the handler that is notifying
     * @param user
     *          the user
     * @param status
     *          the user's status
     */
    void statusChanged(aCommunicationHandler handler, String user, UserStatus status);
  }


  /**
   * Status enum
   */
  public static enum Status {
    ONLINE, OFFLINE, AWAY, BUSY
  }

  /**
   * Class holding status about a user
   */
  public static class UserStatus {
    public Status videoChat = Status.OFFLINE;
    public Status audioChat = Status.OFFLINE;
    public Status textChat  = Status.OFFLINE;
  }
}
