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

package com.sparseware.bellavista.external.fhir;

import com.appnativa.rare.scripting.Functions;
import com.appnativa.util.Base64;
import com.appnativa.util.ByteArray;
import com.appnativa.util.CharArray;
import com.appnativa.util.CharScanner;
import com.appnativa.util.Helper;
import com.appnativa.util.SNumber;
import com.appnativa.util.json.JSONArray;
import com.appnativa.util.json.JSONObject;
import com.appnativa.util.json.JSONWriter;
import com.sparseware.bellavista.ActionPath;
import com.sparseware.bellavista.service.ContentWriter;
import com.sparseware.bellavista.service.HttpHeaders;
import com.sparseware.bellavista.service.aRemoteService;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/**
 * The class provides a general utilities
 *
 * @author Don DeCoteau
 */
public class FHIRUtils {
  public static final HashMap<String, String> interpretationColors = new HashMap<String, String>();

  static {
    interpretationColors.put(">", "abnormal");
    interpretationColors.put("<", "abnormal");
    interpretationColors.put("HH", "abnormal");
    interpretationColors.put("HHI", "abnormal");
    interpretationColors.put("H", "abnormal");
    interpretationColors.put("L", "abnormal");
    interpretationColors.put("LL", "abnormal");
    interpretationColors.put("LLO", "abnormal");
    interpretationColors.put("A", "abnormal");
    interpretationColors.put("AA", "abnormal");
    interpretationColors.put("N", "");
  }

  private FHIRUtils() {}

  public static String cleanAndEncodeString(String value) {
    if (value != null) {
      value = value.trim();

      if (value.length() == 0) {
        value = null;
      } else {
        value = Functions.base64NOLF(value);
      }
    }

    return value;
  }

  public static CharArray concatBestMedicalText(JSONArray a, String key, CharArray ca) {
    int len = (a == null)
              ? 0
              : a.length();

    if (len == 0) {
      return ca;
    }

    for (int i = 0; i < len; i++) {
      JSONObject o = a.getJSONObject(i);

      if (key != null) {
        o = o.optJSONObject(key);
      }

      if (o != null) {
        ca.append(getBestMedicalText(o));
        ca.append("; ");
      }
    }

    len = ca.length();

    if ((len > 2) && (ca.charAt(len - 2) == ';') && (ca.charAt(len - 1) == ' ')) {
      ca.setLength(len - 2);
    }

    return ca;
  }

  public static CharArray concatString(JSONArray a, String key, CharArray sb) {
    return concatString(a, key, sb, "; ");
  }

  public static CharArray concatString(JSONArray a, String key, CharArray sb, String sep) {
    int len = (a == null)
              ? 0
              : a.length();

    for (int i = 0; i < len; i++) {
      JSONObject o = a.getJSONObject(i);
      String     s = o.optString(key, null);

      if (s != null) {
        sb.append(s);
        sb.append(sep);
      }
    }

    len = sb.length();

    int slen = sep.length();

    if (sb.indexOf(sep, slen) == slen) {
      sb.setLength(slen);
    }

    return sb;
  }

  public static String convertIfFraction(String stringValue, float value) {
    if (value < 0) {
      value = SNumber.floatValue(stringValue);
    }

    if (SNumber.isEqual(0.5, value)) {
      stringValue = "1/2";
    }

    if (SNumber.isEqual(0.25, value)) {
      stringValue = "1/4";
    }

    if (SNumber.isEqual(0.33, value)) {
      stringValue = "1/3";
    }

    return stringValue;
  }

  public static Object createWriter(ActionPath path, ContentWriter w, HttpHeaders headers, boolean row) {
    Object     writer;
    JSONWriter jw = null;

    if (FHIRUtils.isJSONFormatRequested(path)) {
      jw     = new JSONWriter(w);
      writer = jw;
    } else {
      writer = w;
    }

    headers.setDefaultResponseHeaders();

    if (jw != null) {
      headers.mimeJson();
    } else {
      if (row) {
        headers.mimeRow();
      } else {
        headers.mimeList();
      }
    }

    return writer;
  }

  public static void escapeContolSequencesIfNecessary(Writer w, String value) throws IOException {}

  public static String getActor(JSONObject o, CharArray ca) {
    String     s  = null;
    JSONObject oo = o.optJSONObject("actor");

    if (oo != null) {
      s  = getReferenceText(oo);
      oo = o.optJSONObject("role");
    }

    if (oo == null) {
      return s;
    }

    ca.set(s).append(" (");
    ca.append(getBestMedicalText(oo));
    ca.append(')');

    return ca.toString();
  }

  public static String getBestMedicalCode(JSONArray a) {
    int    len    = (a == null)
                    ? 0
                    : a.length();
    String loinc  = null;
    String other  = null;
    String snomed = null;

    for (int i = 0; i < len; i++) {
      JSONObject coding = a.getJSONObject(i);
      String     system = coding.optString("system", null);

      if (system != null) {
        if (system.startsWith("http://hl7.org/fhir/")) {
          return coding.getString("code");
        } else if (system.startsWith("http://loinc.org")) {
          loinc = coding.getString("code");
        } else if (system.startsWith("http://snomed.info")) {
          snomed = coding.getString("code");
        } else {
          other = coding.getString("code");
        }
      } else {
        other = coding.getString("code");
      }
    }

    return (loinc != null)
           ? loinc
           : ((snomed != null)
              ? snomed
              : other);
  }

  public static String getBestMedicalCode(JSONObject o) {
    JSONArray a    = (o == null)
                     ? null
                     : o.optJSONArray("coding");
    String    code = (a == null)
                     ? null
                     : getBestMedicalCode(a);

    if ((o != null) && (code == null)) {
      code = o.optString("code", null);

      if (code == null) {
        code = o.optString("text", null);
      }
    }

    return code;
  }

  public static String getBestMedicalText(JSONArray a) {
    int    len    = (a == null)
                    ? 0
                    : a.length();
    String loinc  = null;
    String other  = null;
    String snomed = null;

    for (int i = 0; i < len; i++) {
      JSONObject coding = a.getJSONObject(i);
      String     system = coding.optString("system", null);

      if (system != null) {
        if (system.startsWith("http://hl7.org/fhir/")) {
          String s = coding.optString("display", null);

          if (s != null) {
            return s;
          }

          other = coding.optString("code", null);
        } else if (system.startsWith("http://loinc.org")) {
          loinc = coding.optString("display", null);

          if (loinc == null) {
            loinc = coding.optString("code", null);
          }
        } else if (system.startsWith("http://snomed.info")) {
          snomed = coding.optString("display", null);

          if (snomed == null) {
            snomed = coding.optString("code", null);
          }
        } else {
          other = coding.optString("display", null);

          if (other == null) {
            other = coding.optString("code", null);
          }
        }
      } else {
        other = coding.optString("display", null);
      }
    }

    return (loinc != null)
           ? loinc
           : ((snomed != null)
              ? snomed
              : other);
  }

  public static String getBestMedicalText(JSONObject o) {
    if (o != null) {
      String code = o.optString("text", null);

      if (code == null) {
        JSONArray a = (o == null)
                      ? null
                      : o.optJSONArray("coding");

        code = (a == null)
               ? null
               : getBestMedicalText(a);
      }

      return code;
    }

    return null;
  }

  public static String getCodableConcept(JSONArray a, String field, HashMap<String, String> map) {
    int len = a.length();

    for (int i = 0; i < len; i++) {
      JSONObject idf  = a.getJSONObject(i);
      JSONObject type = idf.optJSONObject("type");
      JSONArray  aa   = (type == null)
                        ? null
                        : type.optJSONArray("coding");
      int        alen = (aa == null)
                        ? 0
                        : aa.length();

      for (int ai = 0; ai < alen; ai++) {
        JSONObject coding = aa.getJSONObject(ai);
        String     code   = coding.getString("code");

        code = map.get(code);

        if ((code != null) && code.equals(field)) {
          return idf.getString("value");
        }
      }

      String code = idf.optString("system", null);

      if (code != null) {
        code = map.get(code);

        if ((code != null) && code.equals(field)) {
          return idf.getString("value");
        }
      }
    }

    return null;
  }

  public static String getCodeableConceptOrReferenceText(JSONObject o, String prefix, CharArray ca) {
    ca.set(prefix).append("CodeableConcept");

    JSONObject oo = o.optJSONObject(ca.toString());

    if (oo != null) {
      return getBestMedicalText(oo);
    }

    ca.setLength(prefix.length());
    ca.append("Reference");
    oo = o.optJSONObject(ca.toString());

    return (oo == null)
           ? null
           : oo.optString("display", null);
  }

  public static int getDaysBetweenInSameYear(Date date1, Date date2) {
    Calendar cal = Calendar.getInstance();
    int      y1;
    int      y2;
    int      d1;
    int      days = 0;

    cal.setTime(date1);
    y1 = cal.get(Calendar.YEAR);
    d1 = cal.get(Calendar.DAY_OF_YEAR);
    y2 = cal.get(Calendar.YEAR);

    if (y1 == y2) {
      cal.setTime(date2);
      days = cal.get(Calendar.DAY_OF_YEAR) - d1;

      if (days == 0) {
        days = 1;
      }
    }

    return (days < 0)
           ? -days
           : days;
  }

  public static String getHL7FHIRCode(JSONArray a) {
    int len = (a == null)
              ? 0
              : a.length();

    for (int i = 0; i < len; i++) {
      JSONObject coding = a.getJSONObject(i);
      String     system = coding.getString("system");

      if (system.startsWith("http://hl7.org/fhir/")) {
        return coding.optString("code", null);
      }
    }

    return null;
  }

  public static String getInterpretationColor(String code) {
    if (code == null) {
      return null;
    }

    String color = interpretationColors.get(code);

    if (color == null) {
      return "unknown";
    }

    return (color.length() == 0)
           ? null
           : color;
  }

  public static MedicalCode getMedicalCode(JSONArray a) {
    int    len        = (a == null)
                        ? 0
                        : a.length();
    String hl7        = null;
    String loinc      = null;
    String other      = null;
    String snomed     = null;
    String hl7Text    = null;
    String loincText  = null;
    String otherText  = null;
    String snomedText = null;

    for (int i = 0; i < len; i++) {
      JSONObject coding = a.getJSONObject(i);
      String     system = coding.optString("system", null);

      if (system != null) {
        if (system.startsWith("http://hl7.org/fhir/")) {
          hl7     = coding.getString("code");
          hl7Text = coding.optString("display", null);
        } else if (system.startsWith("http://loinc.org")) {
          loinc     = coding.getString("code");
          loincText = coding.optString("display", null);
        } else if (system.startsWith("http://snomed.info")) {
          snomed     = coding.getString("code");
          snomedText = coding.optString("display", null);
        } else {
          other     = coding.getString("code");
          otherText = coding.optString("display", null);
        }
      } else {
        other     = coding.getString("code");
        otherText = coding.optString("display", null);
      }
    }

    if ((snomed == null) && (other != null)) {
      snomed     = other;
      snomedText = otherText;
    }

    if ((hl7 == null) && (loinc == null) && (snomed == null)) {
      return null;
    }

    return new MedicalCode(hl7, snomed, loinc, hl7Text, loincText, snomedText);
  }

  public static MedicalCode getMedicalCode(JSONObject o) {
    if (o == null) {
      return null;
    }

    JSONArray   a  = o.optJSONArray("coding");
    MedicalCode mc = (a == null)
                     ? null
                     : getMedicalCode(a);

    if (mc == null) {
      String code    = o.optString("code", null);
      String display = o.optString("display", null);

      if (code != null) {
        mc = new MedicalCode(code, null, display, null);
      }
    }

    if (mc == null) {
      mc = new MedicalCode(null, null);
    }

    if (mc != null) {
      mc.text = o.optString("text", null);
    }

    return mc;
  }

  public static String getQuantity(JSONObject qty, CharArray ca) {
    if (qty == null) {
      return null;
    }

    String unit       = qty.optString("unit", null);
    String value      = qty.optString("value");
    String comparator = qty.optString("comparator", "=");

    ca._length = 0;

    if (!comparator.equals("=")) {
      ca.append(comparator);
    }

    ca.append(value);

    if (unit != null) {
      ca.append(' ').append(unit);
    }

    return ca.toString();
  }

  public static String getRange(JSONArray ranges, boolean includeUnits, CharArray ca) {
    int len = (ranges == null)
              ? 0
              : ranges.length();

    for (int i = 0; i < len; i++) {
      JSONObject o       = ranges.getJSONObject(i);
      String     range   = getRange(o, includeUnits, ca);
      JSONArray  a       = o.optJSONArray("meaning");
      String     meaning = (a == null)
                           ? null
                           : getHL7FHIRCode(a);

      if ((range != null) && ((meaning == null) || meaning.equals("N"))) {
        return range;
      }
    }

    return null;
  }

  public static String getRange(JSONObject range, boolean includeUnits, CharArray ca) {
    if (range == null) {
      return null;
    }

    String s = getRangeOrRatio(range, "low", "high", " - ", true, ca);

    return (s == null)
           ? range.optString("text", null)
           : s;
  }

  public static String getRangeOrRatio(JSONObject rate, String firstKey, String secondKey, String sep,
          boolean includeUnits, CharArray ca) {
    if (rate == null) {
      return null;
    }

    String     firstUnit   = null;
    String     secondUnit  = null;
    String     firstValue  = null;
    String     secondValue = null;
    JSONObject first       = rate.optJSONObject(firstKey);
    JSONObject second      = rate.optJSONObject(secondKey);

    if (first != null) {
      firstValue = first.optString("value", null);
      firstUnit  = includeUnits
                   ? first.optString("unit", null)
                   : null;
    }

    if (second != null) {
      secondValue = second.optString("value", null);
      secondUnit  = includeUnits
                    ? second.optString("unit", null)
                    : null;
    }

    if ((firstValue == null) && (secondValue == null)) {
      return null;
    }

    ca._length = 0;

    if (firstValue != null) {
      ca.append(firstValue);

      if (firstUnit != null) {
        ca.append(' ').append(firstUnit);
      }
    }

    if (secondValue != null) {
      ca.append(sep).append(secondValue);

      if (secondUnit != null) {
        ca.append(' ').append(secondUnit);
      }
    }

    return ca.toString();
  }

  public static String getRatio(JSONObject rate, CharArray ca) {
    if (rate == null) {
      return null;
    }

    String s = getRangeOrRatio(rate, "numerator", "denominator", "/", true, ca);

    return (s == null)
           ? rate.optString("text", null)
           : s;
  }

  public static String getPeriod(JSONObject period, CharArray ca) {
    if (period == null) {
      return null;
    }

    String start = period.optString("start", null);
    String end   = period.optString("end", null);

    if (start != null) {
      ca.append(FHIRServer.convertDateTime(start));
    }

    if (end != null) {
      if (start != null) {
        ca.append(" - ");
      }

      ca.append(FHIRServer.convertDateTime(end));
    }

    return ca.toString();
  }

  public static String getReferenceText(JSONObject o) {
    return (o == null)
           ? null
           : o.optString("display");
  }

  public static String getTiming(JSONObject o, CharArray sb) {
    return (o == null)
           ? null
           : getTiming(o, null, null, true, sb);
  }

  public static String getTiming(JSONObject o, String dose, String route, boolean appendBounds, CharArray sb) {
    String     duration      = null;
    String     durationMax   = null;
    String     durationUnits = null;
    String     period        = null;
    String     periodMax     = null;
    String     periodUnits   = null;
    String     frequency     = null;
    String     frequencyMax  = null;
    String     bounds        = null;
    String     count         = null;
    String     code          = null;
    String     s             = null;
    String     when          = o.optString("when", null);
    float      p             = 0;
    float      f             = 0;
    float      d             = 0;
    FHIRServer server        = FHIRServer.getInstance();
    JSONObject oo            = o.optJSONObject("code");

    if (oo != null) {
      MedicalCode mc = getMedicalCode(oo);

      s = null;

      if (mc != null) {
        s = mc.getBestCode();

        if (s != null) {
          HashMap<String, String> map = server.timingCode;

          s = map.get(s.toUpperCase(Locale.US));
        }

        if (s == null) {
          s = mc.getBestText();
          sb.trim().append("; ").append(s);
        }
      } else {
        s = oo.optString("text", null);
      }

      code = s;
    }

    sb.setLength(0);

    JSONObject repeat = o.optJSONObject("repeat");

    if (repeat != null) {
      JSONObject so = repeat.optJSONObject("boundsPeriod");

      if (so == null) {
        so = repeat.optJSONObject("bounds");
      }

      if (so != null) {
        String start = so.optString("start", null);
        String end   = so.optString("end", null);

        if (start != null) {
          o.put("_start", start);

          if (end != null) {
            o.put("_end", end);
            start  = FHIRServer.convertDateTime(start);
            end    = FHIRServer.convertDateTime(end);
            bounds = String.format(server.getResourceAsString("bv.timing.format.bounds_start_stop"), start, end);
          } else {
            start  = FHIRServer.convertDateTime(start);
            bounds = String.format(server.getResourceAsString("bv.timing.format.bounds_start"), start);
          }
        } else if (end != null) {
          start = FHIRServer.convertDateTime(start);
          o.put("_end", end);
          end    = FHIRServer.convertDateTime(end);
          bounds = String.format(server.getResourceAsString("bv.timing.format.bounds_stop"), start);
        }

        if (start != null) {
          o.put("_startDate", start);
        }

        if (end != null) {
          o.put("_stopDate", end);
        }
      } else {
        so = repeat.optJSONObject("boundsQuantity");

        if (so != null) {
          bounds = getQuantity(so, sb);

          if ((bounds != null) && (bounds.length() > 0)) {
            bounds = String.format(server.getResourceAsString("bv.timing.format.bounds_duration"), bounds);
          }
        } else {
          so = repeat.optJSONObject("boundsRange");

          if (so != null) {
            bounds = getRange(so, true, sb);

            if ((bounds != null) && (bounds.length() > 0)) {
              bounds = String.format(server.getResourceAsString("bv.timing.format.bounds_range"), bounds);
            }
          }
        }
      }

      duration      = repeat.optString("duration", null);
      durationMax   = repeat.optString("durationMax", null);
      durationUnits = repeat.optString("durationUnits", null);
      count         = repeat.optString("count", null);
      frequency     = repeat.optString("frequency", null);
      frequencyMax  = repeat.optString("frequencyMax", null);
      period        = repeat.optString("period", null);
      periodMax     = repeat.optString("periodMax", null);
      periodUnits   = repeat.optString("periodUnits", null);
      when          = repeat.optString("when", null);
      d             = (duration == null)
                      ? 0
                      : SNumber.floatValue(duration);
      f             = (frequency == null)
                      ? 0
                      : SNumber.floatValue(frequency);
      p             = (period == null)
                      ? 0
                      : SNumber.floatValue(period);

      if (durationUnits != null) {
        s = durationUnits;

        if (d > 1) {
          sb.set(s).append("_p");
          s = sb.toString();
        }

        s = server.timeUnits.get(s);

        if (s != null) {
          durationUnits = s;
        }
      }

      if (periodUnits != null) {
        s = periodUnits;

        if (p > 1) {
          sb.set(s).append("_p");
          s = sb.toString();
        }

        s = server.timeUnits.get(s);

        if (s != null) {
          periodUnits = s;
        }
      }

      if (when != null) {
        when = when.toUpperCase(Locale.US);
        s    = server.timingWhen.get(when);

        if (s != null) {
          when = s;
        }
      }

      if ((frequencyMax != null) && frequencyMax.equals(frequency)) {
        frequencyMax = null;
      }

      if ((periodMax != null) && periodMax.equals(period)) {
        periodMax = null;
      }

      if ((durationMax != null) && durationMax.equals(duration)) {
        durationMax = null;
      }
    }

    JSONArray a = o.optJSONArray("event");

    sb.setLength(0);

    int len = (a == null)
              ? 0
              : a.length();

    if (len > 0) {
      sb.append(server.getResourceAsString("bv.timing.text.event")).append(" ");

      for (int i = 0; i < len; i++) {
        if (i > 0) {
          sb.append(", ");
        }

        sb.append(FHIRServer.convertDateTime(a.getString(i)));
      }

      if (len > 1) {
        sb.setLength(sb.length() - 1);
      }

      sb.append("; ");
    }

    if (bounds != null) {
      if (sb.length() > 0) {
        bounds = bounds.toLowerCase(Locale.getDefault());
      }

      if (appendBounds) {
        sb.append(bounds).append(", ");
      } else {
        o.put("_bounds", bounds);
      }
    }

    s = server.getResourceAsString("bv.timing.text.do_give");

    if (sb.length() > 0) {
      s = s.toLowerCase(Locale.getDefault());
    }

    sb.append(s).append(' ');

    if (dose != null) {
      sb.append(dose).append(" ");
    }

    if (repeat != null) {
      if (frequency != null) {
        boolean doTimes = true;

        if (frequencyMax != null) {
          sb.append(convertIfFraction(frequency, f));
          sb.append('-').append(convertIfFraction(frequencyMax, -1));
          sb.append(' ');
        } else {
          if (frequency.equals("1")) {
            sb.append(server.getResourceAsString("bv.timing.text.once"));
            doTimes = false;
          } else if (frequency.equals("2")) {
            sb.append(server.getResourceAsString("bv.timing.text.twice"));
            sb.append(' ');
            doTimes = false;
          } else {
            sb.append(convertIfFraction(frequency, f));
            sb.append(' ');
          }
        }

        if (doTimes) {
          sb.append(server.getResourceAsString("bv.timing.text.times")).append(' ');
        }
      }

      if (route != null) {
        sb.trim();
        sb.append(", ").append(route).append(" ");
      }

      if (period != null) {
        boolean p1 = SNumber.isEqual(p, 1);

        sb.trim().append(", ");

        if (frequency != null) {
          sb.append(server.getResourceAsString("bv.timing.text.every"));
          sb.append(' ');
        }

        if (periodMax != null) {
          sb.append(convertIfFraction(period, p));
          sb.append('-').append(convertIfFraction(periodMax, -1));
          sb.append(' ');
        } else if (!p1) {
          sb.append(convertIfFraction(period, p));
          sb.append(' ');
        }

        if (periodUnits != null) {
          sb.append(periodUnits).append(' ');
        }
      }

      if (when != null) {
        sb.append(when).append(' ');
      }

      if (duration != null) {
        sb.append(server.getResourceAsString("bv.timing.text.for")).append(' ');
        sb.append(convertIfFraction(duration, d)).append(' ');

        if (durationMax != null) {
          sb.append('-').append(convertIfFraction(durationMax, -1));
        }

        sb.append(durationUnits).append(' ');
      }

      if (count != null) {
        sb.append("; ").append(server.getResourceAsString("bv.timing.text.repeat")).append(' ');
        sb.append(count).append(' ');
        sb.append(server.getResourceAsString("bv.text.times")).append(' ');
      }
    }

    if (code != null) {
      sb.trim();

      if (sb.indexOf(',') != -1) {
        sb.append(", ");
      } else if (sb.length() > 0) {
        sb.append(" ");
      }

      sb.append(code);
    }

    return sb.trim().toString();
  }

  public static String getDataAsString(String url, String type) throws IOException {
    if (url == null) {
      return "";
    }

    return FHIRServer.getInstance().createLink(url).getContentAsString();
  }

  public static String getDataAsBase64String(String url, String type) throws IOException {
    if (url == null) {
      return "";
    }

    ByteArray ba = FHIRServer.getInstance().createLink(url).getContentAsBytes();

    return Base64.encodeBytes(ba.A, 0, ba._length);
  }

  public static void writeAttachment(JSONObject attachment, Writer w, boolean child) throws IOException {
    String title   = attachment.optString("title");
    String type    = attachment.optString("contentType", "text/plain");
    String b64Data = attachment.optString("data", null);

    if (child) {
      w.write(Documents.BOUNDARY_START);
      w.write("Content-Type: text/html\r\n\r\n");
    }

    FHIRUtils.writeHTMLDocumentStart(w, title);

    if (type.startsWith("text/html")) {
      if (b64Data == null) {
        w.write(getDataAsString(attachment.optString("url", null), "text/html"));
      } else {
        w.write(Functions.decodeBase64(b64Data));
      }
    } else if (type.startsWith("text/plain")) {
      w.write("<pre>\n");

      if (b64Data == null) {
        w.write(getDataAsString(attachment.optString("url", null), "text/plain"));
      } else {
        w.write(Functions.decodeBase64(b64Data));
      }

      w.write("</pre>\n");
    } else if (type.startsWith("image/")) {
      w.write("<div class=\"image_doc\">\n");
      w.write("<img alt=\"" + title + "\"");
      w.write(" src=\"data:" + type + ";base64,");

      if (b64Data == null) {
        b64Data = getDataAsBase64String(attachment.optString("url", null), type);
      }

      w.write(b64Data);
      w.write("\"/>\n");
      w.write("</div>\n");
    } else if (type.startsWith("application/json+fhir")) {
      JSONObject entry = new JSONObject(getDataAsString(attachment.optString("url", null), type));
      String     rtype = entry.optString("resourceType");

      if (rtype.equals("DiagnosticReport")) {
        DiagnosticReport r = new DiagnosticReport();

        r.readEntry(entry, null, w);
      } else if (rtype.equals("Composition")) {
        Composition r = new Composition();

        r.readEntry(entry, null, w);
      } else if (rtype.equals("Procedure")) {
        Procedures r = new Procedures();

        r.readEntry(entry, null, w);
      } else {
        w.write("<pre>\n");
        w.write(FHIRServer.getInstance().getResourceAsString("bv.text.cant_load_document"));
        w.write("</pre>\n");
      }
    } else {
      w.write("<h3>\n");
      w.write(FHIRServer.getInstance().getResourceAsString("bv.text.cant_load_document"));
      w.write("</h3>\n");
    }

    FHIRUtils.writeHTMLDocumentFinish(w);
  }

  public static String getValue(JSONObject value, boolean includeUnits, CharArray ca) {
    String     s;
    JSONObject o = value.optJSONObject("valueQuantity");

    if (o != null) {
      s = o.getString("value");

      String comparator = o.optString("comparator");
      String unit       = o.optString("unit", null);

      if ((comparator != null) || ((unit != null) && includeUnits)) {
        ca.clear();

        if (comparator != null) {
          ca.append(comparator);
        }

        ca.append(s);

        if (includeUnits && (unit != null)) {
          ca.append(" (").append(unit).append(")");
        }

        s = ca.toString();
      }

      return s;
    }

    s = value.optString("valueString", null);

    if (s != null) {
      return s;
    }

    o = value.optJSONObject("valueCodeableConcept");

    if (o != null) {
      return FHIRUtils.getBestMedicalText(o);
    }

    o = value.optJSONObject("valueRange");

    if (o != null) {
      return FHIRUtils.getRange(o, includeUnits, ca);
    }

    o = value.optJSONObject("valueRatio");

    if (o != null) {
      return FHIRUtils.getRatio(o, ca);
    }

    s = value.optString("valueTime", null);

    if (s != null) {
      return s;
    }

    s = value.optString("valueDateTime", null);

    if (s != null) {
      return FHIRServer.convertDateTime(s);
    }

    s = getPeriod(value.optJSONObject("valuePeriod"), ca);

    if (s != null) {
      return s;
    }

    o = value.optJSONObject("valueAttachment");

    if (o != null) {
      s = o.optString("title", null);

      if (s == null) {
        s = FHIRServer.getInstance().getResourceAsString("bv.text.see_report");
      }

      return s;
    }

    o = value.optJSONObject("valueSampledData");

    if (o != null) {
      return FHIRServer.getInstance().getResourceAsString("bv.text.see_report");
    }

    return s;
  }

  public static boolean isHTMLFormatRequested(ActionPath path) {
    return "html".equals(aRemoteService.getExtension(path));
  }

  public static boolean isJSONFormatRequested(ActionPath path) {
    return "json".equals(aRemoteService.getExtension(path));
  }

  public static void populateAddress(JSONObject src, JSONObject dst) {
    if (src == null) {
      return;
    }

    JSONArray a = src.optJSONArray("line");

    if (a != null) {
      dst.put("street", Helper.toString(a, "\\n"));
    }

    String s = src.optString("city", null);

    if (s != null) {
      dst.put("city", s);
    }

    s = src.optString("state", null);

    if (s != null) {
      dst.put("state_or_province", s);
    }

    s = src.optString("postalCode", null);

    if (s != null) {
      dst.put("zip_code", s);
    }

    s = src.optString("country", null);

    if (s != null) {
      dst.put("country", s);
    }
  }

  public static void populateTelcom(JSONArray src, JSONObject dst) {
    if (src == null) {
      return;
    }

    int len = src.length();

    for (int i = 0; i < len; i++) {
      JSONObject o     = src.getJSONObject(i);
      String     sys   = o.optString("system");
      String     value = o.optString("value");
      String     use   = o.optString("use");

      if (sys.equals("phone")) {
        if (use.equals("work")) {
          dst.put("office_number", value);
        } else if (use.equals("home")) {
          dst.put("home_number", value);
        } else if (use.equals("mobile")) {
          dst.put("mobile_number", value);
        }
      } else if (sys.equals("email")) {
        if (use.equals("work")) {
          dst.put("email", value);
        } else if (use.equals("home")) {
          dst.put("home_email", value);
        } else if (use.equals("mobile")) {
          dst.put("mobile_email", value);
        }
      }
    }

    if (!dst.containsKey("email")) {
      String s = dst.optString("mobile_email", null);

      if (s != null) {
        dst.put("email", s);
        dst.remove("mobile_email");
      } else {
        s = dst.optString("home_email", null);

        if (s != null) {
          dst.put("email", s);
          dst.remove("home_email");
        }
      }
    }
  }

  public static void writeActorList(Writer w, JSONArray list, CharArray ca) throws IOException {
    writeListStart(w);

    int len = list.size();

    for (int i = 0; i < len; i++) {
      writeListValue(w, getActor(list.getJSONObject(i), ca));
    }

    writeListFinish(w);
  }

  public static void writeCodableConcept(JSONWriter jw, JSONArray a, HashMap<String, String> map) throws IOException {
    int len = a.length();

    for (int i = 0; i < len; i++) {
      JSONObject idf  = a.getJSONObject(i);
      JSONObject type = idf.optJSONObject("type");
      JSONArray  aa   = (type == null)
                        ? null
                        : type.optJSONArray("coding");
      int        alen = (aa == null)
                        ? 0
                        : aa.length();

      for (int ai = 0; ai < alen; ai++) {
        JSONObject coding = aa.getJSONObject(ai);
        String     code   = coding.getString("code");

        code = map.get(code);

        if (code != null) {
          jw.key(code).value(idf.getString("value"));

          break;
        }
      }

      String code = idf.optString("system", null);

      if (code != null) {
        code = map.get(code);

        if (code != null) {
          jw.key(code).value(idf.getString("value"));
        }
      }
    }
  }

  public static void writeCodeableConceptList(Writer w, JSONArray list) throws IOException {
    writeListStart(w);

    int len = list.size();

    for (int i = 0; i < len; i++) {
      writeListValue(w, getBestMedicalText(list.getJSONObject(i)));
    }

    writeListFinish(w);
  }

  public static void writeDocumentTitle(Writer w, String title) throws IOException {
    w.append("<h1>").append(title).append("</h1>\n");
  }

  public static void writeEvent(Writer w, JSONObject event) throws IOException {
    String status = event.optString("status");
    String actor  = null;
    String time   = event.optString("dateTime", null);

    if (time != null) {
      time = FHIRServer.convertDateTime(time);
    }

    JSONObject o = event.optJSONObject("actor");

    if (o != null) {
      actor = o.optString("display");
    }

    w.append("<div class='event_div'>\n");
    writeTableStart(w);
    writeTableValue(w, getResourceAsString("bv.text.status"), status);

    if (time != null) {
      writeTableValue(w, getResourceAsString("bv.text.date"), time);
    }

    if (actor != null) {
      writeTableValue(w, getResourceAsString("bv.text.actor"), actor);
    }

    writeTableFinish(w);
    w.append("</div>\n");
  }

  public static void writeEvents(Writer w, JSONArray events) throws IOException {
    int len = (events == null)
              ? 0
              : events.length();

    if (len > 0) {
      w.append("<div class='events_div'>\n");
      w.append("<h3>").append(getResourceAsString("bv.text.events_annotations")).append("</h3>\n");

      for (int i = 0; i < len; i++) {
        writeEvent(w, events.getJSONObject(i));
      }

      w.append("</div>\n");
    }
  }

  public static void writeHTMLDocumentFinish(Writer w) throws IOException {
    w.append("\n</body></html>");
  }

  public static void writeHTMLDocumentStart(Writer w, String title) throws IOException {
    w.append("<html><head>\n");

    if (title != null) {
      w.append("<title>").append(title).append("</title>\n");
    }

    String ss = FHIRServer.getStyleSheet();

    if (ss != null) {
      w.write("<style type=\"text/css\">\n");
      w.write(ss);
      w.write("\n</style>\n");
    }

    w.append("</head><body>\n");
  }

  public static void writeMultipartSubDocumentStart(Writer w, String title) throws IOException {
    w.write(Documents.BOUNDARY_START);
    w.write("Content-Type: text/html\r\n\r\n");
    writeHTMLDocumentStart(w, title);
  }

  public static void writeMultipartDocumentFinish(Writer w) throws IOException {
    w.write(Documents.BOUNDARY_END);
  }

  public static void writeMultipartSubDocumentFinish(Writer w) throws IOException {
    writeHTMLDocumentFinish(w);
  }

  public static void writeMultipartDocumentIndex(String type, String date, String title, String url, Writer w)
          throws IOException {
    w.append(type).append('^').append(title);
    w.append("^");

    if (date != null) {
      w.append(date);
    }

    w.append("^").append((url == null)
                         ? "true"
                         : "false");
    w.append("^");

    if (url != null) {
      w.append(url);
    }

    w.append("\r\n");
  }

  public static void writeHTMLTag(Writer w, String tag, String text, boolean lf) throws IOException {
    w.append("<").append(tag).append(">");
    w.append(text);
    w.append("</").append(tag).append(">");

    if (lf) {
      w.append("\n");
    }
  }

  public static void writeListFinish(Writer w) throws IOException {
    w.append("</ul>\n");
  }

  public static void writeListStart(Writer w) throws IOException {
    w.append("<ul class='value ul>\n");
  }

  public static void writeListValue(Writer w, String value) throws IOException {
    value = Functions.escapeHTML(value, true, false);
    w.append("<li>").append(value).append("<li>\n");
  }

  public static void writeName(Object writer, JSONArray a) throws IOException {
    JSONWriter jw = null;
    Writer     w  = null;

    if (writer instanceof JSONWriter) {
      jw = (JSONWriter) writer;
    } else {
      w = (Writer) writer;
    }

    JSONObject name = null;
    int        len  = a.length();

    if (len == 1) {
      name = a.getJSONObject(0);
    } else {
      for (int i = 0; i < len; i++) {
        JSONObject o = a.getJSONObject(i);

        if ("usual".equals(o.opt("use"))) {
          name = o;

          break;
        }

        if ("official".equals(o.opt("use"))) {
          name = o;
        }
      }
    }

    if (name == null) {
      name = a.getJSONObject(0);
    }

    String s;

    if (jw != null) {
      jw.key("name");
    }

    s = name.optString("text", null);

    if (s != null) {
      if (jw != null) {
        jw.value(s);
      } else {
        w.write(s);
      }

      return;
    }

    a = name.optJSONArray("family");

    StringBuilder sb = new StringBuilder();

    if (a != null) {
      Helper.toString(sb, a.getObjectList(), " ");
      sb.append(", ");
    }

    a = name.optJSONArray("given");

    if (a != null) {
      Helper.toString(sb, a.getObjectList(), " ");
    }

    if (jw != null) {
      jw.value(sb.toString());
    } else {
      w.write(sb.toString());
    }
  }

  public static void writeNote(Writer w, JSONObject note) throws IOException {
    String author = null;
    String time   = note.optString("time", null);

    if (time != null) {
      time = Functions.convertDateTime(time);
    }

    JSONObject o = note.optJSONObject("authorReference");

    if (o != null) {
      author = o.optString("display");
    } else {
      author = note.optString("authorString", null);
    }

    w.append("<div class='note_div'>\n");

    if ((author != null) || (time != null)) {
      writeTableStart(w);

      if (author != null) {
        writeTableValue(w, getResourceAsString("bv.text.author"), author);
      }

      if (time != null) {
        writeTableValue(w, getResourceAsString("bv.text.date"), time);
      }

      writeTableFinish(w);
      w.write("<hr/>\n");
    }

    w.write(html(note.optString("text")));
    w.append("</div>\n");
  }

  public static void writeNotes(Writer w, JSONArray notes) throws IOException {
    int len = (notes == null)
              ? 0
              : notes.length();

    if (len > 0) {
      w.append("<div class='notes_div'>\n");
      w.append("<h3>").append(getResourceAsString("bv.text.notes_annotations")).append("</h3>\n");

      for (int i = 0; i < len; i++) {
        writeNote(w, notes.getJSONObject(i));
      }

      w.append("</div>\n");
    }
  }

  public static boolean writeText(Writer w, JSONObject text, boolean writeHeader, boolean writeGenerated)
          throws IOException {
    String status = text.optString("status");

    if (status.equals("empty")) {
      return false;
    }

    String  div       = text.optString("div", "");
    boolean generated = status.equals("generated");

    do {
      if (!writeGenerated && generated) {
        return false;
      }

      if (div.length() == 0) {
        return false;
      }

      if (writeHeader) {
        status = getResourceAsString(generated
                                     ? "bv.text.generated_text"
                                     : "bv.text.additional_text");
        w.append("<h3>").append(status).append("</h3>\n");
      }

      w.append("<div class='text_div'>\n");
      w.append(div);
      w.append("</div>\n");
    } while(false);

    return true;
  }

  public static void writeQuotedStringIfNecessary(Writer w, String value, CharArray ca) throws IOException {
    char[] a;
    int    len=value.length();
    if (ca == null) {
      a   = value.toCharArray();
    } else {
      ca.set(value);
      a   = ca.A;
    }

    boolean quoted = false;
    int     lp     = 0;
    int     i      = 0;
    if(len>0 && value.charAt(0)=='{') {
      if(value.startsWith("{fgColor:")) {
        int n=CharScanner.indexOf(a, 0, len, '}', true, true);
        if(n!=-1) {
          w.write(a, 0, n+1);
          i=n+1;
          lp=i;
        }
      }
    }
    

    while(i < len) {
      char c = a[i++];

      switch(c) {
        case '|' :
        case '^' :
          if (!quoted) {
            quoted = true;
            w.write('"');
          }

          break;

        case '\t' :
          if (!quoted) {
            quoted = true;
            w.write('"');
          }

          w.write(a, lp, i - lp - 1);
          w.write("\\t");
          i++;
          lp = i;

          break;

        case '[' :
        case ']' :
          if (!quoted) {
            quoted = true;
            w.write('"');
          }

          w.write(a, lp, i - lp - 1);
          w.write('\\');
          w.write(c);
          lp = i;

          break;

        case '\"' :
          if (!quoted) {
            quoted = true;
            w.write('"');
          }

          w.write(a, lp, i - lp - 1);
          w.write("\\\"");
          lp = i;

          break;

        case '\n' :
          if (!quoted) {
            quoted = true;
            w.write('"');
          }

          w.write(a, lp, i - lp - 1);
          w.write("\\n");
          lp = i;

          break;

        case '\r' :
          if (!quoted) {
            quoted = true;
            w.write('"');
          }

          w.write(a, lp, i - lp - 1);
          lp = i;

          break;
      }
    }

    if (lp < len) {
      w.write(a, lp, len - lp);
    }

    if (quoted) {
      w.write('"');
    }
  }

  public static void writeReferenceList(Writer w, JSONArray list) throws IOException {
    writeListStart(w);

    int len = list.size();

    for (int i = 0; i < len; i++) {
      writeListValue(w, getReferenceText(list.getJSONObject(i)));
    }

    writeListFinish(w);
  }

  public static void writeSectionEnd(Writer w) throws IOException {
    w.append("</div>\n");
  }

  public static void writeSectionStart(Writer w, String title) throws IOException {
    w.append("<h2 class='sub'>").append(title).append("</h2>\n");
    w.append("<div class='sub2'>\n");
  }

  public static void writeSubDivEnd(Writer w) throws IOException {
    w.append("</div>");
  }

  public static void writeSubDivStart(Writer w) throws IOException {
    w.append("<div class='sub_div'>");
  }

  public static void writeSubSectionStart(Writer w, String title) throws IOException {
    w.append("<h3 class='sub'>").append(title).append("</h3>\n");
    w.append("<div class='sub3'>\n");
  }

  public static void writeSubSubSectionStart(Writer w, String title) throws IOException {
    w.append("<h4 class='sub'>").append(title).append("</h4>\n");
    w.append("<div class='sub4'>\n");
  }

  public static void writeTableFinish(Writer w) throws IOException {
    w.append("</tbody>\n");
    w.append("</table>\n");
  }

  public static void writeTableSectionFinish(Writer w) throws IOException {
    w.append("</td></tr>\n");
  }

  public static void writeTableSectionStart(Writer w, String title) throws IOException {
    writeTableValueStart(w, title);
    w.append("<td class='section'>");
  }

  public static void writeTableStart(Writer w) throws IOException {
    w.append("<table class='value_table'>\n");
    w.append("<tbody>\n");
  }

  public static void writeTableValue(Writer w, String name, String value) throws IOException {
    if (value != aFHIRemoteService.MISSING_INVALID) {
      value = Functions.escapeHTML(value, true, false);
    }

    if (name != null) {
      writeTableValueStart(w, name);
    } else {
      w.append("<tr><td></td>");
    }

    w.append("<td>").append(value).append("</td></tr>\n");
  }

  public static void writeTableValueStart(Writer w, String name) throws IOException {
    w.append("<tr><td class=\"value_name\">").append(name).append(":</td>");
  }

  public static void writeTableValueActor(Writer w, String name, JSONArray list, CharArray ca) throws IOException {
    int len = (list == null)
              ? 0
              : list.length();

    if (len == -0) {
      return;
    }

    if (name != null) {
      writeTableValueStart(w, name);
    } else {
      w.append("<tr><td></td>");
    }

    w.append("<td>");

    if (len == 1) {
      w.write(html(getActor(list.getJSONObject(0), ca)));
    } else {
      writeActorList(w, list, ca);
    }

    w.append("</td></tr>\n");
  }

  private static String html(String value) {
    return Functions.escapeHTML(value, true, false);
  }

  public static void writeTableValueCodeableConcept(Writer w, String name, JSONArray list) throws IOException {
    int len = (list == null)
              ? 0
              : list.length();

    if (len == -0) {
      return;
    }

    if (name != null) {
      writeTableValueStart(w, name);
    } else {
      w.append("<tr><td></td>");
    }

    w.append("<td>");

    if (len == 1) {
      w.write(html(getBestMedicalText(list.getJSONObject(0))));
    } else {
      writeCodeableConceptList(w, list);
    }

    w.append("</td></tr>\n");
  }

  public static void writeTableValueReference(Writer w, String name, JSONArray list) throws IOException {
    int len = (list == null)
              ? 0
              : list.length();

    if (len == -0) {
      return;
    }

    if (name != null) {
      writeTableValueStart(w, name);
    } else {
      w.append("<tr><td></td>");
    }

    w.append("<td>");

    if (len == 1) {
      w.write(html(getReferenceText(list.getJSONObject(0))));
    } else {
      writeReferenceList(w, list);
    }

    w.append("</td></tr>\n");
  }

  private static String getResourceAsString(String name) {
    return FHIRServer.getInstance().getResourceAsString(name);
  }

  public static class MedicalCode {
    public String hl7Code;
    public String snomedCode;
    public String loincCode;
    public String hl7Display;
    public String loincDisplay;
    public String snomedDisplay;
    public String text;

    public MedicalCode(String loincCode, String snomedCode) {
      super();
      this.loincCode  = loincCode;
      this.snomedCode = snomedCode;
    }

    public MedicalCode(String loincCode, String snomedCode, String loincDisplay, String snomedDisplay) {
      super();
      this.loincCode     = loincCode;
      this.snomedCode    = snomedCode;
      this.loincDisplay  = loincDisplay;
      this.snomedDisplay = snomedDisplay;
    }

    public MedicalCode(String hl7Code, String snomedCode, String loincCode, String hl7Display, String loincDisplay,
                       String snomedDisplay) {
      super();
      this.hl7Code       = hl7Code;
      this.snomedCode    = snomedCode;
      this.loincCode     = loincCode;
      this.hl7Display    = hl7Display;
      this.loincDisplay  = loincDisplay;
      this.snomedDisplay = snomedDisplay;
    }

    public String getBestCode() {
      if (hl7Code != null) {
        return hl7Code;
      }

      if (loincCode != null) {
        return loincCode;
      }

      return snomedCode;
    }

    public String getBestText() {
      if (text != null) {
        return text;
      }

      if (hl7Display != null) {
        return hl7Display;
      }

      if (loincDisplay != null) {
        return loincDisplay;
      }

      if (snomedDisplay == null) {
        return getBestCode();
      }

      return snomedDisplay;
    }

    public boolean isOneOf(Set<String> codes) {
      if ((loincCode != null) && codes.contains(loincCode)) {
        return true;
      }

      if ((snomedCode != null) && codes.contains(snomedCode)) {
        return true;
      }

      return false;
    }

    public boolean isSNOMEDOnlyCode() {
      return (loincCode == null) && (hl7Code == null);
    }

    public void resolveHL7Display(HashMap<String, String> codeMap) {
      if ((hl7Display == null) && (hl7Code != null)) {
        hl7Display = codeMap.get(hl7Code);
      }
    }
  }
}
