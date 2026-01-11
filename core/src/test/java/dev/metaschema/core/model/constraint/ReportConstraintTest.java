/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.net.URI;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.format.IPathFormatter;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.node.IFlagNodeItem;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IConstraint.Level;
import dev.metaschema.core.model.constraint.IConstraint.Type;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.mocking.MockNodeItemFactory;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Unit tests for the {@link IReportConstraint} interface and its builder.
 * <p>
 * These tests verify:
 * <ul>
 * <li>Builder creates valid constraint</li>
 * <li>Test expression is retrievable via getTest()</li>
 * <li>Constraint properties (id, level, message) are accessible</li>
 * <li>Visitor pattern works correctly (visitReportConstraint)</li>
 * <li>Default level is INFORMATIONAL</li>
 * <li>Validation generates findings when test is TRUE (opposite of expect)</li>
 * <li>Validation does not generate findings when test is FALSE</li>
 * </ul>
 */
@SuppressWarnings("PMD.TooManyStaticImports")
class ReportConstraintTest {
  @NonNull
  private static final String TEST_SOURCE = "https://example.com/test";
  @NonNull
  private static final String NS = ObjectUtils.notNull(URI.create("http://example.com/ns").toASCIIString());

  @NonNull
  private static IEnhancedQName qname(@NonNull String name) {
    return IEnhancedQName.of(NS, name);
  }

  /**
   * Test that the builder creates a valid constraint with test expression.
   */
  @Test
  void testBuilderCreatesValidConstraint() {
    ISource source = ISource.externalSource(TEST_SOURCE);
    IMetapathExpression test = IMetapathExpression.compile("string-length(.) > 100");

    IReportConstraint constraint = IReportConstraint.builder()
        .source(source)
        .test(test)
        .build();

    assertNotNull(constraint, "Constraint should not be null");
  }

  /**
   * Test that getTest() returns the test Metapath expression.
   */
  @Test
  void testGetTestReturnsExpression() {
    ISource source = ISource.externalSource(TEST_SOURCE);
    IMetapathExpression test = IMetapathExpression.compile("contains(., 'deprecated')");

    IReportConstraint constraint = IReportConstraint.builder()
        .source(source)
        .test(test)
        .build();

    assertSame(test, constraint.getTest(), "getTest() should return the same expression");
  }

  /**
   * Test that constraint properties (id, level, message) are accessible.
   */
  @Test
  void testConstraintPropertiesAreAccessible() {
    ISource source = ISource.externalSource(TEST_SOURCE);
    IMetapathExpression test = IMetapathExpression.compile(". = 'deprecated'");
    String constraintId = "report-001";
    String constraintMessage = "This value is deprecated";
    Level constraintLevel = Level.WARNING;

    IReportConstraint constraint = IReportConstraint.builder()
        .source(source)
        .test(test)
        .identifier(constraintId)
        .message(constraintMessage)
        .level(constraintLevel)
        .build();

    assertEquals(constraintId, constraint.getId(), "getId() should return the constraint id");
    assertEquals(constraintMessage, constraint.getMessage(), "getMessage() should return the message");
    assertEquals(constraintLevel, constraint.getLevel(), "getLevel() should return the configured level");
  }

  /**
   * Test that the visitor pattern works correctly with visitReportConstraint.
   */
  @Test
  void testVisitorPatternWorksCorrectly() {
    ISource source = ISource.externalSource(TEST_SOURCE);
    IMetapathExpression test = IMetapathExpression.compile("true()");

    IReportConstraint constraint = IReportConstraint.builder()
        .source(source)
        .test(test)
        .build();

    // Create a test visitor that tracks if visitReportConstraint was called
    TestConstraintVisitor visitor = new TestConstraintVisitor();

    Boolean result = constraint.accept(visitor, null);

    assertEquals(Boolean.TRUE, result, "Visitor should return true");
    assertEquals(1, visitor.getVisitReportCount(),
        "visitReportConstraint should be called exactly once");
  }

  /**
   * Test that the default level is INFORMATIONAL.
   */
  @Test
  void testDefaultLevelIsInformational() {
    ISource source = ISource.externalSource(TEST_SOURCE);
    IMetapathExpression test = IMetapathExpression.compile("true()");

    IReportConstraint constraint = IReportConstraint.builder()
        .source(source)
        .test(test)
        .build();

    assertEquals(Level.INFORMATIONAL, constraint.getLevel(),
        "Default level should be INFORMATIONAL");
  }

  /**
   * Test that the constraint type is REPORT.
   */
  @Test
  void testConstraintTypeIsReport() {
    ISource source = ISource.externalSource(TEST_SOURCE);
    IMetapathExpression test = IMetapathExpression.compile("true()");

    IReportConstraint constraint = IReportConstraint.builder()
        .source(source)
        .test(test)
        .build();

    assertEquals(Type.REPORT, constraint.getType(), "getType() should return REPORT");
  }

  /**
   * Test that building without a test expression throws an exception.
   */
  @Test
  void testBuilderWithoutTestThrowsException() {
    ISource source = ISource.externalSource(TEST_SOURCE);

    IReportConstraint.Builder builder = IReportConstraint.builder()
        .source(source);

    assertThrows(NullPointerException.class, builder::build,
        "Building without test should throw NullPointerException");
  }

  /**
   * Test that building without a source throws an exception.
   */
  @Test
  void testBuilderWithoutSourceThrowsException() {
    IMetapathExpression test = IMetapathExpression.compile("true()");

    IReportConstraint.Builder builder = IReportConstraint.builder()
        .test(test);

    assertThrows(NullPointerException.class, builder::build,
        "Building without source should throw NullPointerException");
  }

  // =========================================================================
  // Validation Pipeline Tests
  // Report constraints generate findings when test is TRUE (opposite of expect)
  // =========================================================================

  /**
   * Test that report constraint generates a finding when test evaluates to TRUE.
   * <p>
   * This is the opposite of expect constraints, which generate findings when test
   * evaluates to FALSE.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testReportConstraintGeneratesFindingWhenTestIsTrue() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    // Create a flag with value "deprecated-value"
    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("deprecated-value"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create report constraint with test that evaluates to TRUE
    // This should generate a finding because report fires on TRUE
    IReportConstraint reportConstraint = IReportConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("contains(., 'deprecated')"))
        .message("This value is deprecated")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(reportConstraint)).when(flagDefinition).getReportConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertTrue(handler.isPassing(),
            "Validation should pass because default level is INFORMATIONAL"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should be for the flag node", handler.getFindings(),
            hasItem(hasProperty("node", is(flag)))),
        () -> assertThat("finding should have the custom message", handler.getFindings(),
            hasItem(hasProperty("message", is("This value is deprecated")))));
  }

  /**
   * Test that report constraint does NOT generate a finding when test evaluates
   * to FALSE.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testReportConstraintNoFindingWhenTestIsFalse() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    // Create a flag with value "normal-value"
    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("normal-value"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create report constraint with test that evaluates to FALSE
    // This should NOT generate a finding because report only fires on TRUE
    IReportConstraint reportConstraint = IReportConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("contains(., 'deprecated')"))
        .message("This value is deprecated")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(reportConstraint)).when(flagDefinition).getReportConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertTrue(handler.isPassing(), "Validation should pass"),
        () -> assertThat("should have no findings", handler.getFindings(), hasSize(0)));
  }

  /**
   * Test that report constraint with ERROR level causes validation failure.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testReportConstraintWithErrorLevel() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("forbidden-value"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create report constraint with ERROR level
    IReportConstraint reportConstraint = IReportConstraint.builder()
        .source(source)
        .level(Level.ERROR)
        .test(IMetapathExpression.compile("contains(., 'forbidden')"))
        .message("Forbidden value detected")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(reportConstraint)).when(flagDefinition).getReportConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(),
            "Validation should fail because report with ERROR level fired"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should have ERROR severity", handler.getFindings(),
            hasItem(hasProperty("severity", is(Level.ERROR)))));
  }

  /**
   * Test that report constraint with WARNING level does not cause validation
   * failure but still records the finding.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testReportConstraintWithWarningLevel() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("warning-value"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create report constraint with WARNING level
    IReportConstraint reportConstraint = IReportConstraint.builder()
        .source(source)
        .level(Level.WARNING)
        .test(IMetapathExpression.compile("contains(., 'warning')"))
        .message("Warning: check this value")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(reportConstraint)).when(flagDefinition).getReportConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertTrue(handler.isPassing(),
            "Validation should pass because WARNING level does not fail validation"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should have WARNING severity", handler.getFindings(),
            hasItem(hasProperty("severity", is(Level.WARNING)))));
  }

  /**
   * Test that report constraint with CRITICAL level causes validation failure.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testReportConstraintWithCriticalLevel() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("critical-issue"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Create report constraint with CRITICAL level
    IReportConstraint reportConstraint = IReportConstraint.builder()
        .source(source)
        .level(Level.CRITICAL)
        .test(IMetapathExpression.compile("contains(., 'critical')"))
        .message("Critical issue detected!")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(reportConstraint)).when(flagDefinition).getReportConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(),
            "Validation should fail because CRITICAL level report fired"),
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should have CRITICAL severity", handler.getFindings(),
            hasItem(hasProperty("severity", is(Level.CRITICAL)))));
  }

  /**
   * Test that report constraint uses the custom message in finding.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testReportConstraintCustomMessage() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("test"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    String customMessage = "This is a custom report message";

    // Create report constraint with custom message
    IReportConstraint reportConstraint = IReportConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("true()"))
        .message(customMessage)
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(reportConstraint)).when(flagDefinition).getReportConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertThat("should have 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding should have the custom message", handler.getFindings(),
            hasItem(hasProperty("message", is(customMessage)))));
  }

  /**
   * Test report vs expect semantics - verify they are opposites.
   * <p>
   * Report fires when test is TRUE. Expect fails when test is FALSE. Same
   * expression should produce opposite outcomes.
   *
   * @throws ConstraintValidationException
   *           if an error occurred during validation
   */
  @SuppressWarnings("null")
  @Test
  void testReportAndExpectAreOpposites() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    // Value that makes "contains(., 'deprecated')" return TRUE
    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("deprecated-value"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);

    ISource source = mock(ISource.class);

    // Report with test that evaluates to TRUE - should fire
    IReportConstraint reportConstraint = IReportConstraint.builder()
        .source(source)
        .level(Level.ERROR)
        .test(IMetapathExpression.compile("contains(., 'deprecated')"))
        .message("Report: deprecated detected")
        .build();

    // Expect with same test that evaluates to TRUE - should NOT fire
    IExpectConstraint expectConstraint = IExpectConstraint.builder()
        .source(source)
        .level(Level.ERROR)
        .test(IMetapathExpression.compile("contains(., 'deprecated')"))
        .message("Expect: deprecated NOT detected")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.singletonList(expectConstraint)).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(reportConstraint)).when(flagDefinition).getReportConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    // Report fires when TRUE (has finding), Expect passes when TRUE (no finding)
    // So only Report should generate a finding
    assertAll(
        () -> assertFalse(handler.isPassing(),
            "Validation should fail because Report with ERROR level fired"),
        () -> assertThat("should have exactly 1 finding (from Report, not Expect)",
            handler.getFindings(), hasSize(1)),
        () -> assertThat("finding message should be from Report constraint",
            handler.getFindings(),
            hasItem(hasProperty("message", is("Report: deprecated detected")))));
  }

  /**
   * A test visitor implementation to verify the visitor pattern.
   */
  private static final class TestConstraintVisitor implements IConstraintVisitor<Void, Boolean> {
    private int visitReportCount;

    int getVisitReportCount() {
      return visitReportCount;
    }

    @Override
    public Boolean visitAllowedValues(@NonNull IAllowedValuesConstraint constraint, Void state) {
      return Boolean.FALSE;
    }

    @Override
    public Boolean visitCardinalityConstraint(@NonNull ICardinalityConstraint constraint, Void state) {
      return Boolean.FALSE;
    }

    @Override
    public Boolean visitExpectConstraint(@NonNull IExpectConstraint constraint, Void state) {
      return Boolean.FALSE;
    }

    @Override
    public Boolean visitMatchesConstraint(@NonNull IMatchesConstraint constraint, Void state) {
      return Boolean.FALSE;
    }

    @Override
    public Boolean visitIndexConstraint(@NonNull IIndexConstraint constraint, Void state) {
      return Boolean.FALSE;
    }

    @Override
    public Boolean visitIndexHasKeyConstraint(@NonNull IIndexHasKeyConstraint constraint, Void state) {
      return Boolean.FALSE;
    }

    @Override
    public Boolean visitUniqueConstraint(@NonNull IUniqueConstraint constraint, Void state) {
      return Boolean.FALSE;
    }

    @Override
    public Boolean visitReportConstraint(@NonNull IReportConstraint constraint, Void state) {
      visitReportCount++;
      return Boolean.TRUE;
    }
  }
}
