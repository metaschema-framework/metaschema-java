/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IModelConstrained;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.databind.model.annotations.AssemblyConstraints;
import dev.metaschema.databind.model.annotations.ValueConstraints;

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Support class for managing constraints on bound definitions.
 * <p>
 * This class provides utilities for processing and applying constraints defined
 * through binding annotations.
 */
public final class ConstraintSupport {
  private ConstraintSupport() {
    // disable construction
  }

  /**
   * Generate constraints from a {@link ValueConstraints} annotation on a valued
   * object (i.e., fields and flags).
   *
   * @param valueAnnotation
   *          the annotation where the constraints are defined
   * @param source
   *          information about the source of the constraint
   * @param set
   *          the constraint set to parse the constraints into
   */
  @SuppressWarnings("null")
  public static void parse( // NOPMD - intentional
      @Nullable ValueConstraints valueAnnotation,
      @NonNull ISource source,
      @NonNull IValueConstrained set) {
    if (valueAnnotation != null) {
      Arrays.stream(valueAnnotation.lets())
          .map(annotation -> ConstraintFactory.newLetExpression(annotation, source))
          .forEachOrdered(set::addLetExpression);
      Arrays.stream(valueAnnotation.allowedValues())
          .map(annotation -> ConstraintFactory.newAllowedValuesConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);
      Arrays.stream(valueAnnotation.matches())
          .map(annotation -> ConstraintFactory.newMatchesConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);
      Arrays.stream(valueAnnotation.indexHasKey())
          .map(annotation -> ConstraintFactory.newIndexHasKeyConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);
      Arrays.stream(valueAnnotation.expect())
          .map(annotation -> ConstraintFactory.newExpectConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);
      Arrays.stream(valueAnnotation.report())
          .map(annotation -> ConstraintFactory.newReportConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);
    }
  }

  /**
   * Generate constraints from a {@link ValueConstraints} annotation on a valued
   * object (i.e., fields and flags).
   *
   * @param assemblyAnnotation
   *          the annotation where the constraints are defined
   * @param source
   *          information about the source of the constraint
   * @param set
   *          the constraint set to parse the constraints into
   */
  @SuppressWarnings("null")
  public static void parse( // NOPMD - intentional
      @Nullable AssemblyConstraints assemblyAnnotation,
      @NonNull ISource source,
      @NonNull IModelConstrained set) {
    if (assemblyAnnotation != null) {
      Arrays.stream(assemblyAnnotation.index())
          .map(annotation -> ConstraintFactory.newIndexConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);

      Arrays.stream(assemblyAnnotation.unique())
          .map(annotation -> ConstraintFactory.newUniqueConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);

      Arrays.stream(assemblyAnnotation.cardinality())
          .map(annotation -> ConstraintFactory.newCardinalityConstraint(annotation, source))
          .forEachOrdered(set::addConstraint);
    }
  }
}
