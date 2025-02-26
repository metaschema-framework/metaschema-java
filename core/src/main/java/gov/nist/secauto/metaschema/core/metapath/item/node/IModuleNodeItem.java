
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.model.IModule;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports querying of global definitions and associated instances in a
 * Metaschema module by effective name.
 * <p>
 * All definitions in the
 * {@link gov.nist.secauto.metaschema.core.model.IDefinition.ModuleScope#PUBLIC}
 * are visible. This allows the exported structure of the Metaschema module to
 * be queried.
 */
public interface IModuleNodeItem extends IDocumentBasedNodeItem, IFeatureNoDataValuedItem {
  /**
   * Get the static type information of the node item.
   *
   * @return the item type
   */
  @NonNull
  static IItemType type() {
    return IItemType.module();
  }

  @Override
  default NodeType getNodeType() {
    return NodeType.MODULE;
  }

  @Override
  default IItemType getType() {
    return type();
  }

  /**
   * The Metaschema module this item is based on.
   *
   * @return the Metaschema module
   */
  @NonNull
  IModule getModule();

  @Override
  default URI getDocumentUri() {
    return getModule().getLocation();
  }

  @Override
  default NodeItemKind getNodeItemKind() {
    return NodeItemKind.METASCHEMA;
  }

  @Override
  default IModuleNodeItem getNodeItem() {
    return this;
  }

  @Override
  default String format(@NonNull IPathFormatter formatter) {
    return formatter.formatMetaschema(this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitMetaschema(this, context);
  }

  @Override
  default StaticContext getStaticContext() {
    return getModule().getModuleStaticContext();
  }

  @Override
  default boolean deepEquals(ICollectionValue other, DynamicContext dynamicContext) {
    return other instanceof IModuleNodeItem
        && NodeComparators.compareNodeItem(this, (IModuleNodeItem) other, dynamicContext);
  }
}
