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

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a container of rules constraining the effective model of a
 * Metaschema field or flag data instance.
 */
public interface IValueConstrained extends IConstrained {
  /**
   * Get the collection of let expressions, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  Map<QName, ILet> getLetExpressions();

  /**
   * Get the collection of allowed value constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IAllowedValuesConstraint> getAllowedValuesConstraints();

  /**
   * Get the collection of matches constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IMatchesConstraint> getMatchesConstraints();

  /**
   * Get the collection of index key reference constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints();

  /**
   * Get the collection of expect constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IExpectConstraint> getExpectConstraints();

  /**
   * Add a new let expression.
   *
   * @param let
   *          the let statement to add
   * @return the original let with the same name or {@code null} if no let existed
   *         with the same name
   */
  ILet addLetExpression(@NonNull ILet let);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IAllowedValuesConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IMatchesConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IIndexHasKeyConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IExpectConstraint constraint);
}
