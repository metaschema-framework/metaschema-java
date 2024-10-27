/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;
import gov.nist.secauto.metaschema.schemagen.json.JsonSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import edu.umd.cs.findbugs.annotations.NonNull;

class MetaschemaModuleTest {
  @NonNull
  private static final Path METASCHEMA_FILE
      = ObjectUtils.notNull(Paths.get("../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml"));

  @NonNull
  private static IBindingContext getBindingContext() throws IOException {
    return IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(Paths.get("target"), "modules-")))
        .build();
  }

  @Test
  void testGenerateMetaschemaModuleJson() throws MetaschemaException, IOException {
    IBindingContext bindingContext = getBindingContext();
    IBindingModuleLoader loader = bindingContext.newModuleLoader();

    IModule module = loader.load(METASCHEMA_FILE);

    IMutableConfiguration<SchemaGenerationFeature<?>> features
        = new DefaultConfiguration<>();
    features.enableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
    // features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);

    try (Writer writer = Files.newBufferedWriter(
        Path.of("target/metaschema-schema.json"),
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
      assert writer != null;
      ISchemaGenerator schemaGenerator = new JsonSchemaGenerator();
      schemaGenerator.generateFromModule(module, writer, features);
    }
  }

  @Test
  void testGenerateMetaschemaModuleXml() throws MetaschemaException, IOException {
    IBindingContext bindingContext = getBindingContext();
    IBindingModuleLoader loader = bindingContext.newModuleLoader();

    IModule module = loader.load(METASCHEMA_FILE);

    IMutableConfiguration<SchemaGenerationFeature<?>> features
        = new DefaultConfiguration<>();
    features.enableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);
    // features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);

    try (Writer writer = Files.newBufferedWriter(
        Path.of("target/metaschema-schema.xsd"),
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
      assert writer != null;
      ISchemaGenerator schemaGenerator = new XmlSchemaGenerator();
      schemaGenerator.generateFromModule(module, writer, features);
    }
  }
}
