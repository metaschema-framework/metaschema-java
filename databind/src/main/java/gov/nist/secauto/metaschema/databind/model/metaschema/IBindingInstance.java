/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.IInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IBindingInstance extends IInstance, IBindingModelElement {
  @Override
  @NonNull
  IBindingDefinitionModel getContainingDefinition();

  @Override
  default IBindingMetaschemaModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }
}
