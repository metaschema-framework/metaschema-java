/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.ISource;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractConstraintSet implements IConstraintSet {
  @NonNull
  private final ISource source;

  public AbstractConstraintSet(@NonNull ISource source) {
    this.source = source;
  }

  @Override
  public ISource getSource() {
    return source;
  }
}
