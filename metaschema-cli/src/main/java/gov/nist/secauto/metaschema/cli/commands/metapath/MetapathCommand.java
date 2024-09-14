/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands.metapath;

import gov.nist.secauto.metaschema.cli.processor.command.AbstractParentCommand;

public class MetapathCommand
    extends AbstractParentCommand {
  private static final String COMMAND = "metapath";

  /**
   * Constructor for a new Metapath command.
   */
  public MetapathCommand() {
    super(true);
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
