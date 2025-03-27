
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

public class JsonSchemaDefinitionAssembly
    extends AbstractJsonSchemaModelDefinition<IAssemblyDefinition>
    implements IJsonSchemaDefinitionAssembly {
  private final Lazy<List<JsonSchemaHelper.Choice>> choices;

  public JsonSchemaDefinitionAssembly(
      @NonNull IAssemblyDefinition definition,
      @Nullable IEnhancedQName jsonKeyFlagName,
      @NonNull IJsonGenerationState state) {
    super(definition, jsonKeyFlagName, state);
    this.choices = Lazy.lazy(() -> {
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
    return choices.get();
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    Set<IJsonSchemaDefinition> myVisited
        = Stream.concat(visited.stream(), Stream.of(this))
            .collect(Collectors.toUnmodifiableSet());

    assert visited.contains(this) || visited.stream()
        .noneMatch(schema -> schema.getDefinition().equals(getDefinition()));

    return visited.contains(this)
        ? Stream.of(this)
        : Stream.concat(
            super.collectDefinitions(myVisited, state),
            choices.get().stream()
                .flatMap(choice -> choice.getCombinations().stream()
                    .flatMap(property -> property.collectDefinitions(myVisited, state)
                        .collect(Collectors.toUnmodifiableList()).stream())));
  }

  @Override
  public void generateBody(ObjectNode node, IJsonGenerationState state) {
    JsonSchemaHelper.generateAssemblyBody(this, node, state);
  }
}
