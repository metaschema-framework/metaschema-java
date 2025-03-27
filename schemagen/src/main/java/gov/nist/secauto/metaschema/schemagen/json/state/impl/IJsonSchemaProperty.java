
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonSchemaProperty {
  @NonNull
  Stream<IJsonSchemaDefinable> collectDefinitions(
      @NonNull Set<IJsonSchemaDefinition> visited,
      @NonNull IJsonGenerationState state);

  /**
   * Generate the property contents.
   * 
   * @param node
   *          the property JSON object
   * @param state
   *          the schema generation state used for context
   */
  void generate(@NonNull ObjectNode node, @NonNull IJsonGenerationState state);
}
