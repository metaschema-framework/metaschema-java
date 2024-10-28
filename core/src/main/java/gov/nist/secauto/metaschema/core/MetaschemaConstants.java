/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class MetaschemaConstants {
  /**
   * This is the namespace used by Metaschema in formats that require or use a
   * namespace, and in properties that are defined by the Metaschema
   * specification.
   */
  @NonNull
  public static final String METASCHEMA_NAMESPACE = "http://csrc.nist.gov/ns/oscal/metaschema/1.0";

  /**
   * The {@link #METASCHEMA_NAMESPACE} as a {@link URI}.
   */
  @NonNull
  public static final URI METASCHEMA_NAMESPACE_URI = ObjectUtils.notNull(URI.create(METASCHEMA_NAMESPACE));

  private MetaschemaConstants() {
    // disable construction
  }
}
