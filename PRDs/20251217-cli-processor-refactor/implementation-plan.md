# CLIProcessor Refactoring Implementation Plan

**Goal:** Refactor `processCommand` method and extract `CallingContext` to reduce complexity and improve testability.

**Architecture:** Extract `CallingContext` inner class to a package-private top-level class. Refactor `processCommand()` into discrete phases using Optional-based result chaining. Each phase either returns an exit status (stop) or empty (continue).

**Tech Stack:** Java 11, JUnit 5, Apache Commons CLI, Maven

---

## Pre-Implementation Setup

### Task 0: Rebase on PR #551

**Prerequisites:** PR #551 must be merged into `develop`

### Step 1: Fetch latest develop

```bash
cd ../metaschema-java-252
git fetch origin develop
```

### Step 2: Rebase branch

```bash
git rebase origin/develop
```

### Step 3: Verify build

```bash
mvn -pl cli-processor test
```

Expected: BUILD SUCCESS

---

## Phase 1: Test Infrastructure

### Task 1: Create Test Fixtures

**Files:**
- Create: `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/TestVersionInfo.java`
- Create: `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/TestCommand.java`

### Step 1: Create TestVersionInfo

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import gov.nist.secauto.metaschema.core.util.IVersionInfo;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A simple version info implementation for testing.
 */
class TestVersionInfo implements IVersionInfo {

  @Override
  @NonNull
  public String getName() {
    return "test-cli";
  }

  @Override
  @NonNull
  public String getVersion() {
    return "1.0.0-test";
  }

  @Override
  @NonNull
  public String getBuildTimestamp() {
    return "2025-01-01T00:00:00Z";
  }

  @Override
  @NonNull
  public String getGitOriginUrl() {
    return "https://example.com/test.git";
  }

  @Override
  @NonNull
  public String getGitBranch() {
    return "test-branch";
  }

  @Override
  @NonNull
  public String getGitCommit() {
    return "abc1234";
  }

  @Override
  @NonNull
  public String getGitClosestTag() {
    return "v1.0.0";
  }
}
```

### Step 2: Create TestCommand

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;

import org.apache.commons.cli.CommandLine;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A minimal command implementation for testing.
 */
class TestCommand extends AbstractTerminalCommand {

  @Override
  @NonNull
  public String getName() {
    return "test-cmd";
  }

  @Override
  @NonNull
  public String getDescription() {
    return "A test command";
  }

  @Override
  public ICommandExecutor newExecutor(
      @NonNull CLIProcessor.CallingContext callingContext,
      @NonNull CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  @NonNull
  private void executeCommand(
      @NonNull CLIProcessor.CallingContext callingContext,
      @NonNull CommandLine cmdLine) {
    // Do nothing - success
  }
}
```

### Step 3: Verify compilation

```bash
mvn -pl cli-processor test-compile
```

Expected: BUILD SUCCESS

### Step 4: Commit

```bash
git add cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/TestVersionInfo.java
git add cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/TestCommand.java
git commit -m "test: add test fixtures for CLIProcessor testing"
```

---

### Task 2: Create Integration Tests for Existing Behavior

**Files:**
- Create: `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/CLIProcessorTest.java`

### Step 1: Write integration test class with version test

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for {@link CLIProcessor}.
 * <p>
 * Tests the public API through {@code process(String... args)}.
 */
@DisplayName("CLIProcessor Integration Tests")
class CLIProcessorTest {

  private CLIProcessor processor;
  private ByteArrayOutputStream outputCapture;

  @BeforeEach
  void setUp() {
    outputCapture = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputCapture, true, StandardCharsets.UTF_8);
    processor = new CLIProcessor(
        "test-cli",
        Map.of(CLIProcessor.COMMAND_VERSION, new TestVersionInfo()),
        printStream);
  }

  @Nested
  @DisplayName("Global Options")
  class GlobalOptionsTests {

    @Test
    @DisplayName("--version shows version info and returns OK")
    void testVersionOption() {
      ExitStatus status = processor.process("--version");

      assertAll(
          () -> assertEquals(ExitCode.OK, status.getExitCode()),
          () -> assertThat(outputCapture.toString(StandardCharsets.UTF_8)).contains("test-cli"),
          () -> assertThat(outputCapture.toString(StandardCharsets.UTF_8)).contains("1.0.0-test"));
    }

    @Test
    @DisplayName("--help shows help and returns OK")
    void testHelpOption() {
      ExitStatus status = processor.process("--help");

      assertAll(
          () -> assertEquals(ExitCode.OK, status.getExitCode()),
          () -> assertThat(outputCapture.toString(StandardCharsets.UTF_8)).contains("--help"));
    }

    @Test
    @DisplayName("--quiet option is accepted")
    void testQuietOption() {
      processor.addCommandHandler(new TestCommand());

      ExitStatus status = processor.process("--quiet", "test-cmd");

      assertEquals(ExitCode.OK, status.getExitCode());
    }
  }

  @Nested
  @DisplayName("Command Execution")
  class CommandExecutionTests {

    @Test
    @DisplayName("Valid command executes successfully")
    void testValidCommandExecution() {
      processor.addCommandHandler(new TestCommand());

      ExitStatus status = processor.process("test-cmd");

      assertEquals(ExitCode.OK, status.getExitCode());
    }

    @Test
    @DisplayName("Unknown command returns INVALID_COMMAND")
    void testUnknownCommand() {
      ExitStatus status = processor.process("nonexistent-command");

      assertEquals(ExitCode.INVALID_COMMAND, status.getExitCode());
    }

    @Test
    @DisplayName("Invalid option returns INVALID_COMMAND")
    void testInvalidOption() {
      ExitStatus status = processor.process("--invalid-option-xyz");

      assertEquals(ExitCode.INVALID_COMMAND, status.getExitCode());
    }

    @Test
    @DisplayName("Empty args returns INVALID_COMMAND with help")
    void testEmptyArgs() {
      ExitStatus status = processor.process();

      assertAll(
          () -> assertEquals(ExitCode.INVALID_COMMAND, status.getExitCode()),
          () -> assertThat(outputCapture.toString(StandardCharsets.UTF_8)).contains("--help"));
    }
  }
}
```

### Step 2: Run tests to verify they pass with existing code

```bash
mvn -pl cli-processor test -Dtest=CLIProcessorTest
```

Expected: BUILD SUCCESS (all tests pass - these characterize existing behavior)

### Step 3: Commit

```bash
git add cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/CLIProcessorTest.java
git commit -m "test: add integration tests for CLIProcessor existing behavior"
```

---

## Phase 2: Extract CallingContext

### Task 3: Create CallingContext Top-Level Class (Copy)

**Files:**
- Create: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Create new CallingContext.java file

Copy the inner class to a new file, adding package-private visibility and updating `CLIProcessor.this` references:

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import static org.fusesource.jansi.Ansi.ansi;

import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ICommand;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.cli.processor.command.CommandExecutionException;
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
class CallingContext {
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
   * Process the command identified by the CLI arguments.
   *
   * @return the result of processing the command
   */
  @SuppressWarnings({
      "PMD.OnlyOneReturn",
      "PMD.NPathComplexity",
      "PMD.CyclomaticComplexity"
  })
  @NonNull
  public ExitStatus processCommand() {
    // TODO: Refactor in subsequent commits
    CommandLineParser parser = new DefaultParser();

    // phase 1
    CommandLine cmdLine;
    try {
      Options phase1Options = new Options();
      phase1Options.addOption(CLIProcessor.HELP_OPTION);
      phase1Options.addOption(CLIProcessor.VERSION_OPTION);

      cmdLine = ObjectUtils.notNull(parser.parse(phase1Options, getExtraArgs().toArray(new String[0]), true));
    } catch (ParseException ex) {
      String msg = ex.getMessage();
      assert msg != null;
      return handleInvalidCommand(msg);
    }

    if (cmdLine.hasOption(CLIProcessor.VERSION_OPTION)) {
      cliProcessor.showVersion();
      return ExitCode.OK.exit();
    }
    if (cmdLine.hasOption(CLIProcessor.HELP_OPTION)) {
      showHelp();
      return ExitCode.OK.exit();
    }

    // phase 2
    try {
      cmdLine = ObjectUtils.notNull(parser.parse(toOptions(), getExtraArgs().toArray(new String[0])));
    } catch (ParseException ex) {
      String msg = ex.getMessage();
      assert msg != null;
      return handleInvalidCommand(msg);
    }

    ICommand targetCommand = getTargetCommand();
    if (targetCommand != null) {
      try {
        targetCommand.validateExtraArguments(this, cmdLine);
      } catch (InvalidArgumentException ex) {
        return handleError(
            ExitCode.INVALID_ARGUMENTS.exitMessage(ex.getLocalizedMessage()),
            cmdLine,
            true);
      }
    }

    for (ICommand cmd : getCalledCommands()) {
      try {
        cmd.validateOptions(this, cmdLine);
      } catch (InvalidArgumentException ex) {
        String msg = ex.getMessage();
        assert msg != null;
        return handleInvalidCommand(msg);
      }
    }

    // phase 3
    if (cmdLine.hasOption(CLIProcessor.NO_COLOR_OPTION)) {
      CLIProcessor.handleNoColor();
    }

    if (cmdLine.hasOption(CLIProcessor.QUIET_OPTION)) {
      CLIProcessor.handleQuiet();
    }
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
```

### Step 2: Verify compilation

```bash
mvn -pl cli-processor compile
```

Expected: BUILD SUCCESS

### Step 3: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: extract CallingContext to top-level package-private class"
```

---

### Task 4: Update CLIProcessor to Use Extracted CallingContext

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CLIProcessor.java`

### Step 1: Add package-private getter for outputStream

Add this method to `CLIProcessor.java` (needed by extracted `CallingContext`):

```java
/**
 * Get the output stream used for writing CLI output.
 *
 * @return the output stream
 */
@NonNull
PrintStream getOutputStream() {
  return outputStream;
}
```

### Step 2: Make OPTIONS package-private

Change the `OPTIONS` field visibility from `private` to package-private:

```java
@NonNull
static final List<Option> OPTIONS = ObjectUtils.notNull(List.of(
    HELP_OPTION,
    NO_COLOR_OPTION,
    QUIET_OPTION,
    SHOW_STACK_TRACE_OPTION,
    VERSION_OPTION));
```

### Step 3: Make handleNoColor and handleQuiet package-private

Change visibility from `private`/`public` to package-private (no modifier):

```java
static void handleNoColor() {
  System.setProperty(AnsiConsole.JANSI_MODE, AnsiConsole.JANSI_MODE_STRIP);
  AnsiConsole.systemUninstall();
}

/**
 * Configure the logger to only report errors.
 */
static void handleQuiet() {
  // ... existing implementation
}
```

### Step 4: Update parseCommand to use new CallingContext constructor

```java
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
  if (commandArgs.isEmpty()) {
    status = ExitCode.INVALID_COMMAND.exit();
    callingContext.showHelp();
  } else {
    status = callingContext.processCommand();
  }
  return status;
}
```

### Step 5: Remove the inner CallingContext class

Delete the entire inner class `CallingContext` from `CLIProcessor.java` (lines 328-802 approximately).

### Step 6: Run tests to verify behavior preserved

```bash
mvn -pl cli-processor test
```

Expected: BUILD SUCCESS (all tests pass)

### Step 7: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CLIProcessor.java
git commit -m "refactor: update CLIProcessor to use extracted CallingContext"
```

---

### Task 5: Update ShellCompletionCommand Import

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/command/ShellCompletionCommand.java`

### Step 1: Update import statement

Change:
```java
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
```

To:
```java
import gov.nist.secauto.metaschema.cli.processor.CallingContext;
```

### Step 2: Run tests

```bash
mvn -pl cli-processor test
```

Expected: BUILD SUCCESS

### Step 3: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/command/ShellCompletionCommand.java
git commit -m "refactor: update ShellCompletionCommand import for extracted CallingContext"
```

---

## Phase 3: Refactor processCommand

### Task 6: Add Unit Tests for Phase Methods

**Files:**
- Create: `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/CallingContextTest.java`

### Step 1: Create CallingContextTest with phase tests

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Unit tests for {@link CallingContext} phase methods.
 */
@DisplayName("CallingContext Unit Tests")
class CallingContextTest {

  private CLIProcessor processor;

  @BeforeEach
  void setUp() {
    PrintStream nullOutput = new PrintStream(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8);
    processor = new CLIProcessor(
        "test-cli",
        Map.of(CLIProcessor.COMMAND_VERSION, new TestVersionInfo()),
        nullOutput);
  }

  private CallingContext createContext(String... args) {
    return new CallingContext(processor, Arrays.asList(args));
  }

  @Nested
  @DisplayName("checkHelpAndVersion()")
  class CheckHelpAndVersionTests {

    @Test
    @DisplayName("returns ExitStatus for --version")
    void returnsExitStatusForVersionOption() {
      CallingContext ctx = createContext("--version");

      Optional<ExitStatus> result = ctx.checkHelpAndVersion();

      assertAll(
          () -> assertTrue(result.isPresent()),
          () -> assertEquals(ExitCode.OK, result.get().getExitCode()));
    }

    @Test
    @DisplayName("returns ExitStatus for --help")
    void returnsExitStatusForHelpOption() {
      CallingContext ctx = createContext("--help");

      Optional<ExitStatus> result = ctx.checkHelpAndVersion();

      assertAll(
          () -> assertTrue(result.isPresent()),
          () -> assertEquals(ExitCode.OK, result.get().getExitCode()));
    }

    @Test
    @DisplayName("returns empty for other args")
    void returnsEmptyForOtherArgs() {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd");

      Optional<ExitStatus> result = ctx.checkHelpAndVersion();

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("parseOptions()")
  class ParseOptionsTests {

    @Test
    @DisplayName("parses valid options")
    void parsesValidOptions() throws ParseException {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd", "--quiet");

      CommandLine cmdLine = ctx.parseOptions();

      assertTrue(cmdLine.hasOption(CLIProcessor.QUIET_OPTION));
    }

    @Test
    @DisplayName("throws on invalid option")
    void throwsOnInvalidOption() {
      CallingContext ctx = createContext("--invalid-option-xyz");

      assertThrows(ParseException.class, ctx::parseOptions);
    }
  }

  @Nested
  @DisplayName("validateExtraArguments()")
  class ValidateExtraArgumentsTests {

    @Test
    @DisplayName("returns empty when no target command")
    void returnsEmptyWhenNoTargetCommand() throws ParseException {
      CallingContext ctx = createContext("--help");
      CommandLine cmdLine = ctx.parseOptions();

      Optional<ExitStatus> result = ctx.validateExtraArguments(cmdLine);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("returns empty when arguments valid")
    void returnsEmptyWhenArgumentsValid() throws ParseException {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd");
      CommandLine cmdLine = ctx.parseOptions();

      Optional<ExitStatus> result = ctx.validateExtraArguments(cmdLine);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("validateCalledCommands()")
  class ValidateCalledCommandsTests {

    @Test
    @DisplayName("returns empty when all commands valid")
    void returnsEmptyWhenAllCommandsValid() throws ParseException {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd");
      CommandLine cmdLine = ctx.parseOptions();

      Optional<ExitStatus> result = ctx.validateCalledCommands(cmdLine);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("applyGlobalOptions()")
  class ApplyGlobalOptionsTests {

    @Test
    @DisplayName("applies --quiet without error")
    void appliesQuietOption() throws ParseException {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd", "--quiet");
      CommandLine cmdLine = ctx.parseOptions();

      assertDoesNotThrow(() -> ctx.applyGlobalOptions(cmdLine));
    }
  }
}
```

### Step 2: Run tests - they will fail (methods don't exist yet)

```bash
mvn -pl cli-processor test -Dtest=CallingContextTest
```

Expected: COMPILATION ERROR (methods not found) - this is expected for TDD

### Step 3: Commit test file

```bash
git add cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/CallingContextTest.java
git commit -m "test: add unit tests for CallingContext phase methods (TDD - red)"
```

---

### Task 7: Extract checkHelpAndVersion Method

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Add checkHelpAndVersion method

Add this method to `CallingContext.java`:

```java
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
```

### Step 2: Add import for Optional

```java
import java.util.Optional;
```

### Step 3: Run tests for checkHelpAndVersion

```bash
mvn -pl cli-processor test -Dtest=CallingContextTest#*checkHelpAndVersion*
```

Expected: BUILD SUCCESS

### Step 4: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: extract checkHelpAndVersion phase method"
```

---

### Task 8: Extract parseOptions Method

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Add parseOptions method

```java
/**
 * Parse all command line options.
 * <p>
 * This is phase 2 of command processing.
 *
 * @return the parsed command line
 * @throws ParseException if parsing fails
 */
@NonNull
protected CommandLine parseOptions() throws ParseException {
  return ObjectUtils.notNull(
      new DefaultParser().parse(toOptions(), getExtraArgs().toArray(new String[0])));
}
```

### Step 2: Run tests for parseOptions

```bash
mvn -pl cli-processor test -Dtest=CallingContextTest#*parseOptions*
```

Expected: BUILD SUCCESS

### Step 3: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: extract parseOptions phase method"
```

---

### Task 9: Extract validateExtraArguments Method

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Add validateExtraArguments method

```java
/**
 * Validate extra arguments for the target command.
 * <p>
 * This is phase 3 of command processing.
 *
 * @param cmdLine the parsed command line
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
```

### Step 2: Run tests for validateExtraArguments

```bash
mvn -pl cli-processor test -Dtest=CallingContextTest#*validateExtraArguments*
```

Expected: BUILD SUCCESS

### Step 3: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: extract validateExtraArguments phase method"
```

---

### Task 10: Extract validateCalledCommands Method

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Add validateCalledCommands method

```java
/**
 * Validate options for all called commands in the chain.
 * <p>
 * This is phase 4 of command processing.
 *
 * @param cmdLine the parsed command line
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
```

### Step 2: Run tests for validateCalledCommands

```bash
mvn -pl cli-processor test -Dtest=CallingContextTest#*validateCalledCommands*
```

Expected: BUILD SUCCESS

### Step 3: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: extract validateCalledCommands phase method"
```

---

### Task 11: Extract applyGlobalOptions Method

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Add applyGlobalOptions method

```java
/**
 * Apply global options like --no-color and --quiet.
 * <p>
 * This is phase 5 of command processing.
 *
 * @param cmdLine the parsed command line
 */
protected void applyGlobalOptions(@NonNull CommandLine cmdLine) {
  if (cmdLine.hasOption(CLIProcessor.NO_COLOR_OPTION)) {
    CLIProcessor.handleNoColor();
  }
  if (cmdLine.hasOption(CLIProcessor.QUIET_OPTION)) {
    CLIProcessor.handleQuiet();
  }
}
```

### Step 2: Run tests for applyGlobalOptions

```bash
mvn -pl cli-processor test -Dtest=CallingContextTest#*applyGlobalOptions*
```

Expected: BUILD SUCCESS

### Step 3: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: extract applyGlobalOptions phase method"
```

---

### Task 12: Refactor processCommand to Use Phase Methods

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Replace processCommand implementation

Replace the entire `processCommand()` method with:

```java
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
```

### Step 2: Remove PMD suppressions from processCommand

Remove this annotation from processCommand:

```java
@SuppressWarnings({
    "PMD.OnlyOneReturn",
    "PMD.NPathComplexity",
    "PMD.CyclomaticComplexity"
})
```

### Step 3: Run all tests

```bash
mvn -pl cli-processor test
```

Expected: BUILD SUCCESS

### Step 4: Run full CI build

```bash
mvn -pl cli-processor install -PCI
```

Expected: BUILD SUCCESS (no PMD violations)

### Step 5: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: simplify processCommand using extracted phase methods

Reduces cyclomatic complexity by delegating to focused phase methods.
Removes PMD suppressions for complexity warnings."
```

---

## Phase 4: Remove GodClass Warning

### Task 13: Verify GodClass Warning Resolved

**Files:**
- Modify: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java`

### Step 1: Remove GodClass suppression

Remove this line from `CallingContext.java`:

```java
@SuppressWarnings("PMD.GodClass")
```

### Step 2: Run PMD check

```bash
mvn -pl cli-processor pmd:check
```

Expected: BUILD SUCCESS (if GodClass warning persists, we may need additional extraction - but try first)

### Step 3: Run full CI build

```bash
mvn install -PCI -Prelease
```

Expected: BUILD SUCCESS

### Step 4: Commit

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/CallingContext.java
git commit -m "refactor: remove PMD GodClass suppression from CallingContext"
```

---

## Phase 5: Final Verification

### Task 14: Full Build Verification

### Step 1: Run complete CI build

```bash
mvn clean install -PCI -Prelease
```

Expected: BUILD SUCCESS

### Step 2: Verify all tests pass

```bash
mvn test
```

Expected: All tests pass

### Step 3: Verify no checkstyle issues

```bash
mvn checkstyle:check
```

Expected: BUILD SUCCESS

### Step 4: Review commit history

```bash
git log --oneline -15
```

Verify commits are clean and well-organized.

---

## Summary

| Task | Description | Files |
|------|-------------|-------|
| 0 | Rebase on PR #551 | - |
| 1 | Create test fixtures | TestVersionInfo.java, TestCommand.java |
| 2 | Create integration tests | CLIProcessorTest.java |
| 3 | Extract CallingContext | CallingContext.java (new) |
| 4 | Update CLIProcessor | CLIProcessor.java |
| 5 | Update ShellCompletionCommand | ShellCompletionCommand.java |
| 6 | Add unit tests | CallingContextTest.java |
| 7-11 | Extract phase methods | CallingContext.java |
| 12 | Refactor processCommand | CallingContext.java |
| 13 | Remove GodClass warning | CallingContext.java |
| 14 | Final verification | - |

**Estimated commits:** 14
**Estimated time:** 2-3 hours
