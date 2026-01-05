/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.util.MermaidErDiagramGenerator;
import dev.metaschema.core.testsupport.MockedModelTestSupport;
import dev.metaschema.core.testsupport.builder.IModuleBuilder;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

class MermaidErDiagramGeneratorTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/diagram-test";

  @Test
  void testErDiagram() {
    // Build a module with a root assembly containing fields and nested assemblies
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("diagram-test")
        .version("1.0.0")
        .source(source)
        .field(mocking.field().name("title"))
        .field(mocking.field().name("description"))
        .assembly(mocking.assembly()
            .name("child-item")
            .flags(List.of(mocking.flag().name("id"))))
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(mocking.flag().name("id")))
            .modelInstances(List.of(
                mocking.fieldRef("title"),
                mocking.fieldRef("description"),
                mocking.assemblyRef("child-item"))))
        .toModule();

    // Generate the diagram to a string
    StringWriter stringWriter = new StringWriter();
    try (PrintWriter writer = new PrintWriter(stringWriter)) {
      MermaidErDiagramGenerator.generate(module, writer);
    }

    String diagram = stringWriter.toString();

    // Verify the diagram contains expected elements
    assertTrue(diagram.contains("erDiagram"), "Diagram should start with erDiagram");
    assertTrue(diagram.contains("root"), "Diagram should contain root assembly");
    assertTrue(diagram.contains("child-item"), "Diagram should contain child-item assembly");
    assertTrue(diagram.contains("title"), "Diagram should contain title field");
    assertTrue(diagram.contains("description"), "Diagram should contain description field");
  }
}
