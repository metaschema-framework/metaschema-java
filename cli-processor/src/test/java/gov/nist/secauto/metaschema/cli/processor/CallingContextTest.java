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
