/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractMetapathExpression implements IMetapathExpression {

  @NonNull
  private final String path;
  @NonNull
  private final StaticContext staticContext;

  public AbstractMetapathExpression(
      @NonNull String path,
      @NonNull StaticContext context) {
    this.path = path;
    this.staticContext = context;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public StaticContext getStaticContext() {
    return staticContext;
  }
}
