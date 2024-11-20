/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

class ExamplesTest {

  @Test
  void testLoadMetaschema() throws MetaschemaException, IOException {
    ModuleLoader loader = new ModuleLoader();
    loader.allowEntityResolution();

    URI moduleUri = ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/v1.0.0/src/metaschema/oscal_complete_metaschema.xml"));
    IXmlMetaschemaModule module = loader.load(moduleUri);
    assertNotNull(module, "metaschema not found");
  }

  @Test
  void testExamineAssemblyDefinitionByName() throws MetaschemaException, IOException {
    ModuleLoader loader = new ModuleLoader();
    loader.allowEntityResolution();
    URI moduleUri = ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/v1.0.0/src/metaschema/oscal_complete_metaschema.xml"));
    IXmlMetaschemaModule module = loader.load(moduleUri);

    IAssemblyDefinition definition = module.getScopedAssemblyDefinitionByName(
        IEnhancedQName.of("http://csrc.nist.gov/ns/oscal/1.0", "property").getIndexPosition());
    assertNotNull(definition, "definition not found");
  }

}
