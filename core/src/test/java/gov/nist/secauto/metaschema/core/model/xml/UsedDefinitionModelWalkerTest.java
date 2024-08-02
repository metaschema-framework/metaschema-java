/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import static org.junit.jupiter.api.Assertions.assertFalse;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.UsedDefinitionModelWalker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

class UsedDefinitionModelWalkerTest {
  private static final Logger LOGGER = LogManager.getLogger(UsedDefinitionModelWalkerTest.class);

  @Disabled
  @Test
  void test() throws MetaschemaException, IOException {
    ModuleLoader loader = new ModuleLoader();
    loader.allowEntityResolution();

    IXmlMetaschemaModule module = loader.load(new URL(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/v1.0.0/src/metaschema/oscal_complete_metaschema.xml"));

    Collection<? extends IDefinition> definitions
        = UsedDefinitionModelWalker.collectUsedDefinitionsFromModule(module);
    assertFalse(definitions.isEmpty(), "no definitions found");

    if (LOGGER.isDebugEnabled()) {
      for (IDefinition definition : definitions) {
        LOGGER.debug(definition.toCoordinates());
      }
    }
  }

}
