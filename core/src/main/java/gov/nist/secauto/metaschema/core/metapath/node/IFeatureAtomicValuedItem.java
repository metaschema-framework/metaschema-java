
package gov.nist.secauto.metaschema.core.metapath.node;

import gov.nist.secauto.metaschema.core.metapath.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.atomic.IAtomicValuedItem;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
