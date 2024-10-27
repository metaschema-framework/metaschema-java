/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.Property;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.Remarks;

import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents constraint metadata that is common to all constraints.
 */
public interface IConstraintBase {

  /**
   * Get the assigned identifier for the constraint.
   * <p>
   * This identifier is useful for to support references to the constraint.
   *
   * @return the identifier or {@code null} if no identifier is assigned
   */
  @Nullable
  String getId();

  /**
   * Get the assigned formal name for the constraint.
   * <p>
   * This name is useful for understanding the intent of the constraint.
   *
   * @return the formal name or {@code null} if no formal name is assigned
   */
  @Nullable
  String getFormalName();

  /**
   * Get a description of what the constraint does.
   * <p>
   * This description is useful for understanding the intent of the constraint.
   *
   * @return the description or {@code null} if no description is provided
   */
  @Nullable
  MarkupLine getDescription();

  /**
   * Get an optional collection of properties assigned to the constraint.
   * <p>
   * A property provides a means to assign various characteristics to a
   * constraint.
   *
   * @return the properties or {@code null} if no properties are provided
   */
  @Nullable
  List<Property> getProps();

  /**
   * Get the optional remarks that provide additional details explanation the
   * intent or use of the constraint.
   * <p>
   * Use of remarks can provide additional insights into why a constraint exists
   * or why the constraint was implemented in this way.
   *
   * @return the remarks or {@code null} if no remarks are provided
   */
  @Nullable
  Remarks getRemarks();

  /**
   * Get the requested level to report if the constraint is not satisfied.
   * <p>
   * If not provided, then the default level provided by
   * {@link IConstraint#DEFAULT_LEVEL} is used.
   *
   * @return the level or {@code null} if the default level is to be used
   */
  @Nullable
  String getLevel();
}
