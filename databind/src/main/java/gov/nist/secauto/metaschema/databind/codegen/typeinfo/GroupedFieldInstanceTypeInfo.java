/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedField;

import java.lang.annotation.Annotation;

import edu.umd.cs.findbugs.annotations.NonNull;

public class GroupedFieldInstanceTypeInfo
    extends AbstractGroupedNamedModelInstanceTypeInfo<IFieldInstanceGrouped>
    implements IGroupedFieldInstanceTypeInfo {

  public GroupedFieldInstanceTypeInfo(
      @NonNull IFieldInstanceGrouped modelInstance,
      @NonNull IChoiceGroupTypeInfo choiceGroupTypeInfo) {
    super(modelInstance, choiceGroupTypeInfo);
  }

  @Override
  protected Class<? extends Annotation> getBindingAnnotation() {
    return BoundGroupedField.class;
  }

  @Override
  protected void applyInstanceAnnotation(
      @NonNull AnnotationSpec.Builder instanceAnnotation,
      @NonNull AnnotationSpec.Builder choiceGroupAnnotation) {
    choiceGroupAnnotation.addMember("fields", "$L", instanceAnnotation.build());
  }
}
