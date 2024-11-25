/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.type.MatchAnyLocalName;
import gov.nist.secauto.metaschema.core.metapath.cst.type.MatchAnyNamespace;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;

import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IWildcardMatcher extends Predicate<IDefinitionNodeItem<?, ?>> {
  @NonNull
  static IWildcardMatcher anyNamespace(@NonNull String localName) {
    return new MatchAnyNamespace(localName);
  }

  @NonNull
  static IWildcardMatcher anyLocalName(@NonNull String namespace) {
    return new MatchAnyLocalName(namespace);
  }

  @Override
  @NonNull
  String toString();
}
