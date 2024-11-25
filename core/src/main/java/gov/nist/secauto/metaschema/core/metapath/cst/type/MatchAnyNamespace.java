/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import gov.nist.secauto.metaschema.core.metapath.cst.path.IWildcardMatcher;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A wildcard matcher that matches a specific local name in any namespace.
 */
public class MatchAnyNamespace implements IWildcardMatcher {
  @NonNull
  private final String localName;

  /**
   * Construct the matcher using the provided local name for matching.
   *
   * @param localName
   *          the name used to match nodes
   */
  public MatchAnyNamespace(@NonNull String localName) {
    this.localName = localName;
  }

  @Override
  public boolean test(IDefinitionNodeItem<?, ?> item) {
    return localName.equals(item.getQName().getLocalName());
  }

  @Override
  public String toString() {
    return "*:" + localName;
  }
}
