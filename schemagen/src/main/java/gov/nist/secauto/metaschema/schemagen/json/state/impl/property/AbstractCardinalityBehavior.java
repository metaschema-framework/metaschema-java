/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.state.impl.property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractCardinalityBehavior
    implements ICardinalityBehavior {
  private int minOccurrence = IGroupable.DEFAULT_GROUP_AS_MIN_OCCURS;
  private int maxOccurrence = IGroupable.DEFAULT_GROUP_AS_MAX_OCCURS;

  /**
   * Generates the type reference(s).
   *
   * @param object
   *          the parent object node to add properties to
   * @param state
   *          the generation state
   */
  protected void generateInternal(
      @NonNull ObjectNode node,
      @NonNull IModelInstanceAbsolute instance,
      @NonNull Collection<? extends IJsonSchemaDefinable> definitions,
      @NonNull IJsonGenerationState state) {
    assert definitions.size() > 0;

    if (definitions.size() == 1) {
      // build the item type reference
      definitions.iterator().next().generateJsonSchemaOrDefinitionRef(node, state);
    } else if (definitions.size() > 1) {
      // build an anyOf of the item type references
      ArrayNode anyOf = node.putArray("anyOf");
      for (IJsonSchemaDefinable definition : definitions) {
        ObjectNode defNode = anyOf.addObject();
        definition.generateJsonSchemaOrDefinitionRef(defNode, state);
      }
    }
  }
}
