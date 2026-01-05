/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an assembly instance that appears directly within an assembly
 * definition.
 * <p>
 * An absolute assembly instance is not part of a choice or other grouping
 * construct, and has its own distinct cardinality settings.
 */
public interface IAssemblyInstanceAbsolute extends IAssemblyInstance, INamedModelInstanceAbsolute {

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
    return visitor.visitAssemblyInstance(this, context);
  }
}
