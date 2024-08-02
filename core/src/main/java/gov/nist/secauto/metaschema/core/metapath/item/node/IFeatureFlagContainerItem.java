
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * This mixin interface indicates that the implementation is a {@link INodeItem}
 * that is based on an {@link IModelDefinition}. This means it has flag
 * children.
 * <p>
 * If an implementation may have flag and model children, or model children
 * only, then the {@link IFeatureModelContainerItem} should be used instead.
 */
public interface IFeatureFlagContainerItem extends INodeItem {

  /**
   * Get the model implementation that potentially contains flags.
   *
   * @return the model
   */
  @NonNull
  FlagContainer getModel();

  @Override
  default Collection<IFlagNodeItem> getFlags() {
    return getModel().getFlags();
  }

  @Override
  default IFlagNodeItem getFlagByName(@NonNull QName name) {
    return getModel().getFlagByName(name);
  }

  @Override
  default Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    // no model items
    return CollectionUtil.emptyList();
  }

  @Override
  default List<? extends IModelNodeItem<?, ?>> getModelItemsByName(QName name) {
    // no model items
    return CollectionUtil.emptyList();
  }

  /**
   * Provides an abstract implementation of a model that contains a collection of
   * flags.
   */
  class FlagContainer {
    @NonNull
    private final Map<QName, IFlagNodeItem> flags;

    /**
     * Initialize the container with the provided collection of flags.
     *
     * @param flags
     *          a flag mapping of qualified name to corresponding
     *          {@link IFlagNodeItem}
     */
    protected FlagContainer(@NonNull Map<QName, IFlagNodeItem> flags) {
      this.flags = flags;
    }

    /**
     * Get a flag in this container using the associated flag qualified name.
     *
     * @param name
     *          the qualified name of the flag
     * @return the corresponding flag item or {@code null} if no flag had the
     *         provided name
     */
    @Nullable
    public IFlagNodeItem getFlagByName(@NonNull QName name) {
      return flags.get(name);
    }

    /**
     * Get the flags in this container.
     *
     * @return the flags
     */
    @NonNull
    @SuppressWarnings("null")
    public Collection<IFlagNodeItem> getFlags() {
      return flags.values();
    }
  }
}
