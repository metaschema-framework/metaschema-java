/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

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
  default void writeSingleton(@NonNull ITEM item) throws IOException {
    writeItem(item);
  }

  void writeList(@NonNull List<ITEM> items) throws IOException;

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
