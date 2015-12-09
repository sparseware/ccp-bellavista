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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import com.appnativa.rare.Platform;
import com.appnativa.util.ByteArray;
import com.appnativa.util.CharArray;
import com.appnativa.util.ISO88591Helper;
import com.sparseware.bellavista.service.FileBackedBufferedOutputStream.DeleteFileOnCloseInputStream;

/**
 * A writer that writes to memory until it buffer is exceeded
 * and then converts to writing data to a file.
 *
 * <p>
 * If data is written to a file, closing the stream will cause the file to be deleted.
 * </p>
 * 
 * @author Don DeCoteau
 *
 */
public class FileBackedBufferedWriter extends Writer {
  File       backingFile;
  int        bufferSize;
  int        maxBufferSize;
  int        count;
  CharArray  charWriter;
  FileWriter fileWriter;

  /**
   * Creates a new instance.
   * The initial buffer size is set to 1K and maximum buffer size is set to 32K.
   */
  public FileBackedBufferedWriter() {
    this(1024);
  }

  /**
   * Creates a new instance.
   *
   * @param bufferSize the initial size of the memory buffer
   * @param maxBufferSize the maximum size that the memory buffer can reach
   * before data starts being written to a file.
   *
   */
  public FileBackedBufferedWriter(int bufferSize, int maxBufferSize) {
    super();
    this.bufferSize    = bufferSize;
    this.maxBufferSize = maxBufferSize;
  }

  /**
   * Creates a new instance.
   * The maximum buffer size is set to 32K
   *
   * @param bufferSize the initial size of the memory buffer
   */
  public FileBackedBufferedWriter(int bufferSize) {
    this(bufferSize, 1024 * 1024 * 32);
  }

  /**
   * Gets the length of the content that was written
   * @return  the length of the content that was written
   */
  public long getContentLength() {
    if (fileWriter != null) {
      try {
        fileWriter.flush();
      } catch(IOException e) {}

      return (int) backingFile.length();
    }

    if (charWriter == null) {
      return 0;
    }

    return charWriter._length;
  }

  @Override
  public void write(int b) throws IOException {
    count++;

    if ((count > maxBufferSize) && (fileWriter == null)) {
      createFileWriter();
    }

    if (fileWriter != null) {
      fileWriter.write(b);
    } else {
      if (charWriter == null) {
        charWriter = new CharArray(bufferSize);
      }

      charWriter.append((char) b);
    }
  }

  @Override
  public void write(char[] b, int off, int len) throws IOException {
    count += len;

    if ((count > maxBufferSize) && (fileWriter == null)) {
      createFileWriter();
    }

    if (fileWriter != null) {
      fileWriter.write(b, off, len);
    } else {
      if (charWriter == null) {
        charWriter = new CharArray(bufferSize);
      }

      charWriter.append(b, off, len);
    }
  }

  @Override
  public void close() throws IOException {
    if (fileWriter != null) {
      fileWriter.close();
    }
  }

  @Override
  public void flush() throws IOException {
    if (fileWriter != null) {
      fileWriter.flush();
    }
  }

  /**
   * Clears the content's of the writer
   * while leaving the writer open
   */
  public void clear() {
    if (fileWriter != null) {
      try {
        fileWriter.close();
      } catch(IOException ignore) {}

      try {
        backingFile.delete();
      } catch(Exception ignore) {}

      backingFile = null;
      fileWriter  = null;
    }

    if (charWriter != null) {
      charWriter.setLength(0);
    }
  }

  /**
   * Gets a reader to read the  written content
   * @return a reader to read the  written content
   * @throws IOException
   */
  public Reader getReader() throws IOException {
    if (fileWriter != null) {
      fileWriter.close();

      return new DeleteFileOnCloseReader(backingFile);
    }

    if (charWriter == null) {
      return new StringReader("");
    }

    return charWriter;
  }

  /**
   * Gets an input stream to read the  written content
   * @return an input stream to read the  written content
   * @throws IOException
   */
  public InputStream getInputStream() throws IOException {
    if (fileWriter != null) {
      fileWriter.close();

      return new DeleteFileOnCloseInputStream(backingFile);
    }

    if (charWriter == null) {
      return new ByteArrayInputStream(new byte[0]);
    }

    ByteArray ba = new ByteArray(charWriter.length());

    ba._length = charWriter.getBytes(ba, ISO88591Helper.getInstance());

    return ba;
  }

  /**
   * Creates the backing file writer
   *
   * @throws IOException
   */
  protected void createFileWriter() throws IOException {
    backingFile = Platform.createCacheFile("BVOutput");
    fileWriter  = new FileWriter(backingFile);
  }

  /**
   * A file reader extension that deletes the file
   * when the reader is closed
   *
   * @author Don DeCoteau
   *
   */
  static class DeleteFileOnCloseReader extends FileReader {
    File file;

    public DeleteFileOnCloseReader(File file) throws IOException {
      super(file);
      this.file = file;
    }

    @Override
    public void close() throws IOException {
      super.close();

      try {
        file.delete();
      } catch(Exception ignore) {}
    }
  }
}
