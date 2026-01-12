/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import dev.metaschema.core.model.IInstance;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metaschema instance loaded via data binding.
 * <p>
 * This interface provides access to binding-specific metadata for flag, field,
 * and assembly instances.
 */
public interface IBindingInstance extends IInstance, IBindingModelElement {
  @Override
  @NonNull
  IBindingDefinitionModel getContainingDefinition();

  @Override
  default IBindingMetaschemaModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }
}
