/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.commands.metapath.MetapathCommand;
import gov.nist.secauto.metaschema.cli.processor.command.ICommand;

import java.util.List;

public final class MetaschemaCommands {
  public static final List<ICommand> COMMANDS = List.of(
      new ValidateModuleCommand(),
      new GenerateSchemaCommand(),
      new ValidateContentUsingModuleCommand(),
      new MetapathCommand());

  private MetaschemaCommands() {
    // disable construction
  }
}
