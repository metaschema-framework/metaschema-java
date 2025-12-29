/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;

/**
 * Represents a Metaschema assembly definition loaded via data binding.
 * <p>
 * This interface provides access to assembly-specific metadata including the
 * root name for assemblies that can serve as document roots.
 */
public interface IBindingDefinitionModelAssembly extends IBindingDefinitionModel, IAssemblyDefinition {
  // no additional methods
}
