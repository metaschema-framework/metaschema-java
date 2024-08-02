/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A common base implementation of a custom Java object providing an underlying
 * implementation of a data type.
 *
 * @param <TYPE>
 *          the bound object type supported by this data type
 * @param <VALUE>
 *          the inner value of the data type object
 */
public abstract class AbstractCustomJavaDataType<TYPE extends ICustomJavaDataType<TYPE>, VALUE>
    implements ICustomJavaDataType<TYPE> {
  @NonNull
  private final VALUE value;

  /**
   * Construct a new instance of a custom Java object-based data value.
   *
   * @param value
   *          the bound object that the data type is based on
   */
  protected AbstractCustomJavaDataType(@NonNull VALUE value) {
    this.value = ObjectUtils.requireNonNull(value, "value");
  }

  /**
   * Get the bound Java object value.
   *
   * @return the bound object
   */
  @NonNull
  public VALUE getValue() {
    return value;
  }
  //
  // public void setValue(T value) {
  // this.value = value;
  // }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return Objects.equals(value, obj);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
