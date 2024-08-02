/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import javax.xml.XMLConstants;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides constant values used in Metapath.
 */
@SuppressWarnings("PMD.DataClass")
public final class MetapathConstants {
  @NonNull
  public static final URI NS_METAPATH = ObjectUtils.requireNonNull(
      URI.create("http://csrc.nist.gov/ns/metaschema/metapath"));
  @NonNull
  public static final URI NS_XML_SCHEMA = ObjectUtils.requireNonNull(
      URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI));
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS = ObjectUtils.requireNonNull(
      URI.create("http://csrc.nist.gov/ns/metaschema/metapath-functions"));
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_MATH = ObjectUtils.requireNonNull(
      URI.create(NS_METAPATH_FUNCTIONS + "/math"));
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_ARRAY = ObjectUtils.requireNonNull(
      URI.create(NS_METAPATH_FUNCTIONS + "/array"));
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_MAP = ObjectUtils.requireNonNull(
      URI.create(NS_METAPATH_FUNCTIONS + "/map"));
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_EXTENDED = NS_METAPATH_FUNCTIONS;

  @NonNull
  public static final String PREFIX_METAPATH = "meta";
  @NonNull
  public static final String PREFIX_XML_SCHEMA = "xs";
  @NonNull
  public static final String PREFIX_XPATH_FUNCTIONS = "mp";
  @NonNull
  public static final String PREFIX_XPATH_FUNCTIONS_MATH = "math";
  @NonNull
  public static final String PREFIX_XPATH_FUNCTIONS_ARRAY = "array";
  @NonNull
  public static final String PREFIX_XPATH_FUNCTIONS_MAP = "map";

  private MetapathConstants() {
    // disable construction
  }
}
