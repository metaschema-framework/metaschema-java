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
import dev.metaschema.core.mdm.IDMFieldNodeItem;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.MockedModelTestSupport;
import dev.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for Index and Unique constraint validation using real DM node items.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
class IndexUniqueConstraintTest {
  @NonNull
  private static final String NS = ObjectUtils.notNull(URI.create("http://example.com/ns").toASCIIString());
  @NonNull
  private static final IEnhancedQName PARENT_QNAME = IEnhancedQName.of(NS, "parent");
  @NonNull
  private static final IEnhancedQName CHILD_QNAME = IEnhancedQName.of(NS, "child");
  @NonNull
  private static final IEnhancedQName KEY1_QNAME = IEnhancedQName.of("key1");
  @NonNull
  private static final IEnhancedQName KEY2_QNAME = IEnhancedQName.of("key2");

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
   * Helper class to hold assembly definition and field instance with flags.
   */
  private static class AssemblyWithFieldAndFlags {
    @NonNull
    final IAssemblyDefinition assemblyDef;
    @NonNull
    final IFieldInstance fieldInstance;
    @NonNull
    final IFlagInstance key1FlagInstance;
    @NonNull
    final IFlagInstance key2FlagInstance;

    AssemblyWithFieldAndFlags(
        @NonNull IAssemblyDefinition assemblyDef,
        @NonNull IFieldInstance fieldInstance,
        @NonNull IFlagInstance key1FlagInstance,
        @NonNull IFlagInstance key2FlagInstance) {
      this.assemblyDef = assemblyDef;
      this.fieldInstance = fieldInstance;
      this.key1FlagInstance = key1FlagInstance;
      this.key2FlagInstance = key2FlagInstance;
    }
  }

  /**
   * Create an assembly definition with a child field instance named "child".
   *
   * @param mocking
   *          the mock support
   * @param source
   *          the module source
   * @return the assembly with its field instance
   */
  @NonNull
  private static AssemblyWithField createAssemblyWithChildField(
      @NonNull MockedModelTestSupport mocking,
      @NonNull ISource source) {
    IAssemblyDefinition assemblyDef = mocking.assembly()
        .qname(PARENT_QNAME)
        .source(source)
        .toDefinition();
    IFieldInstance fieldInstance = mocking.field()
        .qname(CHILD_QNAME)
        .source(source)
        .toInstance(assemblyDef);
    return new AssemblyWithField(assemblyDef, fieldInstance);
  }

  /**
   * Create an assembly with a child field that has two flag instances (key1,
   * key2).
   *
   * @param mocking
   *          the mock support
   * @param source
   *          the module source
   * @return the assembly with its field and flag instances
   */
  @NonNull
  private static AssemblyWithFieldAndFlags createAssemblyWithChildFieldAndFlags(
      @NonNull MockedModelTestSupport mocking,
      @NonNull ISource source) {
    IAssemblyDefinition assemblyDef = mocking.assembly()
        .qname(PARENT_QNAME)
        .source(source)
        .toDefinition();

    // Create field definition first to add flags to it
    var fieldDef = mocking.field()
        .qname(CHILD_QNAME)
        .source(source)
        .toDefinition();

    // Add flag instances to the field definition
    IFlagInstance key1FlagInstance = mocking.flag()
        .qname(KEY1_QNAME)
        .source(source)
        .toInstance(fieldDef);

    IFlagInstance key2FlagInstance = mocking.flag()
        .qname(KEY2_QNAME)
        .source(source)
        .toInstance(fieldDef);

    // Create field instance linking to parent and definition
    IFieldInstance fieldInstance = mocking.field()
        .qname(CHILD_QNAME)
        .source(source)
        .toInstance(assemblyDef, fieldDef);

    return new AssemblyWithFieldAndFlags(assemblyDef, fieldInstance, key1FlagInstance, key2FlagInstance);
  }

  /**
   * Test index constraint with unique entries (should pass).
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testIndexConstraintSuccess() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithChildField(mocking, source);

    // Create assembly node and add 3 child items with unique values
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("id1"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("id2"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("id3"));

    // Index constraint with key field "." (the field value)
    IIndexConstraint indexConstraint = IIndexConstraint.builder("test-index")
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile(".", staticContext),
            null,
            null))
        .build();
    awf.assemblyDef.addConstraint(indexConstraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "should pass with unique index entries");
  }

  /**
   * Test index constraint with duplicate index names (should fail).
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testIndexConstraintDuplicateName() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithChildField(mocking, source);

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("id1"));

    // Create two index constraints with the same name
    IIndexConstraint indexConstraint1 = IIndexConstraint.builder("test-index")
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile(".", staticContext),
            null,
            null))
        .build();

    IIndexConstraint indexConstraint2 = IIndexConstraint.builder("test-index")
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile(".", staticContext),
            null,
            null))
        .build();

    awf.assemblyDef.addConstraint(indexConstraint1);
    awf.assemblyDef.addConstraint(indexConstraint2);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with duplicate index name"),
        () -> assertThat("should have findings", handler.getFindings(), hasSize(1)));
  }

  /**
   * Test index constraint with duplicate keys (should fail).
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testIndexConstraintDuplicateKeys() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithChildField(mocking, source);

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("duplicate"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("duplicate"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("unique"));

    IIndexConstraint indexConstraint = IIndexConstraint.builder("test-index")
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile(".", staticContext),
            null,
            null))
        .build();
    awf.assemblyDef.addConstraint(indexConstraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with duplicate keys"),
        () -> assertThat("should have findings", handler.getFindings(), hasSize(1)));
  }

  /**
   * Test unique constraint with all unique values (should pass).
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testUniqueConstraintAllUnique() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithChildField(mocking, source);

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value1"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value2"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("value3"));

    IUniqueConstraint uniqueConstraint = IUniqueConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile(".", staticContext),
            null,
            null))
        .build();
    awf.assemblyDef.addConstraint(uniqueConstraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "should pass with all unique values");
  }

  /**
   * Test unique constraint with duplicate values (should fail).
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testUniqueConstraintDuplicateValues() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithField awf = createAssemblyWithChildField(mocking, source);

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awf.assemblyDef, staticContext);
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("duplicate"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("duplicate"));
    assembly.newField(awf.fieldInstance, IStringItem.valueOf("unique"));

    IUniqueConstraint uniqueConstraint = IUniqueConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile(".", staticContext),
            null,
            null))
        .build();
    awf.assemblyDef.addConstraint(uniqueConstraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with duplicate values"),
        () -> assertThat("should have findings", handler.getFindings(), hasSize(1)));
  }

  /**
   * Test unique constraint with compound keys - all unique (should pass).
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testUniqueConstraintCompoundKeys() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithFieldAndFlags awff = createAssemblyWithChildFieldAndFlags(mocking, source);

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awff.assemblyDef, staticContext);

    // Add child fields with unique compound keys
    // child1: key1=a, key2=1
    IDMFieldNodeItem child1 = assembly.newField(awff.fieldInstance, IStringItem.valueOf("data1"));
    child1.newFlag(awff.key1FlagInstance, IStringItem.valueOf("a"));
    child1.newFlag(awff.key2FlagInstance, IStringItem.valueOf("1"));

    // child2: key1=a, key2=2 (different from child1)
    IDMFieldNodeItem child2 = assembly.newField(awff.fieldInstance, IStringItem.valueOf("data2"));
    child2.newFlag(awff.key1FlagInstance, IStringItem.valueOf("a"));
    child2.newFlag(awff.key2FlagInstance, IStringItem.valueOf("2"));

    // child3: key1=b, key2=1 (different from child1 and child2)
    IDMFieldNodeItem child3 = assembly.newField(awff.fieldInstance, IStringItem.valueOf("data3"));
    child3.newFlag(awff.key1FlagInstance, IStringItem.valueOf("b"));
    child3.newFlag(awff.key2FlagInstance, IStringItem.valueOf("1"));

    // Compound key constraint with two key fields
    IUniqueConstraint uniqueConstraint = IUniqueConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile("@key1", staticContext),
            null,
            null))
        .keyField(IKeyField.of(
            IMetapathExpression.compile("@key2", staticContext),
            null,
            null))
        .build();
    awff.assemblyDef.addConstraint(uniqueConstraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "should pass with unique compound keys");
  }

  /**
   * Test unique constraint with duplicate compound keys (should fail).
   *
   * @throws ConstraintValidationException
   *           if an unexpected error occurred while validating a constraint
   */
  @Test
  void testUniqueConstraintCompoundKeysDuplicate() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    AssemblyWithFieldAndFlags awff = createAssemblyWithChildFieldAndFlags(mocking, source);

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(awff.assemblyDef, staticContext);

    // Add child fields with duplicate compound keys
    // child1: key1=a, key2=1
    IDMFieldNodeItem child1 = assembly.newField(awff.fieldInstance, IStringItem.valueOf("data1"));
    child1.newFlag(awff.key1FlagInstance, IStringItem.valueOf("a"));
    child1.newFlag(awff.key2FlagInstance, IStringItem.valueOf("1"));

    // child2: key1=a, key2=1 (duplicate compound key)
    IDMFieldNodeItem child2 = assembly.newField(awff.fieldInstance, IStringItem.valueOf("data2"));
    child2.newFlag(awff.key1FlagInstance, IStringItem.valueOf("a"));
    child2.newFlag(awff.key2FlagInstance, IStringItem.valueOf("1"));

    // child3: key1=b, key2=1 (unique)
    IDMFieldNodeItem child3 = assembly.newField(awff.fieldInstance, IStringItem.valueOf("data3"));
    child3.newFlag(awff.key1FlagInstance, IStringItem.valueOf("b"));
    child3.newFlag(awff.key2FlagInstance, IStringItem.valueOf("1"));

    // Compound key constraint with two key fields
    IUniqueConstraint uniqueConstraint = IUniqueConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("child", staticContext))
        .keyField(IKeyField.of(
            IMetapathExpression.compile("@key1", staticContext),
            null,
            null))
        .keyField(IKeyField.of(
            IMetapathExpression.compile("@key2", staticContext),
            null,
            null))
        .build();
    awff.assemblyDef.addConstraint(uniqueConstraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(assembly, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "should fail with duplicate compound keys"),
        () -> assertThat("should have findings", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for assembly", handler.getFindings().get(0),
            hasProperty("node", is(assembly))));
  }
}
