/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.EmailAddressAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import edu.umd.cs.findbugs.annotations.NonNull;

class EmailAddressItemImpl
    extends AbstractStringItem
    implements IEmailAddressItem {

  public EmailAddressItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public EmailAddressAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.EMAIL_ADDRESS;
  }
}
