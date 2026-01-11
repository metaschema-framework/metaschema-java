/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import dev.metaschema.core.util.ObjectUtils;
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

  @Nested
  @DisplayName("wrapText()")
  class WrapTextTests {

    @Test
    @DisplayName("returns text unchanged when shorter than max width")
    void returnsTextUnchangedWhenShorterThanMaxWidth() {
      String text = "Short text";
      String result = CallingContext.wrapText(text, 80, "    ");
      assertEquals(text, result);
    }

    @Test
    @DisplayName("returns text unchanged when exactly max width")
    void returnsTextUnchangedWhenExactlyMaxWidth() {
      String text = "Exactly twenty chars"; // 20 chars
      String result = CallingContext.wrapText(text, 20, "    ");
      assertEquals(text, result);
    }

    @Test
    @DisplayName("wraps text at word boundary")
    void wrapsTextAtWordBoundary() {
      String text = "This is a long text that should wrap at word boundaries";
      String result = CallingContext.wrapText(text, 30, "    ");

      // Should wrap after "that" (position 26) and continue with indent
      assertTrue(result.contains(System.lineSeparator()));
      assertTrue(result.startsWith("This is a long text that"));
      assertTrue(result.contains("    should wrap"));
    }

    @Test
    @DisplayName("uses correct indentation for continuation lines")
    void usesCorrectIndentationForContinuationLines() {
      String text = "First part of text second part of text third part of text";
      String indent = "        ";
      String result = CallingContext.wrapText(text, 25, indent);

      String[] lines = result.split(System.lineSeparator());
      assertTrue(lines.length > 1, "Expected multiple lines");
      for (int i = 1; i < lines.length; i++) {
        assertTrue(lines[i].startsWith(indent),
            "Line " + i + " should start with indent: [" + lines[i] + "]");
      }
    }

    @Test
    @DisplayName("handles text with no spaces (force break)")
    void handlesTextWithNoSpaces() {
      String text = "ThisIsAVeryLongWordWithNoSpacesAtAll";
      String result = CallingContext.wrapText(text, 15, "  ");

      // Should force break at width since there are no word boundaries
      String[] lines = result.split(System.lineSeparator());
      assertTrue(lines.length > 1, "Expected multiple lines for long word");
    }

    @Test
    @DisplayName("handles empty indent")
    void handlesEmptyIndent() {
      String text = "This text should wrap without indentation on continuation";
      String result = CallingContext.wrapText(text, 20, "");

      assertTrue(result.contains(System.lineSeparator()));
      String[] lines = result.split(System.lineSeparator());
      assertTrue(lines.length > 1);
    }

    @Test
    @DisplayName("handles single word longer than width")
    void handlesSingleWordLongerThanWidth() {
      String text = "Supercalifragilisticexpialidocious";
      String result = CallingContext.wrapText(text, 10, "  ");

      // Should break the word at width boundaries
      String[] lines = result.split(System.lineSeparator());
      assertTrue(lines.length > 1);
    }

    @Test
    @DisplayName("preserves text content after wrapping")
    void preservesTextContentAfterWrapping() {
      String text = "The quick brown fox jumps over the lazy dog";
      String result = CallingContext.wrapText(text, 20, "    ");

      // Remove line separators and indents to verify content preserved
      String normalized = result.replace(System.lineSeparator(), " ").replaceAll("\\s+", " ").trim();
      assertEquals(text, normalized);
    }

    @Test
    @DisplayName("force-break preserves all characters without skipping")
    void forceBreakPreservesAllCharacters() {
      // Test case from CodeRabbit: wrapping "ABCDEFGHIJ" at width 5
      // Should produce "ABCDE" + newline + indent + "FGHIJ" (all characters
      // preserved)
      String text = "ABCDEFGHIJ";
      String result = CallingContext.wrapText(text, 5, "");

      // Remove line separators to get all characters
      String allChars = result.replace(System.lineSeparator(), "");
      assertEquals(text, allChars, "All characters should be preserved after force-break wrapping");
    }

    @Test
    @DisplayName("force-break produces correct line lengths")
    void forceBreakProducesCorrectLineLengths() {
      String text = "ABCDEFGHIJKLMNO"; // 15 chars
      String result = CallingContext.wrapText(text, 5, "");

      String[] lines = result.split(System.lineSeparator());
      assertEquals(3, lines.length, "Should produce 3 lines of 5 chars each");
      assertEquals("ABCDE", lines[0]);
      assertEquals("FGHIJ", lines[1]);
      assertEquals("KLMNO", lines[2]);
    }

    @Test
    @DisplayName("throws IllegalArgumentException when maxWidth is zero")
    void throwsWhenMaxWidthIsZero() {
      assertThrows(IllegalArgumentException.class,
          () -> CallingContext.wrapText("test", 0, ""));
    }

    @Test
    @DisplayName("throws IllegalArgumentException when maxWidth is negative")
    void throwsWhenMaxWidthIsNegative() {
      assertThrows(IllegalArgumentException.class,
          () -> CallingContext.wrapText("test", -5, ""));
    }

    @Test
    @DisplayName("throws IllegalArgumentException when indent >= maxWidth")
    void throwsWhenIndentExceedsMaxWidth() {
      assertThrows(IllegalArgumentException.class,
          () -> CallingContext.wrapText("test", 5, "     ")); // indent length = maxWidth
    }

    @Test
    @DisplayName("throws IllegalArgumentException when indent > maxWidth")
    void throwsWhenIndentGreaterThanMaxWidth() {
      assertThrows(IllegalArgumentException.class,
          () -> CallingContext.wrapText("test", 5, "      ")); // indent length > maxWidth
    }

    @Test
    @DisplayName("handles very narrow width with word boundaries")
    void handlesVeryNarrowWidthWithWordBoundaries() {
      String text = "A B C D E";
      String result = CallingContext.wrapText(text, 3, "");

      // Should wrap at word boundaries where possible
      assertTrue(result.contains(System.lineSeparator()));
      // Verify all characters are preserved
      String allChars = result.replace(System.lineSeparator(), " ").replaceAll("\\s+", " ").trim();
      assertEquals(text, allChars);
    }
  }
}
