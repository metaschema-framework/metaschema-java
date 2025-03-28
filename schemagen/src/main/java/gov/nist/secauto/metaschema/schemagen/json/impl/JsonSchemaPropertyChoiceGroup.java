/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link IChoiceGroupInstance}, which can be generated inline or as a JSON
 * schema definition.
 */
public class JsonSchemaPropertyChoiceGroup
    extends AbstractJsonSchemaPropertyNamed<IChoiceGroupInstance> {
  @NonNull
  private final List<IJsonSchemaPropertyGrouped> choiceInstances;

  /**
   * Construct a new JSON schema property.
   *
   * @param instance
   *          the instance to construct the property for
   * @param state
   *          the JSON generation state used to get JSON schema information
   */
  public JsonSchemaPropertyChoiceGroup(
      @NonNull IChoiceGroupInstance instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getGroupAsName() == null ? "[unknown]" : ObjectUtils.notNull(instance.getGroupAsName()));
    this.choiceInstances = ObjectUtils.notNull(instance.getNamedModelInstances().stream()
        .map(state::getJsonSchemaPropertyGrouped)
        .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  public boolean isRequired() {
    return getInstance().getMinOccurs() > 0;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    return ObjectUtils.notNull(choiceInstances.stream()
        .flatMap(choice -> choice.collectDefinitions(visited, state)));
  }

  @Override
  protected void generateMetadata(ObjectNode obj, IJsonGenerationState state) {
    // do nothing
  }

  @Override
  protected void generateBody(ObjectNode obj, IJsonGenerationState state) {
    ICardinalityBehavior.behaviorFor(getInstance())
        .generate(obj, getInstance(), choiceInstances, state);
    assert !obj.isEmpty();
  }
}
