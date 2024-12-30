/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;

/**
 * A common interface for all expressions that produce a boolean result.
 */
public interface IBooleanLogicExpression extends IExpression {
  @Override
  default Class<IBooleanItem> getBaseResultType() {
    return IBooleanItem.class;
  }

  @Override
  default Class<IBooleanItem> getStaticResultType() {
    return getBaseResultType();
  }
}
