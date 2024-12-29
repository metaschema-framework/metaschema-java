
package gov.nist.secauto.metaschema.core.metapath.node;

import gov.nist.secauto.metaschema.core.metapath.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * A {@link INodeItem} supported by a {@link IFlagInstance}, that may have an
 * associated value.
 */
class FlagInstanceNodeItemImpl
    extends AbstractFlagInstanceNodeItem
    implements IFeatureAtomicValuedItem,
    IFeatureChildNodeItem {

  @NonNull
  private final Object value;

  /**
   * Used to cache this object as an atomic item.
   */
  @NonNull
  private final Lazy<IAnyAtomicItem> atomicItem;

  public FlagInstanceNodeItemImpl(
      @NonNull IFlagInstance instance,
      @NonNull IModelNodeItem<?, ?> parent,
      @NonNull Object value) {
    super(instance, parent);
    this.value = value;
    this.atomicItem = ObjectUtils.notNull(Lazy.lazy(this::newAtomicItem));
  }

  @Override
  @NonNull
  public Object getValue() {
    return value;
  }

  @Override
  public Object getAtomicValue() {
    return getValue();
  }

  @Override
  public IAnyAtomicItem toAtomicItem() {
    return atomicItem.get();
  }

  @Override
  public String stringValue() {
    return toAtomicItem().asString();
  }
}
