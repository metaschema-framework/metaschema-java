/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.qname;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.qname.impl.NamespaceCache;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class WellKnown {
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

  // /**
  // * Get the mapping of prefix to namespace URI for all well-known namespaces
  // * provided by default to the static context.
  // *
  // * @return the mapping of prefix to namespace URI for all well-known
  // namespaces
  // */
  // @SuppressFBWarnings("MS_EXPOSE_REP")
  // public static Map<String, String> getWellKnownNamespacesMap() {
  // return WELL_KNOWN_NAMESPACES;
  // }
  //
  // /**
  // * Get the mapping of namespace URIs to prefixes for all well-known namespaces
  // * provided by default to the static context.
  // *
  // * @return the mapping of namespace URI to prefix for all well-known
  // namespaces
  // */
  // @SuppressFBWarnings("MS_EXPOSE_REP")
  // public static Map<String, String> getWellKnownURIToPrefixMap() {
  // return WELL_KNOWN_URI_TO_PREFIX;
  // }

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
   * Get the namespace associated with the provided prefix, if the prefix is
   * well-known.
   *
   * @param prefix
   *          the prefix
   * @return the URI associated with the prefix or {@code null} if the provided
   *         prefix is not well-known
   */
  @Nullable
  public static String getWellKnownUriForPrefix(@NonNull String prefix) {
    return WELL_KNOWN_NAMESPACES.get(prefix);
  }

  private WellKnown() {
    // disable construction
  }
}
