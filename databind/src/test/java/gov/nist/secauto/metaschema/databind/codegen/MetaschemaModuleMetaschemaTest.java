/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.IXmlMetaschemaModule;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.IDeserializer;
import gov.nist.secauto.metaschema.databind.io.ISerializer;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.METASCHEMA;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingModuleLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.umd.cs.findbugs.annotations.NonNull;

class MetaschemaModuleMetaschemaTest
    extends AbstractMetaschemaTest {
  @NonNull
  private static final Path METASCHEMA_FILE
      = ObjectUtils.notNull(Paths.get("../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml"));

  @Test
  @Disabled
  void testMetaschemaMetaschema() throws MetaschemaException, IOException, ClassNotFoundException, BindingException {
    runTests(
        ObjectUtils.notNull(METASCHEMA_FILE),
        ObjectUtils.notNull(
            Paths.get("../databind-metaschema/src/main/metaschema-bindings/metaschema-metaschema-bindings.xml")),
        null,
        ObjectUtils.notNull(METASCHEMA.class.getName()),
        ObjectUtils.notNull(generationDir),
        null);
  }

  @Test
  @Disabled
  void testReadMetaschemaAsXml() throws IOException {
    IBindingContext context = IBindingContext.instance();

    METASCHEMA metaschema = context.newDeserializer(Format.XML, METASCHEMA.class).deserialize(METASCHEMA_FILE);

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
      deserializer.deserialize(
          ObjectUtils.notNull(Paths.get("target/metaschema.xml")));
    }

    {
      IDeserializer<METASCHEMA> deserializer = context.newDeserializer(Format.JSON, METASCHEMA.class);
      deserializer.deserialize(
          ObjectUtils.notNull(Paths.get("target/metaschema.json")));
    }

    {
      IDeserializer<METASCHEMA> deserializer = context.newDeserializer(Format.YAML, METASCHEMA.class);
      deserializer.deserialize(
          ObjectUtils.notNull(Paths.get("target/metaschema.yaml")));
    }
  }

  @Test
  void testModuleLoader() throws MetaschemaException, IOException {
    BindingModuleLoader loader = new BindingModuleLoader(new DefaultBindingContext());
    IBindingMetaschemaModule module = loader.load(METASCHEMA_FILE);
    assertNotNull(module);
  }

  @Test
  void testOscalBindingModuleLoader() throws MetaschemaException, IOException {
    BindingModuleLoader loader = new BindingModuleLoader(new DefaultBindingContext());
    loader.set(DeserializationFeature.DESERIALIZE_XML_ALLOW_ENTITY_RESOLUTION, true);
    IBindingMetaschemaModule module = loader.load(ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/main/src/metaschema/oscal_complete_metaschema.xml")));
    assertNotNull(module);
  }

  @Test
  void testOscalXmlModuleLoader() throws MetaschemaException, IOException {
    ModuleLoader loader = new ModuleLoader();
    // loader.set(DeserializationFeature.DESERIALIZE_XML_ALLOW_ENTITY_RESOLUTION,
    // true);
    IXmlMetaschemaModule module = loader.load(ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/main/src/metaschema/oscal_complete_metaschema.xml")));
    assertNotNull(module);
  }
}
