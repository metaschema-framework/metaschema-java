# Allowlist URI Resolver PRD

**Issue:** [#183 - Add new allowlist-only resolver for loading models, instances, and dynamic model generation](https://github.com/metaschema-framework/metaschema-java/issues/183)

**Goal:** Provide a secure-by-default URI resolver that restricts resource access to explicitly allowed directories, domains, and URI schemes - preventing local file inclusion, SSRF, and other resource access attacks.

**Architecture:** Implement `IAllowlistUriResolver` extending `IUriResolver` with hierarchical rules (scheme → file/HTTP-specific policies). Integrate at all resolution points: module loading, document loading, constraint loading, and XML entity resolution. Defense-in-depth via user-defined allowlist plus always-enforced built-in denylist.

**Tech Stack:** Java 11, existing Metaschema core interfaces, YAML (SnakeYAML) for configuration files, SLF4J for audit logging.

---

## Problem Statement

As a developer of Metaschema-based tooling deploying services, I need a resolver subsystem that:
1. Restricts access to an allowlist of local filesystem directories
2. Restricts access to an allowlist of remote HTTP services
3. Prevents SSRF attacks to internal services (localhost, cloud metadata endpoints)
4. Prevents local file inclusion attacks (directory traversal, sensitive system files)

### Security Threats Addressed

| Threat | Attack Vector | Mitigation |
|--------|--------------|------------|
| Local File Inclusion | `../../../etc/passwd` in imports | File path normalization + base directory validation |
| SSRF to Internal Services | `http://localhost:8080/admin` | Built-in denylist for localhost, private IPs |
| Cloud Metadata Access | `http://169.254.169.254/` | Built-in denylist for link-local addresses |
| XXE Attacks | XML entity resolution to arbitrary URLs | Route entity resolution through allowlist |
| Scheme Injection | `file://`, `ftp://`, `gopher://` | Scheme allowlist (default: https only) |

---

## Design Decisions

### 1. Primary Use Cases
- **Server/API deployment**: Untrusted users submitting URIs for validation
- **Library security**: Secure defaults for developers integrating the library
- **CLI hardening**: Command-line tools processing user-provided files

### 2. Configuration Model
- **Programmatic API**: Builder pattern for library integrations
- **File-based**: YAML configuration for deployments
- **Hierarchical**: Global defaults with per-loader overrides
- **Secure defaults**: Deny all schemes except https; require explicit allowlist

### 3. Rule Granularity
- **Scheme policies**: Allow/deny by URI scheme (file, http, https, jar)
- **File system rules**: Base directory + recursive/single-level scope
- **HTTP rules**: Domain allowlist + optional path prefix restrictions
- **JAR resources**: Path patterns within JAR files

### 4. Defense in Depth
- **User allowlist**: Explicit permissions required
- **Built-in denylist**: Always enforced, cannot be disabled
  - Localhost and loopback addresses
  - Private IP ranges (10.x, 172.16-31.x, 192.168.x)
  - Link-local addresses (169.254.x.x - cloud metadata)
  - Sensitive system paths (/etc/, /proc/, /sys/, C:\Windows\)

### 5. Access Denied Behavior
- **Default**: Throw `AccessDeniedException` with clear message
- **Configurable**: Custom handler for alternative behavior
- **Audit logging**: Always log blocked attempts via SLF4J

### 6. Integration Points
All resolution paths route through the allowlist resolver:

| Component | Current Behavior | Change Required |
|-----------|-----------------|-----------------|
| `DefaultBoundLoader` | Uses `IUriResolver` | None - already integrated |
| `AbstractModuleLoader` | Raw `URI.resolve()` for imports | Route through `IUriResolver` |
| `BindingConstraintLoader` | Raw `URI.resolve()` for imports | Route through `IUriResolver` |
| `DefaultXmlDeserializer` | Custom `XMLResolver` for entities | Use `IUriResolver` |
| `DefaultJsonDeserializer` | Reads from provided `Reader` | None - uses loader with allowlist |
| `DefaultYamlDeserializer` | Reads from provided `Reader` | None - uses loader with allowlist |

---

## Architecture

### Component Diagram

```text
┌─────────────────────────────────────────────────────────────────┐
│                    IAllowlistUriResolver                        │
│                    (extends IUriResolver)                       │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌──────────────────────────────────────┐  │
│  │  SchemePolicy   │  │        ResourceRules                 │  │
│  │  ─────────────  │  │  ────────────────────────────────    │  │
│  │  file:  DENY    │  │  FileSystemRules:                    │  │
│  │  http:  DENY    │  │    - baseDirs with scope             │  │
│  │  https: ALLOW   │  │    - path patterns                   │  │
│  │  jar:   ALLOW   │  │  HttpRules:                          │  │
│  │                 │  │    - domain allowlist                │  │
│  │                 │  │    - path prefix restrictions        │  │
│  │                 │  │  JarRules:                           │  │
│  │                 │  │    - allowed resource paths          │  │
│  └─────────────────┘  └──────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              BuiltInDenylist (always enforced)            │  │
│  │  ───────────────────────────────────────────────────────  │  │
│  │  Network: localhost, 127.*, 10.*, 172.16-31.*, 192.168.*  │  │
│  │           169.254.* (cloud metadata), [::1], etc.         │  │
│  │  Filesystem: /etc/, /proc/, /sys/, /dev/, ~/.ssh/         │  │
│  │              C:\Windows\, C:\Users\*\AppData\             │  │
│  └───────────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              AccessDeniedHandler (configurable)           │  │
│  │  ───────────────────────────────────────────────────────  │  │
│  │  Default: throw AccessDeniedException                     │  │
│  │  Custom: user-provided handler                            │  │
│  │  Logging: always audit via SLF4J                          │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Class Hierarchy

```text
gov.nist.secauto.metaschema.core.model.resolver/
├── IAllowlistUriResolver.java          # Main interface
├── AllowlistUriResolver.java           # Default implementation
├── AllowlistUriResolverBuilder.java    # Fluent builder
├── AccessDeniedException.java          # Exception for blocked URIs
├── IAccessDeniedHandler.java           # Custom handler interface
├── config/
│   ├── AllowlistConfiguration.java     # Configuration POJO
│   ├── AllowlistConfigurationLoader.java  # YAML loader
│   ├── SchemePolicy.java               # Enum: ALLOW, DENY
│   ├── FileSystemRule.java             # File path rules
│   ├── HttpRule.java                   # Domain/path rules
│   └── JarRule.java                    # JAR resource rules
└── denylist/
    ├── BuiltInDenylist.java            # Immutable security rules
    ├── NetworkDenylist.java            # IP/hostname patterns
    └── FileSystemDenylist.java         # Sensitive path patterns
```

### Integration Flow

```text
User Request (URI)
       │
       ▼
┌──────────────────┐
│  IModuleLoader   │──────┐
│  IDocumentLoader │      │
│  IConstraintLoader      │
│  XMLResolver     │      │
└──────────────────┘      │
       │                  │
       ▼                  ▼
┌──────────────────────────────────────┐
│      IAllowlistUriResolver           │
│  ┌────────────────────────────────┐  │
│  │ 1. Check built-in denylist    │  │
│  │ 2. Check scheme policy        │  │
│  │ 3. Check resource-specific    │  │
│  │    rules (file/http/jar)      │  │
│  │ 4. Log attempt                │  │
│  │ 5. Return URI or throw        │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
       │
       ▼
   Allowed URI → Resource Access
       or
   AccessDeniedException → Blocked
```

---

## API Design

### Programmatic Configuration (Fluent API)

```java
// Strict server mode - HTTPS only
AllowlistUriResolver serverResolver = AllowlistUriResolver.builder()
    .forScheme("https")
        .allowDomain("pages.nist.gov")
        .allowDomain("raw.githubusercontent.com")
            .restrictToPath("/metaschema-framework/")
    .forScheme("http")
        .denyAll()
    .forScheme("file")
        .denyAll()
    .forScheme("jar")
        .allowPath("/schema/")
        .allowPath("/META-INF/metaschema/")
    .defaultDeny()  // deny unlisted schemes
    .onAccessDenied((uri, reason) -> {
        auditLog.warn("Blocked resource access: {} - {}", uri, reason);
        throw new AccessDeniedException(uri, reason);
    })
    .build();

// Development mode - allow local files
AllowlistUriResolver devResolver = AllowlistUriResolver.builder()
    .forScheme("https")
        .allowDomain("pages.nist.gov")
    .forScheme("file")
        .allowDirectory("/workspace/schemas").recursive()
        .allowDirectory("/workspace/examples").recursive()
    .forScheme("jar")
        .allowPath("/schema/")
    .defaultDeny()
    .build();

// Hierarchical - inherit global with overrides
AllowlistUriResolver.setGlobalDefaults(serverResolver);

IModuleLoader loader = context.newModuleLoader();
loader.setUriResolver(AllowlistUriResolver.builder()
    .inheritGlobalDefaults()
    .forScheme("file")  // Override for this loader
        .allowDirectory(trustedSchemaPath).recursive()
    .build());
```

**Convenience constants (optional):**
```java
public final class Schemes {
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    public static final String FILE = "file";
    public static final String JAR = "jar";
    // Users can use any string: forScheme("custom-protocol")
}
```

### Metaschema-Based Configuration Model

The allowlist configuration uses a Metaschema-defined model, enabling:
- **Type-safe configuration** via generated Java classes
- **Multi-format support** - XML, JSON, or YAML
- **Schema validation** - configs validated against the Metaschema model
- **Dogfooding** - using Metaschema for its own tooling

**Metaschema Module Definition** (`allowlist-config_metaschema.yaml`):

```yaml
metaschema:
  schema-name: Allowlist Configuration
  schema-version: 1.0.0
  short-name: allowlist-config
  namespace: http://csrc.nist.gov/ns/metaschema/allowlist-config/1.0
  json-base-uri: http://csrc.nist.gov/ns/metaschema/allowlist-config/1.0

  definitions:
    - define-assembly:
        name: allowlist-config
        formal-name: Allowlist Configuration
        description: Configuration for the allowlist URI resolver.
        root-name: allowlist-config
        flags:
          - define-flag:
              name: default-policy
              as-type: token
              formal-name: Default Policy
              description: Default policy for unlisted schemes.
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
                in-json: BY_KEY
          - assembly:
              ref: logging-config
              min-occurs: 0

    - define-assembly:
        name: scheme-config
        formal-name: Scheme Configuration
        description: Configuration for a specific URI scheme.
        json-key:
          flag-ref: scheme
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
              description: Whether this scheme is enabled.
        model:
          - choice:
              - assembly:
                  ref: http-rule
                  max-occurs: unbounded
                  group-as:
                    name: http-rules
                    in-json: ARRAY
              - assembly:
                  ref: file-rule
                  max-occurs: unbounded
                  group-as:
                    name: file-rules
                    in-json: ARRAY
              - assembly:
                  ref: jar-rule
                  max-occurs: unbounded
                  group-as:
                    name: jar-rules
                    in-json: ARRAY

    - define-assembly:
        name: http-rule
        formal-name: HTTP Rule
        description: Access rule for HTTP/HTTPS URIs.
        flags:
          - define-flag:
              name: domain
              as-type: string
              required: yes
              formal-name: Domain
              description: Domain pattern (e.g., "example.com", "*.nist.gov").
        model:
          - field:
              ref: path-prefix
              max-occurs: unbounded
              group-as:
                name: paths
                in-json: ARRAY

    - define-assembly:
        name: file-rule
        formal-name: File Rule
        description: Access rule for file:// URIs.
        flags:
          - define-flag:
              name: path
              as-type: string
              required: yes
              formal-name: Path
              description: Base directory path.
          - define-flag:
              name: scope
              as-type: token
              formal-name: Scope
              description: Access scope for the directory.
              constraint:
                allowed-values:
                  - enum:
                      value: recursive
                      description: Allow recursive access
                  - enum:
                      value: single-level
                      description: Allow single level only

    - define-assembly:
        name: jar-rule
        formal-name: JAR Rule
        description: Access rule for jar: URIs.
        flags:
          - define-flag:
              name: path
              as-type: string
              required: yes
              formal-name: Path
              description: Resource path pattern within JAR.

    - define-field:
        name: path-prefix
        as-type: string
        formal-name: Path Prefix
        description: Allowed path prefix.

    - define-assembly:
        name: logging-config
        formal-name: Logging Configuration
        description: Audit logging settings.
        flags:
          - define-flag:
              name: level
              as-type: token
              formal-name: Log Level
              description: Minimum log level for access attempts.
          - define-flag:
              name: include-allowed
              as-type: boolean
              formal-name: Include Allowed
              description: Whether to log allowed access attempts.
```

**Example Configuration Files:**

YAML format (`allowlist.yaml`):
```yaml
allowlist-config:
  default-policy: deny
  schemes:
    - scheme: https
      enabled: true
      http-rules:
        - domain: pages.nist.gov
          paths: [/schemas/, /examples/]
        - domain: raw.githubusercontent.com
          paths: [/metaschema-framework/, /usnistgov/OSCAL/]
    - scheme: http
      enabled: false
    - scheme: file
      enabled: true
      file-rules:
        - path: /data/schemas
          scope: recursive
    - scheme: jar
      enabled: true
      jar-rules:
        - path: /schema/
        - path: /META-INF/metaschema/
  logging-config:
    level: WARN
    include-allowed: false
```

JSON format (`allowlist.json`):
```json
{
  "allowlist-config": {
    "default-policy": "deny",
    "schemes": {
      "https": {
        "enabled": true,
        "http-rules": [
          { "domain": "pages.nist.gov", "paths": ["/schemas/"] }
        ]
      },
      "file": {
        "enabled": false
      }
    }
  }
}
```

### Loading Configuration

```java
// Using databind to load configuration
IBindingContext bindingContext = IBindingContext.instance();
IBoundLoader loader = bindingContext.newBoundLoader();

// From file (auto-detects format: XML, JSON, or YAML)
AllowlistConfig config = loader.load(AllowlistConfig.class,
    Path.of("/etc/metaschema-cli/allowlist.yaml"));

// Create resolver from loaded config
AllowlistUriResolver resolver = AllowlistUriResolver.fromConfiguration(config);

// From classpath resource
try (InputStream is = getClass().getResourceAsStream("/allowlist.yaml")) {
    AllowlistConfig config = loader.load(AllowlistConfig.class, is);
    AllowlistUriResolver.setGlobalDefaults(
        AllowlistUriResolver.fromConfiguration(config));
}
```

---

## Configuration System

The allowlist configuration uses a layered configuration system that loads and merges configs from multiple locations, providing flexibility for different deployment scenarios.

### Configuration Directory Locations

Configurations are loaded from the following locations in precedence order (lowest to highest):

| Priority | Location | Platform | Purpose |
|----------|----------|----------|---------|
| 1 (lowest) | `<install-dir>/config/` | All | Shipped defaults bundled with distribution |
| 2 | `/etc/metaschema-cli/` | Unix | System-wide administrator settings |
| 2 | `%ProgramData%\metaschema-cli\` | Windows | System-wide administrator settings |
| 3 | `~/.metaschema-cli/` | All | User-specific preferences |
| 4 | `./.metaschema/` | All | Project-specific overrides |
| 5 (highest) | `--config-dir=<path>` | All | CLI argument override |
| 5 (highest) | `METASCHEMA_CONFIG_DIR` | All | Environment variable override |

**Install Directory Structure:**
```text
metaschema-cli/
├── bin/
│   └── metaschema-cli       # launcher script
├── lib/
│   └── metaschema-cli.jar   # main JAR
└── config/                  # install-level configs
    └── allowlist.yaml
```

**Config Files:**
Each directory can contain:
- `allowlist.yaml` - URI resolver security rules
- `logging.yaml` - Log level configuration (future)
- Other feature-specific configs as needed

### Merge Semantics

Configurations from all discovered locations are merged using the following rules:

- **Deep merge on scheme**: When multiple config files define rules for the same scheme (e.g., `https`), all domain rules are combined from all layers
- **Shallow merge on domain**: When the same domain appears in multiple layers, the higher-precedence layer's rules completely replace the lower one

**Merge Example:**

```yaml
# Install config (priority 1) - <install-dir>/config/allowlist.yaml
default: deny

schemes:
  https:
    enabled: true
    rules:
      - domain: pages.nist.gov
        paths: [/schemas/]
      - domain: raw.githubusercontent.com
        paths: [/metaschema-framework/]
  file:
    enabled: false
```

```yaml
# User config (priority 3) - ~/.metaschema-cli/allowlist.yaml
schemes:
  https:
    rules:
      - domain: pages.nist.gov        # Same domain - REPLACES install's rules
        paths: [/schemas/, /docs/]
      - domain: internal.example.com  # New domain - ADDED
        paths: any
  file:
    enabled: true                     # Overrides install's file policy
    rules:
      - path: /home/user/schemas
        scope: recursive
```

**Merged Result:**
```yaml
default: deny                         # From install (not overridden)

schemes:
  https:
    enabled: true                     # From install
    rules:
      - domain: pages.nist.gov        # User's version (shallow merge on domain)
        paths: [/schemas/, /docs/]
      - domain: raw.githubusercontent.com  # From install (kept)
        paths: [/metaschema-framework/]
      - domain: internal.example.com  # From user (added)
        paths: any
  file:
    enabled: true                     # User override
    rules:
      - path: /home/user/schemas
        scope: recursive
```

### Configuration Service API

```java
public interface IConfigurationService {
    /**
     * Get the merged configuration for a specific config file.
     *
     * @param configName the config file name (e.g., "allowlist.yaml")
     * @return the merged configuration, or empty if no configs found
     */
    Optional<Configuration> getConfiguration(String configName);

    /**
     * Get all discovered config directory paths in precedence order.
     *
     * @return list of paths (lowest to highest precedence)
     */
    List<Path> getConfigDirectories();

    /**
     * Reload all configurations from disk.
     */
    void reload();
}
```

**Integration with CLI:**

```java
// In CLI.java or CLIProcessor initialization
IConfigurationService configService = ConfigurationService.getInstance();

// Get allowlist config and create resolver
Optional<AllowlistConfiguration> allowlistConfig = configService
    .getConfiguration("allowlist.yaml")
    .map(AllowlistConfiguration::fromYaml);

if (allowlistConfig.isPresent()) {
    AllowlistUriResolver.setGlobalDefaults(
        AllowlistUriResolver.fromConfiguration(allowlistConfig.get()));
}
```

### Configuration Loading Process

1. **Discovery Phase**: Scan all config locations in order, collect paths that exist
2. **Load Phase**: Parse each discovered config file (YAML via SnakeYAML)
3. **Merge Phase**: Apply merge rules to produce final configuration
4. **Validation Phase**: Validate merged config against expected schema

**Caching Behavior:**
- Configs loaded once at startup
- `reload()` available for long-running processes
- No file watching (explicit reload only)

### Performance Analysis

| Operation | Expected Time | Notes |
|-----------|---------------|-------|
| Directory existence checks (5-6 paths) | ~1-5ms | Filesystem stat calls |
| YAML parsing (per file, ~1-5KB) | ~5-15ms | SnakeYAML parsing |
| Merge operation | <1ms | In-memory, small data structures |
| **Total (typical: 1-2 configs)** | **~10-30ms** | Negligible for CLI startup |
| **Total (worst case: all 5 locations)** | **~50-100ms** | Still acceptable |

**Context:**
- JVM startup itself takes 50-200ms
- Current CLI startup (loading modules, initializing databind) takes 200-500ms
- Config loading adds ~5-10% overhead in typical case

**Built-in Optimizations:**
- Short-circuit on CLI `--config-dir` override (skip other locations)
- Lazy loading option for configs not needed by every command
- No file watching or polling overhead

**Future Optimizations (if needed):**
- Cache merged config to temp file with checksum validation
- Parallel directory scanning
- Native YAML parser

---

## Built-In Denylist

These patterns are **blocked by default** but can be explicitly overridden when necessary (e.g., for local testing):

### Network Addresses
```java
// IPv4
"127.*.*.*"           // Loopback
"10.*.*.*"            // Private Class A
"172.16-31.*.*"       // Private Class B
"192.168.*.*"         // Private Class C
"169.254.*.*"         // Link-local (AWS/GCP/Azure metadata)
"0.0.0.0"             // All interfaces

// IPv6
"::1"                 // Loopback
"fe80::*"             // Link-local
"fc00::*"             // Unique local

// Hostnames
"localhost"
"*.localhost"
"*.local"
"metadata.google.internal"
"instance-data"       // EC2 metadata hostname
```

**Overriding for local testing:**
```java
AllowlistUriResolver.builder()
    .forScheme("http")
        .allowHost("localhost")    // explicitly override denylist
        .allowHost("127.0.0.1")
        .restrictToPort(8080)      // optional: restrict to specific port
    .build();
```

### File System Paths (Unix)
```java
"/etc/"
"/proc/"
"/sys/"
"/dev/"
"/root/"
"/home/*/.*"          // All hidden files/directories in home
"/var/run/"
"/tmp/"               // Optional - may be needed for some use cases
```

### File System Paths (Windows)
```java
"C:\\Windows\\"
"C:\\Users\\*\\AppData\\"
"C:\\ProgramData\\"
"C:\\$Recycle.Bin\\"
"*\\.ssh\\"
"*\\.aws\\"
```

---

## Success Criteria

From Issue #183:
- [ ] All website and readme documentation affected by the changes have been updated
- [ ] A Pull Request is submitted that fully addresses the goals
- [ ] The CI-CD build process runs without any reported errors

### Additional Acceptance Criteria

**Functional:**
- [ ] Module loading respects allowlist for imports
- [ ] Document loading respects allowlist
- [ ] Constraint loading respects allowlist for imports
- [ ] XML entity resolution respects allowlist
- [ ] Built-in denylist blocks all defined patterns
- [ ] Scheme policies correctly allow/deny by scheme
- [ ] File system rules enforce directory boundaries
- [ ] HTTP rules enforce domain and path restrictions
- [ ] JAR rules enforce resource path restrictions
- [ ] Hierarchical configuration (global + per-loader) works correctly
- [ ] YAML configuration loading works correctly

**Security:**
- [ ] Path traversal attacks are blocked (../../../etc/passwd)
- [ ] SSRF to localhost is blocked
- [ ] SSRF to private IP ranges is blocked
- [ ] Cloud metadata endpoints are blocked (169.254.169.254)
- [ ] Sensitive system paths are blocked

**Non-Functional:**
- [ ] Clear error messages when access is denied
- [ ] Audit logging for all blocked attempts
- [ ] Minimal performance overhead for resolution
- [ ] 80%+ test coverage for resolver code

---

## Testing Strategy

### Unit Tests
- SchemePolicy allow/deny behavior
- FileSystemRule path matching and boundary validation
- HttpRule domain and path matching
- JarRule resource path matching
- BuiltInDenylist pattern matching
- AllowlistUriResolverBuilder configuration
- YAML configuration parsing

### Integration Tests
- Module loading with allowlist enabled
- Document loading with allowlist enabled
- Constraint loading with allowlist enabled
- XML entity resolution with allowlist enabled
- Hierarchical configuration inheritance

### Security Tests
- Path traversal attack vectors
- SSRF attack vectors (localhost, private IPs, metadata endpoints)
- Scheme injection attacks
- Unicode/encoding bypass attempts
- Case sensitivity handling (Windows paths)

---

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking existing applications | High | Opt-in by default; document migration path |
| Performance overhead | Medium | Efficient pattern matching; caching |
| Incomplete denylist | High | Research common attack vectors; allow updates |
| Configuration complexity | Medium | Sensible defaults; clear documentation |
| Platform-specific path issues | Medium | Test on Windows/Linux/Mac; normalize paths |

---

## Out of Scope

- Authentication/authorization for HTTP resources (use existing HTTP client config)
- Rate limiting or request throttling
- Content inspection (only URI-based filtering)
- Certificate validation (use JVM truststore config)
