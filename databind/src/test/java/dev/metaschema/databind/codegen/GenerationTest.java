/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.IBindingModuleLoader;

public class GenerationTest
    extends AbstractMetaschemaTest {

  @Test
  void testOscalBindingModuleLoader() throws MetaschemaException, IOException {
    IBindingContext bindingContext = newBindingContext();

    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();
    IBindingMetaschemaModule module = loader.load(ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/refs/tags/v1.1.3/src/metaschema/oscal_complete_metaschema.xml")));

    IBoundModule registeredModule = bindingContext.registerModule(module);
    assertAll(
        () -> assertNotNull(module),
        () -> assertNotNull(registeredModule));
  }
}
