/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.qname;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public final class NamespaceCache {
  @NonNull
  private static final Lazy<NamespaceCache> INSTANCE = ObjectUtils.notNull(Lazy.lazy(NamespaceCache::new));

  private final Map<String, Integer> nsToIndex = new ConcurrentHashMap<>();
  private final Map<Integer, String> indexToNs = new ConcurrentHashMap<>();
  private final Map<Integer, URI> indexToNsUri = new ConcurrentHashMap<>();
  /**
   * The next available namespace index position.
   * <p>
   * This value starts at 1, since the "" no namspace has the zero position.
   */
  private final AtomicInteger indexCounter = new AtomicInteger();

  @NonNull
  public static NamespaceCache instance() {
    return ObjectUtils.notNull(INSTANCE.get());
  }

  public NamespaceCache() {
    // claim the "0" position
    int noNamespaceIndex = of("");
    assert noNamespaceIndex == 0;
  }

  // FIXME: check for use and prefer the string version
  @SuppressWarnings("PMD.ShortMethodName")
  public int of(@NonNull URI namespace) {
    return of(ObjectUtils.notNull(namespace.toASCIIString()));
  }

  @SuppressWarnings("PMD.ShortMethodName")
  public int of(@NonNull String namespace) {
    return nsToIndex.computeIfAbsent(namespace, key -> {
      int nextIndex = indexCounter.getAndIncrement();
      indexToNs.put(nextIndex, namespace);
      return nextIndex;
    });
  }

  @NonNull
  public Optional<Integer> get(@NonNull String namespace) {
    return ObjectUtils.notNull(Optional.ofNullable(nsToIndex.get(namespace)));
  }

  @NonNull
  public Optional<String> get(int index) {
    return ObjectUtils.notNull(Optional.ofNullable(indexToNs.get(index)));
  }

  @NonNull
  public Optional<URI> getAsURI(int index) {
    return ObjectUtils.notNull(Optional.ofNullable(indexToNsUri.computeIfAbsent(index, key -> {
      Optional<String> namespace = get(key);
      URI nsUri = null;
      if (namespace.isPresent()) {
        nsUri = URI.create(namespace.get());
        indexToNsUri.put(key, nsUri);
      }
      return nsUri;
    })));
  }
}
