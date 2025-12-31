/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor;
import gov.nist.secauto.metaschema.cli.processor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.completion.CompletionScriptGenerator;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A command that generates shell completion scripts for Bash and Zsh.
 * <p>
 * This command introspects all registered commands and generates a completion
 * script that provides intelligent tab-completion for the CLI tool.
 */
public class ShellCompletionCommand
    extends AbstractTerminalCommand {

  @NonNull
  private static final String COMMAND = "shell-completion";

  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
      ExtraArgument.newInstance("shell", true)));

  @NonNull
  private static final Option TO_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("to")
          .hasArg()
          .argName("FILE")
          .type(File.class)
          .desc("write completion script to this file instead of stdout")
          .get());

  /**
   * Supported shell types.
   */
  public enum Shell {
    /** Bash shell. */
    BASH("bash"),
    /** Zsh shell. */
    ZSH("zsh");

    @NonNull
    private final String name;

    Shell(@NonNull String name) {
      this.name = name;
    }

    /**
     * Get the shell name.
     *
     * @return the name
     */
    @NonNull
    public String getName() {
      return name;
    }

    /**
     * Parse a shell type from a string.
     *
     * @param value
     *          the string value to parse
     * @return the shell type
     * @throws IllegalArgumentException
     *           if the value is not a recognized shell type
     */
    @NonNull
    public static Shell fromString(@NonNull String value) {
      String normalized = value.toLowerCase(Locale.ROOT);
      for (Shell shell : values()) {
        if (shell.name.equals(normalized)) {
          return shell;
        }
      }
      throw new IllegalArgumentException(
          "Unknown shell: " + value + ". Supported shells: bash, zsh");
    }
  }

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Generate shell completion script for bash or zsh";
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends Option> gatherOptions() {
    return List.of(TO_OPTION);
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
   * Execute the shell completion generation.
   *
   * @param callingContext
   *          the calling context
   * @param cmdLine
   *          the parsed command line
   * @throws CommandExecutionException
   *           if an error occurs during execution
   */
  protected void executeCommand(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) throws CommandExecutionException {

    List<String> extraArgs = cmdLine.getArgList();
    if (extraArgs.isEmpty()) {
      throw new CommandExecutionException(
          ExitCode.INVALID_ARGUMENTS,
          "Shell type is required. Supported shells: bash, zsh");
    }

    Shell shell;
    try {
      shell = Shell.fromString(ObjectUtils.notNull(extraArgs.get(0)));
    } catch (IllegalArgumentException ex) {
      throw new CommandExecutionException(ExitCode.INVALID_ARGUMENTS, ex.getMessage());
    }

    CLIProcessor processor = callingContext.getCLIProcessor();
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        processor.getExec(),
        processor.getTopLevelCommands());

    String script = shell == Shell.BASH
        ? generator.generateBashCompletion()
        : generator.generateZshCompletion();

    String outputFile = cmdLine.getOptionValue(TO_OPTION);
    if (outputFile != null) {
      writeToFile(outputFile, script);
    } else {
      writeToStdout(script);
    }
  }

  private static void writeToFile(@NonNull String outputFile, @NonNull String script)
      throws CommandExecutionException {
    Path path = resolveAgainstCWD(ObjectUtils.notNull(Path.of(outputFile)));
    try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      writer.write(script);
    } catch (IOException ex) {
      throw new CommandExecutionException(
          ExitCode.IO_ERROR,
          "Failed to write completion script to: " + path,
          ex);
    }
  }

  @SuppressWarnings("PMD.SystemPrintln")
  private static void writeToStdout(@NonNull String script) {
    PrintWriter writer = new PrintWriter(
        new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
    writer.print(script);
    writer.flush();
  }
}
