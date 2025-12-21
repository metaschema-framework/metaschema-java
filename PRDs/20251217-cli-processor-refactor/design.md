# Design: Refactor processCommand in CLIProcessor

**Issue:** [#252](https://github.com/metaschema-framework/metaschema-java/issues/252)
**Date:** 2025-12-17
**Status:** Approved
**Depends On:** [PR #551](https://github.com/metaschema-framework/metaschema-java/pull/551) (shell completion)

## Problem Statement

The `processCommand` method in `CLIProcessor.java` has high cyclomatic and NPath complexity, requiring PMD suppressions. The `CallingContext` inner class is also flagged as a GodClass. This makes the code harder to test, understand, and maintain.

## Goals

1. Reduce complexity metrics to remove PMD suppressions
2. Improve testability with comprehensive unit and integration tests
3. Extract `CallingContext` to a top-level package-private class
4. Use result-chaining pattern for clean flow control
5. Allow minor improvements with explicit approval for functional changes

## Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Test coverage | Both integration + unit tests | Most comprehensive coverage |
| Method visibility | Protected | Allows subclassing for testing/extension |
| CallingContext location | Top-level, package-private | Clean separation, proper encapsulation |
| Flow control pattern | Optional-based chaining | Idiomatic Java, clean flow, easy to test |

## Architecture

### File Structure

```text
cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/
├── CLIProcessor.java          (simplified, delegates to CallingContext)
├── CallingContext.java        (NEW - extracted, package-private)
└── ... (existing files unchanged)

cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/
├── CLIProcessorTest.java      (NEW - integration tests)
├── CallingContextTest.java    (NEW - unit tests for phases)
└── ExitCodeTest.java          (existing)
```

### Phase Flow

```text
processCommand()
    → checkHelpAndVersion()     : Optional<ExitStatus>
    → parseOptions()            : CommandLine (throws ParseException)
    → validateExtraArguments()  : Optional<ExitStatus>
    → validateCalledCommands()  : Optional<ExitStatus>
    → applyGlobalOptions()      : void
    → invokeCommand()           : ExitStatus
```

## Implementation Details

### Refactored processCommand()

```java
@NonNull
public ExitStatus processCommand() {
    // Phase 1: Check help/version before full parsing
    Optional<ExitStatus> earlyExit = checkHelpAndVersion();
    if (earlyExit.isPresent()) {
        return earlyExit.get();
    }

    // Phase 2: Parse all options
    CommandLine cmdLine;
    try {
        cmdLine = parseOptions();
    } catch (ParseException ex) {
        return handleInvalidCommand(ex.getMessage());
    }

    // Phase 3-4: Validate arguments and options
    Optional<ExitStatus> validationResult = validateExtraArguments(cmdLine)
        .or(() -> validateCalledCommands(cmdLine));
    if (validationResult.isPresent()) {
        return validationResult.get();
    }

    // Phase 5: Apply global options and execute
    applyGlobalOptions(cmdLine);
    return invokeCommand(cmdLine);
}
```

### Phase Method Signatures

```java
// Phase 1: Check --help and --version (before full parsing)
protected Optional<ExitStatus> checkHelpAndVersion()

// Phase 2: Parse all command line options
protected CommandLine parseOptions() throws ParseException

// Phase 3: Validate target command's extra arguments
protected Optional<ExitStatus> validateExtraArguments(@NonNull CommandLine cmdLine)

// Phase 4: Validate options for all called commands
protected Optional<ExitStatus> validateCalledCommands(@NonNull CommandLine cmdLine)

// Phase 5: Apply global options (--no-color, --quiet)
protected void applyGlobalOptions(@NonNull CommandLine cmdLine)
```

### Phase Implementations

**Phase 1 - checkHelpAndVersion():**

```java
protected Optional<ExitStatus> checkHelpAndVersion() {
    Options phase1Options = new Options();
    phase1Options.addOption(HELP_OPTION);
    phase1Options.addOption(VERSION_OPTION);

    try {
        CommandLine cmdLine = new DefaultParser()
            .parse(phase1Options, getExtraArgs().toArray(new String[0]), true);

        if (cmdLine.hasOption(VERSION_OPTION)) {
            getCLIProcessor().showVersion();
            return Optional.of(ExitCode.OK.exit());
        }
        if (cmdLine.hasOption(HELP_OPTION)) {
            showHelp();
            return Optional.of(ExitCode.OK.exit());
        }
    } catch (ParseException ex) {
        return Optional.of(handleInvalidCommand(ex.getMessage()));
    }
    return Optional.empty();
}
```

**Phase 2 - parseOptions():**

```java
protected CommandLine parseOptions() throws ParseException {
    return new DefaultParser().parse(toOptions(), getExtraArgs().toArray(new String[0]));
}
```

**Phase 3 - validateExtraArguments():**

```java
protected Optional<ExitStatus> validateExtraArguments(@NonNull CommandLine cmdLine) {
    ICommand target = getTargetCommand();
    if (target == null) {
        return Optional.empty();
    }
    try {
        target.validateExtraArguments(this, cmdLine);
        return Optional.empty();
    } catch (InvalidArgumentException ex) {
        return Optional.of(handleError(
            ExitCode.INVALID_ARGUMENTS.exitMessage(ex.getLocalizedMessage()),
            cmdLine, true));
    }
}
```

**Phase 4 - validateCalledCommands():**

```java
protected Optional<ExitStatus> validateCalledCommands(@NonNull CommandLine cmdLine) {
    for (ICommand cmd : getCalledCommands()) {
        try {
            cmd.validateOptions(this, cmdLine);
        } catch (InvalidArgumentException ex) {
            return Optional.of(handleInvalidCommand(ex.getMessage()));
        }
    }
    return Optional.empty();
}
```

**Phase 5 - applyGlobalOptions():**

```java
protected void applyGlobalOptions(@NonNull CommandLine cmdLine) {
    if (cmdLine.hasOption(NO_COLOR_OPTION)) {
        handleNoColor();
    }
    if (cmdLine.hasOption(QUIET_OPTION)) {
        handleQuiet();
    }
}
```

## Testing Strategy

### Integration Tests (CLIProcessorTest.java)

Test the public API through `process(String... args)`:

- `--version` shows version info and returns OK
- `--help` shows help and returns OK
- Invalid command returns INVALID_COMMAND
- Invalid option returns INVALID_COMMAND
- Valid command executes successfully
- `--quiet` option works
- `--no-color` option works

### Unit Tests (CallingContextTest.java)

Test individual phases via protected methods:

**checkHelpAndVersion():**
- Returns ExitStatus for --version
- Returns ExitStatus for --help
- Returns empty for other args

**parseOptions():**
- Parses valid options
- Throws on invalid option

**validateExtraArguments():**
- Returns empty when no target command
- Returns empty when arguments valid
- Returns error when arguments invalid

**validateCalledCommands():**
- Returns empty when all commands valid
- Returns error on first invalid command

**applyGlobalOptions():**
- Applies --no-color without error
- Applies --quiet without error

### Test Fixtures

```java
// Minimal command for basic tests
class TestCommand implements ICommand { ... }

// Command that accepts extra arguments
class TestCommandWithArgs implements ICommand { ... }

// Command that requires extra arguments
class TestCommandRequiringArgs implements ICommand { ... }

// Command with required option
class TestCommandWithRequiredOption implements ICommand { ... }
```

## Dependencies

### PR #551 Impact

This refactoring must be based on PR #551 (shell completion). That PR introduces:

1. **`getTopLevelCommands()` visibility change** - Changed from `protected` to `public`. No impact on our design.

2. **`ShellCompletionCommand`** - New command that imports `CLIProcessor.CallingContext`. When we extract `CallingContext` to a top-level class, we must update this import:

```java
// Before (PR #551)
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;

// After (our refactor)
import gov.nist.secauto.metaschema.cli.processor.CallingContext;
```

3. **`ExtraArgument.getType()`** - New method for completion hints. No impact on our design.

## Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking existing CLI behavior | High | Write characterization tests first, run full test suite after each change |
| CallingContext extraction breaks internal references | Medium | `CLIProcessor.this` references need updating; search for all usages |
| ShellCompletionCommand import breaks | Low | Update import when extracting CallingContext |
| Test fixtures become complex/fragile | Medium | Keep test commands minimal; use builder pattern if needed |
| PMD/Checkstyle finds new issues in refactored code | Low | Run `mvn checkstyle:check` incrementally |

## Success Criteria

- [ ] All existing tests pass (ExitCodeTest + any integration tests)
- [ ] New tests provide coverage for all phases
- [ ] `@SuppressWarnings` for PMD complexity removed from `processCommand()`
- [ ] `@SuppressWarnings("PMD.GodClass")` removed from `CallingContext`
- [ ] `mvn install -PCI -Prelease` passes
- [ ] No functional behavior changes (unless explicitly approved)

## Out of Scope

- Refactoring `invokeCommand()` (already reasonably sized)
- Refactoring help/footer building methods (not complexity issues)
- Changes to `ICommand` interface or other classes
