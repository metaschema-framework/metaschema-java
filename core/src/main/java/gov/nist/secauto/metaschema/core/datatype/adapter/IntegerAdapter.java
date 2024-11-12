/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigInteger;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#integer">integer</a>
 * data type.
 */
public class IntegerAdapter
    extends AbstractIntegerAdapter<IIntegerItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "integer")));

  IntegerAdapter() {
    // avoid general construction
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  @NonNull
  public Class<IIntegerItem> getItemClass() {
    return IIntegerItem.class;
  }

  @Override
  public IIntegerItem newItem(Object value) {
    BigInteger item = toValue(value);
    return IIntegerItem.valueOf(item);
  }
}
