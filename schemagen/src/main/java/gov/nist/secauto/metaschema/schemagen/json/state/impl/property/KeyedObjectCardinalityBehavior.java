/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.state.impl.property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public class KeyedObjectCardinalityBehavior
    extends AbstractCardinalityBehavior {

  private static final KeyedObjectCardinalityBehavior SINGLETON = new KeyedObjectCardinalityBehavior();

  @NonNull
  public static KeyedObjectCardinalityBehavior instance() {
    return SINGLETON;
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
      generateInternal(wildcard, instance, types, state);
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
