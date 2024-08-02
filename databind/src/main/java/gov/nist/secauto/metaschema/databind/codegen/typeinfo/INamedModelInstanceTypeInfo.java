/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface INamedModelInstanceTypeInfo extends IModelInstanceTypeInfo {
  @Override
  INamedModelInstanceAbsolute getInstance();

  /**
   * Generate annotation values that are common to all named model instances.
   *
   * @param annotation
   *          the annotation builder.
   */
  default void buildBindingAnnotationCommon(@NonNull AnnotationSpec.Builder annotation) {
    TypeInfoUtils.buildCommonBindingAnnotationValues(getInstance(), annotation);
  }
}
