/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ICommand;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Unit tests for {@link CompletionScriptGenerator}.
 */
class CompletionScriptGeneratorTest {

  /**
   * Test enum for format completion.
   */
  enum TestFormat {
    XML,
    JSON,
    YAML
  }

  @BeforeAll
  static void setup() {
    // Register test format enum
    CompletionTypeRegistry.registerEnum(TestFormat.class);
  }

  @Test
  void testBashIncludesAllCommands() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand(), new MockConvertCommand()));

    String bash = generator.generateBashCompletion();

    assertTrue(bash.contains("validate"), "Bash script should include 'validate' command");
    assertTrue(bash.contains("convert"), "Bash script should include 'convert' command");
  }

  @Test
  void testZshIncludesAllCommands() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand(), new MockConvertCommand()));

    String zsh = generator.generateZshCompletion();

    assertTrue(zsh.contains("validate"), "Zsh script should include 'validate' command");
    assertTrue(zsh.contains("convert"), "Zsh script should include 'convert' command");
  }

  @Test
  void testBashIncludesOptions() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String bash = generator.generateBashCompletion();

    assertTrue(bash.contains("--metaschema"), "Bash script should include '--metaschema' option");
    assertTrue(bash.contains("--as"), "Bash script should include '--as' option");
  }

  @Test
  void testZshIncludesOptions() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String zsh = generator.generateZshCompletion();

    assertTrue(zsh.contains("--metaschema"), "Zsh script should include '--metaschema' option");
    assertTrue(zsh.contains("--as"), "Zsh script should include '--as' option");
  }

  @Test
  void testBashFileTypeCompletion() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String bash = generator.generateBashCompletion();

    assertTrue(bash.contains("_filedir"), "Bash script should use _filedir for File type");
  }

  @Test
  void testZshFileTypeCompletion() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String zsh = generator.generateZshCompletion();

    assertTrue(zsh.contains("_files"), "Zsh script should use _files for File type");
  }

  @Test
  void testBashEnumCompletion() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String bash = generator.generateBashCompletion();

    assertTrue(bash.contains("compgen -W \"xml json yaml\""),
        "Bash script should include enum values for format option");
  }

  @Test
  void testZshEnumCompletion() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String zsh = generator.generateZshCompletion();

    assertTrue(zsh.contains("(xml json yaml)"),
        "Zsh script should include enum values for format option");
  }

  @Test
  void testBashHeader() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String bash = generator.generateBashCompletion();

    assertTrue(bash.startsWith("# Bash completion for test-cli"),
        "Bash script should have proper header");
    assertTrue(bash.contains("complete -F _test_cli test-cli"),
        "Bash script should register completion function");
  }

  @Test
  void testBashFallbackFunctions() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String bash = generator.generateBashCompletion();

    assertTrue(bash.contains("if ! type _init_completion"),
        "Bash script should include _init_completion fallback check");
    assertTrue(bash.contains("if ! type _filedir"),
        "Bash script should include _filedir fallback check");
    assertTrue(bash.contains("cur=\"${COMP_WORDS[COMP_CWORD]}\""),
        "Bash script should include fallback implementation");
  }

  @Test
  void testZshHeader() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String zsh = generator.generateZshCompletion();

    assertTrue(zsh.startsWith("#compdef test-cli"),
        "Zsh script should start with #compdef directive");
  }

  @Test
  void testBashScriptNotEmpty() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String bash = generator.generateBashCompletion();

    assertFalse(bash.isEmpty(), "Bash script should not be empty");
    assertTrue(bash.length() > 100, "Bash script should have substantial content");
  }

  @Test
  void testZshScriptNotEmpty() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand()));

    String zsh = generator.generateZshCompletion();

    assertFalse(zsh.isEmpty(), "Zsh script should not be empty");
    assertTrue(zsh.length() > 100, "Zsh script should have substantial content");
  }

  @Test
  void testBashExtraArgumentFileCompletion() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockConvertCommand()));

    String bash = generator.generateBashCompletion();

    // Extra arguments with File type should get file completion
    assertTrue(bash.contains("_filedir"), "Bash should provide file completion for extra args");
  }

  @Test
  void testZshCommandDescriptions() {
    CompletionScriptGenerator generator = new CompletionScriptGenerator(
        "test-cli",
        List.of(new MockValidateCommand(), new MockConvertCommand()));

    String zsh = generator.generateZshCompletion();

    assertTrue(zsh.contains("Validate content"),
        "Zsh script should include command descriptions");
    assertTrue(zsh.contains("Convert between formats"),
        "Zsh script should include command descriptions");
  }

  /**
   * Mock validate command for testing.
   */
  private static class MockValidateCommand implements ICommand {
    @Override
    public String getName() {
      return "validate";
    }

    @Override
    public String getDescription() {
      return "Validate content against a metaschema";
    }

    @Override
    public Collection<? extends Option> gatherOptions() {
      return List.of(
          Option.builder("m")
              .longOpt("metaschema")
              .hasArg()
              .argName("FILE")
              .type(File.class)
              .desc("metaschema module")
              .get(),
          Option.builder()
              .longOpt("as")
              .hasArg()
              .argName("FORMAT")
              .type(TestFormat.class)
              .desc("input format")
              .get());
    }

    @Override
    public List<ExtraArgument> getExtraArguments() {
      return List.of(
          ExtraArgument.newInstance("input-file", true, File.class));
    }

    @Override
    public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine commandLine) {
      return null;
    }
  }

  /**
   * Mock convert command for testing.
   */
  private static class MockConvertCommand implements ICommand {
    @Override
    public String getName() {
      return "convert";
    }

    @Override
    public String getDescription() {
      return "Convert between formats";
    }

    @Override
    public Collection<? extends Option> gatherOptions() {
      return List.of(
          Option.builder()
              .longOpt("to")
              .hasArg()
              .argName("FORMAT")
              .type(TestFormat.class)
              .desc("output format")
              .get(),
          Option.builder()
              .longOpt("source")
              .hasArg()
              .argName("URL")
              .type(URI.class)
              .desc("source URL")
              .get());
    }

    @Override
    public List<ExtraArgument> getExtraArguments() {
      return List.of(
          ExtraArgument.newInstance("input-file", true, File.class),
          ExtraArgument.newInstance("output-file", false, File.class));
    }

    @Override
    public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine commandLine) {
      return null;
    }
  }
}
