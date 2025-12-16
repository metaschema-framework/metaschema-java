# Shell Completion Command

## Overview

Add a `shell-completion` command to the `cli-processor` module that dynamically introspects registered commands and generates shell completion scripts for Bash and Zsh. Include an argument type resolver system to provide intelligent completions for different argument types (FILE, FORMAT, URL, etc.).

## Goals

1. Create a `shell-completion` command that generates shell completion scripts
2. Support Bash and Zsh shells (most common Linux/macOS environments)
3. Dynamically introspect all registered commands via `CLIProcessor` API
4. Provide intelligent completion for different argument types
5. Design for reusability across all CLIs built on the metaschema-java framework
6. Enable easy installation of completions for end users

## User Story

As an engineer using a metaschema-java-based CLI tool (oscal-cli, metaschema-cli, etc.), I want tab completions to work in bash and zsh environments so that I can:

- Discover available commands and subcommands
- See available options for each command
- Get file path completion for file arguments
- Get format suggestions for format arguments
- Make more effective use of the tool without memorizing all commands

## Design

### Type-Based Completion Approach

This design leverages Apache Commons CLI's `Option.type()` method to declare completion types programmatically. Instead of inferring completion behavior from `argName` strings, we use Java types directly:

- **`File.class`** → File path completion
- **`URI.class` / `URL.class`** → URL completion
- **Custom `ICompletionType` implementations** → Enumerated value completion (e.g., formats)

This approach provides:
- **Type safety** at compile time
- **Explicit declaration** of completion behavior
- **Extensibility** via custom completion types
- **Reusability** across all CLIs built on cli-processor

### New Interfaces and Classes

```
cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/
├── command/
│   └── ShellCompletionCommand.java
└── completion/
    ├── ICompletionType.java
    ├── Format.java
    └── CompletionScriptGenerator.java
```

### ICompletionType Interface

```java
/**
 * Marker interface for types that provide shell completion information.
 * Implement this interface on enums or classes to define custom completion
 * behavior for command-line options.
 */
public interface ICompletionType {
    /**
     * Generate Bash completion code for this type.
     * @return bash completion snippet, or empty string for freeform input
     */
    @NonNull
    String getBashCompletion();

    /**
     * Generate Zsh completion code for this type.
     * @return zsh completion snippet, or empty string for freeform input
     */
    @NonNull
    String getZshCompletion();
}
```

### Format Enum (Example ICompletionType)

```java
/**
 * Content format options with shell completion support.
 */
public enum Format implements ICompletionType {
    XML("xml"),
    JSON("json"),
    YAML("yaml");

    private final String name;

    Format(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getBashCompletion() {
        return "compgen -W \"" + getCompletionValues() + "\"";
    }

    @Override
    public String getZshCompletion() {
        return "(" + getCompletionValues() + ")";
    }

    private static String getCompletionValues() {
        return Arrays.stream(values())
            .map(Format::getName)
            .collect(Collectors.joining(" "));
    }
}
```

### Built-in Type Completion Mapping

| Java Type | Bash Completion | Zsh Completion |
|-----------|-----------------|----------------|
| `File.class` | `_filedir` | `_files` |
| `URI.class` / `URL.class` | (freeform) | `_urls` |
| `Format.class` | `compgen -W "xml json yaml"` | `(xml json yaml)` |
| `String.class` / `null` | (freeform) | (freeform) |
| `ICompletionType` impl | from interface | from interface |

### ShellCompletionCommand

```java
public class ShellCompletionCommand extends AbstractTerminalCommand {
    private static final String COMMAND = "shell-completion";

    public enum Shell {
        BASH,
        ZSH
    }

    private static final List<ExtraArgument> EXTRA_ARGUMENTS = List.of(
        ExtraArgument.newInstance("shell", true)  // Required: bash or zsh
    );

    private static final Option TO_OPTION = Option.builder()
        .longOpt("to")
        .hasArg()
        .argName("FILE")
        .desc("write completion script to this file instead of stdout")
        .build();

    @Override
    public String getName() {
        return COMMAND;
    }

    @Override
    public String getDescription() {
        return "Generate shell completion script for bash or zsh";
    }
}
```

### CompletionScriptGenerator

```java
public class CompletionScriptGenerator {
    private final String programName;
    private final List<ICommand> commands;

    public CompletionScriptGenerator(
            @NonNull String programName,
            @NonNull List<ICommand> commands);

    @NonNull
    public String generateBashCompletion();

    @NonNull
    public String generateZshCompletion();

    // Internal helpers
    private void visitCommand(ICommand cmd, List<String> path);
    private List<String> collectOptions(ICommand cmd);
    private List<String> collectSubcommands(ICommand cmd);

    /**
     * Get completion code for an option based on its type.
     */
    private String getCompletionForOption(Option option, Shell shell) {
        Class<?> type = option.getType();

        if (type == null || String.class.equals(type)) {
            return "";  // freeform
        } else if (File.class.isAssignableFrom(type)) {
            return shell == Shell.BASH ? "_filedir" : "_files";
        } else if (URI.class.isAssignableFrom(type) || URL.class.isAssignableFrom(type)) {
            return shell == Shell.BASH ? "" : "_urls";
        } else if (ICompletionType.class.isAssignableFrom(type)) {
            ICompletionType instance = getCompletionTypeInstance(type);
            return shell == Shell.BASH ? instance.getBashCompletion() : instance.getZshCompletion();
        }
        return "";
    }
}
```

## Command Introspection API

The generator will use these existing CLIProcessor/ICommand methods:

```java
// From CLIProcessor
List<ICommand> getTopLevelCommands()
Map<String, ICommand> getTopLevelCommandsByName()

// From ICommand
String getName()
String getDescription()
Collection<? extends Option> gatherOptions()
List<ExtraArgument> getExtraArguments()
Collection<ICommand> getSubCommands()
boolean isSubCommandRequired()

// From Option (Apache Commons CLI)
String getLongOpt()
String getOpt()
Class<?> getType()       // KEY: Used to determine completion type
boolean hasArg()
boolean isRequired()
String getDescription()

// From ExtraArgument
String getName()
boolean isRequired()
```

## Option Type Migration

Existing CLI commands should be updated to use `.type()` for completion support:

### Before (argName-based)

```java
Option.builder("m")
    .hasArg()
    .argName("FILE_OR_URL")
    .desc("metaschema resource")
    .get()
```

### After (type-based)

```java
Option.builder("m")
    .hasArg()
    .argName("FILE_OR_URL")  // Keep for help text
    .type(File.class)        // Add for completion
    .desc("metaschema resource")
    .get()
```

### Type Mapping Guide

| Current argName | Recommended Type | Notes |
|-----------------|------------------|-------|
| `FILE_OR_URL` | `File.class` | File completion covers most use cases |
| `FILE` | `File.class` | Direct mapping |
| `FORMAT` | `Format.class` | Custom enum with completion |
| `URL` | `URI.class` | Use standard Java type |
| `EXPRESSION` | `String.class` | Freeform input |

## Generated Script Examples

### Bash Completion

```bash
_metaschema_cli() {
    local cur prev words cword
    _init_completion || return

    local commands="validate convert generate-schema shell-completion"

    case "${words[1]}" in
        validate)
            if [[ "$cur" == -* ]]; then
                COMPREPLY=($(compgen -W "--help --metaschema --as --overwrite" -- "$cur"))
            else
                _filedir
            fi
            ;;
        convert)
            if [[ "$cur" == -* ]]; then
                COMPREPLY=($(compgen -W "--help --to --overwrite" -- "$cur"))
            elif [[ "$prev" == "--to" ]]; then
                COMPREPLY=($(compgen -W "xml json yaml" -- "$cur"))
            else
                _filedir
            fi
            ;;
        shell-completion)
            if [[ "$cur" == -* ]]; then
                COMPREPLY=($(compgen -W "--help --to" -- "$cur"))
            else
                COMPREPLY=($(compgen -W "bash zsh" -- "$cur"))
            fi
            ;;
        *)
            COMPREPLY=($(compgen -W "$commands" -- "$cur"))
            ;;
    esac
}

complete -F _metaschema_cli metaschema-cli
```

### Zsh Completion

```zsh
#compdef metaschema-cli

_metaschema-cli() {
    local -a commands
    commands=(
        'validate:Validate content against a metaschema'
        'convert:Convert between formats'
        'generate-schema:Generate XML or JSON schema'
        'shell-completion:Generate shell completion script'
    )

    _arguments -C \
        '1:command:->command' \
        '*::arg:->args'

    case $state in
        command)
            _describe 'command' commands
            ;;
        args)
            case $words[1] in
                validate)
                    _arguments \
                        '--help[Show help]' \
                        '--metaschema[Metaschema module]:file:_files' \
                        '--as[Input format]:format:(xml json yaml)' \
                        '*:file:_files'
                    ;;
                convert)
                    _arguments \
                        '--help[Show help]' \
                        '--to[Output format]:format:(xml json yaml)' \
                        '--overwrite[Overwrite existing file]' \
                        '*:file:_files'
                    ;;
                shell-completion)
                    _arguments \
                        '--help[Show help]' \
                        '--to[Output file]:file:_files' \
                        '1:shell:(bash zsh)'
                    ;;
            esac
            ;;
    esac
}

_metaschema-cli "$@"
```

## Usage

```bash
# Generate and install bash completion
metaschema-cli shell-completion bash > ~/.local/share/bash-completion/completions/metaschema-cli
source ~/.local/share/bash-completion/completions/metaschema-cli

# Generate and install zsh completion
metaschema-cli shell-completion zsh > ~/.zfunc/_metaschema-cli
# Add to .zshrc: fpath=(~/.zfunc $fpath); autoload -Uz compinit; compinit

# Write directly to file
metaschema-cli shell-completion bash --to /etc/bash_completion.d/metaschema-cli
```

## Testing Strategy

### Unit Tests

1. Test `CompletionScriptGenerator` produces valid bash syntax
2. Test `CompletionScriptGenerator` produces valid zsh syntax
3. Test argument type resolvers return correct completion code
4. Test command introspection traverses full hierarchy
5. Test option and argument collection

### Integration Tests

1. `shell-completion bash` exits OK and produces output
2. `shell-completion zsh` exits OK and produces output
3. `shell-completion invalid` exits with error
4. Generated scripts are syntactically valid (`bash -n`, `zsh -n`)

## File Changes Summary

### New Files

```
cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/
├── command/
│   └── ShellCompletionCommand.java
└── completion/
    ├── ICompletionType.java
    ├── Format.java
    └── CompletionScriptGenerator.java

cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/
└── completion/
    ├── CompletionScriptGeneratorTest.java
    └── FormatTest.java
```

### Modified Files (Option Type Migration)

Commands that define options with arguments should add `.type()` calls:

```
metaschema-cli/src/main/java/gov/nist/secauto/metaschema/cli/commands/
├── MetaschemaCommands.java          // Shared options
├── AbstractValidateContentCommand.java
├── GenerateSchemaCommand.java
└── (other command classes)
```

### Consumer Updates (e.g., oscal-cli)

After this feature is released, consuming CLIs can:

1. Register `ShellCompletionCommand` in their CLI entry point
2. The command will automatically introspect all registered commands
3. Generate completion scripts specific to that CLI

## Related Issues

- metaschema-framework/oscal-cli#85
- usnistgov/oscal-cli#162
