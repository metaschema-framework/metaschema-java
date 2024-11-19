/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.info.IModelInstanceCollectionInfo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an assembly or field instance bound to Java data.
 *
 * @param <ITEM>
 *          the Java type for associated bound objects
 */
public interface IBoundInstanceModel<ITEM>
    extends IBoundInstance<ITEM>, IModelInstanceAbsolute {
  /**
   * Get the collection Java item type for the Java field associated with this
   * instance.
   *
   * @param field
   *          the Java field for the instance
   * @return the Java item type
   */
  @NonNull
  static Class<?> getItemType(@NonNull Field field) {
    Type fieldType = field.getGenericType();
    Class<?> rawType = ObjectUtils.notNull(
        (Class<?>) (fieldType instanceof ParameterizedType ? ((ParameterizedType) fieldType).getRawType() : fieldType));

    Class<?> itemType;
    if (Map.class.isAssignableFrom(rawType)) {
      // this is a Map so the second generic type is the value
      itemType = ObjectUtils.notNull((Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[1]);
    } else if (List.class.isAssignableFrom(rawType)) {
      // this is a List so there is only a single generic type
      itemType = ObjectUtils.notNull((Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[0]);
    } else {
      // non-collection
      itemType = rawType;
    }
    return itemType;
  }

  /**
   * Get the collection Java item type for the Java field associated with this
   * instance.
   *
   * @param <TYPE>
   *          the item's expected Java type
   * @param field
   *          the Java field for the instance
   * @param expectedItemType
   *          the item's expected Java type, which may be a super type of the
   *          item's type
   * @return the Java item type
   */
  @NonNull
  static <TYPE> Class<? extends TYPE> getItemType(@NonNull Field field, @NonNull Class<TYPE> expectedItemType) {
    Type fieldType = field.getGenericType();
    Class<?> rawType = ObjectUtils.notNull(
        (Class<?>) (fieldType instanceof ParameterizedType ? ((ParameterizedType) fieldType).getRawType() : fieldType));

    Class<?> itemType;
    if (Map.class.isAssignableFrom(rawType)) {
      // this is a Map so the second generic type is the value
      itemType = ObjectUtils.notNull((Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[1]);
    } else if (List.class.isAssignableFrom(rawType)) {
      // this is a List so there is only a single generic type
      itemType = ObjectUtils.notNull((Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[0]);
    } else {
      // non-collection
      itemType = rawType;
    }
    return ObjectUtils.notNull(itemType.asSubclass(expectedItemType));
  }

  @Override
  @NonNull
  IBoundDefinitionModelAssembly getContainingDefinition();

  @Override
  default Object getResolvedDefaultValue() {
    return getMaxOccurs() == 1 ? getEffectiveDefaultValue() : getCollectionInfo().emptyValue();
  }

  /**
   * Get the item values associated with the provided value.
   *
   * @param value
   *          the value which may be a singleton or a collection
   * @return the ordered collection of values
   */
  @Override
  @NonNull
  default Collection<? extends Object> getItemValues(Object value) {
    return getCollectionInfo().getItemsFromValue(value);
  }

  /**
   * Get the Java binding information for the collection type supported by this
   * instance.
   *
   * @return the collection Java binding information
   */
  @NonNull
  IModelInstanceCollectionInfo<ITEM> getCollectionInfo();

  /**
   * Get the JSON key flag for the provided item.
   *
   * @param item
   *          the item to get the JSON key flag for
   * @return the JSON key flag
   */
  @Nullable
  IBoundInstanceFlag getItemJsonKey(@NonNull Object item);

  @Override
  default void deepCopy(@NonNull IBoundObject fromInstance, @NonNull IBoundObject toInstance) throws BindingException {
    Object value = getValue(fromInstance);
    if (value != null) {
      value = getCollectionInfo().deepCopyItems(fromInstance, toInstance);
    }
    setValue(toInstance, value);
  }
}
