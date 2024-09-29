/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.regex;

import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class RegexUtil {

  /**
   * Parse the regular expression flags according to
   * <a href="https://www.w3.org/TR/xpath-functions-31/#flags">the
   * specification</a> producing a bitmask suitable for use in
   * {@link Pattern#compile(String, int)}.
   *
   * @param flags
   *          the flags to process
   * @return the bitmask
   */
  public static int parseFlags(@NonNull String flags) {
    return flags.codePoints()
        .map(i -> characterToFlag((char) i))
        .reduce(0, (mask, flag) -> mask | flag);
  }

  private static int characterToFlag(char ch) {
    int retval;
    switch (ch) {
    case 's':
      retval = Pattern.DOTALL;
      break;
    case 'm':
      retval = Pattern.MULTILINE;
      break;
    case 'i':
      retval = Pattern.CASE_INSENSITIVE;
      break;
    case 'x':
      retval = Pattern.COMMENTS;
      break;
    case 'q':
      retval = Pattern.LITERAL;
      break;
    default:
      throw new RegularExpressionMetapathException(RegularExpressionMetapathException.INVALID_FLAG,
          String.format("Invalid flag '%s'.", ch));
    }
    return retval;
  }

  private RegexUtil() {
    // disable construction
  }
}
