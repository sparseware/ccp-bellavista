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
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.JavaURLConnection;
import com.appnativa.rare.spot.StackPane;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.viewer.GridPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.ComboBoxWidget;
import com.appnativa.rare.widget.PushButtonWidget;
import com.appnativa.rare.widget.aWidget;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;

import com.sparseware.bellavista.Document.DocumentItemType;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class handles clinical notes/documents. The data is assumed to be
 * returned is reverse chronological order (newest values first). The middleware
 * should enforce this constraint.
 *
 * @author Don DeCoteau
 */
public class Notes extends aResultsManager implements iValueChecker {
  protected int                             attachmentColumn;
  protected int                             parentColumn;
  protected int                             documentURLColumn;
  protected String                          infoName     = "notesInfo";
  protected String                          documentPath = "/hub/main/documents/document/";
  Document                                  loadedDocument;
  iPlatformIcon                             attachmentIcon;
  HashMap<String, List<RenderableDataItem>> parentMap = new HashMap<String, List<RenderableDataItem>>();

  public Notes() {
    this("notes", "Notes");
  }

  protected Notes(String namePrefix, String scriptClassName) {
    super(namePrefix, scriptClassName);
    currentView = ResultsView.DOCUMENT;

    JSONObject info = (JSONObject) Platform.getAppContext().getData("notesInfo");

    attachmentColumn  = info.optInt("attachmentColumn", -1);
    parentColumn      = info.optInt("parentColumn", -1);
    documentURLColumn = info.optInt("documentURLColumn", -1);
    attachmentIcon    = Platform.getResourceAsIcon("bv.icon.document_with_attachment");

    if (Document.documentViewerCfg == null) {
      Platform.getWindowViewer().spawn(new Runnable() {
        @Override
        public void run() {
          try {
            Document.staticInitialize();
          } catch(Exception e) {
            Utils.handleError(e);
          }
        }
      });
    }
  }

  @Override
  public boolean checkRow(RenderableDataItem row, int index, int expandableColumn, int rowCount) {
    if (parentColumn > -1) {
      RenderableDataItem item   = row.getItemEx(parentColumn);
      String             parent = (item == null)
                                  ? null
                                  : (String) item.getValue();

      if ((parent != null) && (parent.length() > 0)) {
        List<RenderableDataItem> list = parentMap.get(parent);

        if (list == null) {
          list = new ArrayList<RenderableDataItem>(3);
          parentMap.put(parent, list);
        }

        list.add(row);

        return false;
      }
    }

    if (attachmentColumn > -1) {
      RenderableDataItem item = row.getItemEx(attachmentColumn);

      if (item != null) {
        String s = item.toString();

        if (s.equals("true")) {
          item = row.get(0);

          if (item.getIcon() == null) {
            item.setIcon(attachmentIcon);
          }
        }
      }
    }

    return true;
  }

  @Override
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    if (loadedDocument != null) {
      loadedDocument.dispose();
    }

    loadedDocument = null;
    super.onDispose(eventName, widget, event);
  }

  public void onFiltersConfigure(String eventName, iWidget widget, EventObject event) {
    JSONObject info    = (JSONObject) Platform.getAppContext().getData(infoName);
    JSONArray  filters = info.getJSONArray("filters");
    int        len     = (filters == null)
                         ? 0
                         : filters.size();

    if (len == 0) {
      widget.setEnabled(false);

      return;
    }

    iPlatformIcon icon          = Platform.getResourceAsIcon("bv.icon.notes");
    int           defaultFilter = 0;

    for (int i = 0; i < len; i++) {
      JSONObject         filter = filters.getJSONObject(i);
      RenderableDataItem item   = new RenderableDataItem(widget.expandString(filter.getString("text"), false), null,
                                    icon);
      FilterAction fa = new FilterAction(filter);

      if (fa.isDefault()) {
        defaultFilter = i;
      }

      item.setActionListener(fa);
      ((aWidget) widget).add(item);
    }

    if (widget instanceof PushButtonWidget) {
      ((PushButtonWidget) widget).setPopupScrollable(true);
      ((PushButtonWidget) widget).setSelectedIndex(defaultFilter);
    } else if (widget instanceof ComboBoxWidget) {
      ((ComboBoxWidget) widget).setSelectedIndex(defaultFilter);
    }
  }

  /**
   * Returns to the document view after viewing an attachment and subsequently
   * disposes of the attachment viewer.
   *
   * @param sp
   *          the stack pane viewer
   */
  static void goBackToDocumentView(final StackPaneViewer sp) {
    if ((sp != null) &&!sp.isEmpty() && (sp.getActiveViewerIndex() != 0)) {
      sp.switchTo(0, new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          iViewer v = sp.removeViewer(1);

          if (v != null) {
            v.dispose();
          }
        }
      });
    }
  }

  @Override
  public void onTableAction(String eventName, iWidget widget, EventObject event) {
    try {
      final TableViewer        table = (TableViewer) widget;
      final RenderableDataItem row   = table.getSelectedItem();
      RenderableDataItem       item  = row.get(DATE_POSITION);
      Date                     date  = (Date) item.getValue();
      String                   id    = (String) item.getLinkedData();
      iFormViewer              fv    = widget.getFormViewer();
      final StackPaneViewer    sp    = (StackPaneViewer) widget.getFormViewer().getWidget("documentStack");

      goBackToDocumentView(sp);

      // if we have a stack pane then it should be the form viewer
      if (sp != null) {
        fv = sp;
      }

      if (hasDocumentLoaded(id, fv)) {
        return;
      }

      if (loadedDocument != null) {
        loadedDocument.dispose();
      }

      String     title = row.get(NAME_POSITION).toString();
      ActionLink link  = null;

      if (documentURLColumn > -1) {
        RenderableDataItem ri  = row.getItemEx(documentURLColumn);
        String             url = (ri == null)
                                 ? null
                                 : ri.toString();

        if ((url != null) && (url.length() > 0)) {
          URL u = fv.getURL(url);

          if (!Utils.getServerHost().equals(u.getHost())) {
            throw new MessageException(Platform.getResourceAsString("bv.text.cant_load_xdocument"));
          }

          link = new ActionLink(url);
        }
      }

      if (link == null) {
        link = new ActionLink(widget, widget.getURL(documentPath + id + ".html"));
      }

      Document doc = new Document(widget, link, id);

      loadedDocument = doc;
      doc.setMainDocumentInfo(date, title);

      if (!item.isEmpty()) {
        for (RenderableDataItem di : item.getItems()) {
          addAttachment(table, doc, di);
        }
      }

      GridPaneViewer gp = (GridPaneViewer) fv.getWidget("documentViewer");

      if (gp == null) {
        gp = Document.createDocumentViewer(fv, doc.getAttachmentCount() > 0);

        if (sp != null) {
          sp.setViewer(0, gp);
          sp.switchTo(0);
        } else {
          StackPane cfg = new StackPane();

          cfg.actAsFormViewer.setValue(true);
          cfg.transitionAnimator.setValue("SlideAnimation");
          cfg.name.setValue("documentStack");

          StackPaneViewer spnew = (StackPaneViewer) Platform.getWindowViewer().createViewer(fv, cfg);

          spnew.addViewer(null, gp);
          spnew.switchTo(0);
          Utils.pushWorkspaceViewer(spnew);
        }
      }

      doc.loadAndPopulateViewer(gp.isActAsFormViewer()
                                ? gp
                                : fv);
    } catch(Exception e) {
      Utils.handleError(e);
    }
  }

  @Override
  public void onTableCreated(String eventName, iWidget widget, EventObject event) {
    super.onTableCreated(eventName, widget, event);
    currentView = ResultsView.DOCUMENT;
  }

  @Override
  protected void reset() {
    super.reset();
    parentMap.clear();
  }

  @Override
  protected void dataParsed(iWidget widget, final List<RenderableDataItem> rows, ActionLink link) {
    originalRows = rows;
    tableDataLoaded(link);

     TableViewer table = (TableViewer) widget;

      if (checkAndHandleNoData(table, rows)) {
        return;
      } else {
        processData(table, rows);
      }
  }

  protected void addAttachment(TableViewer table, Document doc, RenderableDataItem row) throws MalformedURLException {
    RenderableDataItem item = row.get(DATE_POSITION);

    table.convert(DATE_POSITION, item);

    Object o = item.getValue();

    if (o instanceof String) {    // the value was never converted be cause it
      // wasnever displayed by the table
      o = item.getValue();
    }

    Date   date  = (Date) o;
    String id    = (String) item.getLinkedData();
    String title = row.get(NAME_POSITION).toString();
    URL    url   = table.getURL(documentPath + id + ".html");

    doc.addAttachment(DocumentItemType.DOCUMENT, date, title, JavaURLConnection.toExternalForm(url));
  }

  protected void processData(TableViewer table, List<RenderableDataItem> rows) {
    if (attachmentColumn > 1) {
      int len = rows.size();

      for (int i = len - 1; i > -1; i--) {
        RenderableDataItem row = rows.get(i);

        if (!checkRow(row, i, 0, len)) {
          rows.remove(i);
        }
      }
    }

    boolean needsSorting = false;

    if (!parentMap.isEmpty()) {
      int                                       len = rows.size();
      HashMap<String, List<RenderableDataItem>> map = parentMap;

      for (int i = 0; i < len; i++) {
        RenderableDataItem       row  = rows.get(i);
        RenderableDataItem       item = row.get(DATE_POSITION);
        String                   id   = (String) item.getLinkedData();
        List<RenderableDataItem> list = map.remove(id);

        if (list != null) {
          item.addAll(list);

          if (item.getIcon() == null) {
            item.setIcon(attachmentIcon);
          }
        }
      }

      if (!map.isEmpty()) {
        Iterator<List<RenderableDataItem>> it = map.values().iterator();

        while(it.hasNext()) {
          rows.addAll(it.next());
        }

        needsSorting = true;
      }
    }

    table.setAll(rows);

    if (needsSorting) {
      table.sort(0, true, false);
    }

    table.finishedLoading();

    ActionPath path = Utils.getActionPath(true);
    String     key  = (path == null)
                      ? null
                      : path.pop();

    if (key != null) {
      handlePathKey(table, key, 0, true);
    }
  }

  static class FilterAction implements iActionListener {
    JSONObject filter;

    public FilterAction(JSONObject filter) {
      this.filter = filter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    public boolean isDefault() {
      String s = filter.optString("filter", null);

      if (s == null) {
        return false;
      }

      return filter.optBoolean("serverSide", false) && (s.length() == 0);
    }
  }


  static class Status {
    String key;
    String icon;
  }
}
