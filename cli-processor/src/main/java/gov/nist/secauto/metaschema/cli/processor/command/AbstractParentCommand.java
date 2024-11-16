/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.CommandLine;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for a command that supports hierarchical command structure with
 * child commands. This class provides the foundation for implementing complex
 * CLI commands that can have multiple levels of sub-commands.
 * <p>
 * This class is thread-safe and supports concurrent access to command handlers.
 */
public abstract class AbstractParentCommand implements ICommand {
  @NonNull
  private final Map<String, ICommand> commandToSubcommandHandlerMap;

  /**
   * Construct a new parent command.
   */
  protected AbstractParentCommand() {
    this.commandToSubcommandHandlerMap = ObjectUtils.notNull(Collections.synchronizedMap(new LinkedHashMap<>()));
  }

  /**
   * Add a child command.
   *
   * @param handler
   *          the command handler for the child command
   */
  protected final void addCommandHandler(ICommand handler) {
    String commandName = handler.getName();
    this.commandToSubcommandHandlerMap.put(commandName, handler);
  }

  @Override
  public ICommand getSubCommandByName(String name) {
    return commandToSubcommandHandlerMap.get(name);
  }

  @Override
  public Collection<ICommand> getSubCommands() {
    return ObjectUtils.notNull(Collections.unmodifiableCollection(commandToSubcommandHandlerMap.values()));
  }

  @Override
  public boolean isSubCommandRequired() {
    return true;
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  @NonNull
  private ExitStatus executeCommand(
      @NonNull CallingContext callingContext,
      @SuppressWarnings("unused") @NonNull CommandLine commandLine) {
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
