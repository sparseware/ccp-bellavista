package com.sparseware.bellavista.external.fhir;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

import com.appnativa.rare.Platform;
import com.appnativa.rare.iCancelableFuture;
import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.rare.net.ActionLink;
import com.appnativa.util.IdentityArrayList;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONTokener;

/**
 * This class initiates the background loading of an link
 * and provides a mechanism for waiting until the loading
 * of all the links have completed.
 *
 * @author Don DeCoteau
 *
 */
public class LinkWaiter {
  IdentityArrayList<ActionLink> links;
  ArrayList<iCancelableFuture>  cancelables;
  ArrayList                     results;
  volatile int                  totalCount;
  volatile int                  completedCount;
  volatile boolean              canceled;
  boolean                       asJSON            = true;
  private boolean               cancelOnException = true;
  Exception                     error;
  ActionLink                    errorLink;
  Map<Object, Integer>          idMap;

  /**
   * Creates a new link waiter the defaults to returning
   * data as JSON objects
   *
   * @param count the expected number of links that will be added
   */
  public LinkWaiter(int count) {
    this(count, true);
  }

  /**
   * Creates a new link waiter
   *
   * @param count the expected number of links that will be added
   * @param jasn true to have results default to being returned as JSON; false for strings
   */
  public LinkWaiter(int count, boolean json) {
    links       = new IdentityArrayList<ActionLink>(count);
    results     = new ArrayList(count);
    cancelables = new ArrayList<iCancelableFuture>(count);
    this.asJSON = json;
  }

  /**
   * Adds a link to be waited on. The link is immediately
   * queued for loading on a background thread.
   * The link's data is retrieved as a string.
   *
   * @param link the link to add
   */
  public void addLink(ActionLink link) {
    addLink(link, false);
  }

  /**
   * Adds a link to be waited on. The link is immediately
   * queued for loading on a background thread.
   *
   * @param link the link to add
   * @param asBytes true to have the data retrieved as bytes; false for a string
   */
  public void addLink(ActionLink link, boolean asBytes) {
    links.add(link);
    results.add(null);
    cancelables.add(Platform.getAppContext().executeBackgroundTask(new DataRetriever(link, results.size() - 1,
            asBytes)));
  }

  /**
   * Adds a link to be waited on. The link is immediately
   * queued for loading on a background thread.
   *
   * @param link the link to add
   * @param asBytes true to have the data retrieved as bytes; false for a string
   */
  public void addLink(ActionLink link, Callable task) {
    links.add(link);
    results.add(null);
    cancelables.add(Platform.getAppContext().executeBackgroundTask(new DataRetriever(link, results.size() - 1, task)));
  }

  /**
   * Starts waiting for the links to be loaded.
   * The method will block until all the links  have been loaded.
   *
   * <p>
   * If <i>cancelOnException</i> is set to true (the default) the
   * the first exception that occurs will cause all pending loads to be cancled
   * and this method will return.
   *
   * You can call the {@link #getError() method to get the error}
   * </p>
   */
  public void startWaiting() {
    synchronized(results) {
      int count = links.size();

      if ((completedCount == count) || canceled) {
        return;
      }

      totalCount = count;

      try {
        results.wait();
      } catch(InterruptedException e) {
        throw new ApplicationException(e);
      }
    }

    cancelables.clear();
  }

  /**
   * Gets whether or not an error occured
   * @return true if an error occured; false otherwise
   */
  public boolean hadError() {
    return error != null;
  }

  /**
   * Get the error that occured during a loading process
   * @return the error that occured during a loading process or null if there was none
   */
  public Exception getError() {
    return error;
  }

  /**
   * Get the link that had the error during the loading process
   * @return the link that had the error during the loading process or null if there was none
   */
  public ActionLink getErrorLink() {
    return errorLink;
  }

  /**
   * Gets the link at the specified index
   * @param index the index
   * @return the link
   */
  public ActionLink getLink(int index) {
    return links.get(index);
  }

  /**
   * Gets the result at the specified index
   * @param index the index
   * @return the result
   */
  public Object getResult(int index) {
    return results.get(index);
  }

  /**
   * Gets the result at the specified index
   * @param index the index
   * @return the result
   */
  public Object getResult(ActionLink link) {
    int n = links.indexOf(link);

    if (n == -1) {
      return null;
    }

    return results.get(n);
  }

  /**
   * Disposes of the waiter
   */
  public void dispose() {
    links.clear();
    results.clear();    //don't set to null as it is possible a background thread may need it to synchronize on because if was not able to be canceled
  }

  /**
   * Called to cancel the waiting process
   * @param e the error that occured
   * @param link the link that caused the error
   */
  public void cancel(Exception e, ActionLink link) {
    synchronized(results) {
      error     = e;
      errorLink = link;

      for (iCancelableFuture f : cancelables) {
        f.cancel(true);
      }

      cancelables.clear();
      canceled = true;
      results.notify();
    }
  }

  /**
   * Set whether loading is canceled when one of the links generates an error
   * @param cancelOnException true if it is; false otherwise
   */
  public void setCancelOnException(boolean cancelOnException) {
    this.cancelOnException = cancelOnException;
  }

  protected class DataRetriever implements Runnable {
    ActionLink link;
    int        index;
    boolean    asBytes;
    Callable   task;

    public DataRetriever(ActionLink link, int index, boolean asBytes) {
      super();
      this.link    = link;
      this.index   = index;
      this.asBytes = asBytes;
    }

    public DataRetriever(ActionLink link, int index, Callable task) {
      super();
      this.link  = link;
      this.index = index;
      this.task  = task;
    }

    @Override
    public void run() {
      Object result;

      if (task != null) {
        try {
          result = task.call();
        } catch(Exception e) {
          result = e;
        }
      } else {
        try {
          if (asBytes) {
            result = link.getContentAsBytes();
          } else if (asJSON) {
            JSONTokener t = new JSONTokener(link.getReader());

            result = new JSONObject(t);
          } else {
            result = link.getContentAsString();
          }
        } catch(Exception e) {
          link.close();

          if (cancelOnException) {
            cancel(e, link);

            return;
          }

          result = e;
        }

        link.close();
      }

      synchronized(results) {
        if (!canceled) {    //check in case this thread was not canceled
          results.set(index, result);
          completedCount++;

          if (completedCount == totalCount) {
            results.notify();
          }
        }
      }
    }
  }
}
