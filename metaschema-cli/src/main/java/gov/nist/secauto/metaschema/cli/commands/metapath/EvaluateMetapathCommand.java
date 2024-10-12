/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands.metapath;

import gov.nist.secauto.metaschema.cli.commands.MetaschemaCommands;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.DefaultItemWriter;
import gov.nist.secauto.metaschema.core.metapath.item.IItemWriter;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class EvaluateMetapathCommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(EvaluateMetapathCommand.class);

  @NonNull
  private static final String COMMAND = "eval";
  @NonNull
  private static final Option EXPRESSION_OPTION = ObjectUtils.notNull(
      Option.builder("e")
          .longOpt("expression")
          .required()
          .hasArg()
          .argName("EXPRESSION")
          .desc("Metapath expression to execute")
          .build());
  @NonNull
  public static final Option CONTENT_OPTION = ObjectUtils.notNull(
      Option.builder("i")
          .hasArg()
          .argName("FILE_OR_URL")
          .desc("Metaschema content instance resource")
          .build());

  @NonNull
  public static final Option METASCHEMA_OPTION = ObjectUtils.notNull(
      Option.builder("m")
          .hasArg()
          .argName("FILE_OR_URL")
          .desc("metaschema resource")
          .build());

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Execute a Metapath expression against a document";
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends Option> gatherOptions() {
    return List.of(
        METASCHEMA_OPTION,
        CONTENT_OPTION,
        EXPRESSION_OPTION);
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return CollectionUtil.emptyList();
  }

  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {
    List<String> extraArgs = cmdLine.getArgList();
    if (!extraArgs.isEmpty()) {
      throw new InvalidArgumentException("Illegal number of extra arguments.");
    }
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  @SuppressWarnings({
      "PMD.OnlyOneReturn", // readability
  })

  protected ExitStatus executeCommand(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) {
    URI cwd = ObjectUtils.notNull(Paths.get("").toAbsolutePath().toUri());

    IModule module = null;
    INodeItem item = null;
    if (cmdLine.hasOption(METASCHEMA_OPTION)) {
      try {
        String moduleName
            = ObjectUtils.requireNonNull(cmdLine.getOptionValue(METASCHEMA_OPTION));
        URI moduleUri = UriUtils.toUri(moduleName, cwd);
        module = MetaschemaCommands.handleModule(moduleUri, CollectionUtil.emptyList());
      } catch (URISyntaxException ex) {
        return ExitCode.INVALID_ARGUMENTS
            .exitMessage(
                String.format("Cannot load module as '%s' is not a valid file or URL.", ex.getInput()))
            .withThrowable(ex);
      } catch (IOException | MetaschemaException ex) {
        return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
      }

      // determine if the query is evaluated against the module or the instance
      if (cmdLine.hasOption(CONTENT_OPTION)) {
        // load the content
        IBindingContext bindingContext = new DefaultBindingContext();

        try {
          Path compilePath = Files.createTempDirectory("validation-");
          compilePath.toFile().deleteOnExit();

          bindingContext.registerModule(module, compilePath);
        } catch (IOException ex) {
          return ExitCode.PROCESSING_ERROR
              .exitMessage("Unable to get binding context. " + ex.getMessage())
              .withThrowable(ex);
        }

        IBoundLoader loader = bindingContext.newBoundLoader();

        String content = ObjectUtils.requireNonNull(cmdLine.getOptionValue(CONTENT_OPTION));
        URI contentResource;
        try {
          contentResource = MetaschemaCommands.handleResource(content, cwd);
        } catch (IOException ex) {
          return ExitCode.INVALID_ARGUMENTS
              .exitMessage("Unable to resolve content location. " + ex.getMessage())
              .withThrowable(ex);
        }

        try {
          item = loader.loadAsNodeItem(contentResource);
        } catch (IOException ex) {
          return ExitCode.INVALID_ARGUMENTS
              .exitMessage("Unable to resolve content location. " + ex.getMessage())
              .withThrowable(ex);
        }
      } else {
        item = INodeItemFactory.instance().newModuleNodeItem(module);
      }
    } else if (cmdLine.hasOption(CONTENT_OPTION)) {
      // content provided, but no module; require module
      return ExitCode.INVALID_ARGUMENTS.exitMessage(
          String.format("Must use '%s' to specify the Metaschema module.", CONTENT_OPTION.getArgName()));
    }

    StaticContext.Builder builder = StaticContext.builder();
    if (module != null) {
      builder.defaultModelNamespace(module.getXmlNamespace());
    }
    StaticContext staticContext = builder.build();

    String expression = cmdLine.getOptionValue(EXPRESSION_OPTION);
    if (expression == null) {
      return ExitCode.INVALID_ARGUMENTS.exitMessage(
          String.format("Must use '%s' to specify the Metapath expression.", EXPRESSION_OPTION.getArgName()));
    }

    try {
      // Parse and compile the Metapath expression
      MetapathExpression compiledMetapath = MetapathExpression.compile(expression, staticContext);
      ISequence<?> sequence = compiledMetapath.evaluate(item, new DynamicContext(staticContext));

      try (Writer stringWriter = new StringWriter()) {
        try (PrintWriter writer = new PrintWriter(stringWriter)) {
          try (IItemWriter itemWriter = new DefaultItemWriter(writer)) {
            itemWriter.writeSequence(sequence);
          }
        }

        // Print the result
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info(stringWriter.toString());
        }
      }
      return ExitCode.OK.exit();
    } catch (Exception ex) {
      return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
    }
  }
}
