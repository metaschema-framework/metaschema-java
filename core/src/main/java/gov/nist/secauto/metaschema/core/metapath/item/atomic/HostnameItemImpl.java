/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.HostnameAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import edu.umd.cs.findbugs.annotations.NonNull;

class HostnameItemImpl
    extends AbstractStringItem
    implements IHostnameItem {

  public HostnameItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public HostnameAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.HOSTNAME;
  }
}
