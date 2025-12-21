---
name: pr-feedback
description: Use when addressing PR review feedback, including responding to comments and resolving review threads. Provides the correct API methods for interacting with GitHub PR conversations.
---

# PR Feedback Skill

Use this skill when addressing PR review feedback to ensure comments are properly responded to and resolved.

## DOs and DONTs

### DO

- **DO** fetch all PR comments before starting to understand the full scope
- **DO** verify technical claims in feedback before implementing (see Step 2)
- **DO** address all feedback, including nits and style suggestions
- **DO** commit fixes with a clear message referencing what was addressed
- **DO** present proposed responses to the user for review BEFORE posting
- **DO** get user approval before resolving any review threads
- **DO** use the GraphQL API to resolve review threads
- **DO** verify all threads are resolved before considering feedback complete
- **DO** repeat the cycle until all checks pass AND no comments remain
- **DO** batch related fixes into a single commit when possible

### DON'T

- **DON'T** blindly implement feedback without verifying correctness
- **DON'T** post responses or resolve threads without user approval
- **DON'T** ignore nits - they improve code quality and show attention to detail
- **DON'T** leave review threads unresolved after addressing them
- **DON'T** use the REST API `/replies` endpoint - it returns 404 for review comments
- **DON'T** assume a general PR comment closes review threads - it doesn't

---

## Step 1: Fetch PR Feedback

Get all review comments and feedback:

```bash
# Get PR comments and reviews
gh pr view <PR_NUMBER> --comments --json comments,reviews,body

# Get file-level review comments
gh api repos/<OWNER>/<REPO>/pulls/<PR_NUMBER>/comments
```

## Step 2: Verify Feedback Correctness

**Automated reviewers (like CodeRabbit) can be wrong.** Before implementing feedback:

### Verify Technical Claims

- **API/schema claims** - Use introspection or official docs to verify
  ```bash
  # Example: Verify GraphQL field exists
  gh api graphql -f query='{ __type(name: "TypeName") { fields { name } } }'
  ```
- **Language/framework claims** - Check official documentation
- **Best practice claims** - Consider project context; not all "best practices" apply universally

### When to Push Back

- Feedback contradicts official documentation
- Suggestion would break existing functionality
- Recommendation conflicts with project conventions

### Verification Examples

| Feedback Type | How to Verify |
|---------------|---------------|
| "Field X doesn't exist on type Y" | GraphQL introspection query |
| "This API is deprecated" | Check official API docs |
| "This pattern is wrong" | Check language/framework docs |
| "Missing required field" | Test the actual behavior |

If feedback is incorrect, note this when presenting the summary to the user (Step 5).

## Step 3: Categorize Feedback

Group feedback by type:
- **Blocking issues** - Must fix before merge
- **Suggestions** - Recommended improvements
- **Nits** - Minor style/formatting issues (still address these)
- **Questions** - May need clarification response
- **Incorrect** - Feedback that was verified as wrong (explain why)

## Step 4: Address Feedback

Make the necessary code changes:

1. Create a todo list for each issue
2. Fix issues in logical groups
3. Commit with a descriptive message:

```bash
git commit -m "fix: address PR review feedback

- <Issue 1 fixed>
- <Issue 2 fixed>
- <Issue 3 fixed>"
```

4. Push changes

## Step 5: Present Summary for User Review

**REQUIRED: Get user approval before posting any responses or resolving threads.**

Present a summary to the user:

```text
## Feedback Addressed

| File | Issue | Fix Applied |
|------|-------|-------------|
| file1.md | Missing X | Added X |
| file2.md | Typo in Y | Fixed typo |

## Proposed Responses

1. **Thread on file1.md:25** - "Fixed in commit abc123 - added X as suggested."
2. **Thread on file2.md:50** - "Fixed in commit abc123 - corrected typo."

## Threads to Resolve

- [ ] Thread 1 (file1.md)
- [ ] Thread 2 (file2.md)

Should I post these responses and resolve the threads?
```

Wait for explicit user approval before proceeding.

## Step 6: Respond to Comments

For review threads, you cannot use the REST API replies endpoint (returns 404).
Instead, either:

1. **Let the bot auto-resolve** - Some bots (like CodeRabbit) auto-detect fixes and resolve threads
2. **Post a consolidated PR comment** - Summarize all fixes in one comment
3. **Resolve via GraphQL** - Use the mutation API (see Step 7)

## Step 7: Resolve Review Threads

### Get Thread IDs

```bash
gh api graphql -f query='
query {
  repository(owner: "<OWNER>", name: "<REPO>") {
    pullRequest(number: <PR_NUMBER>) {
      reviewThreads(first: 50) {
        nodes {
          id
          isResolved
          path
          comments(first: 1) {
            nodes {
              body
            }
          }
        }
      }
    }
  }
}'
```

### Resolve a Thread

```bash
gh api graphql -f query='
mutation {
  resolveReviewThread(input: {threadId: "<THREAD_ID>"}) {
    thread {
      isResolved
    }
  }
}'
```

### Verify All Resolved

```bash
gh api graphql -f query='
query {
  repository(owner: "<OWNER>", name: "<REPO>") {
    pullRequest(number: <PR_NUMBER>) {
      reviewThreads(first: 50) {
        totalCount
        nodes {
          isResolved
        }
      }
    }
  }
}'
```

All `isResolved` values should be `true`.

## Step 8: Handle Special Cases

### CodeRabbit Auto-Resolution

CodeRabbit automatically resolves threads when it detects fixes in new commits.
It adds "✅ Addressed in commit <hash>" to the thread.

### Disagreeing with Feedback

If you disagree with feedback:
1. Reply explaining your reasoning
2. Ask the user if they want to proceed as-is or make changes
3. Only resolve after reaching consensus

### Multiple Review Rounds

**Repeat the feedback cycle until BOTH conditions are met:**
1. ✅ All status checks pass
2. ✅ No unresolved review comments remain

**Iteration workflow:**
```text
Address Feedback → Push → Wait for CI/Bot Review
         ↓
    [New comments?] ──Yes──→ Go to Step 1 (Fetch)
         ↓ No
    [Checks pass?] ──No───→ Fix issues → Push
         ↓ Yes
    ✅ Ready for merge
```

**For each iteration:**
1. Fetch new comments (Step 1)
2. Verify correctness (Step 2)
3. Address valid feedback (Steps 3-4)
4. Present summary and get approval (Step 5)
5. Don't amend previous fix commits - create new ones
6. Squash before merge if needed

---

## Quick Reference

| Task | Method |
|------|--------|
| Fetch PR comments | `gh pr view <N> --comments` |
| Fetch file comments | `gh api repos/.../pulls/<N>/comments` |
| Verify GraphQL field exists | `gh api graphql -f query='{ __type(name: "X") { fields { name } } }'` |
| Get thread IDs | GraphQL `reviewThreads` query |
| Resolve thread | GraphQL `resolveReviewThread` mutation |
| Verify resolved | GraphQL query checking `isResolved` |

## Common Pitfalls

1. **REST replies endpoint 404** - The `/pulls/comments/{id}/replies` endpoint doesn't work for review comments. Use GraphQL instead.

2. **Unresolved threads blocking merge** - Some repos require all threads resolved. Always verify with the GraphQL query.

3. **Missing feedback** - Check both `comments` (general) and `reviews` (code review) in the PR data.

4. **Incorrect automated feedback** - Bots like CodeRabbit can make mistakes. Verify technical claims against official docs or schema introspection before implementing.
