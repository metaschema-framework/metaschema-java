
package dev.metaschema.docsgen.explode;

import dev.metaschema.model.common.IFlagDefinition;
import dev.metaschema.model.common.IFlagInstance;
import dev.metaschema.model.common.metapath.item.IFlagNodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

class FlagModelElementImpl
    extends AbstractModelElement<IFlagNodeItem>
    implements IFlagModelElement {

  protected FlagModelElementImpl(@NonNull IFlagNodeItem nodeItem) {
    super(nodeItem);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IModelElementVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFlag(this, context);
  }

  @Override
  public IFlagDefinition getDefinition() {
    return getNodeItem().getDefinition();
  }

  @Override
  public IFlagInstance getInstance() {
    return getNodeItem().getInstance();
  }
}
