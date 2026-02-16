/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.commands;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

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
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.metaschema.cli.processor.CallingContext;
import dev.metaschema.cli.processor.ExitCode;
import dev.metaschema.cli.processor.command.AbstractTerminalCommand;
import dev.metaschema.cli.processor.command.CommandExecutionException;
import dev.metaschema.cli.processor.command.ExtraArgument;
import dev.metaschema.cli.processor.command.ICommandExecutor;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.item.node.AllowedValueCollectingNodeItemVisitor;
import dev.metaschema.core.metapath.item.node.AllowedValueCollectingNodeItemVisitor.AllowedValuesRecord;
import dev.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import dev.metaschema.core.metapath.item.node.IModuleNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItemFactory;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.constraint.IAllowedValue;
import dev.metaschema.core.model.constraint.IAllowedValuesConstraint;
import dev.metaschema.core.model.constraint.IConstraintSet;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A CLI command that lists allowed-values constraints for a Metaschema module,
 * organized by the target node they apply to.
 * <p>
 * The output is produced in YAML format, showing each target location and the
 * allowed-values constraints that apply to it, including constraint
 * identifiers, allowed values, and source information.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ListAllowedValuesCommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(ListAllowedValuesCommand.class);

  @NonNull
  private static final String COMMAND = "list-allowed-values";
  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
      ExtraArgument.newInstance("metaschema-module-file-or-URL", true),
      ExtraArgument.newInstance("destination-file", false)));
  @NonNull
  private static final Option CONSTRAINTS_OPTION = ObjectUtils.notNull(
      Option.builder("c")
          .hasArgs()
          .argName("URL")
          .desc("additional constraint definitions")
          .get());

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "List allowed values constraints for the provided Metaschema module";
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends Option> gatherOptions() {
    return List.of(
        CONSTRAINTS_OPTION,
        MetaschemaCommands.OVERWRITE_OPTION);
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return EXTRA_ARGUMENTS;
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  /**
   * Execute the list allowed values command.
   *
   * @param callingContext
   *          information about the calling context
   * @param cmdLine
   *          the parsed command line details
   * @throws CommandExecutionException
   *           if an error occurred while executing the command
   */
  @SuppressWarnings({
      "PMD.OnlyOneReturn",
      "PMD.AvoidCatchingGenericException",
      "PMD.CognitiveComplexity"
  })
  protected void executeCommand(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) throws CommandExecutionException {

    List<String> extraArgs = cmdLine.getArgList();

    Path destination = null;
    if (extraArgs.size() > 1) {
      destination = MetaschemaCommands.handleDestination(ObjectUtils.requireNonNull(extraArgs.get(1)), cmdLine);
    }

    URI currentWorkingDirectory = ObjectUtils.notNull(getCurrentWorkingDirectory().toUri());
    Set<IConstraintSet> constraintSets = MetaschemaCommands.loadConstraintSets(
        cmdLine,
        CONSTRAINTS_OPTION,
        currentWorkingDirectory);

    IBindingContext bindingContext = MetaschemaCommands.newBindingContextWithDynamicCompilation(constraintSets);

    URI moduleUri;
    try {
      moduleUri = resolveAgainstCWD(ObjectUtils.requireNonNull(extraArgs.get(0)));
    } catch (URISyntaxException ex) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          String.format("Cannot load module as '%s' is not a valid file or URL. %s",
              extraArgs.get(0),
              ex.getLocalizedMessage()),
          ex);
    }
    IModule module = MetaschemaCommands.loadModule(moduleUri, bindingContext);

    try {
      if (destination == null) {
        Writer stringWriter = new StringWriter();
        try (PrintWriter writer = new PrintWriter(stringWriter)) {
          generateAllowedValuesList(module, writer);
        }

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
            generateAllowedValuesList(module, printWriter);
          }
        }
      }
    } catch (IOException ex) {
      throw new CommandExecutionException(ExitCode.IO_ERROR, ex);
    } catch (RuntimeException ex) {
      throw new CommandExecutionException(ExitCode.RUNTIME_ERROR, ex);
    }
  }

  private static void generateAllowedValuesList(
      @NonNull IModule module,
      @NonNull PrintWriter writer) throws IOException {
    AllowedValueCollectingNodeItemVisitor walker = new AllowedValueCollectingNodeItemVisitor();

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(module.getXmlNamespace())
        .build();

    IModuleNodeItem moduleNodeItem = INodeItemFactory.instance().newModuleNodeItem(module);

    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.disablePredicateEvaluation();

    walker.visit(moduleNodeItem, dynamicContext);

    Map<IDefinitionNodeItem<?, ?>,
        List<AllowedValuesRecord>> allowedValuesByTarget
            = ObjectUtils.notNull(walker.getAllowedValueLocations().stream()
                .flatMap(location -> location.getAllowedValues().stream())
                .collect(Collectors.groupingBy(AllowedValuesRecord::getTarget,
                    () -> new TreeMap<>(Comparator.comparing(IDefinitionNodeItem::getMetapath)),
                    Collectors.mapping(Function.identity(), Collectors.toUnmodifiableList()))));

    generateYaml(allowedValuesByTarget, writer);
  }

  private static void generateYaml(
      @NonNull Map<IDefinitionNodeItem<?, ?>, List<AllowedValuesRecord>> allowedValuesByTarget,
      @NonNull PrintWriter writer) throws IOException {

    YAMLFactoryBuilder builder = YAMLFactory.builder();
    YAMLFactory factory = ObjectUtils.notNull(builder
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .enable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
        .disable(YAMLGenerator.Feature.SPLIT_LINES)
        .build());

    try (YAMLGenerator generator = factory.createGenerator(writer)) {

      generator.writeStartObject();

      writeLocations(allowedValuesByTarget, generator);

      generator.writeEndObject();
    }
  }

  private static void writeLocations(
      @NonNull Map<IDefinitionNodeItem<?, ?>, List<AllowedValuesRecord>> allowedValuesByTarget,
      @NonNull YAMLGenerator generator) throws IOException {
    generator.writeFieldName("locations");
    generator.writeStartObject();

    for (Map.Entry<IDefinitionNodeItem<?, ?>, List<AllowedValuesRecord>> entry : allowedValuesByTarget.entrySet()) {
      assert entry != null;
      writeLocation(entry, generator);
    }
    generator.writeEndObject();
  }

  private static void writeLocation(
      @NonNull Map.Entry<IDefinitionNodeItem<?, ?>, List<AllowedValuesRecord>> entry,
      @NonNull YAMLGenerator generator) throws IOException {

    IDefinitionNodeItem<?, ?> target = ObjectUtils.notNull(entry.getKey());

    generator.writeFieldName(metapath(target));

    generator.writeStartObject();

    writeLocationConstraints(entry, generator);

    generator.writeEndObject();
  }

  private static void writeLocationConstraints(
      @NonNull Map.Entry<IDefinitionNodeItem<?, ?>, List<AllowedValuesRecord>> entry,
      @NonNull YAMLGenerator generator) throws IOException {
    IDefinitionNodeItem<?, ?> target = ObjectUtils.notNull(entry.getKey());

    List<AllowedValuesRecord> allowedValues = entry.getValue();
    if (allowedValues != null) {
      generator.writeFieldName("constraints");

      generator.writeStartArray();

      for (AllowedValuesRecord record : allowedValues) {
        assert target.equals(record.getTarget());

        writeAllowedValue(record, generator);
      }

      generator.writeEndArray();
    }
  }

  private static void writeAllowedValue(@NonNull AllowedValuesRecord record, @NonNull YAMLGenerator generator)
      throws IOException {

    generator.writeStartObject();

    generator.writeStringField("type", "allowed-values");

    IAllowedValuesConstraint constraint = record.getAllowedValues();
    if (constraint.getId() != null) {
      generator.writeStringField("identifier", constraint.getId());
    }
    generator.writeStringField("location", metapath(record.getLocation()));
    generator.writeStringField("target", constraint.getTarget().getPath());

    List<String> values = constraint.getAllowedValues().values().stream()
        .map(IAllowedValue::getValue)
        .collect(Collectors.toList());
    generator.writeFieldName("values");
    if (values == null) {
      generator.writeNull();
    } else {
      generator.writeStartArray();
      for (String value : values) {
        generator.writeString(value);
      }
      generator.writeEndArray();
    }

    generator.writeBooleanField("allow-other", constraint.isAllowedOther());

    URI source = constraint.getSource().getSource();
    generator.writeStringField("source", source == null ? "builtin" : source.toString());

    generator.writeEndObject();
  }

  private static String metapath(@NonNull IDefinitionNodeItem<?, ?> item) {
    return metapath(item.getMetapath());
  }

  private static String metapath(@NonNull String path) {
    // remove position 1 predicates
    return path.replace("[1]", "");
  }
}
