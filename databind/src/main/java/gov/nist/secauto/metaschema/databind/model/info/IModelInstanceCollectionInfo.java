/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

// REFACTOR: parameterize the item type?
public interface IModelInstanceCollectionInfo<ITEM> {

  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static <T> IModelInstanceCollectionInfo<T> of(
      @NonNull IBoundInstanceModel<T> instance) {

    // create the collection info
    Type type = instance.getType();
    Field field = instance.getField();

    IModelInstanceCollectionInfo<T> retval;
    if (instance.getMaxOccurs() == -1 || instance.getMaxOccurs() > 1) {
      // collection case
      JsonGroupAsBehavior jsonGroupAs = instance.getJsonGroupAsBehavior();

      // expect a ParameterizedType
      if (!(type instanceof ParameterizedType)) {
        switch (jsonGroupAs) {
        case KEYED:
          throw new IllegalStateException(
              String.format("The field '%s' on class '%s' has data type of '%s'," + " but should have a type of '%s'.",
                  field.getName(),
                  field.getDeclaringClass().getName(),
                  field.getType().getName(), Map.class.getName()));
        case LIST:
        case SINGLETON_OR_LIST:
          throw new IllegalStateException(
              String.format("The field '%s' on class '%s' has data type of '%s'," + " but should have a type of '%s'.",
                  field.getName(),
                  field.getDeclaringClass().getName(),
                  field.getType().getName(), List.class.getName()));
        default:
          // this should not occur
          throw new IllegalStateException(jsonGroupAs.name());
        }
      }

      Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
      if (JsonGroupAsBehavior.KEYED.equals(jsonGroupAs)) {
        if (!Map.class.isAssignableFrom(rawType)) {
          throw new IllegalArgumentException(String.format(
              "The field '%s' on class '%s' has data type '%s', which is not the expected '%s' derived data type.",
              field.getName(),
              field.getDeclaringClass().getName(),
              field.getType().getName(),
              Map.class.getName()));
        }
        retval = new MapCollectionInfo<>(instance);
      } else {
        if (!List.class.isAssignableFrom(rawType)) {
          throw new IllegalArgumentException(String.format(
              "The field '%s' on class '%s' has data type '%s', which is not the expected '%s' derived data type.",
              field.getName(),
              field.getDeclaringClass().getName(),
              field.getType().getName(),
              List.class.getName()));
        }
        retval = new ListCollectionInfo<>(instance);
      }
    } else {
      // single value case
      if (type instanceof ParameterizedType) {
        throw new IllegalStateException(String.format(
            "The field '%s' on class '%s' has a data parmeterized type of '%s',"
                + " but the occurance is not multi-valued.",
            field.getName(),
            field.getDeclaringClass().getName(),
            field.getType().getName()));
      }
      retval = new SingletonCollectionInfo<>(instance);
    }
    return retval;
  }

  /**
   * Get the associated instance binding for which this info is for.
   *
   * @return the instance binding
   */
  @NonNull
  IBoundInstanceModel<ITEM> getInstance();

  /**
   * Get the number of items associated with the value.
   *
   * @param value
   *          the value to identify items for
   * @return the number of items, which will be {@code 0} if value is {@code null}
   */
  int size(@Nullable Object value);

  /**
   * Determine if the value is empty.
   *
   * @param value
   *          the value representing a collection
   * @return {@code true} if the value represents a collection with no items or
   *         {@code false} otherwise
   */
  boolean isEmpty(@Nullable Object value);

  /**
   * Get the type of the bound object.
   *
   * @return the raw type of the bound object
   */
  @NonNull
  Class<? extends ITEM> getItemType();

  @NonNull
  default Collection<? extends ITEM> getItemsFromParentInstance(@NonNull Object parentInstance) {
    Object value = getInstance().getValue(parentInstance);
    return getItemsFromValue(value);
  }

  @NonNull
  Collection<? extends ITEM> getItemsFromValue(Object value);

  Object emptyValue();

  Object deepCopyItems(@NonNull IBoundObject fromObject, @NonNull IBoundObject toObject) throws BindingException;

  /**
   * Read the value data for the model instance.
   * <p>
   * This method will return a value based on the instance's value type.
   *
   * @param handler
   *          the item parsing handler
   * @return the item collection object or {@code null} if the instance is not
   *         defined
   * @throws IOException
   *           if there was an error when reading the data
   */
  @Nullable
  Object readItems(@NonNull IModelInstanceReadHandler<ITEM> handler) throws IOException;

  void writeItems(
      @NonNull IModelInstanceWriteHandler<ITEM> handler,
      @NonNull Object value) throws IOException;
}
