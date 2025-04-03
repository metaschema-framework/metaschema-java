/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.extra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

class MetaschemaModuleExternalConstraintsTest {

  @Test
  void testInternalBinding() throws MetaschemaException, IOException {
    IBindingContext bindingContext = IBindingContext.newInstance();
    IConstraintLoader constraintLoader = bindingContext.newConstraintLoader();

    List<IConstraintSet> constraints = constraintLoader.load(
        Paths.get("src/test/resources/content/external-constraint.yml"));

    bindingContext = IBindingContext.builder().constraintSet(constraints).build();

    IBoundDefinitionModelComplex definition
        = ObjectUtils.notNull(bindingContext.getBoundDefinitionForClass(METASCHEMA.class));

    List<IConstraint> matchingConstraints = definition.getConstraints().stream()
        .filter(constraint -> "junit-test-expect".equals(constraint.getId()))
        .collect(Collectors.toList());

    assertEquals(1, matchingConstraints.size());
  }

  @Test
  void testExternalBinding() throws MetaschemaException, IOException {
    IBindingContext bindingContext = IBindingContext.newInstance();
    IConstraintLoader constraintLoader = bindingContext.newConstraintLoader();

    List<IConstraintSet> constraints = constraintLoader.load(
        Paths.get("src/test/resources/content/external-constraint.yml"));

    bindingContext = IBindingContext.builder().constraintSet(constraints)
        .compilePath(Paths.get("target/generated-modules")).build();

    IBindingModuleLoader moduleLoader = bindingContext.newModuleLoader();
    IModule module = moduleLoader
        .load(Paths.get("../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml"));
    module = bindingContext.registerModule(module);

    IBoundDefinitionModelComplex internalDefinition
        = ObjectUtils.notNull(bindingContext.getBoundDefinitionForClass(METASCHEMA.class));

    IAssemblyDefinition definition = ObjectUtils.notNull(module.getAssemblyDefinitionByName(
        IEnhancedQName.of("http://csrc.nist.gov/ns/oscal/metaschema/1.0", "METASCHEMA").getIndexPosition()));

    assertNotSame(internalDefinition, definition);

    List<IConstraint> matchingConstraints = definition.getConstraints().stream()
        .filter(constraint -> "junit-test-expect".equals(constraint.getId())).collect(Collectors.toList());

    assertEquals(1, matchingConstraints.size());
  }
}
