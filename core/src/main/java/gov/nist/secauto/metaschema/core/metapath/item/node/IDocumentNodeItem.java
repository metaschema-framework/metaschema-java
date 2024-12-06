
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A node item that represents the root of a tree of nodes associated with a
 * document resource.
 */
public interface IDocumentNodeItem extends IDocumentBasedNodeItem {
  /**
   * The node item's type.
   *
   * @return the type
   */
  @NonNull
  static IItemType type() {
    return IItemType.document();
  }

  @Override
  default IKindTest<IDocumentNodeItem> getType() {
    return IItemType.document(
        getRootAssemblyNodeItem().getType());
  }

  @Override
  default NodeItemKind getNodeItemKind() {
    return NodeItemKind.DOCUMENT;
  }

  @Override
  default NodeType getNodeType() {
    return NodeType.DOCUMENT;
  }

  @Override
  default IDocumentNodeItem getNodeItem() {
    return this;
  }

  /**
   * Get the node item for the document root element.
   *
   * @return the root node item
   */
  @NonNull
  IRootAssemblyNodeItem getRootAssemblyNodeItem();

  @Override
  default String format(@NonNull IPathFormatter formatter) {
    return formatter.formatDocument(this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitDocument(this, context);
  }

  @Override
  default boolean deepEquals(ICollectionValue other) {
    return other instanceof IDocumentNodeItem
        && NodeComparators.compareNodeItem(this, (IDocumentNodeItem) other) == 0;
  }
}
