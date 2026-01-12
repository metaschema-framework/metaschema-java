/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;

class BindingModuleLoaderTest
    extends AbstractMetaschemaTest {

  @Test
  void test() throws MetaschemaException, IOException {
    IBindingModuleLoader loader = newBindingContext().newModuleLoader();
    loader.allowEntityResolution();

    loader.load(ObjectUtils.notNull(
        Paths.get("src/test/resources/test-content/legacy-metaschema-data-types-module.xml")));
  }
}
