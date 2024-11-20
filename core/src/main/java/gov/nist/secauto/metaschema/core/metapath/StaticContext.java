/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.datatype.DataTypeService;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionService;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.qname.NamespaceCache;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// add support for default namespace
/**
 * The implementation of a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#static_context">static context</a>.
 */
public final class StaticContext {
  @NonNull
  private static final Map<String, String> WELL_KNOWN_NAMESPACES;
  @NonNull
  private static final Map<String, String> WELL_KNOWN_URI_TO_PREFIX;

  static {
    Map<String, String> knownNamespaces = new ConcurrentHashMap<>();
    knownNamespaces.put(
        MetapathConstants.PREFIX_METAPATH,
        MetapathConstants.NS_METAPATH);
    knownNamespaces.put(
        MetapathConstants.PREFIX_METAPATH_FUNCTIONS,
        MetapathConstants.NS_METAPATH_FUNCTIONS);
    knownNamespaces.put(
        MetapathConstants.PREFIX_METAPATH_FUNCTIONS_MATH,
        MetapathConstants.NS_METAPATH_FUNCTIONS_MATH);
    knownNamespaces.put(
        MetapathConstants.PREFIX_METAPATH_FUNCTIONS_ARRAY,
        MetapathConstants.NS_METAPATH_FUNCTIONS_ARRAY);
    knownNamespaces.put(
        MetapathConstants.PREFIX_METAPATH_FUNCTIONS_MAP,
        MetapathConstants.NS_METAPATH_FUNCTIONS_MAP);
    WELL_KNOWN_NAMESPACES = CollectionUtil.unmodifiableMap(knownNamespaces);

    WELL_KNOWN_NAMESPACES.forEach(
        (prefix, namespace) -> NamespaceCache.instance().of(ObjectUtils.notNull(namespace)));

    WELL_KNOWN_URI_TO_PREFIX = ObjectUtils.notNull(WELL_KNOWN_NAMESPACES.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(
            (Function<? super Entry<String, String>, ? extends String>) Entry::getValue,
            Map.Entry::getKey,
            (v1, v2) -> v2)));
  }

  @Nullable
  private final URI baseUri;
  @NonNull
  private final Map<String, String> knownNamespaces;
  @Nullable
  private final URI defaultModelNamespace;
  @Nullable
  private final URI defaultFunctionNamespace;
  private final boolean useWildcardWhenNamespaceNotDefaulted;

  /**
   * Get the mapping of prefix to namespace URI for all well-known namespaces
   * provided by default to the static context.
   * <p>
   * These namespaces can be overridden using the
   * {@link Builder#namespace(String, URI)} method.
   *
   * @return the mapping of prefix to namespace URI for all well-known namespaces
   */
  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Map<String, String> getWellKnownNamespacesMap() {
    return WELL_KNOWN_NAMESPACES;
  }

  /**
   * Get the mapping of namespace URIs to prefixes for all well-known namespaces
   * provided by default to the static context.
   *
   * @return the mapping of namespace URI to prefix for all well-known namespaces
   */
  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Map<String, String> getWellKnownURIToPrefixMap() {
    return WELL_KNOWN_URI_TO_PREFIX;
  }

  /**
   * Get the namespace prefix associated with the provided URI, if the URI is
   * well-known.
   *
   * @param uri
   *          the URI to get the prefix for
   * @return the prefix or {@code null} if the provided URI is not well-known
   */
  @Nullable
  public static String getWellKnownPrefixForUri(@NonNull String uri) {
    return WELL_KNOWN_URI_TO_PREFIX.get(uri);
  }

  /**
   * Create a new static context instance using default values.
   *
   * @return a new static context instance
   */
  @NonNull
  public static StaticContext instance() {
    return builder().build();
  }

  private StaticContext(Builder builder) {
    this.baseUri = builder.baseUri;
    this.knownNamespaces = CollectionUtil.unmodifiableMap(ObjectUtils.notNull(Map.copyOf(builder.namespaces)));
    this.defaultModelNamespace = builder.defaultModelNamespace;
    this.defaultFunctionNamespace = builder.defaultFunctionNamespace;
    this.useWildcardWhenNamespaceNotDefaulted = builder.useWildcardWhenNamespaceNotDefaulted;
  }

  /**
   * Get the static base URI to use in resolving URIs handled by the Metapath
   * processor. This URI, if provided, will be used when a document base URI is
   * not available.
   *
   * @return the base URI or {@code null} if not defined
   */
  @Nullable
  public URI getBaseUri() {
    return baseUri;
  }

  /**
   * Get the namespace URI associated with the provided {@code prefix}, if any is
   * bound.
   * <p>
   * This method uses the namespaces set by the
   * {@link Builder#namespace(String, URI)} method, falling back to the well-known
   * namespace bindings when a prefix match is not found.
   * <p>
   * The well-known namespace bindings can be retrieved using the
   * {@link StaticContext#getWellKnownNamespacesMap()} method.
   *
   * @param prefix
   *          the namespace prefix
   * @return the namespace URI bound to the prefix, or {@code null} if no
   *         namespace is bound to the prefix
   * @see Builder#namespace(String, URI)
   * @see #getWellKnownNamespacesMap()
   */
  @Nullable
  private String lookupNamespaceURIForPrefix(@NonNull String prefix) {
    String retval = knownNamespaces.get(prefix);
    if (retval == null) {
      // fall back to well-known namespaces
      retval = WELL_KNOWN_NAMESPACES.get(prefix);
    }
    return retval;
  }

  /**
   * Get the namespace associated with the provided {@code prefix} as a string, if
   * any is bound.
   *
   * @param prefix
   *          the namespace prefix
   * @return the namespace string bound to the prefix, or {@code null} if no
   *         namespace is bound to the prefix
   */
  // FIXME: check for https://www.w3.org/TR/xpath-31/#ERRXPST0081
  @Nullable
  public String lookupNamespaceForPrefix(@NonNull String prefix) {
    String result = lookupNamespaceURIForPrefix(prefix);
    return result == null ? null : result;
  }

  /**
   * Get the default namespace for assembly, field, or flag references that have
   * no namespace prefix.
   *
   * @return the namespace if defined or {@code null} otherwise
   */
  @Nullable
  private URI getDefaultModelNamespace() {
    return defaultModelNamespace;
  }

  /**
   * Get the default namespace for function references that have no namespace
   * prefix.
   *
   * @return the namespace if defined or {@code null} otherwise
   */
  @Nullable
  private URI getDefaultFunctionNamespace() {
    return defaultFunctionNamespace;
  }

  private String resolveBasicPrefix(@NonNull String prefix) {
    String ns = lookupNamespaceForPrefix(prefix);
    return ns == null ? XMLConstants.NULL_NS_URI : ns;
  }

  @NonNull
  private IEnhancedQName parseDataTypeName(@NonNull String name) {
    try {
      return EQNameFactory.instance().parseName(
          name,
          this::resolveDataTypePrefix);
    } catch (StaticMetapathException ex) {
      throw new StaticMetapathException(
          StaticMetapathException.NOT_DEFINED,
          String.format("The data type named '%s' was not found.", name), ex);
    }
  }

  private String resolveDataTypePrefix(@NonNull String prefix) {
    String ns = lookupNamespaceForPrefix(prefix);
    if (ns == null) {
      if (!prefix.isEmpty()) {
        throw new StaticMetapathException(
            StaticMetapathException.PREFIX_NOT_EXPANDABLE,
            String.format("The namespace prefix '%s'  is not expandable.",
                prefix));
      }
      // use the default data type namespace
      ns = MetapathConstants.NS_METAPATH;
    }
    return ns;
  }

  @Nullable
  public IFunction lookupFunction(@NonNull String name, int arity) {
    IEnhancedQName qname = parseFunctionName(name);
    return lookupFunction(qname, arity);
  }

  @Nullable
  public static IFunction lookupFunction(@NonNull IEnhancedQName qname, int arity) {
    return FunctionService.getInstance().getFunction(
        Objects.requireNonNull(qname, "name"),
        arity);
  }

  @NonNull
  public IAtomicOrUnionType lookupDataTypeItemType(@NonNull String name) {
    IEnhancedQName qname = parseDataTypeName(name);
    return lookupDataTypeItemType(qname);
  }

  @NonNull
  public static IAtomicOrUnionType lookupDataTypeItemType(@NonNull IEnhancedQName qname) {
    IAtomicOrUnionType retval = DataTypeService.instance().getDataTypeByQNameIndex(qname.getIndexPosition());
    if (retval == null) {
      throw new StaticMetapathException(
          StaticMetapathException.UNKNOWN_TYPE,
          String.format("The data type named '%s' was not found.", qname));
    }
    return retval;
  }

  @NonNull
  public static IAtomicOrUnionType lookupDataTypeItemType(Class<? extends IAnyAtomicItem> clazz) {
    IAtomicOrUnionType retval = DataTypeService.instance().getDataTypeByItemClass(clazz);
    if (retval == null) {
      throw new StaticMetapathException(
          StaticMetapathException.UNKNOWN_TYPE,
          String.format("The data type for item class '%s' was not found.", clazz.getName()));
    }
    return retval;
  }

  @NonNull
  private IEnhancedQName parseFunctionName(@NonNull String name) {
    return EQNameFactory.instance().parseName(
        name,
        this::resolveFunctionPrefix);
  }

  @NonNull
  private String resolveFunctionPrefix(@NonNull String prefix) {
    String ns = lookupNamespaceForPrefix(prefix);
    if (ns == null) {
      URI uri = getDefaultFunctionNamespace();
      if (uri != null) {
        ns = uri.toASCIIString();
      }
    }
    return ns == null ? XMLConstants.NULL_NS_URI : ns;
  }

  /**
   * Parse a flag name.
   * <p>
   * This method will attempt to identify the namespace corresponding to a given
   * prefix.
   * <p>
   * The prefix will be resolved using the following lookup order, advancing to
   * the next when a {@code null} value is returned:
   * <ol>
   * <li>Lookup the prefix using the namespaces registered with the static
   * context.</li>
   * <li>Lookup the prefix in the well-known namespaces.</li>
   * <li>Use {@link XMLConstants#NULL_NS_URI}.</li>
   * </ol>
   *
   * @param name
   *          the name
   * @return the parsed qualified name
   */
  @NonNull
  public IEnhancedQName parseFlagName(@NonNull String name) {
    return EQNameFactory.instance().parseName(
        name,
        this::resolveBasicPrefix);
  }

  /**
   * Parse a model name.
   * <p>
   * This method will attempt to identify the namespace corresponding to a given
   * prefix.
   * <p>
   * The prefix will be resolved using the following lookup order, advancing to
   * the next when a {@code null} value is returned:
   * <ol>
   * <li>Lookup the prefix using the namespaces registered with the static
   * context.</li>
   * <li>Lookup the prefix in the well-known namespaces.</li>
   * <li>Use the default model namespace (see
   * {@link Builder#defaultModelNamespace(String)}).</li>
   * </ol>
   *
   * @param name
   *          the name
   * @return the parsed qualified name
   */
  @NonNull
  public IEnhancedQName parseModelName(@NonNull String name) {
    return EQNameFactory.instance().parseName(
        name,
        this::resolveModelReferencePrefix);
  }

  @NonNull
  private String resolveModelReferencePrefix(@NonNull String prefix) {
    String ns = lookupNamespaceForPrefix(prefix);
    if (ns == null) {
      URI uri = getDefaultModelNamespace();
      if (uri != null) {
        ns = uri.toASCIIString();
      }
    }
    return ns == null ? XMLConstants.NULL_NS_URI : ns;
  }

  /**
   * Parse a variable name.
   * <p>
   * This method will attempt to identify the namespace corresponding to a given
   * prefix.
   * <p>
   * The prefix will be resolved using the following lookup order, advancing to
   * the next when a {@code null} value is returned:
   *
   * <ol>
   * <li>Lookup the prefix using the namespaces registered with the static
   * context.</li>
   * <li>Lookup the prefix in the well-known namespaces.</li>
   * <li>Use {@link XMLConstants#NULL_NS_URI}.</li>
   * </ol>
   *
   * @param name
   *          the name
   * @return the parsed qualified name
   */
  @NonNull
  public IEnhancedQName parseVariableName(@NonNull String name) {
    return EQNameFactory.instance().parseName(
        name,
        this::resolveBasicPrefix);
  }

  /**
   * Get a new static context builder that is pre-populated with the setting of
   * this static context.
   *
   * @return a new builder
   */
  @NonNull
  public Builder buildFrom() {
    Builder builder = builder();
    builder.baseUri = this.baseUri;
    builder.namespaces.putAll(this.knownNamespaces);
    builder.defaultModelNamespace = this.defaultModelNamespace;
    builder.defaultFunctionNamespace = this.defaultFunctionNamespace;
    return builder;
  }

  /**
   * Indicates if a name match should use a wildcard for the namespace if the
   * namespace does not have a value and the default model namespace is
   * {@code null}.
   *
   * @return {@code true} if a wildcard match on the name space should be used or
   *         {@code false} otherwise
   */
  public boolean isUseWildcardWhenNamespaceNotDefaulted() {
    return useWildcardWhenNamespaceNotDefaulted && getDefaultModelNamespace() == null;
  }

  /**
   * Create a new static context builder that allows for fine-grained adjustments
   * when creating a new static context.
   *
   * @return a new builder
   */
  @NonNull
  public static Builder builder() {
    return new Builder();
  }

  /**
   * A builder used to generate the static context.
   */
  public static final class Builder {
    private boolean useWildcardWhenNamespaceNotDefaulted; // false
    @Nullable
    private URI baseUri;
    @NonNull
    private final Map<String, String> namespaces = new ConcurrentHashMap<>();
    @Nullable
    private URI defaultModelNamespace;
    @Nullable
    private URI defaultFunctionNamespace = MetapathConstants.NS_METAPATH_FUNCTIONS_URI;

    private Builder() {
      // avoid direct construction
    }

    /**
     * Sets the static base URI to use in resolving URIs handled by the Metapath
     * processor, when a document base URI is not available. There is only a single
     * base URI. Subsequent calls to this method will change the base URI.
     *
     * @param uri
     *          the base URI to use
     * @return this builder
     */
    @NonNull
    public Builder baseUri(@NonNull URI uri) {
      this.baseUri = uri;
      return this;
    }

    /**
     * Adds a new prefix to namespace URI binding to the mapping of
     * <a href="https://www.w3.org/TR/xpath-31/#dt-static-namespaces">statically
     * known namespaces</a>.
     * <p>
     * A namespace set by this method can be resolved using the
     * {@link StaticContext#lookupNamespaceForPrefix(String)} method.
     * <p>
     * Well-known namespace bindings are used by default, which can be retrieved
     * using the {@link StaticContext#getWellKnownNamespacesMap()} method.
     *
     * @param prefix
     *          the prefix to associate with the namespace, which may be
     * @param uri
     *          the namespace URI
     * @return this builder
     * @see StaticContext#lookupNamespaceForPrefix(String)
     * @see StaticContext#getWellKnownNamespacesMap()
     */
    // FIXME: check for https://www.w3.org/TR/xpath-31/#ERRXPST0070 for "meta"
    @NonNull
    public Builder namespace(@NonNull String prefix, @NonNull URI uri) {
      return namespace(prefix, ObjectUtils.notNull(uri.toASCIIString()));
    }

    /**
     * A convenience method for {@link #namespace(String, URI)}.
     *
     * @param prefix
     *          the prefix to associate with the namespace, which may be
     * @param uri
     *          the namespace URI
     * @return this builder
     * @throws IllegalArgumentException
     *           if the provided URI is invalid
     * @see StaticContext#lookupNamespaceForPrefix(String)
     * @see StaticContext#getWellKnownNamespacesMap()
     */
    @NonNull
    public Builder namespace(@NonNull String prefix, @NonNull String uri) {
      this.namespaces.put(prefix, uri);
      NamespaceCache.instance().of(uri);
      return this;
    }

    /**
     * Defines the default namespace to use for assembly, field, or flag references
     * that have no namespace prefix.
     *
     * @param uri
     *          the namespace URI
     * @return this builder
     */
    @NonNull
    public Builder defaultModelNamespace(@NonNull URI uri) {
      this.defaultModelNamespace = uri;
      NamespaceCache.instance().of(uri);
      return this;
    }

    /**
     * A convenience method for {@link #defaultModelNamespace(URI)}.
     *
     * @param uri
     *          the namespace URI
     * @return this builder
     * @throws IllegalArgumentException
     *           if the provided URI is invalid
     */
    @NonNull
    public Builder defaultModelNamespace(@NonNull String uri) {
      return defaultModelNamespace(ObjectUtils.notNull(URI.create(uri)));
    }

    /**
     * Defines the default namespace to use for assembly, field, or flag references
     * that have no namespace prefix.
     *
     * @param uri
     *          the namespace URI
     * @return this builder
     */
    @NonNull
    public Builder defaultFunctionNamespace(@NonNull URI uri) {
      this.defaultFunctionNamespace = uri;
      NamespaceCache.instance().of(uri);
      return this;
    }

    /**
     * A convenience method for {@link #defaultFunctionNamespace(URI)}.
     *
     * @param uri
     *          the namespace URI
     * @return this builder
     * @throws IllegalArgumentException
     *           if the provided URI is invalid
     */
    @NonNull
    public Builder defaultFunctionNamespace(@NonNull String uri) {
      return defaultFunctionNamespace(ObjectUtils.notNull(URI.create(uri)));
    }

    /**
     * Set the name matching behavior for when a model node has no namespace.
     *
     * @param value
     *          {@code true} if on or {@code false} otherwise
     * @return this builder
     */
    public Builder useWildcardWhenNamespaceNotDefaulted(boolean value) {
      this.useWildcardWhenNamespaceNotDefaulted = value;
      return this;
    }

    /**
     * Construct a new static context using the information provided to the builder.
     *
     * @return the new static context
     */
    @NonNull
    public StaticContext build() {
      return new StaticContext(this);
    }
  }
}
