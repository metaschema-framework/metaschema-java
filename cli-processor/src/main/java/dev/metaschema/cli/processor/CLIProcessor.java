/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor;

import static org.jline.jansi.Ansi.ansi;

import dev.metaschema.cli.processor.command.CommandService;
import dev.metaschema.cli.processor.command.ICommand;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.IVersionInfo;
import dev.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.Option;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.eclipse.jdt.annotation.NotOwning;
import org.jline.jansi.Ansi;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Processes command line arguments and dispatches called commands.
 * <p>
 * This implementation make significant use of the command pattern to support a
 * delegation chain of commands based on implementations of {@link ICommand}.
 */
public class CLIProcessor {
  private static final Logger LOGGER = LogManager.getLogger(CLIProcessor.class);

  /**
   * This option indicates if the help should be shown.
   */
  @NonNull
  public static final Option HELP_OPTION = ObjectUtils.notNull(Option.builder("h")
      .longOpt("help")
      .desc("display this help message")
      .get());
  /**
   * This option indicates if colorized output should be disabled.
   */
  @NonNull
  public static final Option NO_COLOR_OPTION = ObjectUtils.notNull(Option.builder()
      .longOpt("no-color")
      .desc("do not colorize output")
      .get());
  /**
   * This option indicates if non-errors should be suppressed.
   */
  @NonNull
  public static final Option QUIET_OPTION = ObjectUtils.notNull(Option.builder("q")
      .longOpt("quiet")
      .desc("minimize output to include only errors")
      .get());
  /**
   * This option indicates if a strack trace should be shown for an error
   * {@link ExitStatus}.
   */
  @NonNull
  public static final Option SHOW_STACK_TRACE_OPTION = ObjectUtils.notNull(Option.builder()
      .longOpt("show-stack-trace")
      .desc("display the stack trace associated with an error")
      .get());
  /**
   * This option indicates if the version information should be shown.
   */
  @NonNull
  public static final Option VERSION_OPTION = ObjectUtils.notNull(Option.builder()
      .longOpt("version")
      .desc("display the application version")
      .get());

  @NonNull
  static final List<Option> OPTIONS = ObjectUtils.notNull(List.of(
      HELP_OPTION,
      NO_COLOR_OPTION,
      QUIET_OPTION,
      SHOW_STACK_TRACE_OPTION,
      VERSION_OPTION));

  /**
   * Used to identify the version info for the command.
   */
  public static final String COMMAND_VERSION = "http://csrc.nist.gov/ns/metaschema-java/cli/command-version";

  @NonNull
  private final List<ICommand> commands = new LinkedList<>();
  @NonNull
  private final String exec;
  @NonNull
  private final Map<String, IVersionInfo> versionInfos;
  @NonNull
  @NotOwning
  private final PrintStream outputStream;

  /**
   * The main entry point for command execution.
   *
   * @param args
   *          the command line arguments to process
   */
  public static void main(String... args) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    CLIProcessor processor = new CLIProcessor("metaschema-cli");

    CommandService.getInstance().getCommands().stream().forEach(command -> {
      assert command != null;
      processor.addCommandHandler(command);
    });
    System.exit(processor.process(args).getExitCode().getStatusCode());
  }

  /**
   * The main entry point for CLI processing.
   * <p>
   * This uses the build-in version information.
   *
   * @param args
   *          the command line arguments
   */
  public CLIProcessor(@NonNull String args) {
    this(args, CollectionUtil.singletonMap(COMMAND_VERSION, new ProcessorVersion()));
  }

  /**
   * The main entry point for CLI processing.
   * <p>
   * This uses the provided version information.
   *
   * @param exec
   *          the command name
   * @param versionInfos
   *          the version info to display when the version option is provided
   */
  public CLIProcessor(@NonNull String exec, @NonNull Map<String, IVersionInfo> versionInfos) {
    this(exec, versionInfos, null);
  }

  /**
   * The main entry point for CLI processing.
   * <p>
   * This constructor allows specifying a custom output stream for testing
   * purposes.
   *
   * @param exec
   *          the command name
   * @param versionInfos
   *          the version info to display when the version option is provided
   * @param outputStream
   *          the output stream to write to, or {@code null} to use the default
   *          console. The caller retains ownership of this stream and is
   *          responsible for closing it.
   */
  @SuppressWarnings("resource")
  public CLIProcessor(@NonNull String exec, @NonNull Map<String, IVersionInfo> versionInfos,
      @Nullable @NotOwning PrintStream outputStream) {
    this.exec = exec;
    this.versionInfos = versionInfos;
    // Use System.out directly - modern terminals (Windows 10+, Linux, macOS)
    // support ANSI natively without requiring native terminal detection
    this.outputStream = outputStream != null ? outputStream : ObjectUtils.notNull(System.out);
  }

  /**
   * Gets the command used to execute for use in help text.
   *
   * @return the command name
   */
  @NonNull
  public String getExec() {
    return exec;
  }

  /**
   * Retrieve the version information for this application.
   *
   * @return the versionInfo
   */
  @NonNull
  public Map<String, IVersionInfo> getVersionInfos() {
    return versionInfos;
  }

  /**
   * Register a new command handler.
   *
   * @param handler
   *          the command handler to register
   */
  public void addCommandHandler(@NonNull ICommand handler) {
    commands.add(handler);
  }

  /**
   * Process a set of CLIProcessor arguments.
   * <p>
   * process().getExitCode().getStatusCode()
   *
   * @param args
   *          the arguments to process
   * @return the exit status
   */
  @NonNull
  public ExitStatus process(String... args) {
    return parseCommand(args);
  }

  @NonNull
  private ExitStatus parseCommand(String... args) {
    List<String> commandArgs = Arrays.asList(args);
    assert commandArgs != null;
    CallingContext callingContext = new CallingContext(this, commandArgs);

    if (LOGGER.isDebugEnabled()) {
      String commandChain = callingContext.getCalledCommands().stream()
          .map(ICommand::getName)
          .collect(Collectors.joining(" -> "));
      LOGGER.debug("Processing command chain: {}", commandChain);
    }

    ExitStatus status;
    // the first two arguments should be the <command> and <operation>, where <type>
    // is the object type
    // the <operation> is performed against.
    if (commandArgs.isEmpty()) {
      status = ExitCode.INVALID_COMMAND.exit();
      callingContext.showHelp();
    } else {
      status = callingContext.processCommand();
    }
    return status;
  }

  /**
   * Get the root-level commands.
   *
   * @return the list of commands
   */
  @NonNull
  public final List<ICommand> getTopLevelCommands() {
    return CollectionUtil.unmodifiableList(commands);
  }

  /**
   * Get the root-level commands, mapped from name to command.
   *
   * @return the map of command names to command
   */
  @NonNull
  protected final Map<String, ICommand> getTopLevelCommandsByName() {
    return ObjectUtils.notNull(getTopLevelCommands()
        .stream()
        .collect(Collectors.toUnmodifiableMap(ICommand::getName, Function.identity())));
  }

  /**
   * Disable ANSI escape sequences in output.
   * <p>
   * When called, this method disables ANSI color codes, causing output to use
   * plain text without formatting. This is useful for legacy consoles that do not
   * support ANSI escape codes, CI/CD environments, or when redirecting output to
   * a file.
   */
  static void handleNoColor() {
    Ansi.setEnabled(false);
  }

  /**
   * Get the output stream used for writing CLI output.
   * <p>
   * The caller does not own this stream and must not close it.
   *
   * @return the output stream
   */
  @NonNull
  @NotOwning
  PrintStream getOutputStream() {
    return outputStream;
  }

  /**
   * Configure the logger to only report errors.
   */
  static void handleQuiet() {
    LoggerContext ctx = (LoggerContext) LogManager.getContext(false); // NOPMD not closable here
    Configuration config = ctx.getConfiguration();
    LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    Level oldLevel = loggerConfig.getLevel();
    if (oldLevel.isLessSpecificThan(Level.ERROR)) {
      loggerConfig.setLevel(Level.ERROR);
      ctx.updateLoggers();
    }
  }

  /**
   * Output version information.
   */
  protected void showVersion() {
    getVersionInfos().values().stream().forEach(info -> {
      outputStream.println(ansi()
          .bold().a(info.getName()).boldOff()
          .a(" ")
          .bold().a(info.getVersion()).boldOff()
          .a(" built at ")
          .bold().a(info.getBuildTimestamp()).boldOff()
          .a(" from branch ")
          .bold().a(info.getGitBranch()).boldOff()
          .a(" (")
          .bold().a(info.getGitCommit()).boldOff()
          .a(") at ")
          .bold().a(info.getGitOriginUrl()).boldOff()
          .reset());
    });
    outputStream.flush();
  }
}
