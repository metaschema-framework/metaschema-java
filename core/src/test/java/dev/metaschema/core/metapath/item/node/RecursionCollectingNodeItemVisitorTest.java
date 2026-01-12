/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Set;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.testsupport.MockedModelTestSupport;
import dev.metaschema.core.testsupport.builder.IModuleBuilder;

class RecursionCollectingNodeItemVisitorTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/recursion-test";

  @Test
  void testAssemblyRecursion() {
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

    RecursionCollectingNodeItemVisitor walker = new RecursionCollectingNodeItemVisitor();
    walker.visit(module);
    Set<RecursionCollectingNodeItemVisitor.AssemblyRecord> recursiveAssemblies
        = walker.getRecursiveAssemblyDefinitions();

    // Verify that the recursive assembly was detected
    assertFalse(recursiveAssemblies.isEmpty(), "Should detect recursive assembly");
    assertEquals(1, recursiveAssemblies.size(), "Should have exactly one recursive assembly");

    RecursionCollectingNodeItemVisitor.AssemblyRecord record = recursiveAssemblies.iterator().next();
    assertEquals("recursive-node", record.getDefinition().getName(),
        "Recursive assembly should be 'recursive-node'");
  }

}
