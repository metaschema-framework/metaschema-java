/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.format;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IMetapathQueryable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A named segment of a path that can be formatted.
 */
public interface IPathSegment extends IMetapathQueryable {
  /**
   * Get the path for this node item using the provided formatter.
   *
   * @param formatter
   *          the path formatter to use to produce the path
   *
   * @return the formatted path
   */
  @NonNull
  default String toPath(@NonNull IPathFormatter formatter) {
    return formatter.format(this);
  }

  /**
   * Apply formatting for the path segment. This is a visitor pattern that will be
   * called to format each segment in a larger path.
   *
   * @param formatter
   *          the path formatter
   * @return a textual representation of the path segment
   */
  @NonNull
  String format(@NonNull IPathFormatter formatter);

  /**
   * Get a list of path segments, starting at the root and descending.
   *
   * @return a list of path segments in descending order
   */
  @SuppressWarnings("null")
  @NonNull
  default List<IPathSegment> getPath() {
    return getPathStream().collect(Collectors.toUnmodifiableList());
  }

  /**
   * Get a stream of path segments, starting at the root and descending.
   *
   * @return a stream of path segments in descending order
   */
  @NonNull
  Stream<? extends IPathSegment> getPathStream();

  /**
   * Get the value associated with the path segment.
   *
   * @return the value or {@code} if no value is associated with this path segment
   */
  @Override
  INodeItem getNodeItem();
}
