package dev.metaschema.docsgen.explode;

import dev.metaschema.model.common.IFlagDefinition;
import dev.metaschema.model.common.IFlagInstance;
import dev.metaschema.model.common.metapath.item.IFlagNodeItem;

public interface IFlagModelElement extends IModelElement {
  @Override
  IFlagNodeItem getNodeItem();
  
  @Override
  IFlagDefinition getDefinition();
  
  @Override
  IFlagInstance getInstance();
}
