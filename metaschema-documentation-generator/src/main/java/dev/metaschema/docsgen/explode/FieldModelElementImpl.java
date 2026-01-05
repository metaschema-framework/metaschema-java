package dev.metaschema.docsgen.explode;

import dev.metaschema.model.common.IFieldDefinition;
import dev.metaschema.model.common.IFieldInstance;
import dev.metaschema.model.common.metapath.item.IFieldNodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

class FieldModelElementImpl
    extends AbstractModelElement<IFieldNodeItem>
    implements IFieldModelElement {

  protected FieldModelElementImpl(@NonNull IFieldNodeItem nodeItem) {
    super(nodeItem);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IModelElementVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitField(this, context);
  }

  @Override
  public IFieldDefinition getDefinition() {
    return getNodeItem().getDefinition();
  }

  @Override
  public IFieldInstance getInstance() {
    return getNodeItem().getInstance();
  }
}
