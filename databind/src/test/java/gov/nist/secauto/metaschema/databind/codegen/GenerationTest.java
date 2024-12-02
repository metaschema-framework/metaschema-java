/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

public class GenerationTest
    extends AbstractMetaschemaTest {

  @Test
  void testOscalBindingModuleLoader() throws MetaschemaException, IOException {
    IBindingContext bindingContext = newBindingContext();

    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();
    IBindingMetaschemaModule module = loader.load(ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/refs/tags/v1.1.2/src/metaschema/oscal_complete_metaschema.xml")));

    IBoundModule registeredModule = bindingContext.registerModule(module);
    assertAll(
        () -> assertNotNull(module),
        () -> assertNotNull(registeredModule));
  }
}
