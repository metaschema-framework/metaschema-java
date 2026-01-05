package dev.metaschema.docsgen.explode;

import dev.metaschema.model.common.IFieldDefinition;
import dev.metaschema.model.common.IFieldInstance;
import dev.metaschema.model.common.metapath.item.IFieldNodeItem;

public interface IFieldModelElement extends IModelElement {
  @Override
  IFieldNodeItem getNodeItem();
  
  @Override
  IFieldDefinition getDefinition();
  
  @Override
  IFieldInstance getInstance();
}
