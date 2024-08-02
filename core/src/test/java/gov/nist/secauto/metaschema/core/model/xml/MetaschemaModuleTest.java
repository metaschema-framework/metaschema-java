/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import static org.junit.jupiter.api.Assertions.assertFalse;

import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

class MetaschemaModuleTest {

  @Test
  void testFile() throws MetaschemaException, IOException {
    ModuleLoader loader = new ModuleLoader();
    URI moduleUri = ObjectUtils.notNull(
        Paths.get("metaschema/schema/metaschema/metaschema-module-metaschema.xml").toUri());
    IXmlMetaschemaModule module = loader.load(moduleUri);
    assertFalse(module.getExportedRootAssemblyDefinitions().isEmpty(), "no roots found");
  }
}
