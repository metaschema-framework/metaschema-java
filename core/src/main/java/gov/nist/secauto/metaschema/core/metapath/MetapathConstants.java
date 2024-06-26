/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
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
      URI.create("http://csrc.nist.gov/ns/metaschema/metapath-functions/math"));
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_ARRAY = ObjectUtils.requireNonNull(
      URI.create("http://csrc.nist.gov/ns/metaschema/metapath-functions/array"));
  @NonNull
  public static final URI NS_METAPATH_FUNCTIONS_EXTENDED = NS_METAPATH;

  @NonNull
  public static final String PREFIX_METAPATH = "mp";
  @NonNull
  public static final String PREFIX_XML_SCHEMA = "xs";
  @NonNull
  public static final String PREFIX_XPATH_FUNCTIONS = "mp";
  @NonNull
  public static final String PREFIX_XPATH_FUNCTIONS_MATH = "math";
  @NonNull
  public static final String PREFIX_XPATH_FUNCTIONS_ARRAY = "array";

  private MetapathConstants() {
    // disable construction
  }
}
