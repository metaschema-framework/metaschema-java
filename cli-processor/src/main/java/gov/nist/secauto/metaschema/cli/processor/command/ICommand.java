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
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A command line interface command.
 */
public interface ICommand {
  /**
   * Get the name of the command.
   * <p>
   * This name is used to call the command as a command line argument.
   *
   * @return the command's name
   */
  @NonNull
  String getName();

  /**
   * Get a description of what the command does.
   * <p>
   * This description is displayed in help output.
   *
   * @return the description
   */
  @NonNull
  String getDescription();

  /**
   * Get the non-option arguments.
   *
   * @return the arguments, or an empty list if there are no arguments
   */
  @NonNull
  default List<ExtraArgument> getExtraArguments() {
    return CollectionUtil.emptyList();
  }

  /**
   * Used to gather options directly associated with this command.
   *
   * @return the options
   */
  @NonNull
  default Collection<? extends Option> gatherOptions() {
    // by default there are no options to handle
    return CollectionUtil.emptyList();
  }

  /**
   * Get any sub-commands associated with this command.
   *
   * @return the sub-commands
   */
  @NonNull
  default Collection<ICommand> getSubCommands() {
    // no sub-commands by default
    return CollectionUtil.emptyList();
  }

  /**
   * Get a sub-command by it's command name.
   *
   * @param name
   *          the requested sub-command name
   * @return the command or {@code null} if no sub-command exists with that name
   */
  @Nullable
  default ICommand getSubCommandByName(@NonNull String name) {
    // no sub-commands by default
    return null;
  }

  /**
   * Determine if this command requires the use of a sub-command.
   *
   * @return {@code true} if a sub-command is required or {@code false} otherwise
   */
  default boolean isSubCommandRequired() {
    // no sub-commands by default
    return false;
  }

  /**
   * Validate the options provided on the command line based on what is required
   * for this command.
   *
   * @param callingContext
   *          the context of the command execution
   * @param commandLine
   *          the parsed command line details
   * @throws InvalidArgumentException
   *           if a problem was found while validating the options
   */
  default void validateOptions(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine) throws InvalidArgumentException {
    // by default there are no options to handle
  }

  /**
   * Create a new executor for this command.
   *
   * @param callingContext
   *          the context of the command execution
   * @param commandLine
   *          the parsed command line details
   * @return the executor
   */
  @NonNull
  ICommandExecutor newExecutor(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine);

  /**
   * Validates that the provided extra arguments meet expectations.
   *
   * @param callingContext
   *          the context of the command execution
   * @param commandLine
   *          the parsed command line details
   * @throws InvalidArgumentException
   *           if a problem was found while validating the extra arguments
   */
  default void validateExtraArguments(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine commandLine)
      throws InvalidArgumentException {

    validateSubCommandRequirement();
    validateArgumentCount(commandLine);
    validateRequiredArguments(commandLine);
  }

  private void validateSubCommandRequirement() throws InvalidArgumentException {
    if (isSubCommandRequired()) {
      throw new InvalidArgumentException("Please choose a valid sub-command.");
    }
  }

  private void validateArgumentCount(@NonNull CommandLine commandLine) throws InvalidArgumentException {
    List<ExtraArgument> extraArguments = getExtraArguments();
    int maxArguments = extraArguments.size();
    List<String> actualArgs = commandLine.getArgList();

    if (actualArgs.size() > maxArguments) {
      throw new InvalidArgumentException(
          String.format("Too many extra arguments provided. Expected at most %d, but got %d.",
              maxArguments, actualArgs.size()));
    }

  }

  private void validateRequiredArguments(@NonNull CommandLine commandLine) throws InvalidArgumentException {
    List<String> actualArgs = commandLine.getArgList();
    List<ExtraArgument> requiredExtraArguments = getExtraArguments().stream()
        .filter(ExtraArgument::isRequired)
        .collect(Collectors.toUnmodifiableList());

    if (actualArgs.size() < requiredExtraArguments.size()) {
      throw new InvalidArgumentException(
          String.format("Missing required arguments: %s. Expected %d required arguments, but got %d.",
              requiredExtraArguments.stream()
                  .map(arg -> "<" + arg.getName() + ">")
                  .collect(Collectors.joining(" ")),
              requiredExtraArguments.size(),
              actualArgs.size()));
    }
  }
}
