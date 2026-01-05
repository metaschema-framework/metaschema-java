/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import dev.metaschema.core.datatype.AbstractDataTypeAdapter;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IDecimalItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#decimal">decimal</a>
 * data type.
 */
public class DecimalAdapter
    extends AbstractDataTypeAdapter<BigDecimal, IDecimalItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "decimal")));

  DecimalAdapter() {
    super(BigDecimal.class, IDecimalItem.class, IDecimalItem::cast);
  }

  /**
   * Provides a standardized math context to use with {@link BigDecimal}
   * operations.
   * <p>
   * This math context was chosen for decimal arithmetic operations, since
   * DECIMAL64 provides a precision of 16 digits, which is sufficient for most
   * business calculations while maintaining reasonable performance.
   *
   * @return the standardized math context
   */
  @SuppressWarnings("null")
  @NonNull
  public static MathContext mathContext() {
    return MathContext.DECIMAL64;
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.NUMBER;
  }

  @Override
  public BigDecimal parse(String value) {
    return new BigDecimal(value, mathContext());
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
  public IDecimalItem newItem(Object value) {
    BigDecimal item = toValue(value);
    return IDecimalItem.valueOf(item);
  }
}
