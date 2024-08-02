/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IModelInstanceBuilder<T extends IModelInstanceBuilder<T>> extends IBuilder<T> {
  @NonNull
  T addItemType(@NonNull INamedModelInstanceGrouped itemType);

  @NonNull
  T addItemType(@NonNull INamedModelInstanceAbsolute itemType);

  @NonNull
  T minItems(int min);

  @NonNull
  T maxItems(int max);

  @NonNull
  List<IType> getTypes();

  int getMinOccurrence();

  int getMaxOccurrence();

  interface IType {
    @Nullable
    IDefineableJsonSchema getJsonKeyFlagSchema(@NonNull IJsonGenerationState state);

    @Nullable
    IDataTypeJsonSchema getJsonKeyDataTypeSchema(@NonNull IJsonGenerationState state);

    @NonNull
    IDefinitionJsonSchema<IModelDefinition> getJsonSchema(@NonNull IJsonGenerationState state);

    void build(
        @NonNull ArrayNode anyOf,
        @NonNull IJsonGenerationState state);

    void build(
        @NonNull ObjectNode object,
        @NonNull IJsonGenerationState state);

    default void gatherDefinitions(
        @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
        @NonNull IJsonGenerationState state) {
      IDefinitionJsonSchema<IModelDefinition> schema = getJsonSchema(state);
      schema.gatherDefinitions(gatheredDefinitions, state);
    }
  }

  @NonNull
  static <I extends IModelInstance & IGroupable> IModelInstanceBuilder<?> builder(@NonNull I instance) {
    IModelInstanceBuilder<?> builder;

    if (instance instanceof INamedModelInstanceAbsolute) {
      INamedModelInstanceAbsolute named = (INamedModelInstanceAbsolute) instance;
      builder = newCollectionBuilder(named);
      builder.addItemType(named);
    } else if (instance instanceof IChoiceGroupInstance) {
      IChoiceGroupInstance choice = (IChoiceGroupInstance) instance;
      builder = newCollectionBuilder(choice);
      for (INamedModelInstanceGrouped groupedInstance : choice.getNamedModelInstances()) {
        assert groupedInstance != null;
        builder.addItemType(groupedInstance);
      }
    } else {
      throw new UnsupportedOperationException(
          "Unsupported named model instance type: " + instance.getClass().getName());
    }
    return builder;
  }

  @NonNull
  static IModelInstanceBuilder<?> newCollectionBuilder(@NonNull IGroupable groupable) {
    JsonGroupAsBehavior behavior = groupable.getJsonGroupAsBehavior();
    IModelInstanceBuilder<?> retval;
    switch (behavior) {
    case LIST:
      retval = new ArrayBuilder();
      break;
    case SINGLETON_OR_LIST:
      retval = new SingletonOrListBuilder();
      break;
    case KEYED:
      retval = new KeyedObjectBuilder();
      break;
    case NONE:
      retval = new SingletonBuilder();
      break;
    default:
      throw new UnsupportedOperationException(
          String.format("Unsupported group-as in-json binding '%s'.", behavior));
    }

    retval.minItems(groupable.getMinOccurs());
    retval.maxItems(groupable.getMaxOccurs());
    return retval;
  }
}
