# Shell Completion Implementation Plan

## Phase 1: Core Infrastructure

### Task 1.1: Create ICompletionType Interface

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/completion/ICompletionType.java`

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Marker interface for types that provide shell completion information.
 * <p>
 * Implement this interface on enums or classes to define custom completion
 * behavior for command-line options. The completion generator will use
 * {@link org.apache.commons.cli.Option#getType()} to determine the
 * completion type for each option.
 */
public interface ICompletionType {
    /**
     * Generate Bash completion code for this type.
     *
     * @return bash completion snippet, or empty string for freeform input
     */
    @NonNull
    String getBashCompletion();

    /**
     * Generate Zsh completion code for this type.
     *
     * @return zsh completion snippet, or empty string for freeform input
     */
    @NonNull
    String getZshCompletion();
}
```

**Verification:** Compiles without errors.

---

### Task 1.2: Create Format Enum

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/completion/Format.java`

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import java.util.Arrays;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Content format options with shell completion support.
 */
public enum Format implements ICompletionType {
    XML("xml"),
    JSON("json"),
    YAML("yaml");

    @NonNull
    private final String name;

    Format(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public String getBashCompletion() {
        return "compgen -W \"" + getCompletionValues() + "\"";
    }

    @Override
    @NonNull
    public String getZshCompletion() {
        return "(" + getCompletionValues() + ")";
    }

    @NonNull
    private static String getCompletionValues() {
        return Arrays.stream(values())
            .map(Format::getName)
            .collect(Collectors.joining(" "));
    }
}
```

**Verification:** Unit test for completion methods returns expected values.

---

### Task 1.3: Create CompletionScriptGenerator

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/completion/CompletionScriptGenerator.java`

Key methods:

```java
public CompletionScriptGenerator(
    @NonNull String programName,
    @NonNull List<ICommand> commands);

@NonNull
public String generateBashCompletion();

@NonNull
public String generateZshCompletion();

// Type-based completion lookup
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
```

Implementation approach:

1. **Constructor:** Store program name and commands list
2. **generateBashCompletion():**
   - Generate function header `_<program_name>()`
   - Generate case statement for each top-level command
   - Use `Option.getType()` to determine completion behavior
   - Recursively handle subcommands
   - Generate `complete -F` registration
3. **generateZshCompletion():**
   - Generate `#compdef` header
   - Generate command descriptions array
   - Generate `_arguments` for each command path
   - Use `Option.getType()` for completion specifiers
   - Handle subcommand context switching
4. **getCompletionForOption():**
   - Check `Option.getType()` for completion type
   - Handle built-in types: `File.class`, `URI.class`, `URL.class`
   - Handle custom `ICompletionType` implementations
   - Return empty string for freeform input

**Verification:**
- Unit test generates syntactically valid bash (test with `bash -n`)
- Unit test generates syntactically valid zsh (test with `zsh -n`)

---

## Phase 2: Command Implementation

### Task 2.1: Create ShellCompletionCommand

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/command/ShellCompletionCommand.java`

```java
public class ShellCompletionCommand extends AbstractTerminalCommand {
    private static final String COMMAND = "shell-completion";

    public enum Shell {
        BASH("bash"),
        ZSH("zsh");

        private final String name;

        Shell(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Shell fromString(String value) {
            for (Shell shell : values()) {
                if (shell.name.equalsIgnoreCase(value)) {
                    return shell;
                }
            }
            throw new IllegalArgumentException(
                "Unknown shell: " + value + ". Supported: bash, zsh");
        }
    }
}
```

Options:
- `--to FILE` - Output file (optional, defaults to stdout)

Extra arguments:
- `shell` (required) - `bash` or `zsh`

Execution flow:
1. Parse shell type from extra argument
2. Get CLIProcessor from CallingContext
3. Get top-level commands via `processor.getTopLevelCommands()`
4. Create `CompletionScriptGenerator` with program name and commands
5. Generate appropriate script based on shell type
6. Write to file or stdout

**Verification:**
- `shell-completion bash` produces bash script
- `shell-completion zsh` produces zsh script
- `shell-completion --to output.bash bash` writes to file
- `shell-completion invalid` exits with error

---

### Task 2.2: Expose CLIProcessor Access

The `ShellCompletionCommand` needs access to the `CLIProcessor` to introspect commands. Check if `CallingContext` provides this access, otherwise:

**Option A:** Add `getCLIProcessor()` method to `CallingContext`

**Option B:** Pass commands list as constructor parameter to `ShellCompletionCommand`

**Option C:** Use `CommandService` SPI to auto-discover

Investigate existing patterns and choose the simplest approach.

---

## Phase 3: Testing

### Task 3.1: Unit Tests for ICompletionType

**File:** `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/completion/FormatTest.java`

Test cases:
- `testBashCompletion()` - Returns `compgen -W "xml json yaml"`
- `testZshCompletion()` - Returns `(xml json yaml)`
- `testAllValuesIncluded()` - All enum values appear in completion

---

### Task 3.2: Unit Tests for Generator

**File:** `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/completion/CompletionScriptGeneratorTest.java`

Test cases:
- `testBashSyntax()` - Generated script passes `bash -n`
- `testZshSyntax()` - Generated script passes `zsh -n`
- `testIncludesAllCommands()` - All registered commands appear
- `testIncludesSubcommands()` - Parent command subcommands appear
- `testIncludesOptions()` - Command options appear in completion
- `testFileTypeCompletion()` - Options with `File.class` get file completion
- `testUriTypeCompletion()` - Options with `URI.class` get URL completion
- `testCustomCompletionType()` - Options with `ICompletionType` use custom completion

---

### Task 3.3: Integration Tests

**File:** `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/command/ShellCompletionCommandTest.java`

Test cases:
- `testBashGeneration()` - Exit OK, non-empty output
- `testZshGeneration()` - Exit OK, non-empty output
- `testInvalidShell()` - Exit with error
- `testHelpOption()` - Shows help text
- `testToOption()` - Writes to specified file

---

## Phase 4: Documentation

### Task 4.1: Update README

Add section on shell completion:

```markdown
## Shell Completion

Generate completion scripts for your shell:

### Bash
```bash
your-cli shell-completion bash > ~/.local/share/bash-completion/completions/your-cli
source ~/.local/share/bash-completion/completions/your-cli
```

### Zsh
```bash
your-cli shell-completion zsh > ~/.zfunc/_your-cli
# Add to .zshrc: fpath=(~/.zfunc $fpath); autoload -Uz compinit; compinit
```
```

---

## Phase 5: Option Type Migration

### Task 5.1: Add Types to Existing Options

Update existing option definitions in `metaschema-cli` to include `.type()` calls:

**File:** `metaschema-cli/src/main/java/gov/nist/secauto/metaschema/cli/commands/MetaschemaCommands.java`

```java
// Before
public static final Option METASCHEMA_REQUIRED_OPTION = ObjectUtils.notNull(
    Option.builder("m")
        .hasArg()
        .argName("FILE_OR_URL")
        .required()
        .desc("metaschema resource")
        .numberOfArgs(1)
        .get());

// After
public static final Option METASCHEMA_REQUIRED_OPTION = ObjectUtils.notNull(
    Option.builder("m")
        .hasArg()
        .argName("FILE_OR_URL")
        .type(File.class)           // Added for completion
        .required()
        .desc("metaschema resource")
        .numberOfArgs(1)
        .get());
```

**Files to update:**
- `MetaschemaCommands.java` - Shared options (METASCHEMA_*, TO_OPTION, AS_FORMAT_OPTION, etc.)
- `AbstractValidateContentCommand.java` - CONSTRAINTS_OPTION, SARIF_OUTPUT_FILE_OPTION
- `GenerateSchemaCommand.java` - Any command-specific options
- Other command classes with file/format options

**Type assignments:**
| Option | Type |
|--------|------|
| METASCHEMA_REQUIRED_OPTION | `File.class` |
| METASCHEMA_OPTIONAL_OPTION | `File.class` |
| TO_OPTION | `Format.class` |
| AS_FORMAT_OPTION | `Format.class` |
| AS_SCHEMA_FORMAT_OPTION | Custom schema format enum |
| CONSTRAINTS_OPTION | `URI.class` |
| SARIF_OUTPUT_FILE_OPTION | `File.class` |

**Verification:** Options compile and existing tests pass.

---

## Implementation Order

1. **Task 1.1** - ICompletionType interface
2. **Task 1.2** - Format enum
3. **Task 3.1** - Unit tests for Format
4. **Task 1.3** - CompletionScriptGenerator
5. **Task 3.2** - Unit tests for generator
6. **Task 2.2** - Investigate CLIProcessor access
7. **Task 2.1** - ShellCompletionCommand
8. **Task 3.3** - Integration tests
9. **Task 5.1** - Add types to existing options
10. **Task 4.1** - Documentation

---

## Acceptance Criteria

- [ ] `ICompletionType` interface created with bash/zsh completion methods
- [ ] `Format` enum implements `ICompletionType` with xml/json/yaml values
- [ ] `CompletionScriptGenerator` uses `Option.getType()` for completion lookup
- [ ] `shell-completion bash` generates valid bash completion script
- [ ] `shell-completion zsh` generates valid zsh completion script
- [ ] All registered commands appear in generated completions
- [ ] Subcommands appear for parent commands
- [ ] Options with `File.class` type get file path completion
- [ ] Options with `Format.class` type get format value completion
- [ ] Options with `URI.class` type get URL completion
- [ ] Options with custom `ICompletionType` use custom completion
- [ ] Existing options in `metaschema-cli` updated with `.type()` calls
- [ ] Generated scripts pass syntax validation (`bash -n`, `zsh -n`)
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Documentation updated
