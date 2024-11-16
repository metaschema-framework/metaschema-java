/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides constant values for use in Metapath.
 */
@SuppressWarnings("PMD.DataClass")
public final class MetapathConstants {
  /**
   * The namespace URI for Metapath data types and built-in casting functions.
   */
  @NonNull
  public static final URI NS_METAPATH = ObjectUtils.requireNonNull(
      URI.create("http://csrc.nist.gov/ns/metaschema/metapath"));
  /**
   * The namespace URI for Metapath built-in functions.
   *
   * @see #PREFIX_METAPATH for the default prefix for this namespace
   */
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS = ObjectUtils.requireNonNull(
      URI.create("http://csrc.nist.gov/ns/metaschema/metapath-functions"));
  /**
   * The namespace URI for Metapath math-related built-in functions.
   *
   * @see #PREFIX_METAPATH_FUNCTIONS_MATH for the default prefix for this
   *      namespace
   */
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_MATH = ObjectUtils.requireNonNull(
      URI.create(NS_METAPATH_FUNCTIONS + "/math"));
  /**
   * The namespace URI for Metapath array-related built-in functions.
   *
   * @see #PREFIX_METAPATH_FUNCTIONS_ARRAY for the default prefix for this
   *      namespace
   */
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_ARRAY = ObjectUtils.requireNonNull(
      URI.create(NS_METAPATH_FUNCTIONS + "/array"));
  /**
   * The namespace URI for Metapath map-related built-in functions.
   *
   * @see #PREFIX_METAPATH_FUNCTIONS_MAP for the default prefix for this namespace
   */
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_MAP = ObjectUtils.requireNonNull(
      URI.create(NS_METAPATH_FUNCTIONS + "/map"));
  /**
   * The namespace URI for Metapath extension built-in functions.
   * <p>
   * This is currently an alias for {@link #NS_METAPATH_FUNCTIONS} and can be used
   * when implementing custom extension functions to distinguish them from core
   * functions.
   */
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_EXTENDED = NS_METAPATH_FUNCTIONS;

  /**
   * The namespace prefix for Metapath data types and built-in casting functions.
   *
   * @see #NS_METAPATH for the corresponding namespace URI
   */
  @NonNull
  public static final String PREFIX_METAPATH = "meta";
  /**
   * The namespace prefix for Metapath built-in functions.
   *
   * @see #NS_METAPATH_FUNCTIONS for the corresponding namespace URI
   */
  @NonNull
  public static final String PREFIX_METAPATH_FUNCTIONS = "mp";
  /**
   * The namespace prefix for Metapath math-related built-in functions.
   *
   * @see #NS_METAPATH_FUNCTIONS_MATH for the corresponding namespace URI
   */
  @NonNull
  public static final String PREFIX_METAPATH_FUNCTIONS_MATH = "math";
  /**
   * The namespace prefix for Metapath array-related built-in functions.
   *
   * @see #NS_METAPATH_FUNCTIONS_ARRAY for the corresponding namespace URI
   */
  @NonNull
  public static final String PREFIX_METAPATH_FUNCTIONS_ARRAY = "array";
  /**
   * The namespace prefix for Metapath map-related built-in functions.
   *
   * @see #NS_METAPATH_FUNCTIONS_MAP for the corresponding namespace URI
   */
  @NonNull
  public static final String PREFIX_METAPATH_FUNCTIONS_MAP = "map";

  private MetapathConstants() {
    // disable construction
  }
}
