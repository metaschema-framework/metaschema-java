/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IModelInstanceTypeInfo extends IInstanceTypeInfo {

  @Override
  @NonNull
  IAssemblyDefinitionTypeInfo getParentTypeInfo();

  @Override
  IModelInstanceAbsolute getInstance();

  /**
   * Generate the Metaschema binding annotation for this instance.
   * <p>
   * This method uses a builder so child classes can add to the builder as needed.
   *
   * @param typeBuilder
   *          the class builder the field is on
   * @param fieldBuilder
   *          the field builder the annotation is on
   * @param annotation
   *          if not {@code null} will seed the annotation to build
   *
   * @return a builder for the annotation
   */
  @NonNull
  Set<IModelDefinition> buildBindingAnnotation(
      @NonNull TypeSpec.Builder typeBuilder,
      @NonNull FieldSpec.Builder fieldBuilder,
      @NonNull AnnotationSpec.Builder annotation);
}
