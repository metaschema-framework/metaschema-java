/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingModuleLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class GenerationTest {

  @Test
  void testOscalBindingModuleLoader() throws MetaschemaException, IOException {
    BindingModuleLoader loader = new BindingModuleLoader(new DefaultBindingContext());
    loader.set(DeserializationFeature.DESERIALIZE_XML_ALLOW_ENTITY_RESOLUTION, true);
    IBindingMetaschemaModule module = loader.load(ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/main/src/metaschema/oscal_complete_metaschema.xml")));
    IBindingContext context = IBindingContext.instance().registerModule(module, Paths.get("target/oscal-classes"));
    assertAll(
        () -> assertNotNull(module),
        () -> assertNotNull(context));
  }
}
