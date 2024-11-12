/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.NcNameAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INcNameItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a non-colonized name
 * data value.
 */
@Deprecated(forRemoval = true, since = "0.7.0")
public class NcNameItemImpl
    extends AbstractStringItem
    implements INcNameItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public NcNameItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public NcNameAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.NCNAME;
  }
}
