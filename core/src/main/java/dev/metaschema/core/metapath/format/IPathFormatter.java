/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.format;

import dev.metaschema.core.metapath.item.node.IAssemblyInstanceGroupedNodeItem;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.IDocumentNodeItem;
import dev.metaschema.core.metapath.item.node.IFieldNodeItem;
import dev.metaschema.core.metapath.item.node.IFlagNodeItem;
import dev.metaschema.core.metapath.item.node.IModuleNodeItem;
import dev.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;

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
   * A path formatter that produces XPath 3.1 paths with EQName-qualified names.
   * <p>
   * This formatter generates namespace-qualified paths using the EQName format
   * (e.g., {@code Q{http://example.com}element[1]}), suitable for use with XML
   * tooling that requires namespace qualification.
   *
   * @see XPathFormatter
   */
  @NonNull
  IPathFormatter XPATH_PATH_FORMATTER = new XPathFormatter();

  /**
   * A path formatter that produces RFC 6901 JSON Pointer paths.
   * <p>
   * This formatter generates JSON Pointer paths suitable for use with JSON
   * tooling and JSON-based error reporting. Uses JSON property names, 0-based
   * array indices, and proper RFC 6901 escaping.
   *
   * @see JsonPointerFormatter
   * @see <a href="https://www.rfc-editor.org/rfc/rfc6901">RFC 6901 - JSON
   *      Pointer</a>
   */
  @NonNull
  IPathFormatter JSON_POINTER_PATH_FORMATTER = new JsonPointerFormatter();

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
