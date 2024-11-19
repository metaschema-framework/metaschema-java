/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureScalarItemValueHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IBoundFieldValue extends IFeatureScalarItemValueHandler, IBoundProperty<Object> {
  @Override
  @Nullable
  Object getDefaultValue();

  /**
   * Get the field definition that contain's the field value.
   *
   * @return the parent field definition
   */
  @NonNull
  IBoundDefinitionModelFieldComplex getParentFieldDefinition();

  /**
   * Get the name of the JSON value key flag.
   * <p>
   * Note: if a JSON value key flag is specified, then the JSON value key name is
   * expected to be ignored.
   *
   * @return the flag name or {@code null} if no JSON value key flag is configured
   * @see #getJsonValueKeyName()
   */
  @Nullable
  String getJsonValueKeyFlagName();

  /**
   * Get the name of the JSON value key.
   * <p>
   * Note: if a JSON value key flag is specified, then this value is expected to
   * be ignored.
   *
   * @return the name
   * @see #getJsonValueKeyFlagName()
   */
  @NonNull
  String getJsonValueKeyName();

  @Override
  default Object getEffectiveDefaultValue() {
    return getDefaultValue();
  }

  @Override
  default Object readItem(IBoundObject parent, IItemReadHandler handler) throws IOException {
    return handler.readItemFieldValue(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(Object item, IItemWriteHandler handler) throws IOException {
    handler.writeItemFieldValue(item, this);
  }

  @Override
  default void deepCopy(@NonNull IBoundObject fromInstance, @NonNull IBoundObject toInstance) throws BindingException {
    Object value = getValue(fromInstance);
    if (value != null) {
      setValue(toInstance, value);
    }
  }

  @Override
  default boolean canHandleXmlQName(IEnhancedQName qname) {
    // REFACTOR: Is this correct?
    return getJavaTypeAdapter().canHandleQName(qname);
  }

}
