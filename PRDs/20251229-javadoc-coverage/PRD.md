# PRD: Full Javadoc Coverage

## Document Information

| Field | Value |
|-------|-------|
| **PRD ID** | JAVADOC-COVERAGE |
| **Status** | Approved |
| **Author** | David Waltermire |
| **Created** | 2025-12-29 |
| **Last Updated** | 2025-12-29 |

---

## 1. Overview

### 1.1 Problem Statement

The metaschema-java project has a goal of full Javadoc coverage for all public and protected members. Currently, there are significant gaps in Javadoc coverage across multiple modules, resulting in:
- 337 `MissingJavadocMethod` Checkstyle warnings
- ~185 Javadoc "no comment" warnings

These gaps affect code maintainability, API usability, and generated documentation quality.

### 1.2 Goals

1. Achieve 100% Javadoc coverage on all public/protected methods
2. Add package-level documentation where missing
3. Ensure all Javadoc follows the project's style guide
4. Eliminate all Javadoc-related Checkstyle warnings

### 1.3 Non-Goals

- Modifying generated code (binding classes, ANTLR output)
- Adding Javadoc to test classes
- Adding Javadoc to private methods
- Changing code behavior or structure

### 1.4 Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| MissingJavadocMethod warnings | 337 | 0 |
| Javadoc "no comment" warnings | ~185 | 0 |
| Build with `-PCI -Prelease` | Pass with warnings | Pass without Javadoc warnings |

---

## 2. Issue Breakdown by Module

### 2.1 schemagen Module

| Metric | Count |
|--------|-------|
| Files affected | 36 |
| MissingJavadocMethod | 154 |

This module is the most isolated and a good starting point.

### 2.2 databind Module

| Metric | Count |
|--------|-------|
| Files affected | 64 |
| MissingJavadocMethod | 176 |
| Package-info warnings | ~99 |

This is the largest module with the most issues.

### 2.3 metaschema-maven-plugin Module

| Metric | Count |
|--------|-------|
| MissingJavadocMethod | 7 |

Small number of issues, can be combined with another PR.

---

## 3. Implementation Strategy

### 3.1 PR Approach

Given the volume of changes (100+ files), split into module-focused PRs:

| PR | Module | Est. Files | Est. Methods |
|----|--------|-----------|--------------|
| 1 | schemagen | 36 | 154 |
| 2 | databind | 64 | 176 |
| 3 | maven-plugin | ~5 | 7 |

### 3.2 Javadoc Guidelines

Follow the project's [Javadoc style guide](../../docs/javadoc-style-guide.md):

1. **First sentence**: Brief summary ending with period
2. **@param tags**: Document all parameters
3. **@return tags**: Document non-void return values
4. **@throws tags**: Document declared exceptions
5. **@Override methods**: Use `{@inheritDoc}` only if adding implementation notes

### 3.3 Package Documentation

For missing package-info.java documentation:
- Add brief package description
- Document package purpose and key classes
- Cross-reference related packages

---

## 4. Verification

After each PR, run:

```bash
mvn clean install -PCI -Prelease
```

Verify:
- All tests pass
- No new Javadoc warnings for the module
- Checkstyle passes without Javadoc violations

---

## 5. Exclusions

The following are excluded from Javadoc requirements:

1. **Generated code**: `target/generated-sources/`, binding classes
2. **Test classes**: All files in `src/test/`
3. **@Override methods**: Unless adding implementation notes
4. **Private members**: Only public/protected require Javadoc
