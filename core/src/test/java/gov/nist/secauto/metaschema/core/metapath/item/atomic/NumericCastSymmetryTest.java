/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests to verify that IDecimalItem.cast() and INumericItem.cast() produce
 * equivalent results for the same inputs, ensuring behavioral symmetry.
 */
public class NumericCastSymmetryTest {

  /**
   * Test cases where both cast methods should succeed and produce equivalent
   * numeric values.
   */
  private static Stream<Arguments> provideValidCastInputs() {
    return Stream.of(
        // Integer inputs
        Arguments.of(integer(0), "zero integer"),
        Arguments.of(integer(1), "positive integer"),
        Arguments.of(integer(-1), "negative integer"),
        Arguments.of(integer(12345), "large positive integer"),
        Arguments.of(integer(-12345), "large negative integer"),
        // Decimal inputs
        Arguments.of(decimal("0.0"), "zero decimal"),
        Arguments.of(decimal("1.5"), "positive decimal"),
        Arguments.of(decimal("-1.5"), "negative decimal"),
        Arguments.of(decimal("123.456"), "large positive decimal"),
        Arguments.of(decimal("-123.456"), "large negative decimal"),
        // String inputs that represent valid numbers
        Arguments.of(string("0"), "string zero"),
        Arguments.of(string("123"), "string integer"),
        Arguments.of(string("-456"), "string negative integer"),
        Arguments.of(string("1.5"), "string decimal"),
        Arguments.of(string("-2.5"), "string negative decimal"),
        // Boolean inputs
        Arguments.of(IBooleanItem.TRUE, "boolean true"),
        Arguments.of(IBooleanItem.FALSE, "boolean false"));
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource("provideValidCastInputs")
  void testCastSymmetry(@NonNull IAnyAtomicItem input, @NonNull String description) {
    // Cast using IDecimalItem.cast
    IDecimalItem decimalResult = IDecimalItem.cast(input);

    // Cast using INumericItem.cast
    INumericItem numericResult = INumericItem.cast(input);

    // Both should produce equivalent numeric values (using compareTo for
    // scale-independent comparison)
    assertEquals(
        0,
        decimalResult.asDecimal().compareTo(numericResult.asDecimal()),
        "IDecimalItem.cast and INumericItem.cast should produce same numeric value for: " + description);
  }

  /**
   * Test cases where cast should fail for both methods.
   */
  private static Stream<Arguments> provideInvalidCastInputs() {
    return Stream.of(
        Arguments.of(string("abc"), "non-numeric string"),
        Arguments.of(string(""), "empty string"),
        Arguments.of(string("1.2.3"), "invalid number format"));
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource("provideInvalidCastInputs")
  void testInvalidCastSymmetry(@NonNull IAnyAtomicItem input, @NonNull String description) {
    // Both should throw InvalidValueForCastFunctionException
    assertThrows(
        InvalidValueForCastFunctionException.class,
        () -> IDecimalItem.cast(input),
        "IDecimalItem.cast should throw for: " + description);

    assertThrows(
        InvalidValueForCastFunctionException.class,
        () -> INumericItem.cast(input),
        "INumericItem.cast should throw for: " + description);
  }

  /**
   * Test that numeric items are returned efficiently (identity check for already
   * numeric items).
   */
  private static Stream<Arguments> provideNumericInputs() {
    return Stream.of(
        Arguments.of(integer(42), "integer item"),
        Arguments.of(decimal("3.14"), "decimal item"));
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource("provideNumericInputs")
  void testNumericPassthrough(@NonNull INumericItem input, @NonNull String description) {
    // INumericItem.cast should return the same instance for already-numeric items
    INumericItem result = INumericItem.cast(input);
    assertSame(input, result, "INumericItem.cast should return same instance for: " + description);
  }
}
