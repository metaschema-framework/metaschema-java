/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.List;

public class KeyedObjectBuilder
    extends AbstractCollectionBuilder<KeyedObjectBuilder> {

  @Override
  protected KeyedObjectBuilder thisBuilder() {
    return this;
  }

  @Override
  public void build(
      ObjectNode object,
      IJsonGenerationState state) {
    object.put("type", "object");

    if (getMinOccurrence() > 0) {
      object.put("minProperties", getMinOccurrence());
    }

    if (getMaxOccurrence() != -1) {
      object.put("maxProperties", getMaxOccurrence());
    }

    List<IType> types = getTypes();

    if (!types.isEmpty()) {
      ObjectNode propertyNames = ObjectUtils.notNull(object.putObject("propertyNames"));
      if (types.size() == 1) {
        types.iterator().next().build(propertyNames, state);
      } else {
        ArrayNode anyOf = propertyNames.putArray("anyOf");
        for (IType type : types) {
          type.build(ObjectUtils.notNull(anyOf.objectNode()), state);
        }
      }
    }

    if (!types.isEmpty()) {
      ObjectNode patternProperties = ObjectUtils.notNull(object.putObject("patternProperties"));
      ObjectNode wildcard = patternProperties.putObject("^.*$");
      if (types.size() == 1) {
        types.iterator().next().build(wildcard, state);
      } else {
        ArrayNode oneOf = wildcard.putArray("anyOf");
        for (IType type : types) {
          type.build(oneOf, state);
        }
      }
    }
  }

  @Override
  public KeyedObjectBuilder addItemType(INamedModelInstanceAbsolute itemType) {

    IModelDefinition definition = itemType.getDefinition();

    int flagCount = definition.getFlagInstances().size();
    if (definition.getJsonKey() != null) {
      --flagCount;
    }

    if (flagCount > 0 || itemType instanceof IFieldInstance) {
      super.addItemType(itemType);
    }
    return this;
  }
}
