/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * Unit tests for {@link CompletionTypeRegistry}.
 */
class CompletionTypeRegistryTest {

  /**
   * Test enum for completion testing.
   */
  enum TestFormat {
    XML,
    JSON,
    YAML
  }

  @Test
  void testLookupFile() {
    ICompletionType completion = CompletionTypeRegistry.lookup(File.class);
    assertNotNull(completion, "File.class should be pre-registered");
  }

  @Test
  void testLookupUri() {
    ICompletionType completion = CompletionTypeRegistry.lookup(URI.class);
    assertNotNull(completion, "URI.class should be pre-registered");
  }

  @Test
  void testLookupUrl() {
    ICompletionType completion = CompletionTypeRegistry.lookup(URL.class);
    assertNotNull(completion, "URL.class should be pre-registered");
  }

  @Test
  void testLookupNull() {
    ICompletionType completion = CompletionTypeRegistry.lookup(null);
    assertNull(completion, "null type should return null");
  }

  @Test
  void testLookupUnregistered() {
    ICompletionType completion = CompletionTypeRegistry.lookup(Integer.class);
    assertNull(completion, "Unregistered type should return null");
  }

  @Test
  void testRegisterCustomType() {
    // Create and register a custom completion type
    ICompletionType custom = new ICompletionType() {
      @Override
      public String getBashCompletion() {
        return "custom-bash";
      }

      @Override
      public String getZshCompletion() {
        return "custom-zsh";
      }
    };

    CompletionTypeRegistry.register(StringBuilder.class, custom);
    ICompletionType retrieved = CompletionTypeRegistry.lookup(StringBuilder.class);

    assertNotNull(retrieved, "Custom type should be retrievable after registration");
    assertEquals("custom-bash", retrieved.getBashCompletion());
    assertEquals("custom-zsh", retrieved.getZshCompletion());
  }

  @Test
  void testRegisterEnum() {
    CompletionTypeRegistry.registerEnum(TestFormat.class);
    ICompletionType completion = CompletionTypeRegistry.lookup(TestFormat.class);

    assertNotNull(completion, "Enum should be registered");
  }

  @Test
  void testForEnumBashCompletion() {
    ICompletionType completion = CompletionTypeRegistry.forEnum(TestFormat.class);
    String bash = completion.getBashCompletion();

    assertEquals("compgen -W \"xml json yaml\"", bash,
        "Bash completion should use compgen with lowercase enum values");
  }

  @Test
  void testForEnumZshCompletion() {
    ICompletionType completion = CompletionTypeRegistry.forEnum(TestFormat.class);
    String zsh = completion.getZshCompletion();

    assertEquals("(xml json yaml)", zsh,
        "Zsh completion should list lowercase enum values in parentheses");
  }

  @Test
  void testFileCompletionTypeBash() {
    ICompletionType completion = CompletionTypeRegistry.lookup(File.class);
    assertNotNull(completion, "File.class should be registered");
    assertEquals("_filedir", completion.getBashCompletion(),
        "File bash completion should use _filedir");
  }

  @Test
  void testFileCompletionTypeZsh() {
    ICompletionType completion = CompletionTypeRegistry.lookup(File.class);
    assertNotNull(completion, "File.class should be registered");
    assertEquals("_files", completion.getZshCompletion(),
        "File zsh completion should use _files");
  }

  @Test
  void testUriCompletionTypeBash() {
    ICompletionType completion = CompletionTypeRegistry.lookup(URI.class);
    assertNotNull(completion, "URI.class should be registered");
    assertEquals("", completion.getBashCompletion(),
        "URI bash completion should be freeform (empty string)");
  }

  @Test
  void testUriCompletionTypeZsh() {
    ICompletionType completion = CompletionTypeRegistry.lookup(URI.class);
    assertNotNull(completion, "URI.class should be registered");
    assertEquals("_urls", completion.getZshCompletion(),
        "URI zsh completion should use _urls");
  }

  @Test
  void testUrlCompletionTypeBash() {
    ICompletionType completion = CompletionTypeRegistry.lookup(URL.class);
    assertNotNull(completion, "URL.class should be registered");
    assertEquals("", completion.getBashCompletion(),
        "URL bash completion should be freeform (empty string)");
  }

  @Test
  void testUrlCompletionTypeZsh() {
    ICompletionType completion = CompletionTypeRegistry.lookup(URL.class);
    assertNotNull(completion, "URL.class should be registered");
    assertEquals("_urls", completion.getZshCompletion(),
        "URL zsh completion should use _urls");
  }
}
