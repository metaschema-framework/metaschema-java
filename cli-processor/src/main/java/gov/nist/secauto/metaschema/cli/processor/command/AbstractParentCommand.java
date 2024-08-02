/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;

import org.apache.commons.cli.CommandLine;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractParentCommand implements ICommand {
  @NonNull
  private final Map<String, ICommand> commandToSubcommandHandlerMap;
  private final boolean subCommandRequired;

  @SuppressWarnings("null")
  protected AbstractParentCommand(boolean subCommandRequired) {
    this.commandToSubcommandHandlerMap = Collections.synchronizedMap(new LinkedHashMap<>());
    this.subCommandRequired = subCommandRequired;
  }

  protected final void addCommandHandler(ICommand handler) {
    String commandName = handler.getName();
    this.commandToSubcommandHandlerMap.put(commandName, handler);
  }

  @Override
  public ICommand getSubCommandByName(String name) {
    return commandToSubcommandHandlerMap.get(name);
  }

  @SuppressWarnings("null")
  @Override
  public Collection<ICommand> getSubCommands() {
    return Collections.unmodifiableCollection(commandToSubcommandHandlerMap.values());
  }

  @Override
  public boolean isSubCommandRequired() {
    return subCommandRequired;
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  @NonNull
  protected ExitStatus executeCommand(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine) {
    callingContext.showHelp();
    ExitStatus status;
    if (isSubCommandRequired()) {
      status = ExitCode.INVALID_COMMAND
          .exitMessage("Please use one of the following sub-commands: " +
              getSubCommands().stream()
                  .map(ICommand::getName)
                  .collect(Collectors.joining(", ")));
    } else {
      status = ExitCode.OK.exit();
    }
    return status;
  }

}
