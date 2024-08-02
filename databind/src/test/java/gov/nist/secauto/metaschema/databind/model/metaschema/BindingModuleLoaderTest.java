/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class BindingModuleLoaderTest {

  @Test
  void test() throws MetaschemaException, IOException {
    BindingModuleLoader loader = new BindingModuleLoader(new DefaultBindingContext());
    loader.allowEntityResolution();

    IBindingMetaschemaModule module
        = loader.load(Paths.get("src/test/resources/test-content/legacy-metaschema-data-types-module.xml"));
  }
}
