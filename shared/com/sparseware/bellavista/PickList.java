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
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.spot.ListBox;
import com.appnativa.rare.ui.AlertPanel;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.viewer.ListBoxViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.widget.PushButtonWidget;

import java.util.List;

/**
 * This class manages the creation and display of
 * a widgets the provides for the selection of a single
 * item from a list.
 *
 * @author Don DeCoteau
 *
 */
public class PickList {
  protected boolean                  showNone;
  protected String                   noneButtonText;
  protected boolean                  supportListDblClick = true;
  protected String                   windowTitle;
  protected List<RenderableDataItem> dataRows;
  protected ActionLink               dataLink;
  protected String                   okButtonText;
  protected String                   cancelButtonText;
  protected boolean                    rightAlignButtons=true;

  /**
   * Creates a new instance
   */
  public PickList() {}

  /**
   * Creates a new instance
   *
   * @param windowTitle the window's title
   * @param dataRows the data for the list
   */
  public PickList(String windowTitle, List<RenderableDataItem> dataRows) {
    super();
    this.windowTitle = windowTitle;
    this.dataRows    = dataRows;
  }

  /**
   * Creates a new instance
   *
   * @param windowTitle the window's title
   * @param dataLink the data for the list
   */
  public PickList(String windowTitle, ActionLink dataLink) {
    super();
    this.windowTitle = windowTitle;
    this.dataLink    = dataLink;
  }

  /**
   * Sets the text for the cancel button
   *
   * @param text the button text
   */
  public void setCancelButtonText(String text) {
    this.cancelButtonText = text;
  }

  /**
   * Sets the data for the list
   * @param dataLink the data for the list
   */
  public void setDataLink(ActionLink dataLink) {
    this.dataLink = dataLink;
    this.dataRows = null;
  }

  /**
   * Sets the data for the list
   * @param dataRows the data for the list
   */
  public void setDataRows(List<RenderableDataItem> dataRows) {
    this.dataRows = dataRows;
    this.dataLink = null;
  }

  /**
   * Sets the text for the ok button
   *
   * @param text the button text
   */
  public void setOkButtonText(String text) {
    this.okButtonText = text;
  }

  /**
   * Sets whether the "none" button is shown
   *
   * @param show true to show button; false otherwise
   * @param text the button text
   */
  public void setShowNoneButton(boolean show, String text) {
    showNone       = show;
    noneButtonText = text;
  }

  /**
   * Sets whether double-clicking on an item triggers the ok
   * button action.
   *
   * @param supportListDblClick true to fire the ok action when an item is double clicked; false otherwise
   */
  public void setSupportListDblClick(boolean supportListDblClick) {
    this.supportListDblClick = supportListDblClick;
  }

  /**
   * Sets the window's title
   *
   * @param windowTitle the title
   */
  public void setWindowTitle(String windowTitle) {
    this.windowTitle = windowTitle;
  }

  /**
   * Shows the pick list viewer
   *
   * @param cb the callback to call when the picklist goes away
   */
  public void show(final iFunctionCallback cb) {
    final WindowViewer w   = Platform.getWindowViewer();
    ListBox            cfg = (ListBox) w.createConfigurationObject("ListBox", "bv.listbox.pick_list");

    cfg.singleClickActionEnabled.setValue(true);

    final ListBoxViewer lb = (ListBoxViewer) w.createWidget(cfg);

    if (dataRows != null) {
      lb.setAll(dataRows);
    } else {
      lb.setDataLink(dataLink, true);
    }

    dataRows = null;
    dataLink = null;

    AlertPanel p;

    if (showNone) {
      p = AlertPanel.yesNoCancel(w, windowTitle, lb, null, okButtonText, noneButtonText, cancelButtonText,"bv.button.alert");
    } else {
      p = AlertPanel.yesNo(w, windowTitle, lb, null, okButtonText, noneButtonText, true);
    }

    p.setRightAlignButtons(rightAlignButtons);

    final PushButtonWidget okButton = p.getYesOrOkButton();

    okButton.setEnabled(false);

    if (supportListDblClick) {
      lb.setEventHandler(iConstants.ATTRIBUTE_ON_DOUBLECLICK, new Runnable() {
        @Override
        public void run() {
          okButton.click();
        }
      }, true);
    }

    lb.addActionListener(new iActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButton.setEnabled(true);
      }
    });

    iFunctionCallback pcb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        RenderableDataItem item = null;

        if (Boolean.TRUE.equals(returnValue)) {
          item = lb.getSelectedItem();
        }

        lb.dispose();
        cb.finished(canceled, item);
      }
    };

    p.showDialog(pcb);
  }

  public void setRightAlignButtons(boolean rightAlignButtons) {
    this.rightAlignButtons = rightAlignButtons;
  }
}
