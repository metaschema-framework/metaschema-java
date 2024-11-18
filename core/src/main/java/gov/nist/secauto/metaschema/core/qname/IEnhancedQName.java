/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.qname;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IEnhancedQName {
  int getIndexPosition();

  @NonNull
  String getNamespace();

  @NonNull
  URI getNamespaceAsUri();

  @NonNull
  String getLocalName();

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
    return ObjectUtils.notNull(builder.append(getLocalName())
        .toString());
  }

  @NonNull
  default QName toQName() {
    return toQName(XMLConstants.DEFAULT_NS_PREFIX);
  }

  @NonNull
  QName toQName(@NonNull String prefix);

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
