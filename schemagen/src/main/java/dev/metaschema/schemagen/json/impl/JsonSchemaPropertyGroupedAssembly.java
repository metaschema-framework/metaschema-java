/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link IAssemblyInstanceGrouped}, which can be generated inline or as a JSON
 * schema definition.
 */
public class JsonSchemaPropertyGroupedAssembly
    extends AbstractJsonSchemaPropertyGrouped<IAssemblyInstanceGrouped>
    implements IJsonSchemaDefinitionAssembly {
  private final Lazy<List<JsonSchemaHelper.Choice>> choices;

  /**
   * Construct a new JSON schema property.
   *
   * @param instance
   *          the instance to construct the property for
   * @param state
   *          the JSON generation state used to get JSON schema information
   */
  public JsonSchemaPropertyGroupedAssembly(
      @NonNull IAssemblyInstanceGrouped instance,
      @NonNull IJsonGenerationState state) {
    super(instance, state);
    this.choices = Lazy.of(() -> {
      List<IJsonSchemaPropertyFlag> flagProperties = getFlagProperties();
      List<IJsonSchemaPropertyNamed> modelProperties
          = JsonSchemaHelper.buildModelProperties(instance.getDefinition(), state);

      List<IJsonSchemaPropertyNamed> properties = new ArrayList<>(flagProperties.size() + modelProperties.size());
      properties.add(new DiscriminatorProperty());
      properties.addAll(flagProperties);
      properties.addAll(modelProperties);

      JsonSchemaHelper.Choice baseChoice = new JsonSchemaHelper.Choice(properties);
      return JsonSchemaHelper.explodeChoices(baseChoice, instance.getDefinition().getChoiceInstances(), state)
          .collect(Collectors.toUnmodifiableList());
    });
  }

  @Override
  public IAssemblyDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  public List<JsonSchemaHelper.Choice> getChoices() {
    return ObjectUtils.notNull(choices.get());
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    Set<IJsonSchemaDefinitionAssembly> myVisited
        = ObjectUtils.notNull(Stream.concat(visited.stream(), Stream.of(this))
            .collect(Collectors.toUnmodifiableSet()));

    assert visited.contains(this) || visited.stream()
        .noneMatch(schema -> schema.getDefinition().equals(getDefinition()));

    return ObjectUtils.notNull(visited.contains(this)
        ? Stream.of(this)
        : Stream.concat(
            super.collectDefinitions(myVisited, state),
            choices.get().stream()
                .flatMap(choice -> choice.getCombinations().stream()
                    .flatMap(property -> property.collectDefinitions(myVisited, state)))));
  }

  @Override
  public void generateBody(ObjectNode node, IJsonGenerationState state) {
    JsonSchemaHelper.generateAssemblyBody(this, node, state);
  }
}
