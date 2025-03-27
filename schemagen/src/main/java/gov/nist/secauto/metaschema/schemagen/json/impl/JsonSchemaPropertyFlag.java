
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonSchemaPropertyFlag
    extends AbstractJsonSchemaPropertyNamed<IFlagInstance>
    implements IJsonSchemaPropertyFlag {
  private final IJsonSchemaDefinition definitionSchema;

  public JsonSchemaPropertyFlag(
      @NonNull IFlagInstance instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getJsonName());
    this.definitionSchema = state.getFlagDefinition(instance.getDefinition());
  }

  @Override
  public String getName() {
    return getInstance().getJsonName();
  }

  @Override
  public boolean isRequired() {
    return getInstance().isRequired();
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    return definitionSchema.collectDefinitions(visited, state);
  }

  @Override
  protected void generateMetadata(ObjectNode obj, IJsonGenerationState state) {
    IFlagInstance instance = getInstance();
    MetadataUtils.generateTitle(instance, obj);
    MetadataUtils.generateDescription(instance, obj);
    MetadataUtils.generateDefault(instance, obj);
  }

  @Override
  protected void generateBody(ObjectNode obj, IJsonGenerationState state) {
    definitionSchema.generateJsonSchemaOrDefinitionRef(obj, state);
    assert !obj.isEmpty();
  }
}
