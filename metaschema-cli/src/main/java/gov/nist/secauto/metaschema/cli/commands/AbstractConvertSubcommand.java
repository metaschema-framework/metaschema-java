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
import gov.nist.secauto.metaschema.cli.processor.command.AbstractCommandExecutor;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.DefaultExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractConvertSubcommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(AbstractConvertSubcommand.class);

  @NonNull
  private static final String COMMAND = "convert";
  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
      new DefaultExtraArgument("source-file-or-URL", true),
      new DefaultExtraArgument("destination-file", false)));

  @NonNull
  private static final Option OVERWRITE_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("overwrite")
          .desc("overwrite the destination if it exists")
          .build());
  @NonNull
  private static final Option TO_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("to")
          .required()
          .hasArg().argName("FORMAT")
          .desc("convert to format: xml, json, or yaml")
          .build());

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public Collection<? extends Option> gatherOptions() {
    return ObjectUtils.notNull(List.of(
        OVERWRITE_OPTION,
        TO_OPTION));
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return EXTRA_ARGUMENTS;
  }

  @SuppressWarnings("PMD.PreserveStackTrace") // intended
  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {

    try {
      String toFormatText = cmdLine.getOptionValue(TO_OPTION);
      Format.valueOf(toFormatText.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      InvalidArgumentException newEx = new InvalidArgumentException(
          String.format("Invalid '%s' argument. The format must be one of: %s.",
              OptionUtils.toArgument(TO_OPTION),
              Format.names().stream()
                  .collect(CustomCollectors.joiningWithOxfordComma("and"))));
      newEx.setOption(TO_OPTION);
      newEx.addSuppressed(ex);
      throw newEx;
    }

    List<String> extraArgs = cmdLine.getArgList();
    if (extraArgs.isEmpty() || extraArgs.size() > 2) {
      throw new InvalidArgumentException("Illegal number of arguments.");
    }
  }

  protected abstract static class AbstractConversionCommandExecutor
      extends AbstractCommandExecutor {

    /**
     * Construct a new command executor.
     *
     * @param callingContext
     *          the context of the command execution
     * @param commandLine
     *          the parsed command line details
     */
    protected AbstractConversionCommandExecutor(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine) {
      super(callingContext, commandLine);
    }

    /**
     * Get the binding context to use for data processing.
     *
     * @return the context
     */
    @NonNull
    protected abstract IBindingContext getBindingContext();

    @SuppressWarnings({
        "PMD.OnlyOneReturn", // readability
        "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity" // reasonable
    })
    @Override
    public ExitStatus execute() {
      CommandLine cmdLine = getCommandLine();

      List<String> extraArgs = cmdLine.getArgList();

      Path destination = null;
      if (extraArgs.size() > 1) {
        destination = Paths.get(extraArgs.get(1)).toAbsolutePath();
      }

      if (destination != null) {
        if (Files.exists(destination)) {
          if (!cmdLine.hasOption(OVERWRITE_OPTION)) {
            return ExitCode.INVALID_ARGUMENTS.exitMessage(
                String.format("The provided destination '%s' already exists and the '%s' option was not provided.",
                    destination,
                    OptionUtils.toArgument(OVERWRITE_OPTION)));
          }
          if (!Files.isWritable(destination)) {
            return ExitCode.IO_ERROR.exitMessage(
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

      String sourceName = ObjectUtils.notNull(extraArgs.get(0));
      URI cwd = ObjectUtils.notNull(Paths.get("").toAbsolutePath().toUri());

      URI source;
      try {
        source = UriUtils.toUri(sourceName, cwd);
      } catch (URISyntaxException ex) {
        return ExitCode.IO_ERROR.exitMessage("Cannot load source '%s' as it is not a valid file or URI.")
            .withThrowable(ex);
      }
      assert source != null;

      String toFormatText = cmdLine.getOptionValue(TO_OPTION);
      Format toFormat = Format.valueOf(toFormatText.toUpperCase(Locale.ROOT));

      IBindingContext bindingContext = getBindingContext();
      try {
        IBoundLoader loader = bindingContext.newBoundLoader();
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info("Converting '{}'.", source);
        }

        if (destination == null) {
          // write to STDOUT
          OutputStreamWriter writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
          handleConversion(source, toFormat, writer, loader);
        } else {
          try (Writer writer = Files.newBufferedWriter(
              destination,
              StandardCharsets.UTF_8,
              StandardOpenOption.CREATE,
              StandardOpenOption.WRITE,
              StandardOpenOption.TRUNCATE_EXISTING)) {
            assert writer != null;
            handleConversion(source, toFormat, writer, loader);
          }
        }
      } catch (IOException | IllegalArgumentException ex) {
        return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex); // NOPMD readability
      }
      if (destination != null && LOGGER.isInfoEnabled()) {
        LOGGER.info("Generated {} file: {}", toFormat.toString(), destination);
      }
      return ExitCode.OK.exit();
    }

    /**
     * Called to perform a content conversion.
     *
     * @param source
     *          the resource to convert
     * @param toFormat
     *          the format to convert to
     * @param writer
     *          the writer to use to write converted content
     * @param loader
     *          the Metaschema loader to use to load the content to convert
     * @throws FileNotFoundException
     *           if the requested resource was not found
     * @throws IOException
     *           if there was an error reading or writing content
     */
    protected abstract void handleConversion(
        @NonNull URI source,
        @NonNull Format toFormat,
        @NonNull Writer writer,
        @NonNull IBoundLoader loader) throws FileNotFoundException, IOException;
  }
}
