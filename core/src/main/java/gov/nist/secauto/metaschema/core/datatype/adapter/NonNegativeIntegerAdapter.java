/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INonNegativeIntegerItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigInteger;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class NonNegativeIntegerAdapter
    extends AbstractIntegerAdapter<INonNegativeIntegerItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "non-negative-integer"),
          // for backwards compatibility with original type name
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "nonNegativeInteger")));

  NonNegativeIntegerAdapter() {
    // avoid general construction
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public Class<INonNegativeIntegerItem> getItemClass() {
    return INonNegativeIntegerItem.class;
  }

  @Override
  public INonNegativeIntegerItem newItem(Object value) {
    BigInteger item = toValue(value);
    return INonNegativeIntegerItem.valueOf(item);
  }
}
