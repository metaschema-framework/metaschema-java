/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Built-in Metaschema data type adapters and implementations.
 * <p>
 * This package provides concrete implementations of
 * {@link dev.metaschema.core.datatype.IDataTypeAdapter} for all standard
 * Metaschema data types, including primitives, temporal types, URIs, and binary
 * data.
 * <h2>Abstract Base Classes</h2>
 * <p>
 * The package includes specialized abstract adapters for common type families:
 * <ul>
 * <li>{@link AbstractStringAdapter} - Base for string-based types with
 * whitespace handling (e.g., {@link StringAdapter}, {@link TokenAdapter},
 * {@link NcNameAdapter})</li>
 * <li>{@link AbstractIntegerAdapter} - Base for arbitrary-precision integer
 * types (e.g., {@link IntegerAdapter}, {@link NonNegativeIntegerAdapter})</li>
 * <li>{@link AbstractBinaryAdapter} - Base for binary data types (e.g.,
 * {@link Base64Adapter}, {@link HexBinaryAdapter})</li>
 * <li>{@link AbstractDurationAdapter} - Base for duration types (e.g.,
 * {@link DayTimeAdapter}, {@link YearMonthAdapter})</li>
 * </ul>
 * <h2>Standard Type Adapters</h2>
 * <p>
 * Concrete adapters include:
 * <ul>
 * <li>Primitives: {@link BooleanAdapter}, {@link DecimalAdapter}</li>
 * <li>Strings: {@link StringAdapter}, {@link TokenAdapter},
 * {@link NcNameAdapter}, {@link QNameAdapter}</li>
 * <li>Temporal: {@link DateAdapter}, {@link DateTimeAdapter},
 * {@link TimeAdapter} (with timezone variants)</li>
 * <li>Network: {@link UriAdapter}, {@link UriReferenceAdapter},
 * {@link EmailAddressAdapter}, {@link HostnameAdapter},
 * {@link IPv4AddressAdapter}, {@link IPv6AddressAdapter}</li>
 * <li>Binary: {@link Base64Adapter}, {@link HexBinaryAdapter}</li>
 * <li>Identifiers: {@link UuidAdapter}</li>
 * </ul>
 * <h2>Type Provider</h2>
 * <ul>
 * <li>{@link MetaschemaDataTypeProvider} - Registers all built-in Metaschema
 * types with the {@link dev.metaschema.core.datatype.DataTypeService}</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * These adapters handle conversion between Java objects and their serialized
 * representations in XML, JSON, and YAML formats. Each adapter defines:
 * <ul>
 * <li>Parsing logic to convert string/stream data to Java objects</li>
 * <li>Serialization methods for XML, JSON, and YAML output</li>
 * <li>Validation rules and constraints for the data type</li>
 * <li>Metapath atomic item creation for use in expressions</li>
 * </ul>
 *
 * @see dev.metaschema.core.datatype.IDataTypeAdapter
 */

package dev.metaschema.core.datatype.adapter;
