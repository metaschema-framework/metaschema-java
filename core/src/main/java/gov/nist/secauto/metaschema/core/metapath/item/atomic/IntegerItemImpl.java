/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.IntegerAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

class IntegerItemImpl
    extends AbstractIntegerItem {

  protected IntegerItemImpl(@NonNull BigInteger value) {
    super(value);
  }

  @Override
  public IntegerAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.INTEGER;
  }
}
