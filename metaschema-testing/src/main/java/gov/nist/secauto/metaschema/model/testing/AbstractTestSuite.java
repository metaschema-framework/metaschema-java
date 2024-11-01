/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.validation.IContentValidator;
import gov.nist.secauto.metaschema.core.model.validation.IValidationFinding;
import gov.nist.secauto.metaschema.core.model.validation.IValidationResult;
import gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator.JsonValidationFinding;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.ISerializer;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;
import gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.ContentCaseType;
import gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.GenerateSchemaDocument.GenerateSchema;
import gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.MetaschemaDocument;
import gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.TestCollectionDocument.TestCollection;
import gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.TestScenarioDocument.TestScenario;
import gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.TestSuiteDocument;

import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.platform.commons.JUnitException;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

public abstract class AbstractTestSuite {
  private static final Logger LOGGER = LogManager.getLogger(AbstractTestSuite.class);

  private static final boolean DELETE_RESULTS_ON_EXIT = false;
  private static final OpenOption[] OPEN_OPTIONS_TRUNCATE = {
      StandardOpenOption.CREATE,
      StandardOpenOption.WRITE,
      StandardOpenOption.TRUNCATE_EXISTING
  };

  /**
   * Get the content format used by the test suite.
   *
   * @return the format
   */
  @NonNull
  protected abstract Format getRequiredContentFormat();

  /**
   * Get the resource describing the tests to execute.
   *
   * @return the resource
   */
  @NonNull
  protected abstract URI getTestSuiteURI();

  /**
   * Get the filesystem location to use for generating content.
   *
   * @return the filesystem path
   */
  @NonNull
  protected abstract Path getGenerationPath();

  /**
   * Get the method used to generate a schema using a given Metaschema module and
   * writer.
   *
   * @return the schema generator supplier
   */
  @NonNull
  protected abstract BiFunction<IModule, Writer, Void> getSchemaGeneratorSupplier();

  /**
   * Get the method used to provide a schema validator.
   *
   * @return the method as a supplier
   */
  @Nullable
  protected abstract Supplier<? extends IContentValidator> getSchemaValidatorSupplier();

  /**
   * Get the method used to provide a content validator.
   *
   * @return the method as a supplier
   */
  @NonNull
  protected abstract Function<Path, ? extends IContentValidator> getContentValidatorSupplier();

  /**
   * Dynamically generate the unit tests.
   *
   * @return the steam of unit tests
   */
  @NonNull
  protected Stream<DynamicNode> testFactory(@NonNull IBindingContext bindingContext) {
    try {
      XmlOptions options = new XmlOptions();
      options.setBaseURI(null);
      options.setLoadLineNumbers();

      Path generationPath = getGenerationPath();
      if (Files.exists(generationPath)) {
        if (!Files.isDirectory(generationPath)) {
          throw new JUnitException(String.format("Generation path '%s' exists and is not a directory", generationPath));
        }
      } else {
        Files.createDirectories(generationPath);
      }

      URI testSuiteUri = getTestSuiteURI();
      URL testSuiteUrl = testSuiteUri.toURL();
      TestSuiteDocument directive = TestSuiteDocument.Factory.parse(testSuiteUrl, options);
      return ObjectUtils.notNull(directive.getTestSuite().getTestCollectionList().stream()
          .flatMap(
              collection -> Stream
                  .of(generateCollection(
                      ObjectUtils.notNull(collection),
                      testSuiteUri,
                      generationPath,
                      bindingContext))));
    } catch (XmlException | IOException ex) {
      throw new JUnitException("Unable to generate tests", ex);
    }
  }

  /**
   * Configure removal of the provided directory after test execution.
   *
   * @param path
   *          the directory to configure for removal
   */
  protected void deleteCollectionOnExit(@NonNull Path path) {
    Runtime.getRuntime().addShutdownHook(new Thread( // NOPMD - this is not a webapp
        () -> {
          try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
              @Override
              public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
              }

              @Override
              public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
                if (ex == null) {
                  Files.delete(dir);
                  return FileVisitResult.CONTINUE;
                }
                // directory iteration failed for some reason
                throw ex;
              }
            });
          } catch (IOException ex) {
            throw new JUnitException("Failed to delete collection: " + path, ex);
          }
        }));
  }

  private DynamicContainer generateCollection(
      @NonNull TestCollection collection,
      @NonNull URI testSuiteUri,
      @NonNull Path generationPath,
      @NonNull IBindingContext bindingContext) {
    URI collectionUri = testSuiteUri.resolve(collection.getLocation());
    assert collectionUri != null;

    LOGGER.atInfo().log("Collection: " + collectionUri);
    Lazy<Path> collectionGenerationPath = ObjectUtils.notNull(Lazy.lazy(() -> {
      Path retval;
      try {
        retval = ObjectUtils.requireNonNull(Files.createTempDirectory(generationPath, "collection-"));
        if (DELETE_RESULTS_ON_EXIT) {
          deleteCollectionOnExit(ObjectUtils.requireNonNull(retval));
        }
      } catch (IOException ex) {
        throw new JUnitException("Unable to create collection temp directory", ex);
      }
      return retval;
    }));

    return DynamicContainer.dynamicContainer(
        collection.getName(),
        testSuiteUri,
        collection.getTestScenarioList().stream()
            .flatMap(scenario -> {
              assert scenario != null;
              return Stream.of(generateScenario(
                  scenario,
                  collectionUri,
                  collectionGenerationPath,
                  bindingContext));
            })
            .sequential());
  }

  /**
   * Generate a schema for the provided module using the provided schema
   * generator.
   *
   * @param module
   *          the Metaschema module to generate the schema for
   * @param schemaPath
   *          the location to generate the schema
   * @param schemaProducer
   *          the method callback to use to generate the schema
   * @throws IOException
   *           if an error occurred while writing the schema
   */
  protected void generateSchema(
      @NonNull IModule module,
      @NonNull Path schemaPath,
      @NonNull BiFunction<IModule, Writer, Void> schemaProducer) throws IOException {
    Path parentDir = schemaPath.getParent();
    if (parentDir != null && !Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }

    try (Writer writer = Files.newBufferedWriter(
        schemaPath,
        StandardCharsets.UTF_8,
        getWriteOpenOptions())) {
      schemaProducer.apply(module, writer);
    }
    LOGGER.atInfo().log("Produced schema '{}' for module '{}'", schemaPath, module.getLocation());
  }

  /**
   * The the options for writing generated content.
   *
   * @return the options
   */
  @SuppressWarnings("PMD.MethodReturnsInternalArray")
  protected OpenOption[] getWriteOpenOptions() {
    return OPEN_OPTIONS_TRUNCATE;
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private DynamicContainer generateScenario(
      @NonNull TestScenario scenario,
      @NonNull URI collectionUri,
      @NonNull Lazy<Path> collectionGenerationPath,
      @NonNull IBindingContext bindingContext) {
    Lazy<Path> scenarioGenerationPath = Lazy.lazy(() -> {
      Path retval;
      try {
        retval = Files.createTempDirectory(collectionGenerationPath.get(), "scenario-");
      } catch (IOException ex) {
        throw new JUnitException("Unable to create scenario temp directory", ex);
      }
      return retval;
    });

    // try {
    // // create the directories the schema will be stored in
    // Files.createDirectories(scenarioGenerationPath);
    // } catch (IOException ex) {
    // throw new JUnitException("Unable to create test directories for path: " +
    // scenarioGenerationPath, ex);
    // }

    GenerateSchema generateSchema = scenario.getGenerateSchema();
    MetaschemaDocument.Metaschema metaschemaDirective = generateSchema.getMetaschema();
    URI metaschemaUri = collectionUri.resolve(metaschemaDirective.getLocation());

    IModule module;
    try {
      IBindingModuleLoader loader = bindingContext.newModuleLoader();

      module = loader.load(ObjectUtils.notNull(metaschemaUri.toURL()));
    } catch (IOException | MetaschemaException ex) {
      throw new JUnitException("Unable to generate classes for metaschema: " + metaschemaUri, ex);
    }

    Lazy<Path> lazySchema = Lazy.lazy(() -> {
      String schemaExtension;
      Format requiredContentFormat = getRequiredContentFormat();
      switch (requiredContentFormat) {
      case JSON:
      case YAML:
        schemaExtension = ".json";
        break;
      case XML:
        schemaExtension = ".xsd";
        break;
      default:
        throw new IllegalStateException();
      }

      // determine what file to use for the schema
      Path schemaPath;
      try {
        schemaPath = Files.createTempFile(scenarioGenerationPath.get(), "", "-schema" + schemaExtension);
      } catch (IOException ex) {
        throw new JUnitException("Unable to create schema temp file", ex);
      }
      try {
        generateSchema(ObjectUtils.notNull(module), ObjectUtils.notNull(schemaPath), getSchemaGeneratorSupplier());
      } catch (IOException ex) {
        throw new IllegalStateException(ex);
      }
      return schemaPath;
    });

    Lazy<IContentValidator> lazyContentValidator = Lazy.lazy(() -> {
      Path schemaPath = lazySchema.get();
      return getContentValidatorSupplier().apply(schemaPath);
    });
    assert lazyContentValidator != null;

    // build a test container for the generate and validate steps
    DynamicTest validateSchema = DynamicTest.dynamicTest(
        "Validate Schema",
        () -> {
          Supplier<? extends IContentValidator> supplier = getSchemaValidatorSupplier();
          if (supplier != null) {
            Path schemaPath;
            try {
              schemaPath = ObjectUtils.requireNonNull(lazySchema.get());
            } catch (Exception ex) {
              throw new JUnitException(
                  "failed to generate schema", ex);
            }
            validateWithSchema(ObjectUtils.requireNonNull(supplier.get()), schemaPath);
          }
        });

    Stream<? extends DynamicNode> contentTests = scenario.getValidationCaseList().stream()
        .flatMap(contentCase -> {
          assert contentCase != null;
          DynamicTest test
              = generateValidationCase(
                  contentCase,
                  bindingContext,
                  lazyContentValidator,
                  collectionUri,
                  ObjectUtils.notNull(scenarioGenerationPath));
          return test == null ? Stream.empty() : Stream.of(test);
        }).sequential();

    return DynamicContainer.dynamicContainer(
        scenario.getName(),
        metaschemaUri,
        Stream.concat(Stream.of(validateSchema), contentTests).sequential());
  }

  /**
   * Perform content conversion.
   *
   * @param resource
   *          the resource to convert
   * @param generationPath
   *          the path to write the converted resource to
   * @param context
   *          the Metaschema binding context
   * @return the location of the converted content
   * @throws IOException
   *           if an error occurred while reading or writing content
   * @see #getRequiredContentFormat()
   */
  protected Path convertContent(
      @NonNull URI resource,
      @NonNull Path generationPath,
      @NonNull IBindingContext context)
      throws IOException {
    Object object;
    try {
      object = context.newBoundLoader().load(ObjectUtils.notNull(resource.toURL()));
    } catch (URISyntaxException ex) {
      throw new IOException(ex);
    }

    if (!Files.exists(generationPath)) {
      Files.createDirectories(generationPath);
    }

    Path convertedContetPath;
    try {
      convertedContetPath = ObjectUtils.notNull(Files.createTempFile(generationPath, "", "-content"));
    } catch (IOException ex) {
      throw new JUnitException(
          String.format("Unable to create converted content path in location '%s'", generationPath),
          ex);
    }

    Format toFormat = getRequiredContentFormat();
    if (LOGGER.isInfoEnabled()) {
      LOGGER.atInfo().log("Converting content '{}' to {} as '{}'", resource, toFormat, convertedContetPath);
    }

    ISerializer<?> serializer
        = context.newSerializer(toFormat, ObjectUtils.asType(object.getClass()));
    serializer.serialize(ObjectUtils.asType(object), convertedContetPath, getWriteOpenOptions());

    return convertedContetPath;
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private DynamicTest generateValidationCase(
      @NonNull ContentCaseType contentCase,
      @NonNull IBindingContext bindingContext,
      @NonNull Lazy<IContentValidator> lazyContentValidator,
      @NonNull URI collectionUri,
      @NonNull Lazy<Path> resourceGenerationPath) {

    URI contentUri = ObjectUtils.notNull(collectionUri.resolve(contentCase.getLocation()));

    Format format = contentCase.getSourceFormat();
    DynamicTest retval = null;
    if (getRequiredContentFormat().equals(format)) {
      retval = DynamicTest.dynamicTest(
          String.format("Validate %s=%s: %s", format, contentCase.getValidationResult(),
              contentCase.getLocation()),
          contentUri,
          () -> {
            IContentValidator contentValidator;
            try {
              contentValidator = lazyContentValidator.get();
            } catch (Exception ex) {
              throw new JUnitException( // NOPMD - cause is relevant, exception is not
                  "failed to produce the content validator", ex.getCause());
            }

            assertEquals(
                contentCase.getValidationResult(),
                validateWithSchema(
                    ObjectUtils.notNull(contentValidator), ObjectUtils.notNull(contentUri.toURL())),
                "validation did not match expectation for: " + contentUri.toASCIIString());
          });
    } else if (contentCase.getValidationResult()) {
      retval = DynamicTest.dynamicTest(
          String.format("Convert and Validate %s=%s: %s", format, contentCase.getValidationResult(),
              contentCase.getLocation()),
          contentUri,
          () -> {
            Path convertedContetPath;
            try {
              convertedContetPath = convertContent(
                  contentUri,
                  ObjectUtils.notNull(resourceGenerationPath.get()),
                  bindingContext);
            } catch (Exception ex) { // NOPMD - intentional
              throw new JUnitException("failed to convert content: " + contentUri, ex);
            }

            IContentValidator contentValidator;
            try {
              contentValidator = lazyContentValidator.get();
            } catch (Exception ex) {
              throw new JUnitException( // NOPMD - cause is relevant, exception is not
                  "failed to produce the content validator",
                  ex.getCause());
            }

            if (LOGGER.isInfoEnabled()) {
              LOGGER.atInfo().log("Validating content '{}'", convertedContetPath);
            }
            assertEquals(contentCase.getValidationResult(),
                validateWithSchema(
                    ObjectUtils.notNull(contentValidator),
                    ObjectUtils.notNull(convertedContetPath.toUri().toURL())),
                String.format("validation of '%s' did not match expectation", convertedContetPath));
          });
    }
    return retval;
  }

  private static boolean validateWithSchema(@NonNull IContentValidator validator, @NonNull URL target)
      throws IOException {
    IValidationResult schemaValidationResult;
    try {
      schemaValidationResult = validator.validate(target);
    } catch (URISyntaxException ex) {
      throw new IOException(ex);
    }
    return processValidationResult(schemaValidationResult);
  }

  /**
   * Use the provided validator to validate the provided target.
   *
   * @param validator
   *          the content validator to use
   * @param target
   *          the resource to validate
   * @return {@code true} if the content is valid or {@code false} otherwise
   * @throws IOException
   *           if an error occurred while reading the content
   */
  protected static boolean validateWithSchema(@NonNull IContentValidator validator, @NonNull Path target)
      throws IOException {
    IValidationResult schemaValidationResult = validator.validate(target);
    if (!schemaValidationResult.isPassing()) {
      LOGGER.atError().log("Schema validation failed for: {}", target);
    }
    return processValidationResult(schemaValidationResult);
  }

  private static boolean processValidationResult(IValidationResult schemaValidationResult) {
    for (IValidationFinding finding : schemaValidationResult.getFindings()) {
      logFinding(ObjectUtils.notNull(finding));
    }
    return schemaValidationResult.isPassing();
  }

  private static void logFinding(@NonNull IValidationFinding finding) {
    LogBuilder logBuilder;
    switch (finding.getSeverity()) {
    case CRITICAL:
      logBuilder = LOGGER.atFatal();
      break;
    case ERROR:
      logBuilder = LOGGER.atError();
      break;
    case WARNING:
      logBuilder = LOGGER.atWarn();
      break;
    case INFORMATIONAL:
      logBuilder = LOGGER.atInfo();
      break;
    case DEBUG:
      logBuilder = LOGGER.atDebug();
      break;
    default:
      throw new IllegalArgumentException("Unknown level: " + finding.getSeverity().name());
    }

    // if (finding.getCause() != null) {
    // logBuilder.withThrowable(finding.getCause());
    // }

    if (finding instanceof JsonValidationFinding) {
      JsonValidationFinding jsonFinding = (JsonValidationFinding) finding;
      logBuilder.log("[{}] {}", jsonFinding.getCause().getPointerToViolation(), finding.getMessage());
    } else {
      logBuilder.log("{}", finding.getMessage());
    }
  }
}
