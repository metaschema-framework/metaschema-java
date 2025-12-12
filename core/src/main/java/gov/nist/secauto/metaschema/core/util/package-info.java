/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides utility classes for common operations throughout the Metaschema
 * framework.
 * <p>
 * This package contains helper classes for working with collections, objects,
 * strings, URIs, and other common Java types. These utilities provide null-safe
 * operations, type conversions, and enhanced functionality beyond standard Java
 * libraries.
 * <p>
 * Key utility classes:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.util.ObjectUtils} - Null-safety
 * assertions and object validation ({@code notNull},
 * {@code requireNonNull})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.util.CollectionUtil} - Collection
 * operations including unmodifiable wrappers, stream conversions, and null-safe
 * accessors</li>
 * <li>{@link gov.nist.secauto.metaschema.core.util.CustomCollectors} - Custom
 * stream collectors</li>
 * <li>{@link gov.nist.secauto.metaschema.core.util.AutoCloser} - Adapter for
 * making resources {@link AutoCloseable}</li>
 * <li>{@link gov.nist.secauto.metaschema.core.util.StringUtils} - String
 * manipulation and validation</li>
 * <li>{@link gov.nist.secauto.metaschema.core.util.UriUtils} - URI resolution
 * and manipulation</li>
 * <li>{@link gov.nist.secauto.metaschema.core.util.DeleteOnShutdown} -
 * Temporary file cleanup</li>
 * <li>{@link gov.nist.secauto.metaschema.core.util.IVersionInfo} - Version
 * information interface</li>
 * </ul>
 * <p>
 * These utilities are primarily designed for internal framework use but may
 * also be useful for applications built on the Metaschema framework.
 */

package gov.nist.secauto.metaschema.core.util;
