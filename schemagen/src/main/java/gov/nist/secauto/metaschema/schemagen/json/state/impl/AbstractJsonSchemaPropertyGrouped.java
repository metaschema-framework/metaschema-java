
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.IGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public abstract class AbstractJsonSchemaPropertyGrouped<I extends INamedModelInstanceGrouped>
    extends AbstractJsonSchemaProperty<I>
    implements IJsonSchemaPropertyGrouped {
  @NonNull
  private final Lazy<String> definitionName;
  @NonNull
  private final List<IJsonSchemaPropertyFlag> flagProperties;
  private final IFlagInstance jsonKey;

  protected AbstractJsonSchemaPropertyGrouped(@NonNull I instance, @NonNull IJsonGenerationState state) {
    super(instance);
    this.definitionName = Lazy.lazy(() -> getDefinitionName(state));
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
    return definitionName.get();
  }

  private String getDefinitionName(IJsonGenerationState state) {
    INamedModelInstanceGrouped instance = getInstance();
    IModelDefinition definition = instance.getDefinition();

    String discriminatorProperty = instance.getParentContainer().getJsonDiscriminatorProperty();
    String discriminatorValue = instance.getEffectiveDisciminatorValue();

    StringBuilder builder = new StringBuilder();
    IFlagInstance jsonKey = getJsonKeyFlag();
    if (jsonKey != null) {
      builder
          .append(IGenerationState.toCamelCase(jsonKey.getEffectiveName()))
          .append("JsonKey");
    }

    builder
        .append(IGenerationState.toCamelCase(ObjectUtils.requireNonNull(discriminatorProperty)))
        .append(IGenerationState.toCamelCase(ObjectUtils.requireNonNull(discriminatorValue)))
        .append("Choice");
    return state.getTypeNameForDefinition(
        definition,
        builder.toString());
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    return Stream.concat(
        Stream.of(this),
        getFlagProperties().stream()
            .flatMap(property -> property.collectDefinitions(visited, state)));
  }

  protected class DiscriminatorProperty implements IJsonSchemaPropertyNamed {
    @Override
    public Stream<IJsonSchemaDefinable> collectDefinitions(Set<IJsonSchemaDefinition> visited,
        IJsonGenerationState state) {
      return Stream.empty();
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
