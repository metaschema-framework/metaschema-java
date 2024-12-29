/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import gov.nist.secauto.metaschema.core.metapath.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;

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
