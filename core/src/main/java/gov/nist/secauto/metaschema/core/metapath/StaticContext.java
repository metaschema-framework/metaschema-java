/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.datatype.DataTypeService;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionService;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.qname.NamespaceCache;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.net.URISyntaxException;
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
// FIXME: refactor well-known into a new class
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
        (prefix, namespace) -> NamespaceCache.instance().indexOf(ObjectUtils.notNull(namespace)));

    WELL_KNOWN_URI_TO_PREFIX = ObjectUtils.notNull(WELL_KNOWN_NAMESPACES.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(
            (Function<? super Entry<String, String>, ? extends String>) Entry::getValue,
            Map.Entry::getKey,
            (v1, v2) -> v1)));
  }

  @Nullable
  private final URI baseUri;
  @NonNull
  private final Map<String, String> knownPrefixToNamespace;
  @NonNull
  private final Map<String, String> knownNamespacesToPrefix;
  @Nullable
  private final String defaultModelNamespace;
  @Nullable
  private final String defaultFunctionNamespace;
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
    this.knownPrefixToNamespace = CollectionUtil.unmodifiableMap(ObjectUtils.notNull(Map.copyOf(builder.namespaces)));
    this.knownNamespacesToPrefix = ObjectUtils.notNull(builder.namespaces.entrySet().stream()
        .map(entry -> Map.entry(entry.getValue(), entry.getKey()))
        .collect(Collectors.toUnmodifiableMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            CustomCollectors.useFirstMapper())));
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
    String retval = knownPrefixToNamespace.get(prefix);
    if (retval == null) {
      // fall back to well-known namespaces
      retval = WELL_KNOWN_NAMESPACES.get(prefix);
    }
    return retval;
  }

  @Nullable
  private String lookupPrefixForNamespaceURI(@NonNull String namespace) {
    String retval = knownNamespacesToPrefix.get(namespace);
    if (retval == null) {
      // fall back to well-known namespaces
      retval = WELL_KNOWN_URI_TO_PREFIX.get(namespace);
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
  @Nullable
  public String lookupNamespaceForPrefix(@NonNull String prefix) {
    String result = lookupNamespaceURIForPrefix(prefix);
    return result == null ? null : result;
  }

  /**
   * Get the prefix associated with the provided {@code namespace} as a string, if
   * any is bound.
   *
   * @param namespace
   *          the namespace
   * @return the prefix string bound to the prefix, or {@code null} if no prefix
   *         is bound to the namespace
   */
  @Nullable
  public String lookupPrefixForNamespace(@NonNull String namespace) {
    return lookupPrefixForNamespaceURI(namespace);
  }

  /**
   * Get the default namespace for assembly, field, or flag references that have
   * no namespace prefix.
   *
   * @return the namespace if defined or {@code null} otherwise
   */
  @Nullable
  private String getDefaultModelNamespace() {
    return defaultModelNamespace;
  }

  /**
   * Get the default namespace for function references that have no namespace
   * prefix.
   *
   * @return the namespace if defined or {@code null} otherwise
   */
  @Nullable
  private String getDefaultFunctionNamespace() {
    return defaultFunctionNamespace;
  }

  /**
   * Parse the name of an atomic type.
   *
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
   * </ol>
   *
   * If an empty prefix is provided, the {@link MetapathConstants#NS_METAPATH}
   * namespace will be used.</li>
   *
   * @param name
   *          the name
   * @return the parsed qualified name
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE}
   *           if a non-empty prefix is provided
   */
  @NonNull
  public IEnhancedQName parseAtomicTypeName(@NonNull String name) {
    return EQNameFactory.instance().parseName(
        name,
        this::resolveAtomicTypePrefix);
  }

  private String resolveAtomicTypePrefix(@NonNull String prefix) {
    String ns = lookupNamespaceForPrefix(prefix);
    if (ns == null) {
      checkForUnknownPrefix(prefix);
      // use the default data type namespace
      ns = MetapathConstants.NS_METAPATH;
    }
    return ns;
  }

  /**
   * Lookup the atomic type with the provided name in the static context.
   * <p>
   * This method will first attempt to expand the namespace prefix for a lexical
   * QName. A {@link StaticMetapathException} with the code
   * {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE} if the prefix is not
   * know to the static context.
   * <p>
   * Once the qualified name has been produced, the atomic type will be retrieved
   * from the available atomic types. If the atomic type was not found, a
   * {@link StaticMetapathException} with the code
   * {@link StaticMetapathException#UNKNOWN_TYPE} will be thrown. Otherwise, the
   * type information is returned for the matching atomic type.
   *
   * @param name
   *          the namespace qualified or lexical name of the data type.
   * @return the data type information
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE}
   *           if the lexical name was not able to be expanded or the code
   *           {@link StaticMetapathException#NO_FUNCTION_MATCH} if a matching
   *           type was not found
   */
  @NonNull
  public IAtomicOrUnionType<?> lookupAtomicType(@NonNull String name) {
    IEnhancedQName qname = parseAtomicTypeName(name);
    return lookupAtomicType(qname);
  }

  /**
   * Lookup a known Metapath atomic type based on the type's qualified name.
   *
   * @param qname
   *          the qualified name
   * @return the type
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#UNKNOWN_TYPE} if the
   *           type was not found
   */
  @NonNull
  public static IAtomicOrUnionType<?> lookupAtomicType(@NonNull IEnhancedQName qname) {
    IAtomicOrUnionType<?> retval = DataTypeService.instance().getAtomicTypeByQNameIndex(qname.getIndexPosition());
    if (retval == null) {
      throw new StaticMetapathException(
          StaticMetapathException.UNKNOWN_TYPE,
          String.format("The atomic type named '%s' was not found.", qname));
    }
    return retval;
  }

  /**
   * Lookup a known Metapath atomic type based on the type's item class.
   *
   * @param clazz
   *          the item class associated with the atomic type
   * @return the type
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#UNKNOWN_TYPE} if the
   *           type was not found
   */
  @NonNull
  public static <T extends IAnyAtomicItem> IAtomicOrUnionType<T> lookupAtomicType(Class<T> clazz) {
    IAtomicOrUnionType<T> retval = DataTypeService.instance().getAtomicTypeByItemClass(clazz);
    if (retval == null) {
      throw new StaticMetapathException(
          StaticMetapathException.UNKNOWN_TYPE,
          String.format("The atomic type for item class '%s' was not found.", clazz.getName()));
    }
    return retval;
  }

  /**
   * Lookup a known Metapath item type based on the type's item class.
   *
   * @param clazz
   *          the item class associated with the atomic type
   * @return the type
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#UNKNOWN_TYPE} if the
   *           type was not found
   */
  @NonNull
  public static IItemType lookupItemType(Class<? extends IItem> clazz) {
    IItemType retval = DataTypeService.instance().getItemTypeByItemClass(clazz);
    if (retval == null) {
      throw new StaticMetapathException(
          StaticMetapathException.UNKNOWN_TYPE,
          String.format("The item type for item class '%s' was not found.", clazz.getName()));
    }
    return retval;
  }

  @NonNull
  public IEnhancedQName parseFunctionName(@NonNull String name) {
    return EQNameFactory.instance().parseName(
        name,
        this::resolveFunctionPrefix);
  }

  @NonNull
  private String resolveFunctionPrefix(@NonNull String prefix) {
    String ns = lookupNamespaceForPrefix(prefix);
    if (ns == null) {
      checkForUnknownPrefix(prefix);
      // use the default namespace, since the namespace was omitted
      ns = getDefaultFunctionNamespace();
    }
    return ns == null ? XMLConstants.NULL_NS_URI : ns;
  }

  /**
   * Checks if the provided prefix is not-empty, which means the prefix was not
   * resolvable.
   *
   * @param prefix
   *          the lexical prefix to check
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE}
   *           if a non-empty prefix is provided
   */
  private static void checkForUnknownPrefix(@NonNull String prefix) {
    if (!prefix.isEmpty()) {
      throw new StaticMetapathException(
          StaticMetapathException.PREFIX_NOT_EXPANDABLE,
          String.format("The namespace prefix '%s' is not expandable.",
              prefix));
    }
  }

  /**
   * Lookup a known Metapath function based on the function's name and arity.
   * <p>
   * This method will first attempt to expand the namespace prefix for a lexical
   * QName. A {@link StaticMetapathException} with the code
   * {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE} if the prefix is not
   * know to the static context.
   * <p>
   * Once the qualified name has been produced, the function will be retrieved
   * from the available functions. If the function was not found, a
   * {@link StaticMetapathException} with the code
   * {@link StaticMetapathException#UNKNOWN_TYPE} will be thrown. Otherwise, the
   * data type information is returned for the matching data type.
   *
   * @param name
   *          the qualified or lexical name of the function
   * @param arity
   *          the number of arguments
   * @return the type
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE}
   *           if the lexical name was not able to be expanded or the code
   *           {@link StaticMetapathException#NO_FUNCTION_MATCH} if a matching
   *           function was not found
   */
  @NonNull
  public IFunction lookupFunction(@NonNull String name, int arity) {
    IEnhancedQName qname = parseFunctionName(name);
    return lookupFunction(qname, arity);
  }

  /**
   * Lookup a known Metapath function based on the function's name and arity.
   *
   * @param qname
   *          the qualified name of the function
   * @param arity
   *          the number of arguments
   * @return the function
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#NO_FUNCTION_MATCH} if
   *           a matching function was not found
   */
  @NonNull
  public static IFunction lookupFunction(@NonNull IEnhancedQName qname, int arity) {
    return FunctionService.getInstance().getFunction(
        Objects.requireNonNull(qname, "name"),
        arity);
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
   * </ol>
   *
   * If an empty prefix is provided, the {@link XMLConstants#NULL_NS_URI}
   * namespace will be used.</li>
   *
   * @param name
   *          the name
   * @return the parsed qualified name
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE}
   *           if a non-empty prefix is provided
   */
  @NonNull
  public IEnhancedQName parseFlagName(@NonNull String name) {
    return EQNameFactory.instance().parseName(
        name,
        this::resolveBasicPrefix);
  }

  private String resolveBasicPrefix(@NonNull String prefix) {
    String ns = lookupNamespaceForPrefix(prefix);
    if (ns == null) {
      checkForUnknownPrefix(prefix);
    }
    return ns == null ? XMLConstants.NULL_NS_URI : ns;
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
   * </ol>
   * If an empty prefix is provided, the
   * {@link Builder#defaultModelNamespace(String)} namespace will be used.</li>
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
      checkForUnknownPrefix(prefix);
      ns = getDefaultModelNamespace();
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
   * </ol>
   * If an empty prefix is provided, the {@link XMLConstants#NULL_NS_URI}
   * namespace will be used.</li>
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
    builder.namespaces.putAll(this.knownPrefixToNamespace);
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
    private String defaultModelNamespace;
    @Nullable
    private String defaultFunctionNamespace = MetapathConstants.NS_METAPATH_FUNCTIONS;

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
     *           if the provided prefix or URI is invalid
     * @see StaticContext#lookupNamespaceForPrefix(String)
     * @see StaticContext#getWellKnownNamespacesMap()
     */
    @NonNull
    public Builder namespace(@NonNull String prefix, @NonNull String uri) {
      if (MetapathConstants.PREFIX_METAPATH.equals(prefix)) {
        // check for https://www.w3.org/TR/xpath-31/#ERRXPST0070 for "meta"
        throw new IllegalArgumentException(
            "Redefining the prefix '" + MetapathConstants.PREFIX_METAPATH + "' is not allowed.");
      }
      this.namespaces.put(prefix, uri);
      NamespaceCache.instance().indexOf(uri);
      return this;
    }

    /**
     * Defines the default namespace to use for assembly, field, or flag references
     * that have no namespace prefix.
     *
     * @param namespace
     *          the namespace URI
     * @return this builder
     */
    @NonNull
    public Builder defaultModelNamespace(@NonNull URI namespace) {
      String uri = ObjectUtils.notNull(namespace.toASCIIString());
      this.defaultModelNamespace = uri;
      NamespaceCache.instance().indexOf(uri);
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
      try {
        this.defaultModelNamespace = new URI(uri).toASCIIString();
      } catch (URISyntaxException ex) {
        throw new IllegalArgumentException(ex);
      }
      NamespaceCache.instance().indexOf(uri);
      return this;
    }

    /**
     * Defines the default namespace to use for assembly, field, or flag references
     * that have no namespace prefix.
     *
     * @param namespace
     *          the namespace URI
     * @return this builder
     */
    @NonNull
    public Builder defaultFunctionNamespace(@NonNull URI namespace) {
      String uri = ObjectUtils.notNull(namespace.toASCIIString());
      this.defaultFunctionNamespace = uri;
      NamespaceCache.instance().indexOf(uri);
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
      try {
        this.defaultFunctionNamespace = new URI(uri).toASCIIString();
      } catch (URISyntaxException ex) {
        throw new IllegalArgumentException(ex);
      }
      NamespaceCache.instance().indexOf(uri);
      return this;
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

  /**
   * Provides a callback for resolving namespace prefixes.
   */
  @FunctionalInterface
  public interface EQNameResolver {
    /**
     * Get the URI string for the provided namespace prefix.
     *
     * @param name
     *          the name to resolve
     * @return the URI string or {@code null} if the prefix is unbound
     * @throws StaticMetapathException
     *           with the code {@link StaticMetapathException#PREFIX_NOT_EXPANDABLE}
     *           if a non-empty prefix is provided
     */
    @NonNull
    IEnhancedQName resolve(@NonNull String name);
  }
}
