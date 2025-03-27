/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
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
      ObjectNode object,
      IModelInstanceAbsolute instance,
      Collection<? extends IJsonSchemaDefinable> types,
      IJsonGenerationState state) {
    int minOccurs = instance.getMinOccurs();
    int maxOccurs = instance.getMaxOccurs();

    object.put("type", "object");

    if (minOccurs > 0) {
      object.put("minProperties", minOccurs);
    }

    if (maxOccurs != -1) {
      object.put("maxProperties", maxOccurs);
    }

    if (!types.isEmpty()) {
      ObjectNode propertyNames = ObjectUtils.notNull(object.putObject("propertyNames"));
      if (types.size() == 1) {
        generatePropertyName(propertyNames, instance, types.iterator().next(), state);
      } else {
        ArrayNode anyOf = propertyNames.putArray("anyOf");
        for (IJsonSchemaDefinable type : types) {
          generatePropertyName(ObjectUtils.notNull(anyOf.objectNode()), instance, type, state);
        }
      }
    }

    if (!types.isEmpty()) {
      ObjectNode patternProperties = ObjectUtils.notNull(object.putObject("patternProperties"));
      ObjectNode wildcard = ObjectUtils.notNull(patternProperties.putObject("^.*$"));
      generateInternal(wildcard, types, state);
    }
  }

  private void generatePropertyName(
      ObjectNode propertyNames,
      IModelInstanceAbsolute instance,
      IJsonSchemaDefinable type,
      IJsonGenerationState state) {
    // TODO: implement
  }
}
