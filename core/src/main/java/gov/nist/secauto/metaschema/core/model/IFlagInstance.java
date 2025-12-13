/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a flag instance within a field or assembly definition.
 * <p>
 * A flag instance references a flag definition and specifies how that flag is
 * used within its containing definition, including whether the flag is
 * required.
 */
public interface IFlagInstance extends IFlag, IValuedInstance, IInstanceAbsolute {

  /**
   * The default value for whether a flag is required.
   */
  boolean DEFAULT_FLAG_REQUIRED = false;

  /**
   * Retrieves the parent container that contains this flag instance.
   *
   * @return the parent model definition
   */
  @Override
  IModelDefinition getParentContainer();

  /**
   * Retrieves the flag definition referenced by this instance.
   *
   * @return the flag definition
   */
  @Override
  IFlagDefinition getDefinition();

  @Override
  default IModelDefinition getContainingDefinition() {
    return getParentContainer();
  }

  /**
   * Determines if a flag value is required to be provided.
   *
   * @return {@code true} if a value is required, or {@code false} otherwise
   * @see #DEFAULT_FLAG_REQUIRED
   */
  default boolean isRequired() {
    return DEFAULT_FLAG_REQUIRED;
  }

  /**
   * A visitor callback.
   *
   * @param <CONTEXT>
   *          the type of the context parameter
   * @param <RESULT>
   *          the type of the visitor result
   * @param visitor
   *          the calling visitor
   * @param context
   *          a parameter used to pass contextual information between visitors
   * @return the visitor result
   */
  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitFlagInstance(this, context);
  }
}
