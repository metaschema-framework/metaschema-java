package gov.nist.secauto.metaschema.cli.commands.metapath;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.DefaultExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class TestPathSubCommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(TestPathSubCommand.class);

  @NonNull
  private static final String COMMAND = "test-path";
  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS;
  @NonNull
  private static final Option EXPRESSION_OPTION = ObjectUtils.notNull(
      Option.builder("e")
          .longOpt("expression")
          .required()
          .hasArg()
          .argName("EXPRESSION")
          .desc("The MetaPath expression to execute")
          .build());

  static {
    EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
        new DefaultExtraArgument("input-file", true)));
  }

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Execute a MetaPath expression against a document";
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends Option> gatherOptions() {
    return List.of(EXPRESSION_OPTION);
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return EXTRA_ARGUMENTS;
  }

  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {
    List<String> extraArgs = cmdLine.getArgList();
    if (extraArgs.isEmpty() || extraArgs.size() > 1) {
      throw new InvalidArgumentException("Illegal number of arguments.");
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
    List<String> extraArgs = cmdLine.getArgList();
    String inputFile = extraArgs.get(0);
    String expression = cmdLine.getOptionValue(EXPRESSION_OPTION);

    Path inputPath = Paths.get(inputFile).toAbsolutePath();

    if (!Files.exists(inputPath)) {
      return ExitCode.INVALID_ARGUMENTS.exitMessage(
          String.format("The provided input file '%s' does not exist.", inputPath));
    }

    if (!Files.isReadable(inputPath)) {
      return ExitCode.IO_ERROR.exitMessage(
          String.format("The provided input file '%s' is not readable.", inputPath));
    }

    try {
      // Parse and compile the MetaPath expression
      MetapathExpression compiledMetapath = MetapathExpression.compile(expression);
      var sequence = compiledMetapath.evaluate();

      // Print the result
      System.out.println("Result: " + sequence.toString());

      return ExitCode.OK.exit();
    } catch (Exception ex) {
      return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
    }
  }
}