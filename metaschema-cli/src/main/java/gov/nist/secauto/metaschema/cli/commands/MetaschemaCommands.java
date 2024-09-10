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
import gov.nist.secauto.metaschema.core.model.xml.ExternalConstraintsModulePostProcessor;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class MetaschemaCommands {
  @NonNull
  public static final List<ICommand> COMMANDS = ObjectUtils.notNull(List.of(
      new ValidateModuleCommand(),
      new GenerateSchemaCommand(),
      new GenerateDiagramCommand(),
      new ValidateContentUsingModuleCommand(),
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
      @NonNull Collection<IConstraintSet> constraintSets) throws URISyntaxException, IOException, MetaschemaException {
    String moduleName
        = ObjectUtils.requireNonNull(commandLine.getOptionValue(MetaschemaCommands.METASCHEMA_OPTION));
    URI moduleUri = UriUtils.toUri(moduleName, cwd);
    return handleModule(moduleUri, constraintSets);
  }

  @NonNull
  public static IModule handleModule(
      @NonNull URI moduleResource,
      @NonNull Collection<IConstraintSet> constraintSets) throws IOException, MetaschemaException {
    ExternalConstraintsModulePostProcessor postProcessor
        = new ExternalConstraintsModulePostProcessor(constraintSets);

    ModuleLoader loader = new ModuleLoader(CollectionUtil.singletonList(postProcessor));

    // BindingModuleLoader loader
    // = new BindingModuleLoader(new DefaultBindingContext(),
    // CollectionUtil.singletonList(postProcessor));

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

  private MetaschemaCommands() {
    // disable construction
  }
}
