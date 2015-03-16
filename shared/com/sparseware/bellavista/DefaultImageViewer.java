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
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.iChangeListener;
import com.appnativa.rare.viewer.CarouselViewer;
import com.appnativa.rare.viewer.ImagePaneViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.ObjectHolder;
import com.sparseware.bellavista.Document.DocumentItem;
import com.sparseware.bellavista.external.aAttachmentHandler;

public class DefaultImageViewer extends aAttachmentHandler implements iChangeListener {
  public static int THUMBNAIL_URL_POSITION = 2;
  public static int IMAGE_URL_POSITION     = 3;

  public DefaultImageViewer() {
  }

  @Override
  public void createViewer(final iContainer parent, final Document document, final DocumentItem attachment,
      final iFunctionCallback cb) {
    final WindowViewer w = Platform.getWindowViewer();
    aWorkerTask task = new aWorkerTask() {

      @Override
      public void finish(Object result) {
        if (result instanceof Throwable) {
          cb.finished(true, result);
          return;
        }
        ObjectHolder oh = (ObjectHolder) result;
        iContainer v = (iContainer) w.createViewer(parent.getFormViewer(), (Viewer) oh.type);
        CarouselViewer slides = (CarouselViewer) v.getWidget("thumbNails");
        slides.addAll((List<RenderableDataItem>) oh.value);
        slides.addChangeListener(DefaultImageViewer.this);
        cb.finished(false, new ObjectHolder(DefaultImageViewer.this, attachment, v));
      }

      @Override
      public Object compute() {
        try {
          String id = document.getID();
          ActionLink link = Utils.createLink(w, "/hub/main/imaging/thumbnails/" + id, false);
          List<RenderableDataItem> list = w.parseDataLink(link, true);
          for (RenderableDataItem item : list) {
            item.setValue(item.get(THUMBNAIL_URL_POSITION).getValue());

          }
          Viewer cfg = (Viewer) w.createConfigurationObject(w.createActionLink("/image_viewer.rml"));
          return new ObjectHolder(cfg, list);
        } catch (Exception e) {
          return e;
        }
      }
    };
    Platform.getAppContext().executeWorkerTask(task);
  }

  @Override
  public List<iWidget> getToolbarWidgets(iViewer viewer) {
    return null;
  }

  @Override
  public void dispose() {
  }

  @Override
  public void stateChanged(EventObject e) {
    CarouselViewer slides = (CarouselViewer) Platform.getWidgetForComponent(e.getSource());
    ImagePaneViewer iv = (ImagePaneViewer) slides.getFormViewer().getWidget("imageViewer");
    RenderableDataItem item = (RenderableDataItem) slides.getSelection();
    iv.clearContents();
    if (item != null) {
      String src = (String) item.get(IMAGE_URL_POSITION).getValue();
      if ((src == null) || (src.length() == 0) || (iv == null) || src.equals(iv.getLinkedData())) {
        return;
      }
      iv.setLinkedData(src);
      iv.handleActionLink(new ActionLink(src), true);
    }
  }
}
