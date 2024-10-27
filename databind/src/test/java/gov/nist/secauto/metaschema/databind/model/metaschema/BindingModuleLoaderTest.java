/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.AbstractMetaschemaTest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class BindingModuleLoaderTest
    extends AbstractMetaschemaTest {

  @Test
  void test() throws MetaschemaException, IOException {
    IBindingModuleLoader loader = getBindingContext().newModuleLoader();
    loader.allowEntityResolution();

    loader.load(ObjectUtils.notNull(
        Paths.get("src/test/resources/test-content/legacy-metaschema-data-types-module.xml")));
  }
}
