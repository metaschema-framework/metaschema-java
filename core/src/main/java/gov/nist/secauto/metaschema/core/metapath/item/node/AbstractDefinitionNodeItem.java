
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

abstract class AbstractDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    implements IFeatureOrhpanedDefinitionNodeItem<D, I> {

  @NonNull
  private final D definition;

  public AbstractDefinitionNodeItem(
      @NonNull D definition) {
    this.definition = definition;
  }

  @Override
  public D getDefinition() {
    return definition;
  }
}
