/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;
import dev.metaschema.databind.io.DeserializationFeature;
import dev.metaschema.databind.io.IBoundLoader;

class JsonParserTest
    extends AbstractMetaschemaTest {
  @Test
  void testIssue308Regression() throws IOException, MetaschemaException {
    IBindingContext bindingContext = newBindingContext();

    bindingContext.loadMetaschema(ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/308-choice-regression/metaschema.xml")));

    IBoundLoader loader = bindingContext.newBoundLoader();
    loader.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);
    // Disable required field validation because dynamically compiled binding
    // classes
    // don't preserve choice group information (see issue #594). The metaschema has
    // a
    // choice between x and y, and the example provides y, which should satisfy the
    // choice.
    loader.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
    Object obj = loader.load(ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/308-choice-regression/example.json")));
    assertNotNull(obj);
  }
}
