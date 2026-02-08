/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dev.metaschema.core.metapath.function.FormatDateTimeFunctionException;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.atomic.ITemporalItem;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Utility class for parsing and formatting date/time picture strings as defined
 * in <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#formatting-dates-and-times"> XPath
 * Functions 3.1 Section 9.8</a>.
 */
public final class DateTimeFormatUtil {
  /**
   * The set of valid component specifier characters recognized in picture string
   * variable markers.
   */
  private static final Set<Character> VALID_SPECIFIERS = Set.of(
      'Y', 'M', 'D', 'd', 'F', 'W', 'w', 'H', 'h', 'P', 'm', 's', 'f', 'Z',
      'z', 'C', 'E');

  /**
   * The set of valid second presentation modifier characters that may appear as
   * the last character of a multi-character presentation modifier string.
   */
  private static final Set<Character> SECOND_MODIFIERS = Set.of('a', 't', 'c', 'o');

  private DateTimeFormatUtil() {
    // utility class
  }

  /**
   * Base class for components of a parsed picture string.
   */
  public static class FormatComponent {
    /**
     * Protected constructor to prevent direct instantiation.
     */
    protected FormatComponent() {
      // marker class
    }
  }

  /**
   * A literal text component in a picture string.
   */
  public static class LiteralComponent
      extends FormatComponent {
    @NonNull
    private final String text;

    /**
     * Construct a new literal component.
     *
     * @param text
     *          the literal text
     */
    public LiteralComponent(@NonNull String text) {
      this.text = text;
    }

    /**
     * Get the literal text.
     *
     * @return the text
     */
    @NonNull
    public String getText() {
      return text;
    }
  }

  /**
   * A variable marker component in a picture string, representing a date/time
   * component to be formatted.
   */
  public static class VariableMarkerComponent
      extends FormatComponent {
    private final char specifier;
    @Nullable
    private final String primaryModifier;
    @Nullable
    private final Character secondModifier;
    @Nullable
    private final Integer minWidth;
    @Nullable
    private final Integer maxWidth;

    /**
     * Construct a new variable marker component.
     *
     * @param specifier
     *          the component specifier character
     * @param primaryModifier
     *          the first presentation modifier, or {@code null}
     * @param secondModifier
     *          the second presentation modifier, or {@code null}
     * @param minWidth
     *          the minimum width, or {@code null}
     * @param maxWidth
     *          the maximum width, or {@code null}
     */
    public VariableMarkerComponent(
        char specifier,
        @Nullable String primaryModifier,
        @Nullable Character secondModifier,
        @Nullable Integer minWidth,
        @Nullable Integer maxWidth) {
      this.specifier = specifier;
      this.primaryModifier = primaryModifier;
      this.secondModifier = secondModifier;
      this.minWidth = minWidth;
      this.maxWidth = maxWidth;
    }

    /**
     * Get the component specifier character.
     *
     * @return the specifier
     */
    public char getSpecifier() {
      return specifier;
    }

    /**
     * Get the primary presentation modifier.
     *
     * @return the primary modifier, or {@code null} if not specified
     */
    @Nullable
    public String getPrimaryModifier() {
      return primaryModifier;
    }

    /**
     * Get the second presentation modifier.
     *
     * @return the second modifier character, or {@code null} if not specified
     */
    @Nullable
    public Character getSecondModifier() {
      return secondModifier;
    }

    /**
     * Get the minimum width.
     *
     * @return the minimum width, or {@code null} if not specified
     */
    @Nullable
    public Integer getMinWidth() {
      return minWidth;
    }

    /**
     * Get the maximum width.
     *
     * @return the maximum width, or {@code null} if not specified
     */
    @Nullable
    public Integer getMaxWidth() {
      return maxWidth;
    }
  }

  /**
   * Parse a picture string into a list of format components.
   * <p>
   * The picture string consists of literal substrings and variable markers
   * enclosed in square brackets. Doubled brackets {@code [[} and {@code ]]} are
   * treated as escaped literal brackets.
   *
   * @param picture
   *          the picture string to parse
   * @return an unmodifiable list of format components
   * @throws FormatDateTimeFunctionException
   *           with {@link FormatDateTimeFunctionException#INVALID_PICTURE_STRING}
   *           if the picture string syntax is invalid
   * @see <a href=
   *      "https://www.w3.org/TR/xpath-functions-31/#date-picture-string"> XPath
   *      Functions 3.1 - Date Picture String</a>
   */
  @NonNull
  public static List<FormatComponent> parsePictureString(@NonNull String picture) {
    List<FormatComponent> components = new ArrayList<>();
    StringBuilder literal = new StringBuilder();
    int length = picture.length();
    int index = 0;

    while (index < length) {
      char ch = picture.charAt(index);

      if (ch == '[') {
        // Check for escaped open bracket
        if (index + 1 < length && picture.charAt(index + 1) == '[') {
          literal.append('[');
          index += 2;
        } else {
          // Flush any accumulated literal text
          if (literal.length() > 0) {
            components.add(new LiteralComponent(literal.toString()));
            literal.setLength(0);
          }

          // Find the closing bracket
          int closeIndex = picture.indexOf(']', index + 1);
          if (closeIndex < 0) {
            throw new FormatDateTimeFunctionException(
                FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
                "Unmatched '[' in picture string: " + picture);
          }

          String markerContent = picture.substring(index + 1, closeIndex);
          components.add(parseVariableMarker(markerContent, picture));
          index = closeIndex + 1;
        }
      } else if (ch == ']') {
        // Check for escaped close bracket
        if (index + 1 < length && picture.charAt(index + 1) == ']') {
          literal.append(']');
          index += 2;
        } else {
          throw new FormatDateTimeFunctionException(
              FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
              "Unmatched ']' in picture string: " + picture);
        }
      } else {
        literal.append(ch);
        index++;
      }
    }

    // Flush any remaining literal text
    if (literal.length() > 0) {
      components.add(new LiteralComponent(literal.toString()));
    }

    return Collections.unmodifiableList(components);
  }

  /**
   * Parse the content of a variable marker (the text between {@code [} and
   * {@code ]}) into a {@link VariableMarkerComponent}.
   *
   * @param content
   *          the raw content between the brackets
   * @param picture
   *          the full picture string, used for error messages
   * @return a new variable marker component
   * @throws FormatDateTimeFunctionException
   *           with {@link FormatDateTimeFunctionException#INVALID_PICTURE_STRING}
   *           if the marker syntax is invalid
   */
  @NonNull
  private static VariableMarkerComponent parseVariableMarker(
      @NonNull String content,
      @NonNull String picture) {
    // Strip all whitespace
    String stripped = content.replaceAll("\\s", "");

    if (stripped.isEmpty()) {
      throw new FormatDateTimeFunctionException(
          FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
          "Empty variable marker in picture string: " + picture);
    }

    // First character is the component specifier
    char specifier = stripped.charAt(0);
    if (!VALID_SPECIFIERS.contains(specifier)) {
      throw new FormatDateTimeFunctionException(
          FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
          "Invalid component specifier '" + specifier
              + "' in picture string: " + picture);
    }

    // Remaining string contains presentation + width
    String remaining = stripped.substring(1);

    // Find the LAST comma to split presentation from width
    String presentationPart;
    String widthPart;
    int lastComma = remaining.lastIndexOf(',');
    if (lastComma >= 0) {
      presentationPart = remaining.substring(0, lastComma);
      widthPart = remaining.substring(lastComma + 1);
    } else {
      presentationPart = remaining;
      widthPart = null;
    }

    // Parse presentation part
    String primaryModifier = null;
    Character secondModifier = null;

    if (!presentationPart.isEmpty()) {
      if (presentationPart.length() == 1) {
        // Single character is always the primary modifier
        primaryModifier = presentationPart;
      } else {
        // More than one character: check if last char is a valid second modifier
        char lastChar = presentationPart.charAt(presentationPart.length() - 1);
        if (SECOND_MODIFIERS.contains(lastChar)) {
          secondModifier = lastChar;
          String primary = presentationPart.substring(0, presentationPart.length() - 1);
          primaryModifier = primary.isEmpty() ? null : primary;
        } else {
          primaryModifier = presentationPart;
        }
      }
    }

    // Parse width part
    Integer minWidth = null;
    Integer maxWidth = null;

    if (widthPart != null) {
      Integer[] widths = new Integer[2];
      parseWidth(widthPart, picture, widths);
      minWidth = widths[0];
      maxWidth = widths[1];
    }

    return new VariableMarkerComponent(specifier, primaryModifier, secondModifier,
        minWidth, maxWidth);
  }

  /**
   * Parse a width specification string of the form {@code min-max} or
   * {@code min}, where either value may be {@code *} to indicate unbounded.
   *
   * @param widthPart
   *          the width specification string
   * @param picture
   *          the full picture string, used for error messages
   * @param result
   *          a two-element array to receive the parsed minimum (index 0) and
   *          maximum (index 1) width values; {@code null} indicates unbounded
   * @throws FormatDateTimeFunctionException
   *           with {@link FormatDateTimeFunctionException#INVALID_PICTURE_STRING}
   *           if the width specification is invalid
   */
  private static void parseWidth(
      @NonNull String widthPart,
      @NonNull String picture,
      @NonNull Integer[] result) {
    int dashIndex = widthPart.indexOf('-');
    String minStr;
    String maxStr;

    if (dashIndex >= 0) {
      minStr = widthPart.substring(0, dashIndex);
      maxStr = widthPart.substring(dashIndex + 1);
    } else {
      minStr = widthPart;
      maxStr = null;
    }

    Integer minWidth = parseWidthValue(minStr, picture);
    Integer maxWidth = maxStr != null ? parseWidthValue(maxStr, picture) : null;

    // Validate min-width >= 1
    if (minWidth != null && minWidth < 1) {
      throw new FormatDateTimeFunctionException(
          FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
          "Minimum width must be at least 1 in picture string: " + picture);
    }

    // Validate max-width >= min-width
    if (minWidth != null && maxWidth != null && maxWidth < minWidth) {
      throw new FormatDateTimeFunctionException(
          FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
          "Maximum width must not be less than minimum width in picture string: "
              + picture);
    }

    result[0] = minWidth;
    result[1] = maxWidth;
  }

  /**
   * Parse a single width value, which may be a positive integer or {@code *} for
   * unbounded.
   *
   * @param value
   *          the width value string
   * @param picture
   *          the full picture string, used for error messages
   * @return the parsed integer value, or {@code null} if the value is {@code *}
   * @throws FormatDateTimeFunctionException
   *           with {@link FormatDateTimeFunctionException#INVALID_PICTURE_STRING}
   *           if the value cannot be parsed
   */
  @Nullable
  private static Integer parseWidthValue(@NonNull String value, @NonNull String picture) {
    if ("*".equals(value)) {
      return null;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      throw new FormatDateTimeFunctionException(
          FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
          "Invalid width value '" + value + "' in picture string: " + picture,
          ex);
    }
  }

  // ====================================================================
  // Formatting Engine
  // ====================================================================

  /**
   * English month names indexed from 0 (January) to 11 (December).
   */
  private static final String[] MONTH_NAMES = {
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
  };

  /**
   * English day-of-week names indexed from 0 (Monday) to 6 (Sunday), matching ISO
   * 8601 numbering where Monday is day 1.
   */
  private static final String[] DAY_NAMES = {
      "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
  };

  /**
   * Format a temporal value according to a picture string.
   * <p>
   * This method implements the formatting algorithm defined in
   * <a href="https://www.w3.org/TR/xpath-functions-31/#date-picture-string">
   * XPath Functions 3.1 Section 9.8</a>. The picture string is parsed into
   * literal and variable marker components, and each variable marker is formatted
   * according to its component specifier, presentation modifier, and width
   * modifier.
   *
   * @param value
   *          the temporal value to format
   * @param picture
   *          the picture string
   * @param language
   *          the language for names, or {@code null} for English
   * @param calendar
   *          the calendar system, or {@code null} for Gregorian
   * @param place
   *          the place for timezone, or {@code null}
   * @param allowedMarkers
   *          the set of allowed component specifiers
   * @return the formatted string
   * @throws FormatDateTimeFunctionException
   *           if formatting fails
   */
  @NonNull
  public static String formatDateTime(
      @NonNull ITemporalItem value,
      @NonNull String picture,
      @Nullable String language,
      @Nullable String calendar,
      @Nullable String place,
      @NonNull Set<Character> allowedMarkers) {
    List<FormatComponent> components = parsePictureString(picture);
    StringBuilder result = new StringBuilder();

    for (FormatComponent component : components) {
      if (component instanceof LiteralComponent) {
        result.append(((LiteralComponent) component).getText());
      } else {
        VariableMarkerComponent marker = (VariableMarkerComponent) component;
        char specifier = marker.getSpecifier();

        if (!allowedMarkers.contains(specifier)) {
          throw new FormatDateTimeFunctionException(
              FormatDateTimeFunctionException.COMPONENT_NOT_AVAILABLE,
              "Component specifier '" + specifier
                  + "' is not available for this type in picture string: " + picture);
        }

        result.append(formatComponent(value, marker, language));
      }
    }

    return result.toString();
  }

  /**
   * Format a single variable marker component.
   *
   * @param value
   *          the temporal value
   * @param marker
   *          the variable marker component
   * @param language
   *          the language for locale-dependent formatting, or {@code null}
   * @return the formatted string for this component
   */
  @NonNull
  private static String formatComponent(
      @NonNull ITemporalItem value,
      @NonNull VariableMarkerComponent marker,
      @Nullable String language) {
    char specifier = marker.getSpecifier();
    String primaryMod = marker.getPrimaryModifier();
    Character secondMod = marker.getSecondModifier();
    Integer minWidth = marker.getMinWidth();
    Integer maxWidth = marker.getMaxWidth();

    switch (specifier) {
    case 'Y':
      return formatYear(value, primaryMod, secondMod, minWidth, maxWidth, language);
    case 'M':
      return formatNameableComponent(value.getMonth(), MONTH_NAMES, 1,
          primaryMod, secondMod, minWidth, maxWidth, language, "1");
    case 'D':
      return formatIntegerComponent(value.getDay(),
          primaryMod, secondMod, minWidth, maxWidth, language, "1");
    case 'd':
      return formatDayOfYear(value, primaryMod, secondMod, minWidth, maxWidth, language);
    case 'F':
      return formatDayOfWeek(value, primaryMod, secondMod, minWidth, maxWidth, language);
    case 'W':
      return formatWeekOfYear(value, primaryMod, secondMod, minWidth, maxWidth, language);
    case 'w':
      return formatWeekOfMonth(value, primaryMod, secondMod, minWidth, maxWidth, language);
    case 'H':
      return formatIntegerComponent(value.getHour(),
          primaryMod, secondMod, minWidth, maxWidth, language, "1");
    case 'h':
      return formatIntegerComponent(hourIn12(value.getHour()),
          primaryMod, secondMod, minWidth, maxWidth, language, "1");
    case 'P':
      return formatAmPm(value.getHour(), primaryMod, secondMod, minWidth, maxWidth);
    case 'm':
      return formatIntegerComponent(value.getMinute(),
          primaryMod, secondMod, minWidth, maxWidth, language, "01");
    case 's':
      return formatIntegerComponent(value.getSecond(),
          primaryMod, secondMod, minWidth, maxWidth, language, "01");
    case 'f':
      return formatFractionalSeconds(value.getNano(), primaryMod, minWidth, maxWidth);
    case 'Z':
      return formatTimezone(value, primaryMod, secondMod);
    case 'z':
      return formatGmtTimezone(value, primaryMod, secondMod);
    case 'C':
      return formatCalendar(primaryMod, minWidth, maxWidth);
    case 'E':
      return formatEra(value.getYear(), primaryMod, minWidth, maxWidth);
    default:
      // Should not happen since VALID_SPECIFIERS already checked
      return "";
    }
  }

  /**
   * Convert a 24-hour hour value to 12-hour format.
   *
   * @param hour24
   *          the hour in 24-hour format (0-23)
   * @return the hour in 12-hour format (1-12)
   */
  private static int hourIn12(int hour24) {
    int h = hour24 % 12;
    return h == 0 ? 12 : h;
  }

  /**
   * Format a year value with special modulo handling per spec 9.8.4.4.
   *
   * @param value
   *          the temporal value
   * @param primaryMod
   *          the primary presentation modifier, or {@code null}
   * @param secondMod
   *          the second presentation modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @param language
   *          the language for formatting, or {@code null}
   * @return the formatted year string
   */
  @NonNull
  private static String formatYear(
      @NonNull ITemporalItem value,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      @Nullable String language) {
    // Use long arithmetic and clamp to avoid overflow for Integer.MIN_VALUE
    int year = (int) Math.min(Math.abs((long) value.getYear()), Integer.MAX_VALUE);

    // Determine the effective format token
    String effectiveToken = primaryMod != null ? primaryMod : "1";

    // Spec 9.8.4.4: Determine N for modulo rule
    // If maxWidth defines a finite value -> N = maxWidth
    // Else if format token is a decimal digit pattern with W>=2 mandatory digits ->
    // N = W
    // Else N = infinity (output full year)
    int moduloN = Integer.MAX_VALUE;

    if (maxWidth != null) {
      moduloN = maxWidth;
    } else {
      int mandatoryDigits = countMandatoryDigits(effectiveToken);
      if (mandatoryDigits >= 2 && isDecimalDigitPattern(effectiveToken)) {
        moduloN = mandatoryDigits;
      }
    }

    // Apply modulo if N is finite
    int displayYear = year;
    if (moduloN < Integer.MAX_VALUE) {
      int divisor = (int) Math.pow(10, moduloN);
      displayYear = year % divisor;
    }

    // Format the value
    String formatted = formatIntegerValue(displayYear, effectiveToken, secondMod, language);

    // Apply width modifiers
    formatted = applyWidthModifiers(formatted, minWidth, maxWidth, false);

    // Prepend minus for negative years
    if (value.getYear() < 0) {
      formatted = "-" + formatted;
    }

    return formatted;
  }

  /**
   * Format a component that can be displayed either as a number or as a name
   * (e.g., months, days of week).
   *
   * @param componentValue
   *          the numeric value of the component
   * @param names
   *          the array of names (0-indexed offset from {@code nameOffset})
   * @param nameOffset
   *          the offset subtracted from {@code componentValue} to get the name
   *          array index
   * @param primaryMod
   *          the primary presentation modifier, or {@code null}
   * @param secondMod
   *          the second presentation modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @param language
   *          the language for formatting, or {@code null}
   * @param defaultToken
   *          the default format token when no primary modifier is specified
   * @return the formatted string
   */
  @NonNull
  private static String formatNameableComponent(
      int componentValue,
      @NonNull String[] names,
      int nameOffset,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      @Nullable String language,
      @NonNull String defaultToken) {
    String effective = primaryMod != null ? primaryMod : defaultToken;

    // Check if this is a name format
    if (isNameFormat(effective)) {
      String name = names[componentValue - nameOffset];
      name = applyNameCase(name, effective);
      return applyWidthModifiers(name, minWidth, maxWidth, true);
    }

    return formatIntegerComponent(componentValue, primaryMod, secondMod,
        minWidth, maxWidth, language, defaultToken);
  }

  /**
   * Format a simple integer-valued component.
   *
   * @param componentValue
   *          the integer value
   * @param primaryMod
   *          the primary presentation modifier, or {@code null}
   * @param secondMod
   *          the second presentation modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @param language
   *          the language for formatting, or {@code null}
   * @param defaultToken
   *          the default format token
   * @return the formatted string
   */
  @NonNull
  private static String formatIntegerComponent(
      int componentValue,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      @Nullable String language,
      @NonNull String defaultToken) {
    String effectiveToken = primaryMod != null ? primaryMod : defaultToken;

    String formatted = formatIntegerValue(componentValue, effectiveToken, secondMod, language);
    return applyWidthModifiers(formatted, minWidth, maxWidth, false);
  }

  /**
   * Format an integer using {@link FnFormatInteger#fnFormatInteger}, delegating
   * numeric, alphabetic, roman, and word formatting to the XPath format-integer
   * implementation.
   *
   * @param componentValue
   *          the integer value to format
   * @param formatToken
   *          the primary format token (e.g., "1", "01", "i", "w")
   * @param secondMod
   *          the second modifier character (e.g., 'o' for ordinal), or
   *          {@code null}
   * @param language
   *          the language for locale-dependent formatting, or {@code null}
   * @return the formatted string
   */
  @NonNull
  private static String formatIntegerValue(
      int componentValue,
      @NonNull String formatToken,
      @Nullable Character secondMod,
      @Nullable String language) {
    // Build the format-integer picture
    String picture = formatToken;
    if (secondMod != null && secondMod == 'o') {
      picture = picture + ";o";
    }

    return FnFormatInteger.fnFormatInteger(
        IIntegerItem.valueOf(componentValue),
        picture,
        language);
  }

  /**
   * Format the day-of-year component.
   *
   * @param value
   *          the temporal value
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param secondMod
   *          the second modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @param language
   *          the language, or {@code null}
   * @return the formatted day-of-year string
   */
  @NonNull
  private static String formatDayOfYear(
      @NonNull ITemporalItem value,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      @Nullable String language) {
    // Use proxy year >= 1 because LocalDate.of does not support year <= 0
    int proxyYear = Math.max(1, value.getYear());
    int dayOfYear = LocalDate.of(proxyYear, value.getMonth(), value.getDay()).getDayOfYear();
    return formatIntegerComponent(dayOfYear, primaryMod, secondMod, minWidth, maxWidth, language, "1");
  }

  /**
   * Format the day-of-week component. The default presentation modifier for F is
   * "n" (lowercase name).
   *
   * @param value
   *          the temporal value
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param secondMod
   *          the second modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @param language
   *          the language, or {@code null}
   * @return the formatted day-of-week string
   */
  @NonNull
  private static String formatDayOfWeek(
      @NonNull ITemporalItem value,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      @Nullable String language) {
    // Use proxy year >= 1 because LocalDate.of does not support year <= 0
    int proxyYear = Math.max(1, value.getYear());
    DayOfWeek dow = LocalDate.of(proxyYear, value.getMonth(), value.getDay()).getDayOfWeek();
    int isoValue = dow.getValue(); // Mon=1..Sun=7

    // Default for F is "n" (lowercase name)
    String effective = primaryMod != null ? primaryMod : "n";

    if (isNameFormat(effective)) {
      String name = DAY_NAMES[isoValue - 1];
      name = applyNameCase(name, effective);
      return applyWidthModifiers(name, minWidth, maxWidth, true);
    }

    return formatIntegerComponent(isoValue, primaryMod, secondMod,
        minWidth, maxWidth, language, "n");
  }

  /**
   * Format the ISO week-of-year component.
   *
   * @param value
   *          the temporal value
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param secondMod
   *          the second modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @param language
   *          the language, or {@code null}
   * @return the formatted week-of-year string
   */
  @NonNull
  private static String formatWeekOfYear(
      @NonNull ITemporalItem value,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      @Nullable String language) {
    // Use proxy year >= 1 because LocalDate.of does not support year <= 0
    int proxyYear = Math.max(1, value.getYear());
    int week = LocalDate.of(proxyYear, value.getMonth(), value.getDay())
        .get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    return formatIntegerComponent(week, primaryMod, secondMod, minWidth, maxWidth, language, "1");
  }

  /**
   * Format the week-of-month component.
   *
   * @param value
   *          the temporal value
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param secondMod
   *          the second modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @param language
   *          the language, or {@code null}
   * @return the formatted week-of-month string
   */
  @NonNull
  private static String formatWeekOfMonth(
      @NonNull ITemporalItem value,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      @Nullable String language) {
    // Use proxy year >= 1 because LocalDate.of does not support year <= 0
    int proxyYear = Math.max(1, value.getYear());
    int week = LocalDate.of(proxyYear, value.getMonth(), value.getDay())
        .get(WeekFields.ISO.weekOfMonth());
    return formatIntegerComponent(week, primaryMod, secondMod, minWidth, maxWidth, language, "1");
  }

  /**
   * Format the AM/PM marker. The default presentation is "n" (lowercase name).
   *
   * @param hour
   *          the hour value (0-23)
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param secondMod
   *          the second modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @return the formatted AM/PM string
   */
  @NonNull
  private static String formatAmPm(
      int hour,
      @Nullable String primaryMod,
      @Nullable Character secondMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth) {
    String effective = primaryMod != null ? primaryMod : "n";
    String base = hour < 12 ? "am" : "pm";

    String result;
    if ("N".equals(effective)) {
      result = base.toUpperCase(Locale.ROOT);
    } else if ("Nn".equals(effective)) {
      result = Character.toUpperCase(base.charAt(0)) + base.substring(1);
    } else {
      // default: lowercase
      result = base;
    }

    return applyWidthModifiers(result, minWidth, maxWidth, true);
  }

  /**
   * Format fractional seconds per spec 9.8.4.5.
   * <p>
   * The fractional seconds use a "reverse digit" algorithm: the nano value is
   * converted to a 9-digit string, and the format token determines how many
   * digits to output. A single-digit pattern with no constraints outputs all
   * significant (non-trailing-zero) digits.
   *
   * @param nano
   *          the nanosecond value (0-999999999)
   * @param primaryModifier
   *          the primary modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @return the formatted fractional seconds string
   */
  @NonNull
  private static String formatFractionalSeconds(
      int nano,
      @Nullable String primaryModifier,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth) {
    // Convert nano to 9-digit string
    String nanoStr = String.format("%09d", nano);
    String effective = primaryModifier != null ? primaryModifier : "1";
    int mandatoryDigits = countMandatoryDigits(effective);

    String result;
    if (mandatoryDigits <= 1 && effective.length() <= 1) {
      // Single digit pattern = no constraint, use all significant digits
      result = nanoStr.replaceAll("0+$", "");
      if (result.isEmpty()) {
        result = "0";
      }

      // Apply width constraints
      if (maxWidth != null && result.length() > maxWidth) {
        result = result.substring(0, maxWidth);
      }
      if (minWidth != null && result.length() < minWidth) {
        result = result + "0".repeat(minWidth - result.length());
      }
    } else {
      // Multiple mandatory digits = exact digit count
      int numDigits = mandatoryDigits;
      if (minWidth != null && minWidth > numDigits) {
        numDigits = minWidth;
      }
      if (maxWidth != null && maxWidth < numDigits) {
        numDigits = maxWidth;
      }
      result = nanoStr.substring(0, Math.min(numDigits, 9));
    }

    return result;
  }

  /**
   * Format a timezone offset using the Z specifier per spec 9.8.4.6.
   *
   * @param value
   *          the temporal value
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param secondMod
   *          the second modifier, or {@code null}
   * @return the formatted timezone string
   */
  @NonNull
  private static String formatTimezone(
      @NonNull ITemporalItem value,
      @Nullable String primaryMod,
      @Nullable Character secondMod) {
    ZoneOffset offset = value.getZoneOffset();

    // Military timezone format
    if (primaryMod != null && "Z".equals(primaryMod)) {
      return formatMilitaryTimezone(offset);
    }

    if (offset == null) {
      return "";
    }

    // Check for 't' modifier: UTC -> "Z"
    boolean useZ = secondMod != null && secondMod == 't';
    if (useZ && offset.getTotalSeconds() == 0) {
      return "Z";
    }

    String effective = primaryMod != null ? primaryMod : "01:01";
    return formatTimezoneNumeric(offset, effective);
  }

  /**
   * Format a military timezone letter.
   *
   * @param offset
   *          the zone offset, or {@code null} for local time
   * @return the military timezone letter
   */
  @NonNull
  private static String formatMilitaryTimezone(@Nullable ZoneOffset offset) {
    if (offset == null) {
      return "J"; // local time
    }

    int totalSeconds = offset.getTotalSeconds();
    int totalMinutes = totalSeconds / 60;
    int hours = totalMinutes / 60;
    int minutes = totalMinutes % 60;

    if (totalSeconds == 0) {
      return "Z"; // UTC
    }

    // Military letters only for whole-hour offsets -12..+12, excluding 0
    if (minutes == 0 && hours >= -12 && hours <= 12) {
      if (hours > 0) {
        // A=+1, B=+2, ..., I=+9, K=+10, L=+11, M=+12 (skip J at +10 position)
        if (hours <= 9) {
          return String.valueOf((char) ('A' + hours - 1));
        }
        // hours 10,11,12: skip J so K=10, L=11, M=12
        return String.valueOf((char) ('A' + hours)); // +10->K, +11->L, +12->M
      }
      // Negative: N=-1, O=-2, ..., Y=-12
      return String.valueOf((char) ('N' + (-hours) - 1));
    }

    // Non-whole-hour offsets: fallback to numeric
    return formatTimezoneNumeric(offset, "01:01");
  }

  /**
   * Format a numeric timezone offset according to the specified pattern.
   * <p>
   * The pattern determines the format:
   * <ul>
   * <li>{@code 0} or {@code 1} - hours only (no leading zero), minutes if
   * non-zero</li>
   * <li>{@code 00} or {@code 01} - hours with leading zero, minutes if
   * non-zero</li>
   * <li>{@code 0:00} or {@code 1:01} - hours without leading zero, always show
   * minutes with separator</li>
   * <li>{@code 00:00} or {@code 01:01} - hours with leading zero, always show
   * minutes with separator</li>
   * <li>{@code 0000} or {@code 0001} - concatenated hours+minutes, leading zero
   * on hours</li>
   * <li>{@code 000} or {@code 001} - concatenated hours+minutes, no leading zero
   * on hours</li>
   * </ul>
   *
   * @param offset
   *          the zone offset
   * @param pattern
   *          the format pattern
   * @return the formatted timezone string
   */
  @NonNull
  private static String formatTimezoneNumeric(
      @NonNull ZoneOffset offset,
      @NonNull String pattern) {
    int totalSeconds = offset.getTotalSeconds();
    String sign = totalSeconds >= 0 ? "+" : "-";
    int absSeconds = Math.abs(totalSeconds);
    int hours = absSeconds / 3600;
    int minutes = (absSeconds % 3600) / 60;

    // Determine format from pattern
    boolean hasSeparator = pattern.contains(":") || pattern.contains(".");
    char separator = pattern.contains(":") ? ':' : '.';
    String digitsPart = pattern.replace(":", "").replace(".", "");
    int digitCount = digitsPart.length();

    boolean padHours;
    boolean alwaysShowMinutes;

    if (hasSeparator) {
      // Pattern with separator (e.g., "01:01", "0:00")
      int sepIndex = pattern.indexOf(separator);
      padHours = sepIndex >= 2;
      alwaysShowMinutes = true;
    } else if (digitCount >= 3) {
      // Concatenated format (e.g., "0000", "000")
      padHours = digitCount >= 4;
      alwaysShowMinutes = true;
      // No separator in output
    } else {
      // Hours only (e.g., "0", "00", "01")
      padHours = digitCount >= 2;
      alwaysShowMinutes = false;
    }

    String hoursStr = padHours
        ? String.format("%02d", hours)
        : String.valueOf(hours);

    if (alwaysShowMinutes) {
      String minutesStr = String.format("%02d", minutes);
      if (hasSeparator) {
        return sign + hoursStr + separator + minutesStr;
      }
      return sign + hoursStr + minutesStr;
    }

    // Hours only, minutes if non-zero
    if (minutes != 0) {
      String minutesStr = String.format("%02d", minutes);
      return sign + hoursStr + ":" + minutesStr;
    }

    return sign + hoursStr;
  }

  /**
   * Format a timezone with GMT prefix (z specifier).
   *
   * @param value
   *          the temporal value
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param secondMod
   *          the second modifier, or {@code null}
   * @return the formatted GMT timezone string
   */
  @NonNull
  private static String formatGmtTimezone(
      @NonNull ITemporalItem value,
      @Nullable String primaryMod,
      @Nullable Character secondMod) {
    ZoneOffset offset = value.getZoneOffset();
    if (offset == null) {
      return "";
    }

    String tzPart = formatTimezoneNumeric(offset, primaryMod != null ? primaryMod : "01:01");
    return "GMT" + tzPart;
  }

  /**
   * Format the calendar name. Always returns "ad" for the Gregorian calendar.
   *
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @return the formatted calendar string
   */
  @NonNull
  private static String formatCalendar(
      @Nullable String primaryMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth) {
    String effective = primaryMod != null ? primaryMod : "n";
    String result = applyNameCase("ad", effective);
    return applyWidthModifiers(result, minWidth, maxWidth, true);
  }

  /**
   * Format the era indicator. Returns "ad" for non-negative years and "bc" for
   * negative years.
   *
   * @param year
   *          the year value
   * @param primaryMod
   *          the primary modifier, or {@code null}
   * @param minWidth
   *          the minimum width, or {@code null}
   * @param maxWidth
   *          the maximum width, or {@code null}
   * @return the formatted era string
   */
  @NonNull
  private static String formatEra(
      int year,
      @Nullable String primaryMod,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth) {
    String effective = primaryMod != null ? primaryMod : "n";
    String base = year >= 0 ? "ad" : "bc";
    String result = applyNameCase(base, effective);
    return applyWidthModifiers(result, minWidth, maxWidth, true);
  }

  // ====================================================================
  // Helper methods
  // ====================================================================

  /**
   * Check if a format modifier represents a name format (N, Nn, or n).
   *
   * @param modifier
   *          the modifier string
   * @return {@code true} if the modifier requests name formatting
   */
  private static boolean isNameFormat(@NonNull String modifier) {
    return "N".equals(modifier) || "Nn".equals(modifier) || "n".equals(modifier);
  }

  /**
   * Apply case transformation to a name string based on the modifier.
   *
   * @param name
   *          the name string in its base form
   * @param modifier
   *          the modifier controlling case: "N" for uppercase, "n" for lowercase,
   *          "Nn" for title case
   * @return the name with case applied
   */
  @NonNull
  private static String applyNameCase(@NonNull String name, @NonNull String modifier) {
    switch (modifier) {
    case "N":
      return name.toUpperCase(Locale.ROOT);
    case "n":
      return name.toLowerCase(Locale.ROOT);
    case "Nn":
      if (name.isEmpty()) {
        return name;
      }
      return Character.toUpperCase(name.charAt(0))
          + name.substring(1).toLowerCase(Locale.ROOT);
    default:
      return name;
    }
  }

  /**
   * Apply width modifiers to a formatted string, performing padding and
   * truncation as needed.
   *
   * @param value
   *          the formatted string
   * @param minWidth
   *          the minimum width, or {@code null} for no minimum
   * @param maxWidth
   *          the maximum width, or {@code null} for no maximum
   * @param isName
   *          {@code true} if the value is a name (pad with spaces on the right),
   *          {@code false} if numeric (pad with zeros on the left)
   * @return the string adjusted to fit width constraints
   */
  @NonNull
  private static String applyWidthModifiers(
      @NonNull String value,
      @Nullable Integer minWidth,
      @Nullable Integer maxWidth,
      boolean isName) {
    String result = value;

    // Truncation
    if (maxWidth != null && result.length() > maxWidth) {
      result = result.substring(0, maxWidth);
    }

    // Padding
    if (minWidth != null && result.length() < minWidth) {
      int padAmount = minWidth - result.length();
      if (isName) {
        // Pad names with trailing spaces
        result = result + " ".repeat(padAmount);
      } else {
        // Pad numbers with leading zeros
        result = "0".repeat(padAmount) + result;
      }
    }

    return result;
  }

  /**
   * Count the number of mandatory (digit) characters in a format token.
   *
   * @param pattern
   *          the format token
   * @return the count of digit characters
   */
  private static int countMandatoryDigits(@NonNull String pattern) {
    int count = 0;
    for (int i = 0; i < pattern.length(); i++) {
      if (Character.isDigit(pattern.charAt(i))) {
        count++;
      }
    }
    return count;
  }

  /**
   * Check if a format token is a decimal digit pattern (contains only decimal
   * digits and optional grouping separators).
   *
   * @param token
   *          the format token to check
   * @return {@code true} if the token is a decimal digit pattern
   */
  private static boolean isDecimalDigitPattern(@NonNull String token) {
    if (token.isEmpty()) {
      return false;
    }
    for (int i = 0; i < token.length(); i++) {
      char ch = token.charAt(i);
      if (!Character.isDigit(ch) && ch != '#' && ch != ',' && ch != '.' && ch != ';') {
        return false;
      }
    }
    return true;
  }
}
