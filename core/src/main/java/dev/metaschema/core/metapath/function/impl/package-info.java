/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Internal implementation classes for Metapath function support.
 * <p>
 * This package contains base classes and utilities used internally by the
 * function library implementations in
 * {@link dev.metaschema.core.metapath.function.library}. These classes provide
 * common functionality for function execution, argument conversion, and type
 * checking based on the XPath 3.1 function calling conventions.
 * <p>
 * Key implementation classes:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.function.impl.AbstractFunction} -
 * Base class for all function implementations, providing argument conversion,
 * type promotion, focus dependency handling, and result caching for
 * deterministic functions</li>
 * <li>{@link dev.metaschema.core.metapath.function.impl.OperationFunctions} -
 * Utility class for registering mathematical and logical operation functions
 * used by Metapath operators</li>
 * </ul>
 * <p>
 * The {@code AbstractFunction} class handles critical aspects of XPath 3.1
 * function semantics including:
 * <ul>
 * <li>Function conversion rules (atomization, type promotion, URI-to-string
 * conversion)</li>
 * <li>Argument sequence occurrence validation (zero-or-one, one-or-more,
 * etc.)</li>
 * <li>Focus item management for context-dependent functions</li>
 * <li>Result caching for deterministic functions to avoid redundant
 * computation</li>
 * <li>Error handling with proper exception context registration</li>
 * </ul>
 * <p>
 * This package is considered an implementation detail and should not be
 * directly referenced by application code. Function implementations should
 * extend {@code AbstractFunction} and be registered in a function library.
 *
 * @see dev.metaschema.core.metapath.function
 * @see dev.metaschema.core.metapath.function.library
 * @see <a href="https://www.w3.org/TR/xpath-31/#id-function-calls">XPath 3.1:
 *      Function Calls</a>
 */

package dev.metaschema.core.metapath.function.impl;
