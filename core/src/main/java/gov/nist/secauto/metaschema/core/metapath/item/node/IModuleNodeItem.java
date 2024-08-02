
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ModuleScopeEnum;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

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

  /**
   * Get the root items having the provided {@code name}.
   *
   * @param name
   *          the root item's name to retrieve
   * @return a list of matching root items
   */
  // TODO: delete
  default List<? extends IRootAssemblyNodeItem> getRootNodeItemByName(@NonNull QName name) {
    List<? extends IModelNodeItem<?, ?>> result = getModelItemsByName(name);
    return result.stream().flatMap(item -> {
      IRootAssemblyNodeItem retval = null;
      if (item instanceof IRootAssemblyNodeItem) {
        retval = (IRootAssemblyNodeItem) item;
      }

      return retval == null ? null : Stream.of(retval);
    }).collect(Collectors.toUnmodifiableList());
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
