/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.mdm.IDMFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testsupport.MockedModelTestSupport;
import gov.nist.secauto.metaschema.core.testsupport.mocking.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Comprehensive tests for expect constraint validation.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
class ExpectConstraintTest {
  @NonNull
  private static final String NS = ObjectUtils.notNull(URI.create("http://example.com/ns").toASCIIString());

  @NonNull
  private static IEnhancedQName qname(@NonNull String name) {
    return IEnhancedQName.of(NS, name);
  }

  /**
   * Mock constraint methods on a flag definition to prevent null pointer
   * exceptions during validation.
   *
   * @param flag
   *          the flag node item whose definition to mock
   */
  @SuppressWarnings("null")
  private static void mockFlagDefinitionConstraints(@NonNull IFlagNodeItem flag) {
    IFlagDefinition definition = flag.getDefinition();
    doReturn(CollectionUtil.emptyMap()).when(definition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(definition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(definition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(definition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(definition).getIndexHasKeyConstraints();
  }

  /**
   * Test expect constraint that passes when test expression evaluates to true.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintPasses() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("test-value"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create expect constraint with test that evaluates to true
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("string-length(.) > 0"))
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertTrue(handler.isPassing(), "constraint should pass when test expression evaluates to true");
  }

  /**
   * Test expect constraint that fails when test expression evaluates to false.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintFails() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("test"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create expect constraint with test that evaluates to false
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("string-length(.) > 10"))
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertAll(
        () -> assertFalse(handler.isPassing(), "constraint should fail when test expression evaluates to false"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for the flag node", handler.getFindings(),
            hasItem(hasProperty("node", is(flag)))));
  }

  /**
   * Test expect constraint with custom message.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintWithCustomMessage() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("short"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    String customMessage = "Value must be at least 10 characters long";

    // Create expect constraint with custom message
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("string-length(.) >= 10"))
        .message(customMessage)
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertAll(
        () -> assertFalse(handler.isPassing(), "constraint should fail"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should contain custom message", handler.getFindings(),
            hasItem(hasProperty("message", is(customMessage)))));
  }

  /**
   * Test expect constraint targeting specific Metapath using DM data model.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @Test
  void testExpectConstraintWithTargetMetapath() throws ConstraintValidationException {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource("https://example.com/module");

    // Create field definition with a "status" flag
    IFieldDefinition fieldDef = mocking.field()
        .qname(qname("item"))
        .source(source)
        .toDefinition();

    // Create flag instance on the field definition
    IFlagInstance statusFlagInstance = mocking.flag()
        .qname(IEnhancedQName.of("status"))
        .source(source)
        .toInstance(fieldDef);

    // Create a StaticContext for Metapath compilation
    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .build();

    // Create field node with "status" flag set to "active"
    IDMFieldNodeItem field = IDMFieldNodeItem.newInstance(fieldDef, IStringItem.valueOf("value"), staticContext);
    field.newFlag(statusFlagInstance, IStringItem.valueOf("active"));

    // Create expect constraint targeting the @status flag, expecting 'inactive'
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .target(IMetapathExpression.compile("@status", staticContext))
        .test(IMetapathExpression.compile(". = 'inactive'", staticContext))
        .build();
    fieldDef.addConstraint(expectConstraint);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(field, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertAll(
        () -> assertFalse(handler.isPassing(), "constraint should fail when target test fails"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)));
  }

  /**
   * Test expect constraint with WARNING severity level.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintWithWarningSeverity() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("test"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create expect constraint with WARNING level
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .level(Level.WARNING)
        .test(IMetapathExpression.compile("string-length(.) > 10"))
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertAll(
        () -> assertTrue(handler.isPassing(), "validation should pass with WARNING level violation"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should have WARNING severity", handler.getFindings(),
            hasItem(hasProperty("severity", is(Level.WARNING)))));
  }

  /**
   * Test expect constraint with ERROR severity level.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintWithErrorSeverity() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("test"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create expect constraint with ERROR level (default)
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .level(Level.ERROR)
        .test(IMetapathExpression.compile("string-length(.) > 10"))
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertAll(
        () -> assertFalse(handler.isPassing(), "validation should fail with ERROR level violation"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should have ERROR severity", handler.getFindings(),
            hasItem(hasProperty("severity", is(Level.ERROR)))));
  }

  /**
   * Test expect constraint with CRITICAL severity level.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintWithCriticalSeverity() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("test"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create expect constraint with CRITICAL level
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .level(Level.CRITICAL)
        .test(IMetapathExpression.compile("string-length(.) > 10"))
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertAll(
        () -> assertFalse(handler.isPassing(), "validation should fail with CRITICAL level violation"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should have CRITICAL severity", handler.getFindings(),
            hasItem(hasProperty("severity", is(Level.CRITICAL)))));
  }

  /**
   * Test expect constraint with complex test expression.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintWithComplexExpression() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("email"), IStringItem.valueOf("user@example.com"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create expect constraint with complex test expression
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("contains(., '@') and string-length(.) > 5"))
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertTrue(handler.isPassing(), "constraint should pass when complex expression evaluates to true");
  }

  /**
   * Test expect constraint with formal name and description.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testExpectConstraintWithMetadata() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("test"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create expect constraint with metadata
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .identifier("expect-001")
        .formalName("Minimum Length Constraint")
        .description(MarkupLine.fromMarkdown("Ensures value has minimum length of 10 characters"))
        .test(IMetapathExpression.compile("string-length(.) >= 10"))
        .message("Value is too short")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertAll(
        () -> assertFalse(handler.isPassing(), "constraint should fail"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("constraint should have id", expectConstraint.getId(), is("expect-001")),
        () -> assertThat("constraint should have formal name", expectConstraint.getFormalName(),
            is("Minimum Length Constraint")));
  }
}
