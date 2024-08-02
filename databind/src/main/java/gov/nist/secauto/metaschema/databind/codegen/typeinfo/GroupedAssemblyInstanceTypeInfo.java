/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;

import java.lang.annotation.Annotation;

import edu.umd.cs.findbugs.annotations.NonNull;

public class GroupedAssemblyInstanceTypeInfo
    extends AbstractGroupedNamedModelInstanceTypeInfo<IAssemblyInstanceGrouped>
    implements IGroupedAssemblyInstanceTypeInfo {

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
