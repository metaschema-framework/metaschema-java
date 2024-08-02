/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public final class CommandService {
  private static final Lazy<CommandService> INSTANCE = Lazy.lazy(() -> new CommandService());
  @NonNull
  private final ServiceLoader<ICommand> loader;

  /**
   * Get the singleton instance of the function service.
   *
   * @return the service instance
   */
  public static CommandService getInstance() {
    return INSTANCE.get();
  }

  public CommandService() {
    ServiceLoader<ICommand> loader = ServiceLoader.load(ICommand.class);
    assert loader != null;
    this.loader = loader;
  }

  /**
   * Get the function service loader instance.
   *
   * @return the service loader instance.
   */
  @NonNull
  private ServiceLoader<ICommand> getLoader() {
    return loader;
  }

  @SuppressWarnings("null")
  @NonNull
  public List<ICommand> getCommands() {
    return getLoader().stream()
        .map(Provider<ICommand>::get)
        .collect(Collectors.toUnmodifiableList());
  }
}
