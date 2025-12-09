/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.XmlMetaConstraintLoader;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testsupport.builder.IModuleBuilder;
import gov.nist.secauto.metaschema.core.testsupport.MockedModelTestSupport;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

class ExternalConstraintsModulePostProcessorTest {

  private static final String TEST_NAMESPACE = "http://csrc.nist.gov/ns/test/metaschema/constraint-targeting-test";

  @Test
  void test() throws MetaschemaException, IOException {
    // Load external constraints from XML
    List<IConstraintSet> constraints
        = new XmlMetaConstraintLoader().load(ObjectUtils.notNull(
            Paths.get("src/test/resources/content/issue184-constraints.xml")));

    // Build module programmatically instead of loading from XML
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("constraint-targeting-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("a")
            .rootName("a")
            .flags(List.of(mocking.flag().name("value")))
            .modelInstances(List.of(
                mocking.assemblyRef("a"))))
        .toModule();

    // Apply external constraints to the module
    ExternalConstraintsModulePostProcessor postProcessor
        = new ExternalConstraintsModulePostProcessor(constraints);
    postProcessor.processModule(module);

    // Verify constraint was applied
    IAssemblyDefinition definition = ObjectUtils.requireNonNull(module.getAssemblyDefinitionByName(
        IEnhancedQName.of(TEST_NAMESPACE, "a").getIndexPosition()));

    assertEquals(1, definition.getConstraints().size());
  }

}
