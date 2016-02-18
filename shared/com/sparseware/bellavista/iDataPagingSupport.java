package com.sparseware.bellavista;

import com.appnativa.rare.widget.iWidget;

public interface iDataPagingSupport {
  void changePage(boolean next,iWidget nextPageWidget, iWidget previousPageWidget);
}
