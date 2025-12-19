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
 * paths in various syntaxes for different use cases.
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
 * {@code /root/assembly[1]/field[1]})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.format.XPathFormatter} -
 * Produces XPath 3.1 paths with EQName-qualified names (e.g.,
 * {@code /Q{http://example.com}root/Q{http://example.com}assembly[1]})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.format.JsonPointerFormatter}
 * - Produces RFC 6901 JSON Pointer paths (e.g.,
 * {@code /root/assemblies/0/id})</li>
 * </ul>
 * <p>
 * Available formatter constants on {@link IPathFormatter}:
 * <ul>
 * <li>{@link IPathFormatter#METAPATH_PATH_FORMATER} - Metapath syntax</li>
 * <li>{@link IPathFormatter#XPATH_PATH_FORMATTER} - XPath 3.1 with EQNames</li>
 * <li>{@link IPathFormatter#JSON_POINTER_PATH_FORMATTER} - RFC 6901 JSON
 * Pointer</li>
 * </ul>
 * <p>
 * Path formatters are primarily used for:
 * <ul>
 * <li>Generating human-readable error messages that indicate the location of
 * schema validation failures</li>
 * <li>Creating navigational references for documentation and debugging</li>
 * <li>Providing context when reporting constraint violations</li>
 * <li>Integrating with XML tooling (XPath formatter)</li>
 * <li>Integrating with JSON tooling (JSON Pointer formatter)</li>
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
 * // Result: "/root-assembly/child-assembly[1]/field[1]"
 *
 * // For JSON Pointer paths:
 * String jsonPath = nodeItem.toPath(IPathFormatter.JSON_POINTER_PATH_FORMATTER);
 * // Result: "/root-assembly/child-assemblies/0/field"
 * }</pre>
 * <p>
 * Path formatters are designed to be stateless and thread-safe, allowing reuse
 * across multiple formatting operations.
 */

package gov.nist.secauto.metaschema.core.metapath.format;
