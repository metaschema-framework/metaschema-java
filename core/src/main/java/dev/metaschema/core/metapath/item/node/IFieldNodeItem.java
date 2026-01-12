
package dev.metaschema.core.metapath.item.node;

import java.net.URI;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.format.IPathFormatter;
import dev.metaschema.core.metapath.item.ICollectionValue;
import dev.metaschema.core.metapath.type.IAtomicOrUnionType;
import dev.metaschema.core.metapath.type.IItemType;
import dev.metaschema.core.metapath.type.IKindTest;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstance;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A Metapath node valued item representing a Metaschema module field.
 */
public interface IFieldNodeItem
    extends IModelNodeItem<IFieldDefinition, IFieldInstance>,
    IAtomicValuedNodeItem {
  /**
   * Get the static type information of the node item.
   *
   * @return the item type
   */
  @NonNull
  static IItemType type() {
    return IItemType.field();
  }

  @Override
  default NodeItemKind getNodeItemKind() {
    return NodeItemKind.FIELD;
  }

  @Override
  default NodeType getNodeType() {
    return NodeType.FIELD;
  }

  @Override
  default IFieldNodeItem getNodeItem() {
    return this;
  }

  @Override
  default IKindTest<IFieldNodeItem> getType() {
    StaticContext staticContext = getStaticContext();
    return IItemType.field(
        getQName(),
        getDefinition().getDefinitionQName().toEQName(staticContext),
        staticContext);
  }

  @Override
  default IAtomicOrUnionType<?> getValueItemType() {
    return getDefinition().getJavaTypeAdapter().getItemType();
  }

  @Override
  @Nullable
  default URI getBaseUri() {
    INodeItem parent = getParentNodeItem();
    return parent == null ? null : parent.getBaseUri();
  }

  @Override
  @NonNull
  default String format(@NonNull IPathFormatter formatter) {
    return formatter.formatField(this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitField(this, context);
  }

  @Override
  default boolean deepEquals(ICollectionValue other, DynamicContext dynamicContext) {
    return other instanceof IFieldNodeItem
        && NodeComparators.compareNodeItem(this, (IFieldNodeItem) other, dynamicContext);
  }
}
