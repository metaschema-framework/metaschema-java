/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Core interfaces and classes for the Metapath item type system.
 * <p>
 * This package provides the foundational abstractions for representing values
 * in Metapath expressions, aligning with the XPath 3.1 Data Model. All Metapath
 * values are either items or sequences of items.
 * <p>
 * Key interfaces include:
 * <ul>
 * <li>{@link dev.metaschema.core.metapath.item.IItem} - Base interface for all
 * Metapath items (atomic values, nodes, functions, arrays, and maps)</li>
 * <li>{@link dev.metaschema.core.metapath.item.ISequence} - Ordered collection
 * of items representing expression evaluation results</li>
 * <li>{@link dev.metaschema.core.metapath.item.ICollectionValue} - Common
 * interface for values that can be stored in arrays or maps</li>
 * <li>{@link dev.metaschema.core.metapath.item.IItemVisitor} - Visitor pattern
 * interface for traversing item hierarchies</li>
 * </ul>
 * <p>
 * The type system integrates with Metaschema data types defined in
 * {@link dev.metaschema.core.datatype} while extending them to support the full
 * XPath 3.1 type hierarchy including atomic items, node items, and function
 * items.
 *
 * @see <a href="https://www.w3.org/TR/xpath-31/">XPath 3.1 Specification</a>
 * @see <a href="https://www.w3.org/TR/xpath-datamodel-31/">XPath 3.1 Data
 *      Model</a>
 */

package dev.metaschema.core.metapath.item;
