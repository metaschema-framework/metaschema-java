/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a URI data value.
 */
public class AnyUriItemImpl
    extends AbstractUriItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public AnyUriItemImpl(@NonNull URI value) {
    super(value);
  }

  @Override
  public UriAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.URI;
  }
}
