/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigInteger;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

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
  public @NonNull Class<IIntegerItem> getItemClass() {
    return IIntegerItem.class;
  }

  @Override
  public IIntegerItem newItem(Object value) {
    BigInteger item = toValue(value);
    return IIntegerItem.valueOf(item);
  }

  @Override
  protected IIntegerItem castInternal(@NonNull IAnyAtomicItem item) {
    IIntegerItem retval;
    if (item instanceof INumericItem) {
      retval = newItem(((INumericItem) item).asInteger());
    } else if (item instanceof IBooleanItem) {
      boolean value = ((IBooleanItem) item).toBoolean();
      retval = newItem(ObjectUtils.notNull(value ? BigInteger.ONE : BigInteger.ZERO));
    } else {
      retval = super.castInternal(item);
    }
    return retval;
  }
}
