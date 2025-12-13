
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidTypeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metapath node item that is associated with a document.
 */
public interface IDocumentBasedNodeItem extends INodeItem {

  @Override
  default IModelNodeItem<?, ?> getParentContentNodeItem() {
    // there is no parent
    return null;
  }

  /**
   * Get the URI associated with this document.
   *
   * @return the document's URI or {@code null} if unavailable
   */
  @Nullable
  URI getDocumentUri();

  @Override
  default URI getBaseUri() {
    return getDocumentUri();
  }

  @Override
  default INodeItem getParentNodeItem() {
    // there is no parent
    return null;
  }

  @Override
  default IAnyAtomicItem toAtomicItem() {
    throw new InvalidTypeFunctionException(InvalidTypeFunctionException.DATA_ITEM_IS_FUNCTION, this);
  }
}
