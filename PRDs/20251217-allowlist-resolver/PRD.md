# Resource Access Policy PRD

**Issue:** [#183 - Add new allowlist-only resolver for loading models, instances, and dynamic model generation](https://github.com/metaschema-framework/metaschema-java/issues/183)

**Goal:** Provide a policy-based URI access control system using glob patterns with programmatic IP-based SSRF protection, graduated enforcement modes, mandatory URI normalization, and defense-in-depth file system protections.

**Architecture:** Implement a `ResourceAccessPolicy` in the `core` module combining glob pattern matching with IP-based network security. Integrate at loader level (`IModuleLoader`, `IBoundLoader`) with configurable enforcement modes. Default: DISABLED (fully backwards compatible); opt-in to AUDIT or ENFORCE.

**Tech Stack:** Java 11, existing Metaschema core interfaces, Metaschema-based configuration model, SLF4J for audit logging, IP address library for CIDR block matching.

---

## Problem Statement

As a developer of Metaschema-based tooling deploying services, I need a resolver subsystem that:
1. Restricts access to an allowlist of local filesystem directories
2. Restricts access to an allowlist of remote HTTP services
3. Prevents SSRF attacks to internal services (localhost, cloud metadata endpoints)
4. Prevents local file inclusion attacks (directory traversal, sensitive system files)
5. Can be deployed gradually (audit first, enforce later) without breaking existing workflows
6. Works identically in CLI and library-based deployments

### Security Threats Addressed

| Threat | Attack Vector | Mitigation |
|--------|--------------|------------|
| Local File Inclusion | `../../../etc/passwd` in imports | Mandatory path normalization + symlink resolution + pattern-based access control |
| URL Encoding Bypass | `file:///etc/p%61sswd` | Mandatory URI percent-decoding before matching |
| SSRF to Internal Services | `http://localhost:8080/admin` | IP-based SSRF checking via `NetworkSecurityChecker` |
| IP Encoding Bypass | `http://2130706433/`, `http://0x7f000001/` | Programmatic IP resolution, not string patterns |
| IPv6 SSRF | `http://[::ffff:127.0.0.1]/` | `InetAddress`-based classification of all IP forms |
| Cloud Metadata Access | `http://169.254.169.254/` | CIDR block checking for link-local ranges |
| HTTP Redirect Bypass | `302` redirect to `http://169.254.169.254/` | Re-check policy after every redirect |
| XXE Attacks | XML entity resolution to arbitrary URLs | Route entity resolution through policy |
| Scheme Injection | `file://`, `ftp://`, `gopher://` | Scheme-level allow/deny with glob patterns |
| JAR Scheme SSRF | `jar:http://evil.com/mal.jar!/path` | Recursive policy check on JAR inner URI |
| Symlink Traversal | Symlink from allowed → denied path | Symlink resolution before policy check (default) |
| Config Privilege Escalation | Project-local config weakens admin policy | Ratchet-based configuration layering |
| ReDoS via Patterns | Crafted glob patterns in config files | Non-backtracking regex, pattern complexity limits |

---

## Design Decisions

### 1. Glob Pattern Model

Patterns use glob syntax with `!` negation for deny rules:

- **Allow patterns** (no prefix) define what resources are accessible
- **Deny patterns** (`!` prefix) create exceptions that block previously allowed resources
- Patterns are evaluated **in order**, last match wins
- Patterns are organized **by URI scheme** (file, https, http, jar)

**Important — `!` means DENY:** Unlike `.gitignore` where `!` means "re-include" (stop ignoring), in this system `!` means "deny access." This is the opposite semantic. Documentation must make this explicit.

The pattern syntax itself (glob wildcards `*`, `**`, `?`) follows `.gitignore` conventions but the behavioral model is different. Documentation should describe this as "glob pattern matching with last-match-wins evaluation," not as ".gitignore-style."

**Directory equivalence rule:** A pattern ending in `/**` also matches the directory itself (without trailing slash or children). For example:
- `/workspace/**` matches `/workspace`, `/workspace/`, and `/workspace/project/schema.xml`
- `pages.nist.gov/**` matches `pages.nist.gov`, `pages.nist.gov/`, and `pages.nist.gov/schemas/foo.xml`

This prevents a common misconfiguration where allowing a directory subtree via `path/**` unexpectedly denies access to the directory path itself. The implementation compiles `path/**` as matching `path`, `path/`, and `path/<anything>`.

### 2. Enforcement Modes & Zero-Config Behavior

Three graduated enforcement levels for safe rollout:

| Mode | Behavior | Use Case |
|------|----------|----------|
| `DISABLED` | No policy checking; all URIs allowed | Default — legacy behavior, maximum compatibility |
| `AUDIT` | Check policy, **log violations**, but **allow** all requests | Migration period, discovering needed rules |
| `ENFORCE` | Check policy, **block** violations with exception | Production hardened |

**Zero-config default: `DISABLED`.** When a library user upgrades to a version containing this feature without changing any code, behavior is unchanged. No new log entries, no blocking. Security requires explicit opt-in. This avoids:
- Surprising existing users with new WARN log noise after an upgrade
- Triggering production monitoring alerts unexpectedly
- Breaking existing workflows

**Explicit opt-in is required** via one of:
- **API:** `loader.setResourceAccessPolicy(ResourceAccessPolicy.bundledDefaults())`
- **Config file:** Place a `resource-access-policy.yaml` in a search path
- **CLI flag:** `--resource-policy-mode=audit` or `--resource-policy-mode=enforce`

**Factory methods for common scenarios:**

```java
// Restrictive defaults in AUDIT mode (recommended starting point)
ResourceAccessPolicy.bundledDefaults()

// Permissive for local development (allows localhost, http)
ResourceAccessPolicy.development()

// Explicit no-op (same as not setting a policy)
ResourceAccessPolicy.disabled()
```

The mode is configurable via:
- **API:** `ResourceAccessPolicy.builder().mode(PolicyMode.AUDIT)`
- **Config file:** `mode: audit` in the policy configuration
- **CLI flag:** `--resource-policy-mode=enforce`

### 3. URI Security Processing (Mandatory)

All URIs undergo mandatory security processing **before** pattern matching. This is a non-negotiable requirement, not an implementation detail.

**Processing pipeline for every URI:**

```text
Raw URI
  │
  ├─ 1. Percent-decode URI components (exactly once)
  │     file:///workspace/p%61th → file:///workspace/path
  │
  ├─ 2. Normalize scheme to lowercase
  │     FILE:///path → file:///path
  │
  ├─ 3. For file: scheme:
  │     a. Resolve path via Path.of(path).normalize()
  │        /workspace/../etc/passwd → /etc/passwd
  │     b. Reject paths still containing ".." after normalization
  │     c. If symlink policy is FOLLOW (default):
  │        Resolve via Path.toRealPath() to canonical path
  │     d. Apply case folding per CaseSensitivity mode
  │
  ├─ 4. For http/https schemes:
  │     a. Normalize hostname to lowercase (RFC 3986)
  │     b. Strip default ports (80 for http, 443 for https)
  │     c. Pass to NetworkSecurityChecker for IP-based SSRF check
  │
  ├─ 5. For jar: scheme:
  │     a. Parse inner URI (before !) and recursively check policy
  │     b. Parse internal path (after !) for scheme pattern matching
  │
  └─ 6. For URIs without a scheme (relative URIs):
        Resolve to absolute URI before policy checking.
        Deny if resolution is not possible.
```

**Symlink resolution policy:**

| Mode | Behavior | Default |
|------|----------|---------|
| `FOLLOW` | Resolve symlinks via `Path.toRealPath()` before checking | Yes (default) |
| `NOFOLLOW` | Check the path as-is without symlink resolution | No |

Symlink resolution is enabled by default because a symlink from an allowed directory to a sensitive path is a common bypass vector. When `FOLLOW` is active, the **canonical (real) path** is checked against the policy, not the symlink path.

**Case sensitivity mode:**

| Mode | Behavior | Use Case |
|------|----------|----------|
| `SYSTEM_DEFAULT` | Auto-detect from OS: case-insensitive on Windows, case-sensitive elsewhere | Default |
| `CASE_SENSITIVE` | Always case-sensitive matching | Unix-only deployments |
| `CASE_INSENSITIVE` | Always case-insensitive matching | Windows, testing |

Case sensitivity applies to file path matching in both `FileProtections` and file scheme patterns. For network schemes, hostnames are always case-folded to lowercase per RFC 3986.

**Configurable via API:**

```java
ResourceAccessPolicy.builder()
    .symlinkPolicy(SymlinkPolicy.FOLLOW)        // default
    .caseSensitivity(CaseSensitivity.SYSTEM_DEFAULT)  // default
    .build();
```

### 4. Network Security (IP-Based SSRF Protection)

**Glob patterns alone cannot protect against SSRF** because IP addresses have multiple representations that bypass string matching:

| Representation | Example | Resolves To |
|---------------|---------|-------------|
| Standard | `127.0.0.1` | 127.0.0.1 |
| Decimal | `2130706433` | 127.0.0.1 |
| Hexadecimal | `0x7f000001` | 127.0.0.1 |
| Octal | `0177.0.0.1` | 127.0.0.1 |
| Shorthand | `127.1` | 127.0.0.1 |
| IPv4-mapped IPv6 | `::ffff:127.0.0.1` | 127.0.0.1 |
| IPv6 expanded | `0:0:0:0:0:0:0:1` | ::1 |

The system uses a `NetworkSecurityChecker` that programmatically resolves hostnames to IP addresses and checks them against CIDR blocks using an IP address library.

**Blocked CIDR ranges (checked programmatically, not via glob patterns):**

| CIDR Block | Description |
|-----------|-------------|
| `127.0.0.0/8` | IPv4 loopback |
| `::1/128` | IPv6 loopback |
| `10.0.0.0/8` | Private (Class A) |
| `172.16.0.0/12` | Private (Class B) |
| `192.168.0.0/16` | Private (Class C) |
| `169.254.0.0/16` | Link-local (includes cloud metadata 169.254.169.254) |
| `fe80::/10` | IPv6 link-local |
| `fc00::/7` | IPv6 unique local address (ULA) |
| `::ffff:0:0/96` | IPv4-mapped IPv6 (checked after mapping to IPv4) |
| `0.0.0.0/8` | Unspecified / "this" network |
| `100.64.0.0/10` | Shared address space (CGNAT) |

**Implementation:** Uses an IP address library (e.g., `com.github.seancfoley:ipaddress`) for CIDR matching. The `InetAddress` from `java.net` resolves all IP encoding variants. The CIDR library handles range comparisons.

**HTTP redirect re-checking:** After any HTTP redirect (3xx), the new URI must be re-checked against the policy before following the redirect. This prevents:
- Policy checks `https://allowed-host.com/` → allowed
- HTTP client follows 302 to `http://169.254.169.254/latest/meta-data/`
- Cloud metadata exfiltrated

Re-check is documented as a requirement for HTTP client integration. The policy engine provides the `checkAccess()` method; the loader must call it again after receiving a redirect.

### 5. Scheme-Based Pattern Organization

Patterns are grouped by URI scheme for clarity:

```yaml
resource-access-policy:
  mode: audit
  schemes:
    - scheme: https
      patterns:
        - "pages.nist.gov/**"
        - "raw.githubusercontent.com/metaschema-framework/**"
        - "!*.internal/**"
    - scheme: http
      enabled: false
    - scheme: file
      patterns:
        - "/workspace/**"
        - "/data/schemas/**"
    - scheme: jar
      patterns:
        - "**"
```

**Scheme semantics:**

| Configuration | Behavior |
|--------------|----------|
| `enabled: false` | Deny all URIs for this scheme |
| `enabled: true` + patterns present | Match against patterns (last match wins) |
| `enabled: true` + no patterns | Use `default-scheme-policy` (typically deny) |

**Important change:** `enabled: true` with no patterns uses `default-scheme-policy` (default: deny), NOT "allow all." This prevents a common misconfiguration where an empty scheme section silently allows everything.

**Port handling for host-based schemes (http, https):**

Ports are stripped before pattern matching. Default ports (80 for http, 443 for https) are always stripped. Non-default ports are also stripped so that patterns match against `host/path` only. Port restrictions can be added as a future enhancement if needed.

Example: `https://localhost:8443/api` → match target is `localhost/api`.

**Scheme name validation:**

Scheme names are validated against a known set at config load time: `http`, `https`, `file`, `jar`, `ftp`, `data`. Unrecognized scheme names generate a WARNING log. This catches typos like `htps` that would silently create dead config entries.

**Relative URI handling:**

URIs without a scheme must be resolved to absolute URIs before policy checking. If resolution is not possible, the URI is denied.

### 6. File System Protections (Defense-in-Depth)

The `file` scheme ships with a **default allow-list of safe path patterns**. File protections are checked **before** user-defined scheme patterns — a path must be allowed by file protections before scheme patterns are evaluated.

**Model:** Allow-list. Only paths matching an allow pattern are permitted. Everything else is denied.

**Default allow patterns (shipped with the library):**

All platforms:
- `<cwd>/**` — current working directory subtree
- `<user.home>/**` — user's home directory subtree
- `!<user.home>/.*/**` — except ALL dot-directories in home (blanket exclusion)
- `!**/Library/Keychains/**` — except macOS keychains
- `!**/Library/Application Support/com.apple.TCC/**` — except macOS privacy DB
- `!**/AppData/**` — except Windows AppData

Notes:
- `<cwd>` and `<user.home>` are resolved to absolute paths at policy creation time
- The blanket `!<user.home>/.*/**` pattern excludes all dot-directories: `.ssh`, `.aws`, `.gnupg`, `.kube`, `.docker`, `.azure`, `.netrc`, `.config`, `.local`, `.bash_history`, `.password-store`, `.vault-token`, etc. This is more secure than enumerating individual directories
- If CWD is `/` (root) or `C:\`, a WARNING is logged: "CWD is the filesystem root; FileProtections allow the entire filesystem"

**What the defaults block (by omission):**

| Blocked Path | Reason |
|-------------|--------|
| `/etc/**`, `/proc/**`, `/sys/**`, `/dev/**` | System directories (outside CWD/home) |
| `/root/**` | Root home (unless CWD is there) |
| `C:/Windows/**` | Windows system directory |
| `~/.ssh/**`, `~/.aws/**`, `~/.gnupg/**` | Sensitive dot-directories (blanket exclusion) |
| `~/.kube/**`, `~/.docker/**`, `~/.azure/**` | Cloud/container credentials |
| `~/.config/gcloud/**`, `~/.config/gh/**` | Service credentials |
| `~/.netrc`, `~/.npmrc`, `~/.pypirc` | Network tokens |
| `~/.*_history` | Shell history |

**Case sensitivity and symlink modes:**

FileProtections respect the policy-level `CaseSensitivity` and `SymlinkPolicy` settings. On Windows with `SYSTEM_DEFAULT`, paths are compared case-insensitively.

**Conflict detection at build time:**

When the builder constructs a policy, it checks for conflicts between scheme patterns and FileProtections. If a file scheme allow pattern (e.g., `/opt/data/**`) would be blocked by FileProtections (because `/opt/data/` is outside CWD and home), the builder throws `IllegalStateException`:

```text
Conflict: file scheme pattern '/opt/data/**' will never match because FileProtections
does not allow '/opt/data/'. Add it to FileProtections via:
  .fileProtections(FileProtections.builder().includeDefaults().allow("/opt/data/**").build())
```

**API:**

```java
// Default: CWD + home minus dot-dirs
ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .forScheme("file")
        .allow("/workspace/**")
    .build();

// Inspect defaults
List<String> defaults = FileProtections.defaultAllowPatterns();

// Customize: extend defaults
ResourceAccessPolicy custom = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.builder()
        .includeDefaults()
        .allow("/opt/metaschema/**")
        .build())
    .forScheme("file")
        .allow("/workspace/**")
    .build();

// Customize: narrow defaults
ResourceAccessPolicy tighter = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.builder()
        .includeDefaults()
        .remove("<user.home>/**")   // remove home dir access
        .build())
    .forScheme("file")
        .allow("/workspace/**")
    .build();

// Fully custom (no defaults)
ResourceAccessPolicy fullyCustom = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.builder()
        .allow("/opt/app/**")
        .build())
    .forScheme("file")
        .allow("/opt/app/schemas/**")
    .build();

// Disable file protections (NOT RECOMMENDED — security warning logged)
ResourceAccessPolicy noProtections = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.disabled())
    .forScheme("file")
        .allow("/workspace/**")
    .build();
```

`FileProtections.disabled()` (renamed from `none()`) disables all file system protections. When called:
- Logs a `WARN`: "FileProtections disabled — file scheme relies solely on scheme patterns for security"
- The method's Javadoc includes a security warning about the implications

**Evaluation order for `file:` URIs:**

```text
1. Apply URI security processing (normalize, decode, resolve symlinks, case-fold)
2. Check FileProtections allow-list (is path in a safe area?)
3. If denied by FileProtections → apply mode behavior (log/block), stop
4. If allowed by FileProtections → check scheme patterns (last match wins)
5. If no scheme pattern matches → use default-scheme-policy
```

### 7. JAR Scheme Recursive Checking

The `jar:` URI format is `jar:<inner-uri>!/<path-within-jar>`. Both components must be checked:

1. **Inner URI** (JAR location): Parsed and recursively checked against the policy using the inner URI's scheme. This prevents SSRF through `jar:http://evil.com/mal.jar!/schema.xsd`.
2. **Internal path** (after `!`): Checked against `jar` scheme patterns.

If the inner URI has no `!` separator, it is treated as a malformed JAR URI and denied.

### 8. Default Bundled Policy

The library ships with a **restrictive default policy in AUDIT mode**:

```yaml
resource-access-policy:
  mode: audit
  default-scheme-policy: deny
  schemes:
    - scheme: https
      patterns:
        - "**"
    - scheme: http
      enabled: false
    - scheme: file
      patterns:
        - "**"
    - scheme: jar
      patterns:
        - "**"
```

**Note:** Private IP blocking and cloud metadata protection are handled by the `NetworkSecurityChecker` (Design Decision 4), not by glob patterns. The glob patterns in the default policy focus on scheme-level allow/deny and path restrictions. This separation ensures that IP encoding bypasses cannot circumvent network security.

**When loaded via `ResourceAccessPolicy.bundledDefaults()`:**
- Mode is AUDIT (log violations, allow all requests)
- `NetworkSecurityChecker` is enabled with all default CIDR blocks
- `FileProtections` are enabled with default allow patterns
- HTTP scheme is disabled entirely
- HTTPS, file, and jar schemes allow all paths (network security and FileProtections provide the restrictions)

### 9. Library API (Policy on Loader)

Policy is set on the loader via a new method:

```java
IModuleLoader loader = ...;

// Recommended: use bundled defaults (AUDIT mode)
loader.setResourceAccessPolicy(ResourceAccessPolicy.bundledDefaults());

// Override mode
loader.setResourceAccessPolicy(
    ResourceAccessPolicy.bundledDefaults().withMode(PolicyMode.ENFORCE));

// Development mode (allows localhost, HTTP)
loader.setResourceAccessPolicy(ResourceAccessPolicy.development());

// Modify an existing policy
loader.setResourceAccessPolicy(
    ResourceAccessPolicy.bundledDefaults()
        .toBuilder()
        .forScheme("https")
            .allow("my-internal-host.com/**")
        .build());

// Custom policy
ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .forScheme("https")
        .allow("pages.nist.gov/**")
        .allow("raw.githubusercontent.com/metaschema-framework/**")
    .forScheme("file")
        .allow("/workspace/schemas/**")
    .forScheme("jar")
        .allowAll()
    .denyUnlistedSchemes()
    .build();
loader.setResourceAccessPolicy(policy);
```

**Key API points:**
- `IUriResolver` interface remains unchanged for backwards compatibility
- `setResourceAccessPolicy(null)` disables policy checking (equivalent to DISABLED)
- `ResourceAccessPolicy` is **immutable** (final class, all-final fields). `withMode()` and `toBuilder()` create new instances.
- Loaders should use `volatile` or `AtomicReference` for the policy field for thread safety

**`ResourceAccessPolicy.development()` factory:**

Returns a permissive policy for local development:

```java
// Equivalent to:
ResourceAccessPolicy.builder()
    .mode(PolicyMode.AUDIT)
    .forScheme("https").allowAll()
    .forScheme("http").allow("localhost/**")
    .forScheme("file").allowAll()
    .forScheme("jar").allowAll()
    .denyUnlistedSchemes()
    .networkSecurity(NetworkSecurityConfig.builder()
        .allowLoopback(true)   // allow localhost for dev
        .build())
    .build();
```

### 10. Diagnostic API & Error Messages

#### Diagnostic API

Users need a way to test policies without trial-and-error. The `explain()` method returns a structured `PolicyDecision` without throwing:

```java
PolicyDecision decision = policy.explain(URI.create("https://10.0.0.1/api"));
decision.isAllowed();          // false
decision.getLayer();           // "network-security"
decision.getDenialReason();    // "IP 10.0.0.1 is in private range 10.0.0.0/8"
decision.getRemediation();     // "Add to NetworkSecurityConfig: .allowCidr(\"10.0.0.1/32\")"
decision.getEvaluationTrace(); // ordered list of evaluation steps

// Human-readable summary of all rules
String summary = policy.describeEffectiveRules();
```

**`PolicyDecision` fields:**

| Field | Type | Description |
|-------|------|-------------|
| `allowed` | `boolean` | Whether the URI would be allowed |
| `layer` | `String` | Which layer denied/allowed: "disabled", "file-protections", "network-security", "scheme-patterns", "default-scheme-policy" |
| `denialReason` | `String` | Human-readable reason for denial (null if allowed) |
| `matchingPattern` | `String` | The specific pattern that matched (null if N/A) |
| `configSource` | `String` | Where the matching rule came from (e.g., "bundled defaults", file path) |
| `remediation` | `String` | What to add to allow this URI |
| `evaluationTrace` | `List<EvaluationStep>` | Ordered list of all evaluation steps |

#### Error Messages

Error messages must be actionable. Format for `AccessViolationException`:

```text
Resource access policy violation: 'file:///etc/passwd' was denied.
  Normalized URI: /etc/passwd
  Denied by: file-protections (path not in allowed areas: <cwd>, <user.home>)
  Source: bundled defaults
  To allow: FileProtections.builder().includeDefaults().allow("/etc/passwd").build()
  Or run with --resource-policy-mode=audit to log without blocking.
```

Format for AUDIT mode log messages:

```text
WARN [resource-access-policy] URI 'https://10.0.0.5/api/schema.json' would be denied
  in ENFORCE mode. Denied by: network-security (IP 10.0.0.5 in private range 10.0.0.0/8).
  To allow: add to NetworkSecurityConfig: .allowCidr("10.0.0.5/32")
```

**Logging conventions:**
- Logger name: `dev.metaschema.core.model.policy`
- All audit messages prefixed with `[resource-access-policy]` for grep-ability
- AUDIT violations: WARN level
- ENFORCE violations: ERROR level (before throwing)
- Allowed requests: DEBUG level (optional)
- Policy initialization: INFO level (which config files loaded)

### 11. Builder API Design

The builder uses nested builders for type-safe state transitions:

```text
ResourceAccessPolicy.builder()        → ResourceAccessPolicyBuilder
  .mode(PolicyMode)                   → self
  .symlinkPolicy(SymlinkPolicy)       → self
  .caseSensitivity(CaseSensitivity)   → self
  .fileProtections(FileProtections)   → self
  .networkSecurity(NetworkSecurityConfig) → self
  .forScheme("https")                 → SchemeConfigBuilder
    .allow("pattern")                 → self
    .deny("!pattern")                 → self
    .allowAll()                       → self
    .denyAll()                        → self
    .forScheme("file")               → new SchemeConfigBuilder (finalizes previous)
    .denyUnlistedSchemes()           → ResourceAccessPolicyBuilder (finalizes scheme)
    .build()                          → ResourceAccessPolicy
  .denyUnlistedSchemes()             → self  (renamed from defaultDeny())
  .build()                            → ResourceAccessPolicy
```

**Key behaviors:**
- Calling `.forScheme()` twice for the same scheme **appends** patterns (does not replace)
- Calling `.allow()` or `.deny()` without a preceding `.forScheme()` throws `IllegalStateException`
- `.build()` validates the policy and runs conflict detection
- The built `ResourceAccessPolicy` is **immutable** — all internal collections are unmodifiable copies

**`toBuilder()` method:**

Creates a new builder pre-populated from an existing policy:

```java
ResourceAccessPolicy modified = existingPolicy.toBuilder()
    .forScheme("https")
        .allow("additional-host.com/**")
    .build();
```

### 12. Configuration Layering & Ratcheting

Configurations loaded from multiple locations, merged with precedence:

| Priority | Location | Platform | Purpose |
|----------|----------|----------|---------|
| 1 (lowest) | Bundled in JAR | All | Restrictive defaults in audit mode |
| 2 | `<install-dir>/config/` | All | Distribution-specific overrides |
| 3 | `/etc/metaschema/` | Unix | System-wide administrator settings |
| 3 | `%ProgramData%\metaschema\` | Windows | System-wide administrator settings |
| 4 | `~/.metaschema/` | All | User-specific preferences |
| 5 | `./.metaschema/` | All | Project-specific overrides |
| 6 (highest) | CLI `--resource-policy` | All | CLI argument override |

#### Ratchet Principle (Security)

Higher-precedence configs **can only tighten policy, never loosen it:**

- **Mode ratcheting:** Restriction order: DISABLED < AUDIT < ENFORCE. A higher-precedence layer's mode must be >= the lower-precedence layer's mode. If a project-local config sets `mode: disabled` but the system config sets `mode: enforce`, the effective mode is `enforce`. A WARNING is logged when a layer attempts to weaken the mode.
- **`locked` flag:** Any config layer can mark settings as `locked: true`, preventing higher-precedence layers from changing them. Example: system admin sets `mode: enforce, locked: true` — project-level configs cannot change the mode.

```yaml
# System-level config (/etc/metaschema/resource-access-policy.yaml)
resource-access-policy:
  mode: enforce
  locked: true  # cannot be weakened by project-level configs
```

#### Merge Semantics

| Setting | Merge Behavior |
|---------|----------------|
| `mode` | Ratchet: most restrictive wins |
| `default-scheme-policy` | Higher-precedence wins (subject to ratchet) |
| Scheme configs (default) | Higher-precedence **replaces** entire scheme config |
| Scheme configs (`inherit: true`) | Higher-precedence **appends** patterns to lower-precedence |

**Additive merge via `inherit`:**

```yaml
# Project-level: add patterns to bundled defaults instead of replacing
resource-access-policy:
  schemes:
    - scheme: https
      inherit: true            # append to lower-layer patterns
      patterns:
        - "my-internal-host.com/**"  # additional allow
```

Without `inherit: true`, the project-level `https` section would replace the bundled defaults entirely, losing any deny patterns for private IPs (though those are now handled by `NetworkSecurityChecker`, this still matters for scheme-level patterns).

#### YAML Configuration Footgun

YAML's `!` character is the tag prefix. Unquoted deny patterns will cause silent parsing failures:

```yaml
# WRONG — YAML interprets ! as a tag
patterns:
  - !**/.ssh/**

# CORRECT — must be quoted
patterns:
  - "!**/.ssh/**"
```

Config loading should validate that pattern strings do not contain YAML artifacts and log a clear error if parsing produces unexpected types.

#### Pattern Complexity Limits

To prevent ReDoS attacks via crafted glob patterns in user-controlled config files:
- Maximum patterns per scheme: 100
- Maximum pattern length: 500 characters
- Glob-to-regex compilation uses possessive quantifiers or atomic groups to prevent catastrophic backtracking

### 13. All in Core Module

The entire policy engine lives in `core`:
- Policy model, pattern matching, enforcement
- URI normalization and security processing
- Network security checker
- Metaschema configuration model and loading
- Default bundled policy

CLI-specific concerns (CLI flags, environment variables) are handled in `cli-processor`/`metaschema-cli` but delegate to the core API.

### 14. Integration Points

All resolution paths check the policy:

| Component | Current Behavior | Change Required |
|-----------|-----------------|-----------------|
| `DefaultBoundLoader` | Uses `IUriResolver` | Add policy check before resolution |
| `AbstractModuleLoader` | Raw `URI.resolve()` for imports | Add policy check; re-check after redirect |
| `BindingConstraintLoader` | Raw `URI.resolve()` for imports | Add policy check |
| `DefaultXmlDeserializer` | Custom `XMLResolver` for entities | Route through policy; re-check after redirect |
| `DefaultJsonDeserializer` | Reads from provided `Reader` | None — uses loader with policy |
| `DefaultYamlDeserializer` | Reads from provided `Reader` | None — uses loader with policy |

**Relative URI resolution:** Components that resolve relative URIs (e.g., `../schemas/foo.xml`) must resolve them to absolute URIs before calling `checkAccess()`.

---

## Architecture

### Component Diagram

```text
┌──────────────────────────────────────────────────────────────────────┐
│                      ResourceAccessPolicy                            │
├──────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌────────────────────┐  ┌──────────────────────┐  │
│  │ PolicyMode  │  │  UriNormalizer     │  │ NetworkSecurityChkr  │  │
│  │ ─────────── │  │  ──────────────    │  │ ──────────────────── │  │
│  │ DISABLED    │  │  percent-decode    │  │ CIDR block matching  │  │
│  │ AUDIT       │  │  path normalize   │  │ IP resolution        │  │
│  │ ENFORCE     │  │  symlink resolve  │  │ loopback check       │  │
│  │             │  │  case folding     │  │ site-local check     │  │
│  └─────────────┘  └────────────────────┘  │ link-local check    │  │
│                                            └──────────────────────┘  │
│  ┌──────────────────────────┐  ┌──────────────────────────────────┐  │
│  │   FileProtections        │  │      SchemePatterns              │  │
│  │   ──────────────────     │  │  ────────────────────────────    │  │
│  │   allow: <cwd>/**       │  │  file:                           │  │
│  │   allow: <home>/**      │  │    allow: /workspace/**          │  │
│  │   deny:  <home>/.*/**   │  │  https:                          │  │
│  │   (checked FIRST)       │  │    allow: pages.nist.gov/**      │  │
│  │                          │  │  http:                           │  │
│  │                          │  │    (disabled)                    │  │
│  │                          │  │  jar:                            │  │
│  │                          │  │    allow: **                     │  │
│  └──────────────────────────┘  └──────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │              Audit Logger (SLF4J)                              │  │
│  │  Logger: dev.metaschema.core.model.policy                     │  │
│  │  Prefix: [resource-access-policy]                             │  │
│  │  AUDIT:   WARN for violations, allow request                  │  │
│  │  ENFORCE: ERROR for violations, throw AccessViolationException│  │
│  │  All:     DEBUG for allowed requests, INFO for policy init    │  │
│  └────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
```

### Class Hierarchy

```text
dev.metaschema.core.model.policy/
├── PolicyMode.java                    # Enum: DISABLED, AUDIT, ENFORCE
├── SymlinkPolicy.java                 # Enum: FOLLOW, NOFOLLOW
├── CaseSensitivity.java               # Enum: SYSTEM_DEFAULT, CASE_SENSITIVE, CASE_INSENSITIVE
├── AccessViolationException.java      # Exception for ENFORCE mode
├── IResourceAccessPolicy.java         # Interface for policy checking
├── ResourceAccessPolicy.java          # Main policy implementation (immutable)
├── ResourceAccessPolicyBuilder.java   # Fluent builder with nested SchemeConfigBuilder
├── SchemePatternSet.java              # Glob patterns for one scheme
├── GlobMatcher.java                   # Glob pattern → regex compilation
├── FileProtections.java               # Adjustable file system allow-list
├── UriNormalizer.java                 # URI security processing pipeline
├── NetworkSecurityChecker.java        # IP-based SSRF protection
├── NetworkSecurityConfig.java         # Configuration for network security
├── PolicyDecision.java                # Diagnostic result from explain()
├── EvaluationStep.java                # Single step in evaluation trace
└── package-info.java
```

### Integration Flow

```text
Loader receives URI
       │
       ▼
┌──────────────────────────────────────┐
│  1. If DISABLED → return immediately │
│                                      │
│  2. UriNormalizer.normalize(uri)     │
│     ├─ percent-decode                │
│     ├─ lowercase scheme              │
│     ├─ file: normalize path + symlinks│
│     └─ http/https: lowercase host    │
│                                      │
│  3. If http/https:                   │
│     NetworkSecurityChecker.check()   │
│     ├─ resolve hostname → InetAddress│
│     ├─ check against CIDR blocks     │
│     └─ if private/reserved → deny    │
│                                      │
│  4. If file:                         │
│     FileProtections.isAllowed(path)  │
│     └─ if not in safe area → deny    │
│                                      │
│  5. If jar:                          │
│     ├─ parse inner URI               │
│     ├─ recursively check inner URI   │
│     └─ check internal path patterns  │
│                                      │
│  6. SchemePatternSet.isAllowed()     │
│     └─ last match wins              │
│                                      │
│  7. If no match → default-scheme-    │
│     policy                           │
│                                      │
│  8. Apply mode behavior              │
│     ├─ AUDIT: log WARN, allow        │
│     └─ ENFORCE: log ERROR, throw     │
└──────────────────────────────────────┘
```

---

## API Design

### Programmatic Configuration (Fluent API)

```java
// Restrictive server mode
ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .symlinkPolicy(SymlinkPolicy.FOLLOW)
    .caseSensitivity(CaseSensitivity.SYSTEM_DEFAULT)
    .forScheme("https")
        .allow("pages.nist.gov/**")
        .allow("raw.githubusercontent.com/metaschema-framework/**")
        .deny("*.internal/**")
    .forScheme("http")
        .denyAll()
    .forScheme("file")
        .allow("/data/schemas/**")
    .forScheme("jar")
        .allowAll()
    .denyUnlistedSchemes()
    .build();

// Development mode (one-liner)
ResourceAccessPolicy devPolicy = ResourceAccessPolicy.development();

// Bundled defaults (one-liner)
ResourceAccessPolicy defaults = ResourceAccessPolicy.bundledDefaults();

// Modify existing policy
ResourceAccessPolicy modified = defaults.toBuilder()
    .mode(PolicyMode.ENFORCE)
    .forScheme("https")
        .allow("my-internal-host.com/**")
    .build();

// Override just the mode
ResourceAccessPolicy enforced = defaults.withMode(PolicyMode.ENFORCE);

// Diagnostic check (does NOT throw)
PolicyDecision decision = policy.explain(URI.create("https://10.0.0.1/api"));

// Effective rules summary
String rules = policy.describeEffectiveRules();
```

### Metaschema-Based Configuration Model

The policy configuration uses a Metaschema-defined model, enabling:
- **Type-safe configuration** via generated Java classes
- **Multi-format support** — XML, JSON, or YAML
- **Schema validation** — configs validated against the Metaschema model

**Metaschema Module Definition** (`resource-access-policy-config_metaschema.yaml`):

Note: The root assembly is named `resource-access-policy-config` (not `resource-access-policy`) to avoid naming collision with the hand-written `ResourceAccessPolicy` class.

```yaml
metaschema:
  schema-name: Resource Access Policy Configuration
  schema-version: 1.0.0
  short-name: resource-access-policy-config
  namespace: http://csrc.nist.gov/ns/metaschema/resource-access-policy/1.0
  json-base-uri: http://csrc.nist.gov/ns/metaschema/resource-access-policy/1.0

  definitions:
    - define-assembly:
        name: resource-access-policy-config
        formal-name: Resource Access Policy Configuration
        description: >-
          Configuration controlling which URIs can be accessed during resource
          loading. Uses glob patterns grouped by URI scheme with IP-based
          network security.
        root-name: resource-access-policy
        flags:
          - define-flag:
              name: mode
              as-type: token
              formal-name: Enforcement Mode
              description: >-
                How policy violations are handled.
              constraint:
                allowed-values:
                  - enum:
                      value: disabled
                      description: No policy checking
                  - enum:
                      value: audit
                      description: Log violations but allow requests
                  - enum:
                      value: enforce
                      description: Block violating requests
          - define-flag:
              name: default-scheme-policy
              as-type: token
              formal-name: Default Scheme Policy
              description: >-
                Policy for schemes not explicitly configured.
              constraint:
                allowed-values:
                  - enum:
                      value: allow
                      description: Allow unlisted schemes
                  - enum:
                      value: deny
                      description: Deny unlisted schemes
          - define-flag:
              name: locked
              as-type: boolean
              formal-name: Locked
              description: >-
                When true, higher-precedence configuration layers cannot
                weaken this policy (ratchet enforcement).
        model:
          - assembly:
              ref: scheme-config
              max-occurs: unbounded
              group-as:
                name: schemes
                in-json: ARRAY

    - define-assembly:
        name: scheme-config
        formal-name: Scheme Configuration
        description: >-
          Configuration for a specific URI scheme, containing glob patterns
          that control access.
        flags:
          - define-flag:
              name: scheme
              as-type: token
              required: yes
              formal-name: URI Scheme
              description: The URI scheme (e.g., https, http, file, jar).
          - define-flag:
              name: enabled
              as-type: boolean
              formal-name: Enabled
              description: >-
                Whether this scheme is enabled. When false, all URIs with this
                scheme are denied.
          - define-flag:
              name: inherit
              as-type: boolean
              formal-name: Inherit
              description: >-
                When true, patterns are appended to lower-precedence layer
                patterns instead of replacing them.
        model:
          - field:
              ref: pattern
              max-occurs: unbounded
              group-as:
                name: patterns
                in-json: ARRAY

    - define-field:
        name: pattern
        as-type: string
        formal-name: Access Pattern
        description: >-
          A glob pattern controlling access. Patterns without a prefix are
          allow patterns. Patterns starting with ! are deny patterns
          (exceptions). Patterns are evaluated in order; last match wins.
          IMPORTANT: In YAML, patterns starting with ! must be quoted.
```

### Example Configuration Files

**YAML format** (`resource-access-policy.yaml`):

```yaml
# Patterns starting with ! MUST be quoted in YAML
resource-access-policy:
  mode: audit
  default-scheme-policy: deny
  schemes:
    - scheme: https
      patterns:
        - "pages.nist.gov/**"
        - "raw.githubusercontent.com/metaschema-framework/**"
        - "!*.internal/**"    # quoted! YAML ! is a tag prefix
    - scheme: http
      enabled: false
    - scheme: file
      patterns:
        - "/data/schemas/**"
        - "/workspace/**"
    - scheme: jar
      patterns:
        - "**"
```

### Loading Configuration

```java
// Using databind to load configuration
IBindingContext bindingContext = IBindingContext.instance();
IBoundLoader loader = bindingContext.newBoundLoader();

// From file (auto-detects format)
ResourceAccessPolicyConfig config = loader.load(
    ResourceAccessPolicyConfig.class,
    Path.of("resource-access-policy.yaml"));

// Create policy from config
ResourceAccessPolicy policy = ResourceAccessPolicy.fromConfiguration(config);

// Set on module loader
IModuleLoader moduleLoader = ...;
moduleLoader.setResourceAccessPolicy(policy);
```

---

## Glob Pattern Matching

### Syntax

| Pattern | Matches | Example |
|---------|---------|---------|
| `**` | Everything (any characters including `/`) | All URIs for the scheme |
| `*` | Any characters except `/` (single segment) | One directory level |
| `?` | Single character except `/` | One character |
| `*.nist.gov/**` | Subdomain wildcard | `pages.nist.gov/schemas/foo.xml` |
| `/workspace/**` | Directory tree | `/workspace/project/schema.xml` |
| `/workspace/*` | Single level | `/workspace/schema.xml` (not deeper) |
| `!pattern` | **Deny** (block previously allowed) | Negates a previous allow |

### Pattern Evaluation

For a given URI:
1. Apply URI security processing (normalize, decode, resolve symlinks)
2. Extract the scheme (lowercased)
3. Find the matching `SchemePatternSet`
4. If scheme is `enabled: false`, result is **deny**
5. If no patterns defined and `enabled: true`, use `default-scheme-policy`
6. Evaluate patterns in order; **last matching pattern wins**
7. If no pattern matches, use `default-scheme-policy` (default: deny)

### What Patterns Match Against

After URI normalization, patterns match against the scheme-specific part:

| Scheme | Pattern matches against | Example URI → match target |
|--------|------------------------|---------------------------|
| `file` | Normalized path | `file:///workspace/foo.xml` → `/workspace/foo.xml` |
| `https` | Host + path (no port) | `https://nist.gov:443/x.xml` → `nist.gov/x.xml` |
| `http` | Host + path (no port) | `http://localhost:8080/api` → `localhost/api` |
| `jar` | Path within JAR | `jar:file:///lib.jar!/schema/x.xsd` → `/schema/x.xsd` |

### Important: `!` Means DENY

Unlike `.gitignore` where `!` means "re-include" (stop ignoring a file), in this system `!` means **deny access**. This is the opposite semantic:

| System | `!` Meaning | Example |
|--------|------------|---------|
| `.gitignore` | "Do NOT ignore this file" (re-include) | `!important.log` keeps the file tracked |
| Resource Access Policy | "DENY access to this resource" (block) | `"!**/.ssh/**"` blocks SSH key access |

---

## Success Criteria

From Issue #183:
- [ ] All website and readme documentation affected by the changes have been updated
- [ ] A Pull Request is submitted that fully addresses the goals
- [ ] The CI-CD build process runs without any reported errors

### Functional

- [ ] Module loading checks policy for imports
- [ ] Document loading checks policy
- [ ] Constraint loading checks policy for imports
- [ ] XML entity resolution checks policy
- [ ] Glob pattern matching works correctly for all schemes
- [ ] `!` deny patterns create proper exceptions
- [ ] DISABLED mode allows all URIs without logging
- [ ] AUDIT mode logs violations but allows all URIs
- [ ] ENFORCE mode blocks violations with `AccessViolationException`
- [ ] Bundled defaults are restrictive
- [ ] Configuration loading works from YAML, JSON, and XML
- [ ] Configuration layering merges correctly with ratcheting
- [ ] `development()` factory allows localhost and HTTP

### Security

- [ ] Path traversal attacks caught via mandatory normalization
- [ ] URL encoding bypasses caught via mandatory percent-decoding
- [ ] Symlinks resolved before policy check (default mode)
- [ ] SSRF to localhost caught via IP-based checking (all encodings)
- [ ] SSRF to private IP ranges caught via CIDR block matching
- [ ] Cloud metadata endpoints caught (169.254.169.254)
- [ ] IPv4-mapped IPv6 addresses caught
- [ ] JAR scheme inner URIs recursively checked
- [ ] HTTP redirect URIs re-checked against policy
- [ ] Config layering cannot weaken policy (ratchet)
- [ ] Sensitive system paths denied by FileProtections
- [ ] Case-insensitive matching works on Windows
- [ ] ReDoS prevented via non-backtracking regex

### Backwards Compatibility

- [ ] Zero-config default (DISABLED) does not break any existing workflows
- [ ] Existing code without policy configuration works unchanged
- [ ] Library users can opt-in without changing their URI resolvers
- [ ] CLI users can override mode via flags

### Non-Functional

- [ ] Actionable error messages with layer, source, and remediation
- [ ] `explain()` provides evaluation trace for debugging
- [ ] `describeEffectiveRules()` provides human-readable policy summary
- [ ] Clear log messages identifying policy violations
- [ ] Minimal performance overhead for URI resolution
- [ ] 80%+ test coverage for policy code

---

## Testing Strategy

### Unit Tests

- `GlobMatcher` — Pattern matching for all glob syntax variants, case sensitivity
- `SchemePatternSet` — Pattern evaluation with `!` negation, ordering, empty patterns
- `ResourceAccessPolicy` — Policy checking across modes, factory methods, toBuilder
- `PolicyMode` — Mode behavior (disabled/audit/enforce)
- `UriNormalizer` — Path normalization, percent-decoding, symlink resolution
- `NetworkSecurityChecker` — IP-based CIDR block matching (see below)
- `FileProtections` — Allow-list, defaults, builder, conflict detection, case sensitivity
- `PolicyDecision` — Diagnostic results from explain()
- Configuration loading and validation

### IP Range Boundary Tests

Explicit boundary value tests for every private CIDR block, using the IP library:

```java
@ParameterizedTest
@CsvSource({
    // 127.0.0.0/8 (loopback)
    "126.255.255.255, true",   // just below range — allowed
    "127.0.0.0, false",        // start of range — blocked
    "127.0.0.1, false",        // standard loopback — blocked
    "127.255.255.255, false",  // end of range — blocked
    "128.0.0.0, true",         // just above range — allowed

    // 10.0.0.0/8 (private Class A)
    "9.255.255.255, true",
    "10.0.0.0, false",
    "10.255.255.255, false",
    "11.0.0.0, true",

    // 172.16.0.0/12 (private Class B)
    "172.15.255.255, true",
    "172.16.0.0, false",
    "172.31.255.255, false",
    "172.32.0.0, true",

    // 192.168.0.0/16 (private Class C)
    "192.167.255.255, true",
    "192.168.0.0, false",
    "192.168.255.255, false",
    "192.169.0.0, true",

    // 169.254.0.0/16 (link-local, includes cloud metadata)
    "169.253.255.255, true",
    "169.254.0.0, false",
    "169.254.169.254, false",  // cloud metadata
    "169.254.255.255, false",
    "169.255.0.0, true",

    // 100.64.0.0/10 (CGNAT)
    "100.63.255.255, true",
    "100.64.0.0, false",
    "100.127.255.255, false",
    "100.128.0.0, true",

    // 0.0.0.0/8 (unspecified)
    "0.0.0.0, false",
    "0.255.255.255, false",
    "1.0.0.0, true",
})
void testIpv4CidrBoundaries(String ip, boolean allowed) { ... }

@ParameterizedTest
@CsvSource({
    "::1, false",                    // IPv6 loopback
    "::2, true",                     // not loopback
    "fe80::1, false",                // link-local
    "fe7f::1, true",                 // not link-local
    "fc00::1, false",                // ULA
    "fbff::1, true",                 // not ULA
    "::ffff:127.0.0.1, false",      // IPv4-mapped loopback
    "::ffff:8.8.8.8, true",         // IPv4-mapped public
})
void testIpv6CidrBoundaries(String ip, boolean allowed) { ... }

@ParameterizedTest
@CsvSource({
    "2130706433, false",     // decimal 127.0.0.1
    "0x7f000001, false",     // hex 127.0.0.1
    "0177.0.0.1, false",    // octal 127.0.0.1
    "127.1, false",          // shorthand 127.0.0.1
})
void testAlternateIpEncodings(String host, boolean allowed) { ... }
```

### Security Tests

- Path traversal: `../../../etc/passwd`, `..%2f..%2f`, double-encoding
- URL encoding bypass: `%61` for `a`, `%2f` for `/`
- Symlink traversal: symlink from allowed to denied directory
- IP encoding bypass: decimal, octal, hex, shorthand, IPv4-mapped IPv6
- DNS rebinding documentation (test that re-check API exists)
- HTTP redirect re-checking
- JAR inner URI SSRF: `jar:http://evil.com/mal.jar!/path`
- Case sensitivity: Windows paths, scheme names
- `!` pattern bypass attempts
- ReDoS resistance: patterns with deep nesting
- Config ratchet: lower-precedence config attempts to weaken

### Integration Tests

- Module loading with policy in each mode
- Document loading with policy
- Constraint loading with policy
- XML entity resolution with policy
- Configuration layering from multiple sources
- Ratchet enforcement across config layers

---

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking existing applications | High | DISABLED as zero-config default; explicit opt-in required |
| Performance overhead | Medium | Efficient glob matching; pattern compilation; IP address caching |
| Incomplete SSRF protection | High | IP-based checking via library, not just string patterns |
| Path traversal bypass | High | Mandatory normalization + symlink resolution before matching |
| Configuration complexity | Medium | Factory methods for common scenarios; diagnostic API |
| FileProtections confusion | Medium | Conflict detection at build time; actionable error messages |
| Platform-specific path issues | Medium | CaseSensitivity mode; test on Windows/Linux/Mac |
| ReDoS via crafted patterns | Medium | Non-backtracking regex; pattern complexity limits |
| Config privilege escalation | High | Ratchet principle; locked flag |

---

## Migration Path

### Phase 1: Opt-In (AUDIT mode)

1. Add `loader.setResourceAccessPolicy(ResourceAccessPolicy.bundledDefaults())` to your code
2. Deploy — AUDIT mode logs violations but allows all requests
3. Monitor logs for `[resource-access-policy]` WARN entries
4. Use `policy.explain(uri)` to understand specific decisions
5. Adjust policy patterns to match actual access needs
6. Run in AUDIT mode until no new warnings appear for at least 2 weeks

### Phase 2: Enforcement

1. Switch to ENFORCE: `ResourceAccessPolicy.bundledDefaults().withMode(PolicyMode.ENFORCE)`
2. Or in config: `mode: enforce`
3. Monitor for `AccessViolationException` in error tracking
4. Use `--resource-policy-mode=audit` as emergency rollback

### Phase 3: Customization

1. Create project-specific `.metaschema/resource-access-policy.yaml`
2. Override bundled defaults for organization-specific needs
3. Use per-loader policies for fine-grained control
4. Deploy organization-wide policies via `/etc/metaschema/`

---

## CLI Integration

### Global Flags

These flags apply to all commands that load resources (e.g., `validate`, `validate-content`, `convert`, `generate-schema`):

| Flag | Description |
|------|-------------|
| `--resource-policy-mode=<mode>` | Override enforcement mode (disabled/audit/enforce) |
| `--resource-policy=<path>` | Load custom policy configuration file |

### `resource-policy` Command

A new top-level command with subcommands for policy diagnostics. Follows the same parent/subcommand pattern as the existing `metapath` command (`AbstractParentCommand` with `AbstractTerminalCommand` subcommands).

| Subcommand | Description |
|------------|-------------|
| `resource-policy dump` | Print effective merged policy as YAML and exit |
| `resource-policy check <uri>` | Check a single URI against the policy and print evaluation trace |

**Usage examples:**

```bash
# Dump the effective policy (bundled defaults + any config files)
metaschema-cli resource-policy dump

# Dump with a custom config overlay
metaschema-cli resource-policy dump --resource-policy=my-policy.yaml

# Check whether a specific URI would be allowed
metaschema-cli resource-policy check https://example.com/schema.xsd

# Check with enforce mode override
metaschema-cli resource-policy check --resource-policy-mode=enforce file:///etc/passwd
```

### Environment Variables

| Variable | Description |
|----------|-------------|
| `METASCHEMA_RESOURCE_POLICY_MODE` | Override enforcement mode |

---

## Out of Scope

- Authentication/authorization for HTTP resources (use existing HTTP client config)
- Rate limiting or request throttling
- Content inspection (only URI-based filtering)
- Certificate validation (use JVM truststore config)
- Real-time file watching for config changes (explicit reload only)
- DNS rebinding protection at the HTTP client level (documented as integration requirement)
- Port-based restrictions (may be added in a future version)
