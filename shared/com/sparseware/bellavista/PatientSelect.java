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

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import com.appnativa.rare.Platform;
import com.appnativa.rare.aWorkerTask;
import com.appnativa.rare.iDataCollection;
import com.appnativa.rare.iFunctionCallback;
import com.appnativa.rare.iPlatformAppContext;
import com.appnativa.rare.iWorkerTask;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.ActionLink.RequestMethod;
import com.appnativa.rare.scripting.Functions;
import com.appnativa.rare.spot.StackPane;
import com.appnativa.rare.spot.TabPane;
import com.appnativa.rare.spot.Table;
import com.appnativa.rare.ui.ColorUtils;
import com.appnativa.rare.ui.RenderableDataItem;
import com.appnativa.rare.ui.UICompoundIcon;
import com.appnativa.rare.ui.UIImage;
import com.appnativa.rare.ui.UIImageIcon;
import com.appnativa.rare.ui.UIMenuItem;
import com.appnativa.rare.ui.UIPopupMenu;
import com.appnativa.rare.ui.UIScreen;
import com.appnativa.rare.ui.UISoundHelper;
import com.appnativa.rare.ui.UISpriteIcon;
import com.appnativa.rare.ui.aPlatformIcon;
import com.appnativa.rare.ui.iEventHandler;
import com.appnativa.rare.ui.iPlatformGraphics;
import com.appnativa.rare.ui.iPlatformIcon;
import com.appnativa.rare.ui.border.UILineBorder;
import com.appnativa.rare.ui.event.ActionEvent;
import com.appnativa.rare.ui.event.DataEvent;
import com.appnativa.rare.ui.event.iActionListener;
import com.appnativa.rare.ui.event.iChangeListener;
import com.appnativa.rare.viewer.ImagePaneViewer;
import com.appnativa.rare.viewer.StackPaneViewer;
import com.appnativa.rare.viewer.TableViewer;
import com.appnativa.rare.viewer.WindowViewer;
import com.appnativa.rare.viewer.aListViewer;
import com.appnativa.rare.viewer.iContainer;
import com.appnativa.rare.viewer.iFormViewer;
import com.appnativa.rare.viewer.iTarget;
import com.appnativa.rare.viewer.iViewer;
import com.appnativa.rare.widget.ComboBoxWidget;
import com.appnativa.rare.widget.aGroupableButton;
import com.appnativa.rare.widget.iWidget;
import com.appnativa.util.CharArray;
import com.appnativa.util.SNumber;
import com.appnativa.util.StringCache;
import com.appnativa.util.iFilter;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.sparseware.bellavista.CollectionManager.PatientList;
import com.sparseware.bellavista.Settings.AppPreferences;
import com.sparseware.bellavista.external.DemoPatientLocator;
import com.sparseware.bellavista.external.aBarcodeReader;
import com.sparseware.bellavista.external.aPatientLocator;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeEvent;
import com.sparseware.bellavista.external.aPatientLocator.LocatorChangeType;

/**
 * This class handles the selection of a patient. If provides support for
 * integrating barcode scanning for patient identification as well as an
 * interface for integration location based patient locator systems.
 *
 * @author Don DeCoteau
 */
public class PatientSelect implements iEventHandler, iChangeListener {
  public static final int            ADMIT_DATE             = 6;
  public static final int            ADMIT_DX               = 7;
  public static final int            DOB                    = 2;
  public static final int            DOCTOR                 = 5;
  public static final int            GENDER                 = 3;
  public static final int            ID                     = 0;
  public static final int            LOCATION               = 8;
  public static final int            MRN                    = 4;
  public static final int            NAME                   = 1;
  public static final int            PHOTO                  = 10;
  public static final int            SIGNAL                 = 11;
  public static final int            RM_BED                 = 9;
  static String                      lastPatientID;
  static String                      lastTabName;
  TableViewer                        patientsTable;
  iWidget                            locatorWidget;
  aPatientLocator                    patientLocator;
  aBarcodeReader                     barcodeReader;
  static iPlatformIcon               signalIcons[];
  public static UILineBorder         photoBorder;
  public static PatientIcon          noPhotoIcon;
  Class                              barcodeReaderClass;
  Class                              patientLocatorClass;
  RenderableDataItem                 patientListLoaded;
  RenderableDataItem                 noPatientsFound;
  RenderableDataItem                 noPatientListLoaded;
  RenderableDataItem                 searchingForPatients;
  boolean                            autoShowDefaultList;
  ListPager                          pager                  = new ListPager();
  int                                searchPageSize;
  CardPatientSelectionActionListener psActionListener;
  private UIPopupMenu                selectionMenu;
  private boolean                    alwaysShowSearchFirst;
  private boolean                    genderSearchSupported;
  static final String                PATIENT_SELECTION_TYPE = "pt_selection_type";
  static final String                PATIENT_SELECT_PAGE    = "pt_select_paage";
  static final String                PATIENT_SELECT         = "pt_select_patient";

  static {
    signalIcons = new iPlatformIcon[5];

    for (int i = 0; i < 4; i++) {
      signalIcons[i] = Platform.getResourceAsIcon("bv.icon.signal" + (i + 1));
    }

    signalIcons[4] = Platform.getResourceAsIcon("bv.icon.signal");
    photoBorder = new UILineBorder(ColorUtils.getColor("darkBorder"), UIScreen.platformPixelsf(1.25f), UIScreen.PLATFORM_PIXELS_6);
    noPhotoIcon = new PatientIcon(Platform.getResourceAsIcon("bv.icon.no_photo"));
  }

  public PatientSelect() {
    JSONObject info = (JSONObject) Platform.getAppContext().getData("patientSelectInfo");

    autoShowDefaultList = info.optBoolean("autoShowDefaultList", true);
    searchPageSize = info.optInt("searchPageSize", 0);

    String cls = info.optString("patientLocatorClass");

    if ((cls != null) && (cls.length() > 0)) {
      cls = Utils.resolveClassName(cls, "external");

      try {
        patientLocatorClass = Platform.loadClass(cls);
      } catch (ClassNotFoundException e) {
        Platform.debugLog("Patient locator class not found:" + cls);
      }
    }

    cls = info.optString("barcodeReaderClass");

    if ((cls != null) && (cls.length() > 0)) {
      try {
        cls = Utils.resolveClassName(cls, "external");
        barcodeReaderClass = Platform.loadClass(cls);
      } catch (ClassNotFoundException e) {
        Platform.debugLog("Barcode reader class not found:" + cls);
      }
    }

    if (Utils.isDemo()) {
      if (patientLocatorClass == null) {
        patientLocatorClass = DemoPatientLocator.class;
      }

    }

    alwaysShowSearchFirst = info.optBoolean("alwaysShowSearchFirst", false);
    genderSearchSupported = info.optBoolean("genderSearchSupported", true);

    String s = Platform.getResourceAsString("bv.text.patient_list_loaded");

    patientListLoaded = new RenderableDataItem(s);
    patientListLoaded.setEnabled(false);
    noPatientListLoaded = new RenderableDataItem();
    noPatientListLoaded.setEnabled(false);
    noPatientsFound = Utils.createDisabledTableRow("bv.text.no_patients_found", 1);
  }

  /**
   * Called when the barcode button is pressed.
   */
  public void onBarcodeButtonAction(String eventName, final iWidget widget, EventObject event) {
    if (barcodeReader != null) {
      final WindowViewer w = Platform.getWindowViewer();
      iFunctionCallback cb = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          Utils.setIgnorePausing(false);
          w.hideWaitCursor();

          if (widget.isDisposed()) {
            return;
          }

          if (returnValue == null) {
            w.beep();
          }

          if (returnValue instanceof Throwable) {
            Utils.alert(ApplicationException.getMessageEx((Throwable) returnValue));
          } else {
            if (returnValue == null) {
              returnValue = Collections.EMPTY_LIST;
            }

            try {
              processPatientsList(patientsTable, (List<RenderableDataItem>) returnValue, false);
            } catch (Exception e) {
              Utils.handleError(e);
            }
          }
        }
      };

      Utils.setIgnorePausing(true);
      w.showWaitCursor();
      barcodeReader.read(cb);
    }
  }

  /**
   * Called when the bookmark button is clicked.
   */
  public void onBookmarkButtonAction(String eventName, iWidget widget, EventObject event) {
    stopListeningForNearbyPatients();

    WindowViewer w = widget.getAppContext().getWindowViewer();
    iContainer fv = (iContainer) w.getViewer("patientSelectionForm");

    clearPreview(fv);

    if (!UIScreen.isLargeScreen()) {
      StackPaneViewer sp = (StackPaneViewer) widget.getFormViewer().getWidget("selectionStack");

      sp.switchTo(0);
    }

    loadPatientList(patientsTable, Utils.createLink(patientsTable, "/hub/main/util/patients/list", true), null, null);
  }

  /**
   * Called when the patient selection from is created
   */
  public void onCreated(String eventName, iWidget widget, EventObject event) {
    if (patientLocatorClass != null) {
      try {
        patientLocator = (aPatientLocator) patientLocatorClass.newInstance();
      } catch (Exception e) {
        Platform.ignoreException("faled to instantiate patient locator", e);
      }
    }

    if (barcodeReaderClass != null) {
      try {
        barcodeReader = (aBarcodeReader) barcodeReaderClass.newInstance();
      } catch (Exception e) {
        Platform.ignoreException("faled to instantiate barcode reader", e);
      }
    }

    if (Utils.isCardStack()) {
      StackPane cfg = (StackPane) ((DataEvent) event).getData();

      selectionMenu = new UIPopupMenu(Platform.getWindowViewer(), cfg.getPopupMenu());
      cfg.setPopupMenu(null);
    }
  }

  /**
   * Called when the patient selection from is loadex
   */
  public void onLoad(String eventName, iWidget widget, EventObject event) {
    if (Utils.isCardStack()) {
      Platform.getWindowViewer().getActionBar().setVisible(false);
    }
  }

  /**
   * Called when the patient selection view is disposed
   */
  public void onDispose(String eventName, iWidget widget, EventObject event) {
    if (Utils.isCardStack()) {
      if (patientsTable != null) {
        patientsTable.dispose();
      }
    }

    patientsTable = null;

    if (patientLocator != null) {
      patientLocator.dispose();
    }

    if (barcodeReader != null) {
      barcodeReader.dispose();
    }

    barcodeReader = null;
    patientLocator = null;
    locatorWidget = null;
    psActionListener = null;
  }

  @Override
  public void onEvent(String eventName, iWidget widget, EventObject event) {
  }

  /**
   * Called when the patient identifier field is shown. On android we will give
   * it focus. This will prevent the text field from automatically gaining focus
   * if the find nearby and bar code buttons are disabled
   */
  public void onIdentifierLabelShown(String eventName, final iWidget widget, EventObject event) {
    if (Platform.isAndroid()) {
      widget.requestFocus();
    } else if (!Platform.isTouchDevice()) {
      widget.getFormViewer().getWidget("identifier").requestFocus();
    }
  }

  /**
   * Called when a list category is selected
   */
  public void onListCategoriesAction(String eventName, iWidget widget, EventObject event) {
    PatientList pl = (PatientList) widget.getSelectionData();

    if (pl != null) {
      TableViewer table = patientsTable;

      if (pl.isContainsPatients()) {
        WindowViewer w = widget.getAppContext().getWindowViewer();
        iContainer fv = (iContainer) w.getViewer("patientSelectionForm");
        aGroupableButton pb = (aGroupableButton) fv.getWidget("bookmarkButton");

        if (pb != null) {
          pb.setSelected(false);
        }

        loadPatientList(table, pl.getCollectionLink(table), null, null);
      } else {
        aListViewer lb = (aListViewer) widget.getFormViewer().getWidget("listsBox");

        lb.setLinkedData(pl);
        lb.setDataURL(pl.getCollectionHREF());
      }
    }
  }

  public void onListsBoxFinishLoading(String eventName, iWidget widget, EventObject event) {
    aListViewer lb = (aListViewer) widget;

    if (lb.isEmpty()) {
      WindowViewer w = Platform.getWindowViewer();
      PatientList pl=(PatientList) lb.getLinkedData();
      noPatientListLoaded.setValue(w.getString("bv.format.no_lists",pl.title));
      lb.setAll(Arrays.asList(noPatientListLoaded));
    }
  }

  /**
   * Called when the most recent action is invoked
   */
  public void onMostRecentAction(String eventName, iWidget widget, EventObject event) {
    loadPatientList(patientsTable, Utils.createLink(patientsTable, "/hub/main/util/patients/most_recent", true), null, null);
  }

  /**
   * Called when the nearby patients button is pressed.
   */
  public void onNearbyPatientsAction(String eventName, final iWidget widget, EventObject event) {
    iContainer fv = (iContainer) Platform.getWindowViewer().getViewer("patientSelectionForm");
    aGroupableButton pb = (aGroupableButton) fv.getWidget("signalButton");

    if ((pb != null) && !pb.isVisible()) {
      if (pb.getIcon() == null) {
        UIImage sprite = Platform.getAppContext().getResourceAsImage("bv.icon.beacon.sprite");
        UISpriteIcon icon = new UISpriteIcon(sprite);

        pb.setIcon(icon);
      }

      pb.setVisible(true);
    }

    widget.setEnabled(false);
    pb = (aGroupableButton) fv.getWidget("bookmarkButton");

    if (pb != null) {
      pb.setSelected(false);
    }

    showNearbyPatients(widget);
  }

  /**
   * Called when the next or previous page button is pressed
   */
  public void onNextOrPreviousPage(String eventName, iWidget widget, EventObject event) {
    boolean next = widget.getName().equals("nextPage");
    ActionLink link = null;

    if (next) {
      link = pager.next();
    } else {
      link = pager.previous();
    }

    if (link == null) { //should not happen
      widget.setEnabled(false);
      UISoundHelper.beep();
    } else {
      loadPatientList(patientsTable, link, null, null);
    }
  }

  /**
   * Called when a specific patient list is selected from the list widget
   */
  public void onPatientListSelected(String eventName, iWidget widget, EventObject event) {
    WindowViewer w = widget.getAppContext().getWindowViewer();
    iContainer fv = (iContainer) w.getViewer("patientSelectionForm");
    String list = widget.getSelectionDataAsString();

    clearPreview(fv);

    aGroupableButton pb = (aGroupableButton) fv.getWidget("bookmarkButton");

    if (pb != null) {
      pb.setSelected(false);
    }

    PatientList pl = (PatientList) widget.getLinkedData();

    if (pl != null) {
      loadPatientList(patientsTable, pl.getListLink(patientsTable, list), null, null);
    }
  }

  /**
   * Called when the search button is pressed
   */
  public void onPatientSearch(String eventName, iWidget widget, EventObject event) {
    stopListeningForNearbyPatients();

    WindowViewer w = Platform.getWindowViewer();
    iFormViewer form = widget.getFormViewer();
    TableViewer table = patientsTable;

    pager.clear();

    String id = form.getWidget("identifier").getValueAsString();

    if ((id == null) || (id.length() == 0)) {
      w.beep();

      return;
    }

    String gender = ((aGroupableButton) form.getWidget("M")).getSelectedButtonName();
    StringBuilder sb = new StringBuilder();

    sb.append("/hub/main/util/patients/");

    ActionLink link;

    if (!Utils.isDemo()) {
      sb.append("list");

      HashMap<String, Object> attributes = new HashMap<String, Object>(2);

      attributes.put("identifier", id);

      if (gender != null) {
        attributes.put("gender", gender);
        gender = null;
      }

      if (searchPageSize > 0) {
        attributes.put("max", Integer.toString(searchPageSize));
      }

      id = null;
      link = Utils.createLink(table, sb.toString(), true);
      link.setAttributes(attributes);
      link.setRequestMethod(RequestMethod.POST);
    } else {
      sb.append("nearby"); //for the demo we filter the nearby list
      link = Utils.createLink(table, sb.toString(), true);
    }

    loadPatientList(table, link, id, gender);
  }

  /**
   * Called when the patient selection changes
   */
  public void onPatientsTableChange(String eventName, iWidget widget, EventObject event) {
    showPreview(patientsTable);
  }

  /**
   * Called when the patients table is created but not yet configured
   */
  public void onPatientsTableCreated(String eventName, final iWidget widget, EventObject event) {
    Table table = (Table) ((DataEvent) event).getData();
    float px = UIScreen.toPlatformPixelHeight("2ln", null, 0, false);
    float px54 = UIScreen.platformPixelsf(54);

    if (px < px54) {
      table.rowHeight.setValue("54");
    }

    if (autoShowDefaultList) {
      Platform.invokeLater(new Runnable() {
        @Override
        public void run() {
          loadPatientList((TableViewer) widget, Utils.createLink(patientsTable, "/hub/main/util/patients/list", true), null, null);
        }
      });
    }
  }

  /**
   * Called to configure the search form form
   */
  public void onSearchFormConfigure(String eventName, iWidget widget, EventObject event) {
    iFormViewer fv = widget.getFormViewer();
    aGroupableButton pb = (aGroupableButton) fv.getWidget("bv.action.scan_barcode");

    if (pb != null) {
      if (barcodeReader != null) {
        iPlatformIcon icon = barcodeReader.getButtonIcon();

        if (icon != null) {
          pb.setIcon(icon);
        }

        String text = barcodeReader.getButtonText();

        if (text != null) {
          pb.setText(text);
        }

        pb.setEnabled(barcodeReader.isReaderAvailable());
      } else {
        pb.setEnabled(false);
      }
    }

    pb = (aGroupableButton) fv.getWidget("bv.action.nearby_patients");

    if (pb != null) {
      if (patientLocator != null) {
        pb.setEnabled(patientLocator.isNearbyPatientsSupported());
      } else {
        pb.setEnabled(false);
      }
    }

    if (!genderSearchSupported) {
      iWidget gender = fv.getWidget("gender");

      if (gender != null) {
        gender.setVisible(false);
      }

      gender = fv.getWidget("genderLabel");

      if (gender != null) {
        gender.setVisible(false);
      }
    }
  }

  /**
   * Called when the search form is unloaded. We will stop looking for nearby
   * patients.
   */
  public void onSearchFormUnloaded(String eventName, final iWidget widget, EventObject event) {
    stopListeningForNearbyPatients();
  }

  /**
   * Called after the selection form has been configured
   */
  public void onSelectionFormConfigure(String eventName, final iWidget widget, EventObject event) {
    boolean cardStack = Utils.isCardStack();
    WindowViewer w = Platform.getWindowViewer();
    iFormViewer fv = widget.getFormViewer();

    patientsTable = (TableViewer) fv.getWidget("patientsTable");

    if (cardStack) {
      if (patientsTable == null) { //to keep things simple we will create a hidden table so that code that relys on the table can be used
        Table cfg = (Table) w.createConfigurationObject("Table", "bv.table.patients");

        cfg.spot_clearAttributes(); //remove event handlers so that they are not called
        patientsTable = (TableViewer) w.createWidget(cfg);
      }

      final StackPaneViewer sp = (StackPaneViewer) widget;

      CardStackUtils.createListItemsViewer(sp, null, null, selectionMenu, null, true, false);

      StackPane cfg = (StackPane) ((DataEvent) event).getData();
      final int n = cfg.selectedIndex.intValue();

      sp.switchTo(n);
    } else {
      final aGroupableButton pb = (aGroupableButton) fv.getWidget("patient");
      UIPopupMenu pm = pb.getPopupMenu();
      String format = w.getString("bv.format.goto");
      int len = pm.size();
      iActionListener al = new MenuActionListener();

      for (int i = 0; i < len; i++) {
        UIMenuItem mi = pm.getMenuItem(i);
        String name = mi.getName();
        String s = w.getString("bv.text." + name);

        mi.setText(Functions.format(format, s));
        mi.setActionListener(al);
      }

      if (!UIScreen.isSmallScreen()) {
        aGroupableButton pb2 = (aGroupableButton) fv.getWidget("bv.action.scan_barcode");

        if (pb2 != null) {
          if (barcodeReader != null) {
            iPlatformIcon icon = barcodeReader.getButtonIcon();

            if (icon != null) {
              pb2.setIcon(icon);
            }

            String text = barcodeReader.getButtonText();

            if (text != null) {
              pb2.setText(text);
            }

            pb2.setEnabled(barcodeReader.isReaderAvailable());
          } else {
            pb2.setEnabled(false);
          }
        }

        pb2 = (aGroupableButton) fv.getWidget("signalButton");

        if (pb2 != null) {
          UIImage sprite = Platform.getAppContext().getResourceAsImage("bv.icon.beacon.sprite");
          UISpriteIcon icon = new UISpriteIcon(sprite);

          icon.setFrozen(UIScreen.isMediumScreen());
          pb2.setIcon(icon);
          pb2.setEnabled((patientLocator != null) && patientLocator.isNearbyPatientsSupported());
        }
      }
    }
  }

  /**
   * Called when the patient is selected via double-clicking on the table or
   * clicking on the patient selection button
   */
  public void onSelectPatient(String eventName, iWidget widget, EventObject event) {
    if (widget == patientsTable) {
      showPreview(patientsTable);
    }

    final WindowViewer w = Platform.getWindowViewer();
    iWidget patient = widget.getFormViewer().getWidget("patient");
    RenderableDataItem row = (RenderableDataItem) patient.getLinkedData();
    String id = (String) row.get(0).getValue();
    final iPlatformIcon icon = row.get(0).getIcon();
    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (returnValue instanceof Throwable) {
          Utils.handleError((Throwable) returnValue);
        } else if (!canceled) {
          Platform.getAppContext().putData("pt_thumbnail", icon);
          Utils.showMainView();
        }
      }
    };

    loadPatientEx(w, id, cb);
  }

  /**
   * Called when the signal button is clicked. We will stop updating the list
   * via the patient locator
   */
  public void onSignalButtonAction(String eventName, iWidget widget, EventObject event) {
    patientLocator.stopListeningForNearbyPatients();

    aGroupableButton pb = (aGroupableButton) widget;
    UISpriteIcon icon = (UISpriteIcon) pb.getIcon();

    if (UIScreen.isLargeScreen()) {
      stopListeningForNearbyPatients();
    } else {
      if (icon.isFrozen()) {
        StackPaneViewer sp = (StackPaneViewer) widget.getFormViewer().getWidget("selectionStack");

        sp.switchTo(0);
        showNearbyPatients(widget);
        icon.setFrozen(false);
      } else {
        stopListeningForNearbyPatients();
      }
    }
  }

  public void onSortByName(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;

    table.sort(1, false, true);
  }

  public void onSortByRoomNumber(String eventName, iWidget widget, EventObject event) {
    TableViewer table = (TableViewer) widget;
    Comparator<RenderableDataItem> c = new Comparator<RenderableDataItem>() {
      @Override
      public int compare(RenderableDataItem o1, RenderableDataItem o2) {
        RenderableDataItem rb1 = o1.getItemEx(RM_BED);
        RenderableDataItem rb2 = o2.getItemEx(RM_BED);

        if ((rb1 == null) || (rb2 == null)) {
          if (rb1 == null) {
            return (rb2 == null) ? 0 : 1;
          }

          return -1;
        }

        return (int) (((Double) rb1.getLinkedData()) - ((Double) rb2.getLinkedData()));
      }
    };

    table.sort(c);
  }

  public void onTabPaneCreated(String eventName, iWidget widget, EventObject event) {
    if (UIScreen.isLargeScreen()) {
      if (alwaysShowSearchFirst || ((barcodeReader != null) && barcodeReader.isReaderAvailable())
          || ((patientLocator != null) && patientLocator.isNearbyPatientsSupported())) {
        TabPane cfg = (TabPane) ((DataEvent) event).getData();

        cfg.selectedIndex.setValue(1);
      }
    }
  }

  /**
   * Called when the nearby patients button is pressed.
   */
  public void onVoiceSearchAction(String eventName, final iWidget widget, EventObject event) {
  }

  /**
   * Call by a toolbar button to show the stack pane form with the same name as
   * the button
   */
  public void showFormForButton(String eventName, iWidget widget, EventObject event) {
    stopListeningForNearbyPatients();

    WindowViewer w = widget.getAppContext().getWindowViewer();
    iContainer fv = (iContainer) w.getViewer("patientSelectionForm");

    clearPreview(fv);

    String name = widget.getName();
    StackPaneViewer sp = (StackPaneViewer) widget.getFormViewer().getWidget("selectionStack");

    if (name.equals("listsButton") && sp.isViewerLoaded(name)) {
      iContainer c = (iContainer) sp.getViewer(name);
      ComboBoxWidget cb = (ComboBoxWidget) c.getWidget("listCategories");
      PatientList pl = (PatientList) cb.getSelectionData();

      if ((pl != null) && pl.isContainsPatients()) {
        cb.clearSelection();
      }
    }

    sp.switchTo(name);
  }

  @Override
  public void stateChanged(EventObject e) {
    LocatorChangeEvent ce = (LocatorChangeEvent) e;

    if (ce.getChangeType() == LocatorChangeType.ACCESS_DENIED) {
      stopListeningForNearbyPatients();

      WindowViewer w = Platform.getWindowViewer();

      Utils.alert(w.getString("bv.text.location_service_denied_access"));

      return;
    }

    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if ((locatorWidget != null) && !locatorWidget.isDisposed() && locatorWidget.isShowing() && (patientLocator != null)) {
          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          } else {
            try {
              TableViewer table = patientsTable;
              List<RenderableDataItem> list = (List<RenderableDataItem>) returnValue;

              if (areSamePatientsInSameOrder(list, table)) {
                int len = list.size();

                for (int i = 0; i < len; i++) {
                  if (!list.get(i).get(SIGNAL).valueEquals(table.get(i).get(SIGNAL))) {
                    table.rowChanged(i);
                  }
                }
              } else {
                patientsTable.setAll(processPatientsList(patientsTable, (List<RenderableDataItem>) returnValue, true));
              }
            } catch (Exception e) {
              Utils.handleError(e);
            }
          }
        }
      }
    };

    patientLocator.getNearbyPatients(e, cb);
  }

  /**
   * Clears the current patient preview
   *
   * @param fv
   *          the form viewer containing the preview fields
   */
  protected void clearPreview(iContainer fv) {
    fv.getWidget("patient").setValue(fv.getAppContext().getWindowViewer().getString("bv.text.no_patient"));

    if (fv.getWidget("mrn") != null) {
      fv.getWidget("mrn").setValue("");
    }

    fv.getWidget("age_sex").setValue("");
    fv.getWidget("admit_date").setValue("");

    if (fv.getWidget("admit_dx") != null) {
      fv.getWidget("admit_dx").setValue("");
    }

    ImagePaneViewer ip = ((ImagePaneViewer) fv.getWidget("photo"));

    if (ip != null) {
      ip.setImage(fv.getAppContext().getResourceAsImage("bv.icon.no_photo"));
    }

    fv.getWidget("patient").setEnabled(false);
  }

  protected void loadPatientList(final TableViewer table, final ActionLink link, final String filter, final String gender) {
    final WindowViewer w = Platform.getWindowViewer();
    aWorkerTask task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          List<RenderableDataItem> rows = loadPatientListEx(table, link, filter, gender, false);

          rows = processPatientsList(table, rows, false);

          return rows;
        } catch (Exception e) {
          return e;
        }
      }

      @Override
      public void finish(Object result) {
        w.hideWaitCursor();

        if (!table.isDisposed()) {
          if (result instanceof Exception) {
            Utils.handleError((Exception) result);
          } else {
            try {
              boolean empty = false;

              if (link.isCollection()) {
                aListViewer lb = (aListViewer) w.getViewer("listsBox");

                if (lb != null) {
                  lb.setAll(Arrays.asList(patientListLoaded));
                }
              }

              List<RenderableDataItem> rows = (List<RenderableDataItem>) result;

              table.resetData(rows);
              patientsTableUpdated(table, link, empty);

              if (!Utils.isCardStack()) {
                StackPaneViewer sp = (StackPaneViewer) w.getWidget("selectionStack");

                if (empty) {
                  if (sp != null) {
                    if (sp.getActiveViewerIndex() != 0) {
                      Utils.alert(w.getString("bv.text.no_patients_found"));
                    }
                  }
                } else {
                  if (sp != null) {
                    sp.switchTo(0);
                  }

                  table.setSelectedIndex(0);
                  showPreview(table);
                }
              }
            } catch (Exception e) {
              Utils.handleError(e);
            }
          }
        }
      }
    };

    w.spawn(task);
    w.showWaitCursor();
  }

  protected List<RenderableDataItem> loadPatientListEx(final TableViewer widget, ActionLink link, String filter, String gender,
      boolean sort) throws Exception {
    widget.setLinkedData(link);

    final List<RenderableDataItem> rows = widget.parseDataLink(link, true);

    if (filter != null) { // demo search
      boolean mrn = false;
      int len = filter.length();

      for (int i = 0; i < len; i++) {
        if (Character.isDigit(filter.charAt(i))) {
          mrn = true;

          break;
        }
      }

      len = rows.size();

      if (len != 0) {
        iFilter f = createPatientNameFilter(filter);

        for (int i = len - 1; i >= 0; i--) {
          RenderableDataItem row = rows.get(i);
          RenderableDataItem item = row.get(mrn ? MRN : NAME);
          String s = (String) item.getValue();

          if (!f.passes(s, null)) {
            rows.remove(i);

            continue;
          }

          if (gender != null) {
            item = row.get(GENDER);
            s = (String) item.getValue();

            if (!s.startsWith(gender)) {
              rows.remove(i);
            }
          }
        }
      }
    }

    if (sort) {
      Collections.sort(rows, new Comparator<RenderableDataItem>() {
        @Override
        public int compare(RenderableDataItem o1, RenderableDataItem o2) {
          o1 = o1.getItemEx(1);
          o2 = o2.getItemEx(1);

          return o1.compareTo(o2);
        }
      });
    }

    return rows;
  }

  /**
   * Populates a patient preview form
   *
   * @param fv
   *          the container for the preview form
   * @param row
   *          the row for the preview
   *
   * @throws Exception
   */
  protected void populatePreview(iContainer fv, RenderableDataItem row) {
    WindowViewer w = Platform.getWindowViewer();
    RenderableDataItem item = row.get(NAME);
    String mrn = (String) row.get(MRN).getValue();
    String s = (String) item.getLinkedData();

    fv.getWidget("patient").setValue(s);

    if ((mrn != null) && (mrn.length() > 0)) {
      s += " (" + mrn + ")";
    }

    iWidget field = fv.getWidget("mrn");

    if (field != null) {
      field.setValue(mrn);
    }

    fv.getWidget("age_sex").setValue(row.get(DOB).getLinkedData());
    item = row.get(ADMIT_DATE);
    item.setType(RenderableDataItem.TYPE_DATETIME);
    s = item.toString();
    fv.getWidget("admit_date").setValue(s);
    item = row.get(ADMIT_DX);
    s = (String) item.getValue();
    field = fv.getWidget("admit_dx");

    if (field != null) {
      field.setValue(s);
    }

    item = row.get(LOCATION);

    String loc = item.toString();

    if ((loc == null) || (loc == "")) {
      loc = w.getString("bv.text.unknownLocation");
    }

    field = fv.getWidget("location");

    if (field != null) {
      field.setValue(loc);
    }

    item = row.get(RM_BED);
    s = item.toString();

    if ((s == null) || (s == "")) {
      s = "";
    }

    field = fv.getWidget("rmbed");

    if (field != null) {
      field.setValue(s);
    }

    field = fv.getWidget("location_rm_bed");

    if (field != null) {
      if (s.length() > 0) {
        field.setValue(w.getString("bv.format.location_rm_bed", loc, s));
      } else {
        field.setValue(loc);
      }
    }

    s = (String) row.get(PHOTO).getValue();

    ImagePaneViewer ip = (ImagePaneViewer) fv.getWidget("photo");

    if (ip != null) {
      PatientIcon icon = (PatientIcon) row.get(0).getIcon();
      UIImage img = (icon == null) ? null : icon.getImage();

      ip.setImage(img);
    }
  }

  @SuppressWarnings("resource")
  protected List<RenderableDataItem> processPatientsList(final TableViewer widget, final List<RenderableDataItem> rows,
      boolean checkForSignal) throws IOException {
    RenderableDataItem row, item, nameItem;
    final WindowViewer w = widget.getAppContext().getWindowViewer();
    SNumber num = new SNumber();

    widget.clear();

    int len = (rows == null) ? 0 : rows.size();

    if (len == 0) {
      return Arrays.asList(noPatientsFound);
    }

    String s;
    Date date;
    CharArray ca = new CharArray();
    boolean small = !UIScreen.isLargeScreen();
    CharArray sb = new CharArray(64);

    for (int i = len - 1; i >= 0; i--) {
      row = rows.get(i);
      //we will be changing the name value so copy it first as patietn locators may be caching the value
      item = row.get(NAME).copy();
      nameItem = item;
      row.set(NAME, item);
      s = (String) item.getValue();
      item.setLinkedData(s);
      sb.set(s);
      sb.toTitleCase();
      s = sb.toString();
      item.setLinkedData(s);
      sb.set("<html><b>");
      sb.append(s);
      s = (String) row.get(MRN).getValue();

      String sex = row.get(GENDER).getValue().toString();

      if (small && (sex.length() > 1)) {
        sex = sex.substring(0, 1);
      }

      sb.append("</b> Age/Sex: <b>");

      int asstart = sb.length();

      item = row.getItem(DOB);
      item.setType(RenderableDataItem.TYPE_DATE);
      date = (Date) item.getValue();

      if (date != null) {
        sb.append(Utils.calculateAge(date));
      }

      sb.append("/").append(sex);
      item.setLinkedData(sb.substring(asstart)); // set age/sex on dob field
      sb.append("<br/>");
      s = row.get(LOCATION).toString();

      if ((s != null) && (s.length() > 0)) {
        sb.append("</b> Loc: <b>");
        sb.append(s);
        s = row.get(RM_BED).toString();

        if ((s != null) && (s.length() > 0)) {
          sb.append("</b> Rm: <b>").append(s);
          ca.set(s);
          ca.replace('-', '.');
          num.setValue(ca.A, 0, ca._length);
          num.shiftDecimal(num.scale());
          row.get(RM_BED).setLinkedData(num.doubleValue());
        }
      }

      if (checkForSignal) {
        item = row.getItemEx(SIGNAL);

        if (item != null) {
          int signal = item.intValue();

          if (signal > 0) {
            if (signal > 5) {
              signal = 5;
            }

            item = row.get(2);
            item.setIcon(signalIcons[signal - 1]);
          }
        }
      }

      sb.append("</b></html>");
      s = sb.toString();
      nameItem.setValue(s);
      row.setValue(s);

      if (row.isEnabled()) {
        row.get(0).setIcon(getThimbnail(w, (String) row.get(PHOTO).getValue()));
      }
    }

    return rows;
  }

  /**
   * Called to show the nearby patients button is pressed.
   *
   * @param widget
   *          the widget context for the call
   */
  protected void showNearbyPatients(final iWidget widget) {
    locatorWidget = widget;
    patientLocator.setChangeListener(PatientSelect.this);
    patientLocator.startListeningForNearbyPatients();

    if (searchingForPatients == null) {
      searchingForPatients = Utils.createDisabledTableRow("bv.text.search_dots", 1);
    }

    patientsTable.clear();
    patientsTable.add(searchingForPatients);
  }

  /**
   * Shows the preview for the selected patient
   *
   * @param widget
   *          the table containing the selected patient
   *
   * @throws Exception
   */
  protected void showPreview(TableViewer widget) {
    RenderableDataItem row = widget.getSelectedItem();
    iContainer fv = widget.getFormViewer();

    if ((row == null) || !row.isSelectable()) {
      clearPreview(fv);

      return;
    }

    iWidget patient = fv.getWidget("patient");

    if (patient.getLinkedData() == row) {
      return;
    }

    patient.setEnabled(true);
    patient.setLinkedData(row);
    populatePreview(fv, row);
  }

  private iFilter createPatientNameFilter(String filter) {
    iFilter f = null;
    int i = filter.indexOf(',');

    if (i != -1) {
      filter = filter.substring(0, i) + ".*,\\s*" + filter.substring(i + 1) + ".*";
      f = Functions.createRegExFilter(filter, false);
    }

    i = filter.indexOf(' ');

    if (i != -1) {
      filter = filter.substring(i + 1) + ".*,\\s*" + filter.substring(0, i) + ".*";
      f = Functions.createRegExFilter(filter, false);
    }

    if (f == null) {
      f = Functions.createContainsFilter(filter, true);
    }

    return f;
  }

  boolean areSamePatientsInSameOrder(List<RenderableDataItem> list, TableViewer table) {
    int len = list.size();

    if (table.size() != len) {
      return false;
    }

    for (int i = 0; i < len; i++) {
      if (!list.get(i).get(0).valueEquals(table.getValueAt(i, 0))) {
        return false;
      }
    }

    return true;
  }

  void patientsTableUpdated(TableViewer table, ActionLink link, boolean empty) {
    WindowViewer w = Platform.getWindowViewer();

    if (Utils.isCardStack()) {
      iActionListener al = psActionListener;

      if (al == null) {
        al = psActionListener = new CardPatientSelectionActionListener();
      }

      int len = table.size();
      ArrayList<RenderableDataItem> list = new ArrayList<RenderableDataItem>(len);

      for (int i = 0; i < len; i++) {
        RenderableDataItem row = table.get(i);
        iPlatformIcon icon = row.get(0).getIcon();

        if (icon == null) { //row does not contain a patient
          continue;
        }

        RenderableDataItem item = row.get(NAME).copy();
        iPlatformIcon signal = row.get(2).getIcon();

        if (signal != null) {
          UICompoundIcon ci = new UICompoundIcon(signal, icon);

          item.setIcon(ci);
        } else {
          item.setIcon(icon);
        }

        item.setActionListener(al);
        list.add(item);
        item.setLinkedData(i);
      }

      Utils.pushWorkspaceViewer(CardStackUtils.createListItemsOrPageViewer(null, w, list, 3, -1, null, true, false));
    } else {
      String pn = null;
      iFormViewer fv = table.getFormViewer();

      if (link.getAttributes() != null) { //we searched/or set a max number
        pn = link.getLinkInfo();

        if (pn != null) {
          pager.add(ListPager.createPagingLink(link, pn));
        } else {
          pager.add(link);
        }

        pn = link.getPagingNext();

        if (pn != null) {
          pager.setNext(ListPager.createPagingLink(link, pn));
        }
      } else {
        pager.clear();
      }

      aGroupableButton pb = (aGroupableButton) fv.getWidget("nextPage");

      if (pb != null) {
        pb.setEnabled(pager.hasNext());
      }

      pb = (aGroupableButton) fv.getWidget("previousPage");

      if (pb != null) {
        pb.setEnabled(pager.hasPrevious());
      }
    }
  }

  void stopListeningForNearbyPatients() {
    if (patientLocator != null) {
      patientLocator.stopListeningForNearbyPatients();

      WindowViewer w = Platform.getWindowViewer();
      iContainer fv = (iContainer) w.getViewer("patientSelectionForm");
      aGroupableButton pb = (aGroupableButton) fv.getWidget("signalButton");

      if (pb != null) {
        UISpriteIcon icon = (UISpriteIcon) pb.getIcon();

        if (UIScreen.isLargeScreen()) {
          pb.setVisible(false);
          fv = (iContainer) Platform.getWindowViewer().getViewer("searchForm");

          if (fv != null) {
            pb = (aGroupableButton) fv.getWidget("bv.action.nearby_patients");

            if (pb != null) {
              pb.setEnabled(true);
            }
          }
        } else {
          icon.setFrozen(true);
        }
      }

      if ((patientsTable.size() == 1) && (patientsTable.get(0) == searchingForPatients)) {
        patientsTable.setAll(Arrays.asList(noPatientsFound));
      }
    }
  }

  /**
   * Called to display the patient select search view in order to change
   * patient. This is used by voice control.
   *
   * @param context
   *          the context
   * @param patientSearch
   *          string to populate the search field with
   */
  public static void changePatient(final iWidget context, final ActionPath path) {
    if (!OrderManager.canChangePatientOrExit(false, path)) {
      return;
    }

    final iPlatformAppContext app = Platform.getAppContext();

    app.closePopupWindows(true);

    String id = (path == null) ? null : path.shift();

    Utils.toggleActions(false);

    if ((id == null) || (id.length() == 0)) {
      Utils.setActionPath(new ActionPath());
      showPatientSelectView(context);

      return;
    }

    final WindowViewer w = Platform.getWindowViewer();
    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {

        if (canceled) {
          if (returnValue instanceof Throwable) {
            Platform.ignoreException("failed to load patient from path", (Throwable) returnValue);
          }

          Utils.setActionPath(new ActionPath());
          showPatientSelectView(context);
        } else {
          String photo = ((JSONObject) app.getData("patient")).optString("photo", null);

          app.putData("pt_thumbnail", getThimbnail(w, photo));
          Utils.setActionPath(path);
          Utils.showMainView();
        }
      }
    };

    loadPatientEx(context, id, cb);
  }

  public static void clearoutPatientCentricInfo() {
    Platform.getAppContext().clearData("pt_");
    Platform.getAppContext().removeData("patient");
    CollectionManager.getInstance().clear();
    Utils.resetActionBar();
  }

  public static boolean isShowing() {
    return Platform.getWindowViewer().getViewer("patientSelectionForm") != null;
  }

  public static void showPatientSelectView(iWidget widget) {
    final WindowViewer w = Platform.getWindowViewer();
    iFunctionCallback cb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        w.hideWaitCursor();

        if (returnValue instanceof Exception) {
          Utils.handleError((Throwable) returnValue);
        } else {
          clearoutPatientCentricInfo();
          Utils.toggleActions(false);

          iViewer v = (iViewer) returnValue;

          w.activateViewer(v, iTarget.TARGET_WORKSPACE);

          if (Utils.isCardStack()) {
            CardStackUtils.updateTitle(v, true);
          }
        }
      }
    };

    try {
      w.createViewer("patient_select.rml", cb);
      w.showWaitCursor();
    } catch (Exception e) {
      Utils.handleError(e);
    }
  }

  /**
   * Called to do the actual work of establishing a relation with a patient.
   *
   * @param context
   *          the context
   * @param patientID
   *          the patient id
   * @param relationship
   *          the relationship to establish
   * @param cb
   *          the callback
   */
  protected static void establishRelationship(final iWidget context, final String patientID, final String relationship,
      final iFunctionCallback cb) {
    if (Utils.isDemo()) {
      cb.finished(false, relationship);

      return;
    }

    final WindowViewer w = Platform.getWindowViewer();
    iWorkerTask task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          final ActionLink l = w.createActionLink("/hub/main/util/patients/establish_relationship/" + patientID + "/"
              + relationship);
          String s = l.getContentAsString();

          if ((s == null) || (s.length() == 0)) {
            return true;
          } else {
            return false;
          }
        } catch (Throwable e) {
          return e;
        }
      }

      @Override
      public void finish(Object result) {
        w.hideProgressPopup();

        if (result instanceof Throwable) {
          cb.finished(true, result);
        } else if ((result instanceof Boolean) && ((Boolean) result).booleanValue()) {
          cb.finished(false, relationship);
        } else {
          cb.finished(false, null);
        }
      }
    };

    w.spawn(task);
    w.showProgressPopup(w.getString("bv.text.establish_relationship"));
  }

  protected static iPlatformIcon getThimbnail(WindowViewer w, String s) {
    ActionLink link = null;

    if ((s != null) && (s.length() > 0)) {
      link = Utils.createPhotosActionLink(s, true);
    }

    if (link != null) {
      iPlatformIcon icon;

      try {
        icon = w.getIcon(link.getURL(w), s, 1);

        if (icon != null) {
          icon = new PatientIcon(icon);
        }

        return icon;
      } catch (MalformedURLException e) {
        Platform.ignoreException(null, e);
      }
    }

    return noPhotoIcon;
  }

  static class PatientIcon extends aPlatformIcon {
    final iPlatformIcon icon;
    final boolean       imageIcon;
    static final int    iconSize = UIScreen.platformPixels(48);

    @Override
    public int getIconHeight() {
      return iconSize;
    }

    public UIImage getImage() {
      return imageIcon ? ((UIImageIcon) icon).getImage() : null;
    }

    public PatientIcon(iPlatformIcon icon) {
      super();
      this.icon = icon;
      this.imageIcon = icon instanceof UIImageIcon;
    }

    @Override
    public int getIconWidth() {
      return iconSize;
    }

    @Override
    public iPlatformIcon getDisabledVersion() {
      return this;
    }

    @Override
    public void paint(iPlatformGraphics g, float x, float y, float width, float height) {
      int d1 = UIScreen.PLATFORM_PIXELS_1;
      int d2 = UIScreen.PLATFORM_PIXELS_2;
      float isize = iconSize;
      y += (height - isize) / 2;
      g.saveState();
      photoBorder.clip(g, x, y, isize, isize);
      if (imageIcon) {
        UIImage img = ((UIImageIcon) icon).getImage();
        g.drawImage(img, x+d1, y+d1, isize-d2, isize-d2);
      } else {
        icon.paint(g,  x+d1, y+d1, isize-d2, isize-d2);
      }
      g.restoreState();
      photoBorder.paint(g, x, y, isize, isize, photoBorder.isPaintLast());
    }
  }

  /**
   * Performs the work of loading a patient. We load the JSON object from the
   * server then we augment it with constructs used by the application and set
   * some application level attributes that we can reference in RML markup.
   *
   * <p>
   * If the backend system requires a relationship to be established before we
   * can continue then the relationship assignment UI is shown.
   * <p>
   *
   * @param widget
   *          a context widget
   * @param id
   *          the id of the patient
   * @param cb
   *          the callback
   */
  protected static void loadPatientEx(final iWidget widget, final String id, final iFunctionCallback cb) {
    final WindowViewer w = widget.getAppContext().getWindowViewer();
    aWorkerTask task = new aWorkerTask() {
      @Override
      public Object compute() {
        try {
          ActionLink l = w.createActionLink("/hub/main/util/patients/select/" + id + ".json");
          String s = l.getContentAsString();

          if ((s != null) && (s.length() > 0)) {
            JSONObject patient = new JSONObject(s);
            JSONArray rows = patient.getJSONArray("_rows");

            if (rows != null) {
              patient = rows.getJSONObject(0);
            }

            s = patient.optString("name");

            if ((s != null) && (s.length() > 0)) {
              CollectionManager cm = CollectionManager.getInstance();

              cm.clear();

              JSONObject json = patient.optJSONObject("allergies");
              iDataCollection dc = cm.getCollection("allergies");

              try {
                if (json == null) {
                  dc.refresh(w);
                } else {
                  List<RenderableDataItem> list = Functions.parseJSONObject(w, json, true);

                  dc.setCollectionData(list);
                }
              } catch (IOException e) {
                Platform.ignoreException("couldn't parse allergies", e);
              }

              json = patient.optJSONObject("alerts");
              dc = cm.getCollection("alerts");

              if (dc != null) {
                try {
                  if (json != null) {
                    List<RenderableDataItem> list = Functions.parseJSONObject(w, json, true);

                    dc.setCollectionData(list);
                  } else {
                    dc.refresh(w);
                  }
                } catch (IOException e) {
                  Platform.ignoreException("couldn't parse alerts", e);
                }
              }

              json = patient.optJSONObject("flags");
              dc = cm.getCollection("flags");

              if (dc != null) {
                try {
                  if (json != null) {
                    List<RenderableDataItem> list = Functions.parseJSONObject(w, json, true);

                    dc.setCollectionData(list);
                  } else {
                    dc.refresh(w);
                  }
                } catch (IOException e) {
                  Platform.ignoreException("couldn't parse flags", e);
                }
              }

              if (!UIScreen.isLargeScreen()) {
                dc = cm.getCollection("problems");

                if (dc != null) {
                  dc.refresh(w);
                }
              }

              return patient;
            }
          }

          return null;
        } catch (Exception e) {
          return e;
        }
      }

      @Override
      public void finish(Object result) {
        w.hideWaitCursor();
        if ((result instanceof Throwable) || (result == null)) {
          cb.finished(true, result);

          return;
        }

        final JSONObject patient = (JSONObject) result;
        Object o = patient.get("relationship");
        String s = null;

        if (o instanceof JSONObject) {
          s = ((JSONObject) o).optString("linkedData");
        } else if (o != null) {
          s = o.toString();
        }

        try {
          if ((s == null) || (s.length() == 0) || (SNumber.intValue(s) < 1)) {
            String name = patient.getString("name");

            showRelationshipPopup(w, id, name, new iFunctionCallback() {
              @Override
              public void finished(boolean canceled, Object returnValue) {
                if (canceled) {
                  cb.finished(true, null);
                } else {
                  patient.put("relationship", returnValue);
                  processPatientData(patient);
                  cb.finished(false, patient);
                }
              }
            });

            return;
          }

          patient.put("relationship", s);
          processPatientData(patient);
          cb.finished(false, patient);
        } catch (Exception e) {
          cb.finished(true, e);
        }
      }
    };

    w.spawn(task);
    w.showWaitCursor();
  }

  @SuppressWarnings("resource")
  protected static void processPatientData(JSONObject patient) {
    WindowViewer w = Platform.getWindowViewer();
    iPlatformAppContext app = Platform.getAppContext();

    app.clearData("pt_");
    app.putData("patient", patient);

    String name;
    String mrn;
    CharArray ca = new CharArray();

    name = patient.getString("name");
    name = ca.set(name).toTitleCase().toString();
    app.putData("pt_name", name);
    app.putData("pt_mrn", mrn = patient.optString("mrn"));

    if ((mrn != null) && (mrn.length() > 0)) {
      app.putData("pt_name_mrn", w.getString("bv.format.name_mrn", name, mrn));
    } else {
      app.putData("pt_name_mrn", name);
    }

    app.putData("pt_room_bed", patient.optString("rm_bed"));
    String s=(String) patient.opt("location","value");
    app.putData("pt_location", s==null ? w.getString("bv.text.unknownLocation"): s);

    String sex = patient.optString("gender");

    app.putData("pt_gender", sex);

    Object obj = patient.opt("dob");
    Date date = null;

    if (obj instanceof String) {
      try {
        date = Functions.parseDateString(w, (String) obj);
      } catch (ParseException e) {
        Platform.ignoreException("couldn't parse dob value:" + obj, e);
      }

      if (date != null) {
        patient.put("dob", date);
      }
    } else if (obj instanceof Date) {
      date = (Date) obj;
    }

    if (date != null) {
      int age = Utils.calculateAge(date);

      patient.put("age", age);
      app.putData("pt_age", s = StringCache.valueOf(age));
      app.putData("pt_age_sex", s + "/" + sex);
    } else {
      app.putData("pt_age_sex", "/" + sex);
    }

    app.putData("pt_name_age_sex", w.getString("bv.format.name_age_gender", name, app.getData("pt_age_sex")));
    obj = patient.opt("encounter_date");
    date = null;

    if (obj instanceof String) {
      try {
        date = Functions.parseDateString(w, (String) obj);
      } catch (ParseException e) {
        Platform.ignoreException("couldn't parse encounter date value:" + obj, e);
      }

      if (date != null) {
        patient.put("encounter_date", date);
      }
    } else if (obj instanceof Date) {
      date = (Date) obj;
    }

    s = "";

    if (date != null) {
      s = Functions.convertDate(w, date);
    }

    app.putData("pt_admit_date", s);
    s = patient.optString("encounter_reason");
    app.putData("pt_admit_dx", (s == null) ? "" : Functions.titleCase(s));

    String provider = (String)patient.opt("provider","value");

    s = (String)patient.opt("attending","value");

    if ((provider == null) || (provider.length() == 0)) {
      provider = s;
    }

    app.putData("pt_provider", ca.set(provider).toTitleCase().toString());
    obj = patient.opt("location","value");

    if (obj == null) {
      obj = w.getString("bv.text.unknown_location");
    }

    s = patient.optString("rm_bed");

    if (s != null) {
      s = w.getString("bv.format.location_rmbd", ca.set((String) obj).toTitleCase().toString(), s);
    } else {
      s = (String) obj;
    }

    app.putData("pt_location_rmbd", s);
    s = patient.optString("code_status");

    if ((s == null) || (s.length() == 0)) {
      s = w.getString("bv.text.code_status_not_specified");
    }

    app.putData("pt_code_status", s);
    updateWeightHeightBMI(app, patient);

    JSONObject li = (JSONObject) app.getData("labsInfo");
    String id = li.optString("bun_id");

    if (id != null) {
      ClinicalValue bun = new ClinicalValue(id, "BUN");

      bun.setAttributName("pt_bun");
      patient.put("cv_bun", bun);
    }

    id = li.optString("creatinine_id");

    if (id != null) {
      ClinicalValue bun = new ClinicalValue(id, "Creatinine");

      bun.setAttributName("pt_creatinine");
      patient.put("cv_creatinine", bun);
    }
    OrderManager.patientChanged();
  }

  protected static void showRelationshipPopup(final WindowViewer w, final String patientID, String patientName,
      final iFunctionCallback cb) {
    iFunctionCallback rcb = new iFunctionCallback() {
      @Override
      public void finished(boolean canceled, Object returnValue) {
        if (canceled) {
          cb.finished(true, null);
        } else {
          establishRelationship(w, patientID, (String) ((RenderableDataItem) returnValue).getLinkedData(), cb);
        }
      }
    };
    String title = w.expandString("<html>{resource:bv.text.relationship_prompt}<br/><b>" + patientName + "</b></html>");
    ActionLink link;

    if (Utils.isDemo()) {
      link = Utils.createLink(w, "/hub/main/util/lists/relationships", true);
    } else {
      link = Utils.createLink(w, "/hub/main/util/lists/relationships/" + patientID, true);
    }
    PickList pl=new PickList(title, link);
    pl.setSupportListDblClick(false);
    pl.show(rcb);
  }

  protected static void updateWeightHeightBMI(iPlatformAppContext app, JSONObject patient) {
    float wt = 0;
    float ht = 0;
    StringBuilder sb = new StringBuilder();
    AppPreferences prefs = Utils.getPreferences();
    boolean metric_weight = prefs.getBoolean("metric_weight", false);
    boolean metric_height = prefs.getBoolean("metric_height", false);
    SNumber num = new SNumber();
    String s = null;
    Object obj = patient.opt("wt");

    if (obj instanceof String) {
      s = (String) obj;
      num.setValue(s);

      if (s.contains("kg")) {
        wt = num.floatValue();

        if (!metric_weight) {
          num.multiply(2.2);
          num.round(0, true);
          s = num.toString() + "lb";
        } else if (s.indexOf(' ') != -1) {
          s = s.replace(" ", "");
        }
      } else {
        num.divide(2.2);
        wt = num.floatValue();

        if (metric_weight) {
          num.round(2, false);
          s = num.toString() + "kg";
        } else if (s.indexOf(' ') != -1) {
          s = s.replace(" ", "");
        }
      }
    }

    if (wt > 0) {
      sb.append(s);
    } else {
      sb.append("---");
    }

    sb.append("/");
    obj = patient.opt("ht");

    if (obj instanceof String) {
      s = (String) obj;
      num.setValue(s);

      if (s.contains("m")) {
        ht = num.floatValue();

        if (!metric_height) {
          num.multiply(39.37f);
          num.round(0, true);
          s = num.toString() + "in";
        } else if (s.indexOf(' ') != -1) {
          s = s.replace(" ", "");
        }
      } else {
        num.multiply(0.0254f);
        ht = num.floatValue();

        if (metric_height) {
          num.round(2, false);
          s = num.toString() + "m";
        } else if (s.indexOf(' ') != -1) {
          s = s.replace(" ", "");
        }
      }
    }

    if (ht > 0) {
      sb.append(s);
    } else {
      sb.append("---");
    }

    sb.append("/");

    String bmi = "";

    if ((wt > 0) && (ht > 0)) {
      sb.append(bmi = SNumber.toString(Math.round(wt / (ht * ht))));
    } else {
      sb.append("---");
    }

    app.putData("pt_wt_ht_bmi", sb.toString());
    app.putData("pt_bmi", bmi);
  }

  class CardPatientSelectionActionListener implements iActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      final WindowViewer w = Platform.getWindowViewer();
      RenderableDataItem item = (RenderableDataItem) e.getSource();
      RenderableDataItem row = patientsTable.get((Integer) item.getLinkedData());
      String id = (String) row.get(0).getValue();
      final iPlatformIcon icon = (iPlatformIcon) row.get(0).getIcon();
      iFunctionCallback cb = new iFunctionCallback() {
        @Override
        public void finished(boolean canceled, Object returnValue) {
          if (returnValue instanceof Throwable) {
            Utils.handleError((Throwable) returnValue);
          } else if (!canceled) {
            Platform.getAppContext().putData("pt_thumbnail", icon);
            Utils.showMainView();
          }
        }
      };

      loadPatientEx(w, id, cb);
    }
  }

  static class MenuActionListener implements iActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      UIMenuItem mi = (UIMenuItem) e.getSource();
      String name = mi.getName();

      Utils.setFirstSelectedTab(name);

      aGroupableButton pb = (aGroupableButton) mi.getContextWidget();

      pb.click();
    }
  }
}
