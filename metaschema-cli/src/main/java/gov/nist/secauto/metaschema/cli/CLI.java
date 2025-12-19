/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli;

import gov.nist.secauto.metaschema.cli.commands.MetaschemaCommands;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.command.CommandService;
import gov.nist.secauto.metaschema.cli.processor.completion.CompletionTypeRegistry;
import gov.nist.secauto.metaschema.core.MetaschemaConstants;
import gov.nist.secauto.metaschema.core.MetaschemaJavaVersion;
import gov.nist.secauto.metaschema.core.model.MetaschemaVersion;
import gov.nist.secauto.metaschema.core.util.IVersionInfo;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator.SchemaFormat;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * The main entry point for the CLI application.
 */
@SuppressWarnings("PMD.ShortClassName")
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
