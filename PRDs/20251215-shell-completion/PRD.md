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
    ├── CompletionTypeRegistry.java
    └── CompletionScriptGenerator.java
```

### ICompletionType Interface

```java
/**
 * Provides shell completion code for a specific type.
 * <p>
 * Instances are registered with {@link CompletionTypeRegistry} and looked up
 * by the type class specified in {@link Option#getType()} or
 * {@link ExtraArgument#getType()}.
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

### CompletionTypeRegistry

```java
/**
 * Registry for mapping Java types to their shell completion behavior.
 * <p>
 * This decouples type classes from completion logic, allowing any type to
 * have completion behavior registered without implementing an interface.
 */
public final class CompletionTypeRegistry {
    private static final Map<Class<?>, ICompletionType> REGISTRY = new ConcurrentHashMap<>();

    // Pre-register built-in types
    static {
        register(File.class, new FileCompletionType());
        register(URI.class, new UriCompletionType());
        register(URL.class, new UriCompletionType());
    }

    private CompletionTypeRegistry() {
        // static utility class
    }

    /**
     * Register a completion type for a class.
     *
     * @param type the type class to register
     * @param completion the completion behavior for that type
     */
    public static void register(@NonNull Class<?> type, @NonNull ICompletionType completion) {
        REGISTRY.put(type, completion);
    }

    /**
     * Register an enum type using its constant names as completion values.
     * <p>
     * Enum names are converted to lowercase for completion.
     *
     * @param <E> the enum type
     * @param enumClass the enum class to register
     */
    public static <E extends Enum<E>> void registerEnum(@NonNull Class<E> enumClass) {
        register(enumClass, forEnum(enumClass));
    }

    /**
     * Create a completion type for an enum using its constant names.
     *
     * @param <E> the enum type
     * @param enumClass the enum class
     * @return completion type that offers enum values
     */
    @NonNull
    public static <E extends Enum<E>> ICompletionType forEnum(@NonNull Class<E> enumClass) {
        String values = Arrays.stream(enumClass.getEnumConstants())
            .map(Enum::name)
            .map(String::toLowerCase)
            .collect(Collectors.joining(" "));

        return new ICompletionType() {
            @Override
            public String getBashCompletion() {
                return "compgen -W \"" + values + "\"";
            }

            @Override
            public String getZshCompletion() {
                return "(" + values + ")";
            }
        };
    }

    /**
     * Lookup the completion type for a class.
     *
     * @param type the type class to lookup
     * @return the registered completion type, or {@code null} if none registered
     */
    @Nullable
    public static ICompletionType lookup(@Nullable Class<?> type) {
        return type == null ? null : REGISTRY.get(type);
    }
}
```

### Registration Example

```java
// In metaschema-cli initialization (e.g., MetaschemaCommands static block)
// Format enum doesn't need to implement any interface
public enum Format { XML, JSON, YAML }

// Register completion for the enum
static {
    CompletionTypeRegistry.registerEnum(Format.class);
}
```

### Built-in Type Completion Mapping

Pre-registered types in `CompletionTypeRegistry`:

| Java Type | Bash Completion | Zsh Completion |
|-----------|-----------------|----------------|
| `File.class` | `_filedir` | `_files` |
| `URI.class` | (freeform) | `_urls` |
| `URL.class` | (freeform) | `_urls` |

Types requiring explicit registration:

| Java Type | Registration | Completion |
|-----------|--------------|------------|
| Enum types | `registerEnum(MyEnum.class)` | enum values (lowercase) |
| Custom types | `register(type, completion)` | custom behavior |
| `String.class` / `null` | (none needed) | freeform |

### ExtraArgument Extension

The `ExtraArgument` interface will be extended to support type-based completion, matching the approach used for `Option`:

```java
public interface ExtraArgument {
    // Existing methods...
    String getName();
    boolean isRequired();
    int getNumber();

    /**
     * Get the type for completion purposes.
     *
     * @return the type class used to determine shell completion behavior,
     *         or {@code null} for freeform input
     */
    @Nullable
    default Class<?> getType() {
        return null;  // default: freeform (backward compatible)
    }

    // New factory method with type
    @NonNull
    static ExtraArgument newInstance(@NonNull String name, boolean required, @Nullable Class<?> type) {
        return new DefaultExtraArgument(name, required, type);
    }
}
```

This allows extra arguments to specify completion types:

```java
// Before
ExtraArgument.newInstance("metaschema-module-file-or-URL", true)

// After
ExtraArgument.newInstance("metaschema-module-file-or-URL", true, File.class)
```

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
        ICompletionType completion = CompletionTypeRegistry.lookup(type);

        if (completion != null) {
            return shell == Shell.BASH ? completion.getBashCompletion() : completion.getZshCompletion();
        }
        return "";  // freeform for unregistered types
    }

    /**
     * Get completion code for an extra argument based on its type.
     */
    private String getCompletionForExtraArgument(ExtraArgument arg, Shell shell) {
        Class<?> type = arg.getType();
        ICompletionType completion = CompletionTypeRegistry.lookup(type);

        if (completion != null) {
            return shell == Shell.BASH ? completion.getBashCompletion() : completion.getZshCompletion();
        }
        return "";  // freeform for unregistered types
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
Class<?> getType()   // NEW: Used to determine completion type
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
    .type(URL.class)        // Add for completion
    .desc("metaschema resource")
    .get()
```

### Type Mapping Guide

| Current argName | Recommended Type | Notes |
|-----------------|------------------|-------|
| `FILE_OR_URL` | `URL.class` | File completion covers most use cases |
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

### Command Syntax

```bash
metaschema-cli shell-completion <shell> [--to <file>]
```

**Arguments:**
- `<shell>` - Required. The shell type: `bash` or `zsh`

**Options:**
- `--to <file>` - Optional. Write completion script to this file instead of stdout

**File Handling Behavior (`--to` option):**
- Paths are resolved relative to the current working directory
- Parent directories must exist; the command does not create intermediate directories
- Existing files are overwritten without prompting
- On write failure (invalid path, permission denied, disk full), the command exits with `IO_ERROR` exit code and prints an error message
- Uses UTF-8 encoding for output

### Installing Bash Completions

**Option 1: User-local installation (recommended)**

```bash
# Create the completion directory if it doesn't exist
mkdir -p ~/.local/share/bash-completion/completions

# Generate and save the completion script
metaschema-cli shell-completion bash > ~/.local/share/bash-completion/completions/metaschema-cli

# Load completions in your current shell
source ~/.local/share/bash-completion/completions/metaschema-cli
```

Completions will be automatically loaded in new terminal sessions.

**Option 2: System-wide installation (requires root)**

```bash
# Generate and install for all users
sudo metaschema-cli shell-completion bash --to /etc/bash_completion.d/metaschema-cli

# Reload completions
source /etc/bash_completion.d/metaschema-cli
```

### Installing Zsh Completions

**Step 1: Create a completion directory**

```bash
mkdir -p ~/.zfunc
```

**Step 2: Generate the completion script**

```bash
metaschema-cli shell-completion zsh > ~/.zfunc/_metaschema-cli
```

**Step 3: Configure Zsh to load completions**

Add these lines to your `~/.zshrc` if not already present:

```bash
# Add custom completion directory to fpath
fpath=(~/.zfunc $fpath)

# Initialize completion system
autoload -Uz compinit && compinit
```

**Step 4: Reload your shell**

```bash
# Either restart your terminal or run:
source ~/.zshrc
```

### Verifying Installation

After installation, test that completions work:

```bash
# Type the command and press Tab twice
metaschema-cli <Tab><Tab>
# Should show available commands: validate, convert, generate-schema, etc.

metaschema-cli validate --<Tab><Tab>
# Should show available options: --help, --metaschema, --as, etc.
```

### Updating Completions

When upgrading to a new version of the CLI, regenerate the completion script to get completions for any new commands or options:

```bash
# Bash
metaschema-cli shell-completion bash > ~/.local/share/bash-completion/completions/metaschema-cli

# Zsh
metaschema-cli shell-completion zsh > ~/.zfunc/_metaschema-cli
compinit  # Rebuild completion cache
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
    ├── CompletionTypeRegistry.java
    └── CompletionScriptGenerator.java

cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/
└── completion/
    ├── CompletionTypeRegistryTest.java
    └── CompletionScriptGeneratorTest.java
```

### Modified Files

**ExtraArgument extension (cli-processor):**

```
cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/command/
├── ExtraArgument.java               // Add getType() method and factory overload
└── impl/
    └── DefaultExtraArgument.java    // Add type field and constructor
```

**Option and ExtraArgument type migration (metaschema-cli):**

Commands that define options and extra arguments should add type information:

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
