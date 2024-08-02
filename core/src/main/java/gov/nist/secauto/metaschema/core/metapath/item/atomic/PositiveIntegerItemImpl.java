/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.PositiveIntegerAdapter;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

class PositiveIntegerItemImpl
    extends AbstractIntegerItem
    implements IPositiveIntegerItem {

  protected PositiveIntegerItemImpl(@NonNull BigInteger value) {
    super(value);
  }

  @Override
  public PositiveIntegerAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.POSITIVE_INTEGER;
  }
}
