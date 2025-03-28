/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports production of a JSON schema property construction for a Metaschema
 * instance that has {@link IModelInstance#getJsonGroupAsBehavior()}.
 */
public abstract class AbstractCardinalityBehavior
    implements ICardinalityBehavior {
  /**
   * Generates the type reference(s).
   *
   * @param node
   *          the parent JSON object to add properties to
   * @param definitions
   *          the JSON schemas to use to describe the contained data
   * @param state
   *          the generation state
   */
  protected void generateInternal(
      @NonNull ObjectNode node,
      @NonNull Collection<? extends IJsonSchemaDefinable> definitions,
      @NonNull IJsonGenerationState state) {
    assert !definitions.isEmpty();

    if (definitions.size() == 1) {
      // build the item type reference
      definitions.iterator().next().generateJsonSchemaOrDefinitionRef(node, state);
    } else if (definitions.size() > 1) {
      // build an anyOf of the item type references
      ArrayNode anyOf = node.putArray("anyOf");
      for (IJsonSchemaDefinable definition : definitions) {
        ObjectNode defNode = ObjectUtils.notNull(anyOf.addObject());
        definition.generateJsonSchemaOrDefinitionRef(defNode, state);
      }
    }
  }
}
