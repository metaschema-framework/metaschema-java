/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Internal implementation classes supporting the Metapath expression engine.
 * <p>
 * This package contains concrete implementations of core Metapath abstractions
 * including expression compilation, evaluation, sequence management, and error
 * handling. These classes are primarily used internally by the Metapath
 * evaluator and are subject to change between releases.
 * <p>
 * Key implementation classes:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.impl.MetapathExpression} - Main
 * implementation of {@link dev.metaschema.core.metapath.IMetapathExpression},
 * providing expression compilation from Metapath strings using ANTLR4 parsing
 * and CST generation</li>
 * <li>{@link dev.metaschema.core.metapath.impl.LazyCompilationMetapathExpression}
 * - Defers expression compilation until first evaluation for performance
 * optimization</li>
 * <li>{@link dev.metaschema.core.metapath.impl.AbstractSequence} - Base class
 * for immutable sequence implementations backed by unmodifiable lists</li>
 * <li>{@link dev.metaschema.core.metapath.impl.SingletonSequence} - Optimized
 * sequence containing exactly one item</li>
 * <li>{@link dev.metaschema.core.metapath.impl.SequenceN} - General-purpose
 * sequence containing zero or more items</li>
 * <li>{@link dev.metaschema.core.metapath.impl.StreamSequence} - Lazy sequence
 * backed by a stream for efficient memory usage with large result sets</li>
 * <li>{@link dev.metaschema.core.metapath.impl.ErrorCodeImpl} - Implementation
 * of {@link dev.metaschema.core.metapath.IErrorCode} for Metapath error
 * reporting</li>
 * <li>{@link dev.metaschema.core.metapath.impl.AbstractMapKey} - Base class for
 * map key implementations used in Metapath map operations</li>
 * <li>{@link dev.metaschema.core.metapath.impl.IFeatureCollectionFunctionItem}
 * - Support for function items in map/array collections</li>
 * </ul>
 * <p>
 * This package is considered an implementation detail and should not be
 * directly referenced by application code. Use the public API in
 * {@link dev.metaschema.core.metapath} instead.
 *
 * @see dev.metaschema.core.metapath
 * @see dev.metaschema.core.metapath.IMetapathExpression
 * @see dev.metaschema.core.metapath.item.ISequence
 */

package dev.metaschema.core.metapath.impl;
