/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a set of target constraints that apply to a given Metaschema
 * module namespace and short name.
 */
public interface IScopedContraints {
  /**
   * The Metaschema module namespace the constraints apply to.
   *
   * @return the namespace
   */
  @NonNull
  URI getModuleNamespace();

  /**
   * The Metaschema module short name the constraints apply to.
   *
   * @return the short name
   */
  @NonNull
  String getModuleShortName();

  /**
   * The collection of target constraints.
   *
   * @return the constraints
   */
  @NonNull
  List<ITargetedConstraints> getTargetedContraints();
}
