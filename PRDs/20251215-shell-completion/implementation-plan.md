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
 * Provides shell completion code for a specific type.
 * <p>
 * Instances are registered with {@link CompletionTypeRegistry} and looked up
 * by the type class specified in {@link org.apache.commons.cli.Option#getType()}
 * or {@link gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument#getType()}.
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

### Task 1.2: Create CompletionTypeRegistry

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/completion/CompletionTypeRegistry.java`

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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

    /**
     * Built-in completion type for file paths.
     */
    private static class FileCompletionType implements ICompletionType {
        @Override
        public String getBashCompletion() {
            return "_filedir";
        }

        @Override
        public String getZshCompletion() {
            return "_files";
        }
    }

    /**
     * Built-in completion type for URIs/URLs.
     */
    private static class UriCompletionType implements ICompletionType {
        @Override
        public String getBashCompletion() {
            return "";  // freeform in bash
        }

        @Override
        public String getZshCompletion() {
            return "_urls";
        }
    }
}
```

**Verification:** Unit tests for register, registerEnum, forEnum, and lookup methods.

---

### Task 1.3: Extend ExtraArgument Interface

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/command/ExtraArgument.java`

Add `getType()` method and new factory overload:

```java
/**
 * Get the type for completion purposes.
 *
 * @return the type class used to determine shell completion behavior,
 *         or {@code null} for freeform input
 */
@Nullable
default Class<?> getType() {
    return null;
}

/**
 * Create a new extra argument instance with type information.
 *
 * @param name
 *          the argument name
 * @param required
 *          {@code true} if the argument is required
 * @param type
 *          the type class for completion, or {@code null} for freeform
 * @return the instance
 */
@NonNull
static ExtraArgument newInstance(@NonNull String name, boolean required, @Nullable Class<?> type) {
    if (name.isBlank()) {
        throw new IllegalArgumentException("name cannot be empty or blank");
    }
    return new DefaultExtraArgument(name, required, type);
}
```

**File:** `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/command/impl/DefaultExtraArgument.java`

Add type field and update constructor:

```java
public class DefaultExtraArgument implements ExtraArgument {
    private final String name;
    private final boolean required;
    @Nullable
    private final Class<?> type;

    public DefaultExtraArgument(@NonNull String name, boolean required) {
        this(name, required, null);
    }

    public DefaultExtraArgument(@NonNull String name, boolean required, @Nullable Class<?> type) {
        this.name = name;
        this.required = required;
        this.type = type;
    }

    @Override
    @Nullable
    public Class<?> getType() {
        return type;
    }
    // ... existing methods
}
```

**Verification:** Existing code compiles without changes (backward compatible).

---

### Task 1.4: Create CompletionScriptGenerator

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

/**
 * Get completion code for an option based on its type.
 * Uses {@link CompletionTypeRegistry} to lookup completion behavior.
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
 * Uses {@link CompletionTypeRegistry} to lookup completion behavior.
 */
private String getCompletionForExtraArgument(ExtraArgument arg, Shell shell) {
    Class<?> type = arg.getType();
    ICompletionType completion = CompletionTypeRegistry.lookup(type);

    if (completion != null) {
        return shell == Shell.BASH ? completion.getBashCompletion() : completion.getZshCompletion();
    }
    return "";  // freeform for unregistered types
}
```

Implementation approach:

1. **Constructor:** Store program name and commands list
2. **generateBashCompletion():**
   - Generate function header `_<program_name>()`
   - Generate case statement for each top-level command
   - Use registry lookup for completion behavior
   - Recursively handle subcommands
   - Generate `complete -F` registration
3. **generateZshCompletion():**
   - Generate `#compdef` header
   - Generate command descriptions array
   - Generate `_arguments` for each command path
   - Use registry lookup for completion specifiers
   - Handle subcommand context switching
4. **getCompletionForOption() / getCompletionForExtraArgument():**
   - Lookup type in `CompletionTypeRegistry`
   - Return completion code from registered `ICompletionType`
   - Return empty string for unregistered types (freeform input)

**Verification:**
- Unit test generates syntactically valid bash (test with `bash -n`)
- Unit test generates syntactically valid zsh (test with `zsh -n`)

---

## Phase 2: Command Implementation

### Task 2.1: Expose CLIProcessor Access

The `ShellCompletionCommand` needs access to the `CLIProcessor` to introspect commands. Check if `CallingContext` provides this access, otherwise:

**Option A:** Add `getCLIProcessor()` method to `CallingContext`

**Option B:** Pass commands list as constructor parameter to `ShellCompletionCommand`

**Option C:** Use `CommandService` SPI to auto-discover

Investigate existing patterns and choose the simplest approach.

---

### Task 2.2: Create ShellCompletionCommand

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

## Phase 3: Testing

### Task 3.1: Unit Tests for CompletionTypeRegistry

**File:** `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/completion/CompletionTypeRegistryTest.java`

Test cases:
- `testLookupFile()` - Returns `FileCompletionType` for `File.class`
- `testLookupUri()` - Returns `UriCompletionType` for `URI.class`
- `testLookupUrl()` - Returns `UriCompletionType` for `URL.class`
- `testLookupNull()` - Returns `null` for `null` type
- `testLookupUnregistered()` - Returns `null` for unregistered type
- `testRegisterCustomType()` - Custom type can be registered and retrieved
- `testRegisterEnum()` - Enum completion uses lowercase constant names
- `testForEnumBashCompletion()` - Returns `compgen -W "val1 val2 ..."`
- `testForEnumZshCompletion()` - Returns `(val1 val2 ...)`
- `testFileCompletionTypeBash()` - Returns `_filedir`
- `testFileCompletionTypeZsh()` - Returns `_files`
- `testUriCompletionTypeBash()` - Returns empty string (freeform)
- `testUriCompletionTypeZsh()` - Returns `_urls`

---

### Task 3.2: Unit Tests for Generator

**File:** `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/completion/CompletionScriptGeneratorTest.java`

Test cases:
- `testBashSyntax()` - Generated script passes `bash -n`
- `testZshSyntax()` - Generated script passes `zsh -n`
- `testIncludesAllCommands()` - All registered commands appear
- `testIncludesSubcommands()` - Parent command subcommands appear
- `testIncludesOptions()` - Command options appear in completion
- `testFileTypeCompletion()` - Options with `File.class` get file completion (via registry)
- `testUriTypeCompletion()` - Options with `URI.class` get URL completion (via registry)
- `testRegisteredEnumCompletion()` - Options with registered enum type use enum values
- `testExtraArgumentFileCompletion()` - Extra arguments with `File.class` get file completion
- `testExtraArgumentUnregisteredType()` - Extra arguments with unregistered type get freeform

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
2. **Task 1.2** - CompletionTypeRegistry (with built-in types and enum helper)
3. **Task 3.1** - Unit tests for CompletionTypeRegistry
4. **Task 1.3** - Extend ExtraArgument interface
5. **Task 1.4** - CompletionScriptGenerator (uses registry lookup)
6. **Task 3.2** - Unit tests for generator
7. **Task 2.1** - Investigate CLIProcessor access
8. **Task 2.2** - ShellCompletionCommand
9. **Task 3.3** - Integration tests
10. **Task 5.1** - Add types to existing options (and register enum completion types)
11. **Task 4.1** - Documentation

---

## Acceptance Criteria

- [ ] `ICompletionType` interface created with bash/zsh completion methods
- [ ] `CompletionTypeRegistry` created with:
  - [ ] Pre-registered built-in types (`File.class`, `URI.class`, `URL.class`)
  - [ ] `register()` method for custom type registration
  - [ ] `registerEnum()` helper for enum types
  - [ ] `forEnum()` factory method for creating enum completion types
  - [ ] `lookup()` method for retrieving completion types
- [ ] `ExtraArgument` interface extended with `getType()` method
- [ ] `CompletionScriptGenerator` uses `CompletionTypeRegistry.lookup()` for completion
- [ ] `shell-completion bash` generates valid bash completion script
- [ ] `shell-completion zsh` generates valid zsh completion script
- [ ] All registered commands appear in generated completions
- [ ] Subcommands appear for parent commands
- [ ] Options with `File.class` type get file path completion (via registry)
- [ ] Options with `URI.class` type get URL completion (via registry)
- [ ] Options with registered enum type get enum value completion
- [ ] Extra arguments with registered types get appropriate completion
- [ ] Existing options in `metaschema-cli` updated with `.type()` calls
- [ ] Format enum types registered with `CompletionTypeRegistry.registerEnum()`
- [ ] Generated scripts pass syntax validation (`bash -n`, `zsh -n`)
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Documentation updated
