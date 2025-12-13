/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides the core framework for defining and executing Metapath functions.
 * <p>
 * This package contains the foundational interfaces and classes for
 * implementing functions that can be called within Metapath expressions. It
 * supports function registration, resolution, and execution based on the XPath
 * 3.1 function model.
 *
 * <h2>Key Interfaces</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.IFunction} -
 * Represents a function signature with its name, arguments, return type, and
 * properties</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.IFunctionLibrary}
 * - Provides access to a collection of function signatures</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.IFunctionExecutor}
 * - Executes a function with provided arguments</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.IArgument} -
 * Represents a single function argument signature</li>
 * </ul>
 *
 * <h2>Key Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.DefaultFunction}
 * - Concrete implementation of a function signature</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.FunctionService}
 * - Service-based function discovery using Java ServiceLoader</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.FunctionLibrary}
 * - Registry for organizing and looking up functions</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * Functions are registered in libraries and resolved by name and arity during
 * Metapath expression evaluation. The {@code FunctionService} loads function
 * libraries using the Java ServiceLoader mechanism, making them available to
 * the Metapath evaluator.
 *
 * @see gov.nist.secauto.metaschema.core.metapath.function.library
 */

package gov.nist.secauto.metaschema.core.metapath.function;
