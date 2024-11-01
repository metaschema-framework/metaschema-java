/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;

import org.apache.commons.cli.CommandLine;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ICommandExecutor {
  void execute() throws CommandExecutionException;

  @NonNull
  static ICommandExecutor using(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine,
      @NonNull ExecutionFunction function) {
    return () -> function.execute(callingContext, commandLine);
  }

  @FunctionalInterface
  interface ExecutionFunction {
    void execute(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine) throws CommandExecutionException;
  }
}
