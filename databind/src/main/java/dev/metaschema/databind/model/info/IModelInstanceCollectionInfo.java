/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.info;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.databind.io.BindingException;
import dev.metaschema.databind.model.IBoundInstanceModel;

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
/**
 * Provides information about the collection type for a model instance.
 * <p>
 * This interface abstracts the differences between singleton, list, and map
 * collection types for model instances.
 *
 * @param <ITEM>
 *          the Java type of items in the collection
 */
public interface IModelInstanceCollectionInfo<ITEM> {

  /**
   * Create a new collection info instance for the provided model instance.
   * <p>
   * The appropriate collection info type is determined based on the instance's
   * maximum occurrence and JSON group-as behavior.
   *
   * @param <T>
   *          the Java type of items in the collection
   * @param instance
   *          the model instance to create collection info for
   * @return the new collection info instance
   */
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

  /**
   * Get the items from a parent instance's property value.
   *
   * @param parentInstance
   *          the parent instance to get items from
   * @return a collection of items, which may be empty but never {@code null}
   */
  @NonNull
  default Collection<? extends ITEM> getItemsFromParentInstance(@NonNull Object parentInstance) {
    Object value = getInstance().getValue(parentInstance);
    return getItemsFromValue(value);
  }

  /**
   * Get the items from a raw value object.
   *
   * @param value
   *          the value object to extract items from
   * @return a collection of items, which may be empty but never {@code null}
   */
  @NonNull
  Collection<? extends ITEM> getItemsFromValue(Object value);

  /**
   * Get an empty value appropriate for this collection type.
   *
   * @return an empty collection value, or {@code null} for singleton types
   */
  Object emptyValue();

  /**
   * Create a deep copy of items from one object to another.
   *
   * @param fromObject
   *          the source object to copy items from
   * @param toObject
   *          the target object to copy items to
   * @return the copied value
   * @throws BindingException
   *           if an error occurs during the deep copy
   */
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

  /**
   * Write the items represented by the given value.
   *
   * @param handler
   *          the item writing handler
   * @param value
   *          the value containing items to write
   * @throws IOException
   *           if there was an error when writing the data
   */
  void writeItems(
      @NonNull IModelInstanceWriteHandler<ITEM> handler,
      @NonNull Object value) throws IOException;
}
