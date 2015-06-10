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

import java.util.EventObject;
import java.util.List;

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.widget.LabelWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONObject;

public class Consults extends Notes {
  protected static int STATUS_COLUMN = 3;

  public Consults() {
    super("consults", "Consults");
    JSONObject info = (JSONObject) Platform.getAppContext().getData("consultsInfo");
    attachmentColumn = info.optInt("attachmentColumn", -1);
    parentColumn = info.optInt("parentColumn", -1);
    infoName = "consultsInfo";
    documentPath = "/hub/main/consults/consult/";
  }

  public void onCreated(String eventName, iWidget widget, EventObject event) {
  }

  /**
   * Called when the consults data has been loaded into the table. We populate
   * the the card when using a cardstack UI.
   */
  public void onFinishedLoading(String eventName, iWidget widget, EventObject event) {
    if (Utils.isCardStack()) {
      TableViewer table = (TableViewer) widget;
      iFormViewer fv = table.getFormViewer();
      populateCardStack(fv, table);
      updateCardStackTitle(fv.getTitle(), null);
    }
  }

  /**
   * Populates the card stack form with recent consults and their status
   * 
   * @param fv
   *          the form container
   * @param rows
   *          the rows containing the consults
   */
  protected void populateCardStack(iContainer fv, List<RenderableDataItem> rows) {
    int len = rows.size();
    iContainer itemsForm = (iContainer) fv.getWidget("itemsForm");
    int count = Math.min(itemsForm.getWidgetCount() / 2, len);
    int n = 0;
    for (int i = 0; i < count; i++) {
      RenderableDataItem row = rows.get(i);
      RenderableDataItem name = row.get(NAME_POSITION);
      RenderableDataItem item = row.getItemEx(STATUS_COLUMN);
      String status = item == null ? "" : item.toString();

      LabelWidget nl = (LabelWidget) itemsForm.getWidget(n++);
      LabelWidget sl = (LabelWidget) itemsForm.getWidget(n++);
      if (name.getForeground() != null) {
        nl.setForeground(name.getForeground());
        sl.setForeground(name.getForeground());
      }
      nl.setValue(name);
      sl.setValue(status);
    }
    if (count < len) {
      String s = Platform.getWindowViewer().getString("bv.format.tap_to_see_more", StringCache.valueOf(count),
          StringCache.valueOf(len));
      iWidget tapLabel = fv.getWidget("tapLabel");
      tapLabel.setValue(s);
    }
  }

  /**
   * Handles a card stack drill down action
   */
  protected class OrdersStackActionListener implements iActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      StackPaneViewer sp = CardStackUtils.createListItemsOrPageViewer(null, dataTable.getFormViewer(), dataTable.getRawRows(), -1,
          1, null, false, true);
      Utils.pushWorkspaceViewer(sp, false);
    }
  }
}
