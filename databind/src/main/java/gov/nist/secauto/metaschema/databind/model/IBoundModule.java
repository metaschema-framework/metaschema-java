/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IModuleExtended;
import gov.nist.secauto.metaschema.databind.IBindingContext;

import java.net.URI;
import java.util.Collection;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IBoundModule
    extends IModuleExtended<
        IBoundModule,
        IBoundDefinitionModelComplex,
        IBoundDefinitionFlag,
        IBoundDefinitionModelField<?>,
        IBoundDefinitionModelAssembly> {

  /**
   * Get the Module binding context.
   *
   * @return the context
   */
  @NonNull
  IBindingContext getBindingContext();

  @Override
  default URI getLocation() { // NOPMD - intentional
    // not known
    return null;
  }

  @Override
  Collection<IBoundDefinitionModelAssembly> getAssemblyDefinitions();

  @Override
  IBoundDefinitionModelAssembly getAssemblyDefinitionByName(@NonNull QName name);

  @Override
  Collection<IBoundDefinitionModelField<?>> getFieldDefinitions();

  @Override
  IBoundDefinitionModelField<?> getFieldDefinitionByName(@NonNull QName name);
}
