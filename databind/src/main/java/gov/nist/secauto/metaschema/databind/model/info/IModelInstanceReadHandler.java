/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Handler interface for reading model instance collections during
 * deserialization.
 * <p>
 * This interface provides methods for accepting individual items read from a
 * stream and combining them into a collection.
 *
 * @param <ITEM>
 *          the Java type of items being read
 */
public interface IModelInstanceReadHandler<ITEM> {
  /**
   * Read a singleton item.
   *
   * @return the item read, or {@code null} if no item was present
   * @throws IOException
   *           if an error occurred while reading the input
   */
  @Nullable
  default ITEM readSingleton() throws IOException {
    return readItem();
  }

  /**
   * Read items into a list collection.
   *
   * @return the list of items read
   * @throws IOException
   *           if an error occurred while reading the input
   */
  @NonNull
  List<ITEM> readList() throws IOException;

  /**
   * Read items into a map collection, keyed by JSON key flag value.
   *
   * @return the map of items read, keyed by JSON key flag value
   * @throws IOException
   *           if an error occurred while reading the input
   */
  @NonNull
  Map<String, ITEM> readMap() throws IOException;

  /**
   * Read the next item in the collection of items represented by the instance.
   *
   * @return the Java object representing the item, or {@code null} if no items
   *         remain to be read
   * @throws IOException
   *           if an error occurred while parsing the input
   */
  @Nullable
  ITEM readItem() throws IOException;

  /**
   * Get the name of the JSON key flag, if one is configured for this instance.
   *
   * @return the JSON key flag name, or {@code null} if no JSON key is configured
   */
  @Nullable
  String getJsonKeyFlagName();
}
