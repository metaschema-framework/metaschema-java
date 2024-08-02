/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;

import java.util.Collection;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
  default boolean canHandleXmlQName(QName qname) {
    return qname.equals(getXmlQName());
  }
}
