# PRD: Build Cleanup - Warnings and Deprecation Removal

## Document Information

| Field | Value |
|-------|-------|
| **PRD ID** | BUILD-CLEANUP-001 |
| **Status** | Draft |
| **Author** | Development Team |
| **Created** | 2025-12-06 |
| **Last Updated** | 2025-12-06 |

---

## 1. Overview

### 1.1 Problem Statement

The metaschema-java build produces numerous warnings during compilation that reduce code quality signal and make it harder to identify new issues. These warnings include:

- **Deprecation warnings** for APIs marked `@Deprecated(forRemoval=true)`
- **Javadoc warnings** for missing documentation on public interfaces and methods
- **finalize() usage** warnings for deprecated object finalization
- **Maven plugin deprecation** for outdated annotations

### 1.2 Goals

1. Eliminate all fixable compiler warnings from the build
2. Improve code maintainability by replacing deprecated APIs with modern alternatives
3. Enhance documentation through Javadoc additions
4. Maintain build stability through incremental, reviewable changes

### 1.3 Non-Goals

- Fixing module-info.java warnings (automatic modules from third-party dependencies)
- Modifying generated code in `target/generated-sources/` directories
- Architectural refactoring beyond what's needed for deprecation fixes

### 1.4 Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| Deprecation-for-removal warnings | 28 | 0 |
| finalize() warnings | 3 | 0 |
| Maven @Component warnings | 3 | 0 |
| Javadoc warnings (non-generated) | ~285 | 0 |
| Build success | ✓ | ✓ |
| Test pass rate | 100% | 100% |

---

## 2. Background

### 2.1 Current Build State

Build command: `mvn clean install -PCI -Prelease`

The build succeeds but produces warnings in these categories:

| Category | Count | Root Cause |
|----------|-------|------------|
| Javadoc (non-generated) | ~285 | Missing `@param`, `@return`, `@throws` tags |
| Deprecation-for-removal | 28 | Using APIs marked for removal in future Java versions |
| finalize() usage | 3 | Using deprecated `Object.finalize()` method |
| Maven @Component | 3 | Using deprecated Maven injection annotation |
| Module warnings | 9 | Third-party automatic modules (out of scope) |

### 2.2 Technical Context

- **Java Version**: Target Java 11, Toolchain Java 17
- **Build System**: Maven with multi-module structure
- **Affected Modules**: core, databind, schemagen, metaschema-maven-plugin

---

## 3. Requirements

### 3.1 Functional Requirements

#### FR-1: Remove finalize() Deprecation
Replace all uses of `Object.finalize()` with modern resource management patterns:
- `java.lang.ref.Cleaner` API for cleanup callbacks
- `try-with-resources` for `Closeable` resources
- Remove if cleanup is unnecessary

#### FR-2: Update Maven Plugin Annotations
Replace deprecated `@Component` annotation with `@Inject` from JSR-330.

#### FR-3: Replace Deprecated FunctionUtils Methods
Replace calls to deprecated `FunctionUtils` methods with their recommended replacements:
- `toNumeric()` variants
- `countTypes()`
- `getTypes()`
- `requireTypeOrNull()`
- `requireType()`

#### FR-4: Replace Deprecated Type Interfaces
Update usage of deprecated interfaces:
- `INcNameItem` → replacement type
- `WellKnown` methods → replacement accessors
- `ISequence.getValue()` → replacement method

#### FR-5: Add Missing Javadoc
Add Javadoc comments to all public interfaces and methods missing documentation:
- `@param` tags for all parameters
- `@return` tags for non-void methods
- `@throws` tags for declared exceptions

### 3.2 Non-Functional Requirements

#### NFR-1: PR Size Limits
- **Target**: ≤50 files changed per PR
- **Maximum**: 100 files changed per PR (absolute limit)
- **Rationale**: Enable thorough code review

#### NFR-2: Build Stability
- All tests must pass after each PR
- No new warnings introduced
- Build time should not significantly increase

#### NFR-3: Backwards Compatibility
- Public API signatures must remain unchanged
- Behavior must remain identical
- No breaking changes to consumers

#### NFR-4: Test-Driven Development
All functional code changes (PRs 1-5) must follow a test-driven development approach:
- Write or update tests first to capture expected behavior
- Verify tests pass with existing implementation
- Make the code changes
- Verify tests still pass after changes
- Add additional tests if edge cases are discovered

This ensures behavioral equivalence is verified, not assumed.

---

## 4. Implementation Phases

### Phase 1: Deprecation Fixes (PRs 1-5)

These PRs address compiler deprecation warnings with minimal file changes.

See [implementation-plan.md](./implementation-plan.md) for detailed PR breakdown.

### Phase 2: Javadoc Additions (PRs 6-11)

These PRs add missing Javadoc documentation, split by module and subsystem to stay within PR size limits.

See [implementation-plan.md](./implementation-plan.md) for detailed PR breakdown.

---

## 5. Testing Strategy

### 5.1 Per-PR Testing

Each PR must pass:
```bash
mvn clean install -PCI -Prelease
```

### 5.2 Verification Checklist

- [ ] Build succeeds without new errors
- [ ] All existing tests pass
- [ ] Warning count decreases (verify with build output)
- [ ] No behavioral changes detected

### 5.3 Regression Testing

For deprecation fix PRs (1-5):
- Run full test suite including integration tests
- Verify no performance regression
- Check for memory leaks (especially PR 1)

For Javadoc PRs (6-11):
- Verify generated Javadoc is valid HTML
- Check for broken links in documentation

---

## 6. Rollout Plan

### 6.1 PR Sequence

```
Week 1: PRs 1-2 (finalize, @Component) - Low risk, quick wins
Week 2: PRs 3-4 (FunctionUtils) - Medium risk, cohesive changes
Week 3: PR 5 (Interface deprecation) - Medium risk
Week 4+: PRs 6-11 (Javadoc) - Low risk, can be parallelized
```

### 6.2 Review Guidelines

- Each PR requires at least one reviewer
- Deprecation PRs (1-5) should have additional scrutiny for behavioral changes
- Javadoc PRs (6-11) can use lighter-weight review

### 6.3 Merge Strategy

- All PRs target `develop` branch
- Squash merge preferred for clean history
- Run CI build after each merge to verify cumulative changes

---

## 7. Risks and Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Behavioral change from deprecation fix | High | Low | Extensive testing, careful code review |
| Large PR scope creep | Medium | Medium | Strict file count limits, split PRs if needed |
| Merge conflicts between Javadoc PRs | Low | Medium | Merge sequentially, rebase as needed |
| Missing replacement API | Medium | Low | Research before implementation, fallback patterns |

---

## 8. Open Questions

1. **FunctionUtils replacements**: Need to read `FunctionUtils.java` to identify exact replacement methods
2. **INcNameItem replacement**: Need to identify the intended replacement type
3. **Javadoc style**: Should follow existing project conventions (need to identify)

---

## 9. Related Documents

- [Implementation Plan](./implementation-plan.md) - Detailed PR breakdown
- [Warning Analysis](./warning-analysis.md) - Full warning categorization
- [CLAUDE.md](../../CLAUDE.md) - Development guide
- [CONTRIBUTING.md](../../CONTRIBUTING.md) - Contribution guidelines
