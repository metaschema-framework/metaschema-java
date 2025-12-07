# Warning Analysis: Build Output Review

This document provides a detailed analysis of warnings from the build output.

**Build Command**: `mvn clean install -PCI -Prelease`
**Build Status**: SUCCESS
**Analysis Date**: 2025-12-06

---

## Summary

| Category | Count | Fixable | In Scope |
|----------|-------|---------|----------|
| Javadoc (source code) | ~285 | Yes | Yes |
| Javadoc (generated code) | ~98 | No | No |
| Deprecation-for-removal | 28 | Yes | Yes |
| finalize() usage | 3 | Yes | Yes |
| Maven @Component | 3 | Yes | Yes |
| Module warnings | 9 | No | No (per user) |

---

## 1. finalize() Deprecation Warnings

**Count**: 3
**Severity**: Warning (will become error in future Java)

| File | Line | Context |
|------|------|---------|
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/AbstractContainerModelSupport.java` | 89 | Resource cleanup |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/DefaultBindingContext.java` | 268 | Binding context cleanup |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/model/metaschema/impl/AbstractAbsoluteModelGenerator.java` | 223 | Generator cleanup |

**Recommended Fix**: Replace with `java.lang.ref.Cleaner` API or remove if unnecessary.

---

## 2. Maven @Component Deprecation

**Count**: 3 (same file)
**Severity**: Warning

| File | Line | Field |
|------|------|-------|
| `metaschema-maven-plugin/src/main/java/gov/nist/secauto/metaschema/maven/plugin/AbstractMetaschemaMojo.java` | 102 | Multiple `@Component` fields |

**Recommended Fix**: Replace `@Component` with `@Inject` from JSR-330.

---

## 3. FunctionUtils Deprecation Warnings

**Count**: ~15
**Severity**: Warning (forRemoval=true)

### 3.1 toNumeric() Methods

| File | Line(s) |
|------|---------|
| `core/.../cst/math/AbstractBasicArithmeticExpression.java` | 129, 130 |
| `core/.../cst/math/Modulo.java` | 59, 60 |
| `core/.../cst/math/Multiplication.java` | 160, 162 |
| `core/.../function/library/FnAvg.java` | 96 |
| `core/.../function/library/FnMinMax.java` | 201 |
| `core/.../function/library/FnSum.java` | 137 |

### 3.2 countTypes() / getTypes() Methods

| File | Line(s) |
|------|---------|
| `core/.../function/library/FnMinMax.java` | 234 |
| `core/.../function/library/MpRecurseDepth.java` | 83 |

### 3.3 requireType() / requireTypeOrNull() Methods

| File | Line(s) |
|------|---------|
| `core/.../IMetapathExpression.java` | 38 |
| `core/.../function/library/FnBaseUri.java` | 76 |
| `core/.../function/library/FnData.java` | 70 |
| `core/.../function/library/FnDocumentUri.java` | 72 |
| `core/.../function/library/FnPath.java` | 76 |
| `core/src/test/.../FunctionTestBase.java` | 73, 74 |

---

## 4. INcNameItem Deprecation

**Count**: ~4
**Severity**: Warning

| File | Line(s) | Usage |
|------|---------|-------|
| `core/.../datatype/adapter/NcNameAdapter.java` | 26, 47, 65, 67 | Interface usage |
| `core/.../item/atomic/INcNameItem.java` | 47 | Deprecated interface |
| `core/.../item/atomic/impl/NcNameItemImpl.java` | 21 | Implementation |

---

## 5. Other Interface Deprecation

### 5.1 WellKnown Methods

| File | Line(s) | Method |
|------|---------|--------|
| `core/.../metapath/StaticContext.java` | 73 | `getWellKnownPrefixesToNamespaces()` |
| `core/.../metapath/StaticContext.java` | 90 | `getWellKnownURIsToPrefixes()` |

### 5.2 ISequence.getValue()

| File | Line(s) | Method |
|------|---------|--------|
| `core/.../metapath/impl/AbstractSequence.java` | 50 | `getValue()` |

---

## 6. Javadoc Warnings by Module

### 6.1 Core Module (~130 warnings)

**High-Warning Files:**
- `ExceptionUtils.java` - 10 warnings
- `IConstraint.java` - 8 warnings
- Various model interfaces - 1-5 warnings each

**Categories:**
- Missing `@param` tags on interface methods
- Missing `@return` tags on accessor methods
- Missing class-level documentation

### 6.2 Databind Module (~90 warnings)

**High-Warning Files:**
- `binding/AssemblyModel.java` - 72 warnings (highest in codebase)
- Various binding classes - 1-5 warnings each

### 6.3 Schemagen Module (~60 warnings)

**High-Warning Files:**
- `ModuleIndex.java` - 23 warnings
- Schema writer classes - 5-10 warnings each

### 6.4 Maven Plugin Module (~13 warnings)

**Files:**
- Mojo classes
- Configuration classes

### 6.5 Other Modules (~10 warnings)

- CLI module
- Remaining utility modules

---

## 7. Out of Scope Warnings

### 7.1 Module Warnings (9)

Caused by automatic modules from third-party dependencies. Cannot be fixed without upstream changes.

### 7.2 Generated Code Warnings (~98)

Located in `target/generated-sources/`. Will regenerate on each build.

---

## 8. Warning Resolution Priority

| Priority | Category | Count | Rationale |
|----------|----------|-------|-----------|
| 1 | finalize() | 3 | Future Java incompatibility |
| 2 | Deprecation-for-removal | 28 | API stability |
| 3 | @Component | 3 | Maven plugin best practices |
| 4 | Javadoc | ~285 | Code quality |
