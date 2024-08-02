
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDocumentNodeItem extends IDocumentBasedNodeItem {
  @Override
  default NodeItemType getNodeItemType() {
    return NodeItemType.DOCUMENT;
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
