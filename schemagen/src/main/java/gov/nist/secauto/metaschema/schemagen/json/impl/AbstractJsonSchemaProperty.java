
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractJsonSchemaProperty<I extends IInstance>
    implements IJsonSchemaProperty {
  @NonNull
  private final I instance;

  protected AbstractJsonSchemaProperty(@NonNull I instance) {
    this.instance = instance;
  }

  @NonNull
  public I getInstance() {
    return instance;
  }

  @Override
  public void generate(@NonNull ObjectNode node, @NonNull IJsonGenerationState state) {
    generateMetadata(node, state);

    generateBody(node, state);
    assert !node.isEmpty();
  }

  /**
   * Generate human-focused documentation and other metadata.
   * 
   * @param node
   *          the property JSON object
   * @param state
   *          the schema generation state used for context
   */
  protected void generateMetadata(
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state) {
    // do nothing by default
  }

  /**
   * Generate the JSON schema body.
   * 
   * @param node
   *          the property JSON object
   * @param state
   *          the schema generation state used for context
   */
  protected abstract void generateBody(
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state);
}
