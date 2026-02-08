/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import dev.metaschema.core.metapath.function.FormatDateTimeFunctionException;
import dev.metaschema.core.metapath.item.atomic.IDateItem;
import dev.metaschema.core.metapath.item.atomic.IDateTimeItem;
import dev.metaschema.core.metapath.item.atomic.ITemporalItem;
import dev.metaschema.core.metapath.item.atomic.ITimeItem;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Unit tests for the picture string parser and formatting engine in
 * {@link DateTimeFormatUtil}.
 * <p>
 * These tests verify that {@link DateTimeFormatUtil#parsePictureString(String)}
 * correctly parses XPath 3.1 picture strings (spec section 9.8) into a list of
 * {@link DateTimeFormatUtil.FormatComponent} objects, including literal text,
 * escaped brackets, variable markers with modifiers, width specifications, and
 * error handling.
 * <p>
 * Additionally, tests verify that
 * {@link DateTimeFormatUtil#formatDateTime(ITemporalItem, String, String, String, String, Set)}
 * correctly formats temporal values according to picture strings, covering all
 * component specifiers (Y, M, D, d, F, W, w, H, h, P, m, s, f, Z, z, C, E),
 * presentation modifiers, width modifiers, and error cases.
 *
 * @see <a href=
 *      "https://www.w3.org/TR/xpath-functions-31/#date-picture-string">XPath
 *      Functions 3.1 - Date Picture String</a>
 */
class DateTimeFormatUtilTest {

  // ====================================================================
  // Test Fixture Values
  // ====================================================================

  /** Date: December 31, 2002 (the W3C spec example date). */
  private static final IDateItem TEST_DATE = IDateItem.valueOf("2002-12-31");

  /** Time: 15:58:45.762 with +02:00 timezone. */
  private static final ITimeItem TEST_TIME = ITimeItem.valueOf("15:58:45.762+02:00");

  /** DateTime: combining the above date and time. */
  private static final IDateTimeItem TEST_DATETIME
      = IDateTimeItem.valueOf("2002-12-31T15:58:45.762+02:00");

  /** Allowed markers for date-only functions (format-date). */
  private static final Set<Character> DATE_MARKERS
      = Set.of('Y', 'M', 'D', 'd', 'F', 'W', 'w', 'C', 'E', 'Z', 'z');

  /** Allowed markers for time-only functions (format-time). */
  private static final Set<Character> TIME_MARKERS
      = Set.of('H', 'h', 'P', 'm', 's', 'f', 'Z', 'z');

  /** Allowed markers for dateTime functions (format-dateTime). */
  private static final Set<Character> ALL_MARKERS
      = Set.of('Y', 'M', 'D', 'd', 'F', 'W', 'w', 'H', 'h', 'P', 'm', 's', 'f', 'Z', 'z', 'C', 'E');

  // ====================================================================
  // Helper methods
  // ====================================================================

  /**
   * Assert that the given component is a
   * {@link DateTimeFormatUtil.LiteralComponent} with the expected text.
   *
   * @param component
   *          the component to check
   * @param expectedText
   *          the expected literal text
   */
  private static void assertLiteral(
      @NonNull DateTimeFormatUtil.FormatComponent component,
      @NonNull String expectedText) {
    assertInstanceOf(DateTimeFormatUtil.LiteralComponent.class, component);
    assertEquals(expectedText,
        ((DateTimeFormatUtil.LiteralComponent) component).getText());
  }

  /**
   * Assert that the given component is a
   * {@link DateTimeFormatUtil.VariableMarkerComponent} with the expected field
   * values.
   *
   * @param component
   *          the component to check
   * @param specifier
   *          the expected component specifier character
   * @param primaryModifier
   *          the expected primary modifier string, or {@code null}
   * @param secondModifier
   *          the expected second modifier character, or {@code null}
   * @param minWidth
   *          the expected minimum width, or {@code null}
   * @param maxWidth
   *          the expected maximum width, or {@code null}
   */
  private static void assertMarker(
      @NonNull DateTimeFormatUtil.FormatComponent component,
      char specifier,
      @Nullable String primaryModifier,
      @Nullable Character secondModifier,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth) {
    assertInstanceOf(DateTimeFormatUtil.VariableMarkerComponent.class, component);
    DateTimeFormatUtil.VariableMarkerComponent marker
        = (DateTimeFormatUtil.VariableMarkerComponent) component;
    assertEquals(specifier, marker.getSpecifier(), "specifier");
    assertEquals(primaryModifier, marker.getPrimaryModifier(), "primaryModifier");
    assertEquals(secondModifier, marker.getSecondModifier(), "secondModifier");
    assertEquals(minWidth, marker.getMinWidth(), "minWidth");
    assertEquals(maxWidth, marker.getMaxWidth(), "maxWidth");
  }

  /**
   * Format a temporal value using the given picture string, with default
   * language, calendar, and place parameters.
   *
   * @param value
   *          the temporal value to format
   * @param picture
   *          the picture string
   * @param allowedMarkers
   *          the set of allowed component specifier characters
   * @return the formatted string
   */
  private static String format(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull Set<Character> allowedMarkers) {
    return DateTimeFormatUtil.formatDateTime(value, picture, null, null, null, allowedMarkers);
  }

  // ====================================================================
  // Group 1: Literal Text
  // ====================================================================

  @Test
  void testParseLiteralText() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("hello");

    assertEquals(1, result.size());
    assertLiteral(result.get(0), "hello");
  }

  @Test
  void testParseEmptyString() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("");

    assertTrue(result.isEmpty());
  }

  @Test
  void testParseWhitespaceLiteral() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("  ");

    assertEquals(1, result.size());
    assertLiteral(result.get(0), "  ");
  }

  // ====================================================================
  // Group 2: Escaped Brackets
  // ====================================================================

  @Test
  void testParseEscapedOpenBracket() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[[");

    assertEquals(1, result.size());
    assertLiteral(result.get(0), "[");
  }

  @Test
  void testParseEscapedCloseBracket() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("]]");

    assertEquals(1, result.size());
    assertLiteral(result.get(0), "]");
  }

  @Test
  void testParseEscapedBracketsAroundContent() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[[value]]");

    assertEquals(1, result.size());
    assertLiteral(result.get(0), "[value]");
  }

  // ====================================================================
  // Group 3: Simple Variable Markers
  // ====================================================================

  @Test
  void testParseSimpleYearMarker() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Y]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'Y', null, null, null, null);
  }

  @Test
  void testParseSimpleMonthMarker() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[M]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', null, null, null, null);
  }

  @Test
  void testParseSimpleDayMarker() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[D]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'D', null, null, null, null);
  }

  @Test
  void testParseAllSimpleMarkers() {
    char[] specifiers = { 'Y', 'M', 'D', 'd', 'F', 'W', 'w', 'H', 'h', 'P',
        'm', 's', 'f', 'Z', 'z', 'C', 'E' };

    for (char specifier : specifiers) {
      String picture = "[" + specifier + "]";
      List<DateTimeFormatUtil.FormatComponent> result
          = DateTimeFormatUtil.parsePictureString(picture);

      assertEquals(1, result.size(),
          "Expected 1 component for picture: " + picture);
      assertMarker(result.get(0), specifier, null, null, null, null);
    }
  }

  // ====================================================================
  // Group 4: Whitespace in Markers
  // ====================================================================

  @Test
  void testParseMarkerWithWhitespace() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[ Y 0001 ]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'Y', "0001", null, null, null);
  }

  // ====================================================================
  // Group 5: Markers with Primary Modifier
  // ====================================================================

  @Test
  void testParseYearWithDecimalPattern() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Y0001]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'Y', "0001", null, null, null);
  }

  @Test
  void testParseMonthWithZeroPadded() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[M01]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "01", null, null, null);
  }

  @Test
  void testParseMonthTitleCase() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[MNn]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "Nn", null, null, null);
  }

  @Test
  void testParseMonthUpperCase() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[MN]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "N", null, null, null);
  }

  @Test
  void testParseMonthLowerCase() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Mn]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "n", null, null, null);
  }

  @Test
  void testParseMonthRomanLower() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Mi]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "i", null, null, null);
  }

  @Test
  void testParseMonthRomanUpper() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[MI]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "I", null, null, null);
  }

  @Test
  void testParseMonthWords() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Mw]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "w", null, null, null);
  }

  // ====================================================================
  // Group 6: Markers with Second Modifier
  // ====================================================================

  @Test
  void testParseDayOrdinal() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[D1o]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'D', "1", 'o', null, null);
  }

  @Test
  void testParseDayCardinal() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[D1c]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'D', "1", 'c', null, null);
  }

  @Test
  void testParseDayWordsOrdinal() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Dwo]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'D', "w", 'o', null, null);
  }

  // ====================================================================
  // Group 7: Width Modifiers
  // ====================================================================

  @Test
  void testParseMonthMinWidth() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[M,2]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', null, null, 2, null);
  }

  @Test
  void testParseMonthExactWidth() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[M,2-2]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', null, null, 2, 2);
  }

  @Test
  void testParseNameMaxWidth() {
    // "*" for minWidth means unbounded, represented as null
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[MNn,*-3]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "Nn", null, null, 3);
  }

  @Test
  void testParseNameExactWidth() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[MNn,3-3]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'M', "Nn", null, 3, 3);
  }

  @Test
  void testParseYearWidthOnly() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Y,4-4]");

    assertEquals(1, result.size());
    assertMarker(result.get(0), 'Y', null, null, 4, 4);
  }

  // ====================================================================
  // Group 8: Complex Picture Strings
  // ====================================================================

  @Test
  void testParseIsoDateFormat() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[Y0001]-[M01]-[D01]");

    assertEquals(5, result.size());
    assertMarker(result.get(0), 'Y', "0001", null, null, null);
    assertLiteral(result.get(1), "-");
    assertMarker(result.get(2), 'M', "01", null, null, null);
    assertLiteral(result.get(3), "-");
    assertMarker(result.get(4), 'D', "01", null, null, null);
  }

  @Test
  void testParseDateWithLiterals() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[D] [MNn] [Y]");

    assertEquals(5, result.size());
    assertMarker(result.get(0), 'D', null, null, null, null);
    assertLiteral(result.get(1), " ");
    assertMarker(result.get(2), 'M', "Nn", null, null, null);
    assertLiteral(result.get(3), " ");
    assertMarker(result.get(4), 'Y', null, null, null, null);
  }

  @Test
  void testParseTimeFormat() {
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[h]:[m01]:[s01] [P]");

    assertEquals(7, result.size());
    assertMarker(result.get(0), 'h', null, null, null, null);
    assertLiteral(result.get(1), ":");
    assertMarker(result.get(2), 'm', "01", null, null, null);
    assertLiteral(result.get(3), ":");
    assertMarker(result.get(4), 's', "01", null, null, null);
    assertLiteral(result.get(5), " ");
    assertMarker(result.get(6), 'P', null, null, null, null);
  }

  @Test
  void testParseEscapedBracketsWithDate() {
    // [[[Y0001]-[M01]-[D01]]]
    // Parsing: [[ = literal "[", then [Y0001], literal "-", [M01], literal "-",
    // [D01], ]] = literal "]"
    List<DateTimeFormatUtil.FormatComponent> result
        = DateTimeFormatUtil.parsePictureString("[[[Y0001]-[M01]-[D01]]]");

    assertEquals(7, result.size());
    assertLiteral(result.get(0), "[");
    assertMarker(result.get(1), 'Y', "0001", null, null, null);
    assertLiteral(result.get(2), "-");
    assertMarker(result.get(3), 'M', "01", null, null, null);
    assertLiteral(result.get(4), "-");
    assertMarker(result.get(5), 'D', "01", null, null, null);
    assertLiteral(result.get(6), "]");
  }

  // ====================================================================
  // Group 9: Error Cases
  // ====================================================================

  @Test
  void testParseUnknownMarker() {
    assertThrows(FormatDateTimeFunctionException.class,
        () -> DateTimeFormatUtil.parsePictureString("[X]"));
  }

  @Test
  void testParseUnmatchedOpenBracket() {
    assertThrows(FormatDateTimeFunctionException.class,
        () -> DateTimeFormatUtil.parsePictureString("[Y"));
  }

  @Test
  void testParseUnmatchedCloseBracket() {
    assertThrows(FormatDateTimeFunctionException.class,
        () -> DateTimeFormatUtil.parsePictureString("]"));
  }

  @Test
  void testParseMinWidthLessThanOne() {
    assertThrows(FormatDateTimeFunctionException.class,
        () -> DateTimeFormatUtil.parsePictureString("[M,0]"));
  }

  @Test
  void testParseMaxLessThanMin() {
    assertThrows(FormatDateTimeFunctionException.class,
        () -> DateTimeFormatUtil.parsePictureString("[M,3-1]"));
  }

  // ====================================================================
  // Formatting Engine Tests
  // ====================================================================

  // ====================================================================
  // Group 10: Year Formatting
  // ====================================================================

  private static Stream<Arguments> provideYearFormats() {
    return Stream.of(
        // [Y] default - full year
        Arguments.of(TEST_DATE, "[Y]", "2002"),
        // [Y0001] - 4 digit padded
        Arguments.of(TEST_DATE, "[Y0001]", "2002"),
        // [Y01] - modulo 10^2 = last 2 digits (spec 9.8.4.4 modulo rule)
        Arguments.of(TEST_DATE, "[Y01]", "02"),
        // [Y,4-4] - padded to exactly 4 with width modifier
        Arguments.of(IDateItem.valueOf("0005-01-01"), "[Y,4-4]", "0005"),
        // [Y1] - single digit pattern = no truncation, full year
        Arguments.of(TEST_DATE, "[Y1]", "2002"));
  }

  @ParameterizedTest
  @MethodSource("provideYearFormats")
  void testYearFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, DATE_MARKERS));
  }

  // ====================================================================
  // Group 11: Month Formatting
  // ====================================================================

  private static Stream<Arguments> provideMonthFormats() {
    return Stream.of(
        // Default decimal
        Arguments.of(TEST_DATE, "[M]", "12"),
        // Zero-padded
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[M01]", "03"),
        // Title-case name
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[MNn]", "March"),
        // Uppercase name
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[MN]", "MARCH"),
        // Lowercase name
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[Mn]", "march"),
        // Abbreviated name (max width 3)
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[MNn,*-3]", "Mar"),
        // Exact 3 chars
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[MNn,3-3]", "Mar"),
        // Roman upper
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[MI]", "III"),
        // Roman lower
        Arguments.of(IDateItem.valueOf("2002-03-15"), "[Mi]", "iii"),
        // Month 12 name
        Arguments.of(TEST_DATE, "[MNn]", "December"));
  }

  @ParameterizedTest
  @MethodSource("provideMonthFormats")
  void testMonthFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, DATE_MARKERS));
  }

  // ====================================================================
  // Group 12: Day Formatting
  // ====================================================================

  private static Stream<Arguments> provideDayFormats() {
    return Stream.of(
        // Default decimal
        Arguments.of(TEST_DATE, "[D]", "31"),
        // Zero-padded
        Arguments.of(IDateItem.valueOf("2002-03-05"), "[D01]", "05"),
        // Ordinal
        Arguments.of(IDateItem.valueOf("2002-03-01"), "[D1o]", "1st"),
        Arguments.of(IDateItem.valueOf("2002-03-02"), "[D1o]", "2nd"),
        Arguments.of(IDateItem.valueOf("2002-03-03"), "[D1o]", "3rd"),
        Arguments.of(IDateItem.valueOf("2002-03-04"), "[D1o]", "4th"),
        Arguments.of(IDateItem.valueOf("2002-03-11"), "[D1o]", "11th"),
        Arguments.of(IDateItem.valueOf("2002-03-12"), "[D1o]", "12th"),
        Arguments.of(IDateItem.valueOf("2002-03-13"), "[D1o]", "13th"),
        Arguments.of(IDateItem.valueOf("2002-03-21"), "[D1o]", "21st"),
        Arguments.of(TEST_DATE, "[D1o]", "31st"));
  }

  @ParameterizedTest
  @MethodSource("provideDayFormats")
  void testDayFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, DATE_MARKERS));
  }

  // ====================================================================
  // Group 13: Day of Year
  // ====================================================================

  private static Stream<Arguments> provideDayOfYearFormats() {
    return Stream.of(
        Arguments.of(IDateItem.valueOf("2002-01-01"), "[d]", "1"),
        Arguments.of(IDateItem.valueOf("2002-12-31"), "[d]", "365"),
        // leap year
        Arguments.of(IDateItem.valueOf("2004-12-31"), "[d]", "366"),
        Arguments.of(IDateItem.valueOf("2002-01-05"), "[d001]", "005"));
  }

  @ParameterizedTest
  @MethodSource("provideDayOfYearFormats")
  void testDayOfYearFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, DATE_MARKERS));
  }

  // ====================================================================
  // Group 14: Day of Week
  // ====================================================================

  private static Stream<Arguments> provideDayOfWeekFormats() {
    return Stream.of(
        // 2002-12-31 is a Tuesday
        // F default is name lowercase (n)
        Arguments.of(TEST_DATE, "[F]", "tuesday"),
        // Numeric ISO (Mon=1, Sun=7)
        Arguments.of(TEST_DATE, "[F1]", "2"), // Tuesday = 2
        Arguments.of(IDateItem.valueOf("2002-12-30"), "[F1]", "1"), // Monday = 1
        Arguments.of(IDateItem.valueOf("2003-01-05"), "[F1]", "7"), // Sunday = 7
        // Title-case name
        Arguments.of(TEST_DATE, "[FNn]", "Tuesday"),
        // Abbreviated
        Arguments.of(TEST_DATE, "[FNn,*-3]", "Tue"));
  }

  @ParameterizedTest
  @MethodSource("provideDayOfWeekFormats")
  void testDayOfWeekFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, DATE_MARKERS));
  }

  // ====================================================================
  // Group 15: Hour Formatting (24-hour and 12-hour)
  // ====================================================================

  private static Stream<Arguments> provideHourFormats() {
    return Stream.of(
        // 24-hour (H), default is "1"
        Arguments.of(TEST_TIME, "[H]", "15"),
        Arguments.of(ITimeItem.valueOf("00:00:00+00:00"), "[H]", "0"),
        Arguments.of(ITimeItem.valueOf("00:00:00+00:00"), "[H01]", "00"),
        Arguments.of(ITimeItem.valueOf("23:59:59+00:00"), "[H]", "23"),
        // 12-hour (h), default is "1"
        Arguments.of(ITimeItem.valueOf("00:00:00+00:00"), "[h]", "12"), // midnight = 12
        Arguments.of(ITimeItem.valueOf("12:00:00+00:00"), "[h]", "12"), // noon = 12
        Arguments.of(ITimeItem.valueOf("13:00:00+00:00"), "[h]", "1"),
        Arguments.of(ITimeItem.valueOf("23:00:00+00:00"), "[h]", "11"),
        Arguments.of(TEST_TIME, "[h]", "3")); // 15:58 -> 3
  }

  @ParameterizedTest
  @MethodSource("provideHourFormats")
  void testHourFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, TIME_MARKERS));
  }

  // ====================================================================
  // Group 16: AM/PM
  // ====================================================================

  private static Stream<Arguments> provideAmPmFormats() {
    return Stream.of(
        // P default is name lowercase (n)
        Arguments.of(ITimeItem.valueOf("00:00:00+00:00"), "[P]", "am"),
        Arguments.of(ITimeItem.valueOf("00:00:00+00:00"), "[PN]", "AM"),
        Arguments.of(ITimeItem.valueOf("12:00:00+00:00"), "[Pn]", "pm"),
        Arguments.of(ITimeItem.valueOf("12:00:00+00:00"), "[PN]", "PM"),
        Arguments.of(TEST_TIME, "[PN]", "PM"));
  }

  @ParameterizedTest
  @MethodSource("provideAmPmFormats")
  void testAmPmFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, TIME_MARKERS));
  }

  // ====================================================================
  // Group 17: Minute and Second
  // ====================================================================

  private static Stream<Arguments> provideMinuteSecondFormats() {
    return Stream.of(
        // Minute default is "01" (zero-padded)
        Arguments.of(ITimeItem.valueOf("12:05:00+00:00"), "[m]", "05"),
        Arguments.of(ITimeItem.valueOf("12:05:00+00:00"), "[m1]", "5"),
        Arguments.of(TEST_TIME, "[m]", "58"),
        // Second default is "01" (zero-padded)
        Arguments.of(ITimeItem.valueOf("12:00:00+00:00"), "[s]", "00"),
        Arguments.of(ITimeItem.valueOf("12:00:09+00:00"), "[s1]", "9"),
        Arguments.of(TEST_TIME, "[s]", "45"));
  }

  @ParameterizedTest
  @MethodSource("provideMinuteSecondFormats")
  void testMinuteSecondFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, TIME_MARKERS));
  }

  // ====================================================================
  // Group 18: Fractional Seconds
  // ====================================================================

  private static Stream<Arguments> provideFractionalSecondFormats() {
    return Stream.of(
        // [f001] -> 3 digits (milliseconds)
        Arguments.of(TEST_TIME, "[f001]", "762"), // 0.762 seconds
        // [f001] with zero nanos
        Arguments.of(ITimeItem.valueOf("12:00:00+00:00"), "[f001]", "000"),
        // [f01] -> 2 digits (hundredths) - truncated not rounded
        Arguments.of(TEST_TIME, "[f01]", "76"),
        // [f1] -> all significant digits (single digit = no constraint)
        Arguments.of(TEST_TIME, "[f1]", "762"),
        // [f1,1-1] -> exactly 1 digit via width
        Arguments.of(TEST_TIME, "[f1,1-1]", "7"));
  }

  @ParameterizedTest
  @MethodSource("provideFractionalSecondFormats")
  void testFractionalSecondFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, TIME_MARKERS));
  }

  // ====================================================================
  // Group 19: Timezone (Z)
  // ====================================================================

  private static Stream<Arguments> provideTimezoneFormats() {
    return Stream.of(
        // Default Z format is "01:01"
        Arguments.of(TEST_DATETIME, "[Z]", "+02:00"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45-08:00"), "[Z]", "-08:00"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+00:00"), "[Z]", "+00:00"),
        // No timezone -> empty
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45"), "[Z]", ""),
        // Abbreviated forms
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+05:00"), "[Z0]", "+5"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+05:30"), "[Z0]", "+5:30"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+05:00"), "[Z0:00]", "+5:00"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+05:00"), "[Z00:00]", "+05:00"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+05:30"), "[Z0000]", "+0530"),
        // t modifier (UTC -> Z)
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+00:00"), "[Z01:01t]", "Z"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+05:00"), "[Z01:01t]", "+05:00"),
        // Military timezone
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+00:00"), "[ZZ]", "Z"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+01:00"), "[ZZ]", "A"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45-01:00"), "[ZZ]", "N"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+12:00"), "[ZZ]", "M"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45-12:00"), "[ZZ]", "Y"),
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45+05:30"), "[ZZ]", "+05:30"),
        // Military J for local time (no timezone)
        Arguments.of(IDateTimeItem.valueOf("2002-12-31T15:58:45"), "[ZZ]", "J"));
  }

  @ParameterizedTest
  @MethodSource("provideTimezoneFormats")
  void testTimezoneFormatting(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull String expected) {
    assertEquals(expected, format(value, picture, ALL_MARKERS));
  }

  // ====================================================================
  // Group 20: GMT Prefix Timezone (z)
  // ====================================================================

  @Test
  void testGmtTimezone() {
    assertEquals("GMT+02:00",
        format(TEST_DATETIME, "[z]", ALL_MARKERS));
    assertEquals("GMT+00:00",
        format(IDateTimeItem.valueOf("2002-12-31T15:58:45+00:00"), "[z]", ALL_MARKERS));
    // No timezone -> empty
    assertEquals("",
        format(IDateTimeItem.valueOf("2002-12-31T15:58:45"), "[z]", ALL_MARKERS));
  }

  // ====================================================================
  // Group 21: Calendar and Era
  // ====================================================================

  @Test
  void testCalendarAndEra() {
    // C default is name lowercase (n)
    assertEquals("ad", format(TEST_DATE, "[C]", DATE_MARKERS));
    assertEquals("AD", format(TEST_DATE, "[CN]", DATE_MARKERS));
    // E default is name lowercase (n)
    assertEquals("ad", format(TEST_DATE, "[E]", DATE_MARKERS));
    assertEquals("AD", format(TEST_DATE, "[EN]", DATE_MARKERS));
  }

  // ====================================================================
  // Group 22: Width Modifier Enforcement
  // ====================================================================

  @Test
  void testWidthModifierTruncation() {
    // [MNn,3-3] truncates "December" to "Dec"
    assertEquals("Dec", format(TEST_DATE, "[MNn,3-3]", DATE_MARKERS));
    // [MNn,3-3] with "May" -> "May" (already 3)
    assertEquals("May", format(IDateItem.valueOf("2002-05-15"), "[MNn,3-3]", DATE_MARKERS));
  }

  @Test
  void testWidthModifierPadding() {
    // [MNn,10] pads "May" to 10 chars with spaces
    String result = format(IDateItem.valueOf("2002-05-15"), "[MNn,10]", DATE_MARKERS);
    assertEquals(10, result.length());
    assertTrue(result.startsWith("May"));
  }

  // ====================================================================
  // Group 23: Spec Examples (Integration Tests)
  // ====================================================================

  private static Stream<Arguments> provideSpecExamples() {
    IDateItem specDate = IDateItem.valueOf("2002-12-31");
    ITimeItem specTime = ITimeItem.valueOf("15:58:45.762+02:00");
    IDateTimeItem specDateTime = IDateTimeItem.valueOf("2002-12-31T15:58:45+02:00");
    return Stream.of(
        Arguments.of(specDate, "[Y0001]-[M01]-[D01]", DATE_MARKERS, "2002-12-31"),
        Arguments.of(specDate, "[D1] [MI] [Y]", DATE_MARKERS, "31 XII 2002"),
        Arguments.of(specDate, "[D1o] [MNn], [Y]", DATE_MARKERS, "31st December, 2002"),
        Arguments.of(specDate, "[D01] [MN,*-3] [Y0001]", DATE_MARKERS, "31 DEC 2002"),
        Arguments.of(specDate, "[MNn] [D], [Y]", DATE_MARKERS, "December 31, 2002"),
        Arguments.of(specDate, "[[[Y0001]-[M01]-[D01]]]", DATE_MARKERS, "[2002-12-31]"),
        Arguments.of(specTime, "[h]:[m01] [PN]", TIME_MARKERS, "3:58 PM"),
        Arguments.of(specTime, "[h]:[m01]:[s01] [Pn]", TIME_MARKERS, "3:58:45 pm"),
        Arguments.of(specTime, "[H01]:[m01]", TIME_MARKERS, "15:58"),
        Arguments.of(specTime, "[H01]:[m01]:[s01].[f001]", TIME_MARKERS, "15:58:45.762"),
        Arguments.of(specDateTime, "[M01]/[D01]/[Y0001] at [H01]:[m01]:[s01]", ALL_MARKERS,
            "12/31/2002 at 15:58:45"));
  }

  @ParameterizedTest
  @MethodSource("provideSpecExamples")
  void testSpecExamples(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @NonNull Set<Character> allowedMarkers,
      @NonNull String expected) {
    assertEquals(expected,
        DateTimeFormatUtil.formatDateTime(value, picture, null, null, null, allowedMarkers));
  }

  // ====================================================================
  // Group 24: Error Cases
  // ====================================================================

  @Test
  void testComponentNotAvailable() {
    // format-time with date marker -> FOFD1350
    assertThrows(FormatDateTimeFunctionException.class,
        () -> format(TEST_TIME, "[Y]", TIME_MARKERS));
  }

  // ====================================================================
  // Group 25: Week Formatting
  // ====================================================================

  @Test
  void testWeekOfYear() {
    // 2002-12-31 is in ISO week 1 of 2003
    assertEquals("1", format(TEST_DATE, "[W]", DATE_MARKERS));
    assertEquals("01", format(TEST_DATE, "[W01]", DATE_MARKERS));
  }

  @Test
  void testWeekOfMonth() {
    // 2002-12-31 -> week of month varies; just check it's a valid number
    String result = format(TEST_DATE, "[w]", DATE_MARKERS);
    assertTrue(Integer.parseInt(result) >= 1 && Integer.parseInt(result) <= 6);
  }
}
