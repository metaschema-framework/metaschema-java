/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides support for Metapath function items, including arrays and maps.
 * <p>
 * This package implements the XPath 3.1 function item types, including arrays
 * and maps. These are first-class values in Metapath that can be passed as
 * arguments, returned from functions, and stored in sequences.
 *
 * <h2>Array Items</h2>
 * <p>
 * Arrays are ordered collections of values, where each value is a sequence.
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.function.IArrayItem} - The main
 * interface for array items, implementing {@link java.util.List}</li>
 * </ul>
 * <p>
 * Arrays are immutable once created and support operations like getting members
 * by position (1-indexed), determining size, and conversion to sequences.
 * Factory methods are provided for creating arrays with various numbers of
 * members.
 *
 * <h2>Map Items</h2>
 * <p>
 * Maps are unordered collections of key-value pairs, where keys must be atomic
 * items and values are sequences.
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.function.IMapItem} - The main
 * interface for map items, implementing {@link java.util.Map}</li>
 * <li>{@link dev.metaschema.core.metapath.item.function.IMapKey} - The key type
 * used in maps, wrapping atomic items with proper equality semantics</li>
 * <li>{@link dev.metaschema.core.metapath.item.function.IKeySpecifier} -
 * Interface for computing map keys from items</li>
 * </ul>
 * <p>
 * Maps are immutable once created and support operations like getting values by
 * key, determining size, and merging maps. Factory methods are provided for
 * creating maps with various numbers of entries.
 *
 * <h2>Map Key Types</h2>
 * <p>
 * Different atomic types use different equality semantics for map keys:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.function.IStringMapKey} - Keys
 * for string-based types (string, anyURI)</li>
 * <li>{@link dev.metaschema.core.metapath.item.function.IDecimalMapKey} - Keys
 * for numeric types (decimal, integer, float, double)</li>
 * <li>{@link dev.metaschema.core.metapath.item.function.ITemporalMapKey} - Keys
 * for date/time types (date, dateTime, time)</li>
 * <li>{@link dev.metaschema.core.metapath.item.function.ICalendarMapKey} - Keys
 * for calendar-based types</li>
 * <li>{@link dev.metaschema.core.metapath.item.function.IOpaqueMapKey} - Keys
 * for types with identity-based equality (boolean, QName, etc.)</li>
 * </ul>
 *
 * <h2>Exceptions</h2>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.function.IndexOutOfBoundsArrayMetapathException}
 * - Thrown when accessing an array with an out-of-bounds index</li>
 * <li>{@link dev.metaschema.core.metapath.item.function.NegativeLengthArrayMetapathException}
 * - Thrown when attempting to create an array with a negative length</li>
 * </ul>
 *
 * @see dev.metaschema.core.metapath.item.function.IArrayItem
 * @see dev.metaschema.core.metapath.item.function.IMapItem
 */

package dev.metaschema.core.metapath.item.function;
