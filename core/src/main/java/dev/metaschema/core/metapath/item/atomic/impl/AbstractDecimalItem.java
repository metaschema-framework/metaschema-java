/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import dev.metaschema.core.metapath.impl.AbstractDecimalMapKey;
import dev.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IDecimalItem;
import dev.metaschema.core.metapath.item.function.IMapKey;
import dev.metaschema.core.util.ObjectUtils;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * An abstract implementation of a Metapath atomic item containing a decimal
 * data value.
 *
 * @param <TYPE>
 *          the Java type of the wrapped value
 */
public abstract class AbstractDecimalItem<TYPE>
    extends AbstractAnyAtomicItem<TYPE>
    implements IDecimalItem {

  @SuppressWarnings("synthetic-access")
  private final Lazy<String> stringValue = Lazy.of(super::asString);

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  protected AbstractDecimalItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  public String asString() {
    return ObjectUtils.notNull(stringValue.get());
  }

  @Override
  protected String getValueSignature() {
    return asString();
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  private final class MapKey
      extends AbstractDecimalMapKey {

    @Override
    public IDecimalItem getKey() {
      return AbstractDecimalItem.this;
    }

    @Override
    public BigDecimal asDecimal() {
      return getKey().asDecimal();
    }
  }
}
