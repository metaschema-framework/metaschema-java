# PRD: Date/Time Formatting Functions

## Document Information

| Field | Value |
|-------|-------|
| **PRD ID** | FORMAT-DT-001 |
| **Issue** | [#283](https://github.com/metaschema-framework/metaschema-java/issues/283) |
| **Status** | In Progress |
| **Author** | David Waltermire |
| **Created** | 2026-02-07 |
| **Last Updated** | 2026-02-07 |

---

## 1. Overview

### 1.1 Problem Statement

Metapath authors currently have no way to format date, dateTime, or time values for display. While the Metapath function library provides functions to extract individual components (year, month, day, etc.) and adjust timezones, there is no mechanism to produce human-readable formatted strings from temporal values. This limits the utility of Metapath in report generation and user-facing output scenarios.

### 1.2 Goals

1. Implement `fn:format-integer` per [XPath 3.1 section 4.6.1](https://www.w3.org/TR/xpath-functions-31/#func-format-integer) (prerequisite for date/time formatting)
2. Implement `fn:format-dateTime` per [XPath 3.1 section 9.8.1](https://www.w3.org/TR/xpath-functions-31/#func-format-dateTime)
3. Implement `fn:format-date` per [XPath 3.1 section 9.8.2](https://www.w3.org/TR/xpath-functions-31/#func-format-date)
4. Implement `fn:format-time` per [XPath 3.1 section 9.8.3](https://www.w3.org/TR/xpath-functions-31/#func-format-time)
5. Support both 2-argument and 5-argument signatures for all date/time formatting functions
6. Implement all 16 picture string variable markers defined in the spec

### 1.3 Non-Goals

- `fn:format-number` (separate issue scope)
- Non-Gregorian calendar systems beyond basic infrastructure (implementation-defined fallback to Gregorian)
- Locale-aware month/day names beyond English (implementation-defined fallback to English)

### 1.4 Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| Formatting functions available | 0 of 4 | 4 of 4 (format-integer + 3 date/time) |
| Picture string variable markers supported | 0 of 16 | 16 of 16 |
| Function signatures registered | 0 of 8 | 8 of 8 (2 for format-integer + 6 for date/time) |
| Unit test coverage for new code | N/A | 80%+ |
| CI build passes | N/A | All checks green |

---

## 2. Background

### 2.1 Current State

The Metapath function library currently supports:

- **Component extraction**: `fn:year-from-dateTime`, `fn:month-from-date`, `fn:day-from-date`, `fn:hours-from-time`, etc.
- **Timezone adjustment**: `fn:adjust-dateTime-to-timezone`, `fn:adjust-date-to-timezone`, `fn:adjust-time-to-timezone`
- **Current values**: `fn:current-dateTime`, `fn:current-date`, `fn:current-time`
- **Construction**: `fn:dateTime` (combines date and time)

The formatting functions are marked as P2 (priority 2) in `DefaultFunctionLibrary.java` at lines 100-104, with placeholder comments referencing the XPath spec URLs.

### 2.2 Technical Context

**Item type hierarchy**: All temporal items implement `ITemporalItem`, which provides `getYear()`, `getMonth()`, `getDay()`, `getHour()`, `getMinute()`, `getSecond()`, `getNano()`, `getZoneOffset()`, and `hasTimezone()`. Calendar-based items (`IDateItem`, `IDateTimeItem`) extend `ICalendarTemporalItem` which adds `asZonedDateTime()`.

**Function registration**: Functions are final utility classes with static `SIGNATURE` fields built via `IFunction.builder()`. They are registered in `DefaultFunctionLibrary` via `registerFunction()`.

**Namespace**: All standard functions use `MetapathConstants.NS_METAPATH_FUNCTIONS` (`http://csrc.nist.gov/ns/metaschema/metapath-functions`).

**Existing patterns for multi-arity functions**: `FnSubstring` (2-arg and 3-arg), `FnAdjustDateTimeToTimezone` (1-arg and 2-arg) demonstrate separate `SIGNATURE_*` fields and `execute*` handler methods per arity.

---

## 3. Requirements

### 3.1 Functional Requirements

#### FR-0: fn:format-integer (Prerequisite)

Implement `fn:format-integer` per [XPath 3.1 section 4.6.1](https://www.w3.org/TR/xpath-functions-31/#func-format-integer). This function is used by the date/time formatting functions for rendering integer-valued components.

Two signatures:

```text
fn:format-integer($value as xs:integer?, $picture as xs:string) as xs:string
fn:format-integer($value as xs:integer?, $picture as xs:string,
                  $lang as xs:string?) as xs:string
```

Key behaviors:

- If `$value` is empty sequence, returns zero-length string
- Negative values: format absolute value and prepend minus sign
- `$picture` consists of a primary format token, optionally followed by `;` and a format modifier

Primary format tokens:

| Token | Description |
|-------|-------------|
| Decimal digit pattern (`1`, `01`, `001`, `#,##0`, etc.) | Decimal number with optional zero-padding and grouping separators |
| `a` | Lowercase alphabetic: a, b, c, ..., z, aa, ab, ... |
| `A` | Uppercase alphabetic: A, B, C, ..., Z, AA, AB, ... |
| `i` | Lowercase roman: i, ii, iii, iv, v, ... |
| `I` | Uppercase roman: I, II, III, IV, V, ... |
| `w` | Words, lowercase: one, two, three, ... |
| `W` | Words, uppercase: ONE, TWO, THREE, ... |
| `Ww` | Words, title case: One, Two, Three, ... |

Format modifier (after `;`):

- `c` or `o`: Cardinal or ordinal (e.g., `1;o` → `1st`)
- `a` or `t`: Alphabetic or traditional numbering
- Ordinal parenthesized suffix: `o(-th)` for language-specific endings

Decimal digit pattern details:

- `#` = optional-digit-sign, Unicode Nd = mandatory-digit-sign
- All mandatory digits must be from the same digit family
- Grouping separators: non-alphanumeric characters (e.g., `,` in `#,##0`)
- Regular grouping: extrapolated to the left (e.g., `0'000` → `1'000'000`)

Properties:

- 2-arg: deterministic, context-dependent (default language), focus-independent
- 3-arg: deterministic, context-independent, focus-independent

Error: `FODF1310` for invalid format token syntax.

#### FR-1: fn:format-dateTime

Implement the function with two signatures:

```text
fn:format-dateTime($value as xs:dateTime?, $picture as xs:string) as xs:string?
fn:format-dateTime($value as xs:dateTime?, $picture as xs:string,
                   $language as xs:string?, $calendar as xs:string?,
                   $place as xs:string?) as xs:string?
```

Formats an `IDateTimeItem` using the picture string. Returns empty sequence if `$value` is empty. All 16 variable markers are valid for dateTime values.

#### FR-2: fn:format-date

Implement the function with two signatures:

```text
fn:format-date($value as xs:date?, $picture as xs:string) as xs:string?
fn:format-date($value as xs:date?, $picture as xs:string,
               $language as xs:string?, $calendar as xs:string?,
               $place as xs:string?) as xs:string?
```

Formats an `IDateItem` using the picture string. Time-related markers (`H`, `h`, `P`, `m`, `s`, `f`) raise `FOFD1350` if present in the picture string.

#### FR-3: fn:format-time

Implement the function with two signatures:

```text
fn:format-time($value as xs:time?, $picture as xs:string) as xs:string?
fn:format-time($value as xs:time?, $picture as xs:string,
               $language as xs:string?, $calendar as xs:string?,
               $place as xs:string?) as xs:string?
```

Formats an `ITimeItem` using the picture string. Date-related markers (`Y`, `M`, `D`, `d`, `F`, `W`, `w`, `E`) raise `FOFD1350` if present in the picture string.

#### FR-4: Picture String Parsing

Parse XPath 3.1 picture strings containing:

- **Literal text**: Characters outside `[...]` brackets
- **Escaped brackets**: `[[` → `[`, `]]` → `]`
- **Variable markers**: `[component presentation? width?]`

All 16 variable markers:

| Marker | Description | Valid in dateTime | Valid in date | Valid in time |
|--------|-------------|:-:|:-:|:-:|
| `Y` | Year | yes | yes | no |
| `M` | Month in year | yes | yes | no |
| `D` | Day in month | yes | yes | no |
| `d` | Day in year | yes | yes | no |
| `F` | Day of week | yes | yes | no |
| `W` | Week of year | yes | yes | no |
| `w` | Week of month | yes | yes | no |
| `H` | Hour (24-hour, 0-23) | yes | no | yes |
| `h` | Hour (12-hour, 1-12) | yes | no | yes |
| `P` | AM/PM marker | yes | no | yes |
| `m` | Minute | yes | no | yes |
| `s` | Second | yes | no | yes |
| `f` | Fractional seconds | yes | no | yes |
| `Z` | Timezone (offset format, see FR-10) | yes | yes | yes |
| `z` | Timezone (same as Z with `GMT` prefix, see FR-10) | yes | yes | yes |
| `C` | Calendar name | yes | yes | yes |
| `E` | Era name | yes | yes | no |

#### FR-5: Presentation Modifiers

A variable marker consists of a component specifier followed optionally by a first presentation modifier, an optional second presentation modifier, and an optional width modifier. Whitespace within a variable marker is ignored.

The **first presentation modifier** controls how the value is rendered:

| Format | Description | Example |
|--------|-------------|---------|
| `1` | Decimal number (default for most numeric components) | `[M]` or `[M1]` → `3` |
| `01` | Zero-padded decimal (default for `m`, `s`) | `[m01]` → `05` |
| `001`, `0001`, etc. | Zero-padded to digit count | `[Y0001]` → `2026` |
| `N` | Name, uppercase | `[MN]` → `MARCH` |
| `n` | Name, lowercase (default for `F`, `P`, `C`, `E`) | `[Fn]` → `monday` |
| `Nn` | Name, title case | `[MNn]` → `March` |
| `i` | Roman numeral, lowercase | `[Mi]` → `iii` |
| `I` | Roman numeral, uppercase | `[MI]` → `III` |
| `w` | Words, lowercase | `[Yw]` → `two thousand twenty-six` |
| `W` | Words, uppercase | `[YW]` → `TWO THOUSAND TWENTY-SIX` |
| `Ww` | Words, title case | `[YWw]` → `Two Thousand Twenty-Six` |

Default presentation modifiers per spec:

| Component | Default |
|-----------|---------|
| Y, M, D, d, W, w, H, h, f | `1` (decimal) |
| F, P, C, E | `n` (name, lowercase) |
| m, s | `01` (zero-padded two digits) |
| Z, z | `01:01` (zero-padded hours and minutes with separator) |

The **second presentation modifier** (optional, single character after first modifier) controls numbering style:

| Modifier | Meaning |
|----------|---------|
| `a` or `t` | Alphabetic or traditional numbering (implementation-defined default) |
| `c` or `o` | Cardinal or ordinal numbering: `[D1o]` → `1st`, `[D1c]` → `1` |

If a comma appears in the format token, the last comma introduces the width modifier; all other commas are grouping separators (e.g., `[Y9,999,*]` → `2,026`).

#### FR-6: Width Modifiers

Width modifier syntax: `,min-width` or `,min-width-max-width`

- `min-width`: unsigned integer or `*` (no minimum)
- `max-width`: unsigned integer or `*` (no maximum, the default if omitted)
- Error `FOFD1340` if min < 1, max < 1, or max < min

Examples:

- `[M,2]` — minimum width 2, no maximum
- `[M,2-2]` — exactly 2 characters
- `[MNn,*-3]` — name, maximum 3 characters (abbreviation)
- `[MNn,3-3]` — name, exactly 3 characters

A format token with multiple digits (e.g., `001`, `9999`) implicitly sets min and max width to the digit count; an explicit width modifier overrides this.

For name-based modifiers (`N`, `n`, `Nn`): if shorter than min, pad with spaces; if longer than max, abbreviate using conventional abbreviation or truncation.

#### FR-7: Language, Calendar, and Place Parameters

- `$language`: Language tag (e.g., `"en"`) per `xml:lang`. Default to English. Unsupported languages fall back to English with no error (implementation-defined).
- `$calendar`: Calendar designator as an `EQName`. Default to `"AD"` (Gregorian). Must be a valid EQName or raises `FOFD1340`. Unsupported calendars fall back to Gregorian with a `[Calendar: AD]` prefix in output.
- `$place`: Country code ([ISO 3166-1]) or IANA timezone name. Implementation-defined; in this implementation, IANA timezone names are used to adjust the value's timezone offset before formatting. Unrecognized values are ignored.

The 2-arg form is equivalent to calling the 5-arg form with `$language`, `$calendar`, and `$place` all set to empty sequence.

#### FR-8: Function Properties

Per the W3C spec, these functions are **context-dependent**:

- **2-arg form**: deterministic, context-dependent, focus-independent. Depends on default calendar, default language, default place, and implicit timezone.
- **5-arg form**: deterministic, context-dependent, focus-independent. Depends on implicit timezone and namespaces.

#### FR-9: Error Handling

Per the W3C spec ([section 9.8.4](https://www.w3.org/TR/xpath-functions-31/#date-time-formatting)):

- `FOFD1340`: Invalid picture string syntax (unmatched brackets, unknown marker, invalid width modifier), or invalid `$calendar` value (not a valid EQName or unrecognized no-namespace designator)
- `FOFD1350`: Component specifier refers to components not available in the given value type (e.g., `[H]` in `format-date`, `[Y]` in `format-time`)

Use of valid but unsupported options in `$language`, `$calendar`, or `$place` is **not an error** — the implementation must output a fallback representation.

#### FR-10: Timezone Formatting (Z and z)

Per spec section 9.8.4.6, timezone formatting has special rules:

- If the value has **no timezone**, timezone components produce empty output (except military format `ZZ` → `"J"`)
- `[Z]` default (`01:01`): signed offset with separator, e.g., `+05:00`, `-08:00`
- `[Z0]` or `[Z01]`: hours only, minutes appended with colon only if non-zero, e.g., `-5`, `+03`, `+5:30`
- `[Z0:00]` or `[Z01:01]`: hours and minutes with separator, always, e.g., `-5:00`, `+05:00`
- `[Z0000]` or `[Z0001]`: hours and minutes with no separator, e.g., `-0500`, `+0530`
- `[Z01:01t]`: second modifier `t` causes UTC to render as `Z` instead of `+00:00`
- `[ZZ]`: military timezone letter (A-M for +01 to +12, N-Y for -01 to -12, Z for UTC, J for local/no TZ)
- `[ZN]`: timezone name (e.g., `EST`, `CET`); falls back to `+01:01` format if name unavailable
- `[z...]`: same as `[Z...]` but prefixed with `GMT` (or localized equivalent); prefix omitted when timezone is identified by name

| Variable marker | -05:00 | +00:00 | +05:30 |
|-----------------|--------|--------|--------|
| `[Z]` or `[Z01:01]` | `-05:00` | `+00:00` | `+05:30` |
| `[Z0]` | `-5` | `+0` | `+5:30` |
| `[Z0:00]` | `-5:00` | `+0:00` | `+5:30` |
| `[Z00:00]` | `-05:00` | `+00:00` | `+05:30` |
| `[Z0000]` | `-0500` | `+0000` | `+0530` |
| `[Z00:00t]` | `-05:00` | `Z` | `+05:30` |
| `[ZZ]` | `R` | `Z` | `+05:30` |
| `[z]` | `GMT-05:00` | `GMT+00:00` | `GMT+05:30` |

#### FR-11: Year Formatting

Per spec section 9.8.4.4, the year value output is the absolute value modulo 10^N, where N is:

1. If width modifier specifies a finite max width → that max width
2. Else if first presentation modifier is a decimal-digit-pattern with W digits (W >= 2) → W
3. Otherwise → infinity (full year)

Example: `[Y01]` outputs 2-digit year (`26` for year 2026), `[Y0001]` outputs 4-digit year.

#### FR-12: Fractional Seconds Formatting

Per spec section 9.8.4.5, fractional seconds use a reverse-digit algorithm:

1. A single-digit format token (`[f1]`) retains all fractional digits
2. Multi-digit tokens set precision: `[f001]` = 3 decimal places, `[f01]` = 2
3. Width modifier overrides: `[f1,1-1]` = exactly 1 digit
4. The algorithm reverses the decimal digit pattern, reverses the fractional value (removing trailing zeros), formats using `fn:format-integer` rules, then reverses the result
5. Excess digits are truncated (not rounded)

### 3.2 Non-Functional Requirements

#### NFR-1: Spec Conformance

Implementation must conform to XPath 3.1 specification sections 9.8.1-9.8.5. The spec is the authoritative reference for any ambiguous behavior.

#### NFR-2: Consistency with Existing Functions

Follow the same patterns used by existing Metapath function implementations:

- Final utility class with private constructor
- Static `SIGNATURE` fields using `IFunction.builder()`
- Private `execute*` handler methods with `@SuppressWarnings("unused")`
- Public static implementation methods for direct programmatic access
- SpotBugs annotations (`@NonNull`, `@Nullable`)

#### NFR-3: Performance

Picture string parsing should be efficient for repeated use. Consider caching parsed picture strings if profiling indicates a bottleneck (not required in initial implementation).

#### NFR-4: Testability

The picture string parser and formatting logic must be independently testable, separate from the Metapath function invocation machinery.

---

## 4. Architecture

### 4.1 Component Design

```text
FnFormatDateTime.java  ──┐
FnFormatDate.java      ──┼──▶  DateTimeFormatUtil.java  ──▶  PictureStringParser
FnFormatTime.java      ──┘       (formatting logic)           (parses picture strings)
                                        │                            │
                                        ▼                            ▼
                                  ITemporalItem methods       FormatComponent[]
                                  (extract values)            (intermediate repr)
```

#### PictureStringParser

Parses a picture string into a list of `FormatComponent` objects:

- `LiteralComponent` — plain text to include verbatim
- `VariableMarkerComponent` — a marker letter with optional presentation modifier and width constraint

#### DateTimeFormatUtil

Contains the shared formatting algorithm:

1. Parse the picture string via `PictureStringParser`
2. For each component:
   - Literal: append text to output
   - Variable marker: extract the value from `ITemporalItem`, apply presentation modifier and width, append to output
3. Return the assembled string

Validates that markers are valid for the temporal type (date-only, time-only, or dateTime).

#### Function Classes

Each function class (`FnFormatDateTime`, `FnFormatDate`, `FnFormatTime`):

- Defines 2-arg and 5-arg `SIGNATURE` constants
- Has `executeTwoArg` and `executeFiveArg` handler methods
- Delegates to `DateTimeFormatUtil` with the appropriate `ITemporalItem` and allowed marker set

### 4.2 Package Location

All new classes go in:

```text
core/src/main/java/dev/metaschema/core/metapath/function/library/
```

The parser and utility classes are package-private (used only by the function classes in the same package). The exception class goes in:

```text
core/src/main/java/dev/metaschema/core/metapath/function/
```

---

## 5. Testing Strategy

### 5.1 Test Approach

All development follows TDD. Tests are written first, verified to fail, then implementation makes them pass.

### 5.2 Test Classes

#### PictureStringParserTest

Unit tests for parsing picture strings into component lists:

- Simple literal text (`"hello"`)
- Single variable marker (`"[Y0001]"`)
- Mixed literal and markers (`"[Y0001]-[M01]-[D01]"`)
- Escaped brackets (`"[["` → `[`, `"]]"` → `]`)
- All 16 variable marker letters
- Presentation modifiers: numeric (`1`, `01`, `0001`), name (`N`, `n`, `Nn`), roman (`i`, `I`), word (`w`, `W`, `Ww`), ordinal (`o`)
- Width modifiers: min only, min-max, `*-max`, exact
- Nested/complex picture strings
- Error cases: unmatched `[`, unknown marker letter, malformed modifiers

#### DateTimeFormatUtilTest

Unit tests for the formatting algorithm:

- **Year formatting**: 4-digit, 2-digit, zero-padded, word form
- **Month formatting**: numeric, zero-padded, full name, abbreviated name (3-char), title case, uppercase, lowercase
- **Day formatting**: numeric, zero-padded, ordinal (`1st`, `2nd`, `3rd`, `11th`, `21st`)
- **Day of year**: 1-366 range, zero-padded
- **Day of week**: numeric, full name (`Monday`), abbreviated
- **Week of year**: ISO week number
- **Week of month**: 1-5
- **Hour formatting**: 24-hour, 12-hour, zero-padded, midnight/noon edge cases
- **AM/PM**: uppercase, lowercase, title case
- **Minute/Second**: zero-padded
- **Fractional seconds**: variable precision, trailing zero handling
- **Timezone**: offset format (`+05:00`, `-08:00`, `Z`), name format
- **Calendar**: Gregorian identifier
- **Era**: AD/BC
- **Roman numerals**: months I-XII
- **Width constraints**: truncation, padding, exact width
- **Empty sequence handling**: null/empty input returns empty sequence
- **Error cases**: time markers on date values, date markers on time values

#### FnFormatDateTimeTest

Integration tests via Metapath expression evaluation:

- ISO 8601 format: `fn:format-dateTime($dt, "[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]")`
- Human-readable: `fn:format-dateTime($dt, "[D] [MNn] [Y]")`
- US format: `fn:format-dateTime($dt, "[M]/[D]/[Y0001]")`
- 12-hour time: `fn:format-dateTime($dt, "[h]:[m01] [P]")`
- With timezone: `fn:format-dateTime($dt, "[Y]-[M01]-[D01] [H01]:[m01] [Z]")`
- 5-arg form with language parameter

#### FnFormatDateTest

Integration tests:

- Standard date formats
- Ordinal dates: `fn:format-date($d, "[D1o] [MNn] [Y]")` → `"3rd March 2026"`
- Day-of-week: `fn:format-date($d, "[FNn], [D] [MNn] [Y]")` → `"Tuesday, 3 March 2026"` (March 3, 2026 is a Tuesday)
- Error: time markers in date picture string

#### FnFormatTimeTest

Integration tests:

- 24-hour time: `fn:format-time($t, "[H01]:[m01]:[s01]")`
- 12-hour time: `fn:format-time($t, "[h]:[m01]:[s01] [P]")`
- With fractional seconds: `fn:format-time($t, "[H01]:[m01]:[s01].[f001]")`
- Error: date markers in time picture string

### 5.3 Edge Cases

- Boundary dates: Jan 1, Dec 31, leap year Feb 29
- Midnight and noon: hour 0 vs 12 in 12-hour format
- Negative years (BCE)
- Values with and without timezone
- Empty timezone offset
- Fractional seconds with 0 nanos vs maximum nanos
- Year values > 9999 and < 0
- Width constraints producing truncation vs padding
- Picture string with only literals (no markers)
- Empty picture string

### 5.4 Verification Checklist

- [ ] All new tests pass
- [ ] All existing tests still pass
- [ ] CI build succeeds: `mvn clean install -PCI -Prelease`
- [ ] No new SpotBugs/PMD/Checkstyle violations
- [ ] Javadoc on all public/protected members
- [ ] Code coverage ≥80% for new code

---

## 6. Risks and Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Picture string spec ambiguity | Medium | Medium | Use XPath 3.1 spec as authoritative reference; test against Saxon's behavior for ambiguous cases |
| Ordinal formatting complexity | Low | Medium | English ordinals are well-defined; note limitation for other languages |
| Roman numeral edge cases | Low | Low | Only months (1-12) are commonly used; handle values 1-3999 |
| Number-to-words conversion | Medium | Medium | Use a simple English-only implementation; note limitation |
| Timezone name formatting | Low | Medium | Use Java's timezone display names; `[z]` falls back to offset format |

---

## 7. Open Questions

1. **Timezone names (`[ZN]`)**: The spec allows timezone name output when `$place` provides context. Since `ITemporalItem` stores offsets (not zone IDs), `[ZN]` will fall back to offset format (`+01:01`) unless a recognized IANA timezone name is provided in `$place`. Is this acceptable?

---

## 8. Related Documents

- [Implementation Plan](./implementation-plan.md)
- [XPath 3.1 Formatting Functions Spec](https://www.w3.org/TR/xpath-functions-31/#formatting-dates-and-times)
- [GitHub Issue #283](https://github.com/metaschema-framework/metaschema-java/issues/283)
