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

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.appnativa.rare.FunctionCallbackWaiter;
import com.appnativa.rare.FunctionCallbackWaiter.CallbackResult;
import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iConstants;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.JavaURLConnection;
import com.appnativa.rare.net.iMultipartMimeHandler.iMultipart;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.GridPane;
import com.appnativa.rare.spot.Region;
import com.appnativa.rare.spot.SplitPane;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.ChangeEvent;
import com.appnativa.rare.ui.event.aViewListenerAdapter;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.util.DataItemCSVParser;
import com.appnativa.rare.util.DataItemParserHandler;
import com.appnativa.rare.viewer.CollapsiblePaneViewer;
import com.appnativa.rare.viewer.DocumentPaneViewer;
import com.appnativa.rare.viewer.GridPaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.ToolBarViewer;
import com.appnativa.rare.viewer.WebBrowser;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.ObjectHolder;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONObject;
import com.google.j2objc.annotations.Weak;
import com.sparseware.bellavista.external.aAttachmentHandler;

public class Document extends aWorkerTask {
  protected String                        documentID;
  protected ActionLink                    documentLink;
  protected List<DocumentItem>            docAttachments;
  protected DocumentItem                  mainDocument;
  protected iWidget                       contextWidget;
  protected iFunctionCallback             callback;
  private static final RenderableDataItem _documentItem        = new RenderableDataItem("document");
  private static String                   VIEWER_CALLBACK_ID   = "viewer";
  private static String                   DOCUMENT_CALLBACK_ID = "document";
  Date                                    documentDate;
  String                                  documentTitle;
  static String                           bodyOutOfBandText;
  static GridPane                         documentViewerCfg;

  public Document(iWidget widget, ActionLink link, String id) {
    this.documentLink = link;
    this.documentID = id;
    contextWidget = widget;
  }

  /**
   * Adds an attachment to this document
   * 
   * @param type
   *          the type of attachment
   * @param date
   *          the attachment's date
   * @param title
   *          the attachment's date
   * @param href
   *          the attachment's href
   */
  public void addAttachment(DocumentItemType type, Date date, String title, String href) {
    addAttachment(type, date, title, null, null, href);
  }

  /**
   * Adds an attachment to this document
   * 
   * @param type
   *          the type of attachment
   * @param date
   *          the attachment's date
   * @param title
   *          the attachment's date
   * @param mimeType
   *          the attachment's date
   * @param body
   *          the attachment's body
   * @param href
   *          the attachment's href (required if there is no body)
   */
  public void addAttachment(DocumentItemType type, Date date, String title, String mimeType, String body, String href) {
    final WindowViewer w = Platform.getWindowViewer();
    RenderableDataItem row = w.createRow(5, true);
    row.get(0).setValue(type.toString().toLowerCase(Locale.US));
    row.get(1).setValue(title);
    RenderableDataItem item = row.get(2);
    item.setType(RenderableDataItem.TYPE_DATETIME);
    item.setValue(date);
    row.get(3).setValue("false");
    row.get(4).setValue(href);
    DocumentItem di = new DocumentItem(body, mimeType, type);
    di.rowData = row;
    if (docAttachments == null) {
      docAttachments = new ArrayList<Document.DocumentItem>(5);
    }
    docAttachments.add(di);
  }

  static void staticInitialize() throws Exception {
    documentViewerCfg = (GridPane) Platform.getWindowViewer().createConfigurationObject(new ActionLink("/document_viewer.rml"));
    JSONObject info = (JSONObject) Platform.getAppContext().getData("notesInfo");
    bodyOutOfBandText = info.optString("bodyOutOfBandText", "");
    if (bodyOutOfBandText.length() == 0) {
      bodyOutOfBandText = null;
    }
  }

  @Override
  public Object compute() {
    ActionLink link = documentLink;
    try {
      if (!link.isMultipartDocument()) {
        String type = link.getContentType();
        String data = link.getContentAsString();

        if (Utils.isDemo() && data.contains("SIRF_MULTIPART_BOUNDARY_PART")) { // support
                                                                               // multi-part
                                                                               // in
                                                                               // demo
                                                                               // mode
          link = new ActionLink(data, "multipart/mixed; boundary=\"__FF00_SIRF_MULTIPART_BOUNDARY_PART_OOFF__\"");
          link.setContext(contextWidget);
        } else {
          mainDocument = new DocumentItem(data, type, DocumentItemType.DOCUMENT);
          setMainDocumentInfo(documentDate, documentTitle);
          return null;
        }
      }
      iMultipart mp = link.getFirstPart(contextWidget);
      int i = 0;
      String s = mp.getPreamble();
      List<RenderableDataItem> items = null;
      List<DocumentItem> docs = new ArrayList<DocumentItem>(5);

      if (s != null) {
        s = s.trim();

        if (s.length() != 0) {
          DataItemParserHandler ph = new DataItemParserHandler(contextWidget);
          DataItemCSVParser p = new DataItemCSVParser(new StringReader(s), Utils.colSeparator, Utils.ldSeparator);

          p.parse(contextWidget, -1, ph);
          items = ph.getListEx();
        }
      }

      RenderableDataItem row;
      int len = (items == null) ? 0 : items.size();

      while (mp != null) {
        DocumentItem doc = new DocumentItem();

        doc.mimeType = mp.getContentType();
        doc.itemBody = mp.getContentAsString();

        if (i < len) {
          row = items.get(i++);
          if (bodyOutOfBandText != null && doc.itemBody.startsWith(bodyOutOfBandText)) {
            doc.itemBody = null;
          }
          doc = processDocumentRow(doc, row);
        }

        if ((doc != null) && (mainDocument != doc)) {
          docs.add(doc);
        }

        mp = mp.nextPart();
      }

      while (i < len) {
        DocumentItem doc = new DocumentItem();

        row = items.get(i++);
        processDocumentRow(doc, row);
        docs.add(doc);
      }

      if ((mainDocument == null) && (docs.size() > 0)) {
        mainDocument = docs.remove(0);
      }
      if (mainDocument.rowData == null) {
        setMainDocumentInfo(documentDate, documentTitle);
      } else {
        if (documentTitle != null) {
          mainDocument.rowData.get(1).setValue(documentTitle);
        }
        if (documentDate != null) {
          mainDocument.rowData.get(2).setValue(documentDate);
        }
      }
      if (docAttachments != null) {
        docAttachments.addAll(0, docs);
      } else {
        docAttachments = docs;
      }
    } catch (Exception e) {
      return e;
    } finally {
      link.close();
      if (link != documentLink) {
        documentLink.close();
      }
    }
    return null;
  }

  /**
   * Disposes of the document
   */
  public void dispose() {
    if (mainDocument != null) {
      mainDocument.dispose();
    }
    if (docAttachments != null) {
      for (DocumentItem d : docAttachments) {
        d.dispose();
      }
      docAttachments.clear();
    }
    if (documentLink != null) {
      documentLink.clear();
    }
    contextWidget = null;
    mainDocument = null;
    docAttachments = null;
    documentLink = null;
    callback = null;
    this.window = null;
  }

  @Override
  public void finish(Object result) {
    if (callback != null) {
      callback.finished(result != null, result == null ? this : result);
      callback = null;
    }
  }

  public DocumentItem getAttachment(int index) {
    return docAttachments.get(index);
  }

  public int getAttachmentCount() {
    return docAttachments == null ? 0 : docAttachments.size();
  }

  public String getID() {
    return documentID;
  }

  /**
   * Loads the document an calls the specified callback when the document is
   * loaded
   * 
   * @param cb
   *          the callback
   */
  public void load(iFunctionCallback cb) {
    this.callback = cb;
    Platform.getWindowViewer().spawn(this);
  }

  /**
   * Called to load the document and create the viewer represented by the
   * specified link and then populate and activate the viewer. The link contains
   * the name of the target where the viewer is to be placed.
   * 
   * @param viewerLink
   *          the link for the viewer
   * @throws Exception
   */
  public void loadAndPopulateViewer(final ActionLink viewerLink) throws Exception {
    final WindowViewer w = Platform.getWindowViewer();
    final FunctionCallbackWaiter waiter = new FunctionCallbackWaiter();
    w.createViewer(viewerLink, waiter.createCallback(VIEWER_CALLBACK_ID));
    load(waiter.createCallback(DOCUMENT_CALLBACK_ID));

    iFunctionCallback cb = new iFunctionCallback() {

      @Override
      public void finished(boolean canceled, Object returnValue) {
        try {
          ObjectHolder h = (ObjectHolder) returnValue;
          HashMap<Object, CallbackResult> resultsMap = (HashMap<Object, CallbackResult>) h.value;
          CallbackResult vr = resultsMap.get(VIEWER_CALLBACK_ID);
          CallbackResult dr = resultsMap.get(DOCUMENT_CALLBACK_ID);
          if (vr.resultIsError()) {
            Utils.handleError((Throwable) vr.getReturnValue());
            return;
          }
          if (dr.resultIsError()) {
            Utils.handleError((Throwable) dr.getReturnValue());
            return;
          }
          iViewer v = (iViewer) vr.getContent();
          populateViewer(v.getFormViewer());
          String target = viewerLink.getTargetName();
          if (target == null) {
            target = iTarget.TARGET_WORKSPACE;
          }
          if (iTarget.TARGET_WORKSPACE.equals(target)) {
            Utils.pushWorkspaceViewer(v);
          } else {
            w.activateViewer(v, target);
          }
        } finally {
          w.hideWaitCursor();
        }
      }
    };
    w.showWaitCursor();
    waiter.startWaiting(cb);
  }

  /**
   * Loads the document and populates the specified viewer
   * 
   * @param fv
   *          the viewer to populate
   */
  public void loadAndPopulateViewer(final iContainer fv) {
    final WindowViewer w=Platform.getWindowViewer();
    iFunctionCallback cb = new iFunctionCallback() {

      @Override
      public void finished(boolean canceled, Object returnValue) {
        w.hideWaitCursor();
        
        if (returnValue instanceof Exception) {
          Utils.handleError((Throwable) returnValue);
        } else {
          populateViewer(fv);
        }
      }
    };
    w.showWaitCursor();
    load(cb);
  }

  /**
   * Populates a form with the contents of this a document and attachments
   * document. The document is assumed to already be loaded
   * 
   * @param fv
   *          the form
   */
  public void populateViewer(iContainer fv) {
    fv.setLinkedData(this);
    populateViewer(fv, mainDocument);
    WindowViewer w = Platform.getWindowViewer();
    CollapsiblePaneViewer cpv = (CollapsiblePaneViewer) fv.getWidget("attachments");
    if (cpv != null) {
      TableViewer table = (TableViewer) cpv.getWidget();
      iActionListener al = new AttachmentActionListener();
      table.clear();
      table.setActionListener(al);
      int len = getAttachmentCount();
      cpv.getTarget().setVisible(len > 0);
      if (len > 0) {
        cpv.setTitleText(w.getString("bv.format.attachments_spec", StringCache.valueOf(len)));
        cpv.setActionListener(al);
        // we can change this because we have already populated the form
        mainDocument.rowData.get(1).setValue(w.getString("bv.text.master_document"));
        boolean hasDate = false;
        for (int i = 0; i < len; i++) {
          DocumentItem item = docAttachments.get(i);
          if (item.rowData != null) {
            table.addEx(item.rowData);
            if (item.hasDate()) {
              hasDate = true;
            }
          }
        }
        if (UIScreen.isMediumDensity()) {
          table.addEx(mainDocument.rowData);
        }
        table.setColumnVisible(2, hasDate);
        table.refreshItems();
      }
    }
  }

  /**
   * Populates a form with the contents of a document item beloing to this
   * document
   * 
   * @param fv
   *          the form
   * @param the
   *          document item
   */
  public void populateViewer(iContainer fv, DocumentItem doc) {
    try {
      RenderableDataItem row = doc.rowData;
      iWidget field;

      field = fv.getWidget("documentDate");
      String s;
      if (field != null) {
        s = row == null ? "" : Functions.convertDateTime(field, row.get(2).getValue());
        field.setValue(s);
      }

      field = fv.getWidget("documentTitle");
      if (field != null) {
        s = doc.itemTitle;
        if (s == null && row != null) {
          s = row.get(1).toString();
        }
        field.setValue(s == null ? "" : s);
      }

      field = fv.getWidget("extraHeaderDetail");

      if (field != null) {
        s = "";
        if ((row != null) && (row.size() > 4)) {
          s = field.getValueAsString();
          if ((s != null) && (s.length() != 0)) {
            String title = (String) row.get(4).getValue();
            s = String.format(s, title);
          } else {
            s = (String) row.get(4).getValue();
          }
        }
        field.setValue(s);
      }

      iViewer v = (iViewer) fv.getWidget("documentDetail");

      if (v != null) {
        boolean html = false;

        if ((doc.mimeType != null) && doc.mimeType.startsWith(iConstants.HTML_MIME_TYPE)) {
          html = true;
        }
        if (v instanceof DocumentPaneViewer) {
          ((DocumentPaneViewer) v).setText(doc.itemBody, html);
        } else if (v instanceof WebBrowser) {
          s = doc.itemBody;

          if (!html) {
            s = "<html><body><pre>" + s + "</pre></body></html>";
          }
          String href = JavaURLConnection.baseToExternalForm(documentLink.getURL(contextWidget));
          final WebBrowser browser = (WebBrowser) v;
          if (v.getWidth() < 50) {
            /**
             * We need to wait until the browser has been sized before we load
             * the content so the the content will be appropriately zoomed for
             * iOS
             * 
             * Setting the 'autoInsertMetaContent' to true in the custom
             * properties in the RML file Causes a 'viewport' meta tag to be
             * inserted and set to the size of the browser.
             */
            final String data = s;
            final String url = href;
            browser.getContainerComponent().addViewListener(new aViewListenerAdapter() {
              @Override
              public void viewResized(ChangeEvent e) {
                if (!browser.isDisposed() && browser.getWidth() > 50) {
                  browser.getContainerComponent().removeViewListener(this);
                  browser.setHTML(url, data);
                }
              }

              @Override
              public boolean wantsResizeEvent() {
                return true;
              }
            });
          } else {
            browser.setHTML(href, s);
          }
        }
      }
    } catch (Exception e) {
      Utils.handleError(e);
    }
  }

  /**
   * Sets information about the main document.
   *
   * @param body
   *          the body of the document
   * @param type
   *          the type of the document
   * @param date
   *          the document date
   * @param title
   *          the document title
   */
  public void setMainDocument(String body, String type, Date date, String title) {
    mainDocument = new DocumentItem(body, type, DocumentItemType.DOCUMENT);
    setMainDocumentInfo(date, title);
  }

  /**
   * Sets information about the main document. This information generally comes
   * from the table from which the document was selected.
   * 
   * @param date
   *          the document date
   * @param title
   *          the document title
   */
  public void setMainDocumentInfo(Date date, String title) {
    if (mainDocument != null) {
      RenderableDataItem row = new RenderableDataItem();

      row.add(_documentItem);
      row.add(new RenderableDataItem(title));
      row.add(new RenderableDataItem(date));
      mainDocument.rowData = row;
    } else {
      documentTitle = title;
      documentDate = date;
    }
  }

  /**
   * Populates the specified document item with data for information in the
   * attachment row item.
   *
   * @param doc
   *          the document item
   * @param row
   *          the attachment row information
   * @return the passed in doc item
   */
  protected DocumentItem processDocumentRow(DocumentItem doc, RenderableDataItem row) {
    if (row.size() > 3) {
      DocumentItemType type = DocumentItemType.DOCUMENT;
      try {
        String s = row.get(0).toString();
        String custom = null;
        int n = s.indexOf(':');
        if (n != -1) {
          custom = s.substring(n + 1);
          s = s.substring(0, n);
        }
        s = s.toUpperCase(Locale.US);
        type = DocumentItemType.valueOf(s);
        if (type == DocumentItemType.CUSTOM) {
          doc.customItemType = custom;
        }
      } catch (Exception e) {
        Platform.ignoreException(null, e);
      }
      doc.itemType = type;
      switch (type) {
        case DOCUMENT:
          if ("true".equals(row.get(3).getValue())) {
            mainDocument = doc;
          }
          break;
        case IMAGE:
          row.get(0).setIcon(Platform.getAppContext().getResourceAsIcon("bv.icon.xray"));
          break;
        default:
          break;
      }
      row.get(2).setType(RenderableDataItem.TYPE_DATETIME);
    }

    doc.rowData = row;

    return doc;
  }
  
  /**
   * Creates a new document viewer. This call should only be made from
   * a background task
   * 
   * @param parent the parent viewer
   * @param hasAttachments true if the document for the viewer has attachments; false otherwise
   * @return the new document viewer
   */
  public static GridPaneViewer createDocumentViewer(iContainer parent,boolean hasAttachments) {
    Region r = (Region) documentViewerCfg.regions.getEx(documentViewerCfg.regions.size() - 1);
    r = (Region) ((SplitPane)r.viewer.getValue()).regions.getEx(documentViewerCfg.regions.size() - 1);
    r.visible.setValue(hasAttachments);
    return (GridPaneViewer) Platform.getWindowViewer().createViewer(parent, Document.documentViewerCfg);
  }
  
  /**
   * Class to hold document attachment items
   *
   * @author Don DeCoteau
   */
  public static class DocumentItem {
    public DocumentItemType   itemType = DocumentItemType.DOCUMENT;
    public String             itemBody;
    public String             mimeType;
    public RenderableDataItem rowData;
    public String             itemTitle;
    public String             customItemType;
    public aAttachmentHandler handler;

    public DocumentItem() {
    }

    public DocumentItem(String body, String mimeType, DocumentItemType itemType) {
      this.itemBody = body;
      this.mimeType = mimeType;
      this.itemType = itemType;
    }

    public void dispose() {
      if (handler != null) {
        handler.dispose();
      }
      if (rowData != null) {
        rowData.clear();
      }
      handler = null;
      rowData = null;
    }

    public Date getDate() {
      if (rowData != null) {
        return (Date) rowData.get(2).getValue();
      }
      return null;
    }

    public String getHREF() {
      if (rowData != null) {
        RenderableDataItem item = rowData.getItemEx(4);
        if (item != null) {
          return item.toString();
        }
      }
      return null;
    }

    public String getDateString() {
      if (rowData != null) {
        return rowData.get(2).toString();
      }
      return null;
    }

    public aAttachmentHandler getHandler() {
      if (handler == null) {
        handler = Utils.getAttachmentHandler(itemType);
      }
      return handler;
    }

    public String getTitle() {
      if (itemTitle == null) {
        if (rowData != null) {
          itemTitle = rowData.get(1).toString();
        } else {
          itemTitle = "";
        }
      }
      return itemTitle;
    }

    public boolean hasDate() {
      RenderableDataItem item = rowData.getItemEx(2);
      return item != null && item.getValue() != null;
    }

    @Override
    public String toString() {
      return itemBody;
    }
  }

  public static enum DocumentItemType {
    DOCUMENT, IMAGE, AUDIO, VIDEO, CUSTOM
  }

  /**
   * Called when an item in the attachments list is clicked on
   */
  static class AttachmentActionListener implements iActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      final WindowViewer w = Platform.getWindowViewer();
      TableViewer table = (TableViewer) e.getWidget();
      iFormViewer fv = table.getFormViewer();
      Document doc = (Document) fv.getLinkedData();
      int n = table.getSelectedIndex();
      DocumentItem item = doc.getAttachment(n);
      switch (item.itemType) {
        case DOCUMENT:
          if (item.itemBody == null) {
            handleOutOfBandAttachment(doc, fv, item);
          } else {
            doc.populateViewer(fv, item);
          }
          break;
        default:
          aAttachmentHandler handler = item.getHandler();
          if (handler == null) {
            w.alert(w.getString("bv.text.cant_view_attachment"));
          } else {
            w.showWaitCursor();
            StackPaneViewer sp = (StackPaneViewer) Utils.getStackPaneViewer(fv);
            GridPaneViewer gp = Utils.createGenericContainerViewer(sp);
            n = sp.addViewer(null, gp);
            sp.switchTo(n);
            handler.createViewer(gp, doc, item, new AttachmentCallback(gp));
          }
          break;
      }
    }

    /**
     * Handles the case where we need to retrieve the body of an attachment
     * 
     * @param doc
     *          the document the attachment belongs to
     * @param fv
     *          the form viewer to be populated
     * @param item
     *          the attachment item
     */
    void handleOutOfBandAttachment(final Document doc, final iFormViewer fv, final DocumentItem item) {
      final WindowViewer w = Platform.getWindowViewer();
      String href = item.getHREF();
      try {
        String id = href;
        int n = href.lastIndexOf('/');
        if (n != -1) {
          id = href.substring(n + 1);
        }
        n = id.lastIndexOf('.');
        if (n == -1) {
          href += ".html";
        } else {
          id = id.substring(0, n);
        }
        ActionLink link = new ActionLink(fv.getURL(href));
        final Document ndoc = new Document(fv, link, id);
        ndoc.load(new iFunctionCallback() {

          @Override
          public void finished(boolean canceled, Object returnValue) {
            w.hideWaitCursor();
            if (returnValue instanceof Throwable) {
              Utils.handleError((Throwable) returnValue);
            } else if (canceled || fv.isDisposed()) {
              return;
            }
            ndoc.mainDocument.rowData=item.rowData;
            if (ndoc.getAttachmentCount() == 0) {
              doc.populateViewer(fv, ndoc.mainDocument);
            } else {
              StackPaneViewer sp = Utils.getStackPaneViewer(fv);
              GridPaneViewer gp = createDocumentViewer(sp, true);
              iWidget field = gp.getWidget("backButton");
              if (field != null) {
                field.setVisible(true);
              }
              int pos = sp.addViewer(null, gp);
              ndoc.populateViewer(gp);
              sp.switchTo(pos);
            }
          }
        });
        w.showWaitCursor();
      } catch (MalformedURLException e) {
        Utils.handleError(e);
      }
    }
  }

  /**
   * This is called by a media handler when it has finished creating a viewer to
   * handle the media of an attachment;
   */
  static class AttachmentCallback implements iFunctionCallback {
    @Weak
    GridPaneViewer gridPane;

    AttachmentCallback(GridPaneViewer gp) {
      gridPane = gp;
    }

    @Override
    public void finished(boolean canceled, Object returnValue) {
      final WindowViewer w = Platform.getWindowViewer();
      w.hideWaitCursor();
      if (!canceled) {
        if (returnValue instanceof Throwable) {
          Utils.handleError((Throwable) returnValue);
        } else {
          ObjectHolder oh = (ObjectHolder) returnValue;
          DocumentItem ditem = (DocumentItem) oh.type;
          aAttachmentHandler handler = (aAttachmentHandler) oh.source;
          iViewer v = (iViewer) oh.value;
          final GridPaneViewer gp = gridPane;
          if (gp.isDisposed()) {
            v.dispose();
            return;
          }
          gp.setViewer(1, v);
          ToolBarViewer tb = (ToolBarViewer) gp.getWidget("genericToolbar");
          if (tb != null) {
            iWidget field = tb.getWidget("genericLabel");
            if (field != null) {
              String s = ditem.getTitle();
              String date = ditem.getDateString();
              if (date != null) {
                s = s + " - " + date;
              }

              field.setValue(s);
            }
            List<iWidget> buttons = handler.getToolbarWidgets(v);
            if (buttons != null) {
              tb.addSeparator();
              for (iWidget b : buttons) {
                tb.addWidget(b);
              }
            }
          }
        }
      }
    }
  }
}
