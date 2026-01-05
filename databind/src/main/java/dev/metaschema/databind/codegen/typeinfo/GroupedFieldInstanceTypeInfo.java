/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import dev.metaschema.core.model.IFieldInstanceGrouped;
import dev.metaschema.databind.model.annotations.BoundGroupedField;

import java.lang.annotation.Annotation;

import edu.umd.cs.findbugs.annotations.NonNull;

public class GroupedFieldInstanceTypeInfo
    extends AbstractGroupedNamedModelInstanceTypeInfo<IFieldInstanceGrouped>
    implements IGroupedFieldInstanceTypeInfo {

  /**
   * Constructs a new type information object for a grouped field instance.
   *
   * @param modelInstance
   *          the grouped field instance
   * @param choiceGroupTypeInfo
   *          the type information for the parent choice group containing this
   *          instance
   */
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
