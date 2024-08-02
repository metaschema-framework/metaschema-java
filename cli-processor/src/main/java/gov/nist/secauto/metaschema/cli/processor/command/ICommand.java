/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ICommand {
  @NonNull
  String getName();

  @NonNull
  String getDescription();

  @NonNull
  default List<ExtraArgument> getExtraArguments() {
    return CollectionUtil.emptyList();
  }

  default int requiredExtraArgumentsCount() {
    return (int) getExtraArguments().stream()
        .filter(arg -> arg.isRequired())
        .count();
  }

  @NonNull
  default Collection<? extends Option> gatherOptions() {
    // by default there are no options to handle
    return CollectionUtil.emptyList();
  }

  @NonNull
  Collection<ICommand> getSubCommands();

  boolean isSubCommandRequired();

  @SuppressWarnings("unused")
  default ICommand getSubCommandByName(@NonNull String name) {
    // no sub commands by default
    return null;
  }

  @SuppressWarnings("unused")
  default void validateOptions(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) throws InvalidArgumentException {
    // by default there are no options to handle
  }

  @NonNull
  ICommandExecutor newExecutor(@NonNull CallingContext callingContext, @NonNull CommandLine cmdLine);
}
