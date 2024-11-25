/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ILiteralExpression<RESULT_TYPE extends IAnyAtomicItem, VALUE> extends IExpression {
  /**
   * Get the literal value.
   *
   * @return the value
   */
  @NonNull
  VALUE getValue();

  @Override
  Class<RESULT_TYPE> getBaseResultType();

  @Override
  default Class<RESULT_TYPE> getStaticResultType() {
    return getBaseResultType();
  }

  @SuppressWarnings("null")
  @Override
  default List<? extends IExpression> getChildren() {
    // a literal never has children
    return Collections.emptyList();
  }
}
