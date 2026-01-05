/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Built-in Metapath function implementations based on the XPath 3.1
 * specification.
 * <p>
 * This package provides concrete implementations of standard XPath 3.1
 * functions adapted to work with the Metaschema document model. Functions that
 * operate on an XML document model (XDM) in XPath have been adapted to work
 * with the Metaschema module-based model instead.
 *
 * <h2>Function Categories</h2>
 * <ul>
 * <li><b>String Functions:</b> {@code fn:string}, {@code fn:concat},
 * {@code fn:contains}, {@code fn:starts-with}, {@code fn:ends-with},
 * {@code fn:substring}, {@code fn:normalize-space}, {@code fn:upper-case},
 * {@code fn:lower-case}, {@code fn:tokenize}</li>
 * <li><b>Numeric Functions:</b> {@code fn:abs}, {@code fn:ceiling},
 * {@code fn:floor}, {@code fn:round}</li>
 * <li><b>Sequence Functions:</b> {@code fn:count}, {@code fn:empty},
 * {@code fn:exists}, {@code fn:head}, {@code fn:tail}, {@code fn:reverse},
 * {@code fn:distinct-values}, {@code fn:index-of}, {@code fn:insert-before},
 * {@code fn:remove}</li>
 * <li><b>Date/Time Functions:</b> {@code fn:current-date},
 * {@code fn:current-time}, {@code fn:current-dateTime},
 * {@code fn:year-from-date}, {@code fn:month-from-date},
 * {@code fn:day-from-date}, and related component extraction functions</li>
 * <li><b>Boolean Functions:</b> {@code fn:boolean}, {@code fn:true},
 * {@code fn:false}, {@code fn:not}</li>
 * <li><b>Array Functions:</b> {@code array:size}, {@code array:get},
 * {@code array:head}, {@code array:tail}, {@code array:reverse},
 * {@code array:flatten}, {@code array:join}</li>
 * <li><b>Map Functions:</b> {@code map:contains}, {@code map:get},
 * {@code map:keys}, {@code map:put}, {@code map:remove}, {@code map:merge}</li>
 * <li><b>Document Functions:</b> {@code fn:doc}, {@code fn:doc-available},
 * {@code fn:base-uri}, {@code fn:document-uri}</li>
 * </ul>
 *
 * <h2>Metaschema-Specific Extensions</h2>
 * <p>
 * This package also includes Metapath-specific functions not present in XPath
 * 3.1:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.function.library.MpRecurseDepth} -
 * Recursively evaluates a Metapath expression to a specified depth</li>
 * <li>{@link dev.metaschema.core.metapath.function.library.MpBase64Encode} -
 * Encodes strings to Base64</li>
 * <li>{@link dev.metaschema.core.metapath.function.library.MpBase64Decode} -
 * Decodes Base64 strings</li>
 * </ul>
 *
 * <h2>Function Registration</h2>
 * <p>
 * All built-in functions are registered in
 * {@link dev.metaschema.core.metapath.function.library.DefaultFunctionLibrary},
 * which is loaded via the Java ServiceLoader mechanism.
 *
 * @see <a href="https://www.w3.org/TR/xpath-functions-31/">XPath and XQuery
 *      Functions and Operators 3.1</a>
 * @see dev.metaschema.core.metapath.function
 */

package dev.metaschema.core.metapath.function.library;
