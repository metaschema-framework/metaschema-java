/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IPositiveIntegerItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigInteger;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#positive-integer">positive-integer</a>
 * data type.
 */
public class PositiveIntegerAdapter
    extends AbstractIntegerAdapter<IPositiveIntegerItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "positive-integer"),
          // for backwards compatibility with original type name
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "positiveInteger")));

  PositiveIntegerAdapter() {
    super(IPositiveIntegerItem.class, IPositiveIntegerItem::cast);
    // avoid general construction
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public IPositiveIntegerItem newItem(Object value) {
    BigInteger item = toValue(value);
    return IPositiveIntegerItem.valueOf(item);
  }
}
