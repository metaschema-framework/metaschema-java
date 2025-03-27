
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IInstance;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

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

  protected void generateMetadata(
      @NonNull ObjectNode obj,
      @NonNull IJsonGenerationState state) {
    // do nothing by default
  }

  protected abstract void generateBody(
      @NonNull ObjectNode obj,
      @NonNull IJsonGenerationState state);
}
