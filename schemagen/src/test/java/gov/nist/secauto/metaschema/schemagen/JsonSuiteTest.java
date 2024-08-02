/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.validation.IContentValidator;
import gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingModuleLoader;
import gov.nist.secauto.metaschema.schemagen.json.JsonSchemaGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

class JsonSuiteTest
    extends AbstractSchemaGeneratorTestSuite {

  @Override
  protected Supplier<IContentValidator> getSchemaValidatorSupplier() {
    return () -> JSON_SCHEMA_VALIDATOR;
  }

  @Override
  protected Format getRequiredContentFormat() {
    return Format.JSON;
  }

  @Override
  protected Function<Path, JsonSchemaContentValidator> getContentValidatorSupplier() {
    return JSON_CONTENT_VALIDATOR_PROVIDER;
  }

  @Override
  protected BiFunction<IModule, Writer, Void> getGeneratorSupplier() {
    return JSON_SCHEMA_PROVIDER;
  }

  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  @Execution(ExecutionMode.SAME_THREAD)
  @DisplayName("JSON Schema Generation")
  @TestFactory
  Stream<DynamicNode> generateTests() {
    return testFactory();
  }

  @Disabled
  @Test
  void testDatatypeUuid() throws IOException, MetaschemaException { // NOPMD - testing delegated to doTest
    doTest(
        "datatypes/",
        "datatypes-uuid_metaschema.xml",
        "test-schema",
        contentCase(Format.JSON, "datatypes-uuid_test_valid_PASS.json", true));
  }

  @Disabled
  @Test
  void testChoice() throws IOException, MetaschemaException { // NOPMD - testing delegated to doTest
    doTest(
        "choice/",
        "choice-multiple_metaschema.xml",
        "test-choice-schema",
        contentCase(Format.JSON, "choice-multiple_test_multiple_PASS.json", true));
  }

  @Disabled
  @Test
  void testDatatypeCharStrings() throws IOException, MetaschemaException { // NOPMD - testing delegated to doTest
    doTest(
        "datatypes/",
        "charstrings_metaschema.xml",
        "datatypes-charstrings-schema",
        contentCase(Format.JSON, "charstrings_test_okay_PASS.json", true),
        contentCase(Format.XML, "charstrings_test_okay_PASS.xml", true));
  }

  @Disabled
  @Test
  void testFlagBasic() throws IOException, MetaschemaException { // NOPMD - testing delegated to doTest
    doTest(
        "flag/",
        "flag-basic_metaschema.xml",
        "flag-basic-schema",
        contentCase(Format.JSON, "flag-basic_test_datatype_FAIL.json", false),
        contentCase(Format.JSON, "flag-basic_test_simple_PASS.json", true));
  }

  @Disabled
  @Test
  void testOscalComplete() throws IOException, MetaschemaException { // NOPMD - delegated to doTest
    IBindingContext context = new DefaultBindingContext();
    BindingModuleLoader loader = new BindingModuleLoader(context);
    loader.allowEntityResolution();

    IModule module = loader.load(new URL(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/develop/src/metaschema/oscal_complete_metaschema.xml"));
    ISchemaGenerator schemaGenerator = new JsonSchemaGenerator();
    IMutableConfiguration<SchemaGenerationFeature<?>> features
        = new DefaultConfiguration<>();
    features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
    try (Writer writer = Files.newBufferedWriter(
        Path.of("target/oscal-complete_schema.json"),
        StandardCharsets.UTF_8,
        getWriteOpenOptions())) {
      assert writer != null;
      schemaGenerator.generateFromModule(module, writer, features);
    }
  }

  @Disabled
  @Test
  void testTestMetaschema() throws IOException, MetaschemaException { // NOPMD - delegated to doTest
    IBindingContext context = new DefaultBindingContext();
    BindingModuleLoader loader = new BindingModuleLoader(context);
    loader.allowEntityResolution();

    IModule module = loader.load(new URL(
        "https://raw.githubusercontent.com/usnistgov/metaschema/71233f4eb6854e820c7949144e86afa4d7981b22/test-suite/metaschema-xspec/json-schema-gen/json-value-testing-mini-metaschema.xml"));
    ISchemaGenerator schemaGenerator = new JsonSchemaGenerator();
    IMutableConfiguration<SchemaGenerationFeature<?>> features = new DefaultConfiguration<>();
    features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
    try (Writer writer = Files.newBufferedWriter(
        Path.of("target/json-value-testing-mini_schema.json"),
        StandardCharsets.UTF_8,
        getWriteOpenOptions())) {
      assert writer != null;
      schemaGenerator.generateFromModule(module, writer, features);
    }
  }

}
