
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonSchemaPropertyChoiceGroup
    extends AbstractJsonSchemaPropertyNamed<IChoiceGroupInstance> {

  private final List<IJsonSchemaPropertyGrouped> choiceInstances;

  public JsonSchemaPropertyChoiceGroup(
      @NonNull IChoiceGroupInstance instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getGroupAsName() == null ? "[unknown]" : ObjectUtils.notNull(instance.getGroupAsName()));
    this.choiceInstances = instance.getNamedModelInstances().stream()
        .map(state::getJsonSchemaPropertyGrouped)
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public boolean isRequired() {
    return getInstance().getMinOccurs() > 0;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    return choiceInstances.stream()
        .flatMap(choice -> choice.collectDefinitions(visited, state));
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
