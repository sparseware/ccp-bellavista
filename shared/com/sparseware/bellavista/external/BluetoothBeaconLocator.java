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

import java.util.regex.Pattern;

/**
 * The class provides generic support for bluetooth beacons patients and
 * locations locator service.
 *
 * @author Don DeCoteau
 *
 */
public class BluetoothBeaconLocator extends aBeaconLocator {

  /**
   * Create as new instance
   */
  public BluetoothBeaconLocator() {
    super();
  }

  @Override

  /**
   * Creates the beacon support object
   */
  protected aBeaconLocatorSupport createBeaconSupport() {
    return new BluetoothBeaconLocatorSupport();
  }

  @Override
  protected Pattern createIDPattern() {
    return Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
  }
}
