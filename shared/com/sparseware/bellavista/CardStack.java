/*
 * @(#)CardStack.java
 *
 * Copyright (c) SparseWare. All rights reserved.
 *
 * Use is subject to license terms.
 */

package com.sparseware.bellavista;

import java.net.MalformedURLException;
import java.util.EventObject;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.FlingEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TabPaneViewer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.json.JSONObject;

/**
 * This class manages a two dimensional stack of viewers for use in navigation
 * on wearable devices that use this metaphor.
 *
 * @author Don DeCoteau
 */
public class CardStack implements iEventHandler, iFunctionCallback {
  public static final int GO_DOWN  = 1;
  public static final int GO_LEFT  = 2;
  public static final int GO_RIGHT = 3;
  public static final int GO_UP    = 0;
  protected iPlatformIcon bundleIcon;
  protected long          lastKeyTime;
  protected boolean       switching;
  protected int           flingVelocityThreshold = 500;
  protected int           keyTimeThreshold       = 500;
  static int              pinDigitCount          = 4;

  public CardStack() {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("cardStackInfo");

    if (info != null) {
      flingVelocityThreshold = info.optInt("flingVelocityThreshold", 500);
      keyTimeThreshold       = info.optInt("keyTimeThreshold", 500);
      pinDigitCount          = info.optInt("pinDigitCount", 4);
    }
  }

  /**
   * Get the number of digits that should be used for a pin for logging in
   *
   * @return the number of digits that should be used for a pin
   */
  public static int getPinDigitCount() {
    return pinDigitCount;
  }

  /**
   * Drills down on the stack. The active view is queries for a url to use to
   * create the new viewer that will be displayed.
   *
   * @param widget
   *          the context widget
   * @throws MalformedURLException
   */
  public void drillDown(iViewer v) throws MalformedURLException {
    String url = CardStackUtils.getViewerBundleURL(v);

    if (url == null) {
      Object action = CardStackUtils.getViewerAction(v);

      if (action instanceof iActionListener) {
        ((iActionListener) action).actionPerformed(new ActionEvent(v));
      } else if (action instanceof Runnable) {
        ((Runnable) action).run();
      } else if (action instanceof String) {
        ((aWidget) v).evaluateCode(action);
      }

      return;
    }

    Utils.pushWorkspaceViewer(url, true,null);
  }

  @Override
  public void finished(boolean canceled, Object returnValue) {
    switching = false;
  }

  /**
   * Goes up the stack
   *
   * @param widget
   */
  public void goUp(iWidget widget) {
    if (!Utils.popViewerStack()) {
      Platform.getWindowViewer().toBack();
    }
  }

  /**
   * Call to handle a change of a viewer in a stack/tab pane viewer.
   *
   * We update the card stack title as appropriate
   */
  public void onChangeEvent(String eventName, iWidget widget, EventObject event) {
    try {
      CardStackUtils.updateTitle((iViewer) widget, false);
    } finally {
      switching = false;
    }
  }

  /**
   * Called to create and set the pin value use to allow the device to piggyback
   * a logged in server session
   */
  public void onConfigurePinValue(String eventName, iWidget widget, EventObject event) {
    Platform.getWindowViewer().getActionBar().setVisible(false);
    widget.setValue(CardStackUtils.generateRandomNumberString(pinDigitCount));
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {}

  /**
   * Called to handle fling events
   */
  public void onFlingEvent(String eventName, iWidget widget, EventObject event) {
    float      n = 0;
    FlingEvent e = (FlingEvent) event;

    e.consume();

    if (switching) {
      return;
    }

    if (Math.abs(e.getGestureX()) >= Math.abs(e.getGestureY())) {
      n = e.getGestureX();
    } else {
      n = e.getGestureY();

      if ((Math.abs(n) > flingVelocityThreshold) && (n > 0)) {
        go(widget, GO_UP);
      }

      return;
    }

    if (Math.abs(n) > flingVelocityThreshold) {
      if (Utils.isReverseFling()) {
        n *= -1;
      }

      if (n > 0) {
        go(widget, GO_LEFT);
      } else {
        go(widget, GO_RIGHT);
      }
    }
  }

  /**
   * Called by wearables that generate key events for navigation gestures.
   */
  public void onKeyUpEvent(String eventName, iWidget widget, EventObject event) {
//    KeyEvent ke = (KeyEvent) event;
//    long time = ke.getEventTime();
//
//    if ((time - lastKeyTime < keyTimeThreshold) || switching) {
//      return;
//    }
//
//    lastKeyTime = time;
//
//    if (ke.isEscapeKeyPressed()) {
//      go(widget, GO_UP);
//    }
  }

  /**
   * Called to configure the main tab pane for the application
   */
  public void onTabPaneConfigured(String eventName, iWidget widget, EventObject event) {
    TabPaneViewer tp = (TabPaneViewer) widget;

    tp.setReloadTimeout(60000);
    tp.getTabPaneComponent().getTabStrip().setVisible(false);
    Utils.updateActionBar();
  }

  /**
   * Called when the user taps on the device
   */
  public void onTapEvent(String eventName, iWidget widget, EventObject event) {
    if (switching) {
      return;
    }

    go(widget, GO_DOWN);
  }

  /**
   * Goes to the a viewer on the stack
   *
   * @param sp
   *          the widget context
   * @param direction
   *          the direction to go
   */
  protected void go(final iWidget widget, final int direction) {
    final StackPaneViewer sp = Utils.getStackPaneViewer(widget);
    final TabPaneViewer   tp = (widget.getParent() instanceof TabPaneViewer)
                               ? (TabPaneViewer) widget.getParent()
                               : null;

    if (switching || ((sp == null) && (tp == null))) {
      return;
    }

    Runnable r = new Runnable() {
      @Override
      public void run() {
        switch(direction) {
          case GO_UP :
            if (tp != null) {
              PatientSelect.changePatient(widget, null);
            } else {
              goUp(sp);
            }

            break;

          case GO_LEFT :
            if (tp != null) {
              if (tp.getSelectedTab() != 0) {
                switchTo(tp, tp.getSelectedTab() - 1);
              } else {
                tp.getContainerViewer().animate("Rare.anim.pullBackLeft", null);
              }

              break;
            }

            if (sp.getActiveViewerIndex() == 0) {
              sp.getContainerViewer().animate("Rare.anim.pullBackLeft", null);

              break;
            }

            switchTo(sp, sp.getActiveViewerIndex() - 1);

            break;

          case GO_RIGHT :
            if (tp != null) {
              if (tp.getSelectedTab() + 1 != tp.getTabCount()) {
                switchTo(tp, tp.getSelectedTab() + 1);
              } else {
                tp.getContainerViewer().animate("Rare.anim.pullBackRight", null);
              }

              break;
            }

            if ((sp.getActiveViewerIndex() == sp.size() - 1) || (sp.size() == 1)) {
              sp.getContainerViewer().animate("Rare.anim.pullBackRight", null);

              return;
            }

            switchTo(sp, sp.getActiveViewerIndex() + 1);

            break;

          case GO_DOWN :
          default :
            try {
              drillDown((tp == null)
                        ? sp.getActiveViewer()
                        : tp.getSelectedTabViewer());
            } catch(MalformedURLException e) {
              Platform.getWindowViewer().handleException(e);
            }

            break;
        }
      }
    };

    Platform.invokeLater(r);
  }

  private void switchTo(final StackPaneViewer sp, final int index) {
    switching = true;

    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        sp.switchTo(index);
      }
    };

    sp.getViewer(index, cb);
  }

  private void switchTo(final TabPaneViewer tp, final int index) {
    switching = true;

    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        tp.setSelectedTab(index);
      }
    };

    tp.getTabViewer(index, cb);
  }
}
