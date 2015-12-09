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

import com.appnativa.rare.CallbackFunctionCallback;
import com.appnativa.rare.Platform;
import com.appnativa.rare.aFunctionCallback;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.ActionLink.RequestEncoding;
import com.appnativa.rare.net.HTTPException;
import com.appnativa.rare.net.aLinkErrorHandler;
import com.appnativa.rare.net.iURLConnection;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.GridPane;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.ui.ActionBar;
import com.appnativa.rare.ui.FontUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIAction;
import com.appnativa.rare.ui.UICompoundIcon;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.ViewerCreator;
import com.appnativa.rare.ui.aPlatformIcon;
import com.appnativa.rare.ui.effects.PullBackAnimation;
import com.appnativa.rare.ui.effects.ShakeAnimation;
import com.appnativa.rare.ui.effects.SlideAnimation;
import com.appnativa.rare.ui.effects.TransitionAnimator;
import com.appnativa.rare.ui.effects.iAnimator.Direction;
import com.appnativa.rare.ui.effects.iTransitionAnimator;
import com.appnativa.rare.ui.iPlatformGraphics;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.util.Grouper;
import com.appnativa.rare.util.SubItemComparator;
import com.appnativa.rare.viewer.GridPaneViewer;
import com.appnativa.rare.viewer.SplitPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TabPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WidgetPaneViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.aContainer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.Helper;
import com.appnativa.util.IdentityArrayList;
import com.appnativa.util.ObjectHolder;
import com.appnativa.util.SNumber;
import com.appnativa.util.iFilterableList;
import com.appnativa.util.json.JSONObject;

import com.google.j2objc.annotations.Weak;

import com.sparseware.bellavista.ActionPath.iActionPathSupporter;
import com.sparseware.bellavista.Document.DocumentItemType;
import com.sparseware.bellavista.Settings.AppPreferences;
import com.sparseware.bellavista.Settings.Server;
import com.sparseware.bellavista.external.aAttachmentHandler;
import com.sparseware.bellavista.external.aCommunicationHandler;
import com.sparseware.bellavista.external.aCommunicationHandler.Status;
import com.sparseware.bellavista.external.aProtocolHandler;
import com.sparseware.bellavista.service.StreamHandlerFactory;

import java.io.IOException;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLStreamHandler;

import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class Utils {
  public static char                             colSeparator                   = '^';
  public static char                             ldSeparator                    = '|';
  public static char                             riSeparator                    = '~';
  public static String                           DISPOSABLE_STACKENTRY_PROPERTY = "_BV_DISPOSABLE_";
  public static String                           POP_RUNNER_STACKENTRY_PROPERTY = "_BV_POP_RUNNER_";
  static AppPreferences                          preferences;
  static LoginManager                            loginManager;
  static boolean                                 demo;
  static PullBackAnimation                       pullbackAnimation;
  static ShakeAnimation                          shakeAnimation;
  static ActionPath                              actionPath;
  static IdentityArrayList<iActionPathSupporter> actionPathSupporters =
    new IdentityArrayList<ActionPath.iActionPathSupporter>(5);
  static HashMap<Status, iPlatformIcon>    statusIcons;
  static CollectionManager                 collectionManager;
  static JSONObject                        attachmentHandlers;
  static boolean                           wasClosing;
  static ApplicationLock                   applicationLock;
  static aCommunicationHandler             commHandler;
  static Settings                          settingsHandler;
  static ArrayList<StackEntry>             stack = new ArrayList<StackEntry>(3);
  static BackIcon                          actionbarIcon;
  static Map                               dialogOptions = new HashMap(5);
  static Viewer                            reloginConfig;
  static GridPane                          genericContainerCfg;
  static boolean                           ignorePausing;
  static boolean                           shuttingDown;
  static boolean                           fullscreenMode;
  public static boolean                    cardStack;
  static UIFont                            boldFont;
  static TransitionAnimator                popAnimation;
  static iContainer                        titleWidget;
  private static CharSequence              actionBarTitle;
  public static boolean                    googleGlass;
  public static boolean                    reverseFling;
  static HashMap<String, aProtocolHandler> protocolHandlers;
  static DefaultProtocolHandler            defaultProtocolHandler;
  private static boolean                   locking;

  /**
   * This provides generic utilities used throughout the application. It is also
   * responsible for logging in/out a user and locking/unlocking the application
   * as appropriate
   *
   * @author Don DeCoteau
   */
  private Utils() {}

  /**
   * Adds an action path supporter to the stack
   *
   * @param supporter
   *          the supporter to add
   */
  public static void addActionPathSupporter(iActionPathSupporter supporter) {
    actionPathSupporters.remove(supporter);    //only one instance can be on the stack an any given time
    actionPathSupporters.add(supporter);
  }

  public static void alert(Object message) {
    alert(message, false);
  }

  public static void alert(Object message, boolean exit) {
    WindowViewer w = Platform.getWindowViewer();

    if (cardStack) {
      WidgetPaneViewer v = CardStackUtils.createTextCard(w, message.toString(), new Runnable() {
        @Override
        public void run() {
          Utils.popViewerStack();
        }
      });

      if (exit) {
        v.setEventHandler(iConstants.EVENT_DISPOSE, "class:Actions#onExit", true);
      }

      Utils.pushWorkspaceViewer(v);
    } else {
      iFunctionCallback cb = null;

      if (exit) {
        cb = new iFunctionCallback() {
          @Override
          public void finished(boolean canceled, Object returnValue) {
            exitEx();
          }
        };
      }

      Platform.getWindowViewer().alert(message, cb);
    }
  }

  /**
   * Called when the application is paused. We will show the timed out screen
   * but we will not log the user out from the server and we will not remove the
   * patient info. It is up to the server to time us out (the client should not
   * be relied upon)
   */
  public static void applicationPaused() {
    Platform.getAppContext().closePopupWindows(true);

    if (!ignorePausing && (getUserID() != null)) {
      lockApplication(ApplicationLockType.PAUSED, false);
    }
  }

  /**
   * Called when the application resumes after being paused. If we are paused
   * while we were still logged in, we will check with the server to see if the
   * user's session is still valid. If it is we will just resume otherwise we
   * will force the user to re-enter thier password
   */
  public static void applicationResumed() {
    if ((applicationLock == null) || (applicationLock.viewer == null)) {
      return;
    }

    final WindowViewer w  = Platform.getWindowViewer();
    iFunctionCallback  cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        w.hideProgressPopup();

        try {
          if (canceled || (returnValue instanceof Throwable)) {
            applicationLock.disposeViewer();
          } else {
            toggleActions(true);
            handleStatusObject((JSONObject) ((ObjectHolder) returnValue).value);
            applicationLock.restoreViewer();
            updateActionBar();
            applicationLock = null;
          }
        } catch(Exception e) {
          e.printStackTrace();
          exitEx();    // exit with prejudice, forcing the user to restart the app
        }
      }
    };

    try {
      final ActionLink l = w.createActionLink("/hub/main/account/status");

      w.getContentAsJSON(l, cb);
      w.showProgressPopup(w.getString("bv.text.authenticating"));
    } catch(Exception e) {
      applicationLock.disposeViewer();
    }
  }

  public static int calculateAge(Object date) {
    Calendar cal;

    if (date instanceof Date) {
      cal = Calendar.getInstance();
      cal.setTime((Date) date);
    } else {
      cal = (Calendar) date;
    }

    Calendar now = Calendar.getInstance();
    int      age = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);

    if ((cal.get(Calendar.MONTH) > now.get(Calendar.MONTH))
        || ((cal.get(Calendar.MONTH) == now.get(Calendar.MONTH))
            && (cal.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH)))) {
      age--;
    }

    return age;
  }

  /**
   * Called from getReloginPatient to see if it is ok to reselect the current
   * patient. This returns true only if we were forced to relogin because our
   * session timed out. The value is reset after the call.
   *
   * @return
   */
  public static boolean canReselectPatient() {
    return (loginManager != null) && loginManager.canReselectPatient();
  }

  /**
   * Creates a categorizes a list of rows based on the value of a specified
   * column in the specified rows.
   *
   * @param rows
   *          the rows to categorize
   * @param column
   *          the column to categorize on
   * @param missingCategoryItem
   *          the category item for rows that do no have a category specified
   * @param sortOnLinkedData
   *          true to sort the rows using the linked data; false to sort on
   *          value
   *
   * @return the categorized rows for display in a list or combo-box
   */
  public static List<RenderableDataItem> categorize(List<RenderableDataItem> rows, int column,
          RenderableDataItem missingCategoryItem, boolean sortOnLinkedData) {
    int                                 len = rows.size();
    HashMap<String, RenderableDataItem> map = new HashMap<String, RenderableDataItem>();

    if ((len == 1) &&!rows.get(0).isEnabled()) {
      return Collections.EMPTY_LIST;
    }

    for (int i = 0; i < len; i++) {
      RenderableDataItem item = rows.get(i).getItemEx(column);

      if (item != null) {
        String s = item.toString().trim();

        if ((s == null) || (s.length() == 0)) {
          s    = missingCategoryItem.toString();
          item = missingCategoryItem;
        }

        map.put(s, item);
      }
    }

    ArrayList<RenderableDataItem> list = new ArrayList<RenderableDataItem>(map.values());
    SubItemComparator             c    = new SubItemComparator();

    c.setUseLinkedData(sortOnLinkedData);
    Collections.sort(list, c);

    return list;
  }

  /**
   * Creates a categorizes a list of rows based on the value of a specified
   * column in the specified rows.
   *
   * @param rows
   *          the rows to categorize
   * @param column
   *          the column to categorize on
   * @param missingCategoryItem
   *          the category item for rows that do no have a category specified
   * @param sortOnLinkedData
   *          true to sort the rows using the linked data; false to sort on
   *          value
   *
   * @return the categorized rows for display in a list or combo-box
   */
  public static List<RenderableDataItem> categorize(TableViewer table, int column,
          RenderableDataItem missingCategoryItem, boolean sortOnLinkedData) {
    int                                 len = table.size();
    HashMap<String, RenderableDataItem> map = new HashMap<String, RenderableDataItem>();

    if ((len == 1) &&!table.get(0).isEnabled()) {
      return Collections.EMPTY_LIST;
    }

    for (int i = 0; i < len; i++) {
      RenderableDataItem item = table.get(i).getItemEx(column);

      if (item != null) {
        String s = item.toString().trim();

        if ((s == null) || (s.length() == 0)) {
          s    = missingCategoryItem.toString();
          item = missingCategoryItem;
        }

        map.put(s, item);
      }
    }

    ArrayList<RenderableDataItem> list = new ArrayList<RenderableDataItem>(map.values());
    SubItemComparator             c    = new SubItemComparator();

    c.setUseLinkedData(sortOnLinkedData);
    Collections.sort(list, c);

    return list;
  }

  /**
   * This method walks the rows and converts date values only when the string
   * value is different for that of the previous row. If the values are the same
   * then the converted date from the previous row is used as the current rows
   * value. This avoids unnecessary string to date conversions when multiple
   * rows have the same date value
   * <p>
   * It also allows you to specify a custom row value checker.
   * <p>
   *
   * @param rows
   *          the rows to check the table
   * @param dateCol
   *          the column containing the date value
   * @param dateType
   *          the type date/time time of the item
   * @param checker
   *          an optional value checker for the row
   */
  public static void checkRowsAndOptimizeDates(List<RenderableDataItem> rows, int dateCol, int dateType,
          iValueChecker checker) {
    try {
      final int len       = rows.size();
      String    lastValue = null;
      Date      lastDate  = null;

      for (int i = 0; i < len; i++) {
        final RenderableDataItem row   = rows.get(i);
        final RenderableDataItem ditem = row.get(dateCol);
        Object                   value = ditem.getValueEx();

        ditem.setType(dateType);

        if ((lastValue != null) && lastValue.equals(value)) {
          ditem.setValue(lastDate);
        } else {
          lastValue = ditem.toString();
          lastDate  = (Date) ditem.getValue();
        }

        if (checker != null) {
          checker.checkRow(row, i, 0, len);
        }
      }
    } catch(Exception e) {
      Platform.ignoreException(null, e);
    }
  }

  /**
   * This method walks the rows, in reverse, and converts date values only when
   * the string value is different for that of the previous row. If the values
   * are the same then the converted date from the previous row is used as the
   * current rows value. This avoids unnecessary string to date conversions when
   * multiple rows have the same date value
   * <p>
   * It also allows you to specify a custom row value checker. If one is
   * specified and the check method returns false the row will be removed from
   * the list
   * <p>
   *
   * @param rows
   *          the rows to check the table
   * @param dateCol
   *          the column containing the date value
   * @param dateType
   *          the type date/time time of the item
   * @param checker
   *          an optional value checker for the row
   */
  public static void checkRowsInReverseAndOptimizeDates(List<RenderableDataItem> rows, int dateCol, int dateType,
          iValueChecker checker) {
    try {
      final int len       = rows.size();
      String    lastValue = null;
      Date      lastDate  = null;

      for (int i = len - 1; i > -1; i--) {
        final RenderableDataItem row   = rows.get(i);
        final RenderableDataItem ditem = row.get(dateCol);
        Object                   value = ditem.getValueEx();

        ditem.setType(dateType);

        if ((lastValue != null) && lastValue.equals(value)) {
          ditem.setValue(lastDate);
        } else {
          lastValue = ditem.toString();
          lastDate  = (Date) ditem.getValue();
        }

        if (checker != null) {
          if (!checker.checkRow(row, i, 0, len)) {
            rows.remove(i);
          }
        }
      }
    } catch(Exception e) {
      Platform.ignoreException(null, e);
    }
  }

  /**
   * Returns whether or not polling for updates should continue
   *
   * @return true to continue; false toe stop
   */
  public static boolean continuePollingForUpdates() {
    if (isApplicationLocked() || (getPatient() == null)) {
      return false;
    }

    return true;
  }

  /**
   * Creates a disabled table row and sets the value of the specified column to
   * the value of the named resource
   *
   * @param resource
   *          the name of the resource
   * @param resourceColumn
   *          the column to set the value of
   *
   * @return the created row
   */
  public static RenderableDataItem createDisabledTableRow(String resource, int resourceColumn) {
    RenderableDataItem row = Platform.getWindowViewer().createRow(3, true);

    row.setEnabled(false);

    RenderableDataItem item = row.get(1);

    item.setValue(Platform.getResourceAsString(resource));
    item.setEnabled(false);

    return row;
  }

  /**
   * Creates a grid pane viewer for viewing arbitrary content. The
   * {@code generic_container.rml} markup is used to create the viewer
   *
   * @param parent
   *          the parent for the viewer
   * @return the grid pane viewer
   */
  public static GridPaneViewer createGenericContainerViewer(iContainer parent) {
    return (GridPaneViewer) Platform.getWindowViewer().createViewer(parent, genericContainerCfg);
  }

  public static ActionLink createLink(iWidget context, String url) {
    if (context == null) {
      context = Platform.getWindowViewer();
    }

    try {
      return createLink(context, context.getURL(url));
    } catch(MalformedURLException e) {
      handleError(e);

      return null;
    }
  }

  public static ActionLink createLink(iWidget context, String url, boolean rowInfo) {
    if (context == null) {
      context = Platform.getWindowViewer();
    }

    try {
      return createLink(context, context.getURL(url), rowInfo);
    } catch(MalformedURLException e) {
      handleError(e);

      return null;
    }
  }

  public static URLStreamHandler createURLStreamHandler(String protocol) {
    aProtocolHandler ph = (protocolHandlers == null)
                          ? null
                          : protocolHandlers.get(protocol);

    return (ph == null)
           ? null
           : ph.createURLStreamHandler(protocol);
  }

  public static ActionLink createLink(iWidget context, URL url) {
    if (protocolHandlers != null) {
      return getProtocolHelper(url).createLink(context, url);
    }

    return new ActionLink(context, url);
  }

  public static ActionLink createLink(iWidget context, URL url, boolean rowInfo) {
    if (context == null) {
      context = Platform.getWindowViewer();
    }

    ActionLink link = createLink(context, url);

    link = new ActionLink(context, url);
    link.setColumnSeparator(colSeparator);
    link.setLinkedDataSeparator(ldSeparator);

    if (rowInfo) {
      link.setRowInfoSeparator(riSeparator);
    }

    return link;
  }

  public static ActionLink createLink(iWidget context, URL url, String type) {
    if (protocolHandlers != null) {
      return getProtocolHelper(url).createLink(context, url, type);
    }

    return new ActionLink(context, url, type);
  }

  /**
   * Creates and action link for retrieving a patient's photo
   *
   * @param id
   *          the patient's id
   * @param thumbnail
   *          true to create a link for a thumbnail, false for the full photo
   * @return the link
   */
  public static ActionLink createPhotosActionLink(String id, boolean thumbnail) {
    if ((id.indexOf(':') != -1) || (id.indexOf('/') != -1)) {    //we have a url
      return new ActionLink(id);
    }

    JSONObject ps = (JSONObject) Platform.getAppContext().getData("patientSelectInfo");
    String     s  = ps.optString(thumbnail
                                 ? "photosThumbnailsURL"
                                 : "photosURL", null);

    if ((s == null) || (s.length() == 0)) {
      return null;
    }

    return new ActionLink(Functions.format(s, id));
  }

  /**
   * Exits the application, prompting as appropriate
   */
  public static void exit() {
    if (wasClosing) {
      return;
    }

    if (cardStack || (getUserID() == null)) {
      exitEx();

      return;
    }

    final WindowViewer w = Platform.getWindowViewer();

    if (applicationLock != null) {
      int oc = OrderManager.getPendingOrderCount();

      if (oc != 0) {
        iFunctionCallback cb = new iFunctionCallback() {
          public void finished(boolean canceled, Object returnValue) {
            if (canceled || (returnValue == null)) {
              return;
            }

            if (returnValue instanceof Exception) {
              w.handleException((Exception) returnValue);
            }

            if (Boolean.TRUE.equals(returnValue)) {
              exitEx();
            }
          }
        };
        String s = w.getString("bv.oe.text.has_pending_orders_while_locked", oc);

        w.yesNo(null, s, cb);

        return;
      }

      exitEx();

      return;
    }

    w.setCanClose(false);

    if (!OrderManager.canChangePatientOrExit(true, null)) {
      return;
    }

    iFunctionCallback cb = new iFunctionCallback() {
      public void finished(boolean canceled, Object returnValue) {
        if (!canceled && Boolean.TRUE.equals(returnValue)) {
          Platform.invokeLater(new Runnable() {
            @Override
            public void run() {
              exitEx();
            }
          });
        }
      }
    };

    w.yesNo(Platform.getWindowViewer().getString("bv.text.exit_application"), cb);
  }

  public static void exitEx() {
    if (!shuttingDown &&!Platform.isShuttingDown()) {
      shuttingDown = true;

      if (loginManager != null) {
        loginManager.logout();
      }

      WindowViewer w = Platform.getWindowViewer();

      if ((w != null) &&!w.isDisposed()) {
        w.setCanClose(true);
      }

      wasClosing = true;
      Platform.getAppContext().exit();
    }
  }

  /**
   * Filters the specified rows such the the filtered row will only contain the
   * columns for pageStart MIN(pageStart+pageSize,totalCount)
   *
   * @param rows
   *          the rows to filter
   * @param pageStart
   *          the column the is the start of the page
   * @param pageSize
   *          the number of columns that makes up the page
   * @param totalCount
   *          the total number of items in an un-filtered row
   */
  public static void filetToOnlyShowPage(List<RenderableDataItem> rows, int pageStart, int pageSize, int totalCount) {
    int end = Math.min(pageStart + pageSize, totalCount);

    for (int i = 0; i < end; i++) {
      RenderableDataItem                  row   = rows.get(i);
      iFilterableList<RenderableDataItem> items = row.getItems();

      items.unfilter();

      for (int n = pageStart; n < end; n++) {
        items.addIndexToFilteredList(n);
      }
    }
  }

  /**
   * Fixes categorized rows for display in a table
   *
   * @param rows
   * @param font
   *          the font for a categorized row
   * @param appendCounts
   *          true the append the count of items to the category; false
   *          otherwise
   * @param returnCategories
   *          true to return the list of categories; false otherwise
   *
   * @return the list of categories or null
   */
  public static List<String> fixCategorizedRows(List<RenderableDataItem> rows, UIFont font, boolean appendCounts,
          boolean returnCategories) {
    RenderableDataItem item;
    RenderableDataItem nitem;
    RenderableDataItem row;
    ArrayList<String>  categories = null;
    final int          len        = rows.size();

    if (returnCategories) {
      categories = new ArrayList<String>(len);
    }

    for (int i = 0; i < len; i++) {
      row  = rows.get(i);
      item = row.getItem(0);

      String s = (appendCounts || returnCategories)
                 ? item.toString()
                 : null;

      nitem = new RenderableDataItem(item.getValue(), item.getType());

      if (returnCategories) {
        categories.add(s);
      }

      if (appendCounts) {
        s += " (" + item.size() + ")";
        nitem.setType(RenderableDataItem.TYPE_STRING);
        nitem.setValue(s);
      }

      nitem.setFont(font);
      nitem.setColumnSpan(-1);
      row.add(nitem);
      item.setColumnSpan(1);
    }

    return categories;
  }

  public static aCommunicationHandler getaCommunicationHandler() {
    return commHandler;
  }

  /**
   * Gets the current action path
   *
   * @param remove
   *          true to remove the action path; false to just return it
   * @return the current action path
   */
  public static ActionPath getActionPath(boolean remove) {
    ActionPath p = actionPath;

    if (remove) {
      actionPath = null;
    }

    return p;
  }

  public static aAttachmentHandler getAttachmentHandler(Document.DocumentItemType type) {
    String s = type.name().toLowerCase(Locale.US);

    if (attachmentHandlers == null) {
      attachmentHandlers = (JSONObject) Platform.getAppContext().getData("attachmentHandlers");

      if (attachmentHandlers == null) {
        attachmentHandlers = new JSONObject();
      }
    }

    s = attachmentHandlers.optString(s);

    if ((s == null) || (s.length() == 0) || s.equals("default")) {
      if (type == DocumentItemType.IMAGE) {
        return new DefaultImageViewer();
      }
    }

    if (s.indexOf('.') == 0) {
      String pn = Platform.getUIDefaults().getString("Rare.class.defaultPackage");

      if (pn != null) {
        s = pn + "." + s;
      } else {
        s = Utils.class.getPackage().getName() + "." + s;
      }
    }

    return (aAttachmentHandler) Platform.createObject(s);
  }

  public static Server getDefaultServer() {
    return settingsHandler.getDefaultServer();
  }

  /**
   * Get the action path for the currently displayed screen
   *
   * @return the action path for the currently displayed screen
   */
  public static ActionPath getDisplayedActionPath() {
    int len = actionPathSupporters.size();

    if (len == 0) {
      String        patient = getPatientID();
      TabPaneViewer tp      = (patient == null)
                              ? null
                              : (TabPaneViewer) Platform.getWindowViewer().getViewer("applicationTabs");

      if (tp != null) {
        return new ActionPath(patient, tp.getSelectedTabName());
      }

      return null;
    }

    return actionPathSupporters.get(len - 1).getDisplayedActionPath();
  }

  /**
   * Gets the bold font used by list type widgets.
   *
   * @param widget
   *          a list widget
   * @return the bold font
   */
  public static UIFont getListWidgetBoldFont(iWidget widget) {
    if (boldFont == null) {
      UIFont f = widget.getFont();

      if (f == null) {
        f = FontUtils.getDefaultFont();
      }

      boldFont = widget.getFont().deriveBold();
    }

    return boldFont;
  }

  public static JSONObject getPatient() {
    return (JSONObject) Platform.getAppContext().getData("patient");
  }

  public static String getPatientID() {
    JSONObject o = (JSONObject) Platform.getAppContext().getData("patient");

    return (o == null)
           ? null
           : o.optString("id", null);
  }

  public static AppPreferences getPreferences() {
    return preferences;
  }

  /**
   * Get the split pane viewer at the top of the widgets hierarchy. Only a split
   * pane that is is a the direct child of a tab pane viewer will be returned.
   *
   * @param widget
   *          the widget
   * @return the split pane viewer or null
   */
  public static SplitPaneViewer getSplitPaneViewer(iWidget widget) {
    if (widget instanceof SplitPaneViewer) {
      return (SplitPaneViewer) widget;
    }

    iContainer p = widget.getParent();

    while(p != null) {
      if ((p instanceof SplitPaneViewer) && (p.getParent() instanceof TabPaneViewer)) {
        return (SplitPaneViewer) p;
      }

      p = (aContainer) p.getParent();
    }

    return null;
  }

  public static StackPaneViewer getStackPaneViewer(iWidget widget) {
    if (widget instanceof StackPaneViewer) {
      return (StackPaneViewer) widget;
    }

    iContainer p = widget.getParent();

    while(p != null) {
      if (p instanceof StackPaneViewer) {
        return (StackPaneViewer) p;
      }

      p = p.getParent();
    }

    return null;
  }

  public static HashMap<Status, iPlatformIcon> getStatusIcons() {
    if (statusIcons == null) {
      statusIcons = new HashMap<aCommunicationHandler.Status, iPlatformIcon>(4);
      statusIcons.put(Status.OFFLINE, Platform.getResourceAsIcon("bv.icon.status_offline"));
      statusIcons.put(Status.ONLINE, Platform.getResourceAsIcon("bv.icon.status_online"));
      statusIcons.put(Status.AWAY, Platform.getResourceAsIcon("bv.icon.status_away"));
      statusIcons.put(Status.BUSY, Platform.getResourceAsIcon("bv.icon.status_busy"));
    }

    return statusIcons;
  }

  public static String getUserDisplayName() {
    JSONObject map = (JSONObject) Platform.getAppContext().getData("user");

    return map.getString("name");
  }

  public static String getUserID() {
    return (String) Platform.getAppContext().getData("username");
  }

  /**
   * Gets the size of the viewer stack
   *
   * @return the size of the viewer stack
   */
  public static int getViewerStackSize() {
    return stack.size();
  }

  /**
   * Clears the viewer stack
   *
   * @return true if the stack was cleared false if a viewer prevented the stack from being cleared
   */
  public static boolean clearViewerStack() {
    int vsize = Utils.getViewerStackSize();

    while(vsize > 0) {
      if (!Utils.popViewerStack()) {
        return false;
      }

      vsize = Utils.getViewerStackSize();
    }

    return true;
  }

  public static List<RenderableDataItem> groupByDate(TableViewer widget, List<RenderableDataItem> rows) {
    final int len = rows.size();

    for (int i = 0; i < len; i++) {
      // we need to set the data type of the first column before calling the
      // grouper
      // because we haven't added the values to the table yet (the table would
      // normally handle this);
      rows.get(i).get(0).setType(RenderableDataItem.TYPE_DATETIME);
    }

    Grouper g = new Grouper(new int[] { 0 }, null, true, true);

    g.setFormatForTable(true);
    g.setSortOrder(-1);
    rows = g.group(widget, rows);

    UIFont f = getListWidgetBoldFont(widget);

    fixCategorizedRows(rows, f, false, false);

    return rows;
  }

  public static List<RenderableDataItem> groupRows(TableViewer widget, List<RenderableDataItem> rows, int column,
          int sortOrder, boolean appendCounts) {
    Grouper g = new Grouper(new int[] { column }, null, true, true);

    g.setFormatForTable(true);
    g.setSortOrder(sortOrder);
    rows = g.group(widget, rows);

    UIFont f = getListWidgetBoldFont(widget);

    fixCategorizedRows(rows, f, appendCounts, false);

    return rows;
  }

  /**
   * Handles an action path. This is meant to be called after the user has
   * logged in
   *
   * @param path
   *          the path to handle
   *
   * @return true if action was taken; false otherwise
   */
  public static boolean handleActionPath(ActionPath path) {
    actionPath = path;

    WindowViewer w = Platform.getWindowViewer();
    String       s = path.shiftPeek();

    if (SNumber.isNumeric(s)) {
      PatientSelect.changePatient(w, actionPath);

      return true;
    } else {
      TabPaneViewer tv = (TabPaneViewer) w.getViewer("applicationTabs");

      if (tv != null) {
        path.shift();
        tv.setSelectedTabName(s);

        return true;
      }

      return false;
    }
  }

  public static void handleError(Throwable ex) {
    if ((ex instanceof ClosedChannelException) || (ex instanceof ClosedByInterruptException)) {    //we forcibly closed the connection (most likely logging out)
      return;
    }

    final WindowViewer w = Platform.getWindowViewer();
    final Throwable    e = ApplicationException.pealException(ex);

    Platform.runOnUIThread(new Runnable() {
      @Override
      public void run() {
        if (applicationLock != null) {
          return;
        }

        if (e instanceof MessageException) {
          w.alert(((MessageException) e).getMessage());

          return;
        }

        if (e instanceof PollingConnectionRefused) {
          lockApplication(ApplicationLockType.UNAVAILABLE, true);

          return;
        }

        if (e instanceof ConnectException) {
          lockApplication(ApplicationLockType.UNAVAILABLE, true);
          w.alert(w.getString("bv.text.connection_refused"));

          return;
        }

        if (e instanceof HTTPException) {
          HTTPException he   = (HTTPException) e;
          int           code = he.getStatusCode();

          switch(code) {
            case 401 :    // unauthorized
            case 504 :    // gateway timed out
              // if orders are pending make sure they are preserved
              lockApplication(true);

              return;

            case 503 :    // gateway timed out
              // if orders are pending make sure they are preserved
              lockApplication(ApplicationLockType.UNAVAILABLE, true);

              return;

            case 403 :     // forbidden
              exitEx();    // exit with prejudice as access has be denied

              return;

            case 303 :     // forbidden
              String msg = he.getMessageBody();

              if (msg.length() > 0) {
                w.alert(msg);

                return;
              }

              return;

            case 409 :     // conflict
              // should have be handled by the requestor as this would mean that
              // a put request failed due to a lock or some other inconsistant
              // state
              break;
          }
        }

        Platform.ignoreException(null, e);
        alert(e, !demo);
      }
    });
  }

  public static void handleStatusObject(JSONObject status) {}

  /**
   * Returns whether or not the application is currently locked
   *
   * @return whether or not the application is currently locked
   */
  public static boolean isApplicationLocked() {
    return applicationLock != null;
  }

  public static boolean isCardStack() {
    return cardStack;
  }

  /**
   * Gets whether the client supports the specified server protocol
   *
   * @param protocol
   *          the the URL protocol
   * @return true if it does; false otherwise
   */
  public static boolean isServerProtocolSupported(String protocol) {
    if (protocol.equals("https") || protocol.equals("http")) {
      return true;
    }

    if (protocolHandlers != null) {
      return protocolHandlers.get(protocol) != null;
    }

    return false;
  }

  /**
   * Returns whether the specified value is chartable. A value is chartable if
   * it contains a numeric value.
   *
   * @param row
   *          the table row
   * @return true if the row is chartable; false otherwise
   */
  public static boolean isChartable(String value) {
    char c = (value == null)
             ? 0
             : value.charAt(0);

    if (c == '-') {
      if (value.length() == 1) {
        return false;
      }

      c = value.charAt(1);
    }

    return Character.isDigit(c);
  }

  public static boolean isDemo() {
    return demo;
  }

  public static boolean isGoogleGlass() {
    return googleGlass;
  }

  public static boolean isLoggingOff() {
    return (loginManager != null) && loginManager.loggingOff;
  }

  public static boolean isReloggingIn() {
    return (loginManager != null) && loginManager.reloggingIn;
  }

  public static boolean isReverseFling() {
    return reverseFling;
  }

  public static boolean isShuttingDown() {
    return shuttingDown || Platform.isShuttingDown();
  }

  public static boolean isLocking() {
    return locking;
  }

  /**
   * Lock's the application preventing it form being used without entering the
   * correct password.
   *
   * @param timedout
   *          true if this lock is due to a server timeout; false otherwise
   */
  public static void lockApplication(boolean timedout) {
    lockApplication(timedout
                    ? ApplicationLockType.TIMEOUT
                    : ApplicationLockType.PAUSED, true);
  }

  /**
   * Removes the current viewer from the workspace and redisplays the viewer
   * that was previously displayed
   *
   * @return true if there was a viewer to pop; false otherwise
   */
  public static boolean popViewerStack() {
    return popViewerStack(false);
  }

  /**
   * Removes the current viewer from the workspace and redisplays the viewer
   * that was previously displayed
   *
   * @param ignorePopRunner true to ignore any 'pop' runnable defined for the viewer; false otherwise
   * @return true if there was a viewer to pop; false otherwise
   */
  public static boolean popViewerStack(boolean ignorePopRunner) {
    int len = stack.size();

    if (len == 0) {
      return false;
    }

    StackEntry   e = stack.get(len - 1);
    WindowViewer w = Platform.getWindowViewer();
    iTarget      t = e.target;
    iViewer      v = t.getViewer();

    if (v != null) {
      Runnable r = (Runnable) v.getAttribute(POP_RUNNER_STACKENTRY_PROPERTY);

      if (r != null) {
        if (!ignorePopRunner) {
          r.run();

          return false;
        }

        v.removeAttribute(POP_RUNNER_STACKENTRY_PROPERTY);
      }
    }

    stack.remove(len - 1);
    actionbarIcon.decrement();

    ActionBar ab = w.getActionBar();

    if (e.viewer == null) {
      e.createViewerAndCallpopWorkspaceViewer();
    } else {
      ab.setTitleIcon(actionbarIcon);

      if (actionbarIcon.getBacks() == 0) {
        if (UIScreen.isLargeScreen()) {
          ab.setTitle(w.getTitle());
        }
      }

      iViewer ov = t.setViewer(e.viewer);

      if (isCardStack()) {
        CardStackUtils.updateTitle(e.viewer, true);
      }

      if ((ov != null) && ov.isAutoDispose()) {
        ov.dispose();
      }

      t = w.getTarget("patient_info");

      if (t != null) {
        t.setVisible(!fullscreenMode);
      }
    }

    ab.revalidate();

    return true;
  }

  /**
   * Displays a new viewer in the workspace while saving the previous viewer
   *
   * @param v
   *          the viewer
   */
  public static void pushWorkspaceViewer(iViewer v) {
    pushWorkspaceViewer(v, false, null);
  }

  /**
   * Performs a callback
   *
   * @param cb
   *          the call back to invoke
   * @param canceled
   *          the canceled value
   * @param returnValue
   *          the return value
   */
  public static void performCallback(final iFunctionCallback cb, final boolean canceled, final Object returnValue) {
    if (cb != null) {
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          cb.finished(canceled, returnValue);
        }
      });
    }
  }

  /**
   * Displays a new viewer in the workspace while saving the previous viewer
   *
   * @param v
   *          the viewer
   * @param disposible
   *          true if the specified can be disposed when not being displayed;
   *          false otherwise
   */
  public static void pushWorkspaceViewer(iViewer v, boolean disposible) {
    pushWorkspaceViewer(v, disposible, null);
  }

  /**
   * Displays a new viewer in the workspace while saving the previous viewer
   *
   * @param v
   *          the viewer
   * @param disposible
   *          true if the specified can be disposed when not being displayed;
   *          false otherwise
   *
   * @param popRunner a runner to call to pop the viewer off the stack
   */
  public static void pushWorkspaceViewer(iViewer v, boolean disposible, Runnable popRunner) {
    iTarget t = Platform.getWindowViewer().getTarget(iTarget.TARGET_WORKSPACE);

    pushViewer(v, t, disposible, popRunner);
  }

  /**
   * Displays a new viewer in the specified target while saving the previous viewer
   *
   * @param v
   *          the viewer
   * @param t the target for the viewer
   * @param disposible
   *          true if the specified can be disposed when not being displayed;
   *          false otherwise
   *
   * @param popRunner a runner to call to pop the viewer off the stack
   */
  public static void pushViewer(iViewer v, iTarget t, boolean disposible, Runnable popRunner) {
    WindowViewer w  = Platform.getWindowViewer();
    ActionBar    ab = w.getActionBar();

    if (popRunner != null) {
      v.setAttribute(POP_RUNNER_STACKENTRY_PROPERTY, popRunner);
    }

    if (actionbarIcon.getBacks() == 0) {
      if (UIScreen.isLargeScreen() && (t == w.getWorkspaceViewer().getTarget())) {
        ab.setTitle((CharSequence) Platform.getAppContext().getData("pt_name_age_sex"));
      }
    }

    iViewer    ov = t.removeViewer();
    StackEntry e  = new StackEntry(ov, t);

    stack.add(e);
    actionbarIcon.increment();
    ab.setTitleIcon(actionbarIcon);

    if (Utils.isCardStack()) {
      CardStackUtils.updateTitle(v, false);
    }

    if (disposible) {
      v.setAttribute(DISPOSABLE_STACKENTRY_PROPERTY, true);
    }

    t.setTransitionAnimator(null);

    if (v == null) {
      v = null;
    }

    t.setViewer(v);
    ab.revalidate();
  }

  /**
   * Displays a new viewer in the workspace while saving the previous viewer
   *
   * @param url
   *          the url to use to create the viewer
   */
  public static void pushWorkspaceViewer(String url) throws MalformedURLException {
    pushWorkspaceViewer(url, false, null);
  }

  /**
   * Displays a new viewer in the workspace while saving the previous viewer
   *
   * @param url
   *          the url to use to create the viewer
   * @param disposible
   *          true if the specified can be disposed when not being displayed;
   *          false otherwise
   * @param popRunner runner to invoke when the viewer is poped of the stack
   */
  public static void pushWorkspaceViewer(final String url, final boolean disposable, final Runnable popRunner)
          throws MalformedURLException {
    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (returnValue instanceof Throwable) {
          handleError((Throwable) returnValue);
        } else if (!canceled) {
          pushWorkspaceViewer((iViewer) returnValue, disposable, popRunner);
        }
      }
    };

    Platform.getWindowViewer().createViewer(url, cb);
  }

  /**
   * Removes an action path supporter
   *
   * @param supporter
   *          the supporter to remove
   */
  public static void removeActionPathSupporter(iActionPathSupporter supporter) {
    actionPathSupporters.remove(supporter);
  }

  public static void resetActionBar() {
    if (actionBarTitle != null) {
      ActionBar ab = Platform.getWindowViewer().getActionBar();

      ab.setTitle(actionBarTitle);
      ab.setTitleIcon(null);
      ab.setTitleIcon(actionbarIcon);
      actionbarIcon.showPatientIcon(false);

      if (cardStack) {
        CardStackUtils.clearTitle();
      } else {
        ab.setSecondaryTitle((iWidget) null);
      }
    }
  }

  public static void resignIn(final iWidget context, String password) {
    loginManager.relogin(context, password);
  }

  public static String resolveClassName(String cls, String addon) {
    if (cls.indexOf('.') == -1) {
      String pkg = Platform.getUIDefaults().getString("Rare.class.defaultPackage");

      if (pkg == null) {
        pkg = Utils.class.getPackage().getName();
      }

      if (addon != null) {
        cls = pkg + "." + addon + "." + cls;
      } else {
        cls = pkg + "." + cls;
      }
    }

    return cls;
  }

  /**
   * Sets the current action path.
   *
   * @see #getActionPath(boolean)
   *
   * @param path
   *          the path
   */
  public static void setActionPath(ActionPath path) {
    actionPath = path;
  }

  /**
   * Sets the first tab to select after the user selects a patient
   *
   * @param name
   *          the name of the tab
   */
  public static void setFirstSelectedTab(String name) {
    setActionPath(new ActionPath(name));
  }

  /**
   * Sets the ignore pausing flag.
   *
   * This method should only be called if you are about to initiate an action
   * that can cause the O/S to pause the application (like bringing another
   * application to the foreground to handle a request) and you don't want the
   * standard pausing logic to be executed.
   *
   * @param ignore
   *          true to ignore OS pausing action; false otherwise
   */
  public static void setIgnorePausing(boolean ignore) {
    ignorePausing = ignore;
  }

  /**
   * Switches from one viewer in a stack pane to another with animation. The
   * stack pane is expected to only contain 2 viewers max that you are switching
   * back and forth between. If there are two viewers already in the pane that
   * one will be replaced
   *
   * @param sp
   *          the stack pane
   * @param the
   *          viewer to set
   * @param ta
   *          the animation to use for the switch
   * @param forward
   *          true if the direction of the switch if forward; false for backward
   * @param horizontal
   *          true if for horizontal animation; false for vertical
   * @param disposeOld
   *          true to dispose the old viewer after the animation completes;
   *          false otherwise
   */
  public static void setViewerInStackPaneViewer(final StackPaneViewer sp, iViewer viewer, iTransitionAnimator ta,
          boolean forward, boolean horizontal, boolean disposeOld) {
    int n   = sp.getActiveViewerIndex();
    int len = sp.size();

    if (len < 2) {
      sp.addViewer(null, viewer);
    } else {
      if (n == -1) {
        n = 1;
      }

      iViewer ov = sp.setViewer(1 - n, viewer);

      if (ov != null) {
        ov.dispose();
      }
    }

    if (n == -1) {
      sp.setTransitionAnimator((iTransitionAnimator) null);
      n = 0;
    } else {
      sp.setTransitionAnimator(ta);

      if (ta != null) {
        ((SlideAnimation) ta.getInAnimator()).setHorizontal(horizontal);
      }

      if (forward) {
        if (n == 1) {
          sp.swap(0, 1);
        }

        n = 1;
      } else {
        if (n == 0) {
          sp.swap(0, 1);
        }

        n = 0;
      }
    }

    if (isCardStack()) {
      CardStackUtils.updateTitle(viewer, true);
    }

    if (disposeOld) {    //saves memory
      iFunctionCallback cb = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          if (!sp.isDisposed()) {
            int     n = 1 - sp.getActiveViewerIndex();
            iViewer v = sp.removeViewer(n);

            if (v != null) {
              v.dispose();
            }
          }
        }
      };

      sp.switchTo(n, cb);
    } else {
      sp.switchTo(n);
    }
  }

  public static void showDialog(iViewer v, boolean decorated, boolean opaque) {
    final WindowViewer w   = v.getAppContext().getWindowViewer();
    Map                map = dialogOptions;

    map.clear();
    map.put("opaque", opaque);
    map.put("decorated", decorated);
    map.put("title", v.getTitle());

    try {
      stopRealtimeUpdates();
      v.showAsDialog(map);
    } catch(Exception e) {
      w.handleException(e);
    }
  }

  public static void showDialog(String url, final boolean decorated, final boolean opaque) {
    try {
      WindowViewer w    = Platform.getWindowViewer();
      ActionLink   link = w.createActionLink(url);

      ViewerCreator.createViewer(w, link, new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          Platform.getWindowViewer().hideWaitCursor();

          if (returnValue instanceof Throwable) {
            handleError((Throwable) returnValue);
          } else if (!canceled) {
            showDialog((iViewer) returnValue, decorated, opaque);
          }
        }
      });
      Platform.getWindowViewer().showWaitCursor();
    } catch(IOException e) {    // should not happen
      handleError(e);
    }
  }

  public static void showMainView() {
    iPlatformAppContext app     = Platform.getAppContext();
    JSONObject          patient = (JSONObject) app.getData("patient");

    if (patient != null) {
      String id   = (String) patient.optString("id", null);
      String name = (String) patient.optString("name", null);

      if ((id != null) && (name != null)) {
        final WindowViewer w  = Platform.getWindowViewer();
        iFunctionCallback  cb = new iFunctionCallback() {
          @Override
          public void finished(boolean canceled, Object returnValue) {
            if (returnValue instanceof Exception) {
              Utils.handleError((Throwable) returnValue);
            } else {
              Utils.toggleActions(true);

              iViewer v = (iViewer) returnValue;

              w.activateViewer(v, iTarget.TARGET_WORKSPACE);
            }
          }
        };

        try {
          JSONObject map = (JSONObject) Platform.getAppContext().getData("user");
          String     s   = map.optString("rmlMarkupFile", "physician.rml");

          w.createViewer(s, cb);
        } catch(Exception e) {
          Utils.handleError(e);
        }

        return;
      }
    }

    PatientSelect.changePatient(app.getMainWindowViewer(), null);
  }

  /**
   * Causes a pull-back animation on a viewer
   *
   * @param viewer
   *          the viewer
   * @param horizontal
   *          true for a horizontal animation; false for a vertical one
   * @param topLeft
   *          true if the pull back is from the top or left; false if from the
   *          bottom or right
   */
  public static void showPullBackAnimation(iViewer viewer, boolean horizontal, boolean topLeft) {
    if (pullbackAnimation == null) {
      pullbackAnimation = new PullBackAnimation(true);
    }

    PullBackAnimation pa = pullbackAnimation;

    if (pa.isRunning()) {
      pa = new PullBackAnimation();
    }

    pa.setHorizontal(horizontal);
    pa.setDirection(topLeft
                    ? Direction.BACKWARD
                    : Direction.FORWARD);
    viewer.animate(pa, null);
  }

  /**
   * Causes a shake animation on a viewer
   *
   * @param viewer
   *          the viewer
   */
  public static void showShakeAnimation(iViewer viewer) {
    if (shakeAnimation == null) {
      shakeAnimation = new ShakeAnimation();
    }

    ShakeAnimation sa = shakeAnimation;

    if (sa.isRunning()) {
      sa = new ShakeAnimation();
    }

    viewer.animate(sa, null);
  }

  public static void signIn(final iWidget context, String username, String password, Server server) {
    if (password != null) {
      password = password.trim();
    }

    if (username != null) {
      username = username.trim();
    }

    String href = server.serverURL;

    if (!href.endsWith("/")) {
      href += "/";
    }

    final WindowViewer w = Platform.getWindowViewer();

    if ("other".equals(href) && (context.getFormViewer().getWidget("url") != null)) {
      href = context.getFormViewer().getWidget("url").getValueAsString();
    }

    String title;
    int    n = href.indexOf('^');

    if (n != -1) {
      title = w.getString(href.substring(n + 1));
      href  = href.substring(0, n);
    } else {
      title = w.getString("bv.text.welcome");
    }

    String root = "";
    URL    url  = null;

    if ((href != null) && (href.length() > 0) &&!href.startsWith("file:") &&!"local/".equalsIgnoreCase(href)) {
      if (!href.contains("/demo") &&!href.contains("/BellaVista-android/assets/")) {
        if ((password == null) || (password.length() < 4)) {
          w.alert(w.getString("bv.text.missing_password"));

          return;
        }

        if ((username == null) || (username.length() < 4)) {
          w.alert(w.getString("bv.text.missing_username"));

          return;
        }
      } else {
        demo = true;
      }

      try {
        url  = new URL(href);    //server utl
        root = url.getFile();
        n    = root.lastIndexOf('/');

        if (n == -1) {
          root = "";
        } else if (n > 0) {
          root = root.substring(0, n);
        }
        //root is root path for the server (e.g. if the url was https://ccp/mydomain/cerner the root would be cerner)
      } catch(Exception e) {
        w.alert(w.getString("bv.text.invalid_server"));

        return;
      }
    } else {
      demo = true;
    }

    loginManager = new LoginManager(context, url, root, server, title);
    loginManager.login(username, password);
  }

  public static void sortTable(TableViewer table, int sortPosition) {
    List<RenderableDataItem> rows = table.getRawRows();
    final int                len  = rows.size();
    SubItemComparator        c    = new SubItemComparator();

    c.setColumn(sortPosition);

    for (int i = 0; i < len; i++) {
      RenderableDataItem item = rows.get(i).getItemEx(0);

      if (item != null) {
        item.sort(c);
      }
    }

    table.refreshItems();
  }

  public static void stopRealtimeUpdates() {}

  /**
   * Switches from one viewer in a stack pane to another with animation. The
   * stack pane is expected to have 2 viewers that you are switching back and
   * forth between,
   *
   * @param sp
   *          the stack pane
   * @param ta
   *          the animation to use for the switch
   * @param forward
   *          true if the direction of the switch if forware; false for backward
   */
  public static void switchStackPaneViewer(StackPaneViewer sp, iTransitionAnimator ta, boolean forward) {
    int n = sp.getActiveViewerIndex();

    if (n == -1) {
      sp.setTransitionAnimator((iTransitionAnimator) null);
      n = 0;
    } else {
      sp.setTransitionAnimator(ta);

      if (forward) {
        if (n == 1) {
          sp.swap(0, 1);
          n = 1;
        }
      } else {
        if (n == 0) {
          sp.swap(0, 1);
          n = 0;
        }
      }
    }

    sp.switchTo(n);
  }

  public static void switchToTab(String name) {}

  public static void toggleActions(boolean enabled) {
    iPlatformAppContext app = Platform.getAppContext();
    UIAction            a   = app.getAction("bv.action.lock");

    if ((a != null) &&!a.isEnabled()) {
      a.setEnabled(enabled);
    }

    a = app.getAction("bv.action.preferences");

    if ((a != null) && ((applicationLock != null) ||!a.isEnabled())) {
      a.setEnabled(enabled);
    }

    a = app.getAction("bv.action.change_patient");

    if (a != null) {
      a.setEnabled(enabled);
    }

    a = app.getAction("bv.action.new_orders");

    if (a != null) {
      a.setEnabled(enabled);
    }

    a = app.getAction("bv.action.print");

    if (a != null) {
      a.setEnabled(enabled);
    }

    a = app.getAction("bv.action.help");

    if ((a != null) &&!a.isEnabled()) {
      a.setEnabled(enabled);
    }
  }

  public static void toggleFullScreen(boolean full) {
    WindowViewer w  = Platform.getWindowViewer();
    ActionBar    ab = w.getActionBar();

    if (full) {
      if (!Platform.getPlatform().isDesktop()) {
        Platform.setUseFullScreen(true);
      }


      iTarget t = w.getTarget("patient_info");

      if (t != null) {    //can be null if we have another viewer loaded in the workspace
        t.setVisible(false);
      }

      ab.setTitle((CharSequence) w.getAppContext().getData("pt_name"));
    } else {
      if (!Platform.getPlatform().isDesktop()) {
        Platform.setUseFullScreen(false);
      }

      iTarget t = w.getTarget("patient_info");

      if (t != null) {
        t.setVisible(true);
      }

    }

    fullscreenMode = full;
  }

  public static void updateActionBar() {
    if (!UIScreen.isLargeScreen()) {
      WindowViewer w  = Platform.getWindowViewer();
      ActionBar    ab = w.getActionBar();

      if (actionBarTitle == null) {
        actionBarTitle = isCardStack()
                         ? ""
                         : ab.getTitleComponent().getText();
      }

      ab.setVisible(true);

      if (Utils.getPatient() != null) {
        actionbarIcon.patientIcon = (iPlatformIcon) Platform.getAppContext().getData("pt_thumbnail");
        actionbarIcon.showPatientIcon(true);
        ab.setTitleIcon(null);
        ab.setTitleIcon(actionbarIcon);

        if (isCardStack()) {
          ab.setTitle("");
          titleWidget.getWidget("title").reset();
          ab.setSecondaryTitle(titleWidget);
        } else {
          ab.setTitle(Platform.getAppContext().getData("pt_name").toString());
        }
      } else if (isCardStack()) {
        ab.setTitle("");

        iViewer v = Platform.getWindowViewer().getTarget(iTarget.TARGET_WORKSPACE).getViewer();

        CardStackUtils.updateTitle(v, true);
      }
    }
  }

  private static aProtocolHandler getProtocolHelper(URL url) {
    aProtocolHandler ph = (protocolHandlers == null)
                          ? null
                          : protocolHandlers.get(url.getProtocol());

    return (ph == null)
           ? defaultProtocolHandler
           : ph;
  }

  /**
   * Locks the application removing the currently displayed information from the
   * screen
   *
   * @param type
   *          the lock type
   * @param logout
   *          true to log the user out; false to keep then logged in and let the
   *          server time them out
   */
  static void lockApplication(ApplicationLockType type, boolean logout) {
    if (applicationLock != null) {
      return;
    }

    locking = true;

    try {
      WindowViewer w = Platform.getWindowViewer();
      OrderManager.lockingApplication();
      while(popViewerStack(true));
      applicationLock = new ApplicationLock(type);
      Utils.toggleActions(false);

      UIAction a = w.getAction("bv.action.lock");

      if (a != null) {
        a.setEnabled(false);
      }


      if (logout) {
        PatientSelect.clearoutPatientCentricInfo();
        loginManager.logout();
      } else {
        resetActionBar();
      }

      iContainer      rv     = (iContainer) w.createViewer(w, reloginConfig);
      StackPaneViewer panel  = (StackPaneViewer) rv.getWidget("reloginPanel");
      iWidget         reason = ((iContainer) panel.getViewer(0)).getWidget("reasonLabel");

      switch(type) {
        case TIMEOUT :
          reason.setValue(w.getString("bv.text.client_timedout"));

          break;

        case UNAVAILABLE :
          reason.setValue(w.getString("bv.text.service_unavailable"));

          break;

        default :
          break;
      }

      iTarget t = w.getTarget(iTarget.TARGET_WORKSPACE);
      iViewer v = t.setViewer(rv);

      if (logout) {
        v.dispose();
      } else {
        panel.switchTo(1);
        applicationLock.viewer = v;
      }
    } finally {
      locking = false;
    }
  }

  /**
   * This class represents a compound icon used for displaying an icon with a
   * badge
   *
   * @author Don DeCoteau
   */
  public static class BadgeCompoundIcon extends UICompoundIcon {
    public BadgeCompoundIcon(iPlatformIcon firstIcon, iPlatformIcon secondIcon) {
      super(firstIcon, secondIcon);
    }

    @Override
    public void paint(iPlatformGraphics g, float x, float y, float width, float height) {
      icons[0].paint(g, x, y, width, height);

      int w = icons[1].getIconWidth();

      icons[1].paint(g, x + width - w, 0, w, w);
    }
  }


  /**
   * This class represents and entry for a viewer on the workspace stack.
   *
   * @author Don DeCoteau
   */
  public static class StackEntry implements iFunctionCallback {
    iViewer    viewer;
    ActionLink viewerLink;
    boolean    disposable;
    iTarget    target;

    /**
     * Creates a new instance.
     *
     * @param viewer
     *          the viewer
     * @param t
     */
    public StackEntry(iViewer viewer, iTarget t) {
      super();

      if (Boolean.TRUE.equals(viewer.getAttribute(DISPOSABLE_STACKENTRY_PROPERTY))) {
        ActionLink l = viewer.getViewerActionLink();

        if (l != null) {
          viewerLink = l;
          viewer.dispose();
          viewer = null;
        }
      }

      this.target = t;
      this.viewer = viewer;
    }

    /**
     * The popWorkspaceViewer method call the method when the viewer field for
     * this entry is null. A null viewer field means that the viewer needs to be
     * recreated from a link.
     */
    public void createViewerAndCallpopWorkspaceViewer() {
      final WindowViewer w = Platform.getWindowViewer();

      try {
        w.showWaitCursor();
        w.createViewer(viewerLink, this);
      } catch(MalformedURLException e) {
        w.hideWaitCursor();
        handleError(e);
      }
    }

    @Override
    public void finished(boolean canceled, Object returnValue) {
      Platform.getWindowViewer().hideWaitCursor();

      if (returnValue instanceof Throwable) {
        handleError((Throwable) returnValue);
      } else if (!canceled) {
        viewer = ((iViewer) returnValue);
        popViewerStack();
      }
    }
  }


  static class AlphaAnimator implements Runnable {
    iWidget widget;
    String  dataKey;
    float   alpha;
    boolean up;
    float   increment = 0.1f;
    float   min       = 0.3f;

    public AlphaAnimator(iWidget widget, String dataKey) {
      this.widget  = widget;
      this.dataKey = dataKey;
      this.alpha   = 1;
    }

    @Override
    public void run() {
      if (widget.isDisposed()) {
        return;
      }

      if (Platform.getAppContext().getData(dataKey) != null) {
        alpha = 1;
      } else {
        if (alpha >= 1) {
          up    = false;
          alpha = 1 - increment;
        } else if (alpha <= min) {
          up    = true;
          alpha = min + increment;
        } else {
          if (up) {
            alpha += increment;
            alpha = Math.min(1, alpha);
          } else {
            alpha -= increment;
            alpha = Math.max(min, alpha);
          }
        }

        Platform.getWindowViewer().setTimeout(this, 100);
      }

      widget.getContainerComponent().setAlpha(alpha);
      widget.repaint();
    }
  }


  /**
   * The class holds information used during the locking and unlocking of the
   * application
   *
   * @author Don DeCoteau
   *
   */
  static class ApplicationLock {

    /** the viewer on the screen when we locked the application */
    iViewer viewer;

    /** the action path to what was displayed at the time of the lock */
    ActionPath actionPath;

    /**
     * Creates a new instance
     *
     * @param type
     *          the type of lock we are initiating
     */
    ApplicationLock(ApplicationLockType type) {
      String patient = getPatientID();

      if (patient != null) {
        actionPath = Utils.getDisplayedActionPath();
      }
    }

    /**
     * Disposes of the viewer that was on the screen when we locked the
     * application
     */
    public void disposeViewer() {
      viewer.dispose();
      PatientSelect.clearoutPatientCentricInfo();

      iTarget    t  = Platform.getWindowViewer().getTarget(iTarget.TARGET_WORKSPACE);
      iContainer rv = (iContainer) t.getViewer();

      ((StackPaneViewer) rv.getWidget("reloginPanel")).switchTo(0);
      rv.update();
      viewer = null;
    }

    public void restoreViewer() {
      WindowViewer w = Platform.getWindowViewer();
      iTarget      t = w.getTarget(iTarget.TARGET_WORKSPACE);
      iViewer      v = t.setViewer(viewer);

      v.dispose();
      viewer = null;
    }
  }


  enum ApplicationLockType { TIMEOUT, PAUSED, UNAVAILABLE }

  static class DefaultProtocolHandler extends aProtocolHandler {
    @Override
    public void initialize(Server server) throws Exception {}
  }


  /**
   * We want to now when we get an error during polling as we want to ignore
   * connection errors while polling. That way, if the server or connection goes
   * down it will only generate an error if the user is active.
   * <p>
   * If the server/connection is restored before any direct user activity then
   * the application we just continue to work
   * </p>
   *
   * <p>
   * This class provides the functionality for change the exception that is
   * generated when a connection exception occurs
   * </p>
   *
   * @author Don DeCoteau
   */
  static class LinkErrorHandler extends aLinkErrorHandler {
    public LinkErrorHandler() {}

    @Override
    public Exception getExceptionChange(ActionLink link, Exception ex) {
      if (ex instanceof ConnectException) {
        return new PollingConnectionRefused();
      }

      return ex;
    }

    @Override
    public Action handleError(ActionLink link, Exception ex, iURLConnection conn) {
      if (ex instanceof ConnectException) {
        if ((link.getAttributes() != null) && (link.getAttributes().get("polling") != null)) {
          return Action.CHANGE_ERROR;
        }
      }

      return Action.ERROR;
    }
  }


  /**
   * This class manages the login process and setup up the the user context.
   *
   *
   * @author Don DeCoteau
   */
  static class LoginManager extends aWorkerTask {
    String  root;
    String  title;
    Server  server;
    String  username;
    URL     url;
    boolean loggingOff;
    boolean reloggingIn;
    @Weak
    iWidget contextWidget;
    String  password;
    boolean reselectPatient;

    public LoginManager(iWidget context, URL url, String root, Server server, String title) {
      this.contextWidget = context;
      this.url           = url;
      this.root          = root;
      this.server        = server;
      this.title         = title;
    }

    /**
     * Called from getReloginPatient to see if it is ok to reselect the current
     * patient. This returns true only if we were forced to relogin because our
     * session timed out. The value is reset after the call.
     *
     * @return
     */
    public boolean canReselectPatient() {
      boolean ok = reselectPatient;

      reselectPatient = false;

      return ok;
    }

    @Override
    public Object compute() {
      Object result = null;

      try {
        ActionLink          l;
        iPlatformAppContext app = window.getAppContext();

        if (cardStack &&!demo) {
          if (url == null) {
            l = createLink(window, "/hub/main/account/pin_login/user.json");
          } else {
            l = createLink(window, new URL(url, "hub/main/account/pin_login/user.json"));
          }
        } else {
          if (url == null) {
            l = createLink(window, "/hub/main/account/login/user.json");
          } else {
            l = createLink(window, new URL(url, "hub/main/account/login/user.json"));
          }
        }

        String s;

        if (!demo) {
          if (protocolHandlers != null) {
            aProtocolHandler ph = protocolHandlers.get(l.getURL(window).getProtocol());

            if (ph != null) {
              ph.initialize(server);
            }
          }

          Map map = new HashMap();

          map.put("username", username);
          map.put("password", Functions.base64(password));
          map.put("size", UIScreen.getRelativeScreenSizeName());
          map.put("base64", "true");
          s = l.sendFormData(contextWidget, map);
        } else {
          s = l.getContentAsString();
        }

        JSONObject user = new JSONObject(s);

        result = user;

        if (server.isContextServer()) {    //serves up UI content
          if (url != null) {

            /**
             * In multi-screen mode the server automatically sets the context
             * url to the a URL relative to the screen size name. We need to do
             * this also in order to preserve the multi-screen functionality
             * (trailing slash required)
             */
            app.setContextURL(new URL(url, UIScreen.getRelativeScreenSizeName() + "/"), root);
          }
        }

        if (url != null) {

          /**
           * Make sure the /hub/ urls route to this server
           */
          s = server.serverURL;

          if (s.endsWith("/")) {
            s += "hub/";
          } else {
            s += "/hub/";
          }

          app.addURLPrefixMapping("/hub/", s);
        }

        /**
         * Check to see if the server want to overwrite the application
         * attributes
         */
        JSONObject csp = user.optJSONObject("site_parameters");

        s = (csp == null)
            ? null
            : csp.optString("attributesURL", null);

        if ((s != null) && (s.length() > 0)) {    //the server wants to overwrite the attributes loaded at startup
          l = window.createActionLink(window, s);

          JSONObject att = new JSONObject(l.getContentAsString());

          window.getAppContext().putData(att, true);
        } else if (csp != null) {
          window.getAppContext().putData(csp, false);
        }

        s = (csp == null)
            ? null
            : csp.optString("resourceStringsURL", null);

        if ((s != null) && (s.length() > 0)) {    //the server wants to overwrite the strings loaded at startup
          l = window.createActionLink(window, s);
          Platform.loadResourceStrings(app, app.getResourceStrings(), l, false);
        }

        /**
         * create the re-login configuration object so that we can quickly
         * create the viewer. We create it after we have set the context so that
         * relogin screen can come from the server (if it was a context server)
         */
        reloginConfig = (Viewer) window.createConfigurationObject(new ActionLink(window, "relogin.rml"));

        /**
         * Create the generic container configuration to use to create a generic
         * container on the fly
         */
        genericContainerCfg = (GridPane) window.createConfigurationObject(new ActionLink(window,
                "/generic_container.rml"));

        if (cardStack) {
          final Viewer cfg = (Viewer) window.createConfigurationObject(new ActionLink(window, "infobar.rml"));
          Runnable     r   = new Runnable() {
            @Override
            public void run() {
              titleWidget = (iContainer) window.createWidget(cfg);
            }
          };

          Platform.invokeLater(r);
        }

        /**
         * Set a handle to be called when there is an error opening a link
         */
        ActionLink.setGlobalErrorHandler(new LinkErrorHandler());
      } catch(Exception e) {
        result = e;
      }

      password = null;

      return result;
    }

    public void dispose() {}

    @Override
    public void finish(Object result) {
      WindowViewer w = Platform.getWindowViewer();

      w.hideProgressPopup();

      if (contextWidget.isDisposed()) {
        return;
      }

      if (result instanceof Exception) {
        if (result instanceof HTTPException) {
          HTTPException he = (HTTPException) result;

          if (he.getStatusCode() == 401) {
            w.alert(w.getString("bv.text.invalidCredentials"));

            iWidget ww = contextWidget.getFormViewer().getWidget("password");

            if (ww != null) {
              ww.requestFocus();
            }
          } else {
            String s = he.getMessageBody();

            if (s.length() == 0) {
              s = ApplicationException.getMessageEx(he);
            }

            w.error(s);
          }

          return;
        } else if (result instanceof SocketException) {    //connect refused type errors
          w.error(ApplicationException.getMessageEx((Throwable) result));

          return;
        }

        w.error(result);
        Platform.ignoreException(null, (Throwable) result);

        return;
      }

      preferences = settingsHandler.getAppPreferences(username);

      iPlatformAppContext app  = Platform.getAppContext();
      JSONObject          user = (JSONObject) result;

      app.putData("user", user);
      app.putData("username", username);
      app.putData("userDisplayName", user.optString("name", username));

      //enable preferences and help once we have logged in
      UIAction a = app.getAction("bv.action.preferences");

      if (a != null) {
        a.setEnabled(true);
      }

      a = app.getAction("bv.action.help");

      if (a != null) {
        a.setEnabled(true);
      }

      if ((title != null) && (title.length() > 0)) {
        w.getStrings().put("bv.text.welcome", title);    // replace default
        // title for use with
        // the re-login
        // screen
      }

      try {
//        if (isDemo()) {
//          actionPath = new ActionPath("2");
//        }

        if (applicationLock != null) {    //we are re-logging in 
          actionPath      = applicationLock.actionPath;
          applicationLock = null;
        } else {
          collectionManager = new CollectionManager();

          JSONObject info = (JSONObject) Platform.getAppContext().getData("careTeamInfo");
          String     cls  = info.optString("communicationHandlerClass", null);

          if ((cls != null) && (cls.length() > 0)) {
            cls         = Utils.resolveClassName(cls, "external");
            commHandler = (aCommunicationHandler) Platform.createObject(cls);

            if (commHandler == null) {
              Platform.debugLog("Unable to load communication class:" + cls);
            }
          }

          if (!cardStack) {
            collectionManager.startPolling();
          }
        }

        if (UIScreen.isSmallScreen()) {
          CardStackUtils.setupEnvironment(w);
        }

        Orders.setupEnvironment(w);

        if (!Orders.hasOrderEntrySupport) {
          ActionBar ab = Platform.getWindowViewer().getActionBar();

          ab.remove("bv.action.new_orders");
        }

        PatientSelect.changePatient(w, actionPath);
      } catch(Exception ex) {
        handleError(ex);
      }

      contextWidget = null;
    }

    public void login(String username, String password) {
      this.username   = username;
      this.password   = password;
      loggingOff      = false;
      reloggingIn     = false;
      reselectPatient = false;
      Utils.toggleActions(false);
      start();
    }

    public void logout() {
      loggingOff = true;
      Platform.closeOpenConnections(true);

      if (!isDemo() &&!isApplicationLocked()) {
        try {
          final ActionLink l = Platform.getWindowViewer().createActionLink("/hub/main/account/logout");
          Thread           t = new Thread(new Runnable() {
            public void run() {
              try {
                l.hit();
              } catch(Exception ex) {
                Platform.ignoreException("logout", ex);
              }
            }
          });

          t.setDaemon(true);
          t.start();
          t.join(1000);
        } catch(Exception ex) {}
      }
    }

    public void relogin(iWidget context, String password) {
      contextWidget = context;
      this.password = password;
      loggingOff    = false;
      reloggingIn   = true;
      start();
    }

    private void start() {
      final WindowViewer w = Platform.getWindowViewer();

      w.showProgressPopup(w.getString("bv.text.authenticating"));
      w.spawn(this);
    }
  }


  /**
   * The class is a means of telling the main error handler that a request made
   * via background polling has refused to connect.
   *
   * In those cases we want to ignore the exception. One if we get a connection
   * refused request for a user initiated action do we want to do something
   *
   */
  static class PollingConnectionRefused extends IOException {}


  static class ShowPickListCallback extends aFunctionCallback {
    iFunctionCallback        callback;
    ActionLink               dataUrl;
    List<RenderableDataItem> rows;
    boolean                  supportListDblClick;
    String                   title;

    public ShowPickListCallback(String title, ActionLink dataUrl, List<RenderableDataItem> rows,
                                boolean supportListDblClick, iFunctionCallback callback, boolean onUiThread) {
      super();
      this.title               = title;
      this.dataUrl             = dataUrl;
      this.rows                = rows;
      this.supportListDblClick = supportListDblClick;
      this.callback            = new CallbackFunctionCallback(callback, onUiThread);
    }

    public void finished(boolean canceled, Object returnValue) {
      hideWaitCursor();

      try {
        if (canceled) {
          callback.finished(canceled, returnValue);

          if (returnValue instanceof Exception) {
            window.handleException((Exception) returnValue);
          }

          return;
        }

        iContainer v = (iContainer) returnValue;

        v.setAttribute("dlgCallback", callback);
        v.getWidget("titleLabel").setValue(title);

        aWidget list = (aWidget) v.getWidget("list");

        if (rows != null) {
          list.setAll(rows);
        } else {
          list.setDataLink(dataUrl, true);
        }

        if (supportListDblClick) {
          list.setEventHandler("onDblClick", "class:PickList#onOkAction", true);
        }

        Map map = new HashMap();

        map.put("decorated", false);
        map.put("opaque", false);
        v.showAsDialog(map);
      } finally {
        callback = null;
        rows     = null;
        dataUrl  = null;
      }
    }
  }


  public static void applicationInitialized() {
    cardStack = UIScreen.isSmallScreen();

    /**
     * Check for protocol handlers
     */
    if (protocolHandlers == null) {    //check only once
      JSONObject ph = (JSONObject) Platform.getAppContext().getData("protocolHandlers");

      if ((ph != null) &&!ph.isEmpty()) {
        Iterator<Entry> it = ph.getObjectMap().entrySet().iterator();

        protocolHandlers = new HashMap<String, aProtocolHandler>(2);

        while(it.hasNext()) {
          Entry  e        = it.next();
          String prototol = (String) e.getKey();
          String cls      = (String) e.getValue();

          if (cls.indexOf('.') == -1) {
            cls = Utils.class.getPackage().getName() + ".external." + cls;
          }

          aProtocolHandler h = (aProtocolHandler) Platform.createObject(cls);

          if (ph == null) {
            Platform.debugLog("unable to create protocol handler:" + cls);
          } else {
            protocolHandlers.put(prototol, h);
            protocolHandlers.put(prototol + "s", h);
          }
        }

        if (protocolHandlers.isEmpty()) {
          protocolHandlers = null;
        } else {
          defaultProtocolHandler = new DefaultProtocolHandler();
          URL.setURLStreamHandlerFactory(new StreamHandlerFactory());
        }
      }
    }

    WindowViewer w  = Platform.getWindowViewer();
    ActionBar    ab = w.getActionBar();

    actionbarIcon = new BackIcon(ab.getTitleComponent().getIcon());
    ab.setTitleIcon(actionbarIcon);
    ab.setTitleAction(w.getAction("bv.action.back"));
  }

  /**
   * Returns the data from the specified url as a SJON object.
   * This method will throw an exception if called from the UI thread
   *
   * @param url the url
   * @param data data to send (can be null)
   * @param sendAsJSON true to send form data as JSON; false otherwise
   * @return the JSON object representing the data
   * @throws Exception
   */
  public static JSONObject getContentAsJSON(String url, Map data, boolean sendAsJSON) throws Exception {
    if (Platform.isUIThread()) {
      throw new ApplicationException("This method must only be called from a background thread");
    }

    ActionLink l = new ActionLink(url);

    if ((data != null) && sendAsJSON) {
      l.setRequestEncoding(RequestEncoding.JSON);
    }

    String s = (data == null)
               ? l.getContentAsString()
               : l.sendFormData(Platform.getWindowViewer(), data);

    return new JSONObject(s);
  }

  public static Date parseDate(String value) {
    try {
      value = value.trim();

      if (value.length() == 0) {
        return null;
      }

      int n = value.indexOf(',');

      if (n != -1) {
        DateFormat df = Platform.getAppContext().getDefaultDateTimeContext().getDisplayFormat();

        return df.parse(value);
      }

      if (Character.isDigit(value.charAt(0))) {
        Calendar cal = Calendar.getInstance();

        Helper.setDateTime(value, cal, true);

        return cal.getTime();
      }

      if (value.startsWith("@")) {
        value = "T" + "value";
      }

      return Helper.createDate(value);
    } catch(ParseException e) {
      return null;
    }
  }

  static class BackIcon extends aPlatformIcon {
    iPlatformIcon appIcon;
    iPlatformIcon patientIcon;
    iPlatformIcon icon;
    iPlatformIcon backIcon;
    int           backs;
    int           iconHeight;
    int           iconWidth;
    int           backIconWidth;
    int           backIconHeight;

    public BackIcon(iPlatformIcon icon) {
      super();
      this.icon      = icon;
      this.appIcon   = icon;
      this.backIcon  = Platform.getResourceAsIcon("bv.icon.back_thin");
      iconWidth      = icon.getIconWidth();
      iconHeight     = icon.getIconHeight();
      backIconWidth  = backIcon.getIconWidth();
      backIconHeight = backIcon.getIconHeight();
    }

    public int getBacks() {
      return backs;
    }

    @Override
    public int getIconHeight() {
      return Math.max(backIconHeight, iconHeight);
    }

    @Override
    public int getIconWidth() {
      return iconWidth + (backs * backIconWidth);
    }

    public void setIcon(iPlatformIcon icon) {
      this.icon = icon;
    }

    public void showPatientIcon(boolean show) {
      if (show) {
        icon = patientIcon;
      } else {
        icon = appIcon;
      }

      if (icon == null) {
        icon = appIcon;
      }

      iconWidth  = icon.getIconWidth();
      iconHeight = icon.getIconHeight();
    }

    @Override
    public iPlatformIcon getDisabledVersion() {
      return this;
    }

    @Override
    public void paint(iPlatformGraphics g, float x, float y, float width, float height) {
      float yy = (height - backIconHeight) / 2;

      for (int i = 0; i < backs; i++) {
        backIcon.paint(g, x, yy, width, height);
        x     += backIconWidth;
        width -= backIconWidth;
      }

      yy = (height - iconHeight) / 2;
      icon.paint(g, x, yy, width, height);
    }

    public void increment() {
      backs++;
    }

    public void decrement() {
      backs--;

      if (backs < 0) {
        backs = 0;
      }
    }
  }
}
