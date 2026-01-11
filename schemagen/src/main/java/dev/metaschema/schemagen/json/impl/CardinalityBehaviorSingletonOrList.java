/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

import dev.metaschema.core.model.IModelInstance;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Produces a JSON schema property construction for a Metaschema instance that
 * has {@link IModelInstance#getJsonGroupAsBehavior()} set to
 * {@link JsonGroupAsBehavior#SINGLETON_OR_LIST}.
 * <p>
 * This construction allows a single JSON property value or an array of two or
 * more property values.
 */
public final class CardinalityBehaviorSingletonOrList
    extends AbstractCardinalityBehavior {
  @NonNull
  private static final CardinalityBehaviorSingletonOrList SINGLETON = new CardinalityBehaviorSingletonOrList();

  /**
   * Get the singleton instance for this behavior.
   *
   * @return the singleton instance
   */
  @NonNull
  public static CardinalityBehaviorSingletonOrList instance() {
    return SINGLETON;
  }

  private CardinalityBehaviorSingletonOrList() {
    // force use of singleton pattern
  }

  @Override
  public void generate(
      ObjectNode node,
      IModelInstanceAbsolute instance,
      Collection<? extends IJsonSchemaModelDefinition> types,
      IJsonGenerationState state) {
    ArrayNode oneOf = node.putArray("oneOf");

    generateInternal(ObjectUtils.notNull(oneOf.addObject()), types, state);

    generateArray(ObjectUtils.notNull(oneOf.addObject()), instance, types, state);
  }

  private void generateArray(
      @NonNull ObjectNode node,
      @NonNull IModelInstanceAbsolute instance,
      @NonNull Collection<? extends IJsonSchemaDefinable> types,
      @NonNull IJsonGenerationState state) {
    int minOccurs = instance.getMinOccurs();
    int maxOccurs = instance.getMaxOccurs();

    assert minOccurs >= 0;
    assert maxOccurs == -1 || maxOccurs >= 1;

    node.put("type", "array");

    ObjectNode items = ObjectUtils.notNull(node.putObject("items"));
    generateInternal(items, types, state);
    assert !items.isEmpty();

    node.put("minItems", Math.max(2, minOccurs));

    if (maxOccurs != -1) {
      node.put("maxItems", maxOccurs);
    }
  }
}
