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

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.eclipse.jdt.annotation.Owning;
import org.junit.jupiter.api.AfterEach;
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

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Unit tests for {@link CallingContext} phase methods.
 */
@DisplayName("CallingContext Unit Tests")
class CallingContextTest {

  private CLIProcessor processor;
  @SuppressWarnings("resource")
  @Owning
  private PrintStream nullOutput;

  @BeforeEach
  void setUp() {
    nullOutput = new PrintStream(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8);
    processor = new CLIProcessor(
        "test-cli",
        ObjectUtils.notNull(Map.of(CLIProcessor.COMMAND_VERSION, new TestVersionInfo())),
        nullOutput);
  }

  @AfterEach
  void tearDown() {
    nullOutput.close();
  }

  @NonNull
  private CallingContext createContext(@NonNull String... args) {
    return new CallingContext(ObjectUtils.notNull(processor), ObjectUtils.notNull(Arrays.asList(args)));
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

    @Test
    @DisplayName("throws on multiple invalid options")
    void throwsOnMultipleInvalidOptions() {
      CallingContext ctx = createContext("--invalid-one", "--invalid-two", "--invalid-three");

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

    @Test
    @DisplayName("returns error when required argument missing")
    void returnsErrorWhenRequiredArgumentMissing() throws ParseException {
      processor.addCommandHandler(new TestCommandWithRequiredArg());
      CallingContext ctx = createContext("test-cmd-with-arg");
      CommandLine cmdLine = ctx.parseOptions();

      Optional<ExitStatus> result = ctx.validateExtraArguments(cmdLine);

      assertAll(
          () -> assertTrue(result.isPresent()),
          () -> assertEquals(ExitCode.INVALID_ARGUMENTS, result.get().getExitCode()));
    }

    @Test
    @DisplayName("returns error when too many arguments provided")
    void returnsErrorWhenTooManyArguments() throws ParseException {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd", "extra-arg-1", "extra-arg-2");
      CommandLine cmdLine = ctx.parseOptions();

      Optional<ExitStatus> result = ctx.validateExtraArguments(cmdLine);

      assertAll(
          () -> assertTrue(result.isPresent()),
          () -> assertEquals(ExitCode.INVALID_ARGUMENTS, result.get().getExitCode()));
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

    @Test
    @DisplayName("returns error when required option missing")
    void returnsErrorWhenRequiredOptionMissing() throws ParseException {
      processor.addCommandHandler(new TestCommandWithRequiredOption());
      CallingContext ctx = createContext("test-cmd-with-option");
      CommandLine cmdLine = ctx.parseOptions();

      Optional<ExitStatus> result = ctx.validateCalledCommands(cmdLine);

      assertAll(
          () -> assertTrue(result.isPresent()),
          () -> assertEquals(ExitCode.INVALID_COMMAND, result.get().getExitCode()));
    }

    @Test
    @DisplayName("returns empty when required option provided")
    void returnsEmptyWhenRequiredOptionProvided() throws ParseException {
      processor.addCommandHandler(new TestCommandWithRequiredOption());
      CallingContext ctx = createContext("test-cmd-with-option", "--required-opt", "value");
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

    @Test
    @DisplayName("applies --no-color without error")
    void appliesNoColorOption() throws ParseException {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd", "--no-color");
      CommandLine cmdLine = ctx.parseOptions();

      assertDoesNotThrow(() -> ctx.applyGlobalOptions(cmdLine));
    }

    @Test
    @DisplayName("applies both --quiet and --no-color without error")
    void appliesBothQuietAndNoColor() throws ParseException {
      processor.addCommandHandler(new TestCommand());
      CallingContext ctx = createContext("test-cmd", "--quiet", "--no-color");
      CommandLine cmdLine = ctx.parseOptions();

      assertDoesNotThrow(() -> ctx.applyGlobalOptions(cmdLine));
    }
  }
}
