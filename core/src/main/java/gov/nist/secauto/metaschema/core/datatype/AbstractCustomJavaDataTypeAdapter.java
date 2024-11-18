/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a Java type adapter implementation for data types that are based on
 * {@link ICustomJavaDataType}.
 *
 * @param <TYPE>
 *          the Java type this adapter supports, which is based on
 *          {@link ICustomJavaDataType}
 * @param <ITEM_TYPE>
 *          the Metapath item type associated with the adapter
 */
public abstract class AbstractCustomJavaDataTypeAdapter<
    TYPE extends ICustomJavaDataType<
        TYPE>,
    ITEM_TYPE extends IAnyAtomicItem>
    extends AbstractDataTypeAdapter<TYPE, ITEM_TYPE> {

  /**
   * Construct a new Java type adapter for the class based on
   * {@link ICustomJavaDataType}.
   *
   * @param valueClass
   *          a data type class based on {@link ICustomJavaDataType}
   * @param itemClass
   *          the Java type of the Matepath item this adapter supports
   */
  public AbstractCustomJavaDataTypeAdapter(
      @NonNull Class<TYPE> valueClass,
      @NonNull Class<ITEM_TYPE> itemClass) {
    super(valueClass, itemClass);
  }

  @SuppressWarnings("unchecked")
  @Override
  public TYPE copy(Object obj) {
    // Datatype-based types are required to provide a copy method. Delegate to this
    // method.
    return ((TYPE) obj).copy();
  }
}
