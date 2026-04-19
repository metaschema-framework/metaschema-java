/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor.ansi;

import static dev.metaschema.cli.processor.ansi.Ansi.ansi;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
class AnsiTest {
  private static final String ESC = "\u001B";

  @BeforeEach
  void ensureEnabled() {
    Ansi.setEnabled(true);
  }

  @AfterEach
  void restoreEnabled() {
    Ansi.setEnabled(true);
  }

  @Test
  void emptyBuilderRendersEmpty() {
    assertEquals("", ansi().toString());
  }

  @Test
  void appendStringRendersPlainText() {
    assertEquals("hello", ansi().a("hello").toString());
  }

  @Test
  void appendCharRendersSingleCharacter() {
    assertEquals("x", ansi().a('x').toString());
  }

  @Test
  void formatUsesPrintfSemantics() {
    assertEquals("count=5", ansi().format("count=%d", 5).toString());
  }

  @Test
  void resetEmitsResetSequence() {
    assertEquals(ESC + "[0m", ansi().reset().toString());
  }

  @Test
  void boldEmitsBoldSequence() {
    assertEquals(ESC + "[1m", ansi().bold().toString());
  }

  @Test
  void boldOffEmitsBoldOffSequence() {
    assertEquals(ESC + "[22m", ansi().boldOff().toString());
  }

  @Test
  void fgRedEmitsRedSequence() {
    assertEquals(ESC + "[31m", ansi().fgRed().toString());
  }

  @Test
  void fgBrightRedEmitsBrightRedSequence() {
    assertEquals(ESC + "[91m", ansi().fgBrightRed().toString());
  }

  @Test
  void fgBrightYellowEmitsBrightYellowSequence() {
    assertEquals(ESC + "[93m", ansi().fgBrightYellow().toString());
  }

  @Test
  void fgBrightBlueEmitsBrightBlueSequence() {
    assertEquals(ESC + "[94m", ansi().fgBrightBlue().toString());
  }

  @Test
  void fgBrightCyanEmitsBrightCyanSequence() {
    assertEquals(ESC + "[96m", ansi().fgBrightCyan().toString());
  }

  @Test
  void fgBrightWhiteViaColorEnum() {
    assertEquals(ESC + "[97m", ansi().fgBright(Ansi.Color.WHITE).toString());
  }

  @Test
  void fgBrightMagentaViaColorEnum() {
    assertEquals(ESC + "[95m", ansi().fgBright(Ansi.Color.MAGENTA).toString());
  }

  @Test
  void chainingColorTextReset() {
    assertEquals(
        ESC + "[31mCRITICAL" + ESC + "[0m",
        ansi().fgRed().a("CRITICAL").reset().toString());
  }

  @Test
  void chainingBoldAndText() {
    assertEquals(
        ESC + "[1mname" + ESC + "[22m",
        ansi().bold().a("name").boldOff().toString());
  }

  @Test
  void fluentReassignmentPreservesSingleBuilder() {
    // Mirrors caller pattern: ansi = ansi.format(...)
    Ansi ansi = ansi();
    ansi = ansi.a("a");
    ansi = ansi.format(" %s", "b");
    assertEquals("a b", ansi.toString());
  }

  @Test
  void toStringIsIdempotent() {
    Ansi ansi = ansi().fgRed().a("x").reset();
    String first = ansi.toString();
    String second = ansi.toString();
    assertEquals(first, second);
  }

  @Test
  void preservesLiteralPercentInText() {
    assertEquals("50%", ansi().a("50%").toString());
  }

  @Nested
  class DisabledMode {
    @Test
    void setEnabledFalseSuppressesEscapeCodes() {
      Ansi.setEnabled(false);
      assertEquals(
          "CRITICAL",
          ansi().fgRed().a("CRITICAL").reset().toString());
    }

    @Test
    void setEnabledFalsePreservesPlainText() {
      Ansi.setEnabled(false);
      assertEquals(
          "hello world",
          ansi().a("hello ").bold().a("world").boldOff().toString());
    }

    @Test
    void setEnabledFalseSuppressesFormattedColorOutput() {
      Ansi.setEnabled(false);
      String out = ansi().fgBrightYellow().format("x=%d", 1).reset().toString();
      assertEquals("x=1", out);
      assertTrue(!out.contains(ESC), "No escape sequences expected when disabled");
    }
  }
}
