/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.qname;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

public class QNameCache {
  @NonNull
  private static final Lazy<QNameCache> INSTANCE = ObjectUtils.notNull(Lazy.lazy(QNameCache::new));

  @NonNull
  private final NamespaceCache namespaceCache;

  private final Map<Integer, QNameRecord> indexToQName = new ConcurrentHashMap<>();
  private final Map<Integer, Map<String, QNameRecord>> nsIndexToLocalNameToIndex = new ConcurrentHashMap<>();
  /**
   * The next available qualified-name index position.
   */
  private final AtomicInteger indexCounter = new AtomicInteger();

  @NonNull
  public static QNameCache instance() {
    return ObjectUtils.notNull(INSTANCE.get());
  }

  private QNameCache() {
    // disable construction
    this(NamespaceCache.instance());
  }

  public QNameCache(@NonNull NamespaceCache nsCache) {
    this.namespaceCache = nsCache;
  }

  @NonNull
  public NamespaceCache getNamespaceCache() {
    return namespaceCache;
  }

  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  public IEnhancedQName of(@NonNull URI namespace, @NonNull String name) {
    return of(
        ObjectUtils.notNull(namespace.toASCIIString()),
        name);
  }

  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  public IEnhancedQName of(@NonNull QName qname) {
    return of(
        ObjectUtils.notNull(qname.getNamespaceURI()),
        ObjectUtils.notNull(qname.getLocalPart()));
  }

  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  public IEnhancedQName of(@NonNull String namespace, @NonNull String name) {

    int namespacePosition = namespaceCache.of(namespace);

    Map<String, QNameRecord> namespaceNames = nsIndexToLocalNameToIndex
        .computeIfAbsent(namespacePosition, key -> new ConcurrentHashMap<>());

    return ObjectUtils.notNull(namespaceNames.computeIfAbsent(name, key -> {
      assert key != null;
      QNameRecord record = new QNameRecord(namespacePosition, namespace, key);
      indexToQName.put(record.getIndexPosition(), record);
      return record;
    }));
  }

  @Nullable
  public IEnhancedQName get(@NonNull QName qname) {
    return get(
        ObjectUtils.notNull(qname.getNamespaceURI()),
        ObjectUtils.notNull(qname.getLocalPart()));
  }

  @Nullable
  public IEnhancedQName get(@NonNull String namespace, @NonNull String name) {
    Optional<Integer> nsPosition = namespaceCache.get(namespace);
    if (!nsPosition.isPresent()) {
      throw new IllegalArgumentException(
          String.format("The namespace '%s' is not recognized.", namespace));
    }

    Map<String, QNameRecord> namespaceNames = nsIndexToLocalNameToIndex.get(nsPosition.get());
    return namespaceNames == null
        ? null
        : namespaceNames.get(name);
  }

  @Nullable
  public IEnhancedQName get(int index) {
    return indexToQName.get(index);
  }

  private final class QNameRecord implements IEnhancedQName {
    private final int qnameIndexPosition;
    private final int namespaceIndexPosition;
    @NonNull
    private final String namespace;
    @NonNull
    private final String localName;

    public QNameRecord(
        int namespaceIndexPosition,
        @NonNull String namespace,
        @NonNull String localName) {
      this.qnameIndexPosition = indexCounter.getAndIncrement();
      this.namespaceIndexPosition = namespaceIndexPosition;
      this.namespace = namespace;
      this.localName = localName;
    }

    @Override
    public int getIndexPosition() {
      return qnameIndexPosition;
    }

    @Override
    public URI getNamespaceAsUri() {
      return ObjectUtils.notNull(getNamespaceCache().getAsURI(namespaceIndexPosition).get());
    }

    @Override
    public String getNamespace() {
      return namespace;
    }

    @Override
    public String getLocalName() {
      return localName;
    }

    @Override
    public QName toQName(@NonNull String prefix) {
      return new QName(getNamespace(), getLocalName(), prefix);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(qnameIndexPosition);
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      QNameRecord other = (QNameRecord) obj;
      return Objects.equals(qnameIndexPosition, other.getIndexPosition());
    }

    @Override
    public String toString() {
      return toEQName(null);
    }
  }
}
