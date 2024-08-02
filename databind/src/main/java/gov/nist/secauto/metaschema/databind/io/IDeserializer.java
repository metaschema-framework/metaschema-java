/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implementations of this interface are able to read structured data into a
 * bound object instance of the parameterized type.
 *
 * @param <CLASS>
 *          the Java type into which data can be read
 */
public interface IDeserializer<CLASS extends IBoundObject> extends IMutableConfiguration<DeserializationFeature<?>> {

  @Override
  IDeserializer<CLASS> enableFeature(DeserializationFeature<?> feature);

  @Override
  IDeserializer<CLASS> disableFeature(DeserializationFeature<?> feature);

  @Override
  IDeserializer<CLASS> applyConfiguration(IConfiguration<DeserializationFeature<?>> other);

  @Override
  IDeserializer<CLASS> set(DeserializationFeature<?> feature, Object value);

  /**
   * Determine if the serializer is performing validation.
   *
   * @return {@code true} if the serializer is performing content validation, or
   *         {@code false} otherwise
   */
  default boolean isValidating() {
    return isFeatureEnabled(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);
  }

  /**
   * Get the constraint validation handler configured for this deserializer, which
   * will be used to validate loaded data.
   *
   * @return the validation handler
   */
  @NonNull
  IConstraintValidationHandler getConstraintValidationHandler();

  /**
   * Set the constraint violation handler for constraint validation.
   *
   * @param handler
   *          the handler to use
   */
  void setConstraintValidationHandler(@NonNull IConstraintValidationHandler handler);

  /**
   * Read data from the {@link InputStream} into a bound class instance.
   *
   * @param is
   *          the input stream to read from
   * @param documentUri
   *          the URI of the document to read from
   * @return the instance data
   * @throws IOException
   *           if an error occurred while reading data from the stream
   */
  @NonNull
  default CLASS deserialize(@NonNull InputStream is, @NonNull URI documentUri) throws IOException {
    return deserialize(new InputStreamReader(is, StandardCharsets.UTF_8), documentUri);
  }

  /**
   * Read data from the {@link Path} into a bound class instance.
   *
   * @param path
   *          the file to read from
   * @return the instance data
   * @throws IOException
   *           if an error occurred while writing data to the file indicated by
   *           the {@code path} parameter
   */
  @NonNull
  default CLASS deserialize(@NonNull Path path) throws IOException {
    try (Reader reader = ObjectUtils.notNull(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
      return deserialize(reader, ObjectUtils.notNull(path.toUri()));
    }
  }

  /**
   * Read data from the {@link File} into a bound class instance.
   *
   * @param file
   *          the file to read from
   * @return the instance data
   * @throws IOException
   *           if an error occurred while reading data from the stream
   */
  @NonNull
  default CLASS deserialize(@NonNull File file) throws IOException {
    return deserialize(ObjectUtils.notNull(file.toPath()));
  }

  /**
   * Read data from the remote resource into a bound class instance.
   *
   *
   * @param url
   *          the remote resource to read from
   * @return the instance data
   * @throws IOException
   *           if an error occurred while reading data from the stream
   * @throws URISyntaxException
   *           if the provided URL is not formatted strictly according to to
   *           RFC2396 and cannot be converted to a URI.
   */
  @NonNull
  default CLASS deserialize(@NonNull URL url) throws IOException, URISyntaxException {
    try (InputStream in = ObjectUtils.notNull(url.openStream())) {
      return deserialize(in, ObjectUtils.notNull(url.toURI()));
    }
  }

  /**
   * Read data from the {@link Reader} into a bound class instance.
   *
   *
   * @param reader
   *          the reader to read from
   * @param documentUri
   *          the URI of the document to read from
   * @return the instance data
   * @throws IOException
   *           if an error occurred while reading data from the stream
   */
  @NonNull
  default CLASS deserialize(@NonNull Reader reader, @NonNull URI documentUri) throws IOException {
    return deserializeToValue(reader, documentUri);
  }

  /**
   * Read data from the {@link Reader} into a node item instance.
   *
   * @param is
   *          the input stream to read from
   * @param documentUri
   *          the URI of the document to read from
   * @return a new node item
   * @throws IOException
   *           if an error occurred while reading data from the stream
   */
  @NonNull
  default INodeItem deserializeToNodeItem(@NonNull InputStream is, @NonNull URI documentUri)
      throws IOException {
    return deserializeToNodeItem(new InputStreamReader(is, StandardCharsets.UTF_8), documentUri);
  }

  /**
   * Read data from the {@link Reader} into a node item instance.
   *
   * @param reader
   *          the reader to read from
   * @param documentUri
   *          the URI of the document to read from
   * @return a new node item
   * @throws IOException
   *           if an error occurred while reading data from the stream
   */
  @NonNull
  INodeItem deserializeToNodeItem(@NonNull Reader reader, @NonNull URI documentUri) throws IOException;

  /**
   * Read data from the {@link Reader} into a node item instance.
   *
   * @param reader
   *          the reader to read from
   * @param documentUri
   *          the URI of the document to read from
   * @return a new node item
   * @throws IOException
   *           if an error occurred while reading data from the stream
   */
  @NonNull
  CLASS deserializeToValue(@NonNull Reader reader, @NonNull URI documentUri) throws IOException;
}
