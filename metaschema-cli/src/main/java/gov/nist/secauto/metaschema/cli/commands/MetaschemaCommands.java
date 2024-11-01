/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.commands.metapath.MetapathCommand;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.cli.processor.OptionUtils;
import gov.nist.secauto.metaschema.cli.processor.command.CommandExecutionException;
import gov.nist.secauto.metaschema.cli.processor.command.ICommand;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator.SchemaFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class MetaschemaCommands {
  @NonNull
  public static final List<ICommand> COMMANDS = ObjectUtils.notNull(List.of(
      new ValidateModuleCommand(),
      new GenerateSchemaCommand(),
      new GenerateDiagramCommand(),
      new ValidateContentUsingModuleCommand(),
      new ConvertContentUsingModuleCommand(),
      new MetapathCommand()));

  @NonNull
  public static final Option METASCHEMA_REQUIRED_OPTION = ObjectUtils.notNull(
      Option.builder("m")
          .hasArg()
          .argName("FILE_OR_URL")
          .required()
          .desc("metaschema resource")
          .numberOfArgs(1)
          .build());
  @NonNull
  public static final Option METASCHEMA_OPTIONAL_OPTION = ObjectUtils.notNull(
      Option.builder("m")
          .hasArg()
          .argName("FILE_OR_URL")
          .desc("metaschema resource")
          .numberOfArgs(1)
          .build());
  @NonNull
  public static final Option OVERWRITE_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("overwrite")
          .desc("overwrite the destination if it exists")
          .build());
  @NonNull
  public static final Option TO_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("to")
          .required()
          .hasArg().argName("FORMAT")
          .desc("convert to format: " + Arrays.stream(Format.values())
              .map(Enum::name)
              .collect(CustomCollectors.joiningWithOxfordComma("or")))
          .numberOfArgs(1)
          .build());
  @NonNull
  public static final Option AS_FORMAT_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("as")
          .hasArg()
          .argName("FORMAT")
          .desc("source format: " + Arrays.stream(Format.values())
              .map(Enum::name)
              .collect(CustomCollectors.joiningWithOxfordComma("or")))
          .numberOfArgs(1)
          .build());
  @NonNull
  public static final Option AS_SCHEMA_FORMAT_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("as")
          .required()
          .hasArg()
          .argName("FORMAT")
          .desc("schema format: " + Arrays.stream(SchemaFormat.values())
              .map(Enum::name)
              .collect(CustomCollectors.joiningWithOxfordComma("or")))
          .numberOfArgs(1)
          .build());

  /**
   * Get the provided source path or URI string as an absolute {@link URI} for the
   * resource.
   *
   * @param pathOrUri
   *          the resource
   * @param currentWorkingDirectory
   *          the current working directory the URI will be resolved against to
   *          ensure it is absolute
   * @return the absolute URI for the resource
   * @throws CommandExecutionException
   *           if the resulting URI is not a well-formed URI
   */
  @NonNull
  public static URI handleSource(
      @NonNull String pathOrUri,
      @NonNull URI currentWorkingDirectory) throws CommandExecutionException {
    try {
      return getResourceUri(pathOrUri, currentWorkingDirectory);
    } catch (URISyntaxException ex) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          String.format(
              "Cannot load source '%s' as it is not a valid file or URI.",
              pathOrUri),
          ex);
    }
  }

  /**
   * Get the provided destination path as an absolute {@link Path} for the
   * resource.
   * <p>
   * This method checks if the path exists and if so, if the overwrite option is
   * set. The method also ensures that the parent directory is created, if it
   * doesn't already exist.
   *
   * @param path
   *          the resource
   * @param commandLine
   *          the provided command line argument information
   * @return the absolute URI for the resource
   * @throws CommandExecutionException
   *           if the path exists and cannot be overwritten or is not writable
   */
  public static Path handleDestination(
      @NonNull String path,
      @NonNull CommandLine commandLine) throws CommandExecutionException {
    Path retval = Paths.get(path).toAbsolutePath();

    if (Files.exists(retval)) {
      if (!commandLine.hasOption(OVERWRITE_OPTION)) {
        throw new CommandExecutionException(
            ExitCode.INVALID_ARGUMENTS,
            String.format("The provided destination '%s' already exists and the '%s' option was not provided.",
                retval,
                OptionUtils.toArgument(OVERWRITE_OPTION)));
      }
      if (!Files.isWritable(retval)) {
        throw new CommandExecutionException(
            ExitCode.IO_ERROR,
            String.format(
                "The provided destination '%s' is not writable.", retval));
      }
    } else {
      Path parent = retval.getParent();
      if (parent != null) {
        try {
          Files.createDirectories(parent);
        } catch (IOException ex) {
          throw new CommandExecutionException(
              ExitCode.INVALID_TARGET,
              ex);
        }
      }
    }
    return retval;
  }

  /**
   * Parse the command line options to get the selected format.
   *
   * @param commandLine
   *          the provided command line argument information
   * @param option
   *          the option specifying the format, which must be present on the
   *          command line
   * @return the format
   * @throws CommandExecutionException
   *           if the format option was not provided or was an invalid choice
   */
  @SuppressWarnings("PMD.PreserveStackTrace")
  @NonNull
  public static Format getFormat(
      @NonNull CommandLine cmdLine,
      @NonNull Option option) throws CommandExecutionException {
    // use the option
    String toFormatText = cmdLine.getOptionValue(option);
    if (toFormatText == null) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          String.format("The '%s' argument was not provided.",
              option.hasLongOpt()
                  ? "--" + option.getLongOpt()
                  : "-" + option.getOpt()));
    }
    try {
      return Format.valueOf(toFormatText.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          String.format("Invalid '%s' argument. The format must be one of: %s.",
              option.hasLongOpt()
                  ? "--" + option.getLongOpt()
                  : "-" + option.getOpt(),
              Arrays.stream(Format.values())
                  .map(Enum::name)
                  .collect(CustomCollectors.joiningWithOxfordComma("or"))));
    }
  }

  /**
   *
   * @param cmdLine
   * @param option
   * @param loader
   * @param resource
   * @return
   * @throws InvalidArgumentException
   *           if the option is not a supported format
   * @throws FileNotFoundException
   *           if the URI is a file that was not found
   * @throws IOException
   *           if there was an error reading the file to determine the format of
   *           the file
   * @throws IllegalArgumentException
   *           if the format of the source file was not recognized
   * @since 2.0.0
   */
  @SuppressWarnings({ "PMD.PreserveStackTrace", "PMD.OnlyOneReturn" })
  @NonNull
  public static Format determineSourceFormat(
      @NonNull CommandLine cmdLine,
      @NonNull Option option,
      @NonNull IBoundLoader loader,
      @NonNull URI resource) throws CommandExecutionException {
    if (cmdLine.hasOption(option)) {
      // use the option
      return getFormat(cmdLine, option);
    }

    // attempt to determine the format
    try {
      return loader.detectFormat(resource);
    } catch (FileNotFoundException ex) {
      // this case was already checked for
      throw new CommandExecutionException(
          ExitCode.IO_ERROR,
          String.format("The provided source '%s' does not exist.", resource),
          ex);
    } catch (IOException ex) {
      throw new CommandExecutionException(
          ExitCode.IO_ERROR,
          String.format("Unable to determine source format. Use '%s' to specify the format. %s",
              option.hasLongOpt()
                  ? "--" + option.getLongOpt()
                  : "-" + option.getOpt(),
              ex.getLocalizedMessage()),
          ex);
    }
  }

  @NonNull
  public static IModule handleModule(
      @NonNull CommandLine commandLine,
      @NonNull Option option,
      @NonNull URI cwd,
      @NonNull IBindingContext bindingContext) throws CommandExecutionException {
    String moduleName = commandLine.getOptionValue(option);
    if (moduleName == null) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          String.format("Unable to determine the module to load. Use '%s' to specify the module.",
              option.hasLongOpt()
                  ? "--" + option.getLongOpt()
                  : "-" + option.getOpt()));
    }

    URI moduleUri;
    try {
      moduleUri = UriUtils.toUri(moduleName, cwd);
    } catch (URISyntaxException ex) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          String.format("Cannot load module as '%s' is not a valid file or URL. %s",
              ex.getInput(),
              ex.getLocalizedMessage()),
          ex);
    }
    return handleModule(moduleUri, bindingContext);
  }

  @NonNull
  public static IModule handleModule(
      @NonNull String moduleResource,
      @NonNull URI currentWorkingDirectory,
      @NonNull IBindingContext bindingContext) throws CommandExecutionException {
    try {
      URI moduleUri = getResourceUri(
          moduleResource,
          currentWorkingDirectory);
      return handleModule(moduleUri, bindingContext);
    } catch (URISyntaxException ex) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          String.format("Cannot load module as '%s' is not a valid file or URL. %s",
              ex.getInput(),
              ex.getLocalizedMessage()),
          ex);
    }
  }

  @NonNull
  public static IModule handleModule(
      @NonNull URI moduleResource,
      @NonNull IBindingContext bindingContext) throws CommandExecutionException {
    try {
      IBindingModuleLoader loader = bindingContext.newModuleLoader();
      loader.allowEntityResolution();
      return loader.load(moduleResource);
    } catch (IOException | MetaschemaException ex) {
      throw new CommandExecutionException(ExitCode.PROCESSING_ERROR, ex);
    }
  }

  /**
   * For a given resource location, resolve the location into an absolute URI.
   *
   * @param location
   *          the resource location
   * @param cwd
   *          the URI of the current working directory
   * @return the resolved URI
   * @throws URISyntaxException
   *           if the location is not a valid URI
   */
  @NonNull
  public static URI getResourceUri(
      @NonNull String location,
      @NonNull URI cwd) throws URISyntaxException {
    return UriUtils.toUri(location, cwd);
  }

  @NonNull
  public static Set<IConstraintSet> loadConstraintSets(
      @NonNull CommandLine cmdLine,
      @NonNull Option option,
      @NonNull URI cwd) throws CommandExecutionException {
    Set<IConstraintSet> constraintSets;
    if (cmdLine.hasOption(option)) {
      IConstraintLoader constraintLoader = IBindingContext.getConstraintLoader();
      constraintSets = new LinkedHashSet<>();
      String[] args = cmdLine.getOptionValues(option);
      for (String arg : args) {
        assert arg != null;
        try {
          URI constraintUri = ObjectUtils.requireNonNull(UriUtils.toUri(arg, cwd));
          constraintSets.addAll(constraintLoader.load(constraintUri));
        } catch (URISyntaxException | IOException | MetaschemaException | MetapathException ex) {
          throw new CommandExecutionException(
              ExitCode.IO_ERROR,
              String.format("Unable to process constraint set '%s'. %s",
                  arg,
                  ex.getLocalizedMessage()),
              ex);
        }
      }
    } else {
      constraintSets = CollectionUtil.emptySet();
    }
    return constraintSets;
  }

  @NonNull
  public static Path newTempDir() throws IOException {
    Path retval = Files.createTempDirectory("metaschema-cli-");
    retval.toFile().deleteOnExit();
    return retval;
  }

  @NonNull
  public static IBindingContext newBindingContextWithDynamicCompilation() throws CommandExecutionException {
    return newBindingContextWithDynamicCompilation(CollectionUtil.emptySet());
  }

  @NonNull
  public static IBindingContext newBindingContextWithDynamicCompilation(@NonNull Set<IConstraintSet> constraintSets)
      throws CommandExecutionException {
    try {
      Path tempDir = newTempDir();
      tempDir.toFile().deleteOnExit();
      return IBindingContext.builder()
          .compilePath(tempDir)
          .constraintSet(constraintSets)
          .build();
    } catch (IOException ex) {
      throw new CommandExecutionException(ExitCode.RUNTIME_ERROR,
          String.format("Unable to initialize the binding context. %s", ex.getLocalizedMessage()),
          ex);
    }
  }

  @SuppressWarnings("PMD.PreserveStackTrace")
  @NonNull
  public static SchemaFormat getSchemaFormat(
      @NonNull CommandLine cmdLine,
      @NonNull Option option) throws InvalidArgumentException {
    // use the option
    String toFormatText = cmdLine.getOptionValue(option);
    if (toFormatText == null) {
      throw new IllegalArgumentException(
          String.format("Option '%s' not provided.",
              option.hasLongOpt()
                  ? "--" + option.getLongOpt()
                  : "-" + option.getOpt()));
    }
    try {
      return SchemaFormat.valueOf(toFormatText.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      throw new InvalidArgumentException(
          String.format("Invalid '%s' argument. The schema format must be one of: %s.",
              option.hasLongOpt()
                  ? "--" + option.getLongOpt()
                  : "-" + option.getOpt(),
              Arrays.stream(SchemaFormat.values())
                  .map(Enum::name)
                  .collect(CustomCollectors.joiningWithOxfordComma("or"))));
    }
  }

  private MetaschemaCommands() {
    // disable construction
  }
}
