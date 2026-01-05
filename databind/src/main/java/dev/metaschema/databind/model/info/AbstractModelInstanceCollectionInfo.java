/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.info;

import dev.metaschema.databind.model.IBoundInstanceModel;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract base class for managing collection information for model
 * instances.
 * <p>
 * This class provides common functionality for handling collections of items
 * during serialization and deserialization.
 *
 * @param <ITEM>
 *          the Java type of items in the collection
 */
public abstract class AbstractModelInstanceCollectionInfo<ITEM>
    implements IModelInstanceCollectionInfo<ITEM> {

  @NonNull
  private final IBoundInstanceModel<ITEM> instance;

  /**
   * Construct a new collection info for the provided model instance.
   *
   * @param instance
   *          the model instance this collection info is for
   */
  public AbstractModelInstanceCollectionInfo(
      @NonNull IBoundInstanceModel<ITEM> instance) {
    this.instance = instance;
  }

  @Override
  public IBoundInstanceModel<ITEM> getInstance() {
    return instance;
  }
}
