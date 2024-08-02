/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.model.constraint.ILet;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultLet implements ILet {
  @NonNull
  private final QName name;
  @NonNull
  private final MetapathExpression valueExpression;
  @NonNull
  private final ISource source;

  /**
   * Construct a new let statement.
   *
   * @param name
   *          the variable name
   * @param metapath
   *          the Metapath expression used to query the value
   * @param source
   *          the source of the let statement
   */
  public DefaultLet(
      @NonNull QName name,
      @NonNull MetapathExpression metapath,
      @NonNull ISource source) {
    this.name = name;
    this.valueExpression = metapath;
    this.source = source;
  }

  @Override
  public QName getName() {
    return name;
  }

  @Override
  public MetapathExpression getValueExpression() {
    return valueExpression;
  }

  @Override
  @NonNull
  public ISource getSource() {
    return source;
  }
}
