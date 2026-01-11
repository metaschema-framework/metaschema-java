/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.metaschema.cli.commands.MetaschemaCommands;
import dev.metaschema.cli.processor.CLIProcessor;
import dev.metaschema.cli.processor.ExitStatus;
import dev.metaschema.cli.processor.command.CommandService;
import dev.metaschema.cli.processor.completion.CompletionTypeRegistry;
import dev.metaschema.core.MetaschemaConstants;
import dev.metaschema.core.MetaschemaJavaVersion;
import dev.metaschema.core.metapath.format.PathFormatSelection;
import dev.metaschema.core.model.MetaschemaVersion;
import dev.metaschema.core.util.IVersionInfo;
import dev.metaschema.databind.io.Format;
import dev.metaschema.schemagen.ISchemaGenerator.SchemaFormat;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * The main entry point for the CLI application.
 */
public final class CLI {
  /**
   * The main command line entry point.
   *
   * @param args
   *          the command line arguments
   */
  public static void main(String[] args) {
    System.exit(runCli(args).getExitCode().getStatusCode());
  }

  /**
   * Execute a command line.
   *
   * @param args
   *          the command line arguments
   * @return the execution result
   */
  @NonNull
  public static ExitStatus runCli(String... args) {
    return runCli(null, args);
  }

  /**
   * Execute a command line with a custom output stream.
   * <p>
   * This method is useful for testing, allowing output to be captured instead of
   * being written directly to the console.
   *
   * @param outputStream
   *          the output stream to write to, or {@code null} to use the default
   *          console
   * @param args
   *          the command line arguments
   * @return the execution result
   */
  @NonNull
  public static ExitStatus runCli(@Nullable PrintStream outputStream, String... args) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

    // Register completion types for shell completion script generation
    CompletionTypeRegistry.registerEnum(Format.class);
    CompletionTypeRegistry.registerEnum(PathFormatSelection.class);
    CompletionTypeRegistry.registerEnum(SchemaFormat.class);

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    Map<String, IVersionInfo> versions = new LinkedHashMap<>();
    versions.put(CLIProcessor.COMMAND_VERSION, new MetaschemaJavaVersion());
    versions.put(MetaschemaConstants.METASCHEMA_NAMESPACE, new MetaschemaVersion());

    CLIProcessor processor = new CLIProcessor("metaschema-cli", versions, outputStream);
    MetaschemaCommands.COMMANDS.forEach(processor::addCommandHandler);

    CommandService.getInstance().getCommands().stream().forEach(command -> {
      assert command != null;
      processor.addCommandHandler(command);
    });
    return processor.process(args);
  }

  private CLI() {
    // disable construction
  }
}
