/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import org.apache.commons.cli.Option;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A collection of utilities for handling command line options.
 */
public final class OptionUtils {

  private OptionUtils() {
    // disable construction
  }

  /**
   * Generate the argument text for the given option.
   *
   * @param option
   *          the CLI option
   * @return the argument text
   */
  @NonNull
  public static String toArgument(@NonNull Option option) {
    return option.hasLongOpt() ? "--" + option.getLongOpt() : "-" + option.getOpt();
  }

}
