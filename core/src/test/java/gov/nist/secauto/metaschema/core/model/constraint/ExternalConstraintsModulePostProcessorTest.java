/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testsupport.MockedModelTestSupport;
import gov.nist.secauto.metaschema.core.testsupport.builder.IModuleBuilder;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

class ExternalConstraintsModulePostProcessorTest {

  private static final String TEST_NAMESPACE = "http://csrc.nist.gov/ns/test/metaschema/constraint-targeting-test";

  @Test
  void test() {
    // Build constraints programmatically instead of loading from XML
    // Original XML:
    // <context>
    // <metapath target="//*"/>
    // <constraints>
    // <allowed-values target="@value">
    // <enum value="value1">Value #1</enum>
    // </allowed-values>
    // </constraints>
    // </context>
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource constraintSource = ISource.externalSource(URI.create(TEST_NAMESPACE + "/constraints"));

    IConstraintSet constraintSet = mocking.constraintSet()
        .source(constraintSource)
        .context(ctx -> ctx
            .metapath("//*")
            .constraint(IAllowedValuesConstraint.builder()
                .source(constraintSource)
                .target(IMetapathExpression.compile("@value"))
                .allowedValue(IAllowedValue.of("value1", MarkupLine.fromMarkdown("Value #1"), null))))
        .build();

    // Build module programmatically
    ISource moduleSource = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("constraint-targeting-test")
        .version("1.0.0")
        .source(moduleSource)
        .assembly(mocking.assembly()
            .name("a")
            .rootName("a")
            .flags(List.of(mocking.flag().name("value")))
            .modelInstances(List.of(
                mocking.assemblyRef("a"))))
        .toModule();

    // Apply external constraints to the module
    ExternalConstraintsModulePostProcessor postProcessor
        = new ExternalConstraintsModulePostProcessor(Collections.singletonList(constraintSet));
    postProcessor.processModule(module);

    // Verify constraint was applied
    IAssemblyDefinition definition = ObjectUtils.requireNonNull(module.getAssemblyDefinitionByName(
        IEnhancedQName.of(TEST_NAMESPACE, "a").getIndexPosition()));

    assertEquals(1, definition.getConstraints().size());
  }

}
