/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.cli.processor.OptionUtils;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.DefaultExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.MermaidErDiagramGenerator;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class GenerateDiagramCommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(GenerateDiagramCommand.class);

  @NonNull
  private static final String COMMAND = "generate-diagram";
  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS;

  static {
    EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
        new DefaultExtraArgument("metaschema-module-file-or-URL", true),
        new DefaultExtraArgument("destination-diagram-file", false)));
  }

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Generate a diagram for the provided Metaschema module";
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends Option> gatherOptions() {
    return List.of(
        MetaschemaCommands.OVERWRITE_OPTION);
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return EXTRA_ARGUMENTS;
  }

  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {
    List<String> extraArgs = cmdLine.getArgList();
    if (extraArgs.isEmpty() || extraArgs.size() > 2) {
      throw new InvalidArgumentException("Illegal number of arguments.");
    }
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  /**
   * Execute the diagram generation command.
   *
   * @param callingContext
   *          information about the calling context
   * @param cmdLine
   *          the parsed command line details
   * @return the execution result
   */
  @SuppressWarnings({
      "PMD.OnlyOneReturn", // readability
  })
  protected ExitStatus executeCommand(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) {

    List<String> extraArgs = cmdLine.getArgList();

    Path destination = null;
    if (extraArgs.size() > 1) {
      destination = Paths.get(extraArgs.get(1)).toAbsolutePath();
    }

    if (destination != null) {
      if (Files.exists(destination)) {
        if (!cmdLine.hasOption(MetaschemaCommands.OVERWRITE_OPTION)) {
          return ExitCode.INVALID_ARGUMENTS.exitMessage( // NOPMD readability
              String.format("The provided destination '%s' already exists and the '%s' option was not provided.",
                  destination,
                  OptionUtils.toArgument(MetaschemaCommands.OVERWRITE_OPTION)));
        }
        if (!Files.isWritable(destination)) {
          return ExitCode.IO_ERROR.exitMessage( // NOPMD readability
              "The provided destination '" + destination + "' is not writable.");
        }
      } else {
        Path parent = destination.getParent();
        if (parent != null) {
          try {
            Files.createDirectories(parent);
          } catch (IOException ex) {
            return ExitCode.INVALID_TARGET.exit().withThrowable(ex); // NOPMD readability
          }
        }
      }
    }

    URI cwd = ObjectUtils.notNull(Paths.get("").toAbsolutePath().toUri());

    IModule module;
    try {
      URI moduleUri = UriUtils.toUri(ObjectUtils.requireNonNull(extraArgs.get(0)), cwd);
      module = MetaschemaCommands.handleModule(moduleUri, CollectionUtil.emptyList());
    } catch (URISyntaxException ex) {
      return ExitCode.INVALID_ARGUMENTS
          .exitMessage(
              String.format("Cannot load module as '%s' is not a valid file or URL.", ex.getInput()))
          .withThrowable(ex);
    } catch (IOException | MetaschemaException ex) {
      return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
    }

    try {
      if (destination == null) {
        Writer stringWriter = new StringWriter();
        try (PrintWriter writer = new PrintWriter(stringWriter)) {
          MermaidErDiagramGenerator.generate(module, writer);
        }

        // Print the result
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info(stringWriter.toString());
        }
      } else {
        try (Writer writer = Files.newBufferedWriter(
            destination,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING)) {
          try (PrintWriter printWriter = new PrintWriter(writer)) {
            MermaidErDiagramGenerator.generate(module, printWriter);
          }
        }
      }

      return ExitCode.OK.exit();
    } catch (Exception ex) {
      return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
    }
  }
}
