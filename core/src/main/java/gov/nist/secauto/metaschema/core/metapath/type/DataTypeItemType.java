/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract implementation of an atomic type backed by a data type adapter.
 *
 * @param <T>
 *          the Java type of the item supported by the implementation
 */
public class DataTypeItemType<T extends IAnyAtomicItem>
    extends AbstractAtomicOrUnionType<T> {
  @NonNull
  private final IDataTypeAdapter<?> adapter;

  /**
   * Construct a new atomic type.
   *
   * @param adapter
   *          the data type adapter supporting the item type
   * @param itemClass
   *          the item class this atomic type supports
   * @param castExecutor
   *          the executor used to cast an item to an item of this type
   */
  public DataTypeItemType(
      @NonNull IDataTypeAdapter<?> adapter,
      @NonNull Class<T> itemClass,
      @NonNull ICastExecutor<T> castExecutor) {
    super(itemClass, castExecutor);
    this.adapter = adapter;
  }

  @Override
  @NonNull
  public IDataTypeAdapter<?> getAdapter() {
    return adapter;
  }

  @Override
  public IEnhancedQName getQName() {
    return getAdapter().getPreferredName();
  }

  @Override
  public String toString() {
    return toSignature();
  }

  @Override
  public int hashCode() {
    return Objects.hash(adapter, getItemClass());
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DataTypeItemType)) {
      return false;
    }
    DataTypeItemType<?> other = (DataTypeItemType<?>) obj;
    return Objects.equals(getAdapter(), other.getAdapter())
        && Objects.equals(getItemClass(), other.getItemClass());
  }
}
