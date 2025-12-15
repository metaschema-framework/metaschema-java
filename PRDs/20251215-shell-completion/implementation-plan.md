# Shell Completion Implementation Plan

## Phase 1: Core Infrastructure

### Task 1.1: Create IArgumentTypeResolver Interface

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/completion/IArgumentTypeResolver.java`

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides shell completion code for a specific argument type.
 */
public interface IArgumentTypeResolver {
    /**
     * Get the argument type name this resolver handles.
     *
     * @return the argument type (e.g., "FILE", "FORMAT", "URL")
     */
    @NonNull
    String getArgumentType();

    /**
     * Generate Bash completion code for this argument type.
     *
     * @return bash completion snippet, or empty string if no completion
     */
    @NonNull
    String getBashCompletion();

    /**
     * Generate Zsh completion code for this argument type.
     *
     * @return zsh completion snippet, or empty string if no completion
     */
    @NonNull
    String getZshCompletion();
}
```

**Verification:** Compiles without errors.

---

### Task 1.2: Create ArgumentTypeResolvers Registry

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/completion/ArgumentTypeResolvers.java`

Implement built-in resolvers:

| Resolver | argName | Bash | Zsh |
|----------|---------|------|-----|
| FileResolver | `FILE` | `_filedir` | `_files` |
| FileOrUrlResolver | `FILE_OR_URL` | `_filedir` | `_files` |
| FormatResolver | `FORMAT` | `compgen -W "xml json yaml"` | `(xml json yaml)` |
| UrlResolver | `URL` | (empty) | `_urls` |
| ExpressionResolver | `EXPRESSION` | (empty) | (empty) |

Include:
- Static registry map
- `getResolver(String argName)` lookup method
- `getDefaultResolver()` for unknown types (file completion)

**Verification:** Unit test for each resolver returns expected completion code.

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
```

Implementation approach:

1. **Constructor:** Store program name, commands list, initialize resolver registry
2. **generateBashCompletion():**
   - Generate function header `_<program_name>()`
   - Generate case statement for each top-level command
   - Recursively handle subcommands
   - Generate `complete -F` registration
3. **generateZshCompletion():**
   - Generate `#compdef` header
   - Generate command descriptions array
   - Generate `_arguments` for each command path
   - Handle subcommand context switching

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

### Task 3.1: Unit Tests for Resolvers

**File:** `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/completion/ArgumentTypeResolversTest.java`

Test cases:
- `testFileResolver()` - Returns `_filedir` for bash
- `testFormatResolver()` - Returns format list
- `testUnknownType()` - Falls back to default resolver
- `testNullArgName()` - Handles gracefully

---

### Task 3.2: Unit Tests for Generator

**File:** `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/completion/CompletionScriptGeneratorTest.java`

Test cases:
- `testBashSyntax()` - Generated script passes `bash -n`
- `testZshSyntax()` - Generated script passes `zsh -n`
- `testIncludesAllCommands()` - All registered commands appear
- `testIncludesSubcommands()` - Parent command subcommands appear
- `testIncludesOptions()` - Command options appear in completion

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

## Implementation Order

1. **Task 1.1** - IArgumentTypeResolver interface
2. **Task 1.2** - ArgumentTypeResolvers registry
3. **Task 3.1** - Unit tests for resolvers
4. **Task 1.3** - CompletionScriptGenerator
5. **Task 3.2** - Unit tests for generator
6. **Task 2.2** - Investigate CLIProcessor access
7. **Task 2.1** - ShellCompletionCommand
8. **Task 3.3** - Integration tests
9. **Task 4.1** - Documentation

---

## Acceptance Criteria

- [ ] `shell-completion bash` generates valid bash completion script
- [ ] `shell-completion zsh` generates valid zsh completion script
- [ ] All registered commands appear in generated completions
- [ ] Subcommands appear for parent commands
- [ ] Options with arguments get appropriate type-based completion
- [ ] File arguments get file path completion
- [ ] Format arguments get format value completion
- [ ] Generated scripts pass syntax validation (`bash -n`, `zsh -n`)
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Documentation updated
