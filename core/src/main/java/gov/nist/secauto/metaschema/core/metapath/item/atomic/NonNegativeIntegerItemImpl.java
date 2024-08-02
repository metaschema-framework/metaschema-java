/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.NonNegativeIntegerAdapter;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

class NonNegativeIntegerItemImpl
    extends AbstractIntegerItem
    implements INonNegativeIntegerItem {

  protected NonNegativeIntegerItemImpl(@NonNull BigInteger value) {
    super(value);
  }

  @Override
  public NonNegativeIntegerAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.NON_NEGATIVE_INTEGER;
  }
}
