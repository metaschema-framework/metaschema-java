
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDocumentNodeItem extends IDocumentBasedNodeItem {
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
}
