/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import java.util.Collection;

import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.qname.IEnhancedQName;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a bound model instance that is named and can be addressed by that
 * name in the Metaschema model.
 *
 * @param <ITEM>
 *          the Java type of the bound item
 */
public interface IBoundInstanceModelNamed<ITEM>
    extends IBoundInstanceModel<ITEM>, INamedModelInstanceAbsolute {

  @Override
  @NonNull
  IBoundDefinitionModel<ITEM> getDefinition();

  @Override
  default String getName() {
    // delegate to the definition
    return getDefinition().getName();
  }

  @Override
  default Integer getIndex() {
    // delegate to the definition
    return getDefinition().getIndex();
  }

  @Override
  @Nullable
  default IBoundInstanceFlag getEffectiveJsonKey() {
    return JsonGroupAsBehavior.KEYED.equals(getJsonGroupAsBehavior())
        ? getJsonKey()
        : null;
  }

  @Override
  default IBoundInstanceFlag getJsonKey() {
    return getDefinition().getJsonKey();
  }

  @Override
  default IBoundInstanceFlag getItemJsonKey(Object item) {
    return getEffectiveJsonKey();
  }

  @Override
  default Collection<? extends Object> getItemValues(Object value) {
    return getCollectionInfo().getItemsFromValue(value);
  }

  @Override
  default boolean canHandleXmlQName(IEnhancedQName qname) {
    return qname.equals(getQName());
  }
}
