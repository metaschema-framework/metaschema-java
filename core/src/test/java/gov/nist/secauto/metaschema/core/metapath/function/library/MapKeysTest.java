/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class MapKeysTest
    extends ExpressionTestBase {

  @Test
  void test() {
    ISequence<IAnyAtomicItem> result = IMetapathExpression.compile("map:keys(map{1:\"yes\", 2:\"no\"})")
        .evaluate(null);
    assert result != null;

    // use a set to allow any ordering of the keys, since we have no control over
    // their order
    Set<IAnyAtomicItem> keys = new HashSet<>(result);

    assertEquals(Set.of(integer(1), integer(2)), keys);
  }
}
