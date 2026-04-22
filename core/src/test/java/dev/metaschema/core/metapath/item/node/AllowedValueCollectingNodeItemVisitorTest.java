/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.node;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IAllowedValue;
import dev.metaschema.core.model.constraint.IAllowedValuesConstraint;
import dev.metaschema.core.model.constraint.ILet;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.core.qname.IEnhancedQName;
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

  @Test
  void testVisitorFindsConstraintOnFieldDefinition() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("field-av-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("category"))))
        .toModule();

    // Add a constraint directly to the field definition (exercises visitField)
    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    IFieldDefinition fieldDef = rootDef.getFieldInstances().iterator().next().getDefinition();
    IValueConstrained constraintSupport = fieldDef.getConstraintSupport();
    constraintSupport.addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("."))
            .allowedValue(IAllowedValue.of("alpha", MarkupLine.fromMarkdown("Alpha"), null))
            .allowedValue(IAllowedValue.of("beta", MarkupLine.fromMarkdown("Beta"), null))
            .build());

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    visitor.visit(module);

    Collection<NodeItemRecord> locations = visitor.getAllowedValueLocations();
    assertFalse(locations.isEmpty(), "Visitor should find constraints declared on a field definition");

    NodeItemRecord record = locations.iterator().next();
    AllowedValuesRecord avRecord = record.getAllowedValues().get(0);
    assertEquals(2, avRecord.getAllowedValues().getAllowedValues().size(),
        "Both allowed values should be present on the field-level constraint");
    // The target should be the field itself (self reference)
    assertEquals("category", avRecord.getTarget().getDefinition().getName(),
        "Target should be the field node item");
  }

  @Test
  void testAllowOtherFlagPreserved() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("allow-other-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(
                mocking.flag().name("strict"),
                mocking.flag().name("lax"))))
        .toModule();

    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    IValueConstrained constraintSupport = rootDef.getConstraintSupport();
    // Explicit allow-other=false (strict)
    constraintSupport.addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@strict"))
            .allowedValue(IAllowedValue.of("x", MarkupLine.fromMarkdown("X"), null))
            .allowsOther(false)
            .build());
    // Explicit allow-other=true (lax)
    constraintSupport.addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@lax"))
            .allowedValue(IAllowedValue.of("y", MarkupLine.fromMarkdown("Y"), null))
            .allowsOther(true)
            .build());

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    visitor.visit(module);

    List<AllowedValuesRecord> allRecords = visitor.getAllowedValueLocations().stream()
        .flatMap(loc -> loc.getAllowedValues().stream())
        .collect(java.util.stream.Collectors.toList());
    assertEquals(2, allRecords.size(), "Both constraints should be captured");

    boolean foundStrict = false;
    boolean foundLax = false;
    for (AllowedValuesRecord record : allRecords) {
      String targetName = record.getTarget().getDefinition().getName();
      if ("strict".equals(targetName)) {
        foundStrict = true;
        assertFalse(record.getAllowedValues().isAllowedOther(),
            "allow-other=false should be preserved on the strict constraint");
      } else if ("lax".equals(targetName)) {
        foundLax = true;
        assertTrue(record.getAllowedValues().isAllowedOther(),
            "allow-other=true should be preserved on the lax constraint");
      }
    }
    assertTrue(foundStrict, "Expected a record for the 'strict' flag");
    assertTrue(foundLax, "Expected a record for the 'lax' flag");
  }

  @Test
  void testNodeItemRecordAllowedValuesIsUnmodifiable() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("unmodifiable-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(mocking.flag().name("flag"))))
        .toModule();

    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    rootDef.getConstraintSupport().addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@flag"))
            .allowedValue(IAllowedValue.of("v", MarkupLine.fromMarkdown("V"), null))
            .build());

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    visitor.visit(module);

    NodeItemRecord record = visitor.getAllowedValueLocations().iterator().next();
    List<AllowedValuesRecord> view = record.getAllowedValues();
    // Callers must not be able to mutate the list returned by the getter
    assertThrows(UnsupportedOperationException.class, () -> view.clear(),
        "NodeItemRecord.getAllowedValues() must return an unmodifiable view");
  }

  @Test
  void testAllowedValuesRecordCarriesConstraintIdentity() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("identity-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(mocking.flag().name("phase"))))
        .toModule();

    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    IAllowedValuesConstraint constraint = IAllowedValuesConstraint.builder()
        .identifier("phase-values")
        .source(source)
        .target(IMetapathExpression.compile("@phase"))
        .allowedValue(IAllowedValue.of("init", MarkupLine.fromMarkdown("Initialize"), null))
        .allowedValue(IAllowedValue.of("done", MarkupLine.fromMarkdown("Complete"), null))
        .build();
    rootDef.getConstraintSupport().addConstraint(constraint);

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    visitor.visit(module);

    AllowedValuesRecord record = visitor.getAllowedValueLocations().iterator().next()
        .getAllowedValues().get(0);

    // The visitor must surface the exact constraint instance, its id, and the
    // declaration location
    assertEquals(constraint, record.getAllowedValues(),
        "Record should reference the same constraint instance that was added");
    assertEquals("phase-values", record.getAllowedValues().getId(),
        "Constraint identifier should be preserved");
    assertEquals("root", record.getLocation().getDefinition().getName(),
        "Location should be the assembly where the constraint is declared");
    assertTrue(record.getAllowedValues().getAllowedValues().containsKey("init"),
        "Allowed value keys should be preserved");
    assertTrue(record.getAllowedValues().getAllowedValues().containsKey("done"),
        "Allowed value keys should be preserved");
  }

  @Test
  void testVisitorSkipsLetExpressionThatCannotBeEvaluatedAtDefinition() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("let-eval-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(mocking.flag().name("href"))))
        .toModule();

    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    // A let whose value expression atomizes a flag node item. At definition-walk
    // time the flag has no typed value, so this evaluation would raise
    // InvalidTypeFunctionException (FOTY). The visitor must tolerate this and
    // continue collecting allowed-values constraints.
    rootDef.getConstraintSupport().addLetExpression(
        ILet.of(
            IEnhancedQName.of("resolved"),
            IMetapathExpression.compile("@href + 1"),
            source,
            null));
    rootDef.getConstraintSupport().addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@href"))
            .allowedValue(IAllowedValue.of("http://example.com/a", MarkupLine.fromMarkdown("A"), null))
            .build());

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    assertDoesNotThrow(() -> visitor.visit(module),
        "Visitor must not abort when a let expression cannot be evaluated on a definition node");

    Collection<NodeItemRecord> locations = visitor.getAllowedValueLocations();
    assertEquals(1, locations.size(),
        "The allowed-values constraint that does not reference the failing let must still be collected");
  }

  @Test
  void testVisitorSkipsAllowedValuesTargetThatReferencesUnboundLetVariable() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("target-eval-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(
                mocking.flag().name("href"),
                mocking.flag().name("status"))))
        .toModule();

    IAssemblyDefinition rootDef = module.getAssemblyDefinitions().iterator().next();
    // The let's value expression atomizes a flag with no typed value, so the
    // variable will never be bound.
    rootDef.getConstraintSupport().addLetExpression(
        ILet.of(
            IEnhancedQName.of("resolved"),
            IMetapathExpression.compile("@href + 1"),
            source,
            null));
    // This constraint's target references the (unbound) $resolved variable. The
    // visitor must catch the resulting evaluation error and move on instead of
    // aborting the walk.
    rootDef.getConstraintSupport().addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("$resolved"))
            .allowedValue(IAllowedValue.of("skipped", MarkupLine.fromMarkdown("Skipped"), null))
            .build());
    // An independent constraint with a simple target should still be collected.
    rootDef.getConstraintSupport().addConstraint(
        IAllowedValuesConstraint.builder()
            .source(source)
            .target(IMetapathExpression.compile("@status"))
            .allowedValue(IAllowedValue.of("active", MarkupLine.fromMarkdown("Active"), null))
            .build());

    AllowedValueCollectingNodeItemVisitor visitor = new AllowedValueCollectingNodeItemVisitor();
    assertDoesNotThrow(() -> visitor.visit(module),
        "Visitor must not abort when an allowed-values target references an unbound let variable");

    Collection<NodeItemRecord> locations = visitor.getAllowedValueLocations();
    assertEquals(1, locations.size(),
        "Only the independent allowed-values constraint should be collected");
    assertEquals("status", locations.iterator().next().getItem().getDefinition().getName(),
        "The surviving constraint should be the one targeting @status");
  }
}
