/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.FormatFunctionException;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-format-integer">fn:format-integer</a>
 * functions.
 *
 * @see <a href=
 *      "https://www.w3.org/TR/xpath-functions-31/#func-format-integer">XPath
 *      3.1 fn:format-integer</a>
 */
public final class FnFormatInteger {
  private static final String NAME = "format-integer";

  @NonNull
  static final IFunction SIGNATURE_TWO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("value")
          .type(IIntegerItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("picture")
          .type(IStringItem.type())
          .one()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnFormatInteger::executeTwoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_THREE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("value")
          .type(IIntegerItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("picture")
          .type(IStringItem.type())
          .one()
          .build())
      .argument(IArgument.builder()
          .name("lang")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnFormatInteger::executeThreeArg)
      .build();

  /**
   * Roman numeral values in descending order, used for converting integers to
   * Roman numeral representation.
   */
  private static final int[] ROMAN_VALUES = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };

  /**
   * Roman numeral symbols corresponding to {@link #ROMAN_VALUES}.
   */
  private static final String[] ROMAN_SYMBOLS = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV",
      "I" };

  private static final String[] ONES
      = { "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
          "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
          "seventeen", "eighteen", "nineteen" };

  private static final String[] TENS
      = { "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety" };

  /**
   * Pattern to match the format modifier portion of a picture string. The
   * modifier appears after the last {@code ;} in the picture and must match
   * {@code ^([co](\(.+\))?)?[at]?$}.
   */
  private static final Pattern MODIFIER_PATTERN
      = Pattern.compile("^([co](\\(.+\\))?)?[at]?$");

  private FnFormatInteger() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeTwoArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    IIntegerItem value = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IStringItem picture = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(1).getFirstItem(true)));

    String lang = dynamicContext.getStaticContext().getDefaultLanguage();

    return ISequence.of(IStringItem.valueOf(
        fnFormatInteger(value, picture.asString(), lang)));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeThreeArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    IIntegerItem value = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IStringItem picture = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(1).getFirstItem(true)));
    IStringItem lang = FunctionUtils.asTypeOrNull(arguments.get(2).getFirstItem(true));

    return ISequence.of(IStringItem.valueOf(
        fnFormatInteger(value, picture.asString(), lang == null ? null : lang.asString())));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-format-integer">fn:format-integer</a>.
   *
   * @param value
   *          the integer value to format, or {@code null} for empty sequence
   * @param picture
   *          the picture string controlling the format
   * @param lang
   *          the language for locale-dependent formatting, or {@code null} to use
   *          the default
   * @return the formatted integer string
   * @throws FormatFunctionException
   *           if the picture string contains an invalid format token
   */
  @NonNull
  public static String fnFormatInteger(
      @Nullable IIntegerItem value,
      @NonNull String picture,
      @Nullable String lang) {

    // If $value is an empty sequence, return zero-length string
    if (value == null) {
      return "";
    }

    if (picture.isEmpty()) {
      throw new FormatFunctionException(
          FormatFunctionException.INVALID_FORMAT_TOKEN,
          "The picture string for format-integer must not be empty.");
    }

    // Parse primary format token and modifier
    // The modifier is separated by the last ';' that is part of the modifier
    // syntax. We need to find the split point.
    String[] parsed = parsePicture(picture);
    String primaryToken = parsed[0];
    String modifier = parsed[1];

    // Parse modifier flags
    boolean ordinal = false;
    if (!modifier.isEmpty()) {
      Matcher modMatcher = MODIFIER_PATTERN.matcher(modifier);
      if (!modMatcher.matches()) {
        throw new FormatFunctionException(
            FormatFunctionException.INVALID_FORMAT_TOKEN,
            String.format("Invalid format modifier '%s' in picture string '%s'.", modifier, picture));
      }
      String modLetters = modMatcher.group(1);
      if (modLetters != null && modLetters.startsWith("o")) {
        ordinal = true;
      }
    }

    BigInteger bigValue = value.asInteger();
    boolean negative = bigValue.signum() < 0;
    BigInteger absValue = bigValue.abs();

    String formatted = formatWithPrimaryToken(primaryToken, absValue, picture);

    // Apply ordinal suffix if requested and supported for this format token.
    // Per spec: "If ordinal numbering is not supported for the combination of
    // the format token, the language, and the string appearing in parentheses,
    // the request is ignored and cardinal numbers are generated instead."
    // Only decimal digit patterns support ordinal suffix in this implementation.
    if (ordinal && isDecimalDigitPattern(primaryToken)) {
      formatted = applyOrdinal(formatted, absValue);
    }

    // Prepend minus sign for negative values
    if (negative) {
      formatted = "-" + formatted;
    }

    return ObjectUtils.notNull(formatted);
  }

  /**
   * Parses the picture string into the primary format token and the format
   * modifier. The modifier is separated from the primary token by the last
   * semicolon. However, semicolons can appear as grouping separators within the
   * primary token. The modifier must match {@code ^([co](\(.+\))?)?[at]?$}.
   *
   * @param picture
   *          the picture string to parse
   * @return a two-element array where index 0 is the primary format token and
   *         index 1 is the format modifier
   */
  @NonNull
  private static String[] parsePicture(@NonNull String picture) {
    // Try splitting at each ';' from the right. The part after the ';' must match
    // the modifier pattern (or be empty). The first valid split from the right is
    // the correct one.
    for (int i = picture.length() - 1; i >= 0; i--) {
      if (picture.charAt(i) == ';') {
        String candidateModifier = picture.substring(i + 1);
        String candidateToken = picture.substring(0, i);
        if (MODIFIER_PATTERN.matcher(candidateModifier).matches()) {
          return new String[] { candidateToken, candidateModifier };
        }
      }
    }
    // No valid modifier split found; the entire picture is the primary token
    return new String[] { picture, "" };
  }

  /**
   * Formats the absolute integer value using the given primary format token.
   *
   * @param primaryToken
   *          the primary format token
   * @param absValue
   *          the absolute value of the integer to format
   * @param picture
   *          the original picture string, used in error messages
   * @return the formatted string
   * @throws FormatFunctionException
   *           if the format token is invalid
   */
  @NonNull
  private static String formatWithPrimaryToken(
      @NonNull String primaryToken,
      @NonNull BigInteger absValue,
      @NonNull String picture) {

    if (primaryToken.isEmpty()) {
      throw new FormatFunctionException(
          FormatFunctionException.INVALID_FORMAT_TOKEN,
          String.format("The primary format token in picture string '%s' must not be empty.", picture));
    }

    // Check for known named tokens
    if ("a".equals(primaryToken)) {
      return formatAlphabetic(absValue, false);
    }
    if ("A".equals(primaryToken)) {
      return formatAlphabetic(absValue, true);
    }
    if ("i".equals(primaryToken)) {
      return formatRoman(absValue, false);
    }
    if ("I".equals(primaryToken)) {
      return formatRoman(absValue, true);
    }
    if ("w".equals(primaryToken)) {
      return formatWords(absValue, false, false);
    }
    if ("W".equals(primaryToken)) {
      return formatWords(absValue, true, false);
    }
    if ("Ww".equals(primaryToken)) {
      return formatWords(absValue, false, true);
    }

    // Must be a decimal digit pattern
    return formatDecimalDigitPattern(primaryToken, absValue, picture);
  }

  /**
   * Formats an integer as a decimal digit pattern with optional grouping
   * separators and zero-padding.
   *
   * @param pattern
   *          the decimal digit pattern portion of the picture string
   * @param absValue
   *          the absolute value of the integer to format
   * @param picture
   *          the original picture string, used in error messages
   * @return the formatted decimal string
   * @throws FormatFunctionException
   *           if the pattern is invalid
   */
  @NonNull
  @SuppressWarnings("PMD.CyclomaticComplexity")
  private static String formatDecimalDigitPattern(
      @NonNull String pattern,
      @NonNull BigInteger absValue,
      @NonNull String picture) {

    // Parse the pattern to identify mandatory digits, optional digits, and grouping
    // separators. Mandatory digits are '0'-'9', optional digits are '#', and
    // everything else that is not a letter or digit is a grouping separator.
    List<Character> patternChars = new ArrayList<>();
    List<Boolean> isSeparator = new ArrayList<>();

    int mandatoryCount = 0;
    boolean foundMandatory = false;
    boolean hasOptional = false;

    for (int i = 0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);
      patternChars.add(ch);

      if (ch >= '0' && ch <= '9') {
        isSeparator.add(false);
        mandatoryCount++;
        foundMandatory = true;
      } else if (ch == '#') {
        if (foundMandatory) {
          // optional digits must precede mandatory digits
          throw new FormatFunctionException(
              FormatFunctionException.INVALID_FORMAT_TOKEN,
              String.format(
                  "In picture string '%s', optional-digit-sign '#' must precede all mandatory-digit-signs.",
                  picture));
        }
        isSeparator.add(false);
        hasOptional = true;
      } else if (!Character.isLetterOrDigit(ch)) {
        // grouping separator
        isSeparator.add(true);
      } else {
        // unrecognized letter/digit that isn't 0-9 or #; fallback to format '1'
        return ObjectUtils.notNull(absValue.toString());
      }
    }

    if (mandatoryCount == 0) {
      throw new FormatFunctionException(
          FormatFunctionException.INVALID_FORMAT_TOKEN,
          String.format(
              "The decimal digit pattern in picture string '%s' must contain at least one mandatory digit.",
              picture));
    }

    // Validate: separators not at start or end, and not adjacent
    validateSeparators(patternChars, isSeparator, picture);

    // Determine the grouping separator character and positions (from right)
    // We work from the right side of the pattern.
    char groupingSep = 0;
    List<Integer> groupPositions = new ArrayList<>();
    int digitIndex = 0;
    for (int i = patternChars.size() - 1; i >= 0; i--) {
      if (Boolean.TRUE.equals(isSeparator.get(i))) {
        groupingSep = patternChars.get(i);
        groupPositions.add(digitIndex);
      } else {
        digitIndex++;
      }
    }

    // Format the number with minimum width
    String digits = absValue.toString();
    if (digits.length() < mandatoryCount) {
      StringBuilder padded = new StringBuilder();
      for (int i = digits.length(); i < mandatoryCount; i++) {
        padded.append('0');
      }
      padded.append(digits);
      digits = padded.toString();
    }

    // Insert grouping separators if any
    if (!groupPositions.isEmpty() && groupingSep != 0) {
      digits = insertGroupingSeparators(digits, groupingSep, groupPositions, hasOptional);
    }

    return ObjectUtils.notNull(digits);
  }

  /**
   * Validates that grouping separators do not appear at the start or end of the
   * pattern, and that no two separators are adjacent.
   *
   * @param patternChars
   *          the characters in the pattern
   * @param isSeparator
   *          flags indicating which positions are separators
   * @param picture
   *          the original picture string, used in error messages
   * @throws FormatFunctionException
   *           if separator placement is invalid
   */
  private static void validateSeparators(
      @NonNull List<Character> patternChars,
      @NonNull List<Boolean> isSeparator,
      @NonNull String picture) {

    if (!patternChars.isEmpty()) {
      if (Boolean.TRUE.equals(isSeparator.get(0))) {
        throw new FormatFunctionException(
            FormatFunctionException.INVALID_FORMAT_TOKEN,
            String.format(
                "Grouping separator must not appear at the start of the pattern in picture string '%s'.",
                picture));
      }
      if (Boolean.TRUE.equals(isSeparator.get(isSeparator.size() - 1))) {
        throw new FormatFunctionException(
            FormatFunctionException.INVALID_FORMAT_TOKEN,
            String.format(
                "Grouping separator must not appear at the end of the pattern in picture string '%s'.",
                picture));
      }
      for (int i = 1; i < isSeparator.size(); i++) {
        if (Boolean.TRUE.equals(isSeparator.get(i)) && Boolean.TRUE.equals(isSeparator.get(i - 1))) {
          throw new FormatFunctionException(
              FormatFunctionException.INVALID_FORMAT_TOKEN,
              String.format(
                  "Adjacent grouping separators are not allowed in picture string '%s'.",
                  picture));
        }
      }
    }
  }

  /**
   * Inserts grouping separators into a digit string at specified positions.
   *
   * <p>
   * If separators appear at regular intervals (all same character, evenly
   * spaced), the pattern is extrapolated to the left. Otherwise, separators are
   * inserted only at the explicit positions.
   *
   * @param digits
   *          the digit string to insert separators into
   * @param separator
   *          the grouping separator character
   * @param positions
   *          the positions (from right, 0-based digit positions) where separators
   *          appear in the pattern
   * @param hasOptional
   *          whether the pattern contains optional-digit-signs
   * @return the digit string with grouping separators inserted
   */
  @NonNull
  private static String insertGroupingSeparators(
      @NonNull String digits,
      char separator,
      @NonNull List<Integer> positions,
      boolean hasOptional) {

    // Determine if the pattern is regular (all positions at same interval)
    int groupSize = -1;
    boolean regular = true;

    List<Integer> sorted = new ArrayList<>(positions);
    sorted.sort(null);

    if (sorted.size() == 1) {
      groupSize = sorted.get(0);
      regular = true;
    } else {
      // Check if all positions are at regular intervals (multiples of the smallest)
      int candidate = sorted.get(0);
      regular = true;
      for (int i = 0; i < sorted.size(); i++) {
        if (sorted.get(i) != candidate * (i + 1)) {
          regular = false;
          break;
        }
      }
      if (regular) {
        groupSize = candidate;
      }
    }

    // Build result from right to left, inserting separators at group boundaries
    StringBuilder result = new StringBuilder();
    int digitCount = digits.length();
    int rightDigitCount = 0;

    for (int i = digitCount - 1; i >= 0; i--) {
      if (rightDigitCount > 0) {
        boolean insertSep;
        if (regular && groupSize > 0) {
          insertSep = rightDigitCount % groupSize == 0;
        } else {
          insertSep = sorted.contains(rightDigitCount);
        }
        if (insertSep) {
          result.insert(0, separator);
        }
      }
      result.insert(0, digits.charAt(i));
      rightDigitCount++;
    }

    return ObjectUtils.notNull(result.toString());
  }

  /**
   * Formats an integer as an alphabetic sequence (a, b, ..., z, aa, ab, ...).
   *
   * <p>
   * The value 1 maps to 'a', 2 to 'b', ..., 26 to 'z', 27 to 'aa', 28 to 'ab',
   * and so on, similar to spreadsheet column names. Zero is formatted as '0'.
   *
   * @param absValue
   *          the absolute value of the integer
   * @param uppercase
   *          whether to produce uppercase letters
   * @return the alphabetic representation
   */
  @NonNull
  private static String formatAlphabetic(@NonNull BigInteger absValue, boolean uppercase) {
    if (absValue.signum() == 0) {
      return "0";
    }

    char base = uppercase ? 'A' : 'a';
    StringBuilder result = new StringBuilder();
    BigInteger remaining = absValue;
    BigInteger twentySix = BigInteger.valueOf(26);

    while (remaining.signum() > 0) {
      remaining = remaining.subtract(BigInteger.ONE);
      int digit = remaining.mod(twentySix).intValue();
      result.insert(0, (char) (base + digit));
      remaining = remaining.divide(twentySix);
    }

    return ObjectUtils.notNull(result.toString());
  }

  /**
   * Formats an integer as a Roman numeral string using standard subtractive
   * notation. Supports values from 1 to 3999.
   *
   * @param absValue
   *          the absolute value of the integer
   * @param uppercase
   *          whether to produce uppercase Roman numerals
   * @return the Roman numeral representation
   * @throws FormatFunctionException
   *           if the value is zero or exceeds 3999
   */
  @NonNull
  private static String formatRoman(@NonNull BigInteger absValue, boolean uppercase) {
    if (absValue.signum() == 0 || absValue.compareTo(BigInteger.valueOf(3999)) > 0) {
      // Fallback: use decimal for values outside Roman numeral range
      return ObjectUtils.notNull(absValue.toString());
    }

    int num = absValue.intValue();
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < ROMAN_VALUES.length; i++) {
      while (num >= ROMAN_VALUES[i]) {
        result.append(ROMAN_SYMBOLS[i]);
        num -= ROMAN_VALUES[i];
      }
    }

    String roman = result.toString();
    if (!uppercase) {
      roman = roman.toLowerCase(Locale.ROOT);
    }
    return ObjectUtils.notNull(roman);
  }

  /**
   * Formats an integer as English words.
   *
   * @param absValue
   *          the absolute value of the integer
   * @param allUppercase
   *          whether to produce all-uppercase output
   * @param titleCase
   *          whether to produce title-case output (first letter of each word
   *          capitalized)
   * @return the English word representation
   */
  @NonNull
  private static String formatWords(@NonNull BigInteger absValue, boolean allUppercase, boolean titleCase) {
    String words = numberToWords(absValue);

    if (allUppercase) {
      words = words.toUpperCase(Locale.ROOT);
    } else if (titleCase) {
      words = toTitleCase(words);
    }

    return ObjectUtils.notNull(words);
  }

  /**
   * Converts a non-negative integer to its English word representation. Supports
   * values up to 999,999,999 and beyond through recursive decomposition.
   *
   * @param value
   *          the non-negative integer value
   * @return the English word representation in lowercase
   */
  @NonNull
  @SuppressWarnings("PMD.CyclomaticComplexity")
  private static String numberToWords(@NonNull BigInteger value) {
    if (value.signum() == 0) {
      return "zero";
    }

    if (value.compareTo(BigInteger.valueOf(20)) < 0) {
      return ObjectUtils.notNull(ONES[value.intValue()]);
    }

    if (value.compareTo(BigInteger.valueOf(100)) < 0) {
      int tens = value.intValue() / 10;
      int ones = value.intValue() % 10;
      if (ones == 0) {
        return ObjectUtils.notNull(TENS[tens]);
      }
      return ObjectUtils.notNull(TENS[tens] + "-" + ONES[ones]);
    }

    if (value.compareTo(BigInteger.valueOf(1000)) < 0) {
      int hundreds = value.intValue() / 100;
      int remainder = value.intValue() % 100;
      if (remainder == 0) {
        return ONES[hundreds] + " hundred";
      }
      return ONES[hundreds] + " hundred " + numberToWords(BigInteger.valueOf(remainder));
    }

    // Handle thousands, millions, billions, etc.
    return formatLargeNumber(value);
  }

  /**
   * Formats a number of 1000 or greater using the standard naming convention
   * (thousand, million, billion, trillion, etc.).
   *
   * @param value
   *          the value to format (must be >= 1000)
   * @return the English word representation
   */
  @NonNull
  private static String formatLargeNumber(@NonNull BigInteger value) {
    String[] scaleWords = { "", "thousand", "million", "billion", "trillion",
        "quadrillion", "quintillion", "sextillion", "septillion" };
    BigInteger oneThousand = BigInteger.valueOf(1000);

    // Fall back to decimal representation for values beyond supported scale
    BigInteger maxSupported = BigInteger.TEN.pow((scaleWords.length) * 3);
    if (value.compareTo(maxSupported) >= 0) {
      return ObjectUtils.notNull(value.toString());
    }

    // Decompose into groups of three digits
    List<Integer> groups = new ArrayList<>();
    BigInteger remaining = value;
    while (remaining.signum() > 0) {
      groups.add(remaining.mod(oneThousand).intValue());
      remaining = remaining.divide(oneThousand);
    }

    StringBuilder result = new StringBuilder();
    for (int i = groups.size() - 1; i >= 0; i--) {
      int group = groups.get(i);
      if (group == 0) {
        continue;
      }
      if (result.length() > 0) {
        result.append(' ');
      }
      result.append(numberToWords(BigInteger.valueOf(group)));
      if (i > 0 && i < scaleWords.length) {
        result.append(' ').append(scaleWords[i]);
      }
    }

    return ObjectUtils.notNull(result.toString());
  }

  /**
   * Converts a hyphen-separated word string to title case, where the first letter
   * of each word (split on spaces and hyphens) is capitalized.
   *
   * @param input
   *          the input string in lowercase
   * @return the title-cased string
   */
  @NonNull
  private static String toTitleCase(@NonNull String input) {
    StringBuilder result = new StringBuilder();
    boolean capitalizeNext = true;

    for (int i = 0; i < input.length(); i++) {
      char ch = input.charAt(i);
      if (ch == ' ' || ch == '-') {
        result.append(ch);
        capitalizeNext = true;
      } else if (capitalizeNext) {
        result.append(Character.toUpperCase(ch));
        capitalizeNext = false;
      } else {
        result.append(ch);
      }
    }

    return ObjectUtils.notNull(result.toString());
  }

  /**
   * Checks whether the given primary format token is a decimal digit pattern
   * (contains at least one ASCII digit or '#'). Named format tokens like
   * {@code a}, {@code i}, {@code w}, etc. are not decimal digit patterns.
   *
   * @param primaryToken
   *          the primary format token
   * @return {@code true} if the token is a decimal digit pattern
   */
  private static boolean isDecimalDigitPattern(@NonNull String primaryToken) {
    for (int i = 0; i < primaryToken.length(); i++) {
      char ch = primaryToken.charAt(i);
      if ((ch >= '0' && ch <= '9') || ch == '#') {
        return true;
      }
    }
    return false;
  }

  /**
   * Appends an ordinal suffix to a formatted number string. For English, the
   * rules are:
   * <ul>
   * <li>If the last two digits are 11, 12, or 13: "th"</li>
   * <li>If the last digit is 1: "st"</li>
   * <li>If the last digit is 2: "nd"</li>
   * <li>If the last digit is 3: "rd"</li>
   * <li>Otherwise: "th"</li>
   * </ul>
   *
   * @param formatted
   *          the formatted number string
   * @param absValue
   *          the absolute value of the integer
   * @return the formatted string with ordinal suffix appended
   */
  @NonNull
  private static String applyOrdinal(
      @NonNull String formatted,
      @NonNull BigInteger absValue) {

    int num = absValue.mod(BigInteger.valueOf(100)).intValue();
    int lastDigit = absValue.mod(BigInteger.valueOf(10)).intValue();

    String suffix;
    if (num >= 11 && num <= 13) {
      suffix = "th";
    } else if (lastDigit == 1) {
      suffix = "st";
    } else if (lastDigit == 2) {
      suffix = "nd";
    } else if (lastDigit == 3) {
      suffix = "rd";
    } else {
      suffix = "th";
    }

    return ObjectUtils.notNull(formatted + suffix);
  }
}
