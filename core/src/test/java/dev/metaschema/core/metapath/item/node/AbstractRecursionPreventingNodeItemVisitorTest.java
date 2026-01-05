/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.node;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.testsupport.MockedModelTestSupport;
import dev.metaschema.core.testsupport.builder.IModuleBuilder;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
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
    // Using timeout assertion to make the test intent explicit
    assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
      visitor.visitMetaschema(INodeItemFactory.instance().newModuleNodeItem(module), null);
    });
  }

}
