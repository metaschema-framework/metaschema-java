
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class JsonSchemaDefinitionField
    extends AbstractJsonSchemaModelDefinition<IFieldDefinition>
    implements IJsonSchemaDefinitionField {
  private final IDataTypeJsonSchema fieldValueDataType;

  public JsonSchemaDefinitionField(
      @NonNull IFieldDefinition definition,
      @Nullable IEnhancedQName jsonKeyFlagName,
      @NonNull IJsonGenerationState state) {
    super(definition, jsonKeyFlagName, state);
    this.fieldValueDataType = state.getDataTypeSchemaForDefinition(getDefinition());
  }

  @Override
  public List<? extends IJsonSchemaPropertyNamed> getNonValueProperties() {
    return getFlagProperties();
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
