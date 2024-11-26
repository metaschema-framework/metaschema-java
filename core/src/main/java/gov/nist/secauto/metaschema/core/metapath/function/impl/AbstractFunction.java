/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

abstract class AbstractFunction implements IFunction {
  @NonNull
  private final IEnhancedQName qname;
  @NonNull
  private final List<IArgument> arguments;

  protected AbstractFunction(
      @NonNull String name,
      @NonNull String namespace,
      @NonNull List<IArgument> arguments) {
    this(IEnhancedQName.of(namespace, name), arguments);
  }

  protected AbstractFunction(
      @NonNull IEnhancedQName qname,
      @NonNull List<IArgument> arguments) {
    this.qname = qname;
    this.arguments = arguments;
  }

  @Override
  public IEnhancedQName getQName() {
    return qname;
  }

  @Override
  public int arity() {
    return arguments.size();
  }

  @Override
  public List<IArgument> getArguments() {
    return arguments;
  }
}
