
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ModuleScopeEnum;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports querying of global definitions and associated instances in a
 * Metaschema module by effective name.
 * <p>
 * All definitions in the {@link ModuleScopeEnum#INHERITED} scope. This allows
 * the exported structure of the Metaschema module to be queried.
 */
public interface IModuleNodeItem extends IDocumentBasedNodeItem, IFeatureNoDataValuedItem {

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
  default NodeItemType getNodeItemType() {
    return NodeItemType.METASCHEMA;
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
}
