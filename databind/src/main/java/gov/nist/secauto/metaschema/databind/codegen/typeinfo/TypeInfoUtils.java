/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class TypeInfoUtils {
  private TypeInfoUtils() {
    // disable construction
  }

  public static void buildCommonBindingAnnotationValues(
      @NonNull INamedModelInstance instance,
      @NonNull AnnotationSpec.Builder annotation) {

    String formalName = instance.getEffectiveFormalName();
    if (formalName != null) {
      annotation.addMember("formalName", "$S", formalName);
    }

    MarkupLine description = instance.getEffectiveDescription();
    if (description != null) {
      annotation.addMember("description", "$S", description.toMarkdown());
    }

    annotation.addMember("useName", "$S", instance.getEffectiveName());

    Integer index = instance.getEffectiveIndex();
    if (index != null) {
      annotation.addMember("useIndex", "$L", index);
    }

    // TODO: handle instance namespace as a prefix

    MarkupMultiline remarks = instance.getRemarks();
    if (remarks != null) {
      annotation.addMember("remarks", "$S", remarks.toMarkdown());
    }
  }
}
