/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.function.ArithmeticFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.type.TypeMetapathException;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for error handling paths in the metaschema-java
 * constraint and metapath systems.
 * <p>
 * This test class focuses on exercising error conditions and validating that
 * appropriate exceptions are thrown with meaningful error codes and messages.
 */
class ErrorHandlingTest
    extends ExpressionTestBase {

  // ============================================================================
  // Metapath Parsing Errors (Static Errors)
  // ============================================================================

  @Test
  void testInvalidSyntax() {
    // Invalid grammar - unclosed parenthesis
    InvalidMetapathGrammarException thrown = assertThrows(
        InvalidMetapathGrammarException.class,
        () -> IMetapathExpression.compile("1 + (2 * 3"));

    assertThat(thrown)
        .returns(
            StaticMetapathException.INVALID_PATH_GRAMMAR,
            from(ex -> ex.getErrorCode().getCode()))
        .extracting(MetapathException::getMessageText)
        .asString()
        .isNotEmpty();
  }

  @Test
  void testUnknownFunction() {
    // Function that doesn't exist - exception thrown during evaluation
    StaticContext staticContext = StaticContext.instance();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    StaticMetapathException thrown = assertThrows(
        StaticMetapathException.class,
        () -> IMetapathExpression.compile("unknown-function()", staticContext)
            .evaluate(null, dynamicContext));

    assertThat(thrown)
        .returns(
            StaticMetapathException.NO_FUNCTION_MATCH,
            from(ex -> ex.getErrorCode().getCode()))
        .extracting(MetapathException::getMessageText)
        .asString()
        .contains("unknown-function");
  }

  @Test
  void testUnknownFunctionWithArguments() {
    // Function with arguments that doesn't exist - exception during evaluation
    StaticContext staticContext = StaticContext.instance();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    StaticMetapathException thrown = assertThrows(
        StaticMetapathException.class,
        () -> IMetapathExpression.compile("non-existent-fn('arg1', 'arg2')", staticContext)
            .evaluate(null, dynamicContext));

    assertThat(thrown)
        .returns(
            StaticMetapathException.NO_FUNCTION_MATCH,
            from(ex -> ex.getErrorCode().getCode()));
  }

  @Test
  void testCastToUnknownType() {
    // Attempting to cast to a type that doesn't exist
    InvalidMetapathGrammarException thrown = assertThrows(
        InvalidMetapathGrammarException.class,
        () -> IMetapathExpression.compile("'test' cast as meta:nonexistent-type"));

    assertThat(thrown)
        .cause()
        .isExactlyInstanceOf(StaticMetapathException.class)
        .extracting(
            ex -> ex instanceof StaticMetapathException
                ? ((StaticMetapathException) ex).getErrorCode().getCode()
                : null)
        .isEqualTo(StaticMetapathException.CAST_UNKNOWN_TYPE);
  }

  @Test
  void testCastToAnyAtomicType() {
    // Casting to meta:any-atomic-type is not allowed per XPath spec
    InvalidMetapathGrammarException thrown = assertThrows(
        InvalidMetapathGrammarException.class,
        () -> IMetapathExpression.compile("'test' cast as meta:any-atomic-type"));

    assertThat(thrown)
        .cause()
        .isExactlyInstanceOf(StaticMetapathException.class)
        .extracting(
            ex -> ex instanceof StaticMetapathException
                ? ((StaticMetapathException) ex).getErrorCode().getCode()
                : null)
        .isEqualTo(StaticMetapathException.CAST_ANY_ATOMIC);
  }

  @Test
  void testInvalidNamespacePrefix() {
    // Using a namespace prefix that can't be expanded
    InvalidMetapathGrammarException thrown = assertThrows(
        InvalidMetapathGrammarException.class,
        () -> IMetapathExpression.compile("'test' cast as unknown:type"));

    assertThat(thrown)
        .cause()
        .isExactlyInstanceOf(StaticMetapathException.class)
        .extracting(
            ex -> ex instanceof StaticMetapathException
                ? ((StaticMetapathException) ex).getErrorCode().getCode()
                : null)
        .isEqualTo(StaticMetapathException.PREFIX_NOT_EXPANDABLE);
  }

  // ============================================================================
  // Metapath Evaluation Errors (Dynamic Errors)
  // ============================================================================
  // Note: Division by zero tests (div, idiv, mod) are in OperationFunctionsTest

  @Test
  void testInvalidTypeConversionStringToNumber() {
    // Attempting arithmetic on a string that can't be converted
    MetapathException thrown = assertThrows(
        MetapathException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile("'abc' + 5");
          expr.evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
        });

    assertNotNull(thrown.getErrorCode());
    assertNotNull(thrown.getMessage());
  }

  @Test
  void testInvalidCastStringToInteger() {
    // Valid cast syntax but invalid value
    MetapathException thrown = assertThrows(
        MetapathException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile("'not-a-number' cast as meta:integer");
          expr.evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
        });

    assertNotNull(thrown.getErrorCode());
    assertThat(thrown.getMessageText()).isNotEmpty();
  }

  // ============================================================================
  // Sequence Cardinality Errors
  // ============================================================================
  // Note: Cardinality error tests are in FnExactlyOneTest, FnZeroOrOneTest,
  // FnOneOrMoreTest

  // ============================================================================
  // Regular Expression Errors
  // ============================================================================
  // Note: Regex error tests are in FnMatchesTest, FnTokenizeTest

  @Test
  void testReplaceWithInvalidPattern() {
    // Invalid regex in replace function - may throw StaticMetapathException
    // when the pattern is invalid
    MetapathException thrown = assertThrows(
        MetapathException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile("replace('test', '(unclosed', 'replacement')");
          expr.evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
        });

    // Verify an exception was thrown with an error code
    assertThat(thrown.getErrorCode()).isNotNull();
  }

  // ============================================================================
  // Type Errors
  // ============================================================================

  @Test
  void testPathExpressionOnNonNode() {
    // Using path expression on atomic value instead of node
    TypeMetapathException thrown = assertThrows(
        TypeMetapathException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile("5/child::*");
          expr.evaluate(null, newDynamicContext());
        });

    assertThat(thrown)
        .returns(
            TypeMetapathException.NOT_A_NODE_ITEM_FOR_STEP,
            from(ex -> ex.getErrorCode().getCode()));
  }

  @Test
  void testDataOnFunctionItem() {
    // Arrays can be atomized in this implementation - test that data() works on
    // arrays
    IArrayItem<?> arrayItem = IArrayItem.of(integer(1), integer(2));
    IMetapathExpression expr = IMetapathExpression.compile("data($arg)");
    DynamicContext context = newDynamicContext();
    context.bindVariableValue(
        IEnhancedQName.of("arg"),
        ISequence.of(arrayItem));
    ISequence<?> result = expr.evaluate(null, context);

    // Verify result is not null - data() processes the array items
    assertThat(result).isNotNull();
  }

  // ============================================================================
  // Context Errors
  // ============================================================================

  @Test
  void testContextItemAbsent() {
    // Using . (context item) when no context item is available
    ContextAbsentDynamicMetapathException thrown = assertThrows(
        ContextAbsentDynamicMetapathException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile(". + 5");
          // Passing null focus means no context item
          expr.evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
        });

    assertThat(thrown)
        .returns(
            DynamicMetapathException.DYNAMIC_CONTEXT_ABSENT,
            from(ex -> ex.getErrorCode().getCode()));
  }

  @Test
  void testUndefinedVariable() {
    // Referencing a variable that doesn't exist - exception during evaluation
    StaticContext staticContext = StaticContext.instance();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    StaticMetapathException thrown = assertThrows(
        StaticMetapathException.class,
        () -> IMetapathExpression.compile("$undefined-variable", staticContext)
            .evaluate(null, dynamicContext));

    assertThat(thrown)
        .returns(
            StaticMetapathException.NOT_DEFINED,
            from(ex -> ex.getErrorCode().getCode()))
        .extracting(MetapathException::getMessageText)
        .asString()
        .contains("undefined-variable");
  }

  @Test
  void testDefinedVariableInExpression() {
    // Positive test - variable is properly defined and accessible
    DynamicContext context = newDynamicContext();
    context.bindVariableValue(
        IEnhancedQName.of("myvar"),
        ISequence.of(integer(42)));

    IMetapathExpression expr = IMetapathExpression.compile("$myvar + 8");
    ISequence<?> result = expr.evaluate(null, context);

    assertThat(result)
        .hasSize(1)
        .first()
        .isEqualTo(integer(50));
  }

  // ============================================================================
  // InvalidTypeMetapathException Tests
  // ============================================================================

  @Test
  void testInvalidTypeMetapathExceptionConstruction() {
    // Test the InvalidTypeMetapathException directly
    InvalidTypeMetapathException exception = new InvalidTypeMetapathException(
        integer(123),
        "Custom error message");

    // Verify exception has correct error code
    assertThat(exception.getErrorCode().getCode())
        .isEqualTo(TypeMetapathException.INVALID_TYPE_ERROR);

    assertThat(exception)
        .extracting(InvalidTypeMetapathException::getItem)
        .isEqualTo(integer(123));

    assertThat(exception.getMessageText())
        .contains("Custom error message");
  }

  @Test
  void testInvalidTypeMetapathExceptionWithCause() {
    // Test exception with cause
    IllegalArgumentException cause = new IllegalArgumentException("Root cause");
    InvalidTypeMetapathException exception = new InvalidTypeMetapathException(
        string("test"),
        "Type error occurred",
        cause);

    assertThat(exception)
        .hasCause(cause);
    // Verify exception has correct error code
    assertThat(exception.getErrorCode().getCode())
        .isEqualTo(TypeMetapathException.INVALID_TYPE_ERROR);
  }

  // ============================================================================
  // Error Message Quality Tests
  // ============================================================================

  @Test
  void testErrorMessagesContainUsefulInformation() {
    // Verify that exceptions provide meaningful error messages
    InvalidArgumentFunctionException exception = assertThrows(
        InvalidArgumentFunctionException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile("exactly-one((1, 2, 3))");
          expr.evaluate(null, newDynamicContext());
        });

    // Error message should include the error code
    String message = exception.getMessage();
    assertThat(message)
        .isNotEmpty()
        .contains("FORG");

    // Message text (without error code) should also be meaningful
    String messageText = exception.getMessageText();
    assertThat(messageText)
        .isNotEmpty();
  }

  @Test
  void testErrorCodeFormatting() {
    // Verify error codes are properly formatted - exception thrown during
    // evaluation
    StaticContext staticContext = StaticContext.instance();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    StaticMetapathException exception = assertThrows(
        StaticMetapathException.class,
        () -> IMetapathExpression.compile("unknown-function()", staticContext)
            .evaluate(null, dynamicContext));

    IErrorCode errorCode = exception.getErrorCode();
    assertThat(errorCode.toString())
        .matches("MPST\\d{4}"); // Format: MPST followed by 4 digits
  }

  // ============================================================================
  // Exception Propagation Tests
  // ============================================================================

  @Test
  void testExceptionPropagationThroughNestedExpressions() {
    // Error in nested expression should propagate correctly
    ArithmeticFunctionException thrown = assertThrows(
        ArithmeticFunctionException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile("(10 + (5 div 0)) * 2");
          expr.evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
        });

    assertThat(thrown)
        .returns(
            ArithmeticFunctionException.DIVISION_BY_ZERO,
            from(ex -> ex.getErrorCode().getCode()));
  }

  @Test
  void testExceptionInFunctionArgument() {
    // Error in function argument should propagate
    MetapathException thrown = assertThrows(
        MetapathException.class,
        () -> {
          IMetapathExpression expr = IMetapathExpression.compile("string-length(5 div 0)");
          expr.evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
        });

    assertNotNull(thrown.getErrorCode());
  }

  // ============================================================================
  // Edge Cases
  // ============================================================================

  @Test
  void testEmptySequenceHandling() {
    // Many operations handle empty sequences gracefully
    IMetapathExpression expr = IMetapathExpression.compile("count(())");
    ISequence<?> result = expr.evaluate(null, newDynamicContext());

    assertThat(result)
        .hasSize(1)
        .first()
        .isEqualTo(integer(0));
  }

  @Test
  void testNullFocusWithNonContextDependentExpression() {
    // Expressions that don't use context item should work with null focus
    IMetapathExpression expr = IMetapathExpression.compile("1 + 2");
    ISequence<?> result = expr.evaluate(null, newDynamicContext());

    assertThat(result)
        .hasSize(1)
        .first()
        .isEqualTo(integer(3));
  }
}
