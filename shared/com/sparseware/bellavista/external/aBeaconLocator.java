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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.event.iChangeListener;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.util.SNumber;
import com.appnativa.util.StringCache;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.PatientSelect;
import com.sparseware.bellavista.Utils;

/**
 * The class provides generic support for beacon-based patients and locations
 * locator service. The can be bluetooth beacons or RFID/NFC tags
 *
 * @author Don DeCoteau
 *
 */
public abstract class aBeaconLocator extends aPatientLocator implements iChangeListener {

  /** location beacons to monitor */
  protected List<Beacon> locationBeacons;

  /** patient beacons to monitor */
  protected List<Beacon> patientBeacons;

  /** platform specific beacon support class */
  protected aBeaconLocatorSupport locatorSupport;

  /** tracks if we are listening for patients */
  protected boolean listeningForPatients;

  /** tracks if we are listening for locations */
  protected boolean listeningForLocations;

  /** list of nearby patients */
  protected ArrayList<RenderableDataItem> nearbyPatients = new ArrayList<RenderableDataItem>();

  /** list of nearby locations */
  protected ArrayList<RenderableDataItem> nearbyLocations = new ArrayList<RenderableDataItem>();

  /** the column that will hold the locations signal item */
  protected int locationsSignalColumn;

  /** a pattern for verifying beacon id's */
  protected Pattern idPattern;

  /**
   * Create as new instance
   */
  public aBeaconLocator() {
    locatorSupport = createBeaconSupport();
    idPattern      = createIDPattern();
    locatorSupport.setChangeListener(this);

    if (locatorSupport.isAvailable()) {
      JSONObject info = (JSONObject) Platform.getAppContext().getData("patientSelectInfo");

      info = info.optJSONObject("beaconInfo");

      if (info != null) {
        locationBeacons       = getBeacons(info.optJSONArray("locationBeacons"));
        patientBeacons        = getBeacons(info.optJSONArray("patientBeacons"));
        locationsSignalColumn = info.optInt("locationsSignalColumn", 1);
      }

      if (locationBeacons != null) {
        locatorSupport.setLocationBeacons(locationBeacons);
      }

      if (patientBeacons != null) {
        locatorSupport.setPatientBeacons(patientBeacons);
      }
    }
  }

  @Override
  public void dispose() {
    locatorSupport.dispose();
    locatorSupport = null;
  }

  /**
   * Creates the beacon support object
   */
  protected abstract aBeaconLocatorSupport createBeaconSupport();

  /**
   * Creates a pattern for verifying beacon id's
   *
   * @return
   */
  protected abstract Pattern createIDPattern();

  @Override
  public void getNearbyLocations(EventObject event, final iFunctionCallback cb) {
    LocatorChangeEvent      ce  = (LocatorChangeEvent) event;
    HashMap<String, Beacon> map = (HashMap<String, Beacon>) ce.getData();

    if ((map != null) &&!map.isEmpty()) {
      resolveBeacons("/hub/main/util/lists/nearby_locations", map, nearbyLocations, locationsSignalColumn, cb);

      return;
    }

    Platform.invokeLater(new Runnable() {
      @Override
      public void run() {
        cb.finished(false, new ArrayList<RenderableDataItem>(nearbyLocations));
      }
    });
  }

  @Override
  public void getNearbyPatients(EventObject event, final iFunctionCallback cb) {
    LocatorChangeEvent      ce  = (LocatorChangeEvent) event;
    HashMap<String, Beacon> map = (HashMap<String, Beacon>) ce.getData();

    if ((map != null) &&!map.isEmpty()) {
      resolveBeacons("/hub/main/util/patients/nearby", map, nearbyPatients, PatientSelect.SIGNAL, cb);

      return;
    }

    final List<RenderableDataItem> patients = copyList(nearbyPatients);

    Platform.invokeLater(new Runnable() {
      @Override
      public void run() {
        cb.finished(false, new ArrayList<RenderableDataItem>(patients));
      }
    });
  }

  @Override
  public void ignoreEvent(EventObject e) {}

  @Override
  public boolean isNearbyLocationsSupported() {
    return locationBeacons != null;
  }

  @Override
  public boolean isNearbyPatientsSupported() {
    return patientBeacons != null;
  }

  @Override
  public void startListeningForNearbyLocations() {
    listeningForLocations = true;
    locatorSupport.startListeningForLocations();
  }

  @Override
  public void startListeningForNearbyPatients() {
    if (locatorSupport.wasAccessDenied()) {
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          fireStactChanged(new LocatorChangeEvent(this, LocatorChangeType.ACCESS_DENIED));
        }
      });
    } else {
      listeningForPatients = true;
      locatorSupport.startListeningForPatients();
    }
  }

  @Override
  public void stateChanged(EventObject e) {
    LocatorChangeEvent       ce   = (LocatorChangeEvent) e;
    int                      col  = 0;
    List<RenderableDataItem> list = null;

    switch(ce.getChangeType()) {
      case PATIENTS :
        if (listeningForPatients && (changeListener != null)) {
          list = nearbyPatients;
          col  = PatientSelect.SIGNAL;
        }

        break;

      case LOCATIONS :
        if (listeningForLocations && (changeListener != null)) {
          list = nearbyLocations;
          col  = locationsSignalColumn;
        }

        break;

      case ACCESS_DENIED :
        return;

      default :
        fireStactChanged(ce);

        break;
    }

    if (list != null) {
      List<Beacon>            beacons = (List<Beacon>) ce.getData();
      HashMap<String, Beacon> map     = new HashMap<String, Beacon>(beacons.size());

      for (Beacon b : beacons) {
        if (b.proximity >= 0) {
          map.put(b.toString(), b);
        }
      }

      synchronizeItemsWithBeacons(list, map, col);

      if (map.isEmpty()) {
        sortItemsAndUpdate(list, col);
      }

      LocatorChangeEvent lce = new LocatorChangeEvent(this, (list == nearbyPatients)
              ? LocatorChangeType.PATIENTS
              : LocatorChangeType.LOCATIONS, map);

      fireStactChanged(lce);
    }
  }

  @Override
  public void stopListeningForNearbyLocations() {
    listeningForLocations = false;
    locatorSupport.stopListeningForLocations();
  }

  @Override
  public void stopListeningForNearbyPatients() {
    listeningForPatients = false;
    locatorSupport.stopListeningForPatients();
  }

  /**
   * Creates a copy of each item and add it to a new list
   *
   * @param list
   *          the list
   * @return a new list with the copied items
   */
  protected List<RenderableDataItem> copyList(List<RenderableDataItem> list) {
    int                                 len   = list.size();
    final ArrayList<RenderableDataItem> nlist = new ArrayList<RenderableDataItem>(len);

    for (int i = 0; i < len; i++) {
      nlist.add(list.get(i).copy());
    }

    return nlist;
  }

  protected void fireStactChanged(final LocatorChangeEvent ce) {
    if (Platform.isUIThread()) {
      if (changeListener != null) {
        changeListener.stateChanged(ce);
      }
    } else {
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (changeListener != null) {
            changeListener.stateChanged(ce);
          }
        }
      });
    }
  }

  /**
   * Gets an array of uuid's from a configuration string
   *
   * @param value
   *          a configuration string
   * @return an array of uuid's or null
   */
  protected List<Beacon> getBeacons(JSONArray array) {
    int len = (array == null)
              ? 0
              : array.size();

    if (len == 0) {
      return null;
    }

    ArrayList<Beacon> list = new ArrayList<BluetoothBeaconLocator.Beacon>(len);
    Beacon            b;

    for (int i = 0; i < len; i++) {
      String s = array.getString(i);

      list.add(b = new Beacon(s));

      if (!idPattern.matcher(b.uuid).matches()) {
        throw new ApplicationException("Bad uuid:" + b.uuid);
      }
    }

    return list;
  }

  protected void removeBeaconsWithItems(List<RenderableDataItem> items, HashMap<String, Beacon> beacons, int col) {
    int len = items.size();

    for (int i = 0; i < len; i++) {
      RenderableDataItem item = items.get(i).get(col);
      String             id   = (String) item.getLinkedData();

      if (id != null) {
        beacons.remove(id);
      }
    }
  }

  protected boolean removeFromList(List<RenderableDataItem> items, HashMap<String, Beacon> beacons, int col) {
    int     len     = items.size();
    boolean removed = false;

    for (int i = len - 1; i > -1; i--) {
      RenderableDataItem item = items.get(i).get(col);
      String             id   = (String) item.getLinkedData();

      if ((id != null) && beacons.containsKey(id)) {
        items.remove(i);
        removed = true;
      }
    }

    return removed;
  }

  /**
   * Converts the beacons to a list of locations or patients
   *
   * @param url
   *          the url that will resolve the beacons
   * @param beacons
   *          the beacons
   * @param cb
   *          the callback to call with the results
   */
  protected void resolveBeacons(final String url, final HashMap<String, Beacon> beacons,
                                final List<RenderableDataItem> list, final int col, final iFunctionCallback cb) {
    final WindowViewer w    = Platform.getWindowViewer();
    aWorkerTask        task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          ActionLink l = Utils.createLink(w, url, true);

          if (!Utils.isDemo()) {
            HashMap          data = new HashMap(2);
            StringBuilder    sb   = new StringBuilder();
            Iterator<String> it   = beacons.keySet().iterator();

            while(it.hasNext()) {
              sb.append(it.next()).append("^");
            }

            sb.setLength(sb.length() - 1);
            data.put("beacons", sb.toString());
            l.sendFormData(w, data);
          }

          List<RenderableDataItem> items = w.parseDataLink(l, true);

          if (items != null) {
            if (Utils.isDemo() && (list == nearbyPatients)) {
              updateBeaconIsForDemo(beacons, items, col);
            }

            updateProximity(items, beacons, col);
            list.addAll(items);
            sortItemsAndUpdate(list, col);
          }

          return copyList(list);
        } catch(Exception e) {
          return e;
        }
      }
      @Override
      public void finish(Object result) {
        cb.finished(result instanceof Exception, result);
      }
    };

    w.spawn(task);
  }

  protected void sortItemsAndUpdate(List<RenderableDataItem> items, final int col) {
    Comparator<RenderableDataItem> c = new Comparator<RenderableDataItem>() {
      @Override
      public int compare(RenderableDataItem o1, RenderableDataItem o2) {
        o1 = o1.getItem(col);
        o2 = o2.getItem(col);

        return o1.getWidth() - o2.getWidth();
      }
    };

    Collections.sort(items, c);

    int len = items.size();

    for (int i = 0; i < len; i++) {
      RenderableDataItem item = items.get(i).getItem(col);

      item.setValue(StringCache.valueOf(getNumbersOfBars(item.getWidth())));
      item.setWidth(0);
    }
  }

  /**
   * The method synchronizes the beacons map an items list. If an item in the
   * list does not have a beacon then it is removed otherwise the item is
   * updated and the beacon is removed from the map
   *
   * @param items
   *          the items
   * @param beacons
   *          the beacons
   * @param col
   *          the column when the beacon id is stored
   */
  protected void synchronizeItemsWithBeacons(List<RenderableDataItem> items, HashMap<String, Beacon> beacons, int col) {
    int len = items.size();

    for (int i = len - 1; i > -1; i--) {
      RenderableDataItem item = items.get(i).get(col);
      String             id   = (String) item.getLinkedData();
      Beacon             b    = (id == null)
                                ? null
                                : beacons.get(id);

      if (b == null) {
        items.remove(i);
      } else {
        item.setWidth((int) (b.proximity * 1000));    //convert to centimeters
        beacons.remove(id);
      }
    }
  }

  /**
   * Updates the proximity for each item for that of the beacons
   *
   * @param items
   *          the items
   * @param beacons
   *          the beacons
   * @param col
   *          the column when the beacon id is stored
   */
  protected void updateProximity(List<RenderableDataItem> items, HashMap<String, Beacon> beacons, int col) {
    int len = items.size();

    for (int i = 0; i < len; i++) {
      RenderableDataItem item = items.get(i).get(col);
      String             id   = (String) item.getLinkedData();
      Beacon             b    = (id == null)
                                ? null
                                : beacons.get(id);

      if (b != null) {
        item.setWidth((int) (b.proximity * 1000));    //convert to centimeters
        beacons.remove(id);
      }
    }
  }

  /**
   * Converts the proximity value to a number of strength bars
   *
   * @param proximity
   *          the proximity in centimeters
   * @return the number of bars
   */
  protected int getNumbersOfBars(int proximity) {
    if (proximity < 1000) {
      return 5;
    }

    if (proximity < 5000) {
      return 4;
    }

    if (proximity < 10000) {
      return 3;
    }

    if (proximity < 15000) {
      return 2;
    }

    return 1;
  }

  protected HashMap<String, Beacon> createBeaconMap(List<Beacon> beacons) {
    HashMap<String, Beacon> map = new HashMap<String, Beacon>(beacons.size());

    for (Beacon b : beacons) {
      map.put(b.toString(), b);
    }

    return map;
  }

  protected void updateBeaconIsForDemo(final HashMap<String, Beacon> beacons, final List<RenderableDataItem> list,
          int col) {
    Iterator<String> it  = beacons.keySet().iterator();
    int              len = list.size();

    while(it.hasNext()) {
      String bid = it.next();
      int    n   = bid.lastIndexOf(';');

      if (n != -1) {
        String s = bid.substring(n + 1);

        for (int i = 0; i < len; i++) {
          RenderableDataItem row  = list.get(i);
          RenderableDataItem item = row.get(0);

          if (item.valueEquals(s)) {
            item = row.get(col);
            item.setLinkedData(bid);
          }
        }
      }
    }

    //remove items without beacons
    for (int i = len - 1; i > -1; i--) {
      RenderableDataItem row = list.get(i);

      if (row.get(col).getLinkedData() == null) {
        list.remove(i);
      }
    }
  }

  /**
   * Class representing a beacon
   *
   * @author Don DeCoteau
   *
   */
  public static class Beacon {
    public String uuid;
    public int    major;
    public int    minor;
    public float  proximity;

    public Beacon(String bstring) {
      int n = bstring.indexOf(';');

      uuid = (n == -1)
             ? bstring
             : bstring.substring(0, n);

      if (n != -1) {
        int p = bstring.indexOf(n + 1, ';');

        if (p != -1) {
          major = SNumber.intValue(bstring.substring(n + 1, p));
          minor = SNumber.intValue(bstring.substring(p + 1));
        }
      }
    }

    public Beacon(String uuid, int major, int minor) {
      super();
      this.uuid  = uuid;
      this.major = major;
      this.minor = minor;
    }

    public Beacon(String uuid, int major, int minor, float proximity) {
      this.uuid      = uuid;
      this.major     = major;
      this.minor     = minor;
      this.proximity = proximity;
    }

    /**
     * Returns if a found beacon is one of this type
     *
     * @param uuid
     *          the uuid value of the found beacon
     * @param major
     *          the major value of the found beacon
     * @return true if it is; false otherwise
     */
    public boolean isOneOfMine(String uuid, int major) {
      if (this.major == 0) {
        major = 0;
      }

      return this.uuid.equals(uuid) && (this.major == major);
    }

    @Override
    public String toString() {
      return uuid + ";" + major + ";" + minor;
    }
  }
}
