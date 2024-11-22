
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This mixin interface indicates that the implementation is a {@link INodeItem}
 * that may have both flag and model children.
 */
public interface IFeatureModelContainerItem extends IFeatureFlagContainerItem {

  @Override
  ModelContainer getModel();

  @Override
  default Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    return getModel().getModelItems();
  }

  @Override
  default List<? extends IModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    return getModel().getModelItemsByName(name);
  }

  /**
   * Provides an abstract implementation of a lazy loaded model.
   */
  class ModelContainer
      extends FlagContainer {
    private final Map<Integer, List<? extends IModelNodeItem<?, ?>>> modelItems;

    /**
     * Creates a new collection of flags and model items.
     *
     * @param flags
     *          a mapping of flag name to a flag item
     * @param modelItems
     *          a mapping of model item name to a list of model items
     */
    protected ModelContainer(
        @NonNull Map<Integer, IFlagNodeItem> flags,
        @NonNull Map<Integer, List<? extends IModelNodeItem<?, ?>>> modelItems) {
      super(flags);
      this.modelItems = modelItems;
    }

    /**
     * Get the matching list of model items having the provided name.
     *
     * @param name
     *          the name of the model items to retrieve
     * @return a lisy of matching model items or {@code null} if no match was found
     */
    @NonNull
    public List<? extends IModelNodeItem<?, ?>> getModelItemsByName(@NonNull IEnhancedQName name) {
      List<? extends IModelNodeItem<?, ?>> result = modelItems.get(name.getIndexPosition());
      return result == null ? CollectionUtil.emptyList() : result;
    }

    /**
     * Get all model items grouped by model item name.
     *
     * @return a collection of lists containg model items grouped by names
     */
    @SuppressWarnings("null")
    @NonNull
    public Collection<List<? extends IModelNodeItem<?, ?>>> getModelItems() {
      return modelItems.values();
    }
  }
}
