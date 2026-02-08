# Resource Access Policy - Implementation Plan

**Goal:** Implement policy-based URI access control with glob patterns, graduated enforcement modes, and bundled defaults.

**Architecture:** All policy engine code in `core` module. CLI integration in `cli-processor`/`metaschema-cli`.

**Tech Stack:** Java 11, JUnit 5, SLF4J, Metaschema databind for configuration model.

---

## PR Breakdown

Implementation is organized into 4 PRs, each building on the previous:

| PR | Scope | Estimated Files | Key Deliverables |
|----|-------|-----------------|------------------|
| PR1 | Policy engine core | ~15 files | `GlobMatcher`, `SchemePatternSet`, `PolicyMode`, `ResourceAccessPolicy`, builder |
| PR2 | Configuration model | ~10 files | Metaschema module, config loading, bundled defaults, layering |
| PR3 | Loader integration | ~10 files | `IModuleLoader`/`IBoundLoader` integration, XML entity policy |
| PR4 | CLI integration + docs | ~8 files | CLI flags, env vars, documentation |

---

## PR1: Policy Engine Core

**Goal:** Implement the glob-based pattern matching engine, enforcement modes, and the `ResourceAccessPolicy` builder.

**Module:** `core`

**Package:** `dev.metaschema.core.model.policy`

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
  void testDefaultModeIsAudit() {
    assertEquals(PolicyMode.AUDIT, PolicyMode.defaultMode());
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
}
```

**Implementation:**

```java
package dev.metaschema.core.model.policy;

import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Enforcement mode for resource access policies.
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

  /**
   * Whether this mode performs policy checks.
   *
   * @return {@code true} if policy rules are evaluated
   */
  public boolean isCheckEnabled() {
    return checkEnabled;
  }

  /**
   * Whether this mode blocks violating requests.
   *
   * @return {@code true} if violations throw exceptions
   */
  public boolean isBlockEnabled() {
    return blockEnabled;
  }

  /**
   * Returns the default enforcement mode ({@link #AUDIT}).
   *
   * @return the default mode
   */
  @NonNull
  public static PolicyMode defaultMode() {
    return AUDIT;
  }

  /**
   * Parses a mode from a string value (case-insensitive).
   *
   * @param value
   *          the string to parse
   * @return the matching mode
   * @throws IllegalArgumentException
   *           if the value does not match any mode
   */
  @NonNull
  public static PolicyMode fromString(@NonNull String value) {
    return valueOf(value.toUpperCase(Locale.ROOT));
  }
}
```

---

### Task 1.2: Create AccessViolationException

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
  void testExceptionContainsUriAndReason() {
    URI uri = URI.create("file:///etc/passwd");
    String reason = "Denied by pattern: !/etc/**";

    AccessViolationException ex = new AccessViolationException(uri, reason);

    assertEquals(uri, ex.getUri());
    assertEquals(reason, ex.getReason());
    assertTrue(ex.getMessage().contains(uri.toString()));
    assertTrue(ex.getMessage().contains(reason));
  }

  @Test
  void testExtendsSecurityException() {
    AccessViolationException ex = new AccessViolationException(
        URI.create("http://localhost"), "denied");
    assertInstanceOf(SecurityException.class, ex);
  }
}
```

**Implementation:**

```java
package dev.metaschema.core.model.policy;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Exception thrown when a URI access violates the resource access policy
 * in {@link PolicyMode#ENFORCE} mode.
 */
public class AccessViolationException extends SecurityException {
  private static final long serialVersionUID = 1L;

  @NonNull
  private final URI uri;
  @NonNull
  private final String reason;

  /**
   * Constructs a new access violation exception.
   *
   * @param uri
   *          the URI that violated the policy
   * @param reason
   *          human-readable explanation of the violation
   */
  public AccessViolationException(@NonNull URI uri, @NonNull String reason) {
    super(String.format("Resource access policy violation for '%s': %s", uri, reason));
    this.uri = uri;
    this.reason = reason;
  }

  /**
   * Returns the URI that violated the policy.
   *
   * @return the violating URI
   */
  @NonNull
  public URI getUri() {
    return uri;
  }

  /**
   * Returns the reason for the violation.
   *
   * @return the violation reason
   */
  @NonNull
  public String getReason() {
    return reason;
  }
}
```

---

### Task 1.3: Create GlobMatcher

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
      "'example.com/path/**', 'example.com/path/to/resource', true",
      "'example.com/path/**', 'example.com/other/resource', false",
      "'**/.ssh/**', '/home/user/.ssh/id_rsa', true",
      "'**/.ssh/**', '/home/user/projects/ssh-keys', false",
      "'localhost/**', 'localhost:8080/api', true",
      "'127.*/**', '127.0.0.1/secret', true",
      "'127.*/**', '128.0.0.1/public', false",
  })
  void testPatternMatching(String pattern, String target, boolean expected) {
    GlobMatcher matcher = GlobMatcher.compile(pattern);
    assertEquals(expected, matcher.matches(target),
        () -> String.format("Pattern '%s' vs '%s'", pattern, target));
  }

  @Test
  void testNullSafety() {
    GlobMatcher matcher = GlobMatcher.compile("**");
    assertThrows(NullPointerException.class, () -> matcher.matches(null));
  }

  @Test
  void testEmptyPattern() {
    GlobMatcher matcher = GlobMatcher.compile("");
    assertTrue(matcher.matches(""));
    assertFalse(matcher.matches("anything"));
  }
}
```

**Implementation:** Compile glob patterns to `java.util.regex.Pattern`:
- `*` → matches any characters except `/`
- `**` → matches any characters including `/`
- `?` → matches single character except `/`
- Escape regex special characters
- Case-insensitive matching on Windows for file paths

---

### Task 1.4: Create SchemePatternSet

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
  void testNoPatternsAllowsAll() {
    SchemePatternSet set = SchemePatternSet.enabled("https");
    assertTrue(set.isAllowed("example.com/anything"));
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
        .allow("**")          // allow everything
        .deny("/etc/**")      // except /etc
        .allow("/etc/motd")   // but re-allow /etc/motd
        .build();

    assertTrue(set.isAllowed("/workspace/file.xml"));
    assertFalse(set.isAllowed("/etc/passwd"));
    assertTrue(set.isAllowed("/etc/motd"));
  }

  @Test
  void testNoMatchUsesDefault() {
    // With default deny (no patterns match)
    SchemePatternSet set = SchemePatternSet.builder("https")
        .allow("nist.gov/**")
        .build();

    assertTrue(set.isAllowed("nist.gov/schemas/x.xml"));
    assertFalse(set.isAllowed("evil.com/attack"));
  }
}
```

**Implementation:** Holds an ordered list of `(GlobMatcher, boolean isAllow)` entries. Evaluates last-match-wins.

---

### Task 1.5: Create IResourceAccessPolicy Interface

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/IResourceAccessPolicy.java`

**Implementation:**

```java
package dev.metaschema.core.model.policy;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Policy that controls which URIs can be accessed during resource loading.
 * <p>
 * Implementations evaluate URIs against configured rules and take action
 * based on the {@link PolicyMode}: log violations (audit), block violations
 * (enforce), or skip checking entirely (disabled).
 *
 * @see ResourceAccessPolicy
 */
public interface IResourceAccessPolicy {

  /**
   * A policy that allows all access without checking.
   */
  IResourceAccessPolicy ALLOW_ALL = uri -> { /* no-op */ };

  /**
   * Checks whether the given URI is allowed by this policy.
   * <p>
   * Depending on the {@link PolicyMode}:
   * <ul>
   *   <li>{@code DISABLED}: No checking, always returns</li>
   *   <li>{@code AUDIT}: Checks and logs violations, always returns</li>
   *   <li>{@code ENFORCE}: Checks and throws
   *       {@link AccessViolationException} on violation</li>
   * </ul>
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

### Task 1.6: Create ResourceAccessPolicy and Builder

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

    // Should not throw even though file scheme is denied
    assertDoesNotThrow(() -> policy.checkAccess(URI.create("file:///etc/passwd")));
  }

  @Test
  void testAuditModeLogsButAllows() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.AUDIT)
        .forScheme("http").denyAll()
        .defaultDeny()
        .build();

    // Should not throw even though http is denied
    assertDoesNotThrow(() -> policy.checkAccess(URI.create("http://localhost/admin")));
  }

  @Test
  void testEnforceModeBlocks() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("http").denyAll()
        .defaultDeny()
        .build();

    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(URI.create("http://localhost/admin")));
  }

  @Test
  void testEnforceModeAllowsMatching() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("https")
            .allow("nist.gov/**")
        .forScheme("file")
            .allow("/workspace/**")
        .defaultDeny()
        .build();

    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("https://nist.gov/schemas/x.xml")));
    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("file:///workspace/project/module.xml")));
  }

  @Test
  void testDenyPatternExceptions() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("file")
            .allow("**")
            .deny("**/.ssh/**")
        .defaultDeny()
        .build();

    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("file:///workspace/schema.xml")));
    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(
            URI.create("file:///home/user/.ssh/id_rsa")));
  }

  @Test
  void testDefaultDenyBlocksUnknownSchemes() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("https").allowAll()
        .defaultDeny()
        .build();

    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(URI.create("ftp://evil.com/file")));
  }

  @Test
  void testWithModeCreatesNewPolicy() {
    ResourceAccessPolicy audit = ResourceAccessPolicy.builder()
        .mode(PolicyMode.AUDIT)
        .forScheme("http").denyAll()
        .defaultDeny()
        .build();

    // Audit mode allows
    assertDoesNotThrow(() -> audit.checkAccess(
        URI.create("http://localhost/admin")));

    // Enforce mode blocks
    ResourceAccessPolicy enforced = audit.withMode(PolicyMode.ENFORCE);
    assertThrows(AccessViolationException.class,
        () -> enforced.checkAccess(URI.create("http://localhost/admin")));
  }

  @Test
  void testUriSchemeExtraction() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("file")
            .allow("/workspace/**")
        .defaultDeny()
        .build();

    // file:///workspace/x → matches file scheme, path /workspace/x
    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("file:///workspace/x.xml")));

    // https not configured, default deny
    assertThrows(AccessViolationException.class,
        () -> policy.checkAccess(URI.create("https://example.com")));
  }

  @Test
  void testJarSchemeExtraction() {
    ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
        .mode(PolicyMode.ENFORCE)
        .forScheme("jar")
            .allow("/schema/**")
        .defaultDeny()
        .build();

    // jar:file:///lib.jar!/schema/x.xsd → matches jar scheme, path /schema/x.xsd
    assertDoesNotThrow(() -> policy.checkAccess(
        URI.create("jar:file:///lib.jar!/schema/x.xsd")));
  }
}
```

**Implementation:** Main policy class that:
1. Extracts scheme from URI
2. Looks up `SchemePatternSet` for that scheme
3. Extracts scheme-specific match target from URI
4. Evaluates patterns
5. Applies mode behavior (log/block/ignore)

---

### Task 1.7: Create FileProtections

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
    FileProtections protections = FileProtections.withDefaults(cwd);
    assertFalse(protections.isAllowed(path),
        "Should deny (outside safe areas): " + path);
  }

  @Test
  void testDefaultAllowsCwdSubtree() {
    FileProtections protections = FileProtections.withDefaults(cwd);
    String cwdPath = cwd.resolve("project/schema.xml").toString();
    assertTrue(protections.isAllowed(cwdPath),
        "Should allow CWD subtree");
  }

  @Test
  void testDefaultDeniesSensitiveDotDirsInHome() {
    // Home dir subtree is allowed, but sensitive dot-dirs are excluded
    Path home = Path.of(System.getProperty("user.home"));
    FileProtections protections = FileProtections.withDefaults(cwd);

    String sshKey = home.resolve(".ssh/id_rsa").toString();
    assertFalse(protections.isAllowed(sshKey),
        "Should deny ~/.ssh even though home is allowed");

    String awsCreds = home.resolve(".aws/credentials").toString();
    assertFalse(protections.isAllowed(awsCreds),
        "Should deny ~/.aws even though home is allowed");

    String normalFile = home.resolve("projects/schema.xml").toString();
    assertTrue(protections.isAllowed(normalFile),
        "Should allow normal files in home");
  }

  @Test
  void testBuilderIncludeDefaults() {
    FileProtections protections = FileProtections.builder(cwd)
        .includeDefaults()
        .allow("/opt/metaschema/**")
        .build();

    String cwdFile = cwd.resolve("schema.xml").toString();
    assertTrue(protections.isAllowed(cwdFile));             // from defaults
    assertTrue(protections.isAllowed("/opt/metaschema/x")); // custom addition
    assertFalse(protections.isAllowed("/etc/passwd"));      // not allowed
  }

  @Test
  void testBuilderRemoveDefault() {
    FileProtections protections = FileProtections.builder(cwd)
        .includeDefaults()
        .remove("<user.home>/**")  // remove home dir access
        .build();

    Path home = Path.of(System.getProperty("user.home"));
    String homeFile = home.resolve("file.txt").toString();
    assertFalse(protections.isAllowed(homeFile));  // removed

    String cwdFile = cwd.resolve("file.txt").toString();
    assertTrue(protections.isAllowed(cwdFile));     // CWD still allowed
  }

  @Test
  void testBuilderFullyCustom() {
    FileProtections protections = FileProtections.builder(cwd)
        .allow("/opt/app/**")
        .build();

    assertTrue(protections.isAllowed("/opt/app/schema.xml"));
    assertFalse(protections.isAllowed("/etc/passwd"));
    // CWD not included since we didn't call includeDefaults()
    String cwdFile = cwd.resolve("file.txt").toString();
    assertFalse(protections.isAllowed(cwdFile));
  }

  @Test
  void testNoneAllowsEverything() {
    FileProtections protections = FileProtections.none();
    assertTrue(protections.isAllowed("/etc/passwd"));
    assertTrue(protections.isAllowed("/home/user/.ssh/key"));
  }

  @Test
  void testDefaultPatternsAreInspectable() {
    assertFalse(FileProtections.defaultAllowPatterns().isEmpty());
  }
}
```

**Implementation:** `FileProtections` holds an ordered list of allow/deny patterns (with `!` negation) checked against file paths. Provides:
- `withDefaults(Path cwd)` — shipped allow patterns (CWD + home minus sensitive dot-dirs)
- `none()` — no protections (allows everything)
- `builder(Path cwd)` — customizable with `includeDefaults()`, `allow()`, `remove()`
- `defaultAllowPatterns()` — static method to inspect defaults
- `isAllowed(String path)` — check if a path is allowed

---

### Task 1.8: Add package-info.java

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/package-info.java`

---

### Task 1.9: Verify PR1 Build

```bash
mvn -pl core clean install
mvn -pl core checkstyle:check
```

---

## PR2: Configuration Model and Bundled Defaults

**Goal:** Define the Metaschema configuration module, implement config loading, and ship bundled restrictive defaults.

### Task 2.1: Create Metaschema Configuration Module

**Files:**
- Create: `core/src/main/metaschema/resource-access-policy_metaschema.yaml`

The Metaschema module definition for the resource access policy configuration model. See PRD for full module definition.

---

### Task 2.2: Configure Maven Code Generation

**Files:**
- Modify: `core/pom.xml` (if needed - verify if metaschema-maven-plugin is already configured for `src/main/metaschema`)

Verify generated binding classes compile and contain expected fields:
- `ResourceAccessPolicy` (root assembly)
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
    // Audit mode: should log but not throw
    assertDoesNotThrow(() -> defaults.checkAccess(
        URI.create("http://localhost/admin")));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "https://pages.nist.gov/schemas/x.xml",
      "https://example.com/api",
      "file:///workspace/schema.xml",
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

  @ParameterizedTest
  @ValueSource(strings = {
      "file:///etc/passwd",
      "file:///proc/self/environ",
      "file:///home/user/.ssh/id_rsa",
      "file:///home/user/.aws/credentials",
  })
  void testDefaultDeniedFilePathsInEnforceMode(String uriString) {
    ResourceAccessPolicy enforced = defaults.withMode(PolicyMode.ENFORCE);
    assertThrows(AccessViolationException.class,
        () -> enforced.checkAccess(URI.create(uriString)));
  }
}
```

**Implementation:** Load bundled YAML from classpath resource and parse into `ResourceAccessPolicy`.

---

### Task 2.4: Implement Configuration Loading

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/policy/ResourceAccessPolicyLoader.java`
- Test: `core/src/test/java/dev/metaschema/core/model/policy/ResourceAccessPolicyLoaderTest.java`

**Test first:** Verify loading from YAML, JSON, and XML config files. Verify configuration layering with merge semantics.

**Implementation:** Uses `IBoundLoader` to load the generated binding classes, then converts to `ResourceAccessPolicy`.

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

Add method to set resource access policy:

```java
/**
 * Sets the resource access policy for this loader.
 * <p>
 * When set, all URIs resolved by this loader are checked against the policy
 * before loading.
 *
 * @param policy
 *          the policy to enforce, or {@code null} to disable
 */
void setResourceAccessPolicy(@Nullable IResourceAccessPolicy policy);
```

---

### Task 3.2: Integrate Policy in AbstractModuleLoader

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/AbstractModuleLoader.java`
- Test: `core/src/test/java/dev/metaschema/core/model/AbstractModuleLoaderPolicyTest.java`

**Test first:** Verify module import URIs are checked against policy.

**Implementation:** Add policy field and check before URI resolution:

```java
// In resolveImport or similar method:
URI resolvedResource = ObjectUtils.notNull(resource.resolve(importedResource));
IResourceAccessPolicy policy = getResourceAccessPolicy();
if (policy != null) {
  policy.checkAccess(resolvedResource);
}
```

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

**Test first:** Verify XML entity resolution URIs are checked against policy.

---

### Task 3.6: Verify PR3 Build

```bash
mvn clean install -PCI -Prelease
```

---

## PR4: CLI Integration and Documentation

**Goal:** Add CLI flags for policy mode control and documentation.

### Task 4.1: Add CLI Flags

**Files:**
- Modify: `metaschema-cli/src/main/java/dev/metaschema/cli/CLI.java` (or relevant command classes)

Add flags:
- `--resource-policy-mode=<disabled|audit|enforce>` - Override enforcement mode
- `--resource-policy=<path>` - Load custom policy configuration file

---

### Task 4.2: Add Environment Variable Support

Support `METASCHEMA_RESOURCE_POLICY_MODE` environment variable for mode override.

---

### Task 4.3: Documentation

**Files:**
- Update: Website documentation with resource access policy guide
- Update: CLI help text

---

### Task 4.4: Final Verification

```bash
mvn clean install -PCI -Prelease
```

---

## Completion Checklist

**Phase 1: Policy Engine Core (PR1)**
- [ ] `PolicyMode` enum with DISABLED/AUDIT/ENFORCE
- [ ] `AccessViolationException` for ENFORCE mode
- [ ] `GlobMatcher` with `.gitignore`-style glob matching
- [ ] `SchemePatternSet` with ordered pattern evaluation and `!` negation
- [ ] `IResourceAccessPolicy` interface
- [ ] `ResourceAccessPolicy` with builder
- [ ] `FileProtections` with defaults, builder, and customization API
- [ ] `package-info.java`
- [ ] All tests passing

**Phase 2: Configuration Model (PR2)**
- [ ] Metaschema module definition (`resource-access-policy_metaschema.yaml`)
- [ ] Maven code generation verified
- [ ] Bundled default policy (restrictive, audit mode)
- [ ] `ResourceAccessPolicyLoader` for config file loading
- [ ] Configuration layering with merge semantics
- [ ] All tests passing

**Phase 3: Loader Integration (PR3)**
- [ ] `IModuleLoader.setResourceAccessPolicy()` method
- [ ] `AbstractModuleLoader` policy integration
- [ ] `DefaultBoundLoader` policy integration
- [ ] `BindingConstraintLoader` policy integration
- [ ] `DefaultXmlDeserializer` policy integration
- [ ] Integration tests for each loader type
- [ ] All tests passing

**Phase 4: CLI Integration (PR4)**
- [ ] `--resource-policy-mode` CLI flag
- [ ] `--resource-policy` CLI flag
- [ ] `METASCHEMA_RESOURCE_POLICY_MODE` env var
- [ ] Documentation
- [ ] Full CI build passing

**Final Verification:**
```bash
mvn clean install -PCI -Prelease
```
