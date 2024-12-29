/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IItem;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ErroniousMetapathExpression
    extends AbstractMetapathExpression {
  @NonNull
  private final MetapathException throwable;

  public ErroniousMetapathExpression(
      @NonNull String path,
      @NonNull StaticContext staticContext,
      @NonNull MetapathException throwable) {
    super(path, staticContext);
    this.throwable = throwable;
  }

  @Override
  public <T extends IItem> ISequence<T> evaluate(IItem focus, DynamicContext dynamicContext) {
    throw throwable;
  }
}
