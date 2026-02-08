# Resource Access Policy PRD

**Issue:** [#183 - Add new allowlist-only resolver for loading models, instances, and dynamic model generation](https://github.com/metaschema-framework/metaschema-java/issues/183)

**Goal:** Provide a policy-based URI resolver that controls resource access using glob patterns, with graduated enforcement modes (disabled, audit, enforce) for low-impact, backwards-compatible deployment.

**Architecture:** Implement a `ResourceAccessPolicy` in the `core` module using `.gitignore`-style glob patterns grouped by URI scheme. Integrate at loader level (`IModuleLoader`, `IBoundLoader`) with configurable enforcement modes. Default: restrictive rules in audit mode (log violations but allow all requests).

**Tech Stack:** Java 11, existing Metaschema core interfaces, Metaschema-based configuration model, SLF4J for audit logging.

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
| Local File Inclusion | `../../../etc/passwd` in imports | Path normalization + pattern-based access control |
| SSRF to Internal Services | `http://localhost:8080/admin` | Default-deny for `http` scheme patterns |
| Cloud Metadata Access | `http://169.254.169.254/` | Default-deny for private/link-local patterns |
| XXE Attacks | XML entity resolution to arbitrary URLs | Route entity resolution through policy |
| Scheme Injection | `file://`, `ftp://`, `gopher://` | Scheme-level allow/deny with glob patterns |

---

## Design Decisions

### 1. Glob Pattern Model (`.gitignore`-style)

Patterns use familiar `.gitignore` glob syntax with `!` negation:

- **Allow patterns** define what resources are accessible
- **`!` patterns** create exceptions (deny previously allowed resources)
- Patterns are evaluated **in order**, last match wins
- Patterns are organized **by scheme** (file, https, http, jar)

This replaces the previous allowlist+denylist dual model with a single, unified pattern list per scheme. Users familiar with `.gitignore` can immediately understand and author policies.

### 2. Enforcement Modes

Three graduated enforcement levels for safe rollout:

| Mode | Behavior | Use Case |
|------|----------|----------|
| `DISABLED` | No policy checking; all URIs allowed | Legacy behavior, maximum compatibility |
| `AUDIT` | Check policy, **log violations**, but **allow** all requests | Migration period, discovering needed rules |
| `ENFORCE` | Check policy, **block** violations with exception | Production hardened |

**Default mode:** `AUDIT` — provides visibility into what would be blocked without breaking existing workflows.

The mode is configurable via:
- **API:** `ResourceAccessPolicy.builder().mode(PolicyMode.AUDIT)`
- **Config file:** `mode: audit` in the policy configuration
- **CLI flag:** `--resource-policy-mode=enforce`

### 3. Scheme-Based Pattern Organization

Patterns are grouped by URI scheme for clarity and to avoid ambiguity:

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
        - "!**/.ssh/**"
        - "!**/.aws/**"
    - scheme: jar
      patterns:
        - "/schema/**"
        - "/META-INF/metaschema/**"
```

Within each scheme section:
- `enabled: false` disables the entire scheme (deny all)
- `enabled: true` (default) enables pattern matching
- If patterns are present, only matching URIs are allowed
- If no patterns are present and enabled is true, all URIs for that scheme are allowed
- `!` patterns create exceptions within the allowed set

### 4. File System Protections (Defense-in-Depth)

The `file` scheme ships with a **default allow-list of safe path patterns**, providing defense-in-depth against misconfiguration. File protections are checked **before** user-defined scheme patterns — a path must be allowed by file protections before scheme patterns are evaluated. This can be adjusted via the API.

**Model:** File protections use an allow-list approach. Only paths matching an allow pattern are permitted; everything else is denied. This is the inverse of the scheme-level glob patterns — protections define a **floor** of safe paths.

**Default allow patterns (shipped with the library):**

All platforms:
- `<cwd>/**` — current working directory subtree (resolved at policy creation time)
- `<user.home>/**` — user's home directory subtree
- `!<user.home>/.ssh/**` — except SSH keys
- `!<user.home>/.aws/**` — except AWS credentials
- `!<user.home>/.gnupg/**` — except GPG keys
- `!**/Library/Keychains/**` — except macOS keychains
- `!**/Library/Application Support/com.apple.TCC/**` — except macOS privacy DB
- `!**/AppData/**` — except Windows AppData

Note: `<cwd>` and `<user.home>` are resolved to absolute paths at policy creation time, not treated as literal patterns.

**What the defaults block (by omission):**

Since only the CWD and home directory subtrees are allowed, paths like these are denied automatically:
- `/etc/**`, `/proc/**`, `/sys/**`, `/dev/**` — system directories
- `/root/**` — root home (unless CWD is there)
- `C:/Windows/**` — Windows system directory
- Any path outside CWD and home

**Behavior by mode:**

| Mode | File protection behavior |
|------|--------------------------|
| `DISABLED` | Not checked (policy is fully off) |
| `AUDIT` | Checked, violations logged as WARN, request **allowed** |
| `ENFORCE` | Checked, violations **blocked** with `AccessViolationException` |

**API for adjusting the protection list:**

```java
// Default behavior: CWD subtree + home (minus sensitive dot-dirs)
// are allowed. Everything else denied.
ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .forScheme("file")
        .allow("/workspace/**")
    .build();
// file:///workspace/schema.xml → ALLOWED
// file:///etc/passwd → DENIED (not in file protections allow-list)

// Inspect the default allow patterns
List<String> defaults = FileProtections.defaultAllowPatterns();

// Customize: start from defaults and allow additional paths
ResourceAccessPolicy custom = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.builder()
        .includeDefaults()              // CWD + home minus sensitive dirs
        .allow("/opt/metaschema/**")    // add another safe area
        .allow("/data/schemas/**")      // add another safe area
        .build())
    .forScheme("file")
        .allow("/workspace/**")
    .build();

// Customize: start from defaults but narrow scope
ResourceAccessPolicy tighter = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.builder()
        .includeDefaults()
        .remove("<user.home>/**")       // remove home dir access
        .build())
    .forScheme("file")
        .allow("/workspace/**")
    .build();

// Completely replace: no defaults, fully custom safe list
ResourceAccessPolicy fullyCustom = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.builder()
        .allow("/opt/app/**")           // only this directory tree
        .build())                       // no defaults included
    .forScheme("file")
        .allow("/opt/app/schemas/**")
    .build();

// Disable file protections entirely (not recommended)
ResourceAccessPolicy noProtections = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .fileProtections(FileProtections.none())
    .forScheme("file")
        .allow("/workspace/**")
    .build();
```

**Evaluation order:** File protections are checked **before** user-defined scheme patterns. The flow for a `file:` URI is:
1. Check file protections allow-list (is the path in a safe area?)
2. If denied by file protections → apply mode behavior (log/block), stop
3. If allowed by file protections → check user's scheme patterns (last match wins)
4. If no scheme pattern matches → use `default-scheme-policy`

This means file protections act as a gate — a path must be in a safe area before user scheme patterns are even considered.

### 5. Default Bundled Policy

The library ships with a **restrictive default policy in audit mode**:

```yaml
resource-access-policy:
  mode: audit
  default-scheme-policy: deny
  schemes:
    - scheme: https
      patterns:
        - "**"
        - "!localhost/**"
        - "!127.*/**"
        - "!10.*/**"
        - "!172.16.*/**"
        - "!172.17.*/**"
        - "!172.18.*/**"
        - "!172.19.*/**"
        - "!172.2?.*/**"
        - "!172.30.*/**"
        - "!172.31.*/**"
        - "!192.168.*/**"
        - "!169.254.*/**"
        - "![::1]/**"
        - "!metadata.google.internal/**"
    - scheme: http
      enabled: false
    - scheme: file
      patterns:
        - "**"
        - "!/etc/**"
        - "!/proc/**"
        - "!/sys/**"
        - "!/dev/**"
        - "!/root/**"
        - "!**/.ssh/**"
        - "!**/.aws/**"
        - "!**/.gnupg/**"
        - "!/var/run/**"
        - "!C:/Windows/**"
        - "!**/AppData/**"
    - scheme: jar
      patterns:
        - "**"
```

In `AUDIT` mode (the default), this logs every URI access that would fail these rules but allows the request to proceed. Users can review logs to understand their access patterns before switching to `ENFORCE`.

### 6. Policy on Loader (Library API)

For library users, policy is set on the loader:

```java
// Create a policy
ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
    .mode(PolicyMode.AUDIT)
    .forScheme("https")
        .allow("pages.nist.gov/**")
        .allow("raw.githubusercontent.com/metaschema-framework/**")
    .forScheme("file")
        .allow("/workspace/schemas/**")
        .deny("**/.ssh/**")
    .forScheme("jar")
        .allowAll()
    .defaultDeny()
    .build();

// Set on loader
IModuleLoader loader = ...;
loader.setResourceAccessPolicy(policy);

// Or use bundled defaults
loader.setResourceAccessPolicy(ResourceAccessPolicy.bundledDefaults());

// Override mode
loader.setResourceAccessPolicy(
    ResourceAccessPolicy.bundledDefaults()
        .withMode(PolicyMode.ENFORCE));
```

This keeps the API on the loader itself, making it discoverable and natural for library users. The `IUriResolver` interface remains unchanged for backwards compatibility.

### 7. All in Core Module

The entire policy engine lives in `core`:
- Policy model, pattern matching, enforcement
- Metaschema configuration model and loading
- Default bundled policy

CLI-specific concerns (CLI flags, environment variables) are handled in `cli-processor`/`metaschema-cli` but delegate to the core API.

### 8. Integration Points

All resolution paths check the policy:

| Component | Current Behavior | Change Required |
|-----------|-----------------|-----------------|
| `DefaultBoundLoader` | Uses `IUriResolver` | Add policy check before resolution |
| `AbstractModuleLoader` | Raw `URI.resolve()` for imports | Add policy check |
| `BindingConstraintLoader` | Raw `URI.resolve()` for imports | Add policy check |
| `DefaultXmlDeserializer` | Custom `XMLResolver` for entities | Route through policy |
| `DefaultJsonDeserializer` | Reads from provided `Reader` | None - uses loader with policy |
| `DefaultYamlDeserializer` | Reads from provided `Reader` | None - uses loader with policy |

---

## Architecture

### Component Diagram

```text
┌─────────────────────────────────────────────────────────────────┐
│                    ResourceAccessPolicy                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌──────────────────────────────────────┐  │
│  │  PolicyMode     │  │      SchemePatterns                  │  │
│  │  ─────────────  │  │  ────────────────────────────────    │  │
│  │  DISABLED       │  │  file:                               │  │
│  │  AUDIT          │  │    allow: /workspace/**              │  │
│  │  ENFORCE        │  │    deny:  **/.ssh/**                 │  │
│  │                 │  │  https:                              │  │
│  │                 │  │    allow: pages.nist.gov/**          │  │
│  │                 │  │    deny:  localhost/**               │  │
│  │                 │  │  http:                               │  │
│  │                 │  │    (disabled)                        │  │
│  │                 │  │  jar:                                │  │
│  │                 │  │    allow: **                         │  │
│  └─────────────────┘  └──────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              Audit Logger (SLF4J)                         │  │
│  │  ───────────────────────────────────────────────────────  │  │
│  │  AUDIT mode:   log WARN for violations, allow request    │  │
│  │  ENFORCE mode: log ERROR for violations, throw exception │  │
│  │  All modes:    log DEBUG for allowed requests (optional)  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Class Hierarchy

```text
dev.metaschema.core.model.policy/
├── ResourceAccessPolicy.java          # Main policy implementation
├── ResourceAccessPolicyBuilder.java   # Fluent builder
├── PolicyMode.java                    # Enum: DISABLED, AUDIT, ENFORCE
├── AccessViolationException.java      # Exception for ENFORCE mode
├── IResourceAccessPolicy.java         # Interface for policy checking
├── SchemePatternSet.java              # Glob patterns for one scheme
├── GlobMatcher.java                   # .gitignore-style glob matching
├── FileProtections.java               # Adjustable file system deny patterns (ships with defaults)
└── package-info.java
```

### Integration Flow

```text
Loader receives URI
       │
       ▼
┌──────────────────────────────────┐
│  IResourceAccessPolicy.check()   │
│  ┌────────────────────────────┐  │
│  │ 1. If DISABLED → return    │  │
│  │ 2. Get scheme from URI     │  │
│  │ 3. If file: scheme, check  │  │
│  │    FileProtections first   │  │
│  │    (allow-list gate)       │  │
│  │ 4. Find SchemePatternSet   │  │
│  │ 5. Match against patterns  │  │
│  │    (last match wins)       │  │
│  │ 6. Apply mode behavior     │  │
│  └────────────────────────────┘  │
└──────────────────────────────────┘
       │
       ├── DISABLED → allow silently
       │
       ├── AUDIT + violation → log WARN, allow
       ├── AUDIT + allowed → (optionally log DEBUG), allow
       │
       ├── ENFORCE + violation → log ERROR, throw AccessViolationException
       └── ENFORCE + allowed → allow
```

---

## API Design

### Programmatic Configuration (Fluent API)

```java
// Restrictive server mode
ResourceAccessPolicy policy = ResourceAccessPolicy.builder()
    .mode(PolicyMode.ENFORCE)
    .forScheme("https")
        .allow("pages.nist.gov/**")
        .allow("raw.githubusercontent.com/metaschema-framework/**")
        .deny("*.internal/**")
    .forScheme("http")
        .denyAll()
    .forScheme("file")
        .allow("/data/schemas/**")
        .deny("**/.ssh/**")
        .deny("**/.aws/**")
    .forScheme("jar")
        .allowAll()
    .defaultDeny()  // deny unlisted schemes
    .build();

// Development mode - permissive with audit
ResourceAccessPolicy devPolicy = ResourceAccessPolicy.builder()
    .mode(PolicyMode.AUDIT)
    .forScheme("https").allowAll()
    .forScheme("http").allow("localhost/**")  // allow local dev servers
    .forScheme("file").allowAll()
    .forScheme("jar").allowAll()
    .defaultDeny()
    .build();

// Use bundled defaults
ResourceAccessPolicy defaults = ResourceAccessPolicy.bundledDefaults();

// Override mode on bundled defaults
ResourceAccessPolicy enforced = ResourceAccessPolicy.bundledDefaults()
    .withMode(PolicyMode.ENFORCE);

// Set on loader
IModuleLoader loader = ...;
loader.setResourceAccessPolicy(policy);
```

### Metaschema-Based Configuration Model

The policy configuration uses a Metaschema-defined model, enabling:
- **Type-safe configuration** via generated Java classes
- **Multi-format support** - XML, JSON, or YAML
- **Schema validation** - configs validated against the Metaschema model
- **Dogfooding** - using Metaschema for its own tooling

**Metaschema Module Definition** (`resource-access-policy_metaschema.yaml`):

```yaml
metaschema:
  schema-name: Resource Access Policy
  schema-version: 1.0.0
  short-name: resource-access-policy
  namespace: http://csrc.nist.gov/ns/metaschema/resource-access-policy/1.0
  json-base-uri: http://csrc.nist.gov/ns/metaschema/resource-access-policy/1.0

  definitions:
    - define-assembly:
        name: resource-access-policy
        formal-name: Resource Access Policy
        description: >-
          Policy controlling which URIs can be accessed during resource loading.
          Uses glob patterns grouped by URI scheme.
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
```

### Example Configuration Files

**YAML format** (`resource-access-policy.yaml`):

```yaml
resource-access-policy:
  mode: audit
  default-scheme-policy: deny
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
        - "/data/schemas/**"
        - "/workspace/**"
        - "!**/.ssh/**"
        - "!**/.aws/**"
    - scheme: jar
      patterns:
        - "**"
```

**JSON format** (`resource-access-policy.json`):

```json
{
  "resource-access-policy": {
    "mode": "audit",
    "default-scheme-policy": "deny",
    "schemes": [
      {
        "scheme": "https",
        "patterns": [
          "pages.nist.gov/**",
          "raw.githubusercontent.com/metaschema-framework/**"
        ]
      },
      {
        "scheme": "http",
        "enabled": false
      },
      {
        "scheme": "file",
        "patterns": [
          "/data/schemas/**",
          "!**/.ssh/**"
        ]
      }
    ]
  }
}
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

## Configuration Layering

Configurations are loaded from multiple locations, merged with higher-precedence layers overriding lower ones:

| Priority | Location | Platform | Purpose |
|----------|----------|----------|---------|
| 1 (lowest) | Bundled in JAR | All | Restrictive defaults in audit mode |
| 2 | `<install-dir>/config/` | All | Distribution-specific overrides |
| 3 | `/etc/metaschema/` | Unix | System-wide administrator settings |
| 3 | `%ProgramData%\metaschema\` | Windows | System-wide administrator settings |
| 4 | `~/.metaschema/` | All | User-specific preferences |
| 5 | `./.metaschema/` | All | Project-specific overrides |
| 6 (highest) | CLI `--resource-policy` | All | CLI argument override |

### Merge Semantics

- **Mode**: Higher-precedence layer's mode wins
- **Scheme configs**: Merged by scheme name; higher-precedence replaces entire scheme config
- **Default scheme policy**: Higher-precedence layer's value wins

---

## Glob Pattern Matching

### Syntax

Patterns follow `.gitignore` glob syntax applied to the scheme-specific part of URIs:

| Pattern | Matches | Example |
|---------|---------|---------|
| `**` | Everything | All URIs for the scheme |
| `*.nist.gov/**` | Subdomain wildcard | `pages.nist.gov/schemas/foo.xml` |
| `example.com/path/**` | Path prefix | `example.com/path/to/resource` |
| `/workspace/**` | Directory tree (file scheme) | `/workspace/project/schema.xml` |
| `/workspace/*` | Single level (file scheme) | `/workspace/schema.xml` but not `/workspace/sub/schema.xml` |
| `!pattern` | Deny (exception) | Negates a previous allow |

### Pattern Evaluation

For a given URI:
1. Extract the scheme
2. Find the matching `SchemePatternSet`
3. If scheme is `enabled: false`, result is **deny**
4. If no patterns defined and `enabled: true`, result is **allow**
5. Evaluate patterns in order; **last matching pattern wins**
6. If no pattern matches, use `default-scheme-policy` (default: deny)

### What Patterns Match Against

For each scheme, patterns match against the scheme-specific part:

| Scheme | Pattern matches against | Example URI → match target |
|--------|------------------------|---------------------------|
| `file` | Path component | `file:///workspace/foo.xml` → `/workspace/foo.xml` |
| `https` | Host + path | `https://nist.gov/schemas/x.xml` → `nist.gov/schemas/x.xml` |
| `http` | Host + path | `http://localhost:8080/api` → `localhost:8080/api` |
| `jar` | Path within JAR | `jar:file:///lib.jar!/schema/x.xsd` → `/schema/x.xsd` |

---

## Success Criteria

From Issue #183:
- [ ] All website and readme documentation affected by the changes have been updated
- [ ] A Pull Request is submitted that fully addresses the goals
- [ ] The CI-CD build process runs without any reported errors

### Additional Acceptance Criteria

**Functional:**
- [ ] Module loading checks policy for imports
- [ ] Document loading checks policy
- [ ] Constraint loading checks policy for imports
- [ ] XML entity resolution checks policy
- [ ] Glob pattern matching works correctly for all schemes
- [ ] `!` negation patterns create proper exceptions
- [ ] DISABLED mode allows all URIs without logging
- [ ] AUDIT mode logs violations but allows all URIs
- [ ] ENFORCE mode blocks violations with `AccessViolationException`
- [ ] Bundled defaults are restrictive (deny localhost, private IPs, sensitive paths)
- [ ] Configuration loading works from YAML, JSON, and XML
- [ ] Configuration layering merges correctly

**Security:**
- [ ] Path traversal attacks are caught (../../../etc/passwd)
- [ ] SSRF to localhost is caught in default policy
- [ ] SSRF to private IP ranges is caught in default policy
- [ ] Cloud metadata endpoints are caught in default policy
- [ ] Sensitive system paths are caught in default policy

**Backwards Compatibility:**
- [ ] Default mode (AUDIT) does not break any existing workflows
- [ ] Existing code without policy configuration works unchanged
- [ ] Library users can opt-in without changing their URI resolvers
- [ ] CLI users can override mode via flags

**Non-Functional:**
- [ ] Clear log messages identifying policy violations
- [ ] Minimal performance overhead for URI resolution
- [ ] 80%+ test coverage for policy code

---

## Testing Strategy

### Unit Tests

- `GlobMatcher` - Pattern matching for all glob syntax variants
- `SchemePatternSet` - Pattern evaluation with `!` negation and ordering
- `ResourceAccessPolicy` - Policy checking across modes
- `PolicyMode` - Mode behavior (disabled/audit/enforce)
- Metaschema configuration loading and validation

### Integration Tests

- Module loading with policy in each mode
- Document loading with policy
- Constraint loading with policy
- XML entity resolution with policy
- Configuration layering from multiple sources

### Security Tests

- Path traversal attack vectors
- SSRF attack vectors (localhost, private IPs, metadata endpoints)
- Scheme injection attacks
- Unicode/encoding bypass attempts
- Case sensitivity handling (Windows paths)
- `!` pattern bypass attempts

---

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking existing applications | High | AUDIT mode as default; DISABLED available |
| Performance overhead | Medium | Efficient glob matching; pattern compilation |
| Incomplete default policy | Medium | Community feedback during audit phase |
| Configuration complexity | Medium | `.gitignore`-style syntax is widely known |
| Platform-specific path issues | Medium | Test on Windows/Linux/Mac; normalize paths |

---

## Migration Path

### Phase 1: Deployment (AUDIT mode)

1. Deploy with default policy (restrictive rules, audit mode)
2. Monitor logs for `WARN` entries showing policy violations
3. Adjust policy patterns to match actual access needs
4. Share policy configs across team/org

### Phase 2: Enforcement

1. Once policy accurately reflects needed access, switch to `ENFORCE`
2. `ResourceAccessPolicy.bundledDefaults().withMode(PolicyMode.ENFORCE)`
3. Or in config: `mode: enforce`

### Phase 3: Customization

1. Create project-specific `.metaschema/resource-access-policy.yaml`
2. Override bundled defaults for organization-specific needs
3. Use per-loader policies for fine-grained control

---

## Out of Scope

- Authentication/authorization for HTTP resources (use existing HTTP client config)
- Rate limiting or request throttling
- Content inspection (only URI-based filtering)
- Certificate validation (use JVM truststore config)
- Real-time file watching for config changes (explicit reload only)
