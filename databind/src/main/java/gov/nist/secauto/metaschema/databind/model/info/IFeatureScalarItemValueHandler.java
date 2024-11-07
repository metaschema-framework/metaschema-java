/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IValuedMutable;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureScalarItemValueHandler
    extends IItemValueHandler<Object>, IValuedMutable {

  /**
   * Apply the string value.
   * <p>
   * This first parses the value using the underlying data type implementation and
   * then applies the parsed value.
   *
   * @param parent
   *          the parent object to apply the value to
   * @param text
   *          the value to parse
   * @throws IllegalArgumentException
   *           if the text was malformed
   * @see #getJavaTypeAdapter()
   */
  default void setValue(@NonNull Object parent, @NonNull String text) {
    Object item = getValueFromString(text);
    setValue(parent, item);
  }

  /**
   * Parse a string value using the underlying data type implementation.
   *
   * @param text
   *          the value to parse
   * @return the parsed value
   * @throws IllegalArgumentException
   *           if the text was malformed
   * @see #getJavaTypeAdapter()
   */
  default Object getValueFromString(@NonNull String text) {
    return getJavaTypeAdapter().parse(text);
  }

  /**
   * Get the data type adapter supporting the scalar value.
   *
   * @return the data type adapter
   */
  @NonNull
  IDataTypeAdapter<?> getJavaTypeAdapter();

  @Override
  default Object deepCopyItem(Object source, IBoundObject parentInstance) throws BindingException {
    return getJavaTypeAdapter().copy(source);
  }
}
