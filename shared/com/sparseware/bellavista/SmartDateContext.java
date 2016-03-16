/*
 * Copyright appNativa Inc. All Rights Reserved.
 *
 * This file is part of the Real-time Application Rendering Engine (RARE).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparseware.bellavista;

import com.appnativa.rare.converters.DateContext;
import com.appnativa.util.Helper;
import com.appnativa.util.SNumber;
import com.appnativa.util.SimpleDateFormatEx;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * A date context that understands imprecise date and can handle
 * parsing standard date formats
 *
 * @author Don DeCoteau
 *
 */
public class SmartDateContext extends DateContext {
  protected boolean    dateTime             = true;
  private boolean      supportRelativeDates = true;
  protected DateFormat yearMonthDisplay     = new SimpleDateFormatEx("MMM yyyy");

  /**
   * Creates a new instance
   * @param dateTime true for a data/time context; false for a date only context
   * @param displayFormat the display format for this context
   */
  public SmartDateContext(boolean dateTime, DateFormat displayFormat) {
    this.dateTime      = dateTime;
    this.displayFormat = displayFormat;
  }

  /**
   * Creates a new instance
   * @param dateTime true for a data/time context; false for a date only context
   * @param displayFormat the display format for this context
   */
  public SmartDateContext() {}

  @Override
  public boolean isCustomConverter() {
    return true;
  }

  @Override
  public String dateToString(Date date) {
    if (date instanceof ImperciseDate) {
      return date.toString();
    }

    DateFormat df = displayFormat;

    synchronized(df) {
      return df.format(date);
    }
  }

  @Override
  public DateContext create(DateFormat iformat, DateFormat dformat) {
    if (iformat != null) {
      return super.create(iformat, (dformat == null)
                                   ? displayFormat
                                   : dformat);
    }

    return new SmartDateContext(dateTime, (dformat == null)
            ? displayFormat
            : dformat);
  }

  @Override
  public DateContext create(DateFormat[] iformat, DateFormat dformat) {
    if (iformat != null) {
      return super.create(iformat, (dformat == null)
                                   ? displayFormat
                                   : dformat);
    }

    return new SmartDateContext(dateTime, (dformat == null)
            ? displayFormat
            : dformat);
  }

  @Override
  public Date dateFromString(String value) {
    int len = (value == null)
              ? 0
              : value.length();

    if (len == 0) {
      return null;
    }

    if (!Character.isDigit(value.charAt(0))) {
      Date date = supportRelativeDates ? Helper.createDate(value) : null;

      if (date == null) {
        return new ImperciseDate(value, System.currentTimeMillis(), null);
      }

      return date;
    }

    if (len < 8) {
      return new ImperciseDate(value, getPartialDate(value), yearMonthDisplay);
    }

    Calendar cal = Calendar.getInstance();

    if (!Helper.setDateTime(value, cal, dateTime)) {
      return new ImperciseDate(value, getPartialDate(value), yearMonthDisplay);
    }

    return cal.getTime();
  }

  /**
   * Converts a partial date to milliseconds representing the partial components
   * @param value the partial date value
   * @return milliseconds representing the partial components
   */
  static long getPartialDate(String value) {
    Calendar c = Calendar.getInstance();

    try {
      c.setTimeInMillis(0);

      int len = value.length();

      if (len > 3) {
        c.set(Calendar.YEAR, SNumber.intValue(value));
      }

      if (len > 6) {
        int n = value.indexOf('-');

        if (n != -1) {
          c.set(Calendar.MONTH, SNumber.intValue(value.substring(n + 1)));
        }
      }
    } catch(Exception ignore) {}

    return c.getTimeInMillis();
  }

  static class ImperciseDate extends Date {
    private DateFormat toStringFormat;
    private String     stringValue;

    public ImperciseDate(String value, long time, DateFormat format) {
      super(time);
      stringValue    = value;
      toStringFormat = format;
    }

    @Override
    public String toString() {
      if (toStringFormat != null) {
        synchronized(toStringFormat) {
          try {
            stringValue = toStringFormat.format(this);
          } catch(Exception e) {
            e.printStackTrace();
          }

          toStringFormat = null;
        }
      }

      return stringValue;
    }
  }


  public String parseAndFormat(String date) {
    Date d = dateFromString(date);

    return (d == null)
           ? date
           : dateToString(d);
  }

  public boolean isSupportRelativeDates() {
    return supportRelativeDates;
  }

  public void setSupportRelativeDates(boolean supportRelativeDates) {
    this.supportRelativeDates = supportRelativeDates;
  }
}
