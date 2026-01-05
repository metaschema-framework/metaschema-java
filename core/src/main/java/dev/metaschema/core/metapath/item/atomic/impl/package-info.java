/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Concrete implementations of atomic item types for the Metapath type system.
 * <p>
 * This package provides the internal implementation classes for all atomic item
 * types defined in {@link dev.metaschema.core.metapath.item.atomic}. Each
 * implementation class wraps a native Java value and provides Metapath
 * operations, comparisons, and type conversions according to XPath 3.1
 * semantics.
 *
 * <h2>Base Implementation Classes</h2>
 * <p>
 * The package includes abstract base classes that provide common functionality
 * for related atomic types:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractStringItem}
 * - Base for string-based types providing whitespace normalization and string
 * operations</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractIntegerItem}
 * - Base for integer types providing numeric operations and conversions</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractDecimalItem}
 * - Base for decimal numeric types</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractTemporalItem}
 * - Base for date/time types</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractDateItem} -
 * Base for date values with optional timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractTimeItem} -
 * Base for time values with optional timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractDateTimeItem}
 * - Base for combined date/time values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractDurationItem}
 * - Base for duration types</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractBinaryItem}
 * - Base for binary data types (base64 and hex)</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractUriItem} -
 * Base for URI-related types</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractMarkupItem}
 * - Base for markup content types</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AbstractIPAddressItem}
 * - Base for IP address types (v4 and v6)</li>
 * </ul>
 *
 * <h2>String and Text Types</h2>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.StringItemImpl} -
 * Standard string values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.TokenItemImpl} -
 * Normalized whitespace strings</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.NcNameItemImpl} -
 * XML NCNames (non-colonized names)</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.MarkupLineItemImpl}
 * - Single-line formatted text</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.MarkupMultiLineItemImpl}
 * - Multi-line formatted text</li>
 * </ul>
 *
 * <h2>Numeric Types</h2>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.IntegerItemImpl} -
 * Arbitrary-precision integer values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.NonNegativeIntegerItemImpl}
 * - Non-negative integers (zero or positive)</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.PositiveIntegerItemImpl}
 * - Strictly positive integers</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.DecimalItemImpl} -
 * Decimal numeric values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.BooleanItemImpl} -
 * Boolean true/false values</li>
 * </ul>
 *
 * <h2>Temporal Types</h2>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.DateTimeWithTimeZoneItemImpl}
 * - Date/time values with explicit timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.DateTimeWithoutTimeZoneItemImpl}
 * - Date/time values without timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.DateWithTimeZoneItemImpl}
 * - Date values with timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.DateWithoutTimeZoneItemImpl}
 * - Date values without timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.TimeWithTimeZoneItemImpl}
 * - Time values with timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.TimeWithoutTimeZoneItemImpl}
 * - Time values without timezone</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.DayTimeDurationItemImpl}
 * - Durations measured in days/hours/minutes/seconds</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.YearMonthDurationItemImpl}
 * - Durations measured in years/months</li>
 * </ul>
 *
 * <h2>URI and Network Types</h2>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.AnyUriItemImpl} -
 * Arbitrary URI values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.UriReferenceItemImpl}
 * - URI references (absolute or relative)</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.EmailAddressItemImpl}
 * - Email address values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.HostnameItemImpl} -
 * DNS hostname values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.IPv4AddressItemImpl}
 * - IPv4 address values</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.IPv6AddressItemImpl}
 * - IPv6 address values</li>
 * </ul>
 *
 * <h2>Binary and Other Types</h2>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.Base64BinaryItemImpl}
 * - Base64-encoded binary data</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.HexBinaryItem} -
 * Hex-encoded binary data</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.QNameItemImpl} -
 * Qualified names with namespace</li>
 * <li>{@link dev.metaschema.core.metapath.item.atomic.impl.UuidItemImpl} -
 * UUID/GUID values</li>
 * </ul>
 * <p>
 * Each implementation class is paired with a corresponding
 * {@link dev.metaschema.core.datatype.IDataTypeAdapter} that handles parsing
 * from string representations, validation, and serialization for XML/JSON
 * output.
 * <p>
 * This package is considered an implementation detail. Application code should
 * use the public interfaces in {@link dev.metaschema.core.metapath.item.atomic}
 * and factory methods like {@code IStringItem.valueOf()} rather than directly
 * instantiating these implementation classes.
 *
 * @see dev.metaschema.core.metapath.item.atomic
 * @see dev.metaschema.core.datatype
 * @see <a href="https://www.w3.org/TR/xpath-datamodel-31/#atomic-values">XPath
 *      3.1 Data Model: Atomic Values</a>
 */

package dev.metaschema.core.metapath.item.atomic.impl;
