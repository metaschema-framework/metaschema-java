/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.Option;

import edu.umd.cs.findbugs.annotations.NonNull;

public class MetaschemaCommandSupport {
  @NonNull
  public static final Option METASCHEMA_OPTION = ObjectUtils.notNull(
      Option.builder("m")
          .hasArg()
          .argName("FILE")
          .required()
          .desc("metaschema resource")
          .build());
}
