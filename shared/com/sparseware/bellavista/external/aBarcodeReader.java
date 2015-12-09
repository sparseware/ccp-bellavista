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

package com.sparseware.bellavista.external;

import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.ui.iPlatformIcon;

/**
 * This class provides a base implementation for barcode readers
 *
 * @author Don DeCoteau
 *
 */
public abstract class aBarcodeReader {

  /**
   * Creates a new instance
   */
  protected aBarcodeReader() {}

  /**
   * Get the text for the barcode button
   *
   * @return the text for the barcode button or null to use the default
   */
  public String getButtonText() {
    return null;
  }

  /**
   * Get the icon for the barcode button
   *
   * @return the icon for the barcode button or null to use the default
   */
  public iPlatformIcon getButtonIcon() {
    return null;
  }

  /**
   * Gets whether or not the barcode reader service is currently available
   *
   * @return true if it is available; false otherwise
   */
  public abstract boolean isReaderAvailable();

  /**
   * Called to initiate barcode reading
   *
   * @param resultCallback
   *          the callback to be called with a list of one or more patients or null or an error
   */
  public abstract void read(iFunctionCallback resultCallback);

  /**
   * Call to dispose of this object. All background services should be stopped
   * and all resources released.
   */
  public abstract void dispose();
}
