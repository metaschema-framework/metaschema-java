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

public interface IModelInstanceReadHandler<ITEM> {
  @Nullable
  default ITEM readSingleton() throws IOException {
    return readItem();
  }

  @NonNull
  List<ITEM> readList() throws IOException;

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

  @Nullable
  String getJsonKeyFlagName();
}
