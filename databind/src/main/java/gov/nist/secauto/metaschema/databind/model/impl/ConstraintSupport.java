/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.model.constraint.IModelConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IValueConstrained;
import gov.nist.secauto.metaschema.databind.model.annotations.AssemblyConstraints;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
      try {
        Arrays.stream(valueAnnotation.lets())
            .map(annotation -> ConstraintFactory.newLetExpression(annotation, source))
            .forEachOrdered(let -> set.addLetExpression(let));
        Arrays.stream(valueAnnotation.allowedValues())
            .map(annotation -> ConstraintFactory.newAllowedValuesConstraint(annotation, source))
            .forEachOrdered(constraint -> set.addConstraint(constraint));
        Arrays.stream(valueAnnotation.matches())
            .map(annotation -> ConstraintFactory.newMatchesConstraint(annotation, source))
            .forEachOrdered(constraint -> set.addConstraint(constraint));
        Arrays.stream(valueAnnotation.indexHasKey())
            .map(annotation -> ConstraintFactory.newIndexHasKeyConstraint(annotation, source))
            .forEachOrdered(constraint -> set.addConstraint(constraint));
        Arrays.stream(valueAnnotation.expect())
            .map(annotation -> ConstraintFactory.newExpectConstraint(annotation, source))
            .forEachOrdered(constraint -> set.addConstraint(constraint));
      } catch (MetapathException ex) {
        throw new MetapathException(
            String.format("Unable to compile a Metapath in '%s'. %s", source.getSource(), ex.getLocalizedMessage()),
            ex);
      }
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
      try {
        Arrays.stream(assemblyAnnotation.index())
            .map(annotation -> ConstraintFactory.newIndexConstraint(annotation, source))
            .forEachOrdered(constraint -> set.addConstraint(constraint));

        Arrays.stream(assemblyAnnotation.unique())
            .map(annotation -> ConstraintFactory.newUniqueConstraint(annotation, source))
            .forEachOrdered(constraint -> set.addConstraint(constraint));

        Arrays.stream(assemblyAnnotation.cardinality())
            .map(annotation -> ConstraintFactory.newCardinalityConstraint(annotation, source))
            .forEachOrdered(constraint -> set.addConstraint(constraint));
      } catch (MetapathException ex) {
        throw new MetapathException(
            String.format("Unable to compile a Metapath in '%s'. %s", source.getSource(), ex.getLocalizedMessage()),
            ex);
      }
    }
  }
}