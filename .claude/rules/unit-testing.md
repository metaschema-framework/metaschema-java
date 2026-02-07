# Unit Testing Standards

## 100% Test Pass Rate (BLOCKING)

**All unit tests MUST pass before pushing code or creating PRs.**

- Run `mvn test` or `mvn -pl {module} test` to verify
- A single failing test blocks the PR
- Flaky tests must be fixed or marked as `@Disabled` with explanation
- Network-dependent tests should have appropriate timeouts and retry logic

**If CI fails due to test timeout or flakiness:**
1. Investigate the root cause
2. If it's a pre-existing issue on develop, note this in the PR
3. Re-run CI to verify it's not caused by your changes
4. Open an issue to track the flaky test if not already tracked

### No Excuses for Test Failures (BLOCKING)

**"Pre-existing failure" is NOT a valid excuse.** Any broken test in your branch IS your responsibility:

- Do not claim "tests were already failing before my changes"
- Do not dismiss failures as "not caused by my change"
- Do not proceed with commits or pushes when tests fail

**When encountering test failures:**
1. Fix them, even if they predate your changes — always prefer actual fixes over `@Disabled`
2. Never disable a test without asking the user first — explain the situation and propose options
3. If truly unrelated, fix in the current PR or a separate branch — either way, fix before merging
4. The 100% pass rate policy has no exceptions
5. Always use `superpowers:systematic-debugging` skill (4-phase framework: root cause investigation, pattern analysis, hypothesis testing, implementation — see `development-workflow.md` for details) when investigating test failures
6. The full CI build (`mvn clean install -PCI -Prelease`) is the authoritative pass/fail check

## Core Principles

### What NOT to Test

**Never test trivial code:**
- Getters and setters (no logic = no test)
- Simple constructors that only assign fields
- Delegation methods that just call another method
- Framework-generated code (Lombok, records, etc.)

**Why:** Tests should verify behavior, not structure. Trivial tests add maintenance burden without catching bugs.

### What TO Test

**Always test:**
- Business logic and calculations
- Validation and error handling
- State changes and side effects
- Integration points and boundaries
- Edge cases and corner cases

### Prioritize Edge Cases

Every test class should prioritize edge case coverage over happy paths:

- **Boundary conditions** - empty collections, null inputs, zero values, max values
- **Error paths** - invalid inputs, missing required data, exception scenarios
- **State transitions** - before/after states, partial completion, rollback scenarios
- **Combinations** - multiple flags, conflicting options, compound conditions

## Mandatory Coverage Evaluation

**When modifying existing code, you MUST evaluate and expand test coverage.**

1. Check if tests exist for the code path you're touching
2. Identify missing edge cases
3. Add tests before completing the work

Use the `unit-test-writing` skill for the detailed workflow.

## Legacy Code Coverage

**When improving or refactoring existing classes, add tests for legacy functionality.**

This ensures:
- Existing behavior is documented through tests
- Regressions are caught if refactoring breaks something
- Test coverage improves incrementally over time

### Process

1. **Before changes**: Write tests capturing current behavior of code you're touching
2. **Verify tests pass**: Confirms tests accurately reflect existing behavior
3. **Make improvements**: Refactor or enhance the code
4. **Verify tests still pass**: Confirms behavioral equivalence

This approach builds test coverage organically as the codebase evolves.
