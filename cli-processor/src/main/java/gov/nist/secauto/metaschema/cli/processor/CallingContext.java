/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import static org.fusesource.jansi.Ansi.ansi;

import gov.nist.secauto.metaschema.cli.processor.command.CommandExecutionException;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ICommand;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.util.AutoCloser;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fusesource.jansi.AnsiPrintStream;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Records information about the command line options and called command
 * hierarchy.
 */
@SuppressWarnings("PMD.GodClass")
public class CallingContext {
  @NonNull
  private final CLIProcessor cliProcessor;
  @NonNull
  private final List<Option> options;
  @NonNull
  private final List<ICommand> calledCommands;
  @Nullable
  private final ICommand targetCommand;
  @NonNull
  private final List<String> extraArgs;

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  CallingContext(@NonNull CLIProcessor cliProcessor, @NonNull List<String> args) {
    this.cliProcessor = cliProcessor;

    @SuppressWarnings("PMD.LooseCoupling") // needed to support getLast
    LinkedList<ICommand> calledCommands = new LinkedList<>();
    List<Option> options = new LinkedList<>(CLIProcessor.OPTIONS);
    List<String> extraArgs = new LinkedList<>();

    AtomicBoolean endArgs = new AtomicBoolean();
    args.forEach(arg -> {
      if (endArgs.get() || arg.startsWith("-")) {
        extraArgs.add(arg);
      } else if ("--".equals(arg)) {
        endArgs.set(true);
      } else {
        ICommand command = calledCommands.isEmpty()
            ? cliProcessor.getTopLevelCommandsByName().get(arg)
            : calledCommands.getLast().getSubCommandByName(arg);

        if (command == null) {
          extraArgs.add(arg);
          endArgs.set(true);
        } else {
          calledCommands.add(command);
          options.addAll(command.gatherOptions());
        }
      }
    });

    this.calledCommands = CollectionUtil.unmodifiableList(calledCommands);
    this.targetCommand = calledCommands.peekLast();
    this.options = CollectionUtil.unmodifiableList(options);
    this.extraArgs = CollectionUtil.unmodifiableList(extraArgs);
  }

  /**
   * Get the command line processor instance that generated this calling context.
   *
   * @return the instance
   */
  @NonNull
  public CLIProcessor getCLIProcessor() {
    return cliProcessor;
  }

  /**
   * Get the command that was triggered by the CLI arguments.
   *
   * @return the command or {@code null} if no command was triggered
   */
  @Nullable
  public ICommand getTargetCommand() {
    return targetCommand;
  }

  /**
   * Get the options that are in scope for the current command context.
   *
   * @return the list of options
   */
  @NonNull
  List<Option> getOptionsList() {
    return options;
  }

  @NonNull
  List<ICommand> getCalledCommands() {
    return calledCommands;
  }

  /**
   * Get any left over arguments that were not consumed by CLI options.
   *
   * @return the list of remaining arguments, which may be empty
   */
  @NonNull
  List<String> getExtraArgs() {
    return extraArgs;
  }

  /**
   * Get the collections of in scope options as an options group.
   *
   * @return the options group
   */
  Options toOptions() {
    Options retval = new Options();
    for (Option option : getOptionsList()) {
      retval.addOption(option);
    }
    return retval;
  }

  /**
   * Check for --help and --version options before full parsing.
   * <p>
   * This is phase 1 of command processing.
   *
   * @return an exit status if help or version was requested, or empty to continue
   */
  @NonNull
  protected Optional<ExitStatus> checkHelpAndVersion() {
    Options phase1Options = new Options();
    phase1Options.addOption(CLIProcessor.HELP_OPTION);
    phase1Options.addOption(CLIProcessor.VERSION_OPTION);

    try {
      CommandLine cmdLine = new DefaultParser()
          .parse(phase1Options, getExtraArgs().toArray(new String[0]), true);

      if (cmdLine.hasOption(CLIProcessor.VERSION_OPTION)) {
        cliProcessor.showVersion();
        return Optional.of(ExitCode.OK.exit());
      }
      if (cmdLine.hasOption(CLIProcessor.HELP_OPTION)) {
        showHelp();
        return Optional.of(ExitCode.OK.exit());
      }
    } catch (ParseException ex) {
      return Optional.of(handleInvalidCommand(ObjectUtils.notNull(ex.getMessage())));
    }
    return Optional.empty();
  }

  /**
   * Parse all command line options.
   * <p>
   * This is phase 2 of command processing.
   *
   * @return the parsed command line
   * @throws ParseException
   *           if parsing fails
   */
  @NonNull
  protected CommandLine parseOptions() throws ParseException {
    return ObjectUtils.notNull(
        new DefaultParser().parse(toOptions(), getExtraArgs().toArray(new String[0])));
  }

  /**
   * Validate extra arguments for the target command.
   * <p>
   * This is phase 3 of command processing.
   *
   * @param cmdLine
   *          the parsed command line
   * @return an exit status if validation failed, or empty to continue
   */
  @NonNull
  protected Optional<ExitStatus> validateExtraArguments(@NonNull CommandLine cmdLine) {
    ICommand target = getTargetCommand();
    if (target == null) {
      return Optional.empty();
    }
    try {
      target.validateExtraArguments(this, cmdLine);
      return Optional.empty();
    } catch (InvalidArgumentException ex) {
      return Optional.of(handleError(
          ExitCode.INVALID_ARGUMENTS.exitMessage(ex.getLocalizedMessage()),
          cmdLine,
          true));
    }
  }

  /**
   * Validate options for all called commands in the chain.
   * <p>
   * This is phase 4 of command processing.
   *
   * @param cmdLine
   *          the parsed command line
   * @return an exit status if validation failed, or empty to continue
   */
  @NonNull
  protected Optional<ExitStatus> validateCalledCommands(@NonNull CommandLine cmdLine) {
    for (ICommand cmd : getCalledCommands()) {
      try {
        cmd.validateOptions(this, cmdLine);
      } catch (InvalidArgumentException ex) {
        String msg = ex.getMessage();
        assert msg != null;
        return Optional.of(handleInvalidCommand(msg));
      }
    }
    return Optional.empty();
  }

  /**
   * Apply global options like --no-color and --quiet.
   * <p>
   * This is phase 5 of command processing.
   *
   * @param cmdLine
   *          the parsed command line
   */
  protected void applyGlobalOptions(@NonNull CommandLine cmdLine) {
    if (cmdLine.hasOption(CLIProcessor.NO_COLOR_OPTION)) {
      CLIProcessor.handleNoColor();
    }
    if (cmdLine.hasOption(CLIProcessor.QUIET_OPTION)) {
      CLIProcessor.handleQuiet();
    }
  }

  /**
   * Process the command identified by the CLI arguments.
   *
   * @return the result of processing the command
   */
  @NonNull
  public ExitStatus processCommand() {
    // Phase 1: Check help/version before full parsing
    Optional<ExitStatus> earlyExit = checkHelpAndVersion();
    if (earlyExit.isPresent()) {
      return earlyExit.get();
    }

    // Phase 2: Parse all options
    CommandLine cmdLine;
    try {
      cmdLine = parseOptions();
    } catch (ParseException ex) {
      String msg = ex.getMessage();
      assert msg != null;
      return handleInvalidCommand(msg);
    }

    // Phase 3-4: Validate arguments and options
    Optional<ExitStatus> validationResult = validateExtraArguments(cmdLine)
        .or(() -> validateCalledCommands(cmdLine));
    if (validationResult.isPresent()) {
      return validationResult.get();
    }

    // Phase 5: Apply global options and execute
    applyGlobalOptions(cmdLine);
    return invokeCommand(cmdLine);
  }

  /**
   * Directly execute the logic associated with the command.
   *
   * @param cmdLine
   *          the command line information
   * @return the result of executing the command
   */
  @SuppressWarnings({
      "PMD.OnlyOneReturn", // readability
      "PMD.AvoidCatchingGenericException" // needed here
  })
  @NonNull
  private ExitStatus invokeCommand(@NonNull CommandLine cmdLine) {
    ExitStatus retval;
    try {
      ICommand targetCommand = getTargetCommand();
      if (targetCommand == null) {
        retval = ExitCode.INVALID_COMMAND.exit();
      } else {
        ICommandExecutor executor = targetCommand.newExecutor(this, cmdLine);
        try {
          executor.execute();
          retval = ExitCode.OK.exit();
        } catch (CommandExecutionException ex) {
          retval = ex.toExitStatus();
        } catch (RuntimeException ex) {
          retval = ExitCode.RUNTIME_ERROR
              .exitMessage("Unexpected error occured: " + ex.getLocalizedMessage())
              .withThrowable(ex);
        }
      }
    } catch (RuntimeException ex) {
      retval = ExitCode.RUNTIME_ERROR
          .exitMessage(String.format("An uncaught runtime error occurred. %s", ex.getLocalizedMessage()))
          .withThrowable(ex);
    }

    if (!ExitCode.OK.equals(retval.getExitCode())) {
      retval.generateMessage(cmdLine.hasOption(CLIProcessor.SHOW_STACK_TRACE_OPTION));

      if (ExitCode.INVALID_COMMAND.equals(retval.getExitCode())) {
        showHelp();
      }
    }
    return retval;
  }

  /**
   * Handle an error that occurred while executing the command.
   *
   * @param exitStatus
   *          the execution result
   * @param cmdLine
   *          the command line information
   * @param showHelp
   *          if {@code true} show the help information
   * @return the resulting exit status
   */
  @NonNull
  public ExitStatus handleError(
      @NonNull ExitStatus exitStatus,
      @NonNull CommandLine cmdLine,
      boolean showHelp) {
    exitStatus.generateMessage(cmdLine.hasOption(CLIProcessor.SHOW_STACK_TRACE_OPTION));
    if (showHelp) {
      showHelp();
    }
    return exitStatus;
  }

  /**
   * Generate the help message and exit status for an invalid command using the
   * provided message.
   *
   * @param message
   *          the error message
   * @return the resulting exit status
   */
  @NonNull
  public ExitStatus handleInvalidCommand(
      @NonNull String message) {
    showHelp();

    ExitStatus retval = ExitCode.INVALID_COMMAND.exitMessage(message);
    retval.generateMessage(false);
    return retval;
  }

  /**
   * Callback for providing a help header.
   *
   * @return the header or {@code null}
   */
  @Nullable
  private String buildHelpHeader() {
    // TODO: build a suitable header
    return null;
  }

  /**
   * Callback for providing a help footer.
   *
   * @return the footer or {@code null}
   */
  @NonNull
  private String buildHelpFooter() {
    ICommand targetCommand = getTargetCommand();
    Collection<ICommand> subCommands;
    if (targetCommand == null) {
      subCommands = cliProcessor.getTopLevelCommands();
    } else {
      subCommands = targetCommand.getSubCommands();
    }

    String retval;
    if (subCommands.isEmpty()) {
      retval = "";
    } else {
      StringBuilder builder = new StringBuilder(128);
      builder
          .append(System.lineSeparator())
          .append("The following are available commands:")
          .append(System.lineSeparator());

      int length = subCommands.stream()
          .mapToInt(command -> command.getName().length())
          .max().orElse(0);

      for (ICommand command : subCommands) {
        builder.append(
            ansi()
                .render(String.format("   @|bold %-" + length + "s|@ %s%n",
                    command.getName(),
                    command.getDescription())));
      }
      builder
          .append(System.lineSeparator())
          .append('\'')
          .append(cliProcessor.getExec())
          .append(" <command> --help' will show help on that specific command.")
          .append(System.lineSeparator());
      retval = builder.toString();
      assert retval != null;
    }
    return retval;
  }

  /**
   * Get the CLI syntax.
   *
   * @return the CLI syntax to display in help output
   */
  private String buildHelpCliSyntax() {
    StringBuilder builder = new StringBuilder(64);
    builder.append(cliProcessor.getExec());

    List<ICommand> calledCommands = getCalledCommands();
    if (!calledCommands.isEmpty()) {
      builder.append(calledCommands.stream()
          .map(ICommand::getName)
          .collect(Collectors.joining(" ", " ", "")));
    }

    // output calling commands
    ICommand targetCommand = getTargetCommand();
    if (targetCommand == null) {
      builder.append(" <command>");
    } else {
      builder.append(getSubCommands(targetCommand));
    }

    // output required options
    getOptionsList().stream()
        .filter(Option::isRequired)
        .forEach(option -> {
          builder
              .append(' ')
              .append(OptionUtils.toArgument(ObjectUtils.notNull(option)));
          if (option.hasArg()) {
            builder
                .append('=')
                .append(option.getArgName());
          }
        });

    // output non-required option placeholder
    builder.append(" [<options>]");

    // output extra arguments
    if (targetCommand != null) {
      // handle extra arguments
      builder.append(getExtraArguments(targetCommand));
    }

    String retval = builder.toString();
    assert retval != null;
    return retval;
  }

  @NonNull
  private CharSequence getSubCommands(ICommand targetCommand) {
    Collection<ICommand> subCommands = targetCommand.getSubCommands();

    StringBuilder builder = new StringBuilder();
    if (!subCommands.isEmpty()) {
      builder.append(' ');
      if (!targetCommand.isSubCommandRequired()) {
        builder.append('[');
      }

      builder.append("<command>");

      if (!targetCommand.isSubCommandRequired()) {
        builder.append(']');
      }
    }
    return builder;
  }

  @NonNull
  private CharSequence getExtraArguments(@NonNull ICommand targetCommand) {
    StringBuilder builder = new StringBuilder();
    for (ExtraArgument argument : targetCommand.getExtraArguments()) {
      builder.append(' ');
      if (!argument.isRequired()) {
        builder.append('[');
      }

      builder.append('<')
          .append(argument.getName())
          .append('>');

      if (argument.getNumber() > 1) {
        builder.append("...");
      }

      if (!argument.isRequired()) {
        builder.append(']');
      }
    }
    return builder;
  }

  /**
   * Output the help text to the console.
   */
  public void showHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setLongOptSeparator("=");

    PrintStream out = cliProcessor.getOutputStream();
    int terminalWidth = (out instanceof AnsiPrintStream)
        ? ((AnsiPrintStream) out).getTerminalWidth()
        : 80;

    try (PrintWriter writer = new PrintWriter( // NOPMD not owned
        AutoCloser.preventClose(out),
        true,
        StandardCharsets.UTF_8)) {
      formatter.printHelp(
          writer,
          Math.max(terminalWidth, 50),
          buildHelpCliSyntax(),
          buildHelpHeader(),
          toOptions(),
          HelpFormatter.DEFAULT_LEFT_PAD,
          HelpFormatter.DEFAULT_DESC_PAD,
          buildHelpFooter(),
          false);
      writer.flush();
    }
  }
}
