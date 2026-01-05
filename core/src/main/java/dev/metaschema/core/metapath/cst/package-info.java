/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete syntax tree (CST) node implementations for Metapath
 * expressions.
 * <p>
 * Metapath is an XPath 3.1-based query language for navigating and querying
 * Metaschema-based data. This package contains the expression implementations
 * that form the executable representation of parsed Metapath queries.
 *
 * <h2>Package Structure</h2>
 * <p>
 * This package is organized into several subpackages:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.cst.math} - Arithmetic expressions
 * (addition, subtraction, multiplication, division, etc.)</li>
 * <li>{@link dev.metaschema.core.metapath.cst.path} - Path navigation
 * expressions (axes, steps, node tests)</li>
 * <li>{@link dev.metaschema.core.metapath.cst.type} - Type operations (cast,
 * castable, instance of, treat)</li>
 * <li>{@link dev.metaschema.core.metapath.cst.logic} - Boolean logic and
 * comparison expressions</li>
 * <li>{@link dev.metaschema.core.metapath.cst.items} - Literal values,
 * sequences, and collection operations</li>
 * </ul>
 *
 * <h2>Key Classes and Interfaces</h2>
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.cst.AbstractExpression} - Base class
 * for all CST expression nodes</li>
 * <li>{@link dev.metaschema.core.metapath.cst.BuildCSTVisitor} - Transforms
 * ANTLRv4 abstract syntax tree (AST) into executable CST nodes</li>
 * <li>{@link dev.metaschema.core.metapath.cst.IExpressionVisitor} - Visitor
 * pattern interface for processing CST nodes</li>
 * <li>{@link dev.metaschema.core.metapath.cst.CSTPrinter} - Debugging utility
 * for visualizing CST structure</li>
 * <li>{@link dev.metaschema.core.metapath.cst.For} - For loop expressions</li>
 * <li>{@link dev.metaschema.core.metapath.cst.Let} - Variable binding
 * expressions</li>
 * <li>{@link dev.metaschema.core.metapath.cst.StaticFunctionCall} - Static
 * function invocation</li>
 * </ul>
 *
 * <h2>Usage Context</h2>
 * <p>
 * CST nodes are created by
 * {@link dev.metaschema.core.metapath.cst.BuildCSTVisitor} during Metapath
 * expression compilation. Each CST node implements
 * {@link dev.metaschema.core.metapath.IExpression} and can be evaluated against
 * a dynamic context to produce results.
 *
 * @see dev.metaschema.core.metapath.IExpression
 * @see dev.metaschema.core.metapath.DynamicContext
 */

package dev.metaschema.core.metapath.cst;
