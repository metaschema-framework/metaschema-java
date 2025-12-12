/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Internal implementation classes for Flexmark-based markup processing.
 * <p>
 * This package contains the low-level implementation components that power
 * Metaschema's markup processing capabilities using the Flexmark Markdown
 * parser library. These classes handle the details of parsing, visiting, and
 * rendering markup content in various formats (HTML, XML, etc.).
 * <h2>Configuration</h2>
 * <ul>
 * <li>{@link FlexmarkConfiguration} - Centralized singleton configuration for
 * Flexmark parser, renderer, and formatter with Metaschema-specific
 * extensions</li>
 * </ul>
 * <h2>Visitors</h2>
 * <ul>
 * <li>{@link IMarkupVisitor} - Interface for visiting and processing markup
 * Abstract Syntax Tree (AST) nodes</li>
 * <li>{@link MarkupVisitor} - Default implementation that dispatches to
 * appropriate handler methods based on node type</li>
 * <li>{@link AstCollectingVisitor} - Visitor that collects AST nodes for
 * further processing</li>
 * </ul>
 * <h2>Writers</h2>
 * <ul>
 * <li>{@link IMarkupWriter} - Interface for writing markup to various output
 * formats with support for elements, text, entities, and special
 * constructs</li>
 * <li>{@link AbstractMarkupWriter} - Base implementation providing common
 * functionality for markup writers</li>
 * <li>{@link MarkupXmlStreamWriter} - Writes markup as XML using StAX
 * {@link javax.xml.stream.XMLStreamWriter}</li>
 * <li>{@link MarkupXmlEventWriter} - Writes markup as XML using StAX
 * {@link javax.xml.stream.XMLEventWriter}</li>
 * </ul>
 * <h2>Custom Extensions</h2>
 * <ul>
 * <li>{@link HtmlCodeRenderExtension} - Custom rendering for inline
 * {@code <code>} elements to properly handle special characters</li>
 * <li>{@link SuppressPTagExtension} - Suppresses paragraph tags in single-line
 * markup content</li>
 * <li>{@link FixedEmphasisDelimiterProcessor} - Fixed implementation of
 * emphasis delimiter processing to handle edge cases in Markdown emphasis
 * parsing</li>
 * </ul>
 * <h2>Design Patterns</h2>
 * <p>
 * The package follows several design patterns:
 * <ul>
 * <li><b>Visitor Pattern</b> - {@link IMarkupVisitor} and implementations
 * traverse the Flexmark AST</li>
 * <li><b>Strategy Pattern</b> - {@link IMarkupWriter} allows different output
 * strategies (stream vs. event-based XML)</li>
 * <li><b>Template Method</b> - {@link AbstractMarkupWriter} provides common
 * structure with extension points</li>
 * <li><b>Singleton</b> - {@link FlexmarkConfiguration} manages shared parser
 * configuration</li>
 * </ul>
 * <h2>Thread Safety</h2>
 * <p>
 * The {@link FlexmarkConfiguration} is thread-safe and immutable after
 * initialization. Writer and visitor implementations are typically not
 * thread-safe and should be used within a single thread or processing context.
 * <h2>Usage Context</h2>
 * <p>
 * This package is internal implementation detail used by:
 * <ul>
 * <li>Parent {@link gov.nist.secauto.metaschema.core.datatype.markup.flexmark}
 * package for public markup processing APIs</li>
 * <li>{@link gov.nist.secauto.metaschema.core.datatype.markup} for higher-level
 * markup data type support</li>
 * <li>XML and JSON serialization components that need to render markup
 * content</li>
 * </ul>
 *
 * @see gov.nist.secauto.metaschema.core.datatype.markup.flexmark
 * @see gov.nist.secauto.metaschema.core.datatype.markup
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl;
