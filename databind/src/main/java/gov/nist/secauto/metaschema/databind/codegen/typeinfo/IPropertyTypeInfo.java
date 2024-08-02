/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IPropertyTypeInfo extends ITypeInfo {
  /**
   * Generate the Java field associated with this property.
   *
   * @param builder
   *          the containing class builder
   * @return the set of definitions used by this field
   */
  Set<? extends IModelDefinition> build(@NonNull TypeSpec.Builder builder);

  /**
   * Get the Javadoc description for the current property.
   *
   * @param builder
   *          the field builder to annotate with the Javadoc
   */
  default void buildFieldJavadoc(@NonNull FieldSpec.Builder builder) {
    // do nothing by default
  }
}
