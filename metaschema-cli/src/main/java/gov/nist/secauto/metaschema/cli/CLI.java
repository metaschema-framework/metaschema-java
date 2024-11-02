/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli;

import gov.nist.secauto.metaschema.cli.commands.MetaschemaCommands;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.command.CommandService;
import gov.nist.secauto.metaschema.core.MetaschemaConstants;
import gov.nist.secauto.metaschema.core.MetaschemaJavaVersion;
import gov.nist.secauto.metaschema.core.model.MetaschemaVersion;
import gov.nist.secauto.metaschema.core.util.IVersionInfo;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

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
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

    @SuppressWarnings("PMD.UseConcurrentHashMap") Map<String, IVersionInfo> versions = new LinkedHashMap<>();
    versions.put(CLIProcessor.COMMAND_VERSION, new MetaschemaJavaVersion());
    versions.put(MetaschemaConstants.METASCHEMA_NAMESPACE, new MetaschemaVersion());

    CLIProcessor processor = new CLIProcessor("metaschema-cli", versions);
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
