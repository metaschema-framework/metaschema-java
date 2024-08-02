/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

abstract class AbstractFunction implements IFunction {
  @NonNull
  private final QName qname;
  @NonNull
  private final List<IArgument> arguments;

  protected AbstractFunction(
      @NonNull String name,
      @NonNull String namespace,
      @NonNull List<IArgument> arguments) {
    this(new QName(namespace, name), arguments);
  }

  protected AbstractFunction(
      @NonNull QName qname,
      @NonNull List<IArgument> arguments) {
    this.qname = qname;
    this.arguments = arguments;
  }

  @Override
  public QName getQName() {
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
