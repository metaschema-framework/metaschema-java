/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class ReplacementScanner {
  private ReplacementScanner() {
    // disable construction
  }

  /**
   * Search for instances of {@code pattern} in {@code text}. Replace each
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
  public static CharSequence replaceTokens(@NonNull CharSequence text, @NonNull Pattern pattern,
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
