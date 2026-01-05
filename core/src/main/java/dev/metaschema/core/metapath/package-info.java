/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides the core Metapath expression language implementation.
 * <p>
 * Metapath is an XPath 3.1-based expression language for querying and
 * navigating Metaschema-based data models. This package provides the primary
 * API for compiling and evaluating Metapath expressions against Metaschema
 * content.
 * <p>
 * Key interfaces and classes:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.IMetapathExpression} - Main interface
 * for compiled Metapath expressions with methods for compilation and
 * evaluation</li>
 * <li>{@link dev.metaschema.core.metapath.StaticContext} - XPath 3.1 static
 * context containing namespace bindings, function resolver, and other
 * compile-time information</li>
 * <li>{@link dev.metaschema.core.metapath.DynamicContext} - XPath 3.1 dynamic
 * context containing runtime state such as focus items, variables, and current
 * date/time</li>
 * <li>{@link dev.metaschema.core.metapath.MetapathException} - Base exception
 * type for all Metapath evaluation errors</li>
 * </ul>
 * <p>
 * Typical usage pattern:
 *
 * <pre>{@code
 * // Compile a Metapath expression
 * IMetapathExpression expr = IMetapathExpression.compile("//assembly[@name='foo']");
 *
 * // Evaluate against a document node
 * ISequence<?> results = expr.evaluate(documentNode);
 *
 * // Or evaluate with type conversion
 * Boolean result = expr.evaluateAs(documentNode, ResultType.BOOLEAN);
 * }</pre>
 * <p>
 * This package also contains subpackages for:
 * <ul>
 * <li>{@code antlr} - ANTLR4-based parser infrastructure</li>
 * <li>{@code cst} - Concrete syntax tree expression implementations</li>
 * <li>{@code format} - Path formatting utilities</li>
 * <li>{@code function} - Metapath function library</li>
 * <li>{@code item} - Metapath item types (atomic values, nodes, sequences)</li>
 * <li>{@code type} - Metapath type system</li>
 * </ul>
 *
 * @see <a href="https://www.w3.org/TR/xpath-31/">XPath 3.1 Specification</a>
 */

package dev.metaschema.core.metapath;
