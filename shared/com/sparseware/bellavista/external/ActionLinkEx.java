package com.sparseware.bellavista.external;

import com.appnativa.rare.net.ActionLink;
import com.appnativa.rare.net.iURLConnection;
import com.appnativa.rare.spot.Link;
import com.appnativa.rare.spot.Viewer;
import com.appnativa.spot.SPOTPrintableString;
import com.appnativa.util.iURLResolver;

import java.net.URL;

public class ActionLinkEx extends ActionLink {
  protected int    linkedIndex;
  protected Object linkedData;

  public ActionLinkEx() {}

  public ActionLinkEx(iURLResolver context) {
    super(context);
  }

  public ActionLinkEx(iURLResolver context, iURLConnection connection) {
    super(context, connection);
  }

  public ActionLinkEx(iURLResolver context, Link link) {
    super(context, link);
  }

  public ActionLinkEx(iURLResolver context, SPOTPrintableString url) {
    super(context, url);
  }

  public ActionLinkEx(iURLResolver context, String url) {
    super(context, url);
  }

  public ActionLinkEx(iURLResolver context, String url, String type) {
    super(context, url, type);
  }

  public ActionLinkEx(iURLResolver context, URL url) {
    super(context, url);
  }

  public ActionLinkEx(iURLResolver context, URL url, String type) {
    super(context, url, type);
  }

  public ActionLinkEx(iURLResolver context, Viewer cfg) {
    super(context, cfg);
  }

  public ActionLinkEx(String url) {
    super(url);
  }

  public ActionLinkEx(String data, String type) {
    super(data, type);
  }

  public ActionLinkEx(URL url) {
    super(url);
  }

  public ActionLinkEx(URL url, String type) {
    super(url, type);
  }

  public Object getLinkedData() {
    return linkedData;
  }

  public void setLinkedData(Object linkedData) {
    this.linkedData = linkedData;
  }

  public int getLinkedIndex() {
    return linkedIndex;
  }

  public void setLinkedIndex(int linkedIndex) {
    this.linkedIndex = linkedIndex;
  }
}
