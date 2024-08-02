/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This marker interface is used to indicate the implementation class is a
 * provider of constraints.
 */
public interface IConstrained {
  /**
   * Retrieve the ordered collection of constraints.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IConstraint> getConstraints();
}
