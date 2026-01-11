/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Provides a means to generate a JSON schema based on a Metaschema assembly
 * definition.
 */
public class JsonSchemaDefinitionAssembly
    extends AbstractJsonSchemaModelDefinition<IAssemblyDefinition>
    implements IJsonSchemaDefinitionAssembly {
  private final Lazy<List<JsonSchemaHelper.Choice>> choices;

  /**
   * Construct a new JSON schema definition based on a Metaschema module
   * definition.
   *
   * @param definition
   *          the Metaschema module definition
   * @param jsonKeyFlagName
   *          the JSON key flag to use with thsi definition or {@code null} if no
   *          JSON key is used
   * @param state
   *          the JSON generation state
   */
  public JsonSchemaDefinitionAssembly(
      @NonNull IAssemblyDefinition definition,
      @Nullable IEnhancedQName jsonKeyFlagName,
      @NonNull IJsonGenerationState state) {
    super(definition, jsonKeyFlagName, state);
    this.choices = Lazy.of(() -> {
      List<IJsonSchemaPropertyFlag> flagProperties = getFlagProperties();
      List<IJsonSchemaPropertyNamed> modelProperties = JsonSchemaHelper.buildModelProperties(getDefinition(), state);

      List<IJsonSchemaPropertyNamed> properties = new ArrayList<>(flagProperties.size() + modelProperties.size());
      properties.addAll(flagProperties);
      properties.addAll(modelProperties);

      JsonSchemaHelper.Choice baseChoice = new JsonSchemaHelper.Choice(properties);
      return JsonSchemaHelper.explodeChoices(baseChoice, getDefinition().getChoiceInstances(), state)
          .collect(Collectors.toUnmodifiableList());
    });
  }

  @Override
  public List<JsonSchemaHelper.Choice> getChoices() {
    return ObjectUtils.notNull(choices.get());
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    Set<IJsonSchemaDefinitionAssembly> myVisited = ObjectUtils.notNull(Stream.concat(
        visited.stream(),
        Stream.of(this))
        .collect(Collectors.toUnmodifiableSet()));

    assert visited.contains(this) || visited.stream()
        .noneMatch(schema -> schema.getDefinition().equals(getDefinition()));

    return ObjectUtils.notNull(visited.contains(this)
        ? Stream.of(this)
        : Stream.concat(
            super.collectDefinitions(myVisited, state),
            choices.get().stream()
                .flatMap(choice -> choice.getCombinations().stream()
                    .flatMap(property -> property.collectDefinitions(myVisited, state)
                        .collect(Collectors.toUnmodifiableList()).stream()))));
  }

  @Override
  public void generateBody(ObjectNode node, IJsonGenerationState state) {
    JsonSchemaHelper.generateAssemblyBody(this, node, state);
  }
}
