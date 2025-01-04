/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;

/**
 * A common interface for all expressions that produce a boolean result.
 * <p>
 * This interface provides default implementations for result type methods that
 * consistently return {@link IBooleanItem} as both the base and static result
 * type.
 *
 * @since 1.0.0
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
