/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.EmailAddressAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IEmailAddressItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing an email address data
 * value.
 */
public class EmailAddressItemImpl
    extends AbstractStringItem
    implements IEmailAddressItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public EmailAddressItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public EmailAddressAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.EMAIL_ADDRESS;
  }
}
