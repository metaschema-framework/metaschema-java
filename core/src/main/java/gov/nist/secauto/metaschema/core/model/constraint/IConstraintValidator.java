/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This interface provides an entry point for performing validations over
 * Metapath items associated with a Metaschema model.
 * <p>
 * Implementations may hold resources such as thread pools that must be released
 * when validation is complete. Callers should use try-with-resources or
 * explicitly call {@link #close()} when done with the validator.
 */
public interface IConstraintValidator extends AutoCloseable {
  /**
   * Validate the provided item against any associated constraints.
   *
   * @param item
   *          the item to validate
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @throws ConstraintValidationException
   *           if a constraint violation is detected
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a
   *           constraint
   */
  void validate(
      @NonNull INodeItem item,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException;

  /**
   * Complete any validations that require full analysis of the content model.
   *
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @throws ConstraintValidationException
   *           if a constraint violation is detected during finalization
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a
   *           constraint
   */
  void finalizeValidation(@NonNull DynamicContext dynamicContext) throws ConstraintValidationException;

  /**
   * Release any resources held by this validator.
   * <p>
   * This method should be called when the validator is no longer needed to
   * release resources such as thread pools. For validators using sequential
   * execution, this method does nothing.
   */
  @Override
  void close();
}
