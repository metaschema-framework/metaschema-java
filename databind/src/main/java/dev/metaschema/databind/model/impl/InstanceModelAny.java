/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import java.lang.reflect.Field;

import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.model.IContainerModel;
import dev.metaschema.core.model.IModule;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.IBoundInstanceModelAny;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements a bound {@code any} instance backed by a Java field annotated with
 * {@link dev.metaschema.databind.model.annotations.BoundAny @BoundAny}.
 *
 * <p>
 * This class uses reflection to get and set the {@link IAnyContent} field on a
 * bound object, following the same pattern as other bound instance
 * implementations in this package.
 */
public final class InstanceModelAny implements IBoundInstanceModelAny {
  @NonNull
  private final Field javaField;
  @NonNull
  private final IBoundDefinitionModelAssembly containingDefinition;

  /**
   * Construct a new bound {@code any} instance.
   *
   * @param javaField
   *          the Java field annotated with {@code @BoundAny}
   * @param containingDefinition
   *          the assembly definition containing this instance
   * @return the new instance
   */
  @NonNull
  public static InstanceModelAny newInstance(
      @NonNull Field javaField,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    return new InstanceModelAny(javaField, containingDefinition);
  }

  private InstanceModelAny(
      @NonNull Field javaField,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    FieldSupport.bindField(javaField);
    this.javaField = javaField;
    this.containingDefinition = containingDefinition;
  }

  @Override
  public Field getField() {
    return javaField;
  }

  @Override
  public IBoundDefinitionModelAssembly getContainingDefinition() {
    return containingDefinition;
  }

  @Override
  public IContainerModel getParentContainer() {
    return getContainingDefinition();
  }

  @Override
  public IModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  @Override
  @Nullable
  public MarkupMultiline getRemarks() {
    // any instances do not have remarks
    return null;
  }
}
