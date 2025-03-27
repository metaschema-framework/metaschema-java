
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.impl.MetadataUtils;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.property.ICardinalityBehavior;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonSchemaPropertyField
    extends AbstractJsonSchemaPropertyNamed<IFieldInstanceAbsolute> {
  @NonNull
  private final IJsonSchemaModelDefinition definitionSchema;
  private final IFlagInstance jsonKey;

  public JsonSchemaPropertyField(
      @NonNull IFieldInstanceAbsolute instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getJsonName());
    this.jsonKey = instance.getJsonKey();
    this.definitionSchema = state.getFieldDefinition(
        instance.getDefinition(),
        jsonKey == null ? null : jsonKey.getQName());
  }

  @Override
  public boolean isRequired() {
    return getInstance().getMinOccurs() > 0;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    return definitionSchema.collectDefinitions(visited, state);
  }

  @Override
  protected void generateMetadata(ObjectNode obj, IJsonGenerationState state) {
    IFieldInstance instance = getInstance();
    MetadataUtils.generateTitle(instance, obj);
    MetadataUtils.generateDescription(instance, obj);
    // TODO: handle complex case
    MetadataUtils.generateDefault(instance, obj);
  }

  @Override
  protected void generateBody(ObjectNode obj, IJsonGenerationState state) {
    ICardinalityBehavior.behaviorFor(getInstance())
        .generate(obj, getInstance(), CollectionUtil.singleton(definitionSchema), state);
    assert !obj.isEmpty();
  }
}
