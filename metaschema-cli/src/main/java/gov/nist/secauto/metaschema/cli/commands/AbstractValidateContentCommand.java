/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.cli.processor.OptionUtils;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractCommandExecutor;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.DefaultExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.util.LoggingValidationHandler;
import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.ValidationFeature;
import gov.nist.secauto.metaschema.core.model.validation.AggregateValidationResult;
import gov.nist.secauto.metaschema.core.model.validation.IValidationResult;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.IVersionInfo;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext.ISchemaValidationProvider;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingConstraintLoader;
import gov.nist.secauto.metaschema.modules.sarif.SarifValidationHandler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractValidateContentCommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(AbstractValidateContentCommand.class);
  @NonNull
  private static final String COMMAND = "validate";
  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
      new DefaultExtraArgument("file-or-URI-to-validate", true)));

  @NonNull
  private static final Option AS_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("as")
          .hasArg()
          .argName("FORMAT")
          .desc("source format: xml, json, or yaml")
          .numberOfArgs(1)
          .build());
  @NonNull
  private static final Option CONSTRAINTS_OPTION = ObjectUtils.notNull(
      Option.builder("c")
          .hasArgs()
          .argName("URL")
          .desc("additional constraint definitions")
          .build());
  @NonNull
  private static final Option SARIF_OUTPUT_FILE_OPTION = ObjectUtils.notNull(
      Option.builder("o")
          .hasArg()
          .argName("FILE")
          .desc("write SARIF results to the provided FILE")
          .numberOfArgs(1)
          .build());
  @NonNull
  private static final Option SARIF_INCLUDE_PASS_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("sarif-include-pass")
          .desc("include pass results in SARIF")
          .build());
  @NonNull
  private static final Option NO_SCHEMA_VALIDATION_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("disable-schema-validation")
          .desc("do not perform schema validation")
          .build());
  @NonNull
  private static final Option NO_CONSTRAINT_VALIDATION_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("disable-constraint-validation")
          .desc("do not perform constraint validation")
          .build());

  @Override
  public String getName() {
    return COMMAND;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends Option> gatherOptions() {
    return List.of(
        AS_OPTION,
        CONSTRAINTS_OPTION,
        SARIF_OUTPUT_FILE_OPTION,
        SARIF_INCLUDE_PASS_OPTION,
        NO_SCHEMA_VALIDATION_OPTION,
        NO_CONSTRAINT_VALIDATION_OPTION);
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return EXTRA_ARGUMENTS;
  }

  @SuppressWarnings("PMD.PreserveStackTrace") // intended
  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {
    List<String> extraArgs = cmdLine.getArgList();
    if (extraArgs.size() != 1) {
      throw new InvalidArgumentException("The source to validate must be provided.");
    }

    if (cmdLine.hasOption(AS_OPTION)) {
      try {
        String toFormatText = cmdLine.getOptionValue(AS_OPTION);
        Format.valueOf(toFormatText.toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException ex) {
        InvalidArgumentException newEx = new InvalidArgumentException(
            String.format("Invalid '%s' argument. The format must be one of: %s.",
                OptionUtils.toArgument(AS_OPTION),
                Arrays.asList(Format.values()).stream()
                    .map(Enum::name)
                    .collect(CustomCollectors.joiningWithOxfordComma("and"))));
        newEx.addSuppressed(ex);
        throw newEx;
      }
    }
  }

  protected abstract class AbstractValidationCommandExecutor
      extends AbstractCommandExecutor
      implements ISchemaValidationProvider {

    /**
     * Construct a new command executor.
     *
     * @param callingContext
     *          the context of the command execution
     * @param commandLine
     *          the parsed command line details
     */
    public AbstractValidationCommandExecutor(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine) {
      super(callingContext, commandLine);
    }

    /**
     * Get the binding context to use for data processing.
     *
     * @param constraintSets
     *          the constraints to configure in the resulting binding context
     * @return the context
     * @throws MetaschemaException
     *           if a Metaschema error occurred
     * @throws IOException
     *           if an error occurred while reading data
     */
    @NonNull
    protected abstract IBindingContext getBindingContext(@NonNull Set<IConstraintSet> constraintSets)
        throws MetaschemaException, IOException;

    @SuppressWarnings("PMD.OnlyOneReturn") // readability
    @Override
    public ExitStatus execute() {
      URI cwd = ObjectUtils.notNull(Paths.get("").toAbsolutePath().toUri());
      CommandLine cmdLine = getCommandLine();

      Set<IConstraintSet> constraintSets;
      if (cmdLine.hasOption(CONSTRAINTS_OPTION)) {
        IConstraintLoader constraintLoader = new BindingConstraintLoader(IBindingContext.instance());
        constraintSets = new LinkedHashSet<>();
        String[] args = cmdLine.getOptionValues(CONSTRAINTS_OPTION);
        for (String arg : args) {
          assert arg != null;
          try {
            URI constraintUri = ObjectUtils.requireNonNull(UriUtils.toUri(arg, cwd));
            constraintSets.addAll(constraintLoader.load(constraintUri));
          } catch (IOException | MetaschemaException | MetapathException | URISyntaxException ex) {
            return ExitCode.IO_ERROR.exitMessage("Unable to load constraint set '" + arg + "'.").withThrowable(ex);
          }
        }
      } else {
        constraintSets = CollectionUtil.emptySet();
      }

      IBindingContext bindingContext;
      try {
        bindingContext = getBindingContext(constraintSets);
      } catch (IOException | MetaschemaException ex) {
        return ExitCode.PROCESSING_ERROR
            .exitMessage("Unable to get binding context. " + ex.getMessage())
            .withThrowable(ex);
      }

      IBoundLoader loader = bindingContext.newBoundLoader();

      List<String> extraArgs = cmdLine.getArgList();

      String sourceName = ObjectUtils.requireNonNull(extraArgs.get(0));
      URI source;

      try {
        source = UriUtils.toUri(sourceName, cwd);
      } catch (URISyntaxException ex) {
        return ExitCode.IO_ERROR.exitMessage("Cannot load source '%s' as it is not a valid file or URI.")
            .withThrowable(ex);
      }

      Format asFormat;
      if (cmdLine.hasOption(AS_OPTION)) {
        try {
          String toFormatText = cmdLine.getOptionValue(AS_OPTION);
          asFormat = Format.valueOf(toFormatText.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
          return ExitCode.IO_ERROR
              .exitMessage("Invalid '--as' argument. The format must be one of: "
                  + Arrays.stream(Format.values())
                      .map(Enum::name)
                      .collect(CustomCollectors.joiningWithOxfordComma("or")))
              .withThrowable(ex);
        }
      } else {
        // attempt to determine the format
        try {
          asFormat = loader.detectFormat(source);
        } catch (FileNotFoundException ex) {
          // this case was already checked for
          return ExitCode.IO_ERROR.exitMessage("The provided source file '" + source + "' does not exist.");
        } catch (IOException ex) {
          return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
        } catch (IllegalArgumentException ex) {
          return ExitCode.IO_ERROR.exitMessage(
              "Source file has unrecognizable format. Use '--as' to specify the format. The format must be one of: "
                  + Arrays.stream(Format.values())
                      .map(Enum::name)
                      .collect(CustomCollectors.joiningWithOxfordComma("or")));
        }
      }

      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Validating '{}' as {}.", source, asFormat.name());
      }

      IMutableConfiguration<ValidationFeature<?>> configuration = new DefaultConfiguration<>();
      if (cmdLine.hasOption(SARIF_OUTPUT_FILE_OPTION) && cmdLine.hasOption(SARIF_INCLUDE_PASS_OPTION)) {
        configuration.enableFeature(ValidationFeature.VALIDATE_GENERATE_PASS_FINDINGS);
      }

      IValidationResult validationResult = null;
      try {
        if (!cmdLine.hasOption(NO_SCHEMA_VALIDATION_OPTION)) {
          // perform schema validation
          validationResult = this.validateWithSchema(source, asFormat);
        }

        if (!cmdLine.hasOption(NO_CONSTRAINT_VALIDATION_OPTION)) {
          // perform constraint validation
          IValidationResult constraintValidationResult = bindingContext.validateWithConstraints(source, configuration);
          validationResult = validationResult == null
              ? constraintValidationResult
              : AggregateValidationResult.aggregate(validationResult, constraintValidationResult);
        }
      } catch (FileNotFoundException ex) {
        return ExitCode.IO_ERROR.exitMessage(String.format("Resource not found at '%s'", source)).withThrowable(ex);
      } catch (UnknownHostException ex) {
        return ExitCode.IO_ERROR.exitMessage(String.format("Unknown host for '%s'.", source)).withThrowable(ex);
      } catch (IOException ex) {
        return ExitCode.IO_ERROR.exit().withThrowable(ex);
      } catch (MetapathException ex) {
        return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
      }

      if (cmdLine.hasOption(SARIF_OUTPUT_FILE_OPTION) && LOGGER.isInfoEnabled()) {
        Path sarifFile = ObjectUtils.notNull(Paths.get(cmdLine.getOptionValue(SARIF_OUTPUT_FILE_OPTION)));

        IVersionInfo version
            = getCallingContext().getCLIProcessor().getVersionInfos().get(CLIProcessor.COMMAND_VERSION);

        try {
          SarifValidationHandler sarifHandler = new SarifValidationHandler(source, version);
          if (validationResult != null) {
            sarifHandler.addFindings(validationResult.getFindings());
          }
          sarifHandler.write(sarifFile);
        } catch (IOException ex) {
          return ExitCode.IO_ERROR.exit().withThrowable(ex);
        }
      } else if (validationResult != null && !validationResult.getFindings().isEmpty()) {
        LOGGER.info("Validation identified the following issues:");
        LoggingValidationHandler.instance().handleValidationResults(validationResult);
      }

      if (validationResult == null || validationResult.isPassing()) {
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info("The file '{}' is valid.", source);
        }
      } else if (LOGGER.isErrorEnabled()) {
        LOGGER.error("The file '{}' is invalid.", source);
      }

      return (validationResult == null || validationResult.isPassing() ? ExitCode.OK : ExitCode.FAIL).exit();
    }
  }
}
