/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete implementations of Metapath function items (arrays and
 * maps).
 * <p>
 * This package contains the implementation classes for the function item
 * interfaces defined in
 * {@link gov.nist.secauto.metaschema.core.metapath.item.function}. These
 * classes provide immutable, efficient implementations of arrays and maps for
 * use in Metapath expressions.
 *
 * <h2>Array Implementations</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.AbstractArrayItem}
 * - Base class for all array item implementations, providing common utility
 * methods</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.ArrayItemN}
 * - Array implementation that supports an unbounded number of members</li>
 * </ul>
 *
 * <h2>Map Implementations</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.AbstractMapItem}
 * - Base class for all map item implementations, providing common utility
 * methods including the function call interface for map lookup</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.MapItemN}
 * - Map implementation that supports an unbounded number of entries</li>
 * </ul>
 *
 * <h2>Map Key Implementations</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.AbstractStringMapKey}
 * - Base implementation for string-based map keys</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.AbstractKeySpecifier}
 * - Base implementation for key specifier that computes keys from items</li>
 * </ul>
 *
 * <h2>Utility Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.ImmutableCollections}
 * - Utility classes for creating immutable collection wrappers</li>
 * </ul>
 *
 * <h2>Exceptions</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.function.impl.ArrayMetapathException}
 * - Base exception for array-related errors</li>
 * </ul>
 *
 * <h2>Implementation Notes</h2>
 * <p>
 * All array and map implementations in this package are immutable. Attempts to
 * modify them through the {@link java.util.List} or {@link java.util.Map}
 * interfaces will throw {@link UnsupportedOperationException}.
 * <p>
 * Array indices in the public API are 1-based (following XPath 3.1
 * conventions), but are converted to 0-based indices internally when accessing
 * the underlying Java collections.
 *
 * @see gov.nist.secauto.metaschema.core.metapath.item.function
 */

package gov.nist.secauto.metaschema.core.metapath.item.function.impl;
