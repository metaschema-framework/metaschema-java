/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.HostnameAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IHostnameItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a hostname data value.
 */
public class HostnameItemImpl
    extends AbstractStringItem
    implements IHostnameItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public HostnameItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public HostnameAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.HOSTNAME;
  }
}
