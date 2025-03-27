
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import gov.nist.secauto.metaschema.core.model.IInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractJsonSchemaPropertyNamed<I extends IInstance>
    extends AbstractJsonSchemaProperty<I>
    implements IJsonSchemaPropertyNamed {
  @NonNull
  private final String name;

  protected AbstractJsonSchemaPropertyNamed(@NonNull I instance, @NonNull String name) {
    super(instance);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
