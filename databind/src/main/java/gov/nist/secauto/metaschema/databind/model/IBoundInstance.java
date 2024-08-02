/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IInstance;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metaschema module instance bound to Java data.
 *
 * @param <ITEM>
 *          the Java type for associated bound objects
 */
public interface IBoundInstance<ITEM> extends IBoundProperty<ITEM>, IBoundModelElement, IInstance {
  @Override
  IBoundDefinitionModel<IBoundObject> getContainingDefinition();

  @Override
  default IBoundModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Always bound to a field.
   */
  @Override
  @Nullable
  default Object getValue(@NonNull Object parent) {
    return IBoundProperty.super.getValue(parent);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Always bound to a field.
   */
  @Override
  default void setValue(@NonNull Object parentObject, @Nullable Object value) {
    IBoundProperty.super.setValue(parentObject, value);
  }
}
