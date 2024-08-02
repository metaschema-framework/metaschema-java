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
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class ListCollectionInfo<ITEM>
    extends AbstractModelInstanceCollectionInfo<ITEM> {

  public ListCollectionInfo(
      @NonNull IBoundInstanceModel<ITEM> instance) {
    super(instance);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends ITEM> getItemType() {
    ParameterizedType actualType = (ParameterizedType) getInstance().getType();
    // this is a List so there is only a single generic type
    return ObjectUtils.notNull((Class<? extends ITEM>) actualType.getActualTypeArguments()[0]);
  }

  @Override
  public List<ITEM> getItemsFromParentInstance(Object parentInstance) {
    Object value = getInstance().getValue(parentInstance);
    return getItemsFromValue(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ITEM> getItemsFromValue(Object value) {
    return value == null ? CollectionUtil.emptyList() : (List<ITEM>) value;
  }

  @Override
  public int size(Object value) {
    return value == null ? 0 : ((List<?>) value).size();
  }

  @Override
  public boolean isEmpty(@Nullable Object value) {
    return value == null || ((List<?>) value).isEmpty();
  }

  @Override
  public List<ITEM> deepCopyItems(@NonNull IBoundObject fromInstance, @NonNull IBoundObject toInstance)
      throws BindingException {
    IBoundInstanceModel<ITEM> instance = getInstance();

    List<ITEM> copy = emptyValue();
    for (ITEM item : getItemsFromParentInstance(fromInstance)) {
      copy.add(instance.deepCopyItem(ObjectUtils.requireNonNull(item), toInstance));
    }
    return copy;
  }

  @Override
  public List<ITEM> emptyValue() {
    return new LinkedList<>();
  }

  @Override
  public List<ITEM> readItems(IModelInstanceReadHandler<ITEM> handler) throws IOException {
    return handler.readList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void writeItems(IModelInstanceWriteHandler<ITEM> handler, Object value) throws IOException {
    handler.writeList((List<ITEM>) value);
  }
}
