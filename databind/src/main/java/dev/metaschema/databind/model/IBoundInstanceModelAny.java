/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import java.lang.reflect.Field;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.model.IAnyInstance;
import dev.metaschema.databind.model.impl.InstanceModelAny;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an {@code any} instance bound to a Java field annotated with
 * {@link dev.metaschema.databind.model.annotations.BoundAny @BoundAny}.
 *
 * <p>
 * This interface bridges the core {@link IAnyInstance} with the databind
 * binding layer, providing reflective access to the {@link IAnyContent} field
 * on a bound object.
 */
public interface IBoundInstanceModelAny extends IAnyInstance, IFeatureJavaField {

  /**
   * Create a new bound {@code any} instance.
   *
   * @param field
   *          the Java field annotated with {@code @BoundAny}
   * @param containingDefinition
   *          the assembly definition containing this instance
   * @return the new bound {@code any} instance
   */
  @NonNull
  static IBoundInstanceModelAny newInstance(
      @NonNull Field field,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    return InstanceModelAny.newInstance(field, containingDefinition);
  }

  /**
   * Get the containing assembly definition for this instance.
   *
   * @return the containing assembly definition
   */
  @Override
  @NonNull
  IBoundDefinitionModelAssembly getContainingDefinition();

  /**
   * Get the {@link IAnyContent} value from the parent bound object.
   *
   * @param parent
   *          the parent object containing the bound field
   * @return the captured unmodeled content, or {@code null} if no content has
   *         been captured
   */
  @Nullable
  default IAnyContent getAnyContent(@NonNull Object parent) {
    return (IAnyContent) getValue(parent);
  }

  /**
   * Set the {@link IAnyContent} value on the parent bound object.
   *
   * @param parent
   *          the parent object containing the bound field
   * @param value
   *          the unmodeled content to set, or {@code null} to clear it
   */
  default void setAnyContent(@NonNull Object parent, @Nullable IAnyContent value) {
    setValue(parent, value);
  }
}
