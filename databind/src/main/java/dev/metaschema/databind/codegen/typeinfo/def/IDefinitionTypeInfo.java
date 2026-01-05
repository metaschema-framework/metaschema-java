/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo.def;

import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.IInstance;
import dev.metaschema.databind.codegen.typeinfo.IInstanceTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.IPropertyTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.ITypeResolver;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides type information for a Metaschema definition.
 * <p>
 * This interface provides access to type resolution and instance information
 * for generating Java classes from Metaschema definitions.
 */
public interface IDefinitionTypeInfo {
  /**
   * Get the definition associated with this type info.
   *
   * @return the definition
   */
  @NonNull
  IDefinition getDefinition();

  /**
   * Gets the resolver which can be used to lookup Java type information for
   * Module objects.
   *
   * @return the type resolver
   */
  @NonNull
  ITypeResolver getTypeResolver();

  // /**
  // * Check's if the Java class to be generated will have a property with the
  // given
  // * name.
  // *
  // * @param propertyName
  // * the property name to look for
  // * @return {@code true} if there is an associated property with the name or
  // * {@code false} otherwise
  // */
  // boolean hasPropertyWithName(@NonNull String propertyName);

  /**
   * Get the type information for the provided {@code instance} value.
   *
   * @param instance
   *          the instance to get type information for
   * @return the type information
   */
  @Nullable
  IInstanceTypeInfo getInstanceTypeInfo(@NonNull IInstance instance);

  /**
   * Get the type information for all instance values on this definition.
   *
   * @return the type information
   */
  @NonNull
  Collection<IInstanceTypeInfo> getInstanceTypeInfos();

  /**
   * Get the type information for all Java class properties associated with a
   * given instance.
   *
   * @return the type information
   */
  @NonNull
  Collection<IPropertyTypeInfo> getPropertyTypeInfos();
}
