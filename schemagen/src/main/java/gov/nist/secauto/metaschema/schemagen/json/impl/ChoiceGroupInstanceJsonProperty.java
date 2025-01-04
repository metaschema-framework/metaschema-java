/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.impl.builder.IModelInstanceBuilder;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ChoiceGroupInstanceJsonProperty
    extends AbstractJsonProperty<IChoiceGroupInstance>
    implements IGroupableModelInstanceJsonProperty<IChoiceGroupInstance> {

  private final IModelInstanceBuilder<?> collectionBuilder;

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public ChoiceGroupInstanceJsonProperty(@NonNull IChoiceGroupInstance instance) {
    super(instance);
    this.collectionBuilder = IModelInstanceBuilder.newCollectionBuilder(instance);

    for (INamedModelInstanceGrouped groupedInstance : instance.getNamedModelInstances()) {
      assert groupedInstance != null;
      this.collectionBuilder.addItemType(groupedInstance);
    }
  }

  protected IModelInstanceBuilder<?> getCollectionBuilder() {
    return collectionBuilder;
  }

  @Override
  public String getName() {
    return ObjectUtils.requireNonNull(getInstance().getGroupAsName());
  }

  @Override
  protected void generateBody(
      ObjectNode obj,
      IJsonGenerationState state) {
    getCollectionBuilder().build(obj, state);
  }

  @Override
  public void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state) {
    for (IModelInstanceBuilder.IType type : collectionBuilder.getTypes()) {
      type.gatherDefinitions(gatheredDefinitions, state);
    }
  }
}
