
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * This feature interface represents an item that has no associated value data.
 * <p>
 * This will occur when an {@link IItem} represents a Metaschema definition or
 * instance that is not associated with data. This is typical when constructing
 * items for querying a Metaschema directly, instead of content which will
 * always have data.
 */
public interface IFeatureNoDataValuedItem extends IItem {

  @Override
  @Nullable
  default Object getValue() {
    // no value
    return null;
  }

  @Override
  default boolean hasValue() {
    // no value
    return false;
  }
}
