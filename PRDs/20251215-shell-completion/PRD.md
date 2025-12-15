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

### New Interfaces and Classes

```
cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/
├── command/
│   └── ShellCompletionCommand.java
└── completion/
    ├── IArgumentTypeResolver.java
    ├── ArgumentTypeResolvers.java
    └── CompletionScriptGenerator.java
```

### IArgumentTypeResolver Interface

```java
public interface IArgumentTypeResolver {
    /**
     * Get the argument type name this resolver handles.
     * @return the argument type (e.g., "FILE", "FORMAT", "URL")
     */
    String getArgumentType();

    /**
     * Generate Bash completion code for this argument type.
     * @return bash completion snippet
     */
    String getBashCompletion();

    /**
     * Generate Zsh completion code for this argument type.
     * @return zsh completion snippet
     */
    String getZshCompletion();
}
```

### Built-in Argument Type Resolvers

| Argument Type | Bash Completion | Zsh Completion |
|---------------|-----------------|----------------|
| `FILE` | `_filedir` | `_files` |
| `FILE_OR_URL` | `_filedir` | `_files` |
| `FORMAT` | `compgen -W "xml json yaml"` | `(xml json yaml)` |
| `URL` | (freeform) | `_urls` |
| `EXPRESSION` | (freeform) | (freeform) |
| (none/flag) | N/A | N/A |

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
    private final Map<String, IArgumentTypeResolver> resolvers;

    public CompletionScriptGenerator(
            String programName,
            List<ICommand> commands,
            Map<String, IArgumentTypeResolver> resolvers);

    public String generateBashCompletion();
    public String generateZshCompletion();

    // Internal helpers
    private void visitCommand(ICommand cmd, List<String> path);
    private List<String> collectOptions(ICommand cmd);
    private List<String> collectSubcommands(ICommand cmd);
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
String getArgName()      // KEY: Used to lookup resolver
boolean hasArg()
boolean isRequired()
String getDescription()

// From ExtraArgument
String getName()         // Contains type hint (e.g., "source-file-or-URL")
boolean isRequired()
```

## Argument Type Inventory

Analysis of existing CLI commands reveals these argument types:

### Option argName Values

| argName | Usage | Completion |
|---------|-------|------------|
| `FILE_OR_URL` | Input documents | File paths |
| `FORMAT` | Output format selection | `xml`, `json`, `yaml` |
| `FILE` | Local file paths | File paths |
| `URL` | Web resources | Freeform |
| `EXPRESSION` | Metapath expressions | Freeform |

### ExtraArgument Name Patterns

| Pattern | Inferred Type | Completion |
|---------|---------------|------------|
| `*-file` | FILE | File paths |
| `*-file-or-URL` | FILE_OR_URL | File paths |
| `*URL*` | URL | Freeform |
| `destination*` | FILE | File paths |

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
    ├── IArgumentTypeResolver.java
    ├── ArgumentTypeResolvers.java
    └── CompletionScriptGenerator.java

cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/
└── completion/
    └── CompletionScriptGeneratorTest.java
```

### Consumer Updates (e.g., oscal-cli)

After this feature is released, consuming CLIs can:

1. Register `ShellCompletionCommand` in their CLI entry point
2. The command will automatically introspect all registered commands
3. Generate completion scripts specific to that CLI

## Related Issues

- metaschema-framework/oscal-cli#85
- usnistgov/oscal-cli#162
