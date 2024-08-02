/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class MapCollectionInfo<ITEM>
    extends AbstractModelInstanceCollectionInfo<ITEM> {

  public MapCollectionInfo(@NonNull IBoundInstanceModel<ITEM> instance) {
    super(instance);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<ITEM> getItemsFromValue(Object value) {
    return value == null ? CollectionUtil.emptyList() : ObjectUtils.notNull(((Map<?, ITEM>) value).values());
  }

  @Override
  public int size(Object value) {
    return value == null ? 0 : ((Map<?, ?>) value).size();
  }

  @Override
  public boolean isEmpty(@Nullable Object value) {
    return value == null || ((Map<?, ?>) value).isEmpty();
  }

  @SuppressWarnings("null")
  @NonNull
  public Class<?> getKeyType() {
    ParameterizedType actualType = (ParameterizedType) getInstance().getType();
    // this is a Map so the first generic type is the key
    return (Class<?>) actualType.getActualTypeArguments()[0];
  }

  @Override
  public Class<? extends ITEM> getItemType() {
    return getValueType();
  }

  @SuppressWarnings({ "null", "unchecked" })
  @NonNull
  public Class<? extends ITEM> getValueType() {
    ParameterizedType actualType = (ParameterizedType) getInstance().getType();
    // this is a Map so the second generic type is the value
    return (Class<? extends ITEM>) actualType.getActualTypeArguments()[1];
  }

  @Override
  public Map<String, ITEM> deepCopyItems(@NonNull IBoundObject fromInstance, @NonNull IBoundObject toInstance)
      throws BindingException {

    IBoundInstanceModel<ITEM> instance = getInstance();
    Map<String, ITEM> copy = emptyValue();
    for (ITEM item : getItemsFromParentInstance(fromInstance)) {
      assert item != null;

      IBoundInstanceFlag jsonKey = instance.getItemJsonKey(item);
      assert jsonKey != null;

      ITEM itemCopy = instance.deepCopyItem(ObjectUtils.requireNonNull(item), toInstance);
      String key = ObjectUtils.requireNonNull(jsonKey.getValue(itemCopy)).toString();
      copy.put(key, itemCopy);
    }
    return copy;
  }

  @Override
  public Map<String, ITEM> emptyValue() {
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, ITEM> readItems(IModelInstanceReadHandler<ITEM> handler) throws IOException {
    return handler.readMap();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void writeItems(
      IModelInstanceWriteHandler<ITEM> handler,
      Object value) throws IOException {
    handler.writeMap((Map<String, ITEM>) value);
  }
}
