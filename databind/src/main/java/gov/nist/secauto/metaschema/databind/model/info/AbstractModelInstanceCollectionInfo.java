/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;

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

  public AbstractModelInstanceCollectionInfo(
      @NonNull IBoundInstanceModel<ITEM> instance) {
    this.instance = instance;
  }

  @Override
  public IBoundInstanceModel<ITEM> getInstance() {
    return instance;
  }
}
