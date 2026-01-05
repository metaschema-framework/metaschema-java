/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IBooleanItem;
import dev.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

class FnFalseTest
    extends FunctionTestBase {

  @Test
  void test() {
    assertFunctionResult(
        FnFalse.SIGNATURE,
        ISequence.of(IBooleanItem.FALSE),
        CollectionUtil.emptyList());
  }
}
