/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath sequence supporting an unbounded number of items backed initially
 * by a stream.
 *
 * @param <ITEM>
 *          the Java type of the items
 */
public class StreamSequence<ITEM extends IItem>
    extends AbstractSequence<ITEM> {

  private Stream<ITEM> stream;
  private List<ITEM> list;

  /**
   * Construct a new sequence using the provided item stream.
   *
   * @param stream
   *          the items to add to the sequence
   */
  public StreamSequence(@NonNull Stream<ITEM> stream) {
    Objects.requireNonNull(stream, "stream");
    this.stream = stream;
  }

  @Override
  public List<ITEM> getValue() {
    synchronized (this) {
      if (list == null) {
        list = stream().collect(Collectors.toUnmodifiableList());
      }
      assert list != null;
      return list;
    }
  }

  @Override
  public Stream<ITEM> stream() {
    @NonNull Stream<ITEM> retval;
    // Ensure thread safety and prevent multiple consumptions of the stream
    synchronized (this) {
      if (list == null) {
        if (stream == null) {
          throw new IllegalStateException("stream is already consumed");
        }
        assert stream != null;
        retval = stream;
        stream = null; // NOPMD - readability
      } else {
        retval = ObjectUtils.notNull(list.stream());
      }
    }
    return retval;
  }

  @Override
  public void forEach(Consumer<? super ITEM> action) {
    stream().forEachOrdered(action);
  }
}
