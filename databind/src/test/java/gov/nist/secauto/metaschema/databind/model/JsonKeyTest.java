/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.codegen.AbstractMetaschemaTest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class JsonKeyTest
    extends AbstractMetaschemaTest {
  @Test
  void testJsonKey() throws IOException, MetaschemaException {
    IBindingContext bindingContext = getBindingContext();

    bindingContext.newModuleLoader().load(ObjectUtils.requireNonNull(
        Paths.get("src/test/resources/metaschema/json-key/metaschema.xml")));

    Object obj = bindingContext.newBoundLoader().load(
        ObjectUtils.requireNonNull(Paths.get("src/test/resources/metaschema/json-key/test.json")));

    assertNotNull(obj);
  }
}
