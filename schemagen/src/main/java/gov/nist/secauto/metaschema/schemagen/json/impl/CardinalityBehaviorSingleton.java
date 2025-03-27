/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class CardinalityBehaviorSingleton
    extends AbstractCardinalityBehavior
    implements ICardinalityBehavior {

  @NonNull
  private static final CardinalityBehaviorSingleton SINGLETON = new CardinalityBehaviorSingleton();

  @NonNull
  public static CardinalityBehaviorSingleton instance() {
    return SINGLETON;
  }

  private CardinalityBehaviorSingleton() {
    // force use of singleton pattern
  }

  @Override
  public void generate(
      ObjectNode node,
      IModelInstanceAbsolute instance,
      Collection<? extends IJsonSchemaDefinable> types,
      IJsonGenerationState state) {
    generateInternal(node, types, state);
  }
}
