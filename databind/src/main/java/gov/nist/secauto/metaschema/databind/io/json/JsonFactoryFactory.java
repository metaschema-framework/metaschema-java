/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class JsonFactoryFactory {
  @NonNull
  private static final JsonFactory SINGLETON = newJsonFactoryInstance();

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
