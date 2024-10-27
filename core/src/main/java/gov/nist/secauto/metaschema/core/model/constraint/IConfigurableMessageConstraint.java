/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a constraint that allows a configurable message.
 *
 * @since 2.0.0
 */
public interface IConfigurableMessageConstraint extends IConstraint {

  /**
   * A message to emit when the constraint is violated. Allows embedded Metapath
   * expressions using the syntax {@code \{ metapath \}}.
   *
   * @return the message if defined or {@code null} otherwise
   */
  @Nullable
  String getMessage();

  /**
   * Generate a violation message using the provide item and dynamic context for
   * inline Metapath value insertion.
   *
   * @param item
   *          the target Metapath item to use as the focus for Metapath evaluation
   * @param context
   *          the dynamic context for Metapath evaluation
   * @return the message
   * @throws IllegalStateException
   *           if a custom message is not defined, which will occur if this method
   *           is called while {@link #getMessage()} returns {@code null}
   */
  @NonNull
  String generateMessage(@NonNull INodeItem item, @NonNull DynamicContext context);
}
