/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.commands.metapath.MetapathCommand;
import gov.nist.secauto.metaschema.cli.processor.command.ICommand;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
  public static final Option METASCHEMA_OPTION = ObjectUtils.notNull(
      Option.builder("m")
          .hasArg()
          .argName("FILE_OR_URL")
          .required()
          .desc("metaschema resource")
          .build());
  @NonNull
  public static final Option OVERWRITE_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("overwrite")
          .desc("overwrite the destination if it exists")
          .build());

  @NonNull
  public static IModule handleModule(
      @NonNull CommandLine commandLine,
      @NonNull URI cwd,
      @NonNull IBindingContext bindingContext) throws URISyntaxException, IOException, MetaschemaException {
    String moduleName = ObjectUtils.requireNonNull(commandLine.getOptionValue(METASCHEMA_OPTION));
    URI moduleUri = UriUtils.toUri(moduleName, cwd);
    return handleModule(moduleUri, bindingContext);
  }

  @NonNull
  public static IModule handleModule(
      @NonNull URI moduleResource,
      @NonNull IBindingContext bindingContext) throws IOException, MetaschemaException {
    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();
    return loader.load(moduleResource);
  }

  @NonNull
  public static URI handleResource(
      @NonNull String location,
      @NonNull URI cwd) throws IOException {
    try {
      return UriUtils.toUri(location, cwd);
    } catch (URISyntaxException ex) {
      IOException newEx = new IOException( // NOPMD - intentional
          String.format("Cannot load module as '%s' is not a valid file or URL.", location));
      newEx.addSuppressed(ex);
      throw newEx;
    }
  }

  @NonNull
  public static Path newTempDir() throws IOException {
    Path retval = Files.createTempDirectory("metaschema-cli-");
    retval.toFile().deleteOnExit();
    return retval;
  }

  @NonNull
  public static IBindingContext newBindingContextWithDynamicCompilation() throws IOException {
    return newBindingContextWithDynamicCompilation(CollectionUtil.emptySet());
  }

  @NonNull
  public static IBindingContext newBindingContextWithDynamicCompilation(@NonNull Set<IConstraintSet> constraintSets)
      throws IOException {
    return IBindingContext.builder()
        .compilePath(newTempDir())
        .constraintSet(constraintSets)
        .build();
  }

  private MetaschemaCommands() {
    // disable construction
  }
}
