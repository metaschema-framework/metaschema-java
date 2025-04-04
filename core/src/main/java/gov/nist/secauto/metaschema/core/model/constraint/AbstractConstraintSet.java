/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.ISource;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A common implementation for a set of constraints targeted at the contents of
 * a Metaschema module.
 */
public abstract class AbstractConstraintSet implements IConstraintSet {
  @NonNull
  private final ISource source;

  /**
   * Construct a new constraint set.
   *
   * @param source
   *          the constraint source information
   */
  public AbstractConstraintSet(@NonNull ISource source) {
    this.source = source;
  }

  @Override
  public ISource getSource() {
    return source;
  }
}
