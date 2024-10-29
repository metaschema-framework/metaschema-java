/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.BindingException;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A feature interface for handling read, writing, and copying item objects,
 * which are the data building blocks of a Metaschema module instance.
 *
 * @param <TYPE>
 *          the Java type of the item
 */
public interface IItemValueHandler<TYPE> {
  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks, or
   *          {@code null} if there is no parent
   * @param handler
   *          the item parsing handler
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @Nullable
  TYPE readItem(
      @Nullable IBoundObject parent,
      @NonNull IItemReadHandler handler) throws IOException;

  /**
   * Write the provided item.
   *
   * @param item
   *          the data to write
   * @param handler
   *          the item writing handler
   * @throws IOException
   *           if an error occurred while writing
   */
  void writeItem(
      @NonNull TYPE item,
      @NonNull IItemWriteHandler handler) throws IOException;

  /**
   * Create and return a deep copy of the provided item.
   *
   * @param item
   *          the item to copy
   * @param parentInstance
   *          an optional parent object to use for serialization callbacks
   * @return the new deep copy
   * @throws BindingException
   *           if an error occurred while analyzing the bound objects
   */
  @NonNull
  TYPE deepCopyItem(@NonNull TYPE item, @Nullable IBoundObject parentInstance) throws BindingException;
}
