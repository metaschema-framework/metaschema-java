/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides a collection of utilities for checking and managing strings.
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
  @SuppressWarnings("null")
  @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  public static String requireNonEmpty(@Nullable String string) {
    if (string.isEmpty()) {
      throw new IllegalArgumentException("String is empty.");
    }
    return string;
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
  @SuppressWarnings("null")
  @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  public static String requireNonEmpty(@Nullable String string, @NonNull String message) {
    if (string.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }

  /**
   * Searches for instances of {@code pattern} in {@code text}. Replace each
   * matching occurrence using the {@code replacementFunction}.
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
      Function<Matcher, CharSequence> replacementFunction) {
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
