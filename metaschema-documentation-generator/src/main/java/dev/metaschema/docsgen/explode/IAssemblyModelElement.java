
package dev.metaschema.docsgen.explode;

import dev.metaschema.model.common.IAssemblyDefinition;
import dev.metaschema.model.common.IAssemblyInstance;
import dev.metaschema.model.common.metapath.item.IAssemblyNodeItem;

/**
 * A marker interface that identifies a Metaschema construct as part of an assembly definition's model.
 */
public interface IAssemblyModelElement extends IModelElement {
  @Override
  IAssemblyNodeItem getNodeItem();

  @Override
  IAssemblyDefinition getDefinition();

  @Override
  IAssemblyInstance getInstance();
}
