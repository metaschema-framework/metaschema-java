/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModelInstance;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Produces a JSON schema property construction for a Metaschema instance that
 * has {@link IModelInstance#getJsonGroupAsBehavior()} set to
 * {@link JsonGroupAsBehavior#KEYED}.
 * <p>
 * This construction uses an object to have "keyed" properties for each value.
 * The key is based on the {@link IFlagInstance} that is identified as the
 * {@link IModelDefinition#getJsonKey()}.
 */
public final class CardinalityBehaviorKeyedObject
    extends AbstractCardinalityBehavior {

  @NonNull
  private static final CardinalityBehaviorKeyedObject SINGLETON = new CardinalityBehaviorKeyedObject();

  /**
   * Get the singleton instance for this behavior.
   *
   * @return the singleton instance
   */
  @NonNull
  public static CardinalityBehaviorKeyedObject instance() {
    return SINGLETON;
  }

  private CardinalityBehaviorKeyedObject() {
    // force use of singleton pattern
  }

  @Override
  public void generate(
      ObjectNode node,
      IModelInstanceAbsolute instance,
      Collection<? extends IJsonSchemaModelDefinition> types,
      IJsonGenerationState state) {
    int minOccurs = instance.getMinOccurs();
    int maxOccurs = instance.getMaxOccurs();

    node.put("type", "object");

    if (minOccurs > 0) {
      node.put("minProperties", minOccurs);
    }

    if (maxOccurs != -1) {
      node.put("maxProperties", maxOccurs);
    }

    if (!types.isEmpty()) {
      if (types.size() == 1) {
        generatePropertyName(node, ObjectUtils.notNull(types.iterator().next()), state);
      } else {
        ArrayNode anyOf = node.putArray("anyOf");
        for (IJsonSchemaModelDefinition type : types) {
          generatePropertyName(ObjectUtils.notNull(anyOf.objectNode()), ObjectUtils.notNull(type), state);
        }
      }
    }
  }

  private void generatePropertyName(
      @NonNull ObjectNode node,
      @NonNull IJsonSchemaModelDefinition type,
      @NonNull IJsonGenerationState state) {

    IFlagInstance flag = type.getJsonKeyFlag();
    if (flag != null) {
      IJsonSchemaDefinition flagSchema = state.getFlagDefinition(flag.getDefinition());
      ObjectNode propertyNames = ObjectUtils.notNull(node.putObject("propertyNames"));
      flagSchema.generateJsonSchemaOrDefinitionRef(propertyNames, state);
    }

    ObjectNode patternProperties = ObjectUtils.notNull(node.putObject("patternProperties"));
    ObjectNode wildcard = ObjectUtils.notNull(patternProperties.putObject("^.*$"));
    generateInternal(wildcard, CollectionUtil.singleton(type), state);
  }
}
