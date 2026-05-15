# Resource Access Policy - Implementation Plan

**Goal:** Implement policy-based URI access control with glob patterns, IP-based SSRF protection, mandatory URI normalization, graduated enforcement modes, and bundled defaults.

**Architecture:** All policy engine code in `core` module. CLI integration in `cli-processor`/`metaschema-cli`.

**Tech Stack:** Java 11, JUnit 5, SLF4J, IP address library (`com.github.seancfoley:ipaddress`), Metaschema databind for configuration model.

---

## PR Breakdown

| PR | Scope | Estimated Files | Key Deliverables |
|----|-------|-----------------|------------------|
| PR1 | Policy engine core | ~25 files | Enums, `GlobMatcher`, `UriNormalizer`, `NetworkSecurityChecker`, `SchemePatternSet`, `FileProtections`, `ResourceAccessPolicy`, builder, diagnostics |
| PR2 | Configuration model | ~10 files | Metaschema module, config loading, bundled defaults, layering with ratchet |
| PR3 | Loader integration | ~10 files | `IModuleLoader`/`IBoundLoader` integration, XML entity policy, redirect re-check |
| PR4 | CLI integration + docs | ~8 files | CLI flags, `resource-policy` command with `dump`/`check` subcommands, env vars, documentation |

---

## PR1: Policy Engine Core

**Goal:** Implement the glob-based pattern matching engine, URI normalization, IP-based network security, enforcement modes, diagnostics, and the `ResourceAccessPolicy` builder.

**Module:** `core`

**Package:** `dev.metaschema.core.model.policy`

**New dependency:** Add `com.github.seancfoley:ipaddress` to `core/pom.xml` for CIDR block matching.

### Task 1.1: Create PolicyMode Enum

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/PolicyMode.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/PolicyModeTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PolicyModeTest {

  @Test
  void testDefaultModeIsDisabled() {
    assertEquals(PolicyMode.DISABLED, PolicyMode.defaultMode());
  }

  @ParameterizedTest
  @CsvSource({
      "DISABLED, false, false",
      "AUDIT, true, false",
      "ENFORCE, true, true"
  })
  void testModeCharacteristics(PolicyMode mode, boolean checks, boolean blocks) {
    assertEquals(checks, mode.isCheckEnabled());
    assertEquals(blocks, mode.isBlockEnabled());
  }

  @ParameterizedTest
  @CsvSource({
      "disabled, DISABLED",
      "audit, AUDIT",
      "enforce, ENFORCE",
      "DISABLED, DISABLED",
      "Audit, AUDIT"
  })
  void testFromString(String input, PolicyMode expected) {
    assertEquals(expected, PolicyMode.fromString(input));
  }

  @Test
  void testRestrictionOrdering() {
    assertTrue(PolicyMode.DISABLED.ordinal() < PolicyMode.AUDIT.ordinal());
    assertTrue(PolicyMode.AUDIT.ordinal() < PolicyMode.ENFORCE.ordinal());
  }

  @ParameterizedTest
  @CsvSource({
      "DISABLED, AUDIT, AUDIT",
      "AUDIT, ENFORCE, ENFORCE",
      "ENFORCE, DISABLED, ENFORCE",
      "AUDIT, DISABLED, AUDIT",
      "ENFORCE, AUDIT, ENFORCE"
  })
  void testMostRestrictive(PolicyMode a, PolicyMode b, PolicyMode expected) {
    assertEquals(expected, PolicyMode.mostRestrictive(a, b));
  }
}
```

**Implementation:**

```java
package dev.metaschema.core.model.policy;

import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Enforcement mode for resource access policies.
 *
 * <p>Modes are ordered by restriction level: DISABLED &lt; AUDIT &lt; ENFORCE.
 * The {@link #mostRestrictive(PolicyMode, PolicyMode)} method supports the
 * ratchet principle where configuration layers can only tighten policy.
 */
public enum PolicyMode {
  /** No policy checking; all URIs allowed silently. */
  DISABLED(false, false),
  /** Check policy and log violations, but allow all requests. */
  AUDIT(true, false),
  /** Check policy and block violations with an exception. */
  ENFORCE(true, true);

  private final boolean checkEnabled;
  private final boolean blockEnabled;

  PolicyMode(boolean checkEnabled, boolean blockEnabled) {
    this.checkEnabled = checkEnabled;
    this.blockEnabled = blockEnabled;
  }

  /** Whether this mode performs policy checks. */
  public boolean isCheckEnabled() { return checkEnabled; }

  /** Whether this mode blocks violating requests. */
  public boolean isBlockEnabled() { return blockEnabled; }

  /** Returns the default enforcement mode ({@link #DISABLED}). */
  @NonNull
  public static PolicyMode defaultMode() { return DISABLED; }

  /** Parses a mode from a string value (case-insensitive). */
  @NonNull
  public static PolicyMode fromString(@NonNull String value) {
    return valueOf(value.toUpperCase(Locale.ROOT));
  }

  /** Returns the more restrictive of two modes (ratchet principle). */
  @NonNull
  public static PolicyMode mostRestrictive(@NonNull PolicyMode a, @NonNull PolicyMode b) {
    return a.ordinal() >= b.ordinal() ? a : b;
  }
}
```

---

### Task 1.2: Create SymlinkPolicy and CaseSensitivity Enums

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/SymlinkPolicy.java`
- Create: `core/src/main/java/dev/metaschema/core/model/policy/CaseSensitivity.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/CaseSensitivityTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CaseSensitivityTest {

  @Test
  void testSystemDefaultDetectsOs() {
    boolean isWindows = System.getProperty("os.name")
        .toLowerCase(java.util.Locale.ROOT).contains("win");
    CaseSensitivity systemDefault = CaseSensitivity.SYSTEM_DEFAULT;

    assertEquals(!isWindows, systemDefault.isCaseSensitive());
  }

  @Test
  void testExplicitModes() {
    assertTrue(CaseSensitivity.CASE_SENSITIVE.isCaseSensitive());
    assertFalse(CaseSensitivity.CASE_INSENSITIVE.isCaseSensitive());
  }
}
```

**Implementation:**

```java
/** Policy for resolving symbolic links during file path checking. */
public enum SymlinkPolicy {
  /** Resolve symlinks via {@code Path.toRealPath()} before checking (default). */
  FOLLOW,
  /** Check the path as-is without symlink resolution. */
  NOFOLLOW
}

/** Case sensitivity mode for file path matching. */
public enum CaseSensitivity {
  /** Auto-detect from OS: case-insensitive on Windows, case-sensitive elsewhere. */
  SYSTEM_DEFAULT,
  /** Always case-sensitive matching. */
  CASE_SENSITIVE,
  /** Always case-insensitive matching. */
  CASE_INSENSITIVE;

  /** Whether this mode uses case-sensitive matching. */
  public boolean isCaseSensitive() {
    return switch (this) {
      case CASE_SENSITIVE -> true;
      case CASE_INSENSITIVE -> false;
      case SYSTEM_DEFAULT -> !System.getProperty("os.name")
          .toLowerCase(java.util.Locale.ROOT).contains("win");
    };
  }
}
```

---

### Task 1.3: Create AccessViolationException

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/AccessViolationException.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/AccessViolationExceptionTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.net.URI;

class AccessViolationExceptionTest {

  @Test
  void testExceptionContainsStructuredFields() {
    URI uri = URI.create("file:///etc/passwd");
    AccessViolationException ex = new AccessViolationException(
        uri, "file-protections", "path not in allowed areas",
        "bundled defaults",
        "FileProtections.builder().includeDefaults().allow(\"/etc/passwd\").build()");

    assertEquals(uri, ex.getUri());
    assertEquals("file-protections", ex.getLayer());
    assertEquals("path not in allowed areas", ex.getDenialReason());
    assertEquals("bundled defaults", ex.getConfigSource());
    assertNotNull(ex.getRemediation());
    assertTrue(ex.getMessage().contains(uri.toString()));
    assertTrue(ex.getMessage().contains("file-protections"));
  }

  @Test
  void testExtendsSecurityException() {
    AccessViolationException ex = new AccessViolationException(
        URI.create("http://localhost"), "network-security",
        "loopback denied", "bundled defaults", null);
    assertInstanceOf(SecurityException.class, ex);
  }
}
```

**Implementation:** Exception with fields for `uri`, `layer`, `denialReason`, `configSource`, `remediation`. Message format matches PRD:

```text
Resource access policy violation: '<uri>' was denied.
  Denied by: <layer> (<denialReason>)
  Source: <configSource>
  To allow: <remediation>
```

---

### Task 1.4: Create GlobMatcher

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/GlobMatcher.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/GlobMatcherTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GlobMatcherTest {

  @ParameterizedTest
  @CsvSource({
      "'**', 'anything/at/all', true",
      "'**', '', true",
      "'*.nist.gov/**', 'pages.nist.gov/schemas/foo.xml', true",
      "'*.nist.gov/**', 'evil.com/nist.gov/attack', false",
      "'/workspace/**', '/workspace/project/schema.xml', true",
      "'/workspace/*', '/workspace/schema.xml', true",
      "'/workspace/*', '/workspace/sub/schema.xml', false",
      "'example.com/path/**', 'example.com/path', true",
      "'example.com/path/**', 'example.com/path/', true",
      "'example.com/path/**', 'example.com/path/to/resource', true",
      "'example.com/path/**', 'example.com/other/resource', false",
      "'**/.ssh/**', '/home/user/.ssh/id_rsa', true",
      "'**/.ssh/**', '/home/user/projects/ssh-keys', false",
      "'localhost/**', 'localhost/api', true",
  })
  void testPatternMatching(String pattern, String target, boolean expected) {
    GlobMatcher matcher = GlobMatcher.compile(pattern, true);
    assertEquals(expected, matcher.matches(target),
        () -> String.format("Pattern '%s' vs '%s'", pattern, target));
  }

  @Test
  void testCaseInsensitiveMatching() {
    GlobMatcher matcher = GlobMatcher.compile("/Workspace/**", false);
    assertTrue(matcher.matches("/workspace/file.xml"));
    assertTrue(matcher.matches("/WORKSPACE/file.xml"));
  }

  @Test
  void testCaseSensitiveMatching() {
    GlobMatcher matcher = GlobMatcher.compile("/workspace/**", true);
    assertTrue(matcher.matches("/workspace/file.xml"));
    assertFalse(matcher.matches("/Workspace/file.xml"));
  }

  @Test
  void testNullSafety() {
    GlobMatcher matcher = GlobMatcher.compile("**", true);
    assertThrows(NullPointerException.class, () -> matcher.matches(null));
  }

  @Test
  void testDirectoryEquivalence() {
    // path/** must also match path itself (without trailing slash or children)
    GlobMatcher matcher = GlobMatcher.compile("/workspace/**", true);
    assertTrue(matcher.matches("/workspace"), "directory itself without trailing slash");
    assertTrue(matcher.matches("/workspace/"), "directory with trailing slash");
    assertTrue(matcher.matches("/workspace/project/schema.xml"), "child path");
    assertFalse(matcher.matches("/workspaceX"), "must not match prefix that is not the directory");
    assertFalse(matcher.matches("/workspac"), "must not match shorter prefix");

    // Same for host-style patterns
    GlobMatcher hostMatcher = GlobMatcher.compile("pages.nist.gov/**", true);
    assertTrue(hostMatcher.matches("pages.nist.gov"), "host directory itself");
    assertTrue(hostMatcher.matches("pages.nist.gov/"), "host directory with trailing slash");
    assertTrue(hostMatcher.matches("pages.nist.gov/schemas/foo.xml"), "host child path");
  }

  @Test
  void testEmptyPattern() {
    GlobMatcher matcher = GlobMatcher.compile("", true);
    assertTrue(matcher.matches(""));
    assertFalse(matcher.matches("anything"));
  }

  @Test
  void testReDoSResistance() {
    // Crafted pattern that would cause catastrophic backtracking with naive regex
    String malicious = "**/**/**/**/**/**/**/**/**/**";
    GlobMatcher matcher = GlobMatcher.compile(malicious, true);
    String longPath = "a/b/c/d/e/f/g/h/i/j/k/l/m/n/o/p/q/r/s/t/u/v/w/x/y/z";
    // Should complete in reasonable time (< 1 second), not hang
    assertTimeout(java.time.Duration.ofSeconds(1),
        () -> matcher.matches(longPath));
  }
}
```

**Implementation:** Compile glob patterns to `java.util.regex.Pattern`:
- `*` → `[^/]*+` (possessive quantifier to prevent backtracking)
- `**` → `.*+` (possessive quantifier)
- `?` → `[^/]`
- Escape regex special characters
- Accept `caseSensitive` parameter for `Pattern.CASE_INSENSITIVE` flag
- Use possessive quantifiers or atomic groups to prevent ReDoS
- Validate pattern length (max 500 chars)
- **Directory equivalence:** When a pattern ends with `/**`, compile it to also match the directory prefix itself. For pattern `P/**`, the compiled regex matches `P`, `P/`, and `P/<anything>`. Implementation: detect trailing `/**`, strip it to get prefix `P`, compile as `P(/.*+)?` (optional `/` followed by anything). This ensures `path/**` ≡ `path` in allow lists.

---

### Task 1.5: Create UriNormalizer

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/UriNormalizer.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/UriNormalizerTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.URI;

class UriNormalizerTest {

  // --- Path normalization ---

  @ParameterizedTest
  @CsvSource({
      "file:///workspace/../etc/passwd, /etc/passwd",
      "file:///workspace/./schema.xml, /workspace/schema.xml",
      "file:///workspace/project/../../etc/passwd, /etc/passwd",
  })
  void testPathTraversalNormalization(String rawUri, String expectedPath) {
    URI uri = URI.create(rawUri);
    String normalized = UriNormalizer.normalizeFilePath(uri, SymlinkPolicy.NOFOLLOW);
    assertEquals(expectedPath, normalized);
  }

  @Test
  void testRejectPathWithDotsAfterNormalization() {
    // Edge case: a path that still contains ".." after normalization
    // (should not happen with Path.normalize() but tested for defense-in-depth)
    URI uri = URI.create("file:///workspace/../etc/passwd");
    String normalized = UriNormalizer.normalizeFilePath(uri, SymlinkPolicy.NOFOLLOW);
    assertFalse(normalized.contains(".."), "Normalized path must not contain '..'");
  }

  // --- Percent-decoding ---

  @ParameterizedTest
  @CsvSource({
      "file:///etc/p%61sswd, /etc/passwd",
      "file:///workspace%2F..%2F..%2Fetc%2Fpasswd, /etc/passwd",
  })
  void testPercentDecoding(String rawUri, String expectedPath) {
    URI uri = URI.create(rawUri);
    String normalized = UriNormalizer.normalizeFilePath(uri, SymlinkPolicy.NOFOLLOW);
    assertEquals(expectedPath, normalized);
  }

  // --- Scheme normalization ---

  @Test
  void testSchemeNormalization() {
    assertEquals("file", UriNormalizer.normalizeScheme(URI.create("FILE:///path")));
    assertEquals("https", UriNormalizer.normalizeScheme(URI.create("HTTPS://host/path")));
  }

  // --- Host normalization (http/https) ---

  @ParameterizedTest
  @CsvSource({
      "https://EXAMPLE.COM/path, example.com/path",
      "https://Example.Com:443/path, example.com/path",
      "https://example.com:8443/path, example.com/path",
      "http://LOCALHOST:8080/api, localhost/api",
      "http://localhost:80/api, localhost/api",
  })
  void testHostNormalization(String rawUri, String expectedTarget) {
    URI uri = URI.create(rawUri);
    String target = UriNormalizer.normalizeNetworkTarget(uri);
    assertEquals(expectedTarget, target);
  }

  // --- JAR scheme parsing ---

  @Test
  void testJarSchemeInnerUri() {
    URI jarUri = URI.create("jar:http://evil.com/mal.jar!/schema/x.xsd");
    URI innerUri = UriNormalizer.extractJarInnerUri(jarUri);
    assertEquals(URI.create("http://evil.com/mal.jar"), innerUri);
  }

  @Test
  void testJarSchemeInternalPath() {
    URI jarUri = URI.create("jar:file:///lib.jar!/schema/x.xsd");
    String internalPath = UriNormalizer.extractJarInternalPath(jarUri);
    assertEquals("/schema/x.xsd", internalPath);
  }

  @Test
  void testMalformedJarUri() {
    URI jarUri = URI.create("jar:file:///lib.jar");
    assertThrows(IllegalArgumentException.class,
        () -> UriNormalizer.extractJarInternalPath(jarUri));
  }
}
```

**Implementation:** Static utility class with methods:
- `normalizeScheme(URI)` → lowercase scheme string
- `normalizeFilePath(URI, SymlinkPolicy)` → decode, normalize path, optionally resolve symlinks
- `normalizeNetworkTarget(URI)` → lowercase host, strip default ports, return `host/path`
- `extractJarInnerUri(URI)` → parse inner URI before `!`
- `extractJarInternalPath(URI)` → parse path after `!`
- Reject paths containing `..` after normalization (defense-in-depth)

---

### Task 1.6: Create NetworkSecurityChecker

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/NetworkSecurityChecker.java`
- Create: `core/src/main/java/dev/metaschema/core/model/policy/NetworkSecurityConfig.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/NetworkSecurityCheckerTest.java`

**Test first — IP CIDR boundary tests:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NetworkSecurityCheckerTest {

  private final NetworkSecurityChecker checker
      = new NetworkSecurityChecker(NetworkSecurityConfig.defaults());

  // --- IPv4 CIDR boundary tests ---

  @ParameterizedTest
  @CsvSource({
      // 127.0.0.0/8 (loopback)
      "126.255.255.255, true",
      "127.0.0.0, false",
      "127.0.0.1, false",
      "127.255.255.255, false",
      "128.0.0.0, true",

      // 10.0.0.0/8 (private Class A)
      "9.255.255.255, true",
      "10.0.0.0, false",
      "10.128.0.1, false",
      "10.255.255.255, false",
      "11.0.0.0, true",

      // 172.16.0.0/12 (private Class B)
      "172.15.255.255, true",
      "172.16.0.0, false",
      "172.20.0.1, false",
      "172.31.255.255, false",
      "172.32.0.0, true",

      // 192.168.0.0/16 (private Class C)
      "192.167.255.255, true",
      "192.168.0.0, false",
      "192.168.1.100, false",
      "192.168.255.255, false",
      "192.169.0.0, true",

      // 169.254.0.0/16 (link-local / cloud metadata)
      "169.253.255.255, true",
      "169.254.0.0, false",
      "169.254.169.254, false",
      "169.254.255.255, false",
      "169.255.0.0, true",

      // 100.64.0.0/10 (CGNAT / shared address space)
      "100.63.255.255, true",
      "100.64.0.0, false",
      "100.100.0.1, false",
      "100.127.255.255, false",
      "100.128.0.0, true",

      // 0.0.0.0/8 (unspecified)
      "0.0.0.0, false",
      "0.255.255.255, false",
      "1.0.0.0, true",

      // Public IPs (should be allowed)
      "8.8.8.8, true",
      "1.1.1.1, true",
      "93.184.216.34, true",
  })
  void testIpv4CidrBoundaries(String ip, boolean allowed) {
    assertEquals(allowed, checker.isAllowed(ip),
        () -> "IP " + ip + " should be " + (allowed ? "allowed" : "blocked"));
  }

  // --- IPv6 CIDR boundary tests ---

  @ParameterizedTest
  @CsvSource({
      // ::1/128 (IPv6 loopback)
      "::1, false",
      "::2, true",

      // fe80::/10 (IPv6 link-local)
      "fe80::1, false",
      "fe80::ffff, false",
      "febf::1, false",
      "fec0::1, true",

      // fc00::/7 (IPv6 ULA)
      "fc00::1, false",
      "fd00::1, false",
      "fdff::ffff, false",
      "fe00::1, true",

      // ::ffff:0:0/96 (IPv4-mapped IPv6 — checked after mapping)
      "::ffff:127.0.0.1, false",
      "::ffff:10.0.0.1, false",
      "::ffff:192.168.1.1, false",
      "::ffff:8.8.8.8, true",
      "::ffff:1.1.1.1, true",

      // Public IPv6 (should be allowed)
      "2001:4860:4860::8888, true",
  })
  void testIpv6CidrBoundaries(String ip, boolean allowed) {
    assertEquals(allowed, checker.isAllowed(ip),
        () -> "IP " + ip + " should be " + (allowed ? "allowed" : "blocked"));
  }

  // --- Alternate IP encoding tests ---

  @ParameterizedTest
  @CsvSource({
      "2130706433, false",     // decimal 127.0.0.1
      "0x7f000001, false",     // hex 127.0.0.1
      "0177.0.0.1, false",    // octal 127.0.0.1
      "127.1, false",          // shorthand 127.0.0.1
  })
  void testAlternateIpEncodings(String host, boolean allowed) {
    assertEquals(allowed, checker.isAllowed(host),
        () -> "Host " + host + " should be " + (allowed ? "allowed" : "blocked"));
  }

  // --- Hostname resolution ---

  @Test
  void testLocalhostResolution() {
    assertFalse(checker.isAllowed("localhost"));
  }

  // --- Custom config ---

  @Test
  void testAllowLoopback() {
    NetworkSecurityChecker devChecker = new NetworkSecurityChecker(
        NetworkSecurityConfig.builder()
            .allowLoopback(true)
            .build());

    assertTrue(devChecker.isAllowed("127.0.0.1"));
    assertTrue(devChecker.isAllowed("localhost"));
    assertFalse(devChecker.isAllowed("10.0.0.1")); // still blocked
  }

  @Test
  void testAllowSpecificCidr() {
    NetworkSecurityChecker customChecker = new NetworkSecurityChecker(
        NetworkSecurityConfig.builder()
            .allowCidr("10.0.0.0/24")
            .build());

    assertTrue(customChecker.isAllowed("10.0.0.1"));
    assertFalse(customChecker.isAllowed("10.0.1.1"));
    assertFalse(customChecker.isAllowed("192.168.1.1"));
  }

  // --- Denial reason ---

  @Test
  void testDenialReasonIncludesCidr() {
    String reason = checker.getDenialReason("10.0.0.1");
    assertNotNull(reason);
    assertTrue(reason.contains("10.0.0.0/8"));
  }
}
```

**Implementation:**
- `NetworkSecurityChecker` accepts a `NetworkSecurityConfig`
- Uses `com.github.seancfoley:ipaddress` library for CIDR matching
- `isAllowed(String hostOrIp)` — resolves hostname to `InetAddress`, checks against blocked CIDR ranges
- `getDenialReason(String hostOrIp)` — returns human-readable reason with the matching CIDR block
- `NetworkSecurityConfig` — builder with `allowLoopback(boolean)`, `allowCidr(String)`, `defaults()` factory

---

### Task 1.7: Create SchemePatternSet

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/SchemePatternSet.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/SchemePatternSetTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SchemePatternSetTest {

  @Test
  void testDisabledSchemeDeniesAll() {
    SchemePatternSet set = SchemePatternSet.disabled("http");
    assertFalse(set.isAllowed("example.com/api"));
    assertEquals("scheme 'http' is disabled", set.getDenialReason("example.com/api"));
  }

  @Test
  void testNoPatternsUsesDefaultPolicy() {
    // enabled + no patterns should NOT allow all — uses default deny
    SchemePatternSet set = SchemePatternSet.enabled("https");
    assertFalse(set.isAllowed("example.com/anything"));
  }

  @Test
  void testAllowPattern() {
    SchemePatternSet set = SchemePatternSet.builder("file")
        .allow("/workspace/**")
        .build();

    assertTrue(set.isAllowed("/workspace/project/schema.xml"));
    assertFalse(set.isAllowed("/etc/passwd"));
  }

  @Test
  void testDenyPatternOverridesAllow() {
    SchemePatternSet set = SchemePatternSet.builder("file")
        .allow("**")
        .deny("**/.ssh/**")
        .build();

    assertTrue(set.isAllowed("/workspace/schema.xml"));
    assertFalse(set.isAllowed("/home/user/.ssh/id_rsa"));
  }

  @Test
  void testLastMatchWins() {
    SchemePatternSet set = SchemePatternSet.builder("file")
        .allow("**")
        .deny("/etc/**")
        .allow("/etc/motd")
        .build();

    assertTrue(set.isAllowed("/workspace/file.xml"));
    assertFalse(set.isAllowed("/etc/passwd"));
    assertTrue(set.isAllowed("/etc/motd"));
  }

  @Test
  void testNoMatchDenies() {
    SchemePatternSet set = SchemePatternSet.builder("https")
        .allow("nist.gov/**")
        .build();

    assertTrue(set.isAllowed("nist.gov/schemas/x.xml"));
    assertFalse(set.isAllowed("evil.com/attack"));
  }

  @Test
  void testAllowAll() {
    SchemePatternSet set = SchemePatternSet.builder("jar")
        .allowAll()
        .build();
    assertTrue(set.isAllowed("/any/path"));
  }

  @Test
  void testDenyAll() {
    SchemePatternSet set = SchemePatternSet.builder("http")
        .denyAll()
        .build();
    assertFalse(set.isAllowed("example.com/api"));
  }

  @Test
  void testCaseInsensitiveMatching() {
    SchemePatternSet set = SchemePatternSet.builder("file")
        .caseSensitive(false)
        .allow("/Workspace/**")
        .build();

    assertTrue(set.isAllowed("/workspace/file.xml"));
    assertTrue(set.isAllowed("/WORKSPACE/file.xml"));
  }
}
```

**Implementation:** Holds an ordered list of `(GlobMatcher, boolean isAllow)` entries. Evaluates last-match-wins. `enabled: true` with no patterns returns `false` (deny, matching default-scheme-policy behavior). Accepts `caseSensitive` flag passed to `GlobMatcher.compile()`.

---

### Task 1.8: Create PolicyDecision and EvaluationStep

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/PolicyDecision.java`
- Create: `core/src/main/java/dev/metaschema/core/model/policy/EvaluationStep.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/PolicyDecisionTest.java`

**Implementation:** Simple immutable data classes:

```java
/** Diagnostic result from {@link ResourceAccessPolicy#explain(URI)}. */
public final class PolicyDecision {
  private final boolean allowed;
  private final String layer;
  private final String denialReason;
  private final String matchingPattern;
  private final String configSource;
  private final String remediation;
  private final List<EvaluationStep> evaluationTrace;
  // constructor, getters, toString
}

/** Single step in the policy evaluation trace. */
public final class EvaluationStep {
  private final String layer;
  private final String description;
  private final boolean matched;
  private final boolean resultIfMatched;
  // constructor, getters
}
```

---

### Task 1.9: Create IResourceAccessPolicy Interface

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/IResourceAccessPolicy.java`

**Implementation:**

```java
package dev.metaschema.core.model.policy;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Policy that controls which URIs can be accessed during resource loading.
 *
 * @see ResourceAccessPolicy
 */
public interface IResourceAccessPolicy {

  /** A policy that allows all access without checking. */
  IResourceAccessPolicy ALLOW_ALL = uri -> { /* no-op */ };

  /**
   * Checks whether the given URI is allowed by this policy.
   *
   * @param uri
   *          the URI to check
   * @throws AccessViolationException
   *           if the policy is in ENFORCE mode and the URI is denied
   */
  void checkAccess(@NonNull URI uri);
}
```

---

### Task 1.10: Create FileProtections

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/FileProtections.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/FileProtectionsTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

class FileProtectionsTest {

  @TempDir
  Path cwd;

  @ParameterizedTest
  @ValueSource(strings = {
      "/etc/passwd",
      "/proc/self/environ",
      "/sys/kernel/debug",
      "/dev/null",
      "/root/.bashrc",
      "/var/run/secrets/kubernetes.io/token",
      "C:/Windows/System32/config/SAM",
  })
  void testDefaultDeniesPathsOutsideSafeAreas(String path) {
    FileProtections protections = FileProtections.withDefaults(cwd,
        CaseSensitivity.CASE_SENSITIVE);
    assertFalse(protections.isAllowed(path),
        "Should deny (outside safe areas): " + path);
  }

  @Test
  void testDefaultAllowsCwdSubtree() {
    FileProtections protections = FileProtections.withDefaults(cwd,
        CaseSensitivity.CASE_SENSITIVE);
    String cwdPath = cwd.resolve("project/schema.xml").toString();
    assertTrue(protections.isAllowed(cwdPath), "Should allow CWD subtree");
  }

  @Test
  void testDefaultDeniesDotDirsInHome() {
    Path home = Path.of(System.getProperty("user.home"));
    FileProtections protections = FileProtections.withDefaults(cwd,
        CaseSensitivity.CASE_SENSITIVE);

    String sshKey = home.resolve(".ssh/id_rsa").toString();
    assertFalse(protections.isAllowed(sshKey),
        "Should deny ~/.ssh (blanket dot-dir exclusion)");

    String kubeCfg = home.resolve(".kube/config").toString();
    assertFalse(protections.isAllowed(kubeCfg),
        "Should deny ~/.kube (blanket dot-dir exclusion)");

    String normalFile = home.resolve("projects/schema.xml").toString();
    assertTrue(protections.isAllowed(normalFile),
        "Should allow normal files in home");
  }

  @Test
  void testBuilderIncludeDefaults() {
    FileProtections protections = FileProtections.builder(cwd,
        CaseSensitivity.CASE_SENSITIVE)
        .includeDefaults()
        .allow("/opt/metaschema/**")
        .build();

    String cwdFile = cwd.resolve("schema.xml").toString();
    assertTrue(protections.isAllowed(cwdFile));
    assertTrue(protections.isAllowed("/opt/metaschema/x"));
    assertFalse(protections.isAllowed("/etc/passwd"));
  }

  @Test
  void testBuilderRemoveDefault() {
    FileProtections protections = FileProtections.builder(cwd,
        CaseSensitivity.CASE_SENSITIVE)
        .includeDefaults()
        .remove("<user.home>/**")
        .build();

    Path home = Path.of(System.getProperty("user.home"));
    assertFalse(protections.isAllowed(home.resolve("file.txt").toString()));
    assertTrue(protections.isAllowed(cwd.resolve("file.txt").toString()));
  }

  @Test
  void testBuilderFullyCustom() {
    FileProtections protections = FileProtections.builder(cwd,
        CaseSensitivity.CASE_SENSITIVE)
        .allow("/opt/app/**")
        .build();

    assertTrue(protections.isAllowed("/opt/app/schema.xml"));
    assertFalse(protections.isAllowed("/etc/passwd"));
    assertFalse(protections.isAllowed(cwd.resolve("file.txt").toString()));
  }

  @Test
  void testDisabledAllowsEverythingAndLogsWarning() {
    FileProtections protections = FileProtections.disabled();
    assertTrue(protections.isAllowed("/etc/passwd"));
    assertTrue(protections.isAllowed("/home/user/.ssh/key"));
  }

  @Test
  void testDefaultPatternsAreInspectable() {
    assertFalse(FileProtections.defaultAllowPatterns().isEmpty());
  }

  @Test
  void testCaseInsensitiveOnWindows() {
    FileProtections protections = FileProtections.withDefaults(cwd,
        CaseSensitivity.CASE_INSENSITIVE);
    String cwdUpper = cwd.resolve("Schema.XML").toString().toUpperCase();
    // Should match CWD subtree case-insensitively
    assertTrue(protections.isAllowed(
        cwd.resolve("Schema.XML").toString()));
  }

  @Test
  void testCwdRootWarning() {
    // When CWD is filesystem root, should log a warning
    Path root = Path.of("/");
    // This should succeed but log a WARNING
    FileProtections protections = FileProtections.withDefaults(root,
        CaseSensitivity.CASE_SENSITIVE);
    // Root allows everything via <cwd>/**
    assertTrue(protections.isAllowed("/etc/passwd"));
  }
}
```

**Implementation:**
- `withDefaults(Path cwd, CaseSensitivity cs)` — default patterns with CWD + home minus dot-dirs
- `disabled()` — allows everything, logs WARN, Javadoc security warning
- `builder(Path cwd, CaseSensitivity cs)` — customizable builder
- `defaultAllowPatterns()` — static inspection method
- `isAllowed(String path)` — check path against patterns
- Warn if CWD is root (`/` or drive root)

---

### Task 1.11: Create ResourceAccessPolicy and Builder

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/ResourceAccessPolicy.java`
- Create: `core/src/main/java/dev/metaschema/core/model/policy/ResourceAccessPolicyBuilder.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/ResourceAccessPolicyTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.net.URI;

class ResourceAccessPolicyTest {

  @Test
  void testDisabledModeAllowsEverything() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.DISABLED)
        .forScheme("file").denyAll()
        .build();

    assertDoesNotThrow(() -> policy.checkAccess(URI.create("file:///etc/passwd")));
  }

  @Test
  void testAuditModeLogsButAllows() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.AUDIT)
        .forScheme("http").denyAll()
        .denyUnlistedSchemes()
        .build();

    assertDoesNotThrow(() -> policy.checkAccess(URI.create("http://localhost/admin")));
  }

  @Test
  void testEnforceModeBlocks() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("http").denyAll()
        .denyUnlistedSchemes()
        .build();

    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(URI.create("http://localhost/admin")));
  }

  @Test
  void testEnforceModeAllowsMatching() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("https").allow("nist.gov/**")
        .denyUnlistedSchemes()
        .build();

    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("https://nist.gov/schemas/x.xml")));
  }

  @Test
  void testDenyPatternExceptions() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("file")
            .allow("**")
            .deny("**/.ssh/**")
        .fileProtections(FileProtections.disabled())
        .denyUnlistedSchemes()
        .build();

    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("file:///workspace/schema.xml")));
    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(
            URI.create("file:///home/user/.ssh/id_rsa")));
  }

  @Test
  void testDenyUnlistedSchemesBlocksUnknown() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("https").allowAll()
        .denyUnlistedSchemes()
        .build();

    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(URI.create("ftp://evil.com/file")));
  }

  @Test
  void testWithModeCreatesNewPolicy() {
    ResourceAccessPolicy audit = ResourceAccessPolicy.builder()
        .mode(PolicyMode.AUDIT)
        .forScheme("http").denyAll()
        .denyUnlistedSchemes()
        .build();

    assertDoesNotThrow(() -> audit.checkAccess(
        URI.create("http://localhost/admin")));

    ResourceAccessPolicy enforced = audit.withMode(PolicyMode.ENFORCE);
    assertThrows(AccessViolationException.class,
        () -> enforced.checkAccess(URI.create("http://localhost/admin")));
  }

  @Test
  void testToBuilder() {
    ResourceAccessPolicy original = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("https").allow("nist.gov/**")
        .denyUnlistedSchemes()
        .build();

    ResourceAccessPolicy modified = original.toBuilder()
        .forScheme("https").allow("github.com/**")
        .build();

    assertDoesNotThrow(() -> modified.checkAccess(
        URI.create("https://nist.gov/x.xml")));
    assertDoesNotThrow(() -> modified.checkAccess(
        URI.create("https://github.com/x.xml")));
  }

  @Test
  void testExplainReturnsDecision() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("https").allow("nist.gov/**")
        .denyUnlistedSchemes()
        .build();

    PolicyDecision allowed = policy.explain(URI.create("https://nist.gov/x.xml"));
    assertTrue(allowed.isAllowed());

    PolicyDecision denied = policy.explain(URI.create("https://evil.com/x.xml"));
    assertFalse(denied.isAllowed());
    assertNotNull(denied.getDenialReason());
    assertNotNull(denied.getLayer());
    assertNotNull(denied.getRemediation());
    assertFalse(denied.getEvaluationTrace().isEmpty());
  }

  @Test
  void testDescribeEffectiveRules() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("https").allow("nist.gov/**")
        .forScheme("file").allow("/workspace/**")
        .denyUnlistedSchemes()
        .build();

    String description = policy.describeEffectiveRules();
    assertNotNull(description);
    assertTrue(description.contains("ENFORCE"));
    assertTrue(description.contains("https"));
    assertTrue(description.contains("file"));
  }

  @Test
  void testBundledDefaultsFactory() {
    ResourceAccessPolicy defaults = ResourceAccessPolicy.bundledDefaults();
    assertNotNull(defaults);
    // Bundled defaults are AUDIT mode — should not throw
    assertDoesNotThrow(() -> defaults.checkAccess(
        URI.create("https://example.com/test")));
  }

  @Test
  void testDevelopmentFactory() {
    ResourceAccessPolicy dev = ResourceAccessPolicy.development();
    assertNotNull(dev);
    // Dev mode should allow localhost
    assertDoesNotThrow(() -> dev.checkAccess(
        URI.create("http://localhost/api")));
  }

  @Test
  void testDisabledFactory() {
    ResourceAccessPolicy disabled = ResourceAccessPolicy.disabled();
    assertDoesNotThrow(() -> disabled.checkAccess(
        URI.create("file:///etc/passwd")));
  }

  @Test
  void testJarSchemeRecursiveCheck() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("file").allow("/lib/**")
        .forScheme("jar").allowAll()
        .fileProtections(FileProtections.disabled())
        .denyUnlistedSchemes()
        .build();

    // jar: with file: inner URI pointing to allowed path
    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("jar:file:///lib/app.jar!/schema/x.xsd")));

    // jar: with http: inner URI — http not configured, default deny
    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(
            URI.create("jar:http://evil.com/mal.jar!/schema/x.xsd")));
  }

  @Test
  void testPathNormalizationPreventsTraversal() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("file").allow("/workspace/**")
        .fileProtections(FileProtections.disabled())
        .denyUnlistedSchemes()
        .build();

    // Path traversal should be caught after normalization
    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(
            URI.create("file:///workspace/../etc/passwd")));
  }

  @Test
  void testSchemeNormalization() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("file").allow("/workspace/**")
        .fileProtections(FileProtections.disabled())
        .denyUnlistedSchemes()
        .build();

    // Uppercase scheme should still match
    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("FILE:///workspace/schema.xml")));
  }

  @Test
  void testFileProtectionsConflictDetection() {
    // /opt/data/ is outside CWD and home — should throw at build time
    assertThrows(IllegalStateException.class,
        () -> ResourceAccessPolicy.builder()
            .mode(PolicyMode.ENFORCE)
            .forScheme("file")
                .allow("/opt/data/**")
            .denyUnlistedSchemes()
            .build());
  }

  @Test
  void testEnabledNoPatternsUsesDeny() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .denyUnlistedSchemes()
        .build();

    // No schemes configured — should use default-scheme-policy (deny)
    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(URI.create("https://example.com")));
  }

  @Test
  void testImmutability() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.AUDIT)
        .forScheme("https").allow("nist.gov/**")
        .build();

    ResourceAccessPolicy withEnforce = policy.withMode(PolicyMode.ENFORCE);

    // Original should not be affected
    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("https://evil.com")));
    // New instance should enforce
    assertThrows(AccessViolationException.class,
        () -> withEnforce.checkAccess(URI.create("https://evil.com")));
  }
}
```

**Implementation:**
- `ResourceAccessPolicy` is a **final, immutable** class
- All internal collections are unmodifiable copies
- `checkAccess(URI)` — full evaluation pipeline (normalize → network check → file protections → scheme patterns → mode behavior)
- `explain(URI)` — same pipeline but returns `PolicyDecision` instead of throwing
- `withMode(PolicyMode)` — returns new instance with different mode
- `toBuilder()` — returns pre-populated builder
- Factory methods: `bundledDefaults()`, `development()`, `disabled()`
- `describeEffectiveRules()` — returns human-readable summary
- `ResourceAccessPolicyBuilder` uses nested `SchemeConfigBuilder` pattern
- `.forScheme()` twice for same scheme appends patterns
- `.build()` runs conflict detection (file scheme patterns vs FileProtections)

---

### Task 1.12: Add package-info.java

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/package-info.java`

---

### Task 1.13: Verify PR1 Build

```bash
mvn -pl core clean install
mvn -pl core checkstyle:check
```

---

## PR2: Configuration Model and Bundled Defaults

**Goal:** Define the Metaschema configuration module, implement config loading with ratcheting, and ship bundled restrictive defaults.

### Task 2.1: Create Metaschema Configuration Module

**Files:**
- Create: `core/src/main/metaschema/resource-access-policy-config_metaschema.yaml`

The Metaschema module definition for the resource access policy configuration model. See PRD for full module definition. Root assembly is `resource-access-policy-config` to avoid naming collision with the hand-written `ResourceAccessPolicy` class.

---

### Task 2.2: Configure Maven Code Generation

**Files:**
- Modify: `core/pom.xml` (add dependency on `ipaddress` library, verify metaschema-maven-plugin config)

Verify generated binding classes compile and contain expected fields:
- `ResourceAccessPolicyConfig` (root assembly)
- `SchemeConfig` (scheme configuration)
- `Pattern` (access pattern field)

---

### Task 2.3: Create Bundled Default Policy

**Files:**
- Create: `core/src/main/resources/dev/metaschema/core/model/policy/default-resource-access-policy.yaml`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/BundledDefaultsTest.java`

**Test first:**

```java
package dev.metaschema.core.model.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;

class BundledDefaultsTest {

  private final ResourceAccessPolicy defaults = ResourceAccessPolicy.bundledDefaults();

  @Test
  void testDefaultModeIsAudit() {
    // AUDIT mode: should log but not throw
    assertDoesNotThrow(() -> defaults.checkAccess(
        URI.create("http://localhost/admin")));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "https://pages.nist.gov/schemas/x.xml",
      "https://example.com/api",
      "jar:file:///lib.jar!/schema/x.xsd",
  })
  void testDefaultAllowedInEnforceMode(String uriString) {
    ResourceAccessPolicy enforced = defaults.withMode(PolicyMode.ENFORCE);
    assertDoesNotThrow(() -> enforced.checkAccess(URI.create(uriString)));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "http://example.com/api",
      "ftp://evil.com/file",
  })
  void testDefaultDeniedSchemesInEnforceMode(String uriString) {
    ResourceAccessPolicy enforced = defaults.withMode(PolicyMode.ENFORCE);
    assertThrows(AccessViolationException.class,
        () -> enforced.checkAccess(URI.create(uriString)));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "https://localhost/admin",
      "https://127.0.0.1/secret",
      "https://169.254.169.254/meta",
      "https://10.0.0.1/internal",
      "https://192.168.1.1/router",
  })
  void testDefaultDeniedNetworkInEnforceMode(String uriString) {
    ResourceAccessPolicy enforced = defaults.withMode(PolicyMode.ENFORCE);
    assertThrows(AccessViolationException.class,
        () -> enforced.checkAccess(URI.create(uriString)));
  }
}
```

---

### Task 2.4: Implement Configuration Loading with Ratcheting

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/ResourceAccessPolicyLoader.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/ResourceAccessPolicyLoaderTest.java`

**Test first:** Verify:
- Loading from YAML, JSON, and XML config files
- Configuration layering with merge semantics
- Ratchet enforcement (can only tighten, never loosen mode)
- `locked: true` prevents overrides
- `inherit: true` appends patterns instead of replacing
- Scheme name validation (warn on unrecognized schemes like "htps")
- YAML `!` pattern validation (detect unquoted `!` patterns)

---

### Task 2.5: Verify PR2 Build

```bash
mvn -pl core clean install
mvn -pl core checkstyle:check
```

---

## PR3: Loader Integration

**Goal:** Integrate policy checking into all resource loading paths.

### Task 3.1: Add Policy Support to Loader Interfaces

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/IModuleLoader.java`

Add method:

```java
/**
 * Sets the resource access policy for this loader.
 * <p>
 * When set, all URIs resolved by this loader are checked against the policy
 * before loading. Use {@link ResourceAccessPolicy#bundledDefaults()} for
 * recommended defaults.
 *
 * @param policy
 *          the policy to enforce, or {@code null} to disable policy checking
 */
void setResourceAccessPolicy(@Nullable IResourceAccessPolicy policy);
```

---

### Task 3.2: Integrate Policy in AbstractModuleLoader

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/AbstractModuleLoader.java`
- Test: `core/src/test/java/dev/metaschema/core/model/AbstractModuleLoaderPolicyTest.java`

**Test first:** Verify module import URIs are checked against policy. Verify relative URIs are resolved to absolute before checking.

**Implementation:** Add `volatile IResourceAccessPolicy` field. Check before URI resolution. Resolve relative URIs to absolute before calling `checkAccess()`.

---

### Task 3.3: Integrate Policy in DefaultBoundLoader

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/io/DefaultBoundLoader.java`
- Test: `databind/src/test/java/dev/metaschema/databind/io/DefaultBoundLoaderPolicyTest.java`

**Test first:** Verify document loading URIs are checked against policy.

---

### Task 3.4: Integrate Policy in BindingConstraintLoader

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/model/metaschema/BindingConstraintLoader.java`
- Test: `databind/src/test/java/dev/metaschema/databind/model/metaschema/BindingConstraintLoaderPolicyTest.java`

**Test first:** Verify constraint import URIs are checked against policy.

---

### Task 3.5: Integrate Policy in DefaultXmlDeserializer

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/io/xml/DefaultXmlDeserializer.java`
- Test: `databind/src/test/java/dev/metaschema/databind/io/xml/DefaultXmlDeserializerPolicyTest.java`

**Test first:** Verify XML entity resolution URIs are checked against policy. Document HTTP redirect re-checking requirement.

---

### Task 3.6: Verify PR3 Build

```bash
mvn clean install -PCI -Prelease
```

---

## PR4: CLI Integration and Documentation

**Goal:** Add CLI flags for policy control, diagnostic commands, and documentation.

### Task 4.1: Add Global CLI Flags

**Files:**
- Modify: `metaschema-cli/src/main/java/dev/metaschema/cli/commands/MetaschemaCommands.java` (shared options)
- Modify: Resource-loading commands (validate, validate-content, convert, generate-schema) to accept policy flags

Add global flags available on all resource-loading commands:
- `--resource-policy-mode=<disabled|audit|enforce>` — Override enforcement mode
- `--resource-policy=<path>` — Load custom policy configuration file

### Task 4.2: Add Environment Variable Support

Support `METASCHEMA_RESOURCE_POLICY_MODE` environment variable for mode override.

### Task 4.3: Create ResourcePolicyCommand (Parent Command)

**Files:**
- Create: `metaschema-cli/src/main/java/dev/metaschema/cli/commands/resourcepolicy/ResourcePolicyCommand.java`
- Modify: `metaschema-cli/src/main/java/dev/metaschema/cli/commands/MetaschemaCommands.java` (register command)

Create a new `AbstractParentCommand` with `dump` and `check` subcommands. Follows the same pattern as `MetapathCommand`. Register in `MetaschemaCommands.COMMANDS`.

### Task 4.4: Implement `resource-policy dump` Subcommand

**Files:**
- Create: `metaschema-cli/src/main/java/dev/metaschema/cli/commands/resourcepolicy/DumpSubcommand.java`

`AbstractTerminalCommand` that prints the effective merged policy (after all config layers) as YAML to stdout. Uses `policy.describeEffectiveRules()`. Accepts `--resource-policy` and `--resource-policy-mode` flags.

### Task 4.5: Implement `resource-policy check` Subcommand

**Files:**
- Create: `metaschema-cli/src/main/java/dev/metaschema/cli/commands/resourcepolicy/CheckSubcommand.java`

`AbstractTerminalCommand` that takes a URI as a positional argument, runs it through the policy, and prints the `PolicyDecision` evaluation trace. Uses `policy.explain(URI)`. Accepts `--resource-policy` and `--resource-policy-mode` flags.

### Task 4.6: Documentation

**Files:**
- Update: Website documentation with resource access policy guide
- Update: CLI help text
- Include: Migration guide (AUDIT → ENFORCE transition steps)
- Include: YAML `!` quoting warning
- Include: Explicit note that `!` means DENY (contrast with `.gitignore`)

### Task 4.7: Final Verification

```bash
mvn clean install -PCI -Prelease
```

---

## Completion Checklist

**Phase 1: Policy Engine Core (PR1)**
- [ ] `PolicyMode` enum with DISABLED/AUDIT/ENFORCE and `mostRestrictive()`
- [ ] `SymlinkPolicy` enum with FOLLOW/NOFOLLOW
- [ ] `CaseSensitivity` enum with SYSTEM_DEFAULT/CASE_SENSITIVE/CASE_INSENSITIVE
- [ ] `AccessViolationException` with structured fields (layer, reason, source, remediation)
- [ ] `GlobMatcher` with case sensitivity, possessive quantifiers, pattern length limit
- [ ] `UriNormalizer` with path normalization, percent-decoding, symlink resolution, scheme/host normalization, JAR parsing
- [ ] `NetworkSecurityChecker` with CIDR block matching via IP library, alternate encoding support
- [ ] `NetworkSecurityConfig` with builder and `allowLoopback()`, `allowCidr()`
- [ ] `SchemePatternSet` with ordered pattern evaluation, case sensitivity, updated empty-patterns semantics
- [ ] `PolicyDecision` and `EvaluationStep` for diagnostics
- [ ] `IResourceAccessPolicy` interface
- [ ] `ResourceAccessPolicy` (immutable) with builder, factory methods, `toBuilder()`, `explain()`, `describeEffectiveRules()`
- [ ] `FileProtections` with `disabled()` (renamed from `none()`), blanket dot-dir exclusion, CWD root warning, conflict detection
- [ ] `package-info.java`
- [ ] IP boundary value tests for all private CIDR blocks
- [ ] Alternate IP encoding tests (decimal, hex, octal, shorthand, IPv4-mapped IPv6)
- [ ] Path traversal normalization tests
- [ ] Symlink traversal tests
- [ ] Case sensitivity tests
- [ ] ReDoS resistance tests
- [ ] JAR recursive checking tests
- [ ] All tests passing

**Phase 2: Configuration Model (PR2)**
- [ ] Metaschema module (`resource-access-policy-config_metaschema.yaml`)
- [ ] Maven code generation verified (no naming collision)
- [ ] IP address library dependency added (`com.github.seancfoley:ipaddress`)
- [ ] Bundled default policy (restrictive, AUDIT mode)
- [ ] `ResourceAccessPolicyLoader` with ratchet enforcement, `locked` flag, `inherit` merge
- [ ] Scheme name validation (warn on unrecognized)
- [ ] YAML `!` pattern validation
- [ ] Pattern complexity limits (count, length)
- [ ] Configuration layering tests
- [ ] All tests passing

**Phase 3: Loader Integration (PR3)**
- [ ] `IModuleLoader.setResourceAccessPolicy()` method
- [ ] `AbstractModuleLoader` policy integration (with relative URI resolution)
- [ ] `DefaultBoundLoader` policy integration
- [ ] `BindingConstraintLoader` policy integration
- [ ] `DefaultXmlDeserializer` policy integration
- [ ] HTTP redirect re-checking documented as integration requirement
- [ ] Integration tests for each loader type
- [ ] All tests passing

**Phase 4: CLI Integration (PR4)**
- [ ] `--resource-policy-mode` CLI flag
- [ ] `--resource-policy` CLI flag
- [ ] `ResourcePolicyCommand` parent command (extends `AbstractParentCommand`)
- [ ] `resource-policy dump` subcommand
- [ ] `resource-policy check <uri>` subcommand
- [ ] `METASCHEMA_RESOURCE_POLICY_MODE` env var
- [ ] Documentation (migration guide, YAML warnings, `!` semantics)
- [ ] Full CI build passing

**Final Verification:**
```bash
mvn clean install -PCI -Prelease
```
