/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A factory for creating and configuring {@link JsonFactory} instances with
 * standard Metaschema settings.
 * <p>
 * This class provides a singleton factory instance configured for optimal use
 * with Metaschema data binding operations. The factory is configured to not
 * auto-close streams and includes a default codec.
 */
public final class JsonFactoryFactory {
  @NonNull
  private static final JsonFactory SINGLETON = newJsonFactoryInstance();

  /**
   * Private constructor to prevent instantiation.
   */
  private JsonFactoryFactory() {
    // disable construction
  }

  /**
   * Create a new {@link JsonFactory}.
   *
   * @return the factory
   */
  @NonNull
  private static JsonFactory newJsonFactoryInstance() {
    JsonFactory retval = new JsonFactory();
    configureJsonFactory(retval);
    return retval;
  }

  /**
   * Get the cached {@link JsonFactory} instance.
   *
   * @return the factory
   */
  @NonNull
  public static JsonFactory instance() {
    return SINGLETON;
  }

  /**
   * Apply a standard configuration to the provided JSON {@code factory}.
   *
   * @param factory
   *          the factory to configure
   */
  public static void configureJsonFactory(@NonNull JsonFactory factory) {
    // avoid automatically closing parsing streams not owned by the reader
    factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    // avoid automatically closing generation streams not owned by the reader
    factory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    // ensure there is a default codec
    factory.setCodec(new ObjectMapper(factory));
  }
}
