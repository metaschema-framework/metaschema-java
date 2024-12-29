/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.node.INodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This interface provides an entry point for performing validations over
 * Metapath items associated with a Metaschema model.
 */
public interface IConstraintValidator {
  /**
   * Validate the provided item against any associated constraints.
   *
   * @param item
   *          the item to validate
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a
   *           constraint
   */
  void validate(
      @NonNull INodeItem item,
      @NonNull DynamicContext dynamicContext);

  /**
   * Complete any validations that require full analysis of the content model.
   *
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a
   *           constraint
   */
  void finalizeValidation(@NonNull DynamicContext dynamicContext);
}
