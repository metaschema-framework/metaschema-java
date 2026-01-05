/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Atomic item types representing indivisible values in the Metapath type
 * system.
 * <p>
 * This package provides interfaces and implementations for atomic items, which
 * are fundamental data values that cannot be decomposed into smaller units.
 * Atomic items correspond to simple types in XPath 3.1 and include primitive
 * types, numeric types, temporal types, and Metaschema-specific types.
 * <p>
 * Core atomic type interfaces include:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem} - Base
 * interface for all atomic items</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IStringItem} - Text
 * values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.INumericItem} - Numeric
 * value base interface</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IIntegerItem} - Integer
 * values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IDecimalItem} - Decimal
 * values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IBooleanItem} - Boolean
 * values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IDateTimeItem} - Date and
 * time values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IUriReferenceItem} - URI
 * references</li>
 * </ul>
 * <p>
 * Metaschema-specific atomic types include:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IMarkupItem} - Formatted
 * text with inline markup</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IEmailAddressItem} -
 * Email addresses</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IHostnameItem} -
 * Hostnames</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IIPAddressItem} - IP
 * addresses (v4 and v6)</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.IUuidItem} - UUIDs</li>
 * </ul>
 * <p>
 * Atomic items support type conversions, comparisons, and operations as defined
 * by the XPath 3.1 Functions and Operators specification. Each atomic type is
 * backed by a corresponding
 * {@link dev.metaschema.core.datatype.IDataTypeAdapter} that handles value
 * parsing, validation, and serialization.
 *
 * @see <a href="https://www.w3.org/TR/xpath-datamodel-31/#atomic-values">XPath
 *      3.1 Data Model: Atomic Values</a>
 * @see <a href="https://www.w3.org/TR/xpath-functions-31/">XPath 3.1 Functions
 *      and Operators</a>
 */

package dev.metaschema.core.metapath.item.atomic;
