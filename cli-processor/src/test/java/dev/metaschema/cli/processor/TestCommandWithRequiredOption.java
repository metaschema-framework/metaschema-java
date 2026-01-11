/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.Collection;
import java.util.List;

import dev.metaschema.cli.processor.command.AbstractTerminalCommand;
import dev.metaschema.cli.processor.command.ICommandExecutor;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A test command that requires a specific option.
 */
class TestCommandWithRequiredOption
    extends AbstractTerminalCommand {

  private static final Option REQUIRED_OPTION = Option.builder()
      .longOpt("required-opt")
      .desc("A required option for testing")
      .hasArg()
      .argName("VALUE")
      .get();

  @Override
  @NonNull
  public String getName() {
    return "test-cmd-with-option";
  }

  @Override
  @NonNull
  public String getDescription() {
    return "A test command requiring an option";
  }

  @Override
  public Collection<? extends Option> gatherOptions() {
    return ObjectUtils.notNull(List.of(REQUIRED_OPTION));
  }

  @Override
  public void validateOptions(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) throws InvalidArgumentException {
    if (!cmdLine.hasOption(REQUIRED_OPTION)) {
      throw new InvalidArgumentException("The '--required-opt' option is required.");
    }
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
