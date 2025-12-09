/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testsupport.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.core.testsupport.MockedModelTestSupport;
import gov.nist.secauto.metaschema.core.testsupport.builder.IConstraintSetBuilder;

import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * Unit tests for {@link IConstraintSetBuilder}.
 */
class ConstraintSetBuilderTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/constraint-test";

  @Test
  void testBasicConstraintSetCreation() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    // When
    IConstraintSet constraintSet = mocking.constraintSet()
        .source(source)
        .build();

    // Then
    assertNotNull(constraintSet, "Constraint set should not be null");
    assertEquals(source, constraintSet.getSource(), "Source should match");
    assertTrue(constraintSet.getImportedConstraintSets().isEmpty(), "Should have no imports");
  }

  @Test
  void testConstraintSetWithAllowedValuesContext() {
    // Given - simulating issue184-constraints.xml:
    // <context>
    // <metapath target="//*"/>
    // <constraints>
    // <allowed-values target="@value">
    // <enum value="value1">Value #1</enum>
    // </allowed-values>
    // </constraints>
    // </context>
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    // When
    IConstraintSet constraintSet = mocking.constraintSet()
        .source(source)
        .context(ctx -> ctx
            .metapath("//*")
            .constraint(IAllowedValuesConstraint.builder()
                .source(source)
                .target(IMetapathExpression.compile("@value"))
                .allowedValue(IAllowedValue.of("value1", MarkupLine.fromMarkdown("Value #1"), null))))
        .build();

    // Then
    assertNotNull(constraintSet, "Constraint set should not be null");
  }

  @Test
  void testConstraintSetWithMatchesContext() {
    // Given - simulating computer-metaschema-meta-constraints.xml:
    // <context>
    // <metapath target="/(computer|vendor)/@id"/>
    // <constraints>
    // <matches target="." datatype="uuid"/>
    // </constraints>
    // </context>
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    // When
    IConstraintSet constraintSet = mocking.constraintSet()
        .source(source)
        .context(ctx -> ctx
            .metapath("/(computer|vendor)/@id")
            .constraint(IMatchesConstraint.builder()
                .source(source)
                .target(IMetapathExpression.compile("."))
                .datatype(MetaschemaDataTypeProvider.UUID)))
        .build();

    // Then
    assertNotNull(constraintSet, "Constraint set should not be null");
  }

  @Test
  void testConstraintSetWithNestedContexts() {
    // Given - test nested context hierarchy
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    // When
    IConstraintSet constraintSet = mocking.constraintSet()
        .source(source)
        .context(ctx -> ctx
            .metapath("//parent")
            .constraint(IAllowedValuesConstraint.builder()
                .source(source)
                .target(IMetapathExpression.compile("@type"))
                .allowedValue(IAllowedValue.of("typeA", MarkupLine.fromMarkdown("Type A"), null)))
            .childContext(child -> child
                .metapath("child")
                .constraint(IAllowedValuesConstraint.builder()
                    .source(source)
                    .target(IMetapathExpression.compile("@name"))
                    .allowedValue(IAllowedValue.of("nameX", MarkupLine.fromMarkdown("Name X"), null)))))
        .build();

    // Then
    assertNotNull(constraintSet, "Constraint set should not be null");
  }

  @Test
  void testConstraintSetWithImports() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source1 = ISource.externalSource(URI.create(TEST_NAMESPACE + "/imported"));
    ISource source2 = ISource.externalSource(URI.create(TEST_NAMESPACE + "/main"));

    // Create an imported constraint set
    IConstraintSet importedSet = mocking.constraintSet()
        .source(source1)
        .build();

    // When - create main constraint set that imports the first
    IConstraintSet mainSet = mocking.constraintSet()
        .source(source2)
        .imports(importedSet)
        .build();

    // Then
    assertNotNull(mainSet, "Main constraint set should not be null");
    assertEquals(1, mainSet.getImportedConstraintSets().size(), "Should have one import");
    assertTrue(mainSet.getImportedConstraintSets().contains(importedSet), "Should contain imported set");
  }

  @Test
  void testConstraintSetWithMultipleContexts() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    // When
    IConstraintSet constraintSet = mocking.constraintSet()
        .source(source)
        .context(ctx -> ctx
            .metapath("//assembly1")
            .constraint(IAllowedValuesConstraint.builder()
                .source(source)
                .target(IMetapathExpression.compile("@attr1"))
                .allowedValue(IAllowedValue.of("val1", MarkupLine.fromMarkdown("Value 1"), null))))
        .context(ctx -> ctx
            .metapath("//assembly2")
            .constraint(IMatchesConstraint.builder()
                .source(source)
                .target(IMetapathExpression.compile("@attr2"))
                .datatype(MetaschemaDataTypeProvider.STRING)))
        .build();

    // Then
    assertNotNull(constraintSet, "Constraint set should not be null");
  }
}
