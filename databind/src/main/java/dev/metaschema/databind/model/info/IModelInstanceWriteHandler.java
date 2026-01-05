/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.info;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Handler interface for writing model instance collections during
 * serialization.
 * <p>
 * This interface provides methods for iterating over collection items and
 * writing them to an output stream.
 *
 * @param <ITEM>
 *          the Java type of items being written
 */
public interface IModelInstanceWriteHandler<ITEM> {
  /**
   * Write a singleton item.
   *
   * @param item
   *          the item to write
   * @throws IOException
   *           if an error occurred while writing the output
   */
  default void writeSingleton(@NonNull ITEM item) throws IOException {
    writeItem(item);
  }

  /**
   * Write items from a list collection.
   *
   * @param items
   *          the list of items to write
   * @throws IOException
   *           if an error occurred while writing the output
   */
  void writeList(@NonNull List<ITEM> items) throws IOException;

  /**
   * Write items from a map collection.
   *
   * @param items
   *          the map of items to write, keyed by JSON key flag value
   * @throws IOException
   *           if an error occurred while writing the output
   */
  void writeMap(@NonNull Map<String, ITEM> items) throws IOException;

  /**
   * Write the next item in the collection of items represented by the instance.
   *
   * @param item
   *          the item Java object to write
   * @throws IOException
   *           if an error occurred while parsing the input
   */
  void writeItem(@NonNull ITEM item) throws IOException;
}
