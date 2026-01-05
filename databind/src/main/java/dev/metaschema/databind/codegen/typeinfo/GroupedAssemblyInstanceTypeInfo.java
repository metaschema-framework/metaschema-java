/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.databind.model.annotations.BoundGroupedAssembly;

import java.lang.annotation.Annotation;

import edu.umd.cs.findbugs.annotations.NonNull;

public class GroupedAssemblyInstanceTypeInfo
    extends AbstractGroupedNamedModelInstanceTypeInfo<IAssemblyInstanceGrouped>
    implements IGroupedAssemblyInstanceTypeInfo {

  /**
   * Constructs a new type information object for a grouped assembly instance.
   *
   * @param modelInstance
   *          the grouped assembly instance
   * @param choiceGroupTypeInfo
   *          the type information for the parent choice group containing this
   *          instance
   */
  public GroupedAssemblyInstanceTypeInfo(
      @NonNull IAssemblyInstanceGrouped modelInstance,
      @NonNull IChoiceGroupTypeInfo choiceGroupTypeInfo) {
    super(modelInstance, choiceGroupTypeInfo);
  }

  @Override
  protected Class<? extends Annotation> getBindingAnnotation() {
    return BoundGroupedAssembly.class;
  }

  @Override
  protected void applyInstanceAnnotation(
      @NonNull AnnotationSpec.Builder instanceAnnotation,
      @NonNull AnnotationSpec.Builder choiceGroupAnnotation) {
    choiceGroupAnnotation.addMember("assemblies", "$L", instanceAnnotation.build());
  }
}
