/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor;

import org.apache.commons.cli.CommandLine;

import dev.metaschema.cli.processor.command.AbstractTerminalCommand;
import dev.metaschema.cli.processor.command.ICommandExecutor;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A minimal command implementation for testing.
 */
class TestCommand
    extends AbstractTerminalCommand {

  @Override
  @NonNull
  public String getName() {
    return "test-cmd";
  }

  @Override
  @NonNull
  public String getDescription() {
    return "A test command";
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
