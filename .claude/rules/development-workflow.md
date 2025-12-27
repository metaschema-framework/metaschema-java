# Development Workflow

> **Scope:** This document describes development workflows for AI agents (Claude + superpowers plugin) and automated code review. Human developers should adapt these TDD, parallel-execution, and review principles to their tooling and IDE.

## Skill Usage Protocol (MANDATORY)

Before responding to ANY task, complete this checklist:

1. **Check for relevant skills** - Does any skill match this request?
2. **If skill exists** → Use `Skill` tool to load and run it
3. **Announce usage** - "I'm using [skill] to [action]"
4. **Follow skill exactly** - Don't adapt away the discipline

### Checklist Tracking Rule
If a skill has a checklist, create **TodoWrite todos for EACH item**. Never:
- Work through checklists mentally
- Skip todos "to save time"
- Batch multiple items into one todo

### Common Rationalizations (RED FLAGS)
If you think any of these, STOP and check for skills:
- "This is just a simple question"
- "Let me gather information first"
- "This doesn't need a formal skill"
- "The skill is overkill for this"
- "I'll just do this one thing first"

**Why:** Skills document proven techniques. Not using available skills = repeating solved problems.

### Available Skills

**Project Skills** (defined in `.claude/skills/`):
- `prd-construction` - Templates and methodology for creating PRDs and implementation plans
- `unit-test-writing` - Edge case checklist, test structure patterns, coverage workflow
- `metaschema-module-authoring` - Module structure, definitions, format-specific features
- `metaschema-constraints-authoring` - Constraint types, validation patterns
- `metapath-expressions` - Path syntax, operators, functions based on XPath 3.1
- `metaschema-java-library` - Key interfaces, exception hierarchy, Metapath evaluation, serialization

**Managed Skills** (from superpowers plugin - install via `claude plugin add superpowers-marketplace`):
- `superpowers:brainstorming` - Refine ideas into designs through collaborative questioning
- `superpowers:writing-plans` - Create detailed implementation plans
- `superpowers:executing-plans` - Execute tasks in batches with review checkpoints
- `superpowers:test-driven-development` - Write tests first, then implementation
- `superpowers:subagent-driven-development` - Quality-gated autonomous work
- `superpowers:dispatching-parallel-agents` - Concurrent work on independent tasks
- `superpowers:systematic-debugging` - 4-phase debugging framework
- `superpowers:root-cause-tracing` - Trace bugs backward to find source
- `superpowers:verification-before-completion` - Confirm work is complete
- `superpowers:requesting-code-review` - Review implementation against plan
- `superpowers:testing-anti-patterns` - Avoid common testing mistakes

### Instructions ≠ Permission to Skip Workflows
User instructions describe WHAT to do, not HOW. "Add X" or "Fix Y" does NOT mean skip brainstorming, TDD, or verification workflows.

---

## Test-Driven Development (MANDATORY - BLOCKING)

**ALL code changes MUST follow TDD. No exceptions. This is BLOCKING.**

### The Iron Law of TDD

```text
TESTS MUST BE WRITTEN AND FAIL BEFORE ANY IMPLEMENTATION CODE EXISTS
```

This is non-negotiable. Implementation code written before tests is a violation.

### The TDD Cycle

1. **Write the test first** - Before writing any implementation code
2. **Watch it fail** - Run the test to verify it fails for the right reason
3. **Write minimal code** - Implement just enough to make the test pass
4. **Refactor** - Clean up while keeping tests green
5. **Repeat** - For each new behavior

### Enforcement Gate

**Before writing ANY implementation code, you MUST have:**
- [ ] A test file created for the new functionality
- [ ] At least one failing test that exercises the code path
- [ ] Verification that the test fails for the expected reason (not a syntax error)

**If you haven't completed these steps, STOP. Go back and write tests first.**

### What's Allowed with TDD

Multiple test-writing agents **CAN run in parallel** with each other. Only implementation agents must wait for all test agents to complete. See "TDD with Parallel Agents" section below.

### Red Flags (You're Skipping TDD)

If you catch yourself doing ANY of these, STOP IMMEDIATELY:
- Writing implementation code before tests
- "I'll add tests after I get it working"
- "This is too simple to need tests"
- "Let me just make this small change first"
- Dispatching implementation agents before test agents complete
- Dispatching tests and implementation in the same parallel batch
- Modifying code without first verifying existing test coverage

### Common Rationalizations That Violate TDD

| Rationalization | Why It's Wrong |
|-----------------|----------------|
| "Tests slow me down" | Tests written after are harder to write and less effective |
| "I'll write tests once I know the design" | TDD helps you discover the design |
| "Implementation is straightforward" | Straightforward code still needs tests first |
| "Tests and impl in parallel for efficiency" | Tests MUST complete before implementation starts |
| "The code already works" | Prove it works by writing the test first |

### When Tests Already Exist

When modifying existing code:
1. **Run existing tests first** - Verify they pass before changes
2. **Add tests for new behavior** - Write failing tests for new functionality
3. **Make changes** - Implement while keeping all tests passing

### Enforcement

- Use `superpowers:test-driven-development` skill for the full workflow
- The skill will guide you through RED-GREEN-REFACTOR
- Never skip the "watch it fail" step - it proves your test works
- **Agents dispatched for implementation MUST have tests written first**

---

## PRD-Based Development Lifecycle

For new development work, follow this structured lifecycle:

### Phase 1: PRD Development

#### Check for Existing Work (CRITICAL - Do This First)

**Before making ANY changes for PRD-related work:**
1. Run `git worktree list` to check for existing worktrees
2. If a worktree exists for this PRD/feature, switch to it using `cd <worktree-path>`
3. **NEVER work in the main repository** if a worktree exists for the task

**Why:** Working in the wrong directory causes:
- Changes in wrong location that must be manually moved
- Risk of committing to wrong branch
- Merge conflicts and cleanup overhead

#### Creating New PRDs

1. **Use `superpowers:brainstorming`** to refine requirements
2. **Use `prd-construction` skill** for templates and methodology
3. **Create PRD directory**: `PRDs/[date]-[name]/` (see `prd-conventions.md` for naming)
4. **Create PRD documents** using skill templates:
   - `PRD.md` - Problem statement, goals, requirements, success metrics
   - `implementation-plan.md` - Detailed PR breakdown with acceptance criteria
5. **Add supporting documents** to the directory as needed (analysis, research, diagrams)

### PRD Directory Structure
```text
PRDs/[date]-[name]/
├── PRD.md                 # Main requirements document
├── implementation-plan.md # Detailed PR breakdown
└── [supporting-docs].md   # Analysis, research, etc.
```

### Phase 2: User Approval
- Present PRD to user for review
- Incorporate feedback and iterate
- **Do NOT proceed to development until PRD is approved**

### Phase 3: Development

Execute the plan using these skills:
- `superpowers:executing-plans` - Execute tasks in batches with review checkpoints
- `superpowers:test-driven-development` - Write tests first, then implementation
- `superpowers:subagent-driven-development` - Quality-gated autonomous work
- `superpowers:dispatching-parallel-agents` - Concurrent work on independent tasks

**Auto-applied skills** (Claude automatically uses these when relevant):
- `superpowers:testing-anti-patterns` - Avoid testing mock behavior, test-only production methods

**Update PRD documents as you work:**
- Mark acceptance criteria as `[x]` when completed
- Update PR status in implementation-plan.md
- Add notes about deviations from plan

#### Parallel Agent Usage

**Use multiple agents in parallel whenever tasks are independent.**

Dispatch multiple agents in a single message when:
- Tasks operate on different files with no dependencies
- Tasks can be clearly scoped with complete context
- Each task can succeed or fail independently
- Waiting for sequential completion would waste time

| Scenario | Parallel Approach |
|----------|-------------------|
| Implementing 3 new classes | One agent per class |
| Updating interface + implementation | Agent for each file |
| Writing tests for multiple components | Agent per test class |
| Code review + linting | Separate review agents |
| Reading multiple files for context | Agent per exploration area |

**How to dispatch:** Use a SINGLE message with multiple Task tool calls. Do not dispatch agents sequentially when they can run in parallel.

**Dispatch granularity:** When deciding agent scope, prefer finer-grained agents (one per file/class) over coarse-grained agents (one for entire feature). Smaller scopes:
- Fail independently without blocking other work
- Produce clearer error messages
- Enable better parallelism

**Detecting dependencies:** Tasks have dependencies when:
- One task's output is another's input (e.g., interface before implementation)
- Shared state must be modified in sequence
- Compilation order matters (e.g., base class before subclass)

Tasks are independent when they touch different files with no shared interfaces.

**Red flags (you're not using parallel agents):**
- Dispatching one agent, waiting for result, then dispatching another
- "I'll run this agent first to see what happens"
- "These tasks might have dependencies" (when they clearly don't)
- Running sequential agents for independent file changes

#### TDD with Parallel Agents

Tests MUST be written before implementation, but test-writing agents CAN run in parallel:
- Dispatch multiple test-writing agents in parallel (one per test class)
- Wait for all test agents to complete
- Verify tests fail for the correct reasons
- THEN dispatch implementation agents in parallel

**Correct order:**
1. Parallel agents write tests → all complete
2. Run the full test suite and verify new tests fail correctly:
   - Failures should be assertion failures (e.g., "expected X but was Y"), not compilation errors
   - Review failure messages to confirm they match test intent, not "class not found" or similar
3. Parallel agents write implementation → all complete
4. Verify tests pass

**Wrong order:**
- Tests and implementation agents in the same parallel batch
- Implementation agents before test agents complete

### Phase 4: Code Review Cycle
Perform iterative code review until all issues are resolved:

1. **Run code review agents** (can run multiple in parallel):
   - `superpowers:requesting-code-review` - Review implementation against plan

2. **Consolidate review findings** into a report of identified issues

3. **Work the issues**:
   - Address each identified issue
   - Apply `superpowers:test-driven-development` for any gaps found

4. **Re-review** - Run code review again on changes

5. **Repeat** until all issues are resolved and changes are accepted

```text
Code Complete
    ↓
Code Review Agents (parallel) → Consolidated Issues Report
    ↓
Work Issues → Re-review
    ↓
[Issues Found?] → Yes → Work Issues → Re-review
    ↓
No Issues → Proceed to Verification
```

#### Handling Optional Nitpicks

When reviewers (human or automated) mark feedback as "nitpick" or "optional":

**Address nitpicks when they:**
- Improve code quality in files already being changed
- Reduce duplication or improve readability
- Add low-risk enhancements (e.g., additional test cases)
- Can be implemented without significant new changes

**Defer nitpicks when they:**
- Require changes outside the PR's scope
- Introduce significant new code or risk
- Would substantially increase PR size
- Conflict with the PR's focused purpose

**When deferring:** Note the suggestion in a comment or issue for future consideration.

### Phase 5: Verification & PR
- `superpowers:verification-before-completion` - Confirm all tests pass
- Verify all code review issues are resolved
- Create PR against `develop` branch
- Always use squash merge with branch deletion (`gh pr merge --squash --delete-branch`)

#### Build Verification Summary Format

**When:** After running a full build with quality checks (e.g., `mvn clean install -PCI -Prelease`).

**Who:** Developer or AI agent. Manual compilation from tool output is acceptable; automation is preferred.

**Where:** Include in the conversation output or PR description before proceeding to merge.

After running builds with quality checks, provide a scannable summary:

```text
Build verified successfully:
- ✅ Tests: 56 passed, 0 failed
- ✅ SpotBugs: 0 bugs, 0 errors
- ✅ PMD: 0 violations, 97 warnings
- ✅ Checkstyle: 0 violations, 5 warnings
- ✅ Coverage: 65% (target: 60%)
```

**Guidelines:**
- Use ✅ for passing checks (no blocking errors/violations), ❌ for failures
- Use ⚠️ for coverage below target (build succeeds but coverage warning)
- Always report both errors/violations AND warnings for each tool
- Add brief context for notable items (e.g., "import order fixed")
- Report failures clearly so they can be addressed before proceeding

**Example with failures:**
```text
Build failed:
- ❌ Tests: 54 passed, 2 failed
- ❌ SpotBugs: 2 bugs (null pointer issues), 0 errors
- ✅ PMD: 0 violations, 45 warnings
- ❌ Checkstyle: 3 violations (missing Javadoc), 12 warnings
- ⚠️ Coverage: 58% (target: 60%)
```

### Workflow Summary
```text
GitHub issue/prompt
    ↓
brainstorming → prd-construction → PRDs/[date]-[name]/
    ↓
User Approves PRD
    ↓
Phase 3: Development (TDD with Parallel Agents)
  ├─ Parallel test agents → verify failures (MUST complete first)
  ├─ Parallel implementation agents → verify passes (only after tests pass)
  └─ Update implementation-plan.md with progress [x]
    ↓
Phase 4: Code review cycle (parallel review agents)
    ↓
Phase 5: verification-before-completion → PR to develop
```

---

## Debugging Workflow (MANDATORY)

**ALL debugging MUST use `superpowers:systematic-debugging` skill. No exceptions.**

When debugging issues (during development or in production):

### Step 0: Invoke the Debugging Skill (REQUIRED FIRST STEP)

**BEFORE ANY investigation or fix attempts:**
1. Use `Skill` tool to invoke `superpowers:systematic-debugging`
2. Follow the four phases exactly as specified
3. Create TodoWrite todos for each phase

**Red Flags (You're Skipping the Skill):**
- "Let me just check this quickly"
- "This is a simple bug"
- "I can see the problem, let me fix it"
- "One quick fix to try first"

**Why:** Random fixes waste time and create new bugs. The skill ensures systematic root cause identification.

### Step 1: Identify Root Cause (REQUIRED)
**Do NOT attempt fixes until root cause is identified.**

Use these skills:
- `superpowers:systematic-debugging` - 4-phase framework: investigation → pattern analysis → hypothesis testing → implementation
- `superpowers:root-cause-tracing` - Trace bugs backward through call stack to find the source

**Root cause identification must include:**
- What is the actual vs expected behavior?
- Where in the code does the bug originate?
- Why does this code path produce the wrong result?

**Post Bug Reproduction comment to Jira** (if applicable)

### Step 2: Verify Test Coverage
Once root cause is confirmed:
1. **Check for existing test** - Does a test cover this scenario?
2. **If no test exists** - Use `superpowers:test-driven-development` to:
   - Write a failing test that reproduces the bug
   - Confirm the test fails for the intended reason
   - Do NOT proceed to fix until test fails correctly

### Step 3: Implement Fix
- Fix the identified root cause
- Run the failing test - it should now pass
- Run all related tests

### Step 4: Code Review Cycle
1. **Run code review agents** (parallel) on the fix
2. **Consolidate issues** into a report
3. **Work issues** and re-review
4. **Repeat** until all issues resolved

### Step 5: Verification & PR
- Use `superpowers:verification-before-completion` before claiming fixed
- Confirm all tests pass
- Verify all code review issues are resolved
- Create PR against `develop` branch

### Debugging Summary
```
Bug Report / Issue
    ↓
systematic-debugging + root-cause-tracing
    ↓
ROOT CAUSE IDENTIFIED (required before proceeding)
    ↓
Check Test Coverage
    ↓
[No Test?] → test-driven-development → Write Failing Test
    ↓
Implement Fix (target the root cause)
    ↓
verification-before-completion
    ↓
PR to develop
```

---

## Testing Best Practices

When writing or modifying tests, apply `superpowers:testing-anti-patterns`:

### Iron Laws
1. **NEVER test mock behavior** - Assert on real component behavior, not mock existence
2. **NEVER add test-only methods to production classes** - Put test utilities in test files
3. **NEVER mock without understanding dependencies** - Know what side effects tests depend on

### Gate Functions (Ask Before Each Action)

**Before asserting on mocks:**
> "Am I testing real behavior or just mock existence?"
> If testing mock existence → Delete assertion or unmock component

**Before adding methods to production classes:**
> "Is this only used by tests?"
> If yes → Put in test utilities instead

**Before mocking any method:**
> "What side effects does this have? Does my test depend on them?"
> If test depends on side effects → Mock at lower level, not the method test needs

### Quick Reference

| Anti-Pattern | Fix |
|--------------|-----|
| Assert on mock elements | Test real component or unmock |
| Test-only methods in production | Move to test utilities |
| Mock without understanding | Understand deps first, mock minimally |
| Incomplete mocks | Mirror real API completely |
| Tests as afterthought | TDD - tests first |

### Red Flags
- Assertions checking for `*-mock` test IDs
- Methods only called in test files
- Mock setup is >50% of test code
- Test fails when you remove mock
- Mocking "just to be safe"

---

## When to Use PRD Workflow
- New features
- Significant refactoring
- Complex bug fixes requiring architectural changes

## When to Skip PRD
- Simple bug fixes with clear solutions
- Minor UI tweaks
- Minor documentation updates
