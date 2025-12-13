/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides regular expression support for implementing Metapath functions.
 * <p>
 * This package contains utilities for processing regular expressions in
 * accordance with the XPath 3.1 specification. It handles the translation of
 * XPath-style regex flags to Java Pattern flags and provides error handling for
 * invalid regular expressions.
 *
 * <h2>Key Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.regex.RegexUtil}
 * - Utility methods for parsing XPath regex flags and creating Java Pattern
 * objects</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.function.regex.RegularExpressionMetapathException}
 * - Exception thrown when regular expression processing fails</li>
 * </ul>
 *
 * <h2>XPath Regex Flags</h2>
 * <p>
 * The package supports XPath 3.1 regex flags as defined in the specification:
 * <ul>
 * <li>{@code s} - Dot-all mode (. matches newlines)</li>
 * <li>{@code m} - Multiline mode (^ and $ match line boundaries)</li>
 * <li>{@code i} - Case-insensitive matching</li>
 * <li>{@code x} - Comments mode (whitespace and comments ignored)</li>
 * <li>{@code q} - Literal mode (metacharacters treated as ordinary
 * characters)</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * This package is used internally by Metapath functions that perform pattern
 * matching, such as {@code fn:matches}, {@code fn:replace}, and
 * {@code fn:tokenize}.
 *
 * @see <a href="https://www.w3.org/TR/xpath-functions-31/#regex-syntax">XPath
 *      3.1 Regular Expression Syntax</a>
 * @see gov.nist.secauto.metaschema.core.metapath.function.library
 */

package gov.nist.secauto.metaschema.core.metapath.function.regex;
