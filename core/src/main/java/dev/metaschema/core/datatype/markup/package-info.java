/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Data type support for HTML and Markdown markup in Metaschema fields.
 * <p>
 * This package provides specialized data types for handling rich text markup,
 * supporting both single-line and multi-line formatted content in Metaschema
 * documents. Markup is internally represented using Flexmark AST nodes and can
 * be serialized to XML, JSON, or Markdown formats.
 * <h2>Key Interfaces and Classes</h2>
 * <ul>
 * <li>{@link IMarkupString} - Common interface for all markup implementations,
 * providing access to the Flexmark document tree and conversion methods</li>
 * <li>{@link MarkupLine} - Represents single-line markup content (no
 * block-level elements)</li>
 * <li>{@link MarkupMultiline} - Represents multi-line markup content with full
 * block structure support (paragraphs, lists, tables, etc.)</li>
 * <li>{@link AbstractMarkupAdapter} - Base adapter for markup data types,
 * handling parsing and serialization</li>
 * <li>{@link MarkupLineAdapter} - Adapter for single-line markup</li>
 * <li>{@link MarkupMultilineAdapter} - Adapter for multi-line markup</li>
 * </ul>
 * <h2>Type Provider</h2>
 * <ul>
 * <li>{@link MarkupDataTypeProvider} - Registers markup types with the data
 * type service</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * Markup types are used in Metaschema definitions where fields need to support
 * formatted text with emphasis, links, code snippets, and other inline or block
 * formatting. The package handles:
 * <ul>
 * <li>Parsing Markdown syntax into a structured AST</li>
 * <li>Rendering markup to HTML for XML serialization</li>
 * <li>Converting markup to plain text or Markdown</li>
 * <li>Validating markup constraints (e.g., no block elements in single-line
 * markup)</li>
 * </ul>
 * <p>
 * The Flexmark library is used for Markdown parsing and rendering, with custom
 * extensions provided in the {@code flexmark} subpackage.
 *
 * @see dev.metaschema.core.datatype.markup.flexmark.FlexmarkFactory
 */

package dev.metaschema.core.datatype.markup;
