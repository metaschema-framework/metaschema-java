/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands.metapath;

import gov.nist.secauto.metaschema.cli.processor.command.AbstractParentCommand;

/**
 * This sub-command implementation contains all command that relate to Metapath
 * execution.
 */
public class MetapathCommand
    extends AbstractParentCommand {
  private static final String COMMAND = "metapath";

  /**
   * Constructor for a new Metapath command.
   */
  public MetapathCommand() {
    addCommandHandler(new ListFunctionsSubcommand());
    addCommandHandler(new EvaluateMetapathCommand());
  }

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Perform a Metapath operation.";
  }
}
