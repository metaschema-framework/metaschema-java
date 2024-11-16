/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.CommandLine;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for implementation that perform the operation supported by a
 * command.
 */
public abstract class AbstractCommandExecutor implements ICommandExecutor {
  @NonNull
  private final CallingContext callingContext;
  @NonNull
  private final CommandLine commandLine;

  /**
   * Construct a new command executor.
   *
   * @param callingContext
   *          the context of the command execution
   * @param commandLine
   *          the parsed command line details
   */
  protected AbstractCommandExecutor(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine) {
    this.callingContext = callingContext;
    this.commandLine = commandLine;
  }

  /**
   * Get the context of the command execution, which provides access to the
   * execution environment needed for command processing.
   *
   * @return the context
   */
  @NonNull
  protected CallingContext getCallingContext() {
    return callingContext;
  }

  /**
   * Get the parsed command line details containing the command options and
   * arguments provided by the user during execution.
   *
   * @return the cli details
   */
  @NonNull
  protected CommandLine getCommandLine() {
    return commandLine;
  }

  @Override
  public abstract void execute() throws CommandExecutionException;

  /**
   * Get the command associated with this execution.
   *
   * @return the command
   */
  @NonNull
  protected ICommand getCommand() {
    return ObjectUtils.requireNonNull(getCallingContext().getTargetCommand());
  }
}
