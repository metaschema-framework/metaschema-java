/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides ANTLR4-based parsing infrastructure for the Metapath expression
 * language.
 * <p>
 * This package contains classes that support parsing Metapath expression
 * strings into abstract syntax trees (ASTs) using the ANTLR4 parser generator
 * framework. The ANTLR4 grammar definition is located in
 * {@code core/src/main/antlr4} and generates lexer and parser classes during
 * the Maven build process.
 * <p>
 * Key classes:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.antlr.AbstractAstVisitor} - Base
 * visitor class for traversing and transforming ANTLR parse trees into
 * executable Metapath expression objects using the visitor pattern</li>
 * <li>{@link dev.metaschema.core.metapath.antlr.FailingErrorListener} - ANTLR
 * error listener that converts syntax errors into
 * {@link org.antlr.v4.runtime.misc.ParseCancellationException} instances with
 * detailed position information</li>
 * <li>{@link dev.metaschema.core.metapath.antlr.ParseTreePrinter} - Utility for
 * generating textual representations of ANTLR parse trees for debugging
 * purposes</li>
 * <li>{@link dev.metaschema.core.metapath.antlr.Metapath10ParserBase} - Base
 * parser class providing custom parsing logic and helper methods</li>
 * </ul>
 * <p>
 * Generated ANTLR4 classes (created during build):
 * <ul>
 * <li>{@code Metapath10Lexer} - Tokenizes Metapath expression strings</li>
 * <li>{@code Metapath10Parser} - Parses token streams into concrete syntax
 * trees</li>
 * <li>{@code Metapath10BaseVisitor} - Base visitor interface for traversing
 * parse trees</li>
 * </ul>
 * <p>
 * This package is primarily used internally by the Metapath compiler and should
 * not be directly invoked by application code. Use
 * {@link dev.metaschema.core.metapath.IMetapathExpression} for compiling and
 * evaluating Metapath expressions.
 *
 * @see dev.metaschema.core.metapath.IMetapathExpression#compile(String)
 */

package dev.metaschema.core.metapath.antlr;
