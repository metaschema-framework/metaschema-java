/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;

import edu.umd.cs.findbugs.annotations.NonNull;

public class AssemblyInstanceTypeInfoImpl
    extends AbstractNamedModelInstanceTypeInfo<IAssemblyInstanceAbsolute>
    implements IAssemblyInstanceTypeInfo {

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
