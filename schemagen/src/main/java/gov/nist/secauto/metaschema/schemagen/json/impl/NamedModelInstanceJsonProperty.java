/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.impl.builder.IModelInstanceBuilder;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public class NamedModelInstanceJsonProperty
    extends AbstractNamedInstanceJsonProperty<INamedModelInstanceAbsolute>
    implements IGroupableModelInstanceJsonProperty<INamedModelInstanceAbsolute> {

  private final IModelInstanceBuilder<?> collectionBuilder;

  public NamedModelInstanceJsonProperty(
      @NonNull INamedModelInstanceAbsolute instance,
      @NonNull IJsonGenerationState state) {
    super(instance);
    this.collectionBuilder = IModelInstanceBuilder.newCollectionBuilder(instance);
    this.collectionBuilder.addItemType(instance);

    // // register definition
    // // REFACTOR: handle discriminator?
    // state.getSchema(IKey.of(instance.getDefinition(),
    // instance.getJsonKeyFlagName(), null, null));
  }

  protected IModelInstanceBuilder<?> getCollectionBuilder() {
    return collectionBuilder;
  }

  // @Override
  // public void gatherDefinitions(SortedSet<IDefineableJsonSchema> schemaMap,
  // IJsonGenerationState state) {
  // for (IModelInstanceBuilder.IType type : collectionBuilder.getTypes()) {
  // IJsonSchema schema = type.getJsonSchema(state);
  // schema.gatherDefinitions(schemaMap, state);
  // }
  // }

  @Override
  protected void generateBody(
      ObjectNode obj,
      IJsonGenerationState state) {
    getCollectionBuilder().build(
        obj,
        state);
  }

  @Override
  protected void generateMetadata(ObjectNode obj) {
    INamedModelInstanceAbsolute instance = getInstance();
    MetadataUtils.generateTitle(instance, obj);
    MetadataUtils.generateDescription(instance, obj);
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
