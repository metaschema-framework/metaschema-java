/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a text data value.
 */
public class StringItemImpl
    extends AbstractStringItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public StringItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public StringAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.STRING;
  }
}
