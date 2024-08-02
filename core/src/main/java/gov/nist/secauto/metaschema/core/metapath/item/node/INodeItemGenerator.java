
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.node.IFeatureFlagContainerItem.FlagContainer;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFeatureModelContainerItem.ModelContainer;

import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface INodeItemGenerator {

  /**
   * Generate the child Metapath node items for the provided item.
   *
   * @param item
   *          the parent item to generate child node items for
   * @return a container that consisting of the child node items
   */
  @NonNull
  Supplier<FlagContainer> newDataModelSupplier(@NonNull IFieldNodeItem item);

  /**
   * Generate the child Metapath node items for the provided item.
   *
   * @param item
   *          the parent item to generate child node items for
   * @return a container that consisting of the child node items
   */
  @NonNull
  Supplier<ModelContainer> newDataModelSupplier(@NonNull IAssemblyNodeItem item);

  /**
   * Generate the child Metapath node items for the provided item.
   *
   * @param item
   *          the parent item to generate child node items for
   * @return a container that consisting of the child node items
   */
  @NonNull
  Supplier<ModelContainer> newDataModelSupplier(@NonNull IRootAssemblyNodeItem item);

  /**
   * Generate the child Metapath node items for the provided item.
   * <p>
   * The provided item is based on a Metaschema module and has no associated
   * value. As a result, the child items will have no associated value.
   *
   * @param item
   *          the parent item to generate child node items for
   * @return a container that consisting of the child node items
   */
  @NonNull
  Supplier<FlagContainer> newMetaschemaModelSupplier(@NonNull IFieldNodeItem item);

  /**
   * Generate the child Metapath node items for the provided item.
   * <p>
   * The provided item is based on a Metaschema module and has no associated
   * value. As a result, the child items will have no associated value.
   *
   * @param item
   *          the parent item to generate child node items for
   * @return a container that consisting of the child node items
   */
  @NonNull
  Supplier<ModelContainer> newMetaschemaModelSupplier(@NonNull IAssemblyNodeItem item);

  /**
   * Generate the child Metapath node items for the provided item.
   * <p>
   * The provided item is a Metaschema module and has no associated value. As a
   * result, the child items will have no associated value.
   *
   * @param item
   *          the parent item to generate child node items for
   * @return a container that consisting of the child node items
   */
  @NonNull
  Supplier<ModelContainer> newMetaschemaModelSupplier(@NonNull IModuleNodeItem item);
}
