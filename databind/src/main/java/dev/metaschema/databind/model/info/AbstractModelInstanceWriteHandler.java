/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.info;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModel;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract base class for writing model instance collections.
 * <p>
 * This class provides the framework for writing collections of items during
 * serialization, with support for different collection types.
 *
 * @param <ITEM>
 *          the Java type of items being written
 */
public abstract class AbstractModelInstanceWriteHandler<ITEM>
    implements IModelInstanceWriteHandler<ITEM> {
  @NonNull
  private final IBoundInstanceModel<ITEM> instance;

  /**
   * Construct a new write handler for the provided model instance.
   *
   * @param instance
   *          the model instance to write
   */
  public AbstractModelInstanceWriteHandler(@NonNull IBoundInstanceModel<ITEM> instance) {
    this.instance = instance;
  }

  /**
   * Get the associated instance.
   *
   * @return the instance
   */
  public IBoundInstanceModel<ITEM> getInstance() {
    return instance;
  }

  /**
   * Get the collection information.
   *
   * @return the info
   */
  @NonNull
  public IModelInstanceCollectionInfo<ITEM> getCollectionInfo() {
    return instance.getCollectionInfo();
  }

  @Override
  public void writeList(List<ITEM> items) throws IOException {
    writeCollection(items);
  }

  @Override
  public void writeMap(Map<String, ITEM> items) throws IOException {
    writeCollection(ObjectUtils.notNull(items.values()));
  }

  private void writeCollection(@NonNull Collection<ITEM> items) throws IOException {
    for (ITEM item : items) {
      writeItem(ObjectUtils.requireNonNull(item));
    }
  }
}
