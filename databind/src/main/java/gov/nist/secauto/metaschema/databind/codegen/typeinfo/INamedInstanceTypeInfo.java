/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.FieldSpec;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.INamedInstance;

public interface INamedInstanceTypeInfo extends IInstanceTypeInfo {
  @Override
  INamedInstance getInstance();

  @Override
  default void buildFieldJavadoc(FieldSpec.Builder builder) {
    MarkupLine description = getInstance().getEffectiveDescription();
    if (description != null) {
      builder.addJavadoc("$S", description.toHtml());
    }
  }
}
