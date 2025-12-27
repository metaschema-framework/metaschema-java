/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.codegen.AbstractMetaschemaTest;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class JsonParserTest
    extends AbstractMetaschemaTest {
  @Test
  void testIssue308Regression() throws IOException, MetaschemaException {
    IBindingContext bindingContext = newBindingContext();

    bindingContext.loadMetaschema(ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/308-choice-regression/metaschema.xml")));

    IBoundLoader loader = bindingContext.newBoundLoader();
    loader.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);
    // Disable required field validation since dynamically compiled binding classes
    // don't preserve choice group information. The metaschema has a choice between
    // x and y, and the example provides y, which should satisfy the choice.
    loader.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
    Object obj = loader.load(ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/308-choice-regression/example.json")));
    assertNotNull(obj);
  }
}
