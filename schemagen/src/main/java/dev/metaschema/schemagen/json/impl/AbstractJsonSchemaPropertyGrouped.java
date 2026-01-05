/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * A JSON schema for a given Metaschema-based definition instance, that is part
 * of a choice group, which is part of a larger JSON schema.
 *
 * @param <I>
 *          the Java type of the Metaschema definition instance
 */
public abstract class AbstractJsonSchemaPropertyGrouped<I extends INamedModelInstanceGrouped>
    extends AbstractJsonSchemaProperty<I>
    implements IJsonSchemaPropertyGrouped {
  @NonNull
  private final Lazy<String> definitionName;
  @NonNull
  private final List<IJsonSchemaPropertyFlag> flagProperties;
  private final IFlagInstance jsonKey;

  /**
   * Construct a new JSON schema property based on a Metaschema definition
   * instance.
   *
   * @param instance
   *          the Metaschema definition instance
   * @param state
   *          the generation state used to generate this JSON schema
   */
  protected AbstractJsonSchemaPropertyGrouped(@NonNull I instance, @NonNull IJsonGenerationState state) {
    super(instance);
    this.definitionName = ObjectUtils.notNull(Lazy.of(() -> getDefinitionName(state)));
    this.jsonKey = instance.getJsonKey();

    IEnhancedQName jsonKeyName = this.jsonKey == null ? null : this.jsonKey.getQName();
    this.flagProperties
        = JsonSchemaHelper.buildFlagProperties(instance.getDefinition(), jsonKeyName, state);
  }

  @Override
  public final List<IJsonSchemaPropertyFlag> getFlagProperties() {
    return flagProperties;
  }

  @Override
  public IFlagInstance getJsonKeyFlag() {
    return jsonKey;
  }

  @Override
  public String getDefinitionName() {
    return ObjectUtils.notNull(definitionName.get());
  }

  private String getDefinitionName(IJsonGenerationState state) {
    INamedModelInstanceGrouped instance = getInstance();
    IModelDefinition definition = instance.getDefinition();

    String discriminatorProperty = instance.getParentContainer().getJsonDiscriminatorProperty();
    String discriminatorValue = instance.getEffectiveDisciminatorValue();

    IFlagInstance jsonKey = getJsonKeyFlag();

    return state.generateJsonSchemaDefinitionName(
        definition,
        jsonKey == null ? null : jsonKey.getEffectiveName(),
        ObjectUtils.requireNonNull(discriminatorProperty),
        ObjectUtils.requireNonNull(discriminatorValue),
        "Choice");
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    Stream<IJsonSchemaDefinable> retval = Stream.concat(
        Stream.of(this),
        getFlagProperties().stream()
            .flatMap(property -> property.collectDefinitions(visited, state)));

    IFlagInstance jsonKeyFlag = getJsonKeyFlag();
    if (jsonKeyFlag != null) {
      retval = Stream.concat(retval, Stream.of(state.getFlagDefinition(jsonKeyFlag.getDefinition())));
    }
    return ObjectUtils.notNull(retval);

  }

  /**
   * Represents a JSON schema property used to declare the type of a choice group
   * object.
   */
  protected class DiscriminatorProperty implements IJsonSchemaPropertyNamed {
    @Override
    public Stream<IJsonSchemaDefinable> collectDefinitions(Set<IJsonSchemaDefinitionAssembly> visited,
        IJsonGenerationState state) {
      return ObjectUtils.notNull(Stream.empty());
    }

    @Override
    public void generate(ObjectNode node, IJsonGenerationState state) {
      node.put("const", getInstance().getEffectiveDisciminatorValue());
    }

    @Override
    public String getName() {
      return getInstance().getParentContainer().getJsonDiscriminatorProperty();
    }

    @Override
    public boolean isRequired() {
      return true;
    }
  }
}
