/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

import dev.metaschema.core.model.IModelInstance;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Produces a JSON schema property construction for a Metaschema instance that
 * has {@link IModelInstance#getJsonGroupAsBehavior()} set to
 * {@link JsonGroupAsBehavior#NONE}.
 * <p>
 * This construction allows a single JSON property value.
 */
public final class CardinalityBehaviorSingleton
    extends AbstractCardinalityBehavior {

  @NonNull
  private static final CardinalityBehaviorSingleton SINGLETON = new CardinalityBehaviorSingleton();

  /**
   * Get the singleton instance for this behavior.
   *
   * @return the singleton instance
   */
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
      Collection<? extends IJsonSchemaModelDefinition> types,
      IJsonGenerationState state) {
    generateInternal(node, types, state);
  }
}
