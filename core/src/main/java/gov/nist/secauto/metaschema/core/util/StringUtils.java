/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a collection of utilities for checking and managing strings.
 * <p>
 * This utility class provides methods for string validation and manipulation,
 * with strict null-safety guarantees. All methods in this class are thread-safe
 * and throw appropriate exceptions for invalid inputs.
 */
public final class StringUtils {
  private StringUtils() {
    // disable construction
  }

  /**
   * Require a non-empty string value.
   *
   * @param string
   *          the object reference to check for emptiness
   * @return {@code string} if not {@code null} or empty
   * @throws NullPointerException
   *           if {@code string} is {@code null}
   * @throws IllegalArgumentException
   *           if {@code string} is empty
   */
  @NonNull
  public static String requireNonEmpty(@NonNull String string) {
    return requireNonEmpty(string, "String is empty.");
  }

  /**
   * Require a non-empty string value.
   *
   * @param string
   *          the object reference to check for emptiness
   * @param message
   *          detail message to be used in the event that an {@code
   *                IllegalArgumentException} is thrown
   * @return {@code string} if not {@code null} or empty
   * @throws NullPointerException
   *           if {@code string} is {@code null}
   * @throws IllegalArgumentException
   *           if {@code string} is empty
   */
  @NonNull
  public static String requireNonEmpty(@NonNull String string, @NonNull String message) {
    if (string.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }

  /**
   * Searches for instances of {@code pattern} in {@code text}. Replace each
   * matching occurrence using the {@code replacementFunction}.
   * <p>
   * This method builds a new string by efficiently copying unmatched segments and
   * applying the replacement function only to matched portions.
   *
   * @param text
   *          the text to search
   * @param pattern
   *          the pattern to search for
   * @param replacementFunction
   *          a function that will provided the replacement text
   * @return the resulting text after replacing matching occurrences in
   *         {@code text}
   */
  public static CharSequence replaceTokens(
      @NonNull CharSequence text,
      @NonNull Pattern pattern,
      @NonNull Function<Matcher, CharSequence> replacementFunction) {
    int lastIndex = 0;
    StringBuilder retval = new StringBuilder();
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      retval.append(text, lastIndex, matcher.start())
          .append(replacementFunction.apply(matcher));

      lastIndex = matcher.end();
    }
    if (lastIndex < text.length()) {
      retval.append(text, lastIndex, text.length());
    }
    return retval;
  }

}
