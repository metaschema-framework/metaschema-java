/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DecimalAdapter
    extends AbstractDataTypeAdapter<BigDecimal, IDecimalItem> {
  public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
  @NonNull
  private static final BigDecimal DECIMAL_BOOLEAN_TRUE = new BigDecimal("1.0");
  @NonNull
  private static final BigDecimal DECIMAL_BOOLEAN_FALSE = new BigDecimal("0.0");
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "decimal")));

  DecimalAdapter() {
    super(BigDecimal.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.NUMBER;
  }

  @Override
  public BigDecimal parse(String value) {
    return new BigDecimal(value, MATH_CONTEXT);
  }

  @Override
  public void writeJsonValue(Object value, JsonGenerator generator) throws IOException {
    try {
      generator.writeNumber((BigDecimal) value);
    } catch (ClassCastException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public BigDecimal copy(Object obj) {
    // a BigDecimal is immutable
    return (BigDecimal) obj;
  }

  @Override
  public Class<IDecimalItem> getItemClass() {
    return IDecimalItem.class;
  }

  @Override
  public IDecimalItem newItem(Object value) {
    BigDecimal item = toValue(value);
    return IDecimalItem.valueOf(item);
  }

  @Override
  protected IDecimalItem castInternal(@NonNull IAnyAtomicItem item) {
    IDecimalItem retval;
    if (item instanceof INumericItem) {
      retval = newItem(((INumericItem) item).asDecimal());
    } else if (item instanceof IBooleanItem) {
      boolean value = ((IBooleanItem) item).toBoolean();
      retval = newItem(value ? DECIMAL_BOOLEAN_TRUE : DECIMAL_BOOLEAN_FALSE);
    } else {
      retval = super.castInternal(item);
    }
    return retval;
  }
}
