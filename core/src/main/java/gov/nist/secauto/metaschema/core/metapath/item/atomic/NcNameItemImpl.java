/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.NcNameAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

@Deprecated(forRemoval = true, since = "0.7.0")
class NcNameItemImpl
    extends AbstractStringItem
    implements INcNameItem {

  public NcNameItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public NcNameAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.NCNAME;
  }
}
