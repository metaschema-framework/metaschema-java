/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.Writer;

import dev.metaschema.core.configuration.IMutableConfiguration;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.io.AbstractSerializer;
import dev.metaschema.databind.io.SerializationFeature;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Provides support for serializing bound Java objects to JSON format based on a
 * Metaschema module definition.
 * <p>
 * This serializer uses Jackson's {@link JsonGenerator} to produce JSON output
 * that conforms to the Metaschema-defined data structure.
 *
 * @param <CLASS>
 *          the Java type of the bound object to be serialized
 */
public class DefaultJsonSerializer<CLASS extends IBoundObject>
    extends AbstractSerializer<CLASS> {
  private Lazy<JsonFactory> factory;

  /**
   * Construct a new Module binding-based deserializer that reads JSON-based
   * Module content.
   *
   * @param definition
   *          the assembly class binding describing the Java objects this
   *          deserializer parses data into
   */
  public DefaultJsonSerializer(@NonNull IBoundDefinitionModelAssembly definition) {
    super(definition);
    resetFactory();
  }

  /**
   * Resets the JSON factory to use a freshly created instance.
   * <p>
   * This method is called when the serializer configuration changes to ensure the
   * factory reflects the current settings.
   */
  protected final void resetFactory() {
    this.factory = Lazy.of(this::newFactoryInstance);
  }

  @Override
  protected void configurationChanged(IMutableConfiguration<SerializationFeature<?>> config) {
    super.configurationChanged(config);
    resetFactory();
  }

  /**
   * Constructs a new JSON factory.
   * <p>
   * Subclasses can override this method to create a JSON factory with a specific
   * configuration.
   *
   * @return the factory
   */
  @NonNull
  protected JsonFactory newFactoryInstance() {
    return JsonFactoryFactory.instance();
  }

  /**
   * Get the configured JSON factory instance.
   *
   * @return the JSON factory used to create JSON generators
   */
  @NonNull
  private JsonFactory getJsonFactory() {
    return ObjectUtils.notNull(factory.get());
  }

  /**
   * Create a new JSON generator for writing to the provided writer.
   *
   * @param writer
   *          the writer to send JSON output to
   * @return a new JSON generator configured with pretty printing
   * @throws IOException
   *           if an error occurs while creating the generator
   */
  @SuppressWarnings("resource")
  @NonNull
  private JsonGenerator newJsonGenerator(@NonNull Writer writer) throws IOException {
    JsonFactory factory = getJsonFactory();
    return ObjectUtils.notNull(factory.createGenerator(writer)
        .setPrettyPrinter(new DefaultPrettyPrinter()));
  }

  @Override
  public void serialize(IBoundObject data, Writer writer) throws IOException {
    try (JsonGenerator generator = newJsonGenerator(writer)) {
      IBoundDefinitionModelAssembly definition = getDefinition();

      boolean serializeRoot = get(SerializationFeature.SERIALIZE_ROOT);
      if (serializeRoot) {
        // first write the initial START_OBJECT
        generator.writeStartObject();

        generator.writeFieldName(definition.getRootJsonName());
      }

      MetaschemaJsonWriter jsonWriter = new MetaschemaJsonWriter(generator);
      jsonWriter.write(definition, data);

      if (serializeRoot) {
        generator.writeEndObject();
      }
    }
  }
}
