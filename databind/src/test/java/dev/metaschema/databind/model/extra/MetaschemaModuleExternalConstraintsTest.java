/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.extra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IConstraintLoader;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.model.constraint.IConstraintSet;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.IBoundDefinitionModelComplex;
import dev.metaschema.databind.model.metaschema.IBindingModuleLoader;
import dev.metaschema.databind.model.metaschema.binding.METASCHEMA;

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
        ObjectUtils.notNull(Paths.get("src/test/resources/content/external-constraint.yml")));

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
        ObjectUtils.notNull(Paths.get("src/test/resources/content/external-constraint.yml")));

    bindingContext = IBindingContext.builder().constraintSet(constraints)
        .compilePath(ObjectUtils.notNull(Paths.get("target/generated-modules"))).build();

    IBindingModuleLoader moduleLoader = bindingContext.newModuleLoader();
    IModule module = moduleLoader
        .load(ObjectUtils.notNull(Paths.get("../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml")));
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
