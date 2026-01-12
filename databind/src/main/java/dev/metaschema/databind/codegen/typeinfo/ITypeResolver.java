/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.ClassName;

import java.util.List;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import dev.metaschema.databind.codegen.config.IBindingConfiguration;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.def.IFieldDefinitionTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.def.IModelDefinitionTypeInfo;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Resolves type information for Metaschema constructs.
 * <p>
 * This interface provides methods for resolving Java class names, package
 * names, and type information for Metaschema modules, definitions, and
 * instances.
 */
public interface ITypeResolver {
  /**
   * Construct a new type resolver using the default implementation.
   *
   * @param bindingConfiguration
   *          the binding configuration used to configure types
   * @return the type resolver
   */
  @NonNull
  static ITypeResolver newTypeResolver(@NonNull IBindingConfiguration bindingConfiguration) {
    return new DefaultTypeResolver(bindingConfiguration);
  }

  /**
   * Get the binding configuration used by this type resolver.
   *
   * @return the binding configuration
   */
  @NonNull
  IBindingConfiguration getBindingConfiguration();

  /**
   * Get type information for the provided {@code instance}.
   *
   * @param instance
   *          the instance to get type information for
   * @param parent
   *          the type information for the parent definition containing this
   *          instance
   * @return the type information
   */
  @NonNull
  default INamedModelInstanceTypeInfo getTypeInfo(
      @NonNull INamedModelInstanceAbsolute instance,
      @NonNull IAssemblyDefinitionTypeInfo parent) {
    INamedModelInstanceTypeInfo retval;
    if (instance instanceof IAssemblyInstanceAbsolute) {
      retval = new AssemblyInstanceTypeInfoImpl((IAssemblyInstanceAbsolute) instance, parent);
    } else if (instance instanceof IFieldInstance) {
      retval = new FieldInstanceTypeInfoImpl((IFieldInstanceAbsolute) instance, parent);
    } else {
      throw new UnsupportedOperationException(instance.getClass().getName());
    }
    return retval;
  }

  /**
   * Get type information for the provided {@code instance}.
   *
   * @param instance
   *          the instance to get type information for
   * @param parent
   *          the type information for the parent definition containing this
   *          instance
   * @return the type information
   */
  @NonNull
  default IChoiceGroupTypeInfo getTypeInfo(
      @NonNull IChoiceGroupInstance instance,
      @NonNull IAssemblyDefinitionTypeInfo parent) {
    return new ChoiceGroupTypeInfoImpl(instance, parent);
  }

  /**
   * Get type information for the provided grouped model {@code instance}.
   *
   * @param modelInstance
   *          the grouped model instance to get type information for
   * @param choiceGroupTypeInfoImpl
   *          the type information for the parent choice group containing this
   *          instance
   * @return the type information
   */
  @NonNull
  IGroupedNamedModelInstanceTypeInfo getTypeInfo(
      @NonNull INamedModelInstanceGrouped modelInstance,
      @NonNull IChoiceGroupTypeInfo choiceGroupTypeInfoImpl);

  /**
   * Get type information for the provided {@code definition}.
   *
   * @param definition
   *          the definition to get type information for
   * @return the type information
   */
  @NonNull
  IAssemblyDefinitionTypeInfo getTypeInfo(@NonNull IAssemblyDefinition definition);

  /**
   * Get type information for the provided {@code definition}.
   *
   * @param definition
   *          the definition to get type information for
   * @return the type information
   */
  @NonNull
  IFieldDefinitionTypeInfo getTypeInfo(@NonNull IFieldDefinition definition);

  /**
   * Get type information for the provided {@code definition}.
   *
   * @param definition
   *          the definition to get type information for
   * @return the type information
   */
  @NonNull
  IModelDefinitionTypeInfo getTypeInfo(@NonNull IModelDefinition definition);

  /**
   * Get the name of the class associated with the provided Metaschema instance.
   *
   * @param instance
   *          the Metaschema instance to get the class name for
   * @return the class name information for the Module module
   */
  @NonNull
  ClassName getClassName(IChoiceGroupInstance instance);

  /**
   * Get the name of the class associated with the provided Metaschema module.
   *
   * @param module
   *          the Metaschema module to get the class name for
   * @return the class name information for the Module module
   */
  @NonNull
  ClassName getClassName(@NonNull IModule module);

  /**
   * Get the name of the class associated with the provided Metaschema definition.
   *
   * @param definition
   *          the Metaschema definition to get the class name for
   * @return the class name information for the definition
   */
  @NonNull
  ClassName getClassName(@NonNull IModelDefinition definition);

  /**
   * Get the name of the class associated with the provided Metaschema definition.
   *
   * @param typeInfo
   *          the type information to get the class name for
   * @return the class name information for the definition
   */
  @NonNull
  ClassName getClassName(@NonNull INamedModelInstanceTypeInfo typeInfo);

  /**
   * Get the name of the super interfaces associated with the provided Metaschema
   * definition.
   *
   * @param definition
   *          the Metaschema definition to get the super ineterfaces for
   * @return the super interface information for the definition
   */
  @NonNull
  List<ClassName> getSuperinterfaces(@NonNull IModelDefinition definition);

  /**
   * Get the name of the class associated with the provided Metaschema definition
   * using the provided {@code postfix}. This class will be a child of the
   * provided parent class.
   *
   * @param parentClass
   *          the containing class
   * @param suggestedClassName
   *          the name to derive the subclass name from
   * @param definition
   *          the Metaschema definition to get the class name for
   * @return the class name information for the definition
   */
  @NonNull
  ClassName getSubclassName(
      @NonNull ClassName parentClass,
      @NonNull String suggestedClassName,
      @NonNull IModelDefinition definition);

  /**
   * Get the name of the base class to use for the class associated with the
   * provided Metaschema definition.
   *
   * @param definition
   *          a definition that may be built as a class
   * @return the name of the base class or {@code null} if no base class is to be
   *         used
   */
  @Nullable
  ClassName getBaseClassName(@NonNull IModelDefinition definition);

  /**
   * Get the Java package name to use for the provided Module module.
   *
   * @param module
   *          the Module module
   * @return the Java package name
   */
  @NonNull
  String getPackageName(@NonNull IModule module);

  /**
   * Get a unique property name for use within the given parent definition.
   *
   * @param parent
   *          the type information for the parent definition
   * @param name
   *          the base name to derive the property name from
   * @return a unique property name suitable for use in the generated class
   */
  @NonNull
  String getPropertyName(@NonNull IDefinitionTypeInfo parent, @NonNull String name);
}
