/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import dev.metaschema.core.model.IModelDefinition;

/**
 * Represents a Metaschema model definition loaded via data binding.
 * <p>
 * This interface provides access to the binding-specific metadata for field and
 * assembly definitions.
 */
public interface IBindingDefinitionModel extends IModelDefinition, IBindingModelElement {
  // no additional methods
}
