/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides type information for a grouped named model instance within a choice
 * group.
 * <p>
 * This functional interface is used during code generation to produce the
 * appropriate annotations and type definitions for members of a choice group.
 */
@FunctionalInterface
public interface IGroupedNamedModelInstanceTypeInfo {
  /**
   * Generates the member annotation for this grouped instance within the choice
   * group.
   *
   * @param choiceGroupAnnotation
   *          the annotation builder for the parent choice group
   * @param typeBuilder
   *          the type builder for adding any nested type definitions
   * @param requireExtension
   *          {@code true} if the generated type must extend a base class,
   *          {@code false} otherwise
   * @return a set of model definitions that need to be generated as separate
   *         classes
   */
  @NonNull
  Set<IModelDefinition> generateMemberAnnotation(
      @NonNull AnnotationSpec.Builder choiceGroupAnnotation,
      @NonNull TypeSpec.Builder typeBuilder,
      boolean requireExtension);
}
