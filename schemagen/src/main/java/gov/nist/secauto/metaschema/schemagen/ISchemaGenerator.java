/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.schemagen.json.JsonSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides the capability to generate a schema from a Metaschema module.
 */
@FunctionalInterface
public interface ISchemaGenerator {
  /**
   * Generate and write a schema for the provided {@code metaschema} to the
   * {@link Writer} provided by {@code writer} using the provided
   * {@code configuration}.
   *
   * @param metaschema
   *          the Module to generate the schema for
   * @param writer
   *          the writer to use to write the schema
   * @param configuration
   *          the schema generation configuration
   * @throws SchemaGenerationException
   *           if an error occurred while writing the schema
   */
  void generateFromModule(
      @NonNull IModule metaschema,
      @NonNull Writer writer,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration);

  /**
   * Generate a schema for the provided module and write it to the specified file
   * path.
   *
   * @param module
   *          the Metaschema module to generate the schema for
   * @param destination
   *          the file path to write the schema to
   * @param asFormat
   *          the schema format to generate
   * @param configuration
   *          the schema generation configuration
   * @throws IOException
   *           if an I/O error occurs while writing the schema
   */
  static void generateSchema(
      @NonNull IModule module,
      @NonNull Path destination,
      @NonNull SchemaFormat asFormat,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration)
      throws IOException {
    ISchemaGenerator schemaGenerator = asFormat.getSchemaGenerator();

    try (Writer writer = Files.newBufferedWriter(
        destination,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      assert writer != null;
      schemaGenerator.generateFromModule(module, writer, configuration);
      writer.flush();
    }
  }

  /**
   * Generate a schema for the provided module and write it to the specified
   * writer.
   * <p>
   * The writer is not closed by this method, as the caller is responsible for
   * managing its lifecycle.
   *
   * @param module
   *          the Metaschema module to generate the schema for
   * @param writer
   *          the writer to output the schema to
   * @param asFormat
   *          the schema format to generate
   * @param configuration
   *          the schema generation configuration
   * @throws IOException
   *           if an I/O error occurs while writing the schema
   */
  static void generateSchema(
      @NonNull IModule module,
      @NonNull Writer writer,
      @NonNull SchemaFormat asFormat,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration)
      throws IOException {
    ISchemaGenerator schemaGenerator = asFormat.getSchemaGenerator();

    schemaGenerator.generateFromModule(module, writer, configuration);
    writer.flush();
    // we don't want to close os, since we do not own it
  }

  /**
   * Identifies the supported schema generation formats.
   */
  enum SchemaFormat {
    /**
     * a JSON Schema.
     */
    JSON(new JsonSchemaGenerator()),
    /**
     * an XML Schema.
     */
    XML(new XmlSchemaGenerator());

    @NonNull
    private final ISchemaGenerator schemaGenerator;

    SchemaFormat(@NonNull ISchemaGenerator schemaGenerator) {
      this.schemaGenerator = schemaGenerator;
    }

    /**
     * Get the schema generator implementation for this format.
     *
     * @return the schema generator
     */
    @NonNull
    public ISchemaGenerator getSchemaGenerator() {
      return schemaGenerator;
    }
  }
}
