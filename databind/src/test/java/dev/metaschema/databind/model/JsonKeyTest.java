/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;

class JsonKeyTest
    extends AbstractMetaschemaTest {
  @Test
  void testJsonKey() throws IOException, MetaschemaException {
    IBindingContext bindingContext = newBindingContext();

    bindingContext.newModuleLoader().load(ObjectUtils.requireNonNull(
        Paths.get("src/test/resources/metaschema/json-key/metaschema.xml")));

    Object obj = bindingContext.newBoundLoader().load(
        ObjectUtils.requireNonNull(Paths.get("src/test/resources/metaschema/json-key/test.json")));

    assertNotNull(obj);
  }
}
