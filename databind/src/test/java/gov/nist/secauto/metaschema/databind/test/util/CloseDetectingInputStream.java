/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.test.util;

import org.eclipse.jdt.annotation.Owning;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A test utility that wraps an input stream to detect when it is closed.
 * <p>
 * This class is used in tests to verify that resource ownership semantics are
 * correctly implemented. It tracks whether {@link #close()} has been called,
 * allowing tests to assert that streams are properly closed when expected.
 */
public class CloseDetectingInputStream
    extends InputStream {

  @Owning
  private final InputStream delegate;
  private boolean closed;

  /**
   * Create a new input stream that will proxy calls to the provided
   * {@code delegate} and record when {@link #close()} is called on this instance.
   *
   * @param delegate
   *          the underlying input stream. The new instance owns this stream and
   *          is responsible for closing it.
   */
  public CloseDetectingInputStream(@Owning @NonNull InputStream delegate) {
    this.delegate = delegate;
  }

  /**
   * Indicates if {@link #close()} has been called.
   *
   * @return {@code true} if {@link #close()} has been called, or {@code false}
   *         otherwise
   */
  public boolean isClosed() {
    return closed;
  }

  @Override
  public int read() throws IOException {
    return delegate.read();
  }

  @Override
  public int read(byte[] byteArray) throws IOException {
    return delegate.read(byteArray);
  }

  @Override
  public int read(byte[] byteArray, int off, int len) throws IOException {
    return delegate.read(byteArray, off, len);
  }

  @Override
  public byte[] readAllBytes() throws IOException {
    return delegate.readAllBytes();
  }

  @Override
  public byte[] readNBytes(int len) throws IOException {
    return delegate.readNBytes(len);
  }

  @Override
  public int readNBytes(byte[] byteArray, int off, int len) throws IOException {
    return delegate.readNBytes(byteArray, off, len);
  }

  @Override
  public long skip(long numBytes) throws IOException {
    return delegate.skip(numBytes);
  }

  @Override
  public int available() throws IOException {
    return delegate.available();
  }

  @Override
  public void close() throws IOException {
    delegate.close();
    closed = true;
  }

  @SuppressWarnings("sync-override")
  @Override
  public void mark(int readlimit) {
    delegate.mark(readlimit);
  }

  @SuppressWarnings("sync-override")
  @Override
  public void reset() throws IOException {
    delegate.reset();
  }

  @Override
  public boolean markSupported() {
    return delegate.markSupported();
  }

  @Override
  public long transferTo(OutputStream out) throws IOException {
    return delegate.transferTo(out);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
