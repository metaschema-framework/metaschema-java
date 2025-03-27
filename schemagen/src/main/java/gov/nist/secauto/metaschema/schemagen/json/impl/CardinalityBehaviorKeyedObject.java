/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class CardinalityBehaviorKeyedObject
    extends AbstractCardinalityBehavior {

  @NonNull
  private static final CardinalityBehaviorKeyedObject SINGLETON = new CardinalityBehaviorKeyedObject();

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
        generatePropertyName(node, instance, types.iterator().next(), state);
      } else {
        ArrayNode anyOf = node.putArray("anyOf");
        for (IJsonSchemaModelDefinition type : types) {
          generatePropertyName(ObjectUtils.notNull(anyOf.objectNode()), instance, type, state);
        }
      }
    }
  }

  private void generatePropertyName(
      ObjectNode node,
      IModelInstanceAbsolute instance,
      IJsonSchemaModelDefinition type,
      IJsonGenerationState state) {

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
