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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.appnativa.rare.Platform;
import com.appnativa.util.io.ByteArrayOutputStreamEx;

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
public class FileBackedBufferedOutputStream extends OutputStream {
  File                    backingFile;
  int                     bufferSize;
  int                     maxBufferSize;
  int                     count;
  ByteArrayOutputStreamEx byteStream;
  FileOutputStream        fileStream;

  /**
   * Creates a new instance.
   * The initial buffer size is set to 1K and maximum buffer size is set to 32K.
   */
  public FileBackedBufferedOutputStream() {
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
  public FileBackedBufferedOutputStream(int bufferSize, int maxBufferSize) {
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
  public FileBackedBufferedOutputStream(int bufferSize) {
    this(bufferSize, 1024 * 1024 * 32);
  }

  @Override
  public void write(int b) throws IOException {
    count++;

    if ((count > maxBufferSize) && (fileStream == null)) {
      createFileStream();
    }

    if (fileStream != null) {
      fileStream.write(b);
    } else {
      if (byteStream == null) {
        byteStream = new ByteArrayOutputStreamEx(bufferSize);
      }

      byteStream.write(b);
    }
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    count += len;

    if ((count > maxBufferSize) && (fileStream == null)) {
      createFileStream();
    }

    if (fileStream != null) {
      fileStream.write(b, off, len);
    } else {
      if (byteStream == null) {
        byteStream = new ByteArrayOutputStreamEx(bufferSize);
      }

      byteStream.write(b, off, len);
    }
  }

  @Override
  public void close() throws IOException {
    super.close();

    if (fileStream != null) {
      fileStream.close();
    }
  }

  @Override
  public void flush() throws IOException {
    super.flush();

    if (fileStream != null) {
      fileStream.flush();
    }
  }

  /**
   * Gets an input stream to read the  written content
   * @return an input stream to read the  written content
   * @throws IOException
   */
  public InputStream getInputStream() throws IOException {
    if (fileStream != null) {
      fileStream.close();

      return new DeleteFileOnCloseInputStream(backingFile);
    }

    if (byteStream == null) {
      return new ByteArrayInputStream(new byte[0]);
    }

    return new ByteArrayInputStream(byteStream.getArray(), 0, byteStream.size());
  }

  /**
   * Creates the backing file output stream
   *
   * @throws IOException
   */
  protected void createFileStream() throws FileNotFoundException {
    backingFile = Platform.createCacheFile("BVOutput");
    fileStream  = new FileOutputStream(backingFile);
  }

  /**
   * A file input stream extension that deletes the file
   * when the stream is closed
   *
   * @author Don DeCoteau
   *
   */
  public static class DeleteFileOnCloseInputStream extends FileInputStream {
    File file;

    public DeleteFileOnCloseInputStream(File file) throws FileNotFoundException {
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
