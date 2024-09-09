/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.IMetaschemaModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModelTestSupport;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class JsonParserTest
    extends AbstractBoundModelTestSupport {
  @Test
  void testIssue308Regression() throws IOException, MetaschemaException {
    ModuleLoader moduleLoader = new ModuleLoader();
    IMetaschemaModule<?> module
        = moduleLoader.load(Paths.get("src/test/resources/metaschema/308-choice-regression/metaschema.xml"));

    IBindingContext context = IBindingContext.instance();
    context.registerModule(module, Paths.get("target/generated-test-sources/308-choice-regression"));

    IBoundLoader loader = context.newBoundLoader();
    loader.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);
    Object obj = loader.load(Paths.get("src/test/resources/metaschema/308-choice-regression/example.json"));
    assertNotNull(obj);
  }
}
