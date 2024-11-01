/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.CommandLine;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractCommandExecutor implements ICommandExecutor {
  @NonNull
  private final CallingContext callingContext;
  @NonNull
  private final CommandLine commandLine;

  public AbstractCommandExecutor(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine) {
    this.callingContext = callingContext;
    this.commandLine = commandLine;
  }

  @NonNull
  protected CallingContext getCallingContext() {
    return callingContext;
  }

  @NonNull
  protected CommandLine getCommandLine() {
    return commandLine;
  }

  @Override
  public abstract void execute() throws CommandExecutionException;

  @NonNull
  protected ICommand getCommand() {
    return ObjectUtils.requireNonNull(getCallingContext().getTargetCommand());
  }
}
