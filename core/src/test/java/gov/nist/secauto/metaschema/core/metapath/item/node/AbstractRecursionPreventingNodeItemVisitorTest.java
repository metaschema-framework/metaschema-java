/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.testing.model.IModuleBuilder;
import gov.nist.secauto.metaschema.core.testing.model.MockedModelTestSupport;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

class AbstractRecursionPreventingNodeItemVisitorTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/recursion-test";

  @Test
  void testRecursion() {
    // Build a module with a self-referencing assembly
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("recursion-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("recursive-node")
            .rootName("recursive-node")
            .modelInstances(List.of(
                mocking.assemblyRef("recursive-node"))))
        .toModule();

    AbstractRecursionPreventingNodeItemVisitor<Void, Void> visitor
        = new AbstractRecursionPreventingNodeItemVisitor<>() {
          @Override
          protected Void defaultResult() {
            return null;
          }
        };

    // This should complete without infinite loop due to recursion prevention
    visitor.visitMetaschema(INodeItemFactory.instance().newModuleNodeItem(module), null);
  }

}
