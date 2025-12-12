/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete implementations of the Metapath type system interfaces.
 * <p>
 * This package contains the implementation classes for the Metapath type system
 * defined in {@link gov.nist.secauto.metaschema.core.metapath.type}. These
 * classes provide the runtime type checking, validation, and testing mechanisms
 * used throughout the Metapath implementation.
 *
 * <h2>Item Type Implementations</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.AnyItemType} -
 * A type that matches any item (item())</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.AnyFunctionItemType}
 * - A type that matches any function item</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.AbstractItemType}
 * - Base class for custom item type implementations</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.NonAdapterAtomicItemType}
 * - Item type for atomic types that are not backed by data type adapters</li>
 * </ul>
 *
 * <h2>Node Kind Tests</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.AnyKindTest} -
 * Singleton tests for matching any node, document, assembly, field, or
 * flag</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.AbstractDefinitionTest}
 * - Base class for tests that match nodes by name and/or definition type</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.KindDocumentTestImpl}
 * - Tests for matching document nodes with optional root element type
 * constraints</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.KindAssemblyTestImpl}
 * - Tests for matching assembly nodes by name and/or type</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.KindFieldTestImpl}
 * - Tests for matching field nodes by name and/or type</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.KindFlagTestImpl}
 * - Tests for matching flag nodes by name and/or type</li>
 * </ul>
 *
 * <h2>Collection Type Tests</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.ArrayTestImpl}
 * - Tests for matching array items with optional member type constraints</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.MapTestImpl} -
 * Tests for matching map items with key and value type constraints</li>
 * </ul>
 *
 * <h2>Sequence Types</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.SequenceTypeImpl}
 * - Implementation of sequence type testing with occurrence validation</li>
 * </ul>
 *
 * <h2>Type Constants</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.TypeConstants}
 * - Provides singleton instances for abstract atomic types (any-atomic-type,
 * duration, ip-address, numeric)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.type.impl.DynamicTypeSupport}
 * - Utility methods for dynamic type operations</li>
 * </ul>
 *
 * @see gov.nist.secauto.metaschema.core.metapath.type
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;
