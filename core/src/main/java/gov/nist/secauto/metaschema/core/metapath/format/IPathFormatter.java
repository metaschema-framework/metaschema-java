/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.format;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyInstanceGroupedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;

import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This interface provides an implementation contract for all path formatters.
 * When {@link #format(IPathSegment)} is called on a formatter implementation,
 * the formatter will render the path segments based on the implemented path
 * syntax. This allows a collection of path segments to be rendered in different
 * forms by swapping out the formatter used.
 *
 * A path formatter is expected to be stateless and thus thread safe.
 */
public interface IPathFormatter {
  /**
   * A path formatter that produces Metapath-based paths.
   */
  @NonNull
  IPathFormatter METAPATH_PATH_FORMATER = new MetapathFormatter();

  /**
   * Format the path represented by the provided path segment. The provided
   * segment is expected to be the last node in this path. A call to
   * {@link IPathSegment#getPathStream()} or {@link IPathSegment#getPath()} can be
   * used to walk the path tree in descending order.
   *
   * @param segment
   *          The last segment in a sequence of path segments
   * @return a formatted path
   * @see IPathSegment#getPathStream()
   * @see IPathSegment#getPath()
   */
  @SuppressWarnings("null")
  @NonNull
  default String format(@NonNull IPathSegment segment) {
    return segment.getPathStream().map(pathSegment -> {
      return pathSegment.format(this);
    }).collect(Collectors.joining("/"));
  }

  /**
   * This visitor callback is used to format an individual flag path segment.
   *
   * @param flag
   *          the node to format
   * @return the formatted text for the segment
   */
  @NonNull
  String formatFlag(@NonNull IFlagNodeItem flag);

  /**
   * This visitor callback is used to format an individual field path segment.
   *
   * @param field
   *          the node to format
   * @return the formatted text for the segment
   */
  @NonNull
  String formatField(@NonNull IFieldNodeItem field);

  /**
   * This visitor callback is used to format an individual assembly path segment.
   *
   * @param assembly
   *          the node to format
   * @return the formatted text for the segment
   */
  @NonNull
  String formatAssembly(@NonNull IAssemblyNodeItem assembly);

  /**
   * This visitor callback is used to format an individual grouped assembly path
   * segment.
   *
   * @param assembly
   *          the node to format
   * @return the formatted text for the segment
   */
  @NonNull
  String formatAssembly(@NonNull IAssemblyInstanceGroupedNodeItem assembly);

  /**
   * This visitor callback is used to format a root assembly path segment.
   *
   * @param root
   *          the node to format
   * @return the formatted text for the segment
   */
  @NonNull
  String formatRootAssembly(@NonNull IRootAssemblyNodeItem root);

  /**
   * This visitor callback is used to format an individual document path segment.
   *
   * @param document
   *          the node to format
   * @return the formatted text for the segment
   */
  @NonNull
  String formatDocument(@NonNull IDocumentNodeItem document);

  /**
   * This visitor callback is used to format an individual metaschema path
   * segment.
   *
   * @param metaschema
   *          the node to format
   * @return the formatted text for the segment
   */
  @NonNull
  String formatMetaschema(@NonNull IModuleNodeItem metaschema);
}
