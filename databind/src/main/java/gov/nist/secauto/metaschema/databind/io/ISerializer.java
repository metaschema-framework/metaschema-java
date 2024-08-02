/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implementations of this interface are able to write data in a bound object
 * instance of the parameterized type to a structured data format.
 *
 * @param <CLASS>
 *          the Java type from which data can be written
 */
public interface ISerializer<CLASS extends IBoundObject> extends IMutableConfiguration<SerializationFeature<?>> {

  @Override
  ISerializer<CLASS> enableFeature(SerializationFeature<?> feature);

  @Override
  ISerializer<CLASS> disableFeature(SerializationFeature<?> feature);

  @Override
  ISerializer<CLASS> applyConfiguration(IConfiguration<SerializationFeature<?>> other);

  @Override
  ISerializer<CLASS> set(SerializationFeature<?> feature, Object value);

  /**
   * Write data from a bound class instance to the {@link OutputStream}.
   * <p>
   * This method does not have ownership of the the provided output stream and
   * will not close it.
   *
   * @param data
   *          the instance data
   * @param os
   *          the output stream to write to
   * @throws IOException
   *           if an error occurred while writing data to the stream
   */
  default void serialize(@NonNull IBoundObject data, @NonNull OutputStream os) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
    serialize(data, writer);
    writer.flush();
  }

  /**
   * Write data from a bound class instance to the {@link File}.
   *
   * @param data
   *          the instance data
   * @param path
   *          the file to write to
   * @param openOptions
   *          options specifying how the file is opened
   * @throws IOException
   *           if an error occurred while writing data to the file indicated by
   *           the {@code path} parameter
   */
  default void serialize(@NonNull IBoundObject data, @NonNull Path path, OpenOption... openOptions) throws IOException {
    try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, openOptions)) {
      assert writer != null;
      serialize(data, writer);
    }
  }

  /**
   * Write data from a bound class instance to the {@link File}.
   *
   * @param data
   *          the instance data
   * @param file
   *          the file to write to
   * @throws IOException
   *           if an error occurred while writing data to the stream
   */
  default void serialize(@NonNull IBoundObject data, @NonNull File file) throws IOException {
    serialize(data, ObjectUtils.notNull(file.toPath()), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  /**
   * Write data from a bound class instance to the {@link Writer}.
   *
   * @param data
   *          the instance data
   * @param writer
   *          the writer to write to
   * @throws IOException
   *           if an error occurred while writing data to the stream
   */
  void serialize(@NonNull IBoundObject data, @NonNull Writer writer) throws IOException;
}
