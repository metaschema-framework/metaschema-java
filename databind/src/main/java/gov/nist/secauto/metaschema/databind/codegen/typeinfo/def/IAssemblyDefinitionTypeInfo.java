/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo.def;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.ITypeResolver;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Java class type information for an {@link IAssemblyDefinition} used for
 * generating a Java class for the definition.
 */
public interface IAssemblyDefinitionTypeInfo extends IModelDefinitionTypeInfo {

  /**
   * Construct a new type info based on the provided definition.
   *
   * @param definition
   *          the definition associated with the type info
   * @param typeResolver
   *          a resolver used to look up related type information
   * @return the type info for the definition
   */
  @NonNull
  static IAssemblyDefinitionTypeInfo newTypeInfo(@NonNull IAssemblyDefinition definition,
      @NonNull ITypeResolver typeResolver) {
    return new AssemblyDefinitionTypeInfoImpl(definition, typeResolver);
  }

  @Override
  IAssemblyDefinition getDefinition();
}
