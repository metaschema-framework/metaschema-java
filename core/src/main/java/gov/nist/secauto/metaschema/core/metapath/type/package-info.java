/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides support for Metapath type system and sequence type testing.
 * <p>
 * This package implements the Metapath type system, which is based on the XPath
 * 3.1 type system. It provides mechanisms for type testing, type matching, and
 * type validation of Metapath items and sequences.
 * <p>
 * The core interfaces define type information that can be used to:
 * <ul>
 * <li>Test if an item or sequence matches a specific type</li>
 * <li>Validate sequences against expected types and cardinality</li>
 * <li>Perform type casting and conversion operations</li>
 * <li>Generate type signatures for error messages and debugging</li>
 * </ul>
 *
 * <h2>Key Interfaces</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.IItemType} - The
 * base type interface for all item types, providing factory methods for
 * creating tests for atomic types, node kinds, functions, arrays, and maps</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.ISequenceType} -
 * Represents a sequence type with cardinality constraints (occurrence
 * indicators)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.IKindTest} - A
 * specialized item type for testing node items by their kind (document,
 * assembly, field, flag)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType}
 * - Represents atomic types and union types that can be used for type testing
 * and value conversion</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.IArrayTest} -
 * Specialized item type for testing array items with member type
 * constraints</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.IMapTest} -
 * Specialized item type for testing map items with key and value type
 * constraints</li>
 * </ul>
 *
 * <h2>Supporting Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.Occurrence} - Enum
 * representing sequence cardinality indicators (zero, one, zero-or-one,
 * one-or-more, zero-or-more)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.DataTypeItemType} -
 * Item type implementation for Metaschema data types backed by adapters</li>
 * </ul>
 *
 * <h2>Exceptions</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.TypeMetapathException}
 * - Base exception for type-related errors</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException}
 * - Thrown when a value does not match the expected type</li>
 * </ul>
 *
 * @see gov.nist.secauto.metaschema.core.metapath.type.impl
 */

package gov.nist.secauto.metaschema.core.metapath.type;
