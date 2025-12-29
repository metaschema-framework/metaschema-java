/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.IModelInstance;

/**
 * Represents a Metaschema model instance loaded via data binding.
 * <p>
 * This interface provides access to binding-specific metadata for field and
 * assembly instances.
 */
public interface IBindingInstanceModel extends IBindingInstance, IModelInstance {
  @Override
  IBindingDefinitionModelAssembly getContainingDefinition();
}
