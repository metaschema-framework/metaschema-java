/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.metapath.atomic.IUriReferenceItem;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a URI reference data
 * value.
 */
public class UriReferenceItemImpl
    extends AbstractUriItem
    implements IUriReferenceItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public UriReferenceItemImpl(@NonNull URI value) {
    super(value);
  }

  @Override
  public UriReferenceAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.URI_REFERENCE;
  }
}
