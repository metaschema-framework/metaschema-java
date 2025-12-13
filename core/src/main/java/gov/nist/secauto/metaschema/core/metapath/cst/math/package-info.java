/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete syntax tree (CST) node implementations for Metapath
 * arithmetic expressions.
 * <p>
 * This package implements arithmetic operations as defined by the
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">XPath 3.1 arithmetic
 * expressions</a> specification. These operations support numeric calculations,
 * date/time arithmetic, and duration operations.
 *
 * <h2>Key Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.AbstractArithmeticExpression}
 * - Base class for all arithmetic operations</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.AbstractBasicArithmeticExpression}
 * - Base class for basic binary arithmetic operations</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.Addition} -
 * Addition operator (+)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.Subtraction} -
 * Subtraction operator (-)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.Multiplication}
 * - Multiplication operator (*)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.Division} -
 * Division operator (div)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.IntegerDivision}
 * - Integer division operator (idiv)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.Modulo} -
 * Modulo operator (mod)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.math.Negate} - Unary
 * negation operator (-)</li>
 * </ul>
 *
 * <h2>Supported Operations</h2>
 * <p>
 * Arithmetic expressions in this package support operations on:
 * <ul>
 * <li>Numeric types (integer, decimal, float, double)</li>
 * <li>Date and time types (date, dateTime, time)</li>
 * <li>Duration types (yearMonthDuration, dayTimeDuration)</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>
 * // Numeric: 1 + 2 evaluates to 3
 * // Date arithmetic: xs:date('2023-01-01') + xs:yearMonthDuration('P1M')
 * // Evaluates to xs:date('2023-02-01')
 * </pre>
 *
 * @see gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem
 * @see gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem
 * @see gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;
