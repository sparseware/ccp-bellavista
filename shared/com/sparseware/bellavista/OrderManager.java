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

import java.util.HashMap;
import java.util.Map;

import com.appnativa.rare.ui.iPlatformIcon;
/**
 * This manages ordering
 * 
 * @author Don DeCoteau
 */

public class OrderManager {
  public static final int                    ORDERING_STATE_FLAGGED      = 1;
  public static final int                    ORDERING_STATE_DISCONTINUED = 2;
  public static final int                    ORDERING_STATE_REORDERED    = 4;
  private static Map<Integer, iPlatformIcon> stateIcons;

  public OrderManager() {
  }

  /**
   * Called to see if we can exit when the user initiates and exit or change
   * patient action.
   * 
   * If there are orders pending this method will return false and handle the
   * prompting of the user informing about un-submitted orders.
   * 
   * @param exit
   *          true if the user wants to exit; false if the user wants to change
   *          patient
   * @param path
   *          and action path to pass to the change patient logic.
   * 
   * @return true if we can exit ; false otherwise
   */
  public static boolean canChangePatientOrExit(final boolean exit, final ActionPath path) {
    return true;
  }

  /**
   * Called to see if we can exit when the device has been locked. If there are
   * orders pending this method will return false and handle the prompting of
   * the user informing about un-submitted orders.
   * 
   * @return true if we can exit ; false otherwise
   */
  public static boolean canExit() {
    return true;
  }

  public static Map<Integer, iPlatformIcon> getStateIcons() {
    if (stateIcons == null) {
      stateIcons = new HashMap<Integer, iPlatformIcon>(3);
      // TODO: set state icons;
    }
    return stateIcons;
  }
}
