
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.format.IPathFormatter;
import dev.metaschema.core.metapath.function.InvalidTypeFunctionException;
import dev.metaschema.core.metapath.item.ICollectionValue;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.type.IItemType;
import dev.metaschema.core.metapath.type.IKindTest;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstance;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A Metapath node valued item representing a Metaschema module assembly.
 */
public interface IAssemblyNodeItem extends IModelNodeItem<IAssemblyDefinition, IAssemblyInstance> {
  /**
   * Get the static type information of the node item.
   *
   * @return the item type
   */
  @NonNull
  static IItemType type() {
    return IItemType.assembly();
  }

  @Override
  default IKindTest<IAssemblyNodeItem> getType() {
    StaticContext staticContext = getStaticContext();
    return IItemType.assembly(
        getQName(),
        getDefinition().getDefinitionQName().toEQName(staticContext),
        staticContext);
  }

  @Override
  default NodeItemKind getNodeItemKind() {
    return NodeItemKind.ASSEMBLY;
  }

  @Override
  default NodeType getNodeType() {
    return NodeType.ASSEMBLY;
  }

  @Override
  default IAssemblyNodeItem getNodeItem() {
    return this;
  }

  @Override
  @Nullable
  default URI getBaseUri() {
    INodeItem parent = getParentNodeItem();
    return parent == null ? null : parent.getBaseUri();
  }

  @Override
  default String format(@NonNull IPathFormatter formatter) {
    return formatter.formatAssembly(this);
  }

  @Override
  default IAnyAtomicItem toAtomicItem() {
    throw new InvalidTypeFunctionException(InvalidTypeFunctionException.DATA_ITEM_IS_FUNCTION, this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitAssembly(this, context);
  }

  @Override
  default boolean deepEquals(ICollectionValue other, DynamicContext dynamicContext) {
    return other instanceof IAssemblyNodeItem
        && NodeComparators.compareNodeItem(this, (IAssemblyNodeItem) other, dynamicContext);
  }
}
