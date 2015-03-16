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

import com.appnativa.rare.ui.RenderableDataItem;

/**
 * Interface for checking an individual rows's value when a list of rows is
 * being processed by a function
 * 
 * @author Don DeCoteau
 *
 */
public interface iValueChecker {
  /**
   * Called to check a row
   * 
   * @param row
   *          the row to be checked
   * @param index
   *          the index of the row
   * @param expandableColumn
   *          the expandable column if the row is for a tree table
   * @param rowCount
   *          the total number of rows
   * @return true to keep the row; false to remove from the list if deletion is supported
   */
  boolean checkRow(RenderableDataItem row, int index, int expandableColumn, int rowCount);
}
