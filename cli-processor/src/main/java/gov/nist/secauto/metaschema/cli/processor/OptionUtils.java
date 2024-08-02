/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import org.apache.commons.cli.Option;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class OptionUtils {

  private OptionUtils() {
    // disable construction
  }

  @NonNull
  public static String toArgument(@NonNull Option option) {
    return option.hasLongOpt() ? "--" + option.getLongOpt() : "-" + option.getOpt();
  }

}
