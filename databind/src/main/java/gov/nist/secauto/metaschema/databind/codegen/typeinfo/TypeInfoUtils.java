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

  /**
   * Convert the first character of a string to lowercase.
   *
   * <p>
   * This is useful for forming Javadoc sentences where a description needs to
   * flow naturally after a prefix like "Get the" or "Set the".
   *
   * @param text
   *          the text to convert
   * @return the text with the first character lowercased, or the original text if
   *         empty or already lowercase
   */
  @NonNull
  public static String toLowerFirstChar(@NonNull String text) {
    if (text.isEmpty() || Character.isLowerCase(text.charAt(0))) {
      return text;
    }
    return Character.toLowerCase(text.charAt(0)) + text.substring(1);
  }

  /**
   * Builds common binding annotation values for a named model instance.
   * <p>
   * This method populates the annotation builder with common attributes such as
   * formal name, description, use name, use index, and remarks.
   *
   * @param instance
   *          the named model instance to extract values from
   * @param annotation
   *          the annotation builder to populate with values
   */
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
