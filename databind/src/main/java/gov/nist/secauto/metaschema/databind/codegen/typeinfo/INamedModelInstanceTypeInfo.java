/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface INamedModelInstanceTypeInfo extends IModelInstanceTypeInfo {
  @Override
  INamedModelInstanceAbsolute getInstance();

  /**
   * Set the choice ID for this instance.
   * <p>
   * This should be called when the instance is part of a Metaschema choice to
   * associate it with its choice group.
   *
   * @param choiceId
   *          the choice ID to set, or {@code null} to clear it
   */
  void setChoiceId(@Nullable String choiceId);

  /**
   * Generate annotation values that are common to all named model instances.
   *
   * @param annotation
   *          the annotation builder.
   */
  default void buildBindingAnnotationCommon(@NonNull AnnotationSpec.Builder annotation) {
    TypeInfoUtils.buildCommonBindingAnnotationValues(getInstance(), annotation);
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * This implementation adds the effective description from the named model
   * instance as the field's Javadoc content.
   */
  @Override
  default void buildFieldJavadoc(@NonNull FieldSpec.Builder builder) {
    MarkupLine description = getInstance().getEffectiveDescription();
    if (description != null) {
      builder.addJavadoc("$L\n", description.toHtml());
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * This implementation generates getter Javadoc using the instance's formal name
   * (if available) or property name, adds the effective description, and includes
   * an appropriate {@code @return} tag based on whether the property is required
   * or a collection.
   */
  @Override
  default void buildGetterJavadoc(@NonNull MethodSpec.Builder builder) {
    MarkupLine description = getInstance().getEffectiveDescription();
    String formalName = getInstance().getEffectiveFormalName();
    String propertyName = getInstance().getEffectiveName();

    // Use formal name if available, otherwise property name
    if (formalName != null) {
      builder.addJavadoc("Get the $L.\n", TypeInfoUtils.toLowerFirstChar(formalName));
    } else {
      builder.addJavadoc("Get the {@code $L} property.\n", propertyName);
    }

    // Add description as a second paragraph if available
    if (description != null) {
      builder.addJavadoc("\n");
      builder.addJavadoc("<p>\n");
      builder.addJavadoc("$L\n", description.toHtml());
    }

    builder.addJavadoc("\n");
    // Collections are always @NonNull (lazy initialized), required singles are
    // @NonNull
    if (isRequired() || isCollectionType()) {
      builder.addJavadoc("@return the $L value\n", propertyName);
    } else {
      builder.addJavadoc("@return the $L value, or {@code null} if not set\n", propertyName);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * This implementation generates setter Javadoc using the instance's formal name
   * (if available) or property name, adds the effective description, and includes
   * a {@code @param} tag for the value parameter.
   */
  @Override
  default void buildSetterJavadoc(@NonNull MethodSpec.Builder builder, @NonNull String paramName) {
    MarkupLine description = getInstance().getEffectiveDescription();
    String formalName = getInstance().getEffectiveFormalName();
    String propertyName = getInstance().getEffectiveName();

    // Use formal name if available, otherwise property name
    if (formalName != null) {
      builder.addJavadoc("Set the $L.\n", TypeInfoUtils.toLowerFirstChar(formalName));
    } else {
      builder.addJavadoc("Set the {@code $L} property.\n", propertyName);
    }

    // Add description as a second paragraph if available
    if (description != null) {
      builder.addJavadoc("\n");
      builder.addJavadoc("<p>\n");
      builder.addJavadoc("$L\n", description.toHtml());
    }

    builder.addJavadoc("\n");
    builder.addJavadoc("@param $L\n", paramName);
    // Collections and required properties require non-null values;
    // optional properties can be set to null to clear
    if (isRequired() || isCollectionType()) {
      builder.addJavadoc("          the $L value to set\n", propertyName);
    } else {
      builder.addJavadoc("          the $L value to set, or {@code null} to clear\n", propertyName);
    }
  }
}
