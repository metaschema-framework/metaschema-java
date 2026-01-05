/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.metaschema.core.mdm.IDMAssemblyNodeItem;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.MockedModelTestSupport;
import dev.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for cardinality constraint validation using real DM node items.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
class CardinalityConstraintTest {
  @NonNull
  private static final String NS = ObjectUtils.notNull(URI.create("http://example.com/ns").toASCIIString());
  @NonNull
  private static final IEnhancedQName ASSEMBLY_QNAME = IEnhancedQName.of(NS, "assembly");
  @NonNull
  private static final IEnhancedQName ITEM_QNAME = IEnhancedQName.of(NS, "item");

  /**
   * Helper class to hold assembly definition and field instance together.
   */
  private static class AssemblyWithField {
    @NonNull
    final IAssemblyDefinition assemblyDef;
    @NonNull
    final IFieldInstance fieldInstance;

    AssemblyWithField(@NonNull IAssemblyDefinition assemblyDef, @NonNull IFieldInstance fieldInstance) {
      this.assemblyDef = assemblyDef;
      this.fieldInstance = fieldInstance;
    }
  }

  /**
   * Create an assembly definition with a child field instance named "item".
   *
   * @param mocking
   *          the mock support
   * @param source
   *          the module source
   * @return the assembly with its field instance
   */
  @NonNull
  private static AssemblyWithField createAssemblyWithItemField(
      @NonNull MockedModelTestSupport mocking,
      @NonNull ISource source) {
    IAssemblyDefinition assemblyDef = mocking.assembly()
        .qname(ASSEMBLY_QNAME)
        .source(source)
        .toDefinition();
    // Create a field instance named "item" on the assembly
    IFieldInstance fieldInstance = mocking.field()
        .qname(ITEM_QNAME)
        .source(source)
        .toInstance(assemblyDef);
    return new AssemblyWithField(assemblyDef, fieldInstance);
  }

  /**
   * Test that cardinality constraint passes when minOccurs is met.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityMinOccursPass() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node and add 3 child items
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value2"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value3"));

    // Constraint requires minOccurs=2, we have 3 items - should pass
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .minOccurs(2)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "should pass with 3 items when minOccurs=2");
  }

  /**
   * Test that cardinality constraint fails when minOccurs is not met.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityMinOccursFail() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node and add 1 child item
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));

    // Constraint requires minOccurs=2, we have 1 item - should fail
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .minOccurs(2)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with 1 item when minOccurs=2"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for assembly node",
            handler.getFindings().get(0),
            hasProperty("node", is(assembly))));
  }

  /**
   * Test that cardinality constraint passes when maxOccurs is met.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityMaxOccursPass() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node and add 2 child items
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value2"));

    // Constraint requires maxOccurs=3, we have 2 items - should pass
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .maxOccurs(3)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "should pass with 2 items when maxOccurs=3");
  }

  /**
   * Test that cardinality constraint fails when maxOccurs is exceeded.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityMaxOccursFail() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node and add 4 child items
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value2"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value3"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value4"));

    // Constraint requires maxOccurs=3, we have 4 items - should fail
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .maxOccurs(3)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with 4 items when maxOccurs=3"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for assembly node",
            handler.getFindings().get(0),
            hasProperty("node", is(assembly))));
  }

  /**
   * Test that cardinality constraint with both minOccurs and maxOccurs passes
   * when within range.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityBothMinMaxPass() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node and add 3 child items
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value2"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value3"));

    // Constraint requires minOccurs=2, maxOccurs=5, we have 3 items - should pass
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .minOccurs(2)
        .maxOccurs(5)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "should pass with 3 items when minOccurs=2 and maxOccurs=5");
  }

  /**
   * Test that cardinality constraint with both minOccurs and maxOccurs fails when
   * below minimum.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityBothMinMaxFailMin() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node and add 1 child item
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));

    // Constraint requires minOccurs=2, maxOccurs=5, we have 1 item - should fail on
    // minimum
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .minOccurs(2)
        .maxOccurs(5)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with 1 item when minOccurs=2 and maxOccurs=5"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for assembly node",
            handler.getFindings().get(0),
            hasProperty("node", is(assembly))));
  }

  /**
   * Test that cardinality constraint with both minOccurs and maxOccurs fails when
   * above maximum.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityBothMinMaxFailMax() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node and add 6 child items
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value2"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value3"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value4"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value5"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value6"));

    // Constraint requires minOccurs=2, maxOccurs=5, we have 6 items - should fail
    // on maximum
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .minOccurs(2)
        .maxOccurs(5)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with 6 items when minOccurs=2 and maxOccurs=5"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for assembly node",
            handler.getFindings().get(0),
            hasProperty("node", is(assembly))));
  }

  /**
   * Test that cardinality constraint passes when there are zero matching targets
   * and minOccurs is 0.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityZeroTargetsPass() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node with no child items
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    // Don't add any items

    // Constraint requires minOccurs=0, maxOccurs=3, we have 0 items - should pass
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .minOccurs(0)
        .maxOccurs(3)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "should pass with 0 items when minOccurs=0 and maxOccurs=3");
  }

  /**
   * Test that cardinality constraint fails when there are zero matching targets
   * but minOccurs requires items.
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testCardinalityZeroTargetsFail() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithItemField(mocking, source);

    // Create assembly node with no child items
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    // Don't add any items

    // Constraint requires minOccurs=1, we have 0 items - should fail
    ICardinalityConstraint constraint = ICardinalityConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("item", staticContext))
        .minOccurs(1)
        .build();
    awf.assemblyDef.addConstraint(constraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with 0 items when minOccurs=1"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for assembly node",
            handler.getFindings().get(0),
            hasProperty("node", is(assembly))));
  }
}
