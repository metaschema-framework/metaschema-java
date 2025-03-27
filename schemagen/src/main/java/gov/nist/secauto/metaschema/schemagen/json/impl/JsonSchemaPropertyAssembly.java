
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public class JsonSchemaPropertyAssembly
    extends AbstractJsonSchemaPropertyNamed<IAssemblyInstanceAbsolute> {
  @NonNull
  private final Lazy<IJsonSchemaModelDefinition> definitionSchema;
  private final IFlagInstance jsonKey;

  public JsonSchemaPropertyAssembly(
      @NonNull IAssemblyInstanceAbsolute instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getJsonName());
    this.jsonKey = instance.getJsonKey();
    this.definitionSchema = Lazy.lazy(() -> state.getAssemblyDefinition(
        instance.getDefinition(),
        jsonKey == null ? null : jsonKey.getQName()));
  }

  @Override
  public boolean isRequired() {
    return getInstance().getMinOccurs() > 0;
  }

  @NonNull
  protected IJsonSchemaModelDefinition getDefinitionSchema() {
    return definitionSchema.get();
  }

  @Override
  protected void generateMetadata(ObjectNode obj, IJsonGenerationState state) {
    IAssemblyInstance instance = getInstance();
    MetadataUtils.generateTitle(instance, obj);
    MetadataUtils.generateDescription(instance, obj);
  }

  @Override
  protected void generateBody(ObjectNode obj, IJsonGenerationState state) {
    ICardinalityBehavior.behaviorFor(getInstance())
        .generate(obj, getInstance(), CollectionUtil.singleton(getDefinitionSchema()), state);
    assert !obj.isEmpty();
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    return getDefinitionSchema().collectDefinitions(visited, state)
        .collect(Collectors.toUnmodifiableList()).stream();
  }
}
