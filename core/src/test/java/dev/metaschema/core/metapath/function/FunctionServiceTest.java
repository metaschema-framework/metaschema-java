/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function;

import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.StaticMetapathException;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.type.ISequenceType;
import dev.metaschema.core.metapath.type.Occurrence;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for the Metapath function dispatch and type handling mechanism.
 * <p>
 * This test class focuses on the function service infrastructure rather than
 * individual function implementations. It verifies:
 * <ul>
 * <li>Function registration and lookup by name and arity
 * <li>Function signature matching
 * <li>Function execution with various argument types
 * <li>Error handling for missing functions
 * <li>Default function library loading
 * </ul>
 */
class FunctionServiceTest {

  /**
   * Verify that the FunctionService singleton is properly initialized and
   * accessible.
   */
  @Test
  void testGetInstance() {
    FunctionService service = FunctionService.getInstance();
    assertNotNull(service, "FunctionService instance should not be null");
    assertEquals(service, FunctionService.getInstance(),
        "getInstance() should return the same singleton instance");
  }

  /**
   * Verify that the default function library is loaded with standard XPath
   * functions.
   */
  @Test
  void testDefaultFunctionLibraryLoaded() {
    FunctionService service = FunctionService.getInstance();

    // Verify that standard functions are available
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IEnhancedQName concatName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "concat");

    assertAll(
        () -> assertNotNull(service.getFunction(countName, 1),
            "fn:count with arity 1 should be available"),
        () -> assertNotNull(service.getFunction(concatName, 2),
            "fn:concat with arity 2 should be available"));
  }

  /**
   * Verify that function lookup by name and arity works correctly.
   */
  @Test
  void testFunctionLookupByNameAndArity() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");

    IFunction function = service.getFunction(countName, 1);

    assertAll(
        () -> assertNotNull(function, "Function should be found"),
        () -> assertEquals(countName, function.getQName(), "Function name should match"),
        () -> assertEquals(1, function.arity(), "Function arity should match"));
  }

  /**
   * Verify that looking up a non-existent function throws the appropriate
   * exception.
   */
  @Test
  void testFunctionLookupNonExistent() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName nonExistentName = IEnhancedQName.of(
        MetapathConstants.NS_METAPATH_FUNCTIONS,
        "this-function-does-not-exist");

    StaticMetapathException exception = assertThrows(StaticMetapathException.class,
        () -> service.getFunction(nonExistentName, 1),
        "Looking up non-existent function should throw StaticMetapathException");

    assertEquals(StaticMetapathException.NO_FUNCTION_MATCH,
        exception.getErrorCode().getCode(),
        "Exception should have NO_FUNCTION_MATCH error code");
  }

  /**
   * Verify that looking up a function with the wrong arity throws the appropriate
   * exception.
   */
  @Test
  void testFunctionLookupWrongArity() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");

    // fn:count only has arity 1, so arity 2 should fail
    StaticMetapathException exception = assertThrows(StaticMetapathException.class,
        () -> service.getFunction(countName, 2),
        "Looking up function with wrong arity should throw StaticMetapathException");

    assertEquals(StaticMetapathException.NO_FUNCTION_MATCH,
        exception.getErrorCode().getCode(),
        "Exception should have NO_FUNCTION_MATCH error code");
  }

  /**
   * Verify that functions can be executed with correct argument types.
   */
  @Test
  void testFunctionExecutionWithCorrectTypes() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    // Execute fn:count with a sequence of items
    ISequence<?> result = function.execute(
        CollectionUtil.singletonList(ISequence.of(string("a"), string("b"), string("c"))),
        new DynamicContext(),
        ISequence.empty());

    assertAll(
        () -> assertEquals(1, result.size(), "Result should have one item"),
        () -> assertTrue(result.getFirstItem(true) instanceof IIntegerItem,
            "Result should be an integer"),
        () -> assertEquals(integer(3), result.getFirstItem(true),
            "Count should be 3"));
  }

  /**
   * Verify that function stream returns multiple functions.
   */
  @Test
  void testFunctionStream() {
    FunctionService service = FunctionService.getInstance();

    List<IFunction> functions = service.stream().collect(Collectors.toList());

    assertAll(
        () -> assertFalse(functions.isEmpty(), "Function stream should not be empty"),
        // Check for presence of well-known functions rather than a specific count
        // which would be brittle as the library evolves
        () -> assertTrue(functions.stream()
            .anyMatch(f -> "count".equals(f.getName())),
            "Stream should contain fn:count"),
        () -> assertTrue(functions.stream()
            .anyMatch(f -> "concat".equals(f.getName())),
            "Stream should contain fn:concat"));
  }

  /**
   * Verify that function signatures are properly formed and accessible.
   */
  @Test
  void testFunctionSignature() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    assertAll(
        () -> assertEquals("count", function.getName(), "Function name should be 'count'"),
        () -> assertEquals(countName, function.getQName(), "QName should match"),
        () -> assertEquals(1, function.arity(), "Arity should be 1"),
        () -> assertEquals(1, function.getArguments().size(), "Should have 1 argument"),
        () -> assertNotNull(function.getResult(), "Should have a result type"));
  }

  /**
   * Verify that function argument signatures are accessible and well-formed.
   */
  @Test
  void testFunctionArgumentSignature() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    IArgument argument = function.getArguments().get(0);

    assertAll(
        () -> assertNotNull(argument.getName(), "Argument should have a name"),
        () -> assertNotNull(argument.getSequenceType(), "Argument should have a sequence type"),
        () -> assertEquals(IItem.type(), argument.getSequenceType().getType(),
            "Argument type should be item()"),
        () -> assertEquals(Occurrence.ZERO_OR_MORE, argument.getSequenceType().getOccurrence(),
            "Argument occurrence should be zero or more"));
  }

  /**
   * Verify that function properties are correctly reported.
   */
  @Test
  void testFunctionProperties() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    assertAll(
        () -> assertTrue(function.isDeterministic(),
            "fn:count should be deterministic"),
        () -> assertFalse(function.isContextDepenent(),
            "fn:count should be context independent"),
        () -> assertFalse(function.isFocusDependent(),
            "fn:count should be focus independent"),
        () -> assertFalse(function.isArityUnbounded(),
            "fn:count should not have unbounded arity"));
  }

  /**
   * Verify that the concat function has unbounded arity.
   */
  @Test
  void testUnboundedArityFunction() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName concatName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "concat");

    // fn:concat has arity 2 minimum but is unbounded
    IFunction concatFunction = service.getFunction(concatName, 2);

    assertAll(
        () -> assertNotNull(concatFunction, "fn:concat should exist with arity 2"),
        () -> assertTrue(concatFunction.isArityUnbounded(),
            "fn:concat should have unbounded arity"),
        () -> assertEquals(2, concatFunction.arity(),
            "fn:concat base arity should be 2"));
  }

  /**
   * Verify that unbounded arity functions can be looked up with higher arity.
   */
  @Test
  void testUnboundedArityLookupWithHigherArity() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName concatName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "concat");

    // fn:concat should be findable with arity 3, 4, etc.
    assertAll(
        () -> assertNotNull(service.getFunction(concatName, 3),
            "fn:concat should be found with arity 3"),
        () -> assertNotNull(service.getFunction(concatName, 5),
            "fn:concat should be found with arity 5"),
        () -> assertNotNull(service.getFunction(concatName, 10),
            "fn:concat should be found with arity 10"));
  }

  /**
   * Verify that function library can register custom functions.
   */
  @Test
  void testCustomFunctionRegistration() {
    FunctionLibrary library = new FunctionLibrary();

    IEnhancedQName customName = IEnhancedQName.of("http://example.com/functions", "custom");

    IFunction customFunction = IFunction.builder()
        .name("custom")
        .namespace("http://example.com/functions")
        .deterministic()
        .argument(IArgument.builder()
            .name("arg1")
            .type(IStringItem.type())
            .one()
            .build())
        .returnType(IStringItem.type())
        .returnOne()
        .functionHandler((function, arguments, dynamicContext, focus) -> arguments.get(0))
        .build();

    library.registerFunction(customFunction);

    IFunction retrieved = library.getFunction(customName, 1);

    assertAll(
        () -> assertNotNull(retrieved, "Custom function should be retrievable"),
        () -> assertEquals(customName, retrieved.getQName(), "QName should match"),
        () -> assertEquals(1, retrieved.arity(), "Arity should match"));
  }

  /**
   * Verify that registering duplicate functions with same arity throws an
   * exception.
   */
  @Test
  void testDuplicateFunctionRegistration() {
    FunctionLibrary library = new FunctionLibrary();

    IFunction function1 = IFunction.builder()
        .name("duplicate")
        .namespace("http://example.com/functions")
        .deterministic()
        .returnType(IItem.type())
        .returnOne()
        .functionHandler((function, arguments, dynamicContext, focus) -> ISequence.of(string("x")))
        .build();

    IFunction function2 = IFunction.builder()
        .name("duplicate")
        .namespace("http://example.com/functions")
        .deterministic()
        .returnType(IItem.type())
        .returnOne()
        .functionHandler((function, arguments, dynamicContext, focus) -> ISequence.of(string("x")))
        .build();

    library.registerFunction(function1);

    assertThrows(IllegalArgumentException.class,
        () -> library.registerFunction(function2),
        "Registering duplicate function with same arity should throw IllegalArgumentException");
  }

  /**
   * Verify that functions with different arities can coexist with the same name.
   */
  @Test
  void testMultipleAritiesForSameFunction() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName substringName = IEnhancedQName.of(
        MetapathConstants.NS_METAPATH_FUNCTIONS,
        "substring");

    // fn:substring has both arity 2 and arity 3 versions
    assertAll(
        () -> assertNotNull(service.getFunction(substringName, 2),
            "fn:substring should exist with arity 2"),
        () -> assertNotNull(service.getFunction(substringName, 3),
            "fn:substring should exist with arity 3"));
  }

  /**
   * Verify that function result type information is accessible.
   */
  @Test
  void testFunctionResultType() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    ISequenceType resultType = function.getResult();

    assertAll(
        () -> assertNotNull(resultType, "Result type should not be null"),
        () -> assertEquals(IIntegerItem.type(), resultType.getType(),
            "Result item type should be integer"),
        () -> assertEquals(Occurrence.ONE, resultType.getOccurrence(),
            "Result occurrence should be one"));
  }

  /**
   * Verify that function builder enforces required fields.
   */
  @Test
  void testFunctionBuilderValidation() {
    assertAll(
        () -> assertThrows(NullPointerException.class,
            () -> IFunction.builder().build(),
            "Building function without required fields should throw NullPointerException"),
        () -> assertThrows(IllegalArgumentException.class,
            () -> IFunction.builder().name("").build(),
            "Building function with blank name should throw IllegalArgumentException"),
        () -> assertThrows(IllegalStateException.class,
            () -> IFunction.builder()
                .name("test")
                .namespace("http://example.com")
                .allowUnboundedArity(true)
                .returnType(IItem.type())
                .returnOne()
                .functionHandler((f, a, d, foc) -> ISequence.of(string("x")))
                .build(),
            "Building unbounded arity function without arguments should throw IllegalStateException"));
  }

  /**
   * Verify that argument builder enforces required fields.
   */
  @Test
  void testArgumentBuilderValidation() {
    assertAll(
        () -> assertThrows(NullPointerException.class,
            () -> IArgument.builder().build(),
            "Building argument without required fields should throw NullPointerException"),
        () -> assertThrows(IllegalArgumentException.class,
            () -> IArgument.builder().name("").build(),
            "Building argument with blank name should throw IllegalArgumentException"));
  }

  /**
   * Verify that function execution respects focus dependency.
   */
  @Test
  void testFocusDependentFunction() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName stringName = IEnhancedQName.of(
        MetapathConstants.NS_METAPATH_FUNCTIONS,
        "string");

    // fn:string() with no arguments is focus-dependent
    IFunction function = service.getFunction(stringName, 0);

    assertAll(
        () -> assertTrue(function.isFocusDependent(),
            "fn:string() with no arguments should be focus dependent"),
        () -> assertEquals(0, function.arity(),
            "fn:string() with no arguments should have arity 0"));
  }

  /**
   * Verify that function execution with empty sequences works correctly.
   */
  @Test
  void testFunctionExecutionWithEmptySequence() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    ISequence<?> result = function.execute(
        CollectionUtil.singletonList(ISequence.empty()),
        new DynamicContext(),
        ISequence.empty());

    assertAll(
        () -> assertEquals(1, result.size(), "Result should have one item"),
        () -> assertEquals(integer(0), result.getFirstItem(true),
            "Count of empty sequence should be 0"));
  }

  /**
   * Verify that function toSignature produces readable output.
   */
  @Test
  void testFunctionSignatureString() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    String signature = function.toSignature();

    assertAll(
        () -> assertNotNull(signature, "Signature should not be null"),
        () -> assertTrue(signature.contains("count"),
            "Signature should contain function name"),
        () -> assertTrue(signature.contains("as"),
            "Signature should contain 'as' keyword"),
        () -> assertTrue(signature.contains("integer"),
            "Signature should contain return type"));
  }

  /**
   * Verify that isNamedFunction returns true for registered functions.
   */
  @Test
  void testIsNamedFunction() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    assertTrue(function.isNamedFunction(),
        "Functions from the function library should be named functions");
  }

  /**
   * Verify that function properties can be set correctly.
   */
  @Test
  void testFunctionPropertiesBuilder() {
    IFunction deterministicFunction = IFunction.builder()
        .name("test-deterministic")
        .namespace("http://example.com")
        .deterministic()
        .contextIndependent()
        .focusIndependent()
        .returnType(IItem.type())
        .returnOne()
        .functionHandler((f, a, d, foc) -> ISequence.of(string("x")))
        .build();

    IFunction nonDeterministicFunction = IFunction.builder()
        .name("test-non-deterministic")
        .namespace("http://example.com")
        .nonDeterministic()
        .contextDependent()
        .focusDependent()
        .returnType(IItem.type())
        .returnOne()
        .functionHandler((f, a, d, foc) -> ISequence.of(string("x")))
        .build();

    assertAll(
        () -> assertTrue(deterministicFunction.isDeterministic(),
            "Deterministic function should report as deterministic"),
        () -> assertFalse(deterministicFunction.isContextDepenent(),
            "Context independent function should report as context independent"),
        () -> assertFalse(deterministicFunction.isFocusDependent(),
            "Focus independent function should report as focus independent"),
        () -> assertFalse(nonDeterministicFunction.isDeterministic(),
            "Non-deterministic function should not report as deterministic"),
        () -> assertTrue(nonDeterministicFunction.isContextDepenent(),
            "Context dependent function should report as context dependent"),
        () -> assertTrue(nonDeterministicFunction.isFocusDependent(),
            "Focus dependent function should report as focus dependent"));
  }

  /**
   * Verify that getItemTypes on sequence results includes correct type
   * information.
   */
  @Test
  void testFunctionResultItemTypes() {
    FunctionService service = FunctionService.getInstance();
    IEnhancedQName countName = IEnhancedQName.of(MetapathConstants.NS_METAPATH_FUNCTIONS, "count");
    IFunction function = service.getFunction(countName, 1);

    ISequence<?> result = function.execute(
        CollectionUtil.singletonList(ISequence.of(string("a"), string("b"))),
        new DynamicContext(),
        ISequence.empty());

    List<? extends Class<? extends IItem>> itemTypes = result.getItemTypes();

    assertAll(
        () -> assertNotNull(itemTypes, "Item types should not be null"),
        () -> assertEquals(1, itemTypes.size(), "Should have one item type"),
        () -> assertTrue(IIntegerItem.class.isAssignableFrom(itemTypes.get(0)),
            "Should contain integer item type"));
  }

  /**
   * Verify that argument occurrence types work correctly.
   */
  @Test
  void testArgumentOccurrenceTypes() {
    IArgument zeroOrOneArg = IArgument.builder()
        .name("test")
        .type(IStringItem.type())
        .zeroOrOne()
        .build();

    IArgument oneArg = IArgument.builder()
        .name("test")
        .type(IStringItem.type())
        .one()
        .build();

    IArgument zeroOrMoreArg = IArgument.builder()
        .name("test")
        .type(IStringItem.type())
        .zeroOrMore()
        .build();

    IArgument oneOrMoreArg = IArgument.builder()
        .name("test")
        .type(IStringItem.type())
        .oneOrMore()
        .build();

    assertAll(
        () -> assertEquals(Occurrence.ZERO_OR_ONE, zeroOrOneArg.getSequenceType().getOccurrence(),
            "Zero or one occurrence should be set correctly"),
        () -> assertEquals(Occurrence.ONE, oneArg.getSequenceType().getOccurrence(),
            "One occurrence should be set correctly"),
        () -> assertEquals(Occurrence.ZERO_OR_MORE, zeroOrMoreArg.getSequenceType().getOccurrence(),
            "Zero or more occurrence should be set correctly"),
        () -> assertEquals(Occurrence.ONE_OR_MORE, oneOrMoreArg.getSequenceType().getOccurrence(),
            "One or more occurrence should be set correctly"));
  }

  /**
   * Verify that return type occurrence methods work correctly.
   */
  @Test
  void testReturnTypeOccurrenceBuilder() {
    IFunction zeroOrOneFunc = createTestFunction(builder -> builder.returnZeroOrOne());
    IFunction oneFunc = createTestFunction(builder -> builder.returnOne());
    IFunction zeroOrMoreFunc = createTestFunction(builder -> builder.returnZeroOrMore());
    IFunction oneOrMoreFunc = createTestFunction(builder -> builder.returnOneOrMore());

    assertAll(
        () -> assertEquals(Occurrence.ZERO_OR_ONE, zeroOrOneFunc.getResult().getOccurrence(),
            "Return zero or one should be set correctly"),
        () -> assertEquals(Occurrence.ONE, oneFunc.getResult().getOccurrence(),
            "Return one should be set correctly"),
        () -> assertEquals(Occurrence.ZERO_OR_MORE, zeroOrMoreFunc.getResult().getOccurrence(),
            "Return zero or more should be set correctly"),
        () -> assertEquals(Occurrence.ONE_OR_MORE, oneOrMoreFunc.getResult().getOccurrence(),
            "Return one or more should be set correctly"));
  }

  /**
   * Helper method to create a test function with a builder customizer.
   *
   * @param customizer
   *          function to customize the builder
   * @return the built function
   */
  @NonNull
  private static IFunction createTestFunction(
      @NonNull java.util.function.Function<IFunction.Builder, IFunction.Builder> customizer) {
    IFunction.Builder builder = IFunction.builder()
        .name("test")
        .namespace("http://example.com")
        .deterministic()
        .returnType(IItem.type())
        .functionHandler((f, a, d, foc) -> ISequence.of(string("x")));

    return customizer.apply(builder).build();
  }
}
