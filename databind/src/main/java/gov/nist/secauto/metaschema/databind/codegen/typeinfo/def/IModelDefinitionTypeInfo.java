/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo.def;

import com.squareup.javapoet.ClassName;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.IFlagInstanceTypeInfo;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.ITypeResolver;

import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IModelDefinitionTypeInfo extends IDefinitionTypeInfo {
  /**
   * Construct a new type information object for the provided {@code definition}.
   *
   * @param definition
   *          the definition to provide type information for
   * @param typeResolver
   *          use to resolve type information for composite instances
   * @return the type information
   */
  @NonNull
  static IModelDefinitionTypeInfo newTypeInfo(
      @NonNull IModelDefinition definition,
      @NonNull ITypeResolver typeResolver) {
    IModelDefinitionTypeInfo retval;
    switch (definition.getModelType()) {
    case ASSEMBLY:
      retval = IAssemblyDefinitionTypeInfo.newTypeInfo((IAssemblyDefinition) definition, typeResolver);
      break;
    case FIELD:
      retval = IFieldDefinitionTypeInfo.newTypeInfo((IFieldDefinition) definition, typeResolver);
      break;
    default:
      throw new UnsupportedOperationException(
          String.format("Generation of child classes for %s definitions is unsupported",
              definition.getModelType().name()));
    }
    return retval;
  }

  @Override
  IModelDefinition getDefinition();

  /**
   * Get the class type information for the base class of the generated class, .
   *
   * @return the type information or {@code null} if no base class is configured
   */
  @Nullable
  ClassName getBaseClassName();

  /**
   * Gets the class type information for the object definition for which this
   * class is being generated.
   *
   * @return the class's type information
   */
  @NonNull
  ClassName getClassName();

  /**
   * Get the list of super interfaces the class must implement for the object
   * definition for which this class is being generated.
   *
   * @return a list of super interfaces to implement
   */
  @NonNull
  List<ClassName> getSuperinterfaces();

  /**
   * Get the type information for the provided {@code instance} value.
   *
   * @param instance
   *          the instance to get type information for
   * @return the type information
   */
  @Nullable
  IFlagInstanceTypeInfo getFlagInstanceTypeInfo(@NonNull IFlagInstance instance);

  /**
   * Get the type information for all flag instance values on this definition.
   *
   * @return the type information
   */
  @NonNull
  Collection<IFlagInstanceTypeInfo> getFlagInstanceTypeInfos();
  //
  // /**
  // * Generates the associated Java class and saves it using the provided file.
  // *
  // * @param dir
  // * the directory to generate the class in
  // * @return the qualified class name for the generated class
  // * @throws IOException
  // * if a build error occurred while generating the class
  // */
  // @NonNull
  // IGeneratedDefinitionClass generateClass(@NonNull Path dir) throws
  // IOException;
  //
  // /**
  // * This method is responsible for generating the Java class using a builder
  // that
  // * is returned for further customization.
  // *
  // * @return the class definition for the generated class
  // * @throws IOException
  // * if a build error occurred while generating the class
  // */
  // @NonNull
  // TypeSpec generateChildClass() throws IOException;
}
