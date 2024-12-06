/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.qname;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An efficient cache-backed representation of a qualified name.
 * <p>
 * This implementation uses an underlying integer-based cache to reduce the
 * memory footprint of qualified names and namespaces by reusing instances with
 * the same namespace and local name.
 */
public interface IEnhancedQName extends Comparable<IEnhancedQName> {
  /**
   * Get the index position of the qualified name.
   * <p>
   * This value can be used in place of this object. The object can be retrieved
   * using this index with the {@link #of(int)} method.
   *
   * @return the index position
   */
  int getIndexPosition();

  /**
   * Get the namespace part of the qualified name.
   *
   * @return the namespace
   */
  @NonNull
  String getNamespace();

  /**
   * Get the namespace part of the qualified name.
   *
   * @return the namespace as a URI
   */
  @NonNull
  URI getNamespaceAsUri();

  /**
   * Get the local part of the qualified name.
   *
   * @return the local name
   */
  @NonNull
  String getLocalName();

  /**
   * Get an existing qualified name by looking up the cached entry using the
   * provided index value.
   *
   * @param index
   *          the index value to lookup
   * @return an optional containing the qualified name, if it exists
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static Optional<IEnhancedQName> of(int index) {
    return EQNameFactory.instance().get(index);
  }

  /**
   * Get a qualified name using the provided {@link QName} value.
   *
   * @param qname
   *          the qualified name to get
   * @return the qualified name
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IEnhancedQName of(@NonNull QName qname) {
    return of(
        ObjectUtils.notNull(qname.getNamespaceURI()),
        ObjectUtils.notNull(qname.getLocalPart()));
  }

  /**
   * Get a qualified name using the provided local name value with no namespace.
   *
   * @param localName
   *          the qualified name local part
   * @return the qualified name
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IEnhancedQName of(@NonNull String localName) {
    return of("", localName);
  }

  /**
   * Get a qualified name using the provided namespace and local name.
   *
   * @param namespace
   *          the qualified name namespace part
   * @param localName
   *          the qualified name local part
   * @return the qualified name
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IEnhancedQName of(@NonNull URI namespace, @NonNull String localName) {
    return of(ObjectUtils.notNull(namespace.toASCIIString()), localName);
  }

  /**
   * Get a qualified name using the provided namespace and local name.
   *
   * @param namespace
   *          the qualified name namespace part
   * @param localName
   *          the qualified name local part
   * @return the qualified name
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IEnhancedQName of(@NonNull String namespace, @NonNull String localName) {
    return EQNameFactory.instance().newQName(namespace, localName);
  }

  /**
   * Generate a qualified name for this QName.
   * <p>
   * This method uses prefixes associated with well-known namespaces, or will
   * prepending the namespace if no prefix can be resolved.
   *
   * @param resolver
   *          the resolver to use to lookup the prefix
   * @return the extended qualified-name
   */
  @NonNull
  default String toEQName() {
    return toEQName((NamespaceToPrefixResolver) null);
  }

  /**
   * Generate a qualified name for this QName, use a prefix provided by the
   * resolver, or by prepending the namespace if no prefix can be resolved.
   *
   * @param resolver
   *          the resolver to use to lookup the prefix
   * @return the extended qualified-name
   */
  @NonNull
  default String toEQName(@Nullable NamespaceToPrefixResolver resolver) {
    String namespace = getNamespace();
    String prefix = namespace.isEmpty() ? null : StaticContext.getWellKnownPrefixForUri(namespace);
    if (prefix == null && resolver != null) {
      prefix = resolver.resolve(namespace);
    }
    return toEQName(namespace, getLocalName(), prefix);
  }

  /**
   * Generate a qualified name for this QName, use a prefix resolved from the
   * provided static context, or by prepending the namespace if no prefix can be
   * resolved.
   *
   * @param staticContext
   *          the static context to use to lookup the prefix
   * @return the extended qualified-name
   */
  @NonNull
  default String toEQName(@NonNull StaticContext staticContext) {
    String namespace = getNamespace();
    String prefix = namespace.isEmpty() ? null : staticContext.lookupPrefixForNamespace(namespace);
    return toEQName(namespace, getLocalName(), prefix);
  }

  @NonNull
  private static String toEQName(
      @NonNull String namespace,
      @NonNull String localName,
      @Nullable String prefix) {

    StringBuilder builder = new StringBuilder();
    if (prefix == null) {
      if (!namespace.isEmpty()) {
        builder.append("Q{")
            .append(namespace)
            .append('}');
      }
    } else {
      builder.append(prefix)
          .append(':');
    }
    return ObjectUtils.notNull(builder.append(localName)
        .toString());
  }

  /**
   * Generate a {@link QName} without a namespace prefix.
   *
   * @return the name
   */
  @NonNull
  default QName toQName() {
    return toQName(XMLConstants.DEFAULT_NS_PREFIX);
  }

  /**
   * Generate a {@link QName} using the provided namespace prefix.
   *
   * @param prefix
   *          the prefix to use
   * @return the name
   */
  @NonNull
  default QName toQName(@NonNull String prefix) {
    return new QName(getNamespace(), getLocalName(), prefix);
  }

  /**
   * Provides a callback for resolving namespace prefixes.
   */
  @FunctionalInterface
  interface NamespaceToPrefixResolver {
    /**
     * Get the URI string for the provided namespace prefix.
     *
     * @param namespace
     *          the namespace URI
     * @return the associated prefix or {@code null} if no prefix is associated
     */
    @Nullable
    String resolve(@NonNull String namespace);
  }
}
