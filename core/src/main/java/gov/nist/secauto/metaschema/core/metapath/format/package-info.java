/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides formatters for generating path expressions from Metaschema node
 * items.
 * <p>
 * This package contains interfaces and implementations for converting sequences
 * of path segments (representing navigation through a Metaschema document
 * structure) into formatted path strings. Different formatters can produce
 * paths in various syntaxes, such as Metapath expressions or JSON path
 * notation.
 * <p>
 * Key interfaces and classes:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter} -
 * Core interface defining the contract for path formatters with implementations
 * for different path syntaxes</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.format.IPathSegment} -
 * Represents a single segment in a path, providing navigation to parent
 * segments and access to associated node items</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.format.MetapathFormatter}
 * - Produces Metapath expression syntax for paths (e.g.,
 * {@code /root/assembly/field})</li>
 * </ul>
 * <p>
 * Path formatters are primarily used for:
 * <ul>
 * <li>Generating human-readable error messages that indicate the location of
 * schema validation failures</li>
 * <li>Creating navigational references for documentation and debugging</li>
 * <li>Providing context when reporting constraint violations</li>
 * </ul>
 * <p>
 * Typical usage:
 *
 * <pre>{@code
 * // Get a path formatter
 * IPathFormatter formatter = IPathFormatter.METAPATH_PATH_FORMATER;
 *
 * // Format a path from a node item
 * INodeItem nodeItem = ...; // some node in a document
 * String path = nodeItem.toPath(formatter);
 * // Result: "/root-assembly/child-assembly/field[@name='value']"
 * }</pre>
 * <p>
 * Path formatters are designed to be stateless and thread-safe, allowing reuse
 * across multiple formatting operations.
 */

package gov.nist.secauto.metaschema.core.metapath.format;
