# Implementation Plan: Date/Time Formatting Functions

This document details the implementation of `fn:format-integer`, `fn:format-dateTime`, `fn:format-date`, and `fn:format-time` for [issue #283](https://github.com/metaschema-framework/metaschema-java/issues/283).

---

## Prerequisites

- Working build from `develop` branch: `mvn install -DskipTests`
- Familiarity with existing function patterns (e.g., `FnAdjustDateTimeToTimezone`, `FnSubstring`)
- XPath Functions 3.1 spec sections [4.6.1](https://www.w3.org/TR/xpath-functions-31/#func-format-integer) and [9.8](https://www.w3.org/TR/xpath-functions-31/#formatting-dates-and-times)

---

## Test-Driven Development Requirement

All functional code changes must follow TDD:

1. Write or update tests first to capture expected behavior
2. Verify tests fail with existing implementation
3. Make the code changes
4. Verify tests pass after changes

---

## PR 1: Formatting Functions (fn:format-integer, fn:format-dateTime, fn:format-date, fn:format-time)

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~15 |
| **Risk Level** | Medium |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | In Progress |
| **Pull Request** | [#659](https://github.com/metaschema-framework/metaschema-java/pull/659) |

### Files to Create

| File | Description |
|------|-------------|
| `core/src/main/java/.../function/FormatFunctionException.java` | Exception class for format function errors (FODF prefix, FODF1310) |
| `core/src/main/java/.../function/FormatDateTimeFunctionException.java` | Exception class for FOFD error codes (FOFD1340, FOFD1350) |
| `core/src/main/java/.../function/library/FnFormatInteger.java` | `fn:format-integer` function (2-arg and 3-arg) |
| `core/src/main/java/.../function/library/DateTimeFormatUtil.java` | Picture string parser and formatting algorithm |
| `core/src/main/java/.../function/library/FnFormatDateTime.java` | `fn:format-dateTime` function (2-arg and 5-arg) |
| `core/src/main/java/.../function/library/FnFormatDate.java` | `fn:format-date` function (2-arg and 5-arg) |
| `core/src/main/java/.../function/library/FnFormatTime.java` | `fn:format-time` function (2-arg and 5-arg) |
| `core/src/test/java/.../function/FormatFunctionExceptionTest.java` | Unit tests for FODF exception |
| `core/src/test/java/.../function/FormatDateTimeFunctionExceptionTest.java` | Unit tests for FOFD exception |
| `core/src/test/java/.../function/library/FnFormatIntegerTest.java` | Unit tests for `fn:format-integer` |
| `core/src/test/java/.../function/library/DateTimeFormatUtilTest.java` | Unit tests for picture string parser and formatting |
| `core/src/test/java/.../function/library/FnFormatDateTimeTest.java` | Integration tests for `fn:format-dateTime` |
| `core/src/test/java/.../function/library/FnFormatDateTest.java` | Integration tests for `fn:format-date` |
| `core/src/test/java/.../function/library/FnFormatTimeTest.java` | Integration tests for `fn:format-time` |

### Files to Modify

| File | Changes |
|------|---------|
| `core/src/main/java/.../function/library/DefaultFunctionLibrary.java` | Register 8 function signatures; remove P2 comments for format-integer, format-date, format-dateTime, format-time |

### Implementation Approach

#### Phase 1: Exception Classes

1. Create `FormatFunctionExceptionTest.java`:
   - Test construction with `FODF1310` (invalid format token) error code
   - Test error message formatting
   - Test `getCode()` returns correct value

2. Create `FormatFunctionException.java`:
   - Extend `FunctionMetapathError`
   - Prefix: `"FODF"`
   - Constants:
     - `INVALID_FORMAT_TOKEN = 1310` (FODF1310)

3. Create `FormatDateTimeFunctionExceptionTest.java`:
   - Test construction with `FOFD1340` (invalid picture string) error code
   - Test construction with `FOFD1350` (component not available) error code
   - Test error message formatting

4. Create `FormatDateTimeFunctionException.java`:
   - Extend `FunctionMetapathError`
   - Prefix: `"FOFD"`
   - Constants:
     - `INVALID_PICTURE_STRING = 1340` (FOFD1340)
     - `COMPONENT_NOT_AVAILABLE = 1350` (FOFD1350)

5. Verify tests pass.

#### Phase 2: fn:format-integer

1. Create `FnFormatIntegerTest.java` (extends `ExpressionTestBase`) with comprehensive tests:

   **Decimal digit patterns:**
   - `format-integer(123, '1')` → `"123"`
   - `format-integer(123, '0000')` → `"0123"`
   - `format-integer(123, '01')` → `"123"` (never truncated)
   - `format-integer(0, '1')` → `"0"`
   - `format-integer(-123, '1')` → `"-123"` (negative prepends minus)

   **Alphabetic sequences:**
   - `format-integer(1, 'a')` → `"a"`
   - `format-integer(26, 'a')` → `"z"`
   - `format-integer(27, 'a')` → `"aa"`
   - `format-integer(1, 'A')` → `"A"`
   - `format-integer(7, 'a')` → `"g"`

   **Roman numerals:**
   - `format-integer(1, 'i')` → `"i"`
   - `format-integer(4, 'i')` → `"iv"`
   - `format-integer(57, 'I')` → `"LVII"`
   - `format-integer(1999, 'I')` → `"MCMXCIX"`

   **Words:**
   - `format-integer(123, 'w')` → `"one hundred and twenty-three"` (or similar English)
   - `format-integer(1, 'W')` → `"ONE"`
   - `format-integer(21, 'Ww')` → `"Twenty-One"` (or similar)

   **Ordinal modifier:**
   - `format-integer(1, '1;o', 'en')` → `"1st"`
   - `format-integer(2, '1;o', 'en')` → `"2nd"`
   - `format-integer(3, '1;o', 'en')` → `"3rd"`
   - `format-integer(4, '1;o', 'en')` → `"4th"`
   - `format-integer(11, '1;o', 'en')` → `"11th"`
   - `format-integer(21, '1;o', 'en')` → `"21st"`

   **Grouping separators:**
   - `format-integer(1000000, '#,##0')` → `"1,000,000"`
   - `format-integer(15, '#,##0')` → `"15"`
   - `format-integer(1000000, "0'000")` → `"1'000'000"`

   **Empty sequence:**
   - `format-integer((), '1')` → `""` (zero-length string)

   **Error cases:**
   - Invalid format token → FODF1310

   **Context dependency:**
   - 2-arg: context-dependent (default language)
   - 3-arg: context-independent

2. Implement `FnFormatInteger.java`:
   - `SIGNATURE_TWO_ARG` and `SIGNATURE_THREE_ARG`
   - 2-arg: `.contextDependent()`, 3-arg: `.contextIndependent()`
   - Handle primary format token parsing (decimal-digit-pattern, `a`/`A`, `i`/`I`, `w`/`W`/`Ww`)
   - Handle format modifier parsing (`;` separator, `c`/`o`, `a`/`t`)
   - Implement decimal digit pattern formatting with grouping separators
   - Implement alphabetic, roman numeral, and word formatting
   - Implement ordinal suffix for English

3. Verify all format-integer tests pass.

#### Phase 3: Picture String Parser

1. Add parser tests to `DateTimeFormatUtilTest.java`:

   **Literal text parsing:**
   - `"hello"` → single literal component `"hello"`
   - `""` (empty string) → empty component list
   - `"  "` (whitespace) → single literal component `"  "`

   **Escaped brackets:**
   - `"[["` → literal `[`
   - `"]]"` → literal `]`
   - `"[[value]]"` → literal `[value]`
   - `"[[[Y0001]-[M01]-[D01]]]"` → `[`, date, `]` (from spec example)

   **Simple variable markers:**
   - Test all 16 markers: Y, M, D, d, F, W, w, H, h, P, m, s, f, Z, z, C, E
   - Whitespace inside markers is ignored: `"[ Y 0001 ]"` = `"[Y0001]"`

   **Markers with first presentation modifier:**
   - `"[Y0001]"` → year, decimal digit pattern `0001`
   - `"[M01]"` → month, decimal digit pattern `01`
   - `"[MNn]"` → month, name title-case
   - `"[MN]"` → month, name uppercase
   - `"[Mn]"` → month, name lowercase
   - `"[Mi]"` → month, roman lowercase
   - `"[MI]"` → month, roman uppercase
   - `"[Mw]"` → month, words lowercase
   - `"[Y9,999]"` → year with grouping separator (comma before width)

   **Markers with second presentation modifier:**
   - `"[D1o]"` → day, decimal, ordinal
   - `"[D1c]"` → day, decimal, cardinal (same as no modifier)
   - `"[Dwo]"` → day, words, ordinal (e.g., "first")

   **Markers with width modifiers:**
   - `"[M,2]"` → minimum width 2
   - `"[M,2-2]"` → exact width 2
   - `"[MNn,*-3]"` → name max 3 chars
   - `"[MNn,3-3]"` → name exactly 3 chars
   - `"[Y,4-4]"` → year padded/truncated to 4

   **Complex picture strings:**
   - `"[Y0001]-[M01]-[D01]"` → ISO date format
   - `"[D] [MNn] [Y]"` → "5 March 2026"
   - `"[FNn], [D1o] [MNn] [Y]"` → "Thursday, 5th March 2026"
   - `"[h]:[m01]:[s01] [P]"` → 12-hour time with AM/PM

   **Error cases:**
   - `"[X]"` → FOFD1340 (unknown marker)
   - `"[Y"` → FOFD1340 (unmatched bracket)
   - `"]"` alone → FOFD1340 (unmatched bracket)
   - `"[M,0]"` → FOFD1340 (min width < 1)
   - `"[M,3-1]"` → FOFD1340 (max < min)

2. Implement `DateTimeFormatUtil.parsePictureString()`:
   - Parse picture string character by character
   - Build list of `FormatComponent` objects (inner classes or records)
   - `LiteralComponent(String text)`
   - `VariableMarkerComponent(char specifier, String firstModifier, Character secondModifier, Integer minWidth, Integer maxWidth)`
   - Handle escape sequences `[[` and `]]`
   - Ignore whitespace within variable markers
   - Parse comma-separated width modifier
   - Throw `FormatDateTimeFunctionException(INVALID_PICTURE_STRING, ...)` on syntax errors

3. Verify parser tests pass.

#### Phase 4: Formatting Engine

1. Add formatting tests to `DateTimeFormatUtilTest.java`:

   **Year formatting (spec section 9.8.4.4 — modulo rule):**
   - `[Y]` with 2026 → `"2026"` (full year, default)
   - `[Y0001]` with 2026 → `"2026"` (4-digit)
   - `[Y01]` with 2026 → `"26"` (modulo 10^2 because 2 digits in pattern)
   - `[Y,4-4]` with year 5 → `"0005"` (padded to min width)
   - `[Y1]` with 2026 → `"2026"` (single-digit = no truncation, N=infinity)
   - `[Y]` with negative year → minus sign prepended

   **Month formatting:**
   - `[M]` with month 3 → `"3"` (default: decimal `1`)
   - `[M01]` with month 3 → `"03"`
   - `[MNn]` with month 3 → `"March"`
   - `[MN]` with month 3 → `"MARCH"`
   - `[Mn]` with month 3 → `"march"`
   - `[MNn,*-3]` with month 3 → `"Mar"` (abbreviated)
   - `[MNn,3-3]` with month 3 → `"Mar"` (exact 3)
   - `[MI]` with month 3 → `"III"`
   - `[Mi]` with month 3 → `"iii"`
   - All 12 months for name formatting

   **Day formatting:**
   - `[D]` with day 5 → `"5"` (default: decimal `1`)
   - `[D01]` with day 5 → `"05"`
   - `[D1o]` with day 1 → `"1st"` (ordinal via second modifier)
   - `[D1o]` with day 2 → `"2nd"`
   - `[D1o]` with day 3 → `"3rd"`
   - `[D1o]` with day 4 → `"4th"`
   - `[D1o]` with day 11 → `"11th"` (special case)
   - `[D1o]` with day 12 → `"12th"`
   - `[D1o]` with day 13 → `"13th"`
   - `[D1o]` with day 21 → `"21st"`
   - `[D1o]` with day 31 → `"31st"`

   **Day of year (`d`):**
   - `[d]` with Jan 1 → `"1"`
   - `[d]` with Dec 31 (non-leap) → `"365"`
   - `[d]` with Dec 31 (leap) → `"366"`
   - `[d001]` with day 5 → `"005"`

   **Day of week (`F`):**
   - `[F]` with Monday → `"monday"` (default: name `n`)
   - `[F1]` with Monday → `"1"` (ISO: Mon=1)
   - `[F1]` with Sunday → `"7"`
   - `[FNn]` with Monday → `"Monday"`
   - `[FNn,*-3]` with Wednesday → `"Wed"`
   - All 7 days for name formatting

   **Week of year (`W`) and week of month (`w`):**
   - `[W]` with first ISO week → `"1"`
   - `[W01]` with week 1 → `"01"`
   - `[w]` for various dates

   **Hour formatting (24-hour `H`, default: `1`):**
   - `[H]` with hour 0 → `"0"` (midnight)
   - `[H01]` with hour 0 → `"00"`
   - `[H]` with hour 13 → `"13"`
   - `[H]` with hour 23 → `"23"`

   **Hour formatting (12-hour `h`, default: `1`):**
   - `[h]` with hour 0 → `"12"` (midnight)
   - `[h]` with hour 12 → `"12"` (noon)
   - `[h]` with hour 13 → `"1"`
   - `[h]` with hour 23 → `"11"`

   **AM/PM (`P`, default: name `n`):**
   - `[P]` with hour 0 → `"am"` (default: lowercase name)
   - `[PN]` with hour 0 → `"AM"`
   - `[Pn]` with hour 12 → `"pm"`
   - `[PN]` with hour 12 → `"PM"`

   **Minute (default: `01`) and Second (default: `01`):**
   - `[m]` with minute 5 → `"05"` (default is `01`, zero-padded)
   - `[m1]` with minute 5 → `"5"` (explicit single-digit)
   - `[s]` with second 0 → `"00"` (default is `01`)
   - `[s1]` with second 9 → `"9"`

   **Fractional seconds (`f`, spec section 9.8.4.5):**
   - `[f1]` with 123456789 nanos → all digits (single-digit = no constraint)
   - `[f01]` with 100000000 nanos → `"10"` (hundredths)
   - `[f001]` with 123456789 nanos → `"123"` (milliseconds)
   - `[f001]` with 0 nanos → `"000"`
   - `[f1,1-1]` with 123456789 nanos → `"1"` (exactly 1 digit via width)
   - Truncation, not rounding

   **Timezone (`Z`, spec section 9.8.4.6):**
   - `[Z]` with +05:00 → `"+05:00"` (default `01:01`)
   - `[Z]` with -08:00 → `"-08:00"`
   - `[Z]` with UTC → `"+00:00"`
   - `[Z]` with no timezone → `""` (empty output)
   - `[Z0]` with +05:00 → `"+5"`
   - `[Z0]` with +05:30 → `"+5:30"` (minutes appended if non-zero)
   - `[Z0:00]` with +05:00 → `"+5:00"` (always show minutes)
   - `[Z00:00]` with +05:00 → `"+05:00"` (zero-padded)
   - `[Z0000]` with +05:30 → `"+0530"` (no separator)
   - `[Z01:01t]` with UTC → `"Z"` (t modifier)
   - `[Z01:01t]` with +05:00 → `"+05:00"` (non-UTC still signed)
   - `[ZZ]` with UTC → `"Z"` (military)
   - `[ZZ]` with +01:00 → `"A"` (military)
   - `[ZZ]` with -01:00 → `"N"` (military)
   - `[ZZ]` with +12:00 → `"M"` (military)
   - `[ZZ]` with -12:00 → `"Y"` (military)
   - `[ZZ]` with +05:30 → `"+05:30"` (no military letter, fallback)
   - `[ZZ]` with no timezone → `"J"` (local time)
   - `[ZN]` fallback to offset format (no place argument)

   **Timezone `z` (GMT prefix):**
   - `[z]` with +05:00 → `"GMT+05:00"`
   - `[z]` with UTC → `"GMT+00:00"`
   - `[z]` with no timezone → `""` (empty)

   **Calendar (`C`) and Era (`E`):**
   - `[C]` → `"ad"` (default: name `n`, lowercase)
   - `[CN]` → `"AD"`
   - `[E]` with positive year → `"ad"` (default: name `n`)
   - `[EN]` with negative year → `"BC"`

   **Width modifier enforcement:**
   - `[MNn,3-3]` truncates `"January"` to `"Jan"`
   - `[MNn,3-3]` with `"May"` → `"May"` (already 3)
   - `[MNn,*-3]` abbreviates conventionally or truncates
   - Padding: shorter than min → pad with spaces

   **Spec examples (section 9.8.5) as integration tests:**
   - `format-date($d, "[Y0001]-[M01]-[D01]")` → `"2002-12-31"` (with $d = 2002-12-31)
   - `format-date($d, "[D1] [MI] [Y]")` → `"31 XII 2002"`
   - `format-date($d, "[D1o] [MNn], [Y]", "en", (), ())` → `"31st December, 2002"`
   - `format-date($d, "[D01] [MN,*-3] [Y0001]", "en", (), ())` → `"31 DEC 2002"`
   - `format-date($d, "[MNn] [D], [Y]", "en", (), ())` → `"December 31, 2002"`
   - `format-date($d, "[[[Y0001]-[M01]-[D01]]]")` → `"[2002-12-31]"` (escaped brackets)
   - `format-time($t, "[h]:[m01] [PN]", "en", (), ())` → `"3:58 PM"` (with $t = 15:58:45)
   - `format-time($t, "[h]:[m01]:[s01] [Pn]", "en", (), ())` → `"3:58:45 pm"`
   - `format-time($t, "[H01]:[m01]")` → `"15:58"`
   - `format-time($t, "[H01]:[m01]:[s01].[f001]")` → `"15:58:45.762"` (with fractional seconds)
   - `format-dateTime($dt, "[h].[m01][Pn] on [FNn], [D1o] [MNn]")` → `"3.58pm on Tuesday, 31st December"`
   - `format-dateTime($dt, "[M01]/[D01]/[Y0001] at [H01]:[m01]:[s01]")` → `"12/31/2002 at 15:58:45"`

2. Implement `DateTimeFormatUtil`:
   - `parsePictureString(String picture)` → list of components
   - `formatDateTime(ITemporalItem value, String picture, String language, String calendar, String place, Set<Character> allowedMarkers)` → formatted string
   - Delegate integer formatting to `FnFormatInteger` for numeric components
   - Handle timezone formatting per spec section 9.8.4.6
   - Handle year modulo per spec section 9.8.4.4
   - Handle fractional seconds per spec section 9.8.4.5

3. Verify formatting tests pass.

#### Phase 5: Function Classes and Registration

1. Create `FnFormatDateTimeTest.java`, `FnFormatDateTest.java`, `FnFormatTimeTest.java` (extend `ExpressionTestBase`):
   - Use `@ParameterizedTest` with `@MethodSource` pattern
   - Include spec examples from section 9.8.5
   - Test empty sequence → empty sequence
   - Test 2-arg and 5-arg forms
   - Test FOFD1350 for invalid marker/value-type combinations

2. Implement function classes:

   All three follow the same pattern:
   - **Both arities are context-dependent** (depend on implicit timezone)
   - 2-arg: `.contextDependent()` (depends on default calendar, language, place, implicit timezone)
   - 5-arg: `.contextDependent()` (depends on implicit timezone, namespaces)

   ```java
   // Both signatures must use .contextDependent()
   .deterministic()
   .contextDependent()
   .focusIndependent()
   ```

3. Register all 8 signatures in `DefaultFunctionLibrary.java`:
   - Remove P2 comments for `format-integer`, `format-date`, `format-dateTime`, `format-time`
   - Keep P2 comment for `format-number`

   ```java
   // https://www.w3.org/TR/xpath-functions-31/#func-format-integer
   registerFunction(FnFormatInteger.SIGNATURE_TWO_ARG);
   registerFunction(FnFormatInteger.SIGNATURE_THREE_ARG);
   // https://www.w3.org/TR/xpath-functions-31/#func-format-dateTime
   registerFunction(FnFormatDateTime.SIGNATURE_TWO_ARG);
   registerFunction(FnFormatDateTime.SIGNATURE_FIVE_ARG);
   // https://www.w3.org/TR/xpath-functions-31/#func-format-date
   registerFunction(FnFormatDate.SIGNATURE_TWO_ARG);
   registerFunction(FnFormatDate.SIGNATURE_FIVE_ARG);
   // https://www.w3.org/TR/xpath-functions-31/#func-format-time
   registerFunction(FnFormatTime.SIGNATURE_TWO_ARG);
   registerFunction(FnFormatTime.SIGNATURE_FIVE_ARG);
   ```

4. Verify all tests pass: `mvn -pl core test`

5. Run CI build: `mvn clean install -PCI -Prelease`

### Acceptance Criteria

#### fn:format-integer

- [x] `FormatFunctionException` created with FODF1310 error code
- [x] Decimal digit patterns: `1`, `01`, `001`, `0000`, etc.
- [x] Grouping separators: `#,##0`, `#'##0`, etc.
- [x] Alphabetic sequences: `a`, `A`
- [x] Roman numerals: `i`, `I`
- [x] Words: `w`, `W`, `Ww`
- [x] Format modifier: ordinal (`o`) with English suffixes (1st, 2nd, 3rd, 4th, ...)
- [x] Empty sequence returns zero-length string
- [x] Negative values prepend minus sign
- [x] Invalid format token raises FODF1310
- [x] 2-arg and 3-arg signatures registered

#### Date/Time Formatting

- [x] `FormatDateTimeFunctionException` with FOFD1340 and FOFD1350 error codes
- [x] Picture string parser handles all 16 variable markers
- [x] Picture string parser handles first and second presentation modifiers
- [x] Picture string parser handles width modifiers with validation
- [x] Picture string parser handles escaped brackets and literal text
- [x] Picture string parser ignores whitespace within variable markers
- [x] Picture string parser rejects invalid syntax with FOFD1340
- [x] Year formatting uses modulo rule per spec 9.8.4.4
- [x] Fractional seconds use reverse-digit algorithm per spec 9.8.4.5
- [x] Timezone formatting handles all spec 9.8.4.6 variants (numeric, military, name, GMT prefix)
- [x] Timezone produces empty output when value has no timezone (except military `ZZ` → `J`)
- [x] Integer-valued components use `fn:format-integer` rules per spec 9.8.4.3
- [x] `fn:format-dateTime` works with 2-arg and 5-arg signatures
- [x] `fn:format-date` works with 2-arg and 5-arg signatures
- [x] `fn:format-date` rejects time-only markers with FOFD1350
- [x] `fn:format-time` works with 2-arg and 5-arg signatures
- [x] `fn:format-time` rejects date-only and era markers with FOFD1350
- [x] All 8 function signatures registered in `DefaultFunctionLibrary`
- [x] Both arities marked as context-dependent per spec
- [x] Empty sequence input returns empty sequence for all functions
- [x] `$language` defaults to English; unsupported languages fall back
- [x] `$calendar` defaults to Gregorian (`"AD"`); unsupported calendars fall back
- [x] `$place` supports IANA timezone names for timezone adjustment

#### Quality

- [x] Spec examples from section 9.8.5 pass as integration tests
- [x] Javadoc on all public/protected members
- [x] All existing tests still pass
- [x] `mvn clean install -PCI -Prelease` succeeds with no new violations

---

## PR Summary Table

| PR | Description | Files | Risk | Dependencies | Status |
|----|-------------|-------|------|--------------|--------|
| 1 | Formatting functions (format-integer + date/time) | ~15 | Medium | None | In Progress |

**Total PRs**: 1
**Total Files**: ~15

---

## Files Changed Summary

| File | Change Type |
|------|-------------|
| `core/src/main/java/.../function/FormatFunctionException.java` | New |
| `core/src/main/java/.../function/FormatDateTimeFunctionException.java` | New |
| `core/src/main/java/.../function/library/FnFormatInteger.java` | New |
| `core/src/main/java/.../function/library/DateTimeFormatUtil.java` | New |
| `core/src/main/java/.../function/library/FnFormatDateTime.java` | New |
| `core/src/main/java/.../function/library/FnFormatDate.java` | New |
| `core/src/main/java/.../function/library/FnFormatTime.java` | New |
| `core/src/test/java/.../function/FormatFunctionExceptionTest.java` | New |
| `core/src/test/java/.../function/FormatDateTimeFunctionExceptionTest.java` | New |
| `core/src/test/java/.../function/library/FnFormatIntegerTest.java` | New |
| `core/src/test/java/.../function/library/DateTimeFormatUtilTest.java` | New |
| `core/src/test/java/.../function/library/FnFormatDateTimeTest.java` | New |
| `core/src/test/java/.../function/library/FnFormatDateTest.java` | New |
| `core/src/test/java/.../function/library/FnFormatTimeTest.java` | New |
| `core/src/main/java/.../function/library/DefaultFunctionLibrary.java` | Modified |
