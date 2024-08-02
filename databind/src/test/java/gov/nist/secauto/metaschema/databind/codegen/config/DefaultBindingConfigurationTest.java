/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ModelType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

class DefaultBindingConfigurationTest {
  private static final URI METASCHEMA_LOCATION
      = new File("src/test/resources/metaschema/metaschema.xml").getAbsoluteFile().toURI();
  private static final String DEFINITION_NAME = "grandchild";
  private static final ModelType DEFINITION_MODEL_TYPE = ModelType.ASSEMBLY;
  private static final String DEFINITION__CLASS_NAME = "TheChild";

  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery();
  private final IModelDefinition definition = context.mock(IModelDefinition.class);
  private final IModule module = context.mock(IModule.class);

  @Test
  void testLoader() throws MalformedURLException, IOException {
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config.xml");

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    assertEquals("gov.nist.itl.metaschema.codegen.xml.example.assembly",
        config.getPackageNameForNamespace("http://csrc.nist.gov/ns/metaschema/testing/assembly"));

    context.checking(new Expectations() {
      { // NOPMD - intentional
        oneOf(module).getLocation();
        will(returnValue(METASCHEMA_LOCATION));
        allowing(definition).getContainingModule();
        will(returnValue(module));
        allowing(definition).getModelType();
        will(returnValue(DEFINITION_MODEL_TYPE));
        allowing(definition).getName();
        will(returnValue(DEFINITION_NAME));
      }
    });
    IDefinitionBindingConfiguration defConfig = config.getBindingConfigurationForDefinition(
        ObjectUtils.notNull(definition));
    assertNotNull(defConfig);
    assertEquals(DEFINITION__CLASS_NAME, defConfig.getClassName());
  }

}
