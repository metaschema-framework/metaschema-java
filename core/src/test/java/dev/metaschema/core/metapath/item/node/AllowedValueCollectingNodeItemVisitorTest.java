/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.node.AllowedValueCollectingNodeItemVisitor.AllowedValuesRecord;
import dev.metaschema.core.metapath.item.node.AllowedValueCollectingNodeItemVisitor.NodeItemRecord;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IAllowedValue;
import dev.metaschema.core.model.constraint.IAllowedValuesConstraint;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.core.testsupport.MockedModelTestSupport;
import dev.metaschema.core.testsupport.builder.IModuleBuilder;

class AllowedValueCollectingNodeItemVisitorTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/allowed-values-test";

  @Test
  void testVisitorFindsAllowedValuesConstraints() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("av-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(mocking.flag().name("status"))))
        .toModule();

    // Add an allowed-values constraint to the assembly targeting the status flag
    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    IValueConstrained constraintSupport = rootDef.getConstraintSupport();
    constraintSupport.addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@status"))
            .allowedValue(IAllowedValue.of("active", MarkupLine.fromMarkdown("Active"), null))
            .allowedValue(IAllowedValue.of("inactive", MarkupLine.fromMarkdown("Inactive"), null))
            .build());

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    visitor.visit(module);

    Collection<NodeItemRecord> locations = visitor.getAllowedValueLocations();
    assertFalse(locations.isEmpty(), "Visitor should find at least one allowed-values location");

    NodeItemRecord record = locations.iterator().next();
    assertNotNull(record.getItem(), "Node item should not be null");
    assertFalse(record.getAllowedValues().isEmpty(), "Should have at least one allowed-values constraint");

    AllowedValuesRecord avRecord = record.getAllowedValues().get(0);
    assertNotNull(avRecord.getAllowedValues(), "AllowedValuesConstraint should not be null");
    assertNotNull(avRecord.getLocation(), "Location should not be null");
    assertNotNull(avRecord.getTarget(), "Target should not be null");
  }

  @Test
  void testVisitorReturnsEmptyForModuleWithoutConstraints() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("no-constraints-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(mocking.flag().name("id"))))
        .toModule();

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    visitor.visit(module);

    Collection<NodeItemRecord> locations = visitor.getAllowedValueLocations();
    assertTrue(locations.isEmpty(), "Visitor should return empty for module without allowed-values constraints");
  }

  @Test
  void testVisitorHandlesMultipleConstraints() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("multi-constraints-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(
                mocking.flag().name("type"),
                mocking.flag().name("status"))))
        .toModule();

    // Add two allowed-values constraints on the assembly targeting different flags
    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    IValueConstrained constraintSupport = rootDef.getConstraintSupport();
    constraintSupport.addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@type"))
            .allowedValue(IAllowedValue.of("typeA", MarkupLine.fromMarkdown("Type A"), null))
            .build());
    constraintSupport.addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@status"))
            .allowedValue(IAllowedValue.of("active", MarkupLine.fromMarkdown("Active"), null))
            .build());

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    visitor.visit(module);

    Collection<NodeItemRecord> locations = visitor.getAllowedValueLocations();
    assertFalse(locations.isEmpty(), "Visitor should find allowed-values locations");

    int totalConstraints = locations.stream()
        .mapToInt(loc -> loc.getAllowedValues().size())
        .sum();
    assertEquals(2, totalConstraints, "Should find exactly two allowed-values constraints");
  }
}
