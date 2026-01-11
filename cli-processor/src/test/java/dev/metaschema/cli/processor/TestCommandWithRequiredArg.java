/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor;

import org.apache.commons.cli.CommandLine;

import java.util.List;

import dev.metaschema.cli.processor.command.AbstractTerminalCommand;
import dev.metaschema.cli.processor.command.ExtraArgument;
import dev.metaschema.cli.processor.command.ICommandExecutor;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A test command that requires an extra argument.
 */
class TestCommandWithRequiredArg
    extends AbstractTerminalCommand {

  @Override
  @NonNull
  public String getName() {
    return "test-cmd-with-arg";
  }

  @Override
  @NonNull
  public String getDescription() {
    return "A test command requiring an argument";
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return List.of(
        ExtraArgument.newInstance("required-file", true));
  }

  @Override
  public ICommandExecutor newExecutor(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  private void executeCommand(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) {
    // Do nothing - success
  }
}
