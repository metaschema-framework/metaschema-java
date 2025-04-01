/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class FnDocTest {

  /**
   * Ensure that the same document loaded twice produces the exact same node
   * instances.
   */
  @Test
  void testSameNode() {
    IBindingContext bindingContext = IBindingContext.newInstance();

    StaticContext staticContext = StaticContext.builder()
        .baseUri(Paths.get(".").toUri())
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.setDocumentLoader(bindingContext.newBoundLoader());

    IFunction function = staticContext.lookupFunction("doc", 1);
    assertNotNull(function);

    ISequence<?> result1 = function.execute(
        CollectionUtil
            .singletonList(ISequence.of(IStringItem.valueOf("src/test/resources/metaschema/simple/metaschema.xml"))),
        dynamicContext,
        ISequence.empty());

    ISequence<?> result2 = function.execute(
        CollectionUtil
            .singletonList(ISequence.of(IStringItem.valueOf("src/test/resources/metaschema/simple/metaschema.xml"))),
        dynamicContext,
        ISequence.empty());
    assertSame(result1, result2);
  }
}
