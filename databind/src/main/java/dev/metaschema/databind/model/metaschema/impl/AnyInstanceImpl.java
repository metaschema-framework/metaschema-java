/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.IAnyInstance;
import dev.metaschema.core.model.IContainerModelAbsolute;
import dev.metaschema.core.model.IModule;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements an {@code any} instance for module-loaded assembly definitions.
 * <p>
 * This class provides a simple implementation of {@link IAnyInstance} that is
 * used when a Metaschema module is loaded from its binding representation. It
 * stores a reference to the containing model container (typically an assembly
 * definition) and delegates other behavior to the default methods on
 * {@link IAnyInstance}.
 */
public final class AnyInstanceImpl implements IAnyInstance {
  @NonNull
  private final IContainerModelAbsolute parentContainer;

  /**
   * Construct a new {@code any} instance for a module-loaded assembly.
   *
   * @param parentContainer
   *          the model container (assembly definition) that owns this instance
   */
  public AnyInstanceImpl(@NonNull IContainerModelAbsolute parentContainer) {
    this.parentContainer = parentContainer;
  }

  @Override
  public IContainerModelAbsolute getParentContainer() {
    return parentContainer;
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
