/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
  @DisplayName("Version Output Tests")
  class VersionOutputTests {

    @Test
    @DisplayName("version output contains app name")
    void testVersionOutputContainsAppName() {
      processor.process("--version");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertTrue(output.contains("test-cli"), "Version output should contain app name");
    }

    @Test
    @DisplayName("version output contains version number")
    void testVersionOutputContainsVersion() {
      processor.process("--version");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertTrue(output.contains("1.0.0-test"), "Version output should contain version number");
    }

    @Test
    @DisplayName("version output contains build timestamp")
    void testVersionOutputContainsBuildTimestamp() {
      processor.process("--version");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertTrue(output.contains("2025-01-01"), "Version output should contain build timestamp");
    }

    @Test
    @DisplayName("version output contains git branch")
    void testVersionOutputContainsGitBranch() {
      processor.process("--version");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertTrue(output.contains("test-branch"), "Version output should contain git branch");
    }

    @Test
    @DisplayName("version output contains git commit")
    void testVersionOutputContainsGitCommit() {
      processor.process("--version");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertTrue(output.contains("abc1234"), "Version output should contain git commit");
    }

    @Test
    @DisplayName("version output contains git origin URL")
    void testVersionOutputContainsGitOriginUrl() {
      processor.process("--version");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertTrue(output.contains("https://example.com/test.git"),
          "Version output should contain git origin URL");
    }

    @Test
    @DisplayName("version output contains descriptive text")
    void testVersionOutputContainsDescriptiveText() {
      processor.process("--version");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertTrue(output.contains("built at"), "Version output should contain 'built at'");
      assertTrue(output.contains("from branch"), "Version output should contain 'from branch'");
    }
  }

  @Nested
  @DisplayName("No-Color Mode Tests")
  class NoColorModeTests {

    @Test
    @DisplayName("--no-color option is accepted with command")
    void testNoColorOptionAccepted() {
      processor.addCommandHandler(new TestCommand());

      ExitStatus status = processor.process("--no-color", "test-cmd");

      assertEquals(ExitCode.OK, status.getExitCode());
    }

    @Test
    @DisplayName("--no-color with --help produces output")
    void testNoColorWithHelp() {
      // Note: --help must come first for phase 1 parsing to recognize it
      ExitStatus status = processor.process("--help", "--no-color");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertAll(
          () -> assertEquals(ExitCode.OK, status.getExitCode()),
          () -> assertTrue(output.contains("--help"), "Output should contain '--help'"));
    }

    @Test
    @DisplayName("--no-color with --version produces output")
    void testNoColorWithVersion() {
      // Note: --version must come first for phase 1 parsing to recognize it
      ExitStatus status = processor.process("--version", "--no-color");

      String output = outputCapture.toString(StandardCharsets.UTF_8);
      assertAll(
          () -> assertEquals(ExitCode.OK, status.getExitCode()),
          () -> assertTrue(output.contains("test-cli"), "Output should contain app name"));
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
