/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Support for parsing and rendering HTML and Markdown text using Flexmark.
 * <p>
 * This package provides Flexmark-based infrastructure for Metaschema markup
 * processing, including custom extensions, visitors, and writers for
 * specialized Metaschema markup requirements. The implementation uses the
 * <a href="https://github.com/vsch/flexmark-java">Flexmark</a> library for
 * Markdown parsing and HTML rendering.
 * <h2>Core Components</h2>
 * <ul>
 * <li>{@link FlexmarkFactory} - Factory for creating configured Flexmark parser
 * and renderer instances with Metaschema-specific extensions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.FlexmarkConfiguration}
 * - Centralized configuration for Flexmark options and extensions</li>
 * </ul>
 * <h2>Custom Extensions</h2>
 * <p>
 * Metaschema-specific Flexmark extensions:
 * <ul>
 * <li>{@link HtmlQuoteTagExtension} - Supports HTML {@code
 * <q>} tag parsing and rendering</li>
 * <li>{@link InsertAnchorExtension} - Enables insertion of anchor elements for
 * headings</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.HtmlCodeRenderExtension}
 * - Custom rendering for inline code elements</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.SuppressPTagExtension}
 * - Suppresses paragraph tags in single-line markup</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.FixedEmphasisDelimiterProcessor}
 * - Fixes emphasis delimiter processing for Metaschema requirements</li>
 * </ul>
 * <h2>Visitors and Writers</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.IMarkupVisitor}
 * - Interface for visiting markup AST nodes</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.MarkupVisitor}
 * - Default visitor implementation for processing markup nodes</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.IMarkupWriter}
 * - Interface for writing markup to various output formats</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.MarkupXmlEventWriter}
 * - Writes markup as XML events (StAX event-based API)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.MarkupXmlStreamWriter}
 * - Writes markup as XML stream (StAX stream-based API)</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * This package is primarily used internally by the
 * {@link gov.nist.secauto.metaschema.core.datatype.markup} package to handle
 * the low-level parsing and rendering of markup content. The Flexmark
 * extensions ensure that Metaschema markup conforms to specification
 * requirements while supporting a rich subset of Markdown and HTML features.
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;
