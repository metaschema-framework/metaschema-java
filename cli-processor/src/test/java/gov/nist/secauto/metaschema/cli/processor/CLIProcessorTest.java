/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
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

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertAll(
          () -> assertEquals(ExitCode.OK, status.getExitCode()),
          () -> assertTrue(output.contains("test-cli"), "Output should contain 'test-cli'"),
          () -> assertTrue(output.contains("1.0.0-test"), "Output should contain '1.0.0-test'"));
    }

    @Test
    @DisplayName("--help shows help and returns OK")
    void testHelpOption() {
      ExitStatus status = processor.process("--help");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertAll(
          () -> assertEquals(ExitCode.OK, status.getExitCode()),
          () -> assertTrue(output.contains("--help"), "Output should contain '--help'"));
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

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertAll(
          () -> assertEquals(ExitCode.INVALID_COMMAND, status.getExitCode()),
          () -> assertTrue(output.contains("--help"), "Output should contain '--help'"));
    }
  }
}
