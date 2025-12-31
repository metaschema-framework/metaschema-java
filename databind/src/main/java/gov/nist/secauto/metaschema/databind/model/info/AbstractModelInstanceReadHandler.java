/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelNamed;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract base class for reading model instance collections.
 * <p>
 * This class provides the framework for reading collections of items during
 * deserialization, with support for different collection types.
 *
 * @param <ITEM>
 *          the Java type of items being read
 */
public abstract class AbstractModelInstanceReadHandler<ITEM> implements IModelInstanceReadHandler<ITEM> {
  @NonNull
  private final IBoundInstanceModel<ITEM> instance;
  @NonNull
  private final IBoundObject parentObject;

  /**
   * Construct a new read handler for the provided model instance.
   *
   * @param instance
   *          the model instance to read
   * @param parentObject
   *          the parent object that will contain the read data
   */
  protected AbstractModelInstanceReadHandler(
      @NonNull IBoundInstanceModel<ITEM> instance,
      @NonNull IBoundObject parentObject) {
    this.instance = instance;
    this.parentObject = parentObject;
  }

  /**
   * Get the model instance associated with this handler.
   *
   * @return the collection information
   */
  @NonNull
  public IBoundInstanceModel<ITEM> getInstance() {
    return instance;
  }

  /**
   * Get the collection Java type information associated with this handler.
   *
   * @return the collection information
   */
  @NonNull
  public IModelInstanceCollectionInfo<ITEM> getCollectionInfo() {
    return getInstance().getCollectionInfo();
  }

  /**
   * Get the object onto which parsed data will be stored.
   *
   * @return the parentObject
   */
  @NonNull
  public IBoundObject getParentObject() {
    return parentObject;
  }

  @Override
  public String getJsonKeyFlagName() {
    IBoundInstanceModel<?> instance = getInstance();
    String retval = null;
    if (instance instanceof IBoundInstanceModelNamed) {
      IBoundInstanceFlag jsonKey = ((IBoundInstanceModelNamed<?>) instance).getEffectiveJsonKey();
      if (jsonKey != null) {
        retval = jsonKey.getEffectiveName();
      }
    }
    return retval;
  }
}
