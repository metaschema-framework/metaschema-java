/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

class FnTrueTest
    extends FunctionTestBase {

  @Test
  void test() {
    assertFunctionResult(
        FnTrue.SIGNATURE,
        ISequence.of(IBooleanItem.TRUE),
        CollectionUtil.emptyList());
  }
}
