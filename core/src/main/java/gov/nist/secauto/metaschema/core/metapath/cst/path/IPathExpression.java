/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

public interface IPathExpression<RESULT_TYPE extends IItem> extends IExpression {
  @Override
  Class<RESULT_TYPE> getBaseResultType();

  @Override
  Class<? extends RESULT_TYPE> getStaticResultType();
}
