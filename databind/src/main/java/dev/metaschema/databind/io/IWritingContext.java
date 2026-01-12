/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import java.io.IOException;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.databind.model.IBoundDefinitionModelComplex;
import dev.metaschema.databind.model.info.IFeatureComplexItemValueHandler;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides the context for writing bound objects to a specific output format.
 *
 * @param <WRITER>
 *          the type of writer used for output
 */
public interface IWritingContext<WRITER> {
  /**
   * Get the writer associated with the writing context.
   *
   * @return the writer
   */
  @NonNull
  WRITER getWriter();

  /**
   * Write the data described by the provided {@code targetObject} as an XML
   * element.
   *
   * @param definition
   *          the bound Module definition describing the data to write
   * @param targetObject
   *          the Java object data to write
   * @throws IOException
   *           if an error occurred while writing
   */
  void write(
      @NonNull IBoundDefinitionModelComplex definition,
      @NonNull IBoundObject targetObject) throws IOException;

  /**
   * A functional interface for writing object properties.
   *
   * @param <T>
   *          the type of handler used for property writing
   */
  @FunctionalInterface
  interface ObjectWriter<T extends IFeatureComplexItemValueHandler> {

    /**
     * Write the properties of the provided parent item using the given handler.
     *
     * @param parentItem
     *          the parent object whose properties are being written
     * @param handler
     *          the handler that provides property writing capabilities
     * @throws IOException
     *           if an error occurred while writing
     */
    void accept(@NonNull IBoundObject parentItem, @NonNull T handler) throws IOException;

    /**
     * Perform a series of property write operations, starting first with this
     * operation and followed by the {@code after} operation.
     *
     * @param after
     *          the secondary property write operation to perform
     * @return an aggregate property write operation
     */
    @NonNull
    default ObjectWriter<T> andThen(@NonNull ObjectWriter<? super T> after) {
      return (parentItem, handler) -> {
        accept(parentItem, handler);
        after.accept(parentItem, handler);
      };
    }
  }
}
