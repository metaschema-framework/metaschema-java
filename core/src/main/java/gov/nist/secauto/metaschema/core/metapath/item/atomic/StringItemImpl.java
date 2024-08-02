/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

class StringItemImpl
    extends AbstractStringItem {

  public StringItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public StringAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.STRING;
  }
}
