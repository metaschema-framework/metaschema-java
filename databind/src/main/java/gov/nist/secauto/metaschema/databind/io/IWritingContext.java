/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureComplexItemValueHandler;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;

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

  @FunctionalInterface
  interface ObjectWriter<T extends IFeatureComplexItemValueHandler> {

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
