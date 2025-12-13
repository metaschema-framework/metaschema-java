/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete syntax tree (CST) node implementations for Metapath type
 * operations.
 * <p>
 * This package implements type-related expressions as defined by XPath 3.1,
 * including type testing, type casting, and type assertion operations. These
 * expressions enable runtime type introspection and conversion of Metapath
 * values.
 *
 * <h2>Key Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.type.AbstractCastingExpression}
 * - Base class for casting-related expressions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.type.Cast} -
 * Implements the <a href="https://www.w3.org/TR/xpath-31/#id-cast">"cast as"
 * operator</a> for explicit type conversion</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.type.Castable} -
 * Implements the
 * <a href="https://www.w3.org/TR/xpath-31/#id-castable">"castable as"
 * operator</a> for testing type conversion feasibility</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.type.InstanceOf} -
 * Implements the
 * <a href="https://www.w3.org/TR/xpath-31/#id-instance-of">"instance of"
 * operator</a> for runtime type testing</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.type.Treat} -
 * Implements the <a href="https://www.w3.org/TR/xpath-31/#id-treat">"treat as"
 * operator</a> for type assertion with runtime verification</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.type.TypeTestSupport}
 * - Utility class for type testing operations</li>
 * </ul>
 *
 * <h2>Type Operations</h2>
 *
 * <h3>Cast Expression ({@code cast as})</h3>
 * <p>
 * Converts a value to a specified atomic type, raising an error if conversion
 * fails:
 *
 * <pre>
 * "123" cast as xs:integer  → 123
 * "abc" cast as xs:integer  → error
 * </pre>
 *
 * <h3>Castable Expression ({@code castable as})</h3>
 * <p>
 * Tests whether a value can be successfully cast to a specified type:
 *
 * <pre>
 * "123" castable as xs:integer  → true
 * "abc" castable as xs:integer  → false
 * </pre>
 *
 * <h3>Instance Of Expression ({@code instance of})</h3>
 * <p>
 * Tests whether a value matches a specified sequence type:
 *
 * <pre>
 * 5 instance of xs:integer          → true
 * (1, 2, 3) instance of xs:integer+ → true
 * "hello" instance of xs:integer    → false
 * </pre>
 *
 * <h3>Treat Expression ({@code treat as})</h3>
 * <p>
 * Asserts that a value has a specified type, raising an error if it doesn't:
 *
 * <pre>
 * $value treat as xs:integer  → returns $value if it's an integer, error otherwise
 * </pre>
 *
 * @see gov.nist.secauto.metaschema.core.metapath.type
 * @see gov.nist.secauto.metaschema.core.metapath.item.atomic
 * @see gov.nist.secauto.metaschema.core.metapath.cst
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;
