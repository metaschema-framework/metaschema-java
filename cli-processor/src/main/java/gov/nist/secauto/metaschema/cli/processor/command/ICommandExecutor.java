/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;

import org.apache.commons.cli.CommandLine;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ICommandExecutor {
  @NonNull
  ExitStatus execute();

  @NonNull
  static ICommandExecutor using(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine,
      @NonNull ExecutionFunction function) {
    return new ICommandExecutor() {
      @Override
      public ExitStatus execute() {
        return function.execute(callingContext, commandLine);
      }

    };
  }

  @FunctionalInterface
  interface ExecutionFunction {
    @NonNull
    ExitStatus execute(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine);
  }
}
