/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete syntax tree (CST) node implementations for Metapath item
 * expressions and sequence operations.
 * <p>
 * This package implements various expression types as defined by
 * <a href="https://www.w3.org/TR/xpath-31/">XPath 3.1</a> that operate on items
 * and sequences, including literals, sequence constructors, set operations, and
 * quantified expressions.
 *
 * <h2>Key Classes and Interfaces</h2>
 *
 * <h3>Literal Expressions</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.ILiteralExpression}
 * - Common interface for all literal value expressions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.AbstractLiteralExpression}
 * - Base class for literal implementations</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.IntegerLiteral}
 * - Integer literal values</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.DecimalLiteral}
 * - Decimal literal values</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.StringLiteral}
 * - String literal values</li>
 * </ul>
 *
 * <h3>Sequence Operations</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.EmptySequence}
 * - Empty parenthesized expression {@code ()}</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.SequenceExpression}
 * - Comma-separated sequence of expressions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.Range} - Range
 * expression ({@code to} operator)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.StringConcat}
 * - String concatenation operator ({@code ||})</li>
 * </ul>
 *
 * <h3>Set Operations</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.Union} - Union
 * of sequences ({@code union}, {@code |})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.Intersect} -
 * Intersection of sequences ({@code intersect})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.Except} -
 * Difference of sequences ({@code except})</li>
 * </ul>
 *
 * <h3>Map and Filter Operations</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.SimpleMap} -
 * Simple map operator ({@code !})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.AbstractFilterExpression}
 * - Base class for filter expressions</li>
 * </ul>
 *
 * <h3>Lookup Operations</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.AbstractLookup}
 * - Base class for lookup operations</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.PostfixLookup}
 * - Postfix lookup expression</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.UnaryLookup} -
 * Unary lookup expression ({@code ?})</li>
 * </ul>
 *
 * <h3>Array and Map Constructors</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySquareConstructor}
 * - Square array constructor ({@code [ ]})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySequenceConstructor}
 * - Curly array constructor ({@code array { }})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.MapConstructor}
 * - Map constructor ({@code map { }})</li>
 * </ul>
 *
 * <h3>Quantified Expressions</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.items.Quantified} -
 * Quantified expressions ({@code some} and {@code every})</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>
 * // Literals: 42, 3.14, "hello"
 * // Empty sequence: ()
 * // Sequence: (1, 2, 3)
 * // Range: 1 to 10
 * // Union: $seq1 | $seq2
 * // Simple map: $items ! string(.)
 * // Quantified: some $x in $seq satisfies $x &gt; 10
 * </pre>
 *
 * @see gov.nist.secauto.metaschema.core.metapath.item.IItem
 * @see gov.nist.secauto.metaschema.core.metapath.item.ISequence
 * @see gov.nist.secauto.metaschema.core.metapath.cst
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;
