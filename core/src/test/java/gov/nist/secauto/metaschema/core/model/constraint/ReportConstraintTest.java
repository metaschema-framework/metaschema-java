/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Type;

import org.junit.jupiter.api.Test;

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
 * </ul>
 */
class ReportConstraintTest {
  @NonNull
  private static final String TEST_SOURCE = "https://example.com/test";

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
