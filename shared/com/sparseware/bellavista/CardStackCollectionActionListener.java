package com.sparseware.bellavista;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.appnativa.rare.Platform;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.viewer.StackPaneViewer;

/**
 * A card stack action handler that presents a list of items
 */
public class CardStackCollectionActionListener implements iActionListener {
  protected String                   title;
  protected List<RenderableDataItem> items;
  protected int                      column;

  /**
   * Creates a new instance
   * @param parent the parent to use when creating
   * @param title the title for the container
   * @param items the list of items
   *   @param column
   *            the column to use to create the displayed information (use -1 for
   *            one-dimensional lists)
   */
  public CardStackCollectionActionListener(String title, Collection<RenderableDataItem> items, int column) {
    this.title = title;

    if (items instanceof List) {
      this.items = (List<RenderableDataItem>) items;
    } else {
      this.items = new ArrayList<RenderableDataItem>(items);
    }

    this.column = column;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    StackPaneViewer sp = CardStackUtils.createListPagesViewer(null, Platform.getWindowViewer(), items, -1, column);

    Utils.pushWorkspaceViewer(sp, false);
  }
}
