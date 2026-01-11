/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import dev.metaschema.databind.model.annotations.BoundAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;

public class AssemblyInstanceTypeInfoImpl
    extends AbstractNamedModelInstanceTypeInfo<IAssemblyInstanceAbsolute>
    implements IAssemblyInstanceTypeInfo {

  /**
   * Constructs a new type information object for an assembly instance.
   *
   * @param instance
   *          the assembly instance
   * @param parentDefinition
   *          the type information for the parent assembly definition containing
   *          this instance
   */
  public AssemblyInstanceTypeInfoImpl(
      @NonNull IAssemblyInstanceAbsolute instance,
      @NonNull IAssemblyDefinitionTypeInfo parentDefinition) {
    super(instance, parentDefinition);
  }

  @Override
  protected AnnotationSpec.Builder newBindingAnnotation() {
    return ObjectUtils.notNull(AnnotationSpec.builder(BoundAssembly.class));
  }

  // @Override
  // public AnnotationSpec.Builder buildBindingAnnotation() {
  // AnnotationSpec.Builder annotation = super.buildBindingAnnotation();
  //
  // IAssemblyInstance instance = getInstance();
  //
  // // IAssemblyDefinition definition = instance.getDefinition();
  // // if (definition.isInline()) {
  // // AnnotationGenerator.buildValueConstraints(annotation, definition);
  // // AnnotationGenerator.buildAssemblyConstraints(annotation, definition);
  // // }
  //
  // return annotation;
  // }
}
