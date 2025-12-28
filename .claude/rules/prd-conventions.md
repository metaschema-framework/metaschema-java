# PRD Conventions for metaschema-java

This rule defines project-specific conventions for PRDs in this repository.

## Directory Structure

PRDs are stored in `PRDs/[date]-[name]/` with:

```text
PRDs/
└── [date]-[name]/
    ├── PRD.md                 # Main requirements document
    ├── implementation-plan.md # Detailed PR breakdown
    └── [supporting-docs].md   # Analysis, research, etc.
```

### Naming Convention

- **Format**: `[date]-[short-description]`
- **Date**: Use the creation date (YYYYMMDD format)
- **Description**: Lowercase, hyphen-separated, brief identifier

Examples:
- `20251206-build-cleanup`
- `20251217-context-functions`
- `20251220-cli-refactor`

## Verification Commands

All PRs must pass:

```bash
mvn clean install -PCI -Prelease
```

Include this in every PR's acceptance criteria.

## PRD Tracking

### Active PRD Management

When starting development on a PRD:
1. Add entry to CLAUDE.md "Active PRDs" table with status "In Progress"

When completing a PRD:
1. Update status to "Completed" with completion date
2. Archive or remove from active list as appropriate

### Status Values

| Status | Meaning |
|--------|---------|
| Draft | PRD being written, not ready for review |
| In Review | Awaiting user approval |
| Approved | Ready for implementation |
| In Progress | Implementation underway |
| Completed | All PRs merged |

## PR Conventions

### Target Branch

All PRs target `develop` branch (per CONTRIBUTING.md).

### PR Creation

PRs must be created from a personal fork:
1. Create feature branch from `origin/develop`
2. Push to personal fork remote
3. Create PR targeting upstream `develop`

### PR Sizing

PRs should be **logically cohesive**, not artificially small:

- Group related changes into cohesive PRs
- Avoid fragmenting features across many PRs
- Target ≤50 files per PR; maximum 100 files per PR (per CLAUDE.md)
- Prefer fewer, well-organized PRs over many small ones

### Merging

Always use squash merge with branch deletion:

```bash
gh pr merge --squash --delete-branch
```

This keeps the commit history clean and removes stale branches.

## TDD Requirement

All functional code changes require test-driven development:
1. Write/update tests first
2. Verify tests pass with existing implementation
3. Make code changes
4. Verify tests still pass

## PRD Update Requirement (BLOCKING)

**Before pushing any implementation changes, update the PRD documents:**

1. **Mark completed tasks**: Update acceptance criteria checkboxes (`[ ]` → `[x]`) in `implementation-plan.md`
2. **Update file lists**: Add any new files or update change types in the "Files Changed Summary"
3. **Note deviations**: Document any changes from the original plan
4. **Commit PRD updates**: Include PRD updates in the same commit or as a follow-up commit before pushing

### When to Update PRDs

| Event | Required Update |
|-------|-----------------|
| Task completed | Mark acceptance criteria as `[x]` |
| New file created | Add to "Files Changed Summary" |
| Plan deviation | Add note explaining the change |
| Phase completed | Verify all tasks in phase are marked |
| Before creating PR | Ensure all completed work is reflected |

### Why This Matters

- PRDs serve as living documentation of implementation progress
- Reviewers use PRDs to understand what was done vs. planned
- Future maintainers need accurate records of what was implemented
- Incomplete PRD updates create confusion about project status

## Document Quality

### Content Rules

- No AI-specific instructions (e.g., "For Claude:", "AI should...")
- No meta-commentary about the document
- Use Markdown headings (`###`) not bold text (`**text**`) for structure
- Include specific, verifiable acceptance criteria

### Code Block Rules

All fenced code blocks MUST have a language identifier:

| Content Type | Language ID |
|--------------|-------------|
| Bash/shell commands | `bash` |
| Java code | `java` |
| XML | `xml` |
| JSON | `json` |
| Directory structures | `text` |
| Commit messages | `text` |
| Plain text/templates | `text` |
| Markdown examples | `markdown` |

Example:
```text
# Wrong - no language identifier
\`\`\`
some content
\`\`\`

# Correct - has language identifier
\`\`\`text
some content
\`\`\`
```

### Linking

- Reference related documents with relative paths: `[Implementation Plan](./implementation-plan.md)`
- Reference GitHub issues/PRs with full URLs when available
- Update links as documents are created
