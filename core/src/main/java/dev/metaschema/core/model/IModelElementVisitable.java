/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a model element that can be visited using the visitor pattern.
 * <p>
 * This interface supports traversal of model elements using implementations of
 * {@link IModelElementVisitor}.
 */
@FunctionalInterface
public interface IModelElementVisitable {
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
  <CONTEXT, RESULT> RESULT accept(@NonNull IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context);
}
