/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo.def;

import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.databind.codegen.typeinfo.ITypeResolver;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Java class type information for an {@link IFieldDefinition} used for
 * generating a Java class for the definition.
 */
public interface IFieldDefinitionTypeInfo extends IModelDefinitionTypeInfo {

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
  static IFieldDefinitionTypeInfo newTypeInfo(@NonNull IFieldDefinition definition,
      @NonNull ITypeResolver typeResolver) {
    return new FieldDefinitionTypeInfoImpl(definition, typeResolver);
  }

  @Override
  IFieldDefinition getDefinition();
}
