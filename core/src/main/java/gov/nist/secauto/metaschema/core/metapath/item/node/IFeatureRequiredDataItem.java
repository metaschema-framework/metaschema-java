
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import edu.umd.cs.findbugs.annotations.NonNull;

interface IFeatureRequiredDataItem extends IItem {

  @Override
  @NonNull
  Object getValue();

  @Override
  default boolean hasValue() {
    // must have value
    return true;
  }
}
