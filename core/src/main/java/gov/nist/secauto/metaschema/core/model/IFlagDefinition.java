/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a flag definition in a Metaschema module.
 * <p>
 * A flag is a simple named data value that can be associated with a field or
 * assembly. Flag definitions define the data type and constraints for flag
 * values.
 */
public interface IFlagDefinition extends IValuedDefinition, IFlag {
  @Override
  IFlagInstance getInlineInstance();

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
    return visitor.visitFlagDefinition(this, context);
  }
}
