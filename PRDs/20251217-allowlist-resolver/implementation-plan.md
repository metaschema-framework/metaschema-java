# Allowlist URI Resolver - Implementation Plan

**Goal:** Implement secure-by-default URI resolver with allowlist/denylist controls for all resource loading paths.

**Architecture:** Layered approach - core interfaces first, then rules engine, then integration points.

**Tech Stack:** Java 11, JUnit 5, Mockito, SnakeYAML, SLF4J

---

## PR Breakdown

Single PR with multiple commits organized by feature area.

| PR | Scope | Commits | Files | Estimated Size |
|----|-------|---------|-------|----------------|
| PR1 | Complete allowlist resolver implementation | ~15 commits | ~40 files | Large |

### Commit Sequence

**Phase 1: Core Resolver**
1. Core interfaces and exceptions (AccessDeniedException, IAccessDeniedHandler, IUriAccessRule, IAllowlistUriResolver)
2. SchemePolicy implementation
3. Built-in denylist (NetworkDenylist, FileSystemDenylist, BuiltInDenylist)
4. FileSystemRule implementation
5. HttpRule implementation
6. JarRule implementation
7. AllowlistUriResolver and builder

**Phase 2: Configuration System**
8. IConfigurationService interface
9. ConfigurationService implementation with directory discovery
10. AllowlistConfigurationMerger (deep merge on scheme, shallow on domain)
11. AllowlistConfiguration POJO and YAML loading

**Phase 3: Integration**
12. Update AbstractModuleLoader to use IUriResolver
13. Update BindingConstraintLoader to use IUriResolver
14. Update DefaultXmlDeserializer to use IUriResolver
15. CLI integration (--config-dir option, METASCHEMA_CONFIG_DIR env var)
16. Documentation and examples

---

## PR1: Core Interfaces and Exceptions

**Goal:** Establish foundational interfaces and exception types.

**Package:** `gov.nist.secauto.metaschema.core.model.resolver`

### Task 1.1: Create AccessDeniedException

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/AccessDeniedException.java`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/AccessDeniedExceptionTest.java`

**Step 1: Write the failing test**

```java
package gov.nist.secauto.metaschema.core.model.resolver;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.net.URI;

class AccessDeniedExceptionTest {

  @Test
  void testExceptionContainsUriAndReason() {
    URI uri = URI.create("file:///etc/passwd");
    String reason = "File system access denied by allowlist policy";

    AccessDeniedException ex = new AccessDeniedException(uri, reason);

    assertEquals(uri, ex.getUri());
    assertEquals(reason, ex.getReason());
    assertTrue(ex.getMessage().contains(uri.toString()));
    assertTrue(ex.getMessage().contains(reason));
  }

  @Test
  void testExceptionWithCause() {
    URI uri = URI.create("http://localhost:8080/admin");
    String reason = "Built-in denylist: localhost";
    Throwable cause = new SecurityException("Blocked");

    AccessDeniedException ex = new AccessDeniedException(uri, reason, cause);

    assertEquals(uri, ex.getUri());
    assertEquals(reason, ex.getReason());
    assertEquals(cause, ex.getCause());
  }
}
```

**Step 2: Run test to verify it fails**

```bash
mvn -pl core test -Dtest=AccessDeniedExceptionTest -DfailIfNoTests=false
```

Expected: Compilation error - class does not exist

**Step 3: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Exception thrown when URI access is denied by the allowlist resolver.
 * <p>
 * This exception provides details about which URI was blocked and why,
 * supporting both security auditing and debugging.
 */
public class AccessDeniedException extends SecurityException {
  private static final long serialVersionUID = 1L;

  @NonNull
  private final URI uri;
  @NonNull
  private final String reason;

  /**
   * Constructs a new access denied exception.
   *
   * @param uri
   *          the URI that was denied access
   * @param reason
   *          human-readable explanation of why access was denied
   */
  public AccessDeniedException(@NonNull URI uri, @NonNull String reason) {
    super(formatMessage(uri, reason));
    this.uri = uri;
    this.reason = reason;
  }

  /**
   * Constructs a new access denied exception with a cause.
   *
   * @param uri
   *          the URI that was denied access
   * @param reason
   *          human-readable explanation of why access was denied
   * @param cause
   *          the underlying cause of the denial
   */
  public AccessDeniedException(@NonNull URI uri, @NonNull String reason, @NonNull Throwable cause) {
    super(formatMessage(uri, reason), cause);
    this.uri = uri;
    this.reason = reason;
  }

  private static String formatMessage(URI uri, String reason) {
    return String.format("Access denied to URI '%s': %s", uri, reason);
  }

  /**
   * Returns the URI that was denied access.
   *
   * @return the blocked URI
   */
  @NonNull
  public URI getUri() {
    return uri;
  }

  /**
   * Returns the reason access was denied.
   *
   * @return human-readable denial reason
   */
  @NonNull
  public String getReason() {
    return reason;
  }
}
```

**Step 4: Run test to verify it passes**

```bash
mvn -pl core test -Dtest=AccessDeniedExceptionTest
```

Expected: PASS

**Step 5: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/AccessDeniedException.java
git add core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/AccessDeniedExceptionTest.java
git commit -m "feat(resolver): add AccessDeniedException for blocked URI access"
```

---

### Task 1.2: Create IAccessDeniedHandler Interface

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/IAccessDeniedHandler.java`

**Step 1: Write implementation** (interface-only, no test needed)

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Handler invoked when URI access is denied by the allowlist resolver.
 * <p>
 * Implementations can customize behavior when access is blocked, such as
 * logging, throwing custom exceptions, or returning alternative resources.
 */
@FunctionalInterface
public interface IAccessDeniedHandler {

  /**
   * Default handler that throws {@link AccessDeniedException}.
   */
  IAccessDeniedHandler THROW_EXCEPTION = (uri, reason) -> {
    throw new AccessDeniedException(uri, reason);
  };

  /**
   * Called when access to a URI is denied.
   *
   * @param uri
   *          the URI that was denied access
   * @param reason
   *          human-readable explanation of why access was denied
   * @throws AccessDeniedException
   *           if the handler chooses to throw (default behavior)
   */
  void handleAccessDenied(@NonNull URI uri, @NonNull String reason);
}
```

**Step 2: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/IAccessDeniedHandler.java
git commit -m "feat(resolver): add IAccessDeniedHandler interface for custom denial handling"
```

---

### Task 1.3: Create IUriAccessRule Interface

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/IUriAccessRule.java`

**Step 1: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A rule that determines whether access to a URI should be allowed or denied.
 * <p>
 * Rules are evaluated in order, and the first matching rule determines the
 * access decision. If no rule matches, access is denied by default.
 */
public interface IUriAccessRule {

  /**
   * Result of evaluating a URI against this rule.
   */
  enum RuleResult {
    /** The rule allows access to the URI. */
    ALLOW,
    /** The rule denies access to the URI. */
    DENY,
    /** The rule does not apply to this URI; check next rule. */
    NO_MATCH
  }

  /**
   * Evaluates whether this rule applies to the given URI and what the access
   * decision is.
   *
   * @param uri
   *          the URI to evaluate
   * @return the rule result indicating allow, deny, or no match
   */
  @NonNull
  RuleResult evaluate(@NonNull URI uri);

  /**
   * Returns a human-readable description of this rule for logging and debugging.
   *
   * @return rule description
   */
  @NonNull
  String getDescription();
}
```

**Step 2: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/IUriAccessRule.java
git commit -m "feat(resolver): add IUriAccessRule interface for access decisions"
```

---

### Task 1.4: Create IAllowlistUriResolver Interface

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/IAllowlistUriResolver.java`

**Step 1: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver;

import gov.nist.secauto.metaschema.core.model.IUriResolver;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A URI resolver that enforces allowlist-based access control.
 * <p>
 * This resolver validates URIs against configured rules before allowing access,
 * providing defense against local file inclusion, SSRF, and other URI-based
 * attacks.
 * <p>
 * The resolver enforces:
 * <ul>
 *   <li>Scheme policies (allow/deny by URI scheme)</li>
 *   <li>File system rules (allowed directories and paths)</li>
 *   <li>HTTP rules (allowed domains and path prefixes)</li>
 *   <li>JAR resource rules (allowed paths within JARs)</li>
 *   <li>Built-in denylist (always blocks dangerous patterns)</li>
 * </ul>
 *
 * @see AllowlistUriResolver
 */
public interface IAllowlistUriResolver extends IUriResolver {

  /**
   * Checks whether access to the given URI would be allowed without actually
   * resolving it.
   * <p>
   * This method is useful for pre-validation or UI feedback without triggering
   * the access denied handler.
   *
   * @param uri
   *          the URI to check
   * @return {@code true} if the URI would be allowed, {@code false} otherwise
   */
  boolean isAllowed(@NonNull URI uri);

  /**
   * Returns the reason why a URI would be denied, or empty if allowed.
   *
   * @param uri
   *          the URI to check
   * @return denial reason, or {@code null} if the URI is allowed
   */
  String getDenialReason(@NonNull URI uri);
}
```

**Step 2: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/IAllowlistUriResolver.java
git commit -m "feat(resolver): add IAllowlistUriResolver interface extending IUriResolver"
```

---

### Task 1.5: Create SchemePolicy Enum

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/SchemePolicy.java`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/SchemePolicyTest.java`

**Step 1: Write the failing test**

```java
package gov.nist.secauto.metaschema.core.model.resolver;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SchemePolicyTest {

  @Test
  void testDefaultPolicyDeniesAll() {
    SchemePolicy policy = SchemePolicy.denyAll();

    assertFalse(policy.isAllowed("file"));
    assertFalse(policy.isAllowed("http"));
    assertFalse(policy.isAllowed("https"));
    assertFalse(policy.isAllowed("jar"));
  }

  @Test
  void testAllowSpecificSchemes() {
    SchemePolicy policy = SchemePolicy.denyAll()
        .withAllowed("https")
        .withAllowed("jar");

    assertFalse(policy.isAllowed("file"));
    assertFalse(policy.isAllowed("http"));
    assertTrue(policy.isAllowed("https"));
    assertTrue(policy.isAllowed("jar"));
  }

  @Test
  void testDenySpecificSchemes() {
    SchemePolicy policy = SchemePolicy.allowAll()
        .withDenied("file")
        .withDenied("ftp");

    assertFalse(policy.isAllowed("file"));
    assertFalse(policy.isAllowed("ftp"));
    assertTrue(policy.isAllowed("http"));
    assertTrue(policy.isAllowed("https"));
  }

  @ParameterizedTest
  @CsvSource({
      "FILE, file",
      "HTTPS, https",
      "HTTP, http"
  })
  void testSchemeNormalization(String input, String expected) {
    SchemePolicy policy = SchemePolicy.denyAll().withAllowed(input);
    assertTrue(policy.isAllowed(expected));
    assertTrue(policy.isAllowed(input.toUpperCase()));
  }
}
```

**Step 2: Run test to verify it fails**

```bash
mvn -pl core test -Dtest=SchemePolicyTest -DfailIfNoTests=false
```

Expected: Compilation error

**Step 3: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Policy for allowing or denying URI schemes.
 * <p>
 * Schemes are normalized to lowercase for comparison.
 */
public final class SchemePolicy {

  private final boolean defaultAllow;
  @NonNull
  private final Set<String> allowedSchemes;
  @NonNull
  private final Set<String> deniedSchemes;

  private SchemePolicy(boolean defaultAllow, Set<String> allowed, Set<String> denied) {
    this.defaultAllow = defaultAllow;
    this.allowedSchemes = CollectionUtil.unmodifiableSet(new HashSet<>(allowed));
    this.deniedSchemes = CollectionUtil.unmodifiableSet(new HashSet<>(denied));
  }

  /**
   * Creates a policy that denies all schemes by default.
   *
   * @return a deny-all policy
   */
  @NonNull
  public static SchemePolicy denyAll() {
    return new SchemePolicy(false, Set.of(), Set.of());
  }

  /**
   * Creates a policy that allows all schemes by default.
   *
   * @return an allow-all policy
   */
  @NonNull
  public static SchemePolicy allowAll() {
    return new SchemePolicy(true, Set.of(), Set.of());
  }

  /**
   * Returns a new policy with the specified scheme allowed.
   *
   * @param scheme
   *          the scheme to allow
   * @return new policy with scheme allowed
   */
  @NonNull
  public SchemePolicy withAllowed(@NonNull String scheme) {
    Set<String> newAllowed = new HashSet<>(allowedSchemes);
    newAllowed.add(normalizeScheme(scheme));
    return new SchemePolicy(defaultAllow, newAllowed, deniedSchemes);
  }

  /**
   * Returns a new policy with the specified scheme denied.
   *
   * @param scheme
   *          the scheme to deny
   * @return new policy with scheme denied
   */
  @NonNull
  public SchemePolicy withDenied(@NonNull String scheme) {
    Set<String> newDenied = new HashSet<>(deniedSchemes);
    newDenied.add(normalizeScheme(scheme));
    return new SchemePolicy(defaultAllow, allowedSchemes, newDenied);
  }

  /**
   * Checks if the given scheme is allowed by this policy.
   *
   * @param scheme
   *          the scheme to check
   * @return {@code true} if allowed, {@code false} if denied
   */
  public boolean isAllowed(@NonNull String scheme) {
    String normalized = normalizeScheme(scheme);

    // Explicit deny takes precedence
    if (deniedSchemes.contains(normalized)) {
      return false;
    }

    // Explicit allow
    if (allowedSchemes.contains(normalized)) {
      return true;
    }

    // Fall back to default
    return defaultAllow;
  }

  private static String normalizeScheme(String scheme) {
    return scheme.toLowerCase(Locale.ROOT);
  }
}
```

**Step 4: Run test to verify it passes**

```bash
mvn -pl core test -Dtest=SchemePolicyTest
```

Expected: PASS

**Step 5: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/SchemePolicy.java
git add core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/SchemePolicyTest.java
git commit -m "feat(resolver): add SchemePolicy for URI scheme allow/deny decisions"
```

---

### Task 1.6: Verify PR1 Build

**Step 1: Run full build for core module**

```bash
mvn -pl core clean install
```

Expected: BUILD SUCCESS

**Step 2: Run checkstyle**

```bash
mvn -pl core checkstyle:check
```

Expected: No violations

**Step 3: Commit and prepare PR**

```bash
git push -u me feature/183-allowlist-resolver
```

---

## PR2: Built-In Denylist

**Goal:** Implement always-enforced security rules that cannot be disabled.

### Task 2.1: Create NetworkDenylist

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/NetworkDenylist.java`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/NetworkDenylistTest.java`

**Step 1: Write the failing test**

```java
package gov.nist.secauto.metaschema.core.model.resolver.denylist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;

class NetworkDenylistTest {

  private final NetworkDenylist denylist = NetworkDenylist.getInstance();

  @ParameterizedTest
  @ValueSource(strings = {
      "http://localhost/admin",
      "http://localhost:8080/api",
      "http://127.0.0.1/secret",
      "http://127.0.0.2:9000/data",
      "http://[::1]/internal",
      "http://169.254.169.254/latest/meta-data/",  // AWS metadata
      "http://metadata.google.internal/",           // GCP metadata
      "http://10.0.0.1/internal",                   // Private Class A
      "http://172.16.0.1/internal",                 // Private Class B
      "http://172.31.255.255/internal",             // Private Class B upper
      "http://192.168.1.1/router",                  // Private Class C
      "http://0.0.0.0/",                            // All interfaces
  })
  void testBlockedAddresses(String uriString) {
    URI uri = URI.create(uriString);
    assertTrue(denylist.isDenied(uri), "Should block: " + uriString);
    assertNotNull(denylist.getDenialReason(uri));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "https://example.com/api",
      "https://pages.nist.gov/schema",
      "https://raw.githubusercontent.com/file.xml",
      "http://8.8.8.8/dns",                        // Public IP
      "https://172.217.0.1/google",                // Public IP in 172.x range but not private
  })
  void testAllowedAddresses(String uriString) {
    URI uri = URI.create(uriString);
    assertFalse(denylist.isDenied(uri), "Should allow: " + uriString);
  }

  @Test
  void testNonHttpSchemesNotChecked() {
    // File URIs don't have network hosts - handled by FileSystemDenylist
    URI fileUri = URI.create("file:///etc/passwd");
    assertFalse(denylist.isDenied(fileUri));
  }
}
```

**Step 2: Run test to verify it fails**

```bash
mvn -pl core test -Dtest=NetworkDenylistTest -DfailIfNoTests=false
```

Expected: Compilation error

**Step 3: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver.denylist;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Built-in denylist for network addresses that should never be accessed.
 * <p>
 * This includes:
 * <ul>
 *   <li>Localhost and loopback addresses</li>
 *   <li>Private IP ranges (RFC 1918)</li>
 *   <li>Link-local addresses (cloud metadata endpoints)</li>
 *   <li>Known metadata service hostnames</li>
 * </ul>
 * <p>
 * This denylist cannot be disabled and is always enforced.
 */
public final class NetworkDenylist {

  private static final NetworkDenylist INSTANCE = new NetworkDenylist();

  /** Hostnames that are always blocked. */
  private static final List<String> BLOCKED_HOSTNAMES = List.of(
      "localhost",
      "metadata.google.internal",
      "instance-data",
      "kubernetes.default.svc"
  );

  /** Hostname patterns that are always blocked. */
  private static final List<Pattern> BLOCKED_HOSTNAME_PATTERNS = List.of(
      Pattern.compile(".*\\.localhost$", Pattern.CASE_INSENSITIVE),
      Pattern.compile(".*\\.local$", Pattern.CASE_INSENSITIVE)
  );

  private NetworkDenylist() {
    // singleton
  }

  /**
   * Returns the singleton instance.
   *
   * @return the network denylist instance
   */
  @NonNull
  public static NetworkDenylist getInstance() {
    return INSTANCE;
  }

  /**
   * Checks if the given URI's host is on the denylist.
   *
   * @param uri
   *          the URI to check
   * @return {@code true} if the host is denied
   */
  public boolean isDenied(@NonNull URI uri) {
    return getDenialReason(uri) != null;
  }

  /**
   * Returns the reason why the URI's host is denied, or null if allowed.
   *
   * @param uri
   *          the URI to check
   * @return denial reason or null
   */
  @Nullable
  public String getDenialReason(@NonNull URI uri) {
    String host = uri.getHost();
    if (host == null) {
      return null; // No host component (e.g., file:// URIs)
    }

    String lowerHost = host.toLowerCase(Locale.ROOT);

    // Check exact hostname matches
    if (BLOCKED_HOSTNAMES.contains(lowerHost)) {
      return "Built-in denylist: blocked hostname '" + host + "'";
    }

    // Check hostname patterns
    for (Pattern pattern : BLOCKED_HOSTNAME_PATTERNS) {
      if (pattern.matcher(lowerHost).matches()) {
        return "Built-in denylist: blocked hostname pattern '" + host + "'";
      }
    }

    // Check IP addresses
    return checkIpAddress(host);
  }

  @Nullable
  private String checkIpAddress(String host) {
    // Handle IPv6 addresses in brackets
    String ipString = host;
    if (host.startsWith("[") && host.endsWith("]")) {
      ipString = host.substring(1, host.length() - 1);
    }

    try {
      InetAddress addr = InetAddress.getByName(ipString);

      if (addr.isLoopbackAddress()) {
        return "Built-in denylist: loopback address";
      }

      if (addr.isLinkLocalAddress()) {
        return "Built-in denylist: link-local address (potential cloud metadata endpoint)";
      }

      if (addr.isSiteLocalAddress()) {
        return "Built-in denylist: private network address";
      }

      if (addr.isAnyLocalAddress()) {
        return "Built-in denylist: wildcard address";
      }

      // Check for 0.0.0.0 explicitly
      byte[] bytes = addr.getAddress();
      if (bytes.length == 4 && bytes[0] == 0 && bytes[1] == 0 && bytes[2] == 0 && bytes[3] == 0) {
        return "Built-in denylist: all-interfaces address";
      }

    } catch (UnknownHostException e) {
      // Not a valid IP address, treat as hostname (already checked above)
    }

    return null;
  }
}
```

**Step 4: Run test to verify it passes**

```bash
mvn -pl core test -Dtest=NetworkDenylistTest
```

Expected: PASS

**Step 5: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/NetworkDenylist.java
git add core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/NetworkDenylistTest.java
git commit -m "feat(resolver): add NetworkDenylist for blocking internal network access"
```

---

### Task 2.2: Create FileSystemDenylist

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/FileSystemDenylist.java`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/FileSystemDenylistTest.java`

**Step 1: Write the failing test**

```java
package gov.nist.secauto.metaschema.core.model.resolver.denylist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;

class FileSystemDenylistTest {

  private final FileSystemDenylist denylist = FileSystemDenylist.getInstance();

  @ParameterizedTest
  @EnabledOnOs(OS.LINUX)
  @ValueSource(strings = {
      "file:///etc/passwd",
      "file:///etc/shadow",
      "file:///proc/self/environ",
      "file:///sys/kernel/debug",
      "file:///dev/null",
      "file:///root/.ssh/id_rsa",
      "file:///home/user/.ssh/known_hosts",
      "file:///home/user/.aws/credentials",
      "file:///home/user/.gnupg/private-keys-v1.d/key",
      "file:///var/run/secrets/kubernetes.io/token",
  })
  void testBlockedUnixPaths(String uriString) {
    URI uri = URI.create(uriString);
    assertTrue(denylist.isDenied(uri), "Should block: " + uriString);
    assertNotNull(denylist.getDenialReason(uri));
  }

  @ParameterizedTest
  @EnabledOnOs(OS.WINDOWS)
  @ValueSource(strings = {
      "file:///C:/Windows/System32/config/SAM",
      "file:///C:/Users/admin/AppData/Local/secret",
      "file:///C:/ProgramData/sensitive",
      "file:///C:/Users/admin/.ssh/id_rsa",
      "file:///C:/Users/admin/.aws/credentials",
  })
  void testBlockedWindowsPaths(String uriString) {
    URI uri = URI.create(uriString);
    assertTrue(denylist.isDenied(uri), "Should block: " + uriString);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "file:///data/schemas/module.xml",
      "file:///app/config/settings.yaml",
      "file:///workspace/project/src/main.java",
  })
  void testAllowedPaths(String uriString) {
    URI uri = URI.create(uriString);
    assertFalse(denylist.isDenied(uri), "Should allow: " + uriString);
  }

  @Test
  void testPathTraversalNormalization() {
    // These should be blocked after normalization
    URI traversal1 = URI.create("file:///data/../etc/passwd");
    URI traversal2 = URI.create("file:///app/config/../../etc/shadow");

    // Note: URI normalization happens before denylist check
    // The denylist checks the normalized path
    assertEquals("/etc/passwd", traversal1.normalize().getPath());
    assertTrue(denylist.isDenied(traversal1.normalize()));
  }

  @Test
  void testNonFileSchemesNotChecked() {
    URI httpUri = URI.create("http://example.com/etc/passwd");
    assertFalse(denylist.isDenied(httpUri));
  }
}
```

**Step 2: Run test to verify it fails**

```bash
mvn -pl core test -Dtest=FileSystemDenylistTest -DfailIfNoTests=false
```

Expected: Compilation error

**Step 3: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver.denylist;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Built-in denylist for sensitive file system paths.
 * <p>
 * This includes:
 * <ul>
 *   <li>System configuration directories (/etc, C:\Windows)</li>
 *   <li>Process and kernel interfaces (/proc, /sys)</li>
 *   <li>User credential directories (.ssh, .aws, .gnupg)</li>
 *   <li>Temporary and runtime directories</li>
 * </ul>
 * <p>
 * This denylist cannot be disabled and is always enforced.
 */
public final class FileSystemDenylist {

  private static final FileSystemDenylist INSTANCE = new FileSystemDenylist();
  private static final boolean IS_WINDOWS = System.getProperty("os.name", "")
      .toLowerCase(Locale.ROOT).contains("windows");

  /** Unix paths that are always blocked. */
  private static final List<String> BLOCKED_UNIX_PREFIXES = List.of(
      "/etc/",
      "/proc/",
      "/sys/",
      "/dev/",
      "/root/",
      "/var/run/",
      "/run/"
  );

  /** Unix path patterns (checked as substrings). */
  private static final List<String> BLOCKED_UNIX_PATTERNS = List.of(
      "/.ssh/",
      "/.gnupg/",
      "/.aws/",
      "/.azure/",
      "/.config/gcloud/"
  );

  /** Windows paths that are always blocked (lowercase for comparison). */
  private static final List<String> BLOCKED_WINDOWS_PREFIXES = List.of(
      "/c:/windows/",
      "/c:/programdata/",
      "/c:/$recycle.bin/"
  );

  /** Windows patterns (checked as substrings, lowercase). */
  private static final List<String> BLOCKED_WINDOWS_PATTERNS = List.of(
      "/appdata/",
      "/.ssh/",
      "/.aws/",
      "/.azure/"
  );

  private FileSystemDenylist() {
    // singleton
  }

  /**
   * Returns the singleton instance.
   *
   * @return the file system denylist instance
   */
  @NonNull
  public static FileSystemDenylist getInstance() {
    return INSTANCE;
  }

  /**
   * Checks if the given URI's path is on the denylist.
   *
   * @param uri
   *          the URI to check (should be file:// scheme)
   * @return {@code true} if the path is denied
   */
  public boolean isDenied(@NonNull URI uri) {
    return getDenialReason(uri) != null;
  }

  /**
   * Returns the reason why the URI's path is denied, or null if allowed.
   *
   * @param uri
   *          the URI to check
   * @return denial reason or null
   */
  @Nullable
  public String getDenialReason(@NonNull URI uri) {
    String scheme = uri.getScheme();
    if (scheme == null || !"file".equalsIgnoreCase(scheme)) {
      return null; // Not a file URI
    }

    String path = uri.getPath();
    if (path == null) {
      return null;
    }

    // Normalize the path for comparison
    String normalizedPath = path.toLowerCase(Locale.ROOT).replace('\\', '/');

    // Check OS-specific rules
    if (IS_WINDOWS) {
      return checkWindowsPath(normalizedPath, path);
    }
    return checkUnixPath(normalizedPath, path);
  }

  @Nullable
  private String checkUnixPath(String normalizedPath, String originalPath) {
    for (String prefix : BLOCKED_UNIX_PREFIXES) {
      if (normalizedPath.startsWith(prefix) || normalizedPath.equals(prefix.substring(0, prefix.length() - 1))) {
        return "Built-in denylist: sensitive system path '" + originalPath + "'";
      }
    }

    for (String pattern : BLOCKED_UNIX_PATTERNS) {
      if (normalizedPath.contains(pattern)) {
        return "Built-in denylist: credential directory '" + originalPath + "'";
      }
    }

    return null;
  }

  @Nullable
  private String checkWindowsPath(String normalizedPath, String originalPath) {
    for (String prefix : BLOCKED_WINDOWS_PREFIXES) {
      if (normalizedPath.startsWith(prefix)) {
        return "Built-in denylist: sensitive system path '" + originalPath + "'";
      }
    }

    for (String pattern : BLOCKED_WINDOWS_PATTERNS) {
      if (normalizedPath.contains(pattern)) {
        return "Built-in denylist: sensitive directory '" + originalPath + "'";
      }
    }

    // Also check Unix patterns on Windows (for consistency)
    for (String pattern : BLOCKED_UNIX_PATTERNS) {
      if (normalizedPath.contains(pattern)) {
        return "Built-in denylist: credential directory '" + originalPath + "'";
      }
    }

    return null;
  }
}
```

**Step 4: Run test to verify it passes**

```bash
mvn -pl core test -Dtest=FileSystemDenylistTest
```

Expected: PASS

**Step 5: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/FileSystemDenylist.java
git add core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/FileSystemDenylistTest.java
git commit -m "feat(resolver): add FileSystemDenylist for blocking sensitive paths"
```

---

### Task 2.3: Create BuiltInDenylist Facade

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/BuiltInDenylist.java`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/BuiltInDenylistTest.java`

**Step 1: Write the failing test**

```java
package gov.nist.secauto.metaschema.core.model.resolver.denylist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.net.URI;

class BuiltInDenylistTest {

  private final BuiltInDenylist denylist = BuiltInDenylist.getInstance();

  @Test
  void testCombinesNetworkAndFileSystemDenylists() {
    // Network denial
    URI localhost = URI.create("http://localhost/admin");
    assertTrue(denylist.isDenied(localhost));
    assertNotNull(denylist.getDenialReason(localhost));

    // File system denial (platform-dependent path)
    URI etcPasswd = URI.create("file:///etc/passwd");
    // This test only validates on Unix-like systems
    if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
      assertTrue(denylist.isDenied(etcPasswd));
    }
  }

  @Test
  void testAllowedUri() {
    URI allowed = URI.create("https://pages.nist.gov/schema.xml");
    assertFalse(denylist.isDenied(allowed));
    assertNull(denylist.getDenialReason(allowed));
  }

  @Test
  void testAsRule() {
    var rule = denylist.asRule();

    URI blocked = URI.create("http://127.0.0.1/secret");
    assertEquals(
        gov.nist.secauto.metaschema.core.model.resolver.IUriAccessRule.RuleResult.DENY,
        rule.evaluate(blocked));

    URI allowed = URI.create("https://example.com/api");
    assertEquals(
        gov.nist.secauto.metaschema.core.model.resolver.IUriAccessRule.RuleResult.NO_MATCH,
        rule.evaluate(allowed));
  }
}
```

**Step 2: Run test to verify it fails**

```bash
mvn -pl core test -Dtest=BuiltInDenylistTest -DfailIfNoTests=false
```

**Step 3: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.resolver.denylist;

import gov.nist.secauto.metaschema.core.model.resolver.IUriAccessRule;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Combined built-in denylist that aggregates all security denylists.
 * <p>
 * This facade provides a single entry point for checking URIs against all
 * built-in security rules. These rules cannot be disabled and are always
 * enforced before user-defined allowlists.
 */
public final class BuiltInDenylist {

  private static final BuiltInDenylist INSTANCE = new BuiltInDenylist();

  private final NetworkDenylist networkDenylist;
  private final FileSystemDenylist fileSystemDenylist;

  private BuiltInDenylist() {
    this.networkDenylist = NetworkDenylist.getInstance();
    this.fileSystemDenylist = FileSystemDenylist.getInstance();
  }

  /**
   * Returns the singleton instance.
   *
   * @return the built-in denylist instance
   */
  @NonNull
  public static BuiltInDenylist getInstance() {
    return INSTANCE;
  }

  /**
   * Checks if the given URI is denied by any built-in denylist.
   *
   * @param uri
   *          the URI to check
   * @return {@code true} if the URI is denied
   */
  public boolean isDenied(@NonNull URI uri) {
    return getDenialReason(uri) != null;
  }

  /**
   * Returns the reason why the URI is denied, or null if allowed.
   *
   * @param uri
   *          the URI to check
   * @return denial reason or null
   */
  @Nullable
  public String getDenialReason(@NonNull URI uri) {
    // Check network denylist first (for http/https URIs)
    String reason = networkDenylist.getDenialReason(uri);
    if (reason != null) {
      return reason;
    }

    // Check file system denylist (for file:// URIs)
    return fileSystemDenylist.getDenialReason(uri);
  }

  /**
   * Returns this denylist as an {@link IUriAccessRule}.
   * <p>
   * The returned rule returns {@link IUriAccessRule.RuleResult#DENY} for
   * blocked URIs and {@link IUriAccessRule.RuleResult#NO_MATCH} for others
   * (allowing subsequent rules to decide).
   *
   * @return this denylist as a rule
   */
  @NonNull
  public IUriAccessRule asRule() {
    return new IUriAccessRule() {
      @Override
      @NonNull
      public RuleResult evaluate(@NonNull URI uri) {
        return isDenied(uri) ? RuleResult.DENY : RuleResult.NO_MATCH;
      }

      @Override
      @NonNull
      public String getDescription() {
        return "Built-in security denylist";
      }
    };
  }
}
```

**Step 4: Run test to verify it passes**

```bash
mvn -pl core test -Dtest=BuiltInDenylistTest
```

**Step 5: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/BuiltInDenylist.java
git add core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/BuiltInDenylistTest.java
git commit -m "feat(resolver): add BuiltInDenylist facade combining all security denylists"
```

---

### Task 2.4: Add package-info.java

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/package-info.java`
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/package-info.java`

**Step 1: Write implementations**

```java
// package-info.java for denylist package
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Built-in security denylists that are always enforced.
 * <p>
 * These denylists protect against common attack vectors such as:
 * <ul>
 *   <li>Server-side request forgery (SSRF) to internal services</li>
 *   <li>Local file inclusion attacks</li>
 *   <li>Access to cloud metadata endpoints</li>
 *   <li>Exposure of credential files</li>
 * </ul>
 * <p>
 * The denylists in this package cannot be disabled by user configuration.
 */
@DefaultAnnotationForParameters(NonNull.class)
@DefaultAnnotationForFields(NonNull.class)
package gov.nist.secauto.metaschema.core.model.resolver.denylist;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForFields;
import edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters;
import edu.umd.cs.findbugs.annotations.NonNull;
```

```java
// package-info.java for resolver package
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Allowlist-based URI resolver for secure resource access.
 * <p>
 * This package provides a security layer that validates URIs before allowing
 * resource access. It supports:
 * <ul>
 *   <li>Scheme-based policies (allow/deny by URI scheme)</li>
 *   <li>File system access rules (directory boundaries)</li>
 *   <li>HTTP access rules (domain and path restrictions)</li>
 *   <li>Built-in denylists for common attack vectors</li>
 * </ul>
 *
 * @see gov.nist.secauto.metaschema.core.model.resolver.IAllowlistUriResolver
 * @see gov.nist.secauto.metaschema.core.model.resolver.AllowlistUriResolver
 */
@DefaultAnnotationForParameters(NonNull.class)
@DefaultAnnotationForFields(NonNull.class)
package gov.nist.secauto.metaschema.core.model.resolver;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForFields;
import edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters;
import edu.umd.cs.findbugs.annotations.NonNull;
```

**Step 2: Commit**

```bash
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/package-info.java
git add core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/denylist/package-info.java
git commit -m "docs(resolver): add package-info.java for resolver packages"
```

---

### Task 2.5: Verify PR2 Build

```bash
mvn -pl core clean install
mvn -pl core checkstyle:check
```

---

## PR3: Rule Implementations

**Goal:** Implement file system, HTTP, and JAR resource rules.

*(Tasks 3.1-3.4 follow same TDD pattern - FileSystemRule, HttpRule, JarRule, and tests)*

---

## PR4: Main Resolver and Builder

**Goal:** Implement AllowlistUriResolver and its builder.

*(Tasks 4.1-4.3 follow same TDD pattern)*

---

## PR5: Configuration System Infrastructure

**Goal:** Implement layered configuration system with directory discovery and merge support.

**Package:** `gov.nist.secauto.metaschema.cli.processor.config`

### Task 5.1: Create IConfigurationService Interface

**Files:**
- Create: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/config/IConfigurationService.java`

**Implementation:**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Service for discovering and loading layered configuration files.
 * <p>
 * Configurations are loaded from multiple locations in precedence order:
 * <ol>
 *   <li>Install directory ({@code <install-dir>/config/})</li>
 *   <li>System-wide ({@code /etc/metaschema-cli/} or {@code %ProgramData%\metaschema-cli\})</li>
 *   <li>User home ({@code ~/.metaschema-cli/})</li>
 *   <li>Project local ({@code ./.metaschema/})</li>
 *   <li>CLI argument or environment variable override</li>
 * </ol>
 * <p>
 * Configurations from all sources are merged according to type-specific rules.
 */
public interface IConfigurationService {

  /**
   * Get the merged configuration for a specific config file.
   *
   * @param configName
   *          the config file name (e.g., "allowlist.yaml")
   * @return the merged configuration as a map, or empty if no configs found
   */
  @NonNull
  Optional<Map<String, Object>> getConfiguration(@NonNull String configName);

  /**
   * Get all discovered config directory paths in precedence order.
   *
   * @return list of paths (lowest to highest precedence)
   */
  @NonNull
  List<Path> getConfigDirectories();

  /**
   * Reload all configurations from disk.
   */
  void reload();

  /**
   * Set an explicit configuration directory override.
   * <p>
   * When set, this takes highest precedence over all other sources.
   *
   * @param path
   *          the override directory path, or null to clear
   */
  void setOverrideDirectory(Path path);
}
```

**Step 2: Commit**

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/config/IConfigurationService.java
git commit -m "feat(config): add IConfigurationService interface for layered configuration"
```

---

### Task 5.2: Create ConfigurationService Implementation

**Files:**
- Create: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/config/ConfigurationService.java`
- Test: `cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/config/ConfigurationServiceTest.java`

**Step 1: Write the failing test**

```java
package gov.nist.secauto.metaschema.cli.processor.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ConfigurationServiceTest {

  @TempDir
  Path tempDir;

  private Path installConfig;
  private Path userConfig;
  private Path projectConfig;

  @BeforeEach
  void setUp() throws IOException {
    installConfig = tempDir.resolve("install/config");
    userConfig = tempDir.resolve("user/.metaschema-cli");
    projectConfig = tempDir.resolve("project/.metaschema");

    Files.createDirectories(installConfig);
    Files.createDirectories(userConfig);
    Files.createDirectories(projectConfig);
  }

  @Test
  void testConfigDirectoryDiscovery() {
    ConfigurationService service = ConfigurationService.builder()
        .withInstallDirectory(installConfig)
        .withUserDirectory(userConfig)
        .withProjectDirectory(projectConfig)
        .build();

    List<Path> dirs = service.getConfigDirectories();
    assertEquals(3, dirs.size());
    assertEquals(installConfig, dirs.get(0)); // Lowest precedence
    assertEquals(projectConfig, dirs.get(2)); // Highest precedence
  }

  @Test
  void testConfigurationLoading() throws IOException {
    // Create config file in install directory
    Files.writeString(installConfig.resolve("test.yaml"),
        "key1: value1\nkey2: value2");

    ConfigurationService service = ConfigurationService.builder()
        .withInstallDirectory(installConfig)
        .build();

    Optional<Map<String, Object>> config = service.getConfiguration("test.yaml");
    assertTrue(config.isPresent());
    assertEquals("value1", config.get().get("key1"));
    assertEquals("value2", config.get().get("key2"));
  }

  @Test
  void testOverrideDirectoryTakesPrecedence() throws IOException {
    Path overrideDir = tempDir.resolve("override");
    Files.createDirectories(overrideDir);

    Files.writeString(installConfig.resolve("test.yaml"), "source: install");
    Files.writeString(overrideDir.resolve("test.yaml"), "source: override");

    ConfigurationService service = ConfigurationService.builder()
        .withInstallDirectory(installConfig)
        .build();

    service.setOverrideDirectory(overrideDir);

    Optional<Map<String, Object>> config = service.getConfiguration("test.yaml");
    assertTrue(config.isPresent());
    assertEquals("override", config.get().get("source"));
  }

  @Test
  void testMissingConfigReturnsEmpty() {
    ConfigurationService service = ConfigurationService.builder()
        .withInstallDirectory(installConfig)
        .build();

    Optional<Map<String, Object>> config = service.getConfiguration("nonexistent.yaml");
    assertTrue(config.isEmpty());
  }
}
```

**Step 2: Run test to verify it fails**

```bash
mvn -pl cli-processor test -Dtest=ConfigurationServiceTest -DfailIfNoTests=false
```

**Step 3: Write implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.config;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Default implementation of {@link IConfigurationService}.
 * <p>
 * Discovers and loads configuration files from multiple directories,
 * merging them according to precedence rules.
 */
public final class ConfigurationService implements IConfigurationService {

  private static final boolean IS_WINDOWS = System.getProperty("os.name", "")
      .toLowerCase(Locale.ROOT).contains("windows");

  @NonNull
  private final List<Path> configDirectories;
  @NonNull
  private final Map<String, Map<String, Object>> configCache;
  @Nullable
  private Path overrideDirectory;

  private ConfigurationService(@NonNull List<Path> directories) {
    this.configDirectories = CollectionUtil.unmodifiableList(new ArrayList<>(directories));
    this.configCache = new ConcurrentHashMap<>();
  }

  /**
   * Creates a new builder for ConfigurationService.
   *
   * @return a new builder
   */
  @NonNull
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates a ConfigurationService with default directory detection.
   *
   * @return a new ConfigurationService with standard directories
   */
  @NonNull
  public static ConfigurationService createDefault() {
    return builder()
        .withDefaultDirectories()
        .build();
  }

  @Override
  @NonNull
  public Optional<Map<String, Object>> getConfiguration(@NonNull String configName) {
    // Check cache first
    if (configCache.containsKey(configName)) {
      Map<String, Object> cached = configCache.get(configName);
      return cached.isEmpty() ? Optional.empty() : Optional.of(cached);
    }

    // Load and merge from all sources
    Map<String, Object> merged = loadAndMerge(configName);
    configCache.put(configName, merged);

    return merged.isEmpty() ? Optional.empty() : Optional.of(merged);
  }

  @NonNull
  private Map<String, Object> loadAndMerge(@NonNull String configName) {
    Map<String, Object> result = new LinkedHashMap<>();
    Yaml yaml = new Yaml();

    // Load from directories in precedence order (lowest to highest)
    for (Path dir : getEffectiveDirectories()) {
      Path configFile = dir.resolve(configName);
      if (Files.exists(configFile) && Files.isRegularFile(configFile)) {
        try (InputStream is = Files.newInputStream(configFile)) {
          Map<String, Object> loaded = yaml.load(is);
          if (loaded != null) {
            // Simple shallow merge - higher precedence overwrites
            result.putAll(loaded);
          }
        } catch (IOException e) {
          // Log warning and continue
          // In production, use SLF4J logging
        }
      }
    }

    return result;
  }

  @NonNull
  private List<Path> getEffectiveDirectories() {
    if (overrideDirectory != null) {
      // Override takes highest precedence
      List<Path> dirs = new ArrayList<>(configDirectories);
      dirs.add(overrideDirectory);
      return dirs;
    }
    return configDirectories;
  }

  @Override
  @NonNull
  public List<Path> getConfigDirectories() {
    return configDirectories;
  }

  @Override
  public void reload() {
    configCache.clear();
  }

  @Override
  public void setOverrideDirectory(@Nullable Path path) {
    this.overrideDirectory = path;
    reload(); // Clear cache when override changes
  }

  /**
   * Builder for ConfigurationService.
   */
  public static final class Builder {
    private final List<Path> directories = new ArrayList<>();

    private Builder() {
    }

    /**
     * Add a configuration directory.
     *
     * @param dir the directory to add
     * @return this builder
     */
    @NonNull
    public Builder withDirectory(@NonNull Path dir) {
      if (Files.isDirectory(dir)) {
        directories.add(dir);
      }
      return this;
    }

    /**
     * Set the install directory.
     *
     * @param dir the install config directory
     * @return this builder
     */
    @NonNull
    public Builder withInstallDirectory(@NonNull Path dir) {
      return withDirectory(dir);
    }

    /**
     * Set the user directory.
     *
     * @param dir the user config directory
     * @return this builder
     */
    @NonNull
    public Builder withUserDirectory(@NonNull Path dir) {
      return withDirectory(dir);
    }

    /**
     * Set the project directory.
     *
     * @param dir the project config directory
     * @return this builder
     */
    @NonNull
    public Builder withProjectDirectory(@NonNull Path dir) {
      return withDirectory(dir);
    }

    /**
     * Configure with default directory detection.
     *
     * @return this builder
     */
    @NonNull
    public Builder withDefaultDirectories() {
      // 1. Install directory (detect from JAR location)
      detectInstallDirectory().ifPresent(this::withDirectory);

      // 2. System-wide
      Path systemDir = IS_WINDOWS
          ? Path.of(System.getenv("ProgramData"), "metaschema-cli")
          : Path.of("/etc/metaschema-cli");
      withDirectory(systemDir);

      // 3. User home
      String userHome = System.getProperty("user.home");
      if (userHome != null) {
        withDirectory(Path.of(userHome, ".metaschema-cli"));
      }

      // 4. Project local (current working directory)
      withDirectory(Path.of(".metaschema"));

      return this;
    }

    @NonNull
    private Optional<Path> detectInstallDirectory() {
      try {
        // Get the JAR location
        Path jarPath = Path.of(ConfigurationService.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .toURI());

        // Look for sibling config directory
        Path configDir = jarPath.getParent().getParent().resolve("config");
        if (Files.isDirectory(configDir)) {
          return Optional.of(configDir);
        }
      } catch (Exception e) {
        // Ignore - running from IDE or other non-standard location
      }
      return Optional.empty();
    }

    /**
     * Build the ConfigurationService.
     *
     * @return a new ConfigurationService
     */
    @NonNull
    public ConfigurationService build() {
      return new ConfigurationService(directories);
    }
  }
}
```

**Step 4: Run test to verify it passes**

```bash
mvn -pl cli-processor test -Dtest=ConfigurationServiceTest
```

**Step 5: Commit**

```bash
git add cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/config/ConfigurationService.java
git add cli-processor/src/test/java/gov/nist/secauto/metaschema/cli/processor/config/ConfigurationServiceTest.java
git commit -m "feat(config): add ConfigurationService with directory discovery"
```

---

### Task 5.3: Add package-info.java

**Files:**
- Create: `cli-processor/src/main/java/gov/nist/secauto/metaschema/cli/processor/config/package-info.java`

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Layered configuration system for CLI tools.
 * <p>
 * This package provides infrastructure for loading and merging configuration
 * files from multiple locations with precedence rules:
 * <ol>
 *   <li>Install directory - shipped defaults</li>
 *   <li>System-wide - administrator settings</li>
 *   <li>User home - user preferences</li>
 *   <li>Project local - project-specific overrides</li>
 *   <li>CLI/environment override - explicit override</li>
 * </ol>
 *
 * @see gov.nist.secauto.metaschema.cli.processor.config.IConfigurationService
 */
@DefaultAnnotationForParameters(NonNull.class)
@DefaultAnnotationForFields(NonNull.class)
package gov.nist.secauto.metaschema.cli.processor.config;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForFields;
import edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters;
import edu.umd.cs.findbugs.annotations.NonNull;
```

---

### Task 5.4: Verify PR5 Build

```bash
mvn -pl cli-processor clean install
mvn -pl cli-processor checkstyle:check
```

---

## PR6: Allowlist YAML Configuration and Merge

**Goal:** Implement allowlist-specific YAML configuration with scheme-deep/domain-shallow merge semantics.

**Package:** `gov.nist.secauto.metaschema.core.model.resolver.config`

### Task 6.1: Create AllowlistConfigurationMerger

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/config/AllowlistConfigurationMerger.java`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/config/AllowlistConfigurationMergerTest.java`

**Step 1: Write the failing test**

```java
package gov.nist.secauto.metaschema.core.model.resolver.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class AllowlistConfigurationMergerTest {

  private final AllowlistConfigurationMerger merger = new AllowlistConfigurationMerger();

  @Test
  void testDeepMergeOnScheme() {
    // Install config has https rules
    Map<String, Object> install = Map.of(
        "schemes", Map.of(
            "https", Map.of(
                "enabled", true,
                "rules", List.of(
                    Map.of("domain", "nist.gov", "paths", List.of("/schemas/"))))));

    // User config adds more https rules
    Map<String, Object> user = Map.of(
        "schemes", Map.of(
            "https", Map.of(
                "rules", List.of(
                    Map.of("domain", "github.com", "paths", List.of("/repos/"))))));

    Map<String, Object> merged = merger.merge(install, user);

    // Both domains should be present (deep merge on scheme)
    @SuppressWarnings("unchecked")
    Map<String, Object> schemes = (Map<String, Object>) merged.get("schemes");
    @SuppressWarnings("unchecked")
    Map<String, Object> https = (Map<String, Object>) schemes.get("https");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> rules = (List<Map<String, Object>>) https.get("rules");

    assertEquals(2, rules.size());
    assertTrue(rules.stream().anyMatch(r -> "nist.gov".equals(r.get("domain"))));
    assertTrue(rules.stream().anyMatch(r -> "github.com".equals(r.get("domain"))));
  }

  @Test
  void testShallowMergeOnDomain() {
    // Install config has nist.gov with /schemas/
    Map<String, Object> install = Map.of(
        "schemes", Map.of(
            "https", Map.of(
                "rules", List.of(
                    Map.of("domain", "nist.gov", "paths", List.of("/schemas/"))))));

    // User config redefines nist.gov with different paths
    Map<String, Object> user = Map.of(
        "schemes", Map.of(
            "https", Map.of(
                "rules", List.of(
                    Map.of("domain", "nist.gov", "paths", List.of("/docs/", "/api/"))))));

    Map<String, Object> merged = merger.merge(install, user);

    @SuppressWarnings("unchecked")
    Map<String, Object> schemes = (Map<String, Object>) merged.get("schemes");
    @SuppressWarnings("unchecked")
    Map<String, Object> https = (Map<String, Object>) schemes.get("https");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> rules = (List<Map<String, Object>>) https.get("rules");

    // Only one nist.gov entry (user's version replaces install's)
    List<Map<String, Object>> nistRules = rules.stream()
        .filter(r -> "nist.gov".equals(r.get("domain")))
        .toList();
    assertEquals(1, nistRules.size());

    // User's paths should be present, not install's
    @SuppressWarnings("unchecked")
    List<String> paths = (List<String>) nistRules.get(0).get("paths");
    assertTrue(paths.contains("/docs/"));
    assertTrue(paths.contains("/api/"));
    assertFalse(paths.contains("/schemas/"));
  }

  @Test
  void testEnabledFlagOverride() {
    Map<String, Object> install = Map.of(
        "schemes", Map.of(
            "file", Map.of("enabled", false)));

    Map<String, Object> user = Map.of(
        "schemes", Map.of(
            "file", Map.of("enabled", true)));

    Map<String, Object> merged = merger.merge(install, user);

    @SuppressWarnings("unchecked")
    Map<String, Object> schemes = (Map<String, Object>) merged.get("schemes");
    @SuppressWarnings("unchecked")
    Map<String, Object> file = (Map<String, Object>) schemes.get("file");

    // User's enabled=true should override install's enabled=false
    assertEquals(true, file.get("enabled"));
  }
}
```

*(Implementation follows TDD pattern)*

---

### Task 6.2: Create AllowlistConfiguration POJO

**Files:**
- Create: `core/src/main/java/gov/nist/secauto/metaschema/core/model/resolver/config/AllowlistConfiguration.java`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/resolver/config/AllowlistConfigurationTest.java`

*(Implementation loads YAML into typed configuration objects)*

---

### Task 6.3: Verify PR6 Build

```bash
mvn -pl core clean install
mvn -pl core checkstyle:check
```

---

## PR7: Loader Integration

**Goal:** Integrate allowlist resolver with all loading paths.

### Task 6.1: Update AbstractModuleLoader

**Files:**
- Modify: `core/src/main/java/gov/nist/secauto/metaschema/core/model/AbstractModuleLoader.java:88-92`
- Test: `core/src/test/java/gov/nist/secauto/metaschema/core/model/AbstractModuleLoaderTest.java`

**Current code (line ~90):**
```java
URI resolvedResource = ObjectUtils.notNull(resource.resolve(importedResource));
```

**Updated code:**
```java
URI resolvedResource = ObjectUtils.notNull(resource.resolve(importedResource));
// Apply URI resolver if configured
IUriResolver uriResolver = getUriResolver();
if (uriResolver != null) {
  resolvedResource = uriResolver.resolve(resolvedResource);
}
```

### Task 6.2: Update BindingConstraintLoader

**Files:**
- Modify: `databind/src/main/java/gov/nist/secauto/metaschema/databind/model/metaschema/BindingConstraintLoader.java:104,135`

**Similar pattern to AbstractModuleLoader**

### Task 6.3: Update DefaultXmlDeserializer

**Files:**
- Modify: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/xml/DefaultXmlDeserializer.java:103-118`

**Updated XMLResolver to use IUriResolver**

---

## PR7: Documentation

**Goal:** Update documentation and add examples.

### Task 7.1: Add Usage Documentation

**Files:**
- Create: `docs/allowlist-resolver.md`

### Task 7.2: Update README

**Files:**
- Modify: `README.md` - Add security section

---

## Completion Checklist

**Phase 1: Core Resolver**
- [ ] Core interfaces and exceptions
- [ ] SchemePolicy implementation
- [ ] Built-in denylist
- [ ] Rule implementations (FileSystem, Http, Jar)
- [ ] AllowlistUriResolver and builder

**Phase 2: Configuration System**
- [ ] IConfigurationService interface
- [ ] ConfigurationService with directory discovery
- [ ] AllowlistConfigurationMerger
- [ ] AllowlistConfiguration POJO

**Phase 3: Integration**
- [ ] Loader integration (AbstractModuleLoader, BindingConstraintLoader, DefaultXmlDeserializer)
- [ ] CLI integration (--config-dir, METASCHEMA_CONFIG_DIR)
- [ ] Documentation

**Final Verification:**
```bash
mvn clean install -PCI -Prelease
```
