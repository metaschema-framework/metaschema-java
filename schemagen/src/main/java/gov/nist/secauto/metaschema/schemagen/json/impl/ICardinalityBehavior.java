/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ICardinalityBehavior {
  @NonNull
  static ICardinalityBehavior behaviorFor(@NonNull IModelInstanceAbsolute instance) {
    int maxOccurs = instance.getMaxOccurs();

    ICardinalityBehavior retval;
    if (maxOccurs == -1 || maxOccurs > 1) {
      // collection
      switch (instance.getJsonGroupAsBehavior()) {
      case KEYED:
        retval = CardinalityBehaviorKeyedObject.instance();
        break;
      case LIST:
        retval = CardinalityBehaviorArray.instance();
        break;
      case SINGLETON_OR_LIST:
        retval = CardinalityBehaviorSingletonOrList.instance();
        break;
      case NONE:
      default:
        throw new UnsupportedOperationException(
            String.format("Unsupported group-as in-json binding '%s'.", instance.getJsonGroupAsBehavior()));
      }
    } else {
      // singleton
      retval = CardinalityBehaviorSingleton.instance();
    }
    return retval;
  }

  void generate(
      @NonNull ObjectNode object,
      @NonNull IModelInstanceAbsolute instance,
      @NonNull Collection<? extends IJsonSchemaModelDefinition> types,
      @NonNull IJsonGenerationState state);
}
