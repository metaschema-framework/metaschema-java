/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor.ansi;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A minimal ANSI escape-code builder used by the CLI to emit colored and
 * formatted terminal output.
 * <p>
 * Emits a subset of
 * <a href= "https://en.wikipedia.org/wiki/ANSI_escape_code#Colors">Select
 * Graphic Rendition</a> codes for foreground colors, bold, and reset. When
 * globally disabled via {@link #setEnabled(boolean)}, escape sequences are
 * suppressed while appended literal text is preserved so output remains
 * readable on terminals that do not interpret ANSI codes.
 */
public final class Ansi {
  private static final String ESC = "\u001B[";
  private static final String RESET_SEQ = ESC + "0m";
  private static final String BOLD_SEQ = ESC + "1m";
  private static final String BOLD_OFF_SEQ = ESC + "22m";

  private static volatile boolean enabled = true;

  @NonNull
  private final StringBuilder buffer = new StringBuilder();

  private Ansi() {
    // use ansi() factory
  }

  /**
   * Create a new builder.
   *
   * @return a fresh builder with empty contents
   */
  @NonNull
  public static Ansi ansi() {
    return new Ansi();
  }

  /**
   * Globally enable or disable emission of ANSI escape codes.
   * <p>
   * When disabled, all color and style methods are no-ops; literal appended text
   * is still emitted.
   *
   * @param enable
   *          {@code true} to emit escape sequences, {@code false} to suppress
   *          them
   */
  public static void setEnabled(boolean enable) {
    enabled = enable;
  }

  /**
   * Indicates whether ANSI escape code emission is currently enabled.
   *
   * @return {@code true} if enabled
   */
  public static boolean isEnabled() {
    return enabled;
  }

  @NonNull
  private Ansi emit(@NonNull String sequence) {
    if (enabled) {
      buffer.append(sequence);
    }
    return this;
  }

  /**
   * Append a single literal character.
   *
   * @param ch
   *          the character to append
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi a(char ch) {
    buffer.append(ch);
    return this;
  }

  /**
   * Append a literal string.
   *
   * @param text
   *          the text to append
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi a(@NonNull CharSequence text) {
    buffer.append(text);
    return this;
  }

  /**
   * Append formatted text using {@link String#format(String, Object...)}
   * semantics.
   *
   * @param format
   *          the format string
   * @param args
   *          the format arguments
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi format(@NonNull String format, Object... args) {
    buffer.append(String.format(format, args));
    return this;
  }

  /**
   * Emit the ANSI reset sequence, clearing any active color or style.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi reset() {
    return emit(RESET_SEQ);
  }

  /**
   * Enable bold rendering for subsequent appended text.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi bold() {
    return emit(BOLD_SEQ);
  }

  /**
   * Disable bold rendering for subsequent appended text.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi boldOff() {
    return emit(BOLD_OFF_SEQ);
  }

  /**
   * Set the foreground color to red.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi fgRed() {
    return emit(ESC + "31m");
  }

  /**
   * Set the foreground color to bright red.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi fgBrightRed() {
    return emit(ESC + "91m");
  }

  /**
   * Set the foreground color to bright yellow.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi fgBrightYellow() {
    return emit(ESC + "93m");
  }

  /**
   * Set the foreground color to bright blue.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi fgBrightBlue() {
    return emit(ESC + "94m");
  }

  /**
   * Set the foreground color to bright cyan.
   *
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi fgBrightCyan() {
    return emit(ESC + "96m");
  }

  /**
   * Set the foreground color to the bright variant of the supplied color.
   *
   * @param color
   *          the base color
   * @return {@code this} for chaining
   */
  @NonNull
  public Ansi fgBright(@NonNull Color color) {
    return emit(ESC + (90 + color.ordinal()) + "m");
  }

  @Override
  public String toString() {
    return buffer.toString();
  }

  /**
   * Standard 8 ANSI foreground colors. Ordinals align with the standard color
   * codes (0-7) so bright variants are derived by adding 90.
   */
  public enum Color {
    /** Black (code 30, bright 90). */
    BLACK,
    /** Red (code 31, bright 91). */
    RED,
    /** Green (code 32, bright 92). */
    GREEN,
    /** Yellow (code 33, bright 93). */
    YELLOW,
    /** Blue (code 34, bright 94). */
    BLUE,
    /** Magenta (code 35, bright 95). */
    MAGENTA,
    /** Cyan (code 36, bright 96). */
    CYAN,
    /** White (code 37, bright 97). */
    WHITE;
  }
}
