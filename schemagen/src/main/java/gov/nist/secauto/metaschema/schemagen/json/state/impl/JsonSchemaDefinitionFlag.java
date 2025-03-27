
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A JSON schema for a given Metaschema {@link IFlagDefinition}, which is part
 * of a larger JSON schema.
 */
public class JsonSchemaDefinitionFlag
    extends AbstractJsonSchemaDefinition<IFlagDefinition> {

  /**
   * Construct a new JSON schema definition based on a Metaschema module
   * definition.
   * 
   * @param definition
   *          the Metaschema module definition
   */
  public JsonSchemaDefinitionFlag(
      @NonNull IFlagDefinition definition,
      @NonNull IJsonGenerationState state) {
    super(definition, state);
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    return Stream.of(this);
  }

  @Override
  public String generateDefinitionName(IJsonGenerationState state) {
    return state.getTypeNameForDefinition(getDefinition(), null);
  }

  @Override
  public void generateBody(ObjectNode node, IJsonGenerationState state) {
    IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(getDefinition());
    schema.generateSchemaOrRef(node, state);
  }
}
