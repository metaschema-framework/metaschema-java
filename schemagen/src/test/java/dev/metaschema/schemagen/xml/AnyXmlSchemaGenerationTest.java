/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.metaschema.core.configuration.DefaultConfiguration;
import dev.metaschema.core.configuration.IMutableConfiguration;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.metaschema.IBindingModuleLoader;
import dev.metaschema.schemagen.SchemaGenerationFeature;

class AnyXmlSchemaGenerationTest {

  private static final Path ANY_METASCHEMA
      = ObjectUtils.notNull(Paths.get("src/test/resources/metaschema/any-test_metaschema.xml"));

  private static final Path NO_ANY_METASCHEMA
      = ObjectUtils.notNull(Paths.get("src/test/resources/metaschema/no-any-test_metaschema.xml"));

  private String generateXmlSchema(Path metaschemaPath) throws MetaschemaException, IOException {
    IBindingContext bindingContext = IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(Paths.get("target"), "modules-")))
        .build();
    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();

    IModule module = loader.load(metaschemaPath);

    IMutableConfiguration<SchemaGenerationFeature<?>> features
        = new DefaultConfiguration<>();
    features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);

    StringWriter writer = new StringWriter();
    XmlSchemaGenerator schemaGenerator = new XmlSchemaGenerator();
    schemaGenerator.generateFromModule(module, writer, features);

    return writer.toString();
  }

  @Test
  void testAnyGeneratesXsAny() throws MetaschemaException, IOException {
    String schema = generateXmlSchema(ANY_METASCHEMA);

    // The generated XSD should contain an xs:any element with the correct
    // attributes
    assertTrue(schema.contains("xs:any"),
        "Generated XML Schema should contain xs:any element for <any/> in assembly model.\n"
            + "Actual schema:\n" + schema);
    assertTrue(schema.contains("namespace=\"##other\""),
        "xs:any element should have namespace=\"##other\" attribute.\n"
            + "Actual schema:\n" + schema);
    assertTrue(schema.contains("processContents=\"lax\""),
        "xs:any element should have processContents=\"lax\" attribute.\n"
            + "Actual schema:\n" + schema);
  }

  @Test
  void testAssemblyWithoutAnyDoesNotGenerateXsAny() throws MetaschemaException, IOException {
    String schema = generateXmlSchema(NO_ANY_METASCHEMA);

    // A schema without <any/> should NOT contain xs:any
    assertFalse(schema.contains("xs:any"),
        "Generated XML Schema should NOT contain xs:any element when assembly has no <any/>.\n"
            + "Actual schema:\n" + schema);
  }
}
