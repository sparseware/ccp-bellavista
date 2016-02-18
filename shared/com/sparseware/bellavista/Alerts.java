package com.sparseware.bellavista;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iDataCollection;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIAction;
import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UICompoundIcon;
import com.appnativa.rare.ui.UIFont;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.UITextIcon;
import com.appnativa.rare.ui.border.UILineBorder;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.StringCache;

import com.sparseware.bellavista.Utils.BadgeCompoundIcon;

import java.util.EventObject;
import java.util.List;

/**
 * This class manages the loading (from the collection) and display of patient alerts
 * 
 * @author Don DeCoteau
 */
public class Alerts extends aEventHandler {
  private static int            lastAlertCount;
  private static UITextIcon     alertCountIcon;
  private static UICompoundIcon compoundIcon1;
  private static UICompoundIcon compoundIcon2;

  /**
   * Get the icon that represents the number of alerts.
   */
  public static iPlatformIcon getAlertCountIcon() {
    int             taskCount = 0;
    iDataCollection dc        = Platform.getAppContext().getDataCollection("alerts");

    taskCount += (dc == null)
                 ? 0
                 : dc.size();

    if (taskCount == 0) {
      lastAlertCount = 0;

      return null;
    }

    if ((taskCount == lastAlertCount) && (alertCountIcon != null)) {
      return alertCountIcon;
    }

    String value;

    if (taskCount > 9) {
      value = "9+";
    } else {
      value = StringCache.valueOf(taskCount);
    }

    UILineBorder b;

    if (alertCountIcon == null) {
      WindowViewer w = Platform.getWindowViewer();

      b = new UILineBorder(UIColor.WHITE, UIScreen.PLATFORM_PIXELS_2, UIScreen.PLATFORM_PIXELS_16);
      b.setInsetsPadding(UIScreen.PLATFORM_PIXELS_2, UIScreen.PLATFORM_PIXELS_2, UIScreen.PLATFORM_PIXELS_2,
                         UIScreen.PLATFORM_PIXELS_2);

      UIFont font = w.getFont();

      font = font.deriveFont(UIFont.BOLD, font.getSize() - 4);

      UITextIcon icon = new UITextIcon(value, UIColor.WHITE, font, b);

      icon.setSquare(true);
      icon.setBackgroundColor(UIColor.RED.alpha(128));
      alertCountIcon = icon;
    } else {
      b = (UILineBorder) alertCountIcon.getBorder();
      alertCountIcon.setText(value);
    }

    lastAlertCount = taskCount;
    alertCountIcon.setBorder(null);

    int w = alertCountIcon.getIconWidth();

    b.setCornerArc(w);    //makes the border round
    alertCountIcon.setBorder(b);

    return alertCountIcon;
  }

  /**
   * Called to update the alerts icon
   */
  public static void updateAlertsIcon() {
    UIAction a = Platform.getAppContext().getAction("bv.action.alerts");

    if (a == null) {
      return;
    }

    iPlatformIcon ai   = Platform.getResourceAsIcon("bv.icon.alert");
    iPlatformIcon icon = getAlertCountIcon();

    if (icon == null) {
      icon = ai;
    } else {
      //we use 2 icons because the action wont notify listeners
      //if we pass in the same object
      if (compoundIcon1 == null) {
        compoundIcon1 = new BadgeCompoundIcon(ai, icon);
        compoundIcon2 = new BadgeCompoundIcon(ai, icon);
      } else {
        compoundIcon1.setSecondIcon(icon);
        compoundIcon2.setSecondIcon(icon);
      }

      if (a.getIcon() == compoundIcon1) {
        icon = compoundIcon2;
      } else {
        icon = compoundIcon2;
      }
    }

    a.setEnabled(icon != ai);
    a.setIcon(icon);
  }

  /**
   * Called when the alerts popup is about to be shown to update the
   * alerts table
   */
  public void onUpdateTable(String eventName, iWidget widget, EventObject event) {
    iDataCollection dc    = Platform.getAppContext().getDataCollection("alerts");
    TableViewer     table = (TableViewer) widget.getFormViewer().getWidget("alertsTable");

    table.clear();
    table.handleCollection(dc.getCollection(table), 0);
  }

  @Override
  protected void dataParsed(iWidget widget, List<RenderableDataItem> rows, ActionLink link) {}
}
