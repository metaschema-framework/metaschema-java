/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.IBindingModuleLoader;
import dev.metaschema.databind.model.metaschema.binding.METASCHEMA;
import edu.umd.cs.findbugs.annotations.NonNull;

class MetaschemaModuleMetaschemaTest {
  @NonNull
  private static final Path METASCHEMA_FILE
      = ObjectUtils.notNull(Paths.get("../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml"));

  @NonNull
  private static IBindingContext newBindingContext() throws IOException {
    Path generationDir = Paths.get("target/generated-modules");
    Files.createDirectories(generationDir);

    return IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(generationDir, "modules-")))
        .build();
  }

  /**
   * Deserialize content with required field validation disabled.
   * <p>
   * Required field validation is disabled because pre-generated binding classes
   * don't preserve choice group information. See issue #594. TODO: Remove this
   * workaround when #594 is implemented.
   */
  @NonNull
  private static <T extends IBoundObject> T deserializeWithValidationDisabled(
      @NonNull IBindingContext context,
      @NonNull Format format,
      @NonNull Class<T> clazz,
      @NonNull Path path) throws IOException {
    IDeserializer<T> deserializer = context.newDeserializer(format, clazz);
    deserializer.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
    return deserializer.deserialize(path);
  }

  @Test
  void testReadMetaschemaAsXml() throws IOException {
    IBindingContext context = IBindingContext.newInstance();

    METASCHEMA metaschema = deserializeWithValidationDisabled(
        context, Format.XML, METASCHEMA.class, METASCHEMA_FILE);

    // Serialize to all formats
    Path xmlPath = ObjectUtils.notNull(Paths.get("target/metaschema.xml"));
    Path jsonPath = ObjectUtils.notNull(Paths.get("target/metaschema.json"));
    Path yamlPath = ObjectUtils.notNull(Paths.get("target/metaschema.yaml"));

    context.newSerializer(Format.XML, METASCHEMA.class).serialize(metaschema, xmlPath);
    context.newSerializer(Format.JSON, METASCHEMA.class).serialize(metaschema, jsonPath);
    context.newSerializer(Format.YAML, METASCHEMA.class).serialize(metaschema, yamlPath);

    // Round-trip: deserialize from all formats
    deserializeWithValidationDisabled(context, Format.XML, METASCHEMA.class, xmlPath);
    deserializeWithValidationDisabled(context, Format.JSON, METASCHEMA.class, jsonPath);
    deserializeWithValidationDisabled(context, Format.YAML, METASCHEMA.class, yamlPath);
  }

  @Test
  void testModuleLoader() throws MetaschemaException, IOException {
    IBindingModuleLoader loader = newBindingContext().newModuleLoader();
    IBindingMetaschemaModule module = loader.load(METASCHEMA_FILE);
    assertNotNull(module);
  }

  @Test
  void testOscalBindingModuleLoader() throws MetaschemaException, IOException {
    IBindingModuleLoader loader = newBindingContext().newModuleLoader();
    loader.allowEntityResolution();
    IBindingMetaschemaModule module = loader.load(ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/refs/tags/v1.1.3/src/metaschema/oscal_complete_metaschema.xml")));
    assertNotNull(module);
  }
}
