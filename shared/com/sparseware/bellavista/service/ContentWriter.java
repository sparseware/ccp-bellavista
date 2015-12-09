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
package com.sparseware.bellavista.service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.appnativa.rare.exception.ApplicationException;
import com.appnativa.util.CharArray;
import com.appnativa.util.CharScanner;

/**
 * This class represents a writer that is use to send data
 * back to the client. A remote service implementation will retrieve data from
 * its host, and reformat the data to a format that the client expects, and then
 * use an instance of this class to send the data back to the client.
 * 
 * @author Don DeCoteau
 */
public class ContentWriter extends FileBackedBufferedWriter {
  protected FileBackedBufferedOutputStream contentStream;
  protected Writer                         contentWriter;
  protected CharArray                      caIn;
  protected CharArray                      caOut;

  /**
   * Escapes a string
   *
   * @param chars
   *          the characters
   * @param pos
   *          the starting position within the characters array
   * @param len
   *          the number of characters
   * @param out
   *          the array to use to store the result
   * @return the passed in array
   * @throws IOException
   */
  public boolean escape(char[] chars, int pos, int len, CharArray out) {
    char    c;
    boolean quote = false;

    for (int i = 0; i < len; i++) {
      c = chars[pos + i];

      switch(c) {
        case '\\' :
          out.append('\\');

          break;

        case '\r' :
          out.append('\\');
          c = 'r';

          break;

        case '\n' :
          out.append('\\');
          c = 'n';

          break;

        case '\t' :
          out.append('\\');
          c = 't';

          break;

        case '\b' :
          out.append('\\');
          c = 'b';

          break;

        case '\f' :
          out.append('\\');
          c = 'f';

          break;

        case '\"' :
          out.append('\\');
          c = '\"';

          break;

        case '^' :
        case '\'' :
          quote = true;

          break;

        default :
          if ((c < 32) || (c > 126)) {
            CharScanner.charToUnicodeString(c, out, true);

            continue;
          }

          break;
      }

      out.append(c);
    }

    return quote;
  }

  /**
   * Writes a string to the writer
   *
   * @param s
   *          the string
   */
  public void print(String s) {
    try {
      append(s);
    } catch(IOException e) {
      throw new ApplicationException(e);
    }
  }

  /**
   * Writes a list of strings the to writer with each string
   * being separated by a the record separator '^'.
   * <p>
   * A linefeed is appended to the end of the string.
   * </p>
   *
   * @param list the list of strings
   */
  public void println(List<String> list) {
    try {
      int len = list.size();

      if (caIn == null) {
        caIn = new CharArray();
      }

      if (caOut == null) {
        caOut = new CharArray();
      }

      CharArray ca  = caIn;
      CharArray out = caOut;

      out._length = 0;

      for (int i = 0; i < len; i++) {
        if (i != 0) {
          write('^');
        }

        String s = list.get(i);

        ca.set(s);

        if (escape(ca.A, 0, ca._length, out)) {
          write('\"');
          write(out.A, 0, out._length);
          write('\"');
        } else {
          write(out.A, 0, out._length);
        }
      }

      write('\n');
    } catch(IOException e) {
      throw new ApplicationException(e);
    }
  }

  /**
   * Writes a string to the writer
   * <p>
   * A linefeed is appended to the end of the string.
   * </p>
   *
   * @param s
   *          the string
   */
  protected void println(String s) {
    try {
      append(s).append("\n");
    } catch(IOException e) {
      throw new ApplicationException(e);
    }
  }
}
