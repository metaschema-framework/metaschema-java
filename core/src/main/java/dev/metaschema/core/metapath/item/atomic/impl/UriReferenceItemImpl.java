/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import java.net.URI;

import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.datatype.adapter.UriReferenceAdapter;
import dev.metaschema.core.metapath.item.atomic.IUriReferenceItem;
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
