
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonSchemaPropertyGroupedField
    extends AbstractJsonSchemaPropertyGrouped<IFieldInstanceGrouped>
    implements IJsonSchemaDefinitionField {
  private final IDataTypeJsonSchema fieldValueDataType;

  private final List<? extends IJsonSchemaPropertyNamed> nonValueProperties;

  public JsonSchemaPropertyGroupedField(
      @NonNull IFieldInstanceGrouped instance,
      @NonNull IJsonGenerationState state) {
    super(instance, state);
    this.fieldValueDataType = state.getDataTypeSchemaForDefinition(instance.getDefinition());
    this.nonValueProperties = Stream.concat(getFlagProperties().stream(), Stream.of(new DiscriminatorProperty()))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public IFieldDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  public List<? extends IJsonSchemaPropertyNamed> getNonValueProperties() {
    return nonValueProperties;
  }

  @Override
  public IDataTypeJsonSchema getFieldValue() {
    return fieldValueDataType;
  }

  @Override
  public void generateBody(ObjectNode node, IJsonGenerationState state) {
    JsonSchemaHelper.generateFieldBody(this, node, state);
  }
}
