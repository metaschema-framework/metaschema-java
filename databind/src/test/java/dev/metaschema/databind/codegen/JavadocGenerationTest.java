/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.config.DefaultBindingConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for verifying the quality of generated Javadoc in binding classes.
 *
 * <p>
 * These tests verify that generated code meets project Javadoc standards:
 * <ul>
 * <li>Descriptions do not have extraneous quotes</li>
 * <li>Constructors have proper Javadoc</li>
 * <li>Accessor methods have proper Javadoc with @param/@return tags</li>
 * <li>Null-safety annotations are present</li>
 * </ul>
 */
class JavadocGenerationTest
    extends AbstractMetaschemaTest {

  // Use simple_with_field which has descriptions on fields for better testing
  private static final Path TEST_METASCHEMA = ObjectUtils.notNull(
      Paths.get("src/test/resources/metaschema/simple_with_field/metaschema.xml"));

  // The assembly is named "top-level" which generates class "TopLevel"
  private static final String TEST_CLASS_NAME = "TopLevel";

  private Path classDir;

  @BeforeEach
  void setUp() throws IOException {
    Files.createDirectories(generationDir);
    classDir = ObjectUtils.notNull(Files.createTempDirectory(generationDir, "javadoc-test-"));
  }

  /**
   * Generates code and returns the content of the generated Java file.
   *
   * @param metaschemaPath
   *          the path to the Metaschema module
   * @param className
   *          the simple name of the class to find
   * @return the content of the generated Java file
   * @throws Exception
   *           if generation fails
   */
  @NonNull
  private String generateAndReadClass(@NonNull Path metaschemaPath, @NonNull String className) throws Exception {
    IBindingContext context = newBindingContext();
    IModule module = context.loadMetaschema(metaschemaPath);

    DefaultBindingConfiguration bindingConfiguration = new DefaultBindingConfiguration();
    ModuleCompilerHelper.compileModule(module, classDir, bindingConfiguration);

    // Find the generated Java file
    Path generatedFile = findGeneratedFile(classDir, className + ".java");
    return ObjectUtils.notNull(Files.readString(generatedFile));
  }

  /**
   * Finds a generated file by name within the given directory tree.
   *
   * @param dir
   *          the root directory to search
   * @param fileName
   *          the name of the file to find
   * @return the path to the found file
   * @throws IOException
   *           if the file cannot be found or an I/O error occurs
   */
  @SuppressWarnings("resource")
  @NonNull
  private static Path findGeneratedFile(@NonNull Path dir, @NonNull String fileName) throws IOException {
    return ObjectUtils.notNull(Files.walk(dir)
        .filter(p -> p.getFileName().toString().equals(fileName))
        .findFirst()
        .orElseThrow(() -> new IOException("Could not find generated file: " + fileName)));
  }

  @Test
  void testFieldJavadocDoesNotContainQuotes() throws Exception {
    String content = generateAndReadClass(TEST_METASCHEMA, TEST_CLASS_NAME);

    // Pattern to find Javadoc comments that start with a quote
    // Good: /** Some description */
    // Bad: /** "Some description" */ (single-line or multi-line)
    // Match both single-line and multi-line Javadoc with leading quotes
    Pattern quotedJavadoc = Pattern.compile("/\\*\\*\\s*(\\n\\s*\\*\\s*)?\"[^\"]+\"");

    assertFalse(quotedJavadoc.matcher(content).find(),
        "Generated Javadoc should not contain quoted descriptions. Found in:\n" + content);
  }

  @Test
  void testConstructorHasJavadoc() throws Exception {
    String content = generateAndReadClass(TEST_METASCHEMA, TEST_CLASS_NAME);

    // Check that the no-arg constructor has Javadoc
    assertTrue(content.contains("Constructs a new"),
        "Generated constructors should have Javadoc. Content:\n" + content);
  }

  @Test
  void testDataConstructorHasParamTag() throws Exception {
    String content = generateAndReadClass(TEST_METASCHEMA, TEST_CLASS_NAME);

    // Check that the data constructor has @param tag
    assertTrue(content.contains("@param data"),
        "Data constructor should have @param tag for data parameter. Content:\n" + content);
  }

  @Test
  void testGetterHasReturnTag() throws Exception {
    String content = generateAndReadClass(TEST_METASCHEMA, TEST_CLASS_NAME);

    // Check that getter methods have @return tag
    // This assumes the metaschema has at least one field that generates a getter
    // Use DOTALL and allow for @Nullable annotation between Javadoc and method
    Pattern getterWithReturn
        = Pattern.compile("@return.*?\\*/\\s*(@Nullable\\s+)?public\\s+\\w+\\s+get", Pattern.DOTALL);

    assertTrue(getterWithReturn.matcher(content).find(),
        "Getter methods should have @return tag. Content:\n" + content);
  }

  @Test
  void testSetterHasParamTag() throws Exception {
    String content = generateAndReadClass(TEST_METASCHEMA, TEST_CLASS_NAME);

    // Check that setter methods have @param tag
    // Use DOTALL so . matches newlines in multi-line Javadoc
    Pattern setterWithParam = Pattern.compile("@param\\s+value.*?\\*/\\s*public\\s+void\\s+set", Pattern.DOTALL);

    assertTrue(setterWithParam.matcher(content).find(),
        "Setter methods should have @param tag. Content:\n" + content);
  }

  @Test
  void testNullableAnnotationPresent() throws Exception {
    String content = generateAndReadClass(TEST_METASCHEMA, TEST_CLASS_NAME);

    // Check that @Nullable annotations are present
    assertTrue(content.contains("@Nullable") || content.contains("edu.umd.cs.findbugs.annotations.Nullable"),
        "Generated code should include @Nullable annotations. Content:\n" + content);
  }

  @Test
  void testRequiredFlagHasNonNullAnnotation() throws Exception {
    Path requiredFlagMetaschema = ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/required_flag/metaschema.xml"));
    String content = generateAndReadClass(requiredFlagMetaschema, "Item");

    // Required flag getter should have @NonNull annotation
    // Pattern: @NonNull followed by public getter for RequiredId
    Pattern nonNullRequired = Pattern.compile("@NonNull\\s+public\\s+String\\s+getRequiredId");

    assertTrue(nonNullRequired.matcher(content).find(),
        "Required flag getter should have @NonNull annotation. Content:\n" + content);
  }

  @Test
  void testOptionalFlagHasNullableAnnotation() throws Exception {
    Path requiredFlagMetaschema = ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/required_flag/metaschema.xml"));
    String content = generateAndReadClass(requiredFlagMetaschema, "Item");

    // Optional flag getter should have @Nullable annotation
    Pattern nullableOptional = Pattern.compile("@Nullable\\s+public\\s+String\\s+getOptionalName");

    assertTrue(nullableOptional.matcher(content).find(),
        "Optional flag getter should have @Nullable annotation. Content:\n" + content);
  }

  @Test
  void testRequiredFlagJavadocDoesNotMentionNull() throws Exception {
    Path requiredFlagMetaschema = ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/required_flag/metaschema.xml"));
    String content = generateAndReadClass(requiredFlagMetaschema, "Item");

    // Find the getter Javadoc for required-id and verify it doesn't say "or null if
    // not set"
    // The @return should just say "@return the required-id value"
    Pattern requiredGetterJavadoc = Pattern.compile(
        "@return the required-id value\\s*\\n\\s*\\*/\\s*@NonNull", Pattern.DOTALL);

    assertTrue(requiredGetterJavadoc.matcher(content).find(),
        "Required flag @return should not mention null. Content:\n" + content);
  }

  @Test
  void testRequiredFlagSetterHasNonNullParameter() throws Exception {
    Path requiredFlagMetaschema = ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/required_flag/metaschema.xml"));
    String content = generateAndReadClass(requiredFlagMetaschema, "Item");

    // Required flag setter should have @NonNull on the parameter
    Pattern nonNullSetterParam = Pattern.compile("setRequiredId\\(@NonNull\\s+String\\s+value\\)");

    assertTrue(nonNullSetterParam.matcher(content).find(),
        "Required flag setter should have @NonNull parameter. Content:\n" + content);
  }

  /**
   * Tests that a class-level Javadoc is generated even when the metaschema
   * definition only has a formal-name and no description.
   *
   * @throws Exception
   *           if generation fails
   */
  @Test
  void testClassHasJavadocWithOnlyFormalName() throws Exception {
    Path noDescMetaschema = ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/no_description/metaschema.xml"));
    String content = generateAndReadClass(noDescMetaschema, "ItemWithNameOnly");

    // The class should have a Javadoc comment derived from the formal-name
    // Pattern: /** followed by content then */ before the class declaration
    Pattern classJavadoc = Pattern.compile("/\\*\\*.*?\\*/\\s*@MetaschemaAssembly", Pattern.DOTALL);

    assertTrue(classJavadoc.matcher(content).find(),
        "Class with only formal-name should have class-level Javadoc. Content:\n" + content);
  }

  /**
   * Tests that a class-level Javadoc is generated even when the metaschema
   * definition has neither formal-name nor description.
   *
   * @throws Exception
   *           if generation fails
   */
  @Test
  void testClassHasJavadocWithNoDocs() throws Exception {
    Path noDescMetaschema = ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/no_description/metaschema.xml"));
    String content = generateAndReadClass(noDescMetaschema, "ItemWithoutDocs");

    // The class should have a Javadoc comment even without formal-name or
    // description
    // It should use the definition name as a fallback
    Pattern classJavadoc = Pattern.compile("/\\*\\*.*?\\*/\\s*@MetaschemaAssembly", Pattern.DOTALL);

    assertTrue(classJavadoc.matcher(content).find(),
        "Class without formal-name or description should still have class-level Javadoc. Content:\n" + content);
  }
}
