/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete syntax tree (CST) node implementations for Metapath logical
 * and comparison expressions.
 * <p>
 * This package implements logical operations, conditional expressions, and
 * comparison operators as defined by
 * <a href="https://www.w3.org/TR/xpath-31/#id-logical-expressions">XPath 3.1
 * logical expressions</a>,
 * <a href="https://www.w3.org/TR/xpath-31/#id-comparisons">XPath 3.1
 * comparisons</a>, and
 * <a href="https://www.w3.org/TR/xpath-31/#id-conditionals">XPath 3.1
 * conditional expressions</a>.
 *
 * <h2>Key Classes and Interfaces</h2>
 *
 * <h3>Logical Expressions</h3>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.IBooleanLogicExpression} -
 * Common interface for expressions that produce boolean results</li>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.And} - Logical "and"
 * expression with short-circuit evaluation</li>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.Or} - Logical "or"
 * expression with short-circuit evaluation</li>
 * </ul>
 *
 * <h3>Conditional Expressions</h3>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.If} - If-then-else
 * conditional expression</li>
 * </ul>
 *
 * <h3>Comparison Expressions</h3>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.AbstractComparison} - Base
 * class for all comparison operations</li>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.GeneralComparison} -
 * General comparisons ({@code =}, {@code !=}, {@code <}, {@code <=}, {@code >},
 * {@code >=})</li>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.ValueComparison} - Value
 * comparisons ({@code eq}, {@code ne}, {@code lt}, {@code le}, {@code gt},
 * {@code ge})</li>
 * </ul>
 *
 * <h3>Predicate Expressions</h3>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.cst.logic.PredicateExpression} -
 * Predicate filter expression ({@code [predicate]})</li>
 * </ul>
 *
 * <h2>Logical Expression Behavior</h2>
 *
 * <h3>And Expression</h3>
 * <p>
 * The {@code and} operator performs short-circuit evaluation, returning
 * {@code false} as soon as the first operand evaluates to {@code false}.
 *
 * <h3>Or Expression</h3>
 * <p>
 * The {@code or} operator performs short-circuit evaluation, returning
 * {@code true} as soon as the first operand evaluates to {@code true}.
 *
 * <h2>Comparison Types</h2>
 *
 * <h3>General Comparisons</h3>
 * <p>
 * General comparisons ({@code =}, {@code !=}, etc.) compare sequences by
 * checking if any pair of items from the left and right sequences satisfies the
 * comparison. They perform existential quantification.
 *
 * <h3>Value Comparisons</h3>
 * <p>
 * Value comparisons ({@code eq}, {@code ne}, etc.) compare single atomic
 * values. If either operand is an empty sequence or contains more than one
 * item, the comparison raises an error.
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>
 * // Logical: $a and $b, $x or $y
 * // Conditional: if ($count &gt; 10) then "many" else "few"
 * // General comparison: $items = 'value'
 * // Value comparison: $price gt 100
 * // Predicate: $items[position() &lt; 5]
 * </pre>
 *
 * @see dev.metaschema.core.metapath.item.atomic.IBooleanItem
 * @see dev.metaschema.core.metapath.function.ComparisonFunctions
 * @see dev.metaschema.core.metapath.cst
 */

package dev.metaschema.core.metapath.cst.logic;
