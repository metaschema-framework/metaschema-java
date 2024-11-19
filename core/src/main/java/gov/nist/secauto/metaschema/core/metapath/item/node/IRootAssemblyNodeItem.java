
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A marker interface used to expose root node functionality for an assembly
 * node that has root information.
 */
public interface IRootAssemblyNodeItem extends IAssemblyNodeItem, IFeatureChildNodeItem {

  /**
   * Get the name of this node.
   * <p>
   * This overrides the default behavior using the root name for the assembly.
   */
  @Override
  default IEnhancedQName getQName() {
    return ObjectUtils.requireNonNull(getDefinition().getRootQName());
  }

  /**
   * Get the parent document node item for this root.
   *
   * @return the parent document item
   */
  @NonNull
  IDocumentNodeItem getDocumentNodeItem();

  @Override
  @NonNull
  default IDocumentNodeItem getParentNodeItem() {
    return getDocumentNodeItem();
  }

  @Override
  default IAssemblyNodeItem getParentContentNodeItem() {
    // there is no assembly parent
    return null;
  }

  @Override
  default IAssemblyInstance getInstance() {
    // there is no instance
    return null;
  }

  @Override
  default IRootAssemblyNodeItem getNodeItem() {
    return this;
  }

  @Override
  default int getPosition() {
    // a root is always in the first position
    return 1;
  }

  @Override
  default String format(@NonNull IPathFormatter formatter) {
    return formatter.formatRootAssembly(this);
  }
}
