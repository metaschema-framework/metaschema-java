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
import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator.SchemaFormat;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

public class GenerateSchemaCommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(GenerateSchemaCommand.class);

  @NonNull
  private static final String COMMAND = "generate-schema";
  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS;

  @NonNull
  private static final Option AS_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("as")
          .required()
          .hasArg()
          .argName("FORMAT")
          .desc("source format: xml, json, or yaml")
          .build());
  @NonNull
  private static final Option INLINE_TYPES_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("inline-types")
          .desc("definitions declared inline will be generated as inline types")
          .build());

  static {
    EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
        new DefaultExtraArgument("metaschema-module-file-or-URL", true),
        new DefaultExtraArgument("destination-schema-file", false)));
  }

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Generate a schema for the specified Module module";
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends Option> gatherOptions() {
    return List.of(
        MetaschemaCommands.OVERWRITE_OPTION,
        AS_OPTION,
        INLINE_TYPES_OPTION);
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return EXTRA_ARGUMENTS;
  }

  @SuppressWarnings("PMD.PreserveStackTrace") // intended
  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {
    try {
      String asFormatText = cmdLine.getOptionValue(AS_OPTION);
      if (asFormatText != null) {
        SchemaFormat.valueOf(asFormatText.toUpperCase(Locale.ROOT));
      }
    } catch (IllegalArgumentException ex) {
      InvalidArgumentException newEx = new InvalidArgumentException( // NOPMD - intentional
          String.format("Invalid '%s' argument. The format must be one of: %s.",
              OptionUtils.toArgument(AS_OPTION),
              Arrays.asList(Format.values()).stream()
                  .map(format -> format.name())
                  .collect(CustomCollectors.joiningWithOxfordComma("and"))));
      newEx.setOption(AS_OPTION);
      newEx.addSuppressed(ex);
      throw newEx;
    }

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
   * Called to execute the schema generation.
   *
   * @param callingContext
   *          the context information for the execution
   * @param cmdLine
   *          the parsed command line details
   * @return the execution result
   */
  @SuppressWarnings({
      "PMD.OnlyOneReturn", // readability
      "unused"
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

    String asFormatText = cmdLine.getOptionValue(AS_OPTION);
    SchemaFormat asFormat = SchemaFormat.valueOf(asFormatText.toUpperCase(Locale.ROOT));

    IMutableConfiguration<SchemaGenerationFeature<?>> configuration = new DefaultConfiguration<>();
    if (cmdLine.hasOption(INLINE_TYPES_OPTION)) {
      configuration.enableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
      if (SchemaFormat.JSON.equals(asFormat)) {
        configuration.disableFeature(SchemaGenerationFeature.INLINE_CHOICE_DEFINITIONS);
      } else {
        configuration.enableFeature(SchemaGenerationFeature.INLINE_CHOICE_DEFINITIONS);
      }
    }

    String inputName = ObjectUtils.notNull(extraArgs.get(0));
    URI cwd = ObjectUtils.notNull(Paths.get("").toAbsolutePath().toUri());

    URI input;
    try {
      input = UriUtils.toUri(inputName, cwd);
    } catch (URISyntaxException ex) {
      return ExitCode.IO_ERROR.exitMessage(
          String.format("Unable to load '%s' as it is not a valid file or URI.", inputName)).withThrowable(ex);
    }
    assert input != null;
    try {
      ModuleLoader loader = new ModuleLoader();
      loader.allowEntityResolution();
      IModule module = loader.load(input);

      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Generating {} schema for '{}'.", asFormat.name(), input);
      }
      if (destination == null) {
        @SuppressWarnings({ "resource", "PMD.CloseResource" }) // not owned
        OutputStream os = ObjectUtils.notNull(System.out);
        ISchemaGenerator.generateSchema(module, os, asFormat, configuration);
      } else {
        ISchemaGenerator.generateSchema(module, destination, asFormat, configuration);
      }
    } catch (IOException | MetaschemaException ex) {
      return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex); // NOPMD readability
    }
    if (destination != null && LOGGER.isInfoEnabled()) {
      LOGGER.info("Generated {} schema file: {}", asFormat.toString(), destination);
    }
    return ExitCode.OK.exit();
  }
}
