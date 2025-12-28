/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

  @Test
  void testReadMetaschemaAsXml() throws IOException {
    IBindingContext context = IBindingContext.newInstance();

    // Disable required field validation because pre-generated binding classes
    // don't preserve choice group information. See issue #594.
    IDeserializer<METASCHEMA> xmlDeserializer = context.newDeserializer(Format.XML, METASCHEMA.class);
    xmlDeserializer.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
    METASCHEMA metaschema = xmlDeserializer.deserialize(METASCHEMA_FILE);

    {
      ISerializer<METASCHEMA> serializer = context.newSerializer(Format.XML, METASCHEMA.class);
      serializer.serialize(metaschema, ObjectUtils.notNull(Paths.get("target/metaschema.xml")));
    }

    {
      ISerializer<METASCHEMA> serializer = context.newSerializer(Format.JSON, METASCHEMA.class);
      serializer.serialize(metaschema, ObjectUtils.notNull(Paths.get("target/metaschema.json")));
    }

    {
      ISerializer<METASCHEMA> serializer = context.newSerializer(Format.YAML, METASCHEMA.class);
      serializer.serialize(metaschema, ObjectUtils.notNull(Paths.get("target/metaschema.yaml")));
    }

    {
      IDeserializer<METASCHEMA> deserializer = context.newDeserializer(Format.XML, METASCHEMA.class);
      deserializer.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
      deserializer.deserialize(
          ObjectUtils.notNull(Paths.get("target/metaschema.xml")));
    }

    {
      IDeserializer<METASCHEMA> deserializer = context.newDeserializer(Format.JSON, METASCHEMA.class);
      deserializer.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
      deserializer.deserialize(
          ObjectUtils.notNull(Paths.get("target/metaschema.json")));
    }

    {
      IDeserializer<METASCHEMA> deserializer = context.newDeserializer(Format.YAML, METASCHEMA.class);
      deserializer.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
      deserializer.deserialize(
          ObjectUtils.notNull(Paths.get("target/metaschema.yaml")));
    }
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
