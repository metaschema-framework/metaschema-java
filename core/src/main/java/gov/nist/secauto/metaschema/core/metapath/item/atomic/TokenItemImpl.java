/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

class TokenItemImpl
    extends AbstractStringItem
    implements ITokenItem {

  public TokenItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public TokenAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.TOKEN;
  }
}
