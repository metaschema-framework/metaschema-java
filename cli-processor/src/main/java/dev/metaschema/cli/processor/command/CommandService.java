/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor.command;

import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * A service that loads commands using SPI.
 * <p>
 * This class implements the singleton pattern to ensure a single instance of
 * the command service is used throughout the application.
 *
 * @see ServiceLoader for more information
 */
public final class CommandService {
  private static final Lazy<CommandService> INSTANCE = Lazy.of(CommandService::new);
  @NonNull
  private final Lazy<List<ICommand>> cachedCommands;

  /**
   * Get the singleton instance of the function service.
   *
   * @return the service instance
   */
  public static CommandService getInstance() {
    return INSTANCE.get();
  }

  /**
   * Construct a new service.
   * <p>
   * Initializes the ServiceLoader for ICommand implementations and caches the
   * loaded commands for thread-safe access.
   * <p>
   * This constructor is private to enforce the singleton pattern.
   */
  private CommandService() {
    this.cachedCommands = Lazy.of(() -> {
      ServiceLoader<ICommand> loader = ServiceLoader.load(ICommand.class);
      return ObjectUtils.notNull(loader.stream()
          .map(Provider<ICommand>::get)
          .collect(Collectors.toUnmodifiableList()));
    });
  }

  /**
   * Get the loaded commands.
   * <p>
   * Commands are loaded once on first access and cached for subsequent calls.
   * This ensures thread-safe access and consistent results across calls.
   *
   * @return the list of loaded commands
   */
  @NonNull
  public List<ICommand> getCommands() {
    return cachedCommands.get();
  }
}
