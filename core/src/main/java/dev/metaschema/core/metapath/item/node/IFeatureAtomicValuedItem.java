
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IAtomicValuedItem;
import dev.metaschema.core.model.IValuedDefinition;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

// FIXME: cleanup these feature interfaces to reduce the number of interfaces and methods
// FIXME: rename to IFeatureRequiredDataAtomicValuedNodeItem
interface IFeatureAtomicValuedItem
    extends IFeatureRequiredDataItem, IAtomicValuedItem {

  @NonNull
  IValuedDefinition getDefinition();

  @Nullable
  Object getAtomicValue();

  @Nullable
  default IAnyAtomicItem newAtomicItem() {
    Object atomicValue = getAtomicValue();
    IAnyAtomicItem retval = null;
    if (atomicValue != null) {
      IValuedDefinition def = getDefinition();
      retval = def.getJavaTypeAdapter().newItem(atomicValue);
    }
    return retval;
  }
}
