/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.cli.processor.command.ShellCompletionCommand.Shell;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ShellCompletionCommand}.
 */
class ShellCompletionCommandTest {

  @Test
  void testCommandName() {
    ShellCompletionCommand command = new ShellCompletionCommand();
    assertEquals("shell-completion", command.getName());
  }

  @Test
  void testCommandDescription() {
    ShellCompletionCommand command = new ShellCompletionCommand();
    assertNotNull(command.getDescription());
    assertFalse(command.getDescription().isEmpty());
  }

  @Test
  void testExtraArguments() {
    ShellCompletionCommand command = new ShellCompletionCommand();
    assertEquals(1, command.getExtraArguments().size());
    assertEquals("shell", command.getExtraArguments().get(0).getName());
    assertTrue(command.getExtraArguments().get(0).isRequired());
  }

  @Test
  void testOptions() {
    ShellCompletionCommand command = new ShellCompletionCommand();
    assertEquals(1, command.gatherOptions().size());
  }

  @Test
  void testShellFromStringBash() {
    assertEquals(Shell.BASH, Shell.fromString("bash"));
    assertEquals(Shell.BASH, Shell.fromString("BASH"));
    assertEquals(Shell.BASH, Shell.fromString("Bash"));
  }

  @Test
  void testShellFromStringZsh() {
    assertEquals(Shell.ZSH, Shell.fromString("zsh"));
    assertEquals(Shell.ZSH, Shell.fromString("ZSH"));
    assertEquals(Shell.ZSH, Shell.fromString("Zsh"));
  }

  @Test
  void testShellFromStringInvalid() {
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> Shell.fromString("fish"));
    assertTrue(ex.getMessage().contains("Unknown shell"));
    assertTrue(ex.getMessage().contains("fish"));
  }

  @Test
  void testShellEnumValues() {
    assertEquals("bash", Shell.BASH.getName());
    assertEquals("zsh", Shell.ZSH.getName());
  }
}
