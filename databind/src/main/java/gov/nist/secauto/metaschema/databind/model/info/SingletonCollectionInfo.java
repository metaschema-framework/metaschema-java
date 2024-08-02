/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;

import java.io.IOException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class SingletonCollectionInfo<ITEM>
    extends AbstractModelInstanceCollectionInfo<ITEM> {

  public SingletonCollectionInfo(@NonNull IBoundInstanceModel<ITEM> instance) {
    super(instance);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ITEM> getItemsFromValue(Object value) {
    return value == null ? CollectionUtil.emptyList() : CollectionUtil.singletonList((ITEM) value);
  }

  @Override
  public int size(Object value) {
    return value == null ? 0 : 1;
  }

  @Override
  public boolean isEmpty(@Nullable Object value) {
    return value == null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends ITEM> getItemType() {
    return (Class<? extends ITEM>) getInstance().getItemType();
  }

  @Override
  public Object deepCopyItems(@NonNull IBoundObject fromObject, @NonNull IBoundObject toObject)
      throws BindingException {
    IBoundInstanceModel<ITEM> instance = getInstance();

    @SuppressWarnings("unchecked") ITEM value = (ITEM) instance.getValue(fromObject);

    return value == null ? null : instance.deepCopyItem(ObjectUtils.requireNonNull(value), toObject);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ITEM emptyValue() {
    return (ITEM) getInstance().getDefaultValue();
  }

  @Override
  public Object readItems(IModelInstanceReadHandler<ITEM> handler) throws IOException {
    Object value = handler.readSingleton();
    // REFACTOR: this can return a null value
    return value == null ? emptyValue() : value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void writeItems(IModelInstanceWriteHandler<ITEM> handler, Object value) throws IOException {
    handler.writeSingleton((ITEM) value);
  }
}
