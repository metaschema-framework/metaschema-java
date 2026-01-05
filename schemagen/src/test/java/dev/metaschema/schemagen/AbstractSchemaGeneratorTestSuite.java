/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.metaschema.core.configuration.DefaultConfiguration;
import dev.metaschema.core.configuration.IConfiguration;
import dev.metaschema.core.configuration.IMutableConfiguration;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.MetaschemaModelConstants;
import dev.metaschema.core.model.constraint.IConstraintSet;
import dev.metaschema.core.model.validation.JsonSchemaContentValidator;
import dev.metaschema.core.model.validation.XmlSchemaContentValidator;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.io.Format;
import dev.metaschema.databind.model.metaschema.IBindingModuleLoader;
import dev.metaschema.model.testing.AbstractTestSuite;
import dev.metaschema.schemagen.json.JsonSchemaGenerator;
import dev.metaschema.schemagen.xml.XmlSchemaGenerator;

import org.junit.platform.commons.JUnitException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Base class for schema generation test suites, providing utilities for XML and
 * JSON schema generation and validation.
 */
public abstract class AbstractSchemaGeneratorTestSuite
    extends AbstractTestSuite {
  @NonNull
  protected static final ISchemaGenerator XML_SCHEMA_GENERATOR = new XmlSchemaGenerator();
  @NonNull
  protected static final ISchemaGenerator JSON_SCHEMA_GENERATOR = new JsonSchemaGenerator();
  @NonNull
  protected static final IConfiguration<SchemaGenerationFeature<?>> SCHEMA_GENERATION_CONFIG;
  @NonNull
  protected static final BiFunction<IModule, Writer, Void> XML_SCHEMA_PROVIDER;
  @NonNull
  protected static final BiFunction<IModule, Writer, Void> JSON_SCHEMA_PROVIDER;
  @NonNull
  protected static final JsonSchemaContentValidator JSON_SCHEMA_VALIDATOR;
  @NonNull
  protected static final Function<Path, JsonSchemaContentValidator> JSON_CONTENT_VALIDATOR_PROVIDER;
  @NonNull
  protected static final Function<Path, XmlSchemaContentValidator> XML_CONTENT_VALIDATOR_PROVIDER;

  private static final String UNIT_TEST_CONFIG
      = "../core/metaschema/test-suite/schema-generation/unit-tests.xml";

  static {
    IMutableConfiguration<SchemaGenerationFeature<?>> features = new DefaultConfiguration<>();
    // features.enableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
    features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
    SCHEMA_GENERATION_CONFIG = features;

    BiFunction<IModule, Writer, Void> xmlProvider = (module, writer) -> {
      assert module != null;
      assert writer != null;
      try {
        XML_SCHEMA_GENERATOR.generateFromModule(module, writer, SCHEMA_GENERATION_CONFIG);
      } catch (SchemaGenerationException ex) {
        throw new JUnitException("IO error", ex);
      }
      return null;
    };
    XML_SCHEMA_PROVIDER = xmlProvider;

    BiFunction<IModule, Writer, Void> jsonProvider = (module, writer) -> {
      assert module != null;
      assert writer != null;
      try {
        JSON_SCHEMA_GENERATOR.generateFromModule(module, writer, SCHEMA_GENERATION_CONFIG);
      } catch (SchemaGenerationException ex) {
        throw new JUnitException("IO error", ex);
      }
      return null;
    };
    JSON_SCHEMA_PROVIDER = jsonProvider;
    // Module module = ModuleLayer.boot()
    // .findModule("dev.metaschema.core")
    // .orElseThrow();
    //
    // try (InputStream is
    // = module.getResourceAsStream("schema.json/json-schema.json")) {
    try (InputStream is = MetaschemaModelConstants.class.getResourceAsStream("/schema/json/json-schema.json")) {
      assert is != null : "unable to get JSON schema resource";
      JsonSchemaContentValidator schemaValidator = new JsonSchemaContentValidator(is);
      JSON_SCHEMA_VALIDATOR = schemaValidator;
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }

    @SuppressWarnings("null")
    @NonNull
    Function<Path, XmlSchemaContentValidator> xmlContentValidatorProvider = path -> {
      try {
        URL schemaResource = path.toUri().toURL();
        @SuppressWarnings("resource")
        StreamSource source
            = new StreamSource(schemaResource.openStream(), schemaResource.toString());
        List<? extends Source> schemaSources = Collections.singletonList(source);
        return new XmlSchemaContentValidator(schemaSources);
      } catch (IOException ex) {
        throw new IllegalStateException(ex);
      }
    };
    XML_CONTENT_VALIDATOR_PROVIDER = xmlContentValidatorProvider;

    @NonNull
    Function<Path, JsonSchemaContentValidator> jsonContentValidatorProvider = path -> {
      try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
        assert is != null;
        return new JsonSchemaContentValidator(is);
      } catch (IOException ex) {
        throw new JUnitException("Failed to create content validator for schema: " + path.toString(), ex);
      }
    };
    JSON_CONTENT_VALIDATOR_PROVIDER = jsonContentValidatorProvider;
  }

  /**
   * Creates a new binding context with no constraints.
   *
   * @return a new binding context instance
   * @throws IOException
   *           if an I/O error occurs while creating the context
   */
  @NonNull
  protected static IBindingContext newBindingContext() throws IOException {
    return newBindingContext(CollectionUtil.emptyList());
  }

  /**
   * Creates a new binding context with the specified constraints.
   *
   * @param constraints
   *          the constraint sets to apply
   * @return a new binding context instance
   * @throws IOException
   *           if an I/O error occurs while creating the context
   */
  @NonNull
  protected static IBindingContext newBindingContext(@NonNull Collection<IConstraintSet> constraints)
      throws IOException {
    Path generationDir = Paths.get("target/generated-modules");
    Files.createDirectories(generationDir);

    return IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(generationDir, "modules-")))
        .constraintSet(constraints)
        .build();
  }

  @Override
  protected URI getTestSuiteURI() {
    return ObjectUtils
        .notNull(Paths.get(UNIT_TEST_CONFIG).toUri());
  }

  @Override
  protected Path getGenerationPath() {
    return ObjectUtils.notNull(Paths.get("target/test-schemagen"));
  }

  /**
   * Generates an XML schema for the given module and writes it to the specified
   * path.
   *
   * @param module
   *          the Metaschema module to generate the schema for
   * @param schemaPath
   *          the path where the schema should be written
   * @return the path to the generated schema
   * @throws IOException
   *           if an I/O error occurs
   */
  protected Path produceXmlSchema(@NonNull IModule module, @NonNull Path schemaPath) throws IOException {
    generateSchema(module, schemaPath, XML_SCHEMA_PROVIDER);
    return schemaPath;
  }

  /**
   * Generates a JSON schema for the given module and writes it to the specified
   * path.
   *
   * @param module
   *          the Metaschema module to generate the schema for
   * @param schemaPath
   *          the path where the schema should be written
   * @return the path to the generated schema
   * @throws IOException
   *           if an I/O error occurs
   */
  protected Path produceJsonSchema(@NonNull IModule module, @NonNull Path schemaPath)
      throws IOException {
    generateSchema(module, schemaPath, JSON_SCHEMA_PROVIDER);
    return schemaPath;
  }

  /**
   * Runs a schema generation and validation test.
   *
   * @param collectionName
   *          the name of the test collection directory
   * @param metaschemaName
   *          the name of the Metaschema file to load
   * @param generatedSchemaName
   *          the base name for the generated schema file
   * @param contentCases
   *          the content validation test cases to run
   * @throws IOException
   *           if an I/O error occurs
   * @throws MetaschemaException
   *           if a Metaschema error occurs
   */
  @SuppressWarnings("null")
  protected void doTest(
      @NonNull String collectionName,
      @NonNull String metaschemaName,
      @NonNull String generatedSchemaName,
      @NonNull ContentCase... contentCases) throws IOException, MetaschemaException {
    Path generationDir = getGenerationPath();

    Path testSuite = Paths.get("../core/metaschema/test-suite/schema-generation/");
    Path collectionPath = testSuite.resolve(collectionName);

    IBindingContext bindingContext = newBindingContext();

    // load the metaschema module
    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();
    Path modulePath = collectionPath.resolve(metaschemaName);
    IModule module = loader.load(modulePath);

    // generate the schema
    Path schemaPath;
    Format requiredContentFormat = getRequiredContentFormat();
    switch (requiredContentFormat) {
    case JSON:
    case YAML:
      Path jsonSchema = produceJsonSchema(module, generationDir.resolve(generatedSchemaName + ".json"));
      assertTrue(validateWithSchema(JSON_SCHEMA_VALIDATOR, jsonSchema),
          String.format("JSON schema '%s' was invalid", jsonSchema.toString()));
      schemaPath = jsonSchema;
      break;
    case XML:
      schemaPath = produceXmlSchema(module, generationDir.resolve(generatedSchemaName + ".xsd"));
      break;
    default:
      throw new IllegalStateException();
    }

    // create content test cases
    for (ContentCase contentCase : contentCases) {
      Path contentPath = collectionPath.resolve(contentCase.getName());

      if (!requiredContentFormat.equals(contentCase.getActualFormat())) {
        contentPath = convertContent(contentPath.toUri(), generationDir, bindingContext);
      }

      assertEquals(contentCase.isValid(),
          validateWithSchema(getContentValidatorSupplier().apply(schemaPath), contentPath),
          String.format("validation of '%s' did not match expectation", contentPath));
    }
  }

  /**
   * Creates a new content validation test case.
   *
   * @param actualFormat
   *          the format of the content file
   * @param contentName
   *          the name of the content file
   * @param valid
   *          whether the content is expected to be valid
   * @return a new content case
   */
  @NonNull
  protected ContentCase contentCase(
      @NonNull Format actualFormat,
      @NonNull String contentName,
      boolean valid) {
    return new ContentCase(contentName, actualFormat, valid);
  }

  /**
   * Represents a content validation test case.
   */
  protected static class ContentCase {
    @NonNull
    private final String name;
    @NonNull
    private final Format actualFormat;
    private final boolean valid;

    /**
     * Constructs a new content case.
     *
     * @param name
     *          the name of the content file
     * @param actualFormat
     *          the format of the content file
     * @param valid
     *          whether the content is expected to be valid
     */
    public ContentCase(@NonNull String name, @NonNull Format actualFormat, boolean valid) {
      this.name = name;
      this.actualFormat = actualFormat;
      this.valid = valid;
    }

    /**
     * Gets the name of the content file.
     *
     * @return the content file name
     */
    @NonNull
    public String getName() {
      return name;
    }

    /**
     * Gets the format of the content file.
     *
     * @return the content format
     */
    @NonNull
    public Format getActualFormat() {
      return actualFormat;
    }

    /**
     * Checks whether the content is expected to be valid.
     *
     * @return {@code true} if the content should be valid, {@code false} otherwise
     */
    public boolean isValid() {
      return valid;
    }
  }
}
