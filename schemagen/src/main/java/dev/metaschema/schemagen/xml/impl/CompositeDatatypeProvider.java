/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.XMLStreamWriter2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A composite datatype provider that aggregates multiple datatype providers.
 * <p>
 * This class implements the composite pattern, delegating datatype operations
 * to a collection of underlying providers.
 */
public class CompositeDatatypeProvider implements IDatatypeProvider {
  @NonNull
  private final List<IDatatypeProvider> proxiedProviders;

  /**
   * Constructs a new composite datatype provider with the given providers.
   *
   * @param proxiedProviders
   *          the list of providers to aggregate
   */
  public CompositeDatatypeProvider(@NonNull List<IDatatypeProvider> proxiedProviders) {
    this.proxiedProviders = CollectionUtil.unmodifiableList(new ArrayList<>(proxiedProviders));
  }

  /**
   * Retrieves the list of proxied datatype providers.
   *
   * @return an unmodifiable list of the underlying providers
   */
  @NonNull
  protected List<IDatatypeProvider> getProxiedProviders() {
    return proxiedProviders;
  }

  @Override
  public Map<String, IDatatypeContent> getDatatypes() {
    return ObjectUtils.notNull(proxiedProviders.stream()
        .flatMap(provider -> provider.getDatatypes().values().stream())
        .collect(Collectors.toMap(
            IDatatypeContent::getTypeName,
            Function.identity(),
            (e1, e2) -> e2,
            LinkedHashMap::new)));
  }

  @Override
  public Set<String> generateDatatypes(Set<String> requiredTypes, XMLStreamWriter2 writer)
      throws XMLStreamException {
    Set<String> retval = new HashSet<>();

    for (IDatatypeProvider provider : getProxiedProviders()) {
      retval.addAll(provider.generateDatatypes(requiredTypes, writer));
    }
    return retval;
  }

}
