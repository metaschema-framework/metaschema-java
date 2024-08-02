/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingModuleLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.umd.cs.findbugs.annotations.NonNull;

class JsonKeyTest
    extends AbstractBoundModelTestSupport {
  // @TempDir
  // Path generationDir;
  @NonNull
  Path generationDir = ObjectUtils.notNull(Paths.get("target/generated-test-sources/metaschema"));

  @Test
  void testJsonKey() throws IOException, MetaschemaException {
    IBindingMetaschemaModule module
        = new BindingModuleLoader(new DefaultBindingContext()).load(ObjectUtils.requireNonNull(
            Paths.get("src/test/resources/metaschema/json-key/metaschema.xml")));

    IBindingContext bindingContext = IBindingContext.instance();
    bindingContext.registerModule(module, ObjectUtils.notNull(generationDir));

    Object obj = bindingContext.newBoundLoader().load(
        ObjectUtils.requireNonNull(Paths.get("src/test/resources/metaschema/json-key/test.json")));

    assertNotNull(obj);
  }
}
