/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.validation.IContentValidator;
import gov.nist.secauto.metaschema.core.model.validation.XmlSchemaContentValidator;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

class XmlSuiteTest
    extends AbstractSchemaGeneratorTestSuite {
  // private static final XmlSchemaContentValidator SCHEMA_VALIDATOR;
  //
  // static {
  // URL schemaResource =
  // ModuleLoader.class.getResource("/schema/xml/XMLSchema.xsd");
  // try {
  // List<? extends Source> schemaSources = Collections.singletonList(
  // new StreamSource(schemaResource.openStream(), schemaResource.toString()));
  // SCHEMA_VALIDATOR = new XmlSchemaContentValidator(schemaSources);
  // } catch (SAXException | IOException ex) {
  // throw new IllegalStateException(ex);
  // }
  // }

  @Override
  protected Supplier<IContentValidator> getSchemaValidatorSupplier() {
    // return () -> SCHEMA_VALIDATOR;
    return null;
  }

  @Override
  protected Format getRequiredContentFormat() {
    return Format.XML;
  }

  @Override
  protected Function<Path, XmlSchemaContentValidator> getContentValidatorSupplier() {
    return XML_CONTENT_VALIDATOR_PROVIDER;
  }

  @Override
  protected BiFunction<IModule, Writer, Void> getSchemaGeneratorSupplier() {
    return XML_SCHEMA_PROVIDER;
  }

  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  @Execution(ExecutionMode.SAME_THREAD)
  @DisplayName("XML Schema Generation")
  @TestFactory
  Stream<? extends DynamicNode> generateTests() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    return testFactory(bindingContext);
  }

  @Disabled
  @Test
  void testChoiceMultiple() throws IOException, MetaschemaException { // NOPMD - delegated to doTest
    doTest(
        "choice/",
        "choice-multiple_metaschema.xml",
        "choice-schema",
        contentCase(Format.JSON, "choice-multiple_test_multiple_PASS.json", true));
  }

  @Disabled
  @Test
  void testCollapsibleMultiple() throws IOException, MetaschemaException { // NOPMD - delegated to doTest
    doTest(
        "collapsible/",
        "collapsible_metaschema.xml",
        "collapsible-schema",
        contentCase(Format.JSON, "collapsible_test_multiple_PASS.json", true),
        contentCase(Format.JSON, "collapsible_test_singleton_PASS.json", true));
  }

  @Test
  void testByKey() throws IOException, MetaschemaException { // NOPMD - delegated to doTest
    doTest(
        "group-as/",
        "group-as-by-key_metaschema.xml",
        "group-as-by-key-schema",
        contentCase(Format.JSON, "group-as-by-key_test_valid_PASS.json", true));
  }

  @Disabled
  @Test
  void testAllowedValues() throws IOException, MetaschemaException { // NOPMD - delegated to doTest
    doTest(
        "allowed-values",
        "allowed-values-basic_metaschema.xml",
        "allowed-values-basic-schema",
        // contentCase(Format.JSON, "allowed-values-basic_test_baddates_FAIL.json",
        // false),
        // contentCase(Format.JSON, "allowed-values-basic_test_badvalues_FAIL.json",
        // false),
        contentCase(Format.XML, "allowed-values-basic_test_valid_FAIL.xml", false),
        // contentCase(Format.JSON, "allowed-values-basic_test_valid_PASS.json", true),
        contentCase(Format.XML, "allowed-values-basic_test_valid_PASS.xml", true));
  }

  @Test
  void testLocalDeclarations() throws IOException, MetaschemaException { // NOPMD - delegated to doTest
    doTest(
        "local-declarations",
        "global-and-local_metaschema.xml",
        "global-and-local");
  }

  @Test
  void testLiboscalJavaIssue181()
      throws IOException, MetaschemaException, SAXException, ParserConfigurationException, XPathExpressionException {
    IBindingContext bindingContext = newBindingContext();

    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();

    IModule module = loader.load(new URL(
        // "https://raw.githubusercontent.com/usnistgov/OSCAL/develop/src/metaschema/oscal_complete_metaschema.xml"));
        "https://raw.githubusercontent.com/usnistgov/OSCAL/v1.1.1/src/metaschema/oscal_catalog_metaschema.xml"));
    ISchemaGenerator schemaGenerator = new XmlSchemaGenerator();
    IMutableConfiguration<SchemaGenerationFeature<?>> features = new DefaultConfiguration<>();
    features.enableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
    features.disableFeature(SchemaGenerationFeature.INLINE_CHOICE_DEFINITIONS);

    Path schemaPath = Path.of("target/oscal-catalog_schema.xsd");
    try (Writer writer = Files.newBufferedWriter(schemaPath, StandardCharsets.UTF_8, getWriteOpenOptions())) {
      assert writer != null;
      schemaGenerator.generateFromModule(module, writer, features);
    }

    // check for missing attribute types per liboscal-java#181
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document;
    try (InputStream is = Files.newInputStream(schemaPath)) {
      document = builder.parse(is);
    }

    XPath xpath = XPathFactory.newInstance().newXPath();
    xpath.setNamespaceContext(new NamespaceContext() {
      @Override
      public String getNamespaceURI(String prefix) {
        if ("xs".equals(prefix)) {
          return "http://www.w3.org/2001/XMLSchema";
        }
        return null;
      }

      @Override
      public String getPrefix(String namespaceURI) {
        return null;
      }

      @Override
      public Iterator<String> getPrefixes(String namespaceURI) {
        return null;
      }
    });

    NodeList result = (NodeList) xpath.evaluate(
        "//xs:attribute[not(@type or xs:simpleType)]",
        document,
        XPathConstants.NODESET);

    assertTrue(result.getLength() == 0, "Found " + result.getLength() + " attributes without type");
  }
}
