/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.IModelInstance;

public interface IBindingInstanceModel extends IBindingInstance, IModelInstance {
  @Override
  IBindingDefinitionModelAssembly getContainingDefinition();
}
